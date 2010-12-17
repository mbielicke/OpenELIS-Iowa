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
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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

public class ContainerTab extends Screen {

    private OrderManager          manager;
    private AutoComplete          test;
    private Button                addTestButton, removeTestButton, addContainerButton,
                                  removeContainerButton;
    private Table                 orderTestTable, orderContainerTable;
    private boolean               loaded;

    protected ScreenService       analysisService, testService;

    public ContainerTab(ScreenDefInt def, Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        analysisService = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        testService  = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {        
        orderTestTable = (Table)def.getWidget("orderTestTable");
        addScreenHandler(orderTestTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {       
                if(state != State.QUERY)
                    orderTestTable.setModel(getTestTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderTestTable.setEnabled(true);
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
                val = orderTestTable.getValueAt(r,c);
                
                try {
                    data = manager.getTests().getTestAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        if (val != null) {
                            test = (TestMethodVO) ((AutoCompleteValue)val).getData();
                            if(test.getMethodId() == null) {
                                addTestsFromPanel(test.getTestId(), r);
                            } else {
                                data.setTestId(test.getTestId());
                                data.setTestName(test.getTestName());
                                data.setDescription(test.getTestDescription());
                                data.setMethodName(test.getMethodName());
                                
                                orderTestTable.setValueAt(r, 1, test.getMethodName());
                                orderTestTable.setValueAt(r, 2, test.getTestDescription());
                            }
                        } else {
                            data.setTestId(null);
                            data.setTestName(null);
                            data.setMethodName(null);
                            data.setDescription(null);
                            
                            orderTestTable.setValueAt(r, 1, null);
                            orderTestTable.setValueAt(r, 2, null);
                        }
                }
            }
        });

        orderTestTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                AutoCompleteValue val;
                OrderTestViewDO data;
                TestMethodVO test;                
                OrderTestManager man;
                
                r = event.getIndex();
                
                try {
                    man = manager.getTests();
                    man.addTestAt(r);
                    data = man.getTestAt(r);                    
                    
                    val = (AutoCompleteValue)orderTestTable.getValueAt(r, 0);
                    
                    if(val != null) {
                        test = (TestMethodVO)val.getData();
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        orderTestTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTests().removeTestAt(event.getIndex());
                } catch (Exception e) {                    
                    com.google.gwt.user.client.Window.alert(e.getMessage());
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
                ArrayList<Item<Integer>> model;
                Item<Integer> row;
                String param = "";

                fields = new ArrayList<QueryData>();
                query = new Query();
                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e){
                	
                }

                field = new QueryData();
                if(!event.getMatch().equals(""))
                	field.setQuery(parser.getParameter().get(0));
                else
                	field.setQuery("=");
                fields.add(field);
                
                query.setFields(fields);

                try {
                	
                    autoList = analysisService.callList("getTestMethodMatches", query);
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < autoList.size(); i++ ) {
                        data = autoList.get(i);

                        row = new Item<Integer>(data.getTestId(),
                                                            data.getTestName(),
                                                            data.getMethodName(),
                                                            data.getTestDescription());
                        row.setData(data);

                        model.add(row);
                    }

                    test.showAutoMatches(model);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            
            }            
        });
        
        test.getPopupContext().addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Item<Integer> selectedRow;
                TestMethodVO data;
                int r;

                selectedRow = test.getSelectedItem();
                r = orderTestTable.getSelectedRow();

