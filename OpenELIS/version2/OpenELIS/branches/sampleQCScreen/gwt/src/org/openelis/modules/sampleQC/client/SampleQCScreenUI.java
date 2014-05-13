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
package org.openelis.modules.sampleQC.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.QcMeta;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
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

public class SampleQCScreenUI extends Screen {

    @UiTemplate("SampleQCScreen.ui.xml")
    interface SampleQcUiBinder extends UiBinder<Widget, SampleQCScreenUI> {
    };

    public static final SampleQcUiBinder           uiBinder = GWT.create(SampleQcUiBinder.class);

    protected ArrayList<WorksheetManager1>         managers;

    protected ModulePermission                     userPermission;

    @UiField
    protected Button                               query, commit, abort, optionsButton, run;

    @UiField
    protected TextBox<Integer>                     id;

    @UiField
    protected Dropdown<Integer>                    printer;

    @UiField
    protected Tree                                 tree;

    protected SampleQCScreenUI                     screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<ArrayList<WorksheetManager1>> fetchByIdCall, unlockCall;

    private static final String                             WORKSHEET_LEAF = "worksheet",
                    QC_LEAF = "qc";

    public SampleQCScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Sample QC Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        managers = null;

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Sample QC Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                 userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.lock();
                    query.setPressed(true);
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY));
            }
        });

        addShortcut(abort, 'o', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsButton.setEnabled(isState(DISPLAY));
            }
        });

        //
        // screen fields
        //
        addScreenHandler(id, QcMeta.getId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : id;
            }
        });

        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(isState(DISPLAY));
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                // String uid;
                // Object val;
                Node node;

                // val = tree.getValueAt(event.getRow(), event.getCol());
                node = tree.getNodeAt(tree.getSelectedNode());
                if (event.getCol() == 0) {
                    if (QC_LEAF.equals(node.getType())) {

                    } else if (WORKSHEET_LEAF.equals(node.getType())) {

                    }
                }
            }
        });
        tree.setVisible(true);
    }

    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        managers = null;
        /*
         * the tab for aux data uses the cache in query state
         */
        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit(false);
    }

    private void commit(boolean ignoreWarning) {
        Validation validation;

        finishEditing();

        validation = validate();

        switch (validation.getStatus()) {
            case WARNINGS:
                /*
                 * show the warnings and ask the user if the data should still
                 * be committed; commit only if the user says yes
                 */
                if ( !Window.confirm(getWarnings(validation.getExceptions())))
                    return;
                break;
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        commitQuery();

    }

    protected void commitQuery() {
        fetchByAccession(Integer.parseInt(id.getText()));
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                managers = null;
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                clearStatus();
            }
        }
    }

    private void fetchByAccession(Integer accession) {
        if (accession == null) {
            managers = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<ArrayList<WorksheetManager1>>() {
                    public void success(ArrayList<WorksheetManager1> result) {
                        managers = result;
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchByAccession(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchByAccession(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            SampleQCService.get().fetchByAccessionNumber(accession, fetchByIdCall);
        }
    }

    /**
     * creates a string containing the message that there are warnings on the
     * screen, followed by all warning messages, followed by the question
     * whether the data should be committed
     */
    private String getWarnings(ArrayList<Exception> warnings) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        if (warnings != null) {
            for (Exception ex : warnings)
                b.append(" * ").append(ex.getMessage()).append("\n");
        }
        b.append("\n").append(Messages.get().gen_warningDialogLastLine());

        return b.toString();
    }

    private Node getRoot() {
        int i, j, k, l;
        Node root, qnode, wnode;
        WorksheetQcResultViewDO wq;
        WorksheetManager1 wm;

        root = new Node();
        if (managers == null || managers.size() == 0)
            return root;

        for (i = 0; i < managers.size(); i++ ) {
            wm = managers.get(i);
            wnode = new Node(4);
            wnode.setType(WORKSHEET_LEAF);
            wnode.setCell(0, wm.getWorksheet().getId());
            wnode.setCell(1, wm.getWorksheet().getDescription());
            wnode.setCell(2, wm.getWorksheet().getCreatedDate());
            wnode.setCell(2, wm.getWorksheet().getInstrumentName());
            wnode.setData(wm);
            root.add(wnode);

            for (j = 0; j < wm.item.count(); j++ ) {
                for (k = 0; k < wm.analysis.count(wm.item.get(j)); k++ ) {
                    for (l = 0; l < wm.qcResult.count(wm.analysis.get(wm.item.get(j), k)); l++ ) {
                        wq = wm.qcResult.get(wm.analysis.get(wm.item.get(j), k), l);
                        qnode = new Node(2);
                        qnode.setCell(0, new Boolean(false));
                        qnode.setCell(1, wq.getAnalyteName());
                        qnode.setData(wq);
                        wnode.add(qnode);
                    }
                }
            }
        }

        return root;
    }
}