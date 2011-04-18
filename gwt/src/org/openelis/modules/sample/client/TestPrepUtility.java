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
import org.openelis.domain.IdVO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.modules.test.client.TestPrepLookupScreen;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestPrepUtility extends Screen implements HasActionHandlers<TestPrepUtility.Action> {
    public enum Type {
        PANEL, TEST
    };

    public enum Action {
        DONE
    };

//    protected SampleManager             manager;
    protected Integer                   anInPrepId;
    protected Screen                    screen;

    private ArrayList<SampleDataBundle> bundles, analysisDataBundles;
    private ScreenService               panelService;
    private ValidationErrorsList        errorsList;

    public TestPrepUtility() {
        panelService = new ScreenService(
                                         "controller?service=org.openelis.modules.panel.server.PanelService");

        try {
            anInPrepId = DictionaryCache.getIdBySystemName("analysis_inprep");
        } catch (Exception e) {
            Window.alert("testlookup constructor: " + e.getMessage());
        }
    }

//    public SampleManager getManager() {
//        return manager;
//    }
//
//    public void setManager(SampleManager manager) {
//        this.manager = manager;
//    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    /**
     * This method is used under normal circumstances.  It is used when the test/panel has changed
     * and the screen needs to add the records to the tree and check for preps.
     * @param analysisDataBundle
     * @param type
     * @param id
     * @throws Exception
     */
    public void lookup(SampleDataBundle analysisDataBundle, Type type, Integer id) throws Exception {
        lookup(analysisDataBundle, type, id, null);
    }
    
    /**
     * This method is used under normal circumstances.  It is used when a test/panel
     * has been added via the quick entry screen and needs to add the records and check for preps.
     * @param analysisDataBundle
     * @param type
     * @param id
     * @param sectionId
     * @throws Exception
     */
    public void lookup(SampleDataBundle analysisDataBundle, Type type, Integer id, TestSectionViewDO tsVDO) throws Exception {
        ArrayList<IdVO> testIds;

//        assert manager != null : "manager is null";
        assert screen != null : "screen is null";

        bundles = new ArrayList<SampleDataBundle>();
        errorsList = new ValidationErrorsList();

        analysisDataBundles = new ArrayList<SampleDataBundle>(1);
        analysisDataBundles.add(analysisDataBundle);

        // we need to expand a panel to test ids
        if (type == Type.PANEL)
            testIds = panelService.callList("fetchTestIdsByPanelId", id);
        else {
            testIds = new ArrayList<IdVO>(1);
            testIds.add(new IdVO(id));
        }

        processTestListAndCheckPrepTests(testIds, tsVDO);
    }
    
    /**
     * This method is used when the screen has added reflex test.  All that is required is the
     * analysis data bundles for the reflex tests to be added.  The tree also needs to be selecting
     * the first row to be added.
     * @param analysisBundles
     * @throws Exception
     */
    public void lookup(ArrayList<SampleDataBundle> analysisBundles) throws Exception {
//        assert manager != null : "manager is null";
        assert screen != null : "screen is null";
        
        bundles = new ArrayList<SampleDataBundle>();
        errorsList = new ValidationErrorsList();

        this.analysisDataBundles = analysisBundles;

        checkPrepTests(analysisBundles);
    }
    
    /**
     * This method is used to import analyses from an order.  This will add an analysis and check for preps.  The order
     * screen is not responsible for checking for preps.
     * @param analysisDataBundle
     * @param orderTestList
     * @throws Exception
     */
    public void lookup(SampleDataBundle analysisDataBundle, ArrayList<OrderTestViewDO> orderTestList) throws Exception {
        int             i;
        ArrayList<IdVO> testIds;
        OrderTestViewDO testDO;

//        assert manager != null : "manager is null";
        assert screen != null : "screen is null";

        bundles = new ArrayList<SampleDataBundle>();
        errorsList = new ValidationErrorsList();

        analysisDataBundles = new ArrayList<SampleDataBundle>(1);
        analysisDataBundles.add(analysisDataBundle);

        testIds = new ArrayList<IdVO>(orderTestList.size());
        for (i = 0; i < orderTestList.size(); i++){
            testDO = orderTestList.get(i);
            testIds.add(new IdVO(testDO.getTestId()));
        }

        processTestListAndCheckPrepTests(testIds, null);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void processTestListAndCheckPrepTests(ArrayList<IdVO> testIds, TestSectionViewDO tsVDO) throws Exception {
        int                          addedIndex;
        IdVO                         idVO;
        ArrayList<Object>            prepBundle;
        ArrayList<ArrayList<Object>> prepBundles;
        AnalysisManager              anMan;
        SampleDataBundle             anBundle, analysisDataBundle;
        SampleManager                manager;
        TestManager                  testMan;
        TestPrepManager              prepMan;

        analysisDataBundle = analysisDataBundles.get(0);
        manager = analysisDataBundle.getSampleManager();
        anMan = manager.getSampleItems().getAnalysisAt(analysisDataBundle.getSampleItemIndex());

        prepBundles = new ArrayList<ArrayList<Object>>();
        for (int i = 0; i < testIds.size(); i++ ) {
            idVO = testIds.get(i);

            if (idVO.getId() != null) {
                testMan = TestManager.fetchWithPrepTestsSampleTypes(idVO.getId());

                if (testMan.canAssignThisSection(tsVDO)) {
                    if (i == 0) // the first test id row will be in the analysis
                                // manager already
                        addedIndex = analysisDataBundle.getAnalysisIndex();
                    else
                        addedIndex = anMan.addAnalysis();

                    anMan.setTestAt(testMan, addedIndex);
                    if (tsVDO != null)
                        anMan.getAnalysisAt(addedIndex).setSectionId(tsVDO.getSectionId());

                    anBundle = anMan.getBundleAt(addedIndex);
                    bundles.add(anBundle);

                    // create prep bundle and add to list for prep picker screen
                    try {
                        prepMan = testMan.getPrepTests();
                        if (prepMan.count() > 0) {
                            prepBundle = new ArrayList<Object>(2);
                            prepBundle.add(anBundle);
                            prepBundle.add(prepMan);
                            prepBundles.add(prepBundle);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                } else {
                    if (tsVDO != null) {
                        errorsList.add(new FormErrorException("insufficientPrivilegesAddTestForSection",
                                                              testMan.getTest().getName(),
                                                              testMan.getTest().getMethodName(),
                                                              tsVDO.getSection()));
                    } else {
                        errorsList.add(new FormErrorException("insufficientPrivilegesAddTest",
                                                              testMan.getTest().getName(),
                                                              testMan.getTest().getMethodName()));
                    }
                }
            } else{
                anMan.removeTestAt(analysisDataBundle.getAnalysisIndex());
                bundles.add(analysisDataBundle);
            }
        }
        
        if (prepBundles.size() > 0)
            drawTestPrepScreen(prepBundles);
        else
            fireFinished();
    }
    
    private void drawTestPrepScreen(ArrayList<ArrayList<Object>> prepBundles) {
        ScreenWindow         modal;
        TestPrepLookupScreen prepPickerScreen;
        
        try {
            prepPickerScreen = new TestPrepLookupScreen();
            prepPickerScreen.addActionHandler(new ActionHandler<TestPrepLookupScreen.Action>() {
                @SuppressWarnings("unchecked")
                public void onAction(ActionEvent<TestPrepLookupScreen.Action> event) {
                    int                                i;
                    ArrayList<Object>                  prepBundle;
                    ArrayList<ArrayList<Object>>       prepBundles;
                    HashMap<ArrayList<Object>,Integer> indexLinks;

                    if (event.getAction() == TestPrepLookupScreen.Action.SELECTED_PREP_ROW) {
                        indexLinks = new HashMap<ArrayList<Object>,Integer>();
                        prepBundles = (ArrayList<ArrayList<Object>>)event.getData();
                        for (i = 0; i < prepBundles.size(); i++) {
                            prepBundle = prepBundles.get(i);
                            selectedPrepTest(prepBundle, indexLinks);
                        }
                        
                        fireFinished();
                    } else if (event.getAction() == TestPrepLookupScreen.Action.CANCEL) {
                        fireCancelled();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setContent(prepPickerScreen);
        modal.setName(consts.get("prepTestPicker"));
        prepPickerScreen.setBundles(prepBundles);
    }

    //
    // This gets called when a prep test is selected. It is not in the analysis
    // manager at this point.
    //
    @SuppressWarnings("unchecked")
    private void selectedPrepTest(ArrayList<Object> prepBundle, HashMap<ArrayList<Object>,Integer> indexLinks) {
        int               addedIndex;
        Object            tempBundle;
        Integer           parentIndex, sectionId, tpId;
        AnalysisManager   anMan;
        AnalysisViewDO    anDO, prepDO;
        SampleDataBundle  anaBundle;
        TestManager       testMan;
        TestSectionViewDO tsVDO;

        if (prepBundle.get(0) instanceof SampleDataBundle) {
            anaBundle = (SampleDataBundle) prepBundle.get(0);
            parentIndex = anaBundle.getAnalysisIndex();
        } else {
            tempBundle = (ArrayList<Object>) prepBundle.get(0);
            parentIndex = indexLinks.get(tempBundle);
            do {
                tempBundle = ((ArrayList<Object>)tempBundle).get(0);
            } while (!(tempBundle instanceof SampleDataBundle)) ;
            anaBundle = (SampleDataBundle) tempBundle;
        }
        
        try {
            anMan = anaBundle.getSampleManager().getSampleItems()
                             .getAnalysisAt(anaBundle.getSampleItemIndex());
            tpId = (Integer)prepBundle.get(1);
            sectionId = (Integer)prepBundle.get(2);

            prepDO = checkForPrepTest(anMan, tpId);
    
            if (prepDO != null) { // prep already exists
                anDO = anMan.getAnalysisAt(parentIndex);
    
                anDO.setPreAnalysisId(prepDO.getId());
                anDO.setStatusId(anInPrepId);
                anDO.setAvailableDate(null);
                anDO.setPreAnalysisTest(prepDO.getTestName());
                anDO.setPreAnalysisMethod(prepDO.getMethodName());
            } else { // prep doesn't exist in the manager yet
                testMan = null;
                try {
                    testMan = TestManager.fetchWithPrepTestsSampleTypes(tpId);
                    tsVDO = new TestSectionViewDO();
                    tsVDO.setSectionId(sectionId);
                    if (testMan.canAssignThisSection(tsVDO)) {
                        addedIndex = anMan.addPreAnalysis(parentIndex);
                        indexLinks.put(prepBundle, addedIndex);
                        anDO = anMan.getAnalysisAt(addedIndex);
                        anMan.setTestAt(testMan, addedIndex);
                        bundles.add(anMan.getBundleAt(addedIndex));
                    } else {
                        errorsList.add(new FormErrorException("insufficientPrivilegesAddTestForSection",
                                                              testMan.getTest().getName(),
                                                              testMan.getTest().getMethodName(),
                                                              tsVDO.getSection()));
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                
            }
        } catch (Exception anyE) {
            errorsList.add(new FormErrorException());
        }
    }

    private AnalysisViewDO checkForPrepTest(AnalysisManager anMan, Integer testId) {
        AnalysisViewDO anDO, returnDO;

        returnDO = null;
        for (int i = 0; i < anMan.count(); i++ ) {
            anDO = anMan.getAnalysisAt(i);

            if (testId.equals(anDO.getTestId())) {
                returnDO = anDO;
                break;
            }
        }

        return returnDO;
    }

    private void checkPrepTests(ArrayList<SampleDataBundle> analysisBundles) throws Exception {
        int                          i;
        ArrayList<Object>            prepBundle;
        ArrayList<ArrayList<Object>> prepBundles;
        AnalysisManager              anMan;
        SampleDataBundle             bundle;
        SampleManager                manager;
        TestPrepManager              prepMan;
        TestManager                  testMan;
        
        prepBundles = new ArrayList<ArrayList<Object>>();
        for (i = 0; i < analysisBundles.size(); i++) {
            bundle = analysisBundles.get(i);
            manager = bundle.getSampleManager();
            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            testMan = anMan.getTestAt(bundle.getAnalysisIndex());

            bundles.add(bundle);

            // check for prep tests
            try {
                prepMan = testMan.getPrepTests();
                if (prepMan.count() > 0) {
                    prepBundle = new ArrayList<Object>(2);
                    prepBundle.add(bundle);
                    prepBundle.add(prepMan);
                    prepBundles.add(prepBundle);
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }

        if (prepBundles.size() > 0)
            drawTestPrepScreen(prepBundles);
        else
            fireFinished();
    }

    private void fireFinished() {
        ActionEvent.fire(this, Action.DONE, bundles);
        
        if (errorsList.size() > 0)
            screen.showErrors(errorsList);
    }

    private void fireCancelled() {
        int              i;
        SampleDataBundle anBundle;
        AnalysisManager  anMan;
        SampleManager    manager;
        
        try {
            for (i = 0; i < analysisDataBundles.size(); i++) {
                anBundle = analysisDataBundles.get(i);
                manager = anBundle.getSampleManager();
                anMan = manager.getSampleItems().getAnalysisAt(anBundle.getSampleItemIndex());
                anMan.removeTestAt(anBundle.getAnalysisIndex());
            }
            analysisDataBundles.clear();
            bundles.clear();
            ActionEvent.fire(this, Action.DONE, bundles);
        } catch (Exception anyE) {
            Window.alert(consts.get("prepTestCancelledCleanupException"));
        }
    }
}
