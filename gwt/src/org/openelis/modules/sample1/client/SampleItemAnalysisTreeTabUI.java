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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
    protected Tree                                   tree;

    @UiField
    protected Button                                 addItemButton, removeRowButton,
                    popoutTreeButton;

    @UiField
    protected AutoComplete                           test;

    @UiField
    protected Dropdown<Integer>                      analysisStatus;

    protected Screen                                 parentScreen;

    protected SampleItemAnalysisTreeTabUI            screen;

    protected SampleItemPopoutLookupUI               treePopout;

    protected SampleManager1                         manager;

    protected boolean                                canEdit;

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

        addScreenHandler(tree, "itemsTestsTree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                boolean enable;
                String uid;
                AnalysisViewDO a;
                SelectionEvent selEvent;
                Node selection;

                selection = tree.getNodeAt(event.getSelectedItem());
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

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        tree.addNodeAddedHandler(new NodeAddedHandler() {
            public void onNodeAdded(NodeAddedEvent event) {
                Node node;
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
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                    node.setData(manager.getUid(item));
                    setItemDisplay(node, item);
                }
            }
        });

        tree.addNodeDeletedHandler(new NodeDeletedHandler() {
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

        addScreenHandler(test, "testName", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                test.setValue(null, null);
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                addAnalysis(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                test.setEnabled(canEdit && isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : tree;
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer key;
                Query query;
                QueryData field;
                Item<Integer> row;
                Node node;
                SampleItemViewDO item;
                ArrayList<QueryData> fields;
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> tests;

                /*
                 * to add a test, a sample item must be selected and it must
                 * have a sample type
                 */
                if (tree.getSelectedNode() == -1) {
                    parentScreen.getWindow()
                                .setError(Messages.get().sample_sampleItemSelectedToAddAnalysis());
                    return;
                }

                node = tree.getNodeAt(tree.getSelectedNode());
                if ( !"sampleItem".equals(node.getType()))
                    node = node.getParent();

                item = (SampleItemViewDO)manager.getObject((String)node.getData());

                if (item.getTypeOfSampleId() == null) {
                    parentScreen.getWindow().setError(Messages.get()
                                                              .sample_sampleItemTypeRequired());
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();

                try {
                    field = new QueryData();
                    field.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch()) + "%");
                    fields.add(field);

                    field = new QueryData();
                    field.setQuery(String.valueOf(item.getTypeOfSampleId()));
                    fields.add(field);

                    query.setFields(fields);

                    parentScreen.getWindow().setBusy();

                    tests = PanelService.get().fetchByNameSampleTypeWithTests(query);

                    model = new ArrayList<Item<Integer>>();
                    for (TestMethodVO t : tests) {
                        /*
                         * Since the keys of the rows need to be unique and it
                         * can happen that a panel has the same id as a test, a
                         * negative number is used as the key for a row showing
                         * a panel and a positive one for a row showing a test.
                         * An index in a loop can't be used here because it can
                         * clash with an id and two different rows may be
                         * treated as the same.
                         */

                        key = t.getMethodId() == null ? -t.getTestId() : t.getTestId();

                        row = new Item<Integer>(3);
                        row.setKey(key);
                        row.setCell(0, t.getTestName());
                        row.setCell(1, t.getMethodName());
                        row.setCell(2, t.getTestDescription());
                        row.setData(t);

                        model.add(row);
                    }

                    test.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                parentScreen.getWindow().clearStatus();
            }
        });

        addScreenHandler(removeRowButton, "removeRow", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
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
                analysisChanged(event.getUid());
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager))
            this.manager = manager;
    }

    @UiHandler("addItemButton")
    protected void addItem(ClickEvent event) {
        Node node;

        node = new Node(2);
        node.setType("sampleItem");
        node.setOpen(true);
        tree.addNode(node);
        tree.refreshNode(node);
        tree.selectNodeAt(node);
        com.google.gwt.event.logical.shared.SelectionEvent.fire(tree, tree.getSelectedNode());
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        if (tree.getSelectedNode() == -1)
            removeRowButton.setEnabled(false);
    }

    @UiHandler("popoutTreeButton")
    protected void popoutTree(ClickEvent event) {
        ModalWindow modal;

        if (treePopout == null) {
            treePopout = new SampleItemPopoutLookupUI() {
                @Override
                public void ok() {
                    /*
                     * don't reload the tree if the data in the popup couldn't
                     * be changed
                     */
                    if (canEdit && isState(ADD, UPDATE))
                        screen.fireDataChange();
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("500px", "500px");
        modal.setName(Messages.get().itemsAndAnalyses());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(treePopout);

        treePopout.setWindow(modal);
        treePopout.setData(manager, state);
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

    protected void addAnalysis(AutoCompleteValue val) {
        Node node;
        SampleItemViewDO item;
        TestMethodVO data;
        SampleTestRequestVO test;
        ArrayList<SampleTestRequestVO> tests;

        /*
         * find the sample item to get its sample type
         */
        node = tree.getNodeAt(tree.getSelectedNode());

        if ( !"sampleItem".equals(node.getType()))
            node = node.getParent();

        item = (SampleItemViewDO)manager.getObject((String)node.getData());

        tests = new ArrayList<SampleTestRequestVO>();
        data = (TestMethodVO)val.getData();
        if (data.getMethodId() != null)
            test = new SampleTestRequestVO(item.getId(),
                                           data.getTestId(),
                                           null,
                                           null,
                                           null,
                                           null,
                                           false,
                                           null);
        else
            test = new SampleTestRequestVO(item.getId(),
                                           null,
                                           null,
                                           null,
                                           null,
                                           data.getTestId(),
                                           false,
                                           null);

        tests.add(test);
        screen.getEventBus().fireEvent(new AddTestEvent(tests));
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
    }

    private void sampleItemChanged(String itemUid, SampleItemChangeEvent.Action action) {
        int i, j;
        boolean found;
        String anaUid;
        Node parent, node;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        TestManager tm;
        ArrayList<TestTypeOfSampleDO> types;

        for (i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            /*
             * find the node showing the sample item with this uid and refresh
             * its display
             */
            parent = tree.getRoot().getChildAt(i);
            if (itemUid.equals(parent.getData())) {
                item = (SampleItemViewDO)manager.getObject(itemUid);
                setItemDisplay(parent, item);
                tree.refreshNode(parent);

                if ( !SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED.equals(action))
                    break;

                for (j = 0; j < parent.getChildCount(); j++ ) {
                    node = parent.getChildAt(j);
                    anaUid = node.getData();
                    ana = (AnalysisViewDO)manager.getObject(anaUid);

                    try {
                        tm = getTestManager(ana.getTestId());
                        types = tm.getSampleTypes().getTypesBySampleType(item.getTypeOfSampleId());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        return;
                    }

                    /*
                     * show an error in a node, if the test that it is showing,
                     * doesn't have this sample type
                     */
                    found = false;
                    for (TestTypeOfSampleDO t : types) {
                        if (DataBaseUtil.isSame(item.getTypeOfSampleId(), t.getTypeOfSampleId())) {
                            found = true;
                            break;
                        }
                    }

                    if ( !found)
                        tree.addException(node,
                                          0,
                                          new Exception(Messages.get().analysis_sampleTypeInvalid()));
                    else
                        tree.clearExceptions(node, 0);
                }

                break;
            }
        }
    }

    private void analysisChanged(String uid) {
        int i, j;
        boolean found;
        Node parent, node;
        AnalysisViewDO ana;

        found = false;
        for (i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            parent = tree.getRoot().getChildAt(i);
            /*
             * find the node showing the analysis with this uid and refresh its
             * display
             */
            for (j = 0; j < parent.getChildCount(); j++ ) {
                node = parent.getChildAt(j);
                if (uid.equals(node.getData())) {
                    ana = (AnalysisViewDO)manager.getObject(uid);
                    setAnalysisDisplay(node, ana);
                    tree.refreshNode(node);
                    found = true;
                    break;
                }
            }

            if (found)
                break;
        }
    }

    private void setItemDisplay(Node node, SampleItemViewDO item) {
        node.setCell(0, getDisplay(item.getItemSequence(), " - ", item.getContainer()));
        node.setCell(1, getDisplay(item.getTypeOfSample()));
    }

    private void setAnalysisDisplay(Node node, AnalysisViewDO ana) {
        node.setCell(0, getDisplay(ana.getTestName(), " , ", ana.getMethodName()));
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

    /**
     * returns the TestManager for the specified id, from the cache maintained
     * by the parent screen
     */
    private TestManager getTestManager(Integer testId) throws Exception {
        if ( ! (parentScreen instanceof CacheProvider))
            throw new Exception("Parent screen must implement " + CacheProvider.class.toString());

        return ((CacheProvider)parentScreen).get(testId, TestManager.class);
    }
}