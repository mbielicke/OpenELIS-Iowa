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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.modules.test.client.TestReflexLookupScreen;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestReflexUtility extends Screen implements
                                             HasActionHandlers<TestReflexUtility.Action> {
    public enum Action {
        DONE
    };

    protected Screen                    screen;

    private Integer                     autoAddId, promptId;
    private ArrayList<SampleDataBundle> bundles;
    private ValidationErrorsList        errorsList;

    public TestReflexUtility() {
        try {
            autoAddId = DictionaryCache.getIdBySystemName("reflex_auto");
            promptId = DictionaryCache.getIdBySystemName("reflex_prompt");
        } catch (Exception e) {
            Window.alert("testlookup constructor: " + e.getMessage());
        }
    }
    
    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void resultEntered(SampleDataBundle analysisDataBundle, ResultViewDO resultDO) throws Exception {
        ArrayList<ResultViewDO>                           reflexResults;
        ArrayList<SampleDataBundle>                       dataBundles;
        HashMap<SampleDataBundle,ArrayList<ResultViewDO>> reflexMap;
        
        dataBundles = new ArrayList<SampleDataBundle>();
        dataBundles.add(analysisDataBundle);
        
        reflexResults = new ArrayList<ResultViewDO>();
        reflexResults.add(resultDO);
        
        reflexMap = new HashMap<SampleDataBundle,ArrayList<ResultViewDO>>();
        reflexMap.put(analysisDataBundle, reflexResults);
        
        resultsEntered(dataBundles, reflexMap);
    }
    
    public void resultsEntered(ArrayList<SampleDataBundle> dataBundles,
                               HashMap<SampleDataBundle,ArrayList<ResultViewDO>> reflexMap) throws Exception {
        assert screen != null : "screen is null";
        bundles = new ArrayList<SampleDataBundle>();
        errorsList = new ValidationErrorsList();
        
        processReflexTests(dataBundles, reflexMap);
    }

    private void processReflexTests(ArrayList<SampleDataBundle> dataBundles,
                                    HashMap<SampleDataBundle,ArrayList<ResultViewDO>> reflexMap) throws Exception {
        int                                               i, j;
        ArrayList<Object>                                 reflexBundle;
        ArrayList<ArrayList<Object>>                      reflexBundles;
        ArrayList<ResultViewDO>                           reflexResults;
        ArrayList<TestReflexViewDO>                       reflexList;
        HashMap<ResultViewDO,ArrayList<TestReflexViewDO>> anaReflexMap;
        ResultViewDO                                      rVDO;
        SampleDataBundle                                  dataBundle;
        TestManager                                       testMan;
        TestReflexManager                                 reflexMan;

        reflexBundles = new ArrayList<ArrayList<Object>>();
        for (i = 0; i < dataBundles.size(); i++) {
            dataBundle = dataBundles.get(i);
            reflexResults = reflexMap.get(dataBundle);
            
            testMan = dataBundle.getSampleManager().getSampleItems().getAnalysisAt(dataBundle.getSampleItemIndex())
                                .getTestAt(dataBundle.getAnalysisIndex());
            reflexMan = testMan.getReflexTests();
            anaReflexMap = new HashMap<ResultViewDO,ArrayList<TestReflexViewDO>>();
            for (j = 0; j < reflexResults.size(); j++) {
                rVDO = reflexResults.get(j);
                reflexList = reflexMan.getReflexListByTestAnalyteIdTestResultId(rVDO.getTestAnalyteId(),
                                                                                rVDO.getTestResultId());
                if (reflexList.size() > 0)
                    anaReflexMap.put(rVDO, reflexList);
            }
            
            if (!anaReflexMap.isEmpty()) {
                reflexBundle = new ArrayList<Object>();
                reflexBundle.add(dataBundle);
                reflexBundle.add(anaReflexMap);
                reflexBundles.add(reflexBundle);
            }
        }

        if (reflexBundles.size() > 0)
            drawTestReflexScreen(reflexBundles);
        else
            fireFinished();
    }

    private void drawTestReflexScreen(ArrayList<ArrayList<Object>> reflexBundles) {
        ScreenWindow           modal;
        TestReflexLookupScreen reflexPickerScreen;

        try {
            reflexPickerScreen = new TestReflexLookupScreen();
            reflexPickerScreen.addActionHandler(new ActionHandler<TestReflexLookupScreen.Action>() {
                @SuppressWarnings("unchecked")
                public void onAction(ActionEvent<TestReflexLookupScreen.Action> event) {
                    int                          i;
                    ArrayList<Object>            reflexedBundle;
                    ArrayList<ArrayList<Object>> reflexedBundles;

                    if (event.getAction() == TestReflexLookupScreen.Action.SELECTED_REFLEX_ROW) {
                        reflexedBundles = (ArrayList<ArrayList<Object>>)event.getData();
                        for (i = 0; i < reflexedBundles.size(); i++) {
                            reflexedBundle = reflexedBundles.get(i);
                            addReflexTest(reflexedBundle);
                        }
                        
                        fireFinished();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setContent(reflexPickerScreen);
        modal.setName(consts.get("reflexTestPicker"));
        reflexPickerScreen.setBundles(reflexBundles);
    }
    
    private void addReflexTest(ArrayList<Object> reflexBundle) {
        int               addedIndex;
        Integer           sectionId;
        AnalysisManager   anMan;
        AnalysisViewDO    anVDO;
        ResultViewDO      rVDO;
        SampleDataBundle  anaBundle;
        SampleItemManager itemMan;
        TestManager       testMan;
        TestReflexViewDO  reflexVDO;
        TestSectionViewDO tsVDO;
        
        anaBundle = (SampleDataBundle) reflexBundle.get(0);
        rVDO = (ResultViewDO) reflexBundle.get(1);
        reflexVDO = (TestReflexViewDO) reflexBundle.get(2);
        sectionId = (Integer) reflexBundle.get(3);
        try {
            itemMan = anaBundle.getSampleManager().getSampleItems();

            if (promptId.equals(reflexVDO.getFlagsId()) || autoAddId.equals(reflexVDO.getFlagsId()) ||
                !duplicatePresent(itemMan, reflexVDO.getAddTestId())) {
                testMan = TestManager.fetchWithPrepTestsSampleTypes(reflexVDO.getAddTestId());
                tsVDO = new TestSectionViewDO();
                tsVDO.setSectionId(sectionId);
                if (testMan.canAssignThisSection(tsVDO)) {
                    anMan = itemMan.getAnalysisAt(anaBundle.getSampleItemIndex());
                    anVDO = anMan.getAnalysisAt(anaBundle.getAnalysisIndex());
                    addedIndex = anMan.addReflexAnalysis(anVDO.getId(), rVDO.getId());
                    anMan.setTestAt(testMan, addedIndex);
                    bundles.add(anMan.getBundleAt(addedIndex));
                } else {
                    errorsList.add(new FormErrorException("insufficientPrivilegesAddTestForSection",
                                                          testMan.getTest().getName(),
                                                          testMan.getTest().getMethodName(),
                                                          tsVDO.getSection()));
                }
            }
        } catch(Exception e) {
            Window.alert("addReflexTest: "+e.getMessage());
        }
    }
    
    private boolean duplicatePresent(SampleItemManager itemMan, Integer testId) {
        int             i, j;
        AnalysisManager anMan;
        AnalysisViewDO  anDO;
        
        try {
            for (i = 0; i < itemMan.count(); i++) {
                anMan = itemMan.getAnalysisAt(i);
                for (j = 0; j < anMan.count(); j++) {
                    anDO = anMan.getAnalysisAt(j);
                    if (testId.equals(anDO.getTestId()))
                        return true;
                }
            }
        } catch (Exception e) {
            Window.alert("duplicatePresent: " + e.getMessage());
        }
        
        return false;
    }

    private void fireFinished() {
        ActionEvent.fire(this, Action.DONE, bundles);

        if (errorsList.size() > 0)
            screen.showErrors(errorsList);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
