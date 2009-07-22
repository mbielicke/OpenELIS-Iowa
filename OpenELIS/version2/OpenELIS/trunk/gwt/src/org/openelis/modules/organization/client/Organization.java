/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.organization.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.rewrite.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.AutoComplete;
import org.openelis.gwt.widget.rewrite.AutoCompleteCallInt;
import org.openelis.gwt.widget.rewrite.ButtonGroup;
import org.openelis.gwt.widget.rewrite.CheckBox;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.rewrite.KeyListManager;
import org.openelis.gwt.widget.rewrite.ResultsTable;
import org.openelis.gwt.widget.rewrite.AppButton.ButtonState;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.gwt.widget.table.rewrite.TableWidget;
import org.openelis.manager.OrganizationsManager;
import org.openelis.metamap.OrganizationMetaMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class Organization extends Screen implements AutoCompleteCallInt {

    private KeyListManager<org.openelis.gwt.common.rewrite.Query<Object>>    keyList; 
    public enum Tabs {CONTACTS,IDENTIFIERS,NOTES};
    protected Tabs tab = Tabs.CONTACTS;
    private ScreenService                service;
    private OrganizationsManager           manager;
    //private OrganizationRPC        	       rpc;
    private ContactsTab 		           contactsTab;
    private NotesTab 		  	           notesTab;
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    EnumSet<State> enabledStates = EnumSet.of(State.ADD,State.UPDATE,State.QUERY);

    public Organization() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.organization.server.Organization");
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.organization.server.Organization");
        manager = OrganizationsManager.getInstance();
        
        keyList = new KeyListManager<org.openelis.gwt.common.rewrite.Query<Object>>();
        OrgMeta = new OrganizationMetaMap();
        
        //Setup link between Screen and widget Handlers
        initialize();
        
        //Create the Handler for the Contacts tab passing in the ScreenDef
        contactsTab = new ContactsTab(def);
      
        //Create the Handler for the Notes tab passing in the ScreenDef;
        notesTab = new NotesTab(def);
        
        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(contactsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                contactsTab.setManager(manager);
                
                if(tab == Tabs.CONTACTS)
                    drawTabs();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(contactsTab, event.getState());
            }
        });
        
        addScreenHandler(notesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.setManager(manager);
                
                if(tab == Tabs.NOTES)
                    drawTabs();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                StateChangeEvent.fire(notesTab, event.getState());
            }
        });
        //Setup service used by screen
        
        //Initialize Screen
   		setState(State.DEFAULT);
		setCountriesModel();
		setStatesModel();
    }


    
    private void initialize() {
        
    	final TextBox name = (TextBox)def.getWidget(OrgMeta.getName());
    	addScreenHandler(name,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    		    name.setValue(manager.getOrganizationAddress().getName());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			manager.getOrganizationAddress().setName(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    		    name.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
    		    name.setQueryMode(event.getState() == State.QUERY);
    		    
    			if(event.getState() == State.ADD || event.getState() == State.UPDATE)
    				name.setFocus(true);
    		}
    	});
    	
    	final TextBox street = (TextBox)def.getWidget(OrgMeta.ADDRESS.getStreetAddress());
    	addScreenHandler(street, new ScreenEventHandler<String>() {
			public void onDataChange(DataChangeEvent event) {
				street.setValue(manager.getOrganizationAddress().getAddressDO().getStreetAddress());
			}
			public void onValueChange(ValueChangeEvent<String> event) {
			    manager.getOrganizationAddress().getAddressDO().setStreetAddress(event.getValue());
			}
			public void onStateChange(StateChangeEvent<State> event) {
			    street.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                street.setQueryMode(event.getState() == State.QUERY);
			}
    	});
    	
    	final TextBox id = (TextBox)def.getWidget(OrgMeta.getId());
    	addScreenHandler(id,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
		        id.setValue(getString(manager.getOrganizationAddress().getOrganizationId()));
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().setOrganizationId(Integer.valueOf(event.getValue()));
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    		    id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
                
    			if(event.getState() == State.QUERY)
    				id.setFocus(true);
    		}
    	});
    	
    	final TextBox multipleUnit = (TextBox)def.getWidget(OrgMeta.getAddress().getMultipleUnit());
    	addScreenHandler(multipleUnit,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			multipleUnit.setValue(manager.getOrganizationAddress().getAddressDO().getMultipleUnit());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().getAddressDO().setMultipleUnit(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    		    multipleUnit.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
    		    multipleUnit.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final TextBox city = (TextBox)def.getWidget(OrgMeta.getAddress().getCity());
    	addScreenHandler(city, new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			city.setValue(manager.getOrganizationAddress().getAddressDO().getCity());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().getAddressDO().setCity(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
                city.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                city.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final TextBox zipCode = (TextBox)def.getWidget(OrgMeta.getAddress().getZipCode());
    	addScreenHandler(zipCode,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			zipCode.setValue(manager.getOrganizationAddress().getAddressDO().getZipCode());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().getAddressDO().setZipCode(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event){
                zipCode.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                zipCode.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final Dropdown<String> state = (Dropdown<String>)def.getWidget(OrgMeta.getAddress().getState());
    	addScreenHandler(state,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			state.setSelection(manager.getOrganizationAddress().getAddressDO().getState());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().getAddressDO().setState(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
                state.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                state.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final Dropdown<String> country = (Dropdown<String>)def.getWidget(OrgMeta.getAddress().getCountry());
    	addScreenHandler(country,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			country.setSelection(manager.getOrganizationAddress().getAddressDO().getCountry());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().getAddressDO().setCountry(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
                country.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                country.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final CheckBox isActive = (CheckBox)def.getWidget(OrgMeta.getIsActive());
    	addScreenHandler(isActive,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			isActive.setValue(manager.getOrganizationAddress().getIsActive());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    		    manager.getOrganizationAddress().setIsActive(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event){
                isActive.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	final AutoComplete<Integer> parentOrg = (AutoComplete)def.getWidget(OrgMeta.getParentOrganization().getName());
    	addScreenHandler(parentOrg,new ScreenEventHandler<Integer>() {
    		public void onDataChange(DataChangeEvent event){
    		    OrganizationAddressDO orgDO = manager.getOrganizationAddress();
    		    if(orgDO.getParentOrganizationId() == null){
    	            parentOrg.clear();
    	        }else{
    	            ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
    	            model.add(new TableDataRow(orgDO.getParentOrganizationId(), orgDO.getParentOrganization()));
    	            parentOrg.setModel(model);
    	        }
    			parentOrg.setSelection(manager.getOrganizationAddress().getParentOrganizationId());
    		}
    		public void onValueChangeEvent(ValueChangeEvent<Integer> event) {
    		    manager.getOrganizationAddress().setParentOrganizationId(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    		    parentOrg.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
    		    parentOrg.setQueryMode(event.getState() == State.QUERY);
    		}
    	});
    	
    	//Create Default blank model for AutoComplete field Parent Org
    	ArrayList<TableDataRow>  model = new ArrayList<TableDataRow>();
    	TableDataRow row = new TableDataRow(4);
    	model.add(row);
    	parentOrg.setModel(model);
    	
    	//Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
    	parentOrg.setAutoCall(this);
    	
    	final AppButton queryButton = (AppButton)def.getWidget("query");
    	addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
    	   public void onClick(ClickEvent event) {
    	       query();
    	   }
    	   public void onStateChange(StateChangeEvent<State> event){
    	       if(event.getState() == State.DEFAULT || event.getState() == State.DISPLAY)
    	           queryButton.enable(true);
    	       else if(event.getState() == State.QUERY) 
    	           queryButton.changeState(ButtonState.LOCK_PRESSED);
    	       else
    	           queryButton.enable(false);
    	   }
    	});
    	
    	final AppButton addButton = (AppButton)def.getWidget("add");
    	addScreenHandler(addButton, new ScreenEventHandler<Object>() {
    	   public void onClick(ClickEvent event) {
    	       add();
    	   }
    	   public void onStateChange(StateChangeEvent<State> event) {
               if(event.getState() == State.DEFAULT || event.getState() == State.DISPLAY)
                   addButton.enable(true);
               else if(event.getState() == State.ADD) 
                   addButton.changeState(ButtonState.LOCK_PRESSED);
               else
                   addButton.enable(false);
    	   }
    	});
    	
    	final AppButton updateButton = (AppButton)def.getWidget("update");
    	addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
    	   public void onClick(ClickEvent event) {
    	       Window.alert("name was ["+manager.getOrganizationAddress().getName()+"]");
    	       update();
    	   }
    	   public void onStateChange(StateChangeEvent<State> event) {
    	       if(event.getState() == State.DISPLAY) 
    	           updateButton.enable(true);
    	       else if(event.getState() == State.UPDATE)
    	           updateButton.changeState(ButtonState.LOCK_PRESSED);
    	       else
    	           updateButton.enable(false);
    	         
    	   }
    	});
    	
    	final AppButton nextButton = (AppButton)def.getWidget("next");
    	addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
    	   public void onClick(ClickEvent event) {
    	       next();
    	   }
    	   public void onStateChange(StateChangeEvent<State> event) {
    	       if(event.getState() == State.DISPLAY) {
    	           nextButton.enable(true);
    	       }else{
    	           nextButton.enable(false);
    	       }
    	   }
    	});
    	
        final AppButton prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
           public void onClick(ClickEvent event) {
               previous();
               keyList.next();
           }
           public void onStateChange(StateChangeEvent<State> event) {
               if(event.getState() == State.DISPLAY) {
                   prevButton.enable(true);
               }else{
                   prevButton.enable(false);
               }
           }
        });
        
        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
           public void onClick(ClickEvent event) {
               commit();
           }
           public void onStateChange(StateChangeEvent<State> event) {
               if(event.getState() == State.ADD || event.getState() == State.QUERY || event.getState() == State.UPDATE) 
                   commitButton.enable(true);
               else
                   commitButton.enable(false);
           }
        });
        
        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }
            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.QUERY || event.getState() == State.UPDATE) 
                    abortButton.enable(true);
                else 
                    abortButton.enable(false);
                
            }
        });
    	
    	
    	//Set Up Key listeners
        final HasActionHandlers<Screen.Action> orgScreen = this;
    	keyList.addActionHandler(new ActionHandler<KeyListManager.Action>() {
    		public void onAction(ActionEvent<KeyListManager.Action> event) {
    	        if(event.getAction() == KeyListManager.Action.FETCH){
    	        	Integer selection = (Integer)event.getData();
    	        	fetch(selection);
                    ActionEvent.fire(orgScreen, Action.SELECTION_FETCHED, selection);
    	        }else if(event.getAction() == KeyListManager.Action.GETPAGE){
    	            final OrgQuery query = (OrgQuery)((Object[])event.getData())[0];
    	            final AsyncCallback callback = (AsyncCallback)((Object[])event.getData())[1];
    	            window.setBusy(consts.get("querying"));
    	            DeferredCommand.addCommand( new Command() {
    	                public void execute() {
    	                    service.callScreen("query", query, new SyncCallback<OrgQuery>() {
    	                        public void onSuccess(OrgQuery result) {
    	                            loadPage(result);
    	                            callback.onSuccess(result);
    	                        }
    	                        public void onFailure(Throwable caught) {
    	                            if(caught instanceof LastPageException){
    	                                window.setError(caught.getMessage());
    	                            }else
    	                                Window.alert(caught.getMessage());
    	                            callback.onFailure(caught);
    	                        }
    	                    });                        

    	                }
    	            });
    	    	}
    		}
    	});
    	
    	addActionHandler(keyList.screenActions);
    	final ResultsTable results = (ResultsTable)def.getWidget("azTable");
    	addActionHandler(results.screenActions);
    	keyList.addActionHandler(results.keyListActions);
    	results.addActionHandler(keyList.resultsActions);
    	
    	//Get AZ buttons and setup Screen listeners and call to for query
    	final ButtonGroup azButtons = (ButtonGroup)def.getWidget("atozButtons");
    	addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
    		public void onStateChange(StateChangeEvent<State> event) {
    		    if(event.getState() == State.DEFAULT || event.getState() == State.DISPLAY){
    		        azButtons.unlock();
    		    }else if(event.getState() == State.ADD || event.getState() == State.UPDATE || event.getState() == State.QUERY) {
    		        azButtons.lock();
    		    }
    		}
    		public void onClick(ClickEvent event) {
    		    String baction = ((AppButton)event.getSource()).action;
    		    getOrganizations(baction.substring(6, baction.length()));
    		}
    	});
    	
    	//Get TabPanel and set Tab Selection Handlers
    	final TabPanel tabs = (TabPanel)def.getWidget("orgTabPanel");
    	tabs.addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIndex = event.getSelectedItem().intValue();
				if(tabIndex == Tabs.CONTACTS.ordinal())
					tab = Tabs.CONTACTS;
				else if(tabIndex == Tabs.IDENTIFIERS.ordinal())
                    tab = Tabs.IDENTIFIERS;
				else if(tabIndex == Tabs.NOTES.ordinal())
					tab = Tabs.NOTES;
				
				window.setBusy("Loading...");
                drawTabs();
                window.clearStatus();
			}
    	});
    	
    	/*
    	tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
			    
			}
    	});*/
    }
    
    protected void query() {
        manager = OrganizationsManager.getInstance();
        DataChangeEvent.fire(this);
        setState(Screen.State.QUERY);
        window.setDone(consts.get("enterFieldsToQuery"));
    }
    
    protected void previous(){
        keyList.previous();
    }
    
    protected void next(){
        keyList.next();
    }

    protected void add() {
        manager = OrganizationsManager.getInstance();
        DataChangeEvent.fire(this);
    	setState(Screen.State.ADD);
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    protected void update() {
        window.setBusy("Locking Record for update...");
        
        try{
            manager = manager.fetchForUpdate();
            
            if(tab == Tabs.CONTACTS){
                manager.getContacts();
            }else if(tab == Tabs.IDENTIFIERS){
                 //empty for now
            }else if(tab == Tabs.NOTES){
                manager.getNotes();
            }
            
            DataChangeEvent.fire(this);
            window.clearStatus();
            setState(State.UPDATE);
        
        }catch(EntityLockedException e){
            window.clearStatus();
            Window.alert(e.getMessage());
        }catch(Exception e){
            Window.alert(e.getMessage());
        }
    }
    
    /*
    public void update() {
        window.setBusy("Locking Record for update...");
        service.callScreen("fetchForUpdate",rpc, new SyncCallback<OrganizationRPC>() {
            
            public void onFailure(Throwable caught) {   
                Window.alert(caught.getMessage());
            }

            public void onSuccess(OrganizationRPC result) {
                load(result);
                window.clearStatus();
            }
        });
        setState(State.UPDATE);
    }
    */

    public void commit() {
        if (state == State.UPDATE) {
            if (validate()) {
                window.setBusy(consts.get("updating"));
                commitUpdate();
            } else {
                window.setBusy(consts.get("correctErrors"));
            }
        }
        if (state == State.ADD) {
            if (validate()) {
                window.setBusy(consts.get("adding"));
                commitAdd();
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.QUERY) {
        	if(validate()){
        		ArrayList<QueryData> qFields = getQueryFields();
        		commitQuery(qFields);
        	}else {
        		window.setError(consts.get("correctErrors"));
        	}
        }
    }



    public void abort() {
        if (state == State.UPDATE) {
        	window.setBusy("Canceling changes ...");
        	
        	try{
            	manager = manager.abort();
            	
            	if(tab == Tabs.CONTACTS){
                    manager.getContacts();
                }else if(tab == Tabs.IDENTIFIERS){
                     //empty for now
                }else if(tab == Tabs.NOTES){
                    manager.getNotes();
                }
            	
            	DataChangeEvent.fire(this);
            	setState(State.DISPLAY);
                window.clearStatus();
        	}catch(Exception e){
        	    Window.alert(e.getMessage());
        	    window.clearStatus();
        	}
            
        }
        else if (state == State.ADD) {
            manager = OrganizationsManager.getInstance();
            DataChangeEvent.fire(this);
            setState(State.DEFAULT);
            window.setDone(consts.get("addAborted"));
        }
        else if (state == State.QUERY) {
            manager = OrganizationsManager.getInstance();
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        }
    }



    protected void fetch(Integer id) {
        window.setBusy("Fetching ...");
        try{
            if(tab == Tabs.CONTACTS){
                manager = OrganizationsManager.findByIdWithContacts(id);
            }else if(tab == Tabs.IDENTIFIERS){
                manager = OrganizationsManager.findByIdWithIdentifiers(id);
            }else if(tab == Tabs.NOTES){
                manager = OrganizationsManager.findByIdWithNotes(id);
            }
            
        }catch(Exception e){
            setState(Screen.State.DEFAULT);
            Window.alert(consts.get("fetchFailed") + e.getMessage());
            window.clearStatus();
            
            return;
        }
        
        DataChangeEvent.fire(this);
        
        setState(Screen.State.DISPLAY);
        window.clearStatus();
    }



    private void getOrganizations(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryData qField = new QueryData();
            qField.key = OrgMeta.getName();
            qField.query = query;
            qField.type = QueryData.Type.STRING;
            commitQuery(qField);
        }
    }
    
    //FIXME base class
    protected void setState(Screen.State state){
        this.state = state;
        StateChangeEvent.fire(this, state);
    }
    
    //FIXME
    public boolean validate() {
    	boolean valid = true;
    	for(Widget wid : def.getWidgets().values()){
    	    if(wid instanceof HasField){
    	        ((HasField)wid).checkValue();
    	        if(((HasField)wid).getErrors() != null) {
    	            valid = false;
    	        }
    	    }
    	}
    	return valid;
    }
    
    public ArrayList<QueryData> getQueryFields() {
    	ArrayList<QueryData> list = new ArrayList<QueryData>();
    	Set<String> keys = def.getWidgets().keySet();
       	for(String key : def.getWidgets().keySet()){
       	    if(def.getWidget(key) instanceof HasField){
       	        ((HasField)def.getWidget(key)).getQuery(list,key);
       	    }
    	}
       	return list;
    }
    
    public void commitAdd() {
    	window.setBusy("Committing ....");
    	try{
    	    manager = manager.add();
    	    
    	    DataChangeEvent.fire(this);
    	    setState(Screen.State.DISPLAY);
            window.clearStatus();
    	}catch(ValidationErrorsList e){
    	       showErrors(e);
    	       
    	}catch(Exception e){
    	    Window.alert("commitAdd(): "+e.getMessage());
            window.clearStatus();
    	}
    }

    public void commitUpdate() {
    	window.setBusy("Committing ....");
    	try{
            manager = manager.update();
            
            DataChangeEvent.fire(this);
            setState(Screen.State.DISPLAY);
            window.clearStatus();
        }catch(ValidationErrorsList e){
            showErrors(e);
            
        }catch(Exception e){
            Window.alert("commitUpdate(): "+e.getMessage());
            window.clearStatus();

        }            
    }
    
    public void commitQuery(QueryData qField){
    	ArrayList<QueryData> qList = new ArrayList<QueryData>();
    	qList.add(qField);
    	commitQuery(qList);
    }
    
    public void commitQuery(ArrayList<QueryData> qFields)  {
    	OrgQuery query = new OrgQuery();
    	query.fields = qFields;
    	window.setBusy("Querying...");
    	
    	service.callScreen("query",query,new AsyncCallback<OrgQuery>() {
    		public void onSuccess(OrgQuery query){
                loadQuery(query);
    		}
    		public void onFailure(Throwable caught){
                if(caught instanceof ValidationErrorsList)
                    showErrors((ValidationErrorsList)caught);
                else
                    Window.alert(caught.getMessage());
    		}
    	});
    }
    
    public void loadQuery(OrgQuery query) {
        manager = OrganizationsManager.getInstance();
        DataChangeEvent.fire(this);
        
        if(query.results == null || query.results.size() == 0) {
            window.setDone("No records found");
        }else
            window.setDone(consts.get("queryingComplete"));
        query.model = new ArrayList<TableDataRow>();
        for(IdNameDO entry : query.results) {
            query.model.add(new TableDataRow(entry.getId(),entry.getName()));
        }
        ActionEvent.fire(this,Action.NEW_MODEL, query);
    }
    
    private void loadPage(OrgQuery query) {
        window.setDone(consts.get("queryingComplete"));
        ActionEvent.fire(this, Action.NEW_PAGE, query);
    }

	//FIXME in base class
    protected void showErrors(ValidationErrorsList errors) {
    	for(RPCException ex : errors.getErrorList()){
    		if(ex instanceof TableFieldErrorException){
    			TableFieldErrorException tfe = (TableFieldErrorException)ex;
    			((TableWidget)def.getWidget(tfe.getTableKey())).setCellError(tfe.getRowIndex(), tfe.getFieldName(), consts.get(tfe.getMessage()));
    		}else{
    			FieldErrorException fe = (FieldErrorException)ex;
    			((HasField)def.getWidget(fe.getFieldName())).addError(consts.get(fe.getMessage()));
    		}
    	}
    	window.setError(consts.get("correctErrors"));
    }



    private void setCountriesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  (ArrayList<DictionaryDO>)DictionaryCache.getListByCategorySystemName("country")){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        ((Dropdown<String>)def.getWidget(OrgMeta.ADDRESS.getCountry())).setModel(model);
    }



    private void setStatesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  (ArrayList<DictionaryDO>)DictionaryCache.getListByCategorySystemName("state")){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        ((Dropdown<String>)def.getWidget(OrgMeta.ADDRESS.getState())).setModel(model);
    }



    public void callForMatches(final AutoComplete widget,	String text) {
		ParentOrgRPC prpc = new ParentOrgRPC();
		prpc.match = text;
		service.callScreen("getMatches",prpc,new AsyncCallback<ParentOrgRPC>() {
			public void onSuccess(ParentOrgRPC rpc) {
				ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
				for(OrganizationAutoDO autoDO : rpc.model) {
					TableDataRow row = new TableDataRow(4);
					row.key = autoDO.getId();
					row.cells.get(0).value = autoDO.getName();
					row.cells.get(1).value = autoDO.getAddress();
					row.cells.get(2).value = autoDO.getCity();
					row.cells.get(3).value = autoDO.getState();
					model.add(row);
				}
				widget.showAutoMatches(model);
			}
			
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
	}
    
    public String getString(Object obj){
        if(obj == null)
            return "";

        return obj.toString();
    }
    
    private void drawTabs() {
        if(tab == Tabs.CONTACTS){
            contactsTab.draw();
        }else if(tab == Tabs.IDENTIFIERS){
//            manager = OrganizationsManager.findByIdWithIdentifiers(id);
        }else if(tab == Tabs.NOTES){
            notesTab.draw();
        }
    }
}