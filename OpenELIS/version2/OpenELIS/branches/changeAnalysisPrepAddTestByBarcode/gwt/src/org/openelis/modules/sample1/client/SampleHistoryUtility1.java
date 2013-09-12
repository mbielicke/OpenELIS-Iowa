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
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.manager.StorageManager;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.Window;

public class SampleHistoryUtility1 {

    private SampleManager1   manager;
    private WindowInt window;

    public SampleHistoryUtility1(WindowInt window) {
        this.window = window;
    }

    public void historySample() {
        IdNameVO hist;

        window.setBusy();
        hist = new IdNameVO(manager.getSample().getId(), manager.getSample()
                                                                .getAccessionNumber()
                                                                .toString());
        HistoryScreen.showHistory(Messages.get().historySample(),
                                  Constants.table().SAMPLE,
                                  hist);
        window.clearStatus();
    }

    public void historySampleEnvironmental() {
        IdNameVO hist;
        SampleEnvironmentalDO env;

        window.setBusy();
        try {
            env = manager.getSampleEnvironmental();
            hist = new IdNameVO(env.getId(), env.getLocation());
            HistoryScreen.showHistory(Messages.get().historySampleEnvironmental(),
                                      Constants.table().SAMPLE_ENVIRONMENTAL,
                                      hist);
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleEnvironmental: " + e.getMessage());
        }

        window.clearStatus();
    }

    public void historySamplePrivateWell() {
        IdNameVO hist;
        SamplePrivateWellViewDO well;

        window.setBusy();
        try {
            well = manager.getSamplePrivateWell();
            hist = new IdNameVO(well.getId(), well.getLocation());
            HistoryScreen.showHistory(Messages.get().historySamplePrivateWell(),
                                      Constants.table().SAMPLE_PRIVATE_WELL,
                                      hist);
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySamplePrivateWell: " + e.getMessage());
        }

        window.clearStatus();
    }

    public void historySampleSDWIS() {
        IdNameVO hist;
        SampleSDWISViewDO sdwis;

        window.setBusy();
        try {
            sdwis = manager.getSampleSDWIS();
            hist = new IdNameVO(sdwis.getId(), sdwis.getLocation());
            HistoryScreen.showHistory(Messages.get().historySampleSDWIS(),
                                      Constants.table().SAMPLE_SDWIS,
                                      hist);
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleSDWIS: " + e.getMessage());
        }

        window.clearStatus();
    }

    public void historySampleProject() {
        int i, count;
        IdNameVO refVoList[];
        SampleProjectViewDO data;

        window.setBusy();
        try {
            count = manager.project.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.project.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getProjectName());
            }

