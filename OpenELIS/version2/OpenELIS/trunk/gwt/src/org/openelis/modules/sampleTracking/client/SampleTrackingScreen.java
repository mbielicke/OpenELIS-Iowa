package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.BeforeDragStartEvent;
import org.openelis.gwt.event.BeforeDragStartHandler;
import org.openelis.gwt.event.BeforeDropEvent;
import org.openelis.gwt.event.BeforeDropHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.DropEnterEvent;
import org.openelis.gwt.event.DropEnterHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.DropEnterEvent.DropPosition;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.EnvironmentalTab;
import org.openelis.modules.sample.client.PrivateWellTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SDWISTab;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.SampleTreeUtility;
import org.openelis.modules.sample.client.StorageTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Widget;

public class SampleTrackingScreen extends Screen implements HasActionHandlers {

    private SampleManager        manager;
    private ModulePermission     userPermission, unreleasePermission;

    private EnvironmentalTab     environmentalTab;
    private PrivateWellTab       wellTab;
    private SDWISTab             sdwisTab;
    private SampleItemTab        sampleItemTab;
    private AnalysisTab          analysisTab;
    private QAEventsTab          qaEventsTab;
    private StorageTab           storageTab;
    private SampleNotesTab       sampleNotesTab;
    private AnalysisNotesTab     analysisNotesTab;
    private AuxDataTab           auxDataTab;
    private ResultTab            testResultsTab;

    private TreeWidget           trackingTree;
    private TextBox              accessionNumber, clientReference;
    private TextBox<Integer>     orderNumber;
    private TextBox<Datetime>    collectedTime;
    private Dropdown<Integer>    statusId;
    private AppButton            prevPage, nextPage, similarButton, expandButton, collapseButton,
                                 queryButton, updateButton, commitButton, abortButton, addTestButton,
                                 cancelTestButton;
    private MenuItem             envMenuQuery, wellMenuQuery, sdwisMenuQuery, unreleaseSample,previewFinalReport,historySample,
                                 historySampleSpec, historySampleProject, historySampleOrganization,
                                 historySampleItem, historyAnalysis, historyCurrentResult, historyStorage,
                                 historySampleQA, historyAnalysisQA, historyAuxData;
    private CalendarLookUp       collectedDate, receivedDate;

    private Tabs                 tab;
    private TabPanel             tabPanel;

    private SampleTreeUtility    treeUtil;
    private SampleHistoryUtility historyUtility;

    private Integer              analysisLoggedInId,  sampleErrorStatusId, 
                                 sampleReleasedId;
    private Query                query;
    
    private ScreenService        finalReportService;

