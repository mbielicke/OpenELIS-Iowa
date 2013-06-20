package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
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
import org.openelis.gwt.event.DropEnterEvent.DropPosition;
import org.openelis.gwt.event.DropEnterHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
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
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.AccessionNumberUtility;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.EnvironmentalTab;
import org.openelis.modules.sample.client.PrivateWellTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.QuickEntryTab;
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

    private SampleManager            manager;
    private ModulePermission         userPermission, unreleasePermission, changeDomainPermission;
    private SampleTrackingScreen     trackingScreen;
    private EnvironmentalTab         environmentalTab;
    private PrivateWellTab           wellTab;
    private SDWISTab                 sdwisTab;
    private QuickEntryTab            quickEntryTab;
    private SampleItemTab            sampleItemTab;
    private AnalysisTab              analysisTab;
    private QAEventsTab              qaEventsTab;
    private StorageTab               storageTab;
    private SampleNotesTab           sampleNotesTab;
    private AnalysisNotesTab         analysisNotesTab;
    private AuxDataTab               auxDataTab;
    private ResultTab                testResultsTab;

    private TreeWidget               trackingTree;
    private TextBox                  clientReference;
    private TextBox<Integer>         accessionNumber, orderNumber;
    private TextBox<Datetime>        collectedTime;
    private Dropdown<Integer>        statusId;
    private AppButton                prevPage, nextPage, similarButton, expandButton,
                                     collapseButton, queryButton, updateButton, commitButton, abortButton,
                                     addTestButton, cancelTestButton;
    private MenuItem                 unreleaseSample, previewFinalReport, changeDomain,
                                     historySample, historySampleSpec, historySampleProject,
                                     historySampleOrganization, historySampleItem, historyAnalysis,
                                     historyCurrentResult, historyStorage, historySampleQA, historyAnalysisQA,
                                     historyAuxData;
    private CalendarLookUp           collectedDate, receivedDate;

    private Tabs                     tab;
    private TabPanel                 tabPanel;

    private SampleTreeUtility        treeUtil;
    private SampleHistoryUtility     historyUtility;

    private Integer                  analysisLoggedInId, sampleReleasedId;
    private Query                    query;

    private ScreenService            finalReportService;

    protected AccessionNumberUtility accessionNumUtil;

    private ChangeDomainScreen       changeDomainScreen;

    private HashMap<String, Integer> domainMap;

    public enum Tabs {
        BLANK, ENVIRONMENT, PRIVATE_WELL, SDWIS, QUICK_ENTRY, SAMPLE_ITEM, ANALYSIS, 
        TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public SampleTrackingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleTrackingDef.class));
        SampleTrackingScreenImpl();   
    }
    
    public SampleTrackingScreen(ScreenWindow window) throws Exception {
        super((ScreenDefInt)GWT.create(SampleTrackingDef.class));
        this.window = window;
        SampleTrackingScreenImpl();
    }

    private void SampleTrackingScreenImpl() throws Exception {
        service = new ScreenService("controller?service=org.openelis.modules.sampleTracking.server.SampleTrackingService");
        finalReportService = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");

        userPermission = UserCache.getPermission().getModule("sampletracking");
        unreleasePermission = UserCache.getPermission().getModule("sampleunrelease");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Sample Tracking Screen");
        if (unreleasePermission == null)
            unreleasePermission = new ModulePermission();
        
        changeDomainPermission = UserCache.getPermission().getModule("sampledomainchange");
        if (changeDomainPermission == null)
            changeDomainPermission = new ModulePermission();
        /*
         * this is done here in order to make sure that if the screen is brought
         * up from some other screen (i.e. window != null) then its widgets are 
         * initialized before the constructor ends execution
         */
        if (window != null) {
            postConstructor();
        } else {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    postConstructor();
                }
            });
        }
    }
    
    public void postConstructor() {
        tab = Tabs.BLANK;
        manager = SampleManager.getInstance();

        try {
            CategoryCache.getBySystemNames("sample_status", "analysis_status",
                                                         "type_of_sample", "source_of_sample",
                                                         "sample_container", "unit_of_measure",
                                                         "qaevent_type", "aux_field_value_type",
                                                         "organization_type");
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
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
            
            public void onClick(ClickEvent event) {
            	query(null);
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
                addTestButton.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        cancelTestButton = (AppButton)def.getWidget("cancelTest");
        addScreenHandler(cancelTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.cancelTestClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelTestButton.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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
        
        changeDomain = (MenuItem)def.getWidget("changeDomain");
        addScreenHandler(changeDomain, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                changeDomain();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                changeDomain.enable(EnumSet.of(State.UPDATE).contains(event.getState())
                                    && changeDomainPermission.hasSelectPermission()
                                    && !SampleManager.QUICK_ENTRY.equals(manager.getSample().getDomain())
                                    && !sampleReleasedId.equals(manager.getSample().getStatusId()));
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
                TreeDataItem item;
                SampleDataBundle bundle;
                
                historyUtility.setManager(manager);

                item = trackingTree.getSelection();

                if (item == null || !"analysis".equals(item.leafType)) {
                    window.setError(consts.get("resultHistoryException"));
                    return;
                }

                try {
                    bundle = (SampleDataBundle)item.data;
                    historyUtility.historyCurrentResult(bundle.getSampleItemIndex(), bundle.getAnalysisIndex());
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
        trackingTree = (TreeWidget)def.getWidget("trackingTree");
        addScreenHandler(trackingTree, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Object> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingTree.enable(true);
                trackingTree.enableDrag(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
                trackingTree.enableDrop(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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
                                          DictionaryCache.getById((Integer)treeItem.cells.get(1).value).getEntry());
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

        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                Integer       oldNumber;
                SampleManager quickEntryMan;

                oldNumber = manager.getSample().getAccessionNumber();
                if (oldNumber != null) {
                    if (!Window.confirm(consts.get("accessionNumberEditConfirm"))) {
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        return;
                    }
                }
                try {
                    manager.getSample().setAccessionNumber(event.getValue());

                    if (accessionNumUtil == null)
                        accessionNumUtil = new AccessionNumberUtility();

                    quickEntryMan = accessionNumUtil.accessionNumberEntered(manager.getSample());
                    if (quickEntryMan != null)
                        throw new Exception(consts.get("quickEntryNumberExists"));
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                    accessionNumber.setValue(Util.toString(oldNumber));
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    accessionNumber.setValue(Util.toString(oldNumber));
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(event.getState() == State.QUERY ||
                                       (canEdit() && EnumSet.of(State.UPDATE).contains(event.getState())));
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
                collectedDate.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
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
                collectedTime.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
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
                receivedDate.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
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
                clientReference.enable(event.getState() == State.QUERY ||
                                       (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                            .contains(event.getState())));
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
                    showTabs(Tabs.ENVIRONMENT);

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    environmentalTab.setData(null);
                }
                
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
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    wellTab.setData(manager);                    
                    showTabs(Tabs.PRIVATE_WELL);

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    wellTab.setData(null);
                }
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
        
        try {
            quickEntryTab = new QuickEntryTab(window);
            AbsolutePanel sdwisTabPanel = (AbsolutePanel)def.getWidget("quickEntryDomainPanel");
            sdwisTabPanel.add(quickEntryTab);

        } catch (Exception e) {
            Window.alert("quick entryTab initialize: " + e.getMessage());
        }

        addScreenHandler(sdwisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.SDWIS_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    sdwisTab.setData(manager);                    
                    showTabs(Tabs.SDWIS); 

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    sdwisTab.setData(null);
                }
                sdwisTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisTab.setState(event.getState());
            }
        });
        
        addScreenHandler(quickEntryTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                TreeDataItem selectedRow;

                selectedRow = trackingTree.getSelection();

                if (selectedRow != null && "sample".equals(selectedRow.leafType) &&
                    SampleManager.QUICK_ENTRY.equals(manager.getSample().getDomain())) {
                    quickEntryTab.setData(manager);                    
                    showTabs(Tabs.QUICK_ENTRY); 

                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    quickEntryTab.setData(null);
                }
                quickEntryTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quickEntryTab.setState(event.getState());
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
                    showTabs(Tabs.SAMPLE_ITEM);
                    addTestButton.enable(state == State.UPDATE && canEdit());
                    cancelTestButton.enable(false);
                } else {
                    sampleItemTab.setData(null);
                }
                sampleItemTab.draw();

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
                    showTabs(Tabs.ANALYSIS);
                    addTestButton.enable(state == State.UPDATE && canEdit());
                    cancelTestButton.enable(state == State.UPDATE && canEdit());
                } else {
                    analysisTab.setData(null);
                }
                analysisTab.draw();
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
                    showTabs(Tabs.TEST_RESULT);
                    addTestButton.enable(state == State.UPDATE && canEdit());
                    cancelTestButton.enable(state == State.UPDATE && canEdit());
                } else {
                    testResultsTab.setData(null);
                }
                testResultsTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });
        
        /*
         * to notify ResultTab that an analysis' unit of measure has changed
         */
        analysisTab.addActionHandler(testResultsTab);
        
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
                    showTabs(Tabs.ANALYSIS_NOTES);
                    addTestButton.enable(state == State.UPDATE && canEdit());
                    cancelTestButton.enable(state == State.UPDATE && canEdit());
                } else {
                    analysisNotesTab.setData(null);
                }
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
                TreeDataItem selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getSelection();
                bundle = null;

                if (selectedRow != null)
                    bundle = (SampleDataBundle)selectedRow.data;

                if (selectedRow != null && bundle != null && "note".equals(selectedRow.leafType) &&
                    SampleDataBundle.Type.SAMPLE.equals(bundle.getType())) {
                    sampleNotesTab.setManager(manager);                    
                    showTabs(Tabs.SAMPLE_NOTES);
                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    sampleNotesTab.setManager(manager);
                }
                sampleNotesTab.draw();
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
                    showTabs(Tabs.STORAGE);

                    addTestButton.enable(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.parent.leafType) &&
                                         canEdit());
                    cancelTestButton.enable(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.parent.leafType) &&
                                            canEdit());
                } else {
                    storageTab.setData(null);
                }
                storageTab.draw();
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
                    
                    /* 
                     * we need to make sure that any qa events previously shown
                     * in the tab for any analyses get cleared out if we are showing
                     * only the qa events for a sample and thus we pass null to setData()
                     */
                    if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType()))
                        qaEventsTab.setData(bundle);
                    else
                        qaEventsTab.setData(null);

                    qaEventsTab.setManager(manager);                    
                    showTabs(Tabs.QA_EVENTS);
                    addTestButton.enable(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.parent.leafType) &&
                                         canEdit());
                    cancelTestButton.enable(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.parent.leafType) &&
                                            canEdit());
                } else {
                    qaEventsTab.setData(null);
                    qaEventsTab.setManager(manager);                    
                }
                qaEventsTab.draw();
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
                    showTabs(Tabs.AUX_DATA);
                    addTestButton.enable(false);
                    cancelTestButton.enable(false);
                } else {
                    auxDataTab.setManager(manager);
                }
                auxDataTab.draw();
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
                    auxDataTab.setManager(manager);

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
    
    public void loadSample(SampleManager manager) { 
        ArrayList<SampleManager> result;
        
        result = new ArrayList<SampleManager>();
        result.add(manager);
        
        setState(State.DEFAULT);        
        trackingTree.load(getModel(result));        
        trackingTree.select(0);
        trackingTree.toggle(0);

        window.clearStatus();
    }

    private void initializeDropdowns() {
        Integer id;
        ArrayList<TableDataRow> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisLoggedInId = DictionaryCache.getIdBySystemName("analysis_logged_in");
            domainMap = new HashMap<String, Integer>();
            id = DictionaryCache.getIdBySystemName("environmental");
            domainMap.put(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG, id);
            id = DictionaryCache.getIdBySystemName("private_well");
            domainMap.put(SampleManager.WELL_DOMAIN_FLAG, id);
            id = DictionaryCache.getIdBySystemName("sdwis");
            domainMap.put(SampleManager.SDWIS_DOMAIN_FLAG, id);
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

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)trackingTree.getColumns().get("analysis").get(1).colWidget).setModel(model);
    }

    protected void query(String domain) {
        manager = SampleManager.getInstance();
        trackingTree.clear();
        
        showTabs(Tabs.ENVIRONMENT, Tabs.PRIVATE_WELL, Tabs.SDWIS, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS, Tabs.AUX_DATA);

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        environmentalTab.draw();
        wellTab.draw();
        sdwisTab.draw();
        sampleItemTab.draw();
        analysisTab.draw();
        analysisNotesTab.draw();
        sampleNotesTab.draw();
        auxDataTab.draw();

        setFocus(accessionNumber);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void update(boolean withUnrelease) {
        TreeDataItem sampleRow;
        
        if (trackingTree.getSelectedRow() == -1) {
            window.setError(consts.get("selectRecordToUpdate"));
            return;
        }
        
//        if (sampleReleasedId.equals(manager.getSample().getStatusId())) {
//            if (! withUnrelease) {
//                window.setError(consts.get("cantUpdateReleasedException"));
//                return;
//            }
//        } 

        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            treeUtil.setManager(manager);
            
            sampleRow = getTopLevelNode(trackingTree.getSelection());
            sampleRow.data = manager.getBundle();
            trackingTree.unselect(trackingTree.getSelectedRowIndex());
            checkNode(sampleRow);
            setState(State.DISPLAY);
            trackingTree.select(sampleRow);
            window.clearStatus();
            
            //
            // re-check the status to make sure it is still correct
            //
            if (sampleReleasedId.equals(manager.getSample().getStatusId())) {
                if (withUnrelease) {
                    manager.unrelease(true);
//                } else {
//                    abort();
//                    window.setError(consts.get("cantUpdateReleasedException"));
//                    return;
                }
            } 

            setState(State.UPDATE);
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
    	ArrayList<QueryData> fields;
    	
        setFocus(null);
        
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
        
        if (state == State.QUERY) {
            query = new Query();
            
            fields = getQueryFields();
            
            try {
            	setDomainFields(fields);
            }catch(Exception e) {
            	window.setError(consts.get("queryDomainException"));
            	return;
            }
            
            query.setFields(fields);
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
                e.printStackTrace();
                window.clearStatus();
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
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        TreeDataItem sampleRow;
        String domain;
        
        setFocus(null);
        clearErrors();
        environmentalTab.clearErrors();
        wellTab.clearErrors();
        sdwisTab.clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate(); 
                sampleRow = getTopLevelNode(trackingTree.getSelection());
                trackingTree.unselect(trackingTree.getSelectedRowIndex());
                checkNode(sampleRow);
                setState(State.DISPLAY);
                trackingTree.select(sampleRow);
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
        int                  i;
        ArrayList<QueryData> fields, auxFields;
        QueryData            field;

        fields = super.getQueryFields();

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
        
        return fields;
    }
    
    private void setDomainFields(ArrayList<QueryData> fields) throws Exception {
        String domain = null;
        ArrayList<QueryData> envFields, wellFields, sdwisFields;
        QueryData field;

        envFields = environmentalTab.getQueryFields();
        wellFields = wellTab.getQueryFields();
        sdwisFields = sdwisTab.getQueryFields();

        if (envFields.size() > 0) {
            domain = SampleManager.ENVIRONMENTAL_DOMAIN_FLAG;
            fields.addAll(envFields);
        }
        if (wellFields.size() > 0) {
            if (domain == null) {
                domain = SampleManager.WELL_DOMAIN_FLAG;
                fields.addAll(wellFields);
                addPrivateWellFields(fields);
            } else {
                throw new Exception();
            }
        }
        if (sdwisFields.size() > 0) {
            if (domain == null) {
                domain = SampleManager.SDWIS_DOMAIN_FLAG;
                fields.addAll(sdwisFields);
            } else {
                throw new Exception();
            }
        }

        if (domain != null) {
            // Added this Query Param to keep Quick Entry Samples out of query results
            field = new QueryData();
            field.key = SampleMeta.getDomain();
            field.query = domain;
            field.type = QueryData.Type.STRING;
            fields.add(field);
        }
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

        query.setRowsPerPage(14);
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
                    manager = SampleManager.getInstance();
                    DataChangeEvent.fire(trackingScreen);
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

    private boolean canEdit() {
        return (manager != null && !sampleReleasedId.equals(manager.getSample().getStatusId()));
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
                    sample.cells.add(new TableDataCell(DictionaryCache.getById(sm.getSample()
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
        String domain;
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

        domain = manager.getSample().getDomain();
        /*
         * When the screen gets initialized with the widgets defined in the xsl
         * file, the tabs for the domains aren't present; they are added later to
         * panels defined in the file. Thus the widgets in the tabs aren't included
         * in "def" and thus errors don't get added to them when super.showErrors()
         * is called. Therefore we need to call showErrors() on those tabs separately.      
         */
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain))
            environmentalTab.showErrors(errors);
        else if (SampleManager.WELL_DOMAIN_FLAG.equals(domain))
            wellTab.showErrors(errors);
        else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain))
            sdwisTab.showErrors(errors);
    }
    
    public boolean validate() {        
        boolean valid;
        String domain;
        
        valid = super.validate();
        domain = manager.getSample().getDomain();
        
        /*
         * When the screen gets initialized with the widgets defined in the xsl
         * file, the tabs for the domains aren't present; they are added later to
         * panels defined in the file. Thus the widgets in the tabs aren't included
         * in "def" and thus don't get validated when super.validate() is called.
         * Therefore we need to call validate() on those tabs separately.      
         */
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain))
            return valid && environmentalTab.validate();
        else if (SampleManager.WELL_DOMAIN_FLAG.equals(domain))
            return valid && wellTab.validate();
        else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain))
            return valid && sdwisTab.validate();
        else if (state == State.QUERY)
            //
            // in the state Query, we show tabs for all domains 
            //
            return valid && environmentalTab.validate() && wellTab.validate() &&
            sdwisTab.validate();
        return false;
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
    
    private TreeDataItem getTopLevelNode(TreeDataItem node) {
        if (node.parent != null)
            return getTopLevelNode(node.parent);

        return node;
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
        ArrayList<QueryData> fields;

        query = new Query();      
        fields = new ArrayList<QueryData>();
        
        field = new QueryData();
        field.key = "ACCESSION_NUMBER";
        field.query = manager.getSample().getAccessionNumber().toString();
        field.type = QueryData.Type.STRING;        
        fields.add(field);
        
        field = new QueryData();
        field.key = "PRINTER";
        field.query = "-view-";
        field.type = QueryData.Type.STRING;
        fields.add(field);
        
        query.setFields(fields);

        window.setBusy(consts.get("genReportMessage"));

        finalReportService.call("runReportForSingle", query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                url = "report?file=" + status.getMessage();
                Window.open(URL.encode(url), "FinalReport", null);
                window.setDone(consts.get("done"));
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                caught.printStackTrace();
                Window.alert(caught.getMessage());
            }
        });

    }
    
    private void changeDomain() {
        ScreenWindow modal;
        String domain;
        
        if (changeDomainScreen == null) {
            try {
                changeDomainScreen = new ChangeDomainScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("changeDomainScreen error: " + e.getMessage());
                return;
            }   
            
            changeDomainScreen.addActionHandler(new ActionHandler<ChangeDomainScreen.Action>() {                              
                public void onAction(ActionEvent<ChangeDomainScreen.Action> event) {
                    Integer mapKey, val;                    
                    String oldDomain;
                    TreeDataItem selectedRow;
                    SampleDO sample;
                    SampleOrganizationManager som;
                    SampleEnvironmentalManager sem;
                    SamplePrivateWellManager spm;

                    selectedRow = trackingTree.getSelection();                    
                    val = (Integer)event.getData();
                    sample = manager.getSample();
                    oldDomain = sample.getDomain();
                    try {
                        if (ChangeDomainScreen.Action.OK == event.getAction() && val != null) {
                            mapKey = domainMap.get(oldDomain);
                            /*
                             * if the new domain is the same as the old one then
                             * do nothing   
                             */
                            if (mapKey.equals(val))
                                return;                
                            som = manager.getOrganizations();
                            if (val.equals(domainMap.get(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))) {
                               manager.changeDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
                               sem = (SampleEnvironmentalManager)manager.getDomainManager();
                               sem.getEnvironmental().setIsHazardous("N");
                               
                               if (SampleManager.WELL_DOMAIN_FLAG.equals(oldDomain)) {
                                   /*
                                    * if the old domain was private well then
                                    * find its "report to" and if it was an organization
                                    * then set the new domain's "report to" as that 
                                    */
                                   spm = (SamplePrivateWellManager)manager.getDeletedDomainManager();
                                   setReportTo(sample, som, spm);
                               }
                               /*
                                * reload the tab and refresh its data if the sample's
                                * node is selected  
                                */
                               environmentalTab.setData(manager);
                               if ("sample".equals(selectedRow.leafType))
                                   showTabs(Tabs.ENVIRONMENT); 
                               environmentalTab.draw();
                            } else if (val.equals(domainMap.get(SampleManager.WELL_DOMAIN_FLAG))) {
                                manager.changeDomain(SampleManager.WELL_DOMAIN_FLAG);
                                /*
                                 * If the previous domain had a "report to" then
                                 * set it as the "organization" for this one.Mark
                                 * the old "report to" for deletion because private
                                 * well doesn't have sample organization of that type.                                 
                                 */
                                spm = (SamplePrivateWellManager)manager.getDomainManager();
                                setPrivateWellReportTo(sample, som, spm);
                                som.removeReportTo();  
                                /*
                                 * reload the tab and refresh its data if the sample's
                                 * node is selected  
                                 */
                                wellTab.setData(manager);
                                if ("sample".equals(selectedRow.leafType))
                                    showTabs(Tabs.PRIVATE_WELL); 
                                wellTab.draw();
                            } else if (val.equals(domainMap.get(SampleManager.SDWIS_DOMAIN_FLAG))) {
                                manager.changeDomain(SampleManager.SDWIS_DOMAIN_FLAG);
                                if (SampleManager.WELL_DOMAIN_FLAG.equals(oldDomain)) {
                                    /*
                                     * if the old domain was private well then
                                     * find its "report to" and if it was an organization
                                     * then set the new domain's "report to" as that 
                                     */
                                    spm = (SamplePrivateWellManager)manager.getDeletedDomainManager();
                                    setReportTo(sample, som, spm);
                                }
                                /*
                                 * reload the tab and refresh its data if the sample's
                                 * node is selected  
                                 */
                                sdwisTab.setData(manager);
                                if ("sample".equals(selectedRow.leafType))
                                    showTabs(Tabs.SDWIS); 
                                sdwisTab.draw();
                            }
                        }
                   } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }
                
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("changeDomain"));
        modal.setContent(changeDomainScreen);
        domain = manager.getSample().getDomain();
        changeDomainScreen.setDomain(domainMap.get(domain));
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
    
    private void setReportTo(SampleDO sample, SampleOrganizationManager som, SamplePrivateWellManager spm) {
        AddressDO addr;
        OrganizationDO org;           
        SampleOrganizationViewDO sorg;
        
        org = spm.getPrivateWell().getOrganization();
        if (org != null) {
            sorg = new SampleOrganizationViewDO();
            addr = org.getAddress();
            
            sorg.setSampleId(sample.getId());
            sorg.setOrganizationCity(addr.getCity());
            sorg.setOrganizationAttention(spm.getPrivateWell().getReportToAttention());
            sorg.setOrganizationFaxPhone(addr.getFaxPhone());
            sorg.setOrganizationId(org.getId());
            sorg.setOrganizationMultipleUnit(addr.getMultipleUnit());
            sorg.setOrganizationName(org.getName());
            sorg.setOrganizationState(addr.getState());
            sorg.setOrganizationStreetAddress(addr.getStreetAddress());
            sorg.setOrganizationWorkPhone(addr.getWorkPhone());
            sorg.setOrganizationZipCode(addr.getZipCode());                  
            som.setReportTo(sorg);
        }
    }
    
    private void setPrivateWellReportTo(SampleDO sample, SampleOrganizationManager som, SamplePrivateWellManager spm) {
        Integer id;
        AddressDO addr;
        OrganizationDO org;           
        SamplePrivateWellViewDO spw;
        SampleOrganizationViewDO sorg;
        
        sorg = som.getReportTo();
        if (sorg != null) {
            id = sorg.getOrganizationId();
            spw = spm.getPrivateWell();
            org = new OrganizationDO();
            spw.setOrganization(org);              
            org.setId(id);
            org.setName(sorg.getOrganizationName());
            spw.setOrganizationId(id);
            addr = org.getAddress();
            
            spw.setReportToAttention(sorg.getOrganizationAttention());
            addr.setCity(sorg.getOrganizationCity());
            addr.setFaxPhone(sorg.getOrganizationFaxPhone());
            addr.setMultipleUnit(sorg.getOrganizationMultipleUnit());
            addr.setState(sorg.getOrganizationState());
            addr.setStreetAddress(sorg.getOrganizationStreetAddress());
            addr.setWorkPhone(sorg.getOrganizationWorkPhone());
            addr.setZipCode(sorg.getOrganizationZipCode());                                           
        }
    } 
}