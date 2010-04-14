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
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class ShippingScreen extends Screen {
    private ShippingManager       manager;
    private SecurityModule        security;

    private ScreenNavigator       nav;
    
    private NotesTab              noteTab;
    private ItemTab               itemTab;
    private Tabs                  tab;

    private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    protected MenuItem            shippingHistory, shippingItemHistory, shippingTrackingHistory; 
    private TextBox               numberOfPackages, cost, shippedToAddressMultipleUnit,
                                  processedById, shippedToAddressStreetAddress, shippedToAddressCity,
                                  shippedToAddressState, shippedToAddressZipCode;
    private CalendarLookUp        shippedDate, processedDate;
    private Dropdown<Integer>     statusId, shippedFromId, shippedMethodId;
    private AutoComplete<Integer> shippedToName;
    private TabPanel              tabPanel;
    private Integer               status_processed;
    
    protected ScreenService       organizationService;
    
    private enum Tabs {
        ITEM, SHIP_NOTE
    };

    public ShippingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ShippingDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.shipping.server.ShippingService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
        security = OpenELIS.security.getModule("shipping");
        if (security == null)
            throw new SecurityException("screenPermException", "Shipping Screen");

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
        tab = Tabs.ITEM;
        manager = ShippingManager.getInstance();
        
        try {
            DictionaryCache.preloadByCategorySystemNames("shipping_status", "order_ship_from",
                                                         "shipping_method");
        } catch (Exception e) {
            Window.alert("ShippingSreen: missing dictionary entry; " + e.getMessage());
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
        
        shippingHistory = (MenuItem)def.getWidget("shippingHistory");
        addScreenHandler(shippingHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingHistory();
            }


            public void onStateChange(StateChangeEvent<State> event) {
                shippingHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        shippingItemHistory = (MenuItem)def.getWidget("itemHistory");
        addScreenHandler(shippingItemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingItemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippingItemHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        shippingTrackingHistory = (MenuItem)def.getWidget("trackingHistory");
        addScreenHandler(shippingTrackingHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingTrackingHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippingTrackingHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        statusId = (Dropdown)def.getWidget(ShippingMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getShipping().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedDate = (CalendarLookUp)def.getWidget(ShippingMeta.getShippedDate());
        addScreenHandler(shippedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                shippedDate.setValue(manager.getShipping().getShippedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getShipping().setShippedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                numberOfPackages.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                cost.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                cost.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedFromId = (Dropdown)def.getWidget(ShippingMeta.getShippedFromId());
        addScreenHandler(shippedFromId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shippedFromId.setSelection(manager.getShipping().getShippedFromId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setShippedFromId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedFromId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedFromId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToName = (AutoComplete)def.getWidget(ShippingMeta.getShippedToName());
        addScreenHandler(shippedToName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;
                
                data = manager.getShipping();
                if(data.getShippedTo() != null) {
                    shippedToName.setSelection(data.getShippedTo().getId(), 
                                               data.getShippedTo().getName());
                } else {
                    shippedToName.setSelection(null,"");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;
                
                if(shippedToName.getSelection() != null) {
                    data = (OrganizationDO) shippedToName.getSelection().data;
                    
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
                shippedToName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shippedToName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        shippedToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();
                try {
                    list = organizationService.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        
                        row.data = data;
                        
                        model.add(row);
                    }
                    
                    shippedToName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }
            
        });

        processedDate = (CalendarLookUp)def.getWidget(ShippingMeta.getProcessedDate());
        addScreenHandler(processedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                processedDate.setValue(manager.getShipping().getProcessedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getShipping().setProcessedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                processedDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                shippedToAddressMultipleUnit.enable(false);
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
                processedById.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                shippedToAddressStreetAddress.enable(false);
                shippedToAddressStreetAddress.setQueryMode(false);
            }
        });

        shippedMethodId = (Dropdown)def.getWidget(ShippingMeta.getShippedMethodId());
        addScreenHandler(shippedMethodId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shippedMethodId.setSelection(manager.getShipping().getShippedMethodId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setShippedMethodId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedMethodId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                shippedToAddressCity.enable(false);
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
                shippedToAddressState.enable(false);
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
                shippedToAddressZipCode.enable(false);
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
                            Window.alert("Error: Order call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById((entry == null) ? null : ((IdNameVO)entry).getId());
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
    }    
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // order status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("shipping_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        statusId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("order_ship_from"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        shippedFromId.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("shipping_method"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        shippedMethodId.setModel(model);
        
        try {
            status_processed  = DictionaryCache.getIdFromSystemName("shipping_status_processed");
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
        
        setFocus(statusId);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() { 
        ShippingViewDO data;
        Datetime now;
        
        try {
            now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            Window.alert("Shipping Add Datetime: " +e.getMessage());
            return;
        }
        
        manager = ShippingManager.getInstance();
        data = manager.getShipping();
        data.setStatusId(status_processed);
        data.setProcessedDate(now);
        data.setProcessedBy(OpenELIS.security.getSystemUserName());
        
        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(statusId);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(statusId);            
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
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
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
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
                Window.alert("commitUpdate(): " + e.getMessage());
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
            Window.alert(e.getMessage());
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
            Window.alert(e.getMessage());
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
            case ITEM:
                itemTab.draw();
                break;                   
            case SHIP_NOTE:
                noteTab.draw();
                break;   
        }
    }
}

//extends OpenELISScreenForm<ShippingForm, Query<TableDataRow<Integer>>> implements ClickListener, TableManager, ChangeListener, TabListener{
/*
    public enum Action {Commited, Aborted}
    private CommandListener commandTarget;
    private Integer shipFromId, shipToId;
    private String shipToText, multUnitText, streetAddressText, cityText, stateText, zipCodeText;
    private AppButton removeRowButton;
    private TextBox shippedToAptSuite, shippedToAddress, shippedToCity, shippedToState, shippedToZipCode;
    private AutoComplete shippedToDropdown;
    private TableWidget itemsTable, trackingNumbersTable;
    private Dropdown status, shippedFrom, shippedMethod;
    private TableDataModel<TableDataRow<Integer>> itemsShippedModel, checkedOrderIds;
    private ShippingDataService data;
    private ScreenTabPanel tabPanel;
    private boolean closeOnCommitAbort = false;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    private CommandListener listener;
    private KeyListManager keyList = new KeyListManager();
    
    public ShippingScreen() {                
        super("org.openelis.modules.shipping.server.ShippingService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new ShippingForm());
    }
    
    public void setShippingData(ShippingDataService data){
        this.data = data;
        setCloseOnCommitAbort(true);
    }
    
    public void setTarget(CommandListener target){
        this.commandTarget = target;
    }
    
    private void setCloseOnCommitAbort(boolean close){
        closeOnCommitAbort = close;
    }
    
    public void loadShippingScreenFromData(){
        this.shipFromId = data.getShipFromId();
        this.shipToId = data.getShipToId();
        this.shipToText = data.getShipToText();   
        this.multUnitText = data.getMultUnitText();
        this.streetAddressText = data.getStreetAddressText();
        this.cityText = data.getCityText();
        this.stateText = data.getStateText();
        this.zipCodeText = data.getZipCodeText();
        this.itemsShippedModel = data.getItemsShippedModel();

    }
    
    
    public void clearShippingData(){
        this.shipFromId = null;
        this.shipToId = null;
        this.shipToText = null;   
        this.multUnitText = null;
        this.streetAddressText = null;
        this.cityText = null;
        this.stateText = null;
        this.zipCodeText = null;
        this.itemsShippedModel = null;
    }
    
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        
        if(sender == shippedToDropdown){
            if(shippedToDropdown.getSelections().size() > 0){
                TableDataRow selectedRow = shippedToDropdown.getSelections().get(0);
                
                //load address
                shippedToAddress.setText((String)((StringObject)selectedRow.getCells().get(1)).getValue());
                //load city
                shippedToCity.setText((String)((StringObject)selectedRow.getCells().get(2)).getValue());
                //load state
                shippedToState.setText((String)((StringObject)selectedRow.getCells().get(3)).getValue());
                
                ShippingShipToKey hiddenData = (ShippingShipToKey)selectedRow.getData();
                //load apt/suite
                shippedToAptSuite.setText(hiddenData.aptSuite);
                //load zipcode
                shippedToZipCode.setText(hiddenData.zipCode);
            }else{
                shippedToAddress.setText("");
                shippedToCity.setText("");
                shippedToState.setText("");
                shippedToAptSuite.setText("");
                shippedToZipCode.setText("");
            }
        }   
    }
    
    public void afterDraw(boolean success) {
        //shipped to address fields
        shippedToAptSuite  = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
        shippedToAddress = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        shippedToCity = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity());
        shippedToState = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getState());
        shippedToZipCode = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        
        shippedToDropdown = (AutoComplete)getWidget(ShippingMeta.ORGANIZATION_META.getName());

        removeRowButton = (AppButton)getWidget("removeRowButton");
        
        itemsTable = (TableWidget)getWidget("itemsTable");
        itemsTable.model.enableAutoAdd(false);
        
        trackingNumbersTable = (TableWidget)getWidget("trackingNumbersTable");
        trackingNumbersTable.model.enableAutoAdd(false);

        tabPanel = (ScreenTabPanel)widgets.get("shippingTabPanel");
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        status = (Dropdown)getWidget(ShippingMeta.getStatusId()); 
        shippedFrom = (Dropdown)getWidget(ShippingMeta.getShippedFromId());
        shippedMethod = (Dropdown)getWidget(ShippingMeta.getShippedMethodId());
        
        commitAddChain.add(afterCommit);
        
        super.afterDraw(success);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("shippingStatus");
        model = getDictionaryIdEntryList(cache);
        status.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("shipFrom");
        model = getDictionaryIdEntryList(cache);
        shippedFrom.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("shippingMethod");
        model = getDictionaryIdEntryList(cache);
        shippedMethod.setModel(model);
        
        if(data != null)
            add();
    }
    
    protected SyncCallback afterCommit = new SyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            trackingNumbersTable.model.enableAutoAdd(false);
            
            if(commandTarget != null)
                commandTarget.performCommand(Action.Commited, this);
            
            if(closeOnCommitAbort)
                window.close();
        }
    };
    
    public void add() {
       super.add();
       
       window.setBusy();
       
       FieldType[] args = new FieldType[0]; 
         
       //ShippingRPC srpc = new ShippingRPC();
       //srpc.key = form.key;
       //srpc.form = form.form;
       
       
       screenService.call("getAddAutoFillValues", form, new AsyncCallback<ShippingForm>(){
           public void onSuccess(ShippingForm result){    
               //set the values in the rpc
               form.statusId.setValue(result.statusId.getValue());
               form.processedDate.setValue(result.processedDate.getValue());
               form.processedBy.setValue(result.processedBy.getValue());
               form.systemUserId = result.systemUserId;
               //rpc.form.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.get(1)).getValue());
               //rpc.form.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.get(2)).getValue());
               //rpc.form.setFieldValue("systemUserId", ((NumberField)set.get(3)).getValue());
                      
               if(data != null)
                   initScreen();
               
               loadScreen();
               
               trackingNumbersTable.model.enableAutoAdd(true);
               
               window.clearStatus();
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
       
       ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
    }
    
    
    
    public void initScreen(){
        //add();
        
        loadShippingScreenFromData();
        
        //set the values after the screen is in add mode
        form.shippedFromId.setValue(new TableDataRow<Integer>(shipFromId));
        
        if(shipToId != null){
            TableDataModel<TableDataRow<Integer>> shipToModel = new TableDataModel<TableDataRow<Integer>>();
            shipToModel.add(new TableDataRow<Integer>(shipToId,new StringObject(shipToText)));
            form.organization.setModel(shipToModel);
            form.organization.setValue(shipToModel.get(0));
        }
        
        form.multipleUnit.setValue(multUnitText);
        form.streetAddress.setValue(streetAddressText);
        form.city.setValue(cityText);
        form.state.setValue(stateText);
        form.zipcode.setValue(zipCodeText);
        loadItemsShippedTableFromModel(itemsShippedModel);
        form.numberOfPackages.setValue(new Integer(1));
        data = null;
    }
    
    public void query() {
        super.query();
        removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        status.setFocus(true);
    }
    
    public void update() {
        super.update();
        ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
        trackingNumbersTable.model.enableAutoAdd(true);
    }
    
    
    public void abort() {
        trackingNumbersTable.model.enableAutoAdd(false);
        super.abort();
        
        if(commandTarget != null)
            commandTarget.performCommand(Action.Aborted, this);
        
        if(closeOnCommitAbort)
            window.close();
    }
    
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return ((DataObject)addRow.getCells().get(0)).getValue() != null && !((DataObject)addRow.getCells().get(0)).getValue().equals(0);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE)           
            return true;
        return false;
    }
    
    //
    //end table manager methods
    //
    
    private void loadItemsShippedTableFromModel(TableDataModel<TableDataRow<Integer>> model){
        itemsTable.model.clear();
        for(int i=0; i<model.size(); i++){
            TableDataRow<Integer> set = model.get(i);
            
            TableDataRow<Integer> tableRow = itemsTable.model.createRow();
            
            tableRow.getCells().get(0).setValue(set.getCells().get(0).getValue());
            tableRow.getCells().get(1).setValue(((DropDownField)set.getCells().get(1)).getTextValue());
            
            tableRow.setData(set.getData());
            
            itemsTable.model.addRow(tableRow);
        }
        
        if(model.size() > 0)
            itemsTable.model.refresh();
    }
 
    private void onRemoveRowButtonClick() {
        int selectedRow = trackingNumbersTable.model.getSelectedIndex();
        
        if (selectedRow > -1 && trackingNumbersTable.model.numRows() > 0) 
            trackingNumbersTable.model.deleteRow(selectedRow);
        
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if(state != State.QUERY){
            if (tabIndex == 0 && !form.shippingItemsForm.load)
                fillShippingItems();
                
            else if (tabIndex == 1 && !form.shippingNotesForm.load) 
                fillOrderShippingNotes();
              
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) { 
        form.shippingTabPanel = tabPanel.getSelectedTabKey();
    }
    
    private void fillShippingItems() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();
        
        //ShippingItemsForm sirpc = new ShippingItemsRPC();
        form.shippingItemsForm.entityKey = form.entityKey;
        //sirpc.form =form.form.shippingItemsForm;

        screenService.call("loadShippingItems", form.shippingItemsForm, new AsyncCallback<ShippingItemsForm>() {
            public void onSuccess(ShippingItemsForm result) {
                form.shippingItemsForm = result;
                load(form.shippingItemsForm);

                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillOrderShippingNotes() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

        // prepare the argument list for the getObject function
        //ShippingNotesRPC snrpc = new ShippingNotesRPC();
        form.shippingNotesForm.entityKey = form.entityKey;
        //snrpc.form = form.form.shippingNotesForm;
        
        screenService.call("loadOrderShippingNotes", form.shippingNotesForm, new AsyncCallback<ShippingNotesForm>() {
            public void onSuccess(ShippingNotesForm result) {
                form.shippingNotesForm = result;
                load(form.shippingNotesForm);
                
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
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
    */