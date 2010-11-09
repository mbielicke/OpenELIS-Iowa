/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.shipping.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.meta.ShippingMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.NotesTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class ShippingScreen extends Screen {
    private ShippingManager                manager;
    private ModulePermission               userPermission;

    private ScreenNavigator                nav;

    private NotesTab                       noteTab;
    private ItemTab                        itemTab;
    private Tabs                           tab;

    private Button                         queryButton, previousButton, nextButton, addButton,
                                           updateButton, commitButton, abortButton;
    protected MenuItem                     shippingHistory, shippingItemHistory,
                                           shippingTrackingHistory;
    private TextBox                        id, numberOfPackages, cost, shippedToAddressMultipleUnit,
                                           processedById, shippedToAddressStreetAddress, shippedToAddressCity,
                                           shippedToAddressState, shippedToAddressZipCode;
    private Calendar                       shippedDate, processedDate;
    private Dropdown<Integer>              statusId, shippedFromId, shippedMethodId;
    private AutoComplete                   shippedToName;
    private TabPanel                       tabPanel;
    private Integer                        status_processed;
        
    private boolean                        openedFromMenu;
    
    protected ScreenService                organizationService;    
        
    private enum Tabs {
        ITEM, SHIP_NOTE
    };    
    
    public ShippingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ShippingDef.class));

        ShippingScreenImpl(true);
    }

    public ShippingScreen(Window window) throws Exception {
        super((ScreenDefInt)GWT.create(ShippingDef.class));
        this.window = window;

        ShippingScreenImpl(false);
    }
    
    private void ShippingScreenImpl(boolean fromMenu) throws Exception {
        service = new ScreenService("controller?service=org.openelis.modules.shipping.server.ShippingService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");        
        
        userPermission = OpenELIS.getSystemUserPermission().getModule("shipping");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Shipping Screen");

        openedFromMenu = fromMenu;
        
        //
        // this is done here in order to make sure that if the screen is brought
        // up from some other screen (i.e. window != null) then its widgets are 
        // initialized before the constructor ends execution
        //
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

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        tab = Tabs.ITEM;
        manager = ShippingManager.getInstance();
        
        try {
            DictionaryCache.preloadByCategorySystemNames("shipping_status", "order_ship_from",
                                                         "shipping_method");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("ShippingSreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }   

    private void initialize() {
        //
        // button panel buttons
        //
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        previousButton = (Button)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (Button)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasAddPermission());
                if (event.getState() == State.ADD) {
                    addButton.setPressed(true);
                    addButton.lock();
                }
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        shippingHistory = (MenuItem)def.getWidget("shippingHistory");
        addScreenHandler(shippingHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                shippingHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        shippingHistory.addCommand(new Command() {
			public void execute() {
				shippingHistory();
			}
		});
        
        shippingItemHistory = (MenuItem)def.getWidget("itemHistory");
        addScreenHandler(shippingItemHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                shippingItemHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        shippingItemHistory.addCommand(new Command() {
			public void execute() {
				shippingItemHistory();
			}
		});
        
        shippingTrackingHistory = (MenuItem)def.getWidget("trackingHistory");
        addScreenHandler(shippingTrackingHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                shippingTrackingHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        shippingTrackingHistory.addCommand(new Command() {
			public void execute() {
				shippingTrackingHistory();
			}
		});

        id = (TextBox<Integer>)def.getWidget(ShippingMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getShipping().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.setEnabled(EnumSet.of(State.QUERY)
                                 .contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });    
        statusId = (Dropdown<Integer>)def.getWidget(ShippingMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(manager.getShipping().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedDate = (Calendar)def.getWidget(ShippingMeta.getShippedDate());
        addScreenHandler(shippedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                shippedDate.setValue(manager.getShipping().getShippedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getShipping().setShippedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedDate.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        numberOfPackages = (TextBox)def.getWidget(ShippingMeta.getNumberOfPackages());
        addScreenHandler(numberOfPackages, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                numberOfPackages.setValue(manager.getShipping().getNumberOfPackages());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setNumberOfPackages(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                numberOfPackages.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                numberOfPackages.setQueryMode(event.getState() == State.QUERY);
            }
        });

        cost = (TextBox)def.getWidget(ShippingMeta.getCost());
        addScreenHandler(cost, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                cost.setValue(manager.getShipping().getCost());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                manager.getShipping().setCost(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cost.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                cost.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedFromId = (Dropdown<Integer>)def.getWidget(ShippingMeta.getShippedFromId());
        addScreenHandler(shippedFromId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shippedFromId.setValue(manager.getShipping().getShippedFromId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setShippedFromId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedFromId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedFromId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToName = (AutoComplete)def.getWidget(ShippingMeta.getShippedToName());
        addScreenHandler(shippedToName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) {
                    shippedToName.setValue(data.getShippedTo().getId(), 
                                               data.getShippedTo().getName());
                } else {
                    shippedToName.setValue(null,"");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;
                
                if(shippedToName.getSelectedItem() != null) {
                    data = (OrganizationDO) shippedToName.getSelectedItem().getData();
                    
                    manager.getShipping().setShippedToId(data.getId());
                    manager.getShipping().setShippedTo(data);
                    
                    shippedToAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    shippedToAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    shippedToAddressCity.setValue(data.getAddress().getCity());
                    shippedToAddressState.setValue(data.getAddress().getState());
                    shippedToAddressZipCode.setValue(data.getAddress().getZipCode());
                } else {
                    manager.getShipping().setShippedToId(null);
                    manager.getShipping().setShippedTo(null);
                    
                    shippedToAddressMultipleUnit.setValue(null);
                    shippedToAddressStreetAddress.setValue(null);
                    shippedToAddressCity.setValue(null);
                    shippedToAddressState.setValue(null);
                    shippedToAddressZipCode.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToName.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedToName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        shippedToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                window.setBusy();
                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = organizationService.callList("fetchByIdOrName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getAddress().getStreetAddress());
                        row.setCell(2,data.getAddress().getCity());
                        row.setCell(3,data.getAddress().getState());
                        
                        row.setData(data);
                        
                        model.add(row);
                    }
                    
                    shippedToName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }
            
        });

        processedDate = (Calendar)def.getWidget(ShippingMeta.getProcessedDate());
        addScreenHandler(processedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                processedDate.setValue(manager.getShipping().getProcessedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getShipping().setProcessedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                processedDate.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                processedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressMultipleUnit = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressMultipleUnit());
        addScreenHandler(shippedToAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) 
                    shippedToAddressMultipleUnit.setValue(data.getShippedTo().getAddress().getMultipleUnit());
                else 
                    shippedToAddressMultipleUnit.setValue(null);                
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAddressMultipleUnit.setEnabled(false);
                shippedToAddressMultipleUnit.setQueryMode(false);
            }
        });

        processedById = (TextBox)def.getWidget(ShippingMeta.getProcessedBy());
        addScreenHandler(processedById, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                processedById.setValue(manager.getShipping().getProcessedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getShipping().setProcessedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                processedById.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                processedById.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressStreetAddress = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressStreetAddress());
        addScreenHandler(shippedToAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) 
                    shippedToAddressStreetAddress.setValue(data.getShippedTo().getAddress().getStreetAddress());
                else 
                    shippedToAddressStreetAddress.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAddressStreetAddress.setEnabled(false);
                shippedToAddressStreetAddress.setQueryMode(false);
            }
        });

        shippedMethodId = (Dropdown<Integer>)def.getWidget(ShippingMeta.getShippedMethodId());
        addScreenHandler(shippedMethodId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shippedMethodId.setValue(manager.getShipping().getShippedMethodId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setShippedMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedMethodId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedMethodId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressCity = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressCity());
        addScreenHandler(shippedToAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) 
                    shippedToAddressCity.setValue(data.getShippedTo().getAddress().getCity());
                else 
                    shippedToAddressCity.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
               // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAddressCity.setEnabled(false);
                shippedToAddressCity.setQueryMode(false);
            }
        });

        shippedToAddressState = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressState());
        addScreenHandler(shippedToAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) 
                    shippedToAddressState.setValue(data.getShippedTo().getAddress().getState());
                else
                    shippedToAddressState.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
               // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAddressState.setEnabled(false);
                shippedToAddressState.setQueryMode(false);
            }
        });

        shippedToAddressZipCode = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressZipCode());
        addScreenHandler(shippedToAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null)
                    shippedToAddressZipCode.setValue(data.getShippedTo().getAddress().getZipCode());
                else 
                    shippedToAddressZipCode.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAddressZipCode.setEnabled(false);
                shippedToAddressZipCode.setQueryMode(false);
            }
        });

        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        itemTab = new ItemTab(def, window);
        addScreenHandler(itemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                itemTab.setManager(manager);
                if (tab == Tabs.ITEM)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTab.setState(event.getState());
            }
        });

        noteTab = new NotesTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(noteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                noteTab.setManager(manager);
                if (tab == Tabs.SHIP_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                noteTab.setState(event.getState());
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(Query query) {
                service.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
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
                            com.google.gwt.user.client.Window.alert("Error: Order call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById((entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName()));
                }
                return model;
            }
        };
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }    
    
    
    public void loadShippingData(ShippingManager manager, State state) { 
        if(state == State.ADD) {
            add(manager);                    
        } else if(state == State.DISPLAY){
            this.manager = manager; 
            itemTab.setManager(manager);
            noteTab.setManager(manager);
            setState(state);
            DataChangeEvent.fire(this);
            drawTabs();
        }
    }    
    
    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        // order status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("shipping_status");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        statusId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_ship_from");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        shippedFromId.setModel(model);
        
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("shipping_method");
        for (DictionaryDO d : list) {
            model.add(new Item<Integer>(d.getId(), d.getEntry()));
        }

        shippedMethodId.setModel(model);
        
        try {
            status_processed  = DictionaryCache.getIdFromSystemName("shipping_status_processed");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = ShippingManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        itemTab.draw();
        noteTab.draw();
        
        setFocus(id);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add(ShippingManager manager) { 
        ShippingViewDO data;
        Datetime now;
        
        try {
            now = org.openelis.gwt.screen.Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("Shipping Add Datetime: " +e.getMessage());
            return;
        }
        
        if(manager == null)
            this.manager = ShippingManager.getInstance();
        else 
            this.manager = manager;
        
        data = this.manager.getShipping();
        data.setStatusId(status_processed);
        data.setProcessedDate(now);
        data.setProcessedBy(OpenELIS.getSystemUserPermission().getLoginName());
        
        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(statusId);
        window.setDone(consts.get("enterInformationPressCommit"));
    }
    
    protected void update() {
        boolean ok;
        window.setBusy(consts.get("lockForUpdate"));

        try {
            
            if (status_processed.equals(manager.getShipping().getStatusId())) {
               ok = com.google.gwt.user.client.Window.confirm(consts.get("shippingStatusProcessed"));
               if (ok) {
                    manager = manager.fetchForUpdate();
                    setState(State.UPDATE);
                    setFocus(statusId);
                    DataChangeEvent.fire(this);
                    window.clearStatus();
                } else {
                    manager = manager.abortUpdate();
                    setState(State.DISPLAY);
                    DataChangeEvent.fire(this);
                    window.setDone(consts.get("updateAborted"));
                }
            }                         
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.clearStatus();
        }        
    }

    protected void commit() {
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
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
                
                if (!openedFromMenu)                     
                    window.close();                
                    
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
                e.printStackTrace();
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
            
            if (!openedFromMenu)                 
                window.close();
            
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void shippingHistory() {
        IdNameVO hist;
        ShippingViewDO data;
        
        data = manager.getShipping();
        
        hist = new IdNameVO(data.getId(), consts.get("shipping"));
        HistoryScreen.showHistory(consts.get("shippingHistory"),
                                  ReferenceTable.SHIPPING, hist);            
                        
    }
    
    protected void shippingItemHistory() {
        int i, count;
        IdNameVO refVoList[];
        ShippingItemManager man;
        ShippingItemDO data;
        
        try {
            man = manager.getItems();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++) {
                data = man.getItemAt(i);                                
                refVoList[i] = new IdNameVO(data.getId(), data.getDescription());                
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("shippingItemHistory"),
                                  ReferenceTable.SHIPPING_ITEM, refVoList);
    }
    
    protected void shippingTrackingHistory() {
        int i, count;
        IdNameVO refVoList[];
        ShippingTrackingManager man;
        ShippingTrackingDO data;
        
        try {
            man = manager.getTrackings();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++) {
                data = man.getTrackingAt(i);                                
                refVoList[i] = new IdNameVO(data.getId(), data.getTrackingNumber());                
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("shippingTrackingHistory"),
                                  ReferenceTable.SHIPPING_TRACKING, refVoList);
        
    }       
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ShippingManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case ITEM:
                      manager = ShippingManager.fetchWithItemsAndTracking(id);
                      break;
                    case SHIP_NOTE: 
                      manager = ShippingManager.fetchWithNotes(id);
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
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private void drawTabs() {
        switch (tab) {
            case ITEM:
                itemTab.draw();
                break;                   
            case SHIP_NOTE:
                noteTab.draw();
                break;   
        }
    }   
}