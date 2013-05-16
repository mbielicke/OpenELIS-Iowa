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
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.widget.WindowInt;
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

public class LoggedInTab extends Screen {
            
    private boolean                    loadedFromCache, reattachChart;
    private String                     loadBySection;
    private ArrayList<String>          ranges;         
    private ArrayList<AnalysisViewVO> fullList;
    private TableWidget                table;
    private VerticalPanel              loggedInPanel; 
    private ColumnChart                chart;
    private Options                    options;
    
    public LoggedInTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();        
        initializeDropdowns();
    }
    
    private void initialize() {
        table = (TableWidget)def.getWidget("loggedInTable");
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
        loggedInPanel = (VerticalPanel)def.getWidget("loggedInPanel");  
        
        loadBySection = "N";
        
        ranges = new ArrayList<String>();
        ranges.add(Messages.get().today());
        ranges.add(Messages.get().yesterday());
        ranges.add(Messages.get().twoDays());
        ranges.add(Messages.get().threeDays());
        ranges.add(Messages.get().fourToSevenDays());
        ranges.add(Messages.get().eightToTenDays());
        ranges.add(Messages.get().moreThenTenDays());      
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
            window.setBusy(Messages.get().fetching());
            ToDoService.get().getLoggedIn(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
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
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert(error.getMessage());
                        error.printStackTrace();
                        window.clearStatus();
                    }

                }
            });
        }
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
    
    private ArrayList<TableDataRow> getTableModel() {
        boolean sectOnly;
        Integer accNum, prevAccNum;
        String secName;
        TableDataRow row;
        Datetime scd, sct, scdt;
        Date temp;
        SystemUserPermission perm;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);
        accNum = null;
        prevAccNum = null;
        scdt = null;

        try {
            for (AnalysisViewVO data : fullList) {
                secName = data.getSectionName();
                if (sectOnly && perm.getSection(secName) == null)
                    continue;
                row = new TableDataRow(12);
                accNum = data.getAccessionNumber();
                row.cells.get(0).setValue(accNum);
                row.cells.get(1).setValue(data.getDomain());
                row.cells.get(2).setValue(secName);
                row.cells.get(3).setValue(data.getTestName());
                row.cells.get(4).setValue(data.getMethodName());

                if ( !accNum.equals(prevAccNum)) {
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
                        scdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp);
                    } else {
                        scdt = null;
                    }
                }

                row.cells.get(5).setValue(scdt);
                row.cells.get(6).setValue(data.getReceivedDate());
                row.cells.get(7).setValue(data.getAnalysisResultOverride());
                row.cells.get(8).setValue(DataBaseUtil.getPercentHoldingUsed(data.getStartedDate(), data.getCollectionDate(),
                                                                             data.getCollectionTime(), data.getTimeHolding()));
                row.cells.get(9).setValue(DataBaseUtil.getPercentExpectedCompletion(data.getCollectionDate(), data.getCollectionTime(),
                                                                    data.getReceivedDate(), data.getPriority(),
                                                                    data.getTimeTaAverage()));
                row.cells.get(10).setValue(data.getToDoDescription());
                row.cells.get(11).setValue(data.getPrimaryOrganizationName());
                row.data = data;
                model.add(row);

                prevAccNum = accNum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

        return model;
    }
    
    private void refreshChart() {
        long day, twodays, threedays,sevendays, tendays, avdur, mdur;
        Integer val;
        ArrayList<TableDataRow> model;
        Datetime now, avd;
        Date midNight;
        AnalysisViewVO data;
        HashMap<String, Integer> map;
        
        now = Datetime.getInstance();
        map = new HashMap<String, Integer>();        
        model = table.getData();     
        
        day = 86400000;
        twodays = 2 * day; 
        threedays = 3 * day;
        sevendays = 7 * day;
        tendays = 10 * day;
        
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
            avd = data.getAvailableDate();
            if (avd == null)
                avd = now;
            //
            // the length of the time duration between right now and available date 
            //
            avdur = Math.abs(now.getDate().getTime() - avd.getDate().getTime());                        
            
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
        data.addColumn(ColumnType.NUMBER, Messages.get().analyses());       
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
            loggedInPanel.add(chart);
        } else if (reattachChart){            
            loggedInPanel.clear();
            chart = new ColumnChart(data, options);              
            loggedInPanel.add(chart);
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
        aops.setTitle(Messages.get().numAnalyses());
        ops.setVAxisOptions(aops);
        
        aops = AxisOptions.create();
        fts = TextStyle.create();
        fts.setFontSize(10);
        aops.setTextStyle(fts);
        ops.setHAxisOptions(aops);  
                
        ops.setWidth(625);
        ops.setHeight(215);
        ops.setTitle(Messages.get().timeSinceAnalysesLoggedIn());
        return ops;      
    }    
}