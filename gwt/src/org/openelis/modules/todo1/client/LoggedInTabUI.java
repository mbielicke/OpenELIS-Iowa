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

import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.FilterEvent;
import org.openelis.ui.widget.table.event.FilterHandler;
import org.openelis.utilcommon.TurnaroundUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class LoggedInTabUI extends Screen {

    @UiTemplate("LoggedInTab.ui.xml")
    interface LoggedInTabUiBinder extends UiBinder<Widget, LoggedInTabUI> {
    };

    private static LoggedInTabUiBinder               uiBinder = GWT.create(LoggedInTabUiBinder.class);

    @UiField
    protected Table                                  table;

    @UiField
    protected Dropdown<String>                       domain;

    @UiField
    protected Chart                                  chart;

    protected Screen                                 parentScreen;

    protected EventBus                               parentBus;

    private boolean                                  visible, load, draw, mySection;

    private ArrayList<AnalysisViewVO>                analyses;

    private Series                                   chartSeries[];

    private int                                      chartStats[][];

    private HashMap<String, Integer>                 domains;

    private ScheduledCommand                         drawChartCmd;

    private AsyncCallback<ArrayList<AnalysisViewVO>> getLoggedInCall;

    public LoggedInTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();

        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        int i;
        Item<String> row;
        List<DictionaryDO> list;
        ArrayList<Item<String>> model;

        mySection = true;
        load = true;

        addScreenHandler(table, "loggedInTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());

                draw = true;
                recalculateChart();
                update();
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

        table.addFilterHandler(new FilterHandler() {
            public void onFilter(FilterEvent event) {
                draw = true;
                recalculateChart();
                update();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                visible = event.isVisible();
                update();
            }
        });

        i = 0;
        domains = new HashMap<String, Integer>();
        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive())) {
                row = new Item<String>(data.getCode(), data.getEntry());
                model.add(row);

                domains.put(data.getCode(), i++ );
            }
        }
        domain.setModel(model);

        /*
         * chart at the bottom
         */
        chart.setCredits(new Credits().setEnabled(false))
             .setType(Series.Type.COLUMN)
             .setReflow(false)
             .setColumnPlotOptions(new ColumnPlotOptions().setStacking(PlotOptions.Stacking.NORMAL))
             .setLegend(new Legend().setLayout(Legend.Layout.VERTICAL)
                                    .setAlign(Legend.Align.RIGHT)
                                    .setVerticalAlign(Legend.VerticalAlign.TOP)
                                    .setX(10)
                                    .setY(20)
                                    .setBorderWidth(0))
             .setChartTitleText("");

        chart.getXAxis().setCategories(Messages.get().todo_today(),
                                       Messages.get().todo_yesterday(),
                                       Messages.get().todo_twoDays(),
                                       Messages.get().todo_threeDays(),
                                       Messages.get().todo_fourToSevenDays(),
                                       Messages.get().todo_eightToTenDays(),
                                       Messages.get().todo_moreThenTenDays());
        chart.getYAxis()
             .setAllowDecimals(false)
             .setAxisTitleText(Messages.get().todo_loggedinAnalysis())
             .setMin(0);

        chart.addAttachHandler(new AttachEvent.Handler() {
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    draw = true;
                    update();
                }
            }
        });

        chartSeries = new Series[list.size()];
        for (DictionaryDO data : list) {
            i = domains.get(data.getCode());
            chartSeries[i] = chart.createSeries().setName(data.getEntry());
            chart.addSeries(chartSeries[i]);
        }

        /*
         * call for fetch data
         */
        getLoggedInCall = new AsyncCallback<ArrayList<AnalysisViewVO>>() {
            public void onSuccess(ArrayList<AnalysisViewVO> result) {
                analyses = result;
                fireDataChange();
                parentScreen.clearStatus();
            }

            public void onFailure(Throwable error) {
                analyses = null;
                fireDataChange();
                if (error instanceof NotFoundException) {
                    parentScreen.setDone(Messages.get().gen_noRecordsFound());
                } else {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    parentScreen.clearStatus();
                }
            }
        };

        /*
         * call to refresh the chart
         */
        drawChartCmd = new ScheduledCommand() {
            @Override
            public void execute() {
                drawChart();
            }
        };
    }

    /*
     * Returns the current selected records's id
     */
    public Integer getSelectedId() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row != null)
            return ((AnalysisViewVO)row.getData()).getSampleId();

        return null;
    }

    public void setMySectionOnly(boolean mySection) {
        if (this.mySection != mySection) {
            this.mySection = mySection;
            fireDataChange();
        }
    }

    /*
     * Refetch's the data
     */
    public void refresh() {
        load = true;
        update();
    }

    /*
     * update the data, chart, etc.
     */
    private void update() {
        if (visible) {
            if (load) {
                load = false;
                draw = false;
                parentScreen.setBusy(Messages.get().gen_fetching());
                ToDoService1Impl.INSTANCE.getLoggedIn(getLoggedInCall);
            } else if (draw) {
                draw = false;
                Scheduler.get().scheduleDeferred(drawChartCmd);
            }
        }
    }

    /*
     * Returns the fetched data as a table model
     */
    private ArrayList<Row> getTableModel() {
        Row row;
        ArrayList<Row> model;
        SystemUserPermission perm;

        model = new ArrayList<Row>();
        if (analyses != null) {
            perm = UserCache.getPermission();

            for (AnalysisViewVO a : analyses) {
                if (mySection && perm.getSection(a.getSectionName()) == null)
                    continue;

                row = new Row(a.getAccessionNumber(),
                              a.getPriority(),
                              a.getDomain(),
                              a.getSectionName(),
                              a.getTestName(),
                              a.getMethodName(),
                              TurnaroundUtil.getCombinedYM(a.getCollectionDate(),
                                                           a.getCollectionTime()),
                              a.getReceivedDate(),
                              a.getAnalysisResultOverride(),
                              TurnaroundUtil.getPercentHoldingUsed(a.getStartedDate(),
                                                                   a.getCollectionDate(),
                                                                   a.getCollectionTime(),
                                                                   a.getTimeHolding()),
                              TurnaroundUtil.getPercentExpectedCompletion(a.getCollectionDate(),
                                                                          a.getCollectionTime(),
                                                                          a.getReceivedDate(),
                                                                          a.getPriority(),
                                                                          a.getTimeTaAverage()),
                              a.getToDoDescription(),
                              a.getPrimaryOrganizationName());
                row.setData(a);
                model.add(row);
            }
        }

        return model;
    }

    /*
     * Redraws the chart series
     */
    private void drawChart() {
        int i, j;
        Integer stack[];

        chart.reflow();

        /*
         * draw each stack; chart needs to keep the array until it draws
         */
        if (chartStats != null) {
            stack = new Integer[7];
            for (i = 0; i < domains.size(); i++ ) {
                for (j = 0; j < 7; j++ )
                    stack[j] = chartStats[i][j];
                chartSeries[i].setPoints(stack, true);
            }
        }
    }

    /*
     * Creates the chart data model using table model
     */
    private void recalculateChart() {
        int i;
        long days[], day;
        ArrayList<Row> model;
        AnalysisViewVO a;

        model = table.getModel();
        if (model != null) {
            chartStats = new int[domains.size()][7];
            days = last10Days();
            for (Row r : model) {
                // filtered out
                if (table.convertModelIndexToView(r) == -1)
                    continue;

                a = (AnalysisViewVO)r.getData();
                i = domains.get(a.getDomain());

                if (a.getAvailableDate() != null)
                    day = a.getAvailableDate().getDate().getTime();
                else
                    day = days[0];

                if (day >= days[0])
                    chartStats[i][0]++ ;
                else if (day >= days[1])
                    chartStats[i][1]++ ;
                else if (day >= days[2])
                    chartStats[i][2]++ ;
                else if (day >= days[3])
                    chartStats[i][3]++ ;
                else if (day >= days[7])
                    chartStats[i][4]++ ;
                else if (day >= days[9])
                    chartStats[i][5]++ ;
                else
                    chartStats[i][6]++ ;
            }
        } else {
            chartStats = null;
        }
    }

    /*
     * Returns a list representing previous 10 days in Millis
     */
    private long[] last10Days() {
        long days[];
        Date day;

        /*
         * fix the time to midnight for each day
         */
        day = new Date();
        days = new long[10];
        for (int i = 0; i < 10; i++ ) {
            day.setHours(0);
            day.setMinutes(0);
            day.setSeconds(0);
            days[i] = day.getTime();
            day.setTime(day.getTime() - 86400000L);
        }
        return days;
    }
}