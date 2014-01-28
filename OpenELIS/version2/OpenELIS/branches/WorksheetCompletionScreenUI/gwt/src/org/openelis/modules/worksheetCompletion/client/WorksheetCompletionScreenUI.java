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

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.*;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample1.client.SelectionEvent;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetNotesTabUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
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
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
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

public class WorksheetCompletionScreenUI extends Screen {

    @UiTemplate("WorksheetCompletion.ui.xml")
    interface WorksheetCompletionUiBinder extends UiBinder<Widget, WorksheetCompletionScreenUI> {
    };
    
    private static WorksheetCompletionUiBinder          uiBinder = GWT.create(WorksheetCompletionUiBinder.class);

    private ModulePermission                            userPermission;
    private ScreenNavigator<IdNameVO>                   nav;
    private WorksheetManager1                           manager;

    @UiField
    protected AutoComplete                              instrumentName, systemUserId;
    @UiField
    protected Calendar                                  createdDate;
    @UiField
    protected Button                                    query, previous, next, update,
                                                        commit, abort, lookupWorksheetButton,
                                                        atozNext, atozPrev, optionsButton;
    @UiField
    protected Dropdown<Integer>                         formatId, statusId;
    @UiField
    protected Menu                                      optionsMenu;
    @UiField
    protected MenuItem                                  worksheetHistory;
    @UiField
    protected TabLayoutPanel                            tabPanel;
    @UiField
    protected Table                                     atozTable;
    @UiField
    protected TextBox<Integer>                          relatedWorksheetId, worksheetId;
    @UiField
    protected TextBox<String>                           description;
    @UiField(provided = true)
    protected WorksheetItemTabUI                        worksheetItemTab;
    @UiField(provided = true)
    protected WorksheetNotesTabUI                       notesTab;
    
    protected ArrayList<Integer>                        formatIds;
    protected Confirm                                   worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer, ResultViewDO>            modifiedResults;
    protected HashMap<Integer, TestAnalyteViewDO>       addedAnalytes;
    protected HashMap<String, ArrayList<Object>>        analytesMap;
    protected WorksheetLookupScreenUI                   wLookupScreen;
    
    public WorksheetCompletionScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Completion Screen"));

        worksheetItemTab = new WorksheetItemTabUI(this, bus);
        notesTab = new WorksheetNotesTabUI(this, bus);
        initWidget(uiBinder.createAndBindUi(this));
        
        manager = null;
        formatIds = new ArrayList<Integer>();
        analytesMap = new HashMap<String, ArrayList<Object>>();
        modifiedResults = new HashMap<Integer, ResultViewDO>();
        addedAnalytes = new HashMap<Integer, TestAnalyteViewDO>();
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
                worksheetHistory.setEnabled(isState(DISPLAY));
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
                systemUserId.setEnabled(isState(QUERY));
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
                
