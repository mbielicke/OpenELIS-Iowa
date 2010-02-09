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
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
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

    public SampleManager fetchById(Integer sampleId) throws Exception {
        SampleManager man = SampleManager.fetchById(sampleId);
        
        return man;
    }

    public SampleManager fetchWithItemsAnalysis(Integer sampleId) throws Exception {
        SampleManager man = SampleManager.fetchWithItemsAnalyses(sampleId);
        
        return man;
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleManager man = SampleManager.fetchByAccessionNumber(accessionNumber);
        
        return man;
    }

    public SampleManager add(SampleManager man) throws Exception {
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
        }
        
        return man;
    }

    public SampleManager update(SampleManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);
        
        man.validate();
        
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.update();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
        }
        
        return man;
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        lockBean.getLock(ReferenceTable.SAMPLE, sampleId);
        
        SampleManager man = SampleManager.fetchById(sampleId);
        man.getDomainManager();
        man.getProjects();
        man.getOrganizations();
        man.getSampleItems().getAnalysisAt(1);
        
        SampleDO sampleDO = new SampleDO();
        sampleDO.setId(sampleId);
        man.setSample(sampleDO);
        
        return man.fetchForUpdate();
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        lockBean.giveUpLock(ReferenceTable.SAMPLE, sampleId);
        
        return fetchWithItemsAnalysis(sampleId);
    }

    public SampleOrganizationManager fetchSampleOrgsBySampleId(Integer sampleId) throws Exception {
        SampleOrganizationManager man = SampleOrganizationManager.fetchBySampleId(sampleId);   
        
        return man;
    }

    public SampleProjectManager fetchSampleProjectsBySampleId(Integer sampleId) throws Exception {
        SampleProjectManager man = SampleProjectManager.fetchBySampleId(sampleId);   
        
        return man;
    }
    
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        SampleItemManager man = SampleItemManager.fetchBySampleId(sampleId);
        
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
