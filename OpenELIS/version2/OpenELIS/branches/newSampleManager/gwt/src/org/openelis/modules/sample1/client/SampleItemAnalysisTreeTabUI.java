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
package org.openelis.modules.sample1.client;

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.sample1.client.AddTestEvent.AddType;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.NodeAddedEvent;
import org.openelis.ui.widget.tree.event.NodeAddedHandler;
import org.openelis.ui.widget.tree.event.NodeDeletedEvent;
import org.openelis.ui.widget.tree.event.NodeDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SampleItemAnalysisTreeTabUI extends Screen {

    @UiTemplate("SampleItemAnalysisTreeTab.ui.xml")
    interface SampleItemAnalysisTreeTabUIBinder extends
                                               UiBinder<Widget, SampleItemAnalysisTreeTabUI> {
    };

    private static SampleItemAnalysisTreeTabUIBinder uiBinder = GWT.create(SampleItemAnalysisTreeTabUIBinder.class);

    @UiField
    protected Tree                                   itemsTestsTree;

    @UiField
    protected Button                                 addItemButton, addAnalysisButton,
                                                      removeRowButton, popoutTreeButton;

    @UiField
    protected Dropdown<Integer>                      analysisStatus;

    protected Screen                                 screen, parentScreen;

    protected SampleManager1                         manager;

    protected TestLookupUI                           testLookup;

    protected boolean                               canEdit;

    public SampleItemAnalysisTreeTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        
        screen = this;

        addScreenHandler(itemsTestsTree, "itemsTestsTree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemsTestsTree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                itemsTestsTree.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        itemsTestsTree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                boolean enable;
                String uid;
                AnalysisViewDO a;
                SelectionEvent selEvent;
                Node selection;

                selection = itemsTestsTree.getNodeAt(event.getSelectedItem());
                uid = selection.getData();
                enable = false;
                selEvent = null;

                if ("sampleItem".equals(selection.getType())) {
                    if (canEdit && isState(ADD, UPDATE))
                        enable = !selection.hasChildren();
                    if (uid != null)
                        selEvent = new SelectionEvent(SelectedType.SAMPLE_ITEM, uid);
                } else if (uid != null) {
                    a = (AnalysisViewDO)manager.getObject(uid);

                    if (canEdit && isState(ADD, UPDATE))
                        enable = ( !Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId()) && !Constants.dictionary().ANALYSIS_RELEASED.equals(a.getStatusId()));

                    selEvent = new SelectionEvent(SelectedType.ANALYSIS, uid);
                }

                removeRowButton.setEnabled(enable);

                if (selEvent != null)
                    bus.fireEvent(selEvent);
            }
        });

        itemsTestsTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        itemsTestsTree.addNodeAddedHandler(new NodeAddedHandler() {
            public void onNodeAdded(NodeAddedEvent event) {
                String uid;
                Node node, parent;
                DictionaryDO dict;
                SampleItemViewDO item;

                node = event.getNode();

                if ("sampleItem".equals(node.getType())) {
                    item = manager.item.add();
                    /*
                     * if the domain of the sample is sdwis then we set the
                     * sample type for the newly added item to "Drinking Water"
                     */
                    if (Constants.domain().SDWIS.equals(manager.getSample().getDomain())) {
                        try {
                            dict = DictionaryCache.getBySystemName("drinking_water");
                            item.setTypeOfSampleId(dict.getId());
                            item.setTypeOfSample(dict.getEntry());
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                        }
                    }
                    node.setData(manager.getUid(item));
                    setItemDisplay(node, item);
                } else if ("analysis".equals(node.getType())) {
                    parent = node.getParent();
                    uid = (String)parent.getData();
                    item = (SampleItemViewDO)manager.getObject(uid);
                    // ana = manager.item. analysis.add(item);
                    // addedIndex = anMan.addAnalysis();
                    // node.setData(manager.getUid(ana));

                    // treeUtility.updateAnalysisRow(addedRow);
                    // itemsTestsTree.refreshRow(addedRow);
                }
            }
        });

        itemsTestsTree.addNodeDeletedHandler(new NodeDeletedHandler() {
            public void onNodeDeleted(NodeDeletedEvent event) {
                String uid;
                Node row;
                SampleItemViewDO item;

                row = event.getNode();
                uid = (String)row.getData();

                if ("sampleItem".equals(row.getType())) {
                    item = (SampleItemViewDO)manager.getObject(uid);
                    manager.item.remove(item);
                } else {
                    // ana = (AnalysisViewDO)manager.getObject(uid);
                    // manager.analysis.remove(ana);
                }
            }
        });

        addScreenHandler(addItemButton, "addItem", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addItemButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(addAnalysisButton, "addAnalysis", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addAnalysisButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(removeRowButton, "removeRow", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(popoutTreeButton, "popoutTree", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                popoutTreeButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        // analysis status dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("analysis_status");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        analysisStatus.setModel(model);

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandlerToSource(DataChangeEvent.getType(),
                               parentScreen,
                               new DataChangeEvent.Handler() {
                                   public void onDataChange(DataChangeEvent event) {
                                       evaluateEdit();
                                       fireDataChange();
                                       /*
                                        * clear the tabs showing the data
                                        * related to the nodes in the tree e.g.
                                        * sample item, analysis or results
                                        */
                                       bus.fireEvent(new SelectionEvent(SelectedType.NONE, null));
                                   }
                               });

        bus.addHandler(SampleItemChangeEvent.getType(), new SampleItemChangeEvent.Handler() {
            public void onSampleItemChange(SampleItemChangeEvent event) {
                sampleItemChanged(event.getUid(), event.getAction());
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (AnalysisChangeEvent.Action.TEST_CHANGED.equals(event.getAction()))
                    analysisChanged(event.getUid());
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    @UiHandler("addItemButton")
    protected void addItem(ClickEvent event) {
        Node node;

        node = new Node(2);
        node.setType("sampleItem");
        node.setOpen(true);
        itemsTestsTree.addNode(node);
        itemsTestsTree.refreshNode(node);
        itemsTestsTree.selectNodeAt(node);
        com.google.gwt.event.logical.shared.SelectionEvent.fire(itemsTestsTree,
                                                                itemsTestsTree.getSelectedNode());
    }

    @UiHandler("addAnalysisButton")
    protected void addAnalysis(ClickEvent event) {
        Node inode;
        ModalWindow modal;
        final SampleItemViewDO item;

        if (itemsTestsTree.getSelectedNode() == -1 && itemsTestsTree.getRowCount() > 1) {
            parentScreen.getWindow().setError(Messages.get().sampleItemSelectedToAddAnalysis());
            return;
        }

        /*
         * add a sample item to the manager if there none present
         */
        if (itemsTestsTree.getSelectedNode() == -1) {
            if (itemsTestsTree.getRowCount() == 0)
                addItem(null);
            else
                itemsTestsTree.selectNodeAt(0);
        }

        /*
         * find the sample item to get its sample type
         */
        inode = itemsTestsTree.getNodeAt(itemsTestsTree.getSelectedNode());

        if ( !"sampleItem".equals(inode.getType()))
            inode = inode.getParent();

        item = (SampleItemViewDO)manager.getObject((String)inode.getData());

        if (testLookup == null) {
            testLookup = new TestLookupUI() {
                @Override
                public void ok() {
                    AddTestEvent.AddType type;
                    
                    if (testLookup.getTest().getMethodId() != null)
                        type = AddType.TEST;
                    else
                        type = AddType.PANEL;
                    
                    screen.getEventBus().fireEvent(new AddTestEvent(type,
                                                   item.getId(),
                                                   testLookup.getSampletypeId(),
                                                   testLookup.getTest().getTestId()));
                }

                @Override
                public void cancel() {
                    //ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("275px", "150px");
        modal.setName(Messages.get().sample_testLookup());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testLookup);

        testLookup.setSampleTypeId(item.getTypeOfSampleId());
        testLookup.setWindow(modal);
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        if (itemsTestsTree.getSelectedNode() == -1)
            removeRowButton.setEnabled(false);
    }

    @UiHandler("popoutTreeButton")
    protected void popoutTree(ClickEvent event) {
    }

    private Node getRoot() {
        int i, j;
        Node root, inode, anode;
        AnalysisViewDO ana;
        SampleItemViewDO item;

        root = new Node();
        if (manager == null)
            return root;

        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            inode = new Node(2);
            inode.setType("sampleItem");
            inode.setOpen(true);
            inode.setData(manager.getUid(item));
            setItemDisplay(inode, item);
            root.add(inode);

            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);

                anode = new Node(2);
                anode.setType("analysis");
                anode.setData(manager.getUid(ana));
                setAnalysisDisplay(anode, ana);
                inode.add(anode);
            }
        }

        return root;
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null)
            canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                            .getStatusId());
    }

    private void sampleItemChanged(String uid, SampleItemChangeEvent.Action action) {
        Node node;
        SampleItemViewDO item;

        node = itemsTestsTree.getNodeAt(itemsTestsTree.getSelectedNode());
        if ("analysis".equals(node.getType()))
            node = node.getParent();

        item = (SampleItemViewDO)manager.getObject(uid);

        if (SampleItemChangeEvent.Action.CONTAINER_CHANGED.equals(action))
            node.setCell(0, getDisplay(item.getItemSequence(), " - ", item.getContainer()));
        else if (SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED.equals(action))
            node.setCell(1, getDisplay(item.getTypeOfSample()));

        itemsTestsTree.refreshNode(node);
    }

    private void analysisChanged(String uid) {
        Node node;
        AnalysisViewDO item;

        node = itemsTestsTree.getNodeAt(itemsTestsTree.getSelectedNode());

        item = (AnalysisViewDO)manager.getObject(uid);

        node.setCell(0, getDisplay(item.getTestName(), " : ", item.getMethodName()));
        node.setCell(1, item.getStatusId());

        itemsTestsTree.refreshNode(node);
    }

    private void setItemDisplay(Node node, SampleItemViewDO item) {
        node.setCell(0, getDisplay(item.getItemSequence(), " - ", item.getContainer()));
        node.setCell(1, getDisplay(item.getTypeOfSample()));
    }

    private void setAnalysisDisplay(Node node, AnalysisViewDO ana) {
        node.setCell(0, getDisplay(ana.getTestName(), " : ", ana.getMethodName()));
        node.setCell(1, ana.getStatusId());
    }

    /**
     * Creates a display label by delimiting the objects by "delim". A null
     * object is replaced by "<>".
     */
    private String getDisplay(Object o1, String delim, Object o2) {
        List<Object> list;

        list = new ArrayList<Object>();
        list.add(getDisplay(o1));
        list.add(getDisplay(o2));

        return DataBaseUtil.concatWithSeparator(list, delim);
    }

    /**
     * Returns the display label for the object. If the object is null, the
     * label is "<>".
     */
    private Object getDisplay(Object val) {
        if (val == null)
            return "<>";

        return val;
    }
}