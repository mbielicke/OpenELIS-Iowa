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

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class SampleManagerBean  implements SampleManagerRemote {

    @PersistenceContext(name = "openelis")
    
    @Resource
    private SessionContext ctx;
    
    @EJB private LockLocal lockBean;

    private static int sampleRefTableId;
    
    public SampleManagerBean(){
        sampleRefTableId = ReferenceTable.SAMPLE;
    }
    
    public SampleManager fetch(Integer sampleId) throws Exception {
        SampleManager man = SampleManager.findById(sampleId);
        
        return man;
    }

    public SampleManager fetchWithItemsAnalysis(Integer sampleId) throws Exception {
        SampleManager man = SampleManager.findByIdWithItemsAnalyses(sampleId);
        
        return man;
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleManager man = SampleManager.findByAccessionNumber(accessionNumber);
        
        return man;
    }

    public SampleManager add(SampleManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.ADD);
        
        man.validate();
        
        ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();
        
        return man;
    }

    public SampleManager update(SampleManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);
        
        man.validate();
        
        ut = ctx.getUserTransaction();
        ut.begin();
        man.update();
        ut.commit();
        
        return man;
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        lockBean.getLock(sampleRefTableId, sampleId);
        
        return fetch(sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        lockBean.giveUpLock(sampleRefTableId, sampleId);
        
        return fetch(sampleId);
    }

    public SampleOrganizationManager fetchSampleOrgsBySampleId(Integer sampleId) throws Exception {
        SampleOrganizationManager man = SampleOrganizationManager.findBySampleId(sampleId);   
        
        return man;
    }

    public SampleProjectManager fetchSampleProjectsBySampleId(Integer sampleId) throws Exception {
        SampleProjectManager man = SampleProjectManager.findBySampleId(sampleId);   
        
        return man;
    }
    
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        SampleItemManager man = SampleItemManager.findBySampleId(sampleId);
        
        return man;
    }
    
    public void validateAccessionNumber(SampleDO sampleDO) throws Exception {
        SampleManager man = SampleManager.getInstance();
        man.validateAccessionNumber(sampleDO);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "sample", flag);
    }
}
