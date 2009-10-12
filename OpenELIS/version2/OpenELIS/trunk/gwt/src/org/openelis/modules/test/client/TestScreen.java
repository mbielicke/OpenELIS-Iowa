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

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
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
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.MenuItem;
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
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class TestScreen extends Screen {

    public enum Tabs {
        DETAILS, SAMPLE_TYPES, ANALYTES_RESULTS, PREPS_REFLEXES,WORKSHEET
    };

    protected Tabs                   tab = Tabs.DETAILS;    
   
    private TestMetaMap              TestMeta = new TestMetaMap();     
    private TestManager              manager;    
    private SampleTypeTab            sampleTypeTab;
    private AnalyteAndResultTab      analyteAndResultTab;
    private PrepTestAndReflexTestTab prepAndReflexTab; 
    private WorksheetLayoutTab       worksheetLayoutTab;   
    private SecurityModule           security;
    
    private ScreenNavigator          nav;    
    private TableWidget              sectionTable;     
    private TextBox                  id,name,description,reportingDescription,
                                     reportingSequence,timeTaMax,timeTaAverage,
                                     timeTaWarning,timeTransit,timeHolding,labelQty;    
    private AppButton                queryButton, previousButton, nextButton, 
                                     addButton, updateButton, commitButton, 
                                     abortButton,removeSection, addSection;
    private Dropdown<Integer>        sortingMethod,reportingMethod,testFormat,revisionMethod;
    private AutoComplete<Integer>    testTrailer, scriptlet, method,label;
    private MenuItem                 duplicate,history; 
    private TabPanel                 tabs;
    private ButtonGroup              atoz;
    private CheckBox                 isActive,isReportable;
    private CalendarLookUp           activeBegin,activeEnd;
    
    
    public TestScreen() throws Exception{
        super((ScreenDefInt)GWT.create(TestDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.test.server.TestService");               
        
        security = OpenELIS.security.getModule("test");
        if (security == null)
            throw new SecurityException("screenPermException", "Test Screen");
        
        // Setup link between Screen and widget Handlers
        initialize();    
        
        manager = TestManager.getInstance();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred command.
     */
    private void postConstructor() {
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {    
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
            }
        });
        
        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                 .contains(event.getState()) &&
                          security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    security.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);

            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                    .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
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
        
        analyteAndResultTab.addActionHandler(prepAndReflexTab);
        
        worksheetLayoutTab = new WorksheetLayoutTab(def,service);        
        addScreenHandler(worksheetLayoutTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetLayoutTab.setManager(manager);

                if (tab == Tabs.WORKSHEET)
                    drawTabs(); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(worksheetLayoutTab, event.getState());
            }
        });
        
        analyteAndResultTab.addActionHandler(worksheetLayoutTab);
        
        id = (TextBox)def.getWidget(TestMeta.getId());
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
        
        name = (TextBox)def.getWidget(TestMeta.getName());
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
        
        method = (AutoComplete)def.getWidget(TestMeta.getMethod().getName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestViewDO testDO = manager.getTest();
                method.setSelection(testDO.getMethodId(),
                                               testDO.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setMethodId(event.getValue());
                manager.getTest().setMethodName(method.getTextBoxDisplay());
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
        

        description = (TextBox)def.getWidget(TestMeta.getDescription());
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
        

        reportingDescription = (TextBox)def.getWidget(TestMeta.getReportingDescription());
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
        
        timeTaMax = (TextBox)def.getWidget(TestMeta.getTimeTaMax());
        addScreenHandler(timeTaMax, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaMax.setValue(getString(manager.getTest().getTimeTaMax()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaMax(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaMax.enable(EnumSet.of(State.ADD,
                                            State.UPDATE,
                                            State.QUERY)
                                        .contains(event.getState()));
                timeTaMax.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        
        timeTaAverage = (TextBox)def.getWidget(TestMeta.getTimeTaAverage());
        addScreenHandler(timeTaAverage, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaAverage.setValue(getString(manager.getTest().getTimeTaAverage()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaAverage(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaAverage.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTaAverage.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        timeTaWarning = (TextBox)def.getWidget(TestMeta.getTimeTaWarning());
        addScreenHandler(timeTaWarning, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaWarning.setValue(getString(manager.getTest().getTimeTaWarning()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaWarning(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaWarning.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTaWarning.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        timeTransit = (TextBox)def.getWidget(TestMeta.getTimeTransit());
        addScreenHandler(timeTransit, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTransit.setValue(getString(manager.getTest().getTimeTransit()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTransit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTransit.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeTransit.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        timeHolding = (TextBox)def.getWidget(TestMeta.getTimeHolding());
        addScreenHandler(timeHolding, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeHolding.setValue(getString(manager.getTest().getTimeHolding()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeHolding(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeHolding.enable(EnumSet.of(State.ADD,
                                                State.UPDATE,
                                                State.QUERY)
                                            .contains(event.getState()));
                timeHolding.setQueryMode(event.getState() == State.QUERY);
            }
        });
                
        isActive = (CheckBox)def.getWidget(TestMeta.getIsActive());
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
        
        activeBegin = (CalendarLookUp)def.getWidget(TestMeta.getActiveBegin());
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

        activeEnd = (CalendarLookUp)def.getWidget(TestMeta.getActiveEnd());
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
        
        label = (AutoComplete)def.getWidget(TestMeta.getLabel().getName());
        addScreenHandler(label, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestViewDO testDO = manager.getTest();
              label.setSelection(testDO.getLabelId(),testDO.getLabelName());              
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setLabelId(event.getValue());
              manager.getTest().setLabelName(label.getTextBoxDisplay());
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
     
        labelQty = (TextBox)def.getWidget(TestMeta.getLabelQty());
        addScreenHandler(labelQty, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                labelQty.setValue(getString(manager.getTest().getLabelQty()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
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
                if(state != State.QUERY)
                    sectionTable.load(getTableModel());
            }
            public void onStateChange(StateChangeEvent<State> event) {
                sectionTable.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                sectionTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sectionTable.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
                int row,col,i;
                Integer val;
                String systemName;
                TestSectionManager tsm;
                TestSectionViewDO sectionDO;
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
                    manager.getTestSections().addSection(new TestSectionViewDO());
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
        
        removeSection = (AppButton)def.getWidget("removeSectionButton");
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
        
        addSection = (AppButton)def.getWidget("addSectionButton");
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
    
    
        isReportable = (CheckBox)def.getWidget(TestMeta.getIsReportable());
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
        
        revisionMethod = (Dropdown<Integer>)def.getWidget(TestMeta.getRevisionMethodId());
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
        
        sortingMethod = (Dropdown<Integer>)def.getWidget(TestMeta.getSortingMethodId());
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
        
        reportingMethod = (Dropdown<Integer>)def.getWidget(TestMeta.getReportingMethodId());
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
        
        reportingSequence = (TextBox)def.getWidget(TestMeta.getReportingSequence());
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
        
        testTrailer = (AutoComplete)def.getWidget(TestMeta.getTestTrailer()
                                                                        .getName());
        addScreenHandler(testTrailer, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestViewDO testDO = manager.getTest();
              testTrailer.setSelection(testDO.getTestTrailerId(),
                                       testDO.getTrailerName());
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setTestTrailerId(event.getValue());
              manager.getTest().setTrailerName(testTrailer.getTextBoxDisplay());
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

        testFormat = (Dropdown<Integer>)def.getWidget(TestMeta.getTestFormatId());
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
        
        scriptlet = (AutoComplete)def.getWidget(TestMeta.getScriptlet()
                                                                              .getName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
          public void onDataChange(DataChangeEvent event) {
              TestViewDO testDO = manager.getTest();
              scriptlet.setSelection(testDO.getScriptletId(),
                                     testDO.getScriptletName());
          }
        
          public void onValueChange(ValueChangeEvent<Integer> event) {
              manager.getTest().setScriptletId(event.getValue());
              manager.getTest().setScriptletName(scriptlet.getTextBoxDisplay());
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
               
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {               
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        history = (MenuItem)def.getWidget("history");
        addScreenHandler(history, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {               
                //Window.alert("clicked history");
                //history();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                history.enable(EnumSet.of(State.DISPLAY, State.UPDATE).contains(event.getState()));
            }
        });
        
        // Get TabPanel and set Tab Selection Handlers
        tabs = (TabPanel)def.getWidget("testTabPanel");
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
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<TestMethodViewDO>>() {
                    public void onSuccess(ArrayList<TestMethodViewDO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: Test call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                if(entry != null)
                    return fetchById(((TestMethodViewDO)entry).getTestId());
                else 
                    return fetchById(null);
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<TestMethodViewDO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (TestMethodViewDO entry : result)
                        model.add(new TableDataRow(entry.getTestId(), 
                                                   entry.getTestName()+","+
                                                   entry.getMethodName()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = TestMeta.getName();
                field.query = ((AppButton)event.getSource()).action;
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
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
        worksheetLayoutTab.setState(state);
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
            prepAndReflexTab.finishEditing();
            sampleTypeTab.finishEditing();
            analyteAndResultTab.draw();
        } else if (tab == Tabs.PREPS_REFLEXES) {
            analyteAndResultTab.finishEditing();
            prepAndReflexTab.draw();
        } else if (tab == Tabs.WORKSHEET) {
            analyteAndResultTab.finishEditing();
            worksheetLayoutTab.draw();
        }
    }
    
    protected void query() {
        manager = TestManager.getInstance();    
        setState(State.QUERY);
        DataChangeEvent.fire(this);       
        
        sampleTypeTab.draw();
        analyteAndResultTab.draw();
        prepAndReflexTab.draw();
        worksheetLayoutTab.draw();
        
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
        setState(State.ADD);
        DataChangeEvent.fire(this);        
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy("Locking Record for update...");

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            
            //
            // all tabs are loaded here to make sure that the on-screen validation 
            // and the validation done at the back end doesn't suffer from
            // errorneous conclusions about the data because of the lack of some
            // subset of it
            //
            sampleTypeTab.draw();
            analyteAndResultTab.draw();
            prepAndReflexTab.draw();
            worksheetLayoutTab.draw();                               
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        window.clearStatus();
    }

    protected void commit() { 
        Query query;
        //
        // set the focus to null so every field will commit its data.
        //
        name.setFocus(false);
        
        if (!validate()) {
            window.setError(consts.get("correctErrors"));                           
            return;
        }
        
        if (state == State.QUERY) {                        
            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);               
        } else  if (state == State.ADD) {                       
            if (canCommitResultGroups() && allowAnalytesEmpty()) {
                window.setBusy(consts.get("adding"));
                try {
                    manager = manager.add();
                    
                    setState(State.DISPLAY);
                    DataChangeEvent.fire(this);
                    window.setDone(consts.get("addingComplete"));
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitAdd(): " + e.getMessage());
                    window.clearStatus();
                }
            }            
        }  else if (state == State.UPDATE) {                        
            if (canCommitResultGroups() && allowAnalytesEmpty()) {
                window.setBusy(consts.get("updating"));
                try {
                    manager = manager.update();
                    setState(State.DISPLAY);
                    DataChangeEvent.fire(this);
                    window.setDone(consts.get("updatingComplete"));
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    window.clearStatus();
                }
            }            
        } 

    }

    protected void abort() {
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
       
        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {            
            try {
                manager = manager.abort();                
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);                                 
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = TestManager.getInstance();
            setState(State.DEFAULT);
        } else {        
            window.setBusy(consts.get("fetching"));
            try {
                if (tab == Tabs.DETAILS) {
                    manager = TestManager.findById(id);
                } else if (tab == Tabs.SAMPLE_TYPES) {
                    manager = TestManager.findByIdWithSampleTypes(id);
                } else if (tab == Tabs.ANALYTES_RESULTS) {
                    manager = TestManager.findByIdWithAnalytesAndResults(id);
                } else if (tab == Tabs.PREPS_REFLEXES) {
                    manager = TestManager.findByIdWithPrepTestAndReflexTests(id);
                } else if (tab == Tabs.WORKSHEET) {
                    manager = TestManager.findByIdWithWorksheet(id);
                }                
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
            setState(State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();
        
        return true;
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
                ((HasField)def.getWidget(fe.getFieldName())).addError(fe.getMessage());
            } else if (ex instanceof FormErrorException) {
                window.setError(ex.getMessage());
                return;
            }
        }
        window.setError(consts.get("correctErrors"));
    }
    
    private void duplicate() {               
        try {
            manager = TestManager.findById(manager.getTest().getId()); 
                                    
            sampleTypeTab.setManager(manager);            
            analyteAndResultTab.setManager(manager);                        
            prepAndReflexTab.setManager(manager);                        
            worksheetLayoutTab.setManager(manager);
            
            manager.getSampleTypes();
            manager.getTestAnalytes();
            manager.getTestResults();
            manager.getPrepTests();
            manager.getReflexTests();
            manager.getTestWorksheet();
            
            clearKeys();
            
            sampleTypeTab.draw();
            analyteAndResultTab.draw();            
            prepAndReflexTab.draw();
            worksheetLayoutTab.draw();   
            
            setState(State.ADD);
            DataChangeEvent.fire(this);  
            
            window.setDone(consts.get("enterInformationPressCommit"));
        }  catch (Exception e) {
            Window.alert(e.getMessage());
        }
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
        List<SectionViewDO> list = SectionCache.getSectionList();        
        model.add(new TableDataRow(null, ""));
        for (SectionViewDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getName()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
        
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestSectionViewDO sectionDO;
        TestSectionManager tsm;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        tsm = manager.getTestSections();
        for (int iter = 0; iter < tsm.count(); iter++) {
            sectionDO = tsm.getSectionAt(iter);
            
            row = new TableDataRow(2);
            row.key = sectionDO.getId();
            
            row.cells.get(0).value = sectionDO.getSectionId();
            row.cells.get(1).value = sectionDO.getFlagId();
            model.add(row);
        }

        return model;
    }

    private boolean canCommitResultGroups() {
        ArrayList<Integer> list;        
        int i,size;
        TestResultManager trm;
        boolean commit;
        
        trm = null;        
        commit = true;
        
        try {
            trm = manager.getTestResults();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        list = getEmptyResultGroups(trm); 
        size = list.size();        
        if(size > 0) {    
            commit = Window.confirm(consts.get("resultGroupsEmpty"));
            if(commit) {
                for(i = 0; i < size; i++) {
                    trm.removeResultGroup(list.get(i));
                }               
            }        
        }
        return commit;
    }
    
    private boolean allowAnalytesEmpty() {
        int anasize,ressize;
        boolean allow;
        
        anasize = 0;
        ressize = 0;
        allow = true;
        try{
            anasize = manager.getTestAnalytes().rowCount();
            ressize = manager.getTestResults().groupCount();
            
            if(anasize == 0 && ressize > 0) {
                allow = Window.confirm(consts.get("resultNoAnalytes"));
            } 
                
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return allow;
    }
    
           
    private ArrayList<Integer> getEmptyResultGroups(TestResultManager trm) {
        ArrayList<Integer> empList;        
        
        empList = new ArrayList<Integer>();
        
        if(trm != null) {
            for(int i = 0; i < trm.groupCount(); i++) {
                if(trm.getResultGroupSize(i+1) == 0)
                    empList.add(i+1);
            }
        }
        
        return empList;
    }
    
    private void clearKeys() {
        TestDO test;                
        
        test = manager.getTest();     
        test.setId(null);
        test.setIsActive("N");
        test.setIsReportable("N"); 
        test.setActiveBegin(null);
        test.setActiveEnd(null);
        
        try {
            clearSectionKeys(manager.getTestSections());            
            sampleTypeTab.clearKeys(manager.getSampleTypes());
            analyteAndResultTab.clearKeys(manager.getTestAnalytes(),manager.getTestResults());
            prepAndReflexTab.clearKeys(manager.getPrepTests(),manager.getReflexTests());    
            worksheetLayoutTab.clearKeys(manager.getTestWorksheet());            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    private void clearSectionKeys(TestSectionManager tsm) {
        TestSectionViewDO section;
        
        for(int i = 0; i < tsm.count(); i++) {
            section = tsm.getSectionAt(i);
            section.setId(null);
            section.setTestId(null);
        }
    }       
}
