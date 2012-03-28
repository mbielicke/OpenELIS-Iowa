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
import java.util.Iterator;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.TestReflexViewDO;
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
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestReflexLookupScreen extends Screen implements HasActionHandlers<TestReflexLookupScreen.Action> {
    public enum Action {
        SELECTED_REFLEX_ROW
    };

    protected TreeWidget                 reflexTestTree;
    
    private Integer                      autoAddId, autoAddNonDupId, promptNonDupId,
                                         testSectionDefaultId;
    private ArrayList<ArrayList<Object>> reflexBundles;
    private AppButton                    copyToEmptyButton, copyToAllButton;

    public TestReflexLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestReflexLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();
        
        // Initialize Screen
        setState(State.DEFAULT);

        initializeDropdowns();
    }

    private void initialize() {
        reflexTestTree = (TreeWidget)def.getWidget("reflexTestTree");
        addScreenHandler(reflexTestTree, new ScreenEventHandler<ArrayList<TreeDataItem>>() {
            public void onDataChange(DataChangeEvent event) {
                reflexTestTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reflexTestTree.enable(true);
            }
        });

        reflexTestTree.addSelectionHandler(new SelectionHandler<TreeDataItem>() {
            public void onSelection(SelectionEvent<TreeDataItem> event) {
                boolean      enable;
                TreeDataItem selection;

                selection = event.getSelectedItem();
                if (selection != null && "reflexTest".equals(selection.leafType))
                    enable = true;
                else
                    enable = false;

                copyToEmptyButton.enable(enable);
                copyToAllButton.enable(enable);
            }
        });

        reflexTestTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @SuppressWarnings("unchecked")
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int                     i, j;
                ArrayList<TableDataRow> model;
                TableDataRow            tsRow;
                TreeDataItem            row;
                TestSectionManager      tsMan;
                TestSectionViewDO       tsVDO;
                //
                //  
                //
                row = reflexTestTree.getRow(event.getRow());
                if (row.leafType == "reflexTest") {
                    if (event.getCol() == 1) {
                        //
                        // Since the dropdown contains the list of all sections
                        // across the list of reflex tests, we need to enable/disable
                        // the appropriate ones for the reflex test in the selected
                        // row
                        //
                        model = ((Dropdown<Integer>)reflexTestTree.getColumns().get("reflexTest").get(1).colWidget).getData();
                        tsMan = (TestSectionManager) ((ArrayList<Object>)row.data).get(2);
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
                        // If the reflex test is of one of the two AUTO ADD types,
                        // we need to prevent the user from unchecking this item
                        //
                        if (autoAddId.equals(((TestReflexViewDO)((ArrayList<Object>)row.data).get(1)).getFlagsId()) ||
                            autoAddNonDupId.equals(((TestReflexViewDO)((ArrayList<Object>)row.data).get(1)).getFlagsId()))
                            event.cancel();
                    } else {
                        event.cancel();
                    }
                } else {
                    event.cancel();
                }
            }
        });

        reflexTestTree.addBeforeLeafCloseHandler(new BeforeLeafCloseHandler() {
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

        copyToEmptyButton = (AppButton)def.getWidget("copyToEmptyButton");
        addScreenHandler(copyToEmptyButton, new ScreenEventHandler<Object>() {
            @SuppressWarnings("unchecked")
            public void onClick(ClickEvent event) {
                int                i, j;
                Integer            sectionId;
                TreeDataItem       item, selection;
                TestSectionManager tsMan;
                TestSectionViewDO  tsVDO;
                
                reflexTestTree.finishEditing();
                selection = reflexTestTree.getSelection();
                sectionId = (Integer) ((ArrayList<Object>)selection.cells.get(1).getValue()).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < reflexTestTree.numRows(); i++) {
                        item = reflexTestTree.getRow(i);
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
                                    reflexTestTree.refresh(i, 1);
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
                
                reflexTestTree.finishEditing();
                selection = reflexTestTree.getSelection();
                sectionId = (Integer) ((ArrayList<Object>)selection.cells.get(1).getValue()).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < reflexTestTree.numRows(); i++) {
                        item = reflexTestTree.getRow(i);
                        if (item.leafType == "reflexTest") {
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.data).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.cells.get(1).setValue(sectionId);
                                    reflexTestTree.refresh(i, 1);
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
            autoAddId = DictionaryCache.getIdBySystemName("reflex_auto");
            autoAddNonDupId = DictionaryCache.getIdBySystemName("reflex_auto_ndup");
            promptNonDupId = DictionaryCache.getIdBySystemName("reflex_prompt_ndup");
            testSectionDefaultId = DictionaryCache.getIdBySystemName("test_section_default");
        } catch (Exception e) {
            Window.alert("TestReflexUtility constructor: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void ok() {
        int                          i;
        ArrayList<Object>            selectedRow;
        ArrayList<ArrayList<Object>> selectedBundles;
        ValidationErrorsList         errorsList;
        TreeDataItem                 item;

        if (validate()) {
            selectedBundles = new ArrayList<ArrayList<Object>>();
            errorsList = new ValidationErrorsList();
            for (i = 0; i < reflexTestTree.numRows(); i++) {
                item = reflexTestTree.getRow(i);
                if (item.leafType == "analysis")
                    continue;
                
                if ("Y".equals(item.cells.get(2).getValue())) {
                    if (item.cells.get(1).value == null) {
                        errorsList.add(new FormErrorException("reflexTestNeedsSection",
                                                              (String)item.cells.get(0).getValue()));
                    } else {
                        selectedRow = new ArrayList<Object>();
                        selectedRow.add(item.parent.data);
                        selectedRow.add((ResultViewDO)((ArrayList<Object>)item.data).get(0));
                        selectedRow.add((TestReflexViewDO)((ArrayList<Object>)item.data).get(1));
                        selectedRow.add(item.cells.get(1).getValue());
                        selectedBundles.add(selectedRow);
                    }
                }
            }
            
            if (errorsList.size() > 0) {
                showErrors(errorsList);
            } else {
                window.close();
                ActionEvent.fire(this, Action.SELECTED_REFLEX_ROW, selectedBundles);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<TreeDataItem> getTreeModel() {
        int                                i, j, k, anaReflexCount;
        Integer                            defaultId;
        ArrayList<Object>                  reflexBundle, dataObject;
        ArrayList<TableDataRow>            sModel;
        ArrayList<TestReflexViewDO>        reflexList;
        ArrayList<TreeDataItem>            model;
        HashMap<Integer,TestSectionViewDO> sections;
        HashMap<ResultViewDO,ArrayList<TestReflexViewDO>> reflexMap;
        Iterator<ResultViewDO>             iter;
        TableDataRow                       sRow;
        TreeDataItem                       aRow, rtRow;
        AnalysisViewDO                     anDO;
        ResultViewDO                       rVDO;
        SampleDataBundle                   bundle;
        SampleDO                           sDO;
        SampleItemManager                  siMan;
        TestManager                        tMan;
        TestReflexViewDO                   reflexDO;
        TestSectionManager                 tsMan;
        TestSectionViewDO                  tsVDO;

        model = new ArrayList<TreeDataItem>();
        if (reflexBundles == null)
            return model;

        sections = new HashMap<Integer,TestSectionViewDO>();
        try {
            sModel = new ArrayList<TableDataRow>();
            sModel.add(new TableDataRow(null, ""));
            for (i = 0; i < reflexBundles.size(); i++) {
                reflexBundle = reflexBundles.get(i);
                if (reflexBundle.size() != 2)
                    continue;
                
                bundle = (SampleDataBundle) reflexBundle.get(0);
                reflexMap = (HashMap<ResultViewDO,ArrayList<TestReflexViewDO>>) reflexBundle.get(1);
                sDO = bundle.getSampleManager().getSample();
                anDO = bundle.getSampleManager().getSampleItems()
                             .getAnalysisAt(bundle.getSampleItemIndex())
                             .getAnalysisAt(bundle.getAnalysisIndex());

                aRow = new TreeDataItem(3);
                aRow.leafType = "analysis";
                aRow.toggle();
                aRow.key = anDO.getId();
                aRow.cells.get(0).setValue(sDO.getAccessionNumber()+": "+anDO.getTestName()+", "+anDO.getMethodName());
                aRow.data = bundle;

                anaReflexCount = 0;
                iter = reflexMap.keySet().iterator();
                while (iter.hasNext()) {
                    rVDO = iter.next();
                    reflexList = reflexMap.get(rVDO);
                    for (j = 0; j < reflexList.size(); j++) {
                        reflexDO = reflexList.get(j);
                        
                        if (promptNonDupId.equals(reflexDO.getFlagsId()) || autoAddNonDupId.equals(reflexDO.getFlagsId())) {
                            siMan = bundle.getSampleManager().getSampleItems();
                            if (duplicatePresent(siMan, reflexDO.getAddTestId()))
                                continue;
                        }
                        
                        rtRow = new TreeDataItem(3);
                        rtRow.leafType = "reflexTest";
                        rtRow.key = reflexDO.getAddTestId();
                        rtRow.cells.get(0).setValue(reflexDO.getAddTestName()+", "+reflexDO.getAddMethodName());
                        if (autoAddId.equals(reflexDO.getFlagsId()) || autoAddNonDupId.equals(reflexDO.getFlagsId()))
                            rtRow.cells.get(2).setValue("Y");
                        else
                            rtRow.cells.get(2).setValue("N");
                        
                        defaultId = null;
                        try {
                            tMan = TestManager.fetchById(reflexDO.getAddTestId());
                            tsMan = tMan.getTestSections();
                            tsVDO = tsMan.getDefaultSection();
                            if (tsVDO != null)
                                defaultId = tsVDO.getSectionId();
                            for (k = 0; k < tsMan.count(); k++) {
                                tsVDO = tsMan.getSectionAt(k);
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
                                rtRow.cells.get(1).setValue(defaultId);
    
                            dataObject = new ArrayList<Object>();
                            dataObject.add(rVDO);
                            dataObject.add(reflexDO);
                            dataObject.add(tsMan);
                            rtRow.data = dataObject;
                            
                            aRow.addItem(rtRow);
                            anaReflexCount++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                if (anaReflexCount > 0)
                    model.add(aRow);
            }
            ((Dropdown<Integer>)reflexTestTree.getColumns().get("reflexTest").get(1).colWidget).setModel(sModel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }
    
    private boolean duplicatePresent(SampleItemManager itemMan, Integer testId) {
        int             i, j;
        AnalysisManager anMan;
        AnalysisViewDO  anDO;
        
        try {
            for (i = 0; i < itemMan.count(); i++) {
                anMan = itemMan.getAnalysisAt(i);
                for (j = 0; j < anMan.count(); j++) {
                    anDO = anMan.getAnalysisAt(j);
                    if (testId.equals(anDO.getTestId()))
                        return true;
                }
            }
        } catch (Exception e) {
            Window.alert("duplicatePresent: " + e.getMessage());
        }
        
        return false;
    }

    public void setBundles(ArrayList<ArrayList<Object>> bundles) {
        reflexBundles = bundles;

        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}