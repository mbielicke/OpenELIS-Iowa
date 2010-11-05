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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.NodeAddedEvent;
import org.openelis.gwt.widget.tree.event.NodeAddedHandler;
import org.openelis.gwt.widget.tree.event.NodeDeletedEvent;
import org.openelis.gwt.widget.tree.event.NodeDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class SampleItemAnalysisTreeTab extends Screen
                                                     implements
                                                     HasActionHandlers<SampleItemAnalysisTreeTab.Action> {
    public enum Action {
        REFRESH_TABS
    };

    private Integer                       analysisCancelledId, analysisReleasedId;
    protected Tree                        itemsTree;
    protected Button                      removeRow, addItem, addAnalysis, popoutTree;
    private HasActionHandlers             parentScreen;
    private SampleManager                 manager;
    protected SampleTreeUtility           treeUtil;
    protected SampleItemAnalysisTreeTab   treeTab;
    protected SampleItemsPopoutTreeLookup treePopoutScreen;
    protected boolean                     loaded = false;

    public SampleItemAnalysisTreeTab(ScreenDefInt def, Window window,
                                     HasActionHandlers parentScreen) {
        setDefinition(def);
        setWindow(window);

        this.parentScreen = parentScreen;

        initialize();
        initializeDropdowns();
    }

    @SuppressWarnings("unchecked")
	private void initialize() {
        treeTab = this;

        itemsTree = (Tree)def.getWidget("itemsTestsTree");
        treeUtil = new SampleTreeUtility(window, itemsTree, (Screen)parentScreen) {
            public Node addNewTreeRowFromBundle(Node parentRow,
                                                        SampleDataBundle bundle) {
                Node row;

                row = new Node(2);
                row.setType("analysis");
                row.setData(bundle);

                itemsTree.addNodeAt(parentRow, row);

                return row;
            }
            
            public void selectNewRowFromBundle(Node row) {
                itemsTree.selectNodeAt(row);
                itemsTree.scrollToVisible(itemsTree.getSelectedNode());
            }
        };

        treeUtil.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
            public void onAction(ActionEvent<TestPrepUtility.Action> event) {
                
                ActionEvent.fire(treeTab, Action.REFRESH_TABS, event.getData());
            }
        });

        addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemsTree.setRoot(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTree.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                itemsTree.setQueryMode(event.getState() == State.QUERY);
            }
        });

        itemsTree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                // do nothing

            }
        });

        itemsTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                SampleDataBundle data;
                AnalysisViewDO anDO;
                boolean enable;
                Node selection = itemsTree.getNodeAt(itemsTree.getSelectedNode());

                if (selection != null)
                    data = (SampleDataBundle)selection.getData();
                else
                    data = null;

                enable = false;
                if (state == State.ADD || state == State.UPDATE) {
                    if ("sampleItem".equals(selection.getType())) {
                        enable = !selection.hasChildren();

                    } else {
                        if (data != null) {
                            try {
                                anDO = manager.getSampleItems()
                                              .getAnalysisAt(data.getSampleItemIndex())
                                              .getAnalysisAt(data.getAnalysisIndex());

                                if (analysisCancelledId.equals(anDO.getStatusId()) ||
                                    analysisReleasedId.equals(anDO.getStatusId()))
                                    enable = false;
                                else
                                    enable = true;

                            } catch (Exception e) {
                                com.google.gwt.user.client.Window.alert("selectionHandler: " + e.getMessage());
                            }
                        }
                    }
                }

                removeRow.setEnabled(enable);
                window.clearStatus();
                ActionEvent.fire(treeTab, Action.REFRESH_TABS, data);
            }
        });

        itemsTree.addUnselectionHandler(new UnselectionHandler<Integer>() {
        	public void onUnselection(UnselectionEvent<Integer> event) {
                ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
            }
        });

        itemsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // never allowed to edit tree..use tabs
                event.cancel();
            }
        });

        itemsTree.addNodeAddedHandler(new NodeAddedHandler() {			
            public void onNodeAdded(NodeAddedEvent event) {
                Node addedRow, parentRow;
                SampleDataBundle parentData;
                int addedIndex;
                SampleItemManager itemMan;
                AnalysisManager anMan;

                try {
                    addedRow = (Node)event.getNode();

                    if ("sampleItem".equals(addedRow.getType())) {
                        itemMan = manager.getSampleItems();
                        addedIndex = itemMan.addSampleItem();
                        addedRow.setData(itemMan.getBundleAt(addedIndex));

                        treeUtil.updateSampleItemRow(addedRow);
                        //itemsTree.refreshRow(addedRow);

                    } else if ("analysis".equals(addedRow.getType())) {
                        parentRow = addedRow.getParent();
                        parentData = (SampleDataBundle)parentRow.getData();
                        itemMan = manager.getSampleItems();
                        anMan = itemMan.getAnalysisAt(parentData.getSampleItemIndex());

                        addedIndex = anMan.addAnalysis();
                        addedRow.setData(anMan.getBundleAt(addedIndex));

                        treeUtil.updateAnalysisRow(addedRow);
                        //itemsTree.refreshRow(addedRow);

                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        itemsTree.addNodeDeletedHandler(new NodeDeletedHandler() {			
            public void onNodeDeleted(NodeDeletedEvent event) {
                Node row;
                SampleDataBundle bundle;

                row = (Node)event.getNode();
                bundle = (SampleDataBundle)row.getData();

                try {
                    if ("sampleItem".equals(row.getType()))
                        manager.getSampleItems().removeSampleItemAt(bundle.getSampleItemIndex());
                    else if ("analysis".equals(row.getType()))
                        manager.getSampleItems()
                               .getAnalysisAt(bundle.getSampleItemIndex())
                               .removeAnalysisAt(bundle.getAnalysisIndex());

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert("rowDeletedHandler: " + e.getMessage());
                }
            }
        });

        addItem = (Button)def.getWidget("addItemButton");
        addScreenHandler(addItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.onAddItemButtonClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItem.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addAnalysis = (Button)def.getWidget("addAnalysisButton");
        addScreenHandler(addAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.onAddAnalysisButtonClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAnalysis.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeRow = (Button)def.getWidget("removeRowButton");
        addScreenHandler(removeRow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                treeUtil.onRemoveRowButtonClick();
                
                if(itemsTree.getSelectedNode() == -1)
                    removeRow.setEnabled(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRow.setEnabled(false);
            }
        });

        popoutTree = (Button)def.getWidget("popoutTree");
        addScreenHandler(popoutTree, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onTreePopoutClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                popoutTree.setEnabled(EnumSet.of(State.DISPLAY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        parentScreen.addActionHandler(new ActionHandler() {
            public void onAction(ActionEvent event) {
                Node selected;
                SampleDataBundle data;
                if (event.getAction() == SampleItemTab.Action.CHANGED) {
                    selected = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                    data = (SampleDataBundle)selected.getData();
                    
                    // make sure it is a sample item row
                    if ("analysis".equals(selected.getType()))
                        selected = selected.getParent();

                    treeUtil.updateSampleItemRow(selected);
                    //itemsTree.refreshRow(selected);
                    
                    //if the user changes the sample type then the analysis
                    //tab needs to be refreshed to load the new units dropdown model.
                    //This only needs to happen if they have the analysis row selected
                    if ("analysis".equals(itemsTree.getNodeAt(itemsTree.getSelectedNode()).getType()))
                        ActionEvent.fire(treeTab, Action.REFRESH_TABS, data);
                    
                } else if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), false);
                } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                    treeUtil.analysisTestChanged((Integer)event.getData(), true);
                } else if(event.getAction() == AnalysisTab.Action.ORDER_LIST_ADDED) {
                    itemsTree.selectNodeAt(0);
                    treeUtil.onAddAnalysisButtonClick();
                    treeUtil.importOrderTestList((ArrayList<OrderTestViewDO>)event.getData());
                } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                    selected = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                    treeUtil.updateAnalysisRow(selected);
                    //itemsTree.refreshRow(selected);
                } else if (event.getAction() == ResultTab.Action.RESULT_HISTORY) {
                    historyCurrentResult();
                } else if(event.getAction() == ResultTab.Action.REFLEX_ADDED) {
                    //we need to create a new analysis row so the utility can work from that
                    //itemsTree.fireEvents(false);
                    treeUtil.onAddAnalysisButtonClick();
                    //itemsTree.fireEvents(true);
                    
                    treeUtil.importReflexTestList((ArrayList<SampleDataBundle>)event.getData());
                }
            }
        });
    }

    private void onTreePopoutClick() {
        try {
            if (treePopoutScreen == null)
                treePopoutScreen = new SampleItemsPopoutTreeLookup();

            ModalWindow modal = new ModalWindow();
            modal.setName(consts.get("itemsAndAnalyses"));

            modal.setContent(treePopoutScreen);
            treePopoutScreen.setData(manager);
            treePopoutScreen.setScreenState(state);
            
            modal.addBeforeClosedHandler(new BeforeCloseHandler<Window>(){
               public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                   DataChangeEvent.fire(treeTab, itemsTree);
                   ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
                } 
            });

        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert("onTreePopoutClick: " + e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)itemsTree.getNodeDefinitionAt("analysis",1).getCellEditor().getWidget()).setModel(model);
    }

    private Node getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        Node tmp;
        Node treeModelItem, row;
        Node model = new Node();
        ArrayList<Item<Integer>> prepRows;

        try {
            HashMap<Integer, Node> keyTable = new HashMap<Integer, Node>();

            if (manager == null)
                return model;

            for (i = 0; i < manager.getSampleItems().count(); i++ ) {
                SampleItemManager sim = manager.getSampleItems();
                itemDO = sim.getSampleItemAt(i);

                row = new Node(2);
                row.setType("sampleItem");
                //row.toggle();
                row.setKey(itemDO.getId());
                row.setData(sim.getBundleAt(i));
                treeUtil.updateSampleItemRow(row);

                tmp = keyTable.get(itemDO.getId());
                if (itemDO.getId() != null && tmp != null) {
                    tmp.add(row);
                } else {
                    keyTable.put(itemDO.getId(), row);
                    model.add(row);
                }

                am = manager.getSampleItems().getAnalysisAt(i);
                prepRows = new ArrayList<Item<Integer>>();
                prepRows.add(new Item<Integer>(null, ""));

                for (j = 0; j < am.count(); j++ ) {
                    AnalysisViewDO aDO = (AnalysisViewDO)am.getAnalysisAt(j);

                    treeModelItem = new Node(2);
                    treeModelItem.setType("analysis");

                    treeModelItem.setKey(aDO.getId());
                    treeModelItem.setData(am.getBundleAt(j));
                    treeUtil.updateAnalysisRow(treeModelItem);

                    row.add(treeModelItem);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    protected void historyCurrentResult() {
        Node item;
        SampleDataBundle bundle;
        AnalysisResultManager man;
        int i, j, rowCount, count;
        ArrayList<ResultViewDO> row;
        ResultViewDO data;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;

        item = itemsTree.getNodeAt(itemsTree.getSelectedNode());

        if (item == null || !"analysis".equals(item.getType())) {
            window.setError(consts.get("resultHistoryException"));
            return;
        }

        try {
            bundle = (SampleDataBundle)item.getData();
            man = manager.getSampleItems()
                         .getAnalysisAt(bundle.getSampleItemIndex())
                         .getAnalysisResultAt(bundle.getAnalysisIndex());
            rowCount = man.rowCount();
            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < rowCount; i++ ) {
                row = man.getRowAt(i);
                count = row.size();

                for (j = 0; j < count; j++ ) {
                    data = row.get(j);
                    refVoArrayList.add(new IdNameVO(data.getId(), data.getAnalyte()));
                }
            }

            refVoList = new IdNameVO[refVoArrayList.size()];
            for (i = 0; i < refVoArrayList.size(); i++ )
                refVoList[i] = refVoArrayList.get(i);

            HistoryScreen.showHistory(OpenELIS.consts.get("historyCurrentResult"),
                                      ReferenceTable.RESULT, refVoList);
            window.clearStatus();

        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historyCurrentResult: " + e.getMessage());
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        treeUtil.setManager(manager);
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
