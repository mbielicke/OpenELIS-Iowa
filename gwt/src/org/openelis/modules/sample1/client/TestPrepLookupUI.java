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

import java.util.ArrayList;

import org.openelis.cache.CacheProvider;
import org.openelis.constants.Messages;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
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
 * This class is used to allow users to choose the prep test for an analysis
 */
public abstract class TestPrepLookupUI extends Screen {

    @UiTemplate("TestPrepLookup.ui.xml")
    interface TestPrepLookupUIBinder extends UiBinder<Widget, TestPrepLookupUI> {
    };

    private static TestPrepLookupUIBinder uiBinder = GWT.create(TestPrepLookupUIBinder.class);

    @UiField
    protected Button                      copyToEmptyButton, copyToAllButton, okButton,
                    cancelButton;

    @UiField
    protected Tree                        tree;

    @UiField
    protected Dropdown<Integer>           prepSection;

    protected Screen                      parentScreen;

    protected SampleTestReturnVO          data;
    
    protected ArrayList<Item<Integer>>    sectionModel;
    
    protected ArrayList<Node>             selectedPrepNodes;

    public TestPrepLookupUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        data = null;
    }

    private void initialize() {

        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getTests());
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
                if (selection != null && "prepTest".equals(selection.getType()))
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
                TestPrepViewDO tp;
                TestSectionViewDO ts;
                TestSectionManager tsm;
                TestManager tm;

                node = tree.getNodeAt(event.getRow());
                if ( !"prepTest".equals(node.getType()) || event.getCol() == 0) {
                    event.cancel();
                    return;
                }

                tp = node.getData();
                switch (event.getCol()) {
                    case 1:
                        tm = ((CacheProvider)parentScreen).get(tp.getPrepTestId(),
                                                               TestManager.class);
                        tsm = tm.getTestSections();
                        /*
                         * since the dropdown contains the list of all sections
                         * across the list of prep tests, we need to
                         * enable/disable the appropriate ones for the prep test
                         * in the selected row
                         */
                        for (Item<Integer> item : prepSection.getModel()) {
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
                         * if the prep test is NOT optional, we need to prevent
                         * the user from unchecking this item
                         */
                        if ("N".equals(tp.getIsOptional()))
                            event.cancel();
                        break;
                }
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }
    
    public void setSectionModel(ArrayList<Item<Integer>> sectionModel) {
        this.sectionModel = sectionModel;
    }

    /**
     * refreshes the screen's view by setting the state and loading the data in
     * the widgets
     */
    public void setData(SampleTestReturnVO data) {
        this.data = data;
        /*
         * set the model in the dropdown for sections in the tree
         */
        prepSection.setModel(sectionModel);
        setState(state);
        fireDataChange();
    }
    
    /** 
     * returns the list of nodes showing selected prep tests 
     */
    public ArrayList<Node> getSelectedPrepNodes() {        
        return selectedPrepNodes;
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();
    
    /**
     * overridden to load the tree
     */
    public abstract Node getTests();

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        int i, j;
        Integer sectionId;
        Node parent, child;
        ValidationErrorsList errors;
        TestPrepViewDO tp;
        
        if (selectedPrepNodes == null)
            selectedPrepNodes = new ArrayList<Node>();
        else 
            selectedPrepNodes.clear();
        
        errors = new ValidationErrorsList();
        
        for (i = 0; i < tree.getRoot().getChildCount(); i++) {
            parent = tree.getRoot().getChildAt(i);
            
            for (j = 0; j < parent.getChildCount(); j++) {
                child = parent.getChildAt(j);
                tp = child.getData();
                sectionId = child.getCell(1);
                if (sectionId == null)
                    errors.add(new FormErrorException(Messages.get().prepTestNeedsSection(tp.getPrepTestName())));
                else if ("Y".equals(child.getCell(2))) 
                    selectedPrepNodes.add(child);                
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

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        Window.alert(Messages.get().prepTestRequiredException());
        window.close();
        cancel();
    }
    
    @UiHandler("copyToEmptyButton")
    protected void copyToEmpty(ClickEvent event) {
        int i, j;
        Integer sectionId;
        Node item, selection;
        TestSectionManager tsm;
        TestSectionViewDO ts;
        TestPrepViewDO tp;
        TestManager tm;

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
            if ("prepTest".equals(item.getType()) && item.getCell(1) == null) {                
                tp = item.getData();
                tm = ((CacheProvider)parentScreen).get(tp.getPrepTestId(),
                                                       TestManager.class);
                tsm = tm.getTestSections();
                /*
                 * don't set the section if this test doesn't have it  
                 */
                for (j = 0; j < tsm.count(); j++ ) {
                    ts = tsm.getSectionAt(j);
                    if (sectionId.equals(ts.getSectionId())) {
                        item.setCell(1, sectionId);
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
        TestPrepViewDO tp;
        TestManager tm;

        tree.finishEditing();

        selection = tree.getNodeAt(tree.getSelectedNode());
        sectionId = (Integer)selection.getCell(1);

        if (sectionId == null) {
            Window.alert(Messages.get().analysis_cantCopyBlankSect());
            return;
        }

        for (i = 0; i < tree.getRowCount(); i++ ) {
            item = tree.getNodeAt(i);
            if ("prepTest".equals(item.getType())) {
                tp = item.getData();
                tm = ((CacheProvider)parentScreen).get(tp.getPrepTestId(),
                                                       TestManager.class);
                tsm = tm.getTestSections();
                /*
                 * don't set the section if this test doesn't have it  
                 */
                for (j = 0; j < tsm.count(); j++ ) {
                    tsVDO = tsm.getSectionAt(j);
                    if (sectionId.equals(tsVDO.getSectionId())) {
                        item.setCell(1, sectionId);
                        break;
                    }
                }
            }
        }
    }
}