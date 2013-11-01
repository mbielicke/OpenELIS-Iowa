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
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeGetMatchesEvent;
import org.openelis.ui.event.BeforeGetMatchesHandler;
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

    protected SampleItemPopoutLookupUI               treePopout;

    protected SampleManager1                         manager;

    protected boolean                                canEdit;

    protected Confirm                                cancelAnalysisConfirm;

    private static final String                      SAMPLE_ITEM_LEAF = "sampleItem",
                    ANALYSIS_LEAF = "analysis";

    public SampleItemAnalysisTreeTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
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

                if (SAMPLE_ITEM_LEAF.equals(selection.getType())) {
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

                if (SAMPLE_ITEM_LEAF.equals(node.getType())) {
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
                    setItemDisplay(node, item, new StringBuffer());
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
                 * this code is only executed for sample items because analyses
                 * are removed in the back-end and the tree has to be reloaded
                 * in that case
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
        });

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
                return forward ? tree : tree;
            }
        });

        test.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                Integer typeId, addId, sepIndex;
                String match, flag;
                SampleItemViewDO item;
                DictionaryDO dict;
                TestManager tm;
                TestViewDO t;
                Node node;

                /*
                 * to add a test, a sample item must be selected
                 */
                if (tree.getSelectedNode() == -1) {
                    parentScreen.getWindow()
                                .setError(Messages.get().sample_sampleItemSelectedToAddAnalysis());
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
                         * need to be set to something not null to make sure
                         * that when the autocomplete compares the previous
                         * value, which is null in the case of scanning a
                         * barcode, with this one, the display gets refreshed.
                         */
                        test.setValue(new AutoCompleteValue());

                        if ("t".equals(flag)) {
                            tm = getTestManager(addId);
                            t = tm.getTest();
                            addAnalysis(addId, t.getMethodId());
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

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            @Override
            public void onAddTest(AddTestEvent event) {
                if (event.getSource() == screen)
                    return;

                fireDataChange();
                /*
                 * clear the tabs showing the data related to the nodes in the
                 * tree e.g. sample item, analysis or results
                 */
                bus.fireEvent(new SelectionEvent(SelectedType.NONE, null));
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            public void onAnalysisChange(AnalysisChangeEvent event) {
                analysisChanged(event);
            }
        });

        bus.addHandler(RemoveAnalysisEvent.getType(), new RemoveAnalysisEvent.Handler() {
            @Override
            public void onAnalysisRemove(RemoveAnalysisEvent event) {
                if (event.getSource() != screen) {
                    fireDataChange();
                    /*
                     * clear the tabs showing the data related to the nodes in
                     * the tree e.g. sample item, analysis or results
                     */
                    bus.fireEvent(new SelectionEvent(SelectedType.NONE, null));
                }
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
        node.setType(SAMPLE_ITEM_LEAF);
        node.setOpen(true);
        tree.addNode(node);
        tree.refreshNode(node);
        tree.selectNodeAt(node);
        com.google.gwt.event.logical.shared.SelectionEvent.fire(tree, tree.getSelectedNode());
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        final String uid;
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
                            switch (event.getSelectedItem().intValue()) {

                                case 1:
                                    bus.fireEvent(new AnalysisChangeEvent(uid,
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
                 * remove this analysis as it is not an existing one
                 */
                bus.fireEventFromSource(new RemoveAnalysisEvent(uid), this);
            }
        }

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
        boolean validate;
        Node root, inode, anode;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        StringBuffer buf;

        root = new Node();
        if (manager == null)
            return root;

        /*
         * If the tree is reloaded in add or update state then it could mean
         * that a panel was added or an order was loaded etc. In those and
         * other situations, the sample type for analyses needs to be validated.
         */
        validate = canEdit && isState(ADD, UPDATE);

        buf = new StringBuffer();
        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            inode = new Node(1);
            inode.setType(SAMPLE_ITEM_LEAF);
            inode.setOpen(true);
            inode.setData(manager.getUid(item));

            buf.setLength(0);
            setItemDisplay(inode, item, buf);

            inode.setData(manager.getUid(item));

            root.add(inode);

            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);

                anode = new Node(1);
                anode.setType(ANALYSIS_LEAF);

                buf.setLength(0);
                setAnalysisDisplay(anode, ana, buf);

                anode.setData(manager.getUid(ana));
                
                if (validate)
                    validateSampleType(anode, item.getTypeOfSampleId());

                inode.add(anode);
            }
        }

        return root;
    }

    protected void addAnalysis(Integer addId, Integer methodId) {
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
            test = new SampleTestRequestVO(item.getId(), addId, null, null, null, null, false, null);
        else
            test = new SampleTestRequestVO(item.getId(), null, null, null, null, addId, false, null);

        tests.add(test);
        screen.getEventBus().fireEventFromSource(new AddTestEvent(tests), this);
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
    }

    private void sampleItemChanged(String itemUid, SampleItemChangeEvent.Action action) {
        int i;
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
                setItemDisplay(parent, item, new StringBuffer());
                tree.refreshNode(parent);

                if ( !SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED.equals(action))
                    break;

                /*
                 * show an error in the nodes showing the analyses linked to
                 * this sample item if the new sample type isn't valid for them
                 */
                for (i = 0; i < parent.getChildCount(); i++ ) {
                    node = parent.getChildAt(i);
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
                    setAnalysisDisplay(node, ana, new StringBuffer());
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

    private void setItemDisplay(Node node, SampleItemViewDO item, StringBuffer buf) {
        buf.append(item.getItemSequence());
        if (item.getTypeOfSample() != null) {
            buf.append(" - ");
            buf.append(item.getTypeOfSample());
        }
        if (item.getContainer() != null) {
            buf.append(" [");
            buf.append(item.getContainer());
            buf.append("]");
        }
        node.setCell(0, buf.toString());
    }

    private void setAnalysisDisplay(Node node, AnalysisViewDO ana, StringBuffer buf) {
        buf.append(ana.getTestName());
        buf.append(", ");
        buf.append(ana.getMethodName());
        if (ana.getStatusId() != null) {
            try {
                buf.append(" (");
                buf.append(DictionaryCache.getById(ana.getStatusId()).getEntry());
                buf.append(")");
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        node.setCell(0, buf.toString());
    }

    /**
     * Goes through the children of the node showing the item and adds an error
     * to a child if the sample type in the sample item isn't valid for the
     * analysis that it's showing.
     */
    private void validateSampleType(Node node, Integer typeId) {
        boolean found;
        String uid;
        AnalysisViewDO ana;
        TestManager tm;
        ArrayList<TestTypeOfSampleDO> types;

        uid = node.getData();
        ana = (AnalysisViewDO)manager.getObject(uid);

        if (Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return;
        
        found = false;
        if (typeId != null) {
            try {
                tm = getTestManager(ana.getTestId());
                types = tm.getSampleTypes().getTypesBySampleType(typeId);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }

            /*
             * show an error in a node, if the test that it is showing, doesn't
             * have this sample type
             */
            for (TestTypeOfSampleDO t : types) {
                if (DataBaseUtil.isSame(typeId, t.getTypeOfSampleId())) {
                    found = true;
                    break;
                }
            }
        } 

        if ( !found)
            tree.addException(node, 0, new Exception(Messages.get().analysis_sampleTypeInvalid()));
        else
            tree.clearExceptions(node, 0);
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