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
package org.openelis.modules.provider.client;


import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.NotesTab;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.ProviderManager;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class ProviderScreen extends Screen {
         
	private TextBox id, lastName, firstName, npi, middleName;
	private AppButton queryButton, previousButton, nextButton, addButton, updateButton, commitButton, abortButton,  standardNoteButton;
	private Dropdown<Integer> typeId;
	private TabPanel provTabPanel;
     
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;
    
    private ProviderMetaMap META = new ProviderMetaMap();
    private SecurityModule security;
    
    private enum Tabs {
    	ADDRESSES, NOTES
    };
    
    private Tabs tab;
    private AddressesTab          addressesTab;
    private NotesTab              notesTab;
    
    private ProviderManager manager;
    
    public ProviderScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ProviderScreenDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.provider.server.ProviderService");
        
        security = OpenELIS.security.getModule("provider");
        if(security == null)
        	throw new org.openelis.gwt.common.SecurityException("screenPermException", "Provider Screen");
        
        initialize();
        	
        DeferredCommand.addCommand(new Command() {
        	public void execute() {
        		postConstructor();
        	}
        });
    }
    
    private void postConstructor() {
    	tab = Tabs.ADDRESSES;
    	
    	addressesTab.setWindow(window);
    	
    	manager = ProviderManager.getInstance();
    	
        setState(State.DEFAULT);
        initializeDropdowns();

        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && security.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        id = (TextBox)def.getWidget(META.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getProvider().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        lastName = (TextBox)def.getWidget(META.getLastName());
        addScreenHandler(lastName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                lastName.setValue(manager.getProvider().getLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lastName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                lastName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown<Integer>)def.getWidget(META.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(manager.getProvider().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        firstName = (TextBox)def.getWidget(META.getFirstName());
        addScreenHandler(firstName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                firstName.setValue(manager.getProvider().getFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                firstName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                firstName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        npi = (TextBox)def.getWidget(META.getNpi());
        addScreenHandler(npi, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                npi.setValue(manager.getProvider().getNpi());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setNpi(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                npi.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                npi.setQueryMode(event.getState() == State.QUERY);
            }
        });

        middleName = (TextBox)def.getWidget(META.getMiddleName());
        addScreenHandler(middleName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                middleName.setValue(manager.getProvider().getMiddleName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setMiddleName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                middleName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                middleName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        provTabPanel = (TabPanel)def.getWidget("provTabPanel");
        provTabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                i = event.getItem().intValue();
                tab = Tabs.values()[i];
                
                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });
        
        addressesTab = new AddressesTab(def);
        addScreenHandler(addressesTab, new ScreenEventHandler<Object>() {
        	public void onDataChange(DataChangeEvent event){
        		addressesTab.setManager(manager);
        		if(tab == Tabs.ADDRESSES)
        			drawTabs();
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		addressesTab.setState(event.getState());
        	}
        });
        
        notesTab = new NotesTab(def, "notesPanel", "standardNoteButton", false);
        addScreenHandler(notesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.setManager(manager);
                if (tab == Tabs.NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                notesTab.setState(event.getState());
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdLastNameFirstNameDO>>() {
                    public void onSuccess(ArrayList<IdLastNameFirstNameDO> result) {
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
                            Window.alert("Error: Provider call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdLastNameFirstNameDO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdLastNameFirstNameDO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdLastNameFirstNameDO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getLastName(), entry.getFirstName()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = META.getLastName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });        
    }
    
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> cache = DictionaryCache.getListByCategorySystemName("provider_type");
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null,""));
        for(DictionaryDO d: cache) {
        	model.add(new TableDataRow(d.getId(), d.getEntry()));
        }
        typeId.setModel(model);
    }

    protected void query() {
        manager = ProviderManager.getInstance();

        setState(Screen.State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        addressesTab.draw();
        notesTab.draw();
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = ProviderManager.getInstance();

        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
        //
        // set the focus to null so every field will commit its data.
        //
        id.setFocus(false);

        if ( !validate()) {
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
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        id.setFocus(false);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ProviderManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case ADDRESSES:
                        manager = ProviderManager.fetchWithAddresses(id);
                        break;
                    case NOTES:
                        manager = ProviderManager.fetchWithNotes(id);
                        break;
                }
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private void drawTabs() {
        switch (tab) {
            case ADDRESSES:
                addressesTab.draw();
                break;
            case NOTES:
                notesTab.draw();
                break;
        }
    }
    

/*
    public void afterDraw(boolean success) {
        ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");

        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);

        //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        // load other widgets
        removeContactButton = (AppButton)getWidget("removeAddressButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");

        provId = (ScreenTextBox)widgets.get(ProvMeta.getId());
        lastName = (TextBox)getWidget(ProvMeta.getLastName());
        // subjectBox = (TextBox)getWidget(ProvMeta.getNote().getSubject());
        noteArea = (ScreenTextArea)widgets.get(ProvMeta.getNote().getText());
        svp = (ScreenVertical) widgets.get("notesPanel");
        tabPanel = (ScreenTabPanel)widgets.get("provTabPanel");
        
        displayType = (Dropdown)getWidget(ProvMeta.getTypeId());

        provAddController = ((TableWidget)getWidget("providerAddressTable"));

        // load dropdowns
        ScreenTableWidget displayAddressTable = (ScreenTableWidget)widgets.get("providerAddressTable");
        provAddController = (TableWidget)displayAddressTable.getWidget();

        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);

        super.afterDraw(success);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("state");
        model = getDictionaryEntryKeyList(cache);
        ((TableDropdown)provAddController.columns.get(5).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("country");
        model = getDictionaryEntryKeyList(cache);
        ((TableDropdown)provAddController.columns.get(6).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("provider_type");
        model = getDictionaryIdEntryList(cache);
        displayType.setModel(model);
    }
    
    public void query(){
        //clearNotes();            
       super.query();
    
        //set focus to the last name field
        provId.setFocus(true);
        noteArea.enable(false);
        provAddController.model.enableAutoAdd(false);
    }

    public void add(){                                                  
        svp.clear();
        
        //provAddController.setAutoAdd(true);         
        super.add();     
        
        noteArea.enable(true);
        provId.enable(false);
        
        //set focus to the last name field       
        lastName.setFocus(true);
        provAddController.model.enableAutoAdd(true);
        
    }   
    
    public void abort() {
        provAddController.model.enableAutoAdd(false); 
        super.abort();
    }
    
    protected SyncCallback<ProviderForm> afterUpdate = new SyncCallback<ProviderForm>() {
        public void onFailure(Throwable caught) {
        }
        public void onSuccess(ProviderForm result) {            
            provId.enable(false);                                      
            noteArea.enable(true);
            
            //set focus to the last name field
            lastName.setFocus(true);
            provAddController.model.enableAutoAdd(true);
        }
           
    };    
    
    protected SyncCallback<ProviderForm> commitUpdateCallback = new SyncCallback<ProviderForm>() {
        public void onSuccess(ProviderForm result) {
            if (form.status != Form.Status.invalid)                                
                provAddController.model.enableAutoAdd(false);
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected SyncCallback<ProviderForm> commitAddCallback = new SyncCallback<ProviderForm>() {
        public void onSuccess(ProviderForm result) {
            if (form.status != Form.Status.invalid)                                
                provAddController.model.enableAutoAdd(false); 
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };   
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {

        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int index) {
        form.provTabPanel = tabPanel.getSelectedTabKey();
        if(state != State.QUERY){
            if (index == 0 && !form.addresses.load) 
                fillAddressModel();
            else if (index == 1 && !form.notes.load) 
                fillNotesModel();
        }
    }
    
    public boolean canAdd(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canAutoAdd(TableWidget widget,TableDataRow row) {        
        return ((DataObject)row.getCells().get(0)).getValue() != null && !((DataObject)row.getCells().get(0)).getValue().equals("");
    }



    public boolean canDelete(TableWidget widget,TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean canEdit(TableWidget widget,TableDataRow set, int row, int col) {
        return true;
    }



    public boolean canSelect(TableWidget widget,TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY){      
            return true;
        } 
        return false;
    }
    
    private void getProviders(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(ProvMeta.getLastName());
            qField.setValue(query);
            commitQuery(qField); 
       }
    }
    
   private void fillNotesModel() {  
       if(form.entityKey == null)
           return;
       
       window.setBusy();
       
       form.notes.entityKey = form.entityKey;
              
       screenService.call("loadNotes", form.notes, new AsyncCallback<NotesForm>(){
           public void onSuccess(NotesForm result){    
               form.notes = result;
               load(form.notes);
               window.clearStatus();
           }
                  
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });       
   }
   
   private void fillAddressModel() {
       
       if(form.entityKey == null)
           return;
       
       window.setBusy();
       
       form.addresses.entityKey = form.entityKey;
       
       screenService.call("loadAddresses", form.addresses, new AsyncCallback<AddressesForm>() {
           public void onSuccess(AddressesForm result) { 
               form.addresses = result;
               load(form.addresses);
               window.clearStatus();
           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });
      } 
     
      
   
   private void onStandardNoteButtonClick(){
       
       ScreenWindow modal = new ScreenWindow(null,"Standard Note Screen",
                                             "standardNoteScreen","",true,false);
       modal.setName(consts.get("standardNote"));
       modal.setContent(new EditNoteScreen((TextBox)getWidget(ProvMeta.getNote()
                                                                                 .getSubject()),
                                                      (TextArea)getWidget(ProvMeta.getNote()
                                                                                  .getText())));
                                                                                  
    }
    
    private void onRemoveRowButtonClick(){
        int index = provAddController.modelIndexList[provAddController.activeRow];
        if (index > -1) 
            provAddController.model.deleteRow(index);                  
    }

    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
    
    private TableDataModel<TableDataRow> getDictionaryEntryKeyList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<String> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<String>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<String>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getEntry();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
 */
}
