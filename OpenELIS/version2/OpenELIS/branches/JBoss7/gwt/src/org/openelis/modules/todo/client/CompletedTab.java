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
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Dropdown;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;

public class CompletedTab extends Screen {
        
    private boolean                    loadedFromCache, reattachChart;
    private String                     loadBySection;
    private ArrayList<String>          ranges;          
    private ArrayList<AnalysisViewVO> fullList;
    private TableWidget                table;
    private VerticalPanel              completedPanel; 
    private ColumnChart                chart;
    private Options                    options;
    
    public CompletedTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();        
        initializeDropdowns();
    }
    
    private void initialize() {        
        table = (TableWidget)def.getWidget("completedTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTableModel(true);
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
        
        completedPanel = (VerticalPanel)def.getWidget("completedPanel");
        
        loadBySection = "N";    
        
        ranges = new ArrayList<String>();
        ranges.add(consts.get("today"));
        ranges.add(consts.get("yesterday"));
        ranges.add(consts.get("twoDays"));
        ranges.add(consts.get("threeDays"));
        ranges.add(consts.get("moreThanThreeDays"));
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        TableDataRow row;
        List<DictionaryDO> list;
        Dropdown<Integer> domain;
        
        model = new ArrayList<TableDataRow>();
        try {
            list = CategoryCache.getBySystemName("sample_domain");
            for (DictionaryDO data : list) {
                row = new TableDataRow(data.getCode(), data.getEntry());
                model.add(row);
            }            
            domain = ((Dropdown<Integer>)table.getColumnWidget("domain"));
            domain.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    private void loadTableModel(final boolean refreshChart) {
        ArrayList<TableDataRow> model;
        
        if (loadedFromCache) {
            model = getTableModel();
            Collections.sort(model, new ColumnComparator(0, SortDirection.ASCENDING));
            table.load(model);
            if (refreshChart)
                refreshChart();
        } else {
            window.setBusy(consts.get("fetching"));
            ToDoService.get().getCompleted(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
                public void onSuccess(ArrayList<AnalysisViewVO> result) {
                    ArrayList<TableDataRow> model;         
                    
                    fullList = result;
                    model = getTableModel();
                    Collections.sort(model, new ColumnComparator(0, SortDirection.ASCENDING));
                    table.load(model);
                    if (refreshChart)
                        refreshChart();
                    window.clearStatus();
                }

                public void onFailure(Throwable error) {
                    if (error instanceof NotFoundException) {
                        window.setDone(consts.get("noRecordsFound"));
                    } else {
                        Window.alert(error.getMessage());
                        error.printStackTrace();
                        window.clearStatus();
                    }

                }
            });
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        boolean sectOnly;
        String sectName;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        SystemUserPermission perm;

        model = new ArrayList<TableDataRow>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);

        try {
            for (AnalysisViewVO data : fullList) {
                sectName = data.getSectionName();
                if (sectOnly && perm.getSection(sectName) == null)
                    continue;
                row = new TableDataRow(9);
                row.cells.get(0).setValue(data.getAccessionNumber());
                row.cells.get(1).setValue(data.getDomain());
                row.cells.get(2).setValue(sectName);
                row.cells.get(3).setValue(data.getTestName());
                row.cells.get(4).setValue(data.getMethodName());
                row.cells.get(5).setValue(data.getAnalysisResultOverride());
                row.cells.get(6).setValue(data.getCompletedDate());
                row.cells.get(7).setValue(data.getToDoDescription());
                row.cells.get(8).setValue(data.getPrimaryOrganizationName());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
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
        AnalysisViewVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (AnalysisViewVO)row.data;        
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
        long day, twodays, threedays, hour, cmpdur, mdur;
        Integer val;
        ArrayList<TableDataRow> model;
        Datetime now, cmpd;
        Date midNight;
        AnalysisViewVO data;
        HashMap<String, Integer> map;
        
        now = Datetime.getInstance();
        map = new HashMap<String, Integer>();        
        model = table.getData();   
        hour = 3600000;
        day = 24 * hour;
        twodays = 2 * day;
        threedays = 3 * day; 
        
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
            data = (AnalysisViewVO)row.data;
            cmpd = data.getCompletedDate();
            if (cmpd == null)
                cmpd = now;
            //
            // the length of the time duration between right now and completed date 
            //
            cmpdur = Math.abs(now.getDate().getTime() - cmpd.getDate().getTime());

            /*
             * If cmpdur <= mdur then it means that the completed date is today. If 
             * however cmpdur lies somewhere between the length of x number of days
             * (mdur+x) and y number of days (mdur+y), then it means that the 
             * completed date was somewhere between x and y number of days ago.
             * E.g. if x is 2 and y is 3, then the completed date was 2 days ago.     
             */
            if (cmpdur <= mdur) {
                val = map.get(ranges.get(0));
                if (val == null)
                    val = 0;
                map.put(ranges.get(0), ++val);
            } else if (mdur < cmpdur && cmpdur <= (mdur + day)) {
                val = map.get(ranges.get(1));
                if (val == null)
                    val = 0;
                map.put(ranges.get(1), ++val);
            } else if ((mdur + day) < cmpdur && cmpdur <= (mdur + twodays)) {
                val = map.get(ranges.get(2));
                if (val == null)
                    val = 0;
                map.put(ranges.get(2), ++val);
            } else if ((mdur + twodays) < cmpdur && cmpdur <= (mdur + threedays)) {
                val = map.get(ranges.get(3));
                if (val == null)
                    val = 0;
                map.put(ranges.get(3), ++val);
            } else if (cmpdur > (mdur + threedays)) {
                val = map.get(ranges.get(4));
                if (val == null)
                    val = 0;
                map.put(ranges.get(4), ++val);
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
         * because the screen's being dragged caused the chart to get detached. 
         * Otherwise, the chart can just be redrawn because only the data showing 
         * in it changed.    
         */        
        if (chart == null) {
            chart = new ColumnChart(data, options);            
            completedPanel.add(chart);
        } else if (reattachChart){            
            completedPanel.clear();
            chart = new ColumnChart(data, options);            
            completedPanel.add(chart);
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
        ops.setTitle(consts.get("timeSinceAnalysesCompleted"));
        return ops;      
    }
}
