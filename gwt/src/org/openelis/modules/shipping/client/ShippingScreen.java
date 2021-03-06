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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.CalendarService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.meta.ShippingMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.report.client.ShippingReportScreen;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class ShippingScreen extends Screen implements
                                          HasActionHandlers<ShippingScreen.Action> {
    private ShippingManager       manager;
    private ModulePermission      userPermission;

    private ScreenNavigator       nav;

    private NotesTab              noteTab;
    private ItemTab               itemTab;
    private Tabs                  tab;

    private AppButton             queryButton, previousButton, nextButton, addButton,
                                  updateButton, commitButton, abortButton;
    protected MenuItem            processShipping, print, shippingHistory,
                                  shippingItemHistory, shippingTrackingHistory;
    private TextBox               cost, shippedToAttention,
                                  shippedToAddressMultipleUnit, processedById,
                                  shippedToAddressStreetAddress, shippedToAddressCity,
                                  shippedToAddressState, shippedToAddressZipCode;
    private TextBox<Integer>      id, numberOfPackages;
    private CalendarLookUp        shippedDate, processedDate;
    private Dropdown<Integer>     statusId, shippedFromId, shippedMethodId;
    private AutoComplete<Integer> shippedToName;
    private TabPanel              tabPanel;
    private ShippingScreen        screen;
    private ProcessShippingScreen processShippingScreen;
    private ShippingReportScreen  shippingReportScreen;
    private boolean               openedFromMenu;

    private enum Tabs {
        ITEM, SHIP_NOTE
    };

    public enum Action {
        COMMIT, ABORT
    };

    public ShippingScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ShippingDef.class));

        ShippingScreenImpl(true);
    }

    public ShippingScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(ShippingDef.class));
        
        setWindow(window);

        ShippingScreenImpl(false);
    }

    private void ShippingScreenImpl(boolean fromMenu) throws Exception {

        userPermission = UserCache.getPermission().getModule("shipping");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Shipping Screen"));

        openedFromMenu = fromMenu;

        tab = Tabs.ITEM;
        manager = ShippingManager.getInstance();

        try {
            CategoryCache.getBySystemNames("shipping_status",
                                           "laboratory_location",
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
        screen = this;

        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
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
                add(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
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
                                    userPermission.hasUpdatePermission());
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

        processShipping = (MenuItem)def.getWidget("processShipping");
        addScreenHandler(processShipping, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                processShipping();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                processShipping.enable(EnumSet.of(State.DISPLAY, State.DEFAULT)
                                              .contains(event.getState()) &&
                                       userPermission.hasUpdatePermission());
            }
        });

        print = (MenuItem)def.getWidget("print");
        addScreenHandler(print, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                print();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                print.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        shippingHistory = (MenuItem)def.getWidget("shippingHistory");
        addScreenHandler(shippingHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippingHistory.enable(EnumSet.of(State.DISPLAY)
                                              .contains(event.getState()));
            }
        });

        shippingItemHistory = (MenuItem)def.getWidget("itemHistory");
        addScreenHandler(shippingItemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingItemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippingItemHistory.enable(EnumSet.of(State.DISPLAY)
                                                  .contains(event.getState()));
            }
        });

        shippingTrackingHistory = (MenuItem)def.getWidget("trackingHistory");
        addScreenHandler(shippingTrackingHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                shippingTrackingHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippingTrackingHistory.enable(EnumSet.of(State.DISPLAY)
                                                      .contains(event.getState()));
            }
        });

        id = (TextBox<Integer>)def.getWidget(ShippingMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setFieldValue(manager.getShipping().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown)def.getWidget(ShippingMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getShipping().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                if ( !Constants.dictionary().SHIPPING_STATUS_SHIPPED.equals(event.getValue())) {
                    manager.getShipping().setShippedDate(null);
                    shippedDate.setValue(manager.getShipping().getShippedDate());
                }

                manager.getShipping().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
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
                shippedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                shippedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        numberOfPackages = (TextBox<Integer>)def.getWidget(ShippingMeta.getNumberOfPackages());
        addScreenHandler(numberOfPackages, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                numberOfPackages.setFieldValue(manager.getShipping().getNumberOfPackages());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getShipping().setNumberOfPackages(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                numberOfPackages.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                numberOfPackages.setQueryMode(event.getState() == State.QUERY);
            }
        });

        cost = (TextBox)def.getWidget(ShippingMeta.getCost());
        addScreenHandler(cost, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                cost.setFieldValue(manager.getShipping().getCost());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                manager.getShipping().setCost(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cost.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
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
                shippedFromId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                shippedFromId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAttention = (TextBox)def.getWidget(ShippingMeta.getShippedToAttention());
        addScreenHandler(shippedToAttention, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                shippedToAttention.setValue(manager.getShipping().getShippedToAttention());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getShipping().setShippedToAttention(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shippedToAttention.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
                shippedToAttention.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToName = (AutoComplete)def.getWidget(ShippingMeta.getShippedToName());
        addScreenHandler(shippedToName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;

                data = manager.getShipping();
                if (data.getShippedTo() != null) {
                    shippedToName.setSelection(data.getShippedTo().getId(),
                                               data.getShippedTo().getName());
                } else {
                    shippedToName.setSelection(null, "");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                OrganizationDO data;

                if (shippedToName.getSelection() != null) {
                    data = (OrganizationDO)shippedToName.getSelection().data;

                    manager.getShipping().setShippedToId(data.getId());
                    manager.getShipping().setShippedTo(data);

                    shippedToAddressMultipleUnit.setValue(data.getAddress()
                                                              .getMultipleUnit());
                    shippedToAddressStreetAddress.setValue(data.getAddress()
                                                               .getStreetAddress());
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
                shippedToName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                shippedToName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
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
                processedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                processedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressMultipleUnit = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressMultipleUnit());
        addScreenHandler(shippedToAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;

                data = manager.getShipping();
                if (data.getShippedTo() != null)
                    shippedToAddressMultipleUnit.setValue(data.getShippedTo()
                                                              .getAddress()
                                                              .getMultipleUnit());
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
                processedById.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                processedById.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressStreetAddress = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressStreetAddress());
        addScreenHandler(shippedToAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;

                data = manager.getShipping();
                if (data.getShippedTo() != null)
                    shippedToAddressStreetAddress.setValue(data.getShippedTo()
                                                               .getAddress()
                                                               .getStreetAddress());
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
                shippedMethodId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                shippedMethodId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        shippedToAddressCity = (TextBox)def.getWidget(ShippingMeta.getShippedToAddressCity());
        addScreenHandler(shippedToAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ShippingViewDO data;

                data = manager.getShipping();
                if (data.getShippedTo() != null)
                    shippedToAddressCity.setValue(data.getShippedTo()
                                                      .getAddress()
                                                      .getCity());
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
                if (data.getShippedTo() != null)
                    shippedToAddressState.setValue(data.getShippedTo()
                                                       .getAddress()
                                                       .getState());
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
                if (data.getShippedTo() != null)
                    shippedToAddressZipCode.setValue(data.getShippedTo()
                                                         .getAddress()
                                                         .getZipCode());
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
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(12);
                ShippingService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                                     public void onFailure(Throwable error) {
                                         setQueryResult(null);
                                         if (error instanceof NotFoundException) {
                                             window.setDone(Messages.get().noRecordsFound());
                                             setState(State.DEFAULT);
                                         } else if (error instanceof LastPageException) {
                                             window.setError("No more records in this direction");
                                         } else {
                                             Window.alert("Error: Order call query failed; " +
                                                          error.getMessage());
                                             window.setError(Messages.get().queryFailed());
                                         }
                                     }
                                 });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById((entry == null) ? null : entry.getId());
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

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }

    public void loadShippingData(ShippingManager manager, State state) {
        if (state == State.ADD) {
            add(manager);
        } else if (state == State.DISPLAY) {
            this.manager = manager;
            itemTab.setManager(manager);
            noteTab.setManager(manager);
            setState(state);
            DataChangeEvent.fire(this);
            drawTabs();
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<ShippingScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        // order status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("shipping_status");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        statusId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        shippedFromId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("shipping_method");
        for (DictionaryDO d : list) {
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        }

        shippedMethodId.setModel(model);
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
        window.setDone(Messages.get().enterFieldsToQuery());
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
            now = CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
        } catch (Exception e) {
            Window.alert("Shipping Add Datetime: " + e.getMessage());
            return;
        }

        if (manager == null)
            this.manager = ShippingManager.getInstance();
        else
            this.manager = manager;

        data = this.manager.getShipping();
        data.setStatusId(Constants.dictionary().SHIPPING_STATUS_PROCESSED);
        data.setProcessedDate(now);
        data.setProcessedBy(UserCache.getPermission().getLoginName());

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(statusId);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        boolean ok;
        window.setBusy(Messages.get().lockForUpdate());

        try {
            ok = true;

            if (Constants.dictionary().SHIPPING_STATUS_PROCESSED.equals(manager.getShipping()
                                                                               .getStatusId()))
                ok = Window.confirm(Messages.get().shippingStatusProcessed());
            else if (Constants.dictionary().SHIPPING_STATUS_SHIPPED.equals(manager.getShipping()
                                                                                  .getStatusId()))
                ok = Window.confirm(Messages.get().shippingStatusShipped());

            if ( !ok) {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().updateAborted());
                return;
            }

            manager = manager.fetchForUpdate();
            setState(State.UPDATE);
            setFocus(statusId);
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }

    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(Messages.get().adding());
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().addingComplete());

                if ( !openedFromMenu) {
                    ActionEvent.fire(this, Action.COMMIT, manager.getShipping().getId());
                    window.close();
                }
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                e.printStackTrace();
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().updatingComplete());
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        if (state == State.QUERY) {
            setFocus(null);
            clearErrors();
            window.setBusy(Messages.get().cancelChanges());
            fetchById(null);
            window.setDone(Messages.get().queryAborted());
        } else if (state == State.ADD) {
            if ( !openedFromMenu) {
                /*
                 * If this screen was brought up from Fill Order screen then we
                 * ask the user whether he or she is sure about aborting because
                 * then no shipping record would be created for the orders sent
                 * from that screen and there won't be any chance of creating a
                 * shipping record for them in the future. If the user says no
                 * then we don't do anything.
                 */
                if (Window.confirm(Messages.get().abortNotCreateShippingRecord())) {
                    /*
                     * fetchById(null) is called here to make sure that the
                     * screen doesn't have any data and is not in Add mode
                     * before window.close() is called because otherwise
                     * window.close() will prevent the window from being closed
                     * because of it being in Add mode
                     */
                    fetchById(null);
                    ActionEvent.fire(this, Action.ABORT, null);
                    window.close();
                }
                return;
            }
            setFocus(null);
            clearErrors();
            window.setBusy(Messages.get().cancelChanges());
            fetchById(null);
            window.setDone(Messages.get().addAborted());
        } else if (state == State.UPDATE) {
            setFocus(null);
            clearErrors();
            window.setBusy(Messages.get().cancelChanges());
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(Messages.get().updateAborted());
        } else {
            setFocus(null);
            clearErrors();
            window.clearStatus();
        }
    }

    protected void processShipping() {
        int t, l, w;
        ScreenWindow modal;

        if (processShippingScreen == null) {
            try {
                processShippingScreen = new ProcessShippingScreen();
                processShippingScreen.addActionHandler(new ActionHandler<ProcessShippingScreen.Action>() {
                    public void onAction(ActionEvent<ProcessShippingScreen.Action> event) {
                        int i;
                        Integer id;
                        ShippingTrackingManager man;
                        ShippingViewDO data;

                        if (event.getAction() == ProcessShippingScreen.Action.SHIPPING) {
                            id = (Integer)event.getData();

                            if (state == State.UPDATE) {
                                commit();
                                if (state != State.DISPLAY)
                                    return;
                            }
                            //
                            // the current record should be locked and fetched
                            //
                            try {
                                if ( !fetchById(id)) {
                                    processShippingScreen.reset();
                                    return;
                                }

                                window.setBusy(Messages.get().lockForUpdate());
                                manager = manager.fetchForUpdate();
                                data = manager.getShipping();
                                /*
                                 * we don't allow the processing of a shipping
                                 * record if its status isn't either "Processed"
                                 * or "Shipped"
                                 */
                                setState(State.UPDATE);
                                if ( !Constants.dictionary().SHIPPING_STATUS_SHIPPED.equals(data.getStatusId()) &&
                                    !Constants.dictionary().SHIPPING_STATUS_PROCESSED.equals(data.getStatusId())) {
                                    Window.alert(Messages.get().statusProcessedShipped());
                                    abort();
                                    return;
                                }
                                setProcessShippingData(data);
                                DataChangeEvent.fire(screen);
                                window.clearStatus();
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                /*
                                 * if there was any other problem with fetching
                                 * the record with id we fill the widgets in the
                                 * screen with an empty manager
                                 */
                                fetchById(null);
                                processShippingScreen.reset();
                            }
                        } else if (event.getAction() == ProcessShippingScreen.Action.TRACKING) {
                            /*
                             * this is a tracking number, it just needs to be
                             * added to this ShippingManager's list of tracking
                             * numbers
                             */
                            try {
                                man = manager.getTrackings();
                                i = man.addTracking();
                                man.getTrackingAt(i)
                                   .setTrackingNumber((String)event.getData());
                                itemTab.setManager(manager);
                                drawTabs();
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("ProcessShippingScreen error: " + e.getMessage());
                return;
            }
        }
        w = screen.getOffsetWidth();
        t = screen.getAbsoluteTop();
        l = screen.getAbsoluteLeft();

        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(Messages.get().processShipping());
        /*
         * we display the pop up screen a little outside of the area in which
         * this screen is showing
         */
        modal.setContent(processShippingScreen, l + w + 20, t);
        processShippingScreen.reset();

        modal.addCloseHandler(new CloseHandler<WindowInt>() {
            public void onClose(CloseEvent<WindowInt> event) {
                /*
                 * we update the database with the data showing on the screen
                 * when the pop up window is closed if a record was being
                 * updated on the screen
                 */
                if (state == State.UPDATE)
                    commit();
            }
        });
    }

    protected void print() {
        ScreenWindow modal;

        try {
            if (shippingReportScreen == null) {
                shippingReportScreen = new ShippingReportScreen();

                /*
                 * we need to make sure that the value of SHIPPING_ID gets set
                 * the first time the screen is brought up
                 */
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        shippingReportScreen.setFieldValue("SHIPPING_ID",
                                                           manager.getShipping().getId());
                    }
                });
            } else {
                shippingReportScreen.reset();
                shippingReportScreen.setFieldValue("SHIPPING_ID", manager.getShipping()
                                                                         .getId());
            }
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(Messages.get().print());
            modal.setContent(shippingReportScreen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void shippingHistory() {
        IdNameVO hist;
        ShippingViewDO data;

        data = manager.getShipping();

        hist = new IdNameVO(data.getId(), Messages.get().shipping());
        HistoryScreen.showHistory(Messages.get().shippingHistory(),
                                  Constants.table().SHIPPING,
                                  hist);

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
            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().shippingItemHistory(),
                                  Constants.table().SHIPPING_ITEM,
                                  refVoList);
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
            for (i = 0; i < count; i++ ) {
                data = man.getTrackingAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getTrackingNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().shippingTrackingHistory(),
                                  Constants.table().SHIPPING_TRACKING,
                                  refVoList);

    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ShippingManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
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
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
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

    private void setProcessShippingData(ShippingViewDO data) throws Exception {
        data.setShippedDate(CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.DAY));
        data.setStatusId(Constants.dictionary().SHIPPING_STATUS_SHIPPED);
    }
}