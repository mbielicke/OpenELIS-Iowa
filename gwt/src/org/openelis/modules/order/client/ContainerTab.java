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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestMethodVO;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.meta.OrderMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class ContainerTab extends Screen {

    private OrderManager          manager;
    private AutoComplete<Integer> test;
    private AppButton             addTestButton, removeTestButton, addContainerButton,
                                  removeContainerButton;
    private TableWidget           orderTestTable, orderContainerTable;
    private boolean               loaded;

    protected ScreenService       analysisService;

    public ContainerTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        analysisService = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");

        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {        
        orderTestTable = (TableWidget)def.getWidget("orderTestTable");
        addScreenHandler(orderTestTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {                
                orderTestTable.load(getTestTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderTestTable.enable(true);
                orderTestTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderTestTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                OrderTestViewDO data;
                TestMethodVO row;
                Object val;

                r = event.getRow();
                c = event.getCol();
                val = orderTestTable.getObject(r,c);
                
                try {
                    data = manager.getTests().getTestAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        row = (TestMethodVO)((TableDataRow)val).data;                        
                        data.setReferenceId(row.getTestId());
                        data.setReferenceName(row.getTestName());
                        data.setDescription(row.getTestDescription());
                        if(row.getMethodId() == null) {
                            data.setReferenceTableId(ReferenceTable.PANEL);
                        } else { 
                            data.setReferenceTableId(ReferenceTable.TEST);
                            data.setMethodName(row.getMethodName());
                        }
                }
            }
        });

        orderTestTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                
                r = event.getIndex();
                try {
                    manager.getTests().addTestAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        orderTestTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTests().removeTestAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        test = (AutoComplete) orderTestTable.getColumnWidget(OrderMeta.getTestId());
        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {               
                Query query;
                QueryData field;
                QueryFieldUtil parser;
                ArrayList<QueryData> fields;
                ArrayList<TestMethodVO> autoList;
                TestMethodVO data;
                ArrayList<TableDataRow> model;
                TableDataRow row;

                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                field = new QueryData();
                field.query = parser.getParameter().get(0);
                fields.add(field);
                
                query.setFields(fields);

                try {
                    autoList = analysisService.callList("getTestMethodMatches", query);
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < autoList.size(); i++ ) {
                        data = autoList.get(i);

                        row = new TableDataRow(data.getTestId(),
                                                            data.getTestName(),
                                                            data.getMethodName(),
                                                            data.getTestDescription());
                        row.data = data;

                        model.add(row);
                    }

                    test.showAutoMatches(model);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            
            }            
        });
        
        test.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableDataRow selectedRow;
                TestMethodVO data;
                int r;

                selectedRow = event.getSelectedItem().row;                
                r = orderTestTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.key != null) {
                    data = (TestMethodVO)selectedRow.data;
                    orderTestTable.setCell(r, 1, data.getMethodName());
                    orderTestTable.setCell(r, 2, data.getTestDescription());
                } else {
                    orderTestTable.setCell(r, 1, null);
                    orderTestTable.setCell(r, 1, null);
                }
            }

        });

        addTestButton = (AppButton)def.getWidget("addTestButton");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderTestTable.getSelectedRow() + 1;
                if (r == 0) 
                    r = orderTestTable.numRows();                
                orderTestTable.addRow(r);
                orderTestTable.selectRow(r);
                orderTestTable.scrollToSelection();
                orderTestTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeTestButton = (AppButton)def.getWidget("removeTestButton");
        addScreenHandler(removeTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderTestTable.getSelectedRow();
                if (r > -1 && orderTestTable.numRows() > 0)
                    orderTestTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

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

        orderContainerTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer val;
                OrderContainerDO data;

                r = event.getRow();
                c = event.getCol();
                val = (Integer)orderContainerTable.getObject(r,c);
                
                try {
                    data = manager.getContainers().getContainerAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        data.setContainerId(val);
                        break;
                    case 1:
                        data.setNumberOfContainers(val);
                        break;
                    case 2:
                        data.setTypeOfSampleId(val);
                        break;
                }
            }
        });

        orderContainerTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getContainers().addContainer();
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

        container = (Dropdown) orderContainerTable.getColumns().get(0).getColumnWidget();
        sampleTypes = (Dropdown) orderContainerTable.getColumns().get(2).getColumnWidget();        

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_container"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        container.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("type_of_sample"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        sampleTypes.setModel(model);
    }   
    
    private ArrayList<TableDataRow> getTestTableModel() {
        int i;
        OrderTestViewDO data;
        ArrayList<TableDataRow> model;
        OrderTestManager man;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getTests();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderTestViewDO)man.getTestAt(i);
                model.add(new TableDataRow(null, new TableDataRow(data.getReferenceId(), data.getReferenceName()), 
                                           data.getMethodName(), data.getDescription()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private ArrayList<TableDataRow> getContainerTableModel() {
        int i;
        OrderContainerDO data;
        ArrayList<TableDataRow> model;
        OrderContainerManager man;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getContainers();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderContainerDO)man.getContainerAt(i);
                model.add(new TableDataRow(null, data.getContainerId(), 
                                           data.getNumberOfContainers(),                                                            
                                           data.getTypeOfSampleId()));
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
