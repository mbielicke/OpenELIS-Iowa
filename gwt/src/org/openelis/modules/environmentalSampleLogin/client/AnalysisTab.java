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
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.deprecated.ScreenWindow;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.TestManager;
import org.openelis.metamap.SampleMetaMap;
import org.openelis.modules.testPrepPicker.client.TestPrepPickerScreen;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalysisTab extends Screen implements HasActionHandlers<AnalysisTab.Action> {
    public enum Action {CHANGED};
    private boolean loaded;
    
    private SampleMetaMap meta;
    
    private Screen parentScreen;
    protected TestPrepPickerScreen prepPickerScreen;
    
    protected AutoComplete<Integer> test, method;
    protected Dropdown<Integer> sectionId;
    protected CheckBox isReportable;
    
    protected AnalysisManager manager;
    protected AnalysisViewDO analysis;
    protected SampleItemViewDO sampleItem;
    protected Dropdown<Integer> statusId;

    public AnalysisTab(ScreenDefInt def, Screen parentScreen) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.analysis.server.AnalysisService");
        setDef(def);
        this.parentScreen = parentScreen;
        
        meta = new SampleMetaMap("sample.");
        
        initialize();
       
        setStatusesModel(DictionaryCache.getListByCategorySystemName("analysis_status"));
        setSectionsModel(new ArrayList());
    }
    
    private void initialize() {
        final AnalysisTab anTab = this;
        
        test = (AutoComplete)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.TEST.getName());
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setSelection(analysis.getTestId(), analysis.getTestName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try{
                    TestManager testMan=null;
                    TableDataRow selectedRow = test.getSelection();
                    //SampleTestMethodDO testDO=null;
                    
                    if(selectedRow != null){
                        testMan = TestManager.findByIdWithPrepTest(event.getValue());
                        manager.setTests(testMan);
                    }
                    
                    analysis.setTestId(event.getValue());
                    
                    if(testMan != null){
                        //set the method
                        method.setSelection(new TableDataRow(testMan.getTest().getMethodId(), testMan.getTest().getMethodName()));
                        analysis.setTestName(testMan.getTest().getName());
                        analysis.setMethodId(testMan.getTest().getMethodId());
                        analysis.setMethodName(testMan.getTest().getMethodName());
                        
                        //set isreportable
                        isReportable.setValue(testMan.getTest().getIsReportable());
                        analysis.setIsReportable(testMan.getTest().getIsReportable());
                        
                        TestSectionViewDO defaultDO = testMan.getTestSections().getDefaultSection();
                        
                        if(defaultDO != null){
                            setSectionsModel(defaultDO.getSectionId(), defaultDO.getSection());
                            sectionId.setSelection(defaultDO.getSectionId());
                        }else if(testMan.getTestSections().count() > 0){
                            setSectionsModel(testMan.getTestSections().getSections());
                            sectionId.setSelections(null);
                        }
                        
                        //test pre requirement code
                        drawTestPrepScreen();
                    }
                    
                    ActionEvent.fire(anTab, Action.CHANGED, null);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                test.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        test.addGetMatchesHandler(new GetMatchesHandler(){
           public void onGetMatches(GetMatchesEvent event) {
               AnalysisAutoCompleteRPC rpc = new AnalysisAutoCompleteRPC();
               rpc.match = event.getMatch();
               rpc.sampleItemType = sampleItem.getTypeOfSampleId();
               
               if(rpc.sampleItemType == null){
                   parentScreen.window.setError(consts.get("sampleItemTypeRequired"));
               }else{
                   try {
                       rpc = service.call("getTestMethodMatches", rpc);
                       ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                           
                       for (int i=0; i<rpc.model.size(); i++){
                           TestMethodViewDO autoDO = (TestMethodViewDO)rpc.model.get(i);
                           
                           TableDataRow row = new TableDataRow(3);
                           row.key = autoDO.getTestId();
                           row.cells.get(0).value = autoDO.getTestName();
                           row.cells.get(1).value = autoDO.getMethodName();
                           row.cells.get(2).value = autoDO.getTestDescription();
                           row.data = autoDO.getMethodId();
                           
                           model.add(row);
                       } 
                       
                       test.showAutoMatches(model);
                           
                   }catch(Exception e) {
                       Window.alert(e.getMessage());                     
                   }
               }
            } 
        });

        method = (AutoComplete)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setSelection(analysis.getMethodId(), analysis.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(analysis.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setStatusId(event.getValue());
                ActionEvent.fire(anTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox revision = (TextBox)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getRevision());
        addScreenHandler(revision, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revision.setValue(getString(analysis.getRevision()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                revision.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                revision.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isReportable = (CheckBox)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(analysis.getIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                analysis.setIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReportable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sectionId = (Dropdown<Integer>)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.SECTION.getName());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                if(analysis.getSectionId() != null)
                    setSectionsModel(analysis.getSectionId(), analysis.getSectionName());
                else
                    setSectionsModel(new ArrayList());
                
                sectionId.setSelection(analysis.getSectionId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                analysis.setSectionId(event.getValue());
                analysis.setSectionName(sectionId.getTextBoxDisplay());
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                sectionId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp startedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getStartedDate());
        addScreenHandler(startedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startedDate.setValue(analysis.getStartedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startedDate.enable(EnumSet.of(State.QUERY)
                                   .contains(event.getState()));
                startedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp completedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getCompletedDate());
        addScreenHandler(completedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                completedDate.setValue(analysis.getCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedDate.enable(EnumSet.of(State.QUERY)
                                   .contains(event.getState()));
                completedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp releasedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(analysis.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.QUERY)
                                   .contains(event.getState()));
                releasedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final CalendarLookUp printedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE_ITEM.ANALYSIS.getPrintedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                printedDate.setValue(analysis.getPrintedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                analysis.setPrintedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                printedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }
    
    private void setStatusesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        statusId.setModel(model);
    }
    
    private void setSectionsModel(Integer id, String sectionName) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        model.add(new TableDataRow(id, sectionName));
         
        sectionId.setModel(model);
    }
    
    private void setSectionsModel(ArrayList<TestSectionViewDO> sections) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        
        for(int i=0; i<sections.size(); i++){
            TestSectionViewDO sectionDO = sections.get(i);
            model.add(new TableDataRow(sectionDO.getSectionId(), sectionDO.getSection()));
        }
         
        sectionId.setModel(model);
    }
    
    private void drawTestPrepScreen(){
        if (prepPickerScreen == null) {
            try {
                prepPickerScreen = new TestPrepPickerScreen();
                /*editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                    public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                        if (event.getAction() == EditNoteScreen.Action.COMMIT) {
                            loaded = false;
                            draw();
                        }
                    }
                });*/

            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        ScreenWindow modal = new ScreenWindow(null,
                                              "Test Prep Picker Screen",
                                              "testPrepPickerScreen",
                                              "",
                                              true,
                                              false);
        modal.setName(consts.get("prepTestPicker"));
        modal.setContent(prepPickerScreen);
        try{
            prepPickerScreen.setManager(manager.getTests().getPrepTests());
        }catch(Exception e){
            Window.alert(e.getMessage());
        }
/*
        NoteDO note = null;
        
        try{
        if(isExternal)
            note = manager.getExternalEditingNote();
        else
            note = manager.getInternalEditingNote();
        }catch(Exception e ){
            e.printStackTrace();
            Window.alert("error!");
        }
        note.setSystemUser(userName);
        note.setSystemUserId(userId);
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        editNote.setNote(note);
        editNote.setScreenState(State.DEFAULT);
        */
    }
    
    public void setData(SampleDataBundle data) {
        if(data.type == SampleDataBundle.Type.SAMPLE_ITEM){
            analysis = new AnalysisViewDO();
            manager = null;
            StateChangeEvent.fire(this, State.DEFAULT);   
        }else{
            analysis = data.analysisTestDO;
            manager = data.analysisManager;
            sampleItem = data.sampleItemDO;
            
            if(state == State.ADD || state == State.UPDATE)
                StateChangeEvent.fire(this, State.UPDATE);
        }
        
        loaded = false;
    }
     
     public void draw(){
         if(!loaded)
             DataChangeEvent.fire(this);
         
         loaded = true;
     }
     
     public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
         return addHandler(handler, ActionEvent.getType());
     }
}
