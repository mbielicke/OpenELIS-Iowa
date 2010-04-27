package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
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
import org.openelis.gwt.event.DropEvent;
import org.openelis.gwt.event.DropHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.gwt.widget.tree.event.LeafClosedEvent;
import org.openelis.gwt.widget.tree.event.LeafClosedHandler;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
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
import org.openelis.modules.sample.client.SampleTreeUtility;
import org.openelis.modules.sample.client.StorageTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class SampleTrackingScreen extends Screen implements HasActionHandlers {
    
    private Integer                        analysisLoggedInId, analysisCancelledId,
    analysisReleasedId, analysisInPrep, sampleLoggedInId, sampleErrorStatusId,
    sampleReleasedId, userId;
    
    public enum Tabs {BLANK,ENVIRONMENT,PRIVATE_WELL,SDWIS,
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA
    };
	
    protected Tabs                         tab = Tabs.BLANK;
    protected ArrayList<Tabs>              tabIndexes = new ArrayList<Tabs>();
    protected TextBox                      clientReference;
    protected TextBox<Integer>             accessionNumber, orderNumber;
    protected TextBox<Datetime>            collectedTime;
   
    protected Dropdown<Integer>            statusId;
    protected TreeWidget                   itemsTree;
    protected AppButton                    removeRow, similarButton, expandButton, collapseButton,
    									   addItem, addAnalysis, queryButton, updateButton,
    									   nextButton, prevButton, commitButton, abortButton,addTestButton,cancelTestButton;
    protected CalendarLookUp               collectedDate, receivedDate;
	
	private SecurityModule 				   security;
	
	private ScreenNavigator 		       nav;
	private SampleManager                  manager;
    protected AccessionNumberUtility       accessionNumUtil;
    protected SampleHistoryUtility         historyUtility;
	
	private TabPanel                  	   sampleContent;
	//private org.openelis.gwt.widget.TabBar                         sampleBar;
	
	private EnvironmentalTab               environmentalTab;
	private PrivateWellTab			       wellTab;
	private SDWISTab					   sdwisTab;
	private SampleItemTab                  sampleItemTab;
	private AnalysisTab                    analysisTab;
	private QAEventsTab                    qaEventsTab;
	private StorageTab                     storageTab;
	private SampleNotesTab                 sampleNotesTab;
	private AnalysisNotesTab               analysisNotesTab;
	private AuxDataTab                     auxDataTab;
	private ResultTab      				   testResultsTab;
	private TreeWidget                     atozTree;
	private int                            tempId;
	private SampleTreeUtility			   treeUtil;	
	
    protected MenuItem                     historySample, historySampleEnvironmental,historySamplePrivateWell,historySampleSDWIS,
    historySampleProject, historySampleOrganization, historySampleItem,
    historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
    historyAnalysisQA, historyAuxData;
    
    private SampleTrackingScreen trackScreen = this; 
	
    public SampleTrackingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleTrackingDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.sampleTracking.server.SampleTrackingService");

        security = OpenELIS.security.getModule("sample");
        if (security == null)
            throw new SecurityException("screenPermException", "Sample Tracking Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
        
    }
    
    public void postConstructor() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        
        try{
            DictionaryCache.preloadByCategorySystemNames("sample_status", "analysis_status", "type_of_sample", 
                                                         "source_of_sample", "sample_container", "unit_of_measure", 
                                                         "qaevent_type", "aux_field_value_type", "organization_type");
        }catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
    	sampleContent = (TabPanel)def.getWidget("SampleContent");
    	
        sampleContent.addSelectionHandler(new SelectionHandler<Integer>() {
    		public void onSelection(SelectionEvent<Integer> event) {
    			tab = Tabs.values()[event.getSelectedItem()];
    			drawTabs();
    		}
    	});
        
    	sampleContent.getTabBar().setStyleName("None");
    	
        final MenuItem envMenuQuery = (MenuItem)def.getWidget("environmentalSample");
        envMenuQuery.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
                manager = SampleManager.getInstance();
                manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
                showTabs(Tabs.ENVIRONMENT,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);
        		query();
        	}
        });
        
        final MenuItem wellMenuQuery = (MenuItem)def.getWidget("privateWellWaterSample");
        wellMenuQuery.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		manager = SampleManager.getInstance();
        		manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
        		showTabs(Tabs.PRIVATE_WELL,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);
        		query();
        	}
        });
        
        final MenuItem sdwisMenuQuery = (MenuItem)def.getWidget("sdwisSample");
        sdwisMenuQuery.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		manager = SampleManager.getInstance();
        		manager.getSample().setDomain(SampleManager.SDWIS_DOMAIN_FLAG);
        		showTabs(Tabs.SDWIS,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);
        		query();
        	}
        });

        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                //query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasSelectPermission())
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
            
            public void onDataChange(DataChangeEvent event) {
                if (canEdit() && EnumSet.of(State.DISPLAY).contains(state) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if(state == State.DISPLAY)
                    updateButton.enable(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.setState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });
        
        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
        
        expandButton = (AppButton)def.getWidget("expand");
        addScreenHandler(expandButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		for(int i = 0; i < atozTree.getData().size(); i++) {
        			TreeDataItem sample = atozTree.getData().get(i);
        			if(!sample.isLoaded()) {
        				try {
        					loadSampleItem((SampleManager)nav.getQueryResult().get(i),sample);
        					sample.checkForChildren(false);
        				}catch(Exception e){
        					e.printStackTrace();
        				}
        			}
        		}
        		atozTree.expand();
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		expandButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        
        collapseButton = (AppButton)def.getWidget("collapse");
        addScreenHandler(collapseButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		atozTree.collapse();
        		for(TreeDataItem sample : atozTree.getData()){
        			sample.checkForChildren(true);
        			sample.getItems().clear();
        		}
        		
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		collapseButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        
        similarButton = (AppButton)def.getWidget("similar");
        addScreenHandler(similarButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		similar();
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		similarButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        
        addTestButton = (AppButton)def.getWidget("addTest");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event) {
        		addTest();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		if(atozTree.getSelection() != null && atozTree.getSelection().leafType.equals("item")) {
        			addTestButton.enable(event.getState() == State.UPDATE);
        		}else
        			addTestButton.enable(false);
        	}
        });
        
        cancelTestButton = (AppButton)def.getWidget("cancelTest");
        addScreenHandler(cancelTestButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		try {
        			cancelTest();
        		}catch (ValidationErrorsList e) {
                        showErrors(e);
        		}catch(Exception e) {
        			e.printStackTrace();
        			Window.alert(e.toString());
        		}
        	}
        	public void onStateChange(StateChangeEvent<State> event){
        		if(atozTree.getSelection() != null && atozTree.getSelection().leafType.equals("analysis")) {
        			cancelTestButton.enable(event.getState() == State.UPDATE);
        		}else
        			cancelTestButton.enable(false);
        	}
        });
        historyUtility = new SampleHistoryUtility(window){
            public void historyCurrentResult() {
              ActionEvent.fire(trackScreen, ResultTab.Action.RESULT_HISTORY, null);
            }  
        };

        historySample = (MenuItem)def.getWidget("historySample");
        addScreenHandler(historySample, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySample();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySample.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleEnvironmental = (MenuItem)def.getWidget("historySampleEnvironmental");
        addScreenHandler(historySampleEnvironmental, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleEnvironmental();
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	if(manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
            		historySampleEnvironmental.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            	else
            		historySampleEnvironmental.enable(false);
            }
        });
        
        historySamplePrivateWell = (MenuItem)def.getWidget("historySamplePrivateWell");
        addScreenHandler(historySamplePrivateWell, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySamplePrivateWell();
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	if(manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG))
            		historySamplePrivateWell.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            	else
            		historySamplePrivateWell.enable(false);
            }
        });
        
        historySampleSDWIS = (MenuItem)def.getWidget("historySampleSDWIS");
        addScreenHandler(historySampleSDWIS, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleSDWIS();
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	if(manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG))
            		historySampleSDWIS.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            	else
            		historySampleSDWIS.enable(false);
            }
        });
        
        historySampleProject = (MenuItem)def.getWidget("historySampleProject");
        addScreenHandler(historySampleProject, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleProject();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleProject.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleOrganization = (MenuItem)def.getWidget("historySampleOrganization");
        addScreenHandler(historySampleOrganization, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleOrganization();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleOrganization.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleItem();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAnalysis();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyCurrentResult();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyStorage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historySampleQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAnalysisQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        
        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.historyAuxData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });        
        
        nav = new ScreenNavigator(def) {
        	
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<SampleManager>>() {
                    public void onSuccess(ArrayList<SampleManager> result) {
                    	manager = null;
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: envsample call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
            	if(manager == null) {
            		manager = (SampleManager)entry;
            		historyUtility.setManager(manager);
            		treeUtil.setManager(manager);
            		setState(State.DISPLAY);
            		resetScreen();
            		return true;
            	}
                 if(fetchById((entry==null)?null:((SampleManager)entry).getSample().getId())){
                	 entry = manager;
                	 treeUtil.setManager(manager);
                	 return true;
                 }
                 return false;
                 
            }

			public ArrayList<TreeDataItem> getModel() {
				ArrayList<SampleManager> result;
				ArrayList<TreeDataItem> model;
				
				model = new ArrayList<TreeDataItem>();
				result = nav.getQueryResult();
				if(result == null)
					return model;
				try {
					for(SampleManager vo : result) {
					   	TreeDataItem sample = new TreeDataItem();
				    	sample.checkForChildren(true);
				    	sample.leafType = "sample";
				    	sample.data = vo.getBundle();
				    	sample.cells.add(new TableDataCell(vo.getSample().getAccessionNumber()));
				    	try {
				    		sample.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(vo.getSample().getStatusId()).getEntry()));
				    	}catch(Exception e){
				    		sample.cells.add(new TableDataCell(vo.getSample().getStatusId()));
				    	}
						model.add(sample);	
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				return model;
			}
			
        };
        
        addStateChangeHandler(new StateChangeHandler<State>() {
        	public void onStateChange(StateChangeEvent<State> event) {
        		nav.enable(state == State.DEFAULT || state == State.DISPLAY);
        		atozTree.enable(state == State.DEFAULT || state == State.DISPLAY || state == State.UPDATE );
        		atozTree.enableDrag(state == State.UPDATE);
        		atozTree.enableDrop(state == State.UPDATE);
        	}
        });
        
        atozTree = (TreeWidget)def.getWidget("atozTable");
        
        atozTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {

			public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
				if(event.getItem().leafType.equals("sample") && !event.getItem().isLoaded()) {
					try {
						loadSampleItem(manager, event.getItem());
						event.getItem().checkForChildren(false);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
        	
        });
        
        atozTree.addLeafClosedHandler(new LeafClosedHandler() {
        	public void onLeafClosed(LeafClosedEvent event) {
        		if(event.getItem().leafType.equals("sample")){
        			event.getItem().checkForChildren(true);
        			event.getItem().getItems().clear();
        		}
        	}
        });
        
        atozTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
        	public void onBeforeCellEdited(BeforeCellEditedEvent event) {
        		event.cancel();
        	}
        });
        
        atozTree.enableDrag(true);
        atozTree.enableDrop(true);
        
        atozTree.addBeforeDragStartHandler(new BeforeDragStartHandler<TreeRow>() {
        	public void onBeforeDragStart(BeforeDragStartEvent<TreeRow> event) {
        		if(!event.getDragObject().item.leafType.equals("analysis"))
        			event.cancel();
        	};
        });
        
        atozTree.addBeforeDropHandler(new BeforeDropHandler<TreeRow>() {
			public void onBeforeDrop(BeforeDropEvent<TreeRow> event) {
				TreeDataItem dragItem = event.getDragObject().dragItem;
				TreeDataItem dropTarget = ((TreeRow)event.getDropTarget()).item;
				SampleDataBundle dragKey = (SampleDataBundle)dragItem.data;
				SampleDataBundle dropKey = (SampleDataBundle)dropTarget.data;
				try {
					manager.getSampleItems().moveAnalysis(dragKey,dropKey);
					//atozTree.deleteRow(dragItem);
					//atozTree.addChildItem(dropTarget, dragItem, dropTarget.getItems().size()-2);
					TreeDataItem sample = atozTree.getData().get(nav.getSelection());
					atozTree.unselect(-1);
					checkNode(sample);
					//atozTree.refreshRow(sample);
				}catch(Exception e) {
					e.printStackTrace();
					Window.alert("Move failed: "+e.getMessage());
				}
				event.cancel();
				
			}
        });
        
        atozTree.addDropHandler(new DropHandler<TreeRow>() {

			public void onDrop(DropEvent<TreeRow> event) {

			}
        	
        });
        
        atozTree.addDropEnterHandler(new DropEnterHandler<TreeRow>() {
			public void onDropEnter(DropEnterEvent<TreeRow> event) {
				TreeDataItem dropTarget = ((TreeRow)event.getDropTarget()).item;
				TreeDataItem dragItem = ((TreeRow)event.getDragObject()).dragItem;
				
				if(dropTarget.leafType.equals("item") && dragItem.parent != dropTarget &&
				   (((SampleDataBundle)dropTarget.data).getSampleManager().getSample().getId().equals(((SampleDataBundle)dragItem.data).getSampleManager().getSample().getId()))){
				
				}else
					event.cancel();
			}
        });
        
        atozTree.addTarget(atozTree);
        
        atozTree.enableDrag(false);
        atozTree.enableDrop(false);
        
        atozTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>() {
			public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
				if(state == State.UPDATE) {
					SampleDataBundle key = (SampleDataBundle)event.getItem().data;
    				if(!(key.getSampleManager().getSample().getId().equals(manager.getSample().getId())))
    					event.cancel();
				}
			}
        	
        });
        
        atozTree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
        	public void onSelection(SelectionEvent<TreeDataItem> event) {
        		if(event.getSelectedItem().parent == null)
        			return;
        		if(event.getSelectedItem().leafType.equals("item")){
        			try {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
        					fetchById(key.getSampleManager().getSample().getId());
        					sampleItemTab.setData(manager.getSampleItems().getBundleAt(key.getSampleItemIndex()));
        				}else
        					sampleItemTab.setData(key);
        				sampleItemTab.draw();
        				showTabs(Tabs.SAMPLE_ITEM);
        				addTestButton.enable(state == State.UPDATE);
        				cancelTestButton.enable(false);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("analysis")){
        			try {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
        					fetchById(key.getSampleManager().getSample().getId());
        					analysisTab.setData(manager.getSampleItems().getBundleAt(key.getSampleItemIndex()));
        				}else
        					analysisTab.setData(key);
        				analysisTab.draw();
        				showTabs(Tabs.ANALYSIS);
        				addTestButton.enable(false);
        				cancelTestButton.enable(state == State.UPDATE);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("qaevent")) {
    				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
    				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {     					
    					fetchById(key.getSampleManager().getSample().getId());
    					try {
    						if(event.getSelectedItem().parent.leafType.equals("item"))
    							key = manager.getSampleItems().getBundleAt(key.getSampleItemIndex());
    						else
    							key = manager.getSampleItems().getAnalysisAt(key.getSampleItemIndex()).getBundleAt(key.getAnalysisIndex());
    					}catch(Exception e) {
    						
    					}
    				}
        			qaEventsTab.setData(key);
        			qaEventsTab.setManager(manager);
        			qaEventsTab.draw();
        			showTabs(Tabs.QA_EVENTS);
        			addTestButton.enable(false);
        			cancelTestButton.enable(false);
        		}
        		if(event.getSelectedItem().leafType.equals("storage")) {
        			try {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
        					fetchById(key.getSampleManager().getSample().getId());
        					storageTab.setData(manager.getSampleItems().getBundleAt(key.getSampleItemIndex()));
        				}else
        					storageTab.setData(key);
        				storageTab.draw();
        				showTabs(Tabs.STORAGE);
        				addTestButton.enable(false);
        				cancelTestButton.enable(false);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("note")) {
        			
        			if(event.getSelectedItem().parent.leafType.equals("sample")) {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				try {
        					if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
        						fetchById(key.getSampleManager().getSample().getId());
        					}
        					sampleNotesTab.setManager(manager);
        					sampleNotesTab.draw();
        					showTabs(Tabs.SAMPLE_NOTES);
        					addTestButton.enable(false);
        					cancelTestButton.enable(false);
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}else{
        				try {
        					SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
            				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())){
            					fetchById(key.getSampleManager().getSample().getId());
            					analysisNotesTab.setData(manager.getSampleItems().getBundleAt(key.getSampleItemIndex()));
            				}else
            					analysisNotesTab.setData(key);
        					analysisNotesTab.draw();
        					showTabs(Tabs.ANALYSIS_NOTES);
        					addTestButton.enable(false);
        					cancelTestButton.enable(false);
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("auxdata")){
        			try {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())){
    						fetchById(key.getSampleManager().getSample().getId());
        				}
        				auxDataTab.setManager(manager);
        				auxDataTab.draw();
        				showTabs(Tabs.AUX_DATA);
    					addTestButton.enable(false);
    					cancelTestButton.enable(false);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("result")) {
        			try {
        				SampleDataBundle key = (SampleDataBundle)event.getSelectedItem().data;
        				if(!key.getSampleManager().getSample().getId().equals(manager.getSample().getId())) {
        					fetchById(key.getSampleManager().getSample().getId());
        					testResultsTab.setData(manager.getSampleItems().getBundleAt(key.getSampleItemIndex()));
        				}else
        					testResultsTab.setData(key);
        				testResultsTab.draw();
        				showTabs(Tabs.TEST_RESULT);
    					addTestButton.enable(false);
    					cancelTestButton.enable(false);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        	}
        });
        
        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                    manager.getSample().setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY).contains(event.getState()))
                    accessionNumber.setFocus(true);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget("orderNumber");
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(Util.toString(manager.getSample().getOrderId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setOrderId(event.getValue());
                /*
                if (envOrderImport == null)
                    envOrderImport = new SampleEnvironmentalImportOrder();

                try {
                    envOrderImport.importOrderInfo(event.getValue(), manager);
                    DataChangeEvent.fire(envScreen);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                */
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
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
        try{
            environmentalTab = new EnvironmentalTab(window);
            AbsolutePanel envTabPanel = (AbsolutePanel)def.getWidget("envDomainPanel");
            envTabPanel.add(environmentalTab);
        
        }catch(Exception e){
            Window.alert("env tab initialize: "+e.getMessage());
        }
        
        addScreenHandler(environmentalTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		if(manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
        			environmentalTab.setData(manager);
        		else {
        			SampleManager newManager = SampleManager.getInstance();
        			newManager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        			environmentalTab.setData(newManager);
        		}
        		
        		if(tab == Tabs.ENVIRONMENT)
        			environmentalTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		environmentalTab.setState(event.getState());
        	}
        });
        
        try{
            wellTab = new PrivateWellTab(window);
            AbsolutePanel wellTabPanel = (AbsolutePanel)def.getWidget("privateWellDomainPanel");
            wellTabPanel.add(wellTab);
        
        }catch(Exception e){
            Window.alert("well tab initialize: "+e.getMessage());
        }
        
        addScreenHandler(wellTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		if(manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG))
        			wellTab.setData(manager);
        		else {
           			SampleManager newManager = SampleManager.getInstance();
        			newManager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
        			wellTab.setData(newManager);
        		}
        		
        		if(tab == Tabs.PRIVATE_WELL)
        			wellTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		wellTab.setState(event.getState());
        	}
        });
        
        try{
            sdwisTab = new SDWISTab(window);
            AbsolutePanel sdwisTabPanel = (AbsolutePanel)def.getWidget("sdwisDomainPanel");
            sdwisTabPanel.add(sdwisTab);
        
        }catch(Exception e){
            Window.alert("sdwis tab initialize: "+e.getMessage());
        }
        
        
        addScreenHandler(sdwisTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		if(manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG))
        			sdwisTab.setData(manager);
        		else {
           			SampleManager newManager = SampleManager.getInstance();
        			newManager.getSample().setDomain(SampleManager.SDWIS_DOMAIN_FLAG);
        			sdwisTab.setData(newManager);
        		}
        		
        		if(tab == Tabs.SDWIS)
        			sdwisTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		sdwisTab.setState(event.getState());
        	}
        });
        
        sampleItemTab = new SampleItemTab(def, window);

        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTab.setData(null);

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
                analysisTab.setData(null);
                if (tab == Tabs.ANALYSIS){
                    analysisTab.draw();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });
        
        treeUtil = new SampleTreeUtility(window,atozTree,this){
            public TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow, SampleDataBundle bundle) {
                TreeDataItem row;
                
                row = new TreeDataItem(2);
                row.leafType = "analysis";
                row.data = bundle;
                TreeDataItem results = new TreeDataItem();
                results.leafType = "result";
                results.data = bundle;
                results.cells.add(new TableDataCell("Results"));
                row.addItem(results);
                TreeDataItem qaevent = new TreeDataItem();
                qaevent.leafType = "qaevent";
                qaevent.data = bundle;
                qaevent.cells.add(new TableDataCell("QA Events"));
                row.addItem(qaevent);
                TreeDataItem note = new TreeDataItem();
                note.leafType = "note";
                note.data = bundle;
                note.cells.add(new TableDataCell("Notes"));
                row.addItem(note);
                atozTree.addChildItem(parentRow, row, parentRow.getItems().size() - 2);
                
                return row;
            }
        };
        
        treeUtil.addActionHandler(new ActionHandler(){
            public void onAction(ActionEvent event) {
                ActionEvent.fire(null, event.getAction(), event.getData());
            }
        });
        
        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent event) {
                TreeDataItem selected;
                if (event.getAction() == SampleItemTab.Action.CHANGED) {
                    selected = itemsTree.getSelection();

                    // make sure it is a sample item row
                    if ("analysis".equals(selected.leafType))
                        selected = selected.parent;

                    treeUtil.updateSampleItemRow(selected);
                    itemsTree.refreshRow(selected);

                } else if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), false);
                } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), true);
                } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                    selected = itemsTree.getSelection();
                    treeUtil.updateAnalysisRow(selected);
                    itemsTree.refreshRow(selected);
                }
            }
        });
        
        testResultsTab = new ResultTab(def, window);

        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    testResultsTab.setData(null);

                    if (tab == Tabs.TEST_RESULT)
                        testResultsTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });
        
        analysisNotesTab = new AnalysisNotesTab(def, window, "anExNotesPanel", "anExNoteButton", "anIntNotesPanel", "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisNotesTab.setData(null);
                if (tab == Tabs.ANALYSIS_NOTES){
                    analysisNotesTab.draw();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisNotesTab.setState(event.getState());
            }
        });
                
        sampleNotesTab = new SampleNotesTab(def, window, "sampleExtNotesPanel", "sampleExtNoteButton", "sampleIntNotesPanel", "sampleIntNoteButton");
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
                    storageTab.setData(null);

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
                    qaEventsTab.setData(null);
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
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });        
    }
    
    private void loadSampleItem(SampleManager sm, TreeDataItem sample) throws Exception{
    	if(sm.getSampleItems() != null){
    		for(int i= 0; i < sm.getSampleItems().count(); i++ ) {
    			TreeDataItem item = new TreeDataItem();
    			item.leafType = "item";
    			item.data = sm.getSampleItems().getBundleAt(i);
    			item.cells.add(new TableDataCell(sm.getSampleItems().getSampleItemAt(i).getItemSequence()+"-"+sm.getSampleItems().getSampleItemAt(i).getContainer()));
    			item.cells.add(new TableDataCell(sm.getSampleItems().getSampleItemAt(i).getTypeOfSample()));
    			if(sm.getSampleItems().getAnalysisAt(i) != null){
    				for(int j = 0; j < sm.getSampleItems().getAnalysisAt(i).count(); j++) {
    					TreeDataItem analysis = new TreeDataItem();
    					analysis.leafType = "analysis";
    					analysis.data = sm.getSampleItems().getAnalysisAt(i).getBundleAt(j);
    					analysis.cells.add(new TableDataCell(sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getTestName()+" : "+sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getMethodName()));
    					analysis.cells.add(new TableDataCell(sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getStatusId()));
    					TreeDataItem results = new TreeDataItem();
    					results.leafType = "result";
    					results.data = sm.getSampleItems().getAnalysisAt(i).getBundleAt(j);
    					results.cells.add(new TableDataCell("Results"));
    					analysis.addItem(results);
    					TreeDataItem qaevent = new TreeDataItem();
    					qaevent.leafType = "qaevent";
    					qaevent.data = sm.getSampleItems().getAnalysisAt(i).getBundleAt(j);
    					qaevent.cells.add(new TableDataCell("QA Events"));
    					analysis.addItem(qaevent);
    					TreeDataItem note = new TreeDataItem();
    					note.leafType = "note";
    					note.data = sm.getSampleItems().getAnalysisAt(i).getBundleAt(j);
    					note.cells.add(new TableDataCell("Notes"));
    					analysis.addItem(note);																			
    					item.addItem(analysis);
    				}
    			}
    			TreeDataItem storage = new TreeDataItem();
    			storage.leafType = "storage";
    			storage.data = sm.getSampleItems().getBundleAt(i);
    			storage.cells.add(new TableDataCell("Storage"));
    			item.addItem(storage);
    			TreeDataItem qaevent = new TreeDataItem();
    			qaevent.leafType = "qaevent";
    			qaevent.data = sm.getSampleItems().getBundleAt(i);
    			qaevent.cells.add(new TableDataCell("QA Events"));
    			item.addItem(qaevent);	
    			sample.addItem(item);
    		}
    		TreeDataItem note = new TreeDataItem();
    		note.leafType = "note";
    		note.data = sm.getBundle();
    		note.cells.add(new TableDataCell("Notes"));
    		sample.addItem(note);
    		TreeDataItem aux = new TreeDataItem();
    		aux.leafType = "auxdata";
    		aux.data = sm.getBundle();
    		aux.cells.add(new TableDataCell("Aux Data"));
    		sample.addItem(aux);
    	}
    }
    
    private void checkNode(TreeDataItem item) {
    	if(!item.open){
    		item.getItems().clear();
    		item.checkForChildren(true);
    		return;
    	}
    	ArrayList<SampleDataBundle> openItems = new ArrayList<SampleDataBundle>();
    	for(TreeDataItem child : item.getItems()) {
    		checkChildOpen(child,openItems);
    	}
    	item.getItems().clear();    	
    	try {
    		loadSampleItem(manager,item);
    		while(openItems.size() > 0) {
    			SampleDataBundle openKey = openItems.remove(0);
    			searchForKey(openKey,item);
    		}
    		atozTree.refreshRow(item);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private boolean searchForKey(SampleDataBundle bundle, TreeDataItem item) {
    	if(bundle.getType() == ((SampleDataBundle)item.data).getType() &&
    	   bundle.getSampleItemIndex() == ((SampleDataBundle)item.data).getSampleItemIndex() &&
    	   bundle.getAnalysisIndex() == ((SampleDataBundle)item.data).getAnalysisIndex()){
    		if(!item.open)
    			item.toggle();
    		return true;
    	}
    	for(TreeDataItem child : item.getItems()) {
    		if(searchForKey(bundle,child))
    			break;
    	}
    	return false;
    }
    
    private void checkChildOpen(TreeDataItem item, ArrayList<SampleDataBundle> openItems) {
    	if(item.open) {
    		openItems.add((SampleDataBundle)item.data);
    		for(TreeDataItem child : item.getItems()) {
    			checkChildOpen(child,openItems);
    		}
    	}
    }
    
    
    
    private boolean canEdit(){
        return (!sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    protected void query() {
    	try {
    		setState(Screen.State.QUERY);
    		DataChangeEvent.fire(this);
            //we need to make sure the tabs are cleared
    		environmentalTab.draw();
    		wellTab.draw();
            sampleItemTab.draw();
            sdwisTab.draw();
            analysisTab.draw();
            testResultsTab.draw();
            analysisNotesTab.draw();
            sampleNotesTab.draw();
            storageTab.draw();
            qaEventsTab.draw();
            auxDataTab.draw();
    		window.setDone(consts.get("enterFieldsToQuery"));
    	}catch(Exception e) {
    		e.printStackTrace();
    		Window.alert("Set up of Query failed");
    	}
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }
    
    
    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            historyUtility.setManager(manager);
            treeUtil.setManager(manager);
            nav.getQueryResult().set(nav.getSelection(), manager);
            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            window.clearStatus();
            if(atozTree.getSelection() != null)
            	SelectionEvent.fire(atozTree,atozTree.getSelection());
            
        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void commit() {
        clearErrors();
        if ( !validate()){
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;
            query = new Query();
            query.setFields(getQueryFields());
            
            // Added this Query Param to keep Quick Entry Samples out of query results
            QueryData qd = new QueryData();
            qd.query = "!Q";
            qd.key = SampleMeta.getDomain();
            qd.type = QueryData.Type.STRING;
            query.setFields(qd);
            
            nav.setQuery(query);
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.update();
                historyUtility.setManager(manager);
                nav.getQueryResult().set(nav.getSelection(), manager);
                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                
                if(!e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        }
    }
    
    public void abort() {
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        
        if (state == State.QUERY) {
        	String domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        }
        else if (state == State.UPDATE) {
            
            try {
                manager = manager.abortUpdate();
                historyUtility.setManager(manager);
                nav.getQueryResult().set(nav.getSelection(), manager);
                checkNode(atozTree.getData().get(nav.getSelection()));
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
                atozTree.unselect(-1);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        }else{
            window.clearStatus();
        }
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
        	String domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            historyUtility.setManager(manager);
            setState(State.DEFAULT);
        }else if(!id.equals(manager.getSample().getId())){
            window.setBusy(consts.get("fetching"));
            try {
               manager = SampleManager.fetchWithItemsAnalyses(id);
               historyUtility.setManager(manager);
            } catch (Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                window.clearStatus();
                return false;
            }
            setState(Screen.State.DISPLAY);
        }
   
        resetScreen();
   
        return true;
    }
    
    private void resetScreen() {
    	
        if(manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
        	showTabs(Tabs.ENVIRONMENT);

        if(manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG))
        	showTabs(Tabs.PRIVATE_WELL);
        
        if(manager.getSample().getDomain().equals(SampleManager.SDWIS_DOMAIN_FLAG))
        	showTabs(Tabs.SDWIS);
        
        DataChangeEvent.fire(this);
        window.clearStatus();
    }
    

    private String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }
    
    private void initializeDropdowns(){
        ArrayList<TableDataRow> model;

        //preload dictionary models and single entries, close the window if an error is found 
        try{
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
            sampleErrorStatusId = DictionaryCache.getIdFromSystemName("sample_error");
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            analysisInPrep = DictionaryCache.getIdFromSystemName("analysis_inprep");
            sampleReleasedId = DictionaryCache.getIdFromSystemName("sample_released");
        
        }catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        //sample status dropdown
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
        ((Dropdown<Integer>)atozTree.getColumns().get("analysis").get(1).colWidget).setModel(model);
        
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
    
    private void showTabs(Tabs... tabs) {
    	List<Tabs> tabList = Arrays.asList(tabs);

    	for(Tabs tab : Tabs.values()) {
   			sampleContent.setTabVisible(tab.ordinal(),tabList.contains(tab));
    	}
    	
    	if(tabs[0] == Tabs.BLANK){
    		sampleContent.selectTab(tabs[0].ordinal());
    		sampleContent.getTabBar().setStyleName("None");
    	}else{
    		sampleContent.selectTab(tabs[0].ordinal());
    		sampleContent.getTabBar().setStyleName("gwt-TabBar");
    	}
    }
    
    protected void similar() {
    	
    }
    
    protected void addTest() {
    	
    	SampleDataBundle key = (SampleDataBundle)atozTree.getSelection().data;
    	TreeDataItem sampleItem = atozTree.getSelection();
       
        try {

            int analysisIndex = manager.getSampleItems()
            .getAnalysisAt(key.getSampleItemIndex())
            .addAnalysis();
            
            
            TreeDataItem analysis = new TreeDataItem();
			analysis.leafType = "analysis";
			analysis.data = manager.getSampleItems().getAnalysisAt(key.getSampleItemIndex()).getBundleAt(analysisIndex);
			analysis.cells.add(new TableDataCell("<> : <>"));
			analysis.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(analysisLoggedInId).getEntry()));
			TreeDataItem results = new TreeDataItem();
			results.leafType = "result";
			results.data = manager.getSampleItems().getAnalysisAt(key.getSampleItemIndex()).getBundleAt(analysisIndex);
			results.cells.add(new TableDataCell("Results"));
			analysis.addItem(results);
			TreeDataItem qaevent = new TreeDataItem();
			qaevent.leafType = "qaevent";
			qaevent.data = manager.getSampleItems().getAnalysisAt(key.getSampleItemIndex()).getBundleAt(analysisIndex);
			qaevent.cells.add(new TableDataCell("QA Events"));
			analysis.addItem(qaevent);
			TreeDataItem note = new TreeDataItem();
			note.leafType = "note";
			note.data = manager.getSampleItems().getAnalysisAt(key.getSampleItemIndex()).getBundleAt(analysisIndex);
			note.cells.add(new TableDataCell("Notes"));
			analysis.addItem(note);																			
            
			atozTree.addChildItem(sampleItem, analysis, analysisIndex);			
			atozTree.select(analysis);
			atozTree.scrollToSelection();
			
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }
    	
    }
    
    protected void cancelTest() throws Exception {
    	int sindex = ((SampleDataBundle)atozTree.getSelection().data).getSampleItemIndex();
    	int aindex = ((SampleDataBundle)atozTree.getSelection().data).getAnalysisIndex();
    	manager.getSampleItems().getAnalysisAt(sindex).cancelAnalysisAt(aindex);
    	atozTree.setCell(atozTree.getSelectedRow(), 1, manager.getSampleItems().getAnalysisAt(sindex).getAnalysisAt(aindex).getStatusId());
    	analysisTab.setData(manager.getSampleItems().getAnalysisAt(sindex).getBundleAt(aindex));
    	analysisTab.draw();
    }

	public HandlerRegistration addActionHandler(ActionHandler handler) {
		return addHandler(handler,ActionEvent.getType());
	}
    
}
