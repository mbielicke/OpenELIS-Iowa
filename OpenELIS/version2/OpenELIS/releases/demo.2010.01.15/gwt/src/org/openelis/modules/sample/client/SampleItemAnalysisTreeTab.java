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
import org.openelis.domain.TestPrepViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.modules.test.client.TestPrepLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleItemAnalysisTreeTab extends Screen implements HasActionHandlers<SampleItemAnalysisTreeTab.Action> {
    public enum Action {
        REFRESH_TABS, SETUP_BUNDLE
    };

    private Integer              analysisLoggedInId, analysisCancelledId, analysisReleasedId,
                    analysisInPrep;

    protected TreeWidget         itemsTree;
    protected AppButton          removeRow, addItem, addAnalysis;
    private TestPrepLookupScreen prepPickerScreen;
    private Confirm              cancelAnalysisConfirm;

    private HasActionHandlers<SampleTab.Action>    parentScreen;
    private SampleManager        manager;
    private int                  tempId;
    protected boolean            loaded = false;

    public SampleItemAnalysisTreeTab(ScreenDefInt def, ScreenWindow window, HasActionHandlers<SampleTab.Action> parentScreen) {
        setDef(def);
        setWindow(window);

        this.parentScreen = parentScreen;

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        final SampleItemAnalysisTreeTab treeTab = this;

        itemsTree = (TreeWidget)def.getWidget("itemsTestsTree");
        addScreenHandler(itemsTree, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemsTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemsTree.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                        .contains(event.getState()));
                itemsTree.setQueryMode(event.getState() == State.QUERY);
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
                boolean enable;
                TreeDataItem selection = itemsTree.getSelection();

                if (selection != null)
                    data = (SampleDataBundle)selection.data;
                else
                    data = new SampleDataBundle();

                enable = false;
                if (state == State.ADD || state == State.UPDATE) {
                    if ("sampleItem".equals(selection.leafType)) {
                        if (selection.hasChildren())
                            enable = false;
                        else
                            enable = true;
                    } else {
                        if (data.analysisTestDO != null &&
                            (analysisCancelledId.equals(data.analysisTestDO.getStatusId()) || analysisReleasedId.equals(data.analysisTestDO.getStatusId())))
                            enable = false;
                        else
                            enable = true;
                    }
                }

                removeRow.enable(enable);

                ActionEvent.fire(treeTab, Action.REFRESH_TABS, data);
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
                try {
                    TreeDataItem addedRow = (TreeDataItem)event.getRow();

                    if ("sampleItem".equals(addedRow.leafType)) {
                        manager.getSampleItems()
                               .addSampleItem( ((SampleDataBundle)addedRow.data).sampleItemDO);
                    } else if ("analysis".equals(addedRow.leafType)) {
                        SampleDataBundle data = (SampleDataBundle)addedRow.data;
                        int sampleItemIndex = manager.getSampleItems().getIndex(data.sampleItemDO);

                        manager.getSampleItems()
                               .getAnalysisAt(sampleItemIndex)
                               .addAnalysis(data.analysisTestDO);
                    }

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        itemsTree.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                TreeDataItem row = (TreeDataItem)event.getRow();
                SampleDataBundle bundle = (SampleDataBundle)row.data;
                int itemIndex = -1, anIndex = -1;

                try {
                    if (SampleDataBundle.Type.SAMPLE_ITEM.equals(bundle.type)) {
                        itemIndex = manager.getSampleItems().getIndex(bundle.sampleItemDO);
                        manager.getSampleItems().removeSampleItemAt(itemIndex);

                    } else if (SampleDataBundle.Type.ANALYSIS.equals(bundle.type)) {
                        itemIndex = manager.getSampleItems().getIndex(bundle.sampleItemDO);
                        anIndex = manager.getSampleItems()
                                         .getAnalysisAt(itemIndex)
                                         .getIndex(bundle.analysisTestDO);
                        manager.getSampleItems().getAnalysisAt(itemIndex).removeAnalysisAt(anIndex);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addItem = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddItemButtonClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addItem.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addAnalysis = (AppButton)def.getWidget("addAnalysisButton");
        addScreenHandler(addAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onAddAnalysisButtonClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAnalysis.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeRow = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRow, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onRemoveRowButtonClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRow.enable(false);
            }
        });
        
        parentScreen.addActionHandler(new ActionHandler<SampleTab.Action>(){
            public void onAction(ActionEvent<SampleTab.Action> event) {
                if(event.getAction() == SampleTab.Action.SAMPLE_ITEM_CHANGED){
                    TreeDataItem selected = itemsTree.getSelection();

                    // make sure it is a sample item row
                    if ("analysis".equals(selected.leafType))
                        selected = selected.parent;

                    SampleDataBundle data = (SampleDataBundle)selected.data;
                    SampleItemViewDO itemDO = data.sampleItemDO;

                    selected.cells.get(0).value = itemDO.getItemSequence() + " - " +
                                                  formatTreeString(itemDO.getContainer());
                    selected.cells.get(1).value = formatTreeString(itemDO.getTypeOfSample());

                    itemsTree.refresh(true);
                }else if(event.getAction() == SampleTab.Action.ANALYSIS_CHANGED){
                    updateTreeAndCheckPrepTests((ArrayList<SampleDataBundle>)event.getData());
                }else if(event.getAction() == SampleTab.Action.ANALYSIS_CHANGED_DONT_CHECK_PREPS){
                    updateTree((ArrayList<SampleDataBundle>)event.getData());
                }
            }  
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisLoggedInId = DictionaryCache.getIdFromSystemName("analysis_logged_in");
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            analysisInPrep = DictionaryCache.getIdFromSystemName("analysis_inprep");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        // analysis status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("analysis_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)itemsTree.getColumns().get("analysis").get(1).colWidget).setModel(model);
    }

    public void onAddItemButtonClick() {
        int nextItemSequence = manager.getSample().getNextItemSequence();

        TreeDataItem newRow = itemsTree.createTreeItem("sampleItem");
        newRow.toggle();
        newRow.cells.get(0).value = nextItemSequence + " - <>";
        newRow.cells.get(1).value = "<>";

        SampleItemViewDO siDO = new SampleItemViewDO();
        siDO.setItemSequence(nextItemSequence);

        try {

            SampleDataBundle data = new SampleDataBundle(manager.getSampleItems(), siDO);
            newRow.data = data;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        manager.getSample().setNextItemSequence(nextItemSequence + 1);

        itemsTree.addRow(newRow);
        itemsTree.select(newRow);
    }

    public void onAddAnalysisButtonClick() {
        TreeDataItem selectedRow, firstAnalysisRow;
        int selectedIndex;
        TreeDataItem newRow;

        newRow = itemsTree.createTreeItem("analysis");
        newRow.cells.get(0).value = "<> : <>";
        newRow.cells.get(1).value = analysisLoggedInId;

        selectedIndex = itemsTree.getSelectedRow();
        if (selectedIndex == -1)
            onAddItemButtonClick();

        selectedIndex = itemsTree.getSelectedRow();
        selectedRow = itemsTree.getRow(selectedIndex);

        if ( !"sampleItem".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;

        firstAnalysisRow = selectedRow.getFirstChild();

        SampleDataBundle sampleItemData = (SampleDataBundle)selectedRow.data;
        SampleItemViewDO itemDO = sampleItemData.sampleItemDO;
        int sampleItemIndex = sampleItemData.sampleItemManager.getIndex(itemDO);

        try {
            SampleDataBundle data = new SampleDataBundle(
                                                         sampleItemData.sampleItemManager,
                                                         itemDO,
                                                         sampleItemData.sampleItemManager.getAnalysisAt(sampleItemIndex),
                                                         new AnalysisViewDO(), null);
            ActionEvent.fire(this, Action.SETUP_BUNDLE, data);
            data.analysisTestDO.setId(getNextTempId());

            if (firstAnalysisRow != null)
                data.samplePrepDropdownModel = ((SampleDataBundle)firstAnalysisRow.data).samplePrepDropdownModel;
            else
                data.samplePrepDropdownModel = new ArrayList<TableDataRow>();

            data.sampleItemManager.setChangedAt(true, sampleItemIndex);

            newRow.data = data;
            itemsTree.addChildItem(selectedRow, newRow);
            itemsTree.select(newRow);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        if ( !selectedRow.open)
            selectedRow.toggle();
    }

    public void onRemoveRowButtonClick() {
        final TreeDataItem selectedTreeRow;
        SampleDataBundle bundle;
        int sampleItemIndex = -1;

        selectedTreeRow = itemsTree.getSelection();
        if ("analysis".equals(selectedTreeRow.leafType)) {
            if (selectedTreeRow.key != null) {
                if (cancelAnalysisConfirm == null) {
                    cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                        consts.get("cancelAnalysisMessage"), "No",
                                                        "Yes");
                    cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                        public void onSelection(SelectionEvent<Integer> event) {
                            SampleDataBundle bundle = (SampleDataBundle)selectedTreeRow.data;
                            int sampleItemIndex = -1;
                            sampleItemIndex = bundle.sampleItemManager.getIndex(bundle.sampleItemDO);

                            switch (event.getSelectedItem().intValue()) {
                                case 1:
                                    cancelAnalysisRow(itemsTree.getSelectedRow());
                                    bundle.sampleItemManager.setChangedAt(true, sampleItemIndex);
                                    break;
                            }
                        }
                    });
                }

                cancelAnalysisConfirm.show();

            } else {
                bundle = (SampleDataBundle)selectedTreeRow.data;
                sampleItemIndex = bundle.sampleItemManager.getIndex(bundle.sampleItemDO);

                cleanupTestsWithPrep(bundle.analysisTestDO.getId());

                itemsTree.deleteRow(selectedTreeRow);
                bundle.sampleItemManager.setChangedAt(true, sampleItemIndex);
            }
        } else {
            itemsTree.deleteRow(selectedTreeRow);
        }
    }

    private ArrayList<TreeDataItem> getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        TreeDataItem tmp;
        TreeDataItem treeModelItem, row;
        ArrayList<TreeDataItem> model = new ArrayList<TreeDataItem>();
        ArrayList<TableDataRow> prepRows;
        SampleDataBundle aData;
        DictionaryDO dictDO;

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
                // container
                row.cells.get(0).value = itemDO.getItemSequence() + " - " +
                                         formatTreeString(itemDO.getContainer());
                // source,type
                row.cells.get(1).value = itemDO.getTypeOfSample();

                SampleDataBundle data = new SampleDataBundle(manager.getSampleItems(), itemDO);
                row.data = data;

                tmp = keyTable.get(itemDO.getId());
                if (tmp != null) {
                    tmp.addItem(row);
                } else {
                    keyTable.put(itemDO.getId(), row);
                    model.add(row);
                }

                am = manager.getSampleItems().getAnalysisAt(i);
                prepRows = new ArrayList<TableDataRow>();
                prepRows.add(new TableDataRow(null, ""));

                for (j = 0; j < am.count(); j++ ) {
                    AnalysisViewDO aDO = (AnalysisViewDO)am.getAnalysisAt(j);

                    treeModelItem = new TreeDataItem(2);
                    treeModelItem.leafType = "analysis";

                    treeModelItem.key = aDO.getId();
                    treeModelItem.cells.get(0).value = formatTreeString(aDO.getTestName()) + " : " +
                                                       formatTreeString(aDO.getMethodName());
                    treeModelItem.cells.get(1).value = aDO.getStatusId();

                    // only load the test manager in update when the user can
                    // edit it
                    aData = null;
                    if (state == State.UPDATE)
                        aData = new SampleDataBundle(sim, itemDO, am, aDO, am.getTestAt(j));
                    else
                        aData = new SampleDataBundle(sim, itemDO, am, aDO, null);

                    dictDO = DictionaryCache.getEntryFromId(aDO.getStatusId());
                    prepRows.add(new TableDataRow(
                                                  aDO.getId(),
                                                  formatTreeString(aDO.getTestName()) +
                                                                  " : " +
                                                                  formatTreeString(aDO.getMethodName() +
                                                                                   " : " +
                                                                                   dictDO.getEntry()
                                                                                         .trim())));
                    aData.samplePrepDropdownModel = prepRows;

                    treeModelItem.data = aData;

                    row.addItem(treeModelItem);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    private String formatTreeString(String val) {
        if (val == null || "".equals(val))
            return "<>";

        return val.trim();
    }

    private void drawTestPrepScreen(TestPrepManager manager, String testMethodName) {
        if (prepPickerScreen == null) {
            try {
                prepPickerScreen = new TestPrepLookupScreen();
                prepPickerScreen.addActionHandler(new ActionHandler<TestPrepLookupScreen.Action>() {
                    public void onAction(ActionEvent<TestPrepLookupScreen.Action> event) {
                        if (event.getAction() == TestPrepLookupScreen.Action.SELECTED_PREP_ROW) {

                            TableDataRow selectedRow = (TableDataRow)event.getData();
                            Integer testId = (Integer)selectedRow.key;
                            selectedPrepTest(testId);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }

        ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("prepTestPicker"));
        modal.setContent(prepPickerScreen);
        modal.setName(consts.get("prepTestPicker") + " " + testMethodName);
        prepPickerScreen.setManager(manager);
    }

    private void selectedPrepTest(Integer prepTestId) {
        TreeDataItem selectedRow;
        int selectedIndex;
        SampleDataBundle bundle, tmpBundle;
        ArrayList<SampleDataBundle> bundles;
        TestManager testMan;
        Integer currentPrepId;

        currentPrepId = checkForPrepTest(prepTestId);
        bundle = null;
        selectedRow = itemsTree.getSelection();
        selectedIndex = itemsTree.getSelectedRow();
        tmpBundle = (SampleDataBundle)selectedRow.data;

        if (currentPrepId != null) {
            bundle = (SampleDataBundle)selectedRow.data;
            AnalysisViewDO anDO = bundle.analysisTestDO;

            anDO.setPreAnalysisId(currentPrepId);
        } else {
            testMan = null;
            try {
                testMan = TestManager.fetchWithPrepTestsSampleTypes(prepTestId);

            } catch (Exception e) {
                Window.alert(e.getMessage());
            }

            bundle = new SampleDataBundle(tmpBundle.sampleItemManager, tmpBundle.sampleItemDO,
                                          tmpBundle.analysisManager, new AnalysisViewDO(), testMan);
            bundle.samplePrepDropdownModel = tmpBundle.samplePrepDropdownModel;
            ActionEvent.fire(this, Action.SETUP_BUNDLE, bundle);

            // need to put new row in tree
            TreeDataItem newPrepRow = new TreeDataItem(2);
            newPrepRow.leafType = "analysis";
            newPrepRow.checkForChildren(false);
            newPrepRow.data = bundle;

            // set the selected row to in prep
            itemsTree.setCell(selectedIndex, 1, analysisInPrep);
            tmpBundle.analysisTestDO.setStatusId(analysisInPrep);
            tmpBundle.analysisTestDO.setAvailableDate(null);

            // set the pre analysis id
            bundle.analysisTestDO.setId(getNextTempId());
            tmpBundle.analysisTestDO.setPreAnalysisId(bundle.analysisTestDO.getId());

            // set the available date on the prep row
            bundle.analysisTestDO.setAvailableDate(Datetime.getInstance(Datetime.YEAR,
                                                                        Datetime.MINUTE));

            if ("analysis".equals(selectedRow.leafType))
                selectedRow = selectedRow.parent;

            if (newPrepRow != null) {
                itemsTree.addChildItem(selectedRow, newPrepRow);
                itemsTree.select(newPrepRow);
            }

            bundles = new ArrayList<SampleDataBundle>();
            bundles.add(bundle);
            updateTreeAndCheckPrepTests(bundles);
        }
    }

    private Integer checkForPrepTest(Integer testId) {
        // grab the sample item parent
        Integer id = null;
        TreeDataItem selectedRow = itemsTree.getSelection();
        if ("analysis".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;

        for (int i = 0; i < selectedRow.getItems().size(); i++ ) {
            SampleDataBundle bundle = (SampleDataBundle)selectedRow.getItem(i).data;
            AnalysisViewDO anDO = bundle.analysisTestDO;

            if (testId.equals(anDO.getTestId())) {
                id = anDO.getId();
                break;
            }
        }

        return id;

    }

    private void cleanupTestsWithPrep(Integer analysisId) {
        boolean changed = false;
        TreeDataItem treeItem;

        // grab the sample item parent
        TreeDataItem selectedRow = itemsTree.getSelection();
        if ("analysis".equals(selectedRow.leafType))
            selectedRow = selectedRow.parent;

        // iterate through all the children
        for (int i = 0; i < selectedRow.getItems().size(); i++ ) {
            treeItem = selectedRow.getItem(i);
            SampleDataBundle bundle = (SampleDataBundle)treeItem.data;
            AnalysisViewDO anDO = bundle.analysisTestDO;

            // this test points to the prep, we need to clean it up
            if (analysisId.equals(anDO.getPreAnalysisId())) {
                anDO.setPreAnalysisId(null);
                anDO.setStatusId(analysisLoggedInId);
                anDO.setAvailableDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                treeItem.cells.get(1).value = analysisLoggedInId;
                changed = true;
            }
        }

        if (changed)
            itemsTree.refresh(true);
    }

    private void updateTree(ArrayList<SampleDataBundle> bundles) {
        SampleDataBundle bundle;
        TreeDataItem selected;

        selected = itemsTree.getSelection();
        int selectedIndex = itemsTree.getSelectedRow();

        for (int i = 0; i < bundles.size(); i++ ) {
            bundle = bundles.get(i);

            updateTreeRow(selected, selectedIndex, bundle, (i == 0));
        }
    }

    private void updateTreeRow(TreeDataItem selected,
                               int selectedIndex,
                               SampleDataBundle bundle,
                               boolean exists) {
        AnalysisViewDO aDO;

        aDO = bundle.analysisTestDO;

        if (exists)
            selected.data = bundle;
        else {
            // the row doesnt exist. Check to make sure the sample type is right
            // and if it is make a new row and fill it.
            TreeDataItem newRow = new TreeDataItem(2);
            newRow.leafType = "analysis";
            newRow.checkForChildren(false);
            newRow.data = bundle;

            itemsTree.addChildItem(selected.parent, newRow);
            selected = newRow;
        }

        itemsTree.select(selected);
        selectedIndex = itemsTree.getSelectedRow();
        itemsTree.setCell(selectedIndex, 0, formatTreeString(aDO.getTestName()) + " : " +
                                            formatTreeString(aDO.getMethodName()));
        itemsTree.setCell(selectedIndex, 1, aDO.getStatusId());
    }

    private void updateTreeAndCheckPrepTests(ArrayList<SampleDataBundle> bundles) {
        SampleDataBundle bundle;
        TreeDataItem selected;

        selected = itemsTree.getSelection();
        int selectedIndex = itemsTree.getSelectedRow();

        for (int i = 0; i < bundles.size(); i++ ) {
            bundle = bundles.get(i);

            updateTreeRow(selected, selectedIndex, bundle, (i == 0));

            // check for prep tests
            try {
                TestPrepManager prepMan = bundle.testManager.getPrepTests();
                if (prepMan.count() > 0) {
                    TestPrepViewDO requiredTestPrepDO = prepMan.getRequiredTestPrep();
                    if (requiredTestPrepDO == null)
                        drawTestPrepScreen(
                                           prepMan,
                                           formatTreeString(bundle.analysisTestDO.getTestName()) +
                                                           ", " +
                                                           formatTreeString(bundle.analysisTestDO.getMethodName()));
                    else
                        selectedPrepTest(requiredTestPrepDO.getPrepTestId());
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }

    private void cancelAnalysisRow(int selectedIndex) {
        TreeDataItem treeRow;
        SampleDataBundle bundle;
        int index;
        AnalysisViewDO anDO;

        treeRow = itemsTree.getRow(selectedIndex);

        // update the tree row
        bundle = (SampleDataBundle)treeRow.data;
        itemsTree.setCell(selectedIndex, 1, analysisCancelledId);

        // update the analysis manager
        index = bundle.analysisManager.getIndex(bundle.analysisTestDO);
        anDO = bundle.analysisManager.getAnalysisAt(index);
        bundle.analysisManager.cancelAnalysisAt(index);
        
        // cleanup the other rows
        cleanupTestsWithPrep(anDO.getId());

        // update the sample manager status boolean and the tabs.
        // then redraw the tabs to make sure this change didn't change the
        // status
        manager.setHasReleasedCancelledAnalysis(true);
        ActionEvent.fire(this, Action.REFRESH_TABS, bundle);
    }

    private int getNextTempId() {
        return --tempId;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void setData(SampleManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
