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

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class SampleItemsPopoutTreeLookup extends Screen {
    protected SampleTreeUtility     treeUtil;
    protected TreeWidget            itemsTestsTree;
    protected TableWidget           sampleItemTable;
    protected AppButton             moveButton;
    private SampleManager           manager;
    private ArrayList<TreeDataItem> checkedAnaRows;

    public SampleItemsPopoutTreeLookup() throws Exception {
        super((ScreenDefInt)GWT.create(SampleItemsPopoutTreeDef.class));
        
        initialize();
        initializeDropdowns();

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        itemsTestsTree = (TreeWidget)def.getWidget("itemsTestsTree");
        
        addScreenHandler(itemsTestsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (checkedAnaRows == null)
                    checkedAnaRows = new ArrayList<TreeDataItem>();
                else
                    checkedAnaRows.clear();
                itemsTestsTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTestsTree.enable(true);
            }
        });
        
        itemsTestsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               TreeDataItem item;
               
               item = itemsTestsTree.getSelection();
               if ((state != State.ADD && state != State.UPDATE) || !"analysis".equals(item.leafType) 
                               || event.getCol() != 0 || !canEdit())
                   event.cancel();
            } 
        });
        
        itemsTestsTree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                if ("Y".equals(event.getValue()))
                    checkedAnaRows.add(itemsTestsTree.getSelection());
                else
                    checkedAnaRows.remove(itemsTestsTree.getSelection());
            }
        });        
        
        treeUtil = new SampleTreeUtility(window, itemsTestsTree, this){
            public TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow, SampleDataBundle bundle) {
                TreeDataItem row;
                
                row = new TreeDataItem(3);
                row.leafType = "analysis";
                row.data = bundle;
                
                itemsTestsTree.addChildItem(parentRow, row);
                
                return row;
            }
            
            public void selectNewRowFromBundle(TreeDataItem row) {
                itemsTestsTree.select(row);
                itemsTestsTree.scrollToVisible();
            }
        };
        
        moveButton = (AppButton)def.getWidget("moveButton");
        addScreenHandler(moveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                move();
            }
        });
        
        sampleItemTable = (TableWidget)def.getWidget("sampleItemTable");
        addScreenHandler(sampleItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTable.enable(true);
            }
        });
        
        sampleItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ((state != State.ADD && state != State.UPDATE) || event.getCol() != 0 || !canEdit())
                    event.cancel();
            }
        });
        
        sampleItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                /*
                 * if the user checks a row then uncheck all the other rows 
                 */
                if ("Y".equals(event.getValue())) {
                    for (int i = 0; i < sampleItemTable.numRows(); i++) {
                        if (i != event.getRow())
                            sampleItemTable.setCell(i, 0, "N");
                    }
                }
            }
        });
    }    
    
    private void move() {
        AnalysisManager am;
        AnalysisViewDO ana;
        SampleDataBundle anaBundle, itemBundle;
                
        /*
         * at least one analysis must be checked
         */
        if (checkedAnaRows.size() == 0) {
            window.setError(consts.get("selectOneOrMoreAnalyses"));
            return;
        }
        
        itemBundle = null;
        
        /*
         * at least one sample item must be checked
         */
        for (TableDataRow row : sampleItemTable.getData()) {
            if ("Y".equals(row.cells.get(0).getValue())) {
                itemBundle = (SampleDataBundle)row.data;
                break;
            }
        }
        
        if (itemBundle == null) {
            window.setError(consts.get("selectItem"));
            return;
        }
        
        try {
            for (TableDataRow row : checkedAnaRows) {
                anaBundle = (SampleDataBundle)row.data;               
                
                ana = anaBundle.getSampleManager().getSampleItems()
                .getAnalysisAt(anaBundle.getSampleItemIndex())
                .getAnalysisAt(anaBundle.getAnalysisIndex());
                
                if (itemBundle.getSampleItemIndex() == anaBundle.getSampleItemIndex()) {
                    /*
                     * an analysis can't be moved to its own item
                     */
                    window.setError(consts.get("analysisNotMovedToOwnItem"));
                    return;
                } else if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) || 
                                Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId())) {
                    /*
                     * released and cancelled analyses can't be moved
                     */
                    window.setError(consts.get("noMoveReleasedCancelledAnalyses"));
                    return;                    
                } 
            }
            
            for (TableDataRow row : checkedAnaRows) {
                anaBundle = (SampleDataBundle)row.data;
                manager.getSampleItems().moveAnalysis(anaBundle, itemBundle);

                // reset the moved row data bundle
                am = manager.getSampleItems().getAnalysisAt(itemBundle.getSampleItemIndex());
                row.data = am.getBundleAt(am.count() - 1);
            }
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Move failed: " + e.getMessage());
        }
    }

    private ArrayList<TreeDataItem> getTreeModel() {
        int i, j;        
        AnalysisViewDO ana;
        AnalysisManager anaMan;
        SampleItemViewDO item;
        SampleItemManager itemMan;
        TreeDataItem itemRow, anaRow, tmp;
        ArrayList<TreeDataItem> model;
        HashMap<Integer, TreeDataItem> keyTable;

        model = new ArrayList<TreeDataItem>();
        if (manager == null)
            return model;
        
        try {
            itemMan = manager.getSampleItems();
            
            keyTable = new HashMap<Integer, TreeDataItem>();
            for (i = 0; i < itemMan.count(); i++ ) {
                item = itemMan.getSampleItemAt(i);

                itemRow = new TreeDataItem(2);
                itemRow.leafType = "sampleItem";
                itemRow.toggle();
                itemRow.key = item.getId();
                itemRow.data = itemMan.getBundleAt(i);
                treeUtil.updateSampleItemRow(itemRow);

                tmp = keyTable.get(item.getId());
                if (item.getId() != null && tmp != null) {
                    tmp.addItem(itemRow);
                } else {
                    keyTable.put(item.getId(), itemRow);
                    model.add(itemRow);
                }

                anaMan = itemMan.getAnalysisAt(i);

                for (j = 0; j < anaMan.count(); j++ ) {
                    ana = (AnalysisViewDO)anaMan.getAnalysisAt(j);

                    anaRow = new TreeDataItem(3);
                    anaRow.leafType = "analysis";

                    anaRow.key = ana.getId();
                    anaRow.data = anaMan.getBundleAt(j);
                    anaRow.cells.get(0).setValue("N");
                    anaRow.cells.get(1).setValue(treeUtil.formatTreeString(ana.getTestName()) + " : " +
                                                 treeUtil.formatTreeString(ana.getMethodName()));
                    anaRow.cells.get(2).setValue(ana.getStatusId());
                    
                    itemRow.addItem(anaRow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleItemViewDO data;
        TableDataRow row;
        SampleItemManager itemMan;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            itemMan = manager.getSampleItems();

            for (int i = 0; i < itemMan.count(); i++ ) {
                data = itemMan.getSampleItemAt(i);
                row = new TableDataRow(2);
                row.cells.get(0).setValue("N");
                row.cells.get(1).setValue(data.getItemSequence());
                row.data = itemMan.getBundleAt(i);
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)itemsTestsTree.getColumns().get("analysis").get(2).colWidget).setModel(model);
    }    
    
    public void setScreenState(State state){
        setState(state);
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        treeUtil.setManager(manager);
        DataChangeEvent.fire(this);
        moveButton.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(state));
        
        /*
         * this is done to get rid of any old error messages
         */
        window.clearStatus();
    }    
    
    private boolean canEdit() {
        return (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId()));
    }
}