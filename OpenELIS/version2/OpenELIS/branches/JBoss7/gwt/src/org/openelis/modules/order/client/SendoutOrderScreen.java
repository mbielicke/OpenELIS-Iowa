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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
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
import org.openelis.gwt.services.CalendarService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.ShippingManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.auxData.client.AuxDataUtil;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.report.orderRequestForm.client.OrderRequestFormReportScreen;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.SampleOrganizationUtility;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.modules.shipping.client.ShippingService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;

public class SendoutOrderScreen extends Screen {

    private OrderManager                 manager;
    private ModulePermission             userModulePermission;
    private SystemUserPermission         userPermission;

    private ButtonGroup                  atoz;
    private ScreenNavigator              nav;

    private OrganizationTab              organizationTab;
    private AuxDataTab                   auxDataTab;
    private TestTab                      testTab;
    private ContainerTab                 containerTab;
    private ItemTab                      itemTab;
    private ShipNoteTab                  shipNoteTab;
    private CustomerNoteTab              custNoteTab;
    private InternalNoteTab              internalNoteTab;
    private SampleNoteTab                sampleNoteTab;
    private RecurrenceTab                recurrenceTab;
    private FillTab                      fillTab;

    private Tabs                         tab;

    private AppButton                    queryButton, previousButton, nextButton,
                    addButton, updateButton, commitButton, abortButton;
    private MenuItem                     duplicate, shippingInfo, orderRequestForm,
                    orderHistory, organizationHistory, itemHistory, testHistory,
                    containerHistory;
    private TextBox                      id, neededInDays, numberOfForms, requestedBy,
                    organizationAttention, organizationAddressMultipleUnit,
                    organizationAddressStreetAddress, organizationAddressCity,
                    organizationAddressZipCode;
    private CalendarLookUp               orderedDate;
    private Dropdown<Integer>            status, shipFrom, costCenter;
    private Dropdown<String>             organizationAddressState;
    private AutoComplete<Integer>        organizationName;
    private AutoComplete<String>         description;
    private TabPanel                     tabPanel;

    private ShippingManager              shippingManager;
    private ShippingScreen               shippingScreen;
    private OrderRequestFormReportScreen requestformReportScreen;
    private TestContainerPopoutUtil      popoutUtil;

    private Integer                      statusCancelledId;

    private String                       descQuery;
    
    private enum Tabs {
        ORGANIZATION, AUX_DATA, TEST, CONTAINER, ITEM, SHIP_NOTE, CUSTOMER_NOTE,
        INTERNAL_NOTE, SAMPLE_NOTE, RECURRENCE, FILL
    };

