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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.FormErrorException;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafCloseEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafCloseHandler;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestSectionManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestPrepLookupScreen extends Screen implements HasActionHandlers<TestPrepLookupScreen.Action> {
    public enum Action {
        SELECTED_PREP_ROW, CANCEL
    };

    protected TreeWidget      prepTestTree;

    private Integer                      testSectionDefaultId;
    private ArrayList<ArrayList<Object>> prepBundles;
    private AppButton                    copyToEmptyButton, copyToAllButton;

    public TestPrepLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestPrepLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        initializeDropdowns();
    }

    private void initialize() {
        prepTestTree = (TreeWidget)def.getWidget("prepTestTree");
        addScreenHandler(prepTestTree, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                prepTestTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepTestTree.enable(true);
            }
        });

        prepTestTree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                boolean      enable;
                TreeDataItem selection;

                selection = event.getSelectedItem();
                if (selection != null && "prepTest".equals(selection.leafType))
                    enable = true;
                else
                    enable = false;

                copyToEmptyButton.enable(enable);
                copyToAllButton.enable(enable);
            }
        });

        prepTestTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @SuppressWarnings("unchecked")
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int                     i, j;
                ArrayList<TableDataRow> model;
                TableDataRow            tsRow;
                TreeDataItem            row;
                TestSectionManager      tsMan;
                TestSectionViewDO       tsVDO;
                
                row = prepTestTree.getRow(event.getRow());
                if (row.leafType == "prepTest") {
                    if (event.getCol() == 1) {
                        //
                        // Since the dropdown contains the list of all sections
                        // across the list of prep tests, we need to enable/disable
                        // the appropriate ones for the prep test in the selected
                        // row
                        //
                        model = ((Dropdown<Integer>)prepTestTree.getColumns().get("prepTest").get(1).colWidget).getData();
                        tsMan = (TestSectionManager) ((ArrayList<Object>)row.data).get(1);
                        for (i = 0; i < model.size(); i++) {
                            tsRow = model.get(i);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (tsVDO.getSectionId().equals(tsRow.key)) {
                                    tsRow.enabled = true;
                                    break;
                                }
                            }
                            if (j == tsMan.count())
                                tsRow.enabled = false;
                        }
                    } else if (event.getCol() == 2) {
                        //
                        // If the prep test is NOT optional, we need to prevent
                        // the user from unchecking this item
                        //
                        if ("N".equals(((TestPrepViewDO)((ArrayList<Object>)row.data).get(0)).getIsOptional()))
                            event.cancel();
                    } else {
                        event.cancel();
                    }
                } else {
                    event.cancel();
                }
            }
        });

        prepTestTree.addBeforeLeafCloseHandler(new BeforeLeafCloseHandler() {
            public void onBeforeLeafClose(BeforeLeafCloseEvent event) {
                event.cancel();
            }
        });

        final AppButton okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        final AppButton cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });

        copyToEmptyButton = (AppButton)def.getWidget("copyToEmptyButton");
        addScreenHandler(copyToEmptyButton, new ScreenEventHandler<Object>() {
            @SuppressWarnings("unchecked")
            public void onClick(ClickEvent event) {
                int                i, j;
                Integer            sectionId;
                TreeDataItem       item, selection;
                TestSectionManager tsMan;
                TestSectionViewDO  tsVDO;
                
                prepTestTree.finishEditing();
                selection = prepTestTree.getSelection();
                sectionId = (Integer) ((ArrayList<Object>)selection.cells.get(1).getValue()).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < prepTestTree.numRows(); i++) {
                        item = prepTestTree.getRow(i);
                        if (item.leafType == "reflexTest") {
                            if (item.cells.get(1).getValue() != null) {
                                if (item.cells.get(1).getValue() instanceof ArrayList) {
                                    if (((ArrayList<Object>)item.cells.get(1).getValue()).get(0) != null)
                                        continue;
                                } else {
                                    continue;
                                }
                            }
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.data).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.cells.get(1).setValue(sectionId);
                                    prepTestTree.refresh(i, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                copyToEmptyButton.enable(false);
            }
        });

        copyToAllButton = (AppButton)def.getWidget("copyToAllButton");
        addScreenHandler(copyToAllButton, new ScreenEventHandler<Object>() {
            @SuppressWarnings("unchecked")
            public void onClick(ClickEvent event) {
                int                i, j;
                Integer            sectionId;
                TreeDataItem       item, selection;
                TestSectionManager tsMan;
                TestSectionViewDO  tsVDO;
                
                prepTestTree.finishEditing();
                selection = prepTestTree.getSelection();
                sectionId = (Integer) ((ArrayList<Object>)selection.cells.get(1).getValue()).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < prepTestTree.numRows(); i++) {
                        item = prepTestTree.getRow(i);
                        if (item.leafType == "reflexTest") {
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.data).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.cells.get(1).setValue(sectionId);
                                    prepTestTree.refresh(i, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                copyToAllButton.enable(false);
            }
        });
    }

    private void initializeDropdowns() {
        try {
            testSectionDefaultId = DictionaryCache.getIdBySystemName("test_section_default");
        } catch (Exception e) {
            Window.alert("inializeDropdowns: " + e.getMessage());
            window.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void ok() {
        int                          i;
        ArrayList<ArrayList<Object>> selectedBundles;
        ValidationErrorsList         errorsList;
        TreeDataItem                 item;

        if (validate()) {
            selectedBundles = new ArrayList<ArrayList<Object>>(prepTestTree.numRows());
            errorsList = new ValidationErrorsList();
            for (i = 0; i < prepTestTree.numRows(); i++) {
                item = prepTestTree.getRow(i);
                if (item.leafType == "analysis")
                    addPrepTestToSelection(item, (SampleDataBundle)((ArrayList<Object>)item.data).get(0),
                                           selectedBundles, errorsList, i);
            }
            
            if (errorsList.size() > 0) {
                showErrors(errorsList);
            } else {
                window.close();
                ActionEvent.fire(this, Action.SELECTED_PREP_ROW, selectedBundles);
            }
            selectedBundles.clear();
        }
    }

    private void cancel() {
        if (validate()) {
            Window.alert(consts.get("prepTestRequiredException"));
            window.close();
            ActionEvent.fire(this, Action.CANCEL, null);
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<TreeDataItem> getTreeModel() {
        int                                i, j;
        ArrayList<Object>                  prepBundle;
        ArrayList<TableDataRow>            sModel;
        ArrayList<TreeDataItem>            model;
        HashMap<Integer,TestSectionViewDO> sections;
        TreeDataItem                       row;
        AnalysisViewDO                     anDO;
        SampleDataBundle                   bundle;
        TestPrepManager                    tpMan;
        TestPrepViewDO                     tpVDO;

        model = new ArrayList<TreeDataItem>();
        if (prepBundles == null)
            return model;

        sections = new HashMap<Integer,TestSectionViewDO>();
        try {
            sModel = new ArrayList<TableDataRow>();
            for (i = 0; i < prepBundles.size(); i++) {
                prepBundle = prepBundles.get(i);
                if (prepBundle.size() != 2)
                    continue;
                
                bundle = (SampleDataBundle) prepBundle.get(0);
                tpMan = (TestPrepManager) prepBundle.get(1);
                anDO = bundle.getSampleManager().getSampleItems()
                             .getAnalysisAt(bundle.getSampleItemIndex())
                             .getAnalysisAt(bundle.getAnalysisIndex());
                
                row = new TreeDataItem(3);
                row.leafType = "analysis";
                row.toggle();
                row.key = anDO.getId();
                row.cells.get(0).setValue(anDO.getTestName()+", "+anDO.getMethodName());
                row.data = prepBundle;
                
                tpVDO = tpMan.getRequiredTestPrep();
                if (tpVDO != null) {
                    addPrepItem(row, tpVDO, sections, sModel);
                } else {
                    for (j = 0; j < tpMan.count(); j++) {
                        tpVDO = tpMan.getPrepAt(j);
                        addPrepItem(row, tpVDO, sections, sModel);
                    }
                }
                model.add(row);
            }
            ((Dropdown<Integer>)prepTestTree.getColumns().get("prepTest").get(1).colWidget).setModel(sModel);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }
    
    private void addPrepItem(TreeDataItem parentItem, TestPrepViewDO tpVDO,
                             HashMap<Integer,TestSectionViewDO> sections, ArrayList<TableDataRow> sModel) {
        int                i;
        Integer            defaultId;
        ArrayList<Object>  dataObject;
        TableDataRow       sRow;
        TreeDataItem       row;
        TestManager        tMan;
        TestPrepManager    tpMan;
        TestPrepViewDO     tpVDO2;
        TestSectionManager tsMan;
        TestSectionViewDO  tsVDO;

        row = new TreeDataItem(3);
        row.leafType = "prepTest";
        row.key = tpVDO.getPrepTestId();
        row.cells.get(0).setValue(tpVDO.getPrepTestName()+", "+tpVDO.getMethodName());
        if ("N".equals(tpVDO.getIsOptional()))
            row.cells.get(2).setValue("Y");
        else
            row.cells.get(2).setValue("N");

        defaultId = null;
        try {
            tMan = TestManager.fetchById(tpVDO.getPrepTestId());
            tsMan = tMan.getTestSections();
            tsVDO = tsMan.getDefaultSection();
            if (tsVDO != null)
                defaultId = tsVDO.getSectionId();
            for (i = 0; i < tsMan.count(); i++) {
                tsVDO = tsMan.getSectionAt(i);
                if (!sections.containsKey(tsVDO.getSectionId())) {
                    sRow = new TableDataRow(1);
                    sRow.key = tsVDO.getSectionId();
                    sRow.cells.get(0).setValue(tsVDO.getSection());
                    sRow.data = tsVDO;
                    sModel.add(sRow);
                    sections.put(tsVDO.getSectionId(), tsVDO);
                }
            }
    
            if (defaultId != null)
                row.cells.get(1).setValue(defaultId);
    
            tpMan = tMan.getPrepTests();
            if (tpMan.count() > 0) {
                row.toggle();
                tpVDO2 = tpMan.getRequiredTestPrep();
                if (tpVDO2 != null) {
                    addPrepItem(row, tpVDO2, sections, sModel);
                } else {
                    for (i = 0; i < tpMan.count(); i++) {
                        tpVDO2 = tpMan.getPrepAt(i);
                        addPrepItem(row, tpVDO2, sections, sModel);
                    }
                }
            }
            
            dataObject = new ArrayList<Object>(2);
            dataObject.add(tpVDO);
            dataObject.add(tsMan);
            row.data = dataObject;
            
            parentItem.addItem(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void addPrepTestToSelection(TreeDataItem parentItem, Object parentBundle,
                                        ArrayList<ArrayList<Object>> selectedBundles,
                                        ValidationErrorsList errorsList, int index) {
        int               depth;
        ArrayList<Object> selectedRow;
        TreeDataItem      item;

        depth = parentItem.depth + 1;
        for (index++; index < prepTestTree.numRows(); index++) {
            item = prepTestTree.getRow(index);
            if (item.depth < depth) {
                break;
            } else if (item.depth == depth && "Y".equals(item.cells.get(2).getValue())) {
                if (item.cells.get(1).value == null) {
                    errorsList.add(new FormErrorException("prepTestNeedsSection",
                                                          (String)item.cells.get(0).getValue()));
                    break;
                } else {
                    selectedRow = new ArrayList<Object>(3);
                    selectedRow.add(parentBundle);
                    selectedRow.add(((TestPrepViewDO)((ArrayList<Object>)item.data).get(0)).getPrepTestId());
                    selectedRow.add(item.cells.get(1).getValue());
                    selectedBundles.add(selectedRow);
                    if (item.hasChildren())
                        addPrepTestToSelection(item, selectedRow, selectedBundles,
                                               errorsList, index);
                }
            }
        }
        return;
    }

    public void setBundles(ArrayList<ArrayList<Object>> bundles) {
        prepBundles = bundles;

        DataChangeEvent.fire(this);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
