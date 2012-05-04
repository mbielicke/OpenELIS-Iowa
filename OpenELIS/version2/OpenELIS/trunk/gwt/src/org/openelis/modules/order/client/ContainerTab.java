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
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindowInt;
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
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ContainerTab extends Screen {

    private OrderManager          manager;
    private AppButton             addContainerButton,
                                  removeContainerButton;
    private TableWidget           orderContainerTable;
    private boolean               loaded;

    protected ScreenService       analysisService, panelService, testService;

    public enum Action {
        ADD_AUX
    };

    public ContainerTab(ScreenDefInt def, ScreenWindowInt window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        analysisService = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        panelService  = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
        testService  = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {                
        
        orderContainerTable = (TableWidget)def.getWidget("orderContainerTable");
        addScreenHandler(orderContainerTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    orderContainerTable.load(getContainerTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderContainerTable.enable(true);
                orderContainerTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        orderContainerTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();                
            }            
        });

        orderContainerTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderContainerDO data;

                r = event.getRow();
                c = event.getCol();
                val = orderContainerTable.getObject(r,c);
                
                try {
                    data = manager.getContainers().getContainerAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                /*switch(c) {
                    case 0:
                        data.setContainerId((Integer)val);
                        break;
                    case 1:
                        data.setNumberOfContainers((Integer)val);
                        break;
                    case 2:
                        data.setTypeOfSampleId((Integer)val);
                        break;
                }*/
            }
        });

        orderContainerTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int index;
                Integer sampleTypeId;
                OrderContainerDO data, prevData;
                OrderContainerManager man;
                try {
                    man = manager.getContainers();
                    index = man.addContainer();
                    if (index > 0) {
                        prevData = man.getContainerAt(index-1);
                        sampleTypeId = prevData.getTypeOfSampleId();                        
                        data = man.getContainerAt(index);
                        data.setTypeOfSampleId(sampleTypeId);
                        orderContainerTable.setCell(index, 2, sampleTypeId);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        orderContainerTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getContainers().removeContainerAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addContainerButton = (AppButton)def.getWidget("addContainerButton");
        addScreenHandler(addContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                orderContainerTable.addRow();
                n = orderContainerTable.numRows() - 1;
                orderContainerTable.selectRow(n);
                orderContainerTable.scrollToSelection();
                orderContainerTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addContainerButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeContainerButton = (AppButton)def.getWidget("removeContainerButton");
        addScreenHandler(removeContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderContainerTable.getSelectedRow();
                if (r > -1 && orderContainerTable.numRows() > 0)
                    orderContainerTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeContainerButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    private void initializeDropdowns() {
        Dropdown<Integer> container, sampleTypes;
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        container = (Dropdown) orderContainerTable.getColumns().get(1).getColumnWidget();
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("sample_container");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);   
        }
        container.setModel(model);

        sampleTypes = (Dropdown) orderContainerTable.getColumns().get(2).getColumnWidget();
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
                row.cells.get(0).setValue(null);
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