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
package org.openelis.modules.worksheetBuilder.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.meta.WorksheetMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.report.client.WorksheetLabelReportScreen;
import org.openelis.modules.report.client.WorksheetPrintReportScreen;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample1.client.SelectionEvent;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetManagerModifiedEvent;
import org.openelis.modules.worksheet1.client.WorksheetNotesTabUI;
import org.openelis.modules.worksheet1.client.WorksheetReagentTabUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

public class WorksheetBuilderScreenUI extends Screen {

    @UiTemplate("WorksheetBuilder.ui.xml")
    interface WorksheetBuilderUiBinder extends UiBinder<Widget, WorksheetBuilderScreenUI> {
    };
    
    private static WorksheetBuilderUiBinder               uiBinder   = GWT.create(WorksheetBuilderUiBinder.class);

    private Integer                                       origStatusId;
    private ModulePermission                              userPermission;
    private ScreenNavigator<IdNameVO>                     nav;
    private String                                        selectedUid;
    private WorksheetLabelReportScreen                    worksheetLabelReportScreen;
    private WorksheetManager1                             manager;
    private WorksheetPrintReportScreen                    worksheetPrintReportScreen;

    @UiField
    protected AutoComplete                                instrumentName, systemUserId;
    @UiField
    protected Calendar                                    createdDate;
    @UiField
    protected Button                                      query, previous, next,
                                                          add, update, commit, abort,
                                                          lookupWorksheetButton,
                                                          loadResults, optionsButton,
                                                          checkAllAnalytes, uncheckAllAnalytes;
    @UiField
    protected Dropdown<Integer>                           formatId, statusId;
    @UiField
    protected Menu                                        optionsMenu;
    @UiField
    protected MenuItem                                    printLabels, printWorksheet,
                                                          worksheetHistory;
    @UiField
    protected TabLayoutPanel                              tabPanel;
    @UiField
    protected Table                                       analyteTable, atozTable;
    @UiField
    protected TextBox<Integer>                            relatedWorksheetId, worksheetId;
    @UiField
    protected TextBox<String>                             description;
    @UiField(provided = true)
    protected WorksheetItemTabUI                          worksheetItemTab;
    @UiField(provided = true)
    protected WorksheetReagentTabUI                       reagentTab;
    @UiField(provided = true)
    protected WorksheetNotesTabUI                         notesTab;

    protected boolean                                     updateWarningShown;
    protected ArrayList<Integer>                          formatIds;
    protected AsyncCallbackUI<AnalysisResultManager>      fetchAnalytesForDisplayCall;
    protected AsyncCallbackUI<ArrayList<IdNameVO>>        queryCall;
    protected AsyncCallbackUI<ArrayList<QcAnalyteViewDO>> fetchQcAnalytesCall;
    protected AsyncCallbackUI<ArrayList<ResultViewDO>>    fetchAnalytesCall;
    protected AsyncCallbackUI<WorksheetManager1>          addCall, fetchByIdCall,
                                                          fetchForUpdateCall, fetchRelatedByIdCall,
                                                          unlockCall, updateCall;
    protected Confirm                                     worksheetSaveConfirm,
                                                          worksheetExitConfirm;
    protected EditNoteLookupUI                            failedNoteLookup;
    protected HashMap<Integer, ResultViewDO>              modifiedResults;
    protected HashMap<Integer, TestAnalyteViewDO>         addedAnalytes;
    protected HashMap<String, ArrayList<Object>>          analytesMap;
    protected WorksheetLookupScreenUI                     wLookupScreen;
    protected WorksheetManager1.Load                      elements[] = {WorksheetManager1.Load.DETAIL,
                                                                        WorksheetManager1.Load.REAGENT,
                                                                        WorksheetManager1.Load.NOTE,
                                                                        WorksheetManager1.Load.ATTACHMENT};

    public WorksheetBuilderScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Builder Screen"));

        worksheetItemTab = new WorksheetItemTabUI(this);
        reagentTab = new WorksheetReagentTabUI(this);
        notesTab = new WorksheetNotesTabUI(this);
        initWidget(uiBinder.createAndBindUi(this));
        
