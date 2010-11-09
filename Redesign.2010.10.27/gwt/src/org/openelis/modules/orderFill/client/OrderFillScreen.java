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

package org.openelis.modules.orderFill.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.InventoryItemCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.table.Column;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.order.client.CustomerNoteTab;
import org.openelis.modules.order.client.ShipNoteTab;
import org.openelis.modules.shipping.client.ShippingScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;

public class OrderFillScreen extends Screen {
    private ModulePermission                   userPermission;

    private ItemTab                            itemTab;
    private CustomerNoteTab                    custNoteTab;
    private ShipNoteTab                        shipNoteTab;
    private Tabs                               tab;

    private Button                             queryButton, updateButton, processButton,
                                               commitButton, abortButton;
    private MenuItem                           shippingInfo;
    private Table                              orderTable;
    private Tree                               itemsTree;
    private TabPanel                           tabPanel;

    private ShippingManager                    shippingManager;
    private ShippingScreen                     shippingScreen;

    private boolean                            treeValid;
    private Integer                            status_pending, status_processed;
    private HashMap<Row, OrderViewDO>          orderMap;
    private HashMap<Integer, OrderManager>     combinedMap;

    private ScreenService                      shippingService;

    private enum Tabs {
        ITEM, SHIP_NOTE, CUSTOMER_NOTE
    };

