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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.Node;
import org.openelis.gwt.widget.tree.Tree;
import org.openelis.gwt.widget.tree.event.BeforeNodeCloseEvent;
import org.openelis.gwt.widget.tree.event.BeforeNodeCloseHandler;
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

    protected Tree                       reflexTestTree;
    
    private Integer                      autoAddId, autoAddNonDupId, promptNonDupId,
                                         testSectionDefaultId;
    private ArrayList<ArrayList<Object>> reflexBundles;
    private Button                       copyToEmptyButton, copyToAllButton;

    public TestReflexLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestReflexLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();
        
        // Initialize Screen
        setState(State.DEFAULT);

        initializeDropdowns();
    }

    private void initialize() {
        reflexTestTree = (Tree)def.getWidget("reflexTestTree");
        addScreenHandler(reflexTestTree, new ScreenEventHandler<Node>() {
            public void onDataChange(DataChangeEvent event) {
                reflexTestTree.setRoot(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reflexTestTree.setEnabled(true);
            }
        });

        reflexTestTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                boolean      enable;
                Node         selection;

                selection = reflexTestTree.getNodeAt(event.getSelectedItem());
                if (selection != null && "reflexTest".equals(selection.getType()))
                    enable = true;
                else
                    enable = false;

                copyToEmptyButton.setEnabled(enable);
                copyToAllButton.setEnabled(enable);
            }
        });

        reflexTestTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @SuppressWarnings("unchecked")
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int                     i, j;
                ArrayList<Item<Integer>> model;
                Item<Integer>            tsRow;
                Node                    node;
                TestSectionManager      tsMan;
                TestSectionViewDO       tsVDO;
                //
                //  
                //
                node = reflexTestTree.getNodeAt(event.getRow());
                if (node.getType() == "reflexTest") {
                    if (event.getCol() == 1) {
                        //
                        // Since the dropdown contains the list of all sections
                        // across the list of reflex tests, we need to enable/disable
                        // the appropriate ones for the reflex test in the selected
                        // row
                        //
                        model = ((Dropdown<Integer>)reflexTestTree.getNodeDefinitionAt("reflexTest",1).getCellEditor().getWidget()).getModel();
                        tsMan = (TestSectionManager) ((ArrayList<Object>)node.getData()).get(2);
                        for (i = 0; i < model.size(); i++) {
                            tsRow = model.get(i);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (tsVDO.getSectionId().equals(tsRow.getKey())) {
                                    tsRow.setEnabled(true);
                                    break;
                                }
                            }
                            if (j == tsMan.count())
                                tsRow.setEnabled(false);
                        }
                    } else if (event.getCol() == 2) {
                        //
                        // If the reflex test is of one of the two AUTO ADD types,
                        // we need to prevent the user from unchecking this item
                        //
                        if (autoAddId.equals(((TestReflexViewDO)((ArrayList<Object>)node.getData()).get(1)).getFlagsId()) ||
                            autoAddNonDupId.equals(((TestReflexViewDO)((ArrayList<Object>)node.getData()).get(1)).getFlagsId()))
                            event.cancel();
                    } else {
                        event.cancel();
                    }
                } else {
                    event.cancel();
                }
            }
        });

        reflexTestTree.addBeforeNodeCloseHandler(new BeforeNodeCloseHandler() {
            public void onBeforeNodeClose(BeforeNodeCloseEvent event) {
                event.cancel();
            }
        });

        final Button okButton = (Button)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.setEnabled(true);
            }
        });

        copyToEmptyButton = (Button)def.getWidget("copyToEmptyButton");
        addScreenHandler(copyToEmptyButton, new ScreenEventHandler<Object>() {
            @SuppressWarnings("unchecked")
            public void onClick(ClickEvent event) {
                int                i, j;
                Integer            sectionId;
                Node               item, selection;
                TestSectionManager tsMan;
                TestSectionViewDO  tsVDO;
                
                reflexTestTree.finishEditing();
                selection = reflexTestTree.getNodeAt(reflexTestTree.getSelectedNode());
                sectionId = (Integer) ((ArrayList<Object>)selection.getCell(1)).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < reflexTestTree.getRowCount(); i++) {
                        item = reflexTestTree.getNodeAt(i);
                        if (item.getType() == "reflexTest") {
                            if (item.getCell(1) != null) {
                                if (item.getCell(1) instanceof ArrayList) {
                                    if (((ArrayList<Object>)item.getCell(1)).get(0) != null)
                                        continue;
                                } else {
                                    continue;
                                }
                            }
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.getData()).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.setCell(1,sectionId);
                                    //reflexTestTree.refresh(i, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                copyToEmptyButton.setEnabled(false);
            }
        });

        copyToAllButton = (Button)def.getWidget("copyToAllButton");
        addScreenHandler(copyToAllButton, new ScreenEventHandler<Object>() {
            @SuppressWarnings("unchecked")
            public void onClick(ClickEvent event) {
                int                i, j;
                Integer            sectionId;
                Node               item, selection;
                TestSectionManager tsMan;
                TestSectionViewDO  tsVDO;
                
                reflexTestTree.finishEditing();
                selection = reflexTestTree.getNodeAt(reflexTestTree.getSelectedNode());
                sectionId = (Integer) ((ArrayList<Object>)selection.getCell(1)).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < reflexTestTree.getRowCount(); i++) {
                        item = reflexTestTree.getNodeAt(i);
                        if (item.getType() == "reflexTest") {
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.getData()).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.setCell(1,sectionId);
                                    //reflexTestTree.refresh(i, 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                copyToAllButton.setEnabled(false);
            }
        });
    }

    private void initializeDropdowns() {
        try {
            autoAddId = DictionaryCache.getIdFromSystemName("reflex_auto");
            autoAddNonDupId = DictionaryCache.getIdFromSystemName("reflex_auto_ndup");
            promptNonDupId = DictionaryCache.getIdFromSystemName("reflex_prompt_ndup");
            testSectionDefaultId = DictionaryCache.getIdFromSystemName("test_section_default");
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
        Node                         item;

        if (validate()) {
            selectedBundles = new ArrayList<ArrayList<Object>>();
            errorsList = new ValidationErrorsList();
            for (i = 0; i < reflexTestTree.getRowCount(); i++) {
                item = reflexTestTree.getNodeAt(i);
                if (item.getType() == "analysis")
                    continue;
                
                if ("Y".equals(item.getCell(2))) {
                    if (item.getCell(1) == null) {
                        errorsList.add(new FormErrorException("reflexTestNeedsSection",
                                                              (String)item.getCell(0)));
                    } else {
                        selectedRow = new ArrayList<Object>();
                        selectedRow.add(item.getParent().getData());
                        selectedRow.add((ResultViewDO)((ArrayList<Object>)item.getData()).get(0));
                        selectedRow.add((TestReflexViewDO)((ArrayList<Object>)item.getData()).get(1));
                        selectedRow.add(item.getCell(1));
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
    private Node getTreeModel() {
        int                                i, j, k, anaReflexCount;
        Integer                            defaultId;
        ArrayList<Object>                  reflexBundle, dataObject;
        ArrayList<Item<Integer>>           sModel;
        ArrayList<TestReflexViewDO>        reflexList;
        Node                               root;
        HashMap<Integer,TestSectionViewDO> sections;
        HashMap<ResultViewDO,ArrayList<TestReflexViewDO>> reflexMap;
        Iterator<ResultViewDO>             iter;
        Item<Integer>                      sRow;
        Node                               aNode, rtNode;
        AnalysisViewDO                     anDO;
        ResultViewDO                       rVDO;
        SampleDataBundle                   bundle;
        SampleDO                           sDO;
        SampleItemManager                  siMan;
        TestManager                        tMan;
        TestReflexViewDO                   reflexDO;
        TestSectionManager                 tsMan;
        TestSectionViewDO                  tsVDO;

        root = new Node();
        if (reflexBundles == null)
            return root;

        sections = new HashMap<Integer,TestSectionViewDO>();
        try {
            sModel = new ArrayList<Item<Integer>>();
            sModel.add(new Item<Integer>(null, ""));
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

                aNode = new Node(3);
                aNode.setType("analysis");
                aNode.setOpen(true);
                aNode.setKey(anDO.getId());
                aNode.setCell(0,sDO.getAccessionNumber()+": "+anDO.getTestName()+", "+anDO.getMethodName());
                aNode.setData(bundle);

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
                        
                        rtNode = new Node(3);
                        rtNode.setType("reflexTest");
                        rtNode.setKey(reflexDO.getAddTestId());
                        rtNode.setCell(0,reflexDO.getAddTestName()+", "+reflexDO.getAddMethodName());
                        if (autoAddId.equals(reflexDO.getFlagsId()) || autoAddNonDupId.equals(reflexDO.getFlagsId()))
                            rtNode.setCell(2,"Y");
                        else
                            rtNode.setCell(2,"N");
                        
                        defaultId = null;
                        try {
                            tMan = TestManager.fetchById(reflexDO.getAddTestId());
                            tsMan = tMan.getTestSections();
                            for (k = 0; k < tsMan.count(); k++) {
                                tsVDO = tsMan.getSectionAt(k);
                                if (testSectionDefaultId.equals(tsVDO.getFlagId()))
                                    defaultId = tsVDO.getSectionId();
                                if (!sections.containsKey(tsVDO.getSectionId())) {
                                    sRow = new Item<Integer>(1);
                                    sRow.setKey(tsVDO.getSectionId());
                                    sRow.setCell(0,tsVDO.getSection());
                                    sRow.setData(tsVDO);
                                    sModel.add(sRow);
                                    sections.put(tsVDO.getSectionId(), tsVDO);
                                }
                            }
                    
                            if (defaultId != null)
                                rtNode.setCell(1,defaultId);
    
                            dataObject = new ArrayList<Object>();
                            dataObject.add(rVDO);
                            dataObject.add(reflexDO);
                            dataObject.add(tsMan);
                            rtNode.setData(dataObject);
                            
                            aNode.add(rtNode);
                            anaReflexCount++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                if (anaReflexCount > 0)
                    root.add(aNode);
            }
            ((Dropdown<Integer>)reflexTestTree.getNodeDefinitionAt("reflexTest",1).getCellEditor().getWidget()).setModel(sModel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return root;
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