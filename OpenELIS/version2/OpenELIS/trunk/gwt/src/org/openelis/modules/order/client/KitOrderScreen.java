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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
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
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AuxDataTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class KitOrderScreen extends Screen {

    private OrderManager          manager;
    private SecurityModule        security;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private ItemTab               itemTab;
    private FillTab               fillTab;
    private ShipNoteTab           shipNoteTab;
    private CustomerNoteTab       custNoteTab;
    private ReportToBillToTab     reportToBillToTab;
    private AuxDataTab            auxDataTab;
    private ContainerTab          containerTab;
    private Tabs                  tab;

    private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    private MenuItem              duplicate, orderHistory, itemHistory, testHistory, containerHistory;
    private TextBox               id, neededInDays, requestedBy, organizationAttention,organizationAddressMultipleUnit,
                                  organizationAddressStreetAddress, organizationAddressCity,
                                  organizationAddressState, organizationAddressZipCode;
    private CalendarLookUp        orderedDate;
    private Dropdown<Integer>     statusId, shipFromId, costCenterId;
    private AutoComplete<Integer> organizationName;
    private AutoComplete<String>  description;
    private TabPanel              tabPanel;
    private Integer               status_pending;
    private String                descQuery;
    
    protected ScreenService       userService, organizationService;

    private enum Tabs {
        ITEM, FILL, SHIP_NOTE, CUSTOMER_NOTE, REPORT_TO, CONTAINER, AUX_DATA
    };
    
    
    public KitOrderScreen() throws Exception {
        super((ScreenDefInt)GWT.create(KitOrderDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        userService = new ScreenService("controller?service=org.openelis.server.SystemUserService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        security = OpenELIS.security.getModule("order");
        if (security == null)
            throw new SecurityException("screenPermException", "Kit Order Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    

    private void postConstructor() {
        tab = Tabs.ITEM;
        manager = OrderManager.getInstance();

        try {
            DictionaryCache.preloadByCategorySystemNames("order_status", "cost_centers",
                                                         "inventory_store", "inventory_unit",
                                                         "order_ship_from","sample_container",
                                                         "type_of_sample");
        } catch (Exception e) {
            Window.alert("OrderSreen: missing dictionary entry; " + e.getMessage());
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
        
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        orderHistory = (MenuItem)def.getWidget("orderHistory");
        addScreenHandler(orderHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                orderHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        itemHistory = (MenuItem)def.getWidget("itemHistory");
        addScreenHandler(itemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                itemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        testHistory = (MenuItem)def.getWidget("testHistory");
        addScreenHandler(testHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                testHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        containerHistory = (MenuItem)def.getWidget("containerHistory");
        addScreenHandler(containerHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                containerHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        //
        // screen fields
        //
        id = (TextBox)def.getWidget(OrderMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getOrder().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neededInDays = (TextBox)def.getWidget(OrderMeta.getNeededInDays());
        addScreenHandler(neededInDays, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                neededInDays.setValue(manager.getOrder().getNeededInDays());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setNeededInDays(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neededInDays.enable(EnumSet.of(State.QUERY,State.ADD, State.UPDATE).contains(event.getState()));
                neededInDays.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shipFromId = (Dropdown)def.getWidget(OrderMeta.getShipFromId());
        addScreenHandler(shipFromId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shipFromId.setSelection(manager.getOrder().getShipFromId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setShipFromId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shipFromId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                shipFromId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        organizationAttention = (TextBox)def.getWidget(OrderMeta.getOrganizationAttention());
        addScreenHandler(organizationAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                organizationAttention.setValue(manager.getOrder().getOrganizationAttention());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setOrganizationAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAttention.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                organizationAttention.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName = (AutoComplete)def.getWidget(OrderMeta.getOrganizationName());
        addScreenHandler(organizationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;                                    
                
                data = manager.getOrder();
                if (data.getOrganization() != null) {
                    organizationName.setSelection(data.getOrganizationId(),
                                                  data.getOrganization().getName());
                } else {
                    organizationName.setSelection(null,"");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;
                
                if(organizationName.getSelection() != null) {
                    data = (OrganizationDO) organizationName.getSelection().data;
                    
                    manager.getOrder().setOrganizationId(data.getId());
                    manager.getOrder().setOrganization(data);
                    
                    organizationAddressMultipleUnit.setValue(data.getAddress().getMultipleUnit());
                    organizationAddressStreetAddress.setValue(data.getAddress().getStreetAddress());
                    organizationAddressCity.setValue(data.getAddress().getCity());
                    organizationAddressState.setValue(data.getAddress().getState());
                    organizationAddressZipCode.setValue(data.getAddress().getZipCode());
                } else {
                    manager.getOrder().setOrganizationId(null);
                    manager.getOrder().setOrganization(null);
                    
                    organizationAddressMultipleUnit.setValue(null);
                    organizationAddressStreetAddress.setValue(null);
                    organizationAddressCity.setValue(null);
                    organizationAddressState.setValue(null);
                    organizationAddressZipCode.setValue(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                organizationName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        
        organizationName.addGetMatchesHandler(new GetMatchesHandler() {
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
                    organizationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            }
            
        });

        statusId = (Dropdown)def.getWidget(OrderMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getOrder().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressMultipleUnit());
        addScreenHandler(organizationAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();
                if (data.getOrganization() != null) 
                    organizationAddressMultipleUnit.setValue(data.getOrganization().getAddress().getMultipleUnit());
                else 
                    organizationAddressMultipleUnit.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressMultipleUnit.enable(false);
                organizationAddressMultipleUnit.setQueryMode(false);
            }
        });

        orderedDate = (CalendarLookUp)def.getWidget(OrderMeta.getOrderedDate());
        addScreenHandler(orderedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                orderedDate.setValue(manager.getOrder().getOrderedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getOrder().setOrderedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderedDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                orderedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressStreetAddress());
        addScreenHandler(organizationAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressStreetAddress.setValue(data.getOrganization().getAddress().getStreetAddress());
                else 
                    organizationAddressStreetAddress.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressStreetAddress.enable(false);
                organizationAddressStreetAddress.setQueryMode(false);
            }
        });

        requestedBy = (TextBox)def.getWidget(OrderMeta.getRequestedBy());
        addScreenHandler(requestedBy, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                requestedBy.setValue(manager.getOrder().getRequestedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setRequestedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                requestedBy.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                requestedBy.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressCity = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressCity());
        addScreenHandler(organizationAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressCity.setValue(data.getOrganization().getAddress().getCity());
                else 
                    organizationAddressCity.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
             // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressCity.enable(false);
                organizationAddressCity.setQueryMode(false);
            }
        });

        costCenterId = (Dropdown)def.getWidget(OrderMeta.getCostCenterId());
        addScreenHandler(costCenterId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                costCenterId.setSelection(manager.getOrder().getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                costCenterId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                costCenterId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        organizationAddressState = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressState());
        addScreenHandler(organizationAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressState.setValue(data.getOrganization().getAddress().getState());
                else 
                    organizationAddressState.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressState.enable(false);
                organizationAddressState.setQueryMode(false);
            }
        });

        organizationAddressZipCode = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressZipCode());
        addScreenHandler(organizationAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;
                
                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressZipCode.setValue(data.getOrganization().getAddress().getZipCode());
                else
                    organizationAddressZipCode.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressZipCode.enable(false);
                organizationAddressZipCode.setQueryMode(false);
            }
        });

        description = (AutoComplete)def.getWidget(OrderMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                String desc;
                
                desc = manager.getOrder().getDescription();
                description.setSelection(desc, desc);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
                descQuery = null;
            }
        });        

        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ArrayList<TableDataRow> model;                
                QueryFieldUtil parser;
                IdNameVO data;
                ArrayList<IdNameVO> list;  
                String match;
                
                list = null;
                match = event.getMatch();
                window.setBusy();
                
                try {
                    model = new ArrayList<TableDataRow>();
                    
                    row = new TableDataRow(match, match);
                    model.add(row);
                    
                    if(descQuery == null || (!(match.indexOf(descQuery) == 0))) {
                        parser = new QueryFieldUtil();
                        parser.parse(match);
                        list = service.callList("fetchByDescription", parser.getParameter().get(0));
                        for (int i = 0; i < list.size(); i++ ) {
                            data = list.get(i);
                            row = new TableDataRow(data.getName(), data.getName());                  
                            model.add(row);
                        } 
                        
                        if(list.size() == 0)
                            descQuery = match;
                    }                                                                                    
                    
                    description.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
                
            
            }

        });

        //
        // tabs
        //
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

        fillTab = new FillTab(def, window);
        addScreenHandler(fillTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                fillTab.setManager(manager);
                if (tab == Tabs.FILL)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                fillTab.setState(event.getState());
            }
        });

        shipNoteTab = new ShipNoteTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(shipNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                shipNoteTab.setManager(manager);
                if (tab == Tabs.SHIP_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shipNoteTab.setState(event.getState());
            }
        });

        custNoteTab = new CustomerNoteTab(def, window, "customerNotesPanel", "editNoteButton");
        addScreenHandler(custNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                custNoteTab.setManager(manager);
                if (tab == Tabs.CUSTOMER_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                custNoteTab.setState(event.getState());
            }
        });
        
        reportToBillToTab = new ReportToBillToTab(def, window);
        addScreenHandler(reportToBillToTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                reportToBillToTab.setManager(manager);
                if (tab == Tabs.REPORT_TO)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToBillToTab.setState(event.getState());
            }
        });
        
        auxDataTab = new AuxDataTab(def, window);
        addScreenHandler(auxDataTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTab.setManager(manager);
                if (tab == Tabs.AUX_DATA)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });
        
        containerTab  = new ContainerTab(def, window);
        addScreenHandler(containerTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                containerTab.setManager(manager);
                if (tab == Tabs.CONTAINER)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(Query query) {
                QueryData field;

                window.setBusy(consts.get("querying"));
                // this screen should only query for kit orders
                field = new QueryData();
                field.key = OrderMeta.getType();
                field.query = OrderManager.TYPE_KIT;
                field.type = QueryData.Type.STRING;
                query.setFields(field);

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
                            window.setError(consts.get("noMoreRecordInDir"));
                        } else {
                            Window.alert("Error: Order call query failed; " +
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
                        model.add(new TableDataRow(entry.getId(), entry.getId(), entry.getName()));
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
                field.key = OrderMeta.getId();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.INTEGER;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
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
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // order status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("order_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        statusId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("cost_centers"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        costCenterId.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("order_ship_from"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        
        shipFromId.setModel(model);
        
        try {
            status_pending = DictionaryCache.getIdFromSystemName("order_status_pending");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    /*
     * basic button methods
     */

    protected void query() {
        manager = OrderManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        itemTab.draw();
        fillTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();
        reportToBillToTab.draw();        
        
        setFocus(id);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        Datetime now;
        OrderViewDO data;

        try {
            now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            Window.alert("OrderAdd Datetime: " +e.getMessage());
            return;
        }
        
        manager = OrderManager.getInstance();
        data = manager.getOrder();
        data.setStatusId(status_pending);
        data.setOrderedDate(now);
        data.setRequestedBy(OpenELIS.security.getSystemUserName());
        data.setType(OrderManager.TYPE_KIT);

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(neededInDays);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            if (! status_pending.equals(manager.getOrder().getStatusId())) {
                Window.alert(consts.get("orderStatusNotPendingForUpdate"));
                manager = manager.abortUpdate();
            } else {
                setState(State.UPDATE);
                DataChangeEvent.fire(this);
                setFocus(neededInDays);
            }
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
            QueryData field;

            // this screen should only query for internal orders
            field = new QueryData();
            field.key = OrderMeta.getType();
            field.query = OrderManager.TYPE_KIT;
            field.type = QueryData.Type.STRING;

            query = new Query();
            query.setFields(getQueryFields());
            query.setFields(field);
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
    
    protected void duplicate() {
        Datetime now;
        OrderViewDO data;
        
        try {
            manager = OrderManager.fetchById(manager.getOrder().getId());

            try {
                now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
            } catch (Exception e) {
                Window.alert("OrderAdd Datetime: " +e.getMessage());
                return;
            }
            
            data = manager.getOrder();
            data.setStatusId(status_pending);
            data.setOrderedDate(now);
            data.setRequestedBy(OpenELIS.security.getSystemUserName());
            data.setType(OrderManager.TYPE_KIT);           
            
            itemTab.setManager(manager);            
            //fillTab.setManager(manager);
            shipNoteTab.setManager(manager);
            custNoteTab.setManager(manager);
            reportToBillToTab.setManager(manager);  
            containerTab.setManager(manager);      
            auxDataTab.setManager(manager);            
            
            
            manager.getItems();
            //manager.getFills();
            manager.getShippingNotes();       
            manager.getCustomerNotes();
            manager.getTests();
            manager.getContainers();
            manager.getAuxData();
            
            clearKeys();
            
            itemTab.draw();
            //fillTab.draw();
            shipNoteTab.draw();
            custNoteTab.draw();
            reportToBillToTab.draw();  
            containerTab.draw();
            auxDataTab.draw();
            
            setState(State.ADD);
            DataChangeEvent.fire(this);

            setFocus(neededInDays);
            window.setDone(consts.get("enterInformationPressCommit"));
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }                  
    }
    
    protected void orderHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getOrder().getId(), manager.getOrder().getId().toString());
        HistoryScreen.showHistory(consts.get("orderHistory"),
                                  ReferenceTable.ORDER, hist);                
    }
    
    protected void itemHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrderItemManager man;
        OrderItemViewDO data;

        try {
            man = manager.getItems();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getInventoryItemName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orderItemHistory"),
                                  ReferenceTable.ORDER_ITEM, refVoList);
    }
    
    protected void testHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrderTestManager man;
        OrderTestViewDO data;

        try {
            man = manager.getTests();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getTestAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getReferenceName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orderTestHistory"),
                                  ReferenceTable.ORDER_TEST, refVoList);
        
    }
    
    protected void containerHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrderContainerManager man;
        OrderContainerDO data;
        DictionaryDO dict;

        try {
            man = manager.getContainers();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getContainerAt(i);
                dict = DictionaryCache.getEntryFromId(data.getContainerId());
                refVoList[i] = new IdNameVO(data.getId(), dict.getEntry());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orderContainerHistory"),
                                  ReferenceTable.ORDER_CONTAINER, refVoList);
        
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = OrderManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case ITEM:
                        manager = OrderManager.fetchWithItems(id);
                        break;
                    case FILL:
                        manager = OrderManager.fetchWithFills(id);
                        break;
                    case SHIP_NOTE:
                    case CUSTOMER_NOTE:
                        manager = OrderManager.fetchWithNotes(id);
                        break;
                    case REPORT_TO:
                        manager = OrderManager.fetchById(id);
                        break;
                    case CONTAINER:
                        manager = OrderManager.fetchWithTestsAndContainers(id);
                        break;     
                    case AUX_DATA:
                        manager = OrderManager.fetchById(id);
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
    
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> returnList;
        ArrayList<IdNameVO> auxFields;
        QueryData queryData;
        IdNameVO idName;
        
        returnList = super.getQueryFields();
        
        //add aux data values if necessary
        auxFields = auxDataTab.getAuxQueryFields();
        
        if(auxFields.size() > 0){
            //add ref table
            queryData = new QueryData();
            queryData.key = OrderMeta.getAuxDataReferenceTableId();
            queryData.type = QueryData.Type.INTEGER;
            queryData.query = String.valueOf(ReferenceTable.ORDER);
            returnList.add(queryData);
            
            //add aux fields
            for(int i=0; i<auxFields.size(); i++){
                idName = auxFields.get(i);
                
                //aux data id
                queryData = new QueryData();
                queryData.key = OrderMeta.getAuxDataAuxFieldId();
                queryData.type = QueryData.Type.INTEGER;
                queryData.query = String.valueOf(idName.getId());
                returnList.add(queryData);
                
                //aux data value
                queryData = new QueryData();
                queryData.key = OrderMeta.getAuxDataValue();
                queryData.type = QueryData.Type.STRING;
                queryData.query = idName.getName();
                returnList.add(queryData);
            }
        }
        
        return returnList;
    }

    private void drawTabs() {
        switch (tab) {
            case ITEM:
                itemTab.draw();
                break;
            case FILL:
                fillTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;
            case CUSTOMER_NOTE:
                custNoteTab.draw();
                break;
            case REPORT_TO:
                reportToBillToTab.draw();
                break;   
            case CONTAINER:
                containerTab.draw();
                break;    
            case AUX_DATA:
                auxDataTab.draw();
                break;    
        }
    }
    
    private void clearKeys() {
        OrderItemManager iman;
        NoteManager snman, cnman;
        OrderTestManager otman;
        OrderContainerManager ocman;
        AuxDataManager adman;
        OrderItemViewDO item;
        NoteViewDO snote, cnote;
        OrderTestViewDO test;
        OrderContainerDO container;
        AuxDataViewDO aux;
        int i, count;
        
        manager.getOrder().setId(null);
        
        try {
            iman = manager.getItems();
            count = iman.count();
            
            for(i = 0; i < count; i++) {
                item = iman.getItemAt(i);
                item.setId(null);
                item.setOrderId(null);
            } 
            
            snman = manager.getShippingNotes();
            count = snman.count();
            
            for(i = 0; i < count; i++) {
                snote = snman.getNoteAt(i);
                snote.setId(null);
                snote.setReferenceId(null);
                snote.setReferenceTableId(null);
            } 
            
            cnman = manager.getCustomerNotes();
            count = cnman.count();
            
            for(i = 0; i < count; i++) {
                cnote = cnman.getNoteAt(i);
                cnote.setId(null);
                cnote.setReferenceId(null);
                cnote.setReferenceTableId(null);
            } 
            
            otman = manager.getTests();
            count = otman.count();
            
            for(i = 0; i < count; i++) {
                test = otman.getTestAt(i);
                test.setId(null);
                test.setOrderId(null);
            }
            
            ocman = manager.getContainers();
            count = ocman.count();
            
            for(i = 0; i < count; i++) {
                container = ocman.getContainerAt(i);
                container.setId(null);
                container.setOrderId(null);
            }
            
            adman = manager.getAuxData();
            count = adman.count();
            
            for(i = 0; i < count; i++) {
                aux = adman.getAuxDataAt(i);
                aux.setId(null);
                aux.setReferenceId(null);
                aux.setReferenceTableId(null);
            }
            
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }      
    }
}