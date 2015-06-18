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

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.panel1.client.PanelService1Impl;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeGetMatchesEvent;
import org.openelis.ui.event.BeforeGetMatchesHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
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

    protected Screen                                 parentScreen;

    protected SampleItemAnalysisTreeTabUI            screen;

    protected SampleItemPopoutLookupUI               sampleItemPopout;

    protected EventBus                               parentBus;

    protected Confirm                                cancelAnalysisConfirm;

    protected SampleManager1                         manager;

    protected boolean                                canEdit;

    protected static final String                    SAMPLE_ITEM_LEAF = "sampleItem",
                    ANALYSIS_LEAF = "analysis";

    public SampleItemAnalysisTreeTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        screen = this;

        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? addItemButton : popoutTreeButton;
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                boolean enable;
                String uid;
                AnalysisViewDO a;
                SelectionEvent selEvent;
                Node node;

                node = tree.getNodeAt(event.getSelectedItem());
                uid = node.getData();
                enable = false;
                selEvent = null;

                if (SAMPLE_ITEM_LEAF.equals(node.getType())) {
                    if (canEdit && isState(ADD, UPDATE))
                        enable = !node.hasChildren();
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
                    parentBus.fireEvent(selEvent);
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        tree.addNodeAddedHandler(new NodeAddedHandler() {
            public void onNodeAdded(NodeAddedEvent event) {
                String uid;
                Node node;
                SampleItemViewDO item;

                node = event.getNode();

                if (SAMPLE_ITEM_LEAF.equals(node.getType())) {
                    item = manager.item.add();
                    uid = Constants.uid().get(item);
                    node.setData(uid);
                    setItemDisplay(node, item);
                    parentBus.fireEventFromSource(new SampleItemAddedEvent(uid), screen);
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

                /*
                 * only sample items are removed in the front-end; analyses are
                 * removed in the back-end and the tree has to be reloaded in
                 * that case
                 */
                if (SAMPLE_ITEM_LEAF.equals(row.getType())) {
                    item = (SampleItemViewDO)manager.getObject(uid);
                    manager.item.remove(item);
                }
            }
        });

        addScreenHandler(addItemButton, "addItem", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addItemButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? test : tree;
            }
        });

        /*
         * this is for being able to set focus to the button for adding sample
         * items using Ctrl+'i'
         */
        parentScreen.addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                addItemButton.setFocus(true);
            }
        }, 'i', CTRL);

        addScreenHandler(test, "test", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                test.setValue(null, null);
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                TestMethodVO data;
                AutoCompleteValue value;

                value = event.getValue();
                if (value != null) {
                    data = (TestMethodVO)value.getData();
                    addAnalysis(data.getTestId(), data.getMethodId());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                test.setEnabled(canEdit && isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? removeRowButton : addItemButton;
            }
        });

        /*
         * this is for being able to set focus in the autocomplete for adding
         * tests using Ctrl+'t'
         */
        parentScreen.addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                test.setFocus(isState(ADD, UPDATE));
            }
        }, 't', CTRL);

        test.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                Integer typeId, addId, sepIndex;
                String match, flag;
                SampleItemViewDO item;
                DictionaryDO dict;
                TestManager tm;
                Node node;

                /*
                 * to add a test, a sample item must be selected
                 */
                if (tree.getSelectedNode() == -1) {
                    parentScreen.setError(Messages.get().sample_sampleItemSelectedToAddAnalysis());
                    event.cancel();
                    /*
                     * clear the text entered in the autocomplete
                     */
                    test.setValue(new AutoCompleteValue());
                    return;
                }

                node = tree.getNodeAt(tree.getSelectedNode());
                if ( !SAMPLE_ITEM_LEAF.equals(node.getType()))
                    node = node.getParent();

                item = (SampleItemViewDO)manager.getObject((String)node.getData());

                match = event.getMatch();
                /*
                 * check to see if the user is trying to enter a barcode
                 */
                if (match.matches("[tp][0-9]*\\-[0-9]*")) {
                    flag = match.substring(0, 1);
                    sepIndex = match.indexOf("-");
                    addId = Integer.valueOf(match.substring(1, sepIndex));
                    typeId = Integer.valueOf(match.substring(sepIndex + 1));
                    try {
                        if (item.getTypeOfSampleId() == null) {
                            dict = DictionaryCache.getById(typeId);
                            item.setTypeOfSampleId(dict.getId());
                            item.setTypeOfSample(dict.getEntry());
                        }
                        event.cancel();
                        /*
                         * Clear the text entered in the autocomplete. The value
                         * needs to be set to something not null to make sure
                         * that when the autocomplete compares the previous
                         * value, which is null in the case of scanning a
                         * barcode, with this one, the display gets refreshed.
                         */
                        test.setValue(new AutoCompleteValue());

                        if ("t".equals(flag)) {
                            tm = getTestManager(addId);
                            addAnalysis(addId, tm.getTest().getMethodId());
                        } else if ("p".equals(flag)) {
                            addAnalysis(addId, null);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
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

                node = tree.getNodeAt(tree.getSelectedNode());
                if ( !SAMPLE_ITEM_LEAF.equals(node.getType()))
                    node = node.getParent();

                item = (SampleItemViewDO)manager.getObject((String)node.getData());

                if (item.getTypeOfSampleId() == null) {
                    parentScreen.setError(Messages.get().sample_sampleItemTypeRequired());
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

                    parentScreen.setBusy();

                    tests = PanelService1Impl.INSTANCE.fetchByNameSampleTypeWithTests(query);

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

                parentScreen.clearStatus();
            }
        });

        addScreenHandler(removeRowButton, "removeRow", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? popoutTreeButton : test;
            }
        });

        addScreenHandler(popoutTreeButton, "popoutTree", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                popoutTreeButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : removeRowButton;
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        parentBus.addHandler(SampleItemAddedEvent.getType(), new SampleItemAddedEvent.Handler() {
            public void onSampleItemAdded(SampleItemAddedEvent event) {
                if (screen != event.getSource())
                    sampleItemAdded(event.getUid());
            }
        });

        parentBus.addHandler(SampleItemChangeEvent.getType(), new SampleItemChangeEvent.Handler() {
            public void onSampleItemChange(SampleItemChangeEvent event) {
                sampleItemChanged(event.getUid(), event.getAction());
            }
        });

        parentBus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            @Override
            public void onAddTest(AddTestEvent event) {
                if (event.getSource() != screen)
                    testsAdded(event.getTests());
            }
        });

        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            public void onAnalysisChange(AnalysisChangeEvent event) {
                analysisChanged(event);
            }
        });

        parentBus.addHandler(RemoveAnalysisEvent.getType(), new RemoveAnalysisEvent.Handler() {
            @Override
            public void onAnalysisRemove(RemoveAnalysisEvent event) {
                if (event.getSource() != screen)
                    onDataChange();
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        fireDataChange();
        /*
         * clear the tabs showing the data related to the nodes in the tree e.g.
         * sample item, analysis or results
         */
        parentBus.fireEvent(new SelectionEvent(SelectedType.NONE, null));
    }

    /**
     * If a node is selected in the tree then returns the uid of the record that
     * it is showing, otherwise returns null
     */
    public String getSelectedUid() {
        Node node;

        if (tree.getSelectedNode() > -1) {
            node = tree.getNodeAt(tree.getSelectedNode());
            return (String)node.getData();
        }

        return null;
    }

    @UiHandler("addItemButton")
    protected void addItem(ClickEvent event) {
        Node node;
        
        node = new Node(2);
        node.setType(SAMPLE_ITEM_LEAF);
        node.setOpen(true);
        tree.addNode(node);
        tree.refreshNode(node);
        tree.selectNodeAt(node);
        com.google.gwt.event.logical.shared.SelectionEvent.fire(tree, tree.getSelectedNode());
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        String uid;
        Node node;
        AnalysisViewDO ana;
        
        node = tree.getNodeAt(tree.getSelectedNode());
        if (SAMPLE_ITEM_LEAF.equals(node.getType())) {
            /*
             * since this button is only enabled if the sample item doesn't have
             * any analyses linked to it, this node can be removed without doing
             * any other checks
             */
            tree.removeNode(node);
            parentBus.fireEvent(new SelectionEvent(SelectedType.NONE, null));
        } else {
            uid = (String)node.getData();
            ana = (AnalysisViewDO)manager.getObject(uid);

            if (ana.getId() > 0) {
                /*
                 * existing analyses cannot be removed, only cancelled
                 */
                if (cancelAnalysisConfirm == null) {
                    cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                        Messages.get().analysis_cancelCaption(),
                                                        Messages.get().analysis_cancelMessage(),
                                                        Messages.get().gen_no(),
                                                        Messages.get().gen_yes());
                    cancelAnalysisConfirm.setWidth("300px");
                    cancelAnalysisConfirm.setHeight("150px");
                    cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                        public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                            String uid;
                            Node node;

                            switch (event.getSelectedItem().intValue()) {
                                case 1:
                                    /*
                                     * the uid is obtained here again to make
                                     * sure that the event fired below has the
                                     * uid of the currently selected analysis
                                     * and not of the analysis for which the
                                     * event was fired for the first time after
                                     * creating this popup
                                     */
                                    node = tree.getNodeAt(tree.getSelectedNode());
                                    uid = (String)node.getData();

                                    parentBus.fireEvent(new AnalysisChangeEvent(uid,
                                                                                Constants.dictionary().ANALYSIS_CANCELLED,
                                                                                AnalysisChangeEvent.Action.STATUS_CHANGED));
                                    break;
                            }
                        }
                    });
                }

                cancelAnalysisConfirm.show();
            } else {
                /*
                 * remove this analysis as it is not an existing one; isBusy is
                 * not set here because it's only used for events fired when an
                 * editable widget loses focus
                 */
                parentBus.fireEventFromSource(new RemoveAnalysisEvent(uid), this);
            }
        }

    }

    @UiHandler("popoutTreeButton")
    protected void popoutTree(ClickEvent event) {
        ModalWindow modal;
        
        if (sampleItemPopout == null) {
            sampleItemPopout = new SampleItemPopoutLookupUI() {
                @Override
                public void ok() {
                    /*
                     * reload the tree if its data could have changed
                     */
                    if (canEdit && isState(ADD, UPDATE))
                        onDataChange();
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("500px", "500px");
        modal.setName(Messages.get().itemsAndAnalyses());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(sampleItemPopout);

        sampleItemPopout.setWindow(modal);
        sampleItemPopout.setData(manager, state);
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
    }

    private Node getRoot() {
        int i, j;
        boolean validate;
        Node root, inode, anode;
        AnalysisViewDO ana;
        SampleItemViewDO item;

        root = new Node();
        if (manager == null)
            return root;

        /*
         * this prevents any problems with trying to show the errors that were
         * added to the previous nodes and not to the latest ones added below
         */
        tree.clearExceptions();

        /*
         * If the tree is reloaded in add or update state then it could mean
         * that a panel was added or an order was loaded etc. In those and other
         * situations, the sample type for analyses needs to be validated.
         */
        validate = canEdit && isState(ADD, UPDATE);

        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            inode = new Node(1);
            inode.setType(SAMPLE_ITEM_LEAF);
            inode.setOpen(true);
            inode.setData(Constants.uid().get(item));

            setItemDisplay(inode, item);

            root.add(inode);

            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);

                anode = new Node(1);
                anode.setType(ANALYSIS_LEAF);

                setAnalysisDisplay(anode, ana);

                anode.setData(Constants.uid().get(ana));

                if (validate)
                    validateSampleType(anode, item.getTypeOfSampleId());

                inode.add(anode);
            }
        }

        return root;
    }

    private void addAnalysis(Integer addId, Integer methodId) {
        Node node;
        SampleItemViewDO item;
        SampleTestRequestVO test;
        ArrayList<SampleTestRequestVO> tests;

        /*
         * find the sample item to get its sample type
         */
        node = tree.getNodeAt(tree.getSelectedNode());

        if ( !SAMPLE_ITEM_LEAF.equals(node.getType()))
            node = node.getParent();

        item = (SampleItemViewDO)manager.getObject((String)node.getData());

        tests = new ArrayList<SampleTestRequestVO>();
        if (methodId != null)
            test = new SampleTestRequestVO(item.getSampleId(),
                                           item.getId(),
                                           addId,
                                           null,
                                           null,
                                           null,
                                           null,
                                           false,
                                           null);
        else
            test = new SampleTestRequestVO(item.getSampleId(),
                                           item.getId(),
                                           null,
                                           null,
                                           null,
                                           null,
                                           addId,
                                           false,
                                           null);

        tests.add(test);
        parentBus.fireEventFromSource(new AddTestEvent(tests), this);
    }

    private void testsAdded(ArrayList<SampleTestRequestVO> tests) {
        String itemUid, anaUid;
        Node node, child;

        /*
         * this reloads the tab
         */
        fireDataChange();

        /*
         * this is for finding the sample item to which the tests were requested
         * to be added
         */
        itemUid = Constants.uid().getSampleItem(tests.get(0).getSampleItemId());

        /*
         * select the last child of the sample item's node, because that child
         * shows the analysis added most recently; if the item's node doesn't
         * have any children, because no analyses were added to it, then select
         * the node itself
         */
        anaUid = null;
        for (int i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            node = tree.getRoot().getChildAt(i);
            if (itemUid.equals(node.getData())) {
                if (node.hasChildren()) {
                    child = node.getLastChild();
                    anaUid = child.getData();
                    tree.selectNodeAt(child);
                } else {
                    tree.selectNodeAt(node);
                }
                removeRowButton.setEnabled(true);
                break;
            }
        }

        /*
         * notify the other parts of the main screen that depend on the
         * selection in the tree that a sample item or analysis is selected
         */
        if (anaUid != null)
            parentBus.fireEvent(new SelectionEvent(SelectedType.ANALYSIS, anaUid));
        else
            parentBus.fireEvent(new SelectionEvent(SelectedType.SAMPLE_ITEM, itemUid));
    }

    private void sampleItemAdded(String uid) {
        Node node;
        SampleItemViewDO item;

        for (int i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            /*
             * find the node showing the sample item that was added using the
             * uid and refresh its display
             */
            node = tree.getRoot().getChildAt(i);
            if (uid.equals(node.getData())) {
                item = (SampleItemViewDO)manager.getObject(uid);
                setItemDisplay(node, item);
                break;
            }
        }
    }

    private void sampleItemChanged(String itemUid, SampleItemChangeEvent.Action action) {
        int i, j;
        Node parent, node;
        SampleItemViewDO item;

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

                /*
                 * show a warning if the new sample type isn't valid for an
                 * analysis linked to this sample item
                 */
                for (j = 0; j < parent.getChildCount(); j++ ) {
                    node = parent.getChildAt(j);
                    validateSampleType(node, item.getTypeOfSampleId());
                }

                break;
            }
        }
    }

    private void analysisChanged(AnalysisChangeEvent event) {
        int i, j;
        boolean found;
        String uid;
        Node parent, node;
        AnalysisViewDO ana;

        /*
         * if an analysis' status or prep test changed then some other analyses
         * may have been affected too, so the tree needs to be reloaded
         */
        if (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) ||
            AnalysisChangeEvent.Action.PREP_CHANGED.equals(event.getAction()))
            fireDataChange();

        found = false;
        uid = event.getUid();

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
                    tree.selectNodeAt(node);
                    found = true;
                    break;
                }
            }

            if (found)
                break;
        }
    }

    private void setItemDisplay(Node node, SampleItemViewDO item) {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append(item.getItemSequence());
        if (item.getTypeOfSample() != null) {
            sb.append(" - ");
            sb.append(item.getTypeOfSample());
        }
        if (item.getContainer() != null) {
            sb.append(" [");
            sb.append(item.getContainer());
            sb.append("]");
        }
        node.setCell(0, sb.toString());
    }

    private void setAnalysisDisplay(Node node, AnalysisViewDO ana) {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append(ana.getTestName());
        sb.append(", ");
        sb.append(ana.getMethodName());
        if (ana.getStatusId() != null) {
            try {
                sb.append(" [");
                sb.append(DictionaryCache.getById(ana.getStatusId()).getEntry());
                sb.append("]");
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        node.setCell(0, sb.toString());
    }

    /**
     * Goes through the children of the node showing the item and adds a warning
     * to a child if the unit assigned to its analysis isn't valid for the
     * sample item's sample type.
     */
    private void validateSampleType(Node node, Integer typeId) {
        String uid;
        AnalysisViewDO ana;
        TestManager tm;

        uid = node.getData();
        ana = (AnalysisViewDO)manager.getObject(uid);

        if (typeId == null || Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return;

        try {
            tm = getTestManager(ana.getTestId());
            if (ana.getUnitOfMeasureId() != null &&
                !tm.getSampleTypes().hasUnit(ana.getUnitOfMeasureId(), typeId))
                tree.addException(node,
                                  0,
                                  new FormErrorWarning(Messages.get()
                                                               .analysis_unitInvalidForSampleType()));
            else
                tree.clearExceptions(node, 0);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
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