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
package org.openelis.modules.order1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.Arrays;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.manager.OrderManager1;
import org.openelis.meta.OrderMeta;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ContainerTabUI extends Screen {

    @UiTemplate("ContainerTab.ui.xml")
    interface ContainerTabUiBinder extends UiBinder<Widget, ContainerTabUI> {
    };

    private static ContainerTabUiBinder uiBinder = GWT.create(ContainerTabUiBinder.class);

    @UiField
    protected Table                     table;

    @UiField
    protected Dropdown<Integer>         container, sampleType;

    @UiField
    protected Button                    removeContainerButton, addContainerButton,
                    duplicateContainerButton, moveUpButton, moveDownButton;

    protected Screen                    parentScreen;

    protected EventBus                  parentBus;

    protected boolean                   isVisible, redraw;

    protected OrderManager1             manager;

    public ContainerTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        Item<Integer> item;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
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
                for (int i = 0; i < 3; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(OrderMeta.getContainerItemSequence());
                                break;
                            case 1:
                                qd.setKey(OrderMeta.getContainerContainerId());
                                break;
                            case 2:
                                qd.setKey(OrderMeta.getContainerTypeOfSampleId());
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
                if ( !isState(ADD, UPDATE) || event.getCol() == 0)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderContainerDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                data = manager.container.get(r);

                switch (c) {
                    case 1:
                        data.setContainerId((Integer)val);
                        break;
                    case 2:
                        data.setTypeOfSampleId((Integer)val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int index;
                OrderContainerDO data;
                Row row;

                row = event.getRow();
                index = event.getIndex();
                data = manager.container.add(index);
                data.setContainerId((Integer)row.getCell(1));
                data.setTypeOfSampleId((Integer)row.getCell(2));
                table.setModel(getTableModel());
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index;

                index = event.getIndex();
                manager.container.remove(index);
                table.setModel(getTableModel());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeContainerButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addContainerButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicateContainerButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveDownButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveUpButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayContainers();
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("sample_container");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        container.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("type_of_sample");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        sampleType.setModel(model);
    }

    @UiHandler("addContainerButton")
    protected void addContainer(ClickEvent event) {
        int n;
        OrderContainerDO data;
        Row row;

        table.finishEditing();

        n = table.getRowCount();
        row = new Row(3);
        row.setCell(0, n);
        if (n > 0) {
            data = table.getRowAt(n - 1).getData();
            row.setCell(2, data.getTypeOfSampleId());
        }
        table.addRow(row);
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 1);
    }

    @UiHandler("removeContainerButton")
    protected void removeContainer(ClickEvent event) {
        int r;
        Integer[] rows;

        rows = table.getSelectedRows();
        Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i-- ) {
            r = rows[i];
            if (r > -1 && table.getRowCount() > 0)
                table.removeRowAt(r);
        }
    }

    @UiHandler("duplicateContainerButton")
    protected void duplicateContainer(ClickEvent event) {
        int n;
        OrderContainerDO data;
        Row row;

        if (table.getSelectedRows().length > 1) {
            parentScreen.getWindow()
                        .setError(Messages.get()
                                          .order_multiRowDuplicateNotAllowed(DataBaseUtil.toString(manager.getOrder()
                                                                                                          .getId())));
            return;
        }

        table.finishEditing();

        n = table.getSelectedRow() + 1;
        if (n == 0)
            return;
        row = new Row(3);
        row.setCell(0, n);
        if (n > 0) {
            data = table.getRowAt(table.getSelectedRow()).getData();
            row.setCell(1, data.getContainerId());
            row.setCell(2, data.getTypeOfSampleId());
        }
        table.addRowAt(n, row);
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 1);
    }

    @UiHandler("moveUpButton")
    protected void moveUp(ClickEvent event) {
        int r;

        if (table.getSelectedRows().length > 1) {
            parentScreen.getWindow()
                        .setError(Messages.get()
                                          .order_multiRowMoveNotAllowed(DataBaseUtil.toString(manager.getOrder()
                                                                                                     .getId())));
            return;
        }

        table.finishEditing();
        r = table.getSelectedRow();
        if (r <= 0)
            return;

        manager.container.move(r, r - 1);
        table.setModel(getTableModel());
        table.selectRowAt(r - 1);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(r - 1, 1);

    }

    @UiHandler("moveDownButton")
    protected void moveDown(ClickEvent event) {
        int r;

        if (table.getSelectedRows().length > 1) {
            parentScreen.getWindow()
                        .setError(Messages.get()
                                          .order_multiRowMoveNotAllowed(DataBaseUtil.toString(manager.getOrder()
                                                                                                     .getId())));
            return;
        }

        table.finishEditing();
        r = table.getSelectedRow();
        if (r >= manager.container.count() - 1)
            return;

        manager.container.move(r, r + 1);
        table.setModel(getTableModel());
        table.selectRowAt(r + 1);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(r + 1, 1);
    }

    public void setData(OrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        OrderContainerDO data;
        Row r;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.container.count();

        /*
         * find out if there's any difference between the container being
         * displayed and the container in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                r = table.getRowAt(i);
                data = manager.container.get(i);

                if (DataBaseUtil.isDifferent(data.getItemSequence(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(data.getContainerId(), r.getCell(1)) ||
                    DataBaseUtil.isDifferent(data.getTypeOfSampleId(), r.getCell(2))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayContainers();
    }

    private void displayContainers() {
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
        OrderContainerDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.container.count(); i++ ) {
            data = manager.container.get(i);

            row = new Row(3);
            row.setCell(0, data.getItemSequence());
            row.setCell(1, data.getContainerId());
            row.setCell(2, data.getTypeOfSampleId());
            row.setData(data);
            model.add(row);
        }

        return model;
    }
}
