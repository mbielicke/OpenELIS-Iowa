package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.OrderManager;
import org.openelis.manager.StorageLocationManager;

import com.google.gwt.user.client.Window;

public class FillTab extends Screen {

    private OrderManager          manager;
    private TableWidget           table;
    private boolean               loaded;
    
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
        

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE) { 
                    event.cancel();
                    return;
                }                                
            }
        });
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        String location;
        InventoryXUseViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getFills().count(); i++ ) {
                data = (InventoryXUseViewDO)manager.getFills().getFillAt(i);
                location = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                        data.getStorageLocationUnitDescription(),
                                                                        data.getStorageLocationLocation());
                model.add(new TableDataRow(null, data.getInventoryItemName(),
                                           location, data.getQuantity(),
                                           data.getLotNumber(), data.getExpirationDate()));
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