    public enum Tabs {
        BLANK, ENVIRONMENT, PRIVATE_WELL, SDWIS, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public SampleTrackingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleTrackingDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.sampleTracking.server.SampleTrackingService");
        finalReportService = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("sampletracking");
        unreleasePermission = OpenELIS.getSystemUserPermission().getModule("sampleunrelease");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Sample Tracking Screen");

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
            Window.alert("TrackingScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        final SampleTrackingScreen trackingScreen;

        trackingScreen = this;

        //
        // button panel buttons
        //
        expandButton = (AppButton)def.getWidget("expand");
        addScreenHandler(expandButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                SampleManager man;
                TreeDataItem row;
                for (int i = 0; i < trackingTree.getData().size(); i++ ) {
                    row = trackingTree.getData().get(i);

                    if ( !row.isLoaded()) {
                        try {
                            man = ((SampleDataBundle)row.data).getSampleManager();
                            loadSampleItem(man, row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                trackingTree.expand();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                expandButton.enable(event.getState() == State.DISPLAY);
            }
        });

        collapseButton = (AppButton)def.getWidget("collapse");
        addScreenHandler(collapseButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                trackingTree.collapse();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collapseButton.enable(event.getState() == State.DISPLAY);
            }
        });

        similarButton = (AppButton)def.getWidget("similar");
        addScreenHandler(similarButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME needs implemented
            }

            public void onStateChange(StateChangeEvent<State> event) {
                similarButton.enable(event.getState() == State.DISPLAY);
            }
        });

        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        envMenuQuery = (MenuItem)def.getWidget("environmentalSample");
        envMenuQuery.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                query(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            }
        });

        wellMenuQuery = (MenuItem)def.getWidget("privateWellWaterSample");
        wellMenuQuery.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                query(SampleManager.WELL_DOMAIN_FLAG);
            }
        });

        sdwisMenuQuery = (MenuItem)def.getWidget("sdwisSample");
        sdwisMenuQuery.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                query(SampleManager.SDWIS_DOMAIN_FLAG);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        addTestButton = (AppButton)def.getWidget("addTest");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addTest();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        cancelTestButton = (AppButton)def.getWidget("cancelTest");
        addScreenHandler(cancelTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.cancelTestClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelTestButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
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
                ActionEvent.fire(trackingScreen, ResultTab.Action.RESULT_HISTORY, null);
            }
        };
        
        unreleaseSample = (MenuItem)def.getWidget("unreleaseSample");
        addScreenHandler(unreleaseSample, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                unrelease();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unreleaseSample.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) && unreleasePermission.hasSelectPermission());
            }
        });
        
        previewFinalReport = (MenuItem)def.getWidget("previewFinalReport");
        addScreenHandler(previewFinalReport, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                viewFinalReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previewFinalReport.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
        trackingTree = (TreeWidget)def.getWidget("trackingTree");
        addScreenHandler(trackingTree, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Object> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingTree.enable(true);
                trackingTree.enableDrag(EnumSet.of(State.UPDATE).contains(event.getState()));
                trackingTree.enableDrop(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        trackingTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                if (event.getItem().leafType.equals("sample") && !event.getItem().isLoaded()) {
                    try {
                        loadSampleItem(manager, event.getItem());
                        event.getItem().checkForChildren(false);
                    } catch (Exception e) {
                        Window.alert("leafOpened: " + e.getMessage());
                    }
                }
            }

        });

        trackingTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        trackingTree.enableDrag(true);
        trackingTree.enableDrop(true);

        trackingTree.addBeforeDragStartHandler(new BeforeDragStartHandler<TreeRow>() {
            public void onBeforeDragStart(BeforeDragStartEvent<TreeRow> event) {
                TreeDataItem treeItem;
                Label label;

                try {
                    treeItem = event.getDragObject().item;
                    if ( !treeItem.leafType.equals("analysis"))
                        event.cancel();
                    else {
                        label = new Label(treeItem.cells.get(0).value + " | " +
                                          DictionaryCache.getEntryFromId((Integer)treeItem.cells.get(1).value).getEntry());
                        label.setStyleName("ScreenLabel");
                        label.setWordWrap(false);
                        event.setProxy(label);
                    }
                } catch (Exception e) {
                    Window.alert("tree beforeDragStart: " + e.getMessage());
                }
            }
        });

        trackingTree.addBeforeDropHandler(new BeforeDropHandler<TreeRow>() {
            public void onBeforeDrop(BeforeDropEvent<TreeRow> event) {
                AnalysisManager am;
                TreeDataItem dragItem, dropTarget;
                SampleDataBundle dragKey, dropKey, newBundle;

                dragItem = event.getDragObject().dragItem;
                dragKey = (SampleDataBundle)dragItem.data;
                dropTarget = ((TreeRow)event.getDropTarget()).item;
                dropKey = (SampleDataBundle)dropTarget.data;

                try {
                    manager.getSampleItems().moveAnalysis(dragKey, dropKey);

                    // reset the dropped row data bundle, and its children
                    am = manager.getSampleItems().getAnalysisAt(dropKey.getSampleItemIndex());
                    newBundle = am.getBundleAt(am.count()-1);
                    dragItem.data = newBundle;
                    
                    for(int i=0; i<dragItem.getItems().size(); i++)
                        dragItem.getItem(i).data = newBundle;
                                        
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert("Move failed: " + e.getMessage());
                }
            }
        });

        trackingTree.addDropEnterHandler(new DropEnterHandler<TreeRow>() {
            public void onDropEnter(DropEnterEvent<TreeRow> event) {
                TreeDataItem dropTarget, dropTargetParent, dragItem, dragItemParent;

                dragItem = event.getDragObject().dragItem;
                dropTarget = ((TreeRow)event.getDropTarget()).item;
                
                dropTargetParent = dropTarget;
                while(!"sampleItem".equals(dropTargetParent.leafType))
                    dropTargetParent = dropTargetParent.parent;
                
                dragItemParent = dragItem;
                while(!"sampleItem".equals(dragItemParent.leafType))
                    dragItemParent = dragItemParent.parent;
                
                if (!dropTarget.leafType.equals("analysis") && !dropTarget.leafType.equals("storage")  ||
                                (dropTarget.leafType.equals("storage") && "analysis".equals(dropTarget.parent.leafType)) || 
                                (dropTarget.leafType.equals("analysis") && event.getDropPosition() == DropPosition.ON) ||
                                (dropTarget.leafType.equals("storage") && (event.getDropPosition() == DropPosition.ON || event.getDropPosition() == DropPosition.BELOW)) || 
                                dropTargetParent.equals(dragItemParent) || 
                                !dropTargetParent.parent.equals(dragItemParent.parent))
                    event.cancel();
            }
        });

        trackingTree.addTarget(trackingTree);
        trackingTree.enableDrag(false);
        trackingTree.enableDrop(false);

        trackingTree.addUnselectionHandler(new UnselectionHandler<TreeDataItem>() {
            public void onUnselection(UnselectionEvent<TreeDataItem> event) {
                SampleDataBundle key;

                if (state == State.UPDATE && event.getProposedSelect() != null) {
                    key = (SampleDataBundle)event.getProposedSelect().data;

                    if ( !key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
                        event.cancel();
                        return;
                    }
                }

                if (event.getProposedSelect() == null) {
                    manager = SampleManager.getInstance();
                    showTabs(Tabs.BLANK);
                    DataChangeEvent.fire(trackingScreen);
                }
            }
        });

        trackingTree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                SampleDataBundle bundle;
                SampleManager man;

                bundle = (SampleDataBundle)event.getSelectedItem().data;
                man = bundle.getSampleManager();
                if (manager == null || !man.getSample().getId().equals(manager.getSample().getId()))
                    manager = man;

                DataChangeEvent.fire(trackingScreen);
                window.clearStatus();
            }
        });
        
        prevPage = (AppButton)def.getWidget("prevPage");
        addScreenHandler(prevPage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previousPage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevPage.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextPage = (AppButton)def.getWidget("nextPage");
        addScreenHandler(nextPage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                nextPage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextPage.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        accessionNumber = (TextBox)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<String> event) {
                int    index;
                String val;

                val = event.getValue();
                //
                // Trim the Sample Item ID from the end of the bar coded
                // accession number
                //
                index = val.indexOf("-");
                if (index != -1)
                    val = val.substring(0, index);
                accessionNumber.setValue(val);

                manager.getSample().setAccessionNumber(Integer.valueOf(val));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getOrderId());
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(Util.toString(manager.getSample().getOrderId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setOrderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                orderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedDate = (CalendarLookUp)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(manager.getSample().getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                collectedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedTime = (TextBox<Datetime>)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTime.setValue(manager.getSample().getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(manager.getSample().getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getSample().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        clientReference = (TextBox)def.getWidget(SampleMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(manager.getSample().getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getSample().setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
                clientReference.setQueryMode(event.getState() == State.QUERY);
            }
        });

        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.getTabBar().setStyleName("None");
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                tab = Tabs.values()[event.getSelectedItem()];
                drawTabs();
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
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    environmentalTab.setData(manager);
                    environmentalTab.draw();
                    showTabs(Tabs.ENVIRONMENT);

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else
                    environmentalTab.setData(null);
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
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    wellTab.setData(manager);
                    wellTab.draw();
                    showTabs(Tabs.PRIVATE_WELL);

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else
                    wellTab.setData(null);
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
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.SDWIS_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    sdwisTab.setData(manager);
                    sdwisTab.draw();
                    showTabs(Tabs.SDWIS);

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else
                    sdwisTab.setData(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisTab.setState(event.getState());
            }
        });

        sampleItemTab = new SampleItemTab(def, window);
        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sampleItem".equals(selectedRow.leafType)) {
                    bundle = (SampleDataBundle)selectedRow.data;
                    sampleItemTab.setData(bundle);
                    sampleItemTab.draw();
                    showTabs(Tabs.SAMPLE_ITEM);
                    addTestButton.enable(state == State.UPDATE);
                    cancelTestButton.enable(false);
                } else
                    sampleItemTab.setData(null);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTab.setState(event.getState());
            }
        });

        analysisTab = new AnalysisTab(def, window);

        addScreenHandler(analysisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "result".equals(selectedRow.leafType)) {
                    bundle = (SampleDataBundle)selectedRow.data;
                    analysisTab.setData(bundle);
                    analysisTab.draw();
                    showTabs(Tabs.ANALYSIS);
                    addTestButton.enable(state == State.UPDATE);
                    cancelTestButton.enable(state == State.UPDATE);
                } else
                    analysisTab.setData(null);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });

        sampleItemTab.addActionHandler(new ActionHandler<SampleItemTab.Action>() {
            public void onAction(ActionEvent<SampleItemTab.Action> event) {
                TreeDataItem selected;

                if (state != State.QUERY) {
                    selected = trackingTree.getSelection();

                    treeUtil.updateSampleItemRow(selected);
                    trackingTree.refreshRow(selected);
                }
            }
        });

        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent event) {
                TreeDataItem selected;

                if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), false);
                } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), true);
                } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                    selected = trackingTree.getSelection();

                    // we want to update the result tab instead of the analysis
                    // tab
                    treeUtil.updateAnalysisRow(selected.parent);
                    trackingTree.refreshRow(selected.parent);
                } else if (event.getAction() == AnalysisTab.Action.SAMPLE_TYPE_CHANGED) {
                    selected = trackingTree.getSelection();
                    treeUtil.updateSampleItemRow(selected.parent);
                    trackingTree.refreshRow(selected.parent);
                }
            }
        });

        testResultsTab = new ResultTab(def, window, this);
        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "analysis".equals(selectedRow.leafType)) {
                    bundle = (SampleDataBundle)selectedRow.data;

                    testResultsTab.setData(bundle);
                    testResultsTab.draw();
                    showTabs(Tabs.TEST_RESULT);
                    addTestButton.enable(state == State.UPDATE);
                    cancelTestButton.enable(state == State.UPDATE);
                } else
                    testResultsTab.setData(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });
        
        testResultsTab.addActionHandler(new ActionHandler<ResultTab.Action>() {
            public void onAction(ActionEvent<ResultTab.Action> event) {
                ArrayList<SampleDataBundle> data;

                if (state != State.QUERY){
                    data = (ArrayList<SampleDataBundle>)event.getData();
                    //we need to create a new analysis row so the utility can work from that
                    addTest(data.get(0));
                    
                    treeUtil.importReflexTestList((ArrayList<SampleDataBundle>)event.getData());
                }
            }
        });

        analysisNotesTab = new AnalysisNotesTab(def, window, "anExNotesPanel", "anExNoteButton",
                                                "anIntNotesPanel", "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();
                bundle = null;

                if (selectedRow != null)
                    bundle = (SampleDataBundle)selectedRow.data;

                if (selectedRow != null && bundle != null && "note".equals(selectedRow.leafType) &&
                    SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())) {
                    analysisNotesTab.setData(bundle);
                    analysisNotesTab.draw();
                    showTabs(Tabs.ANALYSIS_NOTES);
                    addTestButton.enable(state == State.UPDATE);
                    cancelTestButton.enable(state == State.UPDATE);
                } else
                    analysisNotesTab.setData(null);
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
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();
                bundle = null;

                if (selectedRow != null)
                    bundle = (SampleDataBundle)selectedRow.data;

                if (selectedRow != null && bundle != null && "note".equals(selectedRow.leafType) &&
                    SampleDataBundle.Type.SAMPLE.equals(bundle.getType())) {
                    sampleNotesTab.setManager(manager);
                    sampleNotesTab.draw();
                    showTabs(Tabs.SAMPLE_NOTES);
                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else
                    sampleNotesTab.setManager(manager);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleNotesTab.setState(event.getState());
            }
        });

        storageTab = new StorageTab(def, window);
        addScreenHandler(storageTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "storage".equals(selectedRow.leafType)) {
                    bundle = (SampleDataBundle)selectedRow.data;

                    storageTab.setData(bundle);
                    storageTab.draw();
                    showTabs(Tabs.STORAGE);

                    addTestButton.enable(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.parent.leafType));
                    cancelTestButton.enable(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.parent.leafType));
                } else
                    storageTab.setData(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTab.setState(event.getState());
            }
        });

        qaEventsTab = new QAEventsTab(def, window);
        addScreenHandler(qaEventsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "qaevent".equals(selectedRow.leafType)) {
                    bundle = (SampleDataBundle)selectedRow.data;

                    if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType()))
                        qaEventsTab.setData(bundle);

                    qaEventsTab.setManager(manager);
                    qaEventsTab.draw();
                    showTabs(Tabs.QA_EVENTS);
                    addTestButton.enable(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.parent.leafType));
                    cancelTestButton.enable(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.parent.leafType));
                } else {
                    qaEventsTab.setData(null);
                    qaEventsTab.setManager(manager);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaEventsTab.setState(event.getState());
            }
        });

        auxDataTab = new AuxDataTab(def, window);
        addScreenHandler(auxDataTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "auxdata".equals(selectedRow.leafType)) {
                    auxDataTab.setManager(manager);
                    auxDataTab.draw();
                    showTabs(Tabs.AUX_DATA);
                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else
                    auxDataTab.setManager(manager);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });

        treeUtil = new SampleTreeUtility(window, trackingTree, this) {
            public TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow,
                                                        SampleDataBundle bundle) {
                TreeDataItem results, analysis, storage, qaevent, note;

                results = new TreeDataItem(2);
                results.leafType = "analysis";
                results.data = bundle;

                analysis = new TreeDataItem();
                analysis.leafType = "result";
                analysis.data = bundle;
                analysis.cells.add(new TableDataCell(consts.get("analysis")));
                results.addItem(analysis);

                note = new TreeDataItem();
                note.leafType = "note";
                note.data = bundle;
                note.cells.add(new TableDataCell(consts.get("notes")));
                results.addItem(note);

                storage = new TreeDataItem();
                storage.leafType = "storage";
                storage.data = bundle;
                storage.cells.add(new TableDataCell(consts.get("storage")));
                results.addItem(storage);

                qaevent = new TreeDataItem();
                qaevent.leafType = "qaevent";
                qaevent.data = bundle;
                qaevent.cells.add(new TableDataCell(consts.get("qaEvents")));
                results.addItem(qaevent);
                trackingTree.addChildItem(parentRow, results, parentRow.getItems().size() - 1);

                return results;
            }

            public void selectNewRowFromBundle(TreeDataItem row) {
                trackingTree.select(row.getFirstChild());
                trackingTree.scrollToVisible();
            }
        };

        treeUtil.addActionHandler(new ActionHandler() {
            public void onAction(ActionEvent event) {
                if (event.getAction() == SampleItemAnalysisTreeTab.Action.REFRESH_TABS) {
                    SampleDataBundle data;
                    
                    data = (SampleDataBundle)event.getData();
                    sampleItemTab.setData(data);
                    analysisTab.setData(data);
                    testResultsTab.setData(data);
                    analysisNotesTab.setData(data);
                    storageTab.setData(data);
                    qaEventsTab.setData(data);

                    drawTabs();
                } else {
                    ActionEvent.fire(trackingScreen, event.getAction(), event.getData());
                }
            }
        });


        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
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

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)trackingTree.getColumns().get("analysis").get(1).colWidget).setModel(model);

    }

    protected void query(String domain) {
        Tabs tab;

        manager = SampleManager.getInstance();
        trackingTree.clear();
        tab = null;

        manager.getSample().setDomain(domain);
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain))
            tab = Tabs.ENVIRONMENT;

        else if (SampleManager.WELL_DOMAIN_FLAG.equals(domain))
            tab = Tabs.PRIVATE_WELL;

        else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain))
            tab = Tabs.SDWIS;

        showTabs(tab, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS, Tabs.TEST_RESULT, Tabs.STORAGE,
                 Tabs.QA_EVENTS, Tabs.AUX_DATA);

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
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

        setFocus(accessionNumber);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void update(boolean withUnrelease) {
        int topLevelIndex;
        TreeDataItem sampleRow;
        
        if (trackingTree.getSelectedRow() == -1) {
            window.setError(consts.get("selectRecordToUpdate"));
            return;
        }
        
        if (sampleReleasedId.equals(manager.getSample().getStatusId())) {
            if (! withUnrelease) {
                window.setError(consts.get("cantUpdateReleasedException"));
                return;
            }
        } 

        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            treeUtil.setManager(manager);
            
            topLevelIndex = getTopLevelIndex(trackingTree.getSelection());
            sampleRow = trackingTree.getData().get(topLevelIndex);
            sampleRow.data = manager.getBundle();
            trackingTree.unselect(trackingTree.getSelectedRowIndex());
            checkNode(sampleRow);
            setState(State.DISPLAY);
            trackingTree.select(topLevelIndex);
            window.clearStatus();
            setState(State.UPDATE);
            
            //
            // re-check the status to make sure it is still correct
            //
            if (sampleReleasedId.equals(manager.getSample().getStatusId())) {
                if (withUnrelease) {
                    manager.unrelease();
                } else {
                    abort();
                    window.setError(consts.get("cantUpdateReleasedException"));
                    return;
                }
            } 

            DataChangeEvent.fire(this);
            setFocus(collectedDate);
            window.clearStatus();
            if (trackingTree.getSelection() != null)
                SelectionEvent.fire(trackingTree, trackingTree.getSelection());
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
            query = new Query();
            query.setFields(getQueryFields());

            executeQuery(query);

        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                if (!validateUpdate()) {
                    window.setError(consts.get("correctErrors"));
                    return;
                }

                manager = manager.update();
                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);

                if ( !e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }

    protected void commitWithWarnings() {
        clearErrors();
        environmentalTab.clearErrors();
        wellTab.clearErrors();
        sdwisTab.clearErrors();
        
        manager.setStatusWithError(true);

        if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();
                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }

    protected void abort() {
        int topLevelIndex;
        TreeDataItem sampleRow;
        
        setFocus(null);
        clearErrors();
        environmentalTab.clearErrors();
        wellTab.clearErrors();
        sdwisTab.clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            String domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                topLevelIndex = getTopLevelIndex(trackingTree.getSelection());
                sampleRow = trackingTree.getData().get(topLevelIndex);
                trackingTree.unselect(trackingTree.getSelectedRowIndex());
                checkNode(sampleRow);
                setState(State.DISPLAY);
                trackingTree.select(topLevelIndex);
                window.clearStatus();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else {
            window.clearStatus();
        }
    }
    
    public ArrayList<QueryData> getQueryFields() {
        boolean              addDomain;
        int                  i, index;
        ArrayList<QueryData> fields, auxFields, tmpFields;
        QueryData            field;

        fields = super.getQueryFields();
        tmpFields = null;
        addDomain = true;

        for (i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            if (field.key == SampleMeta.getAccessionNumber()) {
                if (field.query.matches("[0-9]+-[0-9]+")) {
                    //
                    // Trim the Sample Item ID from the end of the bar coded
                    // accession number
                    //
                    index = field.query.indexOf("-");
                    if (index != -1)
                        field.query = field.query.substring(0, index);
                }
                field.type = QueryData.Type.INTEGER;
                break;
            }
        }

        // add aux data values if necessary
        auxFields = auxDataTab.getQueryFields();                
        
        if (auxFields.size() > 0) {
            // add ref table
            field = new QueryData();
            field.key = SampleMeta.getAuxDataReferenceTableId();
            field.type = QueryData.Type.INTEGER;
            field.query = String.valueOf(ReferenceTable.SAMPLE);
            fields.add(field);

            // add aux fields
            for (i = 0; i < auxFields.size(); i++ ) {                
                fields.add(auxFields.get(i));            
            } 
        }
        
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
            tmpFields = environmentalTab.getQueryFields();
        } else if (SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
            tmpFields = wellTab.getQueryFields();            
        } else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
            tmpFields = sdwisTab.getQueryFields();
        }

        if (tmpFields.size() > 0) {
            fields.addAll(tmpFields);
            addDomain = false;
        }
        
        addPrivateWellFields(fields);

        if (addDomain) {
            // Added this Query Param to keep Quick Entry Samples out of
            // query results
            field = new QueryData();
            field.query = "!Q";
            field.key = SampleMeta.getDomain();
            field.query = manager.getSample().getDomain();
            field.type = QueryData.Type.STRING;
            fields.add(field);
        }

        return fields;
    }

    protected void previousPage() {
        int page;

        page = query.getPage();

        if (page == 0) {
            window.clearStatus();
            return;
        }

        query.setPage(page - 1);
        executeQuery(query);
    }

    protected void nextPage() {
        int page;

        page = query.getPage();

        query.setPage(page + 1);
        executeQuery(query);
    }

    protected void addTest() {
        addTest(null);
    }

    protected void addTest(SampleDataBundle analysisBundle){
        SampleDataBundle bundle;
        AnalysisManager anMan;
        TreeDataItem sampleItem;
        int analysisIndex;
        TreeDataItem results, analysis, storage, qaevent, note;
    
        sampleItem = trackingTree.getSelection();
        while ( !"sampleItem".equals(sampleItem.leafType))
            sampleItem = sampleItem.parent;
    
        bundle = (SampleDataBundle)sampleItem.data;
    
        try {
            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            
            if(analysisBundle == null){
                analysisIndex = anMan.addAnalysis();
                analysisBundle = anMan.getBundleAt(analysisIndex);
            }else
                analysisIndex = analysisBundle.getAnalysisIndex();
    
            results = new TreeDataItem();
            results.open = true;
            results.leafType = "analysis";
            results.data = analysisBundle;
            results.cells.add(new TableDataCell("<> : <>"));
            results.cells.add(new TableDataCell(analysisLoggedInId));
    
            analysis = new TreeDataItem();
            analysis.leafType = "result";
            analysis.data = analysisBundle;
            analysis.cells.add(new TableDataCell(consts.get("analysis")));
            results.addItem(analysis);
    
            note = new TreeDataItem();
            note.leafType = "note";
            note.data = analysisBundle;
            note.cells.add(new TableDataCell(consts.get("notes")));
            results.addItem(note);
    
            storage = new TreeDataItem();
            storage.leafType = "storage";
            storage.data = analysisBundle;
            storage.cells.add(new TableDataCell(consts.get("storage")));
            results.addItem(storage);
    
            qaevent = new TreeDataItem();
            qaevent.leafType = "qaevent";
            qaevent.data = analysisBundle;
            qaevent.cells.add(new TableDataCell(consts.get("qaEvents")));
            results.addItem(qaevent);
    
            trackingTree.addChildItem(sampleItem, results, analysisIndex);
            trackingTree.select(analysis);
            trackingTree.scrollToVisible();
    
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }
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

    private void executeQuery(final Query query) {
        window.setBusy(consts.get("querying"));

        service.callList("query", query, new AsyncCallback<ArrayList<SampleManager>>() {
            public void onSuccess(ArrayList<SampleManager> result) {
                manager = null;

                if (result.size() > 0)
                    setState(State.DISPLAY);
                else 
                    setState(State.DEFAULT);
                
                trackingTree.load(getModel(result));
                
                if (result.size() > 0)
                    trackingTree.select(0);

                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                int page;

                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    trackingTree.clear();
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    page = query.getPage();
                    query.setPage(page - 1);
                    window.setError(consts.get("noMoreRecordInDir"));
                } else {
                    trackingTree.clear();
                    Window.alert("Error: envsample call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private ArrayList<TreeDataItem> getModel(ArrayList<SampleManager> result) {
        ArrayList<TreeDataItem> model;

        model = new ArrayList<TreeDataItem>();
        if (result == null)
            return model;

        try {
            for (SampleManager sm : result) {
                TreeDataItem sample = new TreeDataItem();
                sample.checkForChildren(true);
                sample.leafType = "sample";
                sample.data = sm.getBundle();
                sample.cells.add(new TableDataCell(sm.getSample().getAccessionNumber()));
                try {
                    sample.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(sm.getSample()
                                                                                        .getStatusId()).getEntry()));
                } catch (Exception e) {
                    sample.cells.add(new TableDataCell(sm.getSample().getStatusId()));
                }
                model.add(sample);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    private void showTabs(Tabs... tabs) {
        List<Tabs> tabList = Arrays.asList(tabs);

        for (Tabs tab : Tabs.values()) {
            tabPanel.setTabVisible(tab.ordinal(), tabList.contains(tab));
        }

        if (tabs[0] == Tabs.BLANK) {
            tabPanel.selectTab(tabs[0].ordinal());
            tabPanel.getTabBar().setStyleName("None");
        } else {
            tabPanel.selectTab(tabs[0].ordinal());
            tabPanel.getTabBar().setStyleName("gwt-TabBar");
        }
    }

    private void loadSampleItem(SampleManager sm, TreeDataItem row) throws Exception {
        TreeDataItem item, results, analysis, storage, qaevent, note, aux;
        SampleItemManager siMan;
        SampleItemViewDO itemDO;
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        SampleDataBundle sBundle, siBundle, anBundle;

        if (sm.getSampleItems() != null) {
            sBundle = sm.getBundle();
            siMan = sm.getSampleItems();
            for (int i = 0; i < siMan.count(); i++ ) {
                itemDO = siMan.getSampleItemAt(i);
                siBundle = siMan.getBundleAt(i);

                item = new TreeDataItem();
                item.open = true;
                item.leafType = "sampleItem";
                item.key = itemDO.getId();
                item.data = siBundle;
                item.cells.add(new TableDataCell(itemDO.getItemSequence() + " - " +
                                                 treeUtil.formatTreeString(itemDO.getContainer())));
                item.cells.add(new TableDataCell(itemDO.getTypeOfSample()));

                anMan = sm.getSampleItems().getAnalysisAt(i);
                if (anMan != null) {
                    for (int j = 0; j < anMan.count(); j++ ) {
                        anDO = anMan.getAnalysisAt(j);
                        anBundle = anMan.getBundleAt(j);

                        results = new TreeDataItem();
                        results.leafType = "analysis";
                        results.key = anDO.getId();
                        results.data = anBundle;
                        results.cells.add(new TableDataCell(
                                                            treeUtil.formatTreeString(anDO.getTestName()) +
                                                                            " : " +
                                                                            treeUtil.formatTreeString(anDO.getMethodName())));
                        results.cells.add(new TableDataCell(anDO.getStatusId()));

                        analysis = new TreeDataItem();
                        analysis.leafType = "result";
                        analysis.data = anBundle;
                        analysis.cells.add(new TableDataCell(consts.get("analysis")));
                        results.addItem(analysis);

                        storage = new TreeDataItem();
                        storage.leafType = "storage";
                        storage.data = anBundle;
                        storage.cells.add(new TableDataCell(consts.get("storage")));

                        results.addItem(storage);
                        qaevent = new TreeDataItem();
                        qaevent.leafType = "qaevent";
                        qaevent.data = anBundle;
                        qaevent.cells.add(new TableDataCell(consts.get("qaEvents")));
                        results.addItem(qaevent);

                        note = new TreeDataItem();
                        note.leafType = "note";
                        note.data = anBundle;
                        note.cells.add(new TableDataCell(consts.get("notes")));
                        results.addItem(note);
                        item.addItem(results);
                    }
                }
                storage = new TreeDataItem();
                storage.leafType = "storage";
                storage.data = siBundle;
                storage.cells.add(new TableDataCell(consts.get("storage")));
                item.addItem(storage);
                row.addItem(item);
            }
            note = new TreeDataItem();
            note.leafType = "note";
            note.data = sBundle;
            note.cells.add(new TableDataCell(consts.get("notes")));
            row.addItem(note);

            qaevent = new TreeDataItem();
            qaevent.leafType = "qaevent";
            qaevent.data = sBundle;
            qaevent.cells.add(new TableDataCell(consts.get("qaEvents")));
            row.addItem(qaevent);

            aux = new TreeDataItem();
            aux.leafType = "auxdata";
            aux.data = sBundle;
            aux.cells.add(new TableDataCell(consts.get("auxData")));
            row.addItem(aux);
            row.checkForChildren(false);
        }
    }

    public void showErrors(ValidationErrorsList errors) {
        ArrayList<LocalizedException> formErrors;
        TableFieldErrorException tableE;
        FormErrorException formE;
        FieldErrorException fieldE;
        TableWidget tableWid;
        HasField field;

        formErrors = new ArrayList<LocalizedException>();
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException)ex;
                tableWid = (TableWidget)def.getWidget(tableE.getTableKey());
                tableWid.setCellException(tableE.getRowIndex(), tableE.getFieldName(), tableE);
            } else if (ex instanceof FormErrorException) {
                formE = (FormErrorException)ex;
                formErrors.add(formE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasField)def.getWidget(fieldE.getFieldName());

                if (field != null)
                    field.addException(fieldE);

                if (ex instanceof FieldErrorWarning)
                    formErrors.add(new FormErrorWarning(fieldE.getKey(), fieldE.getParams()));
                else
                    formErrors.add(new FormErrorException(fieldE.getKey(), fieldE.getParams()));
            }
        }

        if (formErrors.size() == 0)
            window.setError(consts.get("correctErrors"));
        else if (formErrors.size() == 1)
            window.setError(formErrors.get(0).getMessage());
        else {
            window.setError("(Error 1 of " + formErrors.size() + ") " +
                            formErrors.get(0).getMessage());
            window.setMessagePopup(formErrors, "ErrorPanel");
        }

        // call the correct domain tab show error method
        if (manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
            environmentalTab.showErrors(errors);

        if (manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG))
            wellTab.showErrors(errors);

        if (manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG))
            sdwisTab.showErrors(errors);
    }
    
    public boolean validateUpdate() throws Exception {
        boolean valid = true;

        for (Widget wid : def.getWidgets().values()) {
            if (wid instanceof HasField) {
                ((HasField)wid).checkValue();
                if ( ((HasField)wid).getExceptions() != null)
                    valid = false;
            }
        }
        
        manager.validate();
        
        return valid;
    }
    
    private void checkNode(TreeDataItem item) {
        SampleDataBundle openKey;
        ArrayList<SampleDataBundle> openItems;

        if ( !item.open) {
            item.getItems().clear();
            item.checkForChildren(true);
            return;
        }

        openItems = new ArrayList<SampleDataBundle>();
        for (TreeDataItem child : item.getItems()) {
            checkChildOpen(child, openItems);
        }
        item.getItems().clear();
        try {
            loadSampleItem(manager, item);
            while (openItems.size() > 0) {
                openKey = openItems.remove(0);
                searchForKey(openKey, item);
            }
            trackingTree.refreshRow(item);
        } catch (Exception e) {
            Window.alert("checkNode: " + e.getMessage());
        }
    }

    private boolean searchForKey(SampleDataBundle bundle, TreeDataItem item) {
        if (bundle.getType() == ((SampleDataBundle)item.data).getType() &&
            bundle.getSampleItemIndex() == ((SampleDataBundle)item.data).getSampleItemIndex() &&
            bundle.getAnalysisIndex() == ((SampleDataBundle)item.data).getAnalysisIndex()) {
            if ( !item.open)
                item.toggle();
            return true;
        }
        for (TreeDataItem child : item.getItems()) {
            if (searchForKey(bundle, child))
                break;
        }
        return false;
    }

    private void checkChildOpen(TreeDataItem item, ArrayList<SampleDataBundle> openItems) {
        if (item.open) {
            openItems.add((SampleDataBundle)item.data);
            for (TreeDataItem child : item.getItems()) {
                checkChildOpen(child, openItems);
            }
        }
    }

    private int getTopLevelIndex(TreeDataItem node) {
        if (node.parent != null)
            return getTopLevelIndex(node.parent);

        return trackingTree.getData().indexOf(node);
    }
    
    private void unrelease(){
        Confirm confirm;
               
        if (trackingTree.getSelectedRow() == -1) {
            window.setError(consts.get("selectRecordToUpdate"));
            return;
        }

        if (!sampleReleasedId.equals(manager.getSample().getStatusId())) {
            Window.alert(consts.get("wrongStatusUnrelease"));
            return;
        } 
        
        confirm = new Confirm(Confirm.Type.QUESTION,
                              consts.get("unreleaseSampleCaption"),
                              consts.get("unreleaseSampleMessage"),
                              "Cancel", "OK"); 
        confirm.show();
        confirm.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                switch (event.getSelectedItem().intValue()) {
                    case 0:
                        break;
                    case 1:                        
                        update(true);
                        break;
                }
            }
        });
    }
    
    private void viewFinalReport() {
        Query query;
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.key = "ACCESSION_NUMBER";
        field.query = manager.getSample().getAccessionNumber().toString();
        field.type = QueryData.Type.STRING;

        query.setFields(field);

        window.setBusy(consts.get("genReportMessage"));

        finalReportService.call("runReportForSingle", query, new AsyncCallback<ReportStatus>() {
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

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    /**
     * We need to add additional fields to the list of queried fields if it
     * contains any field belonging to private well water's report to/organization.
     * This is done in order to make sure that names and addresses belonging to 
     * organizations as well as the ones that don't are searched.          
     */
    private void addPrivateWellFields(ArrayList<QueryData> fields){
        int size;
        String dataKey, orgName, addressMult, addressStreet, addressCity,
               addressState, addressZip, addressWorkPhone, addressFaxPhone;
        QueryData data;
        
        orgName = null;
        addressMult = null;
        addressStreet = null;
        addressCity = null;
        addressState = null;
        addressZip = null;
        addressWorkPhone = null;
        addressFaxPhone = null;
        
        size = fields.size();
        for(int i = size-1; i >= 0; i--){
            data = fields.get(i);
            dataKey = data.key;
            
            if (SampleMeta.getWellOrganizationName().equals(dataKey)) {
                orgName = data.query;

                data = new QueryData();
                data.key = SampleMeta.getWellReportToName();
                data.type = QueryData.Type.STRING;
                data.query = orgName;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressMultipleUnit().equals(dataKey)) {
                addressMult = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressMultipleUnit();
                data.type = QueryData.Type.STRING;
                data.query = addressMult;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressStreetAddress().equals(dataKey)) {
                addressStreet = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressStreetAddress();
                data.type = QueryData.Type.STRING;
                data.query = addressStreet;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressCity().equals(dataKey)) {
                addressCity = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressCity();
                data.type = QueryData.Type.STRING;
                data.query = addressCity;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressState().equals(dataKey)) {
                addressState = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressState();
                data.type = QueryData.Type.STRING;
                data.query = addressState;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressZipCode().equals(dataKey)) {
                addressZip = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressZipCode();
                data.type = QueryData.Type.STRING;
                data.query = addressZip;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressWorkPhone().equals(dataKey)) {
                addressWorkPhone = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressWorkPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressWorkPhone;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressFaxPhone().equals(dataKey)) {
                addressFaxPhone = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressFaxPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressFaxPhone;
                fields.add(data);
            }
        }

    }
}
