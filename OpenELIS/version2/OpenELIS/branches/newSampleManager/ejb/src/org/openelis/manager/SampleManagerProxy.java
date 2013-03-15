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

import org.openelis.bean.DictionaryBean;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class SampleManagerProxy {

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
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem()
                                                       .fetchBySampleId(sampleId);
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
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis()
                                                            .fetchBySampleId(sampleId);
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
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem()
                                                       .fetchBySampleId(sampleId);
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
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis()
                                                            .fetchBySampleId(sampleId);
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
        items = (ArrayList<SampleItemViewDO>)EJBFactory.getSampleItem()
                                                       .fetchBySampleId(sampleId);
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
            analyses = (ArrayList<AnalysisViewDO>)EJBFactory.getAnalysis()
                                                            .fetchBySampleId(sampleId);
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
            man.getAuxData().setReferenceTableId(Constants.table().SAMPLE);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().add();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(Constants.table().SAMPLE);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().add();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(Constants.table().SAMPLE);
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
         * the analyses associated with its sample items all getting set to
         * released, so we need to set the released date for the sample
         */
        if (Constants.dictionary().SAMPLE_RELEASED.equals(data.getStatusId()) &&
            data.getReleasedDate() == null)
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
            man.getAuxData().setReferenceTableId(Constants.table().SAMPLE);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().update();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(Constants.table().SAMPLE);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().update();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(Constants.table().SAMPLE);
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
            collectionDateTime = new Datetime(Datetime.YEAR,
                                              Datetime.MINUTE,
                                              cal.getTime());
        } else {
            collectionDateTime = null;
        }

        if (collectionDateTime != null && data.getReceivedDate() != null &&
            collectionDateTime.compareTo(data.getReceivedDate()) == 1)
            errorsList.add(new FieldErrorException(Messages.get().collectedDateInvalidError(),
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
            errorsList.add(new FieldErrorException(Messages.get().orderIdInvalidException(),
                                                   SampleMeta.getOrderId()));
    }
}