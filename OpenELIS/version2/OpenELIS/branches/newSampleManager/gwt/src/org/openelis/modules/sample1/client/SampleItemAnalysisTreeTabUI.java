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
package org.openelis.modules.sample1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.NodeAddedEvent;
import org.openelis.ui.widget.tree.event.NodeAddedHandler;
import org.openelis.ui.widget.tree.event.NodeDeletedEvent;
import org.openelis.ui.widget.tree.event.NodeDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SampleItemAnalysisTreeTabUI extends Screen { //implements HasActionHandlers<SampleItemAnalysisTreeTab1.Action>
    
    @UiTemplate("SampleItemAnalysisTreeTab.ui.xml")
    interface SampleItemAnalysisTreeTabUIBinder extends UiBinder<Widget, SampleItemAnalysisTreeTabUI> {        
    };
    
    private static SampleItemAnalysisTreeTabUIBinder uiBinder = GWT.create(SampleItemAnalysisTreeTabUIBinder.class);
    
    public enum Action {
        REFRESH_TABS
    };
    
    @UiField
    protected Tree                     itemsTestsTree;
    
    @UiField
    protected Button                   removeRow, addItem, addAnalysis, popoutTree;
    
    @UiField
    protected Dropdown<Integer>        analysisStatus;
    
    protected Screen                      parentScreen;
    //protected HasActionHandlers          parentScreen;
    protected SampleManager1              manager;
    protected SampleItemAnalysisTreeTabUI screen;
    protected SampleTreeUtility1          treeUtility;
    /*protected SampleHistoryUtility1        historyUtility;
    protected SampleItemsPopoutTreeLookup1 treePopout;
    protected boolean                     loaded = false;
    protected DictionaryDO                dictDrinkingWater;*/
    
    protected boolean                    canEdit, canPopout;

    public SampleItemAnalysisTreeTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        //this.historyUtility = historyUtility;
        
        manager = null;
    }

     private void initialize() {
         Item<Integer> row;
         ArrayList<Item<Integer>> model;
         ArrayList<DictionaryDO> list;
         
         screen = this;
         
         /*treeUtil = new SampleTreeUtility1(window, itemsTree, (Screen)parentScreen) {
             public TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow,
                                                         SampleDataBundle bundle) {
                 TreeDataItem row;

                 row = new TreeDataItem(2);
                 row.leafType = "analysis";
                 row.data = bundle;

                 itemsTree.addChildItem(parentRow, row);

                 return row;
             }
             
             public void selectNewRowFromBundle(TreeDataItem row) {
                 itemsTree.select(row);
                 itemsTree.scrollToVisible();
             }
         };              
  
         treeUtil.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
             public void onAction(ActionEvent<TestPrepUtility.Action> event) {                
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, event.getData());
             }
         });*/

         addScreenHandler(itemsTestsTree, "itemsTestsTree", new ScreenHandler<String>() {
             public void onDataChange(DataChangeEvent event) {
                 itemsTestsTree.setRoot(getRoot());
             }

             public void onStateChange(StateChangeEvent event) {
                 itemsTestsTree.setEnabled(isState(DISPLAY, ADD, UPDATE));
             }
         });        

        itemsTestsTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                boolean enable;
                String uid;
                AnalysisViewDO a;
                SelectionEvent selEvent;
                Node selection;

                selection = itemsTestsTree.getNodeAt(event.getSelectedItem());
                uid = selection.getData();
                enable = false;
                selEvent = null;      
                
                if ("sampleItem".equals(selection.getType())) {
                    if (canEdit)
                        enable = !selection.hasChildren();
                    if (uid != null) 
                        selEvent = new SelectionEvent(SelectedType.SAMPLE_ITEM, uid);                        
                } else if (uid != null) {
                    a = (AnalysisViewDO)manager.getObject(uid);

                    if (canEdit) 
                        enable = (!Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId()) && 
                                  !Constants.dictionary().ANALYSIS_RELEASED.equals(a.getStatusId()));
                    
                    selEvent = new SelectionEvent(SelectedType.ANALYSIS, uid);
                }
                
                removeRow.setEnabled(enable);
                                  
                if (selEvent != null)
                    bus.fireEvent(selEvent);       
            }
        });

         /*itemsTree.addUnselectionHandler(new UnselectionHandler<TreeDataItem>() {
             public void onUnselection(UnselectionEvent<TreeDataItem> event) {
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
             }
         });*/

        itemsTestsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
             public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                 // never allowed to edit tree..use tabs
                 event.cancel();
             }
         });

        itemsTestsTree.addNodeAddedHandler(new NodeAddedHandler() {
            public void onNodeAdded(NodeAddedEvent event) {
                String uid;
                Node node, parent;
                DictionaryDO dict;
                SampleItemViewDO item;

                node = event.getNode();

                if ("sampleItem".equals(node.getType())) {
                    item = manager.item.add();
                    /*
                     * if the domain of the sample is sdwis then we set the
                     * sample type for the newly added item to "Drinking Water"
                     */
                    if (Constants.domain().SDWIS.equals(manager.getSample().getDomain())) {
                        try {
                            dict = DictionaryCache.getBySystemName("drinking_water");
                            item.setTypeOfSampleId(dict.getId());
                            item.setTypeOfSample(dict.getEntry());
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                        }
                    }
                    node.setData(manager.getUid(item));

                    // treeUtility.updateSampleItemRow(addedRow);
                    // itemsTestsTree.refreshNode(addedRow);
                } else if ("analysis".equals(node.getType())) {
                    parent = node.getParent();
                    uid = (String)parent.getData();
                    item = (SampleItemViewDO)manager.getObject(uid);
                    //ana = manager.analysis.add(item);
                    // addedIndex = anMan.addAnalysis();
                    //node.setData(manager.getUid(ana));

                    // treeUtility.updateAnalysisRow(addedRow);
                    // itemsTestsTree.refreshRow(addedRow);
                }
            }
        });

        itemsTestsTree.addNodeDeletedHandler(new NodeDeletedHandler() {
            public void onNodeDeleted(NodeDeletedEvent event) {
                String uid;
                Node row;
                SampleItemViewDO item;

                row = event.getNode();
                uid = (String)row.getData();

                if ("sampleItem".equals(row.getType())) {
                    item = (SampleItemViewDO)manager.getObject(uid);
                    manager.item.remove(item);
                } else {
                    //ana = (AnalysisViewDO)manager.getObject(uid);
                    //manager.analysis.remove(ana);
                }
            }
        });

         addScreenHandler(addItem, "addItem", new ScreenHandler<Object>() {
             public void onStateChange(StateChangeEvent event) {
                 addItem.setEnabled(canEdit);
             }
         });
         
         addScreenHandler(addAnalysis, "addAnalysis", new ScreenHandler<Object>() {
             public void onStateChange(StateChangeEvent event) {
                 addAnalysis.setEnabled(canEdit);
             }
         });

         addScreenHandler(removeRow, "removeRow", new ScreenHandler<Object>() {
             public void onStateChange(StateChangeEvent event) {
                 removeRow.setEnabled(canEdit);
             }
         });

         addScreenHandler(popoutTree, "popoutTree", new ScreenHandler<Object>() {
             public void onStateChange(StateChangeEvent event) {
                 popoutTree.setEnabled(canPopout);
             }
         });

         /*parentScreen.addActionHandler(new ActionHandler() {
             public void onAction(ActionEvent event) {
                 TreeDataItem selected;
                 ArrayList<SampleDataBundle> bundles;
                 SampleDataBundle data;
                 ArrayList<OrderTestViewDO> orderTests;
                 
                 if (event.getAction() == SampleItemTab.Action.CHANGED) {
                     selected = itemsTree.getSelection();
                     data = (SampleDataBundle)selected.data;
                     
                     // make sure it is a sample item row
                     if ("analysis".equals(selected.leafType))
                         selected = selected.parent;

                     treeUtil.updateSampleItemRow(selected);
                     itemsTree.refreshRow(selected);
                     
                     //if the user changes the sample type then the analysis
                     //tab needs to be refreshed to load the new units dropdown model.
                     //This only needs to happen if they have the analysis row selected
                     if ("analysis".equals(itemsTree.getSelection().leafType))
                         ActionEvent.fire(treeTab, Action.REFRESH_TABS, data);
                 } else if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                     treeUtil.analysisTestChanged((Integer)event.getData(), false);
                 } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                     treeUtil.analysisTestChanged((Integer)event.getData(), true);
                 } else if(event.getAction() == AnalysisTab.Action.ORDER_LIST_ADDED) {
                     treeUtil.importOrderCheckPrep();                   
                 } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                     selected = itemsTree.getSelection();
                     treeUtil.updateAnalysisRow(selected);
                     itemsTree.refreshRow(selected);
                 } else if (event.getAction() == ResultTab.Action.RESULT_HISTORY) {
                     historyCurrentResult();
                 } else if(event.getAction() == ResultTab.Action.REFLEX_ADDED) {
                     //we need to create a new analysis row so the utility can work from that
                     itemsTree.fireEvents(false);
                     treeUtil.onAddAnalysisButtonClick();
                     itemsTree.fireEvents(true);
                     
                     treeUtil.importReflexTestList((ArrayList<SampleDataBundle>)event.getData());
                 } else if (event.getAction() == AnalysisTab.Action.SAMPLE_TYPE_CHANGED) {
                     selected = itemsTree.getSelection();
                     
                     // make sure it is a sample item row
                     if ("analysis".equals(selected.leafType))
                         selected = selected.parent;

                     treeUtil.updateSampleItemRow(selected);
                     itemsTree.refreshRow(selected);
                 }
                 
             }
         });*/
        
        // analysis status dropdown
         model = new ArrayList<Item<Integer>>();
         model.add(new Item<Integer>(null, ""));
         list = CategoryCache.getBySystemName("analysis_status");
         for (DictionaryDO d : list) {
             row = new Item<Integer>(d.getId(), d.getEntry());
             row.setEnabled("Y".equals(d.getIsActive()));
             model.add(row);
         }

         analysisStatus.setModel(model);
         
         /*
          * handlers for the events fired by the screen containing this tab 
          */
         bus.addHandlerToSource(StateChangeEvent.getType(), parentScreen, new StateChangeEvent.Handler() {
             public void onStateChange(StateChangeEvent event) {                
                 setState(event.getState());
                 evaluateEdit();
                 evaluatePopout();
             }
         });
         
         bus.addHandlerToSource(DataChangeEvent.getType(), parentScreen, new DataChangeEvent.Handler() {
             public void onDataChange(DataChangeEvent event) {                
                 evaluateEdit();
                 evaluatePopout();
                 //setState(state);
                 fireDataChange();
                 /*
                  * clear the tabs showing the data related to the nodes in the 
                  * tree e.g. sample item, analysis or results 
                  */
                 bus.fireEvent(new SelectionEvent(SelectedType.NONE, null));   
             }
         });
     }
