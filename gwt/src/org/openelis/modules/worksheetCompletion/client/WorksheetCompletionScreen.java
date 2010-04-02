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
import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabPanel;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.domain.WorksheetDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
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
import org.openelis.gwt.widget.FileUpload;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.QcManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetQcAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;

public class WorksheetCompletionScreen extends Screen {

    private boolean                               isTemplateLoaded, isSaved, closeWindow;
    private int                                   tempId, qcStartIndex;
    private Integer                               formatBatch, formatTotal,
                                                  qcDup, statusWorking, typeFixed,
                                                  typeRand, typeLastWell, typeLastRun,
                                                  typeLastBoth;
    private ScreenService                         instrumentService, qcService;
    private SecurityModule                        security;
    private WorksheetManager                      manager;

    private AppButton                             browseButton, exitButton, insertQCLookupButton,
                                                  insertQCWorksheetButton, loadButton,
                                                  lookupWorksheetButton, printButton,
                                                  removeRowButton;

    private WorksheetTab                          worksheetTab;
    private NotesTab                              noteTab;
    private Tabs                                  tab;
    private TabPanel                              tabPanel;

    protected AutoComplete<Integer>               instrumentId;
    protected ArrayList<Integer>                  testIds;
    protected ArrayList<TableDataRow>             analysisItems, qcLastRunList,
                                                  qcLastBothList, qcLinkModel,
                                                  testWorksheetItems;
    protected CalendarLookUp                      defaultStartedDate, defaultCompletedDate;
    protected Confirm                             worksheetRemoveQCConfirm, worksheetRemoveLastOfQCConfirm,
                                                  worksheetSaveConfirm, worksheetExitConfirm;
    protected Dropdown<Integer>                   statusId;
//    protected FileUpload                          loadFile;
    protected QcLookupScreen                      qcLookupScreen;
    protected TableDataRow                        qcItems[];
    protected TableWidget                         worksheetItemTable;
    protected TextBox<Integer>                    worksheetId, relatedWorksheetId;
    protected TextBox<String>                     loadFile, defaultInitials;
    protected TestWorksheetDO                     testWorksheetDO;
    protected TestWorksheetManager                twManager;
    protected WorksheetLookupScreen               wLookupScreen;
    protected ValidationErrorsList                qcErrors;
    
    private enum Tabs {
        WORKSHEET, NOTE
    };

    public WorksheetCompletionScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCompletionDef.class));

        service           = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCompletion.server.WorksheetCompletionService");
        instrumentService = new ScreenService("OpenELISServlet?service=org.openelis.modules.instrument.server.InstrumentService");
        qcService         = new ScreenService("OpenELISServlet?service=org.openelis.modules.qc.server.QcService");

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
        isSaved     = true;
        tab         = Tabs.WORKSHEET;
        tempId      = -1;
        closeWindow = false;

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
        openLookupWindow();
        initializeDropdowns();

