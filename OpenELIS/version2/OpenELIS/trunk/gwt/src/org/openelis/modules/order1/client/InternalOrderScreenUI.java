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
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.manager.OrderManager1;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
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
import com.google.gwt.user.client.ui.Widget;

public class InternalOrderScreenUI extends Screen {

    @UiTemplate("InternalOrder.ui.xml")
    interface InternalOrderUiBinder extends UiBinder<Widget, InternalOrderScreenUI> {
    };

    public static final InternalOrderUiBinder      uiBinder   = GWT.create(InternalOrderUiBinder.class);

    protected OrderManager1                        manager;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected ButtonGroup                          atozButtons;

    @UiField
    protected Table                                atozTable;

    @UiField
    protected Button                               query, previous, next, add, update, commit,
                    abort, optionsButton, atozNext, atozPrev;

    @UiField
    protected Menu                                 optionsMenu;

    @UiField
    protected MenuItem                             duplicate, orderHistory, orderItemHistory;

    @UiField
    protected TextBox<Integer>                     id, neededDays;

    @UiField
    protected TextBox<String>                      requestedBy;

    @UiField
    protected Calendar                             orderedDate;

    @UiField
    protected Dropdown<Integer>                    status, costCenter;

    @UiField
    protected TabLayoutPanel                       tabPanel;

    @UiField(provided = true)
    protected InternalOrderItemTabUI               itemTab;

    @UiField(provided = true)
    protected ShippingNotesTabUI                   shippingNotesTab;

    @UiField(provided = true)
    protected InternalOrderFillTabUI               fillTab;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<OrderManager1>       addCall, fetchForUpdateCall, updateCall,
                    fetchByIdCall, unlockCall, duplicateCall;

    protected OrderManager1.Load                   elements[] = {OrderManager1.Load.ITEMS};

    public InternalOrderScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("internalorder");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Internal Order Screen"));

        try {
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "inventory_store",
                                           "inventory_unit",
                                           "standard_note_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        itemTab = new InternalOrderItemTabUI(this);
        shippingNotesTab = new ShippingNotesTabUI(this);
        fillTab = new InternalOrderFillTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Internal Order Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

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
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY) && userPermission.hasAddPermission());
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
                return forward ? neededDays : costCenter;
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
                return forward ? requestedBy : neededDays;
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
                return forward ? orderedDate : status;
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
                return forward ? id : orderedDate;
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(itemTab, "itemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                itemTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                itemTab.setState(event.getState());
            }

            public Object getQuery() {
                return itemTab.getQueryFields();
            }
        });

        addScreenHandler(shippingNotesTab, "shippingNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                shippingNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                shippingNotesTab.setState(event.getState());
            }
        });

        addScreenHandler(fillTab, "fillTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                fillTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                fillTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                QueryData field;

                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
                        }

                        public void notFound() {
                            setQueryResult(null);
                            setState(DEFAULT);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Internal Order call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                /*
                 * this screen should only query for internal orders
                 */
                field = new QueryData(OrderMeta.getType(),
                                      QueryData.Type.STRING,
                                      Constants.order().INTERNAL);
                query.setFields(field);
                query.setRowsPerPage(25);
                OrderService1.get().query(query, queryCall);
            }

            public boolean fetch(IdNameVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
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
                    setError(Messages.get().gen_mustCommitOrAbort());
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
        list = CategoryCache.getBySystemName("order_status");
        for (DictionaryDO d : list) {
            /*
             * we're not showing recurring orders on this screen
             */
            if (Constants.dictionary().ORDER_STATUS_RECURRING.equals(d.getId()))
                continue;
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("cost_centers");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        costCenter.setModel(model);
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
        setDone(Messages.get().gen_enterFieldsToQuery());
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
        if (addCall == null) {
            addCall = new AsyncCallbackUI<OrderManager1>() {
                public void success(OrderManager1 result) {
                    manager = result;
                    setData();
                    setState(ADD);
                    fireDataChange();
                    neededDays.setFocus(true);
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }

                public void failure(Throwable error) {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        OrderService1.get().getInstance(Constants.order().INTERNAL, addCall);
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<OrderManager1>() {
                public void success(OrderManager1 result) {
                    manager = result;
                    if (Constants.dictionary().ORDER_STATUS_CANCELLED.equals(manager.getOrder()
                                                                                    .getStatusId())) {
                        Window.alert(Messages.get().order_cancelledOrderCantBeUpdated());
                        try {
                            manager = OrderService1.get().unlock(manager.getOrder().getId(),
                                                                 OrderManager1.Load.ITEMS);
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage()
                                                                           : "null", e);
                        }
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                    } else {
                        setData();
                        setState(UPDATE);
                        fireDataChange();
                        neededDays.setFocus(true);
                    }
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        OrderService1.get()
                     .fetchForUpdate(manager.getOrder().getId(), elements, fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit(false);
    }

    private void commit(boolean ignoreWarning) {
        Validation validation;

        finishEditing();
        clearErrors();

        validation = validate();

        if (validation.getStatus() != VALID) {
            setError(Messages.get().gen_correctErrors());
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
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                manager = null;
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                clearStatus();
            }
        } else if (isState(ADD)) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<OrderManager1>() {
                    public void success(OrderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            OrderService1.get().unlock(manager.getOrder().getId(), elements, unlockCall);
        }
    }

    protected void duplicate() {
        if (duplicateCall == null) {
            duplicateCall = new AsyncCallbackUI<OrderManager1>() {
                public void success(OrderManager1 result) {
                    manager = result;
                    setData();
                    setState(ADD);
                    fireDataChange();
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        OrderService1.get().duplicate(manager.getOrder().getId(), duplicateCall);
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

    protected void commitUpdate(final boolean ignoreWarning) {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<OrderManager1>() {
                public void success(OrderManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning)
                        showWarningsDialog(e);
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        OrderService1.get().update(manager, ignoreWarning, updateCall);
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

    private String getRequestedBy() {
        if (manager == null)
            return null;
        return manager.getOrder().getRequestedBy();
    }

    private void setRequestedBy(String requestedBy) {
        manager.getOrder().setRequestedBy(requestedBy);
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

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        itemTab.setData(manager);
        shippingNotesTab.setData(manager);
        fillTab.setData(manager);
    }

    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<OrderManager1>() {
                    public void success(OrderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            OrderService1.get().fetchById(id, elements, fetchByIdCall);
        }
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