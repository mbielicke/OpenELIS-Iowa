package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
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
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeIndexDropController;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.EnvironmentalTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SampleDataBundle;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.SampleTab;
import org.openelis.modules.sample.client.StorageTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.TabBar;

public class SampleTrackingScreen extends Screen {
    
    private Integer                        analysisLoggedInId, analysisCancelledId,
    analysisReleasedId, analysisInPrep, sampleLoggedInId, sampleErrorStatusId,
    sampleReleasedId, userId;
    
    public enum Tabs {BLANK,ENVIRONMENT,PRIVATE_WELL,
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
	
	private DeckPanel                  	   sampleContent;
	private TabBar                         sampleBar;
	
	private SampleTab                      sampleTab;
	private EnvironmentalTab               environmentalTab;
	private PrivateWellWaterSampleTab      wellTab;
	private SampleItemTab                  sampleItemTab;
	private AnalysisTab                    analysisTab;
	private QAEventsTab                    qaEventsTab;
	private StorageTab                     storageTab;
	private SampleNotesTab                 sampleNotesTab;
	private AnalysisNotesTab               analysisNotesTab;
	private AuxDataTab                     auxDataTab;
	private ResultTab      				   testResultsTab;
	private TreeWidget                     atozTree;
	
	private class TreeKey {

		public Integer sampleId;
		public Integer sampleItemId;
		public Integer analysisId;
		
		public TreeKey(Integer sampleId, Integer sampleItemId, Integer analysisId) {
			this.sampleId = sampleId;
			this.sampleItemId = sampleItemId;
			this.analysisId = analysisId;
		}
		
	}
	
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
    	sampleContent = (DeckPanel)def.getWidget("SampleContent");
    	sampleBar = (TabBar)def.getWidget("SampleBar");
        sampleBar.setStyleName("None");  	
    
