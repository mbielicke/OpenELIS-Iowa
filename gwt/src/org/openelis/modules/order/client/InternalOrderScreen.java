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
import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
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
import org.openelis.gwt.services.CalendarService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.history.client.HistoryScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InternalOrderScreen extends Screen {
    private OrderManager     manager;
    private ModulePermission userPermission;

    private ButtonGroup      atoz;
    private ScreenNavigator  nav;

    private ItemTab          itemTab;
    private FillTab          fillTab;
    private ShipNoteTab      shipNoteTab;
    private Tabs             tab;

    private AppButton        queryButton, previousButton, nextButton, addButton,
                    updateButton, commitButton, abortButton;
    private MenuItem         duplicate, orderHistory, itemHistory;
    private TextBox          id, neededInDays, requestedBy;
    private CalendarLookUp   orderedDate;
    private Dropdown<Integer> statusId, costCenterId;
    private TabPanel          tabPanel;

    private enum Tabs {
        ITEM, FILL, SHIPNOTE
    };

    public InternalOrderScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InternalOrderDef.class));

        userPermission = UserCache.getPermission().getModule("internalorder");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Internal Order Screen");

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
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "inventory_store",
                                           "inventory_unit");
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
                add();
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

        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                 userPermission.hasAddPermission());
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
                if (tab == Tabs.SHIPNOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                shipNoteTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(Query query) {
                QueryData field;

                window.setBusy(consts.get("querying"));
                // this screen should only query for internal orders
                field = new QueryData();
                field.key = OrderMeta.getType();
                field.query = OrderManager.TYPE_INTERNAL;
                field.type = QueryData.Type.STRING;
                query.setFields(field);

                query.setRowsPerPage(20);
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
                                             window.setError("No more records in this direction");
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
                         userPermission.hasSelectPermission();
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
            //
            // we're not showing recurring orders on this screen
            //
            if ("order_status_recurring".equals(d.getSystemName()))
                continue;
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
        data.setType(OrderManager.TYPE_INTERNAL);

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(neededInDays);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(neededInDays);
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
            field.query = OrderManager.TYPE_INTERNAL;
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
        try {
            window.setBusy(consts.get("fetching"));

            manager = OrderService.get().duplicate(manager.getOrder().getId());

            itemTab.setManager(manager);
            shipNoteTab.setManager(manager);

            itemTab.draw();
            shipNoteTab.draw();

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

    protected void orderHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrder().getId(), manager.getOrder()
                                                               .getId()
                                                               .toString());
        HistoryScreen.showHistory(consts.get("orderHistory"),
                                  Constants.table().ORDER,
                                  hist);
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
                                  Constants.table().ORDER_ITEM,
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
                    case SHIPNOTE:
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
            case FILL:
                fillTab.draw();
                break;
            case SHIPNOTE:
                shipNoteTab.draw();
                break;
        }
    }
}