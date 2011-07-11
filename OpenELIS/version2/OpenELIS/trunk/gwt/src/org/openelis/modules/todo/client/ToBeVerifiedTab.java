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
package org.openelis.modules.todo.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisCacheVO;
import org.openelis.domain.SampleCacheVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.FilterEvent;
import org.openelis.gwt.widget.table.event.FilterHandler;
import org.openelis.gwt.widget.table.event.SortEvent.SortDirection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;

public class ToBeVerifiedTab extends Screen {
            
    private boolean                    loadedFromCache, reattachChart;
    //private long                       day, twodays, threedays,sevendays, tendays;    
    //private ArrayList<String>          ranges;         
    //private Date                       midNight;
    private ArrayList<SampleCacheVO> fullList;
    private TableWidget                table;
    private VerticalPanel              toBeVerifiedPanel; 
    //private ColumnChart                chart;
    //private Options                    options;
    
    public ToBeVerifiedTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        service = new ScreenService("controller?service=org.openelis.modules.todo.server.ToDoService");        
        initialize();        
    }
    
    private void initialize() {                
        table = (TableWidget)def.getWidget("toBeVerifiedTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                                 
                event.cancel();
            }            
        });
           
        table.addFilterHandler(new FilterHandler() {           
            public void onFilter(FilterEvent event) {
                //refreshChart();                
            }
        });
        toBeVerifiedPanel = (VerticalPanel)def.getWidget("toBeVerifiedPanel");  
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        TableDataRow row;
        ArrayList<TableDataRow> model;
        Datetime scd, sct;
        Date temp;        
        
        model = new ArrayList<TableDataRow>();
        try {            
            if (!loadedFromCache) {
                window.setBusy(consts.get("fetching"));
                fullList = service.callList("getToBeVerified");
                window.clearStatus();
            }
            for (SampleCacheVO data: fullList) {
                row = new TableDataRow(10);                
                row.cells.get(0).setValue(data.getAccessionNumber());
                row.cells.get(1).setValue(data.getDomain());               
                scd = data.getCollectionDate();
                sct = data.getCollectionTime();
                if (scd != null) {
                    temp = scd.getDate();
                    if (sct == null) {
                        temp.setHours(0);
                        temp.setMinutes(0);
                    } else {
                        temp.setHours(sct.getDate().getHours());
                        temp.setMinutes(sct.getDate().getMinutes());
                    }

                    row.cells.get(2).setValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE,temp));
                }
                row.cells.get(3).setValue(data.getReceivedDate());                
                row.cells.get(4).setValue(data.getQaeventResultOverride());
                row.cells.get(6).setValue(data.getReportToName());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
        Collections.sort(model,new ColumnComparator(0, SortDirection.ASCENDING));
        return model;
    }
    
    public void reloadFromCache() {
        loadedFromCache = false;
    }
    
    public Integer getSelectedSampleId() {
        TableDataRow row;
        SampleCacheVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (SampleCacheVO)row.data;        
        return data.getId();
    }
    
    public void draw() {        
        if (!loadedFromCache)              
            DataChangeEvent.fire(this);                
        loadedFromCache = true;
    }
}