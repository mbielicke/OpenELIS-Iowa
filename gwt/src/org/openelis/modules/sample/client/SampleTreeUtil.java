package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public abstract class SampleTreeUtil extends Screen implements HasActionHandlers {

    protected TestPrepUtility testLookup;
    private HasActionHandlers parentScreen;
    private SampleManager     manager;
    private ScreenWindow      window;
    private TreeWidget        itemsTree;
    private Confirm           cancelAnalysisConfirm;

    public SampleTreeUtil(ScreenWindow window, TreeWidget itemsTree, HasActionHandlers parentScreen) {
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
        TreeDataItem selectedRow;
        int selectedIndex;
        TreeDataItem newRow;

        newRow = itemsTree.createTreeItem("analysis");
        newRow.toggle();

        selectedIndex = itemsTree.getSelectedRow();
        if (selectedIndex == -1)
            onAddItemButtonClick();

        selectedIndex = itemsTree.getSelectedRow();
        selectedRow = itemsTree.getRow(selectedIndex);

        if ( !"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;

        itemsTree.addChildItem(selectedRow, newRow);
        itemsTree.select(newRow);
    }

    public void onRemoveRowButtonClick() {
        final TreeDataItem selectedTreeRow;
        SampleDataBundle bundle;
        AnalysisViewDO anDO;
        // int sampleItemIndex = -1;

        try {
            selectedTreeRow = itemsTree.getSelection();
            if ("analysis".equals(selectedTreeRow.leafType)) {
                if (selectedTreeRow.key != null) {
                    if (cancelAnalysisConfirm == null) {
                        cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                            ((Screen)parentScreen).consts.get("cancelAnalysisMessage"),
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
                    bundle = (SampleDataBundle)selectedTreeRow.data;
                    anDO = manager.getSampleItems()
                                  .getAnalysisAt(bundle.getSampleItemIndex())
                                  .getAnalysisAt(bundle.getAnalysisIndex());

                    cleanupTestsWithPrep(anDO.getId());
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
        window.setBusy();
        TreeDataItem itemRow, newRow;

        itemRow = itemsTree.getSelection().parent;
        newRow = null;
        itemsTree.fireEvents(false);
        for (int i = 0; i < bundles.size(); i++ ) {
            if (i == 0) {
                newRow = itemsTree.getSelection();
                newRow.data = bundles.get(i);
            } else 
                newRow = addNewTreeRowFromBundle(itemRow, bundles.get(i));
               
            updateAnalysisRow(newRow);
            itemsTree.refreshRow(newRow);
        }

        itemsTree.fireEvents(true);
        if (newRow != null)
            itemsTree.select(newRow);
        else if(bundles.size() == 0)
            ActionEvent.fire(this, Action.REFRESH_TABS, itemsTree.getSelection().data);
        
        window.clearStatus();
    }

    public void cleanupTestsWithPrep(Integer analysisId) {
        TreeDataItem treeItem;
        SampleDataBundle bundle;
        AnalysisManager anMan;
        AnalysisViewDO anDO;

        try {
            // grab the sample item parent
            TreeDataItem selectedRow = itemsTree.getSelection();
            if ("analysis".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;

            anMan = null;
            // iterate through all the children
            for (int i = 0; i < selectedRow.getItems().size(); i++ ) {
                treeItem = selectedRow.getItem(i);
                bundle = (SampleDataBundle)treeItem.data;

                if (anMan == null)
                    anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());

                anDO = anMan.getAnalysisAt(bundle.getAnalysisIndex());

                // this test points to the prep, we need to clean it up
                if (analysisId.equals(anDO.getPreAnalysisId())){
                    anMan.unlinkPrepTest(bundle.getAnalysisIndex());
                    updateAnalysisRow(treeItem);
                }
            }
        } catch (Exception e) {
            Window.alert("cleanupTestsWithPrep: " + e.getMessage());
        }
    }
    
    public void analysisTestChanged(Integer id, boolean panel) {
        SampleDataBundle analysisBundle;
        TestPrepUtility.Type type;

        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen((Screen)parentScreen);
            
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
        analysisBundle = (SampleDataBundle)itemsTree.getSelection().data;

        try {
            testLookup.lookup(analysisBundle, type, id);

        } catch (Exception e) {
            Window.alert("analysisTestChanged: " + e.getMessage());
        }
    }
    
    public void cancelAnalysisRow(int selectedIndex) {
        TreeDataItem treeRow;
        SampleDataBundle bundle;
        AnalysisManager anMan;

        try {
            treeRow = itemsTree.getRow(selectedIndex);
            bundle = (SampleDataBundle)treeRow.data;
            anMan = manager.getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());

            // update the analysis manager
            anMan.cancelAnalysisAt(bundle.getAnalysisIndex());
            updateAnalysisRow(treeRow);
            itemsTree.refreshRow(treeRow);

            // cleanup the other rows
            cleanupTestsWithPrep(anMan.getAnalysisAt(bundle.getAnalysisIndex()).getId());

            ActionEvent.fire(this, Action.REFRESH_TABS, bundle);
            
        }catch(ValidationErrorsList e){
            ((Screen)parentScreen).showErrors(e);
        } catch (Exception e) {
            Window.alert("cancelAnalysisRow: " + e.getMessage());
        }
    }
    
    public void updateSampleItemRow(TreeDataItem treeRow) {
        SampleDataBundle bundle;
        SampleItemViewDO siDO;

        try {
            bundle = (SampleDataBundle)treeRow.data;
            siDO = manager.getSampleItems().getSampleItemAt(bundle.getSampleItemIndex());

            treeRow.cells.get(0).setValue(
                                          siDO.getItemSequence() + " - " +
                                                          formatTreeString(siDO.getContainer()));
            treeRow.cells.get(1).setValue(formatTreeString(siDO.getTypeOfSample()));
        } catch (Exception e) {
            Window.alert("updateSampleItemRow: " + e.getMessage());
        }
    }
    
    public void updateAnalysisRow(TreeDataItem treeRow) {
        SampleDataBundle bundle;
        AnalysisViewDO anDO;

        try {
            bundle = (SampleDataBundle)treeRow.data;
            anDO = manager.getSampleItems()
                          .getAnalysisAt(bundle.getSampleItemIndex())
                          .getAnalysisAt(bundle.getAnalysisIndex());

            treeRow.cells.get(0).setValue(
                                          formatTreeString(anDO.getTestName()) + " : " +
                                                          formatTreeString(anDO.getMethodName()));
            treeRow.cells.get(1).setValue(anDO.getStatusId());
        } catch (Exception e) {
            Window.alert("updateAnalysisRow: " + e.getMessage());
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
    public abstract TreeDataItem addNewTreeRowFromBundle(TreeDataItem parentRow, SampleDataBundle bundle);

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
