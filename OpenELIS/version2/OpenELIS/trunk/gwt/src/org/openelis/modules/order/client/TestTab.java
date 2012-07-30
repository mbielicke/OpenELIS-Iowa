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

import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.PanelManager;
import org.openelis.manager.TestManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestTab extends Screen implements HasActionHandlers<TestTab.Action> {

    private OrderManager              manager;
    private TestTab                   screen; 
    private AppButton                 addTestButton, removeTestButton, popoutButton;
    private TreeWidget                tree;
    private AutoComplete<Integer>     test;
    private boolean                   loaded;
    private static final String       TEST_LEAF = "test", ANALYTE_LEAF = "analyte";
    private ArrayList<String>         names;
    private TestContainerPopoutUtil   popoutUtil;
    
    protected ScreenService           analysisService, panelService, testService;

    public enum Action {
        ADD_AUX, REFRESH_AUX
    };

    public TestTab(ScreenDefInt def, ScreenWindowInt window, TestContainerPopoutUtil popoutUtil) {
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
        analysisService = new ScreenService("controller?service=org.openelis.modules.analysis.server.AnalysisService");
        panelService  = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
        testService  = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        this.popoutUtil = popoutUtil;
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {        
        screen = this;
        
        tree = (TreeWidget)def.getWidget("orderTestTree");
        addScreenHandler(tree, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {       
                tree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                tree.enable(true);
            }
        });
        
        tree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                TreeDataItem row;
                
                if (state == State.ADD || state == State.UPDATE) {
                    row = event.getSelectedItem();
                    removeTestButton.enable(TEST_LEAF.equals(row.leafType));
                }              
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                TreeDataItem row;

                if ( (state == State.ADD || state == State.UPDATE)) {
                    row = tree.getSelection();
                    if (ANALYTE_LEAF.equals(row.leafType) && (event.getCol() > 0))
                        event.cancel();                    
                } else {
                    event.cancel();
                }
            }
        });
        
        tree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                TreeDataItem row;
                
                row = event.getItem();
                if(TEST_LEAF.equals(row.leafType)) {
                    addAnalytes(row.childIndex, row);
                    row.checkForChildren(row.hasChildren());
                }
            }            
        }); 
        
        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                OrderTestViewDO data;
                OrderTestAnalyteViewDO ana;
                TestMethodVO test;
                Object val;
                TreeDataItem row;

                r = event.getRow();
                c = event.getCol();
                val = tree.getObject(r, c);
                row = tree.getSelection();

                switch (c) {
                    case 0:
                        if (TEST_LEAF.equals(row.leafType)) {
                            data = (OrderTestViewDO)row.data;
                            data.setItemSequence((Integer)val);
                        } else if (ANALYTE_LEAF.equals(row.leafType)) {
                            ana = (OrderTestAnalyteViewDO)row.data;
                            ana.setTestAnalyteIsReportable((String)val);
                        }
                        break;
                    case 1:
                        if (TEST_LEAF.equals(row.leafType)) {
                            data = (OrderTestViewDO)row.data;

                            if (val != null) {
                                test = (TestMethodVO) ((TableDataRow)val).data;
                                if (test.getMethodId() == null) {
                                    addTestsFromPanel(test.getTestId(), row);
                                } else {
                                    data.setTestId(test.getTestId());
                                    data.setTestName(test.getTestName());
                                    data.setDescription(test.getTestDescription());
                                    data.setMethodName(test.getMethodName());
                                    data.setIsActive(test.getIsActive());
                                    
                                    reloadAnalytes(row);
                                }
                            } else {
                                data.setTestId(null);
                                data.setTestName(null);
                                data.setMethodName(null);
                                data.setDescription(null);
                                data.setIsActive(null);   
                                
                                reloadAnalytes(row);
                            }
                        }
                        break;
                }
            }            
        });

        tree.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow val;
                TreeDataItem row;
                OrderTestViewDO data;
                TestMethodVO test;                
                OrderTestManager man;
                
                try {
                    man = manager.getTests();
                    row = (TreeDataItem)event.getRow();                    
                    man.addTestAt(row.childIndex);
                    data = man.getTestAt(row.childIndex);    
                    row.data = data;
                    
                    val = (TableDataRow)row.cells.get(1).getValue();

                    if (val != null) {
                        test = (TestMethodVO)val.data;
                        data.setTestId(test.getTestId());
                        data.setTestName(test.getTestName());
                        data.setMethodName(test.getMethodName());
                        data.setDescription(test.getTestDescription());
                        data.setIsActive(test.getIsActive());
                    } else {
                        data.setTestId(null);
                        data.setTestName(null);
                        data.setMethodName(null);
                        data.setDescription(null);
                        data.setIsActive(null);
                    }
                    data.setItemSequence(0);
                    tree.setCell(tree.getRowIndex(row), 0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        tree.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                TreeDataItem row;
                
                row = ((TreeDataItem)event.getRow());
                /*
                 * analytes don't need to be deleted here because they get deleted
                 * in the back-end when the test gets deleted                 
                 */
                if (TEST_LEAF.equals(row.leafType)) {
                    try {
                        /*
                         * childIndex and not event.getIndex() is passed to the
                         * method because the former corresponds to the ordering
                         * of the tests in the manager whereas the latter to the 
                         * index of the deleted item in the tree which differs based
                         * on what items are showing
                         */
                        manager.getTests().removeTestAt(row.childIndex);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }
            }
        });
        
        test = (AutoComplete) tree.getColumns().get(TEST_LEAF).get(1).getColumnWidget();
        test.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                int                     r;
                Integer                 sampleType, testPanelId, sepIndex;
                String                  value, flag;
                ArrayList<TableDataRow> model;
                TableDataRow            row;
                OrderContainerDO        oData;
                PanelDO                 pDO;
                PanelManager            pMan;
                TestManager             tMan;
                TestViewDO              tVDO;
                TestMethodVO            tmData;

                value = event.getMatch();
                if (value.matches("[tp][0-9]*\\-[0-9]*")) {
                    flag = value.substring(0, 1);
                    sepIndex = value.indexOf("-");
                    testPanelId = Integer.valueOf(value.substring(1, sepIndex));
                    sampleType = Integer.valueOf(value.substring(sepIndex + 1));
                    try {
                        row = new TableDataRow(3);
                        tmData = new TestMethodVO();
                        if ("t".equals(flag)) {
                            tMan = testService.call("fetchById", testPanelId);
                            tVDO = tMan.getTest();
                            row.key = tVDO.getId();
                            tmData.setTestId(tVDO.getId());
                            row.cells.get(0).value = tVDO.getName();
                            tmData.setTestName(tVDO.getName());
                            row.cells.get(1).value = tVDO.getMethodName();
                            tmData.setMethodId(tVDO.getMethodId());
                            tmData.setMethodName(tVDO.getMethodName());
                            row.cells.get(2).value = tVDO.getDescription();
                            tmData.setTestDescription(tVDO.getDescription());
                        } else if ("p".equals(flag)) {
                            pMan = panelService.call("fetchById", testPanelId);
                            pDO = pMan.getPanel();
                            row.key = pDO.getId();
                            tmData.setTestId(pDO.getId());
                            row.cells.get(0).value = pDO.getName();
                            tmData.setTestName(pDO.getName());
                            row.cells.get(2).value = pDO.getDescription();
                            tmData.setTestDescription(pDO.getDescription());
                        }
                        row.data = tmData;
                        model = new ArrayList<TableDataRow>();
                        model.add(row);
                        test.setModel(model);
                        test.setSelection(row.key);
                        tree.finishEditing();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    event.cancel();
                }
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<TestMethodVO> autoList;
                TableDataRow            row;
                TestMethodVO            data;

                try {
                    autoList = panelService.callList("fetchByNameWithTests", 
                                                     QueryFieldUtil.parseAutocomplete(event.getMatch())+"%");
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < autoList.size(); i++ ) {
                        data = autoList.get(i);
                        row = new TableDataRow(i, getTestLabel(data.getTestName(), data.getMethodName(), data.getTestDescription()));
                        row.data = data;
                        model.add(row);
                    }
                    test.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }            
        });

        addTestButton = (AppButton)def.getWidget("addTestButton");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int index, numRows;
                TreeDataItem selRow, newRow;                
                
                index = 0;
                numRows = tree.getData().size();
                selRow = tree.getSelection();                
                
                if (selRow == null)  
                    index = numRows;
                else if (TEST_LEAF.equals(selRow.leafType))
                    index = selRow.childIndex + 1;
                else if (ANALYTE_LEAF.equals(selRow.leafType))
                    index = selRow.parent.childIndex + 1;
                
                newRow = addTestRowAt(index, null);     
                tree.scrollToSelection();
                tree.startEditing(newRow, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeTestButton = (AppButton)def.getWidget("removeTestButton");
        addScreenHandler(removeTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = tree.getSelectedRow();
                if (r > -1 && tree.numRows() > 0)
                    tree.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        popoutButton = (AppButton)def.getWidget("testPopoutButton");
        if (popoutButton != null) {
            addScreenHandler(popoutButton, new ScreenEventHandler<Object>() {
                public void onClick(ClickEvent event) {
                    tree.finishEditing();
                    showPopout();
                }

                public void onStateChange(StateChangeEvent<State> event) {
                    popoutButton.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY)
                                               .contains(event.getState()));
                }
            });
            
            if (popoutUtil != null) {
                popoutUtil.addActionHandler(new ActionHandler<TestContainerPopoutLookup.Action>() {
                    public void onAction(ActionEvent<TestContainerPopoutLookup.Action> event) {
                        if (event.getAction() == TestContainerPopoutLookup.Action.OK &&
                                        (state == State.ADD || state == State.UPDATE)) {
                            DataChangeEvent.fire(screen);
                            ActionEvent.fire(screen, Action.REFRESH_AUX, null);
                        }
                    }
                });
            }
        }
    }
    
    protected void addAnalytes(int index, TreeDataItem parent) {
        OrderTestAnalyteManager man;
        OrderTestAnalyteViewDO data;
        TreeDataItem item;
        
        if(parent.getItems().size() > 0)
            return;             
        
        try {
            window.setBusy(consts.get("fetching"));
            
            if (state == State.ADD || state == State.UPDATE)
                man = manager.getTests().getMergedAnalytesAt(index); 
            else
                man = manager.getTests().getAnalytesAt(index); 
                        
            for (int i = 0; i < man.count(); i++) {
                data = man.getAnalyteAt(i);
                item = new TreeDataItem(2);
                item.leafType = ANALYTE_LEAF;
                item.cells.get(0).setValue(data.getTestAnalyteIsReportable());
                item.cells.get(1).setValue(data.getAnalyteName());
                item.data = data;
                parent.addItem(item);
            }            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }   

    private ArrayList<TreeDataItem> getTreeModel() {
        int i;
        String label;
        OrderTestViewDO data;
        ArrayList<TreeDataItem> model;
        OrderTestManager man;
        TableDataRow val;
        TreeDataItem row;
                
        model = new ArrayList<TreeDataItem>();
        if (manager == null)
            return model;
        
        try {
            man = manager.getTests();
            for (i = 0; i < man.count(); i++ ) {
                data = (OrderTestViewDO)man.getTestAt(i);
                row = new TreeDataItem(2);
                row.leafType = TEST_LEAF;
                row.key = data.getId();
                row.data = data;
                row.cells.get(0).setValue(data.getItemSequence());
                label = getTestLabel(data.getTestName(), data.getMethodName(), data.getDescription());
                val = new TableDataRow(data.getTestId(), label);                
                val.data = data;
                row.cells.get(1).setValue(val);
                row.close();
                row.checkForChildren(true);
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private String getTestLabel(String testName, String methodName, String description) {
        if (names == null)
            names = new ArrayList<String>();
        else
            names.clear();
        
        names.add(testName);
        names.add(methodName);
        names.add(description);
                
        return DataBaseUtil.concatWithSeparator(names, ", ");
    }   
    
    private void addTestsFromPanel(Integer panelId, TreeDataItem row) {
        int index;
        ArrayList<TestMethodVO> tests;
        TreeDataItem newRow;

        try {
            tests = testService.callList("fetchByPanelId", panelId);   
            index = row.childIndex;
            if (tests != null && tests.size() > 0) {                
                tree.deleteRow(row);
                for (int i = 0; i < tests.size(); i++ ) {
                    newRow = addTestRowAt(index+i, tests.get(i));
                    tree.toggle(newRow);
                    row.checkForChildren(newRow.hasChildren());
                }
                tree.refresh(true);
            } else {
                tree.setCellException(index, 1, new LocalizedException("noActiveTestFoundForPanelException"));
            }
            
            ActionEvent.fire(this, Action.ADD_AUX, panelId);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }

    private void reloadAnalytes(TreeDataItem row) {
        try {
            /*
             * make sure that the OrderTestAnalyteManager for this order test contains 
             * the analytes for the new test 
             */
            manager.getTests().refreshAnalytesByTestAt(row.childIndex);                                
            /*
             * remove all the children because the test has changed and the analytes
             * belonged to the previous test
             */
            row.getItems().clear();
            
            row.close();
            tree.toggle(row);
            row.checkForChildren(row.hasChildren());
            tree.refresh(true);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
    
    private TreeDataItem addTestRowAt(int index, TestMethodVO data) {
        String label;
        TreeDataItem row;
        TableDataRow val;
        
        row = new TreeDataItem(2);
        if (data != null) {
            label = getTestLabel(data.getTestName(), data.getMethodName(), data.getTestDescription());
            val = new TableDataRow(data.getTestId(), label);
            val.data = data;
            row.cells.get(1).setValue(val);
        }
        
        row.leafType = TEST_LEAF;
        row.checkForChildren(true);
        
        if (index < tree.getData().size())                                    
            tree.addRow(index, row);
        else 
            tree.addRow(row);
        
        return row;
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

    public HandlerRegistration addActionHandler(ActionHandler<TestTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}