    public SendoutOrderScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SendoutOrderDef.class));

        SendoutOrderScreenImpl();
    }

    public SendoutOrderScreen(ScreenWindow window) throws Exception {
        super((ScreenDefInt)GWT.create(SendoutOrderDef.class));
        this.window = window;

        SendoutOrderScreenImpl();
    }

    private void SendoutOrderScreenImpl() throws Exception {
        
        userPermission = UserCache.getPermission();
        userModulePermission = userPermission.getModule("sendoutorder");
        if (userModulePermission == null)
            throw new PermissionException("screenPermException", "Send-out Order Screen");

        /*
         * this is done here in order to make sure that if the screen is brought
         * up from some other screen then its widgets are initialized before the
         * constructor ends execution
         */
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

    private void postConstructor() {
        tab = Tabs.ORGANIZATION;
        manager = OrderManager.getInstance();

        try {
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "inventory_store",
                                           "inventory_unit",
                                           "laboratory_location",
                                           "sample_container",
                                           "type_of_sample",
                                           "aux_field_value_type");
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
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userModulePermission.hasSelectPermission());
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
                previousButton.enable(EnumSet.of(State.DISPLAY)
                                             .contains(event.getState()));
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
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userModulePermission.hasAddPermission());
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
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userModulePermission.hasUpdatePermission());
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
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                 userModulePermission.hasAddPermission());
            }
        });

        shippingInfo = (MenuItem)def.getWidget("shippingInfo");
        addScreenHandler(shippingInfo, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * this menu item is enabled here an not in onStateChange
                 * because for the cases where the data changes and the state
                 * remains the same, i.e. on going previous or next in the
                 * results returned by a query, StateChangeEvent doesn't get
                 * fired, so the widget doesn't get a chance to enable or
                 * disable itself appropriately
                 */
                shippingInfo.enable( (state == State.DISPLAY) &&
                                    Constants.dictionary().ORDER_STATUS_PROCESSED.equals(manager.getOrder()
                                                                                                .getStatusId()));
            }

            public void onClick(ClickEvent event) {
                shippingInfo();
            }
        });

        orderRequestForm = (MenuItem)def.getWidget("orderRequestForm");
        addScreenHandler(orderRequestForm, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                orderRequestForm();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderRequestForm.enable(EnumSet.of(State.DISPLAY)
                                               .contains(event.getState()));
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

        organizationHistory = (MenuItem)def.getWidget("organizationHistory");
        addScreenHandler(organizationHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                organizationHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationHistory.enable(EnumSet.of(State.DISPLAY)
                                                  .contains(event.getState()));
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
                containerHistory.enable(EnumSet.of(State.DISPLAY)
                                               .contains(event.getState()));
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
                neededInDays.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                neededInDays.setQueryMode(event.getState() == State.QUERY);
            }
        });

        numberOfForms = (TextBox)def.getWidget(OrderMeta.getNumberOfForms());
        addScreenHandler(numberOfForms, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                numberOfForms.setValue(manager.getOrder().getNumberOfForms());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setNumberOfForms(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                numberOfForms.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                numberOfForms.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shipFrom = (Dropdown)def.getWidget(OrderMeta.getShipFromId());
        addScreenHandler(shipFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                shipFrom.setSelection(manager.getOrder().getShipFromId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setShipFromId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shipFrom.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                shipFrom.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAttention = (TextBox)def.getWidget(OrderMeta.getOrganizationAttention());
        addScreenHandler(organizationAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                organizationAttention.setValue(manager.getOrder()
                                                      .getOrganizationAttention());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrder().setOrganizationAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAttention.enable(EnumSet.of(State.QUERY,
                                                        State.ADD,
                                                        State.UPDATE)
                                                    .contains(event.getState()));
                organizationAttention.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName = (AutoComplete)def.getWidget(OrderMeta.getOrganizationName());
        addScreenHandler(organizationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationName.setSelection(data.getOrganizationId(),
                                                  data.getOrganization().getName());
                else
                    organizationName.setSelection(null, "");
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;
                TableDataRow row;

                row = organizationName.getSelection();
                if (row != null && row.data != null) {
                    data = (OrganizationDO)row.data;

                    manager.getOrder().setOrganizationId(data.getId());
                    manager.getOrder().setOrganization(data);

                    organizationAddressMultipleUnit.setValue(data.getAddress()
                                                                 .getMultipleUnit());
                    organizationAddressStreetAddress.setValue(data.getAddress()
                                                                  .getStreetAddress());
                    organizationAddressCity.setValue(data.getAddress().getCity());
                    organizationAddressState.setValue(data.getAddress().getState());
                    organizationAddressZipCode.setValue(data.getAddress().getZipCode());

                    try {
                        showHoldRefuseWarning(data.getId(), data.getName());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        e.printStackTrace();
                    }
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
                organizationName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                organizationName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = OrganizationService.get().fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).setValue(data.getName());
                        row.cells.get(1).setValue(data.getAddress().getStreetAddress());
                        row.cells.get(2).setValue(data.getAddress().getCity());
                        row.cells.get(3).setValue(data.getAddress().getState());

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

        status = (Dropdown)def.getWidget(OrderMeta.getStatusId());
        addScreenHandler(status, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setSelection(manager.getOrder().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    /*
                     * only orders with no items can be set as "processed" here
                     */
                    if ( !Constants.dictionary().ORDER_STATUS_PROCESSED.equals(event.getValue()) ||
                        manager.getItems().count() == 0) {
                        manager.getOrder().setStatusId(event.getValue());
                    } else {
                        status.setValue(manager.getOrder().getStatusId());
                        Window.alert(consts.get("onlyProcessOrdersWithNoItems"));
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                boolean queryMode;
                Integer statusId;

                ArrayList<TableDataRow> model;

                statusId = manager.getOrder().getStatusId();

                if ( (event.getState() != State.ADD && event.getState() != State.UPDATE)) {
                    queryMode = event.getState() == State.QUERY;

                    /*
                     * no options are to be disabled in Query state
                     */
                    if (queryMode) {
                        for (TableDataRow r : status.getData())
                            r.enabled = true;
                    }

                    status.setQueryMode(queryMode);
                    status.enable(queryMode);
                    return;
                }

                model = status.getData();
                for (TableDataRow r : model) {
                    if (Constants.dictionary().ORDER_STATUS_PENDING.equals(r.key) ||
                        Constants.dictionary().ORDER_STATUS_ON_HOLD.equals(r.key))
                        r.enabled = true;
                    else if (Constants.dictionary().ORDER_STATUS_PROCESSED.equals(r.key))
                        /*
                         * the option for "Processed" is only enabled for an
                         * existing order and only if it is pending
                         */
                        r.enabled = manager.getOrder().getId() != null &&
                                    Constants.dictionary().ORDER_STATUS_PENDING.equals(statusId);
                    else if (Constants.dictionary().ORDER_STATUS_CANCELLED.equals(r.key))
                        /*
                         * the option for "Cancelled" is only enabled for an
                         * existing order and only if it is pending or on hold
                         */
                        r.enabled = manager.getOrder().getId() != null &&
                                    (Constants.dictionary().ORDER_STATUS_PENDING.equals(statusId) || Constants.dictionary().ORDER_STATUS_ON_HOLD.equals(statusId));
                    else
                        r.enabled = false;
                }

                status.enable( !Constants.dictionary().ORDER_STATUS_PROCESSED.equals(statusId) &&
                              !Constants.dictionary().ORDER_STATUS_RECURRING.equals(statusId) &&
                              !Constants.dictionary().ORDER_STATUS_CANCELLED.equals(statusId));
            }
        });

        status.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                TableDataRow r;

                r = event.getItem().row;
                if ( !r.enabled)
                    event.cancel();
            }
        });

        organizationAddressMultipleUnit = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressMultipleUnit());
        addScreenHandler(organizationAddressMultipleUnit,
                         new ScreenEventHandler<String>() {
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
                                 // this is a read only and the value will not
                                 // change
                             }

                             public void onStateChange(StateChangeEvent<State> event) {
                                 organizationAddressMultipleUnit.enable(event.getState() == State.QUERY);
                                 organizationAddressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
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
                orderedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                orderedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressStreetAddress = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressStreetAddress());
        addScreenHandler(organizationAddressStreetAddress,
                         new ScreenEventHandler<String>() {
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
                                 // this is a read only and the value will not
                                 // change
                             }

                             public void onStateChange(StateChangeEvent<State> event) {
                                 organizationAddressStreetAddress.enable(event.getState() == State.QUERY);
                                 organizationAddressStreetAddress.setQueryMode(event.getState() == State.QUERY);
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
                requestedBy.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                requestedBy.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressCity = (TextBox)def.getWidget(OrderMeta.getOrganizationAddressCity());
        addScreenHandler(organizationAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                OrderViewDO data;

                data = manager.getOrder();
                if (data.getOrganization() != null)
                    organizationAddressCity.setValue(data.getOrganization()
                                                         .getAddress()
                                                         .getCity());
                else
                    organizationAddressCity.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this is a read only and the value will not change
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddressCity.enable(event.getState() == State.QUERY);
                organizationAddressCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        costCenter = (Dropdown)def.getWidget(OrderMeta.getCostCenterId());
        addScreenHandler(costCenter, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                costCenter.setSelection(manager.getOrder().getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrder().setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                costCenter.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                costCenter.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationAddressState = (Dropdown<String>)def.getWidget(OrderMeta.getOrganizationAddressState());
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
                organizationAddressState.enable(event.getState() == State.QUERY);
                organizationAddressState.setQueryMode(event.getState() == State.QUERY);
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
                organizationAddressZipCode.enable(event.getState() == State.QUERY);
                organizationAddressZipCode.setQueryMode(event.getState() == State.QUERY);
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
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
                descQuery = null;
            }
        });

        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ArrayList<TableDataRow> model;
                IdNameVO data;
                ArrayList<IdNameVO> dataList;
                ArrayList<String> matchList;
                String match, name;

                match = event.getMatch();
                window.setBusy();

                try {
                    model = new ArrayList<TableDataRow>();
                    row = new TableDataRow(match, match);
                    model.add(row);

                    if (descQuery == null || ( ! (match.indexOf(descQuery) == 0))) {
                        dataList = OrderService.get().fetchByDescription(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                        matchList = new ArrayList<String>();
                        for (int i = 0; i < dataList.size(); i++ ) {
                            data = dataList.get(i);
                            name = data.getName();
                            if ( !matchList.contains(name)) {
                                row = new TableDataRow(name, name);
                                model.add(row);
                                matchList.add(name);
                            }
                        }

                        if (dataList.size() == 0)
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

        organizationTab = new OrganizationTab(def, window);
        addScreenHandler(organizationTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                organizationTab.setManager(manager);
                if (tab == Tabs.ORGANIZATION)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationTab.setState(event.getState());
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

        popoutUtil = new TestContainerPopoutUtil();
        testTab = new TestTab(def, window, popoutUtil);
        addScreenHandler(testTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                testTab.setManager(manager);
                if (tab == Tabs.TEST)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testTab.setState(event.getState());
            }
        });

        testTab.addActionHandler(new ActionHandler<TestTab.Action>() {
            public void onAction(ActionEvent<TestTab.Action> event) {
                if (event.getAction() == TestTab.Action.ADD_AUX) {
                    ValidationErrorsList errors;
                    try {
                        errors = AuxDataUtil.addAuxGroupsFromPanel((Integer)event.getData(),
                                                                   manager.getAuxData());
                        auxDataTab.setManager(manager);
                        if (errors != null && errors.size() > 0)
                            showErrors(errors);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        e.printStackTrace();
                    }
                } else if (event.getAction() == TestTab.Action.REFRESH_AUX) {
                    /*
                     * This event is fired when TestTab responds to the closing
                     * of the pop-out showing tests and containers. It's fired
                     * to make sure that the aux data tab gets refreshed
                     * correctly to any new aux groups added to the order
                     * because of being part of a panel added through the
                     * pop-out.
                     */
                    auxDataTab.setManager(manager);
                }
            }
        });

        containerTab = new ContainerTab(def, window, popoutUtil);
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

        custNoteTab = new CustomerNoteTab(def,
                                          window,
                                          "customerNotesPanel",
                                          "editNoteButton");
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

        internalNoteTab = new InternalNoteTab(def,
                                              window,
                                              userPermission.getLoginName(),
                                              userPermission.getSystemUserId());
        addScreenHandler(internalNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                internalNoteTab.setManager(manager);
                if (tab == Tabs.INTERNAL_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                internalNoteTab.setState(event.getState());
            }
        });

        sampleNoteTab = new SampleNoteTab(def,
                                          window,
                                          "sampleNotesPanel",
                                          "sampleEditNoteButton");
        addScreenHandler(sampleNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNoteTab.setManager(manager);
                if (tab == Tabs.SAMPLE_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleNoteTab.setState(event.getState());
            }
        });

        recurrenceTab = new RecurrenceTab(def, window);
        addScreenHandler(recurrenceTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                recurrenceTab.setManager(manager);
                if (tab == Tabs.RECURRENCE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                recurrenceTab.setState(event.getState());
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

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(Query query) {
                QueryData field;

                window.setBusy(consts.get("querying"));
                // this screen should only query for send-out orders
                field = new QueryData();
                field.key = OrderMeta.getType();
                field.query = OrderManager.TYPE_SEND_OUT;
                field.type = QueryData.Type.STRING;
                query.setFields(field);

                query.setRowsPerPage(23);
                OrderService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(),
                                                   entry.getId(),
                                                   entry.getName()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY)
                                .contains(event.getState()) &&
                         userModulePermission.hasSelectPermission();
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
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        // order status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("order_status");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenter.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        shipFrom.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getEntry(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        organizationAddressState.setModel(model);
    }

    public void setManager(OrderManager manager) {
        this.manager = manager;
        organizationTab.setManager(manager);
        auxDataTab.setManager(manager);
        testTab.setManager(manager);
        containerTab.setManager(manager);
        itemTab.setManager(manager);
        shipNoteTab.setManager(manager);
        custNoteTab.setManager(manager);
        internalNoteTab.setManager(manager);
        sampleNoteTab.setManager(manager);
        recurrenceTab.setManager(manager);
        fillTab.setManager(manager);

        drawAllTabs();

        if (manager.getOrder().getId() != null)
            setState(State.DISPLAY);
        else
            setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    public ArrayList<QueryData> getQueryFields() {
        int i;
        ArrayList<QueryData> fields, auxFields;
        QueryData field;

        fields = super.getQueryFields();

        // add aux data values if necessary
        auxFields = auxDataTab.getQueryFields();

        // add the type
        field = new QueryData();
        field.key = OrderMeta.getType();
        field.query = OrderManager.TYPE_SEND_OUT;
        field.type = QueryData.Type.STRING;
        // fields.add(field);

        if (auxFields.size() > 0) {
            // add ref table
            field = new QueryData();
            field.key = OrderMeta.getAuxDataReferenceTableId();
            field.type = QueryData.Type.INTEGER;
            field.query = String.valueOf(Constants.table().ORDER);
            fields.add(field);

            // add aux fields
            for (i = 0; i < auxFields.size(); i++ ) {
                fields.add(auxFields.get(i));
            }
        }

        return fields;
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = OrderManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        drawAllTabs();

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
            now = CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            Window.alert("OrderAdd Datetime: " + e.getMessage());
            return;
        }

        manager = OrderManager.getInstance();
        data = manager.getOrder();
        data.setStatusId(Constants.dictionary().ORDER_STATUS_PENDING);
        data.setOrderedDate(now);
        data.setRequestedBy(UserCache.getPermission().getLoginName());
        data.setType(OrderManager.TYPE_SEND_OUT);

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(neededInDays);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        if (statusCancelledId.equals(manager.getOrder().getStatusId())) {
            Window.alert(consts.get("cancelledOrderCantBeUpdated"));
            return;
        }

        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            if (statusCancelledId.equals(manager.getOrder().getStatusId())) {
                Window.alert(consts.get("cancelledOrderCantBeUpdated"));
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
                return;
            }

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(neededInDays);

            //
            // these tabs are loaded here to make sure that the on-screen
            // validation
            // and the validation done at the back end doesn't suffer from
            // errorneous conclusions about the data because of the lack of some
            // subset of it
            //
            drawAllTabs();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        Integer prevStatusId;
        Query query;
        OrderViewDO data;
        OrderRecurrenceDO orec;
        ArrayList<QueryData> queryFields;

        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            queryFields = getQueryFields();
            query = new Query();
            query.setFields(queryFields);

            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            data = manager.getOrder();
            prevStatusId = data.getStatusId();
            try {
                removeNotReportableAnalytes();
                orec = manager.getRecurrence();
                /*
                 * if the user entered data in the fields for recurrence but
                 * didn't click on the "active" checkbox, this code will make
                 * sure that a record in the table for recurrence isn't
                 * attempted to be created for this order with null in that
                 * field because that field is required
                 */
                if (orec.getIsActive() == null && !isRecurrenceEmpty(orec))
                    orec.setIsActive("N");
                prevStatusId = data.getStatusId();
                if ("Y".equals(orec.getIsActive()) &&
                    !Constants.dictionary().ORDER_STATUS_RECURRING.equals(prevStatusId))
                    data.setStatusId(Constants.dictionary().ORDER_STATUS_RECURRING);

                manager.validate();
                manager = manager.add();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
                if ( !e.hasErrors() && e.hasWarnings()) {
                    showWarningsDialog(e);
                } else {
                    /*
                     * if the status of the order was set to recurring in this
                     * method and the data couldn't get committed because of
                     * errors in validation, the status gets set back to what it
                     * was before committing, so that if the user commits with
                     * an inactive interval later, the status doesn't remain
                     * recurring, as the status is set to recurring only if the
                     * interval is active
                     */
                    data.setStatusId(prevStatusId);
                }
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));

            data = manager.getOrder();
            prevStatusId = data.getStatusId();
            try {
                removeNotReportableAnalytes();
                orec = manager.getRecurrence();
                /*
                 * if the user entered data in the fields for recurrence but
                 * didn't click on the "active" checkbox, this code will make
                 * sure that a record in the table for recurrence isn't
                 * attempted to be created for this order with null in that
                 * field because that field is required
                 */
                if (orec.getIsActive() == null && !isRecurrenceEmpty(orec))
                    orec.setIsActive("N");
                if ("Y".equals(orec.getIsActive()) &&
                    !Constants.dictionary().ORDER_STATUS_RECURRING.equals(prevStatusId))
                    data.setStatusId(Constants.dictionary().ORDER_STATUS_RECURRING);

                manager.validate();
                manager = manager.update();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
                if ( !e.hasErrors() && e.hasWarnings()) {
                    showWarningsDialog(e);
                } else {
                    /*
                     * if the status of the order was set to recurring in this
                     * method and the data couldn't get committed because of
                     * errors in validation, the status gets set back to what it
                     * was before committing, so that if the user commits with
                     * an inactive interval later, the status doesn't remain
                     * recurring, as the status is set to recurring only if the
                     * interval is active
                     */
                    data.setStatusId(prevStatusId);
                }
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void commitWithWarnings() {
        OrderViewDO data;
        Integer prevStatusId;

        clearErrors();

        data = manager.getOrder();
        prevStatusId = data.getStatusId();
        if (state == State.ADD) {
            window.setBusy(consts.get("adding"));

            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                /*
                 * if the status of the order was set to recurring in this
                 * method and the data couldn't get committed because of errors
                 * in validation, the status gets set back to what it was before
                 * committing, so that if the user commits with an inactive
                 * interval later, the status doesn't remain recurring, as the
                 * status is set to recurring only if the interval is active
                 */
                data.setStatusId(prevStatusId);
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
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                /*
                 * if the status of the order was set to recurring in this
                 * method and the data couldn't get committed because of errors
                 * in validation, the status gets set back to what it was before
                 * committing, so that if the user commits with an inactive
                 * interval later, the status doesn't remain recurring, as the
                 * status is set to recurring only if the interval is active
                 */
                data.setStatusId(prevStatusId);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        boolean ok;

        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            ok = Window.confirm(consts.get("abortWarning"));
            if (ok) {
                fetchById(null);
                window.setDone(consts.get("addAborted"));
            } else {
                window.setDone(consts.get("enterInformationPressCommit"));
            }
        } else if (state == State.UPDATE) {
            ok = Window.confirm(consts.get("abortWarning"));
            if (ok) {
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
        } else {
            window.clearStatus();
        }
    }

    protected void duplicate() {
        try {
            window.setBusy(consts.get("fetching"));

            manager = OrderService.get().duplicate(manager.getOrder().getId());

            organizationTab.setManager(manager);
            auxDataTab.setManager(manager);
            containerTab.setManager(manager);
            itemTab.setManager(manager);
            shipNoteTab.setManager(manager);
            custNoteTab.setManager(manager);
            recurrenceTab.setManager(manager);

            organizationTab.draw();
            auxDataTab.draw();
            containerTab.draw();
            itemTab.draw();
            shipNoteTab.draw();
            custNoteTab.draw();
            recurrenceTab.draw();

            setState(State.ADD);
            DataChangeEvent.fire(this);

            setFocus(neededInDays);
            window.setDone(consts.get("enterInformationPressCommit"));
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }

    protected void shippingInfo() {
        try {
            window.setBusy(consts.get("fetching"));

            ShippingService.get().fetchByOrderId(manager.getOrder().getId(),
                                 new SyncCallback<ShippingViewDO>() {
                                     public void onSuccess(ShippingViewDO result) {
                                         try {
                                             if (result != null)
                                                 shippingManager = ShippingManager.fetchById(result.getId());
                                             else
                                                 shippingManager = null;
                                         } catch (Throwable e) {
                                             shippingManager = null;
                                             e.printStackTrace();
                                             Window.alert(e.getMessage());
                                             window.clearStatus();
                                         }
                                     }

                                     public void onFailure(Throwable error) {
                                         shippingManager = null;
                                         error.printStackTrace();
                                         Window.alert("Error: Fetch failed; " +
                                                      error.getMessage());
                                         window.clearStatus();
                                     }
                                 });

            if (shippingManager != null)
                showShippingScreen(shippingManager, State.DISPLAY);
            else
                window.setDone(consts.get("noRecordsFound"));
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
            return;
        }
    }

    protected void orderRequestForm() {
        Query query;
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.key = "ORDERID";
        field.query = manager.getOrder().getId().toString();
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);

        field = new QueryData();
        field.key = "PRINTER";
        field.query = "-view-";
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);

        try {
            if (requestformReportScreen == null)
                requestformReportScreen = new OrderRequestFormReportScreen(window);
            else
                requestformReportScreen.setWindow(window);

            requestformReportScreen.runReport(query, new AsyncCallback<ReportStatus>() {

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void onSuccess(ReportStatus result) {
                    // TODO Auto-generated method stub
                    
                }
            });
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void orderHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrder().getId(), manager.getOrder()
                                                               .getId()
                                                               .toString());
        HistoryScreen.showHistory(consts.get("orderHistory"),
                                  Constants.table().ORDER,
                                  hist);
    }

    protected void organizationHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrderOrganizationManager man;
        OrderOrganizationViewDO data;

        window.setBusy();
        try {
            man = manager.getOrganizations();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getOrganizationAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getOrganizationName());
            }

            HistoryScreen.showHistory(consts.get("orderOrganizationHistory"),
                                      Constants.table().ORDER_ORGANIZATION,
                                      refVoList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
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
            HistoryScreen.showHistory(consts.get("orderItemHistory"),
                                      Constants.table().ORDER_ITEM,
                                      refVoList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
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
                refVoList[i] = new IdNameVO(data.getId(), data.getTestName());
            }
            HistoryScreen.showHistory(consts.get("orderTestHistory"),
                                      Constants.table().ORDER_TEST,
                                      refVoList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
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
                dict = DictionaryCache.getById(data.getContainerId());
                refVoList[i] = new IdNameVO(data.getId(), dict.getEntry());
            }
            HistoryScreen.showHistory(consts.get("orderContainerHistory"),
                                      Constants.table().ORDER_CONTAINER,
                                      refVoList);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = OrderManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case ORGANIZATION:
                        manager = OrderManager.fetchWithOrganizations(id);
                        break;
                    case AUX_DATA:
                        manager = OrderManager.fetchById(id);
                        break;
                    case TEST:
                        manager = OrderManager.fetchWithTests(id);
                        break;
                    case CONTAINER:
                        manager = OrderManager.fetchWithContainers(id);
                        break;
                    case ITEM:
                        manager = OrderManager.fetchWithItems(id);
                        break;
                    case SHIP_NOTE:
                    case CUSTOMER_NOTE:
                    case INTERNAL_NOTE:
                    case SAMPLE_NOTE:
                        manager = OrderManager.fetchWithNotes(id);
                        break;
                    case RECURRENCE:
                        manager = OrderManager.fetchWithRecurrence(id);
                        break;
                    case FILL:
                        manager = OrderManager.fetchWithFills(id);
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
            case ORGANIZATION:
                organizationTab.draw();
                break;
            case AUX_DATA:
                auxDataTab.draw();
                break;
            case TEST:
                testTab.draw();
                break;
            case CONTAINER:
                containerTab.draw();
                break;
            case ITEM:
                itemTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;
            case CUSTOMER_NOTE:
                custNoteTab.draw();
                break;
            case INTERNAL_NOTE:
                internalNoteTab.draw();
                break;
            case SAMPLE_NOTE:
                sampleNoteTab.draw();
                break;
            case RECURRENCE:
                recurrenceTab.draw();
                break;
            case FILL:
                fillTab.draw();
                break;
        }
    }

    private void showShippingScreen(ShippingManager manager, State state) throws Exception {
        ScreenWindow modal;

        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("shipping"));
        if (shippingScreen == null)
            shippingScreen = new ShippingScreen(modal);

        modal.setContent(shippingScreen);
        shippingScreen.loadShippingData(manager, state);
        window.clearStatus();
    }

    private boolean isRecurrenceEmpty(OrderRecurrenceDO data) {
        if (data == null)
            return true;

        if (DataBaseUtil.isEmpty(data.getId()) &&
            DataBaseUtil.isEmpty(data.getOrderId()) &&
            DataBaseUtil.isEmpty(data.getIsActive()) &&
            DataBaseUtil.isEmpty(data.getActiveBegin()) &&
            DataBaseUtil.isEmpty(data.getActiveEnd()) &&
            DataBaseUtil.isEmpty(data.getFrequency()) &&
            DataBaseUtil.isEmpty(data.getUnitId()))
            return true;

        return false;
    }

    private void drawAllTabs() {
        organizationTab.draw();
        auxDataTab.draw();
        testTab.draw();
        containerTab.draw();
        itemTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();
        internalNoteTab.draw();
        sampleNoteTab.draw();
        recurrenceTab.draw();
        fillTab.draw();
    }

    private void removeNotReportableAnalytes() throws Exception {
        OrderTestManager man;
        /*
         * The analytes that are not marked as reportable are deleted. Since on
         * the screen an analytes can be checked or unchecked several times, it
         * can't be deleted as soon as it's unchecked. Thus that is done here.
         */
        man = manager.getTests();
        for (int i = 0; i < man.count(); i++ )
            man.removeNotReportableAnalytesAt(i);
    }

    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility.isHoldRefuseSampleForOrg(orgId))
            Window.alert(consts.get("orgMarkedAsHoldRefuseSample") + "'" + name + "'");
    }
}