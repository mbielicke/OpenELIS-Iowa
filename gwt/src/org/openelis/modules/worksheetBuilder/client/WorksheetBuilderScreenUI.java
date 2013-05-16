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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.Preferences;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.NotesTabUI;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet.client.WorksheetAnalysisSelectionScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreen;
import org.openelis.modules.worksheet.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet.client.WorksheetNotesTabUI;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.modules.worksheet.client.WorksheetService1;
import org.openelis.modules.worksheetBuilder.client.WorksheetBuilderLookupScreenUI.Action;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
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
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;
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
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public class WorksheetBuilderScreenUI extends Screen {

    @UiTemplate("WorksheetBuilder.ui.xml")
    interface WorksheetBuilderUiBinder extends UiBinder<Widget, WorksheetBuilderScreenUI> {
    };
    
    private static WorksheetBuilderUiBinder             uiBinder = GWT.create(WorksheetBuilderUiBinder.class);

    private int                                         tempId, qcStartIndex;
    private String                                      typeLastBothString, typeLastRunString,
                                                        typeLastSubsetString, typeRandString;
    private ModulePermission                            userPermission;
    private ScreenNavigator<IdVO>                       nav;
    private WorksheetBuilderScreenUI                    screen;
    private WorksheetManager1                           manager;

    @UiField
    protected AutoComplete                              instrumentName, systemUserId/*,
                                                        unitOfMeasureId*/;
    @UiField
    protected Calendar                                  createdDate;
    @UiField
    protected Button                                    query, previous, next, add,
                                                        update, commit, abort, lookupWorksheetButton,
                                                        atozNext, atozPrev;
    @UiField
    protected Dropdown<Integer>                         formatId, statusId;
    @UiField
    protected MenuItem                                  worksheetHistory;
    @UiField
    protected TabLayoutPanel                            tabPanel;
    @UiField
    protected Table                                     analyteTable, atozTable;
    @UiField
    protected TextBox<Integer>                          relatedWorksheetId, worksheetId;
    @UiField
    protected TextBox<String>                           description;
    @UiField(provided = true)
    protected WorksheetItemTabUI                        worksheetItemTab;
    @UiField(provided = true)
    protected WorksheetNotesTabUI                       notesTab;
    
    
    
    protected ArrayList<Integer>                        formatIds;
    protected ArrayList<Item<Integer>>                  qcLinkModel;
//    protected ArrayList<TableDataRow>                   qcLastRunList, qcLastBothList,
//                                                        testWorksheetItems;
    protected Confirm                                   worksheetRemoveDuplicateQCConfirm,
                                                        worksheetRemoveQCConfirm, 
                                                        worksheetRemoveLastOfQCConfirm, 
                                                        worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer, Exception>               qcErrors;
    protected HashMap<MenuItem, Integer>                templateMap;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModels;
    protected HashMap<Integer, SampleManager1>          sampleManagers;
    protected QcLookupScreen                            qcLookupScreen;
//    protected TableDataRow                              qcItems[];
    protected TestWorksheetDO                           testWorksheetDO;
    protected TestWorksheetManager                      twManager;
    protected WorksheetAnalysisSelectionScreen          waSelectionScreen;
    protected WorksheetBuilderLookupScreenUI            wbLookupScreen;
    protected WorksheetLookupScreenUI                   wLookupScreen, wAnaLookupScreen;
    
    public WorksheetBuilderScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Builder Screen"));

        worksheetItemTab = new WorksheetItemTabUI(this, bus);
        notesTab = new WorksheetNotesTabUI(this, bus);
        initWidget(uiBinder.createAndBindUi(this));
        
        manager = null;
        formatIds = new ArrayList<Integer>();
        sampleManagers = new HashMap<Integer, SampleManager1>();

        try {
            CategoryCache.getBySystemNames("test_worksheet_format", "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("WorksheetBuilder Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        screen = this;

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

        worksheetHistory.addCommand(new Command() {
            @Override
            public void execute() {
                worksheetHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                worksheetHistory.setEnabled(isState(DISPLAY));
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
                return forward ? statusId : tabPanel;
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
                statusId.setEnabled(isState(QUERY) || (isState(UPDATE) && canEdit()));
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
                setSystemUserFromSelection(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                systemUserId.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
            }

            public Widget onTab(boolean forward) {
                return forward ? formatId : statusId;
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;
                
                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users)
                        model.add(new Item(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
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
                setRelatedWorksheetId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                relatedWorksheetId.setEnabled(isState(QUERY));
                relatedWorksheetId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? instrumentName : formatId;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                lookupWorksheetButton.setEnabled(isState(ADD, UPDATE) && canEdit());
            }
        });

        addScreenHandler(instrumentName, WorksheetBuilderMeta.getInstrumentName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setInstrumentSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setInstrumentFromSelection(event.getValue());
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
                }
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
                createdDate.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit()));
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
                return forward ? tabPanel : createdDate;
            }
        });

        //
        // tabs
        //
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(13);
                WorksheetService1.get().fetchByQuery(query, new AsyncCallback<ArrayList<IdVO>>() {
                    public void onSuccess(ArrayList<IdVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(Messages.get().noRecordsFound());
                            setState(DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(Messages.get().noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Worksheet call query failed; " +
                                         error.getMessage());
                            window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdVO entry) {
                return fetchById((entry == null) ? null : entry.getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getId()));
                }
                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nav.enable(isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
            }
        });
        
        //
        // right hand analyte table panel
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                analyteTable.setEnabled(true);
            }
        });

        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                //  only the reportable field can be edited
                //
                if (event.getCol() != 0)
                    event.cancel();
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });

        //
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);

        //
        // load worksheet format dropdown model
        //
        dictList = CategoryCache.getBySystemName("test_worksheet_format");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        formatId.setModel(model);
    }
    
    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        sampleManagers.clear();
        setData();
        setState(QUERY);
        fireDataChange();
        worksheetId.setFocus(true);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
