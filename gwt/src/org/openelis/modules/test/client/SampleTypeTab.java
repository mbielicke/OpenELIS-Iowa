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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.TestMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleTypeTab extends Screen implements HasActionHandlers<SampleTypeTab.Action> {

    public enum Action {
        UNIT_CHANGED, UNIT_DELETED;
    };

    private TestManager   manager;
    private boolean       loaded;

    private SampleTypeTab screen;
    private TableWidget   table;
    private AppButton     addButton, removeButton;

    public SampleTypeTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);       
        
        initialize();
        initializeDropdowns();       
    }

    private void initialize() {
        screen = this;

        table = (TableWidget)def.getWidget("sampleTypeTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(event.getState() == State.QUERY);
            }            
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();
            }            
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Integer val;
                TestTypeOfSampleDO data;

                row = event.getRow();
                col = event.getCol();
                val = (Integer)table.getObject(row,col);
                try {
                    data = manager.getSampleTypes().getTypeAt(row);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (col) {
                    case 0:
                        data.setTypeOfSampleId(val);
                        break;
                    case 1:
                        data.setUnitOfMeasureId(val);
                        ActionEvent.fire(screen, Action.UNIT_CHANGED, data);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getSampleTypes().addType(new TestTypeOfSampleDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                TestTypeOfSampleDO data;
                int index;
                try {
                    index = event.getIndex();
                    data = manager.getSampleTypes().getTypeAt(index);
                    manager.getSampleTypes().removeTypeAt(index);
                    ActionEvent.fire(screen, Action.UNIT_DELETED, data);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addButton = (AppButton)def.getWidget("addSampleTypeButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                table.addRow();
                table.selectRow(table.numRows() - 1);
                table.scrollToSelection();
                table.startEditing(table.numRows() - 1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeButton = (AppButton)def.getWidget("removeSampleTypeButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int i;

                i = table.getSelectedRow();
                if (i > -1 && table.numRows() > 0)
                    table.deleteRow(i);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("type_of_sample");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown)table.getColumnWidget(TestMeta.getTypeOfSampleTypeOfSampleId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown)table.getColumnWidget(TestMeta.getTypeOfSampleUnitOfMeasureId())).setModel(model);

    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestTypeOfSampleManager ttsm;
        TestTypeOfSampleDO sampleType;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            ttsm = manager.getSampleTypes();
            for (int i = 0; i < ttsm.count(); i++ ) {
                sampleType = ttsm.getTypeAt(i);
                row = new TableDataRow(2);
                row.key = sampleType.getId();

                row.cells.get(0).value = sampleType.getTypeOfSampleId();
                row.cells.get(1).value = sampleType.getUnitOfMeasureId();
                model.add(row);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    public HandlerRegistration addActionHandler(ActionHandler<SampleTypeTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    protected void clearKeys(TestTypeOfSampleManager ttsm) {
        TestTypeOfSampleDO sampletype;

        for (int i = 0; i < ttsm.count(); i++ ) {
            sampletype = ttsm.getTypeAt(i);
            sampletype.setId(null);
            sampletype.setTestId(null);
        }
    }

    protected void finishEditing() {
        table.finishEditing();
    }

}
