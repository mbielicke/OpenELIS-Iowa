/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.openelis.domain.AnalysisCacheVO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleCacheVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
//import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.ToDoCacheLocal;
import org.openelis.meta.SampleMeta;
import org.openelis.utils.EJBFactory;

public class SampleManagerProxy {
    protected static Integer anLoggedInId, anInitiatedId, anCompletedId, anReleasedId, anInPrepId,
                             anOnHoldId, anRequeueId, anCancelledId, anErrorLoggedInId, anErrorInitiatedId,
                             anErrorInPrepId, anErrorCompletedId, samNotVerifiedId, samLoggedInId,
                             samCompletedId, samReleasedId, samErrorId;

    public SampleManagerProxy() {
        DictionaryLocal l;
        if (anLoggedInId == null) {
            l = EJBFactory.getDictionary();

            try {
                anLoggedInId = l.fetchBySystemName("analysis_logged_in").getId();
                anInitiatedId = l.fetchBySystemName("analysis_initiated").getId();
                anCompletedId = l.fetchBySystemName("analysis_completed").getId();
                anReleasedId = l.fetchBySystemName("analysis_released").getId();
                anInPrepId = l.fetchBySystemName("analysis_inprep").getId();
                anOnHoldId = l.fetchBySystemName("analysis_on_hold").getId();
                anRequeueId = l.fetchBySystemName("analysis_requeue").getId();
                anCancelledId = l.fetchBySystemName("analysis_cancelled").getId();
                anErrorLoggedInId = l.fetchBySystemName("analysis_error_logged_in").getId();
                anErrorInitiatedId = l.fetchBySystemName("analysis_error_initiated").getId();
                anErrorInPrepId = l.fetchBySystemName("analysis_error_inprep").getId();
                anErrorCompletedId = l.fetchBySystemName("analysis_error_completed").getId();
                samNotVerifiedId = l.fetchBySystemName("sample_not_verified").getId();
                samLoggedInId = l.fetchBySystemName("sample_logged_in").getId();
                samCompletedId = l.fetchBySystemName("sample_completed").getId();
                samReleasedId = l.fetchBySystemName("sample_released").getId();
                samErrorId = l.fetchBySystemName("sample_error").getId();
            } catch (Exception e) {
                e.printStackTrace();
                anLoggedInId = null;
            }
        }
    }

    public SampleManager fetchById(Integer sampleId) throws Exception {
        SampleDO data;
        SampleManager man;

        data = EJBFactory.getSample().fetchById(sampleId);

        man = SampleManager.getInstance();
        man.setSample(data);

        man.getDomainManager();
        man.getOrganizations();
        man.getProjects();
        man.getSampleItems();

        return man;
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleDO data;
        SampleManager man;

        data = EJBFactory.getSample().fetchByAccessionNumber(accessionNumber);

        man = SampleManager.getInstance();
        man.setSample(data);
        man.getDomainManager();
        man.getOrganizations();
        man.getProjects();
        man.getSampleItems();

        return man;
    }

    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        SampleDO data;
        SampleManager man;
        SampleItemManager itemMan;
        SampleItemViewDO item;
        ArrayList<SampleItemViewDO> items;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisManager> anaMap;
        AnalysisViewDO analysis;
        AnalysisManager anaMan;

        data = EJBFactory.getSample().fetchById(sampleId);

        man = SampleManager.getInstance();
        man.setSample(data);

        man.getDomainManager();
        man.getOrganizations();
        man.getProjects();

        // sample item
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem().fetchBySampleId(sampleId);
        itemMan = SampleItemManager.getInstance();
        itemMan.setSampleId(sampleId);
        itemMan.setSampleManager(man);
        itemMan.addSampleItems(items);
        man.sampleItems = itemMan;

        anaMap = new HashMap<Integer, AnalysisManager>();
        for (int i = 0; i < itemMan.count(); i++ ) {
            item = itemMan.getSampleItemAt(i);
            anaMan = AnalysisManager.getInstance();
            anaMan.setSampleItemId(item.getId());
            anaMan.setSampleItemManager(itemMan);
            anaMan.setSampleItemBundle(itemMan.getBundleAt(i));
            itemMan.setAnalysisAt(anaMan, i);

            anaMap.put(item.getId(), anaMan);
        }

