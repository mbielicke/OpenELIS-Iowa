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
package org.openelis.modules.worksheetCompletion1.client;

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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetResultsTransferVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.meta.WorksheetMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.attachment.client.DisplayAttachmentEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.modules.pws.client.StatusBarPopupScreenUI;
import org.openelis.modules.report.client.WorksheetPrintReportScreen;
import org.openelis.modules.sample1.client.AttachmentTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.TestReflexUtility1;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetNotesTabUI;
import org.openelis.modules.worksheet1.client.WorksheetReagentTabUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
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
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
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
import org.openelis.ui.widget.table.Table;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

public class WorksheetCompletionScreenUI extends Screen {

    @UiTemplate("WorksheetCompletion.ui.xml")
    interface WorksheetCompletionUiBinder extends UiBinder<Widget, WorksheetCompletionScreenUI> {
    };
    
    private static WorksheetCompletionUiBinder            uiBinder   = GWT.create(WorksheetCompletionUiBinder.class);

    private boolean                                       updateTransferMode;
    private Integer                                       origStatusId;
    private ModulePermission                              userPermission;
    private ScreenNavigator<IdNameVO>                     nav;
    private PopupPanel                                    statusPanel;
    private StatusBarPopupScreenUI                        statusScreen;
    private Timer                                         exportTimer, importTimer;
    private WorksheetManager1                             manager;
    private WorksheetPrintReportScreen                    worksheetPrintReportScreen;

    @UiField
    protected AutoComplete                                instrumentName, systemUserId;
    @UiField
    protected Calendar                                    createdDate;
    @UiField
    protected Button                                      query, previous, next,
                                                          update, commit, abort,
                                                          lookupWorksheetButton,
                                                          atozNext, atozPrev, optionsButton,
                                                          exportToExcelButton, importFromExcelButton,
                                                          transferResultsButton;
    @UiField
    protected Dropdown<Integer>                           formatId, statusId;
    @UiField
    protected Menu                                        optionsMenu;
    @UiField
    protected MenuItem                                    print, worksheetHistory;
    @UiField
    protected TabLayoutPanel                              tabPanel;
    @UiField
    protected Table                                       atozTable;
    @UiField
    protected TextBox<Integer>                            relatedWorksheetId, worksheetId;
    @UiField
    protected TextBox<String>                             description;
    @UiField(provided = true)
    protected WorksheetItemTabUI                          worksheetItemTab;
    @UiField(provided = true)
    protected OverridesTabUI                              overridesTab;
    @UiField(provided = true)
    protected WorksheetReagentTabUI                       reagentTab;
    @UiField(provided = true)
    protected WorksheetNotesTabUI                         notesTab;
    @UiField(provided = true)
    protected AttachmentTabUI                             attachmentTab;

    protected ArrayList<SampleManager1>                   sampleMans;
    protected AsyncCallbackUI<ArrayList<IdNameVO>>        queryCall;
    protected AsyncCallbackUI<ArrayList<SampleManager1>>  unlockSamplesCall;
    protected AsyncCallbackUI<WorksheetManager1>          exportToExcelCall, fetchByIdCall,
                                                          fetchForUpdateCall, fetchRelatedByIdCall,
                                                          importFromExcelCall, unlockCall,
                                                          unlockReflexCall, unlockReflexPopupCall,
                                                          unlockTransferCall, updateCall;
    protected AsyncCallbackUI<WorksheetResultsTransferVO> fetchForTransferCall, transferCall;
    protected EditNoteLookupUI                            failedNoteLookup;
    protected HashMap<Integer, AuxFieldGroupManager>      auxManagers;
    protected HashMap<Integer, TestManager>               testManagers;
    protected SampleManager1.Load                         sampleElements[] = {SampleManager1.Load.ORGANIZATION,
                                                                              SampleManager1.Load.QA,
                                                                              SampleManager1.Load.ANALYSISUSER,
                                                                              SampleManager1.Load.SINGLERESULT};
    protected String                                      displayExcelDirectory;
    protected TestReflexUtility1                          testReflexUtility;
    protected TestSelectionLookupUI                       testSelectionLookup;
    protected WorksheetCompletionScreenUI                 screen;
    protected WorksheetLookupScreenUI                     wLookupScreen;
    protected WorksheetManager1.Load                      elements[] = {WorksheetManager1.Load.DETAIL,
                                                                        WorksheetManager1.Load.REAGENT,
                                                                        WorksheetManager1.Load.NOTE,
                                                                        WorksheetManager1.Load.ATTACHMENT};

