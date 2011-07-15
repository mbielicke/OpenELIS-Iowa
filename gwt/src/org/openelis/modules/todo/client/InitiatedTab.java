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
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
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
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;

public class InitiatedTab extends Screen {
        
    private boolean                    loadedFromCache, reattachChart;
    private Long                       day, twodays, fivedays, tendays, twntydays,
                                       thrtydays, hour;
    private String                     loadBySection;
    private ArrayList<String>          ranges;      
    private ArrayList<AnalysisCacheVO> fullList;
    private TableWidget                table;
    private VerticalPanel              initiatedPanel; 
    private ColumnChart                chart;
    private Options                    options;
    
    public InitiatedTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        service = new ScreenService("controller?service=org.openelis.modules.todo.server.ToDoService");            
        initialize();        
    }
    
    private void initialize() {
        table = (TableWidget)def.getWidget("initiatedTable");
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
        
        initiatedPanel = (VerticalPanel)def.getWidget("initiatedPanel");   
        
        loadBySection = "N";    
        
        ranges = new ArrayList<String>();
        ranges.add(consts.get("today"));
        ranges.add(consts.get("yesterday"));
        ranges.add(consts.get("twoToFiveDays"));
        ranges.add(consts.get("sixToTenDays"));
        ranges.add(consts.get("elevnToTwntyDays"));
        ranges.add(consts.get("twntyOneToThrtyDays"));
        ranges.add(consts.get("moreThanThrtyDays"));
        
        hour = 3600000L;
        day = 24 * hour;
        twodays = 2 * day;
        fivedays = 3 * day; 
        tendays = 10 * day;
        twntydays = 20 * day;
        thrtydays = 30 * day;
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        Long diff;
        Double percent, units, ceil;
        boolean sectOnly;
        String sectName;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        Datetime now, stdt, scd, sct, srd;
        Date temp;        
        SystemUserPermission perm;
        
        model = new ArrayList<TableDataRow>();

        try {            
            perm = UserCache.getPermission();             
            if (!loadedFromCache) {
                window.setBusy(consts.get("fetching"));
                fullList = service.callList("getInitiated");
                window.clearStatus();
            }
            sectOnly = "Y".equals(loadBySection);
            now = Datetime.getInstance();            
            for (AnalysisCacheVO data: fullList) {
                sectName = data.getSectionName();          
                if (sectOnly && perm.getSection(sectName) == null)
                    continue;                
                row = new TableDataRow(10);                
                row.cells.get(0).setValue(data.getSampleAccessionNumber());
                row.cells.get(1).setValue(data.getSampleDomain());
                row.cells.get(2).setValue(sectName);
                row.cells.get(3).setValue(data.getTestName());
                row.cells.get(4).setValue(data.getTestMethodName());
                stdt = data.getStartedDate();
                if (stdt == null)
                    stdt = now;
                scd = data.getSampleCollectionDate();
                sct = data.getSampleCollectionTime();                
                if (scd != null) {
                    //
                    // holding % is calculated based on sample collection date 
                    //
                    temp = scd.getDate();
                    if (sct == null) {
                        temp.setHours(0);
                        temp.setMinutes(0);
                    } else {
                        temp.setHours(sct.getDate().getHours());
                        temp.setMinutes(sct.getDate().getMinutes());
                    }
                    diff = (stdt.getDate().getTime() - temp.getTime());
                    units = diff.doubleValue() / hour.doubleValue();
                    percent = (units / data.getTestTimeHolding().doubleValue() * 100);
                    row.cells.get(5).setValue(percent);
                    
                    /*
                     * average turnaround % is calculated based on sample collection
                     * date if present  
                     */
                    diff = now.getDate().getTime() - temp.getTime();
                    units = diff.doubleValue() / day.doubleValue();
                    percent = (units / data.getTestTimeTaAverage().doubleValue()) * 100;
                    row.cells.get(6).setValue(percent);
                } else {
                    row.cells.get(5).setValue(0.0);
                    
                    /*
                     * average turnaround % is calculated based on sample received
                     * date if sample collection date isn't present  
                     */
                    srd = data.getSampleReceivedDate();
                    if (srd == null)
                        srd = now;
                    diff = now.getDate().getTime() - srd.getDate().getTime();                    
                    units = diff.doubleValue() / day.doubleValue();
                    percent = (units / data.getTestTimeTaAverage().doubleValue()) * 100;
                    row.cells.get(6).setValue(percent);
                }                
                
                /*
                 * Days in initiated are calculated based on started date. 
                 * Math.ceil() returns the value closest to an integer that's greater 
                 * than or equal to the argument("units" here). For example 3.4
                 * is converted to 4.0 and 5.0 stays the same.       
                 */
                diff = now.getDate().getTime() - stdt.getDate().getTime();
                units = diff.doubleValue() / day.doubleValue();                                                
                ceil = Math.ceil(units);                
                row.cells.get(7).setValue(ceil.intValue());
                
                row.cells.get(9).setValue(data.getSampleReportToName());
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
        AnalysisCacheVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (AnalysisCacheVO)row.data;        
        return data.getSampleId();
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
        long stdur, mdur;
        Integer val;
        ArrayList<TableDataRow> model;
        Datetime now, std;
        Date  midNight;
        AnalysisCacheVO data;
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
            data = (AnalysisCacheVO)row.data;
            std = data.getStartedDate();
            if (std == null)
                std = now;
            //
            // the length of the time duration between right now and started date 
            //
            stdur = Math.abs(now.getDate().getTime() - std.getDate().getTime());

            /*
             * If stdur <= mdur then it means that the available date is today. If 
             * however stdur lies somewhere between the length of x number of days
             * (mdur+x) and y number of days (mdur+y), then it means that the 
             * available date was somewhere between x and y number of days ago.
             * E.g. if x is 10 and y is 20, then the started date was somewhere
             * between 11 and 20 days ago (inclusive of 11 and 20).     
             */
            if (stdur <= mdur) {
                val = map.get(ranges.get(0));
                if (val == null)
                    val = 0;
                map.put(ranges.get(0), ++val);
            } else if (mdur < stdur && stdur <= (mdur + day)) {
                val = map.get(ranges.get(1));
                if (val == null)
                    val = 0;
                map.put(ranges.get(1), ++val);
            } else if ((mdur + twodays) < stdur && stdur <= (mdur + fivedays)) {
                val = map.get(ranges.get(2));
                if (val == null)
                    val = 0;
                map.put(ranges.get(2), ++val);
            } else if ((mdur + fivedays) < stdur && stdur <= (mdur + tendays)) {
                val = map.get(ranges.get(3));
                if (val == null)
                    val = 0;
                map.put(ranges.get(3), ++val);
            } else if ((mdur + tendays) < stdur && stdur <= (mdur + twntydays)) {
                val = map.get(ranges.get(4));
                if (val == null)
                    val = 0;
                map.put(ranges.get(4), ++val);
            } else if ((mdur + twntydays) < stdur && stdur <= (mdur + thrtydays)) {
                val = map.get(ranges.get(5));
                if (val == null)
                    val = 0;
                map.put(ranges.get(5), ++val);
            } else if (stdur > (mdur + thrtydays)) {
                val = map.get(ranges.get(6));
                if (val == null)
                    val = 0;
                map.put(ranges.get(6), ++val);
            }        
        }
        
        drawChart(map);
    }
    
    private void drawChart(HashMap<String, Integer> map) {
        int size;
        Integer val;
        String range;
        DataTable data;
        
        data = DataTable.create();
        data.addColumn(ColumnType.STRING);
        data.addColumn(ColumnType.NUMBER, consts.get("analyses"));
        size = ranges.size();
        data.addRows(size);
        for (int i = 0; i < size; i++) {
            range = ranges.get(i);
            data.setValue(i, 0, range);            
            val = map.get(range);
            if (val == null)
                val = 0;
            data.setValue(i, 1, val.intValue());        
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
            initiatedPanel.add(chart);
        } else if (reattachChart){            
            initiatedPanel.clear();
            chart = new ColumnChart(data, options);            
            initiatedPanel.add(chart);
        } else {
            chart.draw(data, options);            
        }
    }
    
    private Options getOptions() {        
        Options ops;
        AxisOptions aops;
        TextStyle fts;        
        
        ops = ColumnChart.createOptions(); 
        ops.setLegend(LegendPosition.NONE);
        
        aops = AxisOptions.create();
        aops.setTitle(consts.get("numAnalyses"));
        ops.setVAxisOptions(aops);
        
        aops = AxisOptions.create();
        fts = TextStyle.create();
        fts.setFontSize(10);
        aops.setTextStyle(fts);
        ops.setHAxisOptions(aops);  
                
        ops.setWidth(625);
        ops.setHeight(215);
        ops.setTitle(consts.get("timeSinceAnalysesInitiated"));
        return ops;      
    }
}