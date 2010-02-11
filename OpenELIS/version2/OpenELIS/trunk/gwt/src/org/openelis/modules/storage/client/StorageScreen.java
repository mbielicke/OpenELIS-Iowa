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
package org.openelis.modules.storage.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.StorageLocationManager;
import org.openelis.manager.StorageManager;
import org.openelis.manager.StorageViewManager;
import org.openelis.meta.StorageMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class StorageScreen extends Screen {
    private StorageViewManager     manager;
    private SecurityModule         security;
    
    private TextBox                name, location, storageUnitDescription;
    private CheckBox               isAvailable;
    private AppButton              queryButton, previousButton, nextButton,
                                   updateButton, commitButton, abortButton;
    private ButtonGroup            atoz;
    private ScreenNavigator        nav;
    
    private CurrentTab             currentTab;
    private HistoryTab             historyTab;
    private Tabs                   tab;
    private TabPanel               tabPanel;
    
    private ScreenService          storageLocationService;             

    private enum Tabs {
        CURRENT, HISTORY
    };    
    
    public StorageScreen() throws Exception {
        super((ScreenDefInt)GWT.create(StorageDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        storageLocationService = new ScreenService("controller?service=org.openelis.modules.storageLocation.server.StorageLocationService");
    
        security = OpenELIS.security.getModule("storage");
        if (security == null)
            throw new SecurityException("screenPermException", "Storage Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        tab = Tabs.CURRENT;
        manager = StorageViewManager.getInstance();        
        
        initialize();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
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

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    security.hasUpdatePermission());
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
                commitButton.enable(EnumSet.of(State.QUERY, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.UPDATE)
                                          .contains(event.getState()));
            }
        });
        
        name = (TextBox)def.getWidget(StorageMeta.getStorageLocationName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                StorageLocationManager slm;
                
                slm = manager.getStorageLocation();
                if(slm != null)
                    name.setValue(slm.getStorageLocation().getName());
                else 
                    name.setValue(null);
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = (TextBox)def.getWidget(StorageMeta.getStorageLocationLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                StorageLocationManager slm;
                
                slm = manager.getStorageLocation();
                if(slm != null)
                    location.setValue(slm.getStorageLocation().getLocation());
                else
                    location.setValue(null);
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storageUnitDescription = (TextBox)def.getWidget(StorageMeta.getStorageLocationStorageUnitDescription());
        addScreenHandler(storageUnitDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                StorageLocationManager slm;
                
                slm = manager.getStorageLocation();
                if(slm != null) 
                    storageUnitDescription.setValue(slm.getStorageLocation().getStorageUnitDescription());
                else 
                    storageUnitDescription.setValue(null);
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageUnitDescription.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                storageUnitDescription.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isAvailable = (CheckBox)def.getWidget(StorageMeta.getStorageLocationIsAvailable());
        addScreenHandler(isAvailable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                StorageLocationManager slm;

                slm = manager.getStorageLocation();
                if (slm != null) 
                    isAvailable.setValue(slm.getStorageLocation().getIsAvailable());
                else 
                    isAvailable.setValue(null);
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isAvailable.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                isAvailable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        currentTab = new CurrentTab(def, window);
        addScreenHandler(currentTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                currentTab.setManager(manager);
                if (tab == Tabs.CURRENT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                currentTab.setState(event.getState());
            }
        });

        historyTab = new HistoryTab(def, window);
        addScreenHandler(historyTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                historyTab.setManager(manager);
                if (tab == Tabs.HISTORY)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyTab.setState(event.getState());
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                storageLocationService.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(consts.get("noMoreRecordInDir"));
                        } else {
                            Window.alert("Error: Storage call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {                
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName()));
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
                field.key = StorageMeta.getStorageLocationName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = StorageViewManager.getInstance();
        
        setState(State.QUERY);
        DataChangeEvent.fire(this);
        
        // clear all the tabs
        currentTab.draw();
        historyTab.draw();
        
        setFocus(name);
        window.setDone(consts.get("enterFieldsToQuery"));
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
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }
    
    public void commit() {                
        Collection<StorageManager> strgList;
        Collection<StorageLocationManager> locList;
        ArrayList<Integer> locIdList;
        Integer id;
        StorageLocationManager man;
        
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {                              
                //
                // we first update the managers that represents the new locations
                // to which the items from this location are to be moved
                //
                strgList = currentTab.getStorageList();
                for(StorageManager sm: strgList) {
                    sm.update();
                }                
                
                //
                // we then call update on the managers that represent the parent
                // storage locations of the new locations that were locked
                //
                locList = currentTab.getLocationList();
                locIdList = new ArrayList<Integer>();                
                for(StorageLocationManager slm: locList) {
                    id = slm.getStorageLocationId();
                    if(!locIdList.contains(id)) {
                        locIdList.add(id);
                        slm.update();
                    }
                }
                
                //
                // this will release the lock on the parent storage location of 
                // the storage locations holding items currently showing on the screen
                //
                man = manager.getStorageLocation();
                if(!locIdList.contains(man.getStorageLocationId())) 
                    man.update();
                
                //
                // we then update the manager representing the current items
                //
                manager = manager.update();                
                
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
        Collection<StorageLocationManager> locList;
        
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {
            try {                
                locList = currentTab.getLocationList();
                for(StorageLocationManager slm: locList) {
                    slm.abortUpdate();
                }
                
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
            manager = StorageViewManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {                
                switch (tab) {
                    case CURRENT:                                      
                        manager = StorageViewManager.fetchById(id);
                        break;
                    case HISTORY:
                        manager = StorageViewManager.fetchById(id);
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
            case CURRENT:
                currentTab.draw();
                break;
            case HISTORY:
                historyTab.draw();
                break;
        }
    }        
}