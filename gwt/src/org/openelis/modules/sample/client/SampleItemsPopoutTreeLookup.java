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
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.BeforeDragStartEvent;
import org.openelis.gwt.event.BeforeDragStartHandler;
import org.openelis.gwt.event.BeforeDropEvent;
import org.openelis.gwt.event.BeforeDropHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.DropEnterEvent;
import org.openelis.gwt.event.DropEnterHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.event.DropEnterEvent.DropPosition;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Window;

public class SampleItemsPopoutTreeLookup extends Screen {
    protected SampleTreeUtility treeUtil;
    protected TreeWidget        sampleTreePopout;
    private SampleManager       manager;

    public SampleItemsPopoutTreeLookup() throws Exception {
        super((ScreenDefInt)GWT.create(SampleItemsPopoutTreeDef.class));

        
        initialize();
        initializeDropdowns();

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        sampleTreePopout = (TreeWidget)def.getWidget("itemsTestsTree");
        sampleTreePopout.enableDrag(true);
        sampleTreePopout.enableDrop(true);
        
        addStateChangeHandler(new StateChangeHandler<State>() {
            public void onStateChange(StateChangeEvent<State> event) {
                sampleTreePopout.enableDrag(EnumSet.of(State.ADD, State.UPDATE).contains(state));
                sampleTreePopout.enableDrop(EnumSet.of(State.ADD, State.UPDATE).contains(state));
            }
        });
        
        treeUtil = new SampleTreeUtility(window, sampleTreePopout, this){
            public TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow, SampleDataBundle bundle) {
                TreeDataItem row;
                
                row = new TreeDataItem(2);
                row.leafType = "analysis";
                row.data = bundle;
                
                sampleTreePopout.addChildItem(parentRow, row);
                
                return row;
            }
        };
        
        addScreenHandler(sampleTreePopout, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTreePopout.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTreePopout.enable(true);
            }
        });
        
        sampleTreePopout.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>(){
            public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
                //nothing
            }
        });
        
        sampleTreePopout.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               event.cancel();
            } 
        });
        
        sampleTreePopout.addBeforeDragStartHandler(new BeforeDragStartHandler<TreeRow>() {
            public void onBeforeDragStart(BeforeDragStartEvent<TreeRow> event) {
                TreeDataItem treeItem;
                Label label;
                
                treeItem = event.getDragObject().item;
                if(!treeItem.leafType.equals("analysis"))
                    event.cancel();
                else{
                    label = new Label(treeItem.cells.get(0).value + " | " + 
                                      treeItem.cells.get(1).value);
                    label.setStyleName("ScreenLabel");
                    event.setProxy(label);
                }
            };
        });
        
        sampleTreePopout.addBeforeDropHandler(new BeforeDropHandler<TreeRow>() {
            public void onBeforeDrop(BeforeDropEvent<TreeRow> event) {
                AnalysisViewDO anDO;
                AnalysisManager am;
                TreeDataItem dropTarget = ((TreeRow)event.getDropTarget()).item;
                TreeDataItem dragItem = event.getDragObject().dragItem;
                SampleDataBundle dragKey = (SampleDataBundle)dragItem.data;
                SampleDataBundle dropKey = (SampleDataBundle)dropTarget.data;
                try {
                    anDO = manager.getSampleItems()
                                  .getAnalysisAt(dragKey.getSampleItemIndex())
                                  .getAnalysisAt(dragKey.getAnalysisIndex());
                    
                    if(anDO.getPreAnalysisId() != null)
                        manager.getSampleItems().getAnalysisAt(dragKey.getSampleItemIndex()).unlinkPrepTest(dragKey.getAnalysisIndex());
                    
                    manager.getSampleItems().moveAnalysis(dragKey,dropKey);
                    
                    //reset the dropped row data bundle
                    am = manager.getSampleItems().getAnalysisAt(dropKey.getSampleItemIndex());
                    dragItem.data = am.getBundleAt(am.count()-1);
                    
                }catch(Exception e) {
                    e.printStackTrace();
                    Window.alert("Move failed: "+e.getMessage());
                }
            }
        });
        
        sampleTreePopout.addDropEnterHandler(new DropEnterHandler<TreeRow>() {
            public void onDropEnter(DropEnterEvent<TreeRow> event) {
                TreeDataItem dropTarget = ((TreeRow)event.getDropTarget()).item;
                TreeDataItem dragItem = event.getDragObject().dragItem;
                
                if((dropTarget.leafType.equals("sampleItem") && (dropTarget.hasChildren() || event.getDropPosition() != DropPosition.ON)) || 
                                (dropTarget.leafType.equals("analysis") && 
                                                (!dropTarget.equals(dropTarget.parent.getLastChild()) || dropTarget.parent.equals(dragItem.parent) || event.getDropPosition() == DropPosition.ON || 
                                                                event.getDropPosition() == DropPosition.ABOVE)))
                    
                    event.cancel();
            }
        });
        
        sampleTreePopout.addTarget(sampleTreePopout);
    }
    
    
    
    private ArrayList<TreeDataItem> getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        TreeDataItem tmp;
        TreeDataItem treeModelItem, row;
        ArrayList<TreeDataItem> model = new ArrayList<TreeDataItem>();

        try {
            HashMap<Integer, TreeDataItem> keyTable = new HashMap<Integer, TreeDataItem>();

            if (manager == null)
                return model;

            for (i = 0; i < manager.getSampleItems().count(); i++ ) {
                SampleItemManager sim = manager.getSampleItems();
                itemDO = sim.getSampleItemAt(i);

                row = new TreeDataItem(2);
                row.leafType = "sampleItem";
                row.toggle();
                row.key = itemDO.getId();
                row.data = sim.getBundleAt(i);
                treeUtil.updateSampleItemRow(row);

                tmp = keyTable.get(itemDO.getId());
                if (itemDO.getId() != null && tmp != null) {
                    tmp.addItem(row);
                } else {
                    keyTable.put(itemDO.getId(), row);
                    model.add(row);
                }

                am = manager.getSampleItems().getAnalysisAt(i);

                for (j = 0; j < am.count(); j++ ) {
                    AnalysisViewDO aDO = (AnalysisViewDO)am.getAnalysisAt(j);

                    treeModelItem = new TreeDataItem(2);
                    treeModelItem.leafType = "analysis";

                    treeModelItem.key = aDO.getId();
                    treeModelItem.data = am.getBundleAt(j);
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)sampleTreePopout.getColumns().get("analysis").get(1).colWidget).setModel(model);
    }
    
    public void setScreenState(State state){
        setState(state);
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        treeUtil.setManager(manager);
        DataChangeEvent.fire(this);
    }
}
