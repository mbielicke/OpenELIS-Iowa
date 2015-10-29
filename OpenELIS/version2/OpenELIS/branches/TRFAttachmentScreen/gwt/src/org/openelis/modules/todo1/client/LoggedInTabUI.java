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
package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.PortalPanel;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;

public class LoggedInTabUI extends Screen {

    @UiTemplate("LoggedInTab.ui.xml")
    interface LoggedInTabUiBinder extends UiBinder<Widget, LoggedInTabUI> {
    };

    private static LoggedInTabUiBinder uiBinder = GWT.create(LoggedInTabUiBinder.class);

    @UiField
    protected Table                    table;

    @UiField
    protected Dropdown<String>         domain;

    @UiField(provided = true)
    protected PortalPanel              loggedInPanel;

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    private boolean                    loadedFromCache, reattachChart, isVisible;

    private String                     loadBySection;

    private ArrayList<String>          ranges;

    private ArrayList<AnalysisViewVO>  fullList;

    private ColumnChart                chart;

    // private JFreeChart chart;

    private Options                    options;

    public LoggedInTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();

        loggedInPanel = new PortalPanel() {
            @Override
            public void onResize() {
                refreshChart();
            }
        };

        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<String>> model;
        Item<String> row;
        List<DictionaryDO> list;