//        try {
//            manager = WorksheetService1.get().getInstance();
//        } catch (Exception e) {
//            Window.alert(e.getMessage());
//            // TODO log to the server
//            return;
//        }
//        sampleManagers.clear();
//        setData();
//        setState(ADD);
//        fireDataChange();
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
//        window.setBusy(Messages.get().lockForUpdate());
//
//        try {
//            manager = WorksheetService1.get().fetchForUpdate(manager.getWorksheet().getId());
//
//            setData();
//            setState(UPDATE);
//            fireDataChange();
//            statusId.setFocus(true);
//        } catch (Exception e) {
//            Window.alert(e.getMessage());
//        }
//
//        window.clearStatus();
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        finishEditing();
        clearErrors();
        
        if (!validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
                
            case ADD:
                commitAdd();
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

    @SuppressWarnings("unchecked")
    private void commitAdd() {
/*
        int                      i, j, k;
        ArrayList<IdNameVO>      columnNameVOs;
        ArrayList<String>        testMethodNames;
        HashMap<String,Integer>  toColumnNames;
        HashMap<Integer,String>  fromColumnNames;
        HashMap<Integer,HashMap<Integer,String>> formatColumnNames;
        Integer                  fromFormatId, toIndex;
        String                   fromName, description;
        Item<String>             row;
        WorksheetAnalysisDO      waDO;
        WorksheetAnalysisManager waManager = null;
        WorksheetItemDO          wiDO;
        WorksheetItemManager     wiManager = null;
        WorksheetQcResultManager wqrManager, newWqrManager;
        WorksheetQcResultViewDO  wqrVDO, newWqrVDO;
        WorksheetResultManager   wrManager, newWrManager;
        WorksheetResultViewDO    wrVDO, newWrVDO;
        
        finishEditing();
        
        if (worksheetItemTable.getRowCount() == 0) {
            Window.alert(Messages.get().worksheetNotSaveEmpty());
            return;
        }
        
        window.setBusy(Messages.get().saving());
        
        //
        // If the format has not been set (QC only worksheet), set it to the default
        //
//        if (formatId == null)
//            formatId = formatTotal;
        
        formatColumnNames = new HashMap<Integer,HashMap<Integer,String>>();

        try {
            columnNameVOs = WorksheetCreationService.get().getColumnNames(formatId.getValue());
            toColumnNames = new HashMap<String,Integer>();
            for (IdNameVO columnNameVO : columnNameVOs)
                toColumnNames.put(columnNameVO.getName(), columnNameVO.getId());
        } catch (Exception anyE) {
//            Window.alert(Messages.get().worksheetToColumnMappingLoadError"));
            anyE.printStackTrace();
            toColumnNames = null;
        }

        try {
            wiManager = manager.getItems();
        } catch (Exception ignE) {
            // ignoring not found exception because it will never get thrown
            // in this situation
        }
        
        testMethodNames = new ArrayList<String>();
        for (i = 0; i < worksheetItemTable.getRowCount(); i++) {
            row = worksheetItemTable.getRowAt(i);
            
            wiDO = new WorksheetItemDO();
            wiDO.setPosition((Integer)row.cells.get(0).value);
            wiManager.addWorksheetItem(wiDO);
            
            waDO = new WorksheetAnalysisDO();
            waDO.setId((Integer)row.getKey());
            waDO.setAccessionNumber((String)row.getCell(1).value);

            testMethodNames.add((String)row.cells.get(4).value + ", " + (String)row.cells.get(5).value);
            //
            // Set either the qc id or the analysis id depending on what type
            // of row we have
            //
            if (row.data instanceof ArrayList) {
                if (((ArrayList<Object>)row.data).size() == 3)
                    waDO.setAnalysisId(((AnalysisViewDO)((ArrayList<Object>)row.data).get(0)).getId());
                else
                    waDO.setQcLotId(((QcLotViewDO)((ArrayList<Object>)row.data).get(1)).getId());
            } else {
                waDO.setAnalysisId(((WorksheetBuilderVO)row.data).getAnalysisId());
            }
            
            //
            // Pull out the analysis id for the qc link column
            //
            if (row.cells.get(3).getValue() != null) {
                if (row.cells.get(3).getValue() instanceof ArrayList) {
                    if (((ArrayList<Object>)row.cells.get(3).getValue()).size() > 0)
                        waDO.setWorksheetAnalysisId((Integer)((ArrayList<Object>)row.cells.get(3).getValue()).get(0));
                } else {
                    waDO.setWorksheetAnalysisId((Integer)row.cells.get(3).getValue());
                }
            }
            waDO.setIsFromOther("N");
            try {
                waManager = wiManager.getWorksheetAnalysisAt(i);
                waManager.addWorksheetAnalysis(waDO);
                //
                // If this analysis is from another worksheet, copy the result
                // records from the manager in the ArrayList
                //
                if (row.data instanceof ArrayList && ((ArrayList<Object>)row.data).size() >= 3) {
                    waDO.setIsFromOther("Y");
                    fromFormatId = (Integer) ((ArrayList<Object>)row.data).get(((ArrayList<Object>)row.data).size() - 1);
                    if (!formatId.equals(fromFormatId)) {
                        fromColumnNames = formatColumnNames.get(fromFormatId);
                        if (fromColumnNames == null) {
                            try {
                                columnNameVOs = WorksheetCreationService.get().getColumnNames(fromFormatId);
                                fromColumnNames = new HashMap<Integer,String>();
                                for (IdNameVO columnNameVO : columnNameVOs)
                                    fromColumnNames.put(columnNameVO.getId(), columnNameVO.getName());
                                formatColumnNames.put(fromFormatId, fromColumnNames);
                            } catch (Exception anyE1) {
//                                Window.alert(Messages.get().worksheetFromColumnMappingLoadError"));
                                anyE1.printStackTrace();
                                fromColumnNames = null;
                            }
                        }
                    } else {
                        fromColumnNames = null;
                    }
                    
                    if (((ArrayList<Object>)row.data).size() == 3) {
                        wrManager = (WorksheetResultManager) ((ArrayList<Object>)row.data).get(1);
                        newWrManager = waManager.getWorksheetResultAt(waManager.count() - 1);
                        for (j = 0; j < wrManager.count(); j++) {
                            wrVDO = wrManager.getWorksheetResultAt(j);
                            newWrVDO = new WorksheetResultViewDO();
                            newWrVDO.setTestAnalyteId(wrVDO.getTestAnalyteId());
                            newWrVDO.setTestResultId(wrVDO.getTestResultId());
                            newWrVDO.setResultRow(wrVDO.getResultRow());
                            newWrVDO.setAnalyteId(wrVDO.getAnalyteId());
                            newWrVDO.setTypeId(wrVDO.getTypeId());
                            if (fromColumnNames != null && toColumnNames != null) {
                                for (k = 0; k < 30; k++) {
                                    fromName = fromColumnNames.get(k+9);
                                    if (fromName != null) {
                                        toIndex = toColumnNames.get(fromName);
                                        if (toIndex != null)
                                            newWrVDO.setValueAt(toIndex.intValue() - 9, wrVDO.getValueAt(k));
                                    }
                                }
                            } else {
                                for (k = 0; k < 30; k++)
                                    newWrVDO.setValueAt(k, wrVDO.getValueAt(k));
                            }
                            newWrVDO.setAnalyteName(wrVDO.getAnalyteName());
                            newWrVDO.setAnalyteExternalId(wrVDO.getAnalyteExternalId());
                            newWrVDO.setResultGroup(wrVDO.getResultGroup());
                            newWrManager.addWorksheetResult(newWrVDO);
                        }
                    } else {
                        wqrManager = (WorksheetQcResultManager) ((ArrayList<Object>)row.data).get(2);
                        newWqrManager = waManager.getWorksheetQcResultAt(waManager.count() - 1);
                        for (j = 0; j < wqrManager.count(); j++) {
                            wqrVDO = wqrManager.getWorksheetQcResultAt(j);
                            newWqrVDO = new WorksheetQcResultViewDO();
                            newWqrVDO.setSortOrder(wqrVDO.getSortOrder());
                            newWqrVDO.setQcAnalyteId(wqrVDO.getQcAnalyteId());
                            newWqrVDO.setTypeId(wqrVDO.getTypeId());
                            if (fromColumnNames != null && toColumnNames != null) {
                                for (k = 0; k < 30; k++) {
                                    fromName = fromColumnNames.get(k+9);
                                    if (fromName != null) {
                                        toIndex = toColumnNames.get(fromName);
                                        if (toIndex != null)
                                            newWqrVDO.setValueAt(toIndex.intValue() - 9, wqrVDO.getValueAt(k));
                                    }
                                }
                            } else {
                                for (k = 0; k < 30; k++)
                                    newWqrVDO.setValueAt(k, wqrVDO.getValueAt(k));
                            }
                            newWqrVDO.setAnalyteId(wqrVDO.getAnalyteId());
                            newWqrVDO.setAnalyteName(wqrVDO.getAnalyteName());
                            newWqrManager.addWorksheetQcResult(newWqrVDO);
                        }
                    }
                }
            } catch (Exception anyE) {
                Window.alert("save(): " + anyE.getMessage());
                window.clearStatus();
            }
        }
        
        if (manager.getWorksheet().getDescription() == null) {
            description = "";
            for (String testMethodName : testMethodNames) {
                if (description.length() > 0)
                    description += "; ";
                description += testMethodName;
            }
            manager.getWorksheet().setDescription(description);
        }
        
        final WorksheetBuilderScreenUI wcs = this;
        WorksheetService.get().add(manager, new AsyncCallback<WorksheetManager>() {
            public void onSuccess(WorksheetManager newMan) {
                manager = newMan;

                setState(DISPLAY);
                fireDataChange();
                window.setDone(Messages.get().savingComplete());
                
                wbLookupScreen.getWindow().close();
            }
            
            public void onFailure(Throwable error) {
                if (error instanceof ValidationErrorsList) {
                    showErrors((ValidationErrorsList)error);
                    manager = WorksheetManager.getInstance();
                } else {
                    Window.alert("save(): " + error.getMessage());
                    window.clearStatus();
                    manager = WorksheetManager.getInstance();
                }
            }
        });
*/
    }
    
    private void commitUpdate() {
    }
    
    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        switch (state) {
            case QUERY:
                fetchById(null);
                window.setDone(Messages.get().queryAborted());
                break;
                
            case ADD:
                abortAdd();
//                fetchById(null);
//                window.setDone(Messages.get().addAborted());
                break;
                
            case UPDATE:
//                try {
//                    manager = manager.abortUpdate();
//                    setState(DISPLAY);
//                    fireDataChange();
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                    fetchById(null);
//                }
//                window.setDone(Messages.get().updateAborted());
//                wbLookupScreen.getWindow().close();
                break;
                
            default:
                window.clearStatus();
        }
    }

    private void abortAdd() {
//        ArrayList<DictionaryDO> dictList;
//        ArrayList<Item<Integer>> model;
//
//        //
//        // reset worksheet format dropdown model
//        //
//        formatIds.clear();
//        dictList = CategoryCache.getBySystemName("test_worksheet_format");
//        model = new ArrayList<Item<Integer>>();
//        model.add(new Item<Integer>(null, ""));
//        for (DictionaryDO resultDO : dictList)
//            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
//        formatId.setModel(model);
//        
//        //
//        // remove all options from the load template menu
//        //
//        templateMap.clear();
//        loadTemplateMenu.clear();
//        
//        worksheetItemTable.clear();
//        
//        manager = WorksheetManager.getInstance();
//        sampleManagers.clear();
//        setState(DEFAULT);
//        fireDataChange();
//        window.clearStatus();
//        
//        wbLookupScreen.getWindow().close();
    }

    private void worksheetHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getWorksheet().getId(), manager.getWorksheet()
                                                                   .getId()
                                                                   .toString());
        HistoryScreen.showHistory(Messages.get().worksheetHistory(),
                                  Constants.table().WORKSHEET,
                                  hist);
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        QueryData field;
        
        fields = super.getQueryFields();
        
        if (systemUserId.getValue() != null && systemUserId.getValue().getId() != null) {
            field = new QueryData();
            field.setKey(WorksheetBuilderMeta.getWorksheetSystemUserId());
            field.setQuery(systemUserId.getValue().getId().toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }
        
        return fields;
    }

    protected boolean fetchById(Integer id) {
        int i, j;
        ArrayList<Integer> analysisIds;
        ArrayList<SampleManager1> sMans;
        WorksheetAnalysisDO waDO;
        WorksheetItemDO wiDO;
        
        sampleManagers.clear();
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = WorksheetService1.get().fetchById(id, WorksheetManager1.Load.DETAIL,
                                                            WorksheetManager1.Load.NOTE);
                analysisIds = new ArrayList<Integer>();
                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = manager.item.get(i);
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        if (waDO.getAnalysisId() != null) {
                            if (!analysisIds.contains(waDO.getAnalysisId()))
                                analysisIds.add(waDO.getAnalysisId());
                        }
                    }
                }

                if (analysisIds.size() > 0) {
                    sMans = SampleService1.get().fetchByAnalyses(analysisIds, SampleManager1.Load.ORGANIZATION,
                                                                 SampleManager1.Load.SINGLERESULT);
                    for (SampleManager1 sManager : sMans)
                        sampleManagers.put(sManager.getSample().getAccessionNumber(), sManager);
                }
                
                setState(DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                sampleManagers.clear();
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }

        }
        fireDataChange();
        window.clearStatus();

        return true;
    }

    @UiHandler("lookupWorksheetButton")
    protected void openWorksheetLookup(ClickEvent event) {
        WindowInt modal;
        
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
                                manager.getWorksheet().setRelatedWorksheetId(wVDO.getId());
                                relatedWorksheetId.fireEvent(new DataChangeEvent());
                            }
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(Messages.get().worksheetLookup());
            modal.setContent(wLookupScreen);
            wLookupScreen.setState(state);
            wLookupScreen.setWindow(modal);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void showAnalytes() {
/*
        ArrayList<TableDataRow> selections;
        
        if (analyteTable.isVisible()) {
            selections = worksheetItemTable.getSelections();
            if (selections.size() == 1) {
                if (selections.get(0).data instanceof WorksheetAnalysisDO) {
                    ResultService.get().fetchByAnalysisIdForDisplay(((WorksheetAnalysisDO)selections.get(0).data).getAnalysisId(),
                                                                    new AsyncCallback<AnalysisResultManager>() {
                        public void onSuccess(AnalysisResultManager arMan) {
                            loadAnalyteTable(arMan);
                        }
            
                        public void onFailure(Throwable error) {
                            analyteTable.load(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().noAnalytesFoundForRow());
                            } else {
                                Window.alert("Error: WorksheetCreation call showAnalytes failed; "+error.getMessage());
                            }
                        }
                    });
                } else if (selections.get(0).data instanceof WorksheetBuilderVO) {
                    ResultService.get().fetchByAnalysisIdForDisplay(((WorksheetBuilderVO)selections.get(0).data).getAnalysisId(),
                                                                    new AsyncCallback<AnalysisResultManager>() {
                        public void onSuccess(AnalysisResultManager arMan) {
                            loadAnalyteTable(arMan);
                        }
            
                        public void onFailure(Throwable error) {
                            analyteTable.load(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().noAnalytesFoundForRow());
                            } else {
                                Window.alert("Error: WorksheetCreation call showAnalytes failed; "+error.getMessage());
                            }
                        }
                    });
                } else {
                    analyteTable.load(null);
                }
            } else {
                analyteTable.load(null);
            }            
        } else {
            analyteTable.load(null);
        }
*/
    }

    public void loadAnalyteTable(AnalysisResultManager arMan) {
/*
        int i;
        ArrayList<ResultViewDO> resultRow;
        ArrayList<TableDataRow> model;
        ResultViewDO result;
        TableDataRow row;

        model = null;
        if (arMan != null) {
            model = new ArrayList<TableDataRow>();
            for (i = 0; i < arMan.getResults().size(); i++) {
                resultRow = arMan.getRowAt(i);
                result = (ResultViewDO)resultRow.get(0);

                row = new TableDataRow(2);
                row.key = result.getId();
                row.cells.get(0).value = result.getIsReportable();
                row.cells.get(1).value = result.getAnalyte();
                row.data = result;
                model.add(row);
            }
        }
        analyteTable.load(model);
*/
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
            systemUserId.setValue(manager.getWorksheet().getSystemUserId(),
                                  manager.getWorksheet().getSystemUser());
    }

    private void setSystemUserFromSelection(Integer userId) {
        manager.getWorksheet().setSystemUserId(userId);
        manager.getWorksheet().setSystemUser(systemUserId.getDisplay());
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
            instrumentName.setValue(manager.getWorksheet().getInstrumentId(),
                                    manager.getWorksheet().getInstrumentName());
    }

    private void setInstrumentFromSelection(Integer instrumentId) {
        manager.getWorksheet().setInstrumentId(instrumentId);
        manager.getWorksheet().setInstrumentName(instrumentName.getDisplay());
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