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
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.LocalizedException;
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

    protected ScreenService       analysisService, testService;

    public ContainerTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        analysisService = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        testService  = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {        
        orderTestTable = (TableWidget)def.getWidget("orderTestTable");
        addScreenHandler(orderTestTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {       
                if(state != State.QUERY)
                    orderTestTable.load(getTestTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderTestTable.enable(true);
                orderTestTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE)  
                    event.cancel();                
            }            
        });
        
        orderTestTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                OrderTestViewDO data;
                TestMethodVO test;
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
                        if (val != null) {
                            test = (TestMethodVO) ((TableDataRow)val).data;
                            if(test.getMethodId() == null) {
                                addTestsFromPanel(test.getTestId(), r);
                            } else {
                                data.setTestId(test.getTestId());
                                data.setTestName(test.getTestName());
                                data.setDescription(test.getTestDescription());
                                data.setMethodName(test.getMethodName());
                                
                                orderTestTable.setCell(r, 1, null);
                                orderTestTable.setCell(r, 2, null);
                            }
                        } else {
                            data.setTestId(null);
                            data.setTestName(null);
                            data.setMethodName(null);
                            data.setDescription(null);
                            
                            orderTestTable.setCell(r, 1, null);
                            orderTestTable.setCell(r, 2, null);
                        }
                }
            }
        });

        orderTestTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                TableDataRow val;
                OrderTestViewDO data;
                TestMethodVO test;                
                OrderTestManager man;
                
                r = event.getIndex();
                
                try {
                    man = manager.getTests();
                    man.addTestAt(r);
                    data = man.getTestAt(r);                    
                    
                    val = (TableDataRow)orderTestTable.getObject(r, 0);
                    
                    if(val != null) {
                        test = (TestMethodVO)val.data;
                        data.setTestId(test.getTestId());
                        data.setTestName(test.getTestName());
                        data.setMethodName(test.getMethodName());
                        data.setDescription(test.getTestDescription());
                    } else {
                        data.setTestId(null);
                        data.setTestName(null);
                        data.setMethodName(null);
                        data.setDescription(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        
        test = (AutoComplete) orderTestTable.getColumnWidget(OrderMeta.getTestName());
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

                //
                // since rows can be added to this table in two ways i.e. by 
                // clicking addTestButton or selecting a panel and adding all
                // the tests belonging to that panel we have to make sure that 
                // in both cases the id of the test is set for each
                // 
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
        
        orderContainerTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();                
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
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        container = (Dropdown) orderContainerTable.getColumns().get(0).getColumnWidget();
        sampleTypes = (Dropdown) orderContainerTable.getColumns().get(2).getColumnWidget();        

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("sample_container");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);   
        }
        container.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("type_of_sample");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row); 
        }
        sampleTypes.setModel(model);
    }   
    
    private ArrayList<TableDataRow> getTestTableModel() {
        int i;
        OrderTestViewDO data;
        ArrayList<TableDataRow> model;
        OrderTestManager man;
        TableDataRow row;
        
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getTests();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderTestViewDO)man.getTestAt(i);
                row = new TableDataRow(data.getTestId(), data.getTestName());
                row.data = data;
                model.add(new TableDataRow(null, row, data.getMethodName(), data.getDescription()));
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
    
    private void addTestsFromPanel(Integer panelId, int index) {
        ArrayList<TestMethodVO> tests;
        TableDataRow row, val;  
        TestMethodVO data;

        try {
            tests = testService.callList("fetchByPanelId", panelId);            
            if (tests != null && tests.size() > 0) {
                orderTestTable.deleteRow(index);
                for (int i = 0; i < tests.size(); i++ ) {
                    data = tests.get(i);

                    row = new TableDataRow(3);

                    val = new TableDataRow(data.getTestId(), data.getTestName());
                    val.data = data;

                    row.cells.get(0).setValue(val);
                    row.cells.get(1).setValue(data.getMethodName());
                    row.cells.get(2).setValue(data.getTestDescription());

                    orderTestTable.addRow(index + i, row);
                }
            } else {
                orderTestTable.setCellException(index, 0, new LocalizedException("noActiveTestFoundForPanelException"));
                orderTestTable.setCell(index, 0, null);
                orderTestTable.setCell(index, 1, null);
                orderTestTable.setCell(index, 2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    } 
}
