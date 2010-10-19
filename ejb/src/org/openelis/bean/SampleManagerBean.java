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

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.NoResultException;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.utils.PermissionInterceptor;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class SampleManagerBean  implements SampleManagerRemote, SampleManagerLocal {
    
    @Resource
    private SessionContext ctx;
    
    @EJB
    private LockLocal lock;
    
    @EJB
    private SystemVariableLocal systemVariable;
    
    @EJB
    private SampleLocal sample;

    public SampleManager fetchById(Integer sampleId) throws Exception {
        return SampleManager.fetchById(sampleId);
    }
    
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return SampleManager.fetchByAccessionNumber(accessionNumber);
    }

    public SampleManager fetchWithItemsAnalysis(Integer sampleId) throws Exception {
        return SampleManager.fetchWithItemsAnalyses(sampleId);
    }
    
    public SampleManager fetchWithAllData(Integer sampleId) throws Exception {
        return SampleManager.fetchWithAllData(sampleId);
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
            throw e;
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
            lock.validateLock(ReferenceTable.SAMPLE, man.getSample().getId());
            man.update();
            lock.giveUpLock(ReferenceTable.SAMPLE, man.getSample().getId());  
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
        
        return man;
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        lock.getLock(ReferenceTable.SAMPLE, sampleId);
        
        return fetchWithAllData(sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        lock.giveUpLock(ReferenceTable.SAMPLE, sampleId);
        
        return fetchWithItemsAnalysis(sampleId);
    }

    public SampleOrganizationManager fetchSampleOrgsBySampleId(Integer sampleId) throws Exception {
        return SampleOrganizationManager.fetchBySampleId(sampleId);   
    }

    public SampleProjectManager fetchSampleProjectsBySampleId(Integer sampleId) throws Exception {
        return SampleProjectManager.fetchBySampleId(sampleId);   
    }
    
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        return SampleItemManager.fetchBySampleId(sampleId);
    }
    
    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        ValidationErrorsList errorsList;
        ArrayList<SystemVariableDO> sysVarList;
        SystemVariableDO sysVarDO;
        SampleDO checkSample;
        SampleManager quickEntryMan;
        AnalysisManager anMan;
        AnalysisResultManager arMan;
        AnalysisViewDO anDO;
        

        errorsList = new ValidationErrorsList();

        // check for dups, if it is quick login return the manager
        try {
            checkSample = sample.fetchByAccessionNumber(sampleDO.getAccessionNumber());

            if (checkSample != null && !checkSample.getId().equals(sampleDO.getId())){
                if(SampleManager.QUICK_ENTRY.equals(checkSample.getDomain())){
                    quickEntryMan = fetchForUpdate(checkSample.getId());
                    
                    //since the record is a quick entry it doesnt have result records at this point.
                    //we need to add the result records for all the analyses on this sample at this point
                    for(int i=0; i<quickEntryMan.getSampleItems().count(); i++){
                        anMan = quickEntryMan.getSampleItems().getAnalysisAt(i);
                        for(int j=0; j<anMan.count(); j++){
                            arMan = anMan.getAnalysisResultAt(j);
                            // some analyses may have been put on worksheets
                            // and therefore may already have result records
                            if (arMan.rowCount() <= 0) {
                                anDO = anMan.getAnalysisAt(j);
                                try {
                                    arMan = AnalysisResultManager.fetchForUpdateWithTestId(anDO.getTestId(), anDO.getUnitOfMeasureId());
                                    anMan.setAnalysisResultAt(arMan, j);
                                } catch (NotFoundException e) {
                                    // ignore result not found error and leave the
                                    // empty AnalysisResultManager attached to the
                                    // AnalysisManger
                                } catch (Exception e) {
                                    throw e;
                                }
                            }
                        }
                    }
                    
                    return quickEntryMan;
                } else {
                    errorsList.add(new FieldErrorException("accessionNumberDuplicate", SampleMeta.getAccessionNumber()));
                }
            }
        } catch (Exception e) {
            if(!(e.getCause() instanceof NoResultException))
                throw e;
        }
        
        // get system variable
        sysVarList = systemVariable.fetchByName("last_accession_number", 1);
        sysVarDO = sysVarList.get(0);

        // we need to set the error
        if (sampleDO.getAccessionNumber().compareTo(new Integer(sysVarDO.getValue())) > 0)
            errorsList.add(new FieldErrorException("accessionNumberNotInUse", SampleMeta.getAccessionNumber()));

        if (errorsList.size() > 0)
            throw errorsList;
        
        return null;
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        PermissionInterceptor.applyPermission("sample", flag);
    }
}
