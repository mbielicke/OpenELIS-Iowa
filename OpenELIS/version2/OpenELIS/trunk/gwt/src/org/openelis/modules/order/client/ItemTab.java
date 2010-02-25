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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
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
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Window;

public class ItemTab extends Screen {

    private OrderManager          manager;
    private TableWidget           table;
    private AutoComplete<Integer> inventory;
    private AppButton             addItemButton, removeItemButton;
    private boolean               loaded;
    
    protected ScreenService       inventoryService;

    public ItemTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        inventoryService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

        setDef(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("itemTable");
        inventory = (AutoComplete) table.getColumnWidget(OrderMeta.getOrderItemInventoryItemName());

        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // store column is not editable -- it is set from inventory item
                // column
                if (event.getCol() == 2)
                    event.cancel();
            }
        });
        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderItemViewDO data;
                InventoryItemDO row;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r,c);

                try {
                    data = manager.getItems().getItemAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch(c) {
                    case 0:
                        data.setQuantity((Integer)val);
                        break;
                    case 1:
                        row = (InventoryItemDO) ((TableDataRow)val).data;
                        data.setInventoryItemId(row.getId());
                        data.setInventoryItemName(row.getName());
                        data.setStoreId(row.getStoreId());
                        table.setCell(r, 2, data.getStoreId());
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getItems().addItem();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        inventory.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                InventoryItemDO data;
                TableDataRow row;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;

                try {
                    list = inventoryService.callList("fetchActiveByName", event.getMatch());
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        row = new TableDataRow(data.getId(), data.getName(),
                                               data.getStoreId(), data.getDispensedUnitsId());
                        row.data = data;
                        model.add(row);
                    }
                    inventory.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addItemButton = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                table.addRow();
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItemButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                            .contains(event.getState()));
            }
        });


        removeItemButton = (AppButton)def.getWidget("removeItemButton");
        addScreenHandler(removeItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeItemButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        Dropdown<Integer> store, storeId, units;
        ArrayList<TableDataRow> model;

        store = (Dropdown) inventory.getColumns().get(1).getColumnWidget();
        units = (Dropdown) inventory.getColumns().get(2).getColumnWidget();
        storeId = (Dropdown) table.getColumnWidget(OrderMeta.getOrderItemInventoryItemStoreId());

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_store"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        store.setModel(model);
        storeId.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_unit"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        units.setModel(model);
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        OrderItemViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++ ) {
                data = (OrderItemViewDO)manager.getItems().getItemAt(i);
                model.add(new TableDataRow(null, data.getQuantity(), 
                                           new TableDataRow(data.getInventoryItemId(),
                                                            data.getInventoryItemName()),
                                           data.getStoreId()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
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