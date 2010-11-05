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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
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
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VendorOrderScreen extends Screen {

    private OrderManager     manager;
    private ModulePermission userPermission;

    private ButtonGroup      atoz;
    private ScreenNavigator  nav;

    private Calendar         orderedDate;
    private Dropdown<Integer> statusId, costCenterId;
    private TextBox           id, neededInDays, organizationAttention,
                    organizationAddressMultipleUnit, requestedBy, organizationAddressStreetAddress,
                    organizationAddressCity, externalOrderNumber, organizationAddressState,
                    organizationAddressZipCode;
    private ItemTab           itemTab;
    private FillTab           fillTab;
    private ShipNoteTab       shipNoteTab;
    private Tabs              tab;

    private AutoComplete      organizationName;
    private Button            queryButton, previousButton, nextButton, addButton, updateButton,
                    commitButton, abortButton;
    private MenuItem          duplicate, orderHistory, itemHistory;
    private TabPanel          tabPanel;
    private Integer           status_pending;

    protected ScreenService   organizationService;

    private enum Tabs {
        ITEM, FILL, SHIP_NOTE
    };

    public VendorOrderScreen() throws Exception {
        super((ScreenDefInt)GWT.create(VendorOrderDef.class));
        service = new ScreenService(
                                    "controller?service=org.openelis.modules.order.server.OrderService");
        organizationService = new ScreenService(
                                                "controller?service=org.openelis.modules.organization.server.OrganizationService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("vendororder");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Vendor Order Screen");

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
                                                         "inventory_store");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("OrderSreen: missing dictionary entry; " + e.getMessage());
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
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
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
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
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
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
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
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        orderHistory = (MenuItem)def.getWidget("orderHistory");
        addScreenHandler(orderHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                orderHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        itemHistory = (MenuItem)def.getWidget("itemHistory");
        addScreenHandler(itemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                itemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
                id.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
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
                neededInDays.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                neededInDays.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(OrderMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(manager.getOrder().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
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
                organizationAttention.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
                organizationAttention.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName = (AutoComplete)def.getWidget(OrderMeta.getOrganizationName());
        addScreenHandler(organizationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null) {
                    organizationName.setValue(data.getOrganizationId(), data.getOrganization()
                                                                                .getName());
                } else {
                    organizationName.setValue(null, "");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;

                if (organizationName.getValue() != null) {
                    data = (OrganizationDO)organizationName.getSelectedItem().getData();

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
                organizationName.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                organizationName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = organizationService.callList("fetchByIdOrName", parser.getParameter()
                                                                                 .get(0));
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
                    organizationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();

            }

        });

        orderedDate = (Calendar)def.getWidget(OrderMeta.getOrderedDate());
        addScreenHandler(orderedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                orderedDate.setValue(manager.getOrder().getOrderedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getOrder().setOrderedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderedDate.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                orderedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressMultipleUnit());
        addScreenHandler(organizationAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressMultipleUnit.setValue(data.getOrganization()
                                                                 .getAddress()
                                                                 .getMultipleUnit());
                else
                    organizationAddressMultipleUnit.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressMultipleUnit.setEnabled(false);
                organizationAddressMultipleUnit.setQueryMode(false);
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
                requestedBy.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                requestedBy.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressStreetAddress());
        addScreenHandler(organizationAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressStreetAddress.setValue(data.getOrganization()
                                                                  .getAddress()
                                                                  .getStreetAddress());
                else
                    organizationAddressStreetAddress.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressStreetAddress.setEnabled(false);
                organizationAddressStreetAddress.setQueryMode(false);
            }
        });

        costCenterId = (Dropdown)def.getWidget(OrderMeta.getCostCenterId());
        addScreenHandler(costCenterId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                costCenterId.setValue(manager.getOrder().getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                costCenterId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                costCenterId.setQueryMode(event.getState() == State.QUERY);
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
                organizationAddressCity.setEnabled(false);
                organizationAddressCity.setQueryMode(false);
            }
        });

        externalOrderNumber = (TextBox)def.getWidget(OrderMeta.getExternalOrderNumber());
        addScreenHandler(externalOrderNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                externalOrderNumber.setValue(manager.getOrder().getExternalOrderNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setExternalOrderNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                externalOrderNumber.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
                externalOrderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressState = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressState());
        addScreenHandler(organizationAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressState.setValue(data.getOrganization()
                                                          .getAddress()
                                                          .getState());
                else
                    organizationAddressState.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressState.setEnabled(false);
                organizationAddressState.setQueryMode(false);
            }
        });

        organizationAddressZipCode = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressZipCode());
        addScreenHandler(organizationAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressZipCode.setValue(data.getOrganization()
                                                            .getAddress()
                                                            .getZipCode());
                else
                    organizationAddressZipCode.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressZipCode.setEnabled(false);
                organizationAddressZipCode.setQueryMode(false);
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

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(Query query) {
                QueryData field;

                window.setBusy(consts.get("querying"));
                // this screen should only query for vendor orders
                field = new QueryData();
                field.key = OrderMeta.getType();
                field.query = OrderManager.TYPE_VENDOR;
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
                            window.setError("No more records in this direction");
                        } else {
                            com.google.gwt.user.client.Window.alert("Error: Order call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getId(), entry.getName()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                atoz.setEnabled(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = OrderMeta.getId();
                field.query = ((Button)event.getSource()).getAction();
                field.type = QueryData.Type.INTEGER;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        // order status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_status");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        statusId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenterId.setModel(model);

        try {
            status_pending = DictionaryCache.getIdFromSystemName("order_status_pending");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
        shipNoteTab.draw();

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
            now = org.openelis.gwt.screen.Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("OrderAdd Datetime: " + e.getMessage());
            return;
        }

        manager = OrderManager.getInstance();
        data = manager.getOrder();
        data.setStatusId(status_pending);
        data.setOrderedDate(now);
        data.setRequestedBy(OpenELIS.getSystemUserPermission().getLoginName());
        data.setType(OrderManager.TYPE_VENDOR);

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(neededInDays);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            if ( !status_pending.equals(manager.getOrder().getStatusId())) {
                com.google.gwt.user.client.Window.alert(consts.get("orderStatusNotPendingForUpdate"));
                manager = manager.abortUpdate();
            } else {
                setState(State.UPDATE);
                DataChangeEvent.fire(this);
                setFocus(neededInDays);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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

            // this screen should only query for vendor orders
            field = new QueryData();
            field.key = OrderMeta.getType();
            field.query = OrderManager.TYPE_VENDOR;
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
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
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

    protected void duplicate() {
        Datetime now;
        OrderViewDO data;

        try {
            manager = OrderManager.fetchById(manager.getOrder().getId());

            try {
                now = org.openelis.gwt.screen.Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("OrderAdd Datetime: " + e.getMessage());
                return;
            }

            data = manager.getOrder();
            data.setStatusId(status_pending);
            data.setOrderedDate(now);
            data.setRequestedBy(OpenELIS.getSystemUserPermission().getLoginName());
            data.setType(OrderManager.TYPE_VENDOR);

            itemTab.setManager(manager);
            // fillTab.setManager(manager);
            shipNoteTab.setManager(manager);

            manager.getItems();
            // manager.getFills();
            manager.getShippingNotes();

            clearKeys();

            itemTab.draw();
            // fillTab.draw();
            shipNoteTab.draw();

            setState(State.ADD);
            DataChangeEvent.fire(this);

            setFocus(neededInDays);
            window.setDone(consts.get("enterInformationPressCommit"));
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }

    protected void orderHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrder().getId(), manager.getOrder().getId().toString());
        HistoryScreen.showHistory(consts.get("orderHistory"), ReferenceTable.ORDER, hist);
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orderItemHistory"), ReferenceTable.ORDER_ITEM,
                                  refVoList);
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
                        manager = OrderManager.fetchWithNotes(id);
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
            case FILL:
                fillTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;
        }
    }

    private void clearKeys() {
        OrderItemManager iman;
        OrderItemViewDO item;
        NoteViewDO note;
        NoteManager nman;
        int i, count;

        manager.getOrder().setId(null);

        try {
            iman = manager.getItems();
            count = iman.count();

            for (i = 0; i < count; i++ ) {
                item = iman.getItemAt(i);
                item.setId(null);
                item.setOrderId(null);
            }

            nman = manager.getShippingNotes();
            count = nman.count();

            for (i = 0; i < count; i++ ) {
                note = nman.getNoteAt(i);
                note.setId(null);
                note.setReferenceId(null);
                note.setReferenceTableId(null);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }
}
