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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleClinicalDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.history.client.HistoryScreen;

/**
 * This class is used to show the history of the various parts of the sample
 * specified by the manager e.g. domain, organization, analysis, results etc.
 * Note: The part whose history is to be shown must already be loaded in the
 * manager, otherwise, the tree for history will be empty.
 */
public class SampleHistoryUtility1 {

    /**
     * shows the history of the sample
     */
    public static void sample(SampleManager1 manager) {
        IdNameVO hist;

        hist = new IdNameVO(manager.getSample().getId(), manager.getSample()
                                                                .getAccessionNumber()
                                                                .toString());
        HistoryScreen.showHistory(Messages.get().history_sample(), Constants.table().SAMPLE, hist);
    }

    /**
     * shows the history of sample environmental
     */
    public static void environmental(SampleManager1 manager) {
        IdNameVO hist;
        SampleEnvironmentalDO data;

        data = manager.getSampleEnvironmental();
        hist = new IdNameVO(data.getId(), data.getLocation());
        HistoryScreen.showHistory(Messages.get().history_sampleEnvironmental(),
                                  Constants.table().SAMPLE_ENVIRONMENTAL,
                                  hist);
    }

    /**
     * shows the history of sample private well
     */
    public static void privateWell(SampleManager1 manager) {
        IdNameVO hist;
        SamplePrivateWellViewDO data;

        data = manager.getSamplePrivateWell();
        hist = new IdNameVO(data.getId(), data.getLocation());
        HistoryScreen.showHistory(Messages.get().history_samplePrivateWell(),
                                  Constants.table().SAMPLE_PRIVATE_WELL,
                                  hist);
    }

    /**
     * shows the history of sample sdwis
     */
    public static void sdwis(SampleManager1 manager) {
        IdNameVO hist;
        SampleSDWISViewDO data;

        data = manager.getSampleSDWIS();
        hist = new IdNameVO(data.getId(), data.getLocation());
        HistoryScreen.showHistory(Messages.get().history_sampleSDWIS(),
                                  Constants.table().SAMPLE_SDWIS,
                                  hist);
    }

    /**
     * shows the history of sample neonatal
     */
    public static void neonatal(SampleManager1 manager) {
        IdNameVO hist;
        SampleNeonatalDO data;

        data = manager.getSampleNeonatal();
        hist = new IdNameVO(data.getId(), "");
        HistoryScreen.showHistory(Messages.get().history_sampleNeonatal(),
                                  Constants.table().SAMPLE_NEONATAL,
                                  hist);
    }

    /**
     * shows the history of sample neonatal
     */
    public static void clinical(SampleManager1 manager) {
        IdNameVO hist;
        SampleClinicalDO data;

        data = manager.getSampleClinical();
        hist = new IdNameVO(data.getId(), "");
        HistoryScreen.showHistory(Messages.get().history_sampleClinical(),
                                  Constants.table().SAMPLE_CLINICAL,
                                  hist);
    }

    /**
     * shows the history of sample neonatal's patient
     */
    public static void neonatalPatient(SampleManager1 manager) {
        SampleNeonatalDO data;

        data = manager.getSampleNeonatal();
        patient(data.getPatient().getId(), Messages.get().history_patient());
    }

    /**
     * shows the history of sample neonatal's next of kin
     */
    public static void neonatalNextOfKin(SampleManager1 manager) {
        SampleNeonatalDO data;

        data = manager.getSampleNeonatal();
        patient(data.getNextOfKin().getId(), Messages.get().history_nextOfKin());
    }
    
    /**
     * shows the history of sample clinical's patient
     */
    public static void clinicalPatient(SampleManager1 manager) {
        SampleClinicalDO data;

        data = manager.getSampleClinical();
        patient(data.getPatient().getId(), Messages.get().history_patient());
    }

    /**
     * shows the history of sample projects
     */
    public static void project(SampleManager1 manager) {
        int i;
        SampleProjectViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        for (i = 0; i < manager.project.count(); i++ ) {
            data = manager.project.get(i);
            list.add(new IdNameVO(data.getId(), data.getProjectName()));
        }

        HistoryScreen.showHistory(Messages.get().history_sampleProject(),
                                  Constants.table().SAMPLE_PROJECT,
                                  list);
    }

    /**
     * shows the history of sample organizations
     */
    public static void organization(SampleManager1 manager) {
        int i;
        SampleOrganizationViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        for (i = 0; i < manager.organization.count(); i++ ) {
            data = manager.organization.get(i);
            list.add(new IdNameVO(data.getId(), data.getOrganizationName()));
        }

        HistoryScreen.showHistory(Messages.get().history_sampleOrganization(),
                                  Constants.table().SAMPLE_ORGANIZATION,
                                  list);
    }

