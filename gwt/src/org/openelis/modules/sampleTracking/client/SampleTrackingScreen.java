package org.openelis.modules.sampleTracking.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemVO;
import org.openelis.domain.SampleTrackingVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.DataChangeEvent;
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
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.SampleDataBundle;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.StorageTab;
import org.openelis.modules.sample.client.TestResultsTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
    protected ArrayList<Tabs>           tabIndexes = new ArrayList<Tabs>();
    protected TextBox                      clientReference;
    protected TextBox<Integer>             accessionNumber, orderNumber;
    protected TextBox<Datetime>            collectedTime;
   
    protected Dropdown<Integer>            statusId;
    protected TreeWidget                   itemsTree;
    protected AppButton                    removeRow, 
    									   addItem, addAnalysis, queryButton, updateButton,
    									   nextButton, prevButton, commitButton, abortButton;
    protected CalendarLookUp               collectedDate, receivedDate;
	
	private SecurityModule 				   security;
	
	private ScreenNavigator 		       nav;
	private SampleManager                  manager;
	
	private DeckPanel                  	   sampleContent;
	private TabBar                         sampleBar;
	
	private EnvironmentTab                 environmentTab;
	private PrivateWellWaterSampleTab      wellTab;
	private SampleItemTab                  sampleItemTab;
	private AnalysisTab                    analysisTab;
	private QAEventsTab                    qaEventsTab;
	private StorageTab                     storageTab;
	private SampleNotesTab                 sampleNotesTab;
	private AnalysisNotesTab               analysisNotesTab;
	private AuxDataTab                     auxDataTab;
	private TestResultsTab				   testResultsTab;
	
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
        
    	accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(getString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                try{
                    manager.getSample().setAccessionNumber(event.getValue());
                    manager.validateAccessionNumber(manager.getSample());
                    
                }catch(ValidationErrorsList e) {
                    showErrors(e);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                .contains(event.getState()))
                    accessionNumber.setFocus(true);
            }
        });
        
        orderNumber = (TextBox<Integer>)def.getWidget("orderNumber");
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //orderNumber.setValue(getString(manager.getSample().getorgetAccessionNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //manager.getSample().setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(EnumSet.of(State.ADD, State.UPDATE)
                                   .contains(event.getState()));
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
                //addButton.enable(canEdit());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY)
                                   .contains(event.getState()));
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
                else
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
        
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<SampleTrackingVO>>() {
                    public void onSuccess(ArrayList<SampleTrackingVO> result) {
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
                return fetchById((entry==null)?null:((SampleTrackingVO)entry).getId());
            }

			public ArrayList<TreeDataItem> getModel() {
				ArrayList<SampleTrackingVO> result;
				ArrayList<TreeDataItem> model;
				
				model = new ArrayList<TreeDataItem>();
				result = nav.getQueryResult();
				if(result == null)
					return model;
				for(SampleTrackingVO vo : result) {
					TreeDataItem sample = new TreeDataItem();
					sample.leafType = "sample";
					sample.key = vo.getId();
					sample.cells.add(new TableDataCell(vo.getAccession()));
					try {
						sample.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(vo.getStatus()).getEntry()));
					}catch(Exception e){
						sample.cells.add(new TableDataCell(vo.getStatus()));
					}
					if(vo.getItems() != null){
						for(SampleItemVO si : vo.getItems()) {
							TreeDataItem item = new TreeDataItem();
							item.leafType = "item";
							item.key = si.getId();
							item.cells.add(new TableDataCell(si.getSequence()+"-"+si.getContainer()));
							item.cells.add(new TableDataCell(si.getType()));
							if(si.getAnalysis() != null){
								for(AnalysisVO avo : si.getAnalysis()) {
									TreeDataItem analysis = new TreeDataItem();
									analysis.leafType = "analysis";
									analysis.key = avo.getId();
									analysis.cells.add(new TableDataCell(avo.getTest()+" : "+avo.getMethod()));
									try {
										analysis.cells.add(new TableDataCell(DictionaryCache.getEntryFromId(avo.getStatus()).getEntry()));
									}catch(Exception e){
										analysis.cells.add(new TableDataCell(avo.getStatus()));
									}
									TreeDataItem results = new TreeDataItem();
									results.leafType = "result";
									results.key = avo.getId();
									results.cells.add(new TableDataCell("Results"));
									analysis.addItem(results);
									TreeDataItem qaevent = new TreeDataItem();
									qaevent.leafType = "qaevent";
									qaevent.key = avo.getId();
									qaevent.cells.add(new TableDataCell("QA Events"));
									analysis.addItem(qaevent);
									TreeDataItem note = new TreeDataItem();
									note.leafType = "note";
									note.key = avo.getId();
									note.cells.add(new TableDataCell("Notes"));
									analysis.addItem(note);																			
									item.addItem(analysis);
								}
							}
							TreeDataItem storage = new TreeDataItem();
							storage.leafType = "storage";
							storage.key = si.getId();
							storage.cells.add(new TableDataCell("Storage"));
							item.addItem(storage);
							TreeDataItem qaevent = new TreeDataItem();
							qaevent.leafType = "qaevent";
							qaevent.key = si.getId();
							qaevent.cells.add(new TableDataCell("QA Events"));
							item.addItem(qaevent);	
							sample.addItem(item);
						}
						TreeDataItem note = new TreeDataItem();
						note.leafType = "note";
						note.key = vo.getId();
						note.cells.add(new TableDataCell("Notes"));
						sample.addItem(note);
						TreeDataItem aux = new TreeDataItem();
						aux.leafType = "auxdata";
						aux.key = vo.getId();
						aux.cells.add(new TableDataCell("Aux Data"));
						sample.addItem(aux);
					}
					model.add(sample);	
				}
				return model;
			}
			
        };
        
        addStateChangeHandler(new StateChangeHandler<State>() {
        	public void onStateChange(StateChangeEvent<State> event) {
        		nav.enable(state == State.DEFAULT || state == State.DISPLAY);
        	}
        });
        
        final TreeWidget tree = (TreeWidget)def.getWidget("atozTable");
        tree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
        	public void onSelection(SelectionEvent<TreeDataItem> event) {
        		if(event.getSelectedItem().parent == null)
        			return;
        		if(event.getSelectedItem().leafType.equals("item")){
        			try {
        				sampleItemTab.setData(getSampleItemBundle((Integer)event.getSelectedItem().key));
        				sampleItemTab.draw();
        				showTabs(Tabs.SAMPLE_ITEM);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("analysis")){
        			try {
        				analysisTab.setData(getAnalysisBundle(((Integer)event.getSelectedItem().key)));
        				analysisTab.draw();
        				showTabs(Tabs.ANALYSIS);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("qaevent")) {
        			if(event.getSelectedItem().parent.leafType.equals("item")) {
        				try {
        					qaEventsTab.setData(getSampleItemBundle((((Integer)event.getSelectedItem().key))));
        				}catch(Exception e){
        					
        				}
        			}else{
        				try {
        					qaEventsTab.setData(getAnalysisBundle((((Integer)event.getSelectedItem().key))));
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}
        			qaEventsTab.setManager(manager);
        			qaEventsTab.draw();
        			showTabs(Tabs.QA_EVENTS);
        		}
        		if(event.getSelectedItem().leafType.equals("storage")) {
        			try {
        				storageTab.setData(getSampleItemBundle((Integer)event.getSelectedItem().key));
        				storageTab.draw();
        				showTabs(Tabs.STORAGE);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("note")) {
        			if(event.getSelectedItem().parent.leafType.equals("sample")) {
        				try {
        					sampleNotesTab.setManager(manager);
        					sampleNotesTab.draw();
        					showTabs(Tabs.SAMPLE_NOTES);
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}else{
        				try {
        					analysisNotesTab.setData(getAnalysisBundle((Integer)event.getSelectedItem().key));
        					analysisNotesTab.draw();
        					showTabs(Tabs.ANALYSIS_NOTES);
        				}catch(Exception e) {
        					Window.alert(e.getMessage());
        					e.printStackTrace();
        				}
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("auxdata")){
        			try {
        				auxDataTab.setManager(manager);
        				auxDataTab.draw();
        				showTabs(Tabs.AUX_DATA);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        		if(event.getSelectedItem().leafType.equals("result")) {
        			try {
        				testResultsTab.setData(getAnalysisBundle((Integer)event.getSelectedItem().key));
        				testResultsTab.draw();
        				showTabs(Tabs.TEST_RESULT);
        			}catch(Exception e) {
        				Window.alert(e.getMessage());
        				e.printStackTrace();
        			}
        		}
        	}
        });
        
        environmentTab = new EnvironmentTab(def,window);
        
        addScreenHandler(environmentTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event) {
        		environmentTab.setData(manager);
        		
        		if(tab == Tabs.ENVIRONMENT)
        			environmentTab.draw();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		environmentTab.setState(event.getState());
        	}
        });
        
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

                if (tab == Tabs.ANALYSIS)
                    analysisTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });
        
        testResultsTab = new TestResultsTab(def, window);

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
                sampleNotesTab.draw();
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
        
    }
    
    private boolean canEdit(){
        return (!sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    protected void query() {
    	try {
    		setState(Screen.State.QUERY);
    		DataChangeEvent.fire(this);
            //we need to make sure the tabs are cleared
    		environmentTab.draw();
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
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
            
        } else if (state == State.ADD) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
            
        }else if (state == State.UPDATE) {
            
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
    
    protected boolean fetchById(Integer id) {
    	
        if (id == null) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
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
        if(manager.getSample().getDomain().equals(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG))
        	showTabs(Tabs.ENVIRONMENT);

        DataChangeEvent.fire(this);
        window.clearStatus();
        
        return true;
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
    		return new SampleDataBundle();
    	return new SampleDataBundle(siManager,siManager.getSampleItemAt(index));
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
        		environmentTab.draw();
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
    			case BLANK:
    				sampleBar.addTab("");
    				tabIndexes.add(tab);
    				break;
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
   
}