//        DataChangeEvent.fire(this);
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
                printButton.enable(true);
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
                statusId.enable(true);
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
                lookupWorksheetButton.enable(true);
            }
        });

        instrumentId = (AutoComplete)def.getWidget("instrumentId");
        addScreenHandler(instrumentId, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                instrumentId.enable(true);
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

        loadFile = (TextBox)def.getWidget("loadFile");
//        loadFile = (FileUpload)def.getWidget("loadFile");
        addScreenHandler(loadFile, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent event) {
                String value;
                
                value = (String) event.getValue();
                if (value != null && value.length() > 0)
                    loadButton.enable(true);
                else
                    loadButton.enable(false);
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadFile.enable(true);
            }
        });

        browseButton = (AppButton)def.getWidget("browseButton");
        addScreenHandler(browseButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                browseButton.enable(true);
            }
        });

        loadButton = (AppButton)def.getWidget("loadButton");
        addScreenHandler(loadButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                load();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loadButton.enable(false);
            }
        });

        defaultInitials = (TextBox)def.getWidget("defaultInitials");
        addScreenHandler(defaultInitials, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultInitials.enable(true);
            }
        });

        defaultStartedDate = (CalendarLookUp)def.getWidget("defaultStartedDate");
        addScreenHandler(defaultStartedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultStartedDate.enable(true);
            }
        });

        defaultCompletedDate = (CalendarLookUp)def.getWidget("defaultCompletedDate");
        addScreenHandler(defaultCompletedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                defaultCompletedDate.enable(true);
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

        worksheetTab = new WorksheetTab(def, window);
        addScreenHandler(worksheetTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetTab.setManager(manager);
                if (tab == Tabs.WORKSHEET)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTab.setState(event.getState());
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
                if (!closeWindow) {
                    event.cancel();
                    exit();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        try {
            statusWorking = DictionaryCache.getIdFromSystemName("worksheet_working");
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
                worksheetTab.draw();
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
    
    protected void load() {
//        Window.alert("Worksheet loaded from '"+loadFile.getValue()+"'");
        Window.alert("Worksheet loaded from '<loadFilename>'");
        isSaved = false;
    }
    
    protected void print() {
        Window.alert("Worksheet printed!!!");
    }
    
    protected void save() {
        Window.alert("Worksheet saved!!!");
/*
        int                      i;
        TableDataRow             row;
        WorksheetDO              wDO;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        
        setFocus(null);
        
        if (worksheetItemTable.numRows() == 0) {
            Window.alert(consts.get("worksheetNotSaveEmpty"));
            return;
        }
        
        if (qcErrors.size() > 0) {
            Window.alert("Please fix errors on worksheet before saving");
            return;
        }
        
        window.setBusy(consts.get("saving"));

        wDO = manager.getWorksheet();
        wDO.setCreatedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        wDO.setSystemUserId(OpenELIS.security.getSystemUserId());
        wDO.setStatusId(statusWorking);
        wDO.setFormatId(testWorksheetDO.getFormatId());
        if (relatedWorksheetId.getFieldValue() != null)
            wDO.setRelatedWorksheetId(relatedWorksheetId.getFieldValue());
        
        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        for (i = 0; i < worksheetItemTable.numRows(); i++) {
            row = worksheetItemTable.getRow(i);
            
            wiDO = new WorksheetItemDO();
            wiDO.setPosition((Integer)row.cells.get(0).value);
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setId((Integer)row.key);
            waDO.setAccessionNumber(row.cells.get(1).value.toString());
            if (row.data instanceof ArrayList) {
                waDO.setQcId(((QcDO)((ArrayList<Object>)row.data).get(1)).getId());
            } else {
                waDO.setAnalysisId(((WorksheetCreationVO)row.data).getAnalysisId());
            }
            waDO.setWorksheetAnalysisId((Integer)row.cells.get(3).value);
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
            } catch (Exception anyE) {
                Window.alert("save(): " + anyE.getMessage());
                window.clearStatus();
            }
        }
        
        try {
            manager = manager.add();

            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("savingComplete"));
            
            isSaved = true;
            printButton.enable(false);
            insertQCWorksheetButton.enable(false);
            insertQCLookupButton.enable(false);
            lookupWorksheetButton.enable(false);
            removeRowButton.enable(false);
            worksheetItemTable.enable(false);
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("save(): " + e.getMessage());
            window.clearStatus();
        }
*/
    }

    protected void exit() {
        if (!isSaved) {
            if (worksheetExitConfirm == null) {
                worksheetExitConfirm = new Confirm(Confirm.Type.QUESTION, "",
                                                   consts.get("worksheetSaveExitConfirm"),
                                                   "Don't Exit", "Save & Exit", "Exit Without Saving");
                worksheetExitConfirm.addSelectionHandler(new SelectionHandler<Integer>(){
                    public void onSelection(SelectionEvent<Integer> event) {
                        switch(event.getSelectedItem().intValue()) {
                            case 1:
                                save();
                            case 2:
                                openLookupWindow();
                                break;
                        }
                    }
                });
            }
            
            worksheetExitConfirm.show();
        } else {
            openLookupWindow();
        }
    }

    private void openWorksheetLookup() {
        ScreenWindow modal;
        
        try {
            if (wLookupScreen == null) {
                final WorksheetCompletionScreen wcs = this;
                wLookupScreen = new WorksheetLookupScreen();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreen.Action>() {
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
            modal.setContent(wLookupScreen);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private int getNextTempId() {
       return --tempId;
   }
}