                window.setBusy();
                try {
                    model = new ArrayList<Item<Integer>>();
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
                window.clearStatus();
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
                formatId.setEnabled(isState(QUERY));
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
                setRelatedWorksheetId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                relatedWorksheetId.setEnabled(isState(QUERY) || (isState(UPDATE) && canEdit()));
                relatedWorksheetId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? instrumentName : formatId;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                lookupWorksheetButton.setEnabled(isState(UPDATE) && canEdit());
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
                instrumentName.setEnabled(isState(QUERY) || (isState(UPDATE) && canEdit()));
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

                window.setBusy();
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
                    e.printStackTrace();
                    Window.alert(e.getMessage());                     
                }
                window.clearStatus();
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
                description.setEnabled(isState(QUERY) || (isState(UPDATE) && canEdit()));
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
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(21);
                WorksheetService1.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(Messages.get().gen_noRecordsFound());
                            setState(DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(Messages.get().gen_noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Worksheet call query failed; " +
                                         error.getMessage());
                            window.setError(Messages.get().gen_queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById((entry == null) ? null : entry.getId());
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
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(UPDATE)) {
                    event.cancel();
                    window.setError(Messages.get().gen_mustCommitOrAbort());
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
        window.setDone(Messages.get().gen_enterFieldsToQuery());
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
    @UiHandler("update")
    protected void update(ClickEvent event) {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = WorksheetService1.get().fetchForUpdate(manager.getWorksheet().getId());

            setData();
            setState(UPDATE);
            fireDataChange();
            statusId.setFocus(true);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        window.clearStatus();
    }

    @SuppressWarnings("unused")
    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;
        
        finishEditing();
        clearErrors();
        
        validation = validate();
        
        if (validation.getStatus() != VALID) {
            window.setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
                
            case UPDATE:
                commitUpdate();
                break;
        }
    }
    
    private void commitQuery() {
        Query query;
        
        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    private void commitUpdate() {
        finishEditing();
        window.setBusy(Messages.get().updating());

        manager.setModifiedResults(new ArrayList<ResultViewDO>(modifiedResults.values()));
        try {
            manager = WorksheetService1.get().update(manager);
            setData();
            setState(DISPLAY);
            fireDataChange();
            window.setDone(Messages.get().gen_updatingComplete());
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("commitUpdate(): " + e.getMessage());
            window.clearStatus();
        }
    }
    
    @SuppressWarnings("unused")
    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().gen_cancelChanges());

        switch (state) {
            case QUERY:
                fetchById(null);
                window.setDone(Messages.get().gen_queryAborted());
                break;
                
            case UPDATE:
                abortUpdate();
                break;
                
            default:
                window.clearStatus();
        }
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

        try {
            manager = WorksheetService1.get().unlock(manager.getWorksheet().getId(), Load.DETAIL);
            setData();
            setState(DISPLAY);
            fireDataChange();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            fetchById(null);
        }
        window.setDone(Messages.get().gen_updateAborted());
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
        QueryData field;
        String loginName;
        StringBuffer userIds;
        
        fields = new ArrayList<QueryData>();
        for (String key : handlers.keySet()) {
            if (WorksheetBuilderMeta.getWorksheetSystemUserId().equals(key))
                continue;
            Object query = handlers.get(key).getQuery();
            if (query instanceof ArrayList<?>) {
                ArrayList<QueryData> qds = (ArrayList<QueryData>)query;
                fields.addAll(qds);
            } else if (query instanceof Object[]) {
                QueryData[] qds = (QueryData[])query;
                for (int i = 0; i < qds.length; i++ )
                    fields.add(qds[i]);
            } else if (query != null) {
                ((QueryData)query).setKey(key);
                fields.add((QueryData)query);
            }
        }

        //
        // Since we cannot join with the security database to link system user's
        // login name to the query, we need to lookup the matching id(s) from the
        // UserCache to input into the query
        //
        loginName = systemUserId.getDisplay();
        if (!"".equals(loginName) && !"*".equals(loginName) && !"!=".equals(loginName)) {
            field = new QueryData();
            field.setKey(WorksheetBuilderMeta.getWorksheetSystemUserId());
            field.setType(QueryData.Type.INTEGER);
            if ("=".equals(loginName)) {
                field.setQuery("-1");
                fields.add(field);
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
                    fields.add(field);
                } catch (Exception anyE) {
                    anyE.printStackTrace();
                    Window.alert(anyE.getMessage());
                }
            }
        }
        
        return fields;
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().gen_fetching());
            try {
                manager = WorksheetService1.get().fetchById(id, WorksheetManager1.Load.DETAIL,
                                                            WorksheetManager1.Load.NOTE);
                setData();
                setState(DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(Messages.get().gen_noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                return false;
            }

        }
        fireDataChange();
        window.clearStatus();

        return true;
    }

    @SuppressWarnings("unused")
    @UiHandler("lookupWorksheetButton")
    protected void openWorksheetLookup(ClickEvent event) {
        ModalWindow modal;
        
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
                                setRelatedWorksheetId(wVDO.getId());
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
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
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

    private void setRelatedWorksheetId(Integer relatedWorksheetId) {
        manager.getWorksheet().setRelatedWorksheetId(relatedWorksheetId);
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