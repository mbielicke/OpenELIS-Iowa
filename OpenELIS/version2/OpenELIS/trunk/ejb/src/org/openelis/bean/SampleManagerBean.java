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
import org.openelis.domain.AnalysisCacheVO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleCacheVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.local.ToDoCacheLocal;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.utils.EJBFactory;

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
    
    @EJB
    private ToDoCacheLocal todoCache;

    public SampleManager fetchById(Integer id) throws Exception {
        return SampleManager.fetchById(id);
    }
    
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return SampleManager.fetchByAccessionNumber(accessionNumber);
    }

    public SampleManager fetchWithItemsAnalysis(Integer id) throws Exception {
        return SampleManager.fetchWithItemsAnalyses(id);
    }
    
    public SampleManager fetchWithAllData(Integer id) throws Exception {
        return SampleManager.fetchWithAllData(id);
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
            updateCache(man);
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
            lock.unlock(ReferenceTable.SAMPLE, man.getSample().getId());  
            ut.commit();    
            updateCache(man);
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
        return man;
    }

    public SampleManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        SampleManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lock.lock(ReferenceTable.SAMPLE, id);
            man = fetchWithAllData(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public SampleManager abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.SAMPLE, id);
        
        return fetchWithItemsAnalysis(id);
    }

    public SampleOrganizationManager fetchSampleOrgsBySampleId(Integer id) throws Exception {
        return SampleOrganizationManager.fetchBySampleId(id);   
    }

    public SampleProjectManager fetchSampleProjectsBySampleId(Integer id) throws Exception {
        return SampleProjectManager.fetchBySampleId(id);   
    }
    
    public SampleItemManager fetchSampleItemsBySampleId(Integer id) throws Exception {
        return SampleItemManager.fetchBySampleId(id);
    }
    
    public SampleManager validateAccessionNumber(SampleDO data) throws Exception {
        ValidationErrorsList errorsList;
        ArrayList<SystemVariableDO> sysVarList;
        SystemVariableDO sysVarDO;
        SampleDO checkSample;
        SampleManager quickEntryMan;
        AnalysisManager anMan;
        AnalysisResultManager arMan;
        AnalysisViewDO anDO;
        Integer accNum;
        

        errorsList = new ValidationErrorsList();
        accNum = data.getAccessionNumber();
        if (accNum  <= 0) {
            errorsList.add(new FieldErrorException("accessionNumberNotPositiveException", SampleMeta.getAccessionNumber()));        
            throw errorsList;
        }
        // get system variable
        sysVarList = systemVariable.fetchByName("last_accession_number", 1);
        sysVarDO = sysVarList.get(0);

        // we need to set the error
        if (data.getAccessionNumber().compareTo(new Integer(sysVarDO.getValue())) > 0) {
            errorsList.add(new FieldErrorException("accessionNumberNotInUse", SampleMeta.getAccessionNumber()));
            throw errorsList;
        }
        
        // check for dups, if it is quick login return the manager
        try {
            checkSample = sample.fetchByAccessionNumber(accNum);

            if (checkSample != null && !checkSample.getId().equals(data.getId())){
                if(SampleManager.QUICK_ENTRY.equals(checkSample.getDomain())){
                    quickEntryMan = fetchForUpdate(checkSample.getId());
                    
                    //since the record is a quick entry it doesn't have result records at this point.
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
                                    arMan = AnalysisResultManager.fetchByTestId(anDO.getTestId(), anDO.getUnitOfMeasureId());
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
        } catch (NotFoundException ignE) {
            // ignore not found because it means they entered an unused accession number
        } catch (Exception e) {
            if(!(e.getCause() instanceof NoResultException))
                throw e;
        }
        
        if (errorsList.size() > 0)
            throw errorsList;
        
        return null;
    }
    
    private void updateCache(SampleManager man) {
        boolean sampleOverriden, analysisOverriden;
        Integer priority;
        String domain, orgName, prjName, owner, pwsName;
        AnalysisManager am;
        SampleItemManager im;
        AnalysisViewDO ana;
        SampleProjectViewDO sproj;
        AnalysisCacheVO avo;  
        SampleCacheVO svo;
        SectionCacheLocal scl;
        SampleQaEventManager sqm;
        AnalysisQaEventManager aqm;
        SampleEnvironmentalManager sem;        
        SamplePrivateWellManager spwm;
        SampleSDWISManager ssdm;
        SamplePrivateWellViewDO spw;
        SampleOrganizationViewDO rt;
        TestViewDO test;
        SampleDO sample;
               
        try {
            scl = EJBFactory.getSectionCache();
            sample = man.getSample();
            rt = man.getOrganizations().getReportTo();
            priority = null;
            orgName = null;
            prjName = null;
            owner = null;
            pwsName= null;
            domain = sample.getDomain();

            if ("E".equals(domain)) {                 
                if (rt != null)             
                    orgName = rt.getOrganizationName();
                sem = (SampleEnvironmentalManager)man.getDomainManager();
                priority = sem.getEnvironmental().getPriority();
                sproj = man.getProjects().getFirstPermanentProject();
                if (sproj != null)
                    prjName = sproj.getProjectName();
            } else if ("W".equals(domain)) {
                spwm = (SamplePrivateWellManager)man.getDomainManager();
                spw = spwm.getPrivateWell();
                if (spw.getOrganizationId() == null)
                    orgName = spw.getReportToName();
                else
                    orgName = spw.getOrganization().getName();
                owner = spw.getOwner();
            } else if ("S".equals(domain)) { 
                if (rt != null)             
                    orgName = rt.getOrganizationName();
                ssdm = (SampleSDWISManager)man.getDomainManager();
                pwsName = ssdm.getSDWIS().getPwsName();
            }
            
            /*
             * a SampleCacheVO is created for this sample and is used to update
             * the various caches used for the ToDo lists   
             */
            svo = new SampleCacheVO();
            svo.setId(sample.getId());
            svo.setStatusId(sample.getStatusId());
            svo.setDomain(domain);
            svo.setAccessionNumber(sample.getAccessionNumber());
            svo.setReceivedDate(sample.getReceivedDate());
            svo.setCollectionDate(sample.getCollectionDate());
            svo.setCollectionTime(sample.getCollectionTime());
            sqm = man.getQaEvents();
            sampleOverriden = sqm.hasResultOverrideQA();
            
            svo.setReportToName(orgName); 
            svo.setSampleEnvironmentalPriority(priority);
            svo.setSampleProjectName(prjName);
            svo.setSamplePrivateWellOwner(owner);
            svo.setSampleSDWISPWSName(pwsName);
            
            im = man.getSampleItems();
            /*
             * AnalysisCacheVOs are created for each analysis under this sample 
             * and are used to update the various caches used for the ToDo lists   
             */
            analysisOverriden = false;
            for (int i = 0; i < im.count(); i++ ) {
                am = im.getAnalysisAt(i);                
                for (int j = 0; j < am.count(); j++ ) {
                    ana = am.getAnalysisAt(j);
                    test = am.getTestAt(j).getTest();
                    avo = new AnalysisCacheVO();
                    avo.setId(ana.getId());
                    avo.setStatusId(ana.getStatusId());
                    avo.setStartedDate(ana.getStartedDate());
                    avo.setCompletedDate(ana.getCompletedDate());
                    avo.setReleasedDate(ana.getReleasedDate());
                    avo.setTestName(test.getName());
                    avo.setTestTimeHolding(test.getTimeHolding());
                    avo.setTestTimeTaAverage(test.getTimeTaAverage());
                    avo.setTestMethodName(ana.getMethodName());
                    avo.setSectionName(scl.getById(ana.getSectionId()).getName());
                    aqm = am.getQAEventAt(j);
                    if (sampleOverriden) 
                        avo.setSampleQaeventResultOverride("Y");
                    else 
                        avo.setSampleQaeventResultOverride("N");
                    
                    if (aqm.hasResultOverrideQA()) {
                        analysisOverriden = true;
                        avo.setAnalysisQaeventResultOverride("Y");
                    } else {
                        avo.setAnalysisQaeventResultOverride("N");
                    }
                    avo.setSampleDomain(domain);
                    avo.setSampleAccessionNumber(sample.getAccessionNumber());
                    avo.setSampleReportToName(orgName);
                    avo.setSampleReceivedDate(sample.getReceivedDate());
                    avo.setSampleCollectionDate(sample.getCollectionDate());
                    avo.setSampleCollectionTime(sample.getCollectionTime());
                    avo.setSampleEnvironmentalPriority(priority);
                    avo.setSampleProjectName(prjName);
                    avo.setSamplePrivateWellOwner(owner);
                    avo.setSampleSDWISPWSName(pwsName);
                    todoCache.update(avo);   
                }
            }       
            
            if (sampleOverriden || analysisOverriden) 
                svo.setQaeventResultOverride("Y");
            else 
                svo.setQaeventResultOverride("N");
            
            todoCache.update(svo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("sample", flag);
    }
}
