/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.AnalyteParameterLocal;
import org.openelis.local.LockLocal;
import org.openelis.manager.AnalyteParameterManager;
import org.openelis.remote.AnalyteParameterManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class AnalyteParameterManagerBean implements AnalyteParameterManagerRemote {   
    
    @Resource
    private SessionContext        ctx;

    @EJB
    private LockLocal             lock;
    
    public AnalyteParameterManager fetchActiveByReferenceIdReferenceTableId(Integer refId,
                                                                      Integer refTableId) throws Exception {        
        return AnalyteParameterManager.fetchActiveByReferenceIdReferenceTableId(refId, refTableId);        
    }

    public AnalyteParameterManager add(AnalyteParameterManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.add();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return fetchActiveByReferenceIdReferenceTableId(man.getReferenceId(), man.getReferenceTableId());
    }
    
    public AnalyteParameterManager update(AnalyteParameterManager man) throws Exception {
        int i;
        AnalyteParameterViewDO data;
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {            
            ut.begin();
            for (i = 0; i < man.count(); i++ ) {
                data = man.getParameterAt(i);
                if ("Y".equals(data.getIsActive()) && data.getId() != null)
                    lock.validateLock(ReferenceTable.ANALYTE_PARAMETER, data.getId()); 
            }
            man.update();
            for (i = 0; i < man.count(); i++ ) {
                data = man.getParameterAt(i);
                if ("Y".equals(data.getIsActive()))
                    lock.unlock(ReferenceTable.ANALYTE_PARAMETER, data.getId()); 
            }
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return fetchActiveByReferenceIdReferenceTableId(man.getReferenceId(), man.getReferenceTableId());
    }       
    
    public AnalyteParameterManager fetchForUpdate(AnalyteParameterManager man) throws Exception {
        UserTransaction ut;
        AnalyteParameterViewDO data;
        AnalyteParameterLocal pl;

        pl = EJBFactory.getAnalyteParameter();
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man = fetchActiveByReferenceIdReferenceTableId(man.getReferenceId(), man.getReferenceTableId());
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getParameterAt(i);
                if ("Y".equals(data.getIsActive()))
                    man.setParameterAt(pl.fetchForUpdate(data.getId()), i);
            }
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }
    
    public AnalyteParameterManager abortUpdate(AnalyteParameterManager man) throws Exception {
        AnalyteParameterViewDO data;
        AnalyteParameterLocal pl;
        
        pl = EJBFactory.getAnalyteParameter();
        for (int i = 0; i < man.count(); i++) {
            data = man.getParameterAt(i);
            if ("Y".equals(data.getIsActive()))
                pl.abortUpdate(data.getId());
        }
        return fetchActiveByReferenceIdReferenceTableId(man.getReferenceId(), man.getReferenceTableId());
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("analyteparameter", flag);
    }
}
