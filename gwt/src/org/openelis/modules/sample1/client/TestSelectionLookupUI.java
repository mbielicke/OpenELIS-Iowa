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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestSectionManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.BeforeNodeCloseEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeCloseHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to choose the prep or reflex test for an
 * analysis
 */
public abstract class TestSelectionLookupUI extends Screen {

    @UiTemplate("TestSelectionLookup.ui.xml")
    interface TestSelectionLookupUIBinder extends UiBinder<Widget, TestSelectionLookupUI> {
    };

    private static TestSelectionLookupUIBinder uiBinder = GWT.create(TestSelectionLookupUIBinder.class);

    @UiField
    protected Button                           copyToEmptyButton, copyToAllButton, okButton;

    @UiField
    protected Tree                             tree;

    @UiField
    protected Dropdown<Integer>                section;

    protected ArrayList<SampleTestRequestVO>   tests, selectedTests;
    protected HashMap<Integer, SampleManager1> managersById;

    public TestSelectionLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        addScreenHandler(copyToEmptyButton, "copyToEmptyButton", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                copyToEmptyButton.setEnabled(false);
            }
        });
        
        addScreenHandler(copyToAllButton, "copyToAllButton", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                copyToAllButton.setEnabled(false);
            }
        });
        
        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                boolean enable;
                Node selection;

                selection = tree.getNodeAt(event.getSelectedItem());
                if (selection != null && "test".equals(selection.getType()))
                    enable = true;
                else
                    enable = false;

                copyToEmptyButton.setEnabled(enable);
                copyToAllButton.setEnabled(enable);
            }
        });

        tree.addBeforeNodeCloseHandler(new BeforeNodeCloseHandler() {
            public void onBeforeNodeClose(BeforeNodeCloseEvent event) {
                event.cancel();
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int i;
                Node node;
                TestSectionViewDO ts;
                TestSectionManager tsm;
                TestData td;

                node = tree.getNodeAt(event.getRow());
                if ( !"test".equals(node.getType()) || event.getCol() == 0) {
                    event.cancel();
                    return;
                }

                td = node.getData();
                switch (event.getCol()) {
                    case 1:
                        tsm = td.testManager.getTestSections();
                        /*
                         * since the dropdown contains the list of all sections
                         * across the list of prep/reflex tests, we need to
                         * enable/disable the appropriate ones for the
                         * prep/reflex test in the selected row
                         */
                        for (Item<Integer> item : section.getModel()) {
                            for (i = 0; i < tsm.count(); i++ ) {
                                ts = tsm.getSectionAt(i);
                                if (ts.getSectionId().equals(item.getKey())) {
                                    item.setEnabled(true);
                                    break;
                                }
                            }
                            if (i == tsm.count())
                                item.setEnabled(false);
                        }
                        break;
                    case 2:
                        /*
                         * if the prep/reflex test is NOT optional, we need to
                         * prevent the user from unchecking this item
                         */
                        if ( !td.isOptional)
                            event.cancel();
                        break;
                }
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                Node node;
                TestData td;

                node = tree.getNodeAt(event.getRow());
                if ("test".equals(node.getType()) && event.getCol() == 1) {
                    td = node.getData();
                    td.test.setSectionId((Integer)tree.getValueAt(event.getRow(), 1));
                }
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setData(SampleManager1 manager, ArrayList<SampleTestRequestVO> tests) {
        ArrayList<SampleManager1> sMans;

        sMans = new ArrayList<SampleManager1>();
        sMans.add(manager);
        setData(sMans, tests);
    }

    /**
     * refreshes the screen's view by setting the state and loading data in
     * widgets
     */
    public void setData(ArrayList<SampleManager1> managers, ArrayList<SampleTestRequestVO> tests) {
        this.tests = tests;

        if (managersById == null)
            managersById = new HashMap<Integer, SampleManager1>();
        managersById.clear();
        for (SampleManager1 man : managers)
            managersById.put(man.getSample().getId(), man);
        
        setState(state);
        fireDataChange();
    }

    /**
     * returns the list of selected prep / reflex tests
     */
    public ArrayList<SampleTestRequestVO> getSelectedTests() {
        return selectedTests;
    }

    /**
     * overridden to return the TestManager for this test id
     */
    public abstract TestManager getTestManager(Integer testId) throws Exception;

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    private Node getRoot() {
        int i;
        boolean isPrep, newAnode;
        Integer anaId, samId, resId;
        String accession;
        Node root, anode, tnode;
        AnalysisViewDO ana;
        TestManager anaTM, testTM;
        TestSectionManager tsm;
        TestPrepViewDO tp;
        TestReflexViewDO tr;
        TestSectionViewDO ts;
        TestData td;
        HashSet<Integer> sectionIds;
        ArrayList<Item<Integer>> model;
        ArrayList<String> labels;
        SampleManager1 manager;
        ResultViewDO res;

        root = new Node();
        anaId = null;
        ana = null;
        anode = null;
        samId = null;
        resId = null;
        sectionIds = new HashSet<Integer>();
        model = new ArrayList<Item<Integer>>();
        labels = new ArrayList<String>();
        manager = null;
        accession = null;
        res = null;

        isPrep = false;
        for (SampleTestRequestVO test : tests) {
            newAnode = false;
            if (!test.getSampleId().equals(samId)) {
                manager = managersById.get(test.getSampleId());
                samId = test.getSampleId();
                if (manager.getSample().getAccessionNumber() != null)
                    accession = DataBaseUtil.toString(manager.getSample().getAccessionNumber());
                else
                    accession = Messages.get().testSelection_newAccession();
                newAnode = true;
            }
                           
            if (!test.getAnalysisId().equals(anaId)) {
                ana = (AnalysisViewDO)manager.getObject(Constants.uid().getAnalysis(test.getAnalysisId()));
                anaId = test.getAnalysisId();
                newAnode = true;
            }
            
            if (!test.getResultId().equals(resId)) {
                res = (ResultViewDO)manager.getObject(Constants.uid().getResult(test.getResultId()));
                resId = test.getResultId();
                newAnode = true;
            }

            if (newAnode) {
                /*
                 * the node for the analysis
                 */
                anode = new Node(3);
                anode.setType("analysis");
                anode.setOpen(true);

                labels.clear();
                labels.add(accession);
                labels.add(":");
                labels.add(ana.getTestName());
                labels.add(",");
                labels.add(ana.getMethodName());
                if (res != null) {
                    labels.add(":");
                    labels.add(res.getAnalyte());
                    labels.add(":");
                    if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId()))
                        labels.add(res.getDictionary());
                    else
                        labels.add(res.getValue());
                }
                anode.setCell(0, DataBaseUtil.concatWithSeparator(labels, " "));
                anode.setData(ana);

                root.add(anode);
            }

            td = new TestData();
            td.test = test;
            try {
                testTM = getTestManager(test.getTestId());
                td.testManager = testTM;

                /*
                 * the node for the prep/reflex test
                 */
                tnode = new Node(3);
                tnode.setType("test");

                labels.clear();
                labels.add(testTM.getTest().getName());
                labels.add(",");
                labels.add(testTM.getTest().getMethodName());
                tnode.setCell(0, DataBaseUtil.concatWithSeparator(labels, " "));

                tsm = testTM.getTestSections();
                /*
                 * add this test's sections to the model for the dropdown for
                 * sections
                 */
                for (i = 0; i < tsm.count(); i++ ) {
                    ts = tsm.getSectionAt(i);
                    if ( !sectionIds.contains(ts.getSectionId())) {
                        model.add(new Item<Integer>(ts.getSectionId(), ts.getSection()));
                        sectionIds.add(ts.getSectionId());
                    }
                }

                /*
                 * if the section is not already set in the VO for the
                 * prep/reflex test then set the default section if there is one
                 */
                if (td.test.getSectionId() == null) {
                    ts = tsm.getDefaultSection();
                    if (ts != null)
                        td.test.setSectionId(ts.getSectionId());
                }

                tnode.setCell(1, td.test.getSectionId());

                anaTM = getTestManager(ana.getTestId());
                if (test.getResultId() == null) {
                    /*
                     * the tree is showing prep tests; find out if the prep test
                     * is required
                     */
                    tp = getPrepTest(anaTM.getPrepTests(), testTM.getTest().getId());
                    td.isOptional = "Y".equals(tp.getIsOptional());
                    isPrep = true;
                } else {
                    /*
                     * the tree is showing reflex tests; find out if the reflex
                     * test needs to be auto-added
                     */
                    tr = getReflexTest(anaTM.getReflexTests(), testTM.getTest().getId());
                    td.isOptional = !Constants.dictionary().REFLEX_AUTO.equals(tr.getFlagsId()) ||
                                    !Constants.dictionary().REFLEX_AUTO_NDUP.equals(tr.getFlagsId());
                }

                if (td.isOptional)
                    tnode.setCell(2, "N");
                else
                    tnode.setCell(2, "Y");

                tnode.setData(td);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return null;
            }

            anode.add(tnode);
        }

        section.setModel(model);

        /*
         * set the header for the first column based on whether the tree is
         * showing prep or reflex tests
         */
        if (isPrep)
            tree.getColumnAt(0).setLabel(Messages.get().testSelection_analysisPrepTestMethod());
        else
            tree.getColumnAt(0).setLabel(Messages.get().testSelection_analysisReflexTestMethod());

        return root;
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        int i, j;
        Node parent, child;
        ValidationErrorsList errors;
        TestViewDO t;
        TestData td;

        if (selectedTests == null)
            selectedTests = new ArrayList<SampleTestRequestVO>();
        else
            selectedTests.clear();

        errors = new ValidationErrorsList();

        /*
         * create the list of prep/reflex test(s) checked by the user; show an
         * error if the section is not selected for some test checked by the
         * user
         */
        for (i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            parent = tree.getRoot().getChildAt(i);

            for (j = 0; j < parent.getChildCount(); j++ ) {
                child = parent.getChildAt(j);
                td = child.getData();

                if ( !"Y".equals(child.getCell(2)))
                    continue;

                if (td.test.getSectionId() == null) {
                    t = td.testManager.getTest();
                    if (td.test.getResultId() == null)
                        errors.add(new FormErrorException(Messages.get()
                                                                  .testSelection_prepTestNeedsSection(t.getName(),
                                                                                                      t.getMethodName())));
                    else
                        errors.add(new FormErrorException(Messages.get()
                                                                  .testSelection_reflexTestNeedsSection(t.getName(),
                                                                                                        t.getMethodName())));
                } else {
                    selectedTests.add(td.test);
                }
            }
        }

        if (errors.size() > 0) {
            showErrors(errors);
            return;
        } else {
            window.close();
            ok();
        }
    }

    @UiHandler("copyToEmptyButton")
    protected void copyToEmpty(ClickEvent event) {
        int i, j;
        Integer sectionId;
        Node item, selection;
        TestSectionManager tsm;
        TestSectionViewDO ts;
        TestData td;

        tree.finishEditing();

        sectionId = null;
        selection = tree.getNodeAt(tree.getSelectedNode());
        sectionId = (Integer)selection.getCell(1);

        if (sectionId == null) {
            Window.alert(Messages.get().analysis_cantCopyBlankSect());
            return;
        }

        for (i = 0; i < tree.getRowCount(); i++ ) {
            item = tree.getNodeAt(i);
            if ("test".equals(item.getType()) && item.getCell(1) == null) {
                td = item.getData();
                tsm = td.testManager.getTestSections();
                /*
                 * don't set the section if this test doesn't have it
                 */
                for (j = 0; j < tsm.count(); j++ ) {
                    ts = tsm.getSectionAt(j);
                    if (sectionId.equals(ts.getSectionId())) {
                        td.test.setSectionId(sectionId);
                        item.setCell(1, sectionId);
                        tree.refreshNode(item);
                        break;
                    }
                }
            }
        }
    }

    @UiHandler("copyToAllButton")
    protected void copyToAll(ClickEvent event) {
        int i, j;
        Integer sectionId;
        Node item, selection;
        TestSectionManager tsm;
        TestSectionViewDO tsVDO;
        TestData td;

        tree.finishEditing();

        selection = tree.getNodeAt(tree.getSelectedNode());
        sectionId = (Integer)selection.getCell(1);

        if (sectionId == null) {
            Window.alert(Messages.get().analysis_cantCopyBlankSect());
            return;
        }

        for (i = 0; i < tree.getRowCount(); i++ ) {
            item = tree.getNodeAt(i);
            if ("test".equals(item.getType())) {
                td = item.getData();
                tsm = td.testManager.getTestSections();
                /*
                 * don't set the section if this test doesn't have it
                 */
                for (j = 0; j < tsm.count(); j++ ) {
                    tsVDO = tsm.getSectionAt(j);
                    if (sectionId.equals(tsVDO.getSectionId())) {
                        td.test.setSectionId(sectionId);
                        item.setCell(1, sectionId);
                        tree.refreshNode(item);
                        break;
                    }
                }
            }
        }
    }

    /**
     * returns from the manager, the prep test that has this prep test id
     */
    private TestPrepViewDO getPrepTest(TestPrepManager tpm, Integer prepTestId) {
        for (int i = 0; i < tpm.count(); i++ ) {
            if (tpm.getPrepAt(i).getPrepTestId().equals(prepTestId))
                return tpm.getPrepAt(i);
        }

        return null;
    }

    /**
     * returns from the manager, the reflex test that has this add test id
     */
    private TestReflexViewDO getReflexTest(TestReflexManager trm, Integer addTestId) {
        for (int i = 0; i < trm.count(); i++ ) {
            if (trm.getReflexAt(i).getAddTestId().equals(addTestId))
                return trm.getReflexAt(i);
        }

        return null;
    }

    /**
     * This class is used to keep track of the data associated with each prep or
     * reflex test that can be chosen for an analysis in the tree
     */
    private static class TestData {
        boolean             isOptional;
        TestManager         testManager;
        SampleTestRequestVO test;
    }
}