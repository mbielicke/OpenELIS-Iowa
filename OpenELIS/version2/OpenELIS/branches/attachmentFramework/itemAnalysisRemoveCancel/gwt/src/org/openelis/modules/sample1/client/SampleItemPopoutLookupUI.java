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

import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to look at the sample items and analyses of
 * a sample in a bigger space and also to move analyses between sample items
 */
public abstract class SampleItemPopoutLookupUI extends Screen {

    @UiTemplate("SampleItemPopoutLookup.ui.xml")
    interface SampleItemPopoutLookupUIBinder extends UiBinder<Widget, SampleItemPopoutLookupUI> {
    };

    private static SampleItemPopoutLookupUIBinder uiBinder = GWT.create(SampleItemPopoutLookupUIBinder.class);

    protected SampleManager1                      manager;

    @UiField
    protected Tree                                tree;

    @UiField
    protected Table                               table;

    @UiField
    protected Button                              moveButton, okButton;

    protected boolean                             canEdit;

    protected ArrayList<Node>                     checkedNodes;
    
    private static final String    SAMPLE_ITEM_LEAF = "sampleItem", ANALYSIS_LEAF = "analysis";

    public SampleItemPopoutLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                Node node;
                AnalysisViewDO ana;

                node = tree.getNodeAt(tree.getSelectedNode());

                if (canEdit && isState(ADD, UPDATE) && ANALYSIS_LEAF.equals(node.getType()) &&
                    event.getCol() == 0) {
                    ana = node.getData();
                    /*
                     * moving released or cancelled analyses is not allowed 
                     */
                    if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
                        Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
                        event.cancel();
                } else {
                    event.cancel();
                }
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Object val;
                Node node;

                val = tree.getValueAt(event.getRow(), event.getCol());
                node = tree.getNodeAt(tree.getSelectedNode());

                if ("Y".equals(val)) {
                    if (checkedNodes == null)
                        checkedNodes = new ArrayList<Node>();
                    checkedNodes.add(node);
                } else {
                    checkedNodes.remove(node);
                }
            }
        });

        addScreenHandler(moveButton, "moveButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                moveButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || event.getCol() != 0 || !canEdit)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                /*
                 * if the user checks a row then uncheck all the other rows
                 */
                if ("Y".equals(table.getValueAt(event.getRow(), event.getCol()))) {
                    for (int i = 0; i < table.getRowCount(); i++ ) {
                        if (i != event.getRow())
                            table.setValueAt(i, 0, "N");
                    }
                }
            }
        });

        addScreenHandler(moveButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                moveButton.setEnabled(canEdit && isState(ADD, UPDATE));
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

    public void setData(SampleManager1 manager, State state) {
        this.manager = manager;
        evaluateEdit();
        setState(state);
        fireDataChange();
        /*
         * this is done to get rid of any old error messages
         */
        window.clearStatus();
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    @UiHandler("moveButton")
    protected void move(ClickEvent event) {
        AnalysisViewDO ana;
        SampleItemViewDO newItem, oldItem;

        /*
         * at least one analysis must be checked
         */
        if (checkedNodes == null || checkedNodes.size() == 0) {
            window.setError(Messages.get().selectOneOrMoreAnalyses());
            return;
        }

        newItem = null;

        /*
         * at least one sample item must be checked
         */
        for (Row row : table.getModel()) {
            if ("Y".equals(row.getCell(0))) {
                newItem = row.getData();
                break;
            }
        }

        if (newItem == null) {
            window.setError(Messages.get().selectItem());
            return;
        }

        try {
            for (Node node : checkedNodes) {
                ana = node.getData();

                oldItem = (SampleItemViewDO)manager.getObject(manager.getSampleItemUid(ana.getSampleItemId()));
                if (newItem.getId().equals(oldItem.getId())) {
                    /*
                     * an analysis can't be moved to its own item
                     */
                    window.setError(Messages.get().analysisNotMovedToOwnItem());
                    return;
                }
            }

            /*
             * move the selected analyses
             */
            for (Node row : checkedNodes) {
                ana = row.getData();
                manager.analysis.moveAnalysis(ana.getId(), newItem.getId());
            }

            fireDataChange();
            checkedNodes.clear();
            window.clearStatus();
        } catch (Exception e) {
            Window.alert("Move failed: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        window.close();
        ok();
    }

    private Node getRoot() {
        int i, j;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        Node root, inode, anode;
        StringBuffer buf;
        ArrayList<String> labels;

        root = new Node();
        if (manager == null)
            return root;

        labels = new ArrayList<String>();
        buf = new StringBuffer(); 
        for (i = 0; i < manager.item.count(); i++ ) {
            item = manager.item.get(i);

            inode = new Node(2);
            inode.setType(SAMPLE_ITEM_LEAF);
            inode.setOpen(true);
            inode.setKey(item.getId());
            inode.setCell(0, item.getItemSequence());
            
            labels.clear();
            labels.add(item.getTypeOfSample());
            if (item.getContainer() != null) {
                labels.add(" [");
                labels.add(item.getContainer());
                labels.add("]");
            }
            buf.setLength(0);
            inode.setCell(1, concat(labels, buf));
            
            inode.setData(item);

            root.add(inode);
            for (j = 0; j < manager.analysis.count(item); j++ ) {
                ana = manager.analysis.get(item, j);

                anode = new Node(3);
                anode.setType(ANALYSIS_LEAF);

                anode.setKey(ana.getId());
                anode.setCell(0, "N");

                labels.clear();
                
                labels.add(ana.getTestName());
                labels.add(", ");
                labels.add(ana.getMethodName());
                if (ana.getStatusId() != null) {
                    try {
                        labels.add(" (");
                        labels.add(DictionaryCache.getById(ana.getStatusId()).getEntry());
                        labels.add(")");
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        return root;
                    } 
                }
                buf.setLength(0);
                anode.setCell(1, concat(labels, buf));
                anode.setData(ana);
                inode.add(anode);
            }
        }

        return root;
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        SampleItemViewDO data;
        Row row;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        for (int i = 0; i < manager.item.count(); i++ ) {
            data = manager.item.get(i);
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, data.getItemSequence());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
    }
    
    private String concat(List<String> list, StringBuffer buf) {
        for (String i : list) {
            if (i != null)
                buf.append(i);
        }
        return buf.toString();
    }
}