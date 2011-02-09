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
import com.google.gwt.user.client.ui.TabPanel;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SectionPermission;
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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.WorksheetManager;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.EditNoteScreen;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.modules.sample.client.TestPrepUtility;
import org.openelis.modules.sample.client.TestReflexUtility;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCompletionScreen extends Screen {

    private boolean              closeWindow, isPopup;
    private Integer              statusWorking, statusFailedRun, origStatus;
    private ScreenService        instrumentService, sysVarService;
    private ModulePermission     userPermission;
    private WorksheetManager     manager;

    private Button            lookupWorksheetButton, printButton, updateButton,
                              commitButton, abortButton, editWorksheetButton, loadFromEditButton,
                              loadFilePopupButton;
    private NotesTab          noteTab;
    private TestPrepUtility   testPrepUtil;
    private TestReflexUtility testReflexUtil;
    private Tabs              tab;
    private TabPanel          tabPanel;
//    private TableWidget       table;

    protected Integer                   userId;
    protected String                    outputFileDirectory, worksheetFileName,
                                        userName;
    protected AutoComplete              instrumentId, defaultUser;
    protected Calendar                  defaultStartedDate, defaultCompletedDate;
    protected Confirm                   worksheetExitConfirm, worksheetEditConfirm;
    protected Dropdown<Integer>         statusId;
    protected EditNoteScreen            editNote;
    protected NoteViewDO                failedRunNote;
    protected TextBox<Integer>          worksheetId, relatedWorksheetId;
    protected WorksheetFileUploadScreen wFileUploadScreen;
    protected WorksheetLookupScreen     wLookupScreen, wrLookupScreen;
    
    private enum Tabs {
        /*WORKSHEET, */NOTE
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
        service           = new ScreenService("controller?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");
        instrumentService = new ScreenService("controller?service=org.openelis.modules.instrument.server.InstrumentService");
        sysVarService     = new ScreenService("controller?service=org.openelis.modules.systemvariable.server.SystemVariableService");
        
        userPermission = OpenELIS.getSystemUserPermission().getModule("worksheet");
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
        tab         = Tabs.NOTE;

        try {
            DictionaryCache.preloadByCategorySystemNames("analysis_status",
                                                         "instrument_type",
                                                         "type_of_sample", 
                                                         "test_worksheet_format",
                                                         "worksheet_status");
            
            list = sysVarService.callList("fetchByName", "worksheet_display_directory");
            if (list.size() == 0)
                throw new Exception(consts.get("worksheetOutputDirectoryLookupException"));
            else
                outputFileDirectory = ((SystemVariableDO)list.get(0)).getValue();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
        printButton = (Button)def.getWidget("print");
        addScreenHandler(printButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                print();
            }

            public void onStateChange(StateChangeEvent<State> event) {
//                printButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
                printButton.setEnabled(Boolean.FALSE);
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission() &&
                                    canEditAny());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
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
                commitButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        worksheetId = (TextBox)def.getWidget(WorksheetCompletionMeta.getId());
        addScreenHandler(worksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(manager.getWorksheet().getId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetId.setEnabled(false);
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
                statusId.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        relatedWorksheetId = (TextBox)def.getWidget(WorksheetCompletionMeta.getRelatedWorksheetId());
        addScreenHandler(relatedWorksheetId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setValue(manager.getWorksheet().getRelatedWorksheetId());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                relatedWorksheetId.setEnabled(false);
            }
        });

        lookupWorksheetButton = (Button)def.getWidget("lookupWorksheetButton");
        addScreenHandler(lookupWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                openWorksheetLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    lookupWorksheetButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                                 statusWorking.equals(statusId));
                } else {
                    lookupWorksheetButton.setEnabled(false);
                }
            }
        });

        instrumentId = (AutoComplete)def.getWidget("instrumentId");
        addScreenHandler(instrumentId, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    instrumentId.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                        statusWorking.equals(statusId));
                } else {
                    instrumentId.setEnabled(false);
                }
            }
        });

        instrumentId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>>    model;
                ArrayList<InstrumentViewDO> matches;
                QueryFieldUtil              parser;
                Item<Integer>               row;
                InstrumentViewDO            iVDO;
                String param = "";

                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")){
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    model = new ArrayList<Item<Integer>>();
                    matches = instrumentService.callList("fetchByName", param);
                    for (int i = 0; i < matches.size(); i++) {
                        iVDO = (InstrumentViewDO)matches.get(i);
                        
                        row = new Item<Integer>(5);
                        row.setKey(iVDO.getId());
                        row.setCell(0,iVDO.getName());
                        row.setCell(1,iVDO.getDescription());
                        row.setCell(2,iVDO.getTypeId());
                        row.setCell(3,iVDO.getLocation());
                        row.setData(iVDO);
                        
                        model.add(row);
                    } 
                    
                    instrumentId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());                     
                }
            } 
        });
