package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab.Action;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleTreeUtil implements HasActionHandlers{
	
    protected TestPrepUtility testLookup;
    private HasActionHandlers parentScreen;
    private SampleManager     manager;
    private ScreenWindow      window;
    private TreeWidget        itemsTree;
    
    public SampleTreeUtil(ScreenWindow window, TreeWidget itemsTree, HasActionHandlers parentScreen) {
    	this.window = window;
    	this.itemsTree = itemsTree;
    	this.parentScreen = parentScreen;
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
            } else {
                newRow  = new TreeDataItem(2);
    			newRow.leafType = "analysis";
    			newRow.data = bundles.get(i);
    			TreeDataItem results = new TreeDataItem();
    			results.leafType = "result";
    			results.data = bundles.get(i);
    			results.cells.add(new TableDataCell("Results"));
    			newRow.addItem(results);
    			TreeDataItem qaevent = new TreeDataItem();
    			qaevent.leafType = "qaevent";
    			qaevent.data = bundles.get(i);
    			qaevent.cells.add(new TableDataCell("QA Events"));
    			newRow.addItem(qaevent);
    			TreeDataItem note = new TreeDataItem();
    			note.leafType = "note";
    			note.data = bundles.get(i);
    			note.cells.add(new TableDataCell("Notes"));
    			newRow.addItem(note);	
                itemsTree.addChildItem(itemRow, newRow,itemRow.getItems().size()-2);
            }

            updateAnalysisRow(newRow);
            itemsTree.refreshRow(newRow);
        }

        itemsTree.fireEvents(true);
        if (newRow != null)
            itemsTree.select(newRow);
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
                if (analysisId.equals(anDO.getPreAnalysisId()))
                    anMan.unlinkPrepTest(bundle.getAnalysisIndex());
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

    private String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }
	
    public void setManager(SampleManager manager){
    	this.manager = manager;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}
}
