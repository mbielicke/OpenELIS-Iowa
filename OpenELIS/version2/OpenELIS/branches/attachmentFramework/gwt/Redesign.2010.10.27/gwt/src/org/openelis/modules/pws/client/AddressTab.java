/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.pws.client;

import java.util.ArrayList;

import org.openelis.domain.PwsAddressDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.PwsAddressManager;
import org.openelis.manager.PwsManager;

public class AddressTab extends Screen {

    private PwsManager  manager;
    private boolean     loaded;
    private Table       addressTable;    
    
    public AddressTab(ScreenDefInt def, Window window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        addressTable = (Table)def.getWidget("addressTable");
        addScreenHandler(addressTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                addressTable.setModel(getTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressTable.setEnabled(true);
            }
        });  
        
        addressTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }            
        });
    }
    
    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        Row row;
        PwsAddressDO data;
        PwsAddressManager man;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model; 
        
        try {
            man = manager.getAddresses();
            for(int i = 0; i < man.count(); i++) {
                data = man.getAddressAt(i);
                
                row = new Row(10);
                row.setCell(0,data.getTypeCode());
                row.setCell(1,data.getActiveIndCd());
                row.setCell(2,data.getName());
                row.setCell(3,data.getAddrLineOneTxt());
                row.setCell(4,data.getAddrLineTwoTxt());
                row.setCell(5,data.getAddressCityName());
                row.setCell(6,data.getAddressStateCode());
                row.setCell(7,data.getAddressZipCode());
                row.setCell(8,data.getStateFipsCode());
                row.setCell(9,data.getPhoneNumber());
                
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }        
        return model;
    }

    public void setManager(PwsManager manager) {
        this.manager = manager;
        loaded = false;        
    }

    public void draw() {
        if (!loaded) 
            DataChangeEvent.fire(this);       

        loaded = true;
    }

}
