package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
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

public class ToBeVerifiedTabUI extends Screen {

    @UiTemplate("ToBeVerifiedTab.ui.xml")
    interface ToBeVerifiedTabUiBinder extends UiBinder<Widget, ToBeVerifiedTabUI> {
    };

    private static ToBeVerifiedTabUiBinder uiBinder = GWT.create(ToBeVerifiedTabUiBinder.class);

    @UiField
    protected Table                        table;

    @UiField
    protected Dropdown<String>             domain;

    @UiField(provided = true)
    protected PortalPanel                  toBeVerifiedPanel;

    protected Screen                       parentScreen;

    protected EventBus                     parentBus;

    private boolean                        loadedFromCache;

    private boolean                        reattachChart;

    private boolean                        isVisible;

    private String                         loadBySection;

    private ArrayList<String>              ranges;

    private ArrayList<ToDoSampleViewVO>    fullList;

    private ColumnChart                    chart;

    private Options                        options;

    public ToBeVerifiedTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        parentBus = parentScreen.getEventBus();

        toBeVerifiedPanel = new PortalPanel() {
            @Override
            public void onResize() {
                refreshChart();
            }
        };

        initWidget((Widget)uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<String>> model;
        Item<String> row;
        List<DictionaryDO> list;

        addScreenHandler(table, "toBeVerifiedTable", new ScreenHandler<ArrayList<Row>>() {
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

        row = table.getRowAt(table.getSelectedRow());
        if (row == null) {
            return null;
        }
        return ((ToDoSampleViewVO)row.getData()).getSampleId();
    }

    public void draw(String loadBySection) {
        if ( !isVisible) {
            return;
        }
        if ( ( !loadedFromCache) || ( !loadBySection.equals(this.loadBySection))) {
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
        if (loadedFromCache) {
            table.setModel(getTableModel());
            if (refreshChart) {
                refreshChart();
            }
        } else {
            parentScreen.setBusy(Messages.get().fetching());
            ToDoService1Impl.INSTANCE.getToBeVerified(new AsyncCallback<ArrayList<ToDoSampleViewVO>>() {
                public void onSuccess(ArrayList<ToDoSampleViewVO> result) {
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
        Date temp;
        Datetime scd, sct;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        try {
            for (ToDoSampleViewVO data : fullList) {
                row = new Row(7);
                row.setCell(0, data.getAccessionNumber());
                row.setCell(1, data.getDomain());
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
                    row.setCell(2, Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp));
                }
                row.setCell(3, data.getReceivedDate());
                row.setCell(4, data.getSampleResultOverride());
                row.setCell(5, data.getDescription());
                row.setCell(6, data.getPrimaryOrganizationName());
                row.setData(data);
                model.add(row);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        return model;
    }

    private void refreshChart() {
        long day, twodays, threedays, sevendays, tendays, mdur, avdur;
        Integer val;
        ArrayList<Row> model;
        Datetime now, srd;
        Date midNight;
        ToDoSampleViewVO data;
        HashMap<String, Integer> map;

        now = Datetime.getInstance();
        map = new HashMap<String, Integer>();
        model = table.getModel();
        if (model == null) {
            drawChart(new HashMap<String, Integer>());
            return;
        }
        day = 86400000L;
        twodays = 2L * day;
        threedays = 3L * day;
        sevendays = 7L * day;
        tendays = 10L * day;

        midNight = new Date();
        midNight.setHours(0);
        midNight.setMinutes(0);
        midNight.setSeconds(0);

        mdur = Math.abs(now.getDate().getTime() - midNight.getTime());
        for (Row row : model) {
            data = (ToDoSampleViewVO)row.getData();
            srd = data.getReceivedDate();
            if (srd == null) {
                srd = now;
            }
            avdur = Math.abs(now.getDate().getTime() - srd.getDate().getTime());
            if (avdur <= mdur) {
                val = (Integer)map.get(ranges.get(0));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(0), val = Integer.valueOf(val.intValue() + 1));
            } else if ( (mdur < avdur) && (avdur <= mdur + day)) {
                val = (Integer)map.get(ranges.get(1));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(1), val = Integer.valueOf(val.intValue() + 1));
            } else if ( (mdur + day < avdur) && (avdur <= mdur + twodays)) {
                val = (Integer)map.get(ranges.get(2));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(2), val = Integer.valueOf(val.intValue() + 1));
            } else if ( (mdur + twodays < avdur) && (avdur <= mdur + threedays)) {
                val = (Integer)map.get(ranges.get(3));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(3), val = Integer.valueOf(val.intValue() + 1));
            } else if ( (mdur + threedays < avdur) && (avdur <= mdur + sevendays)) {
                val = (Integer)map.get(ranges.get(4));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(4), val = Integer.valueOf(val.intValue() + 1));
            } else if ( (mdur + sevendays < avdur) && (avdur <= mdur + tendays)) {
                val = (Integer)map.get(ranges.get(5));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(5), val = Integer.valueOf(val.intValue() + 1));
            } else if (avdur > mdur + tendays) {
                val = (Integer)map.get(ranges.get(6));
                if (val == null) {
                    val = Integer.valueOf(0);
                }
                map.put((String)ranges.get(6), val = Integer.valueOf(val.intValue() + 1));
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
        data.addColumn(ColumnType.NUMBER, "Samples");
        size = ranges.size();
        data.addRows(size);
        for (int i = 0; i < size; i++ ) {
            range = (String)ranges.get(i);
            data.setValue(i, 0, range);
            val = (Integer)map.get(range);
            if (val == null) {
                val = Integer.valueOf(0);
            }
            data.setValue(i, 1, val.intValue());
        }
        if (options == null) {
            options = getOptions();
        }
        options.setWidth(toBeVerifiedPanel.getOffsetWidth());
        if (chart == null) {
            chart = new ColumnChart(data, options);
            toBeVerifiedPanel.add(chart);
        } else if (reattachChart) {
            toBeVerifiedPanel.clear();
            chart = new ColumnChart(data, options);
            toBeVerifiedPanel.add(chart);
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
        aops.setTitle(Messages.get().numSamples());
        ops.setVAxisOptions(aops);

        aops = AxisOptions.create();
        fts = TextStyle.create();
        fts.setFontSize(10);

        aops.setTextStyle(fts);

        ops.setHAxisOptions(aops);
        ops.setWidth(toBeVerifiedPanel.getOffsetWidth());
        ops.setHeight(215);
        ops.setTitle(Messages.get().timeSinceSamplesReceived());
        return ops;
    }
}
