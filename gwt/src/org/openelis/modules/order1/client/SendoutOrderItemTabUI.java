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
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.manager.OrderManager1;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.inventoryItem.client.InventoryItemService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SendoutOrderItemTabUI extends Screen {
    @UiTemplate("SendoutOrderItemTab.ui.xml")
    interface SendoutOrderItemTabUiBinder extends UiBinder<Widget, SendoutOrderItemTabUI> {
    };

    private static SendoutOrderItemTabUiBinder uiBinder = GWT.create(SendoutOrderItemTabUiBinder.class);

    @UiField
    protected Table                            table;

    @UiField
    protected Dropdown<Integer>                store, autocompleteStore, dispensedUnits;

    @UiField
    protected AutoComplete                     inventoryItem;

    @UiField
    protected Button                           removeItemButton, addItemButton;

    protected Screen                           parentScreen;

    protected boolean                          isVisible, canEdit, redraw;

    protected OrderManager1                    manager;

    public SendoutOrderItemTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        setEventBus(parentScreen.getEventBus());
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        Item<Integer> item;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(QUERY, DISPLAY) || (canEdit && isState(ADD, UPDATE)));
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 3; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(OrderMeta.getOrderItemQuantity());
                                break;
                            case 1:
                                qd.setKey(OrderMeta.getOrderItemInventoryItemName());
                                break;
                            case 2:
                                qd.setKey(OrderMeta.getOrderItemInventoryItemStoreId());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(QUERY) && ! (canEdit && isState(ADD, UPDATE) && event.getCol() <= 1))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderItemViewDO data;
                InventoryItemDO item;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                data = manager.item.get(r);

                switch (c) {
                    case 0:
                        data.setQuantity((Integer)val);
                        break;
                    case 1:
                        if (val == null) {
                            data.setId(null);
                            data.setInventoryItemName(null);
                            data.setStoreId(null);
                            table.setValueAt(r, 2, null);
                        } else {
                            item = (InventoryItemDO) ( ((AutoCompleteValue)val).getData());
                            if (item != null) {
                                data.setInventoryItemId(item.getId());
                                data.setInventoryItemName(item.getName());
                                data.setStoreId(item.getStoreId());
                                table.setValueAt(r, 2, item.getStoreId());
                            }
                        }
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.item.add();
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.item.remove(event.getIndex());
            }
        });

        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getItemMatches(event.getMatch());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeItemButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addItemButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayItems();
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("inventory_store");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        store.setModel(model);
        autocompleteStore.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("inventory_unit");
        for (DictionaryDO data : list)
            model.add(new Item<Integer>(data.getId(), data.getEntry()));
        dispensedUnits.setModel(model);
    }

    @UiHandler("addItemButton")
    protected void addItem(ClickEvent event) {
        int n;

        table.finishEditing();
        table.unselectAll();
        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    @UiHandler("removeItemButton")
    protected void removeItem(ClickEvent event) {
        int r;
        Integer[] rows;

        rows = table.getSelectedRows();
        Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i-- ) {
            r = rows[i];
            if (r > -1 && table.getRowCount() > 0)
                table.removeRowAt(r);
        }
    }

    public void setData(OrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
            evaluateEdit();
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        String name;
        OrderItemViewDO item;
        Row r;

        evaluateEdit();
        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.item.count();

        /*
         * find out if there's any difference between the item being
         * displayed and the item in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                item = manager.item.get(i);
                r = table.getRowAt(i);
                if (r.getCell(1) != null)
                    name = ((AutoCompleteValue)r.getCell(1)).getDisplay();
                else
                    name = null;
                if (DataBaseUtil.isDifferent(item.getQuantity(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(item.getInventoryItemName(), name) ||
                    DataBaseUtil.isDifferent(item.getStoreId(), r.getCell(2))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayItems();
    }

    private void displayItems() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        OrderItemViewDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.item.count(); i++ ) {
            data = manager.item.get(i);
            row = new Row(3);
            row.setCell(0, data.getQuantity());
            row.setCell(1, new AutoCompleteValue(data.getInventoryItemId(),
                                                 data.getInventoryItemName()));
            row.setCell(2, data.getStoreId());
            model.add(row);
        }

        return model;
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  !Constants.dictionary().ORDER_STATUS_PROCESSED.equals(manager.getOrder()
                                                                               .getStatusId());
    }

    private void getItemMatches(String match) {
        Item<Integer> row;
        InventoryItemDO data;
        ArrayList<InventoryItemDO> list;
        ArrayList<Item<Integer>> model;

        parentScreen.getWindow().setBusy();
        try {
            list = InventoryItemService.get()
                                       .fetchActiveByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(4);
                data = list.get(i);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());
                row.setCell(1, data.getDescription());
                row.setCell(2, data.getStoreId());
                row.setCell(3, data.getDispensedUnitsId());

                model.add(row);
            }
            inventoryItem.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        parentScreen.getWindow().clearStatus();
    }
}