    public OrderFillScreen() throws Exception {
        super((ScreenDefInt)GWT.create(OrderFillDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        shippingService = new ScreenService("controller?service=org.openelis.modules.shipping.server.ShippingService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("fillorder");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Order Fill Screen");

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

        try {
            DictionaryCache.preloadByCategorySystemNames("order_status", "order_ship_from");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("Order Fill Screen: missing dictionary entry; " + e.getMessage());
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

        processButton = (Button)def.getWidget("process");
        addScreenHandler(processButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                processButton.setEnabled(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.UPDATE).contains(event.getState()));
            }
        });

        shippingInfo = (MenuItem)def.getWidget("shippingInfo");
        addScreenHandler(shippingInfo, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                shippingInfo.setEnabled(false);
            }
        });
        
        shippingInfo.addCommand(new Command() {
			public void execute() {
				shippingInfo();
			}
		});

        orderTable = (Table)def.getWidget("orderTable");

        addScreenHandler(orderTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                Column process;
                ArrayList list;

                orderTable.setEnabled(true);
                orderTable.setQueryMode(event.getState() == State.QUERY);

                if (state == State.QUERY) {
                    process = orderTable.getColumnAt(0);
                    process.setEnabled(false);

                    list = new ArrayList<Integer>();
                    list.add(status_pending);
                    orderTable.setValueAt(0, 2, list);
                }
            }
        });

        orderTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Row row;
                OrderManager man;
                OrderViewDO data, prevSelData;
                String val, type;
                boolean cancel;
                Integer status;

                if (orderMap == null) {
                    event.cancel();
                    return;
                }

                c = event.getCol();
                r = event.getRow();
                row = orderTable.getRowAt(r);
                data = orderMap.get(row);
                val = (String)orderTable.getValueAt(r, 0);
                cancel = false;
                status = data.getStatusId();
                type = data.getType();

                if (state == State.UPDATE) {
                    if (c > 0) {
                        cancel = true;
                        //
                        // if the order has been selected for processing, then
                        // we set the state of the tab showing customer notes to
                        // Update, so that the data in the widgets in it can be
                        // edited, otherwise we set the state to Display, so that
                        // the data can't be edited 
                        //
                        if ("Y".equals(val))
                            custNoteTab.setState(State.UPDATE);
                        else
                            custNoteTab.setState(State.DISPLAY);
                    } else {
                        prevSelData = getProcessShipData();
                        if ( (prevSelData != null) && ("N".equals(val)) &&
                            DataBaseUtil.isDifferent(data.getOrganizationId(),
                                                     prevSelData.getOrganizationId())) {
                            com.google.gwt.user.client.Window.alert(consts.get("sameShipToOrderCombined"));
                            custNoteTab.setState(State.DISPLAY);
                            cancel = true;
                        }
                    }
                } else {
                    if (status_processed.equals(status) && OrderManager.TYPE_SEND_OUT.equals(type))
                        shippingInfo.setEnabled(true);
                    else
                        shippingInfo.setEnabled(false);
                    cancel = true;
                }

                shipNoteTab.setState(State.DISPLAY);

                //
                // this logic makes sure that whenever we disallow a user from
                // editing a cell, we fill the tabs with the data stored in the
                // OrderManager that is set as "data" of this row, we have to do
                // this here because CellEditedEvent won't be fired
                //                
                if (cancel) {
                    man = null;

                    if (row.getData() == null)
                        row.setData(fetchById(data));

                    man = (OrderManager)row.getData();
                    itemTab.setManager(man, combinedMap);
                    shipNoteTab.setManager(man);
                    custNoteTab.setManager(man);

                    drawTabs();
                    event.cancel();
                }
            }
        });

        orderTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String val;
                Row row;
                OrderViewDO data;
                OrderManager man;

                r = event.getRow();
                c = event.getCol();

                if (c == 0) {
                    val = (String)orderTable.getValueAt(r, c);
                    row = orderTable.getRowAt(r);
                    data = orderMap.get(row);

                    if ("Y".equals(val) && (combinedMap.get(data.getId()) == null)) {
                        try {
                            man = (OrderManager)row.getData();
                            if (man == null) {
                                man = OrderManager.getInstance();
                                man.setOrder(data);
                            }

                            window.setBusy(consts.get("lockForUpdate"));
                            row.setData(man.fetchForUpdate());
                            man = (OrderManager)row.getData();
                            window.clearStatus();

                            combinedMap.put(data.getId(), man);
                            itemTab.setManager(man, combinedMap);

                            //
                            // we do this here in order to make sure that the
                            // tree showing order items always gets loaded so that
                            // validation for processing orders always takes place
                            // because it relies on the data in the tree
                            //
                            if (tab != Tabs.ITEM)
                                itemTab.draw();

                            shipNoteTab.setManager(man);

                            if (OrderManager.TYPE_SEND_OUT.equals(data.getType()))
                                custNoteTab.setState(State.UPDATE);
                            else
                                custNoteTab.setState(State.DISPLAY);
                            custNoteTab.setManager(man);
                            drawTabs();
                        } catch (Exception e) {
                            com.google.gwt.user.client.Window.alert(e.getMessage());
                            e.printStackTrace();
                        }
                    } else if ("N".equals(val) && (combinedMap.get(data.getId()) != null)) {
                        custNoteTab.setState(State.DISPLAY);
                        removeFromCombined(row, data);
                        window.setDone(consts.get("updateAborted"));
                    }
                }
            }
        });

        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;
                Row row;
                OrderManager man;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                row = orderTable.getRowAt(orderTable.getSelectedRow());
                if (row != null) {
                    man = (OrderManager)row.getData();
                    if (i == 0 && !treeValid && combinedMap != null &&
                        combinedMap.get(man.getOrder().getId()) != null)
                        itemTab.validate();
                }
                window.clearStatus();
            }
        });

        itemTab = new ItemTab(def, window, this);
        addScreenHandler(itemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.ITEM)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTab.setState(event.getState());
            }
        });

        itemsTree = (Tree)def.getWidget("itemsTree");

        shipNoteTab = new ShipNoteTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(shipNoteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
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
                if (tab == Tabs.CUSTOMER_NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                custNoteTab.setState(event.getState());
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
        ArrayList<Item<String>> smodel;
        List<DictionaryDO> list;
        Item<Integer> row;
        Dropdown<Integer> status, shipFrom, type;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_status");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        status = ((Dropdown<Integer>)orderTable.getColumnWidget(OrderMeta.getStatusId()));
        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("order_ship_from");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        shipFrom = ((Dropdown<Integer>)orderTable.getColumnWidget(OrderMeta.getShipFromId()));
        shipFrom.setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        smodel.add(new Item<String>(OrderManager.TYPE_INTERNAL, consts.get("internal")));
        smodel.add(new Item<String>(OrderManager.TYPE_SEND_OUT, consts.get("sendOut")));

        type = ((Dropdown)orderTable.getColumnWidget(OrderMeta.getType()));
        type.setModel(model);

        try {
            status_pending = DictionaryCache.getIdFromSystemName("order_status_pending");
            status_processed = DictionaryCache.getIdFromSystemName("order_status_processed");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    protected void query() {
        OrderManager man;

        man = OrderManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        itemTab.setManager(man, combinedMap);
        shipNoteTab.setManager(man);
        custNoteTab.setManager(man);

        itemTab.draw();
        shipNoteTab.draw();
        custNoteTab.draw();

        orderMap = null;
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void update() {
        setState(State.UPDATE);
        shipNoteTab.setState(State.DISPLAY);
        custNoteTab.setState(State.DISPLAY);
    }

    protected void commit() {
        Query query;
        Set<Integer> set;
        Iterator<Integer> iter;
        OrderManager man;
        OrderViewDO data;
        Node model;
        ShippingManager shippingManager;

        shippingManager = null;

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            query = new Query();
            query.setFields(getQueryFields());

            executeQuery(query);
            combinedMap = new HashMap<Integer, OrderManager>();
            setState(State.DISPLAY);
        } else if (state == State.UPDATE) {
            data = getProcessShipData();
            if (data == null) {
                com.google.gwt.user.client.Window.alert(consts.get("noOrdersSelectForProcess"));
                return;
            } else {
                loadProcessRows();
                model = itemsTree.getRoot();

                if (model == null || model.size() == 0) {
                    com.google.gwt.user.client.Window.alert(consts.get("noItemsToProcess"));
                    return;
                }
            }
            window.setBusy(consts.get("updating"));
            set = combinedMap.keySet();
            iter = set.iterator();
            try {
                validateQuantityOnHand();
                while (iter.hasNext()) {
                    man = combinedMap.get(iter.next());
                    man.getOrder().setStatusId(status_processed);
                    man = man.update();
                }
                shippingManager = getShippingManager(data);

                releaseLocks();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));

                showShippingScreen(shippingManager, State.ADD);
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        OrderManager man;

        man = OrderManager.getInstance();

        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            itemTab.setManager(man, combinedMap);
            shipNoteTab.setManager(man);
            custNoteTab.setManager(man);

            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.UPDATE) {
            releaseLocks();
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updateAborted"));
        }
    }

    protected void shippingInfo() {
        OrderManager order;
        OrderViewDO data;
        Row row;

        try {
            row = orderTable.getRowAt(orderTable.getSelectedRow());
            order = (OrderManager)row.getData();
            data = order.getOrder();

            window.setBusy(consts.get("fetching"));

            shippingService.call("fetchByOrderId", data.getId(),
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
                                             com.google.gwt.user.client.Window.alert(e.getMessage());
                                             window.clearStatus();
                                         }
                                     }

                                     public void onFailure(Throwable error) {
                                         shippingManager = null;
                                         error.printStackTrace();
                                         com.google.gwt.user.client.Window.alert("Error: Fetch failed; " + error.getMessage());
                                         window.clearStatus();
                                     }
                                 });

            showShippingScreen(shippingManager, State.DISPLAY);
        } catch (Throwable e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.clearStatus();
            return;
        }

        window.clearStatus();
    }

    private void validateQuantityOnHand() throws ValidationErrorsList {
        Node parent, child, root; 
        ArrayList<Node> items;
        OrderItemViewDO ordItem;
        InventoryItemDO invItem;
        InventoryXUseViewDO data;
        Integer sum, locationId;
        HashMap<Integer, Integer> locationSumMap;
        String[] name;
        ArrayList<String[]> names;
        ArrayList<Integer> locIds;
        ValidationErrorsList list;
        FormErrorException exc;
        int i;

        root = itemsTree.getRoot();
        names = new ArrayList<String[]>();
        locationSumMap = new HashMap<Integer, Integer>();
        list = new ValidationErrorsList();
        locIds = new ArrayList<Integer>();
        invItem = null;

        for (i = 0; i < root.getChildCount(); i++ ) {
            parent = root.getChildAt(i);
            ordItem = (OrderItemViewDO)parent.getData();
            try {
                invItem = InventoryItemCache.getActiveInventoryItemFromId(ordItem.getInventoryItemId());
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                e.printStackTrace();
            }

            if (invItem == null || "Y".equals(invItem.getIsNotInventoried()))
                continue;

            if ( !parent.isOpen())
                itemsTree.toggle(parent);
            items = parent.children();

            if (items != null && items.size() > 0) {
                for (int j = 0; j < items.size(); j++ ) {
                    child = items.get(j);
                    data = (InventoryXUseViewDO)child.getData();
                    locationId = data.getInventoryLocationId();

                    sum = locationSumMap.get(locationId);
                    if (sum == null)
                        sum = 0;

                    sum += data.getQuantity();

                    if (sum > data.getInventoryLocationQuantityOnhand()) {
                        if ( !locIds.contains(locationId)) {
                            name = new String[2];
                            name[0] = data.getInventoryItemName();
                            name[1] = data.getStorageLocationName();
                            locIds.add(locationId);
                            names.add(name);
                        }
                    }
                    locationSumMap.put(locationId, sum);
                }
            }
        }

        for (i = 0; i < names.size(); i++ ) {
            exc = new FormErrorException("totalItemsMoreThanQtyOnHandException", names.get(i));
            list.add(exc);
        }

        if (list.size() > 0)
            throw list;
    }

    private OrderManager fetchById(OrderViewDO data) {
        OrderManager man;

        man = null;
        window.setBusy(consts.get("fetching"));
        try {
            man = OrderManager.fetchById(data.getId());
        } catch (NotFoundException e) {
            man = OrderManager.getInstance();
            window.setDone(consts.get("noRecordsFound"));
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
            e.printStackTrace();
        }
        window.clearStatus();

        return man;
    }

    private void drawTabs() {
        switch (tab) {
            case ITEM:
                itemTab.draw();
                break;
            case SHIP_NOTE:
                shipNoteTab.draw();
                break;
            case CUSTOMER_NOTE:
                custNoteTab.draw();
                break;
        }
    }

    public boolean validate() {
        treeValid = itemTab.validate();
        return treeValid && super.validate();
    }

    private void executeQuery(Query query) {
        window.setBusy(consts.get("querying"));

        service.callList("queryOrderFill", query, new AsyncCallback<ArrayList<OrderViewDO>>() {
            public void onSuccess(ArrayList<OrderViewDO> result) {
                ArrayList<Row> model;
                Datetime now;
                Row row;

                orderTable.setQueryMode(false);
                model = null;

                now = null;

                if (result != null) {
                    model = new ArrayList<Row>();
                    try {
                        now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert("Order Fill Datetime: " + e.getMessage());
                    }

                    orderMap = new HashMap<Row, OrderViewDO>();

                    for (OrderViewDO data : result) {
                        row = addByLeastNumDaysLeft(data, model, now);
                        orderMap.put(row, data);
                    }

                    orderTable.setModel(model);
                }

                window.clearStatus();
            }

            public void onFailure(Throwable error) {
                orderTable.setModel(null);
                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));
                    setState(State.DEFAULT);
                } else if (error instanceof LastPageException) {
                    window.setError(consts.get("noMoreRecordInDir"));
                } else {
                    com.google.gwt.user.client.Window.alert("Error: Order call query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }
            }
        });
    }

    private int daysBetween(Date startDate, Date endDate) {
        return (int) ( (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
    }

    /**
     * This method adds new rows to the model for the table displaying order
     * records by the number of days left between the desired order processing
     * date and the current date, in a descending order; it also return the
     * newly added row
     */
    private Row addByLeastNumDaysLeft(OrderViewDO data,
                                      ArrayList<Row> model,
                                      Datetime now) {
        Row row, modelRow;
        Datetime ordDate;
        int num, diff, val, mrowVal, size;

        row = getOrderRowFromOrder(data, now, null);
        ordDate = data.getOrderedDate();
        num = data.getNeededInDays();
        diff = daysBetween(ordDate.getDate(), now.getDate());

        // days left between the desired order processing date and the current
        // date
        val = num - diff;
        size = model.size();

        if (size == 0) {
            model.add(row);
        } else {
            for (int i = 0; i < size; i++ ) {
                modelRow = model.get(i);
                mrowVal = (Integer)modelRow.getCell(9);
                if (val <= mrowVal) {
                    model.add(i, row);
                    break;
                } else if (i == size - 1) {
                    model.add(row);
                }
            }
        }
        return row;
    }

    private void releaseLocks() {
        String val;
        ArrayList<? extends Row> model;
        Row row;
        OrderViewDO data;

        model = orderTable.getModel();

        for (int i = 0; i < model.size(); i++ ) {
            row = model.get(i);
            val = (String)row.getCell(0);

            if ("Y".equals(val)) {
                data = orderMap.get(row);
                removeFromCombined(row, data);
                orderTable.setValueAt(i, 0, "N");
            }
        }
    }

    private void removeFromCombined(Row row, OrderViewDO data) {
        OrderManager man;
        Datetime now;

        try {
            man = (OrderManager)row.getData();
            man = man.abortUpdate();
            row.setData(man);
            combinedMap.remove(data.getId());
            itemTab.setManager(man, combinedMap);
            shipNoteTab.setManager(man);
            custNoteTab.setManager(man);
            drawTabs();
            try {
                now = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
                getOrderRowFromOrder(man.getOrder(), now, row);
                orderMap.put(row, man.getOrder());
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("OrderAdd Datetime: " + e.getMessage());
            }

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private OrderViewDO getProcessShipData() {
        OrderViewDO data, order;
        Set<Integer> set;
        Iterator<Integer> iter;
        OrderManager man;

        data = null;

        if (combinedMap == null || combinedMap.size() == 0)
            return data;

        set = combinedMap.keySet();
        iter = set.iterator();

        man = combinedMap.get(iter.next());
        order = man.getOrder();

        data = new OrderViewDO();
        data.setId(order.getId());
        data.setShipFromId(order.getShipFromId());
        data.setOrganizationId(order.getOrganizationId());
        data.setOrganization(order.getOrganization());
        data.setType(order.getType());
        data.setStatusId(order.getStatusId());
        return data;
    }

    private void createShippingItems(ShippingManager manager,
                                     HashMap<Integer, OrderManager> combinedMap) {
        ShippingItemManager man;
        OrderManager orderMan;
        OrderItemManager orderItemMan;
        OrderFillManager fillMan;
        OrderItemViewDO orderItemData;
        InventoryItemDO invItem;
        InventoryXUseViewDO data;
        ShippingItemDO shippingItem;
        Set<Integer> set;
        Iterator<Integer> iter;
        int i, j, index;

        if (combinedMap == null)
            return;

        try {
            man = manager.getItems();
            set = combinedMap.keySet();
            iter = set.iterator();
            invItem = null;

            while (iter.hasNext()) {
                orderMan = combinedMap.get(iter.next());
                orderItemMan = orderMan.getItems();
                fillMan = orderMan.getFills();

                for (i = 0; i < orderItemMan.count(); i++ ) {
                    orderItemData = orderItemMan.getItemAt(i);
                    try {
                        invItem = InventoryItemCache.getActiveInventoryItemFromId(orderItemData.getInventoryItemId());
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                        e.printStackTrace();
                    }
                    //
                    // Since order items that have their inventory items flagged
                    // as "is not inventoried" don't get inventory x use records
                    // created for them, they won't get diplayed in one of the
                    // shipping items if only inventory x use records were to be
                    // used to create shipping items. Thus the following code
                    // makes
                    // sure that if an order item is associated with such an
                    // inventory item a shipping item gets created for it from
                    // the information in the order item itself.
                    //
                    if (invItem != null && "Y".equals(invItem.getIsNotInventoried())) {
                        index = man.addItem();
                        shippingItem = man.getItemAt(index);
                        shippingItem.setQuantity(orderItemData.getQuantity());
                        shippingItem.setDescription(consts.get("orderNum") + " " +
                                                    orderItemData.getOrderId() + ": " +
                                                    orderItemData.getInventoryItemName());
                        shippingItem.setReferenceId(orderItemData.getId());
                        shippingItem.setReferenceTableId(ReferenceTable.ORDER_ITEM);
                        continue;
                    }

                    for (j = 0; j < fillMan.count(); j++ ) {
                        data = fillMan.getFillAt(j);
                        if (orderItemData.getId().equals(data.getOrderItemId())) {
                            index = man.addItem();
                            shippingItem = man.getItemAt(index);
                            shippingItem.setQuantity(data.getQuantity());
                            shippingItem.setDescription(consts.get("orderNum") + " " +
                                                        data.getOrderItemOrderId() + ": " +
                                                        data.getInventoryItemName());
                            shippingItem.setReferenceId(data.getOrderItemId());
                            shippingItem.setReferenceTableId(ReferenceTable.ORDER_ITEM);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }

    }

    private void loadProcessRows() {
        Row row;
        OrderManager man;

        row = orderTable.getRowAt(orderTable.getSelectedRow());
        man = null;

        if (row != null && "Y".equals(row.getCell(0))) {
            man = (OrderManager)row.getData();
        } else {
            for (int i = 0; i < orderTable.getRowCount(); i++ ) {
                row = orderTable.getRowAt(i);
                if ("Y".equals(row.getCell(0))) {
                    man = (OrderManager)row.getData();
                    orderTable.selectRowAt(i);
                    break;
                }
            }
        }

        if (man != null) {
            itemTab.setManager(man, combinedMap);
            custNoteTab.setManager(man);
            shipNoteTab.setManager(man);

            drawTabs();
        }
    }

    private Row getOrderRowFromOrder(OrderViewDO data, Datetime now, Row row) {
        OrganizationDO organization;
        Datetime ordDate;
        int num, diff, val, index;

        if (row == null) {
            row = new Row(11);
            //row.setKey(data.getId());
            row.setCell(0,"N");
            row.setCell(1,data.getId());
            row.setCell(2,data.getStatusId());
            ordDate = data.getOrderedDate();
            row.setCell(3,ordDate);
            row.setCell(4,data.getShipFromId());
            row.setCell(5,data.getRequestedBy());
            organization = data.getOrganization();

            if (organization != null)
                row.setCell(6,organization.getName());
            row.setCell(7,data.getDescription());

            num = data.getNeededInDays();
            row.setCell(8,num);

            diff = daysBetween(ordDate.getDate(), now.getDate());

            // days left between the desired order processing date and the
            // current date
            val = num - diff;

            row.setCell(9,val);
            row.setCell(10,data.getType());
        } else {
            index = orderTable.getModel().indexOf(row);
            //row.key = data.getId();
            orderTable.setValueAt(index, 0, "N");
            orderTable.setValueAt(index, 1, data.getId());
            orderTable.setValueAt(index, 2, data.getStatusId());
            ordDate = data.getOrderedDate();
            orderTable.setValueAt(index, 3, ordDate);
            orderTable.setValueAt(index, 4, data.getShipFromId());
            orderTable.setValueAt(index, 5, data.getRequestedBy());
            organization = data.getOrganization();

            if (organization != null)
                orderTable.setValueAt(index, 6, organization.getName());
            orderTable.setValueAt(index, 7, data.getDescription());

            num = data.getNeededInDays();
            orderTable.setValueAt(index, 8, num);

            diff = daysBetween(ordDate.getDate(), now.getDate());

            // days left between the desired order processing date and the
            // current date
            val = num - diff;

            orderTable.setValueAt(index, 9, val);
            orderTable.setValueAt(index, 10, data.getType());
        }
        return row;
    }

    private ShippingManager getShippingManager(OrderViewDO data) {
        ShippingManager shippingManager;
        ShippingViewDO shipping;

        shippingManager = null;
        if (OrderManager.TYPE_SEND_OUT.equals(data.getType()) &&
            status_pending.equals(data.getStatusId())) {
            shipping = new ShippingViewDO();
            shipping.setShippedFromId(data.getShipFromId());
            shipping.setShippedToId(data.getOrganizationId());
            shipping.setShippedTo(data.getOrganization());
            shippingManager = ShippingManager.getInstance();
            shippingManager.setShipping(shipping);
            createShippingItems(shippingManager, combinedMap);
        }

        return shippingManager;
    }

    private void showShippingScreen(ShippingManager manager, State state) throws Exception {
        ModalWindow modal;
        if (manager != null) {
            modal = new ModalWindow();
            modal.setName(consts.get("shipping"));
            if (shippingScreen == null)
                shippingScreen = new ShippingScreen(modal);

            modal.setContent(shippingScreen);
            shippingScreen.loadShippingData(manager, state);
            window.clearStatus();
        }
    }

}