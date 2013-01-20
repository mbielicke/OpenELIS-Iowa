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
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.user.client.Window;

public class FillTab extends Screen {

    private OrderManager manager;
    private TableWidget  table;
    private boolean      loaded, hasExtraCols;

    private int          numColumns;

    public FillTab(ScreenDefInt def, ScreenWindowInt window) {

        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("fillTable");

        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(false);
            }
        });

        numColumns = table.getColumns().size();

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

    private ArrayList<TableDataRow> getTableModel() {
        int i, count;
        String location;
        InventoryXUseViewDO fillData;
        InventoryXPutViewDO receiptData;
        InventoryLocationViewDO invLocData;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        OrderFillManager fillMan;
        OrderReceiptManager receiptMan;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            if (hasExtraCols) {
                receiptMan = manager.getReceipts();
                count = receiptMan.count();

                for (i = 0; i < count; i++ ) {
                    receiptData = (InventoryXPutViewDO)receiptMan.getReceiptAt(i);
                    invLocData = receiptData.getInventoryLocation();
                    row = new TableDataRow(numColumns);
                    location = StorageLocationManager.getLocationForDisplay(
                                                                            invLocData.getStorageLocationName(),
                                                                            invLocData.getStorageLocationUnitDescription(),
                                                                            invLocData.getStorageLocationLocation());
                    row.cells.get(0).setValue(invLocData.getInventoryItemName());
                    row.cells.get(1).setValue(location);
                    row.cells.get(2).setValue(receiptData.getQuantity());
                    row.cells.get(3).setValue(invLocData.getLotNumber());
                    row.cells.get(4).setValue(invLocData.getExpirationDate());
                    row.cells.get(5).setValue(receiptData.getInventoryReceiptReceivedDate());
                    row.cells.get(6).setValue(receiptData.getInventoryReceiptUnitCost());
                    row.cells.get(7).setValue(receiptData.getExternalReference());

                    model.add(row);
                }
            } else {
                fillMan = manager.getFills();
                count = fillMan.count();

                for (i = 0; i < count; i++ ) {
                    fillData = (InventoryXUseViewDO)fillMan.getFillAt(i);
                    row = new TableDataRow(numColumns);
                    location = StorageLocationManager.getLocationForDisplay(fillData.getStorageLocationName(),
                                                                            fillData.getStorageLocationUnitDescription(),
                                                                            fillData.getStorageLocationLocation());
                    row.cells.get(0).setValue(fillData.getInventoryItemName());
                    row.cells.get(1).setValue(location);
                    row.cells.get(2).setValue(fillData.getQuantity());
                    row.cells.get(3).setValue(fillData.getInventoryLocationLotNumber());
                    row.cells.get(4).setValue(fillData.getInventoryLocationExpirationDate());

                    model.add(row);
                }
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