        addScreenHandler(table, "loggedInTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTableModel(true);
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                draw(loadBySection);
            }
        });

        loadBySection = "N";
        loadedFromCache = false;

        ranges = new ArrayList<String>();
        ranges.add(Messages.get().today());
        ranges.add(Messages.get().yesterday());
        ranges.add(Messages.get().twoDays());
        ranges.add(Messages.get().threeDays());
        ranges.add(Messages.get().fourToSevenDays());
        ranges.add(Messages.get().eightToTenDays());
        ranges.add(Messages.get().moreThenTenDays());

        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO data : list) {
            row = new Item<String>(data.getCode(), data.getEntry());
            model.add(row);
        }
        domain.setModel(model);
    }

    public void onDataChange(String mySection) {
        loadedFromCache = false;
        draw(mySection);
    }

    public void reattachChart() {
        reattachChart = true;
    }

    public Integer getSelectedId() {
        Row row;
        AnalysisViewVO data;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null)
            return null;

        data = (AnalysisViewVO)row.getData();
        return data.getSampleId();
    }

    public void draw(String loadBySection) {
        if ( !isVisible)
            return;
        if ( !loadedFromCache || !loadBySection.equals(this.loadBySection)) {
            this.loadBySection = loadBySection;
            fireDataChange();
        }
        if (reattachChart) {
            refreshChart();
            reattachChart = false;
        }
        loadedFromCache = true;
    }

    private void loadTableModel(final boolean refreshChart) {
        if (loadedFromCache && fullList != null) {
            table.setModel(getTableModel());
            if (refreshChart)
                refreshChart();
        } else {
            if (parentScreen.getWindow().getContent() != null)
                parentScreen.setBusy(Messages.get().gen_fetching());
            // try {
            // fullList = ToDoService1Impl.INSTANCE.getLoggedIn();
            // table.setModel(getTableModel());
            // } catch (Exception e) {
            // if (e instanceof NotFoundException) {
            // parentScreen.setDone(Messages.get().gen_noRecordsFound());
            // } else {
            // Window.alert(e.getMessage());
            // logger.log(Level.SEVERE, e.getMessage(), e);
            // parentScreen.clearStatus();
            // }
            // }
            ToDoService1Impl.INSTANCE.getLoggedIn(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
                public void onSuccess(ArrayList<AnalysisViewVO> result) {
                    fullList = result;
                    table.setModel(getTableModel());
                    if (refreshChart)
                        refreshChart();
                    parentScreen.clearStatus();
                }

                public void onFailure(Throwable error) {
                    if (error instanceof NotFoundException) {
                        parentScreen.setDone(Messages.get().gen_noRecordsFound());
                    } else {
                        Window.alert(error.getMessage());
                        logger.log(Level.SEVERE, error.getMessage(), error);
                        parentScreen.clearStatus();
                    }

                }
            });
        }
    }

    private ArrayList<Row> getTableModel() {
        boolean sectOnly;
        Integer accNum, prevAccNum;
        String secName;
        Row row;
        Datetime scd, sct, scdt;
        Date temp;
        SystemUserPermission perm;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
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
                row = new Row(13);
                accNum = data.getAccessionNumber();
                row.setCell(0, accNum);
                row.setCell(1, data.getPriority());
                row.setCell(2, data.getDomain());
                row.setCell(3, secName);
                row.setCell(4, data.getTestName());
                row.setCell(5, data.getMethodName());

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

                row.setCell(6, scdt);
                row.setCell(7, data.getReceivedDate());
                row.setCell(8, data.getAnalysisResultOverride());
                row.setCell(9, DataBaseUtil.getPercentHoldingUsed(data.getStartedDate(),
                                                                  data.getCollectionDate(),
                                                                  data.getCollectionTime(),
                                                                  data.getTimeHolding()));
                row.setCell(10, DataBaseUtil.getPercentExpectedCompletion(data.getCollectionDate(),
                                                                          data.getCollectionTime(),
                                                                          data.getReceivedDate(),
                                                                          data.getPriority(),
                                                                          data.getTimeTaAverage()));
                row.setCell(11, data.getToDoDescription());
                row.setCell(12, data.getPrimaryOrganizationName());
                row.setData(data);
                model.add(row);

                prevAccNum = accNum;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }

        return model;
    }

    private void refreshChart() {
        long day, twodays, threedays, sevendays, tendays, avdur, mdur;
        Integer val;
        ArrayList<Row> model;
        Datetime now, avd;
        Date midNight;
        AnalysisViewVO data;
        HashMap<String, Integer> map;

        now = Datetime.getInstance();
        map = new HashMap<String, Integer>();
        model = table.getModel();

        if (model == null) {
            drawChart(new HashMap<String, Integer>());
            return;
        }

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
        for (Row row : model) {
            data = (AnalysisViewVO)row.getData();
            avd = data.getAvailableDate();
            if (avd == null)
                avd = now;
            //
            // the length of the time duration between right now and available
            // date
            //
            avdur = Math.abs(now.getDate().getTime() - avd.getDate().getTime());

            /*
             * If avdur <= mdur then it means that the available date is today.
             * If however avdur lies somewhere between the length of x number of
             * days (mdur+x) and y number of days (mdur+y), then it means that
             * the available date was somewhere between x and y number of days
             * ago. E.g. if x is 3 and y is 7, then the available date was
             * somewhere between 4 and 7 days ago (inclusive of 4 and 7).
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
            } else if ( (mdur + day) < avdur && avdur <= (mdur + twodays)) {
                val = map.get(ranges.get(2));
                if (val == null)
                    val = 0;
                map.put(ranges.get(2), ++val);
            } else if ( (mdur + twodays) < avdur && avdur <= (mdur + threedays)) {
                val = map.get(ranges.get(3));
                if (val == null)
                    val = 0;
                map.put(ranges.get(3), ++val);
            } else if ( (mdur + threedays) < avdur && avdur <= (mdur + sevendays)) {
                val = map.get(ranges.get(4));
                if (val == null)
                    val = 0;
                map.put(ranges.get(4), ++val);
            } else if ( (mdur + sevendays) < avdur && avdur <= (mdur + tendays)) {
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

        drawChart(map);
    }

    private void drawChart(HashMap<String, Integer> map) {
        int size;
        Integer val;
        String range;
        DataTable data;

        data = DataTable.create();
        data.addColumn(ColumnType.STRING);
        data.addColumn(ColumnType.NUMBER, Messages.get().analyses());
        size = ranges.size();
        data.addRows(size);
        for (int i = 0; i < size; i++ ) {
            range = ranges.get(i);
            data.setValue(i, 0, range);
            val = map.get(range);
            if (val == null)
                val = 0;
            data.setValue(i, 1, val.intValue());
        }

        if (options == null)
            options = getOptions();
        options.setWidth(loggedInPanel.getOffsetWidth());
        /*
         * If "chart" is null then this is the first time that it's being drawn,
         * i.e. the tab was opened for the first time. If "reattachChart" is
         * true then chart needs to be re-attached to the panel it's being
         * diplayed in because the screen's being dragged caused chart to get
         * detached. Otherwise, chart can just be redrawn because only the data
         * showing in it changed.
         */
        // TODO
        // chart = createChart(createDataset());
        // ChartPanel panel = new ChartPanel(chart);
        // panel.setFillZoomRectangle(true);
        // panel.setMouseWheelEnabled(true);
        // loggedInPanel.add(panel);

        if (chart == null) {
            chart = new ColumnChart(data, options);
            chart.setWidth("100%");
            chart.setHeight("100%");
            loggedInPanel.add(chart);
        } else if (reattachChart) {
            loggedInPanel.clear();
            chart = new ColumnChart(data, options);
            loggedInPanel.add(chart);
        } else {
            chart.draw(data, options);
        }
    }

    // private static JFreeChart createChart(XYDataset dataset) {
    //
    // JFreeChart chart =
    // ChartFactory.createTimeSeriesChart("Legal & General Unit Trust Prices",
    // // title
    // "Date", // x-axis
    // // label
    // "Price Per Unit", // y-axis
    // // label
    // dataset, // data
    // true, // create
    // // legend?
    // true, // generate
    // // tooltips?
    // false // generate
    // // URLs?
    // );
    //
    // chart.setBackgroundPaint(Color.white);
    //
    // XYPlot plot = (XYPlot)chart.getPlot();
    // plot.setBackgroundPaint(Color.lightGray);
    // plot.setDomainGridlinePaint(Color.white);
    // plot.setRangeGridlinePaint(Color.white);
    // plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
    // plot.setDomainCrosshairVisible(true);
    // plot.setRangeCrosshairVisible(true);
    //
    // XYItemRenderer r = plot.getRenderer();
    // if (r instanceof XYLineAndShapeRenderer) {
    // XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)r;
    // renderer.setBaseShapesVisible(true);
    // renderer.setBaseShapesFilled(true);
    // renderer.setDrawSeriesLineAsPath(true);
    // }
    //
    // DateAxis axis = (DateAxis)plot.getDomainAxis();
    // axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
    //
    // return chart;
    //
    // }
    //
    // private static XYDataset createDataset() {
    //
    // TimeSeries s1 = new TimeSeries("L&G European Index Trust");
    // s1.add(new Month(2, 2001), 181.8);
    // s1.add(new Month(3, 2001), 167.3);
    // s1.add(new Month(4, 2001), 153.8);
    // s1.add(new Month(5, 2001), 167.6);
    // s1.add(new Month(6, 2001), 158.8);
    // s1.add(new Month(7, 2001), 148.3);
    // s1.add(new Month(8, 2001), 153.9);
    // s1.add(new Month(9, 2001), 142.7);
    // s1.add(new Month(10, 2001), 123.2);
    // s1.add(new Month(11, 2001), 131.8);
    // s1.add(new Month(12, 2001), 139.6);
    // s1.add(new Month(1, 2002), 142.9);
    // s1.add(new Month(2, 2002), 138.7);
    // s1.add(new Month(3, 2002), 137.3);
    // s1.add(new Month(4, 2002), 143.9);
    // s1.add(new Month(5, 2002), 139.8);
    // s1.add(new Month(6, 2002), 137.0);
    // s1.add(new Month(7, 2002), 132.8);
    //
    // TimeSeries s2 = new TimeSeries("L&G UK Index Trust");
    // s2.add(new Month(2, 2001), 129.6);
    // s2.add(new Month(3, 2001), 123.2);
    // s2.add(new Month(4, 2001), 117.2);
    // s2.add(new Month(5, 2001), 124.1);
    // s2.add(new Month(6, 2001), 122.6);
    // s2.add(new Month(7, 2001), 119.2);
    // s2.add(new Month(8, 2001), 116.5);
    // s2.add(new Month(9, 2001), 112.7);
    // s2.add(new Month(10, 2001), 101.5);
    // s2.add(new Month(11, 2001), 106.1);
    // s2.add(new Month(12, 2001), 110.3);
    // s2.add(new Month(1, 2002), 111.7);
    // s2.add(new Month(2, 2002), 111.0);
    // s2.add(new Month(3, 2002), 109.6);
    // s2.add(new Month(4, 2002), 113.2);
    // s2.add(new Month(5, 2002), 111.6);
    // s2.add(new Month(6, 2002), 108.8);
    // s2.add(new Month(7, 2002), 101.6);
    //
    // // ******************************************************************
    // // More than 150 demo applications are included with the JFreeChart
    // // Developer Guide...for more information, see:
    // //
    // // > http://www.object-refinery.com/jfreechart/guide.html
    // //
    // // ******************************************************************
    //
    // TimeSeriesCollection dataset = new TimeSeriesCollection();
    // dataset.addSeries(s1);
    // dataset.addSeries(s2);
    //
    // return dataset;
    // }

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

        ops.setWidth(loggedInPanel.getOffsetWidth());
        ops.setHeight(215);
        ops.setTitle(Messages.get().timeSinceAnalysesLoggedIn());
        return ops;
    }

    // private void sortRows(int col, int dir) {
    // try {
    // table.applySort(col, dir, null);
    // } catch (Exception e) {
    // Window.alert("error: " + e.getMessage());
    // logger.log(Level.SEVERE, e.getMessage(), e);
    // }
    // }
}