    /**
     * shows the history of sample items
     */
    public static void item(SampleManager1 manager) {
        int i;
        StringBuilder sb;
        SampleItemViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        sb = new StringBuilder();
        for (i = 0; i < manager.item.count(); i++ ) {
            data = manager.item.get(i);
            sb.setLength(0);
            sb.append(data.getItemSequence());
            if (data.getContainer() != null) {
                sb.append(" [");
                sb.append(data.getContainer());
                sb.append("]");
            }
            list.add(new IdNameVO(data.getId(), sb.toString()));
        }

        HistoryScreen.showHistory(Messages.get().history_sampleItem(),
                                  Constants.table().SAMPLE_ITEM,
                                  list);
    }

    /**
     * shows the history of analyses
     */
    public static void analysis(SampleManager1 manager) {
        int i, j;
        StringBuilder sb;
        AnalysisViewDO data;
        SampleItemViewDO item;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        sb = new StringBuilder();
        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            for (j = 0; j < manager.analysis.count(item); j++ ) {
                data = manager.analysis.get(item, j);
                sb.setLength(0);
                sb.append(data.getTestName());
                sb.append(", ");
                sb.append(data.getMethodName());
                list.add(new IdNameVO(data.getId(), sb.toString()));
            }
        }

        HistoryScreen.showHistory(Messages.get().history_analysis(),
                                  Constants.table().ANALYSIS,
                                  list);
    }

    /**
     * shows the history of the specified analysis' results
     */
    public static void currentResult(SampleManager1 manager, Integer analysisId) {
        int i, j;
        ResultViewDO data;
        AnalysisViewDO ana;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        ana = (AnalysisViewDO)manager.getObject(Constants.uid().getAnalysis(analysisId));
        for (i = 0; i < manager.result.count(ana); i++ ) {
            for (j = 0; j < manager.result.count(ana, i); j++ ) {
                data = manager.result.get(ana, i, j);
                list.add(new IdNameVO(data.getId(), data.getAnalyte()));
            }
        }
        HistoryScreen.showHistory(Messages.get().history_currentResult(),
                                  Constants.table().RESULT,
                                  list);
    }

    /**
     * Shows the history of the storages for both sample items and analyses. For
     * each sample item, first the history of its storages is shown and then the
     * history of its analyses' storages.
     */
    public static void storage(SampleManager1 manager) {
        int i, j, k;
        StorageViewDO data;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();

        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            for (j = 0; j < manager.storage.count(item); j++ ) {
                data = manager.storage.get(item, j);
                list.add(new IdNameVO(data.getId(), data.getStorageLocationName()));
            }

            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);
                for (k = 0; k < manager.storage.count(ana); k++ ) {
                    data = manager.storage.get(ana, k);
                    list.add(new IdNameVO(data.getId(), data.getStorageLocationName()));
                }
            }
        }

        HistoryScreen.showHistory(Messages.get().history_storage(), Constants.table().STORAGE, list);
    }

    /**
     * shows the history of sample qa events
     */
    public static void sampleQA(SampleManager1 manager) {
        int i;
        SampleQaEventViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        for (i = 0; i < manager.qaEvent.count(); i++ ) {
            data = manager.qaEvent.get(i);
            list.add(new IdNameVO(data.getId(), data.getQaEventName()));
        }

        HistoryScreen.showHistory(Messages.get().history_sampleQA(),
                                  Constants.table().SAMPLE_QAEVENT,
                                  list);
    }

    /**
     * shows the history of all analysis qa events
     */
    public static void analysisQA(SampleManager1 manager) {
        int i, j, k;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        AnalysisQaEventViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);
            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);
                for (k = 0; k < manager.qaEvent.count(ana); k++ ) {
                    data = manager.qaEvent.get(ana, k);
                    list.add(new IdNameVO(data.getId(), data.getQaEventName()));
                }
            }
        }

        HistoryScreen.showHistory(Messages.get().history_analysisQA(),
                                  Constants.table().ANALYSIS_QAEVENT,
                                  list);
    }

    /**
     * shows the history of auxiliary data
     */
    public static void auxData(SampleManager1 manager) {
        int i;
        AuxDataViewDO data;
        ArrayList<IdNameVO> list;

        list = new ArrayList<IdNameVO>();
        for (i = 0; i < manager.auxData.count(); i++ ) {
            data = manager.auxData.get(i);
            list.add(new IdNameVO(data.getId(), data.getAnalyteName()));
        }

        HistoryScreen.showHistory(Messages.get().history_auxData(),
                                  Constants.table().AUX_DATA,
                                  list);
    }

    /**
     * shows the history of the patient with this id
     */
    private static void patient(Integer patientId, String title) {
        IdNameVO hist;

        hist = new IdNameVO(patientId, "");
        HistoryScreen.showHistory(title, Constants.table().PATIENT, hist);
    }
}