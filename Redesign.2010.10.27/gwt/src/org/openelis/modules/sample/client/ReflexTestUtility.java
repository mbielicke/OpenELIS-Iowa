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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestIdDupsVO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.modules.test.client.TestReflexLookupScreen;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ReflexTestUtility extends Screen implements
                                             HasActionHandlers<ReflexTestUtility.Action> {
    public enum Action {
        DONE
    };

    protected Screen                    screen;

    private SampleDataBundle            analysisDataBundle;
    private ArrayList<SampleDataBundle> bundles;
    private TestReflexLookupScreen      reflexPickerScreen;
    private ValidationErrorsList        errorsList;
    private boolean                     lookupDrawn; 

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void resultEntered(SampleDataBundle analysisDataBundle, ResultViewDO resultDO)
                                                                                         throws Exception {
        int sampleItemIndex, analysisIndex;
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        TestManager testMan;

        assert screen != null : "screen is null";
        this.analysisDataBundle = analysisDataBundle;
        bundles = new ArrayList<SampleDataBundle>();
        errorsList = new ValidationErrorsList();
        lookupDrawn = false;
        
        sampleItemIndex = analysisDataBundle.getSampleItemIndex();
        analysisIndex = analysisDataBundle.getAnalysisIndex();
        anMan = analysisDataBundle.getSampleManager().getSampleItems().getAnalysisAt(sampleItemIndex);
        anDO = anMan.getAnalysisAt(analysisIndex);
        testMan = anMan.getTestAt(analysisIndex);

        processReflexTests(resultDO, anDO, testMan);
    }

    private void processReflexTests(final ResultViewDO resultDO, final AnalysisViewDO anDO, TestManager testMan) throws Exception {
        TestReflexManager reflexMan;
        ArrayList<TestReflexViewDO> reflexList;

        
        reflexMan = testMan.getReflexTests();
        reflexList = reflexMan.getReflexListByTestResultId(resultDO.getTestResultId());
        
        if(reflexPickerScreen == null){
            reflexPickerScreen = new TestReflexLookupScreen();
            reflexPickerScreen.addActionHandler(new ActionHandler<TestReflexLookupScreen.Action>() {
                public void onAction(ActionEvent<TestReflexLookupScreen.Action> event) {
                    if (event.getAction() == TestReflexLookupScreen.Action.SELECTED_REFLEX_ROW) {
                        lookupDrawn = false;
                        addReflexTests((ArrayList<TestIdDupsVO>)event.getData(), anDO.getId(), resultDO.getId());
                    }
                }
            });
        }
        
        reflexPickerScreen.setData(reflexList);
        if(reflexPickerScreen.needToDrawPopup()){
            lookupDrawn = true;
            ModalWindow modal = new ModalWindow();
            modal.setContent(reflexPickerScreen);
            modal.setName(consts.get("reflexTestPicker") + " " + anDO.getTestName() + " : " + anDO.getMethodName());
        }else
            addReflexTests(reflexPickerScreen.getReflexList(), anDO.getId(), resultDO.getId());
    }

    private void addReflexTests(ArrayList<TestIdDupsVO> reflexList, Integer analysisId, Integer resultId) {
        TestManager testMan;
        SampleItemManager itemMan;
        AnalysisManager anMan;
        TestIdDupsVO testDO;
        int addedIndex;

        try{
            itemMan = analysisDataBundle.getSampleManager().getSampleItems();
            anMan = itemMan.getAnalysisAt(analysisDataBundle.getSampleItemIndex());
            
            for(int i=0; i<reflexList.size(); i++){
                testDO = reflexList.get(i);
                testMan = TestManager.fetchWithPrepTestsSampleTypes(testDO.getTestId());

                if(!testDO.isCheckForDups() || !duplicatePresent(itemMan, testDO.getTestId())){
                    if (testMan.canAssign()) {
                        addedIndex = anMan.addReflexAnalysis(analysisId, resultId);
                        anMan.setTestAt(testMan, addedIndex);
                        bundles.add(anMan.getBundleAt(addedIndex));
                        
                    }else{
                        errorsList.add(new FormErrorException("insufficientPrivilegesAddTest",
                                                              testMan.getTest().getName(),
                                                              testMan.getTest().getMethodName()));
                    }
                }
            }
            
            fireFinished();
            
        }catch(Exception e){
            Window.alert("addReflexTests: "+e.getMessage());
        }
    }
    
    private boolean duplicatePresent(SampleItemManager itemMan, Integer testId) {
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        
        try{
            for(int i=0; i<itemMan.count(); i++){
                anMan = itemMan.getAnalysisAt(i);
                
                for(int j=0; j < anMan.count(); j++){
                    anDO = anMan.getAnalysisAt(j);
                    
                    if(testId.equals(anDO.getTestId()))
                        return true;
                }
            }
        
        }catch(Exception e){
            Window.alert("duplicatePresent: " + e.getMessage());
        }
        
        return false;
    }

    private void fireFinished() {
        if(!lookupDrawn && bundles.size() > 0){
            ActionEvent.fire(this, Action.DONE, bundles);
    
            if (errorsList.size() > 0)
                screen.showErrors(errorsList);
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

}
