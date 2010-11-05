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

import org.openelis.domain.PwsMonitorDO;
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
import org.openelis.manager.PwsManager;
import org.openelis.manager.PwsMonitorManager;


public class MonitorTab extends Screen{

    private PwsManager  manager;
    private boolean     loaded;
    private Table       monitorTable;    

    public MonitorTab(ScreenDefInt def, Window window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        monitorTable = (Table)def.getWidget("monitorTable");
        addScreenHandler(monitorTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                monitorTable.setModel(getTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                monitorTable.setEnabled(true);                
            }
        });       
        
        monitorTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }            
        });
    }
        
    
    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        Row row;
        PwsMonitorDO data;
        PwsMonitorManager man;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model; 
        
        try {
            man = manager.getMonitors();
            for(int i = 0; i < man.count(); i++) {
                data = man.getMonitorAt(i);
                
                row = new Row(8);
                row.setCell(0,data.getStAsgnIdentCd());
                row.setCell(1,data.getName());
                row.setCell(2,data.getTiaanlgpTiaanlytName());
                row.setCell(3,data.getNumberSamples());
                row.setCell(4,data.getCompBeginDate());
                row.setCell(5,data.getCompEndDate());
                row.setCell(6,data.getFrequencyName());
                row.setCell(7,data.getPeriodName());
                
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