/*
        table = (Table)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // table cannot be edited directly
                event.cancel();
            }
        });
*/
        editWorksheetButton = (Button)def.getWidget("editWorksheetButton");
        addScreenHandler(editWorksheetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                editWorksheet();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (manager != null)
                    editWorksheetButton.setEnabled(userPermission.hasUpdatePermission() &&
                                               canEditAny());
                else
                    editWorksheetButton.setEnabled(false);
            }
        });

        loadFromEditButton = (Button)def.getWidget("loadFromEditButton");
        addScreenHandler(loadFromEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadFromEdit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                Integer statusId;
                
                if (manager != null && manager.getWorksheet() != null) {
                    statusId = manager.getWorksheet().getStatusId();
                    loadFromEditButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()) &&
                                              statusWorking.equals(statusId));
                } else {
                    loadFromEditButton.setEnabled(false);
                }
            }
        });

        loadFilePopupButton = (Button)def.getWidget("loadFilePopupButton");
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
                    loadFilePopupButton.setEnabled(Boolean.FALSE);
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
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
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
        ArrayList<Item<Integer>> model;

        try {
            statusWorking = DictionaryCache.getIdFromSystemName("wsheet_working");
            statusFailedRun = DictionaryCache.getIdFromSystemName("worksheet_failed");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
        
        //
        // load worksheet status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("worksheet_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load the instrument type model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("instrument_type");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)instrumentId.getPopupContext().getColumnWidget(2)).setModel(model);

        //
        // load analysis status dropdown model
        //
//        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
//        model = new ArrayList<Item<Integer>>();
//        model.add(new Item<Integer>(null, ""));
//       for (DictionaryDO resultDO : dictList)
//            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
//        ((Dropdown<Integer>)table.getColumnWidget(6)).setModel(model);
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
                        manager = WorksheetManager.fetchWithItemsAndNotes(id);
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
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
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
        ModalWindow modal;
        
        try {
            if (wLookupScreen == null) {
                wLookupScreen = new WorksheetLookupScreen();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<Row> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<Row>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).getData();
                                fetchById(wVDO.getId());
                            }
                        } else if (event.getAction() == WorksheetLookupScreen.Action.CANCEL) {
                            closeWindow = true;
                            window.close();
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        window.setBusy(consts.get("updating"));
        try {
            manager = manager.update();
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updatingComplete"));
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("save(): " + e.getMessage());
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
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected void editWorksheet() {
        window.setBusy("Saving worksheet for editing");
        try {
            service.call("saveForEdit", manager);
            worksheetFileName = new String(outputFileDirectory+manager.getWorksheet().getId()+
                                           "_"+OpenELIS.getSystemUserPermission().getLoginName()+".xls");
            window.setDone(consts.get("worksheetCompletionEditConfirm")+
                                      " "+worksheetFileName);
        } catch (Exception anyE) {
            com.google.gwt.user.client.Window.alert(anyE.getMessage());
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
                            com.google.gwt.user.client.Window.alert("loadFromEdit: " + e.getMessage());
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
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception anyE) {
            com.google.gwt.user.client.Window.alert(anyE.getMessage());
            window.clearStatus();
        } finally {
            manager.getReflexBundles().clear();
        }
    }

    private void openWorksheetLookup() {
        ModalWindow modal;
        
        try {
            if (wrLookupScreen == null) {
                final WorksheetCompletionScreen wcs = this;
                wrLookupScreen = new WorksheetLookupScreen();
                wrLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreen.Action> event) {
                        ArrayList<Row> list;
                        WorksheetViewDO         wVDO;

                        if (event.getAction() == WorksheetLookupScreen.Action.SELECT) {
                            list = (ArrayList<Row>)event.getData();
                            if (list != null) {
                                wVDO = (WorksheetViewDO)list.get(0).getData();
                                manager.getWorksheet().setRelatedWorksheetId(wVDO.getId());
                                DataChangeEvent.fire(wcs, relatedWorksheetId);
                            }
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetLookup"));
            modal.setContent(wrLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    protected void openWorksheetFileUpload() {
        ModalWindow modal;
        
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
            
            modal = new ModalWindow();
            modal.setName(consts.get("worksheetFileUpload"));
            modal.setContent(wFileUploadScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
            return;
        }
    }
    
    protected void openFailedRunNote() {
        ModalWindow modal;
        
        if (editNote == null) {
            userName = OpenELIS.getSystemUserPermission().getLoginName();
            userId = OpenELIS.getSystemUserPermission().getSystemUserId();
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
                                    com.google.gwt.user.client.Window.alert("Error in EditNote:" + anyE.getMessage());
                                }
                            } else {
                                commit();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert("Error in EditNote:" + e.getMessage());
                return;
            }
        }

        modal = new ModalWindow();
        modal.setName(consts.get("noteEditor"));
        modal.setContent(editNote);

        failedRunNote = null;
        try {
            failedRunNote = manager.getNotes().getEditingNote();
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("Error in EditNote:" + e.getMessage());
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
                perm = OpenELIS.getSystemUserPermission().getSection(section.getName());
                if (perm != null && perm.hasCompletePermission())
                    return true;
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
            com.google.gwt.user.client.Window.alert("Error loading sections for permission check:" + anyE.getMessage());
        }

        return false;
    }    

/*    
    private String getWorksheetOutputFileName(Integer worksheetNumber, Integer userId) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        SystemUserVO                userVO;
        
        dirName = "";
        try {
            sysVars = sysVarLocal().fetchByName("worksheet_output_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        userVO = null;
        try {
            userVO = sysUserLocal().fetchById(userId);
        } catch (Exception anyE) {
            throw new Exception("Error retrieving username for worksheet: "+anyE.getMessage());
        }
        
        return dirName+worksheetNumber+"_"+userVO.getLoginName()+".xls";
    }
/*        
/*    
    private ArrayList<Row> getTableModel() {
        int                      i, j, k, l;
        ArrayList<Row>           model;
        Row                      row;
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
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++) {
                wiDO = manager.getItems().getWorksheetItemAt(i);
                waManager = manager.getItems().getWorksheetAnalysisAt(i);

                row = new Row(39);
                row.setCell(0,getPositionNumber(wiDO.getPosition()));
                for (j = 0; j < waManager.count(); j++) {
                    waDO = waManager.getWorksheetAnalysisAt(j);

                    if (j != 0)
                        row.setCell(0,null);
                    
                    row.setCell(1,waDO.getAccessionNumber());

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
                            row.setCell(2,sDomain.getDomainDescription());
                        else
                            row.setCell(2,"");
                        
                        row.setCell(3,"");
                        row.setCell(4,aVDO.getTestName());
                        row.setCell(5,aVDO.getMethodName());
                        row.setCell(6,aVDO.getStatusId());

                        wrManager = waManager.getWorksheetResultAt(j);
                        for (k = 0; k < wrManager.count(); k++) {
                            wrVDO = wrManager.getWorksheetResultAt(k);
                            if (k != 0) {
                                row.setCell(0,null);
                                row.setCell(1,null);
                                row.setCell(2,"");
                                row.setCell(3,"");
                                row.setCell(4,"");
                                row.setCell(5,"");
                                row.setCell(6,0);
                            }
                            rVDO = arManager.getResultAt(wrVDO.getResultRow(), 0);
                            row.setCell(7,wrVDO.getAnalyteName());
                            row.setCell(8,rVDO.getIsReportable());
                            for (l = 0; l < 30; l++)
                                row.setCell(9+l,wrVDO.getValueAt(l));
                            row.setData(bundle);
                            model.add((Row)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.setCell(7,"NO ANALYTES FOUND");
                            row.setData(bundle);
                            model.add((Row)row.clone());
                        }
                    } else if (waDO.getQcId() != null) {
                        qcManager = QcManager.fetchById(waDO.getQcId());
                        
                        row.setCell(2,qcManager.getQc().getName());
                        row.setCell(3,"");
                        row.setCell(4,"");
                        row.setCell(5,"");
                        row.setCell(6,0);

                        wqrManager = waManager.getWorksheetQcResultAt(j);
                        for (k = 0; k < wqrManager.count(); k++) {
                            if (k != 0) {
                                row.setCell(0,null);
                                row.setCell(1,null);
                                row.setCell(2,"");
                                row.setCell(3,"");
                                row.setCell(4,"");
                                row.setCell(5,"");
                                row.setCell(6,0);
                            }
                            wqrVDO = wqrManager.getWorksheetQcResultAt(k);
                            qcaVDO = qcManager.getAnalytes().getAnalyteAt(k);
                            row.setCell(7,wqrVDO.getAnalyteName());
                            row.setCell(8,"");
                            row.setCell(9,wqrVDO.getValue());
                            row.setCell(10,"");
                            row.setCell(11,"");
                            row.setCell(12,qcaVDO.getValue());
                            row.setData(qcManager);
                            model.add((Row)row.clone());
                        }
                        
                        //
                        // Add the row if there were no analytes
                        //
                        if (k == 0) {
                            row.setCell(7,"NO ANALYTES FOUND");
                            row.setData(qcManager);
                            model.add((Row)row.clone());
                        }
                    }
                }
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }
   
    private Object getPositionNumber(int position) {
        int    major, minor;
        Object positionNumber;
        
        positionNumber = "";
        if (formatBatch.equals(manager.getWorksheet().getFormatId())) {
            major = getPositionMajorNumber(position);
            minor = getPositionMinorNumber(position);
            positionNumber = major+"-"+minor;
        } else {
            positionNumber = position;
        }
        
        return positionNumber;
    }
*/    
    /**
     * Parses the position number and returns the major number
     * for batch numbering.
     */
//    private int getPositionMajorNumber(int position) {
//        return (int) (position / (double)manager.getWorksheet().getBatchCapacity() + .99);
//    }

    /**
      * Parses the position number and returns the minor number
      * for batch numbering.
      */
//    private int getPositionMinorNumber(int position) {
//        return position - (getPositionMajorNumber(position) - 1) * manager.getWorksheet().getBatchCapacity();
//    }
}