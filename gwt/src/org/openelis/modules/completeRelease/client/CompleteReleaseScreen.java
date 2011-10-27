package org.openelis.modules.completeRelease.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ReportStatus;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
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
import org.openelis.modules.sample.client.TestPrepUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class CompleteReleaseScreen extends Screen implements HasActionHandlers,
                                                 SelectionHandler<Integer>,
                                                 ActionHandler<EditNoteScreen.Action> {

    protected Tabs                   tab;
    protected ArrayList<Tabs>        tabIndexes = new ArrayList<Tabs>();
    protected TextBox                accessionNumber, clientReference;
    protected TextBox<Integer>       orderNumber;
    protected TextBox<Datetime>      collectedTime;

    protected Dropdown<Integer>      statusId;
    protected AppButton              removeRow, releaseButton, reportButton, completeButton,
                                     addItem, addAnalysis, queryButton, updateButton,
                                     commitButton, abortButton;
    protected CheckBox               autoPreview;
    protected Label<String>          autoPreviewText;

    protected CalendarLookUp         collectedDate, receivedDate;

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
    private TableWidget              completeReleaseTable;
    private EditNoteScreen           internalEditNote;
    private NoteViewDO               internalNote;
    private NoteManager              noteMan;
    private Confirm                  confirm;

    private TestPrepUtility          testPrepUtil;

    protected MenuItem               unreleaseAnalysis, previewFinalReport, historySample,
                                     historySampleSpec, historySampleProject, historySampleOrganization,
                                     historySampleItem, historyAnalysis, historyCurrentResult,
                                     historyStorage, historySampleQA, historyAnalysisQA,
                                     historyAuxData;

    private ScreenService            finalReportService;
    private Integer                  analysisOnHoldId, lastAccession;


    private enum Tabs {
        BLANK, SAMPLE, ENVIRONMENT, PRIVATE_WELL, SDWIS, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public CompleteReleaseScreen() throws Exception {
        super((ScreenDefInt)GWT.create(CompleteReleaseDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.completeRelease.server.CompleteReleaseService");
        finalReportService = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");

        userPermission = UserCache.getPermission().getModule("samplecompleterelease");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Complete and Release Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        tab = Tabs.BLANK;
        manager = SampleManager.getInstance();

        try {
           CategoryCache.getBySystemNames("sample_status",
                                                         "analysis_status",
                                                         "type_of_sample",
                                                         "source_of_sample",
                                                         "sample_container",
                                                         "unit_of_measure",
                                                         "qaevent_type",
                                                         "aux_field_value_type",
                                                         "organization_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.setState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });

        completeButton = (AppButton)def.getWidget("complete");
        addScreenHandler(completeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                complete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completeButton.enable(event.getState() == State.DISPLAY);
            }
        });

        releaseButton = (AppButton)def.getWidget("release");
        addScreenHandler(releaseButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                release();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releaseButton.enable(event.getState() == State.DISPLAY);
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

        historyUtility = new SampleHistoryUtility(window);

        unreleaseAnalysis = (MenuItem)def.getWidget("unreleaseAnalysis");
        addScreenHandler(unreleaseAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> rows;

                rows = completeReleaseTable.getSelections();
                if (rows.size() != 1) {
                    Window.alert(consts.get("selOneRowUnrelease"));
                    return;
                }

                showConfirm();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unreleaseAnalysis.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        previewFinalReport = (MenuItem)def.getWidget("previewFinalReport");
        addScreenHandler(previewFinalReport, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                previewFinalReport.enable(true);
            }
        });

        autoPreview = (CheckBox)def.getWidget("autoPreview");
        addScreenHandler(autoPreview, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                previewFinalReport();
            }

            public void onDataChange(DataChangeEvent event) {
                previewFinalReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                autoPreview.enable(true);
            }
        });

        autoPreviewText = (Label<String>)def.getWidget("autoPreviewText");
        addScreenHandler(autoPreviewText, new ScreenEventHandler<String>() {
            public void onClick(ClickEvent event) {
                if ("N".equals(autoPreview.getValue()))
                    autoPreview.setValue("Y", true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                autoPreviewText.setStyleName("enabled");
            }
        });

        historySample = (MenuItem)def.getWidget("historySample");
        addScreenHandler(historySample, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySample();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySample.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
                historySampleSpec.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleProject = (MenuItem)def.getWidget("historySampleProject");
        addScreenHandler(historySampleProject, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleProject();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleProject.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleOrganization = (MenuItem)def.getWidget("historySampleOrganization");
        addScreenHandler(historySampleOrganization, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleOrganization();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleOrganization.enable(EnumSet.of(State.DISPLAY)
                                                        .contains(event.getState()));
            }
        });

        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleItem();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysis();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                
                historyUtility.setManager(manager);
                try {                    
                    historyUtility.historyCurrentResult(dataBundle.getSampleItemIndex(), dataBundle.getAnalysisIndex());
                    window.clearStatus();
                } catch (Exception e) {
                    window.clearStatus();
                    Window.alert("historyCurrentResult: " + e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyStorage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysisQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAuxData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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

        completeReleaseTable = (TableWidget)def.getWidget("completeReleaseTable");
        addScreenHandler(completeReleaseTable, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                completeReleaseTable.enable(true);
                completeReleaseTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        completeReleaseTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        completeReleaseTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent<TableDataRow> event) {
                if (state == State.UPDATE)
                    event.cancel();
            }
        });

        completeReleaseTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if (completeReleaseTable.getSelections().size() == 1) {
                    dataBundle = (SampleDataBundle)event.getSelectedItem().row.data;
                    manager = dataBundle.getSampleManager();
                    setState(State.DISPLAY);
                    previewFinalReport();
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
            Window.alert("env tab initialize: " + e.getMessage());
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
            Window.alert("well tab initialize: " + e.getMessage());
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
            Window.alert("sdwis tab initialize: " + e.getMessage());
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
        
        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent event) {
                if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    try {
                        if (testPrepUtil == null) {
                            testPrepUtil = new TestPrepUtility();
                            testPrepUtil.setScreen(completeScreen);

                            testPrepUtil.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                                public void onAction(ActionEvent<TestPrepUtility.Action> event) {
                                    ArrayList<SampleDataBundle> list;
                                    
                                    list = (ArrayList<SampleDataBundle>)event.getData();                                    
                                    dataBundle = (SampleDataBundle)list.get(0);
                                    manager = dataBundle.getSampleManager();
                                    try {
                                        updateTableRowCells(completeReleaseTable.getSelectedRow(),
                                                            manager.getSample(),
                                                            manager.getSampleItems()
                                                                   .getAnalysisAt(dataBundle.getSampleItemIndex())
                                                                   .getAnalysisAt(dataBundle.getAnalysisIndex()));

                                        resetScreen();
                                    } catch (Exception e) {
                                        Window.alert("analysisTestChanged: " + e.getMessage());
                                    }
                                }
                            });
                        }

                        testPrepUtil.lookup(dataBundle, TestPrepUtility.Type.TEST,
                                            (Integer)event.getData());
                    } catch (Exception e) {
                        Window.alert("analysisTestChanged: " + e.getMessage());
                    }
                }
            }
        });

        /*
         * in order to notify ResultTab that an analysis' unit of measure has changed
         */
        analysisTab.addActionHandler(testResultsTab);
        
        testResultsTab.addActionHandler(new ActionHandler<ResultTab.Action>() {
            @SuppressWarnings("unchecked")
            public void onAction(ActionEvent<ResultTab.Action> event) {
                if (event.getAction() == ResultTab.Action.REFLEX_ADDED) {
                    if (testPrepUtil == null) {
                        testPrepUtil = new TestPrepUtility();
                        testPrepUtil.setScreen(completeScreen);

                        testPrepUtil.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                            public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                            }
                        });
                    }

                    try {
                        testPrepUtil.lookup((ArrayList<SampleDataBundle>)event.getData());
                    } catch (Exception e) {
                        Window.alert("loadFromEdit: " + e.getMessage());
                    }
                }
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
        
        sampleNotesTab = new SampleNotesTab(def,
                                            window,
                                            "sampleExtNotesPanel",
                                            "sampleExtNoteButton",
                                            "sampleIntNotesPanel",
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

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        window.clearStatus();

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {            
            analysisOnHoldId = DictionaryCache.getIdBySystemName("analysis_on_hold");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // sample status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId())).setModel(model);
        ((Dropdown<Integer>)completeReleaseTable.getColumnWidget(SampleMeta.getStatusId())).setModel(model);

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)completeReleaseTable.getColumnWidget(SampleMeta.getAnalysisStatusId())).setModel(model);

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

        completeReleaseTable.select(0, 0);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void update() {
        if (completeReleaseTable.getSelections().size() > 1) {
            window.setError(consts.get("cantUpdateMultiple"));
            return;
        }

        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            // update row data
            updateAllRows(manager.getSample().getAccessionNumber());

            setState(State.UPDATE);

            DataChangeEvent.fire(this);
            window.clearStatus();

        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    private void complete() {
        int indexList[];
        Item item;
        String errorMsg;
        TableDataRow row;
        SampleManager man;
        AnalysisManager aman;
        AnalysisViewDO data;
        SampleDataBundle bundle;
        ArrayList<TableDataRow> rows;
        HashMap<Integer, Item> hash;

        bundle = null;
        rows = completeReleaseTable.getSelections();
        hash = new HashMap<Integer, Item>();

        window.setBusy(consts.get("updating"));

        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.data;
            item = hash.get(bundle.getSampleManager().getSample().getId());

            try {
                if (item == null) {
                    /*
                     * refetch and lock
                     */
                    man = bundle.getSampleManager();
                    man = man.fetchForUpdate();
                    item = new Item(man, 1);
                    hash.put(man.getSample().getId(), item);
                } else if (item.count == -1) {
                    continue; // unlockable
                } else {
                    man = item.sampleManager;
                    item.count++ ;
                }
                /*
                 * update the manager for this row
                 */
                bundle = getCurrentRowBundle(bundle, man);
                row.data = bundle;

                /*
                 * give them warning for on-hold analysis
                 */
                aman = bundle.getSampleManager()
                             .getSampleItems()
                             .getAnalysisAt(bundle.getSampleItemIndex());
                data = aman.getAnalysisAt(bundle.getAnalysisIndex());
                if (analysisOnHoldId.equals(data.getStatusId()) &&
                    !Window.confirm(consts.get("onHoldWarning")))
                    continue;
                aman.completeAnalysisAt(bundle.getAnalysisIndex());
            } catch (EntityLockedException e) {
                /*
                 * mark the sample as not updateable
                 */
                man = bundle.getSampleManager();
                hash.put(man.getSample().getId(), new Item(man, -1));
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
            } catch (ValidationErrorsList e) {
                /*
                 * mark this analysis as not completable
                 */
                man = bundle.getSampleManager();
                item.count-- ;
                try {
                    aman = man.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                    data = aman.getAnalysisAt(bundle.getAnalysisIndex());
                    errorMsg = "Cannot complete " + data.getTestName() + ":" +
                               data.getMethodName() + " on accession #" +
                               man.getSample().getAccessionNumber() + ":\n";
                    for (int l = 0; l < e.size(); l++ )
                        errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";
                    Window.alert(errorMsg);
                } catch (Exception f) {
                    Window.alert(e.getMessage());
                }
            } catch (Exception e) {
                /*
                 * rollback the entire manager for this accession
                 */
                man = bundle.getSampleManager();
                hash.put(man.getSample().getId(), new Item(man, 0));
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
            }
        }

        indexList = completeReleaseTable.getSelectedRows();
        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void release() {
        int indexList[];
        Item item;
        String errorMsg;
        TableDataRow row;
        SampleManager man;
        AnalysisManager aman;
        AnalysisViewDO data;
        SampleDataBundle bundle;
        ArrayList<TableDataRow> rows;
        HashMap<Integer, Item> hash;
        LocalizedException warn;

        bundle = null;
        rows = completeReleaseTable.getSelections();
        hash = new HashMap<Integer, Item>();

        if (rows.size() > 1) {
            warn = new LocalizedException("releaseMultipleWarning", String.valueOf(rows.size()));
            if ( !Window.confirm(warn.getMessage()))
                return;
        }
        window.setBusy(consts.get("updating"));

        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.data;
            item = hash.get(bundle.getSampleManager().getSample().getId());

            try {
                if (item == null) {
                    /*
                     * refetch and lock
                     */
                    man = bundle.getSampleManager();
                    man = man.fetchForUpdate();
                    item = new Item(man, 1);
                    hash.put(man.getSample().getId(), item);
                } else if (item.count == -1) {
                    continue;
                } else {
                    man = item.sampleManager;
                    item.count++ ;
                }
                /*
                 * update the manager for this row
                 */
                bundle = getCurrentRowBundle(bundle, man);
                row.data = bundle;

                bundle.getSampleManager()
                      .getSampleItems()
                      .getAnalysisAt(bundle.getSampleItemIndex())
                      .releaseAnalysisAt(bundle.getAnalysisIndex());
            } catch (EntityLockedException e) {
                /*
                 * mark the sample as not updateable
                 */
                man = bundle.getSampleManager();
                hash.put(man.getSample().getId(), new Item(man, -1));
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
            } catch (ValidationErrorsList e) {
                /*
                 * mark this analysis as not completable
                 */
                man = bundle.getSampleManager();
                item.count-- ;
                try {
                    aman = man.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                    data = aman.getAnalysisAt(bundle.getAnalysisIndex());
                    errorMsg = "Cannot release " + data.getTestName() + ":" + data.getMethodName() +
                               " on accession #" + man.getSample().getAccessionNumber() + ":\n";
                    for (int l = 0; l < e.size(); l++ )
                        errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";
                    Window.alert(errorMsg);
                } catch (Exception f) {
                    Window.alert(e.getMessage());
                }
            } catch (Exception e) {
                /*
                 * rollback the entire manager for this accession
                 */
                man = bundle.getSampleManager();
                hash.put(man.getSample().getId(), new Item(man, 0));
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
            }
        }

        indexList = completeReleaseTable.getSelectedRows();
        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void unrelease() {
        int[] indexList;
        ArrayList<TableDataRow> rows;
        TableDataRow row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisViewDO data;
        AnalysisManager aman;
        String errorMsg;

        bundle = null;
        rows = completeReleaseTable.getSelections();

        if (rows.size() > 1) {
            Window.alert(consts.get("unreleaseMultipleException"));
            return;
        }
        window.setBusy(consts.get("updating"));

        row = rows.get(0);
        bundle = (SampleDataBundle)row.data;
        hash = new HashMap<Integer, Item>();
        item = null;

        try {
            /*
             * refetch with lock
             */
            man = bundle.getSampleManager();
            man = man.fetchForUpdate();
            item = new Item(man, 1);
            hash.put(man.getSample().getId(), item);
            /*
             * update the manager for this row
             */
            bundle = getCurrentRowBundle(bundle, man);
            row.data = bundle;

            /*
             * force them to enter an internal note for unreleasing
             */
            aman = man.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            aman.setInternalNotes(noteMan, bundle.getAnalysisIndex());
            aman.unreleaseAnalysisAt(bundle.getAnalysisIndex());
        } catch (EntityLockedException e) {
            /*
             * mark the sample as not updateable
             */
            man = bundle.getSampleManager();
            hash.put(man.getSample().getId(), new Item(man, -1));
            Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
                         ":\n\n" + e.getMessage());
        } catch (ValidationErrorsList e) {
            /*
             * mark this analysis as not completable
             */
            man = bundle.getSampleManager();
            if (item != null)
                item.count-- ;
            try {
                aman = man.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                data = aman.getAnalysisAt(bundle.getAnalysisIndex());
                errorMsg = "Cannot unrelease " + data.getTestName() + ":" + data.getMethodName() +
                           " on accession #" + man.getSample().getAccessionNumber() + ":\n";
                for (int l = 0; l < e.size(); l++ )
                    errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";
                Window.alert(errorMsg);
            } catch (Exception f) {
                Window.alert(e.getMessage());
            }
        } catch (Exception e) {
            /*
             * rollback the entire manager for this accession
             */
            man = bundle.getSampleManager();
            hash.put(man.getSample().getId(), new Item(man, 0));
            Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
                         ":\n\n" + e.getMessage());
        }

        indexList = completeReleaseTable.getSelectedRows();
        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void previewFinalReport() {
        Query query;
        QueryData field;

        if ( !"Y".equals(autoPreview.getValue()) || state != State.DISPLAY ||
            completeReleaseTable.getSelectedRows().length != 1 || manager == null ||
            manager.getSample().getAccessionNumber().equals(lastAccession))
            return;

        lastAccession = manager.getSample().getAccessionNumber();
        query = new Query();
        field = new QueryData();
        field.key = "ACCESSION_NUMBER";
        field.query = lastAccession.toString();
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);

        window.setBusy(consts.get("genReportMessage"));

        finalReportService.call("runReportForPreview", query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                url = "report?file=" + status.getMessage();
                Window.open(URL.encode(url), consts.get("finalReportSingleReprint"), null);
                window.setDone(consts.get("done"));
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });

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
                manager = manager.update();
                updateAllRows(manager.getSample().getAccessionNumber());

                setState(State.DISPLAY);
                lastAccession = null;
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                if ( !e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void commitWithWarnings() {
        clearErrors();

        manager.setStatusWithError(true);

        if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();
                updateAllRows(manager.getSample().getAccessionNumber());

                setState(Screen.State.DISPLAY);
                lastAccession = null;
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
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
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else {
            window.clearStatus();
        }
    }

    public ArrayList<TableDataRow> getModel(ArrayList<SampleDataBundle> result) {
        ArrayList<TableDataRow> model;
        TableDataRow analysis;
        SampleManager sampleMan;
        SampleDO sample;
        AnalysisViewDO data;

        model = new ArrayList<TableDataRow>();
        if (result == null)
            return model;

        try {
            for (SampleDataBundle bundle : result) {
                sampleMan = bundle.getSampleManager();
                sample = sampleMan.getSample();
                data = sampleMan.getSampleItems()
                                .getAnalysisAt(bundle.getSampleItemIndex())
                                .getAnalysisAt(bundle.getAnalysisIndex());
                analysis = new TableDataRow();

                analysis.cells.add(new TableDataCell(sample.getAccessionNumber()));
                analysis.cells.add(new TableDataCell(data.getTestName()));
                analysis.cells.add(new TableDataCell(data.getMethodName()));
                analysis.cells.add(new TableDataCell(data.getStatusId()));
                analysis.cells.add(new TableDataCell(sample.getStatusId()));
                analysis.data = bundle;
                model.add(analysis);
            }
        } catch (Exception e) {
            Window.alert("getModel: " + e.getMessage());
        }
        return model;
    }

    private void executeQuery(final Query query) {
        window.setBusy(consts.get("querying"));

        query.setRowsPerPage(500);
        service.callList("query", query, new AsyncCallback<ArrayList<SampleDataBundle>>() {
            public void onSuccess(ArrayList<SampleDataBundle> result) {
                manager = null;

                if (result.size() > 0)
                    setState(State.DISPLAY);
                else
                    setState(State.DEFAULT);
                lastAccession = null;
                completeReleaseTable.load(getModel(result));
                if (result.size() > 0)
                    completeReleaseTable.selectRow(0, true);
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
                    Window.alert("Error: envsample call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
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

    private void updateAndRefreshTable(ArrayList<TableDataRow> rows,
                                       int[] indexList,
                                       HashMap<Integer, Item> hash,
                                       SampleDataBundle bundle) {
        int i;
        Item item;
        TableDataRow row;
        SampleManager man;

        for (i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.data;
            item = hash.get(bundle.getSampleManager().getSample().getId());

            try {
                if (item == null)
                    continue;
                /*
                 * we want to redo the
                 */
                man = item.sampleManager;
                if (item.count > 0) {
                    try {
                        man.validate();
                    } catch (ValidationErrorsList e) {
                        man.setStatusWithError(true);
                    }
                    man = item.sampleManager.update();
                    item.sampleManager = man;
                    item.count = -1;
                } else if (item.count == 0) {
                    man = item.sampleManager.abortUpdate();
                    item.sampleManager = man;
                    item.count = -1;
                }

                if (man != bundle.getSampleManager()) {
                    bundle = getCurrentRowBundle(bundle, man);
                    row.data = bundle;
                }
            } catch (ValidationErrorsList e) {
                Window.alert(consts.get("errorSampleAccNum") +
                             bundle.getSampleManager().getSample().getAccessionNumber() + ":\n\n"+
                             e.getErrorList().get(0).getMessage());
                /*
                 * it can happen that the validation in the backend fails and so
                 * the manager won't get a chance to get unlocked if the following
                 * is not done
                 */
                unlockAndRefetchSample(item, bundle, row);
            }catch (Exception e) {
                Window.alert(consts.get("errorSampleAccNum") +
                             bundle.getSampleManager().getSample().getAccessionNumber() + ":\n\n" +
                             e.getMessage());
                /*
                 * the attempt to update the sample can fails for some other reason
                 * in that case too we'll need to unlock the manager 
                 */
                unlockAndRefetchSample(item, bundle,row);                 
            }
        }

        /*
         * change all the rows that need to be updated
         */
        for (i = 0; i < completeReleaseTable.numRows(); i++ ) {
            row = completeReleaseTable.getRow(i);
            bundle = (SampleDataBundle)row.data;
            man = bundle.getSampleManager();
            item = hash.get(man.getSample().getId());
            if (item == null)
                continue;
            try {
                if (man != item.sampleManager) {
                    bundle = getCurrentRowBundle(bundle, item.sampleManager);
                    row.data = bundle;
                    man = item.sampleManager;
                }
                updateTableRowCells(i, man.getSample(),
                                    man.getSampleItems()
                                       .getAnalysisAt(bundle.getSampleItemIndex())
                                       .getAnalysisAt(bundle.getAnalysisIndex()));
            } catch (Exception e) {
                window.setError(e.getMessage());
                e.printStackTrace();
            }

        }

        // if the tabs are showing data we need to make sure to refresh them
        if (rows.size() == 1) {
            dataBundle = (SampleDataBundle)rows.get(0).data;
            manager = dataBundle.getSampleManager();
            resetScreen();
        }
    }

    private void updateTableRowCells(int row, SampleDO sample, AnalysisViewDO analysis) {
        completeReleaseTable.setCell(row, 0, sample.getAccessionNumber());
        completeReleaseTable.setCell(row, 1, analysis.getTestName());
        completeReleaseTable.setCell(row, 2, analysis.getMethodName());
        completeReleaseTable.setCell(row, 3, analysis.getStatusId());
        completeReleaseTable.setCell(row, 4, sample.getStatusId());
    }

    private void updateAllRows(Integer accessionNumber) {
        TableDataRow row;

        for (int i = 0; i < completeReleaseTable.numRows(); i++ ) {
            row = completeReleaseTable.getRow(i);

            if (accessionNumber.equals(row.cells.get(0).value))
                updateTableRow(i);
        }
    }

    private void updateTableRow(int index) {
        int itemIndex, anIndex;
        TableDataRow row;
        SampleDO sample;
        AnalysisViewDO data;
        SampleDataBundle bundle;

        try {
            row = completeReleaseTable.getRow(index);
            bundle = (SampleDataBundle)row.data;
            bundle = getCurrentRowBundle(bundle, manager);
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
            row.data = bundle;
        } catch (Exception e) {
            Window.alert("updateSelectedTableRow: " + e.getMessage());
        }
    }

    private SampleDataBundle getCurrentRowBundle(SampleDataBundle old, SampleManager man) throws Exception {
        int ii, ai, i, j;
        Integer oid, nid;
        SampleDataBundle nb;
        AnalysisManager am;
        SampleItemManager sim;

        /*
         * get the info from old bundle and transfer it to the new bundle
         */
        ii = old.getSampleItemIndex();
        ai = old.getAnalysisIndex();
        oid = old.getSampleManager().getSampleItems().getAnalysisAt(ii).getAnalysisAt(ai).getId();

        /*
         * find the analysis at the old position
         */
        nid = null;
        sim = man.getSampleItems();
        if (ii < sim.count()) {
            am = sim.getAnalysisAt(ii);
            if (ai < am.count())
                nid = am.getAnalysisAt(ai).getId();
        }

        nb = null;
        if ( !oid.equals(nid)) {
            /*
             * things changed -- find the analysis id the hard way
             */
            for (i = 0; i < sim.count(); i++ ) {
                for (j = 0; j < sim.getAnalysisAt(i).count(); j++ ) {
                    if (sim.getAnalysisAt(i).getAnalysisAt(j).getId().equals(oid)) {
                        nb = sim.getAnalysisAt(i).getBundleAt(j);
                        break;
                    }
                }
            }
        } else {
            nb = man.getSampleItems().getAnalysisAt(ii).getBundleAt(ai);
        }

        return nb;
    }

    private void showNote() {
        final int index;
        ScreenWindow modal;
        AnalysisManager man;

        index = dataBundle.getSampleItemIndex();
        try {
            man = dataBundle.getSampleManager().getSampleItems().getAnalysisAt(index);
            noteMan = man.getInternalNotesAt(index);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        if (internalEditNote == null) {
            try {
                internalEditNote = new EditNoteScreen();
                internalEditNote.addActionHandler(this);
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("standardNote"));
        modal.setContent(internalEditNote);

        internalNote = null;
        try {
            internalNote = noteMan.getEditingNote();
            internalNote.setSystemUser(UserCache.getPermission().getLoginName());
            internalNote.setSystemUserId(UserCache.getPermission().getSystemUserId());
            internalNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
            internalEditNote.setNote(internalNote);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error!");
        }
    }

    private void resetScreen() {
        boolean showingDomainTab;

        showingDomainTab = (tab == Tabs.ENVIRONMENT || tab == Tabs.PRIVATE_WELL || tab == Tabs.SDWIS);

        if (completeReleaseTable.getSelections().size() > 1) {
            showTabs(Tabs.BLANK);
        } else if (manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.ENVIRONMENT;
            showTabs(Tabs.SAMPLE,
                     Tabs.ENVIRONMENT,
                     Tabs.SAMPLE_ITEM,
                     Tabs.ANALYSIS,
                     Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES,
                     Tabs.SAMPLE_NOTES,
                     Tabs.STORAGE,
                     Tabs.QA_EVENTS,
                     Tabs.AUX_DATA);
        } else if (manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.PRIVATE_WELL;
            showTabs(Tabs.SAMPLE,
                     Tabs.PRIVATE_WELL,
                     Tabs.SAMPLE_ITEM,
                     Tabs.ANALYSIS,
                     Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES,
                     Tabs.SAMPLE_NOTES,
                     Tabs.STORAGE,
                     Tabs.QA_EVENTS,
                     Tabs.AUX_DATA);

        } else if (manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG)) {
            if (showingDomainTab)
                tab = Tabs.SDWIS;
            showTabs(Tabs.SAMPLE,
                     Tabs.SDWIS,
                     Tabs.SAMPLE_ITEM,
                     Tabs.ANALYSIS,
                     Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES,
                     Tabs.SAMPLE_NOTES,
                     Tabs.STORAGE,
                     Tabs.QA_EVENTS,
                     Tabs.AUX_DATA);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();
    }

    private void showConfirm() {
        if (confirm == null) {
            confirm = new Confirm(Confirm.Type.QUESTION,
                                  consts.get("unreleaseAnalysisCaption"),
                                  consts.get("unreleaseAnalysisMessage"),
                                  "Cancel",
                                  "OK");
            confirm.addSelectionHandler(this);
        }
        confirm.show();
    }

    private void unlockAndRefetchSample(Item item, SampleDataBundle bundle,
                              TableDataRow row) {
        SampleManager man;
        if (item.count > 0) {
            try {
                man = item.sampleManager.abortUpdate();
                item.sampleManager = man;
                item.count = -1;

                if (man != bundle.getSampleManager()) {
                    bundle = getCurrentRowBundle(bundle, man);
                    row.data = bundle;
                }
            } catch (Exception e1) {
                Window.alert(consts.get("errorSampleAccNum") +
                             bundle.getSampleManager().getSample().getAccessionNumber() +
                             ":\n\n" + e1.getMessage());
            }
        }
    }   

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
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

    private class Item {
        private SampleManager sampleManager;
        private int           count;

        public Item(SampleManager man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
}