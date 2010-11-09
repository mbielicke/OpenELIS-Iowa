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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.DragItem;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasExceptions;
import org.openelis.gwt.widget.HasValue;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeNodeOpenHandler;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
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

    private Tree                 trackingTree;
    private TextBox              clientReference;
    private TextBox<Integer>     accessionNumber, orderNumber;
    private TextBox<Datetime>    collectedTime;
    private Dropdown<Integer>    statusId;
    private Button               prevPage, nextPage, similarButton, expandButton, collapseButton,
                                 queryButton, updateButton, commitButton, abortButton, addTestButton,
                                 cancelTestButton;
    private MenuItem             envMenuQuery, wellMenuQuery, sdwisMenuQuery, unreleaseSample, historySample,
                                 historySampleSpec, historySampleProject, historySampleOrganization,
                                 historySampleItem, historyAnalysis, historyCurrentResult, historyStorage,
                                 historySampleQA, historyAnalysisQA, historyAuxData;
    private Calendar             collectedDate, receivedDate;

    private Tabs                 tab;
    private TabPanel             tabPanel;

    private SampleTreeUtility    treeUtil;
    private SampleHistoryUtility historyUtility;

    private Integer              analysisLoggedInId,  sampleErrorStatusId, 
                                 sampleReleasedId;
    private Query                query;

    public enum Tabs {
        BLANK, ENVIRONMENT, PRIVATE_WELL, SDWIS, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA
    };

    public SampleTrackingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleTrackingDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.sampleTracking.server.SampleTrackingService");

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
            com.google.gwt.user.client.Window.alert("TrackingScreen: missing dictionary entry; " + e.getMessage());
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
        expandButton = (Button)def.getWidget("expand");
        addScreenHandler(expandButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                SampleManager man;
                Node row;
                for (int i = 0; i < trackingTree.getRowCount(); i++ ) {
                    row = trackingTree.getNodeAt(i);

                    if ( !row.isLoaded()) {
                        try {
                            man = ((SampleDataBundle)row.getData()).getSampleManager();
                            loadSampleItem(man, row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                trackingTree.expand();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                expandButton.setEnabled(event.getState() == State.DISPLAY);
            }
        });

        collapseButton = (Button)def.getWidget("collapse");
        addScreenHandler(collapseButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                trackingTree.collapse();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collapseButton.setEnabled(event.getState() == State.DISPLAY);
            }
        });

        similarButton = (Button)def.getWidget("similar");
        addScreenHandler(similarButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME needs implemented
            }

            public void onStateChange(StateChangeEvent<State> event) {
                similarButton.setEnabled(event.getState() == State.DISPLAY);
            }
        });

        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        envMenuQuery = (MenuItem)def.getWidget("environmentalSample");
        envMenuQuery.addCommand(new Command() {
			public void execute() {
                query(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            }
        });

        wellMenuQuery = (MenuItem)def.getWidget("privateWellWaterSample");
        wellMenuQuery.addCommand(new Command() {
            public void execute() {
                query(SampleManager.WELL_DOMAIN_FLAG);
            }
        });

        sdwisMenuQuery = (MenuItem)def.getWidget("sdwisSample");
        sdwisMenuQuery.addCommand(new Command() {
            public void execute() {
                query(SampleManager.SDWIS_DOMAIN_FLAG);
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        addTestButton = (Button)def.getWidget("addTest");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addTest();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        cancelTestButton = (Button)def.getWidget("cancelTest");
        addScreenHandler(cancelTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.cancelTestClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelTestButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
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
                ActionEvent.fire(trackingScreen, ResultTab.Action.RESULT_HISTORY, null);
            }
        };
        
        unreleaseSample = (MenuItem)def.getWidget("unreleaseSample");
        addScreenHandler(unreleaseSample, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                unreleaseSample.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) && unreleasePermission.hasSelectPermission());
            }
        });
        
        unreleaseSample.addCommand(new Command() {
			public void execute() {
				unrelease();
			}
		});

        historySample = (MenuItem)def.getWidget("historySample");
        addScreenHandler(historySample, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySample.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySample.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historySample();
			}
		});

        historySampleSpec = (MenuItem)def.getWidget("historySampleSpec");
        addScreenHandler(historySampleSpec, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySampleSpec.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleSpec.addCommand(new Command() {
			public void execute() {
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
		});

        historySampleProject = (MenuItem)def.getWidget("historySampleProject");
        addScreenHandler(historySampleProject, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySampleProject.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleProject.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historySampleProject();
			}
		});

        historySampleOrganization = (MenuItem)def.getWidget("historySampleOrganization");
        addScreenHandler(historySampleOrganization, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySampleOrganization.setEnabled(EnumSet.of(State.DISPLAY)
                                                        .contains(event.getState()));
            }
        });
        
        historySampleOrganization.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historySampleOrganization();
			}
		});

        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleItem.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historySampleItem();
			}
		});

        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyAnalysis.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historyAnalysis();
			}
		});

        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyCurrentResult.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historyCurrentResult();
			}
		});

        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyStorage.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historyStorage();
			}
		});

        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleQA.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historySampleQA();
			}
		});

        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyAnalysisQA.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historyAnalysisQA();
			}
		});

        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyAuxData.addCommand(new Command() {
			public void execute() {
				historyUtility.setManager(manager);
                historyUtility.historyAuxData();
			}
		});

        //
        // screen fields
        //
        trackingTree = (Tree)def.getWidget("trackingTree");
        addScreenHandler(trackingTree, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Object> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingTree.setEnabled(true);
                trackingTree.enableDrag();
                trackingTree.enableDrop();
            }
        });

        trackingTree.addBeforeNodeOpenHandler(new BeforeNodeOpenHandler() {
            public void onBeforeNodeOpen(BeforeNodeOpenEvent event) {
                if (event.getNode().getType().equals("sample") && !event.getNode().isLoaded()) {
                    try {
                        loadSampleItem(manager, event.getNode());
                        event.getNode().setDeferLoadingUntilExpand(false);
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert("leafOpened: " + e.getMessage());
                    }
                }
            }

        });

        trackingTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        trackingTree.enableDrag();
        trackingTree.enableDrop();

        trackingTree.getDragController().addBeforeDragStartHandler(new BeforeDragStartHandler<DragItem>() {
            public void onBeforeDragStart(BeforeDragStartEvent<DragItem> event) {
                Node treeItem;
                Label label;

                try {
                    treeItem = trackingTree.getNodeAt(event.getDragObject().getIndex());
                    if ( !treeItem.getType().equals("analysis"))
                        event.cancel();
                    else {
                        label = new Label(treeItem.getCell(0) + " | " +
                                          DictionaryCache.getEntryFromId((Integer)treeItem.getCell(1)).getEntry());
                        label.setStyleName("ScreenLabel");
                        label.setWordWrap(false);
                        event.setProxy(label);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert("tree beforeDragStart: " + e.getMessage());
                }
            }
        });

        trackingTree.getDropController().addBeforeDropHandler(new BeforeDropHandler<DragItem>() {
            public void onBeforeDrop(BeforeDropEvent<DragItem> event) {
                AnalysisManager am;
                Node dragItem, dropTarget;
                SampleDataBundle dragKey, dropKey, newBundle;

                dragItem = trackingTree.getNodeAt(event.getDragObject().getIndex());
                dragKey = (SampleDataBundle)dragItem.getData();
                dropTarget = ((Node)event.getDropTarget());
                dropKey = (SampleDataBundle)dropTarget.getData();

                try {
                    manager.getSampleItems().moveAnalysis(dragKey, dropKey);

                    // reset the dropped row data bundle, and its children
                    am = manager.getSampleItems().getAnalysisAt(dropKey.getSampleItemIndex());
                    newBundle = am.getBundleAt(am.count()-1);
                    dragItem.setData(newBundle);
                    
                    for(int i=0; i<dragItem.getChildCount(); i++)
                        dragItem.getChildAt(i).setData(newBundle);
                                        
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert("Move failed: " + e.getMessage());
                }
            }
        });

        trackingTree.getDropController().addDropEnterHandler(new DropEnterHandler<DragItem>() {
            public void onDropEnter(DropEnterEvent<DragItem> event) {
                Node dropTarget, dropTargetParent, dragItem, dragItemParent;

                dragItem = trackingTree.getNodeAt(event.getDragObject().getIndex());
                dropTarget = ((Node)event.getDropTarget());
                
                dropTargetParent = dropTarget;
                while(!"sampleItem".equals(dropTargetParent.getType()))
                    dropTargetParent = dropTargetParent.getParent();
                
                dragItemParent = dragItem;
                while(!"sampleItem".equals(dragItemParent.getType()))
                    dragItemParent = dragItemParent.getParent();
                
                if (!dropTarget.getType().equals("analysis") && !dropTarget.getType().equals("storage")  ||
                                (dropTarget.getType().equals("storage") && "analysis".equals(dropTarget.getParent().getType())) || 
                                (dropTarget.getType().equals("analysis") && event.getDropPosition() == DropPosition.ON) ||
                                (dropTarget.getType().equals("storage") && (event.getDropPosition() == DropPosition.ON || event.getDropPosition() == DropPosition.BELOW)) || 
                                dropTargetParent.equals(dragItemParent) || 
                                !dropTargetParent.getParent().equals(dragItemParent.getParent()))
                    event.cancel();
            }
        });

        trackingTree.addDropTarget(trackingTree.getDropController());
        //trackingTree.enableDrag();
        //trackingTree.enableDrop();

        //NEED TO FIND A WAY TO DO THIS NOW
        trackingTree.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                SampleDataBundle key;

                if (state == State.UPDATE && event.getProposedSelect() != null) {
                    key = (SampleDataBundle)event.getProposedSelect().getData();

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

        trackingTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                SampleDataBundle bundle;
                SampleManager man;

                bundle = (SampleDataBundle)trackingTree.getNodeAt(event.getSelectedItem()).getData();
                man = bundle.getSampleManager();
                if (manager == null || !man.getSample().getId().equals(manager.getSample().getId()))
                    manager = man;

                DataChangeEvent.fire(trackingScreen);
                window.clearStatus();
            }
        });
        
        prevPage = (Button)def.getWidget("prevPage");
        addScreenHandler(prevPage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previousPage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevPage.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextPage = (Button)def.getWidget("nextPage");
        addScreenHandler(nextPage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                nextPage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextPage.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(manager.getSample().getAccessionNumber());
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                manager.getSample().setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getOrderId());
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(manager.getSample().getOrderId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setOrderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                orderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedDate = (Calendar)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(manager.getSample().getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
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
                collectedTime.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        receivedDate = (Calendar)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(manager.getSample().getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(manager.getSample().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
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
                clientReference.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
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
            com.google.gwt.user.client.Window.alert("env tab initialize: " + e.getMessage());
        }

        addScreenHandler(environmentalTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                Node selectedRow;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "sample".equals(selectedRow.getType()) &&
                    SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    environmentalTab.setData(manager);
                    environmentalTab.draw();
                    showTabs(Tabs.ENVIRONMENT);

                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
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
            com.google.gwt.user.client.Window.alert("well tab initialize: " + e.getMessage());
        }

        addScreenHandler(wellTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                Node selectedRow;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "sample".equals(selectedRow.getType()) &&
                    SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    wellTab.setData(manager);
                    wellTab.draw();
                    showTabs(Tabs.PRIVATE_WELL);

                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
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
            com.google.gwt.user.client.Window.alert("sdwis tab initialize: " + e.getMessage());
        }

        addScreenHandler(sdwisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                Node selectedRow;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "sample".equals(selectedRow.getType()) &&
                    SampleManager.SDWIS_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                    sdwisTab.setData(manager);
                    sdwisTab.draw();
                    showTabs(Tabs.SDWIS);

                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "sampleItem".equals(selectedRow.getType())) {
                    bundle = (SampleDataBundle)selectedRow.getData();
                    sampleItemTab.setData(bundle);
                    sampleItemTab.draw();
                    showTabs(Tabs.SAMPLE_ITEM);
                    addTestButton.setEnabled(state == State.UPDATE);
                    cancelTestButton.setEnabled(false);
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "result".equals(selectedRow.getType())) {
                    bundle = (SampleDataBundle)selectedRow.getData();
                    analysisTab.setData(bundle);
                    analysisTab.draw();
                    showTabs(Tabs.ANALYSIS);
                    addTestButton.setEnabled(state == State.UPDATE);
                    cancelTestButton.setEnabled(state == State.UPDATE);
                } else
                    analysisTab.setData(null);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });

        sampleItemTab.addActionHandler(new ActionHandler<SampleItemTab.Action>() {
            public void onAction(ActionEvent<SampleItemTab.Action> event) {
                Node selected;

                if (state != State.QUERY) {
                    selected = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                    treeUtil.updateSampleItemRow(selected);
                    //trackingTree.refreshRow(selected);
                }
            }
        });

        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent event) {
                Node selected;

                if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), false);
                } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), true);
                } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                    selected = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                    // we want to update the result tab instead of the analysis
                    // tab
                    treeUtil.updateAnalysisRow(selected.getParent());
                    //trackingTree.refreshRow(selected.getParent());
                }
            }
        });

        testResultsTab = new ResultTab(def, window, this);
        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "analysis".equals(selectedRow.getType())) {
                    bundle = (SampleDataBundle)selectedRow.getData();

                    testResultsTab.setData(bundle);
                    testResultsTab.draw();
                    showTabs(Tabs.TEST_RESULT);
                    addTestButton.setEnabled(state == State.UPDATE);
                    cancelTestButton.setEnabled(state == State.UPDATE);
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());
                bundle = null;

                if (selectedRow != null)
                    bundle = (SampleDataBundle)selectedRow.getData();

                if (selectedRow != null && bundle != null && "note".equals(selectedRow.getType()) &&
                    SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())) {
                    analysisNotesTab.setData(bundle);
                    analysisNotesTab.draw();
                    showTabs(Tabs.ANALYSIS_NOTES);
                    addTestButton.setEnabled(state == State.UPDATE);
                    cancelTestButton.setEnabled(state == State.UPDATE);
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());
                bundle = null;

                if (selectedRow != null)
                    bundle = (SampleDataBundle)selectedRow.getData();

                if (selectedRow != null && bundle != null && "note".equals(selectedRow.getType()) &&
                    SampleDataBundle.Type.SAMPLE.equals(bundle.getType())) {
                    sampleNotesTab.setManager(manager);
                    sampleNotesTab.draw();
                    showTabs(Tabs.SAMPLE_NOTES);
                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "storage".equals(selectedRow.getType())) {
                    bundle = (SampleDataBundle)selectedRow.getData();

                    storageTab.setData(bundle);
                    storageTab.draw();
                    showTabs(Tabs.STORAGE);

                    addTestButton.setEnabled(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.getParent().getType()));
                    cancelTestButton.setEnabled(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.getParent().getType()));
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
                Node selectedRow;
                SampleDataBundle bundle;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "qaevent".equals(selectedRow.getType())) {
                    bundle = (SampleDataBundle)selectedRow.getData();

                    if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType()))
                        qaEventsTab.setData(bundle);

                    qaEventsTab.setManager(manager);
                    qaEventsTab.draw();
                    showTabs(Tabs.QA_EVENTS);
                    addTestButton.setEnabled(state == State.UPDATE &&
                                         "analysis".equals(selectedRow.getParent().getType()));
                    cancelTestButton.setEnabled(state == State.UPDATE &&
                                            "analysis".equals(selectedRow.getParent().getType()));
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
                Node selectedRow;

                selectedRow = trackingTree.getNodeAt(trackingTree.getSelectedNode());

                if (selectedRow != null && "auxdata".equals(selectedRow.getType())) {
                    auxDataTab.setManager(manager);
                    auxDataTab.draw();
                    showTabs(Tabs.AUX_DATA);
                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
                } else
                    auxDataTab.setManager(manager);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });

        treeUtil = new SampleTreeUtility(window, trackingTree, this) {
            public Node addNewTreeRowFromBundle(Node parentRow,
                                                        SampleDataBundle bundle) {
                Node results, analysis, storage, qaevent, note;

                results = new Node(2);
                results.setType("analysis");
                results.setData(bundle);

                analysis = new Node(1);
                analysis.setType("result");
                analysis.setData(bundle);
                analysis.setCell(0,consts.get("analysis"));
                results.add(analysis);

                note = new Node(1);
                note.setType("note");
                note.setData(bundle);
                note.setCell(0,consts.get("notes"));
                results.add(note);

                storage = new Node(1);
                storage.setType("storage");
                storage.setData(bundle);
                storage.setCell(0,consts.get("storage"));
                results.add(storage);

                qaevent = new Node();
                qaevent.setType("qaevent");
                qaevent.setData(bundle);
                qaevent.setCell(0,consts.get("qaEvents"));
                results.add(qaevent);
                trackingTree.addNodeAt(parentRow, results, parentRow.getChildCount() - 1);

                return results;
            }

            public void selectNewRowFromBundle(Node row) {
                trackingTree.selectNodeAt(row.getFirstChild());
                trackingTree.scrollToVisible(trackingTree.getSelectedNode());
            }
        };

        treeUtil.addActionHandler(new ActionHandler() {
            public void onAction(ActionEvent event) {
                ActionEvent.fire(trackingScreen, event.getAction(), event.getData());
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

        // sample status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId())).setModel(model);

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)trackingTree.getNodeDefinitionAt("analysis",1).getCellEditor().getWidget()).setModel(model);

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
        Node sampleRow;
        
        if (trackingTree.getSelectedNode() == -1) {
            window.setError(consts.get("selectRecordToUpdate"));
            return;
        }
        
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            treeUtil.setManager(manager);
            
            topLevelIndex = getTopLevelIndex(trackingTree.getNodeAt(trackingTree.getSelectedNode()));
            sampleRow = trackingTree.getNodeAt(topLevelIndex);
            sampleRow.setData(manager.getBundle());
            trackingTree.unselectNodeAt(trackingTree.getSelectedNode());
            checkNode(sampleRow);
            setState(State.DISPLAY);
            trackingTree.selectNodeAt(topLevelIndex);
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
            if (trackingTree.getSelectedNode() < -1)
                SelectionEvent.fire(trackingTree, trackingTree.getSelectedNode());
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
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }

    protected void commitWithWarnings() {
        clearErrors();
        environmentalTab.clearErrors();
        wellTab.clearErrors();
        sdwisTab.clearErrors();
        
        manager.getSample().setStatusId(sampleErrorStatusId);

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
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }

    protected void abort() {
        int topLevelIndex;
        Node sampleRow;
        
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
                topLevelIndex = getTopLevelIndex(trackingTree.getNodeAt(trackingTree.getSelectedNode()));
                sampleRow = trackingTree.getNodeAt(topLevelIndex);
                trackingTree.unselectNodeAt(trackingTree.getSelectedNode());
                checkNode(sampleRow);
                setState(State.DISPLAY);
                trackingTree.selectNodeAt(topLevelIndex);
                window.clearStatus();
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else {
            window.clearStatus();
        }
    }
    
    public ArrayList<QueryData> getQueryFields() {
        boolean addDomain;
        ArrayList<QueryData> fields, auxFields, tmpFields;
        QueryData field;

        fields = super.getQueryFields();
        tmpFields = null;
        addDomain = true;

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
            for (int i = 0; i < auxFields.size(); i++ ) {                
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
        Node sampleItem;
        int analysisIndex;
        Node results, analysis, storage, qaevent, note;
    
        sampleItem = trackingTree.getNodeAt(trackingTree.getSelectedNode());
        while ( !"sampleItem".equals(sampleItem.getType()))
            sampleItem = sampleItem.getParent();
    
        bundle = (SampleDataBundle)sampleItem.getData();
    
        try {
            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
            
            if(analysisBundle == null){
                analysisIndex = anMan.addAnalysis();
                analysisBundle = anMan.getBundleAt(analysisIndex);
            }else
                analysisIndex = analysisBundle.getAnalysisIndex();
    
            results = new Node(2);
            results.setOpen(true);
            results.setType("analysis");
            results.setData(analysisBundle);
            results.setCell(0,"<> : <>");
            results.setCell(1,analysisLoggedInId);
    
            analysis = new Node(1);
            analysis.setType("result");
            analysis.setData(analysisBundle);
            analysis.setCell(0,consts.get("analysis"));
            results.add(analysis);
    
            note = new Node(1);
            note.setType("note");
            note.setData(analysisBundle);
            note.setCell(0,consts.get("notes"));
            results.add(note);
    
            storage = new Node(1);
            storage.setType("storage");
            storage.setData(analysisBundle);
            storage.setCell(0,consts.get("storage"));
            results.add(storage);
    
            qaevent = new Node(1);
            qaevent.setType("qaevent");
            qaevent.setData(analysisBundle);
            qaevent.setCell(0,consts.get("qaEvents"));
            results.add(qaevent);
    
            trackingTree.addNodeAt(sampleItem, results, analysisIndex);
            trackingTree.selectNodeAt(analysis);
            trackingTree.scrollToVisible(trackingTree.getSelectedNode());
    
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
                
                trackingTree.setRoot(getModel(result));
                
                if (result.size() > 0)
                    trackingTree.selectNodeAt(0);

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
                    com.google.gwt.user.client.Window.alert("Error: envsample call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private Node getModel(ArrayList<SampleManager> result) {
        Node model;

        model = new Node();
        if (result == null)
            return model;

        try {
            for (SampleManager sm : result) {
                Node sample = new Node(2);
                sample.setDeferLoadingUntilExpand(true);
                sample.setType("sample");
                sample.setData(sm.getBundle());
                sample.setCell(0,sm.getSample().getAccessionNumber());
                try {
                    sample.setCell(1,DictionaryCache.getEntryFromId(sm.getSample().getStatusId()).getEntry());
                } catch (Exception e) {
                    sample.setCell(1,sm.getSample().getStatusId());
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

    private void loadSampleItem(SampleManager sm, Node row) throws Exception {
        Node item, results, analysis, storage, qaevent, note, aux;
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

                item = new Node(2);
                item.setOpen(true);
                item.setType("sampleItem");
                item.setKey(itemDO.getId());
                item.setData(siBundle);
                item.setCell(0,itemDO.getItemSequence() + " - " + treeUtil.formatTreeString(itemDO.getContainer()));
                item.setCell(1,itemDO.getTypeOfSample());

                anMan = sm.getSampleItems().getAnalysisAt(i);
                if (anMan != null) {
                    for (int j = 0; j < anMan.count(); j++ ) {
                        anDO = anMan.getAnalysisAt(j);
                        anBundle = anMan.getBundleAt(j);

                        results = new Node(2);
                        results.setType("analysis");
                        results.setKey(anDO.getId());
                        results.setData(anBundle);
                        results.setCell(0,treeUtil.formatTreeString(anDO.getTestName()) +
                                          " : " +
                                          treeUtil.formatTreeString(anDO.getMethodName()));
                        results.setCell(1,anDO.getStatusId());

                        analysis = new Node(1);
                        analysis.setType("result");
                        analysis.setData(anBundle);
                        analysis.setCell(0,consts.get("analysis"));
                        results.add(analysis);

                        storage = new Node(1);
                        storage.setType("storage");
                        storage.setData(anBundle);
                        storage.setCell(0,consts.get("storage"));

                        results.add(storage);
                        qaevent = new Node(1);
                        qaevent.setType("qaevent");
                        qaevent.setData(anBundle);
                        qaevent.setCell(0,consts.get("qaEvents"));
                        results.add(qaevent);

                        note = new Node(1);
                        note.setType("note");
                        note.setData(anBundle);
                        note.setCell(0,consts.get("notes"));
                        results.add(note);
                        item.add(results);
                    }
                }
                storage = new Node(1);
                storage.setType("storage");
                storage.setData(siBundle);
                storage.setCell(1,consts.get("storage"));
                item.add(storage);
                row.add(item);
            }
            note = new Node(1);
            note.setType("note");
            note.setData(sBundle);
            note.setCell(0,consts.get("notes"));
            row.add(note);

            qaevent = new Node(1);
            qaevent.setType("qaevent");
            qaevent.setData(sBundle);
            qaevent.setCell(0,consts.get("qaEvents"));
            row.add(qaevent);

            aux = new Node(1);
            aux.setType("auxdata");
            aux.setData(sBundle);
            aux.setCell(0,consts.get("auxData"));
            row.add(aux);
            row.setDeferLoadingUntilExpand(false);
        }
    }

    public void showErrors(ValidationErrorsList errors) {
        ArrayList<LocalizedException> formErrors;
        TableFieldErrorException tableE;
        FormErrorException formE;
        FieldErrorException fieldE;
        Table tableWid;
        HasExceptions field;

        formErrors = new ArrayList<LocalizedException>();
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof TableFieldErrorException) {
                tableE = (TableFieldErrorException)ex;
                tableWid = (Table)def.getWidget(tableE.getTableKey());
                tableWid.addException(tableE.getRowIndex(), tableWid.getColumnByName(tableE.getFieldName()), tableE);
            } else if (ex instanceof FormErrorException) {
                formE = (FormErrorException)ex;
                formErrors.add(formE);
            } else if (ex instanceof FieldErrorException) {
                fieldE = (FieldErrorException)ex;
                field = (HasExceptions)def.getWidget(fieldE.getFieldName());

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
            if (wid instanceof HasValue) {
                ((HasValue)wid).validateValue();
                if ( ((HasExceptions)wid).hasExceptions())
                    valid = false;
            }
        }
        
        manager.validate();
        
        return valid;
    }
    
    private void checkNode(Node item) {
        SampleDataBundle openKey;
        ArrayList<SampleDataBundle> openItems;

        if (!item.isOpen()) {
            item.removeAllChildren();
            item.setDeferLoadingUntilExpand(true);
            return;
        }

        openItems = new ArrayList<SampleDataBundle>();
        for (Node child : item.children()) {
            checkChildOpen(child, openItems);
        }
        item.removeAllChildren();
        try {
            loadSampleItem(manager, item);
            while (openItems.size() > 0) {
                openKey = openItems.remove(0);
                searchForKey(openKey, item);
            }
            //trackingTree.refreshRow(item);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("checkNode: " + e.getMessage());
        }
    }

    private boolean searchForKey(SampleDataBundle bundle, Node item) {
        if (bundle.getType() == ((SampleDataBundle)item.getData()).getType() &&
            bundle.getSampleItemIndex() == ((SampleDataBundle)item.getData()).getSampleItemIndex() &&
            bundle.getAnalysisIndex() == ((SampleDataBundle)item.getData()).getAnalysisIndex()) {
            if ( !item.isOpen())
                item.setOpen(true);
            return true;
        }
        for (Node child : item.children()) {
            if (searchForKey(bundle, child))
                break;
        }
        return false;
    }

    private void checkChildOpen(Node item, ArrayList<SampleDataBundle> openItems) {
        if (item.isOpen()) {
            openItems.add((SampleDataBundle)item.getData());
            for (Node child : item.children()) {
                checkChildOpen(child, openItems);
            }
        }
    }

    private int getTopLevelIndex(Node node) {
        if (node.getParent() != null)
            return getTopLevelIndex(node.getParent());

        return trackingTree.getRoot().getIndex(node);
    }
    
    private void unrelease(){
        Confirm confirm;
               
        if (trackingTree.getSelectedNode() == -1) {
            window.setError(consts.get("selectRecordToUpdate"));
            return;
        }

        if (!sampleReleasedId.equals(manager.getSample().getStatusId())) {
            com.google.gwt.user.client.Window.alert(consts.get("wrongStatusUnrelease"));
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