            HistoryScreen.showHistory(Messages.get().historySampleProject(),
                                      Constants.table().SAMPLE_PROJECT,
                                      refVoList);
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleProject: " + e.getMessage());
        }

        window.clearStatus();
    }

    public void historySampleOrganization() {
        int i, count;
        IdNameVO refVoList[];
        SampleOrganizationViewDO data;

        window.setBusy();
        try {
            count = manager.organization.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.organization.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getOrganizationName());
            }

            HistoryScreen.showHistory(Messages.get().historySampleOrganization(),
                                      Constants.table().SAMPLE_ORGANIZATION,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleProject: " + e.getMessage());
        }
    }

    public void historySampleItem() {
        int i, count;
        IdNameVO refVoList[];
        SampleItemViewDO data;
        String container;

        window.setBusy();
        try {
            count = manager.item.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.item.get(i);
                container = data.getContainer();
                if (container == null)
                    container = "";
                else
                    container += " - ";
                refVoList[i] = new IdNameVO(data.getId(), data.getItemSequence() +
                                                          container);
            }

            HistoryScreen.showHistory(Messages.get().historySampleItem(),
                                      Constants.table().SAMPLE_ITEM,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleItem: " + e.getMessage());
        }
    }

    public void historyAnalysis() {
        int i, j, k, listIndex, itemCount, anCount;
        IdNameVO refVoList[];
        SampleItemViewDO item;
        AnalysisViewDO ana;

        window.setBusy();
        try {
            itemCount = manager.item.count();

            // figure out total # of analyses
            anCount = 0;
            for (i = 0; i < itemCount; i++ )
                anCount += manager.analysis.count(manager.item.get(i));

            refVoList = new IdNameVO[anCount];
            listIndex = 0;
            for (j = 0; j < itemCount; j++ ) {
                item = manager.item.get(i);
                anCount = manager.analysis.count(item);

                for (k = 0; k < anCount; k++ ) {
                    ana = manager.analysis.get(item, k);
                    refVoList[listIndex] = new IdNameVO(ana.getId(),
                                                        ana.getTestName() +
                                                                        " : " +
                                                                        ana.getMethodName());
                    listIndex++ ;
                }
            }

            HistoryScreen.showHistory(Messages.get().historyAnalysis(),
                                      Constants.table().ANALYSIS,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            Window.alert("historyAnalysis: " + e.getMessage());
            window.clearStatus();
        }
    }

    public void historyCurrentResult(int sampleItemIndex, int analysisIndex) {
        int i, j, rowCount, count;
        ArrayList<ResultViewDO> row;
        ResultViewDO data;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;
        AnalysisResultManager man;

        /*try {
            man = manager.getSampleItems()
                         .getAnalysisAt(sampleItemIndex)
                         .getAnalysisResultAt(analysisIndex);
            rowCount = man.rowCount();
            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < rowCount; i++ ) {
                row = man.getRowAt(i);
                count = row.size();

                for (j = 0; j < count; j++ ) {
                    data = row.get(j);
                    refVoArrayList.add(new IdNameVO(data.getId(), data.getAnalyte()));
                }
            }

            refVoList = new IdNameVO[refVoArrayList.size()];
            for (i = 0; i < refVoArrayList.size(); i++ )
                refVoList[i] = refVoArrayList.get(i);

            HistoryScreen.showHistory(Messages.get().historyCurrentResult(),
                                      Constants.table().RESULT,
                                      refVoList);
            window.clearStatus();

        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historyCurrentResult: " + e.getMessage());
        }*/
    }

    public void historyStorage() {
        int i, j, k, itemCount, anCount, storageCount;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;
        SampleItemManager man;
        AnalysisManager anMan;
        StorageManager storageMan;
        StorageViewDO data;

        /*window.setBusy();
        try {
            man = manager.getSampleItems();
            itemCount = man.count();

            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < itemCount; i++ ) {
                anMan = man.getAnalysisAt(i);
                anCount = anMan.count();
                storageMan = man.getStorageAt(i);
                storageCount = storageMan.count();

                for (j = 0; j < storageCount; j++ ) {
                    data = storageMan.getStorageAt(j);
                    refVoArrayList.add(new IdNameVO(data.getId(),
                                                    data.getStorageLocationName()));
                }

                for (j = 0; j < anCount; j++ ) {
                    storageMan = anMan.getStorageAt(j);
                    storageCount = storageMan.count();

                    for (k = 0; k < storageCount; k++ ) {
                        data = storageMan.getStorageAt(k);
                        refVoArrayList.add(new IdNameVO(data.getId(),
                                                        data.getStorageLocationName()));
                    }
                }
            }

            refVoList = new IdNameVO[refVoArrayList.size()];
            for (i = 0; i < refVoArrayList.size(); i++ )
                refVoList[i] = refVoArrayList.get(i);

            HistoryScreen.showHistory(Messages.get().historyStorage(),
                                      Constants.table().STORAGE,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historyStorage: " + e.getMessage());
        }*/
    }

    public void historySampleQA() {
        int i, count;
        IdNameVO refVoList[];
        SampleQaEventManager man;
        SampleQaEventViewDO data;

        /*window.setBusy();
        try {
            man = manager.getQaEvents();
            count = manager.qaEvent.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getSampleQAAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getQaEventName());
            }

            HistoryScreen.showHistory(Messages.get().historySampleQA(),
                                      Constants.table().SAMPLE_QAEVENT,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historySampleQA: " + e.getMessage());
        }*/
    }

    public void historyAnalysisQA() {
        int i, j, k, itemCount, anCount, qaCount;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;
        SampleItemManager man;
        AnalysisManager anMan;
        AnalysisQaEventManager qaMan;
        AnalysisQaEventViewDO data;

        /*window.setBusy();
        try {
            man = manager.getSampleItems();
            itemCount = man.count();

            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < itemCount; i++ ) {
                anMan = man.getAnalysisAt(i);
                anCount = anMan.count();

                for (j = 0; j < anCount; j++ ) {
                    qaMan = anMan.getQAEventAt(j);
                    qaCount = qaMan.count();

                    for (k = 0; k < qaCount; k++ ) {
                        data = qaMan.getAnalysisQAAt(k);
                        refVoArrayList.add(new IdNameVO(data.getId(),
                                                        data.getQaEventName()));
                    }
                }
            }

            refVoList = new IdNameVO[refVoArrayList.size()];
            for (i = 0; i < refVoArrayList.size(); i++ )
                refVoList[i] = refVoArrayList.get(i);

            HistoryScreen.showHistory(Messages.get().historyAnalysisQA(),
                                      Constants.table().ANALYSIS_QAEVENT,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historyAnalysis: " + e.getMessage());
        }*/
    }

    public void historyAuxData() {
        int i, count;
        IdNameVO refVoList[];
        AuxDataManager man;
        AuxDataViewDO data;
        AuxFieldViewDO fieldDO;

        /*window.setBusy();
        try {
            man = manager.getAuxData();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getAuxDataAt(i);
                fieldDO = man.getAuxFieldAt(i);
                refVoList[i] = new IdNameVO(data.getId(), fieldDO.getAnalyteName());
            }

            HistoryScreen.showHistory(Messages.get().historyAuxData(),
                                      Constants.table().AUX_DATA,
                                      refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            Window.alert("historyAuxData: " + e.getMessage());
        }*/
    }

    public void setManager(SampleManager1 manager) {
        this.manager = manager;
    }
}
