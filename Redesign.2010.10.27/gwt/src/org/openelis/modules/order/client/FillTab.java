package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.OrderMeta;

public class FillTab extends Screen {

    private OrderManager manager;
    private Table        table;
    private boolean      loaded, hasExtraCols;

    private int          numColumns;

    public FillTab(ScreenDefInt def, Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");

        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        table = (Table)def.getWidget("fillTable");

        addScreenHandler(table, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.setEnabled(true);
                table.setQueryMode(false);
            }
        });

        numColumns = table.getColumnCount();

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        //
        // This class is responsible for managing a table that can show
        // variable number of columns. One table has InventoryReceiptReceivedDate
        // and the other one does not
        //
        hasExtraCols = table.getColumnWidget(OrderMeta.getInventoryReceiptReceivedDate()) != null;
    }

    private ArrayList<Row> getTableModel() {
        int i, count;
        String location;
        InventoryXUseViewDO fillData;
        InventoryXPutViewDO receiptData;
        InventoryLocationViewDO invLocData;
        ArrayList<Row> model;
        Row row;
        OrderFillManager fillMan;
        OrderReceiptManager receiptMan;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            if (hasExtraCols) {
                receiptMan = manager.getReceipts();
                count = receiptMan.count();

                for (i = 0; i < count; i++ ) {
                    receiptData = (InventoryXPutViewDO)receiptMan.getReceiptAt(i);
                    invLocData = receiptData.getInventoryLocation();
                    row = new Row(numColumns);
                    location = StorageLocationManager.getLocationForDisplay(
                                                                            invLocData.getStorageLocationName(),
                                                                            invLocData.getStorageLocationUnitDescription(),
                                                                            invLocData.getStorageLocationLocation());
                    row.setCell(0,invLocData.getInventoryItemName());
                    row.setCell(1,location);
                    row.setCell(2,receiptData.getQuantity());
                    row.setCell(3,invLocData.getLotNumber());
                    row.setCell(4,invLocData.getExpirationDate());
                    row.setCell(5,receiptData.getInventoryReceiptReceivedDate());
                    row.setCell(6,receiptData.getInventoryReceiptUnitCost());
                    row.setCell(7,receiptData.getExternalReference());

                    model.add(row);
                }
            } else {
                fillMan = manager.getFills();
                count = fillMan.count();

                for (i = 0; i < count; i++ ) {
                    fillData = (InventoryXUseViewDO)fillMan.getFillAt(i);
                    row = new Row(numColumns);
                    location = StorageLocationManager.getLocationForDisplay(fillData.getStorageLocationName(),
                                                                            fillData.getStorageLocationUnitDescription(),
                                                                            fillData.getStorageLocationLocation());
                    row.setCell(0,fillData.getInventoryItemName());
                    row.setCell(1,location);
                    row.setCell(2,fillData.getQuantity());
                    row.setCell(3,fillData.getInventoryLocationLotNumber());
                    row.setCell(4,fillData.getInventoryLocationExpirationDate());

                    model.add(row);
                }
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