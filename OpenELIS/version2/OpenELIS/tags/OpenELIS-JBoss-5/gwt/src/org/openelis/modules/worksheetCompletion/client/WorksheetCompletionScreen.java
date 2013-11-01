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
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabPanel;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.common.SystemUserVO;
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
import org.openelis.modules.note.client.EditNoteScreen;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.modules.sample.client.TestPrepUtility;
import org.openelis.modules.sample.client.TestReflexUtility;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCompletionScreen extends Screen {

    private boolean              closeWindow, isPopup, successfulLoad;
    private Integer              statusWorking, statusFailedRun, origStatus;
    private String               headerLabels[];
    private ScreenService        instrumentService, sysVarService;
    private ModulePermission     userPermission;
    private WorksheetManager     manager;

    private AppButton         lookupWorksheetButton, printButton, updateButton,
                              commitButton, abortButton, editWorksheetButton, loadFromEditButton,
                              loadFilePopupButton;
    private NotesTab          noteTab;
    private TestPrepUtility   testPrepUtil;
    private TestReflexUtility testReflexUtil;
    private Tabs              tab;
    private TabPanel          tabPanel;
    private TableWidget       table;

    protected Integer                   userId;
    protected String                    displayFileDirectory, templateFileDirectory, userName;
    protected AutoComplete<Integer>     instrumentId, defaultUser;
    protected CalendarLookUp            defaultStartedDate, defaultCompletedDate;
    protected Confirm                   worksheetExitConfirm, worksheetEditConfirm;
    protected Dropdown<Integer>         statusId;
    protected EditNoteScreen            editNote;
    protected NoteViewDO                failedRunNote;
    protected TextBox<Integer>          worksheetId, relatedWorksheetId;
    protected WorksheetFileUploadScreen wFileUploadScreen;
    protected WorksheetLookupScreen     wLookupScreen, wrLookupScreen;
    
    private enum Tabs {
        WORKSHEET, NOTE
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
        successfulLoad    = false;
        service           = new ScreenService("controller?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");
        instrumentService = new ScreenService("controller?service=org.openelis.modules.instrument.server.InstrumentService");
        sysVarService     = new ScreenService("controller?service=org.openelis.modules.systemvariable.server.SystemVariableService");
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Worksheet Completion Screen");

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
        ArrayList<SystemVariableDO> list;
        
        closeWindow = false;
        tab         = Tabs.WORKSHEET;

        try {
            CategoryCache.getBySystemNames("analysis_status",
                                                         "instrument_type",
                                                         "type_of_sample", 
                                                         "test_worksheet_format",
                                                         "worksheet_status");
            
            list = sysVarService.callList("fetchByName", "worksheet_display_directory");
            if (list.size() == 0)
                throw new Exception(consts.get("worksheetDisplayDirectoryLookupException"));
            else
                displayFileDirectory = ((SystemVariableDO)list.get(0)).getValue();
            
            list = sysVarService.callList("fetchByName", "worksheet_template_directory");
            if (list.size() == 0)
                throw new Exception(consts.get("worksheetTemplateDirectoryLookupException"));
            else
                templateFileDirectory = ((SystemVariableDO)list.get(0)).getValue();
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
//                printButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
                printButton.enable(Boolean.FALSE);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission() &&
                                    canEditAny());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Integer statusId;
                
                statusId = manager.getWorksheet().getStatusId();
                if (statusFailedRun.equals(statusId) && !statusFailedRun.equals(origStatus))
                    openFailedRunNote();
                else
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
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    lookupWorksheetButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                                 statusWorking.equals(statusId));
                } else {
                    lookupWorksheetButton.enable(false);
                }
            }
        });

        instrumentId = (AutoComplete)def.getWidget("instrumentId");
        addScreenHandler(instrumentId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                instrumentId.setSelection(manager.getWorksheet().getInstrumentId(),
                                          manager.getWorksheet().getInstrumentName());
            }
                
            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getWorksheet().setInstrumentId(event.getValue());
                manager.getWorksheet().setInstrumentName(instrumentId.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    instrumentId.enable(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                        statusWorking.equals(statusId));
                } else {
                    instrumentId.enable(false);
                }
            }
        });

        instrumentId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow>     model;
                ArrayList<InstrumentViewDO> matches;
                TableDataRow                row;
                InstrumentViewDO            iVDO;                

                try {
                    model = new ArrayList<TableDataRow>();
                    matches = instrumentService.callList("fetchByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (int i = 0; i < matches.size(); i++) {
                        iVDO = (InstrumentViewDO)matches.get(i);
                        
                        row = new TableDataRow(5);
                        row.key = iVDO.getId();
                        row.cells.get(0).value = iVDO.getName();
                        row.cells.get(1).value = iVDO.getDescription();
                        row.cells.get(2).value = iVDO.getTypeId();
                        row.cells.get(3).value = iVDO.getLocation();
                        row.data = iVDO;
                        
                        if (!"Y".equals(iVDO.getIsActive()))
                            row.enabled = false;
                        
                        model.add(row);
                    } 
                    
                    instrumentId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        editWorksheetButton = (AppButton)def.getWidget("editWorksheetButton");
        addScreenHandler(editWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editWorksheet();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (manager != null)
                    editWorksheetButton.enable(userPermission.hasUpdatePermission() &&
                                               canEditAny());
                else
                    editWorksheetButton.enable(false);
            }
        });

        loadFromEditButton = (AppButton)def.getWidget("loadFromEditButton");
        addScreenHandler(loadFromEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadFromEdit();
                if (tab == Tabs.WORKSHEET)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    loadFromEditButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                              statusWorking.equals(statusId));
                } else {
                    loadFromEditButton.enable(false);
                }
            }
        });

        loadFilePopupButton = (AppButton)def.getWidget("loadFilePopupButton");
        addScreenHandler(loadFilePopupButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetFileUpload();
            }

            public void onStateChange(StateChangeEvent<State> event) {
//                Integer statusId;
//                
//                if (manager != null && manager.getWorksheet() != null) {
//                    statusId = manager.getWorksheet().getStatusId();
//                    loadFilePopupButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()) &&
//                                               statusWorking.equals(statusId));
//                } else {
                    loadFilePopupButton.enable(Boolean.FALSE);
//                }
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

        table = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
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
            statusWorking = DictionaryCache.getIdBySystemName("worksheet_working");
            statusFailedRun = DictionaryCache.getIdBySystemName("worksheet_failed");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load the instrument type model
        //
        dictList  = CategoryCache.getBySystemName("instrument_type");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)instrumentId.getColumns().get(2).getColumnWidget()).setModel(model);

        //
        // load analysis status dropdown model
        //
