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
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.QcMeta;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.worksheet1.client.WorksheetService1;
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

    public static final SampleQcUiBinder                           uiBinder            = GWT.create(SampleQcUiBinder.class);

    protected ArrayList<WorksheetManager1>                         managers;

    protected SampleManager1                                       sm;

    protected HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> selected;

    protected ModulePermission                                     userPermission;

    @UiField
    protected Button                                               query, commit, abort,
                    optionsButton, run;

    @UiField
    protected TextBox<Integer>                                     id;

    @UiField
    protected Dropdown<Integer>                                    printer;

    @UiField
    protected Tree                                                 tree;

    protected SampleQCScreenUI                                     screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>>                 queryCall;

    protected AsyncCallbackUI<SampleManager1>                      fetchByIdCall, unlockCall;

    protected AsyncCallbackUI<ArrayList<WorksheetManager1>>        fetchWorksheetsByIdsCall;

    protected HashMap<Integer, HashSet<Integer>>                   analysisWorksheets,
                    analysisRelatedWorksheets;

    protected HashMap<Integer, Node>                               worksheetNodes;

    protected HashMap<Integer, WorksheetManager1>                  wms;

    protected HashMap<Integer, Integer>                            relatedWorksheets;

    protected HashMap<Integer, String>                             analysisType, analysisStatus,
                    analysisUnit, worksheetStatus, worksheetFormat;

    private static final String                                    ANALYSIS_LEAF       = "analysis",
                    WORKSHEET_LEAF = "worksheet", QC_LEAF = "qc";

    protected SampleManager1.Load                                  sampleElements[]    = {SampleManager1.Load.WORKSHEET};

    protected WorksheetManager1.Load                               worksheetElements[] = {WorksheetManager1.Load.DETAIL};

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

                    } else if (ANALYSIS_LEAF.equals(node.getType())) {

                    }
                }
            }
        });
        tree.setVisible(true);

        analysisType = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_type"))
            analysisType.put(d.getId(), d.getEntry());

        analysisStatus = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status")) {
            analysisStatus.put(d.getId(), d.getEntry());
        }

        analysisUnit = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            analysisUnit.put(d.getId(), d.getEntry());
        }

        worksheetStatus = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("worksheet_status")) {
            worksheetStatus.put(d.getId(), d.getEntry());
        }

        worksheetFormat = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("worksheet_format")) {
            worksheetFormat.put(d.getId(), d.getEntry());
        }
    }

    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        sm = null;
        managers = null;
        wms = null;
        worksheetNodes = null;
        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
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

        if (isState(QUERY)) {
            try {
                sm = null;
                managers = null;
                wms = null;
                worksheetNodes = null;
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
            sm = null;
            managers = null;
            wms = null;
            worksheetNodes = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        Integer analysisId, worksheetId;
                        ArrayList<Integer> wids;
                        SampleItemViewDO item;
                        AnalysisViewDO analysis;

                        sm = result;
                        wids = new ArrayList<Integer>();
                        analysisWorksheets = new HashMap<Integer, HashSet<Integer>>();
                        for (int i = 0; i < sm.item.count(); i++ ) {
                            item = sm.item.get(i);
                            for (int j = 0; j < sm.analysis.count(item); j++ ) {
                                analysis = sm.analysis.get(item, j);
                                analysisId = analysis.getId();
                                analysisWorksheets.put(analysisId, new HashSet<Integer>());
                                analysisRelatedWorksheets.put(analysisId, new HashSet<Integer>());
                                for (int k = 0; k < sm.worksheet.count(analysis); k++ ) {
                                    worksheetId = sm.worksheet.get(analysis, k).getId();
                                    wids.add(worksheetId);
                                    analysisWorksheets.get(analysisId).add(worksheetId);
                                }
                            }
                        }
                        if (fetchWorksheetsByIdsCall == null) {
                            fetchWorksheetsByIdsCall = new AsyncCallbackUI<ArrayList<WorksheetManager1>>() {
                                public void success(ArrayList<WorksheetManager1> result) {
                                    ArrayList<WorksheetManager1> worksheets;
                                    ArrayList<Integer> wids;

                                    worksheets = result;

                                    if (sm == null || managers.size() < 1) {
                                        sm = null;
                                        managers = null;
                                        setState(DISPLAY);
                                        return;
                                    }
                                    wids = new ArrayList<Integer>();
                                    for (WorksheetManager1 wm : worksheets) {
                                        if (wms.get(wm.getWorksheet().getId()) == null)
                                            wms.put(wm.getWorksheet().getId(), wm);
                                        if (wm.getWorksheet().getRelatedWorksheetId() != null) {
                                            relatedWorksheets.put(wm.getWorksheet().getId(),
                                                                  wm.getWorksheet()
                                                                    .getRelatedWorksheetId());
                                            if ( !wms.containsKey(wm.getWorksheet()
                                                                    .getRelatedWorksheetId()))
                                                wids.add(wm.getWorksheet().getRelatedWorksheetId());
                                        }
                                    }
                                    setState(DISPLAY);
                                    if (wids.size() > 0)
                                        WorksheetService1.get()
                                                         .fetchByIds(wids,
                                                                     worksheetElements,
                                                                     fetchWorksheetsByIdsCall);
                                    else {
                                        worksheetNodes = new HashMap<Integer, Node>();
                                        for (Integer id : wms.keySet()) {
                                            worksheetNodes.put(id, createWorksheetNode(wms.get(id)));
                                        }
                                        tree.setRoot(getRoot());
                                    }
                                }

                                public void notFound() {
                                    setDone(Messages.get().gen_noRecordsFound());
                                }

                                public void failure(Throwable e) {
                                    Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                }

                                public void finish() {
                                    fireDataChange();
                                    clearStatus();
                                }
                            };
                        }
                        WorksheetService1.get().fetchByIds(wids,
                                                           worksheetElements,
                                                           fetchWorksheetsByIdsCall);
                    }

                    public void notFound() {
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            SampleService1.get().fetchByAccession(accession, sampleElements, fetchByIdCall);
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
        int i, j, k;
        Node root, wnode, anode;
        AnalysisViewDO analysis;
        WorksheetViewDO worksheet;
        HashMap<Integer, Node> analysisNodes;
        HashSet<Integer> addedWorksheets;

        root = new Node();
        if (managers == null || managers.size() == 0)
            return root;

        analysisNodes = new HashMap<Integer, Node>();
        for (i = 0; i < sm.item.count(); i++ ) {
            for (j = 0; j < sm.analysis.count(sm.item.get(i)); j++ ) {
                analysis = sm.analysis.get(sm.item.get(i), j);
                anode = new Node(8);
                anode.setCell(0, analysis.getTestName());
                anode.setCell(1, analysis.getMethodName());
                anode.setCell(2, analysisType.get(analysis.getTypeId()));
                anode.setCell(3, analysis.getStartedDate());
                anode.setCell(4, analysis.getReleasedDate());
                anode.setCell(5, analysisStatus.get(analysis.getStatusId()));
                anode.setCell(6, analysis.getPreAnalysisTest() + " : " +
                                 analysis.getPreAnalysisMethod());
                anode.setCell(7, analysisUnit.get(analysis.getUnitOfMeasureId()));
                anode.setData(analysis);
                analysisNodes.put(sm.analysis.get(sm.item.get(i), j).getId(), anode);
                root.add(anode);
                
                addedWorksheets = new HashSet<Integer>();
                for (k = 0; k < sm.worksheet.count(analysis); k++ ) {
                    worksheet = wms.get(sm.worksheet.get(analysis, k).getId()).getWorksheet();
                    anode.add(copyNode(worksheetNodes.get(worksheet.getId())));
                    addedWorksheets.add(worksheet.getId());
                    while (worksheet.getRelatedWorksheetId() != null) {
                        if ( !addedWorksheets.contains(worksheet.getRelatedWorksheetId())) {
                            wnode = copyNode(worksheetNodes.get(worksheet.getRelatedWorksheetId()));
                            wnode.setCell(0, "Related: " + wnode.getCell(0));
                            anode.add(wnode);
                            worksheet = wms.get(worksheet.getRelatedWorksheetId()).getWorksheet();
                            addedWorksheets.add(worksheet.getId());
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return root;
    }

    /**
     * copy a node and all of its children to a new object
     */
    public Node copyNode(Node node) {
        int i;
        Node copy;

        if (node == null)
            return null;

        copy = new Node(node.size());
        for (i = 0; i < node.size(); i++ )
            copy.setCell(i, node.getCell(i));
        for (i = 0; i < node.getChildCount(); i++ )
            copy.add(copyNode(node.getChildAt(i)), i);
        copy.setData(node.getData());
        copy.setType(node.getType());

        return copy;
    }

    private Node createWorksheetNode(WorksheetManager1 wm) {
        int i, j, k, l;
        Node qanode, qnode, wnode, vnode;
        WorksheetQcResultViewDO wq;
        WorksheetViewDO worksheet;
        WorksheetItemDO item;
        WorksheetAnalysisViewDO wa;

        worksheet = wm.getWorksheet();
        wnode = new Node(8);
        wnode.setType(WORKSHEET_LEAF);
        wnode.setCell(0, worksheet.getId());
        wnode.setCell(1, worksheet.getDescription());
        wnode.setCell(2, worksheet.getCreatedDate());
        wnode.setCell(3, worksheetStatus.get(worksheet.getStatusId()));
        wnode.setCell(4, worksheetFormat.get(worksheet.getFormatId()));
        wnode.setCell(5, worksheet.getSystemUser());
        wnode.setData(worksheet);

        for (i = 0; i < wm.item.count(); i++ ) {
            item = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(item); j++ ) {
                wa = wm.analysis.get(item, j);
                if (wa.getQcLotId() == null)
                    continue;
                qnode = new Node(1);
                qnode.setCell(0, wa.getDescription());
                // TODO type
                wnode.add(qnode);

                for (k = 0; k < wm.qcResult.count(wa); k++ ) {
                    wq = wm.qcResult.get(wa, k);
                    qanode = new Node(8);
                    qanode.setCell(0, new Boolean(false));
                    qanode.setCell(1, wq.getAnalyteName());
                    qanode.setCell(2, wq.getSortOrder());
                    l = 0;
                    while (l <= 30 && wq.getValueAt(l) != null) {
                        vnode = new Node(1);
                        vnode.setCell(0, wq.getValueAt(l));
                        qanode.add(vnode);
                    }
                    qanode.setData(wq);
                    qnode.add(qanode);

                    wnode.setCell(6, item.getPosition());
                    wnode.setCell(7, wa.getAccessionNumber());
                }
            }
        }
        return wnode;
    }
}