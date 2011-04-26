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
package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.StorageLocationManager;

import com.google.gwt.user.client.Window;

public class LocationTab extends Screen {

    private InventoryItemManager manager;
    private TableWidget          table;
    private boolean              loaded;

    public LocationTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
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
                table.enable(true);
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.QUERY) 
                    event.cancel();                
            }            
        });
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        boolean isSerial;
        String locationName;
        InventoryLocationViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        //
        // if this inventory item is serialized, then we need to show the location
        // id which is its instance id/serial #
        //
        isSerial = "Y".equals(manager.getInventoryItem().getIsSerialMaintained());
        try {
            for (i = 0; i < manager.getLocations().count(); i++ ) {
                data = (InventoryLocationViewDO)manager.getLocations().getLocationAt(i);
                locationName = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                            data.getStorageLocationUnitDescription(),
                                                                            data.getStorageLocationLocation());
                model.add(new TableDataRow(null, locationName, data.getLotNumber(),
                                           (isSerial ? data.getId() : null),
                                           data.getExpirationDate(),
                                           data.getQuantityOnhand()));
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