    public WorksheetCompletionScreenUI(WindowInt window) throws Exception {
        SystemVariableDO sysVarDO;
        
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Completion Screen"));

        try {
            sysVarDO = SystemVariableService.get().fetchByExactName("worksheet_display_directory");
            displayExcelDirectory = sysVarDO.getValue();
        } catch (Exception anyE) {
            throw new Exception(Messages.get().worksheet_displayDirectoryLookupException());
        }
        
        worksheetItemTab = new WorksheetItemTabUI(this);
        overridesTab = new OverridesTabUI(this);
        reagentTab = new WorksheetReagentTabUI(this);
        notesTab = new WorksheetNotesTabUI(this);
        attachmentTab = new AttachmentTabUI(this) {
            @Override
            public int count() {
                if (manager != null)
                    return manager.attachment.count();
                return 0;
            }

            @Override
            public AttachmentItemViewDO get(int i) {
                return manager.attachment.get(i);
            }

            @Override
            public String getAttachmentCreatedDateMetaKey() {
                return SampleMeta.getAttachmentItemAttachmentCreatedDate();
            }

            @Override
            public String getAttachmentSectionIdKey() {
                return SampleMeta.getAttachmentItemAttachmentSectionId();
            }

            @Override
            public String getAttachmentDescriptionKey() {
                return SampleMeta.getAttachmentItemAttachmentDescription();
            }

            @Override
            public AttachmentItemViewDO createAttachmentItem(AttachmentDO att) {
                AttachmentItemViewDO atti;

                atti = manager.attachment.add();
                atti.setId(manager.getNextUID());
                atti.setAttachmentId(att.getId());
                atti.setAttachmentCreatedDate(att.getCreatedDate());
                atti.setAttachmentSectionId(att.getSectionId());
                atti.setAttachmentDescription(att.getDescription());

                return atti;
            }

            @Override
            public void remove(int i) {
                manager.attachment.remove(i);
            }
        };

        initWidget(uiBinder.createAndBindUi(this));
        
        manager = null;
        updateTransferMode = false;
        auxManagers = new HashMap<Integer, AuxFieldGroupManager>();
        testManagers = new HashMap<Integer, TestManager>();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() throws Exception {
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
                update.setEnabled((isState(UPDATE) && !updateTransferMode) ||
                                  (isState(DISPLAY) && userPermission.hasUpdatePermission()));
                if (isState(UPDATE) && !updateTransferMode) {
                    update.setPressed(true);
                    update.lock();
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);


        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                print.setEnabled(isState(DISPLAY));
                worksheetHistory.setEnabled(isState(DISPLAY));
            }
        });
        
        print.addCommand(new Command() {
            @Override
            public void execute() {
                print();
            }
        });

        worksheetHistory.addCommand(new Command() {
            @Override
            public void execute() {
                worksheetHistory();
            }
        });

        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                boolean enable;

                enable = isState(DISPLAY) && canEdit() && userPermission.hasUpdatePermission();
                exportToExcelButton.setEnabled(enable);
                importFromExcelButton.setEnabled(enable);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                boolean enable;

                enable = isState(DISPLAY) && canEdit() && userPermission.hasUpdatePermission();
                exportToExcelButton.setEnabled(enable);
                importFromExcelButton.setEnabled(enable);
            }
        });

        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                transferResultsButton.setEnabled((isState(UPDATE) && updateTransferMode) ||
                                                 (isState(DISPLAY) && canEdit() &&
                                                  userPermission.hasUpdatePermission()));
                if (isState(UPDATE) && updateTransferMode) {
                    transferResultsButton.setPressed(true);
                    transferResultsButton.lock();
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                transferResultsButton.setEnabled((isState(UPDATE) && updateTransferMode) ||
                                                 (isState(DISPLAY) && canEdit() &&
                                                  userPermission.hasUpdatePermission()));
                if (isState(UPDATE) && updateTransferMode) {
                    transferResultsButton.setPressed(true);
                    transferResultsButton.lock();
                }
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
                statusId.setEnabled(isState(QUERY) || (isState(UPDATE) && !updateTransferMode));
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
                setRelatedWorksheetId(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent event) {
                relatedWorksheetId.setEnabled(isState(QUERY) || (isState(UPDATE) && !updateTransferMode && canEdit()));
                relatedWorksheetId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? instrumentName : formatId;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                lookupWorksheetButton.setEnabled(isState(DISPLAY) || (isState(UPDATE) && !updateTransferMode && canEdit()));
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
                instrumentName.setEnabled(isState(QUERY) || (isState(UPDATE) && !updateTransferMode && canEdit()));
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
                description.setEnabled(isState(QUERY) || (isState(UPDATE) && !updateTransferMode && canEdit()));
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

        addScreenHandler(overridesTab, "overridesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                overridesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                overridesTab.setState(event.getState());
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

        addScreenHandler(attachmentTab, "attachmentTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                attachmentTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                if (isState(UPDATE) && (updateTransferMode || !canEdit()))
                    attachmentTab.setState(DISPLAY);
                else
                    attachmentTab.setState(event.getState());
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        attachmentTab.setCanQuery(false);

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
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

                query.setRowsPerPage(21);
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
            }
        });
        
        bus.addHandler(DisplayAttachmentEvent.getType(), new DisplayAttachmentEvent.Handler() {
            @Override
            public void onDisplayAttachment(DisplayAttachmentEvent event) {
                displayAttachment(event.getId(), event.getIsSameWindow());
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(UPDATE)) {
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
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
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
                
            case UPDATE:
                if (!updateTransferMode) {
                    currStatusId = manager.getWorksheet().getStatusId();
                    if (Constants.dictionary().WORKSHEET_FAILED.equals(currStatusId) &&
                        !Constants.dictionary().WORKSHEET_FAILED.equals(origStatusId)) {
                        openFailedRunNote();
                        return;
                    } else if (Constants.dictionary().WORKSHEET_VOID.equals(currStatusId) &&
                               !Constants.dictionary().WORKSHEET_VOID.equals(origStatusId)) {
                        commitUpdate(WorksheetManager1.ANALYSIS_UPDATE.VOID);
                    } else {
                        commitUpdate(null);
                    }
                } else {
                    commitTransfer();
                }
                break;
        }
    }
    
    private void commitQuery() {
        Query query;
        
        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    private void commitUpdate(WorksheetManager1.ANALYSIS_UPDATE flag) {
        finishEditing();
        setBusy(Messages.get().updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    setDone(Messages.get().gen_updatingComplete());
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }
                
                public void failure(Throwable e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().update(manager, flag, updateCall);
    }
    
    private void commitTransfer() {
        ArrayList<WorksheetAnalysisViewDO> waVDOs;
        
        finishEditing();
        setBusy(Messages.get().updating());
        
        waVDOs = worksheetItemTab.getTransferSelection();
        if (transferCall == null) {
            transferCall = new AsyncCallbackUI<WorksheetResultsTransferVO>() {
                public void success(final WorksheetResultsTransferVO result) {
                    ArrayList<SampleTestRequestVO> tests;

                    if (result.getSampleManagers() != null && result.getSampleManagers().size() > 0) {
                        final HashMap<Integer, SampleManager1> sMansById = new HashMap<Integer, SampleManager1>();
                        for (SampleManager1 sMan : result.getSampleManagers())
                            sMansById.put(sMan.getSample().getId(), sMan);
                        
                        if (testReflexUtility == null) {
                            testReflexUtility = new TestReflexUtility1() {
                                @Override
                                public TestManager getTestManager(Integer testId) throws Exception {
                                    return screen.getTestManager(testId);
                                }
                            };
                        }
                        try {
                            tests = testReflexUtility.getReflexTests(result.getSampleManagers(),
                                                                     result.getReflexResultsList());
                            if (tests != null && tests.size() > 0) {
                                showPrepAndReflexTests(sMansById, tests);
                            } else {
                                try {
                                    SampleService1.get().update(result.getSampleManagers(), true);
                                } catch (ValidationErrorsList vel) {
                                    showErrors(vel);
                                    return;
                                } catch (Exception e) {
                                    Window.alert(e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                    return;
                                }
                                if (unlockReflexCall == null) {
                                    unlockReflexCall = new AsyncCallbackUI<WorksheetManager1>() {
                                        public void success(WorksheetManager1 result) {
                                            manager = result;
                                            updateTransferMode = false;
                                            setData();
                                            setState(DISPLAY);
                                            fireDataChange();
                                            setDone(Messages.get().gen_updatingComplete());
                                        }
                                        
                                        public void failure(Throwable e) {
                                            Window.alert(e.getMessage());
                                            logger.log(Level.SEVERE, e.getMessage(), e);
                                            clearStatus();
                                        }
                                    };
                                }
                                
                                WorksheetService1.get().unlock(manager.getWorksheet().getId(),
                                                               elements,
                                                               unlockReflexCall);
                            }
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else {
                        manager = result.getWorksheetManager();
                        updateTransferMode = false;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updatingComplete());
                    }
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }
                
                public void failure(Throwable e) {
                    Window.alert("commitTransfer(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().transferResults(manager, waVDOs, sampleMans, transferCall);
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
                
            case UPDATE:
                if (updateTransferMode)
                    abortTransfer();
                else
                    abortUpdate();
                break;
        }
    }

    private void abortUpdate() {
        if (unlockCall == null) {
            unlockCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    setDone(Messages.get().gen_updateAborted());
                }
                
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
        
        WorksheetService1.get().unlock(manager.getWorksheet().getId(), elements, unlockCall);
    }
    
    private void abortTransfer() {
        ArrayList<Integer> sampleIds;
        
        if (unlockSamplesCall == null) {
            unlockSamplesCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    if (unlockTransferCall == null) {
                        unlockTransferCall = new AsyncCallbackUI<WorksheetManager1>() {
                            public void success(WorksheetManager1 result) {
                                manager = result;
                                updateTransferMode = false;
                                setData();
                                setState(DISPLAY);
                                fireDataChange();
                                setDone(Messages.get().gen_updateAborted());
                            }
                            
                            public void failure(Throwable e) {
                                Window.alert(e.getMessage());
                                logger.log(Level.SEVERE, e.getMessage(), e);
                                clearStatus();
                            }
                        };
                    }
                    
                    WorksheetService1.get().unlock(manager.getWorksheet().getId(),
                                                   elements,
                                                   unlockTransferCall);
                }
                
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
        
        sampleIds = new ArrayList<Integer>();
        for (SampleManager1 sMan : sampleMans)
            sampleIds.add(sMan.getSample().getId());

        SampleService1.get().unlock(sampleIds, sampleElements, unlockSamplesCall);
    }

    protected void print() {
        ScreenWindow modal;

        try {
            if (worksheetPrintReportScreen == null) {
                worksheetPrintReportScreen = new WorksheetPrintReportScreen();

                /*
                 * we need to make sure that the value of SHIPPING_ID gets set
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
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
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

    @SuppressWarnings("unused")
    @UiHandler("exportToExcelButton")
    protected void exportToExcel(ClickEvent event) {
        createStatusBarPopup();
        statusPanel.show();
        statusScreen.setStatus(null);
        
        setBusy("Exporting to Excel");
        if (exportToExcelCall == null) {
            exportToExcelCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    SystemUserVO userVO;
    
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    
                    userVO = null;
                    try {
                        userVO = UserCache.getSystemUser(manager.getWorksheet()
                                                                .getSystemUserId());
                        setDone(Messages.get().worksheet_exportedToExcelFile() + " " +
                                displayExcelDirectory + manager.getWorksheet().getId() +
                                "_" + userVO.getLoginName() + ".xls");
                    } catch (Exception anyE) {
                        Window.alert("Error retrieving username for worksheet: " +
                                     anyE.getMessage());
                        clearStatus();
                    }
                }
    
                public void failure(Throwable e) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    Window.alert("editWorksheet(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().exportToExcel(manager, exportToExcelCall);

        /*
         * refresh the status of exporting the worksheet to Excel every second,
         * until the process successfully completes or is aborted because of an
         * error
         */
        if (exportTimer == null) {
            exportTimer = new Timer() {
                public void run() {
                    ReportStatus status;
                    try {
                        status = WorksheetService1.get().getExportToExcelStatus();
                        /*
                         * the status only needs to be refreshed while the status
                         * panel is showing because once the job is finished, the
                         * panel is closed
                         */
                        if (statusPanel.isShowing()) {
                            statusScreen.setStatus(status);
                            this.schedule(1000);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            };
        }
        exportTimer.schedule(1000);
    }

    @SuppressWarnings("unused")
    @UiHandler("importFromExcelButton")
    protected void importFromExcel(ClickEvent event) {
        createStatusBarPopup();
        statusPanel.show();
        statusScreen.setStatus(null);
        
        setBusy("Importing from Excel");
        if (importFromExcelCall == null) {
            importFromExcelCall = new AsyncCallbackUI<WorksheetManager1>() {
                public void success(WorksheetManager1 result) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);

                    manager = result;
                    setData();
                    fireDataChange();
                    setDone("Import Successful");
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    showErrors(e);
                }
                
                public void failure(Throwable e) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }
        
        WorksheetService1.get().importFromExcel(manager, importFromExcelCall);

        /*
         * refresh the status of importing the worksheet from Excel every second,
         * until the process successfully completes or is aborted because of an
         * error
         */
        if (importTimer == null) {
            importTimer = new Timer() {
                public void run() {
                    ReportStatus status;
                    try {
                        status = WorksheetService1.get().getImportFromExcelStatus();
                        /*
                         * the status only needs to be refreshed while the status
                         * panel is showing because once the job is finished, the
                         * panel is closed
                         */
                        if (statusPanel.isShowing()) {
                            statusScreen.setStatus(status);
                            this.schedule(1000);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            };
        }
        importTimer.schedule(1000);
    }

    @SuppressWarnings("unused")
    @UiHandler("transferResultsButton")
    protected void transferResults(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());
        
        if (fetchForTransferCall == null) {
            fetchForTransferCall = new AsyncCallbackUI<WorksheetResultsTransferVO>() {
                public void success(WorksheetResultsTransferVO result) {
                    int i, a;
                    AnalysisViewDO aVDO;
                    SampleItemViewDO siVDO;
                    
                    manager = result.getWorksheetManager();
                    sampleMans = result.getSampleManagers();
                    updateTransferMode = true;
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    statusPanel.hide();
                    statusScreen.setStatus(null);
                    showErrors(e);
                }
                
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }
                
                public void finish() {
                    clearStatus();
                }
            };
        }

        WorksheetService1.get().fetchForTransfer(manager.getWorksheet().getId(), fetchForTransferCall);
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
    
    public boolean getUpdateTransferMode() {
        return updateTransferMode;
    }

    /**
     * Executes a query to fetch the worksheet whose id is passed in
     */
    public void query(Integer id) {
        Query query;
        QueryData field;
        
        if (id != null && !isState(ADD, UPDATE)) {
            field = new QueryData();
            field.setKey(WorksheetMeta.getId());
            field.setQuery(id.toString());
            field.setType(QueryData.Type.INTEGER);
            
            query = new Query();
            query.setFields(field);
            nav.setQuery(query);
        }
    }
    
    private void setNoteFields(NoteViewDO note, String subject, String text) {
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
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
        overridesTab.setData(manager);
        reagentTab.setData(manager);
        notesTab.setData(manager);
    }
    
    private boolean canEdit() {
        return (manager != null && Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet().getStatusId()));
    }

    private void createStatusBarPopup() {
        if (statusScreen == null) {
            statusScreen = new StatusBarPopupScreenUI();

            /*
             * initialize and show the popup screen
             */
            statusPanel = new PopupPanel();
            statusPanel.setSize("450px", "125px");
            statusPanel.setWidget(statusScreen);
            statusPanel.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
            statusPanel.setModal(true);
        }
    }
    
    /*
     * show the popup for selecting the reflex test
     */
    private void showPrepAndReflexTests(final HashMap<Integer, SampleManager1> sMansById,
                                        ArrayList<SampleTestRequestVO> reqTests) {
        ModalWindow modal;

        if (testSelectionLookup == null) {
            testSelectionLookup = new TestSelectionLookupUI() {
                @Override
                public TestManager getTestManager(Integer testId) throws Exception {
                    return screen.getTestManager(testId);
                }

                @Override
                public void ok() {
                    ArrayList<SampleTestRequestVO> prepTests, selTests, samTests;
                    Integer samId;
                    SampleTestReturnVO returnVO;

                    samId = null;
                    prepTests = new ArrayList<SampleTestRequestVO>();
                    samTests = new ArrayList<SampleTestRequestVO>();
                    selTests = testSelectionLookup.getSelectedTests();
                    if (selTests != null && selTests.size() > 0) {
                        for (SampleTestRequestVO test : selTests) {
                            if (!test.getSampleId().equals(samId)) {
                                if (samId != null) {
                                    try {
                                        returnVO = SampleService1.get().addAnalyses(sMansById.get(samId), samTests);
                                        validateAuxDataAndResults(returnVO.getManager(), returnVO.getErrors());
                                        if (returnVO.getErrors() != null && returnVO.getErrors().size() > 0)
                                            throw returnVO.getErrors();
                                        sMansById.put(samId, returnVO.getManager());
                                        samTests.clear();
                                        if (returnVO.getTests() != null && returnVO.getTests().size() > 0)
                                            prepTests.addAll(returnVO.getTests());
                                    } catch (ValidationErrorsList vel) {
                                        screen.showErrors(vel);
                                        return;
                                    } catch (Exception e) {
                                        Window.alert(e.getMessage());
                                        logger.log(Level.SEVERE, e.getMessage(), e);
                                        return;
                                    }
                                }
                                samId = test.getSampleId();
                            }
                            samTests.add(test);
                        }
                        if (samTests.size() > 0) {
                            try {
                                returnVO = SampleService1.get().addAnalyses(sMansById.get(samId), samTests);
                                validateAuxDataAndResults(returnVO.getManager(), returnVO.getErrors());
                                if (returnVO.getErrors() != null && returnVO.getErrors().size() > 0)
                                    throw returnVO.getErrors();
                                sMansById.put(samId, returnVO.getManager());
                                if (returnVO.getTests() != null && returnVO.getTests().size() > 0)
                                    prepTests.addAll(returnVO.getTests());
                            } catch (ValidationErrorsList vel) {
                                screen.showErrors(vel);
                                return;
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                logger.log(Level.SEVERE, e.getMessage(), e);
                                return;
                            }
                        }
                    }
                    
                    if (prepTests.size() > 0) {
                        showPrepAndReflexTests(sMansById, prepTests);
                    } else {
                        try {
                            SampleService1.get().update(new ArrayList<SampleManager1>(sMansById.values()), true);
                        } catch (ValidationErrorsList vel) {
                            screen.showErrors(vel);
                            return;
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            return;
                        }

                        if (unlockReflexPopupCall == null) {
                            unlockReflexPopupCall = new AsyncCallbackUI<WorksheetManager1>() {
                                public void success(WorksheetManager1 result) {
                                    manager = result;
                                    updateTransferMode = false;
                                    screen.setData();
                                    screen.setState(DISPLAY);
                                    screen.fireDataChange();
                                    screen.setDone(Messages.get().gen_updatingComplete());
                                }
                                
                                public void failure(Throwable e) {
                                    Window.alert(e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                    clearStatus();
                                }
                            };
                        }
                        
                        WorksheetService1.get().unlock(manager.getWorksheet().getId(),
                                                       elements,
                                                       unlockReflexPopupCall);
                    }
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("520px", "350px");
        modal.setName(Messages.get().testSelection_reflexTestSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testSelectionLookup);

        testSelectionLookup.setData(new ArrayList<SampleManager1>(sMansById.values()), reqTests);
        testSelectionLookup.setWindow(modal);
    }

    private TestManager getTestManager(Integer testId) throws Exception {
        TestManager tMan;
        
        tMan = testManagers.get(testId);
        if (tMan == null) {
            tMan = TestService.get().fetchWithPrepTestsAndReflexTests(testId);
            testManagers.put(testId, tMan);
        }
        return tMan;
    }

    private AuxFieldGroupManager getAuxManager(Integer auxId) throws Exception {
        AuxFieldGroupManager afgMan;

        afgMan = auxManagers.get(auxId);
        if (afgMan == null) {
            afgMan = AuxiliaryService.get().fetchById(auxId);
            auxManagers.put(auxId, afgMan);
        }
        return afgMan;
    }

    private void validateAuxDataAndResults(SampleManager1 man, ValidationErrorsList errors) {
        int i, j, k, l;
        AnalysisViewDO aVDO;
        AuxDataViewDO adVDO;
        AuxFieldGroupManager afgMan;
        Integer groupId;
        ResultFormatter rf;
        ResultViewDO rVDO;
        SampleItemViewDO siVDO;
        TestManager tMan;

        groupId = null;
        rf = null;
        for (i = 0; i < man.auxData.count(); i++) {
            adVDO = man.auxData.get(i);
            if (adVDO.getValue() != null && adVDO.getTypeId() == null) {
                try {
                    if (!adVDO.getAuxFieldGroupId().equals(groupId)) {
                        afgMan = getAuxManager(adVDO.getAuxFieldGroupId());
                        rf = afgMan.getFormatter();
                        groupId = adVDO.getAuxFieldGroupId();
                    }
                    ResultHelper.formatValue(adVDO, adVDO.getValue(), rf);
                } catch (Exception anyE) {
                    errors.add(new Exception(Messages.get().aux_defaultValueInvalidException(man.getSample().getAccessionNumber(),
                                                                                             adVDO.getAnalyteName(),
                                                                                             adVDO.getValue())));
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
            }
        }

        for (i = 0; i < man.item.count(); i++) {
            siVDO = man.item.get(i);
            for (j = 0; j < man.analysis.count(siVDO); j++) {
                aVDO = man.analysis.get(siVDO, j);
                try {
                    tMan = getTestManager(aVDO.getTestId());
                    rf = tMan.getFormatter();
                    for (k = 0; k < man.result.count(aVDO); k++) {
                        for (l = 0; l < man.result.count(aVDO, k); l++) {
                            rVDO = man.result.get(aVDO, k, l);
                            if (rVDO.getValue() != null && rVDO.getTypeId() == null) {
                                try {
                                    ResultHelper.formatValue(rVDO,
                                                             rVDO.getValue(),
                                                             aVDO.getUnitOfMeasureId(),
                                                             rf);
                                } catch (Exception anyE1) {
                                    errors.add(new Exception(Messages.get().result_defaultValueInvalidException(man.getSample().getAccessionNumber(),
                                                                                                                aVDO.getTestName(),
                                                                                                                aVDO.getMethodName(),
                                                                                                                rVDO.getAnalyte(),
                                                                                                                rVDO.getValue())));
                                    logger.log(Level.SEVERE, anyE1.getMessage(), anyE1);
                                }
                            }
                        }
                    }
                } catch (Exception anyE) {
                    errors.add(anyE);
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
            }
        }
    }

    /**
     * Opens the file linked to the attachment on the selected row in the table
     * showing the sample's attachment items. If isSameWindow is true then the
     * file is opened in the same browser window/tab as before, otherwise it's
     * opened in a different one.
     */
    private void displayAttachment(Integer id, boolean isSameWindow) {
        String name;

        /*
         * if isSameWindow is true then the name passed to displayAttachment is
         * this screen's window's title because ReportScreen sets that as the
         * title of the window passed to it, so if the name is not the same,
         * then the screen's window's title will get changed
         */
        name = isSameWindow ? Messages.get().worksheetCompletion() : null;
        try {
            AttachmentUtil.displayAttachment(id, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
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