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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.QueryField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.rewrite.data.TableDataRow;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.CommandListenerCollection;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.DataChangeHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.HasDataChangeHandlers;
import org.openelis.gwt.event.HasStateChangeHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.services.ScreenServiceInt;
import org.openelis.gwt.services.ScreenServiceIntAsync;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.AutoComplete;
import org.openelis.gwt.widget.rewrite.AutoCompleteCallInt;
import org.openelis.gwt.widget.rewrite.ButtonPanel;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.rewrite.KeyListManager;
import org.openelis.gwt.widget.rewrite.ResultsTable;
import org.openelis.gwt.widget.table.rewrite.TableWidget;
import org.openelis.metamap.OrganizationMetaMap;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Organization extends Screen implements AutoCompleteCallInt {

    private TableWidget            contactsTable;
    private KeyListManager<org.openelis.gwt.common.rewrite.Query<Object>>    keyList; 
    private Dropdown<String>               states;
    private Dropdown<String>               countries;
    private ScreenServiceIntAsync  service;
    private OrganizationRPC        rpc;
    private ContactsTab 		   contactsTab;
    private NotesTab 		  	   notesTab;
   
    
    private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    public Organization() {
        super("OpenELISScreenServlet?service=org.openelis.modules.organization.server.Organization");         
        service = (ScreenServiceIntAsync)GWT.create(ScreenServiceInt.class);
        ServiceDefTarget target = (ServiceDefTarget)service;
        target.setServiceEntryPoint(GWT.getModuleBaseURL()+"OpenELISScreenServlet?service=org.openelis.modules.organization.server.Organization");
        DeferredCommand.addCommand(new Command() {

        	public void execute() {
        		rpc = new OrganizationRPC();
        		countries = (Dropdown)def.getWidget(OrgMeta.ADDRESS.getCountry());
        		states = (Dropdown)def.getWidget(OrgMeta.ADDRESS.getState());
        		contactsTable = (TableWidget)def.getWidget("contactsTable");
        		window.setBusy("Initialing screen ...");
        		service.callScreen("getDefaults",rpc,new SyncCallback<OrganizationRPC>() {
        			public void onSuccess(OrganizationRPC result) {
        				rpc = result;
        				setCountriesModel();
        				setStatesModel();
        				window.clearStatus();
        				setState(State.DEFAULT);
        			}
        			public void onFailure(Throwable caught) {

        			}
        		});
        	}
        });

    }

    public void afterDraw() {
    	keyList = new KeyListManager<org.openelis.gwt.common.rewrite.Query<Object>>();
    	OrgMeta = new OrganizationMetaMap();
    	setHandlers();
    	
    	//Create the Handler for the Contacts tab passing in the ScreenDef
        contactsTab = new ContactsTab(def);
      
        //Create the Handler for the Notes tab passing in the ScreenDef;
        notesTab = new NotesTab(def);
        
        // Set up tabs to recieve State Change events from the main Screen.
        addScreenHandler(contactsTab, new ScreenEventHandler<ContactsRPC>() {
        	public void onDataChange(DataChangeEvent event) {
        		contactsTab.setRPC(rpc.orgContacts);
        	}
        	public void onValueChange(ValueChangeEvent<ContactsRPC> event) {
        		rpc.orgContacts = event.getValue();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		StateChangeEvent.fire(contactsTab, event.getState());
        	}
        });
        addScreenHandler(contactsTab, new ScreenEventHandler<NotesRPC>() {
        	public void onDataChange(DataChangeEvent event) {
        		notesTab.setRPC(rpc.notes);
        	}
        	public void onValueChange(ValueChangeEvent<NotesRPC> event) {
        		rpc.notes = event.getValue();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		StateChangeEvent.fire(notesTab, event.getState());
        	}
        });
    }
    
    EnumSet<State> enabledStates = EnumSet.of(State.ADD,State.UPDATE,State.QUERY); 
    
    private void setHandlers() {
    	//Set up widgets on Screen with Handlers
    	
    	final TextBox name = (TextBox)def.getWidget(OrgMeta.getName());
    	addScreenHandler(name,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    		    name.setValue(rpc.orgAddressDO.getName());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.setName(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			name.setReadOnly(!enabledStates.contains(event.getState()));
    			if(event.getState() == State.ADD || event.getState() == State.UPDATE)
    				name.setFocus(true);
    		}
    	});
    	
    	final TextBox street = (TextBox)def.getWidget(OrgMeta.ADDRESS.getStreetAddress());
    	addScreenHandler(street, new ScreenEventHandler<String>() {
			public void onDataChange(DataChangeEvent event) {
				street.setValue(rpc.orgAddressDO.getAddressDO().getStreetAddress());
			}
			public void onValueChange(ValueChangeEvent<String> event) {
				rpc.orgAddressDO.getAddressDO().setStreetAddress(event.getValue());
			}
			public void onStateChange(StateChangeEvent<State> event) {
				street.setReadOnly(!enabledStates.contains(event.getState()));
			}
    	});
    	
    	final TextBox id = (TextBox)def.getWidget(OrgMeta.getId());
    	addScreenHandler(id,new ScreenEventHandler<Integer>() {
    		public void onDataChange(DataChangeEvent event) {
    		    if(rpc.orgAddressDO.getOrganizationId() != null)
    		        id.setValue(rpc.orgAddressDO.getOrganizationId().toString());
    		    else
    		        id.setValue("");
    		}
    		public void onValueChange(ValueChangeEvent<Integer> event) {
    			rpc.orgAddressDO.setOrganizationId(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			if(event.getState() == State.QUERY){
    				id.setReadOnly(false);
    				id.setFocus(true);
    			}else
    				id.setReadOnly(true);
    		}
    	});
    	
    	final TextBox multipleUnit = (TextBox)def.getWidget(OrgMeta.getAddress().getMultipleUnit());
    	addScreenHandler(multipleUnit,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			multipleUnit.setValue(rpc.orgAddressDO.getAddressDO().getMultipleUnit());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.getAddressDO().setMultipleUnit(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			multipleUnit.setReadOnly(!enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final TextBox city = (TextBox)def.getWidget(OrgMeta.getAddress().getCity());
    	addScreenHandler(city, new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			city.setValue(rpc.orgAddressDO.getAddressDO().getCity());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.getAddressDO().setCity(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			city.setReadOnly(!enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final TextBox zipCode = (TextBox)def.getWidget(OrgMeta.getAddress().getZipCode());
    	addScreenHandler(zipCode,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			zipCode.setValue(rpc.orgAddressDO.getAddressDO().getZipCode());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.getAddressDO().setZipCode(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event){
    			zipCode.setReadOnly(!enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final Dropdown<String> state = (Dropdown<String>)def.getWidget(OrgMeta.getAddress().getState());
    	addScreenHandler(state,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			state.setSelection(rpc.orgAddressDO.getAddressDO().getState());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.getAddressDO().setState(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			state.enabled(enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final Dropdown<String> country = (Dropdown<String>)def.getWidget(OrgMeta.getAddress().getCountry());
    	addScreenHandler(country,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			country.setSelection(rpc.orgAddressDO.getAddressDO().getCountry());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.getAddressDO().setCountry(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			country.enabled(enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final CheckBox isActive = (CheckBox)def.getWidget(OrgMeta.getIsActive());
    	addScreenHandler(isActive,new ScreenEventHandler<String>() {
    		public void onDataChange(DataChangeEvent event) {
    			isActive.setValue(rpc.orgAddressDO.getIsActive());
    		}
    		public void onValueChange(ValueChangeEvent<String> event) {
    			rpc.orgAddressDO.setIsActive(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event){
    			isActive.enable(enabledStates.contains(event.getState()));
    		}
    	});
    	
    	final AutoComplete<Integer> parentOrg = (AutoComplete)def.getWidget(OrgMeta.getParentOrganization().getName());
    	addScreenHandler(parentOrg,new ScreenEventHandler<Integer>() {
    		public void onDataChange(DataChangeEvent event){
    			if(rpc.parentOrgRPC != null){
    				if(rpc.parentOrgRPC.model != null){
    					ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();	
    					for(OrganizationAutoDO autoDO : rpc.parentOrgRPC.model){
    						TableDataRow row = new TableDataRow(4);
    						row.key = autoDO.getId();
    						row.cells[0] = autoDO.getName();
    						row.cells[1] = autoDO.getAddress();
    						row.cells[2] = autoDO.getCity();
    						row.cells[3] = autoDO.getState();
    						model.add(row);
    					}
    				}
    			}
    			parentOrg.setSelection(rpc.orgAddressDO.getParentOrganizationId());
    		}
    		public void onValueChangeEvent(ValueChangeEvent<Integer> event) {
    			rpc.orgAddressDO.setParentOrganizationId(event.getValue());
    		}
    		public void onStateChange(StateChangeEvent<State> event) {
    			parentOrg.enabled(enabledStates.contains(event.getState()));
    		}
    	});
    	
    	//Create Default blank model for AutoComplete field Parent Org
    	ArrayList<TableDataRow>  model = new ArrayList<TableDataRow>();
    	TableDataRow row = new TableDataRow(4);
    	row.key = null;
    	row.cells[0] = "";
    	row.cells[1] = "";
    	row.cells[2] = "";
    	row.cells[3] = "";
    	model.add(row);
    	parentOrg.setModel(model);
    	
    	//Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
    	parentOrg.setAutoCall(this);
    	
    	//Get ButtonPanel and set Handlers for Screen Functionality
    	final ButtonPanel bpanel = (ButtonPanel)def.getWidget("buttons");
    	addScreenHandler(bpanel, new ScreenEventHandler<Object>() {
    		public void onStateChange(StateChangeEvent<State> event) {
    			bpanel.setState(event.getState());
    		}
    	});
    	bpanel.addActionHandler(new ActionHandler<ButtonPanel.Action>() {
			public void onAction(ActionEvent<ButtonPanel.Action> event) {
				if (event.getAction() == ButtonPanel.Action.QUERY) {
		            query();
		        }
		        else if (event.getAction() == ButtonPanel.Action.ADD) {
		            add();
		        }
		        else if (event.getAction() == ButtonPanel.Action.UPDATE) {
		            update();
		        }
		        else if (event.getAction() == ButtonPanel.Action.DELETE) {
		            delete();
		        }
		        else if (event.getAction() == ButtonPanel.Action.COMMIT) {
		            commit();
		        }
		        else if (event.getAction() == ButtonPanel.Action.ABORT) {
		            abort();
		        }
			}
    	});
    	bpanel.addActionHandler(keyList.buttonActions);
    	
    	//Set Up Key listeners
    	keyList.addActionHandler(new ActionHandler<KeyListManager.Action>() {
    		public void onAction(ActionEvent<KeyListManager.Action> event) {
    	        if(event.getAction() == KeyListManager.Action.FETCH){
    	        	String tab = rpc.orgTabPanel;
    	        	rpc = new OrganizationRPC();
    	        	contactsTab.setRPC(new ContactsRPC());
    	        	notesTab.setRPC(new NotesRPC());
    	        	rpc.orgTabPanel = tab;
    	            rpc.orgAddressDO.setOrganizationId((Integer)((Object[])event.getData())[0]);
    	            final AsyncCallback call = ((AsyncCallback)((Object[])event.getData())[1]);
    	            window.setBusy("Fetching ...");
    	        	service.callScreen("fetch",rpc,new AsyncCallback<OrganizationRPC>() {
    	        		public void onSuccess(OrganizationRPC result) {
    	        			load(result);
    	        			setState(Screen.State.DISPLAY);
    	        			call.onSuccess(result);
    	        			window.clearStatus();
    	        		}
    	        		public void onFailure(Throwable caught) {
    	        			Window.alert(caught.getMessage());
    	        			call.onFailure(caught);
    	        		}
    	        	});
    	        }else if(event.getAction() == KeyListManager.Action.GETPAGE){
    	            //getPage(null,(QueryRPC)((Object[])obj)[0],(AsyncCallback)((Object[])obj)[1]);
    	    	}
    		}
    	});
    	addActionHandler(keyList.screenActions);
    	final ResultsTable results = (ResultsTable)def.getWidget("azTable");
    	addActionHandler(results.screenActions);
    	keyList.addActionHandler(results.keyListActions);
    	results.addActionHandler(keyList.resultsActions);
    	
    	//Get AZ buttons and setup Screen listeners and call to for query
    	final ButtonPanel azButtons = (ButtonPanel)def.getWidget("atozButtons");
    	addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
    		public void onStateChange(StateChangeEvent<State> event) {
    			azButtons.setState(event.getState());
    		}
    	});
    	azButtons.addActionHandler(new ActionHandler<ButtonPanel.Action>() {
			public void onAction(ActionEvent<ButtonPanel.Action> event) {
		        String baction = ((AppButton)event.getData()).action;
		        if (baction.startsWith("query:")) 
		            getOrganizations(baction.substring(6, baction.length()));
			}
    	});
    	
    	//Get TabPanel and set Tab Selection Handlers
    	final TabPanel tabs = (TabPanel)def.getWidget("orgTabPanel");
    	tabs.addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				switch(event.getSelectedItem().intValue()) {
					case 0 : {
						rpc.orgTabPanel = "contactsTab";
						break;
					}
					case 2 : {
						rpc.orgTabPanel = "notesTab";
						break;
					}
				}
			}
    	});
    	tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				switch(event.getItem().intValue()) {
					case 0 : {
						if(rpc.orgContacts == null)
							fillContactsModel();
						break;
					}
					case 2 : {
						if(rpc.notes == null)
							fillNotesModel();
						break;
					}
				}
			}
    	});
    	
    }
    
    public void setCountriesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(IdNameDO resultDO :  rpc.countries){
            model.add(new TableDataRow(resultDO.getName(),resultDO.getName()));
        } 
        countries.setModel(model);
        ((Dropdown<String>)contactsTable.columns.get(7).getColumnWidget()).setModel(model);
        rpc.countries = null;
    }
    
    public void setStatesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(IdNameDO resultDO :  rpc.states){
            model.add(new TableDataRow(resultDO.getName(),resultDO.getName()));
        } 
        states.setModel(model);
        ((Dropdown<String>)contactsTable.columns.get(5).getColumnWidget()).setModel(model);
        rpc.states = null;
    }
    
    public void query() {
        setState(Screen.State.QUERY);
        
    }

    public void add() {
    	rpc = new OrganizationRPC();
    	DataChangeEvent.fire(this);
        setState(Screen.State.ADD);
    }
    
    public void update() {
    	service.callScreen("fetchForUpdate",rpc, new SyncCallback<OrganizationRPC>() {
    		public void onFailure(Throwable caught) {   
    		}

    		public void onSuccess(OrganizationRPC result) {
                rpc = result;
    		}
    	});
        DataChangeEvent.fire(this);
        setState(State.UPDATE);
    }
    
    public void delete() {
    	strikeThru(true);
        setState(State.DELETE);
    }
    
    public void fetch() {
    	service.callScreen("fectch",rpc,new AsyncCallback<OrganizationRPC>() {
    		public void onSuccess(OrganizationRPC result) {
    			load(result);
    			setState(Screen.State.DISPLAY);
    		}
    		public void onFailure(Throwable caught) {
    			Window.alert(caught.getMessage());
    		}
    	});
    }
    
    
    public void load(OrganizationRPC rpc) {
    	this.rpc = rpc;
    	DataChangeEvent.fire(this);
		if(rpc.orgContacts != null) {
			contactsTab.setRPC(rpc.orgContacts);
		}
		if(rpc.notes != null)
			notesTab.setRPC(rpc.notes);
    }


    private void getOrganizations(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(OrgMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }

    /*
     * Get all notes for organization (key)
     */
    private void fillNotesModel(){  
        if(rpc.orgAddressDO.getOrganizationId() == null)
            return;
        
        window.setBusy("Loading notes...");
        rpc.notes = new NotesRPC();
        rpc.notes.key = rpc.orgAddressDO.getOrganizationId();
        service.callScreen("loadNotes", rpc.notes, new AsyncCallback<NotesRPC>(){
            public void onSuccess(NotesRPC result){    
                rpc.notes = result;
                notesTab.setRPC(result);
                window.clearStatus();
            }
                   
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });        
    }

    private void fillContactsModel() {
        if(rpc.orgAddressDO.getOrganizationId() == null)
            return;
        
        window.setBusy("Loading contacts...");
        rpc.orgContacts = new ContactsRPC();
        rpc.orgContacts.orgId = rpc.orgAddressDO.getOrganizationId();
        service.callScreen("loadContacts", rpc.orgContacts, new AsyncCallback<ContactsRPC>() {
            public void onSuccess(ContactsRPC result) {
                rpc.orgContacts = result;
                contactsTab.setRPC(rpc.orgContacts);
                window.clearStatus();

            }
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }


    private void onRemoveContactRowButtonClick() {

    }
	
    public void setState(Screen.State state){
        this.state = state;
        StateChangeEvent.fire(this, state);
    }
    

    
    public void commit() {
        if (state == State.UPDATE) {
            //submitForm();
            if (validate()) {
                //window.setBusy(consts.get("updating"));
                commitUpdate();
            } else {
                //window.setBusy(consts.get("correctErrors"));
            }
        }
        if (state == State.ADD) {
           // submitForm();
            if (validate()) {
                //window.setBusy(consts.get("adding"));
                commitAdd();
            } else {
                //window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.QUERY) {
            //((Query)query).fields = getQueryFields();
            //ArrayList<AbstractField> qList = new ArrayList<AbstractField>();
            //commandListeners.fireCommand(Screen.Action.SUBMIT_QUERY, qList);
            //if(!qList.isEmpty()){
              //  if(!isQueryValid(qList)) {
                    //drawErrors();
                   // window.setError(consts.get("correctErrors"));
                //    return;
              //  }
           // }
           // window.setBusy(consts.get("querying"));
           // clearErrors();
            //commitQuery();
        }
        if(state == State.DELETE){
            //submitForm();
            commitDelete();
        }
        
    }
    
    public void commitUpdate() {
    	service.callScreen("update", rpc, new AsyncCallback<OrganizationRPC>() {
    		public void onSuccess(OrganizationRPC result) {
    			load(result);
    			setState(Screen.State.DISPLAY);
    		}
    		public void onFailure(Throwable caught) {
    			
    		}
    	});
    }
    
    public void commitAdd() {
    	service.callScreen("add", rpc, new AsyncCallback<OrganizationRPC>() {
    		public void onSuccess(OrganizationRPC result) {
    			load(result);
    			setState(Screen.State.DISPLAY);
    		}
    		public void onFailure(Throwable caught) {
    			
    		}
    	});
    }
    
    public void abort() {
        if (state == State.UPDATE) {
            service.callScreen("abort", rpc, new AsyncCallback<OrganizationRPC>() {
            	public void onSuccess(OrganizationRPC rpc) {
            		load(rpc);
            		setState(State.DISPLAY);
            	}
            	public void onFailure(Throwable caught) {
            		Window.alert(caught.getMessage());
            	}
            });
        }
        else if (state == State.ADD) {
            load(new OrganizationRPC());
            setState(State.DEFAULT);
            //window.setDone(consts.get("addAborted"));
        }
        else if (state == State.QUERY) {
            load(new OrganizationRPC());
            setState(State.DEFAULT);
            //window.setDone(consts.get("queryAborted"));
        }
        else if(state == State.DELETE){
        	strikeThru(false);
        	setState(State.DISPLAY);
        	//window.setDone(consts.get("deleteAborted"));
        }
    }
    
    public void commitDelete() {
    	
    }
    
    public void commitQuery(QueryField qField){
    	ArrayList<AbstractField> qList = new ArrayList<AbstractField>();
    	qList.add(qField);
    	commitQuery(qList);
    }
    final Organization orgHandle = this;
    public void commitQuery(ArrayList<AbstractField> qFields) {
    	OrgQuery query = new OrgQuery();
    	query.fields = qFields;
    	service.callScreen("query",query,new AsyncCallback<OrgQuery>() {
    		public void onSuccess(OrgQuery query){
                rpc = new OrganizationRPC();
                load(rpc);
                //window.setDone(consts.get("queryingComplete"));
                query.model = new ArrayList<TableDataRow>();
                for(IdNameDO entry : query.results) {
                	query.model.add(new TableDataRow(entry.getId(),entry.getName()));
                }
                ActionEvent.fire(orgHandle,Action.NEW_MODEL, query);
    		}
    		public void onFailure(Throwable caught){
    			
    		}
    	});
    }
    
    public boolean isQueryValid(ArrayList<QueryField> qFields) {
        for(QueryField field : qFields) {
            if(!field.isValid())
                return false;
        }
        return true;
        
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
					row.cells[0] = autoDO.getName();
					row.cells[1] = autoDO.getAddress();
					row.cells[2] = autoDO.getCity();
					row.cells[3] = autoDO.getState();
					model.add(row);
				}
				widget.showAutoMatches(model);
			}
			
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
	}

}