/*
     private void onTreePopoutClick() {
         ScreenWindow modal;
         
         if (treePopout == null) {
             try {
                 treePopout = new SampleItemsPopoutTreeLookup1();
             } catch (Exception e) {
                 e.printStackTrace();
                 Window.alert("SampleItemsPopoutTreeLookup error: " + e.getMessage());
                 return;
             }
         }
         
         modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
         modal.setName(Messages.get().itemsAndAnalyses());
         modal.setContent(treePopout);
         
         modal.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>(){
             public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                 DataChangeEvent.fire(treeTab, itemsTree);
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
              } 
          });
         
         treePopout.setScreenState(state);
         treePopout.setData(manager);
     }*/

     /*protected void historyCurrentResult() {
         TreeDataItem item;
         SampleDataBundle bundle;

         item = itemsTree.getSelection();

         if (item == null || !"analysis".equals(item.leafType)) {
             window.setError(Messages.get().resultHistoryException());
             return;
         }

         bundle = (SampleDataBundle)item.data;            
         historyUtility.historyCurrentResult(bundle.getSampleItemIndex(), bundle.getAnalysisIndex());
         window.clearStatus();        
     }

     public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
         return addHandler(handler, ActionEvent.getType());
     }*/
    
     public void setData(SampleManager1 manager) {
         if (!DataBaseUtil.isSame(this.manager, manager)) {
             this.manager = manager;
             //treeUtility.setManager(manager);
             evaluateEdit();
         } 
     }
     
     public void evaluateEdit() {
         canEdit = false;
         if (manager != null && isState(ADD, UPDATE))
             canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
     }
     
     public void evaluatePopout() {
         canPopout = isState(DISPLAY, ADD, UPDATE);
     }
     
     public void setState(State state) {
         this.state = state;
         bus.fireEventFromSource(new StateChangeEvent(state), this);
     }
     
     @UiHandler("addItem")
     protected void addItem(ClickEvent event) {
         //treeUtility.onAddItemButtonClick();
     }
     
     @UiHandler("addAnalysis")
     protected void addAnalysis(ClickEvent event) {
         if(itemsTestsTree.getSelectedNode() == -1 && itemsTestsTree.getRowCount() > 1) {
             window.setError(Messages.get().sampleItemSelectedToAddAnalysis());
             return;
         }
         //treeUtility.onAddAnalysisButtonClick();
     }
     
     @UiHandler("removeRow")
     protected void removeRow(ClickEvent event) {
         //treeUtil.onRemoveRowButtonClick();
         
         if(itemsTestsTree.getSelectedNode() == -1)
             removeRow.setEnabled(false);
     }
     
     @UiHandler("popoutTree")
     public void popoutTree(ClickEvent event) {
         /*ScreenWindow modal;
         
         if (treePopout == null) {
             try {
                 treePopout = new SampleItemsPopoutTreeLookup1();
             } catch (Exception e) {
                 e.printStackTrace();
                 Window.alert("SampleItemsPopoutTreeLookup error: " + e.getMessage());
                 return;
             }
         }
         
         modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
         modal.setName(Messages.get().itemsAndAnalyses());
         modal.setContent(treePopout);
         
         modal.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>(){
             public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                 DataChangeEvent.fire(treeTab, itemsTree);
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
              } 
          });
         
         treePopout.setScreenState(state);
         treePopout.setData(manager);*/
     }
     
     private Node getRoot() {
         int i, j;
         Node root, inode, anode;
         AnalysisViewDO ana;
         SampleItemViewDO item;

         root = null;
         if (manager == null)
             return root;

         root = new Node();
         for (i = 0; i < manager.item.count(); i++ ) {
             item = manager.item.get(i);

             inode = new Node(2);
             inode.setType("sampleItem");
             // item.toggle();
             inode.setOpen(true);
             inode.setData(manager.getUid(item));
             //treeUtility.updateSampleItemNode(inode);
             inode.setCell(0, item.getItemSequence() + " - " + formatTreeString(item.getContainer()));
             inode.setCell(1, formatTreeString(item.getTypeOfSample()));
             root.add(inode);

             for (j = 0; j < manager.analysis.count(item); j++ ) {
                 ana = manager.analysis.get(item, j);

                 anode = new Node(2);
                 anode.setType("analysis");
                 anode.setData(manager.getUid(ana));
                 //treeUtility.updateAnalysisRow(anode);
                 anode.setCell(0, formatTreeString(ana.getTestName()) + " : " +
                                 formatTreeString(ana.getMethodName()));
                 anode.setCell(1, ana.getStatusId());
                 inode.add(anode);
             }
         }

         return root;
     }
     
     public String formatTreeString(String val) {
         if (val == null || "".equals(val))
             return "<>";

         return val.trim();
     }
 }