        manager = null;
        formatIds = new ArrayList<Integer>();
        analytesMap = new HashMap<String, ArrayList<Object>>();
        modifiedResults = new HashMap<Integer, ResultViewDO>();
        addedAnalytes = new HashMap<Integer, TestAnalyteViewDO>();
        updateWarningShown = false;
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() throws Exception {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.setPressed(true);
                    query.lock();
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.setPressed(true);
                    add.lock();
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.setPressed(true);
                    update.lock();
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);


        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                printWorksheet.setEnabled(isState(DISPLAY));
                printLabels.setEnabled(isState(DISPLAY));
                worksheetHistory.setEnabled(isState(DISPLAY));
            }
        });

        printWorksheet.addCommand(new Command() {
            @Override
            public void execute() {
                printWorksheet();
            }
        });

        printLabels.addCommand(new Command() {
            @Override
            public void execute() {
                printLabels();
            }
        });

        worksheetHistory.addCommand(new Command() {
            @Override
            public void execute() {
                worksheetHistory();
            }
        });

        addScreenHandler(worksheetId, WorksheetBuilderMeta.getWorksheetId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetId.setValue(getWorksheetId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setWorksheetId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetId.setEnabled(isState(QUERY));
                worksheetId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? statusId : description;
            }
        });

        addScreenHandler(statusId, WorksheetBuilderMeta.getWorksheetStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(getStatusId());
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                statusId.setEnabled(isState(QUERY, UPDATE));
                statusId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? systemUserId : worksheetId;
            }
        });

        addScreenHandler(systemUserId, WorksheetBuilderMeta.getWorksheetSystemUserId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setSystemUserSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getSystemUserFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                systemUserId.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
                systemUserId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? formatId : statusId;
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                
                setBusy();
                try {
                    model = new ArrayList<Item<Integer>>();
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.toString());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        addScreenHandler(formatId, WorksheetBuilderMeta.getWorksheetFormatId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                formatId.setValue(getFormatId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setFormatId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                formatId.setEnabled(isState(QUERY) || (isState(ADD) && canEdit()));
                formatId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? relatedWorksheetId : systemUserId;
            }
        });

        addScreenHandler(relatedWorksheetId, WorksheetBuilderMeta.getWorksheetRelatedWorksheetId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                relatedWorksheetId.setValue(getRelatedWorksheetId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setRelatedWorksheetId(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent event) {
                relatedWorksheetId.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
                relatedWorksheetId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? instrumentName : formatId;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                lookupWorksheetButton.setEnabled(isState(DISPLAY) || (isState(ADD, UPDATE) && canEdit()));
            }
        });

        addScreenHandler(instrumentName, WorksheetBuilderMeta.getInstrumentName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setInstrumentSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getInstrumentFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                instrumentName.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
                instrumentName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? createdDate : relatedWorksheetId;
            }
        });

        instrumentName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<InstrumentViewDO> matches;
                Item<Integer> row;

                setBusy();
                try {
                    model = new ArrayList<Item<Integer>>();
                    matches = InstrumentService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (InstrumentViewDO iVDO : matches) {
                        row = new Item<Integer>(5);
                        row.setKey(iVDO.getId());
                        row.setCell(0, iVDO.getName());
                        row.setCell(1, iVDO.getDescription());
                        row.setCell(2, DictionaryCache.getById(iVDO.getTypeId()).getEntry());
                        row.setCell(3, iVDO.getLocation());
                        row.setData(iVDO);
                        
                        model.add(row);
                    }
                    instrumentName.showAutoMatches(model);
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            } 
        });

        addScreenHandler(createdDate, WorksheetBuilderMeta.getWorksheetCreatedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                createdDate.setValue(getCreatedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setCreatedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                createdDate.setEnabled(isState(QUERY));
                createdDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : instrumentName;
            }
        });

        addScreenHandler(description, WorksheetBuilderMeta.getWorksheetDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
                description.setQueryMode(isState(QUERY));

            }

            public Widget onTab(boolean forward) {
                return forward ? worksheetId : createdDate;
            }
        });

        //
        // tabs
        //
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());
        
        addScreenHandler(worksheetItemTab, "worksheetItemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetItemTab.setState(event.getState());
            }
        });

        addScreenHandler(reagentTab, "reagentTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                reagentTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                reagentTab.setState(event.getState());
            }
        });

        addScreenHandler(notesTab, "notesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                notesTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, loadResults) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            ArrayList<IdNameVO> addedList;
                            
                            clearStatus();
                            if (nav.getQuery().getPage() == 0) {
                                setQueryResult(result);
                                fetchById(result.get(0).getId());
                                select(0);
                            } else {
                                addedList = getQueryResult();
                                addedList.addAll(result);
                                setQueryResult(addedList);
                                select(atozTable.getModel().size() - result.size());
                                atozTable.scrollToVisible(atozTable.getModel().size() - 1);
                            }
                        }
                        
                        public void notFound() {
                            setQueryResult(null);
                            setState(DEFAULT);
                            setDone(Messages.get().gen_noRecordsFound());
                        }
                        
                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }
    
                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Worksheet call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                query.setRowsPerPage(20);
                WorksheetService1.get().query(query, queryCall);
            }

            public boolean fetch(IdNameVO entry) {
                fetchById((entry == null) ? null : entry.getId());
                return true;
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getId(), entry.getName()));
                }
                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nav.enable(isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
                analyteTable.setEnabled(true);
                analytesMap.clear();
                modifiedResults.clear();
                addedAnalytes.clear();
                checkAllAnalytes.setEnabled(false);
                uncheckAllAnalytes.setEnabled(false);
            }
        });
        
        //
        // right hand analyte table panel
        //
        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                analyteTable.setModel(null);
            }
        });
        
        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                //  only the reportable field can be edited
                //
                if (!isState(ADD, UPDATE) || event.getCol() != 0 ||
                    analyteTable.getRowAt(event.getRow()).getData() instanceof QcAnalyteViewDO) {
                    event.cancel();
                } else if (isState(UPDATE) && !updateWarningShown) {
                    Window.alert(Messages.get().worksheet_builderUpdateWarning());
                    updateWarningShown = true;
                    event.cancel();
                }
            }
        });
        
        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                Row row;
                ResultViewDO data;

                r = event.getRow();
                c = event.getCol();
                
                row = analyteTable.getRowAt(r);
                val = analyteTable.getValueAt(r,c);

                data = row.getData();
                switch (c) {
                    case 0:
                        data.setIsReportable((String)val);
                        if (!modifiedResults.containsKey(data.getId()))
                            modifiedResults.put(data.getId(), data);
                        else
                            modifiedResults.remove(data.getId());
                        break;
                }
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                } else {
                    tabPanel.close();
                }
            }
        });
        
        try {
            CategoryCache.getBySystemNames("analysis_status", "instrument_type",
                                           "type_of_sample", "test_worksheet_format",
                                           "test_worksheet_item_type", "unit_of_measure",
                                           "worksheet_status");
        } catch (Exception e) {
            throw new Exception("WorksheetBuilderScreen: missing dictionary entry; " + e.getMessage());
        }
        
        //
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load worksheet format dropdown model
        //
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                showAnalytes(event.getUid());
            }
        });
        
        bus.addHandler(WorksheetManagerModifiedEvent.getType(), new WorksheetManagerModifiedEvent.Handler() {
            public void onManagerModified(WorksheetManagerModifiedEvent event) {
                manager = event.getManager();
                setData();
                fireDataChange();
            }
        });
        
        bus.addHandler(FormatSetEnabledEvent.getType(), new FormatSetEnabledEvent.Handler() {
            public void onFormatSetEnabled(FormatSetEnabledEvent event) {
                formatId.setEnabled(event.getEnable());
            }
        });

        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("WorksheetBuilder Screen Opened");
    }
    
    /*
     * basic button methods
     */
    @SuppressWarnings("unused")
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        setData();
        setState(QUERY);
        fireDataChange();
        worksheetId.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @SuppressWarnings("unused")
    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @SuppressWarnings("unused")
    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @SuppressWarnings("unused")
    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();
        
        if (addCall == null) {
            addCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    manager = result;
                    setData();
                    setState(ADD);
                    fireDataChange();
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }
                
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
    
        WorksheetService1.get().getInstance(addCall);
    }

    @SuppressWarnings("unused")
    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    manager = result;
                    origStatusId = manager.getWorksheet().getStatusId();
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                    statusId.setFocus(true);
                }
                
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                
                public void finish() {
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().fetchForUpdate(manager.getWorksheet().getId(), fetchForUpdateCall);
    }

    @SuppressWarnings("unused")
    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Integer currStatusId;
        Validation validation;
        
        finishEditing();
        clearErrors();
        
        validation = validate();
        
        switch (validation.getStatus()) {
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
            case WARNINGS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
                
            case ADD:
                commitUpdate(WorksheetManager1.ANALYSIS_UPDATE.UPDATE);
                break;
                
            case UPDATE:
                currStatusId = manager.getWorksheet().getStatusId();
                if (Constants.dictionary().WORKSHEET_FAILED.equals(currStatusId) &&
                    !Constants.dictionary().WORKSHEET_FAILED.equals(origStatusId)) {
                    openFailedRunNote();
                    return;
                } else if (Constants.dictionary().WORKSHEET_VOID.equals(currStatusId) &&
                           !Constants.dictionary().WORKSHEET_VOID.equals(origStatusId)) {
                    commitUpdate(WorksheetManager1.ANALYSIS_UPDATE.VOID);
                } else {
                    commitUpdate(WorksheetManager1.ANALYSIS_UPDATE.UPDATE);
                }
                break;
        }
        
        updateWarningShown = false;
    }
    
    private void commitQuery() {
        Query query;
        
        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    private void commitUpdate(WorksheetManager1.ANALYSIS_UPDATE flag) {
        finishEditing();
        if (isState(ADD))
            setBusy(Messages.get().adding());
        else
            setBusy(Messages.get().updating());

        manager.setModifiedResults(new ArrayList<ResultViewDO>(modifiedResults.values()));
        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    State currState;
                    
                    currState = state;
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    if (currState == ADD)
                        setDone(Messages.get().gen_addingComplete());
                    else
                        setDone(Messages.get().gen_updatingComplete());
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }
                
                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
        
        WorksheetService1.get().update(manager, flag, updateCall);
    }
    
    @SuppressWarnings("unused")
    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        switch (state) {
            case QUERY:
                fetchById(null);
                setDone(Messages.get().gen_queryAborted());
                break;
                
            case ADD:
                abortAdd();
                break;
                
            case UPDATE:
                abortUpdate();
                break;
        }

        updateWarningShown = false;
    }

    private void abortAdd() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        //
        // reset worksheet format dropdown model
        //
        formatIds.clear();
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);
        
        fetchById(null);
        
        setDone(Messages.get().gen_addAborted());
    }
    
    private void abortUpdate() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        //
        // reset worksheet format dropdown model
        //
        formatIds.clear();
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);

        if (unlockCall == null) {
            unlockCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    setDone(Messages.get().gen_updateAborted());
                }
                
                public void failure(Throwable  e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().unlock(manager.getWorksheet().getId(), elements,
                                       unlockCall);
    }

    protected void printWorksheet() {
        ScreenWindow modal;

        try {
            if (worksheetPrintReportScreen == null) {
                worksheetPrintReportScreen = new WorksheetPrintReportScreen();

                /*
                 * we need to make sure that the value of WORKSHEET_ID gets set
                 * the first time the screen is brought up
                 */
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        worksheetPrintReportScreen.setFieldValue("WORKSHEET_ID",
                                                                 manager.getWorksheet().getId());
                    }
                });
            } else {
                worksheetPrintReportScreen.reset();
                worksheetPrintReportScreen.setFieldValue("WORKSHEET_ID", manager.getWorksheet()
                                                                                .getId());
            }
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(Messages.get().print());
            modal.setContent(worksheetPrintReportScreen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void printLabels() {
        ScreenWindow modal;

        try {
            if (worksheetLabelReportScreen == null) {
                worksheetLabelReportScreen = new WorksheetLabelReportScreen();

                /*
                 * we need to make sure that the value of WORKSHEET_ID gets set 
                 * the first time the screen is brought up
                 */
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        worksheetLabelReportScreen.setFieldValue("WORKSHEET_ID",
                                                                 manager.getWorksheet().getId());
                    }
                });
            } else {
                worksheetLabelReportScreen.reset();
                worksheetLabelReportScreen.setFieldValue("WORKSHEET_ID", manager.getWorksheet()
                                                                                .getId());
            }
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(Messages.get().print());
            modal.setContent(worksheetLabelReportScreen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void worksheetHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getWorksheet().getId(), manager.getWorksheet()
                                                                   .getId()
                                                                   .toString());
        HistoryScreen.showHistory(Messages.get().worksheet_worksheetHistory(),
                                  Constants.table().WORKSHEET,
                                  hist);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        ArrayList<SystemUserVO> userList;
        String loginName;
        StringBuffer userIds;
        
        fields = super.getQueryFields();
        for (QueryData field : fields) {
            if (WorksheetBuilderMeta.getWorksheetSystemUserId().equals(field.getKey())) {
                //
                // Since we cannot join with the security database to link system user's
                // login name to the query, we need to lookup the matching id(s) from the
                // UserCache to input into the query
                //
                loginName = field.getQuery();
                if (!"".equals(loginName) && !"*".equals(loginName) && !"!=".equals(loginName)) {
                    field.setType(QueryData.Type.INTEGER);
                    if ("=".equals(loginName)) {
                        field.setQuery("-1");
                    } else {
                        userIds = new StringBuffer();
                        try {
                            userList = UserCacheService.get().getEmployees(loginName);
                            if (userList.size() == 0) {
                                field.setQuery("-1");
                            } else {
                                for (SystemUserVO userVO : userList) {
                                    if (userIds.length() > 0)
                                        userIds.append(" | ");
                                    userIds.append(userVO.getId());
                                }
                                field.setQuery(userIds.toString());
                            }
                        } catch (Exception anyE) {
                            Window.alert(anyE.getMessage());
                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                        }
                    }
                }
                break;
            }
        }
            
        return fields;
    }

    protected void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<WorksheetManager1>() {
                    public void success(WorksheetManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }
                    
                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }
                
                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + "; " + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                    
                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            WorksheetService1.get().fetchById(id, elements, fetchByIdCall);
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("lookupWorksheetButton")
    protected void lookupWorksheet(ClickEvent event) {
        ModalWindow modal;
        Query query;
        QueryData field;

        if (isState(DISPLAY)) {
            if (manager.getWorksheet().getRelatedWorksheetId() != null) {
                field = new QueryData();
                field.setKey(WorksheetMeta.getId());
                field.setQuery(manager.getWorksheet().getId() + "|" + manager.getWorksheet().getRelatedWorksheetId());
                field.setType(QueryData.Type.INTEGER);
                
                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        } else {
            try {
                if (wLookupScreen == null) {
                    wLookupScreen = new WorksheetLookupScreenUI();
                    wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreenUI.Action>() {
                        @SuppressWarnings("unchecked")
                        public void onAction(ActionEvent<WorksheetLookupScreenUI.Action> event) {
                            Item<Integer> row;
                            WorksheetViewDO wVDO;
    
                            if (event.getAction() == WorksheetLookupScreenUI.Action.SELECT) {
                                row = (Item<Integer>)event.getData();
                                if (row != null) {
                                    wVDO = (WorksheetViewDO)row.getData();
                                    setRelatedWorksheetId(wVDO.getId(), false);
                                    relatedWorksheetId.setValue(getRelatedWorksheetId());
                                }
                            }
                        }
                    });
                }
                
                modal = new ModalWindow();
                modal.setName(Messages.get().worksheet_worksheetLookup());
                modal.setContent(wLookupScreen);
                modal.setSize("636px", "385px");
                wLookupScreen.setWindow(modal);
            } catch (Exception e) {
                Window.alert("error: " + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }
    }

    protected void openFailedRunNote() {
        ModalWindow modal;
        NoteViewDO note;

        if (failedNoteLookup == null) {
            failedNoteLookup = new EditNoteLookupUI() {
                public void ok() {
                    if (DataBaseUtil.isEmpty(failedNoteLookup.getText())) {
                        manager.note.removeEditing();
                    } else {
                        setNoteFields(manager.note.getEditing(), failedNoteLookup.getSubject(),
                                      failedNoteLookup.getText());
                        commitUpdate(WorksheetManager1.ANALYSIS_UPDATE.FAILED_RUN);
                        updateWarningShown = false;
                    }
                }
                
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(failedNoteLookup);

        note = manager.note.getEditing();
        failedNoteLookup.setWindow(modal);
        failedNoteLookup.setSubject(note.getSubject());
        failedNoteLookup.setText(note.getText());
        failedNoteLookup.setHasSubject("N".equals(note.getIsExternal()));
    }

    private void setNoteFields(NoteViewDO note, String subject, String text) {
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
    }

    @SuppressWarnings("unchecked")
    private void showAnalytes(String uid) {
        ArrayList<Object> analyteList;
        SectionPermission perm;
        WorksheetAnalysisViewDO data;
        
        selectedUid = uid;
        if (selectedUid != null) {
            analyteList = analytesMap.get(selectedUid);
            if (analyteList != null) {
                analyteTable.setModel((ArrayList<Row>)analyteList.get(1));
                if (!isState(ADD, UPDATE) || Boolean.FALSE.equals(analyteList.get(0))) {
                    checkAllAnalytes.setEnabled(false);
                    uncheckAllAnalytes.setEnabled(false);
                } else {
                    checkAllAnalytes.setEnabled(true);
                    uncheckAllAnalytes.setEnabled(true);
                }
                clearStatus();
            } else {
                data = (WorksheetAnalysisViewDO)manager.getObject(selectedUid);
                if (data.getAnalysisId() != null) {
                    perm = UserCache.getPermission().getSection(data.getSectionName());
                    if (isState(ADD, UPDATE) && perm.hasCompletePermission()) {
                        if (fetchAnalytesCall == null) {
                            fetchAnalytesCall = new AsyncCallbackUI<ArrayList<ResultViewDO>>() {
                                public void success(ArrayList<ResultViewDO> analytes) {
                                    ArrayList<Object> analyteList;
                                    ArrayList<Row> model;
                                    Row row;
        
                                    analyteList = new ArrayList<Object>();
                                    model = new ArrayList<Row>();
                                    for (DataObject ana : analytes) {
                                        row = new Row(2);
                                        row.setCell(0, ((ResultViewDO)ana).getIsReportable());
                                        row.setCell(1, ((ResultViewDO)ana).getAnalyte());
                                        row.setData(ana);
                                        model.add(row);
                                    }
                                    analyteTable.setModel(model);
                                    analyteList.add(Boolean.TRUE);
                                    analyteList.add(model);
                                    analytesMap.put(selectedUid, analyteList);
                                    checkAllAnalytes.setEnabled(true);
                                    uncheckAllAnalytes.setEnabled(true);
                                    clearStatus();
                                }
                                
                                public void notFound() {
                                    analyteTable.setModel(null);
                                    setDone(Messages.get().worksheet_noAnalytesFoundForRow());
                                }
                    
                                public void failure(Throwable e) {
                                    analyteTable.setModel(null);
                                    Window.alert("Error: WorksheetBuilder call showAnalytes failed; "+e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                }
                            };
                        }

                        WorksheetService1.get().fetchAnalytesByAnalysis(data.getAnalysisId(),
                                                                        data.getTestId(),
                                                                        fetchAnalytesCall);
                    } else {
                        if (fetchAnalytesForDisplayCall == null) {
                            fetchAnalytesForDisplayCall = new AsyncCallbackUI<AnalysisResultManager>() {
                                public void success(AnalysisResultManager arMan) {
                                    int i;
                                    ArrayList<Object> analyteList;
                                    ArrayList<Row> model;
                                    ResultViewDO result;
                                    Row row;
        
                                    analyteList = new ArrayList<Object>();
                                    model = new ArrayList<Row>();
                                    for (i = 0; i < arMan.rowCount(); i++) {
                                        result = arMan.getResultAt(i, 0);
                                        row = new Row(2);
                                        row.setCell(0, result.getIsReportable());
                                        row.setCell(1, result.getAnalyte());
                                        row.setData(result);
                                        model.add(row);
                                    }
                                    analyteTable.setModel(model);
                                    analyteList.add(Boolean.FALSE);
                                    analyteList.add(model);
                                    analytesMap.put(selectedUid, analyteList);
                                    clearStatus();
                                }
                                
                                public void notFound() {
                                    analyteTable.setModel(null);
                                    setDone(Messages.get().worksheet_noAnalytesFoundForRow());
                                }
                                
                                public void failure(Throwable e) {
                                    analyteTable.setModel(null);
                                    Window.alert("Error: WorksheetBuilder call showAnalytes failed; "+e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                }
                            };
                        }
                        
                        ResultService.get().fetchByAnalysisIdForDisplay(data.getAnalysisId(),
                                                                        fetchAnalytesForDisplayCall);
                    }
                } else if (data.getQcLotId() != null) {
                    if (fetchQcAnalytesCall == null) {
                        fetchQcAnalytesCall = new AsyncCallbackUI<ArrayList<QcAnalyteViewDO>>() {
                            public void success(ArrayList<QcAnalyteViewDO> analytes) {
                                ArrayList<Object> analyteList;
                                ArrayList<Row> model;
                                Row row;
        
                                analyteList = new ArrayList<Object>();
                                model = new ArrayList<Row>();
                                for (QcAnalyteViewDO analyte : analytes) {
                                    row = new Row(2);
                                    row.setCell(0, "N");
                                    row.setCell(1, analyte.getAnalyteName());
                                    row.setData(analyte);
                                    model.add(row);
                                }
                                analyteTable.setModel(model);
                                analyteList.add(Boolean.FALSE);
                                analyteList.add(model);
                                analytesMap.put(selectedUid, analyteList);
                                clearStatus();
                            }
                            
                            public void notFound() {
                                analyteTable.setModel(null);
                                setDone(Messages.get().worksheet_noAnalytesFoundForRow());
                            }
                            
                            public void failure(Throwable e) {
                                analyteTable.setModel(null);
                                Window.alert("Error: WorksheetBuilder call showAnalytes failed; "+e.getMessage());
                                logger.log(Level.SEVERE, e.getMessage(), e);
                            }
                        };
                    }

                    QcService.get().fetchAnalytesByLotId(data.getQcLotId(), fetchQcAnalytesCall);
                }
                
                checkAllAnalytes.setEnabled(false);
                uncheckAllAnalytes.setEnabled(false);
            }
        } else {
            analyteTable.setModel(null);
            checkAllAnalytes.setEnabled(false);
            uncheckAllAnalytes.setEnabled(false);
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("checkAllAnalytes")
    protected void checkAllAnalytes(ClickEvent event) {
        checkAnalytes("Y");
    }

    @SuppressWarnings("unused")
    @UiHandler("uncheckAllAnalytes")
    protected void uncheckAllAnalytes(ClickEvent event) {
        checkAnalytes("N");
    }

    protected void checkAnalytes(String reportable) {
        int i;
        ResultViewDO data;
        Row row;
        
        if (isState(UPDATE) && !updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            updateWarningShown = true;
            return;
        }
        
        for (i = 0; i < analyteTable.getRowCount(); i++) {
            row = analyteTable.getRowAt(i);
            data = row.getData();
            if (!reportable.equals(data.getIsReportable())) {
                data.setIsReportable(reportable);
                analyteTable.setValueAt(i, 0, reportable);
                if (!modifiedResults.containsKey(data.getId()))
                    modifiedResults.put(data.getId(), data);
                else
                    modifiedResults.remove(data.getId());
            }
        }
    }

    /**
     * If the status of the worksheet showing on the screen is changed from
     * Done to something else, on changing the state, the status stays
     * Done and the widgets in the tabs stay disabled. Also, if the status
     * changes from something else to Done, the widgets are not disabled.
     * This is because the data in the tabs is set in their handlers of
     * DataChangeEvent which is fired after StateChangeEvent and the handlers of
     * the latter in the widgets are responsible for enabling or disabling the
     * widgets. That is why we need to set the data in the tabs before changing
     * the state.
     */
    private void setData() {
        worksheetItemTab.setData(manager);
        reagentTab.setData(manager);
        notesTab.setData(manager);
    }
    
    private boolean canEdit() {
        return (manager != null && Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet().getStatusId()));
    }

    private Integer getWorksheetId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getId();
    }

    private void setWorksheetId(Integer worksheetId) {
        manager.getWorksheet().setId(worksheetId);
    }

    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getStatusId();
    }

    private void setStatusId(Integer statusId) {
        manager.getWorksheet().setStatusId(statusId);
    }

    private void setSystemUserSelection() {
        if (manager == null)
            systemUserId.setValue(null, null);
        else
            systemUserId.setValue(getSystemUserId(), getSystemUser());
    }

    private void getSystemUserFromSelection() {
        AutoCompleteValue row;
        
        row = systemUserId.getValue();
        if (row == null || row.getId() == null) {
            setSystemUserId(null);
            setSystemUser(null);
        } else {
            setSystemUserId(row.getId());
            setSystemUser(row.getDisplay());
        }
    }
    
    private void setSystemUserId(Integer id) {
        manager.getWorksheet().setSystemUserId(id);
    }
    
    private Integer getSystemUserId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getSystemUserId();
    }
    
    private void setSystemUser(String name) {
        manager.getWorksheet().setSystemUser(name);
    }
    
    private String getSystemUser() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getSystemUser();
    }
    
    private Integer getFormatId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getFormatId();
    }

    private void setFormatId(Integer formatId) {
        manager.getWorksheet().setFormatId(formatId);
    }
    
    private Integer getRelatedWorksheetId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getRelatedWorksheetId();
    }

    private void setRelatedWorksheetId(Integer id, boolean validate) {
        if (validate && id != null) {
            setBusy(Messages.get().gen_fetching());
            if (fetchRelatedByIdCall == null) {
                fetchRelatedByIdCall = new AsyncCallbackUI<WorksheetManager1>() {
                    public void success(WorksheetManager1 result) {
                        manager.getWorksheet().setRelatedWorksheetId(result.getWorksheet().getId());
                    }
                    
                    public void notFound() {
                        Window.alert(Messages.get().gen_noRecordsFound());
                    }
                    
                    public void failure(Throwable e) {
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        relatedWorksheetId.setValue(manager.getWorksheet().getRelatedWorksheetId());
                        clearStatus();
                    }
                };
            }

            WorksheetService1.get().fetchById(id, elements, fetchRelatedByIdCall);
        } else {
            manager.getWorksheet().setRelatedWorksheetId(id);
        }
    }

    private void setInstrumentSelection() {
        if (manager == null)
            instrumentName.setValue(null, null);
        else
            instrumentName.setValue(getInstrumentId(), getInstrumentName());
    }

    private void getInstrumentFromSelection() {
        AutoCompleteValue row;
        InstrumentViewDO data;
        
        row = instrumentName.getValue();
        if (row == null || row.getId() == null) {
            setInstrumentId(null);
            setInstrumentName(null);
        } else {
            data = (InstrumentViewDO)row.getData();
            setInstrumentId(data.getId());
            setInstrumentName(data.getName());
        }
    }
    
    private void setInstrumentId(Integer id) {
        manager.getWorksheet().setInstrumentId(id);
    }
    
    private Integer getInstrumentId() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getInstrumentId();
    }
    
    private void setInstrumentName(String name) {
        manager.getWorksheet().setInstrumentName(name);
    }
    
    private String getInstrumentName() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getInstrumentName();
    }
    
    private Datetime getCreatedDate() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getCreatedDate();
    }

    private void setCreatedDate(Datetime createdDate) {
        manager.getWorksheet().setCreatedDate(createdDate);
    }
    
    private String getDescription() {
        if (manager == null)
            return null;
        return manager.getWorksheet().getDescription();
    }

    private void setDescription(String description) {
        manager.getWorksheet().setDescription(description);
    }
}