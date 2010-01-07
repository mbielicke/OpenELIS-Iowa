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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
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
import org.openelis.meta.TestMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class TestScreen extends Screen {    
    
    private TestManager              manager;
    private TestMeta                 meta = new TestMeta();
    protected Tabs                   tab;
    private SampleTypeTab            sampleTypeTab;
    private AnalyteAndResultTab      analyteAndResultTab;
    private PrepTestAndReflexTestTab prepAndReflexTab;
    private WorksheetLayoutTab       worksheetLayoutTab;
    private SecurityModule           security;

    private ScreenNavigator          nav;
    private TableWidget              table;
    private TextBox                  id, name, description, reportingDescription,
                                     reportingSequence, timeTaMax, timeTaAverage, timeTaWarning, timeTransit,
                                     timeHolding, labelQty;
    private AppButton                queryButton, previousButton, nextButton, addButton,
                                     updateButton, commitButton, abortButton,
                                     removeSectionButton, addSectionButton;
    private Dropdown<Integer>        sortingMethod, reportingMethod, testFormat, revisionMethod;
    private AutoComplete<Integer>    testTrailer, scriptlet, method, label;
    private MenuItem                 duplicate, history;
    private TabPanel                 testTabPanel;
    private ButtonGroup              atoz;
    private CheckBox                 isActive, isReportable;
    private CalendarLookUp           activeBegin, activeEnd;
    
    private ScreenService            methodService,scriptletService,trailerService,
                                     labelService,analyteService,qcService,dictionaryService;
    
    private enum Tabs {
        DETAILS, SAMPLE_TYPES, ANALYTES_RESULTS, PREPS_REFLEXES, WORKSHEET
    };

    public TestScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        scriptletService = new ScreenService("controller?service=org.openelis.modules.scriptlet.server.ScriptletService");
        methodService = new ScreenService("controller?service=org.openelis.modules.method.server.MethodService"); 
        trailerService = new ScreenService("controller?service=org.openelis.modules.testTrailer.server.TestTrailerService"); 
        labelService = new ScreenService("controller?service=org.openelis.modules.label.server.LabelService"); 
        analyteService = new ScreenService("controller?service=org.openelis.modules.analyte.server.AnalyteService");
        qcService = new ScreenService("controller?service=org.openelis.modules.qc.server.QcService");
        dictionaryService = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");
        
        security = OpenELIS.security.getModule("test");
        if (security == null)
            throw new SecurityException("screenPermException", "Test Screen");        

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        tab = Tabs.DETAILS;
        manager = TestManager.getInstance();
        
        try{
            DictionaryCache.preloadByCategorySystemNames("test_format", "test_reporting_method", "test_sorting_method", 
                                                         "test_revision_method", "test_section_flags", "type_of_sample",
                                                         "unit_of_measure","test_analyte_type","test_result_type",
                                                         "test_result_flags","rounding_method","test_reflex_flags",
                                                         "test_res_type_dictionary","test_worksheet_analyte_flags",
                                                         "test_worksheet_item_type","test_worksheet_format");
        } catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();        
        setState(State.DEFAULT);       
        initializeDropdowns();
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
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
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

        id = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(id, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getTest().getId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setId(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        name = (TextBox)def.getWidget(meta.getName());
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

        method = (AutoComplete)def.getWidget(meta.getMethodName());
        addScreenHandler(method, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                method.setSelection(manager.getTest().getMethodId(),
                                    manager.getTest().getMethodName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setMethodId(event.getValue());
                manager.getTest().setMethodName(method.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                     .contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });

        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        method.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<MethodDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                                
                try {
                    list = methodService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    
                    for (MethodDO data : list)
                        model.add(new TableDataRow(data.getId(),data.getName()));                    
                    method.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

        });

        description = (TextBox)def.getWidget(meta.getDescription());
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

        reportingDescription = (TextBox)def.getWidget(meta.getReportingDescription());
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

        timeTaMax = (TextBox)def.getWidget(meta.getTimeTaMax());
        addScreenHandler(timeTaMax, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaMax.setValue(getString(manager.getTest().getTimeTaMax()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaMax(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaMax.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                timeTaMax.setQueryMode(event.getState() == State.QUERY);
            }
        });

        timeTaAverage = (TextBox)def.getWidget(meta.getTimeTaAverage());
        addScreenHandler(timeTaAverage, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaAverage.setValue(getString(manager.getTest().getTimeTaAverage()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaAverage(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaAverage.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                timeTaAverage.setQueryMode(event.getState() == State.QUERY);
            }
        });

        timeTaWarning = (TextBox)def.getWidget(meta.getTimeTaWarning());
        addScreenHandler(timeTaWarning, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTaWarning.setValue(getString(manager.getTest().getTimeTaWarning()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTaWarning(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTaWarning.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                timeTaWarning.setQueryMode(event.getState() == State.QUERY);
            }
        });

        timeTransit = (TextBox)def.getWidget(meta.getTimeTransit());
        addScreenHandler(timeTransit, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeTransit.setValue(getString(manager.getTest().getTimeTransit()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeTransit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeTransit.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                timeTransit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        timeHolding = (TextBox)def.getWidget(meta.getTimeHolding());
        addScreenHandler(timeHolding, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                timeHolding.setValue(getString(manager.getTest().getTimeHolding()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTimeHolding(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                timeHolding.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                timeHolding.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(meta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getTest().getIsActive());
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

        activeBegin = (CalendarLookUp)def.getWidget(meta.getActiveBegin());
        addScreenHandler(activeBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(manager.getTest().getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if (event.getValue() != null)
                    manager.getTest().setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeBegin.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                activeBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activeEnd = (CalendarLookUp)def.getWidget(meta.getActiveEnd());
        addScreenHandler(activeEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(manager.getTest().getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if (event.getValue() != null)
                    manager.getTest().setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeEnd.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                activeEnd.setQueryMode(event.getState() == State.QUERY);
            }
        });

        label = (AutoComplete)def.getWidget(meta.getLabelName());
        addScreenHandler(label, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                label.setSelection(manager.getTest().getLabelId(),
                                   manager.getTest().getLabelName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setLabelId(event.getValue());
                manager.getTest().setLabelName(label.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                label.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                label.setQueryMode(event.getState() == State.QUERY);
            }
        });

        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        label.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = labelService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    
                    for (IdNameVO data: list)                         
                        model.add(new TableDataRow(data.getId(), data.getName()));                    
                    label.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }               
            }

        });

        labelQty = (TextBox)def.getWidget(meta.getLabelQty());
        addScreenHandler(labelQty, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                labelQty.setValue(getString(manager.getTest().getLabelQty()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setLabelQty(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                labelQty.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                labelQty.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table = (TableWidget)def.getWidget("sectionTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, i;
                Integer val;
                String systemName;
                TestSectionViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = (Integer)table.getObject(r, c);

                try {
                    data = manager.getTestSections().getSectionAt(r);

                    switch (c) {
                        case 0:
                            data.setSectionId(val);
                            break;
                        case 1:
                            data.setFlagId(val);
                            if (val == null)
                                break;
                            systemName = DictionaryCache.getSystemNameFromId(val);
                            if (systemName != null) {
                                if ("test_section_default".equals(systemName)) {
                                    for (i = 0; i < manager.getTestSections().count(); i++ ) {
                                        if (i == r)
                                            continue;
                                        data = manager.getTestSections().getSectionAt(i);
                                        data.setFlagId(null);
                                        table.setCell(i,c,null);
                                    }
                                } else {
                                    for (i = 0; i < manager.getTestSections().count(); i++ ) {
                                        data = manager.getTestSections().getSectionAt(i);
                                        data.setFlagId(val);
                                        table.setCell(i,c,val);
                                    }
                                }
                            }
                            break;
                    }

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getTestSections().addSection(new TestSectionViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTestSections().removeSectionAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });
        
        addSectionButton = (AppButton)def.getWidget("addSectionButton");
        addScreenHandler(addSectionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                table.addRow();
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addSectionButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }

        });
        
        removeSectionButton = (AppButton)def.getWidget("removeSectionButton");
        addScreenHandler(removeSectionButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSectionButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }

        });


        isReportable = (CheckBox)def.getWidget(meta.getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReportable.setValue(manager.getTest().getIsReportable());
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

        revisionMethod = (Dropdown<Integer>)def.getWidget(meta.getRevisionMethodId());
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

        sortingMethod = (Dropdown<Integer>)def.getWidget(meta.getSortingMethodId());
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

        reportingMethod = (Dropdown<Integer>)def.getWidget(meta.getReportingMethodId());
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

        reportingSequence = (TextBox)def.getWidget(meta.getReportingSequence());
        addScreenHandler(reportingSequence, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportingSequence.setValue(getString(manager.getTest().getReportingSequence()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getTest().setReportingSequence(Integer.valueOf(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportingSequence.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                                .contains(event.getState()));
                reportingSequence.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testTrailer = (AutoComplete)def.getWidget(meta.getTestTrailerName());
        addScreenHandler(testTrailer, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestViewDO testDO = manager.getTest();
                testTrailer.setSelection(testDO.getTestTrailerId(), testDO.getTrailerName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setTestTrailerId(event.getValue());
                manager.getTest().setTrailerName(testTrailer.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testTrailer.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                          .contains(event.getState()));
                testTrailer.setQueryMode(event.getState() == State.QUERY);
            }
        });

        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        testTrailer.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = trailerService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO data : list)
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    
                    testTrailer.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                
            }

        });

        testFormat = (Dropdown<Integer>)def.getWidget(meta.getTestFormatId());
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

        scriptlet = (AutoComplete)def.getWidget(meta.getScriptletName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {                
                scriptlet.setSelection(manager.getTest().getScriptletId(),
                                       manager.getTest().getScriptletName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getTest().setScriptletId(event.getValue());
                manager.getTest().setScriptletName(scriptlet.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });

        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                
                try {
                    list = scriptletService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO data : list) {                       
                        model.add(new TableDataRow(data.getId(),data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
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
                // Window.alert("clicked history");
                // history();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                history.enable(EnumSet.of(State.DISPLAY, State.UPDATE).contains(event.getState()));
            }
        });

        // Get TabPanel and set Tab Selection Handlers
        testTabPanel = (TabPanel)def.getWidget("testTabPanel");
        testTabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        // Create the Handler for SampleTypeTab passing in the ScreenDef
        sampleTypeTab = new SampleTypeTab(def,window);

        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(sampleTypeTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTypeTab.setManager(manager);
                if (tab == Tabs.SAMPLE_TYPES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTypeTab.setState(event.getState());
            }
        });

        analyteAndResultTab = new AnalyteAndResultTab(def,window,service,scriptletService,analyteService,dictionaryService);
        sampleTypeTab.addActionHandler(analyteAndResultTab);

        addScreenHandler(analyteAndResultTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analyteAndResultTab.setManager(manager);
                if (tab == Tabs.ANALYTES_RESULTS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteAndResultTab.setState(event.getState());
            }
        });

        prepAndReflexTab = new PrepTestAndReflexTestTab(def,window,service);
        addScreenHandler(prepAndReflexTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                prepAndReflexTab.setManager(manager);
                if (tab == Tabs.PREPS_REFLEXES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepAndReflexTab.setState(event.getState());
            }
        });

        analyteAndResultTab.addActionHandler(prepAndReflexTab);

        worksheetLayoutTab = new WorksheetLayoutTab(def,window,service,scriptletService,qcService);
        addScreenHandler(worksheetLayoutTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetLayoutTab.setManager(manager);
                if (tab == Tabs.WORKSHEET)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetLayoutTab.setState(event.getState());
            }
        });

        analyteAndResultTab.addActionHandler(worksheetLayoutTab);

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<TestMethodVO>>() {
                    public void onSuccess(ArrayList<TestMethodVO> result) {
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
                return fetchById((entry == null)?null:((TestMethodVO)entry).getTestId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<TestMethodVO> list;
                ArrayList<TableDataRow> model;

                list = nav.getQueryResult();
                model = null;
                if (list != null) {
                    model = new ArrayList<TableDataRow>();
                    for (TestMethodVO entry : list)
                        model.add(new TableDataRow(entry.getTestId(), entry.getTestName() + "," +
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
                field.key = meta.getName();
                field.query = ((AppButton)event.getSource()).action;
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
    }

    public void setState(Screen.State state) {
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
        return addHandler(handler, GetMatchesEvent.getType());
    }       

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        List<SectionDO> sectList;

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_format");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        testFormat.setModel(model);

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_reporting_method");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        reportingMethod.setModel(model);

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_sorting_method");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        sortingMethod.setModel(model);

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_revision_method");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        revisionMethod.setModel(model);

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_section_flags");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)table.getColumnWidget(meta.getSectionFlagId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        sectList = SectionCache.getSectionList();
        model.add(new TableDataRow(null, ""));
        for (SectionDO resultDO : sectList) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getName()));
        }
        ((Dropdown<Integer>)table.getColumnWidget(meta.getSectionSectionId())).setModel(model);
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = TestManager.getInstance();
        
        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        sampleTypeTab.draw();
        analyteAndResultTab.draw();
        prepAndReflexTab.draw();
        worksheetLayoutTab.draw();

        setFocus(id);
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
        setFocus(name);
        
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);

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
        setFocus(null);

        //
        // We do this here so that if due to changes in the data of the test analyte 
        // and test result tables some errors need to be added to the test reflex and 
        // test worksheet analyte tables then that will hapen before validate() executes
        // and so it will be able to find those errors and prevent the data from being
        // committed. We have to do this here even though validate() calls checkValue
        // (which in turns calls finishEditing on that table) on each table, because
        // it can be the case that checkValue on the tables to which the errors are
        // to be added could get executed before it gets executed for the test analyte
        // and test result tables.       
        //
        analyteAndResultTab.finishEditing();
        
        if (!validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;
            
            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
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
        } else if (state == State.UPDATE) {
            if (canCommitResultGroups() && allowAnalytesEmpty()) {
                window.setBusy(consts.get("updating"));
                try {
                    manager.update();
                    
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
        setFocus(null);
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
                manager = manager.abortUpdate();
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
                switch (tab) {                                   
                    case DETAILS:
                        manager = TestManager.fetchById(id);
                        break;
                    case SAMPLE_TYPES: 
                        manager = TestManager.fetchWithSampleTypes(id);
                        break;
                    case ANALYTES_RESULTS: 
                        manager = TestManager.fetchWithAnalytesAndResults(id);
                        break;
                    case PREPS_REFLEXES:
                        manager = TestManager.fetchWithPrepTestAndReflexTests(id);
                        break;
                    case WORKSHEET:
                        manager = TestManager.fetchWithWorksheet(id);
                        break;                
                }
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
            
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private void drawTabs() {
        switch (tab) {
            case SAMPLE_TYPES: 
                sampleTypeTab.draw();
                break;
            case ANALYTES_RESULTS:
                prepAndReflexTab.finishEditing();
                sampleTypeTab.finishEditing();
                analyteAndResultTab.draw();
                break;
            case PREPS_REFLEXES:
                analyteAndResultTab.finishEditing();
                prepAndReflexTab.draw();
                break;
            case WORKSHEET:
                analyteAndResultTab.finishEditing();
                worksheetLayoutTab.draw();
                break;
        }
    }
    

    public void showErrors(ValidationErrorsList errors) {
        ArrayList<LocalizedException> formErrors;
        
        formErrors = new ArrayList<LocalizedException>();
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                if (ex instanceof GridFieldErrorException) {
                    GridFieldErrorException gtfe = (GridFieldErrorException)ex;
                    if ("analyteTable".equals(gtfe.getTableKey())) {
                        analyteAndResultTab.showTestAnalyteError(gtfe);
                    } else if ("resultTable".equals(gtfe.getTableKey())) {
                        analyteAndResultTab.showTestResultError(gtfe);
                    }
                } else {
                    TableFieldErrorException tfe = (TableFieldErrorException)ex;
                    ((TableWidget)def.getWidget(tfe.getTableKey())).setCellException(tfe.getRowIndex(),
                                                                                 tfe.getFieldName(),
                                                                                 tfe);
                }
            } else if (ex instanceof FieldErrorException) {
                FieldErrorException fe = (FieldErrorException)ex;
                ((HasField)def.getWidget(fe.getFieldName())).addException(fe);
            } else if (ex instanceof FormErrorException) {
                FormErrorException fe = (FormErrorException)ex;
                formErrors.add(fe);
            } else {
                Window.alert(ex.getMessage());
            }
        }
        if (formErrors.size() == 0)
            window.setError(consts.get("correctErrors"));
        else if (formErrors.size() == 1)
            window.setError(formErrors.get(0).getMessage());
        else {
            window.setError("(Error 1 of " + formErrors.size() + ") " + formErrors.get(0).getMessage());
            window.setMessagePopup(formErrors, "ErrorPanel");
        }
    }

    private void duplicate() {
        try {
            manager = TestManager.fetchById(manager.getTest().getId());

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
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestSectionViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        for (int iter = 0; iter < manager.getTestSections().count(); iter++ ) {
            data = manager.getTestSections().getSectionAt(iter);

            row = new TableDataRow(2);
            row.key = data.getId();

            row.cells.get(0).value = data.getSectionId();
            row.cells.get(1).value = data.getFlagId();
            model.add(row);
        }

        return model;
    }

    private boolean canCommitResultGroups() {
        ArrayList<Integer> list;
        int i, size;
        boolean commit;

        commit = true;

        try {
            list = getEmptyResultGroups(manager.getTestResults());
            size = list.size();
            if (size > 0) {
                commit = Window.confirm(consts.get("resultGroupsEmpty"));
                if (commit) {
                    for (i = 0; i < size; i++ ) {
                        manager.getTestResults().removeResultGroup(list.get(i));
                    }
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return commit;
    }

    private boolean allowAnalytesEmpty() {
        int anasize, ressize;
        boolean allow;

        anasize = 0;
        ressize = 0;
        allow = true;
        try {
            anasize = manager.getTestAnalytes().rowCount();
            ressize = manager.getTestResults().groupCount();

            if (anasize == 0 && ressize > 0) {
                allow = Window.confirm(consts.get("resultNoAnalytes"));
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return allow;
    }

    private ArrayList<Integer> getEmptyResultGroups(TestResultManager trm) {
        ArrayList<Integer> empList;

        empList = new ArrayList<Integer>();

        if (trm != null) {
            for (int i = 0; i < trm.groupCount(); i++ ) {
                if (trm.getResultGroupSize(i + 1) == 0)
                    empList.add(i + 1);
            }
        }

        return empList;
    }

    private void clearKeys() {
        TestDO data;

        data = manager.getTest();
        data.setId(null);
        data.setIsActive("N");
        data.setIsReportable("N");
        data.setActiveBegin(null);
        data.setActiveEnd(null);

        try {
            clearSectionKeys(manager.getTestSections());
            sampleTypeTab.clearKeys(manager.getSampleTypes());
            analyteAndResultTab.clearKeys(manager.getTestAnalytes(), manager.getTestResults());
            prepAndReflexTab.clearKeys(manager.getPrepTests(), manager.getReflexTests());
            worksheetLayoutTab.clearKeys(manager.getTestWorksheet());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

    }

    private void clearSectionKeys(TestSectionManager tsm) {
        TestSectionViewDO data;

        for (int i = 0; i < tsm.count(); i++ ) {
            data = tsm.getSectionAt(i);
            data.setId(null);
            data.setTestId(null);
        }
    }
}
