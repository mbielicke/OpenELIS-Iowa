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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderTestViewDO;
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
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleItemsPopoutTreeLookup;
import org.openelis.modules.sample.client.SampleTreeUtility;
import org.openelis.modules.sample.client.TestPrepUtility;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleItemAnalysisTreeTab1 extends Screen implements HasActionHandlers<SampleItemAnalysisTreeTab1.Action> {
    protected TreeWidget                  itemsTree;
    protected AppButton                   removeRow, addItem, addAnalysis, popoutTree;
    private HasActionHandlers             parentScreen;
    private SampleManager1                 manager;
    protected SampleTreeUtility1           treeUtil;
    protected SampleHistoryUtility1        historyUtility;
    protected SampleItemAnalysisTreeTab1   treeTab;
    protected SampleItemsPopoutTreeLookup1 treePopout;
    protected boolean                     loaded = false;
    protected DictionaryDO                dictDrinkingWater;

    public enum Action {
        REFRESH_TABS
    };

    public SampleItemAnalysisTreeTab1(ScreenDefInt def, WindowInt window,
                                      HasActionHandlers parentScreen, 
                                      SampleHistoryUtility1 historyUtility) {
         setDefinition(def);
         setWindow(window);

         this.parentScreen = parentScreen;
         this.historyUtility = historyUtility;

         initialize();
         initializeDropdowns();
     }

     private void initialize() {
         treeTab = this;
         
         itemsTree = (TreeWidget)def.getWidget("itemsTestsTree");
         treeUtil = new SampleTreeUtility1(window, itemsTree, (Screen)parentScreen) {
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
         });

         addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
             public void onDataChange(DataChangeEvent event) {
                 itemsTree.load(getTreeModel());
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 itemsTree.enable(EnumSet.of(State.ADD, State.UPDATE)
                                         .contains(event.getState()));
             }
         });

         itemsTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>() {
             public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
                 // do nothing
             }
         });

         itemsTree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
             public void onSelection(SelectionEvent<TreeDataItem> event) {
                 SampleDataBundle data;
                 AnalysisViewDO anDO;
                 boolean enable;
                 TreeDataItem selection;

                 selection = itemsTree.getSelection();
                 if (selection != null)
                     data = (SampleDataBundle)selection.data;
                 else
                     data = null;

                 enable = false;
                 if (canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                     if ("sampleItem".equals(selection.leafType)) {
                         enable = !selection.hasChildren();
                     } else {
                         if (data != null) {
                            /* try {
                                 anDO = manager.getSampleItems()
                                               .getAnalysisAt(data.getSampleItemIndex())
                                               .getAnalysisAt(data.getAnalysisIndex());

                                 if (Constants.dictionary().ANALYSIS_CANCELLED.equals(anDO.getStatusId()) ||
                                     Constants.dictionary().ANALYSIS_RELEASED.equals(anDO.getStatusId()))
                                     enable = false;
                                 else
                                     enable = true;

                             } catch (Exception e) {
                                 Window.alert("selectionHandler: " + e.getMessage());
                             } */
                         }
                     }
                 }

                 removeRow.enable(enable);
                 window.clearStatus();
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, data);
             }
         });

         itemsTree.addUnselectionHandler(new UnselectionHandler<TreeDataItem>() {
             public void onUnselection(UnselectionEvent<TreeDataItem> event) {
                 ActionEvent.fire(treeTab, Action.REFRESH_TABS, null);
             }
         });

         itemsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
             public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                 // never allowed to edit tree..use tabs
                 event.cancel();
             }
         });

         itemsTree.addRowAddedHandler(new RowAddedHandler() {
             public void onRowAdded(RowAddedEvent event) {
                 int addedIndex;
                 TreeDataItem addedRow, parentRow;
                 SampleDataBundle parentData;
                 SampleItemManager itemMan;
                 SampleItemViewDO data;
                 AnalysisManager anMan;                

                 try {
                     addedRow = (TreeDataItem)event.getRow();

                     if ("sampleItem".equals(addedRow.leafType)) {
                         //itemMan = manager.getSampleItems();
                         //addedIndex = itemMan.addSampleItem();
                         //data = itemMan.getSampleItemAt(addedIndex);      
                         data = manager.item.add();
                         /*
                          * if the domain of the sample is sdwis then we set the
                          * sample type for the newly added item to "Drinking Water"
                          */
                         if (Constants.domain().SDWIS.equals(manager.getSample().getDomain())) {
                             if (dictDrinkingWater == null)
                                 dictDrinkingWater = DictionaryCache.getBySystemName("drinking_water"); 
                             data.setTypeOfSampleId(dictDrinkingWater.getId());
                             data.setTypeOfSample(dictDrinkingWater.getEntry());
                         }
                         /*addedRow.data = itemMan.getBundleAt(addedIndex);

                         treeUtil.updateSampleItemRow(addedRow);
                         itemsTree.refreshRow(addedRow);*/
                     } else if ("analysis".equals(addedRow.leafType)) {
                         parentRow = addedRow.parent;
                         parentData = (SampleDataBundle)parentRow.data;
                         /*itemMan = manager.getSampleItems();
                         anMan = itemMan.getAnalysisAt(parentData.getSampleItemIndex());

                         addedIndex = anMan.addAnalysis();
                         addedRow.data = anMan.getBundleAt(addedIndex);

                         treeUtil.updateAnalysisRow(addedRow);
                         itemsTree.refreshRow(addedRow);*/
                     }
                 } catch (Exception e) {
                     Window.alert(e.getMessage());
                 }
             }
         });

         itemsTree.addRowDeletedHandler(new RowDeletedHandler() {
             public void onRowDeleted(RowDeletedEvent event) {
                 TreeDataItem row;
                 SampleDataBundle bundle;

                 row = (TreeDataItem)event.getRow();
                 bundle = (SampleDataBundle)row.data;

                 try {
                     /*if ("sampleItem".equals(row.leafType))
                         manager.getSampleItems().removeSampleItemAt(bundle.getSampleItemIndex());
                     else if ("analysis".equals(row.leafType))
                         manager.getSampleItems()
                                .getAnalysisAt(bundle.getSampleItemIndex())
                                .removeAnalysisAt(bundle.getAnalysisIndex());*/

                 } catch (Exception e) {
                     Window.alert("rowDeletedHandler: " + e.getMessage());
                 }
             }
         });

         addItem = (AppButton)def.getWidget("addItemButton");
         addScreenHandler(addItem, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 treeUtil.onAddItemButtonClick();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 addItem.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
             }
         });

         addAnalysis = (AppButton)def.getWidget("addAnalysisButton");
         addScreenHandler(addAnalysis, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 if(itemsTree.getSelectedRow() == -1 && itemsTree.numRows() > 1) {
                     window.setError(Messages.get().sampleItemSelectedToAddAnalysis());
                     return;
                 }
                 treeUtil.onAddAnalysisButtonClick();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 addAnalysis.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
             }
         });

         removeRow = (AppButton)def.getWidget("removeRowButton");
         addScreenHandler(removeRow, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 treeUtil.onRemoveRowButtonClick();
                 
                 if(itemsTree.getSelectedRow() == -1)
                     removeRow.enable(false);
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 removeRow.enable(false);
             }
         });

         popoutTree = (AppButton)def.getWidget("popoutTree");
         addScreenHandler(popoutTree, new ScreenEventHandler<Object>() {
             public void onClick(ClickEvent event) {
                 onTreePopoutClick();
             }

             public void onStateChange(StateChangeEvent<State> event) {
                 popoutTree.enable(EnumSet.of(State.DISPLAY, State.ADD, State.UPDATE).contains(event.getState()));
             }
         });

         parentScreen.addActionHandler(new ActionHandler() {
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
         });
     }

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
     }

     private void initializeDropdowns() {
         ArrayList<TableDataRow> model;

         // preload dictionary models

         // analysis status dropdown
         model = new ArrayList<TableDataRow>();
         model.add(new TableDataRow(null, ""));
         for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
             model.add(new TableDataRow(d.getId(), d.getEntry()));

         ((Dropdown<Integer>)itemsTree.getColumns().get("analysis").get(1).colWidget).setModel(model);
     }

     private ArrayList<TreeDataItem> getTreeModel() {
         int i, j;
         //AnalysisManager am;
         AnalysisViewDO aDO;
         ArrayList<AnalysisViewDO> anas;
         SampleItemViewDO itemDO;
         TreeDataItem tmp;
         TreeDataItem treeModelItem, row;
         ArrayList<TreeDataItem> model;
         ArrayList<TableDataRow> prepRows;
         HashMap<Integer, TreeDataItem> keyTable;

         try {
             
             model = new ArrayList<TreeDataItem>();
             if (manager == null)
                 return model;

             keyTable = new HashMap<Integer, TreeDataItem>();
             for (i = 0; i < manager.item.count(); i++ ) {
                 //SampleItemManager sim = manager.getSampleItems();
                 itemDO = manager.item.get(i);

                 row = new TreeDataItem(2);
                 row.leafType = "sampleItem";
                 row.toggle();
                 row.key = itemDO.getId();
                 //row.data = sim.getBundleAt(i);
                 treeUtil.updateSampleItemRow(row);

                 tmp = keyTable.get(itemDO.getId());
                 if (itemDO.getId() != null && tmp != null) {
                     tmp.addItem(row);
                 } else {
                     keyTable.put(itemDO.getId(), row);
                     model.add(row);
                 }

                 //am = manager.getSampleItems().getAnalysisAt(i);
                 prepRows = new ArrayList<TableDataRow>();
                 prepRows.add(new TableDataRow(null, ""));

                 for (j = 0; j < manager.analysis.count(itemDO); j++ ) {
                     aDO = manager.analysis.get(itemDO, i);
                    // aDO = (AnalysisViewDO)am.getAnalysisAt(j);

                     treeModelItem = new TreeDataItem(2);
                     treeModelItem.leafType = "analysis";

                     treeModelItem.key = aDO.getId();
                     //treeModelItem.data = am.getBundleAt(j);
                     treeUtil.updateAnalysisRow(treeModelItem);

                     row.addItem(treeModelItem);
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }

         return model;
     }

     private boolean canEdit() {
         return (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId()));
     }
     
     protected void historyCurrentResult() {
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
     }

     public void setData(SampleManager1 manager) {
         this.manager = manager;
         treeUtil.setManager(manager);
         loaded = false;
     }

     public void draw() {
         if ( !loaded)
             DataChangeEvent.fire(this);

         loaded = true;
     }
     
     public SampleTreeUtility1 getTreeUtil() {
         return treeUtil;
     }
 }
