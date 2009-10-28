package org.openelis.modules.organization.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.OrganizationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ParameterTab extends Screen {

    private OrganizationManager manager;
    private TableWidget         table;
    private AppButton           removeButton, addButton;
    private boolean             loaded;

    public ParameterTab(ScreenDefInt def) {
        setDef(def);
        initialize();

        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("parameterTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                    .contains(event.getState()));
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrganizationParameterDO data;

                if (state == State.QUERY)
                    return;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r, c);

                try {
                    data = manager.getParameters().getParameterAt(r);
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
                try {
                    manager.getParameters().addParameter(new OrganizationParameterDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getParameters().removeParameterAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        removeButton = (AppButton)def.getWidget("removeParameterButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("addParameterButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                table.addRow();
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("parameter_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget()).setModel(model);
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        OrganizationParameterDO data;
        ArrayList<TableDataRow> model;

        model = null;
        if (manager == null)
            return model;

        try {
            model = new ArrayList<TableDataRow>();
            for (i = 0; i < manager.getParameters().count(); i++) {
                data = (OrganizationParameterDO)manager.getParameters().getParameterAt(i);

                row = new TableDataRow(2);
                row.cells.get(0).value = data.getTypeId();
                row.cells.get(1).value = data.getValue();
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    public void setManager(OrganizationManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
