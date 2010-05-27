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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
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

    protected SampleManager             manager;
    protected Integer                   anInPrepId;
    protected Screen                    screen;

    private SampleDataBundle            analysisDataBundle;
    private ArrayList<SampleDataBundle> bundles;
    private int                         numberOfPrepScreensDrawn;
    private ScreenService               panelService;
    private ValidationErrorsList        errorsList;

    public TestPrepUtility() {
        panelService = new ScreenService(
                                         "controller?service=org.openelis.modules.panel.server.PanelService");

        try {
            anInPrepId = DictionaryCache.getIdFromSystemName("analysis_inprep");
        } catch (Exception e) {
            Window.alert("testlookup constructor: " + e.getMessage());
        }
    }

    public SampleManager getManager() {
        return manager;
    }

    public void setManager(SampleManager manager) {
        this.manager = manager;
    }

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
        ArrayList<IdVO> testIds;

        assert manager != null : "manager is null";
        assert screen != null : "screen is null";

        errorsList = new ValidationErrorsList();
        this.analysisDataBundle = analysisDataBundle;
        bundles = new ArrayList<SampleDataBundle>();
        numberOfPrepScreensDrawn = 0;

        // we need to expand a panel to test ids
        if (type == Type.PANEL)
            testIds = panelService.callList("fetchTestIdsByPanelId", id);
        else {
            testIds = new ArrayList<IdVO>();
            testIds.add(new IdVO(id));
        }

        processTestListAndCheckPrepTests(testIds);
    }
    
    /**
     * This method is used when the screen has added reflex test.  All that is required is the
     * analysis data bundles for the reflex tests to be added.  The tree also needs to be selecting
     * the first row to be added.
     * @param analysisBundles
     * @throws Exception
     */
    public void lookup(ArrayList<SampleDataBundle> analysisBundles) throws Exception {
        assert manager != null : "manager is null";
        assert screen != null : "screen is null";
        
        errorsList = new ValidationErrorsList();
        this.analysisDataBundle = analysisBundles.get(0);
        bundles = new ArrayList<SampleDataBundle>();
        numberOfPrepScreensDrawn = 0;

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
        ArrayList<IdVO> testIds;
        OrderTestViewDO testDO;

        assert manager != null : "manager is null";
        assert screen != null : "screen is null";

        errorsList = new ValidationErrorsList();
        this.analysisDataBundle = analysisDataBundle;
        bundles = new ArrayList<SampleDataBundle>();
        numberOfPrepScreensDrawn = 0;

        testIds = new ArrayList<IdVO>();
        for(int i=0; i<orderTestList.size(); i++){
            testDO = orderTestList.get(i);
            testIds.add(new IdVO(testDO.getTestId()));
        }

        processTestListAndCheckPrepTests(testIds);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void processTestListAndCheckPrepTests(ArrayList<IdVO> testIds) throws Exception {
        IdVO idVO;
        AnalysisManager anMan;
        TestManager testMan;
        int addedIndex;

        anMan = manager.getSampleItems().getAnalysisAt(analysisDataBundle.getSampleItemIndex());

        for (int i = 0; i < testIds.size(); i++ ) {
            idVO = testIds.get(i);

            if (idVO.getId() != null) {
                testMan = TestManager.fetchWithPrepTestsSampleTypes(idVO.getId());

                if (testMan.canAssign()) {
                    if (i == 0) // the first test id row will be in the analysis
                                // manager already
                        addedIndex = analysisDataBundle.getAnalysisIndex();
                    else
                        addedIndex = anMan.addAnalysis();

                    anMan.setTestAt(testMan, addedIndex);
                    updateAnalysisAndCheckForPreps(anMan, addedIndex, testMan);
                } else {
                    errorsList.add(new FormErrorException("insufficientPrivilegesAddTest",
                                                          testMan.getTest().getName(),
                                                          testMan.getTest().getMethodName()));
                }
            } else{
                anMan.removeTestAt(analysisDataBundle.getAnalysisIndex());
                bundles.add(analysisDataBundle);
            }
        }

        fireFinished();
    }
    
    private void checkPrepTests(ArrayList<SampleDataBundle> bundles) throws Exception {
        SampleDataBundle bundle;
        int analysisIndex;
        AnalysisManager anMan;
        TestManager testMan;
        
        for (int i = 0; i < bundles.size(); i++ ) {
            bundle = bundles.get(i);
            analysisIndex = bundle.getAnalysisIndex();
            anMan =  manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            testMan = anMan.getTestAt(bundle.getAnalysisIndex());

            updateAnalysisAndCheckForPreps(anMan, analysisIndex, testMan);
        }

        fireFinished();
    }

    private void updateAnalysisAndCheckForPreps(AnalysisManager anMan,
                                                Integer analysisIndex,
                                                TestManager testMan) {
        TestPrepManager prepMan;
        AnalysisViewDO anDO;
        SampleDataBundle anBundle;

        anDO = anMan.getAnalysisAt(analysisIndex);
        anBundle = anMan.getBundleAt(analysisIndex);
        bundles.add(anBundle);

        // check for prep tests
        try {
            prepMan = testMan.getPrepTests();
            if (prepMan.count() > 0) {
                TestPrepViewDO requiredTestPrepDO = prepMan.getRequiredTestPrep();
                if (requiredTestPrepDO == null)
                    drawTestPrepScreen(anMan, anBundle, prepMan, anDO.getTestName() + ", " +
                                                                 anDO.getMethodName());
                else
                    selectedPrepTest(anMan, anBundle, requiredTestPrepDO.getPrepTestId());
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    //
    // This gets called when a prep test is selected. It is not in the analysis
    // manager at this point.
    //
    private void selectedPrepTest(AnalysisManager anMan,
                                  SampleDataBundle parentBundle,
                                  Integer prepTestId) {
        TestManager testMan;
        AnalysisViewDO anDO, prepDO;
        int addedIndex;

        prepDO = checkForPrepTest(anMan, prepTestId);

        if (prepDO != null) { // prep already exists
            anDO = anMan.getAnalysisAt(parentBundle.getAnalysisIndex());

            anDO.setPreAnalysisId(prepDO.getId());
            anDO.setStatusId(anInPrepId);
            anDO.setAvailableDate(null);
            anDO.setPreAnalysisTest(prepDO.getTestName());
            anDO.setPreAnalysisMethod(prepDO.getMethodName());

        } else { // prep doesnt exist in the manager yet
            testMan = null;
            try {
                testMan = TestManager.fetchWithPrepTestsSampleTypes(prepTestId);

            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
            
            if (testMan.canAssign()) {
                addedIndex = anMan.addPreAnalysis(parentBundle.getAnalysisIndex());
                anDO = anMan.getAnalysisAt(addedIndex);
                anMan.setTestAt(testMan, addedIndex);

                updateAnalysisAndCheckForPreps(anMan, addedIndex, testMan);
            } else {
                errorsList.add(new FormErrorException("insufficientPrivilegesAddTest",
                                          testMan.getTest().getName(),
                                          testMan.getTest().getMethodName()));
            }
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

    private void drawTestPrepScreen(final AnalysisManager anMan,
                                    final SampleDataBundle parentBundle,
                                    TestPrepManager manager,
                                    String testMethodName) {
        TestPrepLookupScreen prepPickerScreen;
        try {
            prepPickerScreen = new TestPrepLookupScreen();
            prepPickerScreen.addActionHandler(new ActionHandler<TestPrepLookupScreen.Action>() {
                public void onAction(ActionEvent<TestPrepLookupScreen.Action> event) {
                    if (event.getAction() == TestPrepLookupScreen.Action.SELECTED_PREP_ROW) {
                        TableDataRow selectedRow;
                        Integer testId;

                        numberOfPrepScreensDrawn-- ;
                        selectedRow = (TableDataRow)event.getData();
                        testId = (Integer)selectedRow.key;
                        selectedPrepTest(anMan, parentBundle, testId);
                    } else
                        numberOfPrepScreensDrawn-- ;

                    fireFinished();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }

        ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setContent(prepPickerScreen, ScreenWindow.position+(numberOfPrepScreensDrawn*20), ScreenWindow.position+(numberOfPrepScreensDrawn*20));
        modal.setName(consts.get("prepTestPicker") + " " + testMethodName);
        prepPickerScreen.setManager(manager);
        numberOfPrepScreensDrawn++ ;
    }

    private void fireFinished() {
        if (numberOfPrepScreensDrawn == 0)
            ActionEvent.fire(this, Action.DONE, bundles);
        
        if(errorsList.size()> 0)
            screen.showErrors(errorsList);
    }
}
