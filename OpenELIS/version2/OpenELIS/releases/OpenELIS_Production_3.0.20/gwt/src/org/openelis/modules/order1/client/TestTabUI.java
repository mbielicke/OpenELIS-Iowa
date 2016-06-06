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
package org.openelis.modules.order1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.IOrderManager1;
import org.openelis.meta.IOrderMeta;
import org.openelis.modules.order1.client.AddTestEvent.AddType;
import org.openelis.modules.panel1.client.PanelService1Impl;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class TestTabUI extends Screen {

    @UiTemplate("TestTab.ui.xml")
    interface TestTabUiBinder extends UiBinder<Widget, TestTabUI> {
    };

    private static TestTabUiBinder uiBinder = GWT.create(TestTabUiBinder.class);

    @UiField
    protected Tree                 tree;

    @UiField
    protected AutoComplete         testName;

    @UiField
    protected Button               removeTestButton, checkAllButton, uncheckAllButton;

    protected Screen               parentScreen;

    protected EventBus             parentBus;

    protected boolean              isVisible, redraw;

    protected IOrderManager1       manager;

    private static final String    TEST_LEAF = "test", ANALYTE_LEAF = "analyte";

    public TestTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(isState(QUERY, DISPLAY, ADD, UPDATE));
                tree.setQueryMode(isState(QUERY));
                if (isState(QUERY))
                    tree.selectNodeAt(0);
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 3; i++ ) {
                    qd = (QueryData)tree.getValueAt(0, i);
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(IOrderMeta.getTestItemSequence());
                                break;
                            case 1:
                                qd.setKey(IOrderMeta.getTestName());
                                break;
                            case 2:
                                qd.setKey(IOrderMeta.getTestMethodName());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() > 0 || !isState(ADD, UPDATE))
                    event.cancel();
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                String uid;
                IOrderTestViewDO ot;
                IOrderTestAnalyteViewDO ota;
                Object val;
                Node node;

                val = tree.getValueAt(event.getRow(), event.getCol());
                node = tree.getNodeAt(tree.getSelectedNode());
                if (event.getCol() == 0) {
                    uid = node.getData();
                    if (TEST_LEAF.equals(node.getType())) {
                        ot = (IOrderTestViewDO)manager.getObject(uid);
                        /*
                         * if the user entered an invalid number, the value in
                         * the cell is a String and not a Integer
                         */
                        if (val instanceof Integer)
                            ot.setItemSequence((Integer)val);
                        else
                            ot.setItemSequence(null);
                    } else if (ANALYTE_LEAF.equals(node.getType())) {
                        ota = (IOrderTestAnalyteViewDO)manager.getObject(uid);
                        ota.setTestAnalyteIsReportable((String)val);
                    }
                }
            }
        });

        addScreenHandler(testName, "testName", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                testName.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                int index;
                Node selRow;
                ScheduledCommand cmd;
                AddTestEvent.AddType type;
                AutoCompleteValue val;
                TestMethodVO test;

                testName.finishEditing();
                val = testName.getValue();
                if (val == null)
                    return;
                test = (TestMethodVO)val.getData();

                if (test.getMethodId() != null)
                    type = AddType.TEST;
                else
                    type = AddType.PANEL;

                index = tree.getSelectedNode();
                /*
                 * find the index where the test should be added
                 */
                if (index == -1) {
                    index = manager.test.count();
                } else {
                    selRow = tree.getNodeAt(index);
                    if (ANALYTE_LEAF.equals(selRow.getType()))
                        index = tree.getRoot().getIndex(selRow.getParent());
                    index++ ;
                }
                /*
                 * to set the focus back on the add test widget, a command must
                 * be scheduled
                 */
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        testName.setFocus(true);
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);

                parentBus.fireEvent(new AddTestEvent(type, test.getTestId(), index));
            }

            public void onStateChange(StateChangeEvent event) {
                testName.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return tree;
            }
        });

        testName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                showTestMatches(event.getMatch());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeTestButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                checkAllButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                uncheckAllButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayTests();
            }
        });
    }

    @UiHandler("removeTestButton")
    protected void removeTest(ClickEvent event) {
        String uid;
        Integer selected[];
        Node selRow;
        HashSet<Integer> testIds;

        selected = tree.getSelectedNodes();
        if (selected.length > 0) {
            /*
             * since we remove the test of selected analytes, we use a HashSet
             * to make sure the same test ID doesn't get added twice
             */
            testIds = new HashSet<Integer>();
            for (int i = 0; i < selected.length; i++ ) {
                selRow = tree.getNodeAt(selected[i]);
                if (ANALYTE_LEAF.equals(selRow.getType()))
                    selRow = selRow.getParent();
                uid = selRow.getData();
                testIds.add( ((IOrderTestViewDO)manager.getObject(uid)).getId());
            }
            parentBus.fireEvent(new RemoveTestEvent(new ArrayList<Integer>(testIds)));
        }
    }

    @UiHandler("checkAllButton")
    protected void checkAll(ClickEvent event) {
        check("Y");
    }

    @UiHandler("uncheckAllButton")
    protected void uncheckAll(ClickEvent event) {
        check("N");
    }

    protected void check(String reportable) {
        int index;
        Integer orderId;
        String uid;
        Node node, child;
        IOrderTestAnalyteViewDO ota;

        orderId = manager.getIorder().getId();
        if (orderId == null)
            orderId = 0;

        if (tree.getSelectedNodes().length > 1) {
            setError(Messages.get().order_multiTestCheckNotAllowed(orderId));
            return;
        }
        index = tree.getSelectedNode();

        if (index == -1)
            return;
        node = tree.getNodeAt(index);
        if (ANALYTE_LEAF.equals(node.getType()))
            index = tree.getRoot().getIndex(node.getParent());

        node = tree.getNodeAt(index);
        tree.open(node);
        for (int i = 0; i < node.getChildCount(); i++ ) {
            child = node.getChildAt(i);
            uid = child.getData();
            ota = (IOrderTestAnalyteViewDO)manager.getObject(uid);
            ota.setTestAnalyteIsReportable(reportable);
            tree.setValueAt(index + i + 1, 0, ota.getTestAnalyteIsReportable());
        }
    }

    public void setData(IOrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        redraw = true;
        displayTests();
    }

    private Node getRoot() {
        int i, j;
        Node root, tnode, anode;
        IOrderTestAnalyteViewDO ota;
        IOrderTestViewDO ot;

        root = new Node();
        if (manager == null)
            return root;

        for (i = 0; i < manager.test.count(); i++ ) {
            ot = manager.test.get(i);

            tnode = new Node(4);
            tnode.setType(TEST_LEAF);
            tnode.setCell(0, ot.getItemSequence());
            /*
             * create label for the test
             */
            tnode.setCell(1, ot.getTestName());
            tnode.setCell(2, ot.getMethodName());
            tnode.setCell(3, ot.getDescription());
            tnode.setData(Constants.uid().get(ot));
            root.add(tnode);

            for (j = 0; j < manager.analyte.count(ot); j++ ) {
                ota = manager.analyte.get(ot, j);

                anode = new Node(2);
                anode.setType(ANALYTE_LEAF);
                anode.setCell(0, ota.getTestAnalyteIsReportable());
                anode.setCell(1, ota.getAnalyteName());
                anode.setData(Constants.uid().get(ota));
                tnode.add(anode);
            }
        }

        return root;
    }

    private void displayTests() {

        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
            testName.setFocus(testName.isEnabled());
        }
    }

    private void showTestMatches(String name) {
        Integer key;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<TestMethodVO> tests;

        try {
            setBusy();
            tests = PanelService1Impl.INSTANCE.fetchByNameWithTests(QueryFieldUtil.parseAutocomplete(name +
                                                                                                     "%"));

            model = new ArrayList<Item<Integer>>();
            for (TestMethodVO t : tests) {
                /*
                 * Since the keys of the rows need to be unique and it can
                 * happen that a panel has the same id as a test, a negative
                 * number is used as the key for a row showing a panel and a
                 * positive one for a row showing a test. An index in a loop
                 * can't be used here because it can clash with an id and two
                 * different rows may be treated as the same.
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

            testName.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        clearStatus();
    }
}