                //
                // since rows can be added to this table in two ways i.e. by 
                // clicking addTestButton or selecting a panel and adding all
                // the tests belonging to that panel we have to make sure that 
                // in both cases the id of the test is set for each
                // 
                if (selectedRow != null && selectedRow.getKey() != null) {
                    data = (TestMethodVO)selectedRow.getData();
                    orderTestTable.setValueAt(r, 1, data.getMethodName());
                    orderTestTable.setValueAt(r, 2, data.getTestDescription());
                } else {
                    orderTestTable.setValueAt(r, 1, null);
                    orderTestTable.setValueAt(r, 1, null);
                }
            }

        });

        addTestButton = (Button)def.getWidget("addTestButton");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderTestTable.getSelectedRow() + 1;
                if (r == 0) 
                    r = orderTestTable.getRowCount();                
                orderTestTable.addRowAt(r);
                orderTestTable.selectRowAt(r);
                orderTestTable.scrollToVisible(r);
                orderTestTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeTestButton = (Button)def.getWidget("removeTestButton");
        addScreenHandler(removeTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderTestTable.getSelectedRow();
                if (r > -1 && orderTestTable.getRowCount() > 0)
                    orderTestTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        orderContainerTable = (Table)def.getWidget("orderContainerTable");
        addScreenHandler(orderContainerTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    orderContainerTable.setModel(getContainerTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderContainerTable.setEnabled(true);
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
                val = orderContainerTable.getValueAt(r,c);
                
                try {
                    data = manager.getContainers().getContainerAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        data.setContainerId((Integer)val);
                        break;
                    case 1:
                        data.setNumberOfContainers((Integer)val);
                        break;
                    case 2:
                        data.setTypeOfSampleId((Integer)val);
                        break;
                }
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
                        orderContainerTable.setValueAt(index, 2, sampleTypeId);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        orderContainerTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getContainers().removeContainerAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addContainerButton = (Button)def.getWidget("addContainerButton");
        addScreenHandler(addContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                orderContainerTable.addRow();
                n = orderContainerTable.getRowCount() - 1;
                orderContainerTable.selectRowAt(n);
                orderContainerTable.scrollToVisible(n);
                orderContainerTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addContainerButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeContainerButton = (Button)def.getWidget("removeContainerButton");
        addScreenHandler(removeContainerButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = orderContainerTable.getSelectedRow();
                if (r > -1 && orderContainerTable.getRowCount() > 0)
                    orderContainerTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeContainerButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    private void initializeDropdowns() {
        Dropdown<Integer> container, sampleTypes;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        container = (Dropdown) orderContainerTable.getColumnWidget(0);
        sampleTypes = (Dropdown) orderContainerTable.getColumnWidget(2);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("sample_container");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);   
        }
        container.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("type_of_sample");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row); 
        }
        sampleTypes.setModel(model);
    }   
    
    private ArrayList<Row> getTestTableModel() {
        int i;
        OrderTestViewDO data;
        ArrayList<Row> model;
        OrderTestManager man;
        AutoCompleteValue av;
        
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getTests();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderTestViewDO)man.getTestAt(i);
                av = new AutoCompleteValue(data.getTestId(), data.getTestName());
                av.setData(data);
                model.add(new Row(av, data.getMethodName(), data.getDescription()));
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private ArrayList<Row> getContainerTableModel() {
        int i;
        OrderContainerDO data;
        ArrayList<Row> model;
        OrderContainerManager man;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getContainers();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderContainerDO)man.getContainerAt(i);
                model.add(new Row(data.getContainerId(), 
                                  data.getNumberOfContainers(),                                                            
                                  data.getTypeOfSampleId()));
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
        Row row;
        AutoCompleteValue val;  
        TestMethodVO data;

        try {
            tests = testService.callList("fetchByPanelId", panelId);            
            if (tests != null && tests.size() > 0) {
                orderTestTable.removeRowAt(index);
                for (int i = 0; i < tests.size(); i++ ) {
                    data = tests.get(i);

                    row = new Row(3);

                    val = new AutoCompleteValue(data.getTestId(), data.getTestName());
                    val.setData(data);

                    row.setCell(0,val);
                    row.setCell(1,data.getMethodName());
                    row.setCell(2,data.getTestDescription());

                    orderTestTable.addRowAt(index + i, row);
                }
            } else {
                orderTestTable.addException(index, 0, new LocalizedException("noActiveTestFoundForPanelException"));
                orderTestTable.setValueAt(index, 0, null);
                orderTestTable.setValueAt(index, 1, null);
                orderTestTable.setValueAt(index, 2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    } 
}
