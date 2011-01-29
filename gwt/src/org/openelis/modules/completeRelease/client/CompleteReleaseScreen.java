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
import org.openelis.gwt.widget.tree.TreeWidget;
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

public class CompleteReleaseScreen extends Screen implements HasActionHandlers, SelectionHandler<Integer>,
                                                             ActionHandler<EditNoteScreen.Action> {

    private Integer                  sampleLoggedInId, sampleErrorStatusId, sampleReleasedId,
                                     analysisOnHoldId, analysisCompletedId;

    protected Tabs                   tab;
    protected ArrayList<Tabs>        tabIndexes = new ArrayList<Tabs>();
    protected TextBox                accessionNumber, clientReference;
    protected TextBox<Integer>       orderNumber;
    protected TextBox<Datetime>      collectedTime;

    protected Dropdown<Integer>      statusId;
    protected TreeWidget             itemsTree;
    protected AppButton              removeRow, releaseButton, reportButton, completeButton,
                                     addItem, addAnalysis, queryButton, updateButton, commitButton, abortButton;
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

    protected MenuItem               unreleaseAnalysis, previewFinalReport, historySample, historySampleSpec,
                                     historySampleProject, historySampleOrganization, historySampleItem,
                                     historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
                                     historyAnalysisQA, historyAuxData;

    private ScreenService            finalReportService;
    private boolean                  redrawFinalPreview;
    
    private enum Tabs {
        BLANK, SAMPLE, ENVIRONMENT, PRIVATE_WELL, SDWIS, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public CompleteReleaseScreen() throws Exception {
        super((ScreenDefInt)GWT.create(CompleteReleaseDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.completeRelease.server.CompleteReleaseService");
        finalReportService = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");
        
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

        historyUtility = new SampleHistoryUtility(window) {
            public void historyCurrentResult() {
                ActionEvent.fire(completeScreen, ResultTab.Action.RESULT_HISTORY, null);
            }
        };

        unreleaseAnalysis = (MenuItem)def.getWidget("unreleaseAnalysis");
        addScreenHandler(unreleaseAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> rows;

                rows = completeReleaseTable.getSelections();

                if (rows.size() != 1) {
                    Window.alert(consts.get("selOneRowUnrelease"));
                    return;
                }
                
                showConfirm() ;
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
                if ("Y".equals(event.getValue()) && state == State.DISPLAY)
                    previewFinalReport();
            }
            
            public void onDataChange(DataChangeEvent event) {
                if ("Y".equals(autoPreview.getValue()) && state == State.DISPLAY &&
                	redrawFinalPreview && completeReleaseTable.getSelectedRows().length == 1)
                    previewFinalReport();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                autoPreview.enable(true);
            }
        });
        
        autoPreviewText = (Label<String>)def.getWidget("autoPreviewText");
        addScreenHandler(autoPreviewText, new ScreenEventHandler<String>() {                       
            
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
                historyUtility.historyCurrentResult();
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
                completeReleaseTable.setQueryMode(EnumSet.of(State.QUERY)
                                                         .contains(event.getState()));
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

        testResultsTab.addActionHandler(new ActionHandler<ResultTab.Action>() {
            @SuppressWarnings("unchecked")
            public void onAction(ActionEvent<ResultTab.Action> event) {
                if(event.getAction() == ResultTab.Action.REFLEX_ADDED) {
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
	        sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
	        sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
	        sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
	        analysisOnHoldId = DictionaryCache.getIdFromSystemName("analysis_on_hold");
	        analysisCompletedId = DictionaryCache.getIdFromSystemName("analysis_completed");
	
	    } catch (Exception e) {
	        Window.alert(e.getMessage());
	        window.close();
	    }
	
	    // sample status dropdown
	    model = new ArrayList<TableDataRow>();
	    model.add(new TableDataRow(null, ""));
	    for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_status"))
	        model.add(new TableDataRow(d.getId(), d.getEntry()));
	
	    ((Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId())).setModel(model);
	    ((Dropdown<Integer>)completeReleaseTable.getColumnWidget(SampleMeta.getStatusId())).setModel(model);
	
	    // analysis status dropdown
	    model = new ArrayList<TableDataRow>();
	    model.add(new TableDataRow(null, ""));
	    for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
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
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
                Window.alert("commitUpdate(): " + e.getMessage());
                e.printStackTrace();
            }
        }

        redrawFinalPreview = true;
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
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }

        redrawFinalPreview = true;
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

	    redrawFinalPreview = true;

	    return model;
	}

	private boolean canEdit() {
	    return ( !sampleReleasedId.equals(manager.getSample().getStatusId()));
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

	private void previewFinalReport() {
        Query query;
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.key = "ACCESSION_NUMBER";
        field.query = manager.getSample().getAccessionNumber().toString();
        field.type = QueryData.Type.STRING;

        query.setFields(field);

        window.setBusy(consts.get("genReportMessage"));

        finalReportService.call("runReportForPreview", query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;
                
                url = "report?file=" + status.getMessage();
                Window.open(URL.encode(url), consts.get("finalReportSingleReprint"), null);
                window.setDone(consts.get("done"));
                redrawFinalPreview = false;
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });

    }

    private void resetScreen() {
        boolean showingDomainTab;

        showingDomainTab = (tab == Tabs.ENVIRONMENT || tab == Tabs.PRIVATE_WELL || tab == Tabs.SDWIS);

        if (completeReleaseTable.getSelections().size() > 1) {
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
            bundle = getCurrentRowBundle(row, manager);

            row.data = bundle;
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
            Window.alert("updateSelectedTableRow: " + e.getMessage());
        }
    }

    private SampleDataBundle getCurrentRowBundle(TableDataRow row, SampleManager manager) throws Exception {
        int itemIndex, anIndex;
        Integer analysisId;
        SampleDataBundle oldBundle, bundle;

        oldBundle = (SampleDataBundle)row.data;
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

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void release() {
        ArrayList<TableDataRow> rows;
        LocalizedException ex;
        int[] indexList;
        TableDataRow row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisViewDO data;
        String errorMsg;

        bundle = null;
        rows = completeReleaseTable.getSelections();
        indexList = completeReleaseTable.getSelectedRows();
        hash = new HashMap<Integer, Item>();

        if (rows.size() > 1) {
            ex = new LocalizedException("releaseMultipleWarning", String.valueOf(rows.size()));
            if ( !Window.confirm(ex.getMessage()))
                return;
        }
        window.setBusy(consts.get("updating"));

        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.data;
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
                row.data = bundle;
                bundle.getSampleManager()
                      .getSampleItems()
                      .getAnalysisAt(bundle.getSampleItemIndex())
                      .releaseAnalysisAt(bundle.getAnalysisIndex());

                try {
                    bundle.getSampleManager().validate();
                } catch (ValidationErrorsList e) {
                    bundle.getSampleManager().setStatusWithError(true);
                }
            } catch (EntityLockedException e) {
                hash.put(man.getSample().getId(), new Item(man, -1));
                Window.alert(consts.get("errorSampleAccNum") +
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

                    Window.alert(errorMsg);
                    window.clearStatus();
                } catch (Exception f) {
                    Window.alert(e.getMessage());
                    window.clearStatus();
                }

            } catch (Exception e) {
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            }
        }

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
        AnalysisManager anaMan;
        String errorMsg;

        rows = completeReleaseTable.getSelections();
        indexList = completeReleaseTable.getSelectedRows();                
        
        bundle = null;
        hash = new HashMap<Integer, Item>();
        row = rows.get(0);
        bundle = (SampleDataBundle)row.data;
        item = hash.get(bundle.getSampleManager().getSample().getId());
        man = bundle.getSampleManager();

        window.setBusy(consts.get("updating"));
        try {
            man = man.fetchForUpdate();
            item = new Item(man, 1);
            hash.put(man.getSample().getId(), item);
            bundle = getCurrentRowBundle(row, man);
            row.data = bundle;
            anaMan = bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            anaMan.setInternalNotes(noteMan, bundle.getAnalysisIndex());
            anaMan.unreleaseAnalysisAt(bundle.getAnalysisIndex());
            try {
                bundle.getSampleManager().validate();
            } catch (ValidationErrorsList e) {
                bundle.getSampleManager().setStatusWithError(true);
            }
        } catch (EntityLockedException e) {
            hash.put(man.getSample().getId(), new Item(man, -1));
            Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
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

                Window.alert(errorMsg);

            } catch (Exception f) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } catch (Exception e) {
            Window.alert(consts.get("errorSampleAccNum") + man.getSample().getAccessionNumber() +
                         ":\n\n" + e.getMessage());
            window.clearStatus();
        }

        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void complete() {
        ArrayList<TableDataRow> rows;
        int[] indexList;
        TableDataRow row;
        Item item;
        SampleDataBundle bundle;
        SampleManager man;
        HashMap<Integer, Item> hash;
        AnalysisManager anMan;
        AnalysisViewDO data;
        ValidationErrorsList errorsList;
        String errorMsg;

        bundle = null;
        rows = completeReleaseTable.getSelections();
        indexList = completeReleaseTable.getSelectedRows();
        hash = new HashMap<Integer, Item>();

        window.setBusy(consts.get("updating"));
        
        // loop through and lock sample if necessary
        for (int i = 0; i < rows.size(); i++ ) {
            row = rows.get(i);
            bundle = (SampleDataBundle)row.data;
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
                data = anMan.getAnalysisAt(bundle.getAnalysisIndex());
                if (analysisCompletedId.equals(data.getStatusId())) {
                    errorsList = new ValidationErrorsList();
                    errorsList.add(new FormErrorException("analysisAlreadyComplete"));
                    throw errorsList;
                }

                if ( !analysisOnHoldId.equals(data.getStatusId()) ||
                    Window.confirm(consts.get("onHoldWarning"))) {
                    bundle = getCurrentRowBundle(row, man);
                    row.data = bundle;
                    bundle.getSampleManager()
                          .getSampleItems()
                          .getAnalysisAt(bundle.getSampleItemIndex())
                          .completeAnalysisAt(bundle.getAnalysisIndex());
                }
                try {
                    bundle.getSampleManager().validate();
                } catch (ValidationErrorsList e) {
                    bundle.getSampleManager().setStatusWithError(true);
                }
            } catch (EntityLockedException e) {
                hash.put(man.getSample().getId(), new Item(man, -1));
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                try {
                    data = man.getSampleItems()
                              .getAnalysisAt(bundle.getSampleItemIndex())
                              .getAnalysisAt(bundle.getAnalysisIndex());
                    item.count = item.count - 1;

                    errorMsg = "Cannot complete " + data.getTestName() + ":" +
                               data.getMethodName() + " on accession #" +
                               man.getSample().getAccessionNumber() + ":\n";

                    for (int l = 0; l < e.size(); l++ )
                        errorMsg += " * " + e.getErrorList().get(l).getMessage() + "\n";

                    Window.alert(errorMsg);

                } catch (Exception f) {
                    Window.alert(e.getMessage());
                    window.clearStatus();
                }

            } catch (Exception e) {
                Window.alert(consts.get("errorSampleAccNum") +
                             man.getSample().getAccessionNumber() + ":\n\n" + e.getMessage());
                window.clearStatus();
            }
        }

        updateAndRefreshTable(rows, indexList, hash, bundle);
        window.clearStatus();
    }

    private void updateAndRefreshTable(ArrayList<TableDataRow> rows,
                                       int[] indexList,
                                       HashMap<Integer, Item> hash,
                                       SampleDataBundle bundle) {
        TableDataRow row;
        int index;
        Item item;

        for (int j = 0; j < rows.size(); j++ ) {
            index = indexList[j];
            row = rows.get(j);
            bundle = (SampleDataBundle)row.data;
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
                Window.alert(consts.get("errorSampleAccNum") +
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
        completeReleaseTable.setCell(row, 0, sample.getAccessionNumber());
        completeReleaseTable.setCell(row, 1, analysis.getTestName());
        completeReleaseTable.setCell(row, 2, analysis.getMethodName());
        completeReleaseTable.setCell(row, 3, analysis.getStatusId());
        completeReleaseTable.setCell(row, 4, sample.getStatusId());
    }

    private void showNote() {                        
        final int index = dataBundle.getSampleItemIndex();        
        ScreenWindow modal;
        AnalysisManager man;
        
        try {
            man = dataBundle.getSampleManager().getSampleItems()
                                    .getAnalysisAt(index);
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
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error!");
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

	private class Item {
	    private SampleManager sampleManager;
	    private int           count;
	
	    public Item(SampleManager man, int count) {
	        this.sampleManager = man;
	        this.count = count;
	    }
	}
}
