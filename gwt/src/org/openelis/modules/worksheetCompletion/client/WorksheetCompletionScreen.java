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
package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;
import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabPanel;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.SecuritySection;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCompletionScreen extends Screen {

    private boolean              closeWindow, isPopup;
    private Integer              formatBatch, formatTotal;
    private ArrayList<SectionDO> sections;
    private ScreenService        instrumentService, userService;
    private SecurityModule       security;
    private WorksheetManager     manager;

    private AppButton   lookupWorksheetButton, printButton, updateButton, commitButton,
                        abortButton, editWorksheetButton, loadFromEditButton, loadFilePopupButton;
    private NotesTab    noteTab;
    private Tabs        tab;
    private TabPanel    tabPanel;
    private TableWidget table;

    protected AutoComplete<Integer>     instrumentId, defaultUser;
    protected CalendarLookUp            defaultStartedDate, defaultCompletedDate;
    protected Confirm                   worksheetExitConfirm;
    protected Dropdown<Integer>         statusId;
    protected TextBox<Integer>          worksheetId, relatedWorksheetId;
    protected WorksheetFileUploadScreen wFileUploadScreen;
    protected WorksheetLookupScreen     wLookupScreen, wrLookupScreen;
    
    private enum Tabs {
        NOTE
    };

    public WorksheetCompletionScreen(final Integer worksheetId) throws Exception {
        this();
        isPopup = true;
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                fetchById(worksheetId);
            }
        });
    }
    
    public WorksheetCompletionScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCompletionDef.class));

        isPopup           = false;
        service           = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");
        instrumentService = new ScreenService("OpenELISServlet?service=org.openelis.modules.instrument.server.InstrumentService");
        userService       = new ScreenService("controller?service=org.openelis.server.SystemUserService");

        security = OpenELIS.security.getModule("worksheet");
        if (security == null)
            throw new SecurityException("screenPermException", "Worksheet Completion Screen");

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
        closeWindow = false;
        tab         = Tabs.NOTE;

        try {
            DictionaryCache.preloadByCategorySystemNames("analysis_status",
                                                         "instrument_type",
                                                         "type_of_sample", 
                                                         "test_worksheet_format",
                                                         "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();

        setState(State.DEFAULT);
        if (!isPopup)
            openLookupWindow();
        initializeDropdowns();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        printButton = (AppButton)def.getWidget("print");
        addScreenHandler(printButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                print();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    security.hasUpdatePermission() &&
                                    canEditAny());
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
                commitButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        worksheetId = (TextBox)def.getWidget(WorksheetCompletionMeta.getId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.enable(false);
            }
        });

        statusId = (Dropdown)def.getWidget(WorksheetCompletionMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(manager.getWorksheet().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        relatedWorksheetId = (TextBox)def.getWidget(WorksheetCompletionMeta.getRelatedWorksheetId());
        addScreenHandler(relatedWorksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setValue(manager.getWorksheet().getRelatedWorksheetId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                relatedWorksheetId.enable(false);
            }
        });

        lookupWorksheetButton = (AppButton)def.getWidget("lookupWorksheetButton");
        addScreenHandler(lookupWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lookupWorksheetButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        instrumentId = (AutoComplete)def.getWidget("instrumentId");
        addScreenHandler(instrumentId, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                instrumentId.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        instrumentId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow>     model;
                ArrayList<InstrumentViewDO> matches;
                QueryFieldUtil              parser;
                TableDataRow                row;
                InstrumentViewDO            iVDO;                

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                try {
                    model = new ArrayList<TableDataRow>();
                    matches = instrumentService.callList("fetchByName", parser.getParameter().get(0));
                    for (int i = 0; i < matches.size(); i++) {
                        iVDO = (InstrumentViewDO)matches.get(i);
                        
                        row = new TableDataRow(5);
                        row.key = iVDO.getId();
                        row.cells.get(0).value = iVDO.getName();
                        row.cells.get(1).value = iVDO.getDescription();
                        row.cells.get(2).value = iVDO.getTypeId();
                        row.cells.get(3).value = iVDO.getLocation();
                        row.data = iVDO;
                        
                        model.add(row);
                    } 
                    
                    instrumentId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        defaultUser = (AutoComplete<Integer>)def.getWidget("defaultUser");
        addScreenHandler(defaultUser, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultUser.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        defaultUser.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SecuritySystemUserDO> users;
                ArrayList<TableDataRow> model;
                
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    users = userService.callList("fetchByLogin", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (SecuritySystemUserDO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    defaultUser.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        defaultStartedDate = (CalendarLookUp)def.getWidget("defaultStartedDate");
        addScreenHandler(defaultStartedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultStartedDate.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        defaultCompletedDate = (CalendarLookUp)def.getWidget("defaultCompletedDate");
        addScreenHandler(defaultCompletedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultCompletedDate.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        table = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // table cannot be edited directly
                event.cancel();
            }
        });

        editWorksheetButton = (AppButton)def.getWidget("editWorksheetButton");
        addScreenHandler(editWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editWorksheet();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editWorksheetButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFromEditButton = (AppButton)def.getWidget("loadFromEditButton");
        addScreenHandler(loadFromEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadFromEdit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFromEditButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        loadFilePopupButton = (AppButton)def.getWidget("loadFilePopupButton");
        addScreenHandler(loadFilePopupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetFileUpload();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFilePopupButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
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

        noteTab = new NotesTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(noteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                noteTab.setManager(manager);
                if (tab == Tabs.NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                noteTab.setState(event.getState());
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                } else {
                    if (!isPopup && !closeWindow) {
                        event.cancel();
                        openLookupWindow();
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        try {
            formatBatch = DictionaryCache.getIdFromSystemName("wsheet_num_format_batch");
            formatTotal = DictionaryCache.getIdFromSystemName("wsheet_num_format_total");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load worksheet status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("worksheet_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load the instrument type model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("instrument_type");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)instrumentId.getColumns().get(2).getColumnWidget()).setModel(model);

        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = WorksheetManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case NOTE:
                        manager = WorksheetManager.fetchWithNotes(id);
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
            case NOTE:
                noteTab.draw();
                break;
        }
    }

    protected void openLookupWindow() {
        ScreenWindow modal;
        
        try {
            if (wLookupScreen == null) {
                wLookupScreen = new WorksheetLookupScreen();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<TableDataRow> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).data;
                                fetchById(wVDO.getId());
                            }
                        } else if (event.getAction() == WorksheetLookupScreen.Action.CANCEL) {
                            closeWindow = true;
                            window.close();
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    protected void print() {
        Window.alert("Worksheet printed!!!");
    }
    
    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        window.setBusy(consts.get("updating"));
        try {
            manager = manager.update();
            commitSampleManagers();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updatingComplete"));
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("save(): " + e.getMessage());
            window.clearStatus();
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.UPDATE) {
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

    /*
     * Call the update method on SampleManagers attached to analysis data rows
     */
    protected void commitSampleManagers() throws Exception {
        int                     i;
        ArrayList<TableDataRow> model;
        SampleDataBundle        bundle;
        SampleManager           manager;
        
        model = table.getData();
        for (i = 0; i < model.size(); i++) {
            if (model.get(i).data instanceof SampleDataBundle) {
                bundle = (SampleDataBundle) model.get(i).data;
                manager = bundle.getSampleManager();
                try {
                    manager.update();
                } catch (ValidationErrorsList e) {
                    throw e;
                } catch (Exception anyE) {
                    throw new Exception("WorksheetTable Row "+(i+1)+": "+anyE.getMessage());
                }
            }
        }
    }
    
    protected void editWorksheet() {
        window.setBusy("Saving worksheet for editing");
        try {
            service.call("saveForEdit", manager);
            window.setDone("Worksheet saved for editing to 'M:\\temp\\Worksheet"+manager.getWorksheet().getId()+".xls");
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    protected void loadFromEdit() {
        window.setBusy("Loading worksheet from edited file");
        try {
            manager = service.call("loadFromEdit", manager);
            DataChangeEvent.fire(this);
            window.setDone("Worksheet loaded");
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    private void openWorksheetLookup() {
        ScreenWindow modal;
        
        try {
            if (wrLookupScreen == null) {
                final WorksheetCompletionScreen wcs = this;
                wrLookupScreen = new WorksheetLookupScreen();
                wrLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<TableDataRow> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<TableDataRow>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).data;
                                manager.getWorksheet().setRelatedWorksheetId(wVDO.getId());
                                DataChangeEvent.fire(wcs, relatedWorksheetId);
                            }
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wrLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    protected void openWorksheetFileUpload() {
        ScreenWindow modal;
        
        try {
            if (wFileUploadScreen == null) {
                final WorksheetCompletionScreen wcs = this;
                wFileUploadScreen = new WorksheetFileUploadScreen();
                wFileUploadScreen.addActionHandler(new ActionHandler<WorksheetFileUploadScreen.Action>() {
                    public void onAction(ActionEvent<WorksheetFileUploadScreen.Action> event) {
                        if (event.getAction() == WorksheetFileUploadScreen.Action.OK) {
                        }
                    }
                });
            }
            
            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(consts.get("worksheetFileUpload"));
            modal.setContent(wFileUploadScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int                      i, j, k;
        ArrayList<TableDataRow>  model;
        TableDataRow             row;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisViewDO           aVDO;
        ResultViewDO             rVDO;
        QcAnalyteViewDO          qcaVDO;
        QcManager                qcManager;
        SampleDataBundle         bundle;
        SampleDomainInt          sDomain;
        SampleManager            sManager;
        SectionDO                sectionDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager;
        WorksheetItemDO          wiDO;
        WorksheetQcResultManager wqrManager;
        WorksheetQcResultViewDO  wqrVDO;
        WorksheetResultManager   wrManager;
        WorksheetResultViewDO    wrVDO;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = getPositionNumber(wiDO.getPosition());
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);

                    if (j != 0)
                        row.cells.get(0).value = null;
                    
                    row.cells.get(1).value = waDO.getAccessionNumber();

                    if (waDO.getAnalysisId() != null) {
                        bundle = waManager.getBundleAt(j);
                        sManager = bundle.getSampleManager();
                        sDomain = sManager.getDomainManager();
                        aManager = sManager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                        aVDO = aManager.getAnalysisAt(bundle.getAnalysisIndex());
                        arManager = aManager.getAnalysisResultAt(bundle.getAnalysisIndex());

                        if (sections == null)
                            sections = new ArrayList<SectionDO>();
                        sectionDO = SectionCache.getSectionFromId(aVDO.getSectionId());
                        if (!sections.contains(sectionDO))
                            sections.add(sectionDO);
                        
                        if (sDomain != null)
                            row.cells.get(2).value = sDomain.getDomainDescription();
                        else
                            row.cells.get(2).value = "";
                        
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = aVDO.getTestName();
                        row.cells.get(5).value = aVDO.getMethodName();
                        row.cells.get(6).value = aVDO.getStatusId();

                        wrManager = waManager.getWorksheetResultAt(j);
                        for (k = 0; k < wrManager.count(); k++) {
                            if (k != 0) {
                                row.cells.get(0).value = null;
                                row.cells.get(1).value = null;
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = 0;
                            }
                            wrVDO = wrManager.getWorksheetResultAt(k);
                            rVDO = arManager.getResultForWorksheet(waDO.getAnalysisId(), wrVDO.getAnalyteId());
                            row.cells.get(7).value = wrVDO.getAnalyteName();
                            row.cells.get(8).value = wrVDO.getValue();
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = rVDO.getValue();
                            row.cells.get(11).value = "";
                            row.cells.get(12).value = "";
                            row.data = bundle;
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            row.data = bundle;
                            model.add((TableDataRow)row.clone());
                        }
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.cells.get(2).value = qcManager.getQc().getName();
                        row.cells.get(3).value = "";
                        row.cells.get(4).value = "";
                        row.cells.get(5).value = "";
                        row.cells.get(6).value = 0;

                        wqrManager = waManager.getWorksheetQcResultAt(j);
                        for (k = 0; k < wqrManager.count(); k++) {
                            if (k != 0) {
                                row.cells.get(0).value = null;
                                row.cells.get(1).value = null;
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = 0;
                            }
                            wqrVDO = wqrManager.getWorksheetQcResultAt(k);
                            qcaVDO = qcManager.getAnalytes().getAnalyteAt(k);
                            row.cells.get(7).value = wqrVDO.getAnalyteName();
                            row.cells.get(8).value = wqrVDO.getValue();
                            row.cells.get(9).value = "";
                            row.cells.get(10).value = "";
                            row.cells.get(11).value = qcaVDO.getValue();
                            row.cells.get(12).value = "";
                            row.data = qcManager;
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            row.data = qcManager;
                            model.add((TableDataRow)row.clone());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private boolean canEditAny() {
        int             i;
        SectionDO       section;
        SecuritySection secSection;
/*        
        for (i = 0; i < sections.size(); i++) {
            section = sections.get(i);
            secSection = OpenELIS.security.getSection(section.getName());
            if (secSection.hasCompletePermission())
                return true;
        }
*/        
        return true;
//        return false;
    }
    
    private Object getPositionNumber(int position) {
        int    major, minor;
        Object positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(manager.getWorksheet().getFormatId())) {
            major = getPositionMajorNumber(position);
            minor = getPositionMinorNumber(position);
            positionNumber = major+"-"+minor;
        } else if (formatTotal.equals(manager.getWorksheet().getFormatId())) {
            positionNumber = position;
        }
        
        return positionNumber;
    }
    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
    private int getPositionMajorNumber(int position) {
        return (int) (position / (double)manager.getWorksheet().getBatchCapacity() + .99);
    }

    /**
      * Parses the position number and returns the minor number
      * for batch numbering.
      */
    private int getPositionMinorNumber(int position) {
        return position - (getPositionMajorNumber(position) - 1) * manager.getWorksheet().getBatchCapacity();
    }
}