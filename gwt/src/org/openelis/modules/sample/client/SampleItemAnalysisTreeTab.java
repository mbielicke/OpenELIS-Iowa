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
import org.openelis.gwt.common.ValidationErrorsList;
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
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleItemAnalysisTreeTab extends Screen
                                                     implements
                                                     HasActionHandlers<SampleItemAnalysisTreeTab.Action> {
    public enum Action {
        REFRESH_TABS
    };

    private Integer           analysisCancelledId, analysisReleasedId;
    protected TreeWidget      itemsTree;
    protected AppButton       removeRow, addItem, addAnalysis;
    private Confirm           cancelAnalysisConfirm;
    protected TestPrepUtility testLookup;
    private HasActionHandlers parentScreen;
    private SampleManager     manager;
    protected boolean         loaded = false;

    public SampleItemAnalysisTreeTab(ScreenDefInt def, ScreenWindow window, HasActionHandlers parentScreen) {
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
                AnalysisViewDO anDO;
                boolean enable;
                TreeDataItem selection = itemsTree.getSelection();

                if (selection != null)
                    data = (SampleDataBundle)selection.data;
                else
                    data = null;

                enable = false;
                if (state == State.ADD || state == State.UPDATE) {
                    if ("sampleItem".equals(selection.leafType)) {
                        enable = !selection.hasChildren();

                    } else {
                        if (data != null) {
                            try {
                                anDO = manager.getSampleItems()
                                              .getAnalysisAt(data.getSampleItemIndex())
                                              .getAnalysisAt(data.getAnalysisIndex());

                                if (analysisCancelledId.equals(anDO.getStatusId()) ||
                                    analysisReleasedId.equals(anDO.getStatusId()))
                                    enable = false;
                                else
                                    enable = true;

                            } catch (Exception e) {
                                Window.alert("selectionHandler: " + e.getMessage());
                            }
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
                TreeDataItem addedRow, parentRow;
                SampleDataBundle parentData;
                int addedIndex;
                SampleItemManager itemMan;
                AnalysisManager anMan;

                try {
                    addedRow = (TreeDataItem)event.getRow();

                    if ("sampleItem".equals(addedRow.leafType)) {
                        itemMan = manager.getSampleItems();
                        addedIndex = itemMan.addSampleItem();
                        addedRow.data = itemMan.getBundleAt(addedIndex);

                        updateSampleItemRow(addedRow);
                        itemsTree.refreshRow(addedRow);

                    } else if ("analysis".equals(addedRow.leafType)) {
                        parentRow = addedRow.parent;
                        parentData = (SampleDataBundle)parentRow.data;
                        itemMan = manager.getSampleItems();
                        anMan = itemMan.getAnalysisAt(parentData.getSampleItemIndex());

                        addedIndex = anMan.addAnalysis();
                        addedRow.data = anMan.getBundleAt(addedIndex);

                        updateAnalysisRow(addedRow);
                        itemsTree.refreshRow(addedRow);

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
                    if ("sampleItem".equals(row.leafType))
                        manager.getSampleItems().removeSampleItemAt(bundle.getSampleItemIndex());
                    else if ("analysis".equals(row.leafType))
                        manager.getSampleItems()
                               .getAnalysisAt(bundle.getSampleItemIndex())
                               .removeAnalysisAt(bundle.getAnalysisIndex());

                } catch (Exception e) {
                    Window.alert("rowDeletedHandler: " + e.getMessage());
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

        parentScreen.addActionHandler(new ActionHandler() {
            public void onAction(ActionEvent event) {
                TreeDataItem selected;
                if (event.getAction() == SampleItemTab.Action.CHANGED) {
                    selected = itemsTree.getSelection();

                    // make sure it is a sample item row
                    if ("analysis".equals(selected.leafType))
                        selected = selected.parent;

                    updateSampleItemRow(selected);
                    itemsTree.refreshRow(selected);

                } else if (event.getAction() == AnalysisTab.Action.ANALYSIS_ADDED) {
                    analysisTestChanged((Integer)event.getData(), false);
                } else if (event.getAction() == AnalysisTab.Action.PANEL_ADDED) {
                    analysisTestChanged((Integer)event.getData(), true);
                } else if (event.getAction() == AnalysisTab.Action.CHANGED_DONT_CHECK_PREPS) {
                    selected = itemsTree.getSelection();
                    updateAnalysisRow(selected);
                    itemsTree.refreshRow(selected);
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
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
                                                            consts.get("cancelAnalysisMessage"),
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

    private ArrayList<TreeDataItem> getTreeModel() {
        int i, j;
        AnalysisManager am;
        SampleItemViewDO itemDO;
        TreeDataItem tmp;
        TreeDataItem treeModelItem, row;
        ArrayList<TreeDataItem> model = new ArrayList<TreeDataItem>();
        ArrayList<TableDataRow> prepRows;

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
                updateSampleItemRow(row);

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
                    treeModelItem.data = am.getBundleAt(j);
                    updateAnalysisRow(treeModelItem);

                    row.addItem(treeModelItem);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
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
                newRow = new TreeDataItem(2);
                newRow.leafType = "analysis";
                newRow.data = bundles.get(i);
                itemsTree.addChildItem(itemRow, newRow);
            }

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
    
    private void cleanupTestsWithPrep(Integer analysisId) {
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

    private void analysisTestChanged(Integer id, boolean panel) {
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

    private void cancelAnalysisRow(int selectedIndex) {
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
            showErrors(e);
        } catch (Exception e) {
            Window.alert("cancelAnalysisRow: " + e.getMessage());
        }
    }

    private void updateSampleItemRow(TreeDataItem treeRow) {
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

    private void updateAnalysisRow(TreeDataItem treeRow) {
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
