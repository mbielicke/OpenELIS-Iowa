package org.openelis.modules.organization1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.manager.OrganizationManager1;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ParameterTabUI extends Screen {

    @UiTemplate("ParameterTab.ui.xml")
    interface ParameterTabUiBinder extends UiBinder<Widget, ParameterTabUI> {
    };

    private static ParameterTabUiBinder uiBinder = GWT.create(ParameterTabUiBinder.class);

    @UiField
    protected Table                     table;

    @UiField
    protected Button                    remove, add;

    @UiField
    protected Dropdown<Integer>         type;

    protected Screen                    parentScreen;

    protected EventBus                  parentBus;

    protected boolean                   isVisible, redraw;

    private OrganizationManager1        manager;

    public ParameterTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(State.QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds = new ArrayList<QueryData>();
                QueryData qd;

                for (int i = 0; i < 2; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(OrganizationMeta.getOrganizationParameterTypeId());
                                break;
                            case 1:
                                qd.setKey(OrganizationMeta.getOrganizationParameterValue());
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
                if ( !isState(ADD, UPDATE, QUERY))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrganizationParameterDO data;

                if (isState(State.QUERY))
                    return;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                try {
                    data = manager.parameter.get(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setValue((String)val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.parameter.add();
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.parameter.remove(event.getIndex());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                remove.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayParameters();
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("parameter_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }
        type.setModel(model);
    }

    @UiHandler("remove")
    protected void remove(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        int n;

        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    public void setData(OrganizationManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        OrganizationParameterDO parameter;
        Row r;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.parameter.count();

        /*
         * find out if there's any difference between the parameter being
         * displayed and the parameter in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                parameter = manager.parameter.get(i);
                r = table.getRowAt(i);
                if (DataBaseUtil.isDifferent(parameter.getTypeId(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(parameter.getValue(), r.getCell(1))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayParameters();
    }

    private void displayParameters() {
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
        OrganizationParameterDO data;
        ArrayList<Row> model;

        model = null;
        if (manager == null)
            return model;

        try {
            model = new ArrayList<Row>();
            for (i = 0; i < manager.parameter.count(); i++ ) {
                data = manager.parameter.get(i);

                row = new Row(2);
                row.setCell(0, data.getTypeId());
                row.setCell(1, data.getValue());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
}
