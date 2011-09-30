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
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
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
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
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
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.utilcommon.ResultValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SendoutOrderScreen extends Screen {

    private OrderManager                           manager;    
    private ModulePermission                       userModulePermission;
    private SystemUserPermission                   userPermission;

    private ButtonGroup                            atoz;
    private ScreenNavigator                        nav;

    private ReportToBillToTab                      reportToBillToTab;
    private AuxDataTab                             auxDataTab;
    private ContainerTab                           containerTab;
    private ItemTab                                itemTab;
    private ShipNoteTab                            shipNoteTab;
    private CustomerNoteTab                        custNoteTab;
    private InternalNoteTab                        internalNoteTab;
    private RecurrenceTab                          recurrenceTab;
    private FillTab                                fillTab;
    
    private Tabs                                   tab;

    private AppButton                              queryButton, previousButton, nextButton,
                                                   addButton, updateButton, commitButton,
                                                   abortButton;
    private MenuItem                               duplicate, orderHistory, itemHistory,
                                                   testHistory, containerHistory;
    private TextBox                                id, neededInDays, requestedBy,
                                                   organizationAttention, organizationAddressMultipleUnit,
                                                   organizationAddressStreetAddress,
                                                   organizationAddressCity, organizationAddressState,
                                                   organizationAddressZipCode;
    private CalendarLookUp                         orderedDate;
    private Dropdown<Integer>                      statusId, shipFromId, costCenterId;
    private AutoComplete<Integer>                  organizationName;
    private AutoComplete<String>                   description;
    private TabPanel                               tabPanel;
    private Integer                                status_pending, status_recurring,
                                                   auxAlphaLowerId, auxAlphaMixedId, 
                                                   auxAlphaUpperId, auxDateId, auxDateTimeId,
                                                   auxDefaultId, auxDictionaryId, 
                                                   auxNumericId, auxTimeId;
    private String                                 descQuery;

    private HashMap<Integer, ResultValidator.Type> types;

    protected ScreenService                        organizationService, panelService;

    private enum Tabs {
        REPORT_TO, AUX_DATA, CONTAINER, ITEM, SHIP_NOTE, CUSTOMER_NOTE, INTERNAL_NOTE, RECURRENCE, FILL 
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
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        panelService = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");

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
        tab = Tabs.REPORT_TO;
        manager = OrderManager.getInstance();

        try {
            CategoryCache.getBySystemNames("order_status", "cost_centers",
                                           "inventory_store", "inventory_unit",
                                           "order_ship_from", "sample_container",
                                           "type_of_sample","aux_field_value_type");
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
                neededInDays.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
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
                shipFromId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
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
                organizationAttention.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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
                    organizationName.setSelection(data.getOrganizationId(), data.getOrganization()
                                                                                .getName());
                } else {
                    organizationName.setSelection(null, "");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;
                TableDataRow row;
               
                row = organizationName.getSelection();
                if (row != null && row.data != null) {
                    data = (OrganizationDO)row.data;

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
                    list = organizationService.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
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
                orderedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                orderedDate.setQueryMode(event.getState() == State.QUERY);
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
                costCenterId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                costCenterId.setQueryMode(event.getState() == State.QUERY);
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
                        dataList = service.callList("fetchByDescription", QueryFieldUtil.parseAutocomplete(event.getMatch()));
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

        containerTab = new ContainerTab(def, window);
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

        containerTab.addActionHandler(new ActionHandler<ContainerTab.Action>() {
            public void onAction(ActionEvent<ContainerTab.Action> event) {
                if (event.getAction() == ContainerTab.Action.ADD_AUX) {
                    addAuxGroupsFromPanel((Integer)event.getData());
                }
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
        
        internalNoteTab = new InternalNoteTab(def, window, userPermission.getLoginName(), userPermission.getSystemUserId());
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
        nav = new ScreenNavigator(def) {
            public void executeQuery(Query query) {
                QueryData field;

                window.setBusy(consts.get("querying"));
                // this screen should only query for kit orders
                field = new QueryData();
                field.key = OrderMeta.getType();
                field.query = OrderManager.TYPE_SEND_OUT;
                field.type = QueryData.Type.STRING;
                query.setFields(field);

                query.setRowsPerPage(20);
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
                            Window.alert("Error: Order call query failed; " + error.getMessage());
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

        statusId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenterId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("order_ship_from");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        shipFromId.setModel(model);

        types = new HashMap<Integer, ResultValidator.Type>();
        try {
            auxAlphaLowerId = DictionaryCache.getIdBySystemName("aux_alpha_lower");
            types.put(auxAlphaLowerId, ResultValidator.Type.ALPHA_LOWER);
            auxAlphaMixedId = DictionaryCache.getIdBySystemName("aux_alpha_mixed");
            types.put(auxAlphaMixedId, ResultValidator.Type.ALPHA_MIXED);
            auxAlphaUpperId = DictionaryCache.getIdBySystemName("aux_alpha_upper");
            types.put(auxAlphaUpperId, ResultValidator.Type.ALPHA_UPPER);
            auxDateId = DictionaryCache.getIdBySystemName("aux_date");
            types.put(auxDateId, ResultValidator.Type.DATE);
            auxDateTimeId = DictionaryCache.getIdBySystemName("aux_date_time");
            types.put(auxDateTimeId, ResultValidator.Type.DATE_TIME);
            auxDefaultId = DictionaryCache.getIdBySystemName("aux_default");
            types.put(auxDefaultId, ResultValidator.Type.DEFAULT);
            auxDictionaryId = DictionaryCache.getIdBySystemName("aux_dictionary");
            types.put(auxDictionaryId, ResultValidator.Type.DICTIONARY);
            auxNumericId = DictionaryCache.getIdBySystemName("aux_numeric");
            types.put(auxNumericId, ResultValidator.Type.NUMERIC);
            auxTimeId = DictionaryCache.getIdBySystemName("aux_time");
            types.put(auxTimeId, ResultValidator.Type.TIME);
            status_pending = DictionaryCache.getIdBySystemName("order_status_pending");
            status_recurring = DictionaryCache.getIdBySystemName("order_status_recurring");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    public void setManager(OrderManager manager) {
        this.manager = manager;
        reportToBillToTab.setManager(manager);
        auxDataTab.setManager(manager);
        containerTab.setManager(manager);
        itemTab.setManager(manager);
        shipNoteTab.setManager(manager);
        custNoteTab.setManager(manager);
        internalNoteTab.setManager(manager);
        recurrenceTab.setManager(manager);
        fillTab.setManager(manager);

        reportToBillToTab.draw();
        auxDataTab.draw();
        containerTab.draw();
        itemTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();
        internalNoteTab.draw();
        recurrenceTab.draw();
        fillTab.draw();

        if (manager.getOrder().getId() != null)
            setState(State.DISPLAY);
        else 
            setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = OrderManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        reportToBillToTab.draw();
        auxDataTab.draw();
        containerTab.draw();
        itemTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();
        internalNoteTab.draw();
        recurrenceTab.draw();
        fillTab.draw();

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
            Window.alert("OrderAdd Datetime: " + e.getMessage());
            return;
        }

        manager = OrderManager.getInstance();
        data = manager.getOrder();
        data.setStatusId(status_pending);
        data.setOrderedDate(now);
        data.setRequestedBy(UserCache.getPermission().getLoginName());
        data.setType(OrderManager.TYPE_SEND_OUT);

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(neededInDays);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        Integer stId;
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            stId = manager.getOrder().getStatusId();
            if ( !status_pending.equals(stId) && !status_recurring.equals(stId)) {
                Window.alert(consts.get("orderStatusNotPendingOrRecurForUpdate"));
                manager = manager.abortUpdate();
            } else {
                setState(State.UPDATE);
                setFocus(neededInDays);
            }

            DataChangeEvent.fire(this);
        } catch (Exception e) {
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

        data = null;
        prevStatusId = null;
        if (state == State.QUERY) {
            queryFields = getQueryFields();
            query = new Query();
            query.setFields(queryFields);           

            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                data = manager.getOrder();
                orec = manager.getRecurrence();
                prevStatusId = data.getStatusId();
                if ("Y".equals(orec.getIsActive()) && !status_recurring.equals(prevStatusId))  
                    data.setStatusId(status_recurring);
                
                manager = manager.add();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
                /*
                 * if the status of the order was set to recurring in this method
                 * and the data couldn't get committed because of errors is validation,
                 * the status gets set back to what it was before committing,
                 * so that if the user commits with an inactive interval later,
                 * the status doesn't remain recurring, as the status is set to
                 * recurring only if the interval is active
                 */
                data.setStatusId(prevStatusId);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                data = manager.getOrder();
                orec = manager.getRecurrence();
                prevStatusId = data.getStatusId();
                if ("Y".equals(orec.getIsActive()) && !status_recurring.equals(prevStatusId))  
                    data.setStatusId(status_recurring);
                
                manager = manager.update();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
                /*
                 * if the status of the order was set to recurring in this method
                 * and the data couldn't get committed because of errors is validation,
                 * the status gets set back to what it was before committing,
                 * so that if the user commits with an inactive interval later,
                 * the status doesn't remain recurring, as the status is set to
                 * recurring only if the interval is active
                 */
                data.setStatusId(prevStatusId);
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
                Window.alert("OrderAdd Datetime: " + e.getMessage());
                return;
            }

            data = manager.getOrder();
            data.setStatusId(status_pending);
            data.setOrderedDate(now);
            data.setRequestedBy(UserCache.getPermission().getLoginName());
            data.setType(OrderManager.TYPE_SEND_OUT);

            reportToBillToTab.setManager(manager);
            auxDataTab.setManager(manager);
            containerTab.setManager(manager);
            itemTab.setManager(manager);
            shipNoteTab.setManager(manager);
            custNoteTab.setManager(manager);

            manager.getAuxData();
            manager.getTests();
            manager.getContainers();
            manager.getItems();
            manager.getShippingNotes();
            manager.getCustomerNotes();

            clearKeys();

            reportToBillToTab.draw();
            auxDataTab.draw();
            containerTab.draw();
            itemTab.draw();
            shipNoteTab.draw();
            custNoteTab.draw();

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
            HistoryScreen.showHistory(consts.get("orderItemHistory"), ReferenceTable.ORDER_ITEM,
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
            HistoryScreen.showHistory(consts.get("orderTestHistory"), ReferenceTable.ORDER_TEST,
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
                                      ReferenceTable.ORDER_CONTAINER, refVoList);
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
                    case REPORT_TO:
                        manager = OrderManager.fetchById(id);
                        break;
                    case AUX_DATA:
                        manager = OrderManager.fetchById(id);
                        break;
                    case CONTAINER:
                        manager = OrderManager.fetchWithTestsAndContainers(id);
                        break;
                    case ITEM:
                        manager = OrderManager.fetchWithItems(id);
                        break;
                    case SHIP_NOTE:
                    case CUSTOMER_NOTE:                        
                    case INTERNAL_NOTE:
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
        
        if (auxFields.size() > 0) {
            // add ref table
            field = new QueryData();
            field.key = OrderMeta.getAuxDataReferenceTableId();
            field.type = QueryData.Type.INTEGER;
            field.query = String.valueOf(ReferenceTable.ORDER);
            fields.add(field);

            // add aux fields
            for (i = 0; i < auxFields.size(); i++ ) {                
                fields.add(auxFields.get(i));            
            } 
        }

        return fields;
    }

    private void drawTabs() {
        switch (tab) {
            case REPORT_TO:
                reportToBillToTab.draw();
                break;
            case AUX_DATA:
                auxDataTab.draw();
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
            case RECURRENCE:
                recurrenceTab.draw();
                break;    
            case FILL:
                fillTab.draw();
                break;
        }
    }

    private void clearKeys() {
        int i;
        OrderItemManager iman;
        NoteManager nman;
        OrderTestManager otman;
        OrderContainerManager ocman;
        AuxDataManager adman;
        OrderItemViewDO item;
        NoteViewDO note;
        OrderTestViewDO test;
        OrderContainerDO container;
        AuxDataViewDO aux;

        manager.getOrder().setId(null);
        manager.getOrder().setParentOrderId(null);

        try {
            iman = manager.getItems();
            for (i = 0; i < iman.count(); i++ ) {
                item = iman.getItemAt(i);
                item.setId(null);
                item.setOrderId(null);
            }

            nman = manager.getShippingNotes();
            for (i = 0; i < nman.count(); i++ ) {
                note = nman.getNoteAt(i);
                note.setId(null);
                note.setReferenceId(null);
                note.setReferenceTableId(null);
            }

            nman = manager.getCustomerNotes();
            for (i = 0; i < nman.count(); i++ ) {
                note = nman.getNoteAt(i);
                note.setId(null);
                note.setReferenceId(null);
                note.setReferenceTableId(null);
            }                        

            otman = manager.getTests();
            for (i = 0; i < otman.count(); i++ ) {
                test = otman.getTestAt(i);
                test.setId(null);
                test.setOrderId(null);
            }

            ocman = manager.getContainers();
            for (i = 0; i < ocman.count(); i++ ) {
                container = ocman.getContainerAt(i);
                container.setId(null);
                container.setOrderId(null);
            }

            adman = manager.getAuxData();
            for (i = 0; i < adman.count(); i++ ) {
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
    
    private void addAuxGroupsFromPanel(Integer panelId) {
        int                            i, j, k;
        ArrayList<AuxFieldValueViewDO> values;
        ArrayList<IdVO>                auxIds;
        AuxDataManager                 adMan;
        AuxDataViewDO                  dataDO;
        AuxFieldManager                afMan;
        AuxFieldViewDO                 fieldDO;
        AuxFieldValueViewDO            defaultDO, valueDO;
        Integer                        validId;
        ResultValidator                validator;
        ValidationErrorsList           errorsList;

        errorsList = new ValidationErrorsList();

        try {
            adMan  = manager.getAuxData();
            auxIds = panelService.callList("fetchAuxIdsByPanelId", panelId);
            for (i = 0; i < auxIds.size(); i++) {
                try {
                    afMan = AuxFieldManager.fetchByGroupIdWithValues(auxIds.get(i).getId());
                    for (j = 0; j < afMan.count(); j++) {
                        fieldDO = afMan.getAuxFieldAt(j);
                        if ("Y".equals(fieldDO.getIsActive())) {
                            values = afMan.getValuesAt(j).getValues();
                            defaultDO = afMan.getValuesAt(j).getDefaultValue();
        
                            dataDO = new AuxDataViewDO();
                            dataDO.setAuxFieldId(fieldDO.getId());
                            dataDO.setIsReportable(fieldDO.getIsReportable());
                            dataDO.setGroupId(fieldDO.getAuxFieldGroupId());
                            
                            validator = getValidatorForValues(values);
                            if (defaultDO != null) {
                                try {
                                    validId = validator.validate(null, defaultDO.getValue());
                                    for (k = 0; k < values.size(); k++) {
                                        valueDO = values.get(k);
                                        if (valueDO.getId().equals(validId)) {
                                            if (auxDictionaryId.equals(valueDO.getTypeId())) {
                                                dataDO.setTypeId(valueDO.getTypeId());
                                                dataDO.setValue(valueDO.getValue());
                                                dataDO.setDictionary(valueDO.getDictionary());
                                            } else {
                                                dataDO.setTypeId(valueDO.getTypeId());
                                                dataDO.setValue(defaultDO.getValue());
                                            }
                                            break;
                                        }
                                    }
                                } catch (ParseException parE) {
                                    errorsList.add(new FormErrorWarning("illegalDefaultValueForAuxFieldException",
                                                                        defaultDO.getValue(),
                                                                        fieldDO.getAnalyteName()));
                                }
                            } else {
                                dataDO.setTypeId(auxAlphaMixedId);
                            }
        
                            adMan.addAuxDataFieldAndValues(dataDO, fieldDO, values);
                        }
                    }
                } catch (Exception anyE2) {
                    errorsList.add(anyE2);
                }
            }
            
            
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
        }
        
        if (errorsList.size() > 0)
            showErrors(errorsList);
    }

    private ResultValidator getValidatorForValues(ArrayList<AuxFieldValueViewDO> values) {
        AuxFieldValueViewDO  af;
        DictionaryDO         dict;
        ResultValidator      rv;
        ResultValidator.Type type;
        String               dictEntry;
        
        rv = new ResultValidator();
        try {
            for (int i = 0; i < values.size(); i++ ) {
                af = values.get(i);
                dictEntry = null;
                rv = new ResultValidator();
                
                type = types.get(af.getTypeId());
                if (type == ResultValidator.Type.DICTIONARY) {
                    dict = DictionaryCache.getById(new Integer(af.getValue()));
                    dictEntry = dict.getEntry();
                }
                rv.addResult(af.getId(), null, type, null, null, af.getValue(), dictEntry);                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        return rv;
    }
}