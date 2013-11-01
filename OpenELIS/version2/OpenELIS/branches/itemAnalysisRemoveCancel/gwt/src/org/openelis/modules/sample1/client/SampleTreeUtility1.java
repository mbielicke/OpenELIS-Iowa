package org.openelis.modules.sample1.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.HasDataChangeHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.tree.Node;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public abstract class SampleTreeUtility1 extends Screen implements HasActionHandlers, HasDataChangeHandlers {

    protected TestPrepUtility1     testPrep;
    private Screen                parentScreen;
    private SampleManager1        manager;
    private WindowInt       window;
    private TreeWidget            itemsTree;
    private TestPrepActionHandler testPrepActionHandler;
    private Confirm               cancelAnalysisConfirm;

    private enum ACTION {
        ORDER, PREP, REFLEX
    }
    
    public SampleTreeUtility1(WindowInt window, TreeWidget itemsTree, Screen parentScreen) {
        this.window = window;
        this.itemsTree = itemsTree;
        this.parentScreen = parentScreen;
    }

    public void onAddItemButtonClick() {
        TreeDataItem newRow;

        newRow = itemsTree.createTreeItem("sampleItem");
        newRow.toggle();

        itemsTree.addRow(newRow);
        itemsTree.select(newRow);        
    }    

    public void onAddAnalysisButtonClick() {
        TreeDataItem selectedRow, newRow;

        if (itemsTree.getSelectedRow() == -1) {
            if (itemsTree.numRows() == 0)
                onAddItemButtonClick();
            else
                itemsTree.select(0);
        }

        selectedRow = itemsTree.getRow(itemsTree.getSelectedRow());

        if ( !"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;

        newRow = itemsTree.createTreeItem("analysis");
        newRow.toggle();
        itemsTree.addChildItem(selectedRow, newRow);
        itemsTree.select(newRow);
    }

    public void cancelTestClick() {
        TreeDataItem selectedTreeRow;

        selectedTreeRow = itemsTree.getSelection();

        if ( !"analysis".equals(selectedTreeRow.leafType))
            selectedTreeRow = selectedTreeRow.parent;

        assert "analysis".equals(selectedTreeRow.leafType) : "can't find analysis tree row";

        itemsTree.select(selectedTreeRow);
        onRemoveRowButtonClick();
    }

    public void onRemoveRowButtonClick() {
        Integer key;
        final TreeDataItem selectedTreeRow;        

        try {
            selectedTreeRow = itemsTree.getSelection();
            if ("analysis".equals(selectedTreeRow.leafType)) {
                key = (Integer)selectedTreeRow.key; 
                if (key != null && key > 0) {
                    if (cancelAnalysisConfirm == null) {
                        cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                            Messages.get().cancelAnalysisCaption(),
                                                            Messages.get().cancelAnalysisMessage(),
                                                            "No", "Yes");
                        cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 1:
                                        cancelAnalysisRow(itemsTree.getSelectedRow());
                                        break;
                                }
                            }
                        });
                    }

                    cancelAnalysisConfirm.show();

                } else {
                    cleanupTestsWithPrep(selectedTreeRow);
                    itemsTree.deleteRow(selectedTreeRow);
                }
            } else {
                itemsTree.deleteRow(selectedTreeRow);
            }
        } catch (Exception e) {
            Window.alert("removeRowButton: " + e.getMessage());
        }
    }

    public void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
        TreeDataItem itemRow, newRow;        
        
        window.setBusy();
        itemRow = itemsTree.getSelection().parent;

        // try once to get an item row
        if ( !"sampleItem".equals(itemRow.leafType))
            itemRow = itemRow.parent;

        assert "sampleItem".equals(itemRow.leafType) : "can't find item tree row";

        newRow = null;
        itemsTree.fireEvents(false);
        for (int i = 0; i < bundles.size(); i++ ) {
            if (i == 0) {
                newRow = itemsTree.getSelection();
                if ( !"analysis".equals(newRow.leafType))
                    newRow = newRow.parent;
                assert "analysis".equals(newRow.leafType) : "can't find analysis tree row";
                newRow.data = bundles.get(i);
            } else
                newRow = addNewTreeRowFromBundle(itemRow, bundles.get(i));
            //updateAnalysisRow(newRow);
            itemsTree.refreshRow(newRow);
        }
        itemsTree.fireEvents(true);
        if (newRow != null)
            selectNewRowFromBundle(newRow);
        else if (bundles.size() == 0)
            ActionEvent.fire(this, Action.REFRESH_TABS, itemsTree.getSelection().data);

        window.clearStatus();
    }

    public void cleanupTestsWithPrep(TreeDataItem selectedRow) {
        TreeDataItem treeItem;
        SampleDataBundle bundle;
        AnalysisManager anMan;
        AnalysisViewDO anDO;
        Integer analysisId;
        int itemIndex, i;

        try {
            // grab the current analysis id
            bundle = (SampleDataBundle)selectedRow.data;
            /*analysisId = manager.getSampleItems()
                                .getAnalysisAt(bundle.getSampleItemIndex())
                                .getAnalysisAt(bundle.getAnalysisIndex())
                                .getId();*/

            if ("analysis".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;

            itemIndex = 0;
            // get the first sample item row
            if (selectedRow.parent == null)
                selectedRow = itemsTree.getData().get(itemIndex);
            else {
                while (selectedRow.getPreviousSibling() != null)
                    selectedRow = selectedRow.getPreviousSibling();
            }

            anMan = null;
            bundle = null;
            while (selectedRow != null && "sampleItem".equals(selectedRow.leafType)) {
                // iterate through all the children
                for (i = 0; i < selectedRow.getItems().size(); i++ ) {
                    treeItem = selectedRow.getItem(i);
                    bundle = (SampleDataBundle)treeItem.data;

                    /*if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())) {
                        if (i == 0)
                            anMan = manager.getSampleItems()
                                           .getAnalysisAt(bundle.getSampleItemIndex());

                        anDO = anMan.getAnalysisAt(bundle.getAnalysisIndex());

                        // this test points to the prep, we need to clean it up
                        if (analysisId.equals(anDO.getPreAnalysisId())) {
                            anMan.unlinkPrepTest(bundle.getAnalysisIndex());
                            updateAnalysisRow(treeItem);
                            itemsTree.refreshRow(treeItem);
                        }
                    }*/
                }
                if (selectedRow.parent == null) {
                    itemIndex++ ;
                    if (itemIndex < itemsTree.getData().size())
                        selectedRow = itemsTree.getData().get(itemIndex);
                    else
                        selectedRow = null;
                } else
                    selectedRow = selectedRow.getNextSibling();
            }
        } catch (Exception e) {
            Window.alert("cleanupTestsWithPrep: " + e.getMessage());
        }
    }

    public void analysisTestChanged(Integer id, boolean panel) {
        SampleDataBundle analysisBundle;
        TreeDataItem selectedRow;
        TestPrepUtility1.Type type;

        if (testPrep == null) {
            testPrep = new TestPrepUtility1();
            testPrep.setScreen(parentScreen);
            
            testPrepActionHandler = new TestPrepActionHandler();
            testPrep.addActionHandler(testPrepActionHandler);
        }

        if (panel)
            type = TestPrepUtility1.Type.PANEL;
        else
            type = TestPrepUtility1.Type.TEST;

        try {
            selectedRow = itemsTree.getSelection();

            if ( !"analysis".equals(selectedRow.leafType)) {
                selectedRow = selectedRow.parent;
                assert "analysis".equals(selectedRow.leafType) : "can't find analysis tree row";
            }
            analysisBundle = (SampleDataBundle)selectedRow.data;
            testPrepActionHandler.action = SampleTreeUtility1.ACTION.PREP;
            testPrep.lookup(analysisBundle, type, id);
        } catch (Exception e) {
            Window.alert("analysisTestChanged: " + e.getMessage());             
        }
    }

    public void importReflexTestList(ArrayList<SampleDataBundle> analysisBundleList) {
        if (testPrep == null) {
            testPrep = new TestPrepUtility1();
            testPrep.setScreen(parentScreen);

            testPrepActionHandler = new TestPrepActionHandler();
            testPrep.addActionHandler(testPrepActionHandler);            
        }

        try {
            testPrepActionHandler.action = SampleTreeUtility1.ACTION.REFLEX;
            testPrep.lookup(analysisBundleList);
        } catch (Exception e) {
            Window.alert("importReflexTestList: " + e.getMessage());
        }
    }

    public void importOrderCheckPrep() {
        SampleItemManager itemMan;        
        AnalysisManager anaMan;
        ArrayList<SampleDataBundle> bundles;
        
        if (testPrep == null) {
            testPrep = new TestPrepUtility1();
            testPrep.setScreen(parentScreen);

            testPrepActionHandler = new TestPrepActionHandler();
            testPrep.addActionHandler(testPrepActionHandler);            
        }        
        
        try {
            /*
             * create the list of analysis bundles to be passed to the prep utility
             * so that any prep tests selected by the user could be added to the
             * manager by the utility
             */
            /*itemMan = manager.getSampleItems();
            bundles = new ArrayList<SampleDataBundle>();
            for (int i = 0; i < itemMan.count(); i++ ) {
                anaMan = itemMan.getAnalysisAt(i);
                for (int j = 0; j < anaMan.count(); j++)
                    bundles.add(anaMan.getBundleAt(j));                
            }*/
            
            testPrepActionHandler.parentScreen = parentScreen;
            testPrepActionHandler.utility = this;
            testPrepActionHandler.action = SampleTreeUtility1.ACTION.ORDER;
            //testPrep.lookup(bundles);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }        
    }

    public void cancelAnalysisRow(int selectedIndex) {
        TreeDataItem treeRow;
        SampleDataBundle bundle;
        AnalysisManager anMan;

        try {
            treeRow = itemsTree.getRow(selectedIndex);
            bundle = (SampleDataBundle)treeRow.data;
            //anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());

            // update the analysis manager
            //anMan.cancelAnalysisAt(bundle.getAnalysisIndex());
            //updateAnalysisRow(treeRow);
            itemsTree.refreshRow(treeRow);

            // cleanup the other rows
            cleanupTestsWithPrep(treeRow);

            ActionEvent.fire(this, Action.REFRESH_TABS, bundle);

        //} catch (ValidationErrorsList e) {
          //  parentScreen.showErrors(e);
        } catch (Exception e) {
            Window.alert("cancelAnalysisRow: " + e.getMessage());
        }
    }

    public void updateSampleItemNode(Node node) {
        String uid;
        SampleItemViewDO i;
        
        uid = (String)node.getData();
        i = (SampleItemViewDO)manager.getObject(uid);
        node.setCell(0, i.getItemSequence() + " - " + formatTreeString(i.getContainer()));
        node.setCell(1, formatTreeString(i.getTypeOfSample()));
    }

    public void updateAnalysisRow(Node node) {
        String uid;
        AnalysisViewDO a;

        uid = (String)node.getData();
        a = (AnalysisViewDO)manager.getObject(uid);
        node.setCell(0, formatTreeString(a.getTestName()) + " : " +
                        formatTreeString(a.getMethodName()));
        node.setCell(1, a.getStatusId());
    }

    public String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }

    public void setManager(SampleManager1 manager) {
        this.manager = manager;
    }

    /**
     * This method is called when the test prep util is done and the screen
     * needs to rebuild the tree. This method is called to add a new row to the
     * tree that the test prep util created.
     */
    public abstract TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow,
                                                         SampleDataBundle bundle);

    /**
     * This method is called when the test prep util is done and the screen
     * needs to rebuild the tree. This method is called to select the new
     * analysis row. It is necessary because the tree structure is different
     * between the login screens and the tracking screen
     */
    public abstract void selectNewRowFromBundle(TreeDataItem row);

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private class TestPrepActionHandler implements ActionHandler<TestPrepUtility1.Action>{

        private SampleTreeUtility1.ACTION action;
        private SampleTreeUtility1        utility;
        private Screen                   parentScreen;
        
        @Override
        public void onAction(ActionEvent<TestPrepUtility1.Action> event) {
            if (SampleTreeUtility1.ACTION.ORDER == action) {
                /*
                 * notify the parent screen that it needs to refresh the tree 
                 * showing the analyses
                 */
                DataChangeEvent.fire(utility, parentScreen);
            } else if (EnumSet.of(SampleTreeUtility1.ACTION.PREP, SampleTreeUtility1.ACTION.REFLEX)
                            .contains(action)) {
                testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
            }            
        }        
    }
}