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
package org.openelis.modules.test1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.manager.TestManager1;
import org.openelis.meta.TestMeta;
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
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SampleTypeTabUI extends Screen {

    @UiTemplate("SampleTypeTab.ui.xml")
    interface SampleTypeTabUiBinder extends UiBinder<Widget, SampleTypeTabUI> {
    };

    private static SampleTypeTabUiBinder uiBinder = GWT.create(SampleTypeTabUiBinder.class);

    @UiField
    protected Button                     addButton, removeButton;

    @UiField
    protected Table                      table;

    @UiField
    protected Dropdown<Integer>          typeOfSample, unitOfMeasure;

    protected Screen                     parentScreen;

    protected EventBus                   parentBus;

    protected boolean                    isVisible, redraw;

    protected TestManager1               manager;

    public SampleTypeTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    public void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        Item<Integer> item;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 9; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(TestMeta.getTypeOfSampleTypeOfSampleId());
                                break;
                            case 1:
                                qd.setKey(TestMeta.getTypeOfSampleUnitOfMeasureId());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE, QUERY))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer val;
                TestTypeOfSampleDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                data = manager.type.get(r);

                switch (c) {
                    case 0:
                        data.setTypeOfSampleId(val);
                        break;
                    case 1:
                        data.setUnitOfMeasureId(val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.type.add(new TestTypeOfSampleDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                // TestTypeOfSampleDO data;
                int index;
                try {
                    index = event.getIndex();
                    // data = manager.type.get(index);
                    manager.type.remove(index);
                    // ActionEvent.fire(this, Action.UNIT_DELETED, data);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySampleTypes();
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("type_of_sample");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        typeOfSample.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("unit_of_measure");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        unitOfMeasure.setModel(model);
    }

    // @UiHandler("addButton")
    protected void addTypeOfSample(ClickEvent event) {
        int n;

        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    // @UiHandler("removeButton")
    protected void removeTypeOfSample(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    public void setData(TestManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        TestTypeOfSampleDO data;
        Row r;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.type.count();

        /*
         * find out if there's any difference between the types being displayed
         * and the types in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                r = table.getRowAt(i);
                data = manager.type.get(i);
                if (DataBaseUtil.isDifferent(data.getTypeOfSampleId(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(data.getUnitOfMeasureId(), r.getCell(1))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displaySampleTypes();
    }

    private void displaySampleTypes() {
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
        TestTypeOfSampleDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.type.count(); i++ ) {
            data = manager.type.get(i);
            row = new Row(2);
            row.setCell(0, data.getTypeOfSampleId());
            row.setCell(1, data.getUnitOfMeasureId());
            row.setData(data);
            model.add(row);
        }

        return model;
    }
}