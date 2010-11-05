package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ReportProgress;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.EditNoteScreen;
import org.openelis.modules.note.client.EditNoteScreen.Action;
import org.openelis.modules.sample.client.AccessionNumberUtility;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.EnvironmentalTab;
import org.openelis.modules.sample.client.PrivateWellTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SDWISTab;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.StorageTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class CompleteReleaseScreen extends Screen implements HasActionHandlers, SelectionHandler<Integer>,
                                                             ActionHandler<EditNoteScreen.Action> {

    private Integer                  sampleLoggedInId, sampleErrorStatusId, sampleReleasedId,
                                     analysisOnHoldId, analysisCompletedId;

    protected Tabs                   tab;
    protected ArrayList<Tabs>        tabIndexes = new ArrayList<Tabs>();
    protected TextBox                clientReference;
    protected TextBox<Integer>       accessionNumber, orderNumber;
    protected TextBox<Datetime>      collectedTime;

    protected Dropdown<Integer>      statusId;
    protected Tree                   itemsTree;
    protected Button                 removeRow, releaseButton, reportButton, completeButton,
                                     addItem, addAnalysis, queryButton, updateButton, commitButton, abortButton;

    protected Calendar               collectedDate, receivedDate;

    protected AccessionNumberUtility accessionNumUtil;
    protected SampleHistoryUtility   historyUtility;

    private ModulePermission         userPermission;
    private SampleManager            manager;
    private SampleDataBundle         dataBundle;
    private TabPanel                 sampleContent;
    private SampleTab                sampleTab;
    private EnvironmentalTab         environmentalTab;
    private PrivateWellTab           wellTab;
    private SDWISTab                 sdwisTab;
    private SampleItemTab            sampleItemTab;
    private AnalysisTab              analysisTab;
    private QAEventsTab              qaEventsTab;
    private StorageTab               storageTab;
    private SampleNotesTab           sampleNotesTab;
    private AnalysisNotesTab         analysisNotesTab;
    private AuxDataTab               auxDataTab;
    private ResultTab                testResultsTab;
    private Table                    completeReleaseTable;
    private EditNoteScreen           internalEditNote;
    private NoteViewDO               internalNote;
    private NoteManager              noteMan;
    private Confirm                  confirm;

    protected MenuItem               unreleaseAnalysis, historySample, historySampleSpec,
                                     historySampleProject, historySampleOrganization, historySampleItem,
                                     historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
                                     historyAnalysisQA, historyAuxData;

    private enum Tabs {
        BLANK, SAMPLE, ENVIRONMENT, PRIVATE_WELL, SDWIS, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public CompleteReleaseScreen() throws Exception {
        super((ScreenDefInt)GWT.create(CompleteReleaseDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.completeRelease.server.CompleteReleaseService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("samplecompleterelease");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Complete and Release Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    public void postConstructor() {
        tab = Tabs.BLANK;
        manager = SampleManager.getInstance();

        try {
            DictionaryCache.preloadByCategorySystemNames("sample_status", "analysis_status",
                                                         "type_of_sample", "source_of_sample",
                                                         "sample_container", "unit_of_measure",
                                                         "qaevent_type", "aux_field_value_type",
                                                         "organization_type");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        final CompleteReleaseScreen completeScreen = this;

        //
        // button panel buttons
        //
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasSelectPermission())
                    queryButton.setEnabled(true);
                else if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }else
                    queryButton.setEnabled(false);
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasUpdatePermission())
                    updateButton.setEnabled(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState())) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }else
                    updateButton.setEnabled(false);

            }
        });

        completeButton = (Button)def.getWidget("complete");
        addScreenHandler(completeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                complete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completeButton.setEnabled(event.getState() == State.DISPLAY);
            }
        });

        releaseButton = (Button)def.getWidget("release");
        addScreenHandler(releaseButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                release();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releaseButton.setEnabled(event.getState() == State.DISPLAY);
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        historyUtility = new SampleHistoryUtility(window) {
            public void historyCurrentResult() {
                ActionEvent.fire(completeScreen, ResultTab.Action.RESULT_HISTORY, null);
            }
        };

        unreleaseAnalysis = (MenuItem)def.getWidget("unreleaseAnalysis");
        addScreenHandler(unreleaseAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {

                if (completeReleaseTable.isMultipleRowsSelected()) {
                    com.google.gwt.user.client.Window.alert(consts.get("selOneRowUnrelease"));
                    return;
                }
                
                showConfirm() ;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unreleaseAnalysis.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySample = (MenuItem)def.getWidget("historySample");
        addScreenHandler(historySample, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySample();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySample.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleSpec = (MenuItem)def.getWidget("historySampleSpec");
        addScreenHandler(historySampleSpec, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                String domain;

                historyUtility.setManager(manager);
                domain = manager.getSample().getDomain();
                if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain))
                    historyUtility.historySampleEnvironmental();
                else if (SampleManager.WELL_DOMAIN_FLAG.equals(domain))
                    historyUtility.historySamplePrivateWell();
                else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain))
                    historyUtility.historySampleSDWIS();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleSpec.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleProject = (MenuItem)def.getWidget("historySampleProject");
        addScreenHandler(historySampleProject, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleProject();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleProject.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleOrganization = (MenuItem)def.getWidget("historySampleOrganization");
        addScreenHandler(historySampleOrganization, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleOrganization();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleOrganization.setEnabled(EnumSet.of(State.DISPLAY)
                                                        .contains(event.getState()));
            }
        });

        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleItem();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAnalysis();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyCurrentResult();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyStorage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAnalysisQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAuxData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        //
        // screen fields
        //
        sampleContent = (TabPanel)def.getWidget("SampleContent");
        sampleContent.getTabBar().setStyleName("None");
        sampleContent.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                tab = Tabs.values()[event.getSelectedItem()];
                drawTabs();
            }
        });

        completeReleaseTable = (Table)def.getWidget("completeReleaseTable");
        addScreenHandler(completeReleaseTable, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                completeReleaseTable.setEnabled(true);
                completeReleaseTable.setQueryMode(EnumSet.of(State.QUERY)
                                                         .contains(event.getState()));
            }
        });

        completeReleaseTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        completeReleaseTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (state == State.UPDATE)
                    event.cancel();
            }
        });

        completeReleaseTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                // always allow
            }
        });

        completeReleaseTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (completeReleaseTable.getSelectedRows().length == 1) {
                    dataBundle = (SampleDataBundle)completeReleaseTable.getRowAt(event.getSelectedItem()).getData();
                    manager = dataBundle.getSampleManager();
                    setState(State.DISPLAY);
                }

                resetScreen();
            }
        });

        //
        // tabs
        //
        sampleTab = new SampleTab(def, window);
        addScreenHandler(sampleTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTab.setData(manager);
                sampleTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTab.setState(event.getState());
            }
        });

        try {
            environmentalTab = new EnvironmentalTab(window);
            AbsolutePanel envTabPanel = (AbsolutePanel)def.getWidget("envDomainPanel");
            envTabPanel.add(environmentalTab);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("env tab initialize: " + e.getMessage());
        }

        addScreenHandler(environmentalTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(manager.getSample().getDomain()))
                    environmentalTab.setData(manager);
                else
                    environmentalTab.setData(null);

                if (tab == Tabs.ENVIRONMENT)
                    environmentalTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                environmentalTab.setState(event.getState());
            }
        });

        try {
            wellTab = new PrivateWellTab(window);
            AbsolutePanel wellTabPanel = (AbsolutePanel)def.getWidget("privateWellDomainPanel");
            wellTabPanel.add(wellTab);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("well tab initialize: " + e.getMessage());
        }

        addScreenHandler(wellTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain()))
                    wellTab.setData(manager);
                else
                    wellTab.setData(null);

                if (tab == Tabs.PRIVATE_WELL)
                    wellTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellTab.setState(event.getState());
            }
        });

        try {
            sdwisTab = new SDWISTab(window);
            AbsolutePanel sdwisTabPanel = (AbsolutePanel)def.getWidget("sdwisDomainPanel");
            sdwisTabPanel.add(sdwisTab);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("sdwis tab initialize: " + e.getMessage());
        }

        addScreenHandler(sdwisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (SampleManager.SDWIS_DOMAIN_FLAG.equals(manager.getSample().getDomain()))
                    sdwisTab.setData(manager);
                else
                    sdwisTab.setData(null);

                if (tab == Tabs.SDWIS)
                    sdwisTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisTab.setState(event.getState());
            }
        });

        sampleItemTab = new SampleItemTab(def, window);
        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTab.setData(dataBundle);

                if (tab == Tabs.SAMPLE_ITEM)
                    sampleItemTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTab.setState(event.getState());
            }
        });

        analysisTab = new AnalysisTab(def, window);
        addScreenHandler(analysisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTab.setData(dataBundle);
                if (tab == Tabs.ANALYSIS) {
                    analysisTab.draw();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });

        testResultsTab = new ResultTab(def, window, this);
        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                testResultsTab.setData(dataBundle);

                if (tab == Tabs.TEST_RESULT)
                    testResultsTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });

        analysisNotesTab = new AnalysisNotesTab(def, window, "anExNotesPanel", "anExNoteButton",
                                                "anIntNotesPanel", "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisNotesTab.setData(dataBundle);
                if (tab == Tabs.ANALYSIS_NOTES)
                    analysisNotesTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisNotesTab.setState(event.getState());
            }
        });

        sampleNotesTab = new SampleNotesTab(def, window, "sampleExtNotesPanel",
                                            "sampleExtNoteButton", "sampleIntNotesPanel",
                                            "sampleIntNoteButton");
        addScreenHandler(sampleNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNotesTab.setManager(manager);
                if (tab == Tabs.SAMPLE_NOTES)
                    sampleNotesTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleNotesTab.setState(event.getState());
            }
        });

        storageTab = new StorageTab(def, window);
        addScreenHandler(storageTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                storageTab.setData(dataBundle);

                if (tab == Tabs.STORAGE)
                    storageTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTab.setState(event.getState());
            }
        });

        qaEventsTab = new QAEventsTab(def, window);
        addScreenHandler(qaEventsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                qaEventsTab.setData(dataBundle);
                qaEventsTab.setManager(manager);

                if (tab == Tabs.QA_EVENTS)
                    qaEventsTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaEventsTab.setState(event.getState());
            }
        });

        auxDataTab = new AuxDataTab(def, window);
        addScreenHandler(auxDataTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTab.setManager(manager);

                if (tab == Tabs.AUX_DATA)
                    auxDataTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if (EnumSet.of(State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });

    }

    private void executeQuery(final Query query) {
        window.setBusy(consts.get("querying"));

        service.callList("query", query, new AsyncCallback<ArrayList<SampleDataBundle>>() {
            public void onSuccess(ArrayList<SampleDataBundle> result) {
                manager = null;

                if (result.size() > 0)
                    setState(State.DISPLAY);
                else
                    setState(State.DEFAULT);

                completeReleaseTable.setModel(getModel(result));

                if (result.size() > 0)
                    completeReleaseTable.selectRowAt(0);

                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                int page;

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    completeReleaseTable.clear();
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    page = query.getPage();
                    query.setPage(page - 1);
                    window.setError(consts.get("noMoreRecordInDir"));
                } else {
                    completeReleaseTable.clear();
                    com.google.gwt.user.client.Window.alert("Error: envsample call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    public ArrayList<Row> getModel(ArrayList<SampleDataBundle> result) {
        ArrayList<Row> model;
        Row analysis;
        SampleManager sampleMan;
        SampleDO sample;
        AnalysisViewDO data;

        model = new ArrayList<Row>();

        if (result == null)
            return model;

        try {
            for (SampleDataBundle bundle : result) {
                sampleMan = bundle.getSampleManager();
                sample = sampleMan.getSample();
                data = sampleMan.getSampleItems()
                                .getAnalysisAt(bundle.getSampleItemIndex())
                                .getAnalysisAt(bundle.getAnalysisIndex());
                analysis = new Row(5);

                analysis.setCell(0,sample.getAccessionNumber());
                analysis.setCell(1,data.getTestName());
                analysis.setCell(2,data.getMethodName());
                analysis.setCell(3,data.getStatusId());
                analysis.setCell(4,sample.getStatusId());
                analysis.setData(bundle);
                model.add(analysis);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("getModel: " + e.getMessage());
        }

        return model;
    }

    private boolean canEdit() {
        return ( !sampleReleasedId.equals(manager.getSample().getStatusId()));
    }

    protected void query() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        dataBundle = null;

        showTabs(Tabs.BLANK);
        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        sampleTab.draw();
        environmentalTab.draw();
        wellTab.draw();
        sdwisTab.draw();
        sampleItemTab.draw();
        analysisTab.draw();
        testResultsTab.draw();
        analysisNotesTab.draw();
        sampleNotesTab.draw();
        storageTab.draw();
        qaEventsTab.draw();
        auxDataTab.draw();

        completeReleaseTable.startEditing(0, 0);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void update() {
        if (completeReleaseTable.isMultipleRowsSelected()) {
            window.setError(consts.get("cantUpdateMultiple"));
            return;
        }

        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            // update row data
            updateTableRow(completeReleaseTable.getSelectedRow());

            setState(State.UPDATE);

            if ( !canEdit()) {
                abort();
                window.setError(consts.get("cantUpdateReleasedException"));
                return;
            }

            DataChangeEvent.fire(this);
            window.clearStatus();

        } catch (EntityLockedException e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }

    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;
            query = new Query();
            query.setFields(getQueryFields());
            QueryData qd = new QueryData();
            qd.key = SampleMeta.getDomain();
            qd.query = "!Q";
            qd.type = QueryData.Type.STRING;
            query.setFields(qd);
            executeQuery(query);

        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.update();
                updateAllRows(manager.getSample().getAccessionNumber());

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);

                if ( !e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void commitWithWarnings() {
        clearErrors();

        manager.getSample().setStatusId(sampleErrorStatusId);

        if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();
                updateAllRows(manager.getSample().getAccessionNumber());

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }

    public void abort() {
        String domain;
        
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            historyUtility.setManager(manager);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {

            try {
                manager = manager.abortUpdate();
                updateTableRow(completeReleaseTable.getSelectedRow());
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();

            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else {
            window.clearStatus();
        }
    }

    private void resetScreen() {
        boolean showingDomainTab;

        showingDomainTab = (tab == Tabs.ENVIRONMENT || tab == Tabs.PRIVATE_WELL || tab == Tabs.SDWIS);

        if (completeReleaseTable.isMultipleRowsSelected()) {
            showTabs(Tabs.BLANK);

        } else if (manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.ENVIRONMENT;

            showTabs(Tabs.SAMPLE, Tabs.ENVIRONMENT, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS,
                     Tabs.TEST_RESULT, Tabs.ANALYSIS_NOTES, Tabs.SAMPLE_NOTES, Tabs.STORAGE,
                     Tabs.QA_EVENTS, Tabs.AUX_DATA);

        } else if (manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.PRIVATE_WELL;

            showTabs(Tabs.SAMPLE, Tabs.PRIVATE_WELL, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS,
                     Tabs.TEST_RESULT, Tabs.ANALYSIS_NOTES, Tabs.SAMPLE_NOTES, Tabs.STORAGE,
                     Tabs.QA_EVENTS, Tabs.AUX_DATA);

        } else if (manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.SDWIS;

            showTabs(Tabs.SAMPLE, Tabs.SDWIS, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS, Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES, Tabs.SAMPLE_NOTES, Tabs.STORAGE, Tabs.QA_EVENTS,
                     Tabs.AUX_DATA);
        }

        DataChangeEvent.fire(this);
        window.clearStatus();
    }

    private SampleDataBundle getAnalysisBundle(Integer id) throws Exception {
        int sindex,aindex;
        SampleItemManager siManager = manager.getSampleItems();
        
        sindex = -1;
        aindex = -1;
        for (int i = 0; i < siManager.count(); i++ ) {
            for (int j = 0; j < siManager.getAnalysisAt(i).count(); j++ ) {
                if (siManager.getAnalysisAt(i).getAnalysisAt(j).getId().equals(id)) {
                    sindex = i;
                    aindex = j;
                    break;
                }
            }
        }
        if (sindex < -1)
            return null;
        return siManager.getAnalysisAt(sindex).getBundleAt(aindex);
    }

    private void updateAllRows(Integer accessionNumber) {
        Row row;
        for (int i = 0; i < completeReleaseTable.getRowCount(); i++ ) {
            row = completeReleaseTable.getRowAt(i);

            if (accessionNumber.equals(row.getCell(0)))
                updateTableRow(i);
        }
    }

    private void updateTableRow(int index) {
        int itemIndex, anIndex;
        Row row;
        SampleDO sample;
        AnalysisViewDO data;
        SampleDataBundle bundle;

        try {
            row = completeReleaseTable.getRowAt(index);
            bundle = getCurrentRowBundle(row, manager);

            row.setData(bundle);
            if (index == completeReleaseTable.getSelectedRow())
                dataBundle = bundle;

            // get the DOs from the new bundle
            itemIndex = bundle.getSampleItemIndex();
            anIndex = bundle.getAnalysisIndex();
            sample = bundle.getSampleManager().getSample();
            data = bundle.getSampleManager()
                         .getSampleItems()
                         .getAnalysisAt(itemIndex)
                         .getAnalysisAt(anIndex);

            updateTableRowCells(index, sample, data);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("updateSelectedTableRow: " + e.getMessage());
        }
    }

    private SampleDataBundle getCurrentRowBundle(Row row, SampleManager manager) throws Exception {
        int itemIndex, anIndex;
        Integer analysisId;
        SampleDataBundle oldBundle, bundle;

        oldBundle = (SampleDataBundle)row.getData();
        itemIndex = oldBundle.getSampleItemIndex();
        anIndex = oldBundle.getAnalysisIndex();
        analysisId = oldBundle.getSampleManager()
                              .getSampleItems()
                              .getAnalysisAt(itemIndex)
                              .getAnalysisAt(anIndex)
                              .getId();

        if (analysisId.equals(manager.getSampleItems()
                                     .getAnalysisAt(itemIndex)
                                     .getAnalysisAt(anIndex)
                                     .getId()))
            bundle = manager.getSampleItems().getAnalysisAt(itemIndex).getBundleAt(anIndex);
        else
            bundle = getAnalysisBundle(analysisId);

        return bundle;
    }

    private void initializeDropdowns() {
        ArrayList<org.openelis.gwt.widget.Item<Integer>> model;
        window.clearStatus();

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
            sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
            analysisOnHoldId = DictionaryCache.getIdFromSystemName("analysis_on_hold");
            analysisCompletedId = DictionaryCache.getIdFromSystemName("analysis_completed");

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

        // sample status dropdown
        model = new ArrayList<org.openelis.gwt.widget.Item<Integer>>();
        model.add(new org.openelis.gwt.widget.Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_status"))
            model.add(new org.openelis.gwt.widget.Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId())).setModel(model);
        ((Dropdown<Integer>)completeReleaseTable.getColumnWidget(SampleMeta.getStatusId())).setModel(model);

        // analysis status dropdown
        model = new ArrayList<org.openelis.gwt.widget.Item<Integer>>();
        model.add(new org.openelis.gwt.widget.Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new org.openelis.gwt.widget.Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)completeReleaseTable.getColumnWidget(SampleMeta.getAnalysisStatusId())).setModel(model);

    }

    private void drawTabs() {
        switch (tab) {
            case ENVIRONMENT:
                environmentalTab.draw();
                break;
            case PRIVATE_WELL:
                wellTab.draw();
                break;
            case SDWIS:
                sdwisTab.draw();
                break;
            case SAMPLE:
                sampleTab.draw();
                break;
            case SAMPLE_ITEM:
                sampleItemTab.draw();
                break;
            case ANALYSIS:
                analysisTab.draw();
                break;
            case TEST_RESULT:
                testResultsTab.draw();
                break;
            case ANALYSIS_NOTES:
                analysisNotesTab.draw();
                break;
            case SAMPLE_NOTES:
                sampleNotesTab.draw();
                break;
            case STORAGE:
                storageTab.draw();
                break;
            case QA_EVENTS:
                qaEventsTab.draw();
                break;
            case AUX_DATA:
                auxDataTab.draw();
                break;
        }
    }

    private void showTabs(Tabs... tabs) {
        List<Tabs> tabList = Arrays.asList(tabs);

        for (Tabs tab : Tabs.values()) {
            sampleContent.setTabVisible(tab.ordinal(), tabList.contains(tab));
        }

        if (tabs[0] == Tabs.BLANK) {
            sampleContent.selectTab(tabs[0].ordinal());
            sampleContent.getTabBar().setStyleName("None");
        } else {
            if (tab == Tabs.BLANK)
                sampleContent.selectTab(tabs[0].ordinal());
            else
                sampleContent.selectTab(tab.ordinal());
            sampleContent.getTabBar().setStyleName("gwt-TabBar");
        }
    }

    private void doReport() {
        Request req = service.call("doFinalReport", new AsyncCallback<ReportProgress>() {
            public void onSuccess(final ReportProgress rp) {
                window.setDone("Done");
                window.setProgress(0);
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        com.google.gwt.user.client.Window.open("Report.html?id=" + rp.name, "Report",
                                    "toolbar=no,location=no,menubar=no,status=no,titlebar=no");
                    }
                });

            }

            public void onFailure(Throwable caught) {
                com.google.gwt.user.client.Window.alert(caught.getMessage());
            }
        });
        getProgress(req);

    }

    public void getProgress(final Request req) {
        if ( !req.isPending()) {
            window.setDone("Done");
            window.setProgress(0);
            return;
        }
        service.call("getProgress", new AsyncCallback<ReportProgress>() {
            public void onSuccess(ReportProgress fp) {
                window.setBusy(consts.get("generatingReport"));
                window.setProgress(fp.generated);
                Timer timer = new Timer() {
                    public void run() {
                        getProgress(req);
                    }
                };
                timer.schedule(500);
            }

            public void onFailure(Throwable caught) {
                com.google.gwt.user.client.Window.alert(caught.getMessage());
            }
        });
    }

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void release() {
        ArrayList<Row> rows;
        LocalizedException ex;
        //int[] indexList;
        Row row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisViewDO data;
        String errorMsg;
        Integer[] sels;

        bundle = null;
        
        sels = completeReleaseTable.getSelectedRows();
        rows = new ArrayList<Row>();
        
        for(int i = 0; i < sels.length; i++) 
        	rows.add(completeReleaseTable.getRowAt(sels[i]));
        
        
        //indexList = completeReleaseTable.getSelectedRows();
        hash = new HashMap<Integer, Item>();

        if (rows.size() > 1) {
            ex = new LocalizedException("releaseMultipleWarning", String.valueOf(rows.size()));
            if ( !com.google.gwt.user.client.Window.confirm(ex.getMessage()))
                return;
        }
        window.setBusy(consts.get("updating"));

        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.getData();
            item = hash.get(bundle.getSampleManager().getSample().getId());
            man = bundle.getSampleManager();

            try {
                if (item == null) {
                    man = man.fetchForUpdate();
                    item = new Item(man, 1);
                    hash.put(man.getSample().getId(), item);
                } else if (item.count != -1) {
                    item.count++ ;
                    man = item.sampleManager;
                }

                bundle = getCurrentRowBundle(row, man);
                row.setData(bundle);
                bundle.getSampleManager()
                      .getSampleItems()
                      .getAnalysisAt(bundle.getSampleItemIndex())
                      .releaseAnalysisAt(bundle.getAnalysisIndex());

            } catch (EntityLockedException e) {
                hash.put(man.getSample().getId(), new Item(man, -1));
                com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                try {
                    data = man.getSampleItems()
                              .getAnalysisAt(bundle.getSampleItemIndex())
                              .getAnalysisAt(bundle.getAnalysisIndex());
                    item.count-- ;

                    errorMsg = "Cannot release " + data.getTestName() + ":" + data.getMethodName() +
                               " on accession #" + man.getSample().getAccessionNumber() + ":\n";

                    for (int l = 0; l < e.size(); l++ )
                        errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";

                    com.google.gwt.user.client.Window.alert(errorMsg);
                    window.clearStatus();
                } catch (Exception f) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    window.clearStatus();
                }

            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            }
        }

        updateAndRefreshTable(rows, sels, hash, bundle);
        window.clearStatus();
    }

    private void unrelease() {
        Integer[] indexList;
        ArrayList<Row> rows;
        Row row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisViewDO data;
        AnalysisManager anaMan;
        String errorMsg;

        indexList = completeReleaseTable.getSelectedRows();
        rows = new ArrayList<Row>();
        for(int i = 0; i < indexList.length; i++) 
        	rows.add(completeReleaseTable.getRowAt(indexList[i]));
        
        
        bundle = null;
        hash = new HashMap<Integer, Item>();
        row = rows.get(0);
        bundle = (SampleDataBundle)row.getData();
        item = hash.get(bundle.getSampleManager().getSample().getId());
        man = bundle.getSampleManager();

        window.setBusy(consts.get("updating"));
        try {
            man = man.fetchForUpdate();
            item = new Item(man, 1);
            hash.put(man.getSample().getId(), item);
            bundle = getCurrentRowBundle(row, man);
            row.setData(bundle);
            anaMan = bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            anaMan.setInternalNotes(noteMan, bundle.getAnalysisIndex());
            anaMan.unreleaseAnalysisAt(bundle.getAnalysisIndex());
        } catch (EntityLockedException e) {
            hash.put(man.getSample().getId(), new Item(man, -1));
            com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
                         ":\n\n" + e.getMessage());
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            try {
                data = man.getSampleItems()
                          .getAnalysisAt(bundle.getSampleItemIndex())
                          .getAnalysisAt(bundle.getAnalysisIndex());
                item.count-- ;

                errorMsg = "Cannot unrelease " + data.getTestName() + ":" + data.getMethodName() +
                           " on accession #" + man.getSample().getAccessionNumber() + ":\n";

                for (int l = 0; l < e.size(); l++ )
                    errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";

                com.google.gwt.user.client.Window.alert(errorMsg);

            } catch (Exception f) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                window.clearStatus();
            }

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
                         ":\n\n" + e.getMessage());
            window.clearStatus();
        }

        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void complete() {
        ArrayList<Row> rows;
        Integer[] indexList;
        Row row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        ValidationErrorsList errorsList;
        String errorMsg;

        bundle = null;
        
        indexList = completeReleaseTable.getSelectedRows();
        rows = new ArrayList<Row>();
        for(int i = 0; i < indexList.length; i++)
        	rows.add(completeReleaseTable.getRowAt(indexList[i]));
        
        hash = new HashMap<Integer, Item>();

        window.setBusy(consts.get("updating"));
        
        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.getData();
            item = hash.get(bundle.getSampleManager().getSample().getId());
            man = bundle.getSampleManager();

            try {
                if (item == null) {
                    man = man.fetchForUpdate();
                    item = new Item(man, 1);
                    hash.put(man.getSample().getId(), item);

                } else if (item.count != -1) {
                    item.count += 1;
                    man = item.sampleManager;

                }

                anMan = bundle.getSampleManager()
                              .getSampleItems()
                              .getAnalysisAt(bundle.getSampleItemIndex());
                anDO = anMan.getAnalysisAt(bundle.getAnalysisIndex());
                if (analysisCompletedId.equals(anDO.getStatusId())) {
                    errorsList = new ValidationErrorsList();
                    errorsList.add(new FormErrorException("analysisAlreadyComplete"));
                    throw errorsList;
                }

                if ( !analysisOnHoldId.equals(anDO.getStatusId()) ||
                    com.google.gwt.user.client.Window.confirm(consts.get("onHoldWarning"))) {
                    bundle = getCurrentRowBundle(row, man);
                    row.setData(bundle);
                    bundle.getSampleManager()
                          .getSampleItems()
                          .getAnalysisAt(bundle.getSampleItemIndex())
                          .completeAnalysisAt(bundle.getAnalysisIndex());
                }

            } catch (EntityLockedException e) {
                hash.put(man.getSample().getId(), new Item(man, -1));
                com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                try {
                    anDO = man.getSampleItems()
                              .getAnalysisAt(bundle.getSampleItemIndex())
                              .getAnalysisAt(bundle.getAnalysisIndex());
                    item.count = item.count - 1;

                    errorMsg = "Cannot complete " + anDO.getTestName() + ":" +
                               anDO.getMethodName() + " on accession #" +
                               man.getSample().getAccessionNumber() + ":\n";

                    for (int l = 0; l < e.size(); l++ )
                        errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";

                    com.google.gwt.user.client.Window.alert(errorMsg);

                } catch (Exception f) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    window.clearStatus();
                }

            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            }
        }

        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void updateAndRefreshTable(ArrayList<Row> rows,
                                       Integer[] indexList,
                                       HashMap<Integer, Item> hash,
                                       SampleDataBundle bundle) {
        Row row;
        int index;
        Item item;

        for (int j = 0; j < rows.size(); j++ ) {
            index = indexList[j];
            row = rows.get(j);
            bundle = (SampleDataBundle)row.getData();
            item = hash.get(bundle.getSampleManager().getSample().getId());

            try {
                if (item != null && item.count > 0) {
                    item.sampleManager.update();
                    item.count = -1;
                }

                // update the row
                updateTableRowCells(index, bundle.getSampleManager().getSample(),
                                    bundle.getSampleManager()
                                          .getSampleItems()
                                          .getAnalysisAt(bundle.getSampleItemIndex())
                                          .getAnalysisAt(bundle.getAnalysisIndex()));

            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(consts.get("errorSampleAccNum") +
                             bundle.getSampleManager().getSample().getAccessionNumber() + ":\n\n" +
                             e.getMessage());
            }
        }

        // if the tabs are showing data we need to make sure to refresh them
        if (rows.size() == 1) {
            dataBundle = bundle;
            manager = bundle.getSampleManager();
            resetScreen();
        }
    }

    private void updateTableRowCells(int row, SampleDO sample, AnalysisViewDO analysis) {
        completeReleaseTable.setValueAt(row, 0, sample.getAccessionNumber());
        completeReleaseTable.setValueAt(row, 1, analysis.getTestName());
        completeReleaseTable.setValueAt(row, 2, analysis.getMethodName());
        completeReleaseTable.setValueAt(row, 3, analysis.getStatusId());
        completeReleaseTable.setValueAt(row, 4, sample.getStatusId());
    }

    private class Item {
        private SampleManager sampleManager;
        private int           count;

        public Item(SampleManager man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
    
    private void showNote() {                        
        final int index = dataBundle.getSampleItemIndex();        
        ModalWindow modal;
        AnalysisManager man;
        
        try {
            man = dataBundle.getSampleManager().getSampleItems()
                                    .getAnalysisAt(index);
            noteMan = man.getInternalNotesAt(index);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }
                        
        if (internalEditNote == null) {
            try {
                internalEditNote = new EditNoteScreen();
                internalEditNote.addActionHandler(this);                
            } catch (Exception e) {
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
                return;
            }
        }

        modal = new ModalWindow();
        modal.setName(consts.get("standardNote"));
        modal.setContent(internalEditNote);

        internalNote = null;
        try {
            internalNote = noteMan.getEditingNote();
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("error!");
        }
        internalNote.setSystemUser(OpenELIS.getSystemUserPermission().getLoginName());
        internalNote.setSystemUserId(OpenELIS.getSystemUserPermission().getSystemUserId());
        internalNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        internalEditNote.setNote(internalNote);
    }

    public void onSelection(SelectionEvent<Integer> event) {
        switch (event.getSelectedItem().intValue()) {
            case 0:
                break;
            case 1:   
                showNote();
                break;
        }
        
    }

    public void onAction(ActionEvent<Action> event) {                
        if (event.getAction() == EditNoteScreen.Action.OK) {
            if (DataBaseUtil.isEmpty(internalNote.getText()) || 
                            DataBaseUtil.isEmpty(internalNote.getSubject())) {                          
                noteMan.removeEditingNote(); 
                showConfirm();  
            } else {                        
                unrelease();
            }
            analysisNotesTab.draw();
        } else {
            showConfirm();
        }
    }
    
    private void showConfirm() {
        if (confirm == null) {
            confirm = new Confirm(Confirm.Type.QUESTION,
                              consts.get("unreleaseAnalysisCaption"),
                              consts.get("unreleaseAnalysisMessage"),
                              "Cancel", "OK");
            confirm.addSelectionHandler(this);
        }
        confirm.show();   
        
    }
}
