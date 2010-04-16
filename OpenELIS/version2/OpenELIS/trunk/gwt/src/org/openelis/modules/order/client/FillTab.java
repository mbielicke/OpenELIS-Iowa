package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class FillTab extends Screen {

    private OrderManager          manager;
    private TableWidget           table;
    private boolean               loaded;
    
    private int                   numColumns;
    
    public FillTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");

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
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i, count;
        String location;
        InventoryXUseViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        Widget widget;
        OrderFillManager man;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            man = manager.getFills();
            count = man.count();
            for (i = 0; i < count; i++ ) {
                data = (InventoryXUseViewDO)man.getFillAt(i);
                row = new TableDataRow(numColumns);
                location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                        data.getStorageLocationUnitDescription(),
                                                                        data.getStorageLocationLocation());                                
                row.cells.get(0).setValue(data.getInventoryItemName());
                row.cells.get(1).setValue(location);
                row.cells.get(2).setValue(data.getQuantity());
                row.cells.get(3).setValue(data.getLotNumber());
                row.cells.get(4).setValue(data.getExpirationDate());
                
                widget = table.getColumnWidget(OrderMeta.getInventoryReceiptReceivedDate());                
                if(widget != null) 
                    row.cells.get(5).setValue(data.getInventoryReceiptReceivedDate());
                
                widget = table.getColumnWidget(OrderMeta.getInventoryReceiptUnitCost());                
                if(widget != null) 
                    row.cells.get(6).setValue(data.getInventoryReceiptUnitCost());
                
                widget = table.getColumnWidget(OrderMeta.getInventoryReceiptExternalReference());                
                if(widget != null) 
                    row.cells.get(7).setValue(data.getInventoryReceiptExternalReference());                                
                
                model.add(row);
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