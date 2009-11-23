package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.InventoryItemManager;

import com.google.gwt.user.client.Window;

public class LocationTab extends Screen {

    private InventoryItemManager manager;
    private TableWidget          table;
    private boolean              loaded;

    public LocationTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("locationTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(event.getState() == State.QUERY);
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        InventoryLocationViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getLocations().count(); i++ ) {
                data = (InventoryLocationViewDO)manager.getLocations().getLocationAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = data.getId();
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    public void setManager(InventoryItemManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}