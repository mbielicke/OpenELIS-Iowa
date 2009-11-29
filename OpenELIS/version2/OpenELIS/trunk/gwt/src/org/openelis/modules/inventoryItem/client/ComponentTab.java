package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.InventoryItemManager;
import org.openelis.meta.InventoryItemMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ComponentTab extends Screen {

    private InventoryItemManager manager;
    private TableWidget          table;
    private AutoComplete         componentId;
    private AppButton            addComponentButton, removeComponentButton;
    private boolean              loaded;

    public ComponentTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

        setDef(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("componentTable");
        componentId = (AutoComplete)table.getColumnWidget(InventoryItemMeta.getComponentName());
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
                TableDataRow row;
                InventoryComponentViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r, c);

                try {
                    data = manager.getComponents().getComponentAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        row = (TableDataRow)val;
                        data.setComponentId((Integer)row.key);
                        if (row.key == null) return;
                        data.setComponentDescription((String)row.getCells().get(1));
                        table.setCell(r, 1, data.getComponentDescription());
                        break;
                    case 2:
                        data.setQuantity((Double)val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getComponents().addComponent(new InventoryComponentViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getComponents().removeComponentAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        componentId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Query query;
                QueryData field;
                QueryFieldUtil parser;
                InventoryItemDO data;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;

                if (manager.getInventoryItem().getStoreId() == null) {
                    window.setError(consts.get("inventoryNoStoreException"));
                    return;
                }
                window.clearStatus();
                
                query = new Query();
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                field = new QueryData();
                field.key = InventoryItemMeta.getName();
                field.type = QueryData.Type.STRING;
                field.query = parser.getParameter().get(0);
                query.setFields(field);

                field = new QueryData();
                field.key = InventoryItemMeta.getStoreId();
                field.type = QueryData.Type.INTEGER;
                field.query = manager.getInventoryItem().getStoreId().toString();
                query.setFields(field);

                try {
                    list = service.callList("fetchActiveByNameAndStore", query); 
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName(),
                                                   data.getDescription()));
                    }
                    componentId.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addComponentButton = (AppButton)def.getWidget("addComponentButton");
        addScreenHandler(addComponentButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                table.addRow();
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addComponentButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeComponentButton = (AppButton)def.getWidget("removeComponentButton");
        addScreenHandler(removeComponentButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeComponentButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
            }
        });

    }

    private ArrayList<TableDataRow> getTableModel() {
        int i;
        InventoryComponentViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getComponents().count(); i++ ) {
                data = (InventoryComponentViewDO)manager.getComponents().getComponentAt(i);
                model.add(new TableDataRow(null,
                                           new TableDataRow(data.getComponentId(),
                                                            data.getComponentName()),
                                           data.getComponentDescription(),
                                           data.getQuantity()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    public void setManager(InventoryItemManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}