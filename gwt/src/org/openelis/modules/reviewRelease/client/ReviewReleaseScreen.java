package org.openelis.modules.reviewRelease.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReviewReleaseVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.ReportProgress;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
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
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
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
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.StorageTab;
import org.openelis.modules.sampleTracking.client.SampleTrackingScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ReviewReleaseScreen extends Screen implements HasActionHandlers {
    
    private Integer                        analysisLoggedInId, analysisCancelledId,
    analysisReleasedId, analysisInPrep, sampleLoggedInId, sampleErrorStatusId,
    sampleReleasedId, userId;
    
    public enum Tabs {BLANK,SAMPLE,ENVIRONMENT,PRIVATE_WELL,
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
    protected AppButton                    removeRow, releaseButton, reportButton, completeButton,
    									   addItem, addAnalysis, queryButton, updateButton,
    									   nextButton, prevButton, commitButton, abortButton;
    
    protected CalendarLookUp               collectedDate, receivedDate;
	
	private SecurityModule 				   security;
    protected AccessionNumberUtility       accessionNumUtil;
    protected SampleHistoryUtility         historyUtility;
	
	private ScreenNavigator 		       nav;
	private SampleManager                  manager;
	private SampleDataBundle               dataBundle;
	private TabPanel                  	   sampleContent;
	private SampleTab                      sampleTab;
	private EnvironmentalTab               environmentalTab;
	private PrivateWellTab			       wellTab;
	private SampleItemTab                  sampleItemTab;
	private AnalysisTab                    analysisTab;
	private QAEventsTab                    qaEventsTab;
	private StorageTab                     storageTab;
	private SampleNotesTab                 sampleNotesTab;
	private AnalysisNotesTab               analysisNotesTab;
	private AuxDataTab                     auxDataTab;
	private ResultTab      				   testResultsTab;
	private TableWidget                    atozTable;
	
    protected MenuItem                     historySample, historySampleEnvironmental,historySamplePrivateWell,
    historySampleProject, historySampleOrganization, historySampleItem,
    historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
    historyAnalysisQA, historyAuxData;
    
    private ReviewReleaseScreen reviewScreen = this; 
	
	
    public ReviewReleaseScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ReviewReleaseDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.reviewRelease.server.ReviewReleaseService");

        security = OpenELIS.security.getModule("sample");
        if (security == null)
            throw new SecurityException("screenPermException", "Review and Release Screen");

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
        sampleContent.getTabBar().setStyleName("None");
        sampleContent.addSelectionHandler(new SelectionHandler<Integer>() {
    		public void onSelection(SelectionEvent<Integer> event) {
    			tab = Tabs.values()[event.getSelectedItem()];
    			drawTabs();
    		}
    	});

        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
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
        
        releaseButton = (AppButton)def.getWidget("release");
        addScreenHandler(releaseButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		release();
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		releaseButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        
        completeButton = (AppButton)def.getWidget("complete");
        addScreenHandler(releaseButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event){
        		complete();
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		completeButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        
        /*
        reportButton = (AppButton)def.getWidget("report");
        addScreenHandler(reportButton, new ScreenEventHandler<Object>() {
        	public void onClick(ClickEvent event) {
        		doReport();
        	}
        	
        	public void onStateChange(StateChangeEvent<State> event) {
        		reportButton.enable(event.getState() == State.DISPLAY);
        	}
        });
        */
        historyUtility = new SampleHistoryUtility(window){
            public void historyCurrentResult() {
              ActionEvent.fire(reviewScreen, ResultTab.Action.RESULT_HISTORY, null);
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
                return fetchByVO((ReviewReleaseVO)entry);
            }

			public ArrayList<TableDataRow> getModel() {
				ArrayList<ReviewReleaseVO> result;
				ArrayList<TableDataRow> model;
				atozTable.setQueryMode(false);
				model = new ArrayList<TableDataRow>();
				result = nav.getQueryResult();
				if(result == null)
					return model;
				try {
					for(ReviewReleaseVO vo : result) {
						TableDataRow analysis = new TableDataRow();
						analysis.cells.add(new TableDataCell(vo.getAccession()));
						analysis.cells.add(new TableDataCell(vo.getTest()));
						analysis.cells.add(new TableDataCell(vo.getMethod()));
						analysis.cells.add(new TableDataCell(vo.getAnalysisStatus()));
						analysis.cells.add(new TableDataCell(vo.getSpecimenStatus()));
						analysis.data = vo;
						model.add(analysis);	
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				return model;
			}
			
        };
        
        atozTable = (TableWidget)def.getWidget("atozTable");
        
        
        addScreenHandler(atozTable,new ScreenEventHandler<Object>() {
        	public void onStateChange(StateChangeEvent<State> event) {
        		nav.enable(event.getState() == State.DEFAULT || event.getState() == State.DISPLAY);
        		atozTable.enable(EnumSet.of(State.DISPLAY,State.QUERY).contains(event.getState()));
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
        
        wellTab = new PrivateWellTab(def,window);
        
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
                    testResultsTab.setData(dataBundle);

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
                analysisNotesTab.setData(dataBundle);
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
        
        
    }
    
    private boolean canEdit(){
        return (!sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    protected void query() {
    	try {
    		setState(Screen.State.QUERY);
    		manager = SampleManager.getInstance();
    		manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
    		dataBundle = null;
    		DataChangeEvent.fire(this);
            //we need to make sure the tabs are cleared
    		sampleTab.draw();
    		environmentalTab.draw();
    		wellTab.draw();
            sampleItemTab.draw();
            analysisTab.draw();
            testResultsTab.draw();
            analysisNotesTab.draw();
            sampleNotesTab.draw();
            storageTab.draw();
            qaEventsTab.draw();
            auxDataTab.draw();
            showTabs(Tabs.BLANK);
            atozTable.setQueryMode(true);
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
            QueryData qd = new QueryData();
            qd.key = SampleMeta.getDomain();
            qd.query = "!Q";
            qd.type = QueryData.Type.STRING;
            query.setFields(qd);
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager.validate();
                manager.getSample().setStatusId(sampleLoggedInId);
                manager = manager.add();
                historyUtility.setManager(manager);
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
                historyUtility.setManager(manager);
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
            historyUtility.setManager(manager);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        }
        else if (state == State.UPDATE) {
            
            try {
                manager = manager.abortUpdate();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        }else{
            window.clearStatus();
        }
    }
    
    protected boolean fetchByVO(ReviewReleaseVO vo) {
        if (vo.getSampleId() == null) {
        	String domain = manager.getSample().getDomain();
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(domain);
            setState(State.DEFAULT);
            historyUtility.setManager(manager);
        }else if(manager == null || !manager.getSample().getId().equals(vo.getSampleId())){
            window.setBusy(consts.get("fetching"));
            try {
               manager = SampleManager.fetchWithItemsAnalyses(vo.getSampleId());
               historyUtility.setManager(manager);
               dataBundle = getAnalysisBundle(vo.getAnalysisId());
            } catch (Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                window.clearStatus();
                return false;
            }
            setState(Screen.State.DISPLAY);
        }else {
        	try {
        		dataBundle = getAnalysisBundle(vo.getAnalysisId());
        	}catch(Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                window.clearStatus();
                return false;
        	}
        	setState(State.DISPLAY);
        	DataChangeEvent.fire(this);
        	return true;
        }
   
        resetScreen();
   
        return true;
    }
    
    private void resetScreen() {
    	
        if(manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
        	showTabs(Tabs.SAMPLE,Tabs.ENVIRONMENT,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.ANALYSIS_NOTES,Tabs.SAMPLE_NOTES,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);
        
        if(manager.getSample().getDomain().equals(SampleManager.WELL_DOMAIN_FLAG))
        	showTabs(Tabs.SAMPLE,Tabs.PRIVATE_WELL,Tabs.SAMPLE_ITEM,Tabs.ANALYSIS,Tabs.TEST_RESULT,Tabs.ANALYSIS_NOTES,Tabs.SAMPLE_NOTES,Tabs.STORAGE,Tabs.QA_EVENTS,Tabs.AUX_DATA);

        DataChangeEvent.fire(this);
        window.clearStatus();
    }
    
    
    private SampleDataBundle getSampleItemBundle(Integer id) throws Exception{
    	SampleItemManager siManager = manager.getSampleItems();
    	int index = -1;
    	for(int i = 0; i < siManager.count(); i++) {
    		if(siManager.getSampleItemAt(i).getId().equals(id)){
    			index = i; 
    			break;
    		}
    	}
    	if(index < -1)
    		return null;
    	return siManager.getBundleAt(index);
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
    		return null;
    	return siManager.getAnalysisAt(sindex).getBundleAt(aindex);
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
        ((Dropdown<Integer>)atozTable.getColumnWidget(SampleMeta.getStatusId())).setModel(model);
        
        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)atozTable.getColumnWidget(SampleMeta.getAnalysisStatusId())).setModel(model);
        
    }
    
    private void drawTabs() {
        switch (tab) {
        	case ENVIRONMENT:
        		environmentalTab.draw();
        		break;
        	case PRIVATE_WELL:
        		wellTab.draw();
        		break;
        	case SAMPLE :
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
    
    private void doReport() {
		Request req = service.call("doFinalReport", new AsyncCallback<ReportProgress>() {
			public void onSuccess(final ReportProgress rp) {
				window.setDone("Done");
				window.setProgress(0);
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						Window.open("Report.html?id="+rp.name, "Report", "toolbar=no,location=no,menubar=no,status=no,titlebar=no");
					}
				});
			    
			}
			
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		getProgress(req);

	}
    
	public void getProgress(final Request req) {
		if(!req.isPending()){
			window.setDone("Done");
			window.setProgress(0);
			return;
		}
		service.call("getProgress", new AsyncCallback<ReportProgress>() {
			public void onSuccess(ReportProgress fp) {
				window.setBusy("Generating Report...");
				window.setProgress(fp.generated);
				Timer timer = new Timer() {
					public void run() {
						getProgress(req);
					}
				};
				timer.schedule(500);
			}	
			
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	public HandlerRegistration addActionHandler(ActionHandler handler) {
		return addHandler(handler,ActionEvent.getType());
	}
	
	private void release() {
		ArrayList<TableDataRow> rows = atozTable.getSelections();
		for(int i = 0; i < rows.size(); i++) {
			try {
				SampleDataBundle bundle = getAnalysisBundle(((ReviewReleaseVO)rows.get(i).data).getAnalysisId());
				bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex()).releaseAnalyssisAt(bundle.getAnalysisIndex());
			}catch(Exception e) {
				if(e instanceof ValidationErrorsList)
					showErrors((ValidationErrorsList)e);
				else
					Window.alert(e.getMessage());
			}
		}
	}
	
	private void complete() {
		ArrayList<TableDataRow> rows = atozTable.getSelections();
		for(int i = 0; i < rows.size(); i++) {
			try {
				SampleDataBundle bundle = getAnalysisBundle(((ReviewReleaseVO)rows.get(i).data).getAnalysisId());
				bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex()).completeAnalysisAt(bundle.getAnalysisIndex());
			}catch(Exception e) {
				if(e instanceof ValidationErrorsList)
					showErrors((ValidationErrorsList)e);
				else
					Window.alert(e.getMessage());
			}
		}
	}
	
}
