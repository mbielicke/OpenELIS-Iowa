package org.openelis.modules.provider1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ProviderAnalyteViewDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.meta.ProviderMeta;
import org.openelis.modules.analyte1.client.AnalyteService1Impl;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalyteTabUI extends Screen {

    @UiTemplate("AnalyteTab.ui.xml")
    interface AnalyteTabUIBinder extends UiBinder<Widget, AnalyteTabUI> {
    };

    private static AnalyteTabUIBinder uiBinder = GWT.create(AnalyteTabUIBinder.class);

    private ProviderManager1          manager;

    @UiField
    protected Table                   table;

    @UiField
    protected AutoComplete            analyte;

    @UiField
    protected Button                  addAnalyteButton, removeAnalyteButton;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    private boolean                   redraw, isVisible;

    public AnalyteTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                if ( !isState(QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(QUERY, DISPLAY, ADD, UPDATE));
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 1; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(ProviderMeta.getProviderAnalyteAnalyteName());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(QUERY, ADD, UPDATE))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalyteDO ana;
                ProviderAnalyteViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);
                data = manager.analyte.get(r);

                switch (c) {
                    case 0:
                        ana = (AnalyteDO) ( ((AutoCompleteValue)val).getData());
                        if (ana != null) {
                            data.setAnalyteId(ana.getId());
                            data.setAnalyteName(ana.getName());
                        } else {
                            data.setAnalyteId(null);
                            data.setAnalyteName(null);
                        }
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                ProviderAnalyteViewDO data;

                data = manager.analyte.add();
                event.getRow().setData(data);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.analyte.remove(event.getIndex());
            }
        });

        analyte.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                getAnalyteMatches(event.getMatch());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addAnalyteButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeAnalyteButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayAnalytes();
            }
        });
    }

    @UiHandler("addAnalyteButton")
    protected void addAnalyte(ClickEvent event) {
        int n;

        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    @UiHandler("removeAnalyteButton")
    protected void removeAnalyte(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    public void setData(ProviderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        Integer id;
        ProviderAnalyteViewDO analyte;
        Row r;
        AutoCompleteValue val;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.analyte.count();

        /*
         * find out if there's any difference between the analyte being
         * displayed and the analyte in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                r = table.getRowAt(i);
                analyte = manager.analyte.get(i);
                val = r.getCell(0);
                id = val != null ? val.getId() : null;
                if (DataBaseUtil.isDifferent(analyte.getId(), id)) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayAnalytes();
    }

    private void displayAnalytes() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        ProviderAnalyteViewDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.analyte.count(); i++ ) {
            data = manager.analyte.get(i);
            row = new Row(1);
            row.setCell(0, new AutoCompleteValue(data.getAnalyteId(), data.getAnalyteName()));
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private void getAnalyteMatches(String match) {
        Item<Integer> row;
        ArrayList<AnalyteDO> list;
        ArrayList<Item<Integer>> model;

        parentScreen.setBusy();
        try {
            list = AnalyteService1Impl.INSTANCE.fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (AnalyteDO data : list) {
                row = new Item<Integer>(1);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());

                model.add(row);
            }
            analyte.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        parentScreen.clearStatus();
    }
}