        sampleBar.addSelectionHandler(new SelectionHandler<Integer>() {
    		public void onSelection(SelectionEvent<Integer> event) {
    			tab = tabIndexes.get(event.getSelectedItem());
    			sampleContent.showWidget(tab.ordinal());
    		}
    	});
        
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
        		manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
        		showTabs(Tabs.PRIVATE_WELL,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);
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
        		cancelTest();
        	}
        	public void onStateChange(StateChangeEvent<State> event){
        		if(atozTree.getSelection() != null && atozTree.getSelection().leafType.equals("analysis")) {
        			cancelTestButton.enable(event.getState() == State.UPDATE);
        		}else
        			cancelTestButton.enable(false);
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
            		setState(State.DISPLAY);
            		resetScreen();
            		return true;
            	}
                return fetchById((entry==null)?null:((SampleManager)entry).getSample().getId());
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
						model.add(getTreeItem(vo));	
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
				TreeDataItem dragItem = event.getDragObject().item;
				TreeDataItem dropTarget = (TreeDataItem)event.getDropTarget();
				TreeKey dragKey = (TreeKey)dragItem.key;
				TreeKey dropKey = (TreeKey)dropTarget.key;
				try {
					manager.getSampleItems().moveAnalysis(getSampleItem(dragKey.sampleItemId),getSampleItem(dropKey.sampleItemId), getAnalysis(dragKey.analysisId));
					atozTree.deleteRow(dragItem);
					atozTree.addChildItem(dropTarget, dragItem, dropTarget.getItems().size()-2);
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
				TreeDataItem dropTarget = (TreeDataItem)event.getDropTarget();
				TreeDataItem dragItem = event.getDragObject().item;
				
				if((!dropTarget.leafType.equals("item") || dragItem.parent == dropTarget) &&
				   (((TreeKey)dropTarget.key).sampleId.equals(((TreeKey)dragItem.key).sampleId)))
					event.cancel();
			}
        });
        
        atozTree.addTarget(atozTree);
        
        atozTree.enableDrag(false);
        atozTree.enableDrop(false);
        
        atozTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>() {
			public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
				if(state == State.UPDATE) {
					TreeKey key = (TreeKey)event.getItem().key;
    				if(!(key.sampleId.equals(manager.getSample().getId())))
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
        				TreeKey key = (TreeKey)event.getSelectedItem().key;
        				if(!(key.sampleId.equals(manager.getSample().getId())))
        					fetchById(key.sampleId);
        				sampleItemTab.setData(getSampleItemBundle(key.sampleItemId));
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
        				TreeKey key = (TreeKey)event.getSelectedItem().key;
        				if(!(key.sampleId.equals(manager.getSample().getId())))
        					fetchById(key.sampleId);
        				analysisTab.setData(getAnalysisBundle(key.analysisId));
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
    				TreeKey key = (TreeKey)event.getSelectedItem().key;
    				if(!(key.sampleId.equals(manager.getSample().getId())))
    					fetchById(key.sampleId);
        			if(event.getSelectedItem().parent.leafType.equals("item")) {
        				try {
        					qaEventsTab.setData(getSampleItemBundle(key.sampleItemId));
        				}catch(Exception e){
        					
        				}
        			}else{
        				try {
        					qaEventsTab.setData(getAnalysisBundle(key.analysisId));
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}
        			qaEventsTab.setManager(manager);
        			qaEventsTab.draw();
        			showTabs(Tabs.QA_EVENTS);
        			addTestButton.enable(false);
        			cancelTestButton.enable(false);
        		}
        		if(event.getSelectedItem().leafType.equals("storage")) {
        			try {
        				TreeKey key = (TreeKey)event.getSelectedItem().key;
        				if(!(key.sampleId.equals(manager.getSample().getId())))
        					fetchById(key.sampleId);
        				storageTab.setData(getSampleItemBundle(key.sampleItemId));
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
        			TreeKey key = (TreeKey)event.getSelectedItem().key;
        			if(event.getSelectedItem().parent.leafType.equals("sample")) {
        				try {
        					if(!(key.sampleId.equals(manager.getSample().getId())))
        						fetchById(key.sampleId);
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
            				if(!(key.sampleId.equals(manager.getSample().getId())))
            					fetchById(key.sampleId);
        					analysisNotesTab.setData(getAnalysisBundle(key.analysisId));
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
        				TreeKey key = (TreeKey)event.getSelectedItem().key;
        				if(!(key.sampleId.equals(manager.getSample().getId())))
    						fetchById(key.sampleId);
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
        				TreeKey key = (TreeKey)event.getSelectedItem().key;
        				if(!(key.sampleId.equals(manager.getSample().getId())))
        					fetchById(key.sampleId);
        				testResultsTab.setData(getAnalysisBundle(key.analysisId));
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
        
        
        sampleTab = new SampleTab(def,window);
        
        addScreenHandler(sampleTab,new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		sampleTab.setData(manager);
        		sampleTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		sampleTab.setState(event.getState());
        	}
        });
        
        environmentalTab = new EnvironmentalTab(def,window);
        
        addScreenHandler(environmentalTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		environmentalTab.setData(manager);
        		
        		if(tab == Tabs.ENVIRONMENT)
        			environmentalTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		environmentalTab.setState(event.getState());
        	}
        });
        /*
        wellTab = new PrivateWellWaterSampleTab(def,window);
        
        addScreenHandler(wellTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		wellTab.setData(manager);
        		
        		if(tab == Tabs.PRIVATE_WELL)
        			wellTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		wellTab.setState(event.getState());
        	}
        });
        */
        sampleItemTab = new SampleItemTab(def, window);

        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTab.setData(new SampleDataBundle());

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
                analysisTab.setData(new SampleDataBundle());
                if (tab == Tabs.ANALYSIS){
                    analysisTab.draw();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });
        
        testResultsTab = new ResultTab(def, window);

        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                    testResultsTab.setData(new SampleDataBundle());

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
                analysisNotesTab.setData(new SampleDataBundle());
                if (tab == Tabs.ANALYSIS_NOTES)
                    analysisNotesTab.draw();
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
                    storageTab.setData(new SampleDataBundle());

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
                    qaEventsTab.setData(new SampleDataBundle());
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
    
    private TreeDataItem getTreeItem(SampleManager sm) throws Exception{
    	TreeDataItem sample = new TreeDataItem();
    	sample.leafType = "sample";
    	sample.key = new TreeKey(sm.getSample().getId(),null,null);
    	sample.cells.add(new TableDataCell(sm.getSample().getAccessionNumber()));
    	try {
    		sample.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(sm.getSample().getStatusId()).getEntry()));
    	}catch(Exception e){
    		sample.cells.add(new TableDataCell(sm.getSample().getStatusId()));
    	}
    	if(sm.getSampleItems() != null){
    		for(int i= 0; i < sm.getSampleItems().count(); i++ ) {
    			TreeDataItem item = new TreeDataItem();
    			item.leafType = "item";
    			item.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),null);
    			item.cells.add(new TableDataCell(sm.getSampleItems().getSampleItemAt(i).getItemSequence()+"-"+sm.getSampleItems().getSampleItemAt(i).getContainer()));
    			item.cells.add(new TableDataCell(sm.getSampleItems().getSampleItemAt(i).getTypeOfSample()));
    			if(sm.getSampleItems().getAnalysisAt(i) != null){
    				for(int j = 0; j < sm.getSampleItems().getAnalysisAt(i).count(); j++) {
    					TreeDataItem analysis = new TreeDataItem();
    					analysis.leafType = "analysis";
    					analysis.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getId());
    					analysis.cells.add(new TableDataCell(sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getTestName()+" : "+sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getMethodName()));
    					try {
    						analysis.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getStatusId()).getEntry()));
    					}catch(Exception e){
    						analysis.cells.add(new TableDataCell(sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getStatusId()));
    					}
    					TreeDataItem results = new TreeDataItem();
    					results.leafType = "result";
    					results.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getId());
    					results.cells.add(new TableDataCell("Results"));
    					analysis.addItem(results);
    					TreeDataItem qaevent = new TreeDataItem();
    					qaevent.leafType = "qaevent";
    					qaevent.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getId());
    					qaevent.cells.add(new TableDataCell("QA Events"));
    					analysis.addItem(qaevent);
    					TreeDataItem note = new TreeDataItem();
    					note.leafType = "note";
    					note.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),sm.getSampleItems().getAnalysisAt(i).getAnalysisAt(j).getId());
    					note.cells.add(new TableDataCell("Notes"));
    					analysis.addItem(note);																			
    					item.addItem(analysis);
    				}
    			}
    			TreeDataItem storage = new TreeDataItem();
    			storage.leafType = "storage";
    			storage.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),null);
    			storage.cells.add(new TableDataCell("Storage"));
    			item.addItem(storage);
    			TreeDataItem qaevent = new TreeDataItem();
    			qaevent.leafType = "qaevent";
    			qaevent.key = new TreeKey(sm.getSample().getId(),sm.getSampleItems().getSampleItemAt(i).getId(),null);
    			qaevent.cells.add(new TableDataCell("QA Events"));
    			item.addItem(qaevent);	
    			sample.addItem(item);
    		}
    		TreeDataItem note = new TreeDataItem();
    		note.leafType = "note";
    		note.key = new TreeKey(sm.getSample().getId(),null,null);
    		note.cells.add(new TableDataCell("Notes"));
    		sample.addItem(note);
    		TreeDataItem aux = new TreeDataItem();
    		aux.leafType = "auxdata";
    		aux.key = new TreeKey(sm.getSample().getId(),null,null);
    		aux.cells.add(new TableDataCell("Aux Data"));
    		sample.addItem(aux);
    	}
    	return sample;
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
            sampleItemTab.draw();
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
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.add();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                
                if(!e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.update();

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
                atozTree.collapse();
                atozTree.setRow(nav.getSelection(),getTreeItem(manager));
                atozTree.select(nav.getSelection());
                setState(State.DISPLAY);
                resetScreen();
            
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
            setState(State.DEFAULT);
        }else if(!id.equals(manager.getSample().getId())){
            window.setBusy(consts.get("fetching"));
            try {
               manager = SampleManager.fetchByIdWithItemsAnalyses(id);
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

        DataChangeEvent.fire(this);
        window.clearStatus();
    }
    
    
    private SampleDataBundle getSampleItemBundle(Integer id) throws Exception{
    	SampleItemManager siManager = manager.getSampleItems();
    	SampleItemViewDO si = getSampleItem(id);
    	if(si == null)
    		return new SampleDataBundle();
    	return new SampleDataBundle(siManager,si);
    }
    
    private SampleItemViewDO getSampleItem(Integer id) throws Exception{
    	SampleItemManager siManager = manager.getSampleItems();
    	int index = -1;
    	for(int i = 0; i < siManager.count(); i++) {
    		if(siManager.getSampleItemAt(i).getId().equals(id)){
    			index = i; 
    			break;
    		}
    	}
    	if(index < 0)
    		return null;
    	return siManager.getSampleItemAt(index);
    }
    
    private AnalysisViewDO getAnalysis(Integer id) throws Exception {
    	SampleItemManager siManager = manager.getSampleItems();
    	int sindex = -1;
    	int aindex = -1;
    	for(int i = 0; i < siManager.count(); i++) {
    		for(int j = 0; j < siManager.getAnalysisAt(i).count(); j++){
    			if(siManager.getAnalysisAt(i).getAnalysisAt(j).getId().equals(id)){
    				sindex = i;
    				aindex = j;
    				break;
    			}
    		}
    	}
    	if(sindex < -1)
    		return null;
    	return siManager.getAnalysisAt(sindex).getAnalysisAt(aindex);
    }
    
    private SampleDataBundle getAnalysisBundle(Integer id) throws Exception {
    	SampleItemManager siManager = manager.getSampleItems();
    	int sindex = -1;
    	int aindex = -1;
    	for(int i = 0; i < siManager.count(); i++) {
    		for(int j = 0; j < siManager.getAnalysisAt(i).count(); j++){
    			if(siManager.getAnalysisAt(i).getAnalysisAt(j).getId().equals(id)){
    				sindex = i;
    				aindex = j;
    				break;
    			}
    		}
    	}
    	if(sindex < -1)
    		return new SampleDataBundle();
    	return new SampleDataBundle(siManager,siManager.getSampleItemAt(sindex),siManager.getAnalysisAt(sindex),siManager.getAnalysisAt(sindex).getAnalysisAt(aindex),
    	                            siManager.getAnalysisAt(sindex).getTestAt(aindex));
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
        
    }
    
    private void drawTabs() {
        switch (tab) {
        	case ENVIRONMENT:
        		environmentalTab.draw();
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
    	while(sampleBar.getTabCount() > 0)
    		sampleBar.removeTab(0);
    	tabIndexes.clear();
    	
    	for(Tabs tab : tabs) {
    		switch(tab) {
    			case ENVIRONMENT:
    				sampleBar.addTab("Environment");
    				tabIndexes.add(tab);
    				break;
    			case PRIVATE_WELL:
    				sampleBar.addTab("Private Well");
    				tabIndexes.add(tab);
    				break;
    			case SAMPLE_ITEM:
    				sampleBar.addTab("Sample Item");
    				tabIndexes.add(tab);
    				break;
    			case ANALYSIS:
    				sampleBar.addTab("Analysis");
    				tabIndexes.add(tab);
    				break;
    			case TEST_RESULT:
    				sampleBar.addTab("Test Results");
    				tabIndexes.add(tab);
    				break;
    			case ANALYSIS_NOTES:
    				sampleBar.addTab("Analysis Notes");
    				tabIndexes.add(tab);
    				break;
    			case SAMPLE_NOTES:
    				sampleBar.addTab("Sample Notes");
    				tabIndexes.add(tab);
    				break;
    			case STORAGE:
    				sampleBar.addTab("Storage");
    				tabIndexes.add(tab);
    				break;
    			case QA_EVENTS:
    				sampleBar.addTab("QA Events");
    				tabIndexes.add(tab);
    				break;
    			case AUX_DATA:
    				sampleBar.addTab("Aux Data");
    				tabIndexes.add(tab);
    				break;
    		}
    	}
    	sampleBar.selectTab(0);
    	sampleBar.setStyleName("gwt-TabBar");
    }
    
    protected void similar() {
    	
    }
    
    protected void addTest() {
    	
    }
    
    protected void cancelTest() {
    	
    }
    
   
}
