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

    protected Tree                       prepTestTree;

    private Integer                      testSectionDefaultId;
    private ArrayList<ArrayList<Object>> prepBundles;
    private Button                       copyToEmptyButton, copyToAllButton;

    public TestPrepLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestPrepLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        initializeDropdowns();
    }

    private void initialize() {
        prepTestTree = (Tree)def.getWidget("prepTestTree");
        addScreenHandler(prepTestTree, new ScreenEventHandler<Node>() {
            public void onDataChange(DataChangeEvent event) {
                prepTestTree.setRoot(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepTestTree.setEnabled(true);
            }
        });

        prepTestTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                boolean      enable;
                Node selection;

                selection = prepTestTree.getNodeAt(event.getSelectedItem());
                if (selection != null && "prepTest".equals(selection.getType()))
                    enable = true;
                else
                    enable = false;

                copyToEmptyButton.setEnabled(enable);
                copyToAllButton.setEnabled(enable);
            }
        });

        prepTestTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @SuppressWarnings("unchecked")
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int                      i, j;
                ArrayList<Item<Integer>> model;
                Item<Integer>            tsRow;
                Node                     row;
                TestSectionManager       tsMan;
                TestSectionViewDO        tsVDO;
                
                row = prepTestTree.getNodeAt(event.getRow());
                if (row.getType() == "prepTest") {
                    if (event.getCol() == 1) {
                        //
                        // Since the dropdown contains the list of all sections
                        // across the list of prep tests, we need to enable/disable
                        // the appropriate ones for the prep test in the selected
                        // row
                        //
                        model = ((Dropdown<Integer>)prepTestTree.getNodeDefinitionAt("prepTest",1).getCellEditor().getWidget()).getModel();
                        tsMan = (TestSectionManager) ((ArrayList<Object>)row.getData()).get(1);
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
                        // If the prep test is NOT optional, we need to prevent
                        // the user from unchecking this item
                        //
                        if ("N".equals(((TestPrepViewDO)((ArrayList<Object>)row.getData()).get(0)).getIsOptional()))
                            event.cancel();
                    } else {
                        event.cancel();
                    }
                } else {
                    event.cancel();
                }
            }
        });

        prepTestTree.addBeforeNodeCloseHandler(new BeforeNodeCloseHandler() {
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

        final Button cancelButton = (Button)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.setEnabled(true);
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
                
                prepTestTree.finishEditing();
                selection = prepTestTree.getNodeAt(prepTestTree.getSelectedNode());
                sectionId = (Integer) ((ArrayList<Object>)selection.getCell(1)).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < prepTestTree.getRowCount(); i++) {
                        item = prepTestTree.getNodeAt(i);
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
                                    //prepTestTree.refresh(i, 1);
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
                
                prepTestTree.finishEditing();
                selection = prepTestTree.getNodeAt(prepTestTree.getSelectedNode());
                sectionId = (Integer) ((ArrayList<Object>)selection.getCell(1)).get(0);
                if (sectionId == null) {
                    Window.alert("Cannot copy blank section");
                } else {
                    for (i = 0; i < prepTestTree.getRowCount(); i++) {
                        item = prepTestTree.getNodeAt(i);
                        if (item.getType() == "reflexTest") {
                            tsMan = (TestSectionManager) ((ArrayList<Object>)item.getData()).get(2);
                            for (j = 0; j < tsMan.count(); j++) {
                                tsVDO = tsMan.getSectionAt(j);
                                if (sectionId.equals(tsVDO.getSectionId())) {
                                    item.setCell(1,sectionId);
                                    //prepTestTree.refresh(i, 1);
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
            testSectionDefaultId = DictionaryCache.getIdFromSystemName("test_section_default");
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
        Node                         item;

        if (validate()) {
            selectedBundles = new ArrayList<ArrayList<Object>>();
            errorsList = new ValidationErrorsList();
            for (i = 0; i < prepTestTree.getRowCount(); i++) {
                item = prepTestTree.getNodeAt(i);
                if (item.getType() == "analysis")
                    continue;
                if (!addPrepTestToSelection(item, (Object)((ArrayList<Object>)item.getData()).get(0),
                                            selectedBundles, errorsList,i))
                    errorsList.add(new FormErrorException("prepTestRequiredForTestException",
                                                          (String)item.getCell(0)));
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
    private Node getTreeModel() {
        int                                i, j;
        ArrayList<Object>                  prepBundle;
        ArrayList<Item<Integer>>           sModel;
        Node                               root;
        HashMap<Integer,TestSectionViewDO> sections;
        Node                               node;
        AnalysisViewDO                     anDO;
        SampleDataBundle                   bundle;
        TestPrepManager                    tpMan;
        TestPrepViewDO                     tpVDO;

        root = new Node();
        if (prepBundles == null)
            return root;

        sections = new HashMap<Integer,TestSectionViewDO>();
        try {
            sModel = new ArrayList<Item<Integer>>();
            for (i = 0; i < prepBundles.size(); i++) {
                prepBundle = prepBundles.get(i);
                if (prepBundle.size() != 2)
                    continue;
                
                bundle = (SampleDataBundle) prepBundle.get(0);
                tpMan = (TestPrepManager) prepBundle.get(1);
                anDO = bundle.getSampleManager().getSampleItems()
                             .getAnalysisAt(bundle.getSampleItemIndex())
                             .getAnalysisAt(bundle.getAnalysisIndex());
                
                node = new Node(3);
                node.setType("analysis");
                node.setOpen(true);
                node.setKey(anDO.getId());
                node.setCell(0,anDO.getTestName()+", "+anDO.getMethodName());
                node.setData(prepBundle);
                
                tpVDO = tpMan.getRequiredTestPrep();
                if (tpVDO != null) {
                    addPrepItem(node, tpVDO, sections, sModel);
                } else {
                    for (j = 0; j < tpMan.count(); j++) {
                        tpVDO = tpMan.getPrepAt(j);
                        addPrepItem(node, tpVDO, sections, sModel);
                    }
                }
                root.add(node);
            }
            ((Dropdown<Integer>)prepTestTree.getNodeDefinitionAt("prepTest",1).getCellEditor().getWidget()).setModel(sModel);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return root;
    }
    
    private void addPrepItem(Node parentItem, TestPrepViewDO tpVDO,
                             HashMap<Integer,TestSectionViewDO> sections, ArrayList<Item<Integer>> sModel) {
        int                i;
        Integer            defaultId;
        ArrayList<Object>  dataObject;
        Item<Integer>      sRow;
        Node               node;
        TestManager        tMan;
        TestPrepManager    tpMan;
        TestPrepViewDO     tpVDO2;
        TestSectionManager tsMan;
        TestSectionViewDO  tsVDO;

        node = new Node(3);
        node.setType("prepTest");
        node.setKey(tpVDO.getPrepTestId());
        node.setCell(0,tpVDO.getPrepTestName()+", "+tpVDO.getMethodName());
        if ("N".equals(tpVDO.getIsOptional()))
            node.setCell(2,"Y");
        else
            node.setCell(2,"N");

        defaultId = null;
        try {
            tMan = TestManager.fetchById(tpVDO.getPrepTestId());
            tsMan = tMan.getTestSections();
            for (i = 0; i < tsMan.count(); i++) {
                tsVDO = tsMan.getSectionAt(i);
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
                node.setCell(1,defaultId);
    
            tpMan = tMan.getPrepTests();
            if (tpMan.count() > 0) {
                node.setOpen(true);
                tpVDO2 = tpMan.getRequiredTestPrep();
                if (tpVDO2 != null) {
                    addPrepItem(node, tpVDO2, sections, sModel);
                } else {
                    for (i = 0; i < tpMan.count(); i++) {
                        tpVDO2 = tpMan.getPrepAt(i);
                        addPrepItem(node, tpVDO2, sections, sModel);
                    }
                }
            }
            
            dataObject = new ArrayList<Object>(2);
            dataObject.add(tpVDO);
            dataObject.add(tsMan);
            node.setData(dataObject);
            
            parentItem.add(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean addPrepTestToSelection(Node parentItem, Object parentBundle,
                                           ArrayList<ArrayList<Object>> selectedBundles,
                                           ValidationErrorsList errorsList, int index) {
        int               depth;
        ArrayList<Object> selectedRow;
        Node              item;

        depth = parentItem.getLevel() + 1;
        for (index++; index < prepTestTree.getRowCount(); index++) {
            item = prepTestTree.getNodeAt(index);
            if (item.getLevel() < depth) {
                return false;
            } else if (item.getLevel() == depth && "Y".equals(item.getCell(2))) {
                if (item.getCell(1) == null) {
                    errorsList.add(new FormErrorException("prepTestNeedsSection",
                                                          (String)item.getCell(0)));
                    break;
                } else {
                    selectedRow = new ArrayList<Object>(3);
                    selectedRow.add(parentBundle);
                    selectedRow.add(((TestPrepViewDO)((ArrayList<Object>)item.getData()).get(0)).getPrepTestId());
                    selectedRow.add(item.getCell(1));
                    selectedBundles.add(selectedRow);
                    if (item.hasChildren())
                        return addPrepTestToSelection(item, selectedRow, selectedBundles,
                                                      errorsList, index);
                    else
                        return true;
                }
            }
        }
        return false;
    }

    public void setBundles(ArrayList<ArrayList<Object>> bundles) {
        prepBundles = bundles;

        DataChangeEvent.fire(this);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}