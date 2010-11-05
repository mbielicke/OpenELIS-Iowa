package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class SampleTreeUtility extends Screen implements HasActionHandlers {

    protected TestPrepUtility testLookup;
    private Screen parentScreen;
    private SampleManager     manager;
    private Window            window;
    private Tree              itemsTree;
    private Confirm           cancelAnalysisConfirm;

    public SampleTreeUtility(Window window, Tree itemsTree, Screen parentScreen) {
        this.window = window;
        this.itemsTree = itemsTree;
        this.parentScreen = parentScreen;
    }

    public void onAddItemButtonClick() {
        Node newRow;

        newRow = new Node(itemsTree.getNodeDefintion("sampleitem").size());
        newRow.setType("sampleitem");
        newRow.setOpen(true);

        itemsTree.addNode(newRow);
        itemsTree.selectNodeAt(newRow);
    }

    public void onAddAnalysisButtonClick() {
        Node selectedRow;
        int selectedIndex;
        Node newRow;

        newRow = new Node(itemsTree.getNodeDefintion("analysis").size());
        newRow.setType("analysis");
        newRow.setOpen(true);

        selectedIndex = itemsTree.getSelectedNode();
        if (selectedIndex == -1)
            onAddItemButtonClick();

        selectedIndex = itemsTree.getSelectedNode();
        selectedRow = itemsTree.getNodeAt(selectedIndex);

        if ( !"sampleItem".equals(selectedRow.getType()))
            selectedRow = selectedRow.getParent();

        itemsTree.addNodeAt(selectedRow, newRow);
        itemsTree.selectNodeAt(newRow);
    }

    public void cancelTestClick() {
        Node selectedTreeRow;

        selectedTreeRow = itemsTree.getNodeAt(itemsTree.getSelectedNode());
            
        if(!"analysis".equals(selectedTreeRow.getType()))
            selectedTreeRow = selectedTreeRow.getParent();
            
        assert "analysis".equals(selectedTreeRow.getType()) : "can't find analysis tree row";
        
        itemsTree.selectNodeAt(selectedTreeRow);
        onRemoveRowButtonClick();
    }
    
    public void onRemoveRowButtonClick() {
        final Node selectedTreeRow;

        try {
            selectedTreeRow = itemsTree.getNodeAt(itemsTree.getSelectedNode());
            if ("analysis".equals(selectedTreeRow.getType())) {
                if (selectedTreeRow.getKey() != null) {
                    if (cancelAnalysisConfirm == null) {
                        cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                            parentScreen.consts.get("cancelAnalysisCaption"),
                                                            parentScreen.consts.get("cancelAnalysisMessage"),
                                                            "No", "Yes");
                        cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 1:
                                        cancelAnalysisRow(itemsTree.getSelectedNode());
                                        break;
                                }
                            }
                        });
                    }

                    cancelAnalysisConfirm.show();

                } else {
                    cleanupTestsWithPrep(selectedTreeRow);
                    itemsTree.removeNode(selectedTreeRow);
                }
            } else {
                itemsTree.removeNode(selectedTreeRow);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("removeRowButton: " + e.getMessage());
        }
    }

    public void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
        window.setBusy();
        Node itemRow, newRow;

        itemRow = itemsTree.getNodeAt(itemsTree.getSelectedNode()).getParent();
        
        //try once to get an item row
        if(!"sampleItem".equals(itemRow.getType()))
            itemRow = itemRow.getParent();
        
        assert "sampleItem".equals(itemRow.getType()) : "can't find item tree row";
        
        newRow = null;
        //itemsTree.fireEvents(false);
        for (int i = 0; i < bundles.size(); i++ ) {
            if (i == 0) {
                newRow = itemsTree.getNodeAt(itemsTree.getSelectedNode());
                
                if(!"analysis".equals(newRow.getType()))
                    newRow = newRow.getParent();
                
                assert "analysis".equals(newRow.getType()) : "can't find analysis tree row";
                
                newRow.setData(bundles.get(i));
            } else 
                newRow = addNewTreeRowFromBundle(itemRow, bundles.get(i));
               
            updateAnalysisRow(newRow);
            //itemsTree.refreshRow(newRow);
        }

        //itemsTree.fireEvents(true);
        if (newRow != null)
            selectNewRowFromBundle(newRow);
        else if(bundles.size() == 0)
            ActionEvent.fire(this, Action.REFRESH_TABS, itemsTree.getNodeAt(itemsTree.getSelectedNode()).getData());        
        window.clearStatus();
    }

    public void cleanupTestsWithPrep(Node selectedRow) {
        Node treeItem;
        SampleDataBundle bundle;
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        Integer analysisId;
        int itemIndex, i;
        
        try {
            // grab the current analysis id
            bundle = (SampleDataBundle)selectedRow.getData();
            analysisId = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex()).getAnalysisAt(bundle.getAnalysisIndex()).getId();
            
            if ("analysis".equals(selectedRow.getType()))
                selectedRow = selectedRow.getParent();
            
            itemIndex = 0;
            //get the first sample item row
            if(selectedRow.getParent() == null)
                selectedRow = itemsTree.getRoot().getChildAt(itemIndex);
            else{
                //while(selectedRow.getPreviousSibling() != null)
                  //  selectedRow = selectedRow.getPreviousSibling();
            	selectedRow = selectedRow.getParent().getFirstChild();
            }
            
            anMan = null;
            bundle = null;
            while(selectedRow != null && "sampleItem".equals(selectedRow.getType())){
                // iterate through all the children
                for (i = 0; i < selectedRow.getChildCount(); i++ ) {
                    treeItem = selectedRow.getChildAt(i);
                    bundle = (SampleDataBundle)treeItem.getData();
    
                    if(SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())){
                        if (i == 0)
                            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
        
                        anDO = anMan.getAnalysisAt(bundle.getAnalysisIndex());
        
                        // this test points to the prep, we need to clean it up
                        if (analysisId.equals(anDO.getPreAnalysisId())){
                            anMan.unlinkPrepTest(bundle.getAnalysisIndex());
                            updateAnalysisRow(treeItem);
                            //itemsTree.refreshRow(treeItem);
                        }
                    }
                }
                if(selectedRow.getParent() == null){
                    itemIndex++;
                    if(itemIndex < itemsTree.getRoot().getChildCount())
                        selectedRow = itemsTree.getRoot().getChildAt(itemIndex);
                    else
                        selectedRow = null;
                }else
                    selectedRow = selectedRow.getParent().getChildAt(selectedRow.getParent().getIndex(selectedRow)+1);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("cleanupTestsWithPrep: " + e.getMessage());
        }
    }
    
    public void analysisTestChanged(Integer id, boolean panel) {
        SampleDataBundle analysisBundle;
        Node selectedRow;
        TestPrepUtility.Type type;

        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen(parentScreen);
            
            testLookup.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                    testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
                }
            });
        }

        if (panel)
            type = TestPrepUtility.Type.PANEL;
        else
            type = TestPrepUtility.Type.TEST;

        testLookup.setManager(manager);
        
        try {
            selectedRow = itemsTree.getNodeAt(itemsTree.getSelectedNode());
            
            if(!"analysis".equals(selectedRow.getType())){
                selectedRow = selectedRow.getParent();
                
                assert "analysis".equals(selectedRow.getType()) : "can't find analysis tree row";
            }
            
            analysisBundle = (SampleDataBundle)selectedRow.getData();
            testLookup.lookup(analysisBundle, type, id);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("analysisTestChanged: " + e.getMessage());
        }
    }

    public void importReflexTestList(ArrayList<SampleDataBundle> analysisBundleList) {
        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen(parentScreen);
            
            testLookup.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                    testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
                }
            });
        }
        
        testLookup.setManager(manager);
        
        try {
            testLookup.lookup(analysisBundleList);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("importReflexTestList: " + e.getMessage());
        }
        
    }
    
    public void importOrderTestList(ArrayList<OrderTestViewDO> list) {
        SampleDataBundle analysisBundle;

        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen(parentScreen);
            
            testLookup.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                    testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
                }
            });
        }

        testLookup.setManager(manager);
        analysisBundle = (SampleDataBundle)itemsTree.getNodeAt(itemsTree.getSelectedNode()).getData();

        try {
            testLookup.lookup(analysisBundle, list);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("importOrderTestList: " + e.getMessage());
        }
    }
    
    public void cancelAnalysisRow(int selectedIndex) {
        Node treeRow;
        SampleDataBundle bundle;
        AnalysisManager anMan;

        try {
            treeRow = itemsTree.getNodeAt(selectedIndex);
            bundle = (SampleDataBundle)treeRow.getData();
            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());

            // update the analysis manager
            anMan.cancelAnalysisAt(bundle.getAnalysisIndex());
            updateAnalysisRow(treeRow);
            //itemsTree.refreshRow(treeRow);

            // cleanup the other rows
            cleanupTestsWithPrep(treeRow);

            ActionEvent.fire(this, Action.REFRESH_TABS, bundle);
            
        }catch(ValidationErrorsList e){
            parentScreen.showErrors(e);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("cancelAnalysisRow: " + e.getMessage());
        }
    }
    
    public void updateSampleItemRow(Node treeRow) {
        SampleDataBundle bundle;
        SampleItemViewDO siDO;

        try {
            bundle = (SampleDataBundle)treeRow.getData();
            siDO = manager.getSampleItems().getSampleItemAt(bundle.getSampleItemIndex());

            treeRow.setCell(0,siDO.getItemSequence() + " - " + formatTreeString(siDO.getContainer()));
            treeRow.setCell(1,formatTreeString(siDO.getTypeOfSample()));
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("updateSampleItemRow: " + e.getMessage());
        }
    }
    
    public void updateAnalysisRow(Node treeRow) {
        SampleDataBundle bundle;
        AnalysisViewDO anDO;

        try {
            bundle = (SampleDataBundle)treeRow.getData();
            anDO = manager.getSampleItems()
                          .getAnalysisAt(bundle.getSampleItemIndex())
                          .getAnalysisAt(bundle.getAnalysisIndex());

            treeRow.setCell(0,formatTreeString(anDO.getTestName()) + " : " + formatTreeString(anDO.getMethodName()));
            treeRow.setCell(1,anDO.getStatusId());
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("updateAnalysisRow: " + e.getMessage());
        }
    }

    public String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }

    public void setManager(SampleManager manager) {
        this.manager = manager;
    }

    /**
     * This method is called when the test prep util is done and the screen
     * needs to rebuild the tree. This method is called to add a new row
     * to the tree that the test prep util created.
     */
    public abstract Node addNewTreeRowFromBundle(Node parentRow, SampleDataBundle bundle);
    
    /**
     * This method is called when the test prep util is done and the screen
     * needs to rebuild the tree. This method is called to select the new 
     * analysis row.  It is necessary because the tree structure is different between
     * the login screens and the tracking screen
     */
    public abstract void selectNewRowFromBundle(Node row);

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
