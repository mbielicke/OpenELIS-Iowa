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
package org.openelis.modules.order1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.manager.OrderManager1;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class VendorOrderScreenUI extends Screen {

    @UiTemplate("VendorOrder.ui.xml")
    interface VendorOrderUiBinder extends UiBinder<Widget, VendorOrderScreenUI> {
    };

    public static final VendorOrderUiBinder uiBinder = GWT.create(VendorOrderUiBinder.class);

    protected OrderManager1                 manager;

    protected ModulePermission              userPermission;

    protected ScreenNavigator<IdNameVO>     nav;

    @UiField
    protected ButtonGroup                   atozButtons;

    @UiField
    protected Table                         atozTable;

    @UiField
    protected Button                        query, previous, next, add, update, commit, abort,
                    atozNext, atozPrev;

    @UiField
    protected Menu                          optionsMenu;

    @UiField
    protected MenuItem                      duplicate, orderHistory, orderItemHistory;

    @UiField
    protected TextBox<Integer>              id, neededDays;

    @UiField
    protected TextBox<String>               vendorAttention, multipleUnit, requestedBy,
                    streetAddress, city, zipCode, externalId;

    @UiField
    protected Calendar                      orderedDate;

    @UiField
    protected Dropdown<Integer>             status, costCenter;

    @UiField
    protected Dropdown<String>              orgState;

    @UiField
    protected AutoComplete                  vendor;

    @UiField
    protected TabLayoutPanel                tabPanel;

    @UiField(provided = true)
    protected VendorOrderItemTabUI          itemTab;

    @UiField(provided = true)
    protected ShippingNotesTabUI            shippingNotesTab;

    @UiField(provided = true)
    protected VendorOrderFillTabUI          fillTab;

    protected HashMap<String, Object>       cache;

    public VendorOrderScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("vendororder");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Vendor Order Screen"));

        try {
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "state",
                                           "inventory_store",
                                           "inventory_unit",
                                           "standard_note_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        itemTab = new VendorOrderItemTabUI(this, bus);
        shippingNotesTab = new ShippingNotesTabUI(this, bus);
        fillTab = new VendorOrderFillTabUI(this, bus);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Vendor Order Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;
        Item<String> srow;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                 userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.lock();
                    query.setPressed(true);
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.lock();
                    update.setPressed(true);
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(true);
            }
        });

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY));
            }
        });
        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderItemHistory.setEnabled(isState(DISPLAY));
            }
        });
        orderItemHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orderItemHistory();
            }
        });

        //
        // screen fields
        //
        addScreenHandler(id, OrderMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? neededDays : zipCode;
            }
        });

        addScreenHandler(neededDays, OrderMeta.getNeededInDays(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                neededDays.setValue(getNeededDays());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeededDays(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                neededDays.setEnabled(isState(QUERY, ADD, UPDATE));
                neededDays.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? status : id;
            }
        });

        addScreenHandler(status, OrderMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                status.setEnabled(isState(QUERY));
                status.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? vendor : neededDays;
            }
        });

        addScreenHandler(vendor, OrderMeta.getOrganizationName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setOrganizationNameSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getOrganizationNameFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                vendor.setEnabled(isState(QUERY, ADD, UPDATE));
                vendor.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? orderedDate : status;
            }
        });

        vendor.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch());
            }
        });

        addScreenHandler(orderedDate, OrderMeta.getOrderedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                orderedDate.setValue(getOrderedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setOrderedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                orderedDate.setEnabled(isState(QUERY, ADD, UPDATE));
                orderedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? vendorAttention : vendor;
            }
        });

        addScreenHandler(vendorAttention,
                         OrderMeta.getOrganizationAttention(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 vendorAttention.setValue(getOrganizationAttention());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setOrganizationAttention(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 vendorAttention.setEnabled(isState(QUERY, ADD, UPDATE));
                                 vendorAttention.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? requestedBy : orderedDate;
                             }
                         });

        addScreenHandler(requestedBy, OrderMeta.getRequestedBy(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                requestedBy.setValue(getRequestedBy());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setRequestedBy(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                requestedBy.setEnabled(isState(QUERY, ADD, UPDATE));
                requestedBy.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? multipleUnit : vendorAttention;
            }
        });

        addScreenHandler(multipleUnit,
                         OrderMeta.getOrderOrganizationOrganizationAddressMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 multipleUnit.setValue(getMultipleUnit());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 multipleUnit.setEnabled(isState(QUERY));
                                 multipleUnit.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? costCenter : requestedBy;
                             }
                         });

        addScreenHandler(costCenter, OrderMeta.getCostCenterId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                costCenter.setValue(getCostCenterId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setCostCenterId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                costCenter.setEnabled(isState(QUERY, ADD, UPDATE));
                costCenter.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? streetAddress : multipleUnit;
            }
        });

        addScreenHandler(streetAddress,
                         OrderMeta.getOrganizationAddressStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 streetAddress.setValue(getStreetAddress());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 streetAddress.setEnabled(isState(QUERY));
                                 streetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? externalId : costCenter;
                             }
                         });

        addScreenHandler(externalId,
                         OrderMeta.getOrganizationAttention(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 externalId.setValue(getExternalId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setExternalId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 externalId.setEnabled(isState(QUERY, ADD, UPDATE));
                                 externalId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? city : streetAddress;
                             }
                         });

        addScreenHandler(city, OrderMeta.getOrganizationAddressCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(getCity());
            }

            public void onStateChange(StateChangeEvent event) {
                city.setEnabled(isState(QUERY));
                city.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? orgState : externalId;
            }
        });

        addScreenHandler(orgState,
                         OrderMeta.getOrganizationAddressState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 orgState.setValue(getState());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 orgState.setEnabled(isState(QUERY));
                                 orgState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? zipCode : city;
                             }
                         });

        addScreenHandler(zipCode,
                         OrderMeta.getOrganizationAddressZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 zipCode.setValue(getZipCode());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 zipCode.setEnabled(isState(QUERY));
                                 zipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? id : orgState;
                             }
                         });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(itemTab, "itemTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return itemTab.getQueryFields();
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                QueryData field;

                window.setBusy(Messages.get().gen_querying());
                /*
                 * this screen should only query for vendor orders
                 */
                field = new QueryData();
                field.setKey(OrderMeta.getType());
                field.setQuery(Constants.order().VENDOR);
                field.setType(QueryData.Type.STRING);
                query.setFields(field);

                query.setRowsPerPage(22);
                try {
                    OrderService1.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                        public void onSuccess(ArrayList<IdNameVO> result) {
                            setQueryResult(result);
                        }

                        public void onFailure(Throwable error) {
                            setQueryResult(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().gen_noRecordsFound());
                                setState(DEFAULT);
                            } else if (error instanceof LastPageException) {
                                window.setError(Messages.get().gen_noMoreRecordInDir());
                            } else {
                                Window.alert("Error: Vendor Order call query failed; " +
                                             error.getMessage());
                                window.setError(Messages.get().gen_queryFailed());
                            }
                        }
                    });
                } catch (Exception e) {
                    Window.alert("Error: Vendor Order call query failed; " + e.getMessage());
                    e.printStackTrace();
                }
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result) {
                        row = new Item<Integer>(2);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getId());
                        row.setCell(1, entry.getName());
                        model.add(row);
                    }
                }
                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                boolean enable;
                enable = isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission();
                atozButtons.setEnabled(enable);
                nav.enable(enable);
            }
        });

        atozButtons.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.setKey(OrderMeta.getId());
                field.setQuery( ((Button)event.getSource()).getAction());
                field.setType(QueryData.Type.INTEGER);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    window.setError(Messages.get().gen_mustCommitOrAbort());
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        // order status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("order_status");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenter.setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(srow);
        }

        orgState.setModel(smodel);
    }

    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        setData();
        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
        window.setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        try {
            manager = OrderService1.get().getInstance(Constants.order().VENDOR);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.severe(e.getMessage());
            window.clearStatus();
            return;
        }
        setData();
        setState(ADD);
        fireDataChange();
        neededDays.setFocus(true);
        window.setDone(Messages.get().gen_enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        window.setBusy(Messages.get().gen_lockForUpdate());
        try {
            manager = OrderService1.get().fetchForUpdate(manager.getOrder().getId(),
                                                         OrderManager1.Load.ITEMS);
            if (Constants.dictionary().ORDER_STATUS_CANCELLED.equals(manager.getOrder()
                                                                            .getStatusId())) {
                Window.alert(Messages.get().order_cancelledOrderCantBeUpdated());
                manager = OrderService1.get().unlock(manager.getOrder().getId(),
                                                     OrderManager1.Load.ITEMS);
                setData();
                setState(DISPLAY);
                fireDataChange();
                window.clearStatus();
                return;

            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
            e.printStackTrace();
            window.clearStatus();
            return;
        }
        setData();
        setState(UPDATE);
        fireDataChange();
        neededDays.setFocus(true);
        window.clearStatus();
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit(false);
    }

    private void commit(boolean ignoreWarning) {
        finishEditing();
        clearErrors();

        if ( !validate()) {
            window.setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (super.state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate(ignoreWarning);
                break;
            case UPDATE:
                commitUpdate(ignoreWarning);
                break;
        }

        cache = null;
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().gen_cancelChanges());

        if (state == QUERY) {
            try {
                manager = null;
                setData();
                setState(DEFAULT);
                fireDataChange();
                window.setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else if (state == ADD) {
            if ( !Window.confirm(Messages.get().order_abortWarning())) {
                window.setDone(Messages.get().gen_enterInformationPressCommit());
                return;
            }
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            window.setDone(Messages.get().gen_addAborted());
        } else if (state == UPDATE) {
            if ( !Window.confirm(Messages.get().order_abortWarning())) {
                window.clearStatus();
                return;
            }
            try {
                manager = OrderService1.get().unlock(manager.getOrder().getId(),
                                                     OrderManager1.Load.ITEMS);
                setData();
                setState(DISPLAY);
                fireDataChange();
                window.setDone(Messages.get().gen_updateAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        }
        cache = null;
    }

    protected void duplicate() {
        try {
            manager = OrderService1.get().duplicate(manager.getOrder().getId());
            setData();
            setState(ADD);
            fireDataChange();
        } catch (Exception ex) {
            Window.alert(ex.getMessage());
        }
    }

    protected void orderHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrder().getId(), manager.getOrder().getId().toString());
        HistoryScreen.showHistory(Messages.get().order_orderHistory(),
                                  Constants.table().ORDER,
                                  hist);
    }

    protected void orderItemHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrderItemViewDO data;

        count = manager.item.count();
        refVoList = new IdNameVO[count];
        for (i = 0; i < count; i++ ) {
            data = manager.item.get(i);
            refVoList[i] = new IdNameVO(data.getId(), data.getInventoryItemName());
        }
        HistoryScreen.showHistory(Messages.get().order_orderItemHistory(),
                                  Constants.table().ORDER_ITEM,
                                  refVoList);
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitUpdate(boolean ignoreWarning) {
        if (state == ADD)
            window.setBusy(Messages.get().gen_adding());
        else
            window.setBusy(Messages.get().gen_updating());

        try {
            manager = OrderService1.get().update(manager, ignoreWarning);
            setData();
            setState(DISPLAY);
            fireDataChange();
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);
            if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning)
                showWarningsDialog(e);
        } catch (Exception e) {
            if (state == ADD)
                Window.alert("commitAdd(): " + e.getMessage());
            else
                Window.alert("commitUpdate(): " + e.getMessage());
            window.clearStatus();
        }
    }

    private void setOrganizationNameSelection() {
        OrganizationDO org;

        org = getShipTo();
        if (org != null)
            vendor.setValue(org.getId(), org.getName());
        else
            vendor.setValue(null, "");
    }

    private void getOrganizationNameFromSelection() {
        AutoCompleteValue row;
        OrganizationDO org;

        row = vendor.getValue();
        if (row == null || row.getId() == null) {
            /*
             * this method is called only when the ship-to changes and if there
             * isn't a ship-to selected currently, then there must have been
             * before, thus it needs to be removed from the manager
             */
            manager.getOrder().setOrganizationId(null);
            manager.getOrder().setOrganization(null);
            vendor.setValue(null, "");
            multipleUnit.setValue(null);
            streetAddress.setValue(null);
            city.setValue(null);
            orgState.setValue(null);
            zipCode.setValue(null);
        } else {
            org = (OrganizationDO)row.getData();
            manager.getOrder().setOrganizationId(org.getId());
            manager.getOrder().setOrganization(org);
            vendor.setValue(org.getId(), org.getName());
            multipleUnit.setValue(org.getAddress().getMultipleUnit());
            streetAddress.setValue(org.getAddress().getStreetAddress());
            city.setValue(org.getAddress().getCity());
            orgState.setValue(org.getAddress().getState());
            zipCode.setValue(org.getAddress().getZipCode());

            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                showHoldRefuseWarning(org.getId(), org.getName());
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }

    private void getOrganizationMatches(String match) {
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        window.setBusy();
        try {
            list = OrganizationService.get()
                                      .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(4);
                data = list.get(i);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());
                row.setCell(1, data.getAddress().getStreetAddress());
                row.setCell(2, data.getAddress().getCity());
                row.setCell(3, data.getAddress().getState());

                model.add(row);
            }
            vendor.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getOrder().getId();
    }

    private Integer getNeededDays() {
        if (manager == null)
            return null;
        return manager.getOrder().getNeededInDays();
    }

    private void setNeededDays(Integer neededDays) {
        manager.getOrder().setNeededInDays(neededDays);
    }

    private String getMultipleUnit() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getMultipleUnit();
    }

    private String getOrganizationAttention() {
        if (manager == null)
            return null;
        return manager.getOrder().getOrganizationAttention();
    }

    private void setOrganizationAttention(String organizationAttention) {
        manager.getOrder().setOrganizationAttention(organizationAttention);
    }

    private String getExternalId() {
        if (manager == null)
            return null;
        return manager.getOrder().getExternalOrderNumber();
    }

    private void setExternalId(String externalId) {
        manager.getOrder().setExternalOrderNumber(externalId);
    }

    private String getRequestedBy() {
        if (manager == null)
            return null;
        return manager.getOrder().getRequestedBy();
    }

    private void setRequestedBy(String requestedBy) {
        manager.getOrder().setRequestedBy(requestedBy);
    }

    private String getStreetAddress() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getStreetAddress();
    }

    private String getCity() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getCity();
    }

    private String getZipCode() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getZipCode();
    }

    private Datetime getOrderedDate() {
        if (manager == null)
            return null;
        return manager.getOrder().getOrderedDate();
    }

    private void setOrderedDate(Datetime date) {
        manager.getOrder().setOrderedDate(date);
    }

    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getOrder().getStatusId();
    }

    private void setStatusId(Integer statusId) {
        manager.getOrder().setStatusId(statusId);
    }

    private Integer getCostCenterId() {
        if (manager == null)
            return null;
        return manager.getOrder().getCostCenterId();
    }

    private void setCostCenterId(Integer costCenterId) {
        manager.getOrder().setCostCenterId(costCenterId);
    }

    private String getState() {
        OrganizationDO org;

        org = getShipTo();
        if (org == null)
            return null;
        return org.getAddress().getState();
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        itemTab.setData(manager);
        shippingNotesTab.setData(manager);
        fillTab.setData(manager);
    }

    private boolean fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().gen_fetching());
            try {

                manager = OrderService1.get().fetchById(id, OrderManager1.Load.ITEMS);
                setData();
                setState(DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(Messages.get().gen_noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                return false;
            }
        }
        fireDataChange();
        window.clearStatus();

        return true;
    }

    /**
     * returns the ship-to organization from the manager if there is one
     */
    private OrganizationDO getShipTo() {
        if (manager == null)
            return null;

        return manager.getOrder().getOrganization();
    }

    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(name));
    }

    /**
     * Shows a list of warnings in the form of a confirm dialog. Specific
     * screens need to override the commitWithWarnings() method to catch the
     * user's response.
     */
    private void showWarningsDialog(ValidationErrorsList warnings) {
        String warningText = "There are warnings on the screen:" + "\n";

        for (Exception ex : warnings.getErrorList()) {
            if (ex instanceof Warning)
                warningText += " * " + ex.getMessage() + "\n";
        }
        warningText += "\n" + "Press Ok to commit anyway or cancel to fix these warnings.";

        if (Window.confirm(warningText))
            commit(true);
    }
}