        // fetch analyses
        try {
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis().fetchBySampleId(sampleId);
            for (int i = 0; i < analyses.size(); i++ ) {
                analysis = analyses.get(i);
                anaMan = anaMap.get(analysis.getSampleItemId());
                anaMan.addAnalysis(analysis);
            }
        } catch (NotFoundException e) {
            // ignore
        }

        return man;
    }
    
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        int addedIndex;
        SampleDO data;
        SampleManager man;
        SampleItemManager itemMan;
        SampleItemViewDO item;
        ArrayList<SampleItemViewDO> items;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisManager> anaMap;
        AnalysisViewDO analysis;
        AnalysisManager anaMan;
        HashMap<Integer, TestManager> testCache;
        TestManager testMan;

        data = EJBFactory.getSample().fetchById(sampleId);

        man = SampleManager.getInstance();
        man.setSample(data);

        man.getDomainManager();
        man.getOrganizations();
        man.getProjects();
        man.getAuxData();

        // sample item
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem().fetchBySampleId(sampleId);
        itemMan = SampleItemManager.getInstance();
        itemMan.setSampleId(sampleId);
        itemMan.setSampleManager(man);
        itemMan.addSampleItems(items);
        man.sampleItems = itemMan;

        anaMap = new HashMap<Integer, AnalysisManager>();
        for (int i = 0; i < itemMan.count(); i++ ) {
            item = itemMan.getSampleItemAt(i);
            anaMan = AnalysisManager.getInstance();
            anaMan.setSampleItemId(item.getId());
            anaMan.setSampleItemManager(itemMan);
            anaMan.setSampleItemBundle(itemMan.getBundleAt(i));
            itemMan.setAnalysisAt(anaMan, i);

            anaMap.put(item.getId(), anaMan);
        }

        // fetch analysess
        try {
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis().fetchBySampleId(sampleId);
            testCache = new HashMap<Integer, TestManager>();
            for (int i = 0; i < analyses.size(); i++ ) {
                analysis = analyses.get(i);

                anaMan = anaMap.get(analysis.getSampleItemId());
                addedIndex = anaMan.addAnalysis(analysis);

                testMan = testCache.get(analysis.getTestId());
                if (testMan == null) {
                    testMan = TestManager.fetchWithPrepTestsSampleTypes(analysis.getTestId());
                    testCache.put(analysis.getTestId(), testMan);
                }
                anaMan.setTestManagerWithResultAt(testMan, analysis.getId(), addedIndex);
            }
        } catch (NotFoundException e) {
            // ignore
        }

        return man;
    }
    
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        int addedIndex;
        Integer sampleId;
        SampleDO data;
        SampleManager man;
        SampleItemManager itemMan;
        SampleItemViewDO item;
        ArrayList<SampleItemViewDO> items;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisManager> anaMap;
        AnalysisViewDO analysis;
        AnalysisManager anaMan;
        HashMap<Integer, TestManager> testCache;
        TestManager testMan;

        data = EJBFactory.getSample().fetchByAccessionNumber(accessionNumber);
        sampleId = data.getId();
        man = SampleManager.getInstance();
        man.setSample(data);

        man.getDomainManager();
        man.getOrganizations();
        man.getProjects();
        man.getAuxData();

        // sample item
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem().fetchBySampleId(sampleId);
        itemMan = SampleItemManager.getInstance();
        itemMan.setSampleId(sampleId);
        itemMan.setSampleManager(man);
        itemMan.addSampleItems(items);
        man.sampleItems = itemMan;

        anaMap = new HashMap<Integer, AnalysisManager>();
        for (int i = 0; i < itemMan.count(); i++ ) {
            item = itemMan.getSampleItemAt(i);
            anaMan = AnalysisManager.getInstance();
            anaMan.setSampleItemId(item.getId());
            anaMan.setSampleItemManager(itemMan);
            anaMan.setSampleItemBundle(itemMan.getBundleAt(i));
            itemMan.setAnalysisAt(anaMan, i);

            anaMap.put(item.getId(), anaMan);
        }

        // fetch analysess
        try {
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis().fetchBySampleId(sampleId);
            testCache = new HashMap<Integer, TestManager>();
            for (int i = 0; i < analyses.size(); i++ ) {
                analysis = analyses.get(i);

                anaMan = anaMap.get(analysis.getSampleItemId());
                addedIndex = anaMan.addAnalysis(analysis);

                testMan = testCache.get(analysis.getTestId());
                if (testMan == null) {
                    testMan = TestManager.fetchWithPrepTestsSampleTypes(analysis.getTestId());
                    testCache.put(analysis.getTestId(), testMan);
                }
                anaMan.setTestManagerWithResultAt(testMan, analysis.getId(), addedIndex);
            }
        } catch (NotFoundException e) {
            // ignore
        }

        return man;
    }

    public SampleManager add(SampleManager man) throws Exception {
        Integer sampleId;

        EJBFactory.getSample().add(man.getSample());
        sampleId = man.getSample().getId();

        if (man.getDomainManager() != null) {
            man.getDomainManager().setSampleId(sampleId);
            man.getDomainManager().add();
        }

        man.getSampleItems().setSampleId(sampleId);
        man.getSampleItems().add();

        if (man.organizations != null) {
            man.getOrganizations().setSampleId(sampleId);
            man.getOrganizations().add();
        }

        if (man.projects != null) {
            man.getProjects().setSampleId(sampleId);
            man.getProjects().add();
        }

        if (man.qaEvents != null) {
            man.getQaEvents().setSampleId(sampleId);
            man.getQaEvents().add();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().add();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().add();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getExternalNote().setReferenceId(sampleId);
            man.getExternalNote().add();
        }

        return man;
    }

    public SampleManager update(SampleManager man) throws Exception {
        Integer sampleId;
        SampleDO data;

        data = man.getSample();
        
        /*
         * a sample's status could get set to released because of the status of
         * the analyses associated with its sample items all getting set to released,
         * so we need to set the released date for the sample 
         */
        if (samReleasedId.equals(data.getStatusId()) && data.getReleasedDate() == null)
            data.setReleasedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
            
        EJBFactory.getSample().update(man.getSample());
        sampleId = man.getSample().getId();

        if (man.deletedDomainManager != null) 
            man.getDeletedDomainManager().delete();  
        
        if (man.domainManager != null) {
            man.getDomainManager().setSampleId(sampleId);
            man.getDomainManager().update();
        }
        
        if (man.sampleItems != null) {
            man.getSampleItems().setSampleId(sampleId);
            man.getSampleItems().update();
        }

        if (man.organizations != null) {
            man.getOrganizations().setSampleId(sampleId);
            man.getOrganizations().update();
        }

        if (man.projects != null) {
            man.getProjects().setSampleId(sampleId);
            man.getProjects().update();
        }

        if (man.qaEvents != null) {
            man.getQaEvents().setSampleId(sampleId);
            man.getQaEvents().update();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().update();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().update();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(ReferenceTable.SAMPLE);
            man.getExternalNote().setReferenceId(sampleId);
            man.getExternalNote().update();
        }

        return man;
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin, end);
    }

    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        boolean quickEntry;
        Calendar cal;
        Datetime collectionDateTime;
        SampleDO data;

        // revalidate accession number
        validateAccessionNumber(man.getSample(), errorsList);
        validateOrderId(man.getSample(), errorsList);

        data = man.getSample();
        quickEntry = SampleManager.QUICK_ENTRY.equals(data.getDomain());

        if (data.getCollectionDate() != null) {
            cal = Calendar.getInstance();
            cal.setTime(data.getCollectionDate().getDate());
            if (data.getCollectionTime() != null) {
                cal.add(Calendar.HOUR_OF_DAY, data.getCollectionTime().get(Datetime.HOUR));
                cal.add(Calendar.MINUTE, data.getCollectionTime().get(Datetime.MINUTE));
            }
            collectionDateTime = new Datetime(Datetime.YEAR, Datetime.MINUTE, cal.getTime());
        } else {
            collectionDateTime = null;
        }

        if (collectionDateTime != null && data.getReceivedDate() != null &&
            collectionDateTime.compareTo(data.getReceivedDate()) == 1)
            errorsList.add(new FieldErrorException("collectedDateInvalidError",
                                                   SampleMeta.getReceivedDate()));
        
        if (man.domainManager != null)
            man.getDomainManager().validate(errorsList);
            
        if (man.sampleItems != null)
            man.getSampleItems().validate(errorsList);

        if ( !quickEntry && man.organizations != null)
            man.getOrganizations().validate(man.getSample().getDomain(), errorsList);

        if ( !quickEntry && man.projects != null)
            man.getProjects().validate(errorsList);

        if ( !quickEntry && man.qaEvents != null)
            man.getQaEvents().validate(errorsList);

        if ( !quickEntry && man.auxData != null)
            man.getAuxData().validate(errorsList);
    }
    
    public void updateCache(SampleManager man) {
        boolean sampleOverriden, analysisOverriden;
        String domain, orgName;
        AnalysisManager am;
        SampleItemManager im;
        AnalysisViewDO ana;
        AnalysisCacheVO avo;  
        SampleCacheVO svo;
        SectionCacheLocal scl;
        ToDoCacheLocal tcl;
        SampleQaEventManager sqm;
        AnalysisQaEventManager aqm;
        SamplePrivateWellManager spwm;
        SamplePrivateWellViewDO spw;
        SampleOrganizationViewDO rt;
        TestViewDO test;
        SampleDO sample;
               
        try {
            scl = EJBFactory.getSectionCache();
            tcl = EJBFactory.getToDoCache();
            sample = man.getSample();
            rt = man.getOrganizations().getReportTo();
            orgName = null;
            domain = sample.getDomain();

            if ("E".equals(domain)) {                 
                if (rt != null)             
                    orgName = rt.getOrganizationName();
                /*sem = (SampleEnvironmentalManager)man.getDomainManager();
                sproj = man.getProjects().getFirstPermanentProject();
                if (sproj != null)
                    prjName = sproj.getProjectName();*/
            } else if ("W".equals(domain)) {
                spwm = (SamplePrivateWellManager)man.getDomainManager();
                spw = spwm.getPrivateWell();
                if (spw.getOrganizationId() == null)
                    orgName = spw.getReportToName();
                else
                    orgName = spw.getOrganization().getName();
                //owner = spw.getOwner();
            } else if ("S".equals(domain)) { 
                if (rt != null)             
                    orgName = rt.getOrganizationName();
                //ssdm = (SampleSDWISManager)man.getDomainManager();
                //pwsName = ssdm.getSDWIS().getPwsName();
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
            /*svo.setSampleEnvironmentalPriority(priority);
            svo.setSampleProjectName(prjName);
            svo.setSamplePrivateWellOwner(owner);
            svo.setSampleSDWISPWSName(pwsName);*/
            
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
                    /*avo.setSampleEnvironmentalPriority(priority);
                    avo.setSampleProjectName(prjName);
                    avo.setSamplePrivateWellOwner(owner);
                    avo.setSampleSDWISPWSName(pwsName);*/
                    tcl.update(avo);   
                }
            }       
            
            if (sampleOverriden || analysisOverriden) 
                svo.setQaeventResultOverride("Y");
            else 
                svo.setQaeventResultOverride("N");
            
            tcl.update(svo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void validateAccessionNumber(SampleDO data, ValidationErrorsList errorsList) throws Exception {
        ArrayList<Exception> list;
        try {
            EJBFactory.getSampleManager().validateAccessionNumber(data);
        } catch (ValidationErrorsList e) {
            list = e.getErrorList();

            for (int i = 0; i < list.size(); i++ )
                errorsList.add(list.get(i));
        }
    }
    
    private void validateOrderId(SampleDO data, ValidationErrorsList errorsList) throws Exception {
        OrderViewDO order;
        if (data.getOrderId() == null)
            return;
        order = EJBFactory.getOrder().fetchById(data.getOrderId());
        if (order == null || !OrderManager.TYPE_SEND_OUT.equals(order.getType()))
            errorsList.add(new FieldErrorException("orderIdInvalidException",
                                                       SampleMeta.getOrderId()));                    
    }
}