//        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
//        model = new ArrayList<TableDataRow>();
//        model.add(new TableDataRow(null, ""));
//        for (DictionaryDO resultDO : dictList)
//            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
//        ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = WorksheetManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case WORKSHEET:
                        manager = WorksheetManager.fetchWithItems(id);
                        break;
                        
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
            case WORKSHEET:
                table.load(getTableModel());
                break;
                
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
        // TODO: Add worksheet printing code
    }
    
    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            origStatus = manager.getWorksheet().getStatusId();
            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

        if (!validate()) {
            Window.alert(consts.get("correctErrors"));
            return;
        }

        window.setBusy(consts.get("updating"));
        try {
            manager = manager.update();
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updatingComplete"));
            successfulLoad = false;
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
        successfulLoad = false;
    }

    protected void editWorksheet() {
        SystemUserVO userVO;
        
        window.setBusy("Saving worksheet for editing");
        try {
            service.call("saveForEdit", manager);

            userVO = null;
            try {
                userVO = UserCache.getSystemUser(manager.getWorksheet().getSystemUserId());
            } catch (Exception anyE1) {
                throw new Exception("Error retrieving username for worksheet: "+anyE1.getMessage());
            }
            
            window.setDone(consts.get("worksheetCompletionEditConfirm")+
                                      " "+displayFileDirectory+manager.getWorksheet().getId()+
                                      "_"+userVO.getLoginName()+".xls");
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        }
    }

    protected void loadFromEdit() {
        int                          i;
        ArrayList<Object>            tempBundle;
        ArrayList<ResultViewDO>      reflexResults;
        ArrayList<SampleDataBundle>  reflexBundles;
        ArrayList<ArrayList<Object>> bundles;
        HashMap<SampleDataBundle,ArrayList<ResultViewDO>> reflexMap;
        SampleDataBundle             bundle;
        
        if (successfulLoad) {
            Window.alert(consts.get("oneWorksheetLoadPerCommit"));
            return;
        }
        
        final WorksheetCompletionScreen wcs = this;
        
        window.setBusy("Loading worksheet from edited file");
        try {
            manager = service.call("loadFromEdit", manager);
            DataChangeEvent.fire(this);

            if (testReflexUtil == null) {
                testReflexUtil = new TestReflexUtility();

                testReflexUtil.addActionHandler(new ActionHandler<TestReflexUtility.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<TestReflexUtility.Action> event) {
                        if (testPrepUtil == null) {
                            testPrepUtil = new TestPrepUtility();
                            testPrepUtil.setScreen(wcs);

                            testPrepUtil.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                                    window.setDone("Worksheet loaded");
                                }
                            });
                        }

                        try {
                            testPrepUtil.lookup((ArrayList<SampleDataBundle>)event.getData());
                        } catch (Exception e) {
                            Window.alert("loadFromEdit: " + e.getMessage());
                        }
                    }
                });
            }

            testReflexUtil.setScreen(this);
            
            bundles = manager.getReflexBundles();
            reflexBundles = new ArrayList<SampleDataBundle>();
            reflexMap = new HashMap<SampleDataBundle,ArrayList<ResultViewDO>>();
            for (i = 0; i < bundles.size(); i++) {
                tempBundle = bundles.get(i);
                bundle = (SampleDataBundle) tempBundle.get(0);
                reflexResults = reflexMap.get(bundle);
                if (reflexResults == null) {
                    reflexResults = new ArrayList<ResultViewDO>();
                    reflexBundles.add(bundle);
                }
                reflexResults.add((ResultViewDO)tempBundle.get(1));
                reflexMap.put(bundle, reflexResults);
            }
            testReflexUtil.resultsEntered(reflexBundles, reflexMap);
            successfulLoad = true;
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
            window.clearStatus();
        } finally {
            manager.getReflexBundles().clear();
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
    
    protected void openFailedRunNote() {
        ScreenWindow modal;
        
        if (editNote == null) {         
            userName = UserCache.getPermission().getLoginName();
            userId = UserCache.getPermission().getSystemUserId();
            try {                
                editNote = new EditNoteScreen();
                editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                    public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                        if (event.getAction() == EditNoteScreen.Action.OK) {
                            if (failedRunNote.getText() == null || failedRunNote.getText().trim().length() == 0) {
                                try {
                                    manager.getNotes().removeEditingNote();
                                } catch (Exception anyE) {
                                    anyE.printStackTrace();
                                    Window.alert("Error in EditNote:" + anyE.getMessage());
                                }
                            } else {
                                commit();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("Error in EditNote:" + e.getMessage());
                return;
            }
        }

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("noteEditor"));
        modal.setContent(editNote);

        failedRunNote = null;
        try {
            failedRunNote = manager.getNotes().getEditingNote();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error in EditNote:" + e.getMessage());
        }
        failedRunNote.setSystemUser(userName);
        failedRunNote.setSystemUserId(userId);
        failedRunNote.setSubject(consts.get("failedRunSubject"));
        failedRunNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        editNote.setNote(failedRunNote);
    }

    private boolean canEditAny() {
        HashMap<Integer,SectionViewDO> sections;
        Iterator<SectionViewDO>        iter;
        SectionViewDO                  section;
        SectionPermission              perm;

        try {
            sections = manager.getAnalysisSections();
            iter = sections.values().iterator();
            while (iter.hasNext()) {
                section = iter.next();
                perm = UserCache.getPermission().getSection(section.getName());
                if (perm != null && perm.hasCompletePermission())
                    return true;
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
            Window.alert("Error loading sections for permission check:" + anyE.getMessage());
        }

        return false;
    }

    private ArrayList<TableDataRow> getTableModel() {
        int                      i, j, k, l;
        AnalysisManager          aManager;
        AnalysisResultManager    arManager;
        AnalysisViewDO           aVDO;
        ArrayList<IdNameVO>      headers;
        ArrayList<TableDataRow>  model;
        HashMap<Integer,String>  qcLinkMap;
        QcManager                qcManager;
        ResultViewDO             rVDO;
        SampleDataBundle         bundle;
        SampleDomainInt          sDomain;
        SampleManager            sManager;
        String                   headerLabels[];
        TableDataRow             row;
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

        headers = new ArrayList<IdNameVO>();
        try {
            headers = service.callList("getHeaderLabelsForScreen", manager);
        } catch (Exception anyE) {
            Window.alert("Error loading headers for format; "+anyE.getMessage());
            anyE.printStackTrace();
        }

        headerLabels = table.view.headers;
        if (headerLabels == null)
            headerLabels = new String[39];
        for (i = 0; i < headers.size(); i++)
            headerLabels[i] = headers.get(i).getName();
        table.view.header.setHeaders(headerLabels);
        
//        row = new TableDataRow(39);
//        for (i = 0; i < headers.size(); i++)
//            row.cells.get(i).value = headers.get(i).getName();
//        model.add(row);

        qcLinkMap = new HashMap<Integer,String>();
        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new TableDataRow(39);
                row.cells.get(0).value = wiDO.getPosition().toString();
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);
                    qcLinkMap.put(waDO.getId(), waDO.getAccessionNumber());
                    
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

                        if (sDomain != null)
                            row.cells.get(2).value = sDomain.getDomainDescription();
                        else
                            row.cells.get(2).value = "";
                        
                        row.cells.get(3).value = waDO.getWorksheetAnalysisId();
                        row.cells.get(4).value = aVDO.getTestName();
                        row.cells.get(5).value = aVDO.getMethodName();
                        row.cells.get(6).value = DictionaryCache.getById(aVDO.getStatusId()).getEntry();

                        wrManager = waManager.getWorksheetResultAt(j);
                        for (k = 0; k < wrManager.count(); k++) {
                            wrVDO = wrManager.getWorksheetResultAt(k);
                            if (k != 0) {
                                row.cells.get(0).value = "";
                                row.cells.get(1).value = "";
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = "";
                            }
                            rVDO = arManager.getResultAt(wrVDO.getResultRow(), 0);
                            row.cells.get(7).value = wrVDO.getAnalyteName();
                            row.cells.get(8).value = rVDO.getIsReportable();
                            for (l = 0; l < 30; l++)
                                row.cells.get(9+l).value = wrVDO.getValueAt(l);
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            model.add((TableDataRow)row.clone());
                        }
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.cells.get(2).value = qcManager.getQc().getName();
                        row.cells.get(3).value = waDO.getWorksheetAnalysisId();
                        row.cells.get(4).value = "";
                        row.cells.get(5).value = "";
                        row.cells.get(6).value = "";

                        wqrManager = waManager.getWorksheetQcResultAt(j);
                        for (k = 0; k < wqrManager.count(); k++) {
                            if (k != 0) {
                                row.cells.get(0).value = "";
                                row.cells.get(1).value = "";
                                row.cells.get(2).value = "";
                                row.cells.get(3).value = "";
                                row.cells.get(4).value = "";
                                row.cells.get(5).value = "";
                                row.cells.get(6).value = "";
                            }
                            wqrVDO = wqrManager.getWorksheetQcResultAt(k);
                            row.cells.get(7).value = wqrVDO.getAnalyteName();
                            row.cells.get(8).value = "";
                            for (l = 0; l < 30; l++)
                                row.cells.get(9+l).value = wqrVDO.getValueAt(l);
                            model.add((TableDataRow)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.cells.get(7).value = "NO ANALYTES FOUND";
                            model.add((TableDataRow)row.clone());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        // Set the QC Link column for each row to the Accession Number for the
        // row to which it is linked
        for (i = 0; i < model.size(); i++) {
            row = model.get(i);
            if (row.cells.get(3).value != null)
                row.cells.get(3).value = qcLinkMap.get(row.cells.get(3).value);
        }
        return model;
    }
}