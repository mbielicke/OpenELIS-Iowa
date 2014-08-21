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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeDragStartEvent;
import org.openelis.gwt.event.BeforeDragStartHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.RowMovedEvent;
import org.openelis.gwt.widget.table.event.RowMovedHandler;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class ContainerTab extends Screen {

    private OrderManager            manager;
    private AppButton               addContainerButton, removeContainerButton, popoutButton,
                                    duplicateButton;
    private TableWidget             table;
    private ContainerTab            screen;
    private boolean                 loaded;
    private TestContainerPopoutUtil popoutUtil;
    
    public ContainerTab(ScreenDefInt def, ScreenWindowInt window, TestContainerPopoutUtil popoutUtil) {

        this.popoutUtil = popoutUtil;
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {                
        screen = this;
        
        table = (TableWidget)def.getWidget("orderContainerTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    table.load(getContainerTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                table.enable(true);
                table.setQueryMode(event.getState() == State.QUERY);
                
                enable = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                table.enableDrag(enable);
                table.enableDrop(enable);
            }
        });
        
        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if((state == State.ADD || state == State.UPDATE)) 
                    duplicateButton.enable(true);
            }
        });
                    
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if((state != State.ADD && state != State.UPDATE) || event.getCol() == 0) 
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
                val = table.getObject(r,c);
                
                try {
                    data = manager.getContainers().getContainerAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
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
                TableDataRow row;
                OrderContainerDO data;
                
                try {
                    index = event.getIndex();
                    row = table.getRow(index);
                    
                    data = new OrderContainerDO();
                    data.setItemSequence(index);
                    data.setContainerId((Integer)row.cells.get(1).getValue());
                    data.setTypeOfSampleId((Integer)row.cells.get(2).getValue());
                    
                    manager.getContainers().addContainerAt(data, index);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index;
                OrderContainerManager man;
                
                index = event.getIndex();
                try {
                    man = manager.getContainers();
                    man.removeContainerAt(index);
                    /*
                     * if the removed container was not at the end of the old list 
                     * then the sequences of all the containers appearing after 
                     * it in the old list needs to be reset
                     */
                    resetSequencesFrom(index);                                                    
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        
        table.enableDrag(true);
        table.enableDrop(true);
        table.addTarget(table);
        
        table.addBeforeDragStartHandler(new BeforeDragStartHandler<TableRow>() {
            public void onBeforeDragStart(BeforeDragStartEvent<TableRow> event) {
                /*
                 * this is done to make sure that the drag and drop doesn't interfere
                 * with editing of the cells of the table
                 */
                if (table.isEditing())
                    event.cancel();
            }
            
        });
        
        table.addRowMovedHandler(new RowMovedHandler() {
            public void onRowMoved(RowMovedEvent event) {
                int oldIndex, newIndex;
                OrderContainerManager man;
                
                try {
                    man = manager.getContainers();
                    oldIndex = event.getOldIndex();
                    newIndex = event.getNewIndex();
                    man.moveContainer(oldIndex, newIndex);
                    /*
                     * the sequence in the containers appearing in the new list 
                     * needs to be reset including that of the dropped object 
                     */
                    resetSequencesFrom(newIndex < oldIndex ? newIndex : oldIndex);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        
        addContainerButton = (AppButton)def.getWidget("addContainerButton");
        addScreenHandler(addContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n, r;
                TableDataRow row;
                OrderContainerDO prevData;
                OrderContainerManager man;

                table.finishEditing();
                
                r = table.numRows();
                row = new TableDataRow(3);
                if (r > 0) {
                    try {
                        man = manager.getContainers();
                        prevData = man.getContainerAt(r-1);
                        row.cells.get(0).setValue(r);
                        row.cells.get(2).setValue(prevData.getTypeOfSampleId());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                }
                table.addRow(r, row);
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addContainerButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeContainerButton = (AppButton)def.getWidget("removeContainerButton");
        addScreenHandler(removeContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeContainerButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        popoutButton = (AppButton)def.getWidget("containerPopoutButton");
        if (popoutButton != null) {
            addScreenHandler(popoutButton, new ScreenEventHandler<Object>() {
                public void onClick(ClickEvent event) {
                    table.finishEditing();
                    showPopout();
                }

                public void onStateChange(StateChangeEvent<State> event) {
                    popoutButton.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                               .contains(event.getState()));
                }
            });
            
            if (popoutUtil != null) {
                popoutUtil.addActionHandler(new ActionHandler<TestContainerPopoutLookup.Action>() {
                    public void onAction(ActionEvent<org.openelis.modules.order.client.TestContainerPopoutLookup.Action> event) {
                        if (event.getAction() == TestContainerPopoutLookup.Action.OK &&
                                        (state == State.ADD || state == State.UPDATE)) {
                            DataChangeEvent.fire(screen);
                        }
                    }
                });
            }
        }
        
        duplicateButton = (AppButton)def.getWidget("duplicateContainerButton");
        addScreenHandler(duplicateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n, r;     
                OrderContainerDO prevData;
                TableDataRow row;
                
                r = table.getSelectedRow();
                if (r == -1)
                    return;
                
                table.finishEditing();
                try {
                    prevData = manager.getContainers().getContainerAt(r);
                   
                    n = r+1;
                    row = new TableDataRow(3);
                    row.cells.get(0).setValue(n);
                    row.cells.get(1).setValue(prevData.getContainerId());
                    row.cells.get(2).setValue(prevData.getTypeOfSampleId());

                    if (n < table.numRows())
                        table.addRow(n, row);
                    else
                        table.addRow(row);
                    
                    resetSequencesFrom(n);
                    
                    table.selectRow(n);
                    table.scrollToSelection();
                    table.startEditing(n, 1);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicateButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    private void initializeDropdowns() {
        Dropdown<Integer> container, sampleTypes;
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        container = (Dropdown) table.getColumns().get(1).getColumnWidget();
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("sample_container");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);   
        }
        container.setModel(model);

        sampleTypes = (Dropdown) table.getColumns().get(2).getColumnWidget();
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("type_of_sample");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row); 
        }
        sampleTypes.setModel(model);
    }
    
    private ArrayList<TableDataRow> getContainerTableModel() {
        OrderContainerDO data;
        ArrayList<TableDataRow> model;
        OrderContainerManager man;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getContainers();
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getContainerAt(i);
                
                row = new TableDataRow(3);
                row.cells.get(0).setValue(data.getItemSequence());
                row.cells.get(1).setValue(data.getContainerId());
                row.cells.get(2).setValue(data.getTypeOfSampleId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private void resetSequencesFrom(int index) throws Exception {
        OrderContainerManager man;
        
        man = manager.getContainers();
        for (int i = index; i < man.count(); i++) {
           man.getContainerAt(i).setItemSequence(i);
           table.setCell(i, 0, i);
        }
    }
    
    private void showPopout() {
        try {
            popoutUtil.showPopout(consts.get("testsAndContainers"), state, manager);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("TestContainerPopoutLookup error: " + e.getMessage());
        }
    }
    
    public void setManager(OrderManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}