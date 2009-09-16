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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestIdNameMethodNameDO;
import org.openelis.domain.TestMethodAutoDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class TestScreen extends Screen {

    public enum Tabs {
        DETAILS, SAMPLE_TYPES, ANALYTES_RESULTS, PREPS_REFLEXES,WORKSHEET
    };

    protected Tabs tab = Tabs.DETAILS;    
   
    private TestMetaMap TestMeta;     
    private TestManager manager;
    private SampleTypeTab sampleTypeTab;
    private AnalyteAndResultTab analyteAndResultTab;
    private PrepTestAndReflexTestTab prepAndReflexTab; 
  
    private SecurityModule security;
    
    EnumSet<State> enabledStates = EnumSet.of(State.ADD,State.UPDATE,State.QUERY);       
    
    ScreenNavigator nav;
    
    private TableWidget sectionTable; 
    
    public TestScreen() throws Exception{
        super("OpenELISServlet?service=org.openelis.modules.test.server.TestService");        
        manager = TestManager.getInstance();
        
        TestMeta = new TestMetaMap();
        
        security = OpenELIS.security.getModule("test");

        // Setup link between Screen and widget Handlers
        initialize();    
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);               
    }

    private void initialize() {
        
        nav = new ScreenNavigator<Query<TestIdNameMethodNameDO>>(this) {
            public void getSelection(RPC entry) {
                fetch(((TestIdNameMethodNameDO)entry).getId());
            }
            public void loadPage(Query<TestIdNameMethodNameDO> query) {
                loadQueryPage(query);
            }
        };
        
        // Create the Handler for SampleTypeTab passing in the ScreenDef
        sampleTypeTab = new SampleTypeTab(def);

        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(sampleTypeTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTypeTab.setManager(manager);

                if (tab == Tabs.SAMPLE_TYPES)
                    drawTabs(); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(sampleTypeTab, event.getState());
            }
        });
        
        analyteAndResultTab = new AnalyteAndResultTab(def,service);

        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(analyteAndResultTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analyteAndResultTab.setManager(manager);

                if (tab == Tabs.ANALYTES_RESULTS)
                    drawTabs(); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(analyteAndResultTab, event.getState());
            }
        });
        
        prepAndReflexTab = new PrepTestAndReflexTestTab(def,service);

        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(prepAndReflexTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                prepAndReflexTab.setManager(manager);

                if (tab == Tabs.PREPS_REFLEXES)
                    drawTabs(); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(prepAndReflexTab, event.getState());
            }
        });
        
        final TextBox id = (TextBox)def.getWidget(TestMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getString(manager.getTest().getId()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setId(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);

                if (event.getState() == State.QUERY)
                    id.setFocus(true);
            }
        });
        
        final TextBox name = (TextBox)def.getWidget(TestMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getTest().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);

                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    name.setFocus(true);
            }
        });
        
        final AutoComplete<Integer> method = (AutoComplete)def.getWidget(TestMeta.getMethod()
                                                                                   .getName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestDO testDO = manager.getTest();
                method.setSelection(testDO.getMethodId(),
                                               testDO.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest()
                       .setMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(EnumSet.of(State.ADD,
                                            State.UPDATE,
                                            State.QUERY)
                                        .contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });           

        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface       
        method.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameDO autoDO;
                
                trpc = new AutocompleteRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getMethodMatches",trpc);
                    model = new ArrayList<TableDataRow>();                    
                    for(int i = 0; i < trpc.model.size(); i++) {
                        autoDO = (IdNameDO)trpc.model.get(i);
                        row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        model.add(row);
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });
        

        final TextBox description = (TextBox)def.getWidget(TestMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getTest().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
               
            }
        });
        

        final TextBox reportingDescription = (TextBox)def.getWidget(TestMeta.getReportingDescription());
        addScreenHandler(reportingDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportingDescription.setValue(manager.getTest().getReportingDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setReportingDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportingDescription.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                reportingDescription.setQueryMode(event.getState() == State.QUERY);
                
            }
        });
        
        final TextBox timeTaMax = (TextBox)def.getWidget(TestMeta.getTimeTaMax());
        addScreenHandler(timeTaMax, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaMax.setValue(getString(manager.getTest().getTimeTaMax()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setTimeTaMax(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaMax.enable(EnumSet.of(State.ADD,
                                            State.UPDATE,
                                            State.QUERY)
                                        .contains(event.getState()));
                timeTaMax.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        
        final TextBox timeTaAverage = (TextBox)def.getWidget(TestMeta.getTimeTaAverage());
        addScreenHandler(timeTaAverage, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaAverage.setValue(getString(manager.getTest().getTimeTaAverage()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setTimeTaAverage(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaAverage.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTaAverage.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final TextBox timeTaWarning = (TextBox)def.getWidget(TestMeta.getTimeTaWarning());
        addScreenHandler(timeTaWarning, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaWarning.setValue(getString(manager.getTest().getTimeTaWarning()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setTimeTaWarning(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaWarning.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTaWarning.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final TextBox timeTransit = (TextBox)def.getWidget(TestMeta.getTimeTransit());
        addScreenHandler(timeTransit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                timeTransit.setValue(getString(manager.getTest().getTimeTransit()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setTimeTransit(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTransit.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTransit.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final TextBox timeHolding = (TextBox)def.getWidget(TestMeta.getTimeHolding());
        addScreenHandler(timeHolding, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                timeHolding.setValue(getString(manager.getTest().getTimeHolding()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setTimeHolding(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeHolding.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeHolding.setQueryMode(event.getState() == State.QUERY);
            }
        });
                
        final CheckBox isActive = (CheckBox)def.getWidget(TestMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getTest()
                                         .getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final CalendarLookUp activeBegin = (CalendarLookUp)def.getWidget(TestMeta.getActiveBegin());
        addScreenHandler(activeBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(manager.getTest().getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if(event.getValue() != null)
                    manager.getTest().setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeBegin.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                activeBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final CalendarLookUp activeEnd = (CalendarLookUp)def.getWidget(TestMeta.getActiveEnd());
        addScreenHandler(activeEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(manager.getTest().getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if(event.getValue() != null)
                    manager.getTest().setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeEnd.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                activeEnd.setQueryMode(event.getState() == State.QUERY);                                                                                                             
            }
        });        
        
        final AutoComplete<Integer> label = (AutoComplete)def.getWidget(TestMeta.getLabel()
                                                                        .getName());
        addScreenHandler(label, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestDO testDO = manager.getTest();
              label.setSelection(testDO.getLabelId(),testDO.getLabelName());              
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setLabelId(event.getValue());
          }
        
          public void onStateChange(StateChangeEvent<State> event) {
              label.enable(EnumSet.of(State.ADD,
                                          State.UPDATE,
                                          State.QUERY)
                                      .contains(event.getState()));
              label.setQueryMode(event.getState() == State.QUERY);
          }
        });           
        
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
        label.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameDO autoDO;
                
                trpc = new AutocompleteRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getLabelMatches",trpc);
                    model = new ArrayList<TableDataRow>();                    
                    for(int i = 0; i < trpc.model.size(); i++) {
                        autoDO = (IdNameDO)trpc.model.get(i);
                        row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        model.add(row);
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });        
     
        final TextBox labelQty = (TextBox)def.getWidget(TestMeta.getLabelQty());
        addScreenHandler(labelQty, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                labelQty.setValue(getString(manager.getTest().getLabelQty()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setLabelQty(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                labelQty.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                labelQty.setQueryMode(event.getState() == State.QUERY);
            }
        });        
        
        sectionTable = (TableWidget)def.getWidget("sectionTable");
        addScreenHandler(sectionTable,new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sectionTable.load(getTableModel());
            }
            public void onStateChange(StateChangeEvent<State> event) {
                sectionTable.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
            }
        });
        
        sectionTable.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
                int row,col,i;
                Integer val;
                String systemName;
                TestSectionManager tsm;
                TestSectionDO sectionDO;
                TableDataRow tableRow;
                
                row = event.getRow();
                col = event.getCell();
                tableRow = sectionTable.getRow(row);
                tsm = manager.getTestSections();
                
                try{
                    sectionDO = tsm.getSectionAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                val = (Integer)tableRow.cells.get(col).value;
                
                switch (col){
                    case 0:
                            sectionDO.setSectionId(val);                            
                            break;
                    case 1:
                            sectionDO.setFlagId(val);
                            if(val == null)
                                break;
                            systemName = DictionaryCache.getSystemNameFromId(val);
                            if(systemName != null) {
                                if ("test_section_default".equals(systemName)) {
                                    for(i = 0; i < tsm.count(); i++) {
                                        if(i == row) 
                                            continue;
                                        sectionDO = tsm.getSectionAt(i);
                                        sectionDO.setFlagId(null);    
                                        sectionTable.getData().get(i).cells.get(col).setValue(null);
                                    }
                                } else {
                                    for(i = 0; i < tsm.count(); i++) {
                                        sectionDO = tsm.getSectionAt(i);
                                        sectionDO.setFlagId(val);     
                                        sectionTable.getData().get(i).cells.get(col).setValue(val);
                                    }
                                }      
                                sectionTable.refresh();
                            }
                            break;
                }
            }
        });
        
        sectionTable.addRowAddedHandler(new RowAddedHandler(){
            public void onRowAdded(RowAddedEvent event) {
                try{
                    manager.getTestSections().addSection(new TestSectionDO());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
                
            }
        });
        
        sectionTable.addRowDeletedHandler(new RowDeletedHandler(){
            public void onRowDeleted(RowDeletedEvent event) {
                try{
                    manager.getTestSections().removeSectionAt(event.getIndex());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
                
            }
        }); 
        
        final AppButton removeSection = (AppButton)def.getWidget("removeSectionButton");
        addScreenHandler(removeSection,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sectionTable.getSelectedIndex();
                if (selectedRow > -1 && sectionTable.numRows() > 0) {
                    sectionTable.deleteRow(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeSection.enable(true);
                else
                    removeSection.enable(false);
            }
            
        });
        
        final AppButton addSection = (AppButton)def.getWidget("addSectionButton");
        addScreenHandler(addSection,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sectionTable.addRow();
                sectionTable.selectRow(sectionTable.numRows()-1);
                sectionTable.scrollToSelection();
                sectionTable.startEditing(sectionTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addSection.enable(true);
                else
                    addSection.enable(false);
            }
            
        });
    
    
        final CheckBox isReportable = (CheckBox)def.getWidget(TestMeta.getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(manager.getTest()
                                         .getIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReportable.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final Dropdown<Integer> revisionMethod = 
            (Dropdown<Integer>)def.getWidget(TestMeta.getRevisionMethodId());
        addScreenHandler(revisionMethod, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                revisionMethod.setSelection(manager.getTest().getRevisionMethodId());
            }
        
             public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setRevisionMethodId(event.getValue());
            }
        
             public void onStateChange(StateChangeEvent<State> event) {
                 revisionMethod.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                 revisionMethod.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final Dropdown<Integer> sortingMethod = 
            (Dropdown<Integer>)def.getWidget(TestMeta.getSortingMethodId());
        addScreenHandler(sortingMethod, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sortingMethod.setSelection(manager.getTest().getSortingMethodId());
            }
        
             public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setSortingMethodId(event.getValue());
            }
        
             public void onStateChange(StateChangeEvent<State> event) {
                 sortingMethod.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                 sortingMethod.setQueryMode(event.getState() == State.QUERY);
            }
        });  
        
        final Dropdown<Integer> reportingMethod = 
            (Dropdown<Integer>)def.getWidget(TestMeta.getReportingMethodId());
        addScreenHandler(reportingMethod, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                reportingMethod.setSelection(manager.getTest().getReportingMethodId());
            }
        
             public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setReportingMethodId(event.getValue());
            }
        
             public void onStateChange(StateChangeEvent<State> event) {
                 reportingMethod.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                 reportingMethod.setQueryMode(event.getState() == State.QUERY);
            }
        });      
        
        final TextBox reportingSequence = (TextBox)def.getWidget(TestMeta.getReportingSequence());
        addScreenHandler(reportingSequence, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportingSequence.setValue(getString(manager.getTest().getReportingSequence()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setReportingSequence(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportingSequence.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                reportingSequence.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final AutoComplete<Integer> testTrailer = (AutoComplete)def.getWidget(TestMeta.getTestTrailer()
                                                                        .getName());
        addScreenHandler(testTrailer, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestDO testDO = manager.getTest();
              testTrailer.setSelection(testDO.getTestTrailerId(),
                                       testDO.getTestTrailerName());
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setTestTrailerId(event.getValue());
          }
        
          public void onStateChange(StateChangeEvent<State> event) {
              testTrailer.enable(EnumSet.of(State.ADD,
                                          State.UPDATE,
                                          State.QUERY)
                                      .contains(event.getState()));
              testTrailer.setQueryMode(event.getState() == State.QUERY);
          }
        });           
        
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
        testTrailer.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameDO autoDO;
                
                trpc = new AutocompleteRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getTrailerMatches",trpc);
                    model = new ArrayList<TableDataRow>();                    
                    for(int i = 0; i < trpc.model.size(); i++) {
                        autoDO = (IdNameDO)trpc.model.get(i);
                        row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        model.add(row);
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });

        final Dropdown<Integer> testFormat = 
            (Dropdown<Integer>)def.getWidget(TestMeta.getTestFormatId());
        addScreenHandler(testFormat, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testFormat.setSelection(manager.getTest().getTestFormatId());
            }
        
             public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTestFormatId(event.getValue());
            }
        
             public void onStateChange(StateChangeEvent<State> event) {
                 testFormat.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                 testFormat.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final AutoComplete<Integer> scriptlet = (AutoComplete)def.getWidget(TestMeta.getScriptlet()
                                                                              .getName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestDO testDO = manager.getTest();
              scriptlet.setSelection(testDO.getScriptletId(),
                                     testDO.getScriptletName());
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setScriptletId(event.getValue());
          }
         
          public void onStateChange(StateChangeEvent<State> event) {
              scriptlet.enable(EnumSet.of(State.ADD,
                                          State.UPDATE,
                                          State.QUERY)
                                      .contains(event.getState()));
              scriptlet.setQueryMode(event.getState() == State.QUERY);
          }
        });           
          
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameDO autoDO;
                
                trpc = new AutocompleteRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getScriptletMatches",trpc);
                    model = new ArrayList<TableDataRow>();
                    for(int i = 0; i < trpc.model.size(); i++) {
                        autoDO = (IdNameDO)trpc.model.get(i);
                        row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        model.add(row);
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });        
        
        final AppButton queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        final AppButton addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()))
                    addButton.enable(true);
                else if (EnumSet.of(State.ADD).contains(event.getState()))
                    addButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    addButton.enable(false);
            }
        });

        final AppButton updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });

        final AppButton nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        // Get AZ buttons and setup Screen listeners and call to for query
        final ButtonGroup azButtons = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                String baction = ((AppButton)event.getSource()).action;
                getTests(baction.substring(6, baction.length()));
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                                     security.hasSelectPermission())
                    azButtons.unlock();
                else
                    azButtons.lock();
            }

        });
        
        // Get TabPanel and set Tab Selection Handlers
        final TabPanel tabs = (TabPanel)def.getWidget("testTabPanel");
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tabIndex = event.getSelectedItem().intValue();
                if (tabIndex == Tabs.DETAILS.ordinal())
                    tab = Tabs.DETAILS;
                else if (tabIndex == Tabs.SAMPLE_TYPES.ordinal())
                    tab = Tabs.SAMPLE_TYPES;
                else if (tabIndex == Tabs.ANALYTES_RESULTS.ordinal())
                    tab = Tabs.ANALYTES_RESULTS;
                else if (tabIndex == Tabs.PREPS_REFLEXES.ordinal())
                    tab = Tabs.PREPS_REFLEXES;
                else if (tabIndex == Tabs.WORKSHEET.ordinal())
                    tab = Tabs.WORKSHEET;

                window.setBusy("Loading...");
                drawTabs();
                window.clearStatus();
            }
        });
        //
        // this is done to remove an unwanted tab that gets added to
        // testTabPanel, for some reason, when you put a tab panel inside one
        // of its tabs
        //
        //tabs.remove(3);        
        //resultTabPanel  = (ScrollableTabBar)def.getWidget("resultTabPanel");
        //resultTabPanel.remove(0);
        
        setRevisionMethodsModel();
        setSortingMethodsModel();
        setReportingMethodsModel();
        setTestFormatsModel();
        setTestSectionsModel();
        setTestSectionFlagsModel();
    }
    
    public void setState(Screen.State state){
        super.setState(state);
        sampleTypeTab.setState(state);
        analyteAndResultTab.setState(state);
        prepAndReflexTab.setState(state);
    }
    
    public HandlerRegistration addBeforeGetMatchesHandler(BeforeGetMatchesHandler handler) {
        return addHandler(handler, BeforeGetMatchesEvent.getType());
    }
    
    public HandlerRegistration addGetMatchesHandler(GetMatchesHandler handler) {
        return addHandler(handler,GetMatchesEvent.getType());
    }
    
    
    public String getString(Object obj) {
        if (obj == null)
            return "";

        return obj.toString();
    }
    
    private void drawTabs() { 
        if (tab == Tabs.SAMPLE_TYPES) {
            sampleTypeTab.draw();
        } else if (tab == Tabs.ANALYTES_RESULTS) {
            analyteAndResultTab.draw();
        } else if (tab == Tabs.PREPS_REFLEXES) {
            prepAndReflexTab.draw();
        } else if (tab == Tabs.WORKSHEET) {
            
        }
    }
    
    protected void query() {
        manager = TestManager.getInstance();
        setState(Screen.State.QUERY);
        DataChangeEvent.fire(this);        
        window.setDone(consts.get("enterFieldsToQuery"));
    }
    
    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        TestDO test;
        manager = TestManager.getInstance(); 
        test = manager.getTest();        
        test.setIsActive("N");
        test.setIsReportable("N");    
        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);        
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy("Locking Record for update...");

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            window.clearStatus();
            

        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    public void commit() {
        if (state == State.UPDATE) {
            commitUpdate();
            /*if (validate()) {
                window.setBusy(consts.get("updating"));
                commitUpdate();
            } else {
                window.setError(consts.get("correctErrors"));
            }*/
        }
        if (state == State.ADD) {
            commitAdd();
            /*if (validate()) {
                window.setBusy(consts.get("adding"));
                commitAdd();
            } else {
                window.setError(consts.get("correctErrors"));
            }*/
        }
        if (state == State.QUERY) {
            if (validate()) {
                ArrayList<QueryData> qFields = getQueryFields();
                commitQuery(qFields);
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
    }

    public void abort() {
        if (state == State.UPDATE) {
            window.setBusy("Canceling changes ...");

            try {
                manager = manager.abort();
                clearErrors();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);                                               
                window.clearStatus();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else if (state == State.ADD) {
            manager = TestManager.getInstance();
            clearErrors();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);            
            window.setDone(consts.get("addAborted"));
        } else if (state == State.QUERY) {
            manager = TestManager.getInstance();
            clearErrors();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        }
    }

    protected void fetch(Integer id) {
        window.setBusy("Fetching ...");        
        try {
            if(tab == Tabs.DETAILS) {
               manager = TestManager.findById(id);
            } else if (tab == Tabs.SAMPLE_TYPES) {
                manager = TestManager.findByIdWithSampleTypes(id);                
            } else if (tab == Tabs.ANALYTES_RESULTS) {
                manager = TestManager.findByIdWithAnalytesAndResults(id);
            } else if (tab == Tabs.PREPS_REFLEXES) {
                manager = TestManager.findByIdWithPrepTestAndReflexTests(id);
            } else if (tab == Tabs.WORKSHEET) {
                
            }

        } catch (Exception e) {
            setState(Screen.State.DEFAULT);
            Window.alert(consts.get("fetchFailed") + e.getMessage());
            window.clearStatus();

            return;
        }

        setState(Screen.State.DISPLAY);
        DataChangeEvent.fire(this);
        window.clearStatus();
    }
    
    protected boolean validate() {
        return super.validate();
    }
    
    private void getTests(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryData qField = new QueryData();
            qField.key = TestMeta.getName();
            qField.query = query;
            qField.type = QueryData.Type.STRING;
            commitQuery(qField);
        }
    }
    
    public void commitQuery(QueryData qField) {
        ArrayList<QueryData> qList = new ArrayList<QueryData>();
        qList.add(qField);
        commitQuery(qList);
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list = new ArrayList<QueryData>();
        Set<String> keys = def.getWidgets().keySet();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }
        return list;
    }

    public void commitAdd() {
        window.setBusy("Committing ....");
        try {
            manager = manager.add();
            setState(Screen.State.DISPLAY);
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitAdd(): " + e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
    }

    public void commitUpdate() {
        window.setBusy("Committing ....");
        try {
            manager = manager.update();
            setState(Screen.State.DISPLAY);
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitUpdate(): " + e.getMessage());
            e.printStackTrace();
            window.clearStatus();

        }
    }
    
    public void commitQuery(ArrayList<QueryData> qFields) {
        Query<TestIdNameMethodNameDO> query = new Query<TestIdNameMethodNameDO>();
        query.fields = qFields;
        window.setBusy("Querying...");

        service.call("query", query, new AsyncCallback<Query<TestIdNameMethodNameDO>>() {
            public void onSuccess(Query<TestIdNameMethodNameDO> query) {
                loadQuery(query);
            }

            public void onFailure(Throwable caught) {
                if (caught instanceof ValidationErrorsList)
                    showErrors((ValidationErrorsList)caught);
                else
                    Window.alert(caught.getMessage());
            }
        });
    }
    
    protected void showErrors(ValidationErrorsList errors) {
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {                               
                if(ex instanceof GridFieldErrorException) {
                    GridFieldErrorException gtfe = (GridFieldErrorException)ex;
                    if ("analyteTable".equals(gtfe.getTableKey())) {
                        analyteAndResultTab.showTestAnalyteError(gtfe);
                    } else if ("resultTable".equals(gtfe.getTableKey())) {
                        analyteAndResultTab.showTestResultError(gtfe);
                    }
                } else {
                    TableFieldErrorException tfe = (TableFieldErrorException)ex;
                    ((TableWidget)def.getWidget(tfe.getTableKey())).setCellError(tfe.getRowIndex(),
                                                                                 tfe.getFieldName(),
                                                                                 tfe.getMessage());
                }
            } else if(ex instanceof FieldErrorException){
                FieldErrorException fe = (FieldErrorException)ex;
                ((HasField)def.getWidget(fe.getFieldName())).addError(consts.get(fe.getMessage()));
            } else if (ex instanceof FormErrorException) {
                window.setError(consts.get(ex.getMessage()));
                return;
            }
        }
        window.setError(consts.get("correctErrors"));
    }

    public void loadQuery(Query<TestIdNameMethodNameDO> query) {
        manager = TestManager.getInstance();
        DataChangeEvent.fire(this);

        loadQueryPage(query);
    }

    private void loadQueryPage(Query<TestIdNameMethodNameDO> query) {
        window.setDone(consts.get("queryingComplete"));
        if (query.results == null || query.results.size() == 0) {
            window.setDone("No records found");
        } else
            window.setDone(consts.get("queryingComplete"));
        query.model = new ArrayList<TableDataRow>();
        for (TestIdNameMethodNameDO entry : query.results) {
            query.model.add(new TableDataRow(entry.getId(), entry.getTestName()+","+entry.getMethodName()));
        }
        nav.setQuery(query);
        //ActionEvent.fire(this, Action.NEW_PAGE, query);
    }
    
    private void setTestFormatsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_format");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)def.getWidget(TestMeta.getTestFormatId())).setModel(model);
        
    }

    private void setReportingMethodsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_reporting_method");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)def.getWidget(TestMeta.getReportingMethodId())).setModel(model);
        
    }

    private void setSortingMethodsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_sorting_method");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)def.getWidget(TestMeta.getSortingMethodId())).setModel(model);
        
    }

    private void setRevisionMethodsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_revision_method");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)def.getWidget(TestMeta.getRevisionMethodId())).setModel(model);
        
    }
    
    private void setTestSectionFlagsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  sectionTable.columns.get(1);
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_section_flags");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
        
    }

    private void setTestSectionsModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  sectionTable.columns.get(0);
        List<SectionDO> list = SectionCache.getSectionList();        
        model.add(new TableDataRow(null, ""));
        for (SectionDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getName()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
        
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestSectionDO sectionDO;
        TestSectionManager tsm;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        tsm = manager.getTestSections();
        try {
            for (int iter = 0; iter < tsm.count(); iter++) {
                sectionDO = tsm.getSectionAt(iter);

                row = new TableDataRow(2);
                row.key = sectionDO.getId();

                row.cells.get(0).value = sectionDO.getSectionId();
                row.cells.get(1).value = sectionDO.getFlagId();
                model.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }


}
