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
import org.openelis.gwt.event.DropEvent;
import org.openelis.gwt.event.DropHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.event.DropEnterEvent.DropPosition;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.DragItem;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
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
    protected Tree              sampleTreePopout;
    private SampleManager       manager;

    public SampleItemsPopoutTreeLookup() throws Exception {
        super((ScreenDefInt)GWT.create(SampleItemsPopoutTreeDef.class));

        
        initialize();
        initializeDropdowns();

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        sampleTreePopout = (Tree)def.getWidget("itemsTestsTree");
        sampleTreePopout.enableDrag();
        sampleTreePopout.enableDrop();
        
        addStateChangeHandler(new StateChangeHandler<State>() {
            public void onStateChange(StateChangeEvent<State> event) {
                //sampleTreePopout.enableDrag(EnumSet.of(State.ADD, State.UPDATE).contains(state));
                //sampleTreePopout.enableDrop(EnumSet.of(State.ADD, State.UPDATE).contains(state));
            }
        });
        
        treeUtil = new SampleTreeUtility(window, sampleTreePopout, this){
            public Node addNewTreeRowFromBundle(Node parentRow, SampleDataBundle bundle) {
                Node row;
                
                row = new Node(2);
                row.setType("analysis");
                row.setData(bundle);
                
                sampleTreePopout.addNodeAt(parentRow, row);
                
                return row;
            }
            
            public void selectNewRowFromBundle(Node row) {
                sampleTreePopout.selectNodeAt(row);
                sampleTreePopout.scrollToVisible(sampleTreePopout.getSelectedNode());
            }
        };
        
        addScreenHandler(sampleTreePopout, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTreePopout.setRoot(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTreePopout.setEnabled(true);
            }
        });
        
        sampleTreePopout.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //nothing
            }
        });
        
        sampleTreePopout.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               event.cancel();
            } 
        });
        
        sampleTreePopout.getDragController().addBeforeDragStartHandler(new BeforeDragStartHandler<DragItem>() {
            public void onBeforeDragStart(BeforeDragStartEvent<DragItem> event) {
                Node treeItem;
                Label label;
                
                try{
                    treeItem = sampleTreePopout.getNodeAt(event.getDragObject().getIndex());
                    if(!treeItem.getType().equals("analysis"))
                        event.cancel();
                    else{
                        label = new Label(treeItem.getCell(0) + " | " + 
                                          DictionaryCache.getEntryFromId((Integer)treeItem.getCell(1)).getEntry());
                        label.setStyleName("ScreenLabel");
                        label.setWordWrap(false);
                        event.setProxy(label);
                    }
                }catch(Exception e){
                    Window.alert("tree beforeDragStart: "+e.getMessage());
                }
            }
        });
        
        sampleTreePopout.getDropController().addBeforeDropHandler(new BeforeDropHandler<DragItem>() {
            public void onBeforeDrop(BeforeDropEvent<DragItem> event) {
                AnalysisViewDO anDO;
                AnalysisManager am;
                Node dropTarget = (Node)event.getDropTarget();
                Node dragItem = sampleTreePopout.getNodeAt(event.getDragObject().getIndex());
                SampleDataBundle dragKey = (SampleDataBundle)dragItem.getData();
                SampleDataBundle dropKey = (SampleDataBundle)dropTarget.getData();
                try {
                    anDO = manager.getSampleItems()
                                  .getAnalysisAt(dragKey.getSampleItemIndex())
                                  .getAnalysisAt(dragKey.getAnalysisIndex());
                    
                    if(anDO.getPreAnalysisId() != null)
                        manager.getSampleItems().getAnalysisAt(dragKey.getSampleItemIndex()).unlinkPrepTest(dragKey.getAnalysisIndex());
                    
                    manager.getSampleItems().moveAnalysis(dragKey,dropKey);
                    
                    //reset the dropped row data bundle
                    am = manager.getSampleItems().getAnalysisAt(dropKey.getSampleItemIndex());
                    dragItem.setData(am.getBundleAt(am.count()-1));
                    
                }catch(Exception e) {
                    e.printStackTrace();
                    Window.alert("Move failed: "+e.getMessage());
                }
            }
        });
        
        sampleTreePopout.getDropController().addDropEnterHandler(new DropEnterHandler<DragItem>() {
            public void onDropEnter(DropEnterEvent<DragItem> event) {
                Node dropTarget = (Node)event.getDropTarget();
                Node dragItem = sampleTreePopout.getNodeAt(event.getDragObject().getIndex());
                
                if(!dropTarget.getType().equals("sampleItem") || event.getDropPosition() != DropPosition.ON || 
                                (dropTarget.getType().equals("sampleItem") && dropTarget.equals(dragItem.getParent())))
                    event.cancel();
            }
        });
        
        sampleTreePopout.addDropTarget(sampleTreePopout.getDropController());
    }
    
    private Node getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        Node tmp;
        Node treeModelItem, row;
        Node model = new Node();

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

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)sampleTreePopout.getNodeDefinitionAt("analysis",1).getCellEditor().getWidget()).setModel(model);
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
