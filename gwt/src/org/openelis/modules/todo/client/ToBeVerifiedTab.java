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

import org.openelis.domain.SampleCacheVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
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
    private long                       day, twodays, threedays,sevendays, tendays;    
    private String                     loadBySection;
    private ArrayList<String>          ranges;         
    private ArrayList<SampleCacheVO>   fullList;
    private TableWidget                table;
    private VerticalPanel              toBeVerifiedPanel; 
    private ColumnChart                chart;
    private Options                    options;
    
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
                refreshChart();
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
                refreshChart();                
            }
        });
        toBeVerifiedPanel = (VerticalPanel)def.getWidget("toBeVerifiedPanel");  
        
        loadBySection = "N";
        
        ranges = new ArrayList<String>();
        ranges.add(consts.get("today"));
        ranges.add(consts.get("yesterday"));
        ranges.add(consts.get("twoDays"));
        ranges.add(consts.get("threeDays"));
        ranges.add(consts.get("fourToSevenDays"));
        ranges.add(consts.get("eightToTenDays"));
        ranges.add(consts.get("moreThenTenDays"));
        
        day = 86400000;
        twodays = 2 * day; 
        threedays = 3 * day;
        sevendays = 7 * day;
        tendays = 10 * day;
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
            Collections.sort(model,new ColumnComparator(0, SortDirection.ASCENDING));
        } catch (NotFoundException e) {
            window.setDone(consts.get("noRecordsFound"));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
        return model;
    }
    
    public void reloadFromCache() {
        loadedFromCache = false;
    }
    
    public void reattachChart() {
        reattachChart = true;
    }
    
    public Integer getSelectedId() {
        TableDataRow row;
        SampleCacheVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (SampleCacheVO)row.data;        
        return data.getId();
    }
    
    public void draw(String loadBySection) {                
        if (!loadedFromCache || !loadBySection.equals(this.loadBySection)) {             
            this.loadBySection = loadBySection;              
            DataChangeEvent.fire(this);   
        }
        if (reattachChart) {
            refreshChart();
            reattachChart = false;            
        }
        loadedFromCache = true;
    }
    
    private void refreshChart() {
        long avdur, mdur;
        Integer val;
        ArrayList<TableDataRow> model;
        Datetime now, srd;
        Date midNight;
        SampleCacheVO data;
        HashMap<String, Integer> map;
        
        now = Datetime.getInstance();
        map = new HashMap<String, Integer>();        
        model = table.getData();    
        
        midNight = new Date();
        midNight.setHours(0);
        midNight.setMinutes(0);
        midNight.setSeconds(0);
        //
        // the length of the time duration between right now and last midnight 
        //
        mdur = Math.abs(now.getDate().getTime() - midNight.getTime());
        for (TableDataRow row : model) {
            if (!row.shown)
                 continue;
            data = (SampleCacheVO)row.data;
            srd = data.getReceivedDate();
            if (srd == null)
                srd = now;
            //
            // the length of the time duration between right now and available date 
            //
            avdur = Math.abs(now.getDate().getTime() - srd.getDate().getTime());                        
            
            /*
             * If avdur <= mdur then it means that the available date is today. If 
             * however avdur lies somewhere between the length of x number of days
             * (mdur+x) and y number of days (mdur+y), then it means that the 
             * available date was somewhere between x and y number of days ago.
             * E.g. if x is 3 and y is 7, then the available date was somewhere
             * between 4 and 7 days ago (inclusive of 4 and 7).
             */
            if (avdur <= mdur) {
                val = map.get(ranges.get(0));
                if (val == null)
                    val = 0;
                map.put(ranges.get(0), ++val);
            } else if (mdur < avdur && avdur <= (mdur + day)) {
                val = map.get(ranges.get(1));
                if (val == null)
                    val = 0;
                map.put(ranges.get(1), ++val);
            } else if ((mdur + day) < avdur && avdur <= (mdur + twodays)) {
                val = map.get(ranges.get(2));
                if (val == null)
                    val = 0;
                map.put(ranges.get(2), ++val);
            } else if ((mdur + twodays) < avdur && avdur <= (mdur + threedays)) {
                val = map.get(ranges.get(3));
                if (val == null)
                    val = 0;
                map.put(ranges.get(3), ++val);
            } else if ((mdur + threedays) < avdur && avdur <= (mdur + sevendays)) {
                val = map.get(ranges.get(4));
                if (val == null)
                    val = 0;
                map.put(ranges.get(4), ++val);
            } else if ((mdur + sevendays) < avdur && avdur <= (mdur + tendays)) {
                val = map.get(ranges.get(5));
                if (val == null)
                    val = 0;
                map.put(ranges.get(5), ++val);
            } else if (avdur > (mdur + tendays)) {
                val = map.get(ranges.get(6));
                if (val == null)
                    val = 0;
                map.put(ranges.get(6), ++val);
            }
        }
        
        drawCharts(map);
    }
    
    private void drawCharts(HashMap<String, Integer> map) {
        int size;
        Integer val;
        String range;
        DataTable data;

        data = DataTable.create();
        data.addColumn(ColumnType.STRING);
        data.addColumn(ColumnType.NUMBER, "Samples");       
        size = ranges.size();
        data.addRows(size);
        for (int i = 0; i < size; i++) {
            range = ranges.get(i);
            data.setValue(i, 0, range);            
            val = map.get(range);
            if (val == null)
                val = 0;
            data.setValue(i, 1, val);        
        }
        
        if (options == null)
            options = getOptions();
        /*
         * If "chart" is null then this is the first time that it's being drawn,
         * i.e. the tab was opened for the first time. If "reattachChart" is true
         * then chart needs to be re-attached to the panel it's being diplayed in
         * because the screen's being dragged caused chart to get detached. 
         * Otherwise, chart can just be redrawn because only the data showing 
         * in it changed.    
         */
        if (chart == null) {
            chart = new ColumnChart(data, options);            
            toBeVerifiedPanel.add(chart);
        } else if (reattachChart){            
            toBeVerifiedPanel.clear();
            chart = new ColumnChart(data, options);              
            toBeVerifiedPanel.add(chart);
        } else {
            chart.draw(data, options );
        }                
    }
    
    private Options getOptions() {        
        Options ops;
        AxisOptions aops;
        TextStyle fts;        
        
        ops = ColumnChart.createOptions(); 
        ops.setLegend(LegendPosition.NONE);                      
        
        aops = AxisOptions.create();
        aops.setTitle(consts.get("numSamples"));
        ops.setVAxisOptions(aops);
        
        aops = AxisOptions.create();
        fts = TextStyle.create();
        fts.setFontSize(10);
        
        aops.setTextStyle(fts);
        
        ops.setHAxisOptions(aops);                  
        ops.setWidth(625);
        ops.setHeight(215);        
        ops.setTitle(consts.get("timeSinceSamplesReceived")); 
        return ops;      
    }
}