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
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.PwsAddressManager;
import org.openelis.manager.PwsManager;

import com.google.gwt.user.client.Window;

public class AddressTab extends Screen {

    private PwsManager  manager;
    private boolean     loaded;
    private TableWidget addressTable;    
    
    public AddressTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        addressTable = (TableWidget)def.getWidget("addressTable");
        addScreenHandler(addressTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                addressTable.load(getTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressTable.enable(false);
            }
        });          
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TableDataRow row;
        PwsAddressDO data;
        PwsAddressManager man;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model; 
        
        try {
            man = manager.getAddresses();
            for(int i = 0; i < man.count(); i++) {
                data = man.getAddressAt(i);
                
                row = new TableDataRow(10);
                row.cells.get(0).setValue(data.getTypeCode());
                row.cells.get(1).setValue(data.getActiveIndCd());
                row.cells.get(2).setValue(data.getName());
                row.cells.get(3).setValue(data.getAddrLineOneTxt());
                row.cells.get(4).setValue(data.getAddrLineTwoTxt());
                row.cells.get(5).setValue(data.getAddressCityName());
                row.cells.get(6).setValue(data.getAddressStateCode());
                row.cells.get(7).setValue(data.getAddressZipCode());
                row.cells.get(8).setValue(data.getStateFipsCode());
                row.cells.get(9).setValue(data.getPhoneNumber());
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
