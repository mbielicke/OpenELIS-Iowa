package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.OrderManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.event.dom.client.ClickEvent;

public class ItemTab extends Screen {

    private OrderManager          manager;
    private Table                 table;
    private AutoComplete          inventory;
    private Button                addItemButton, removeItemButton;

    private boolean               loaded, hasExtraCols;
    private int                   numColumns;

    protected ScreenService       inventoryService;

    public ItemTab(ScreenDefInt def, Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        inventoryService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

        setDefinition(def);
        setWindow(window);
        initialize();

        initializeDropdowns();
    }

    private void initialize() {
        table = (Table)def.getWidget("itemTable");
        inventory = (AutoComplete)table.getColumnWidget(OrderMeta.getOrderItemInventoryItemName());

        addScreenHandler(table, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.setEnabled(true);
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE) {
                    event.cancel();
                    return;
                }

                // store column is not editable -- it is set from inventory item
                // column
                if (event.getCol() == 2)
                    event.cancel();
            }
        });

        numColumns = table.getColumnCount();

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderItemViewDO data;
                InventoryItemDO row;
                AutoCompleteValue trow;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                try {
                    data = manager.getItems().getItemAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setQuantity((Integer)val);
                        break;
                    case 1:
                        trow = ((AutoCompleteValue)val);
                        if (trow != null) {
                            row = (InventoryItemDO)trow.getData();
                            data.setInventoryItemId(row.getId());
                            data.setInventoryItemName(row.getName());
                            data.setStoreId(row.getStoreId());
                            table.setValueAt(r, 2, data.getStoreId());
                        } else {
                            data.setInventoryItemId(null);
                            data.setInventoryItemName(null);
                            data.setStoreId(null);
                            table.setValueAt(r, 2, null);
                        }
                        break;
                    case 3:
                        data.setUnitCost((Double)val);
                        break;
                    case 4:
                        data.setCatalogNumber((String)val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getItems().addItem();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        inventory.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                InventoryItemDO data;
                Item<Integer> row;
                ArrayList<InventoryItemDO> list;
                ArrayList<Item<Integer>> model;

                try {
                    list = inventoryService.callList("fetchActiveByName", event.getMatch());
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO)list.get(i);
                        row = new Item<Integer>(data.getId(), data.getName(), data.getStoreId(),
                                               data.getDispensedUnitsId());
                        row.setData(data);
                        model.add(row);
                    }
                    inventory.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addItemButton = (Button)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                table.addRow();
                n = table.getRowCount() - 1;
                table.selectRowAt(n);
                table.scrollToVisible(n);
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeItemButton = (Button)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.getRowCount() > 0)
                    table.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        //
        // This class is responsible for managing a table that can show
        // variable number of columns. One table has OrderItemUnitCost and the
        // other one does not
        //
        hasExtraCols = table.getColumnWidget(OrderMeta.getOrderItemUnitCost()) != null;
    }

    private void initializeDropdowns() {
        Dropdown<Integer> store, storeId, units;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        store = (Dropdown)inventory.getPopupContext().getColumnWidget(1);
        units = (Dropdown)inventory.getPopupContext().getColumnWidget(2);
        storeId = (Dropdown)table.getColumnWidget(OrderMeta.getOrderItemInventoryItemStoreId());

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_store");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        store.setModel(model);
        storeId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_unit");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        units.setModel(model);
    }

    private ArrayList<Row> getTableModel() {
        int i;
        OrderItemViewDO data;
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++ ) {
                data = (OrderItemViewDO)manager.getItems().getItemAt(i);
                row = new Row(numColumns);

                row.setCell(0,data.getQuantity());
                row.setCell(1,new AutoCompleteValue(data.getInventoryItemId(),
                                                           data.getInventoryItemName()));
                row.setCell(2,data.getStoreId());
                if (hasExtraCols) {
                    row.setCell(3,data.getUnitCost());
                    row.setCell(4,data.getCatalogNumber());
                }
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    public void setManager(OrderManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

}