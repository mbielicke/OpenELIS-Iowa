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
import java.util.Iterator;
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
import org.openelis.manager.QcManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.QcMeta;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
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

public class SampleQCScreenUI extends Screen {

    @UiTemplate("SampleQCScreen.ui.xml")
    interface SampleQcUiBinder extends UiBinder<Widget, SampleQCScreenUI> {
    };

    public static final SampleQcUiBinder                           uiBinder            = GWT.create(SampleQcUiBinder.class);

    protected SampleManager1                                       sm;

    protected HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> selected;

    protected ModulePermission                                     userPermission;

    @UiField
    protected Button                                               query, commit, abort,
                    optionsButton, run, checkAllButton, uncheckAllButton;

    @UiField
    protected TextBox<Integer>                                     id;

    // @UiField
    // protected Dropdown<Integer> printer;

    @UiField
    protected Table                                                table;

    @UiField
    protected Tree                                                 tree;

    protected SampleQCScreenUI                                     screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>>                 queryCall;

    protected AsyncCallbackUI<SampleManager1>                      fetchByIdCall, unlockCall;

    protected AsyncCallbackUI<ArrayList<WorksheetManager1>>        fetchWorksheetsByIdsCall;

    protected AsyncCallbackUI<QcManager>                           fetchQcByIdCall;

    protected HashSet<Integer>                                     qcIds;

    protected HashMap<Integer, HashSet<Integer>>                   analysisWorksheets,
                    analysisRelatedWorksheets;

    protected HashMap<Integer, Node>                               worksheetNodes;

    protected HashMap<Integer, WorksheetManager1>                  wms;

    protected HashMap<Integer, QcManager>                          qms;

    protected HashMap<Integer, Integer>                            relatedWorksheets;

    protected HashMap<Integer, String>                             analysisStatus, analysisUnit,
                    worksheetStatus, worksheetFormatName, worksheetFormatCategories, qcType;

    protected HashMap<String, ArrayList<String>>                   worksheetFormat;
    protected HashMap<Integer, String>                             qcAnalyteList;

    private static final String                                    ANALYSIS_LEAF       = "analysis",
                    WORKSHEET_LEAF = "worksheet",
                    QC_LEAF = "qc",
                    ANALYTE_LEAF = "qcAnalyte",
                    VALUE_LEAF = "value";

    protected SampleManager1.Load                                  sampleElements[]    = {SampleManager1.Load.WORKSHEET};

    protected WorksheetManager1.Load                               worksheetElements[] = {WorksheetManager1.Load.DETAIL};

    public SampleQCScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Sample QC Screen"));

        try {
            CategoryCache.getBySystemNames("type_of_sample",
                                           "analysis_status",
                                           "unit_of_measure",
                                           "worksheet_status",
                                           "test_worksheet_format");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        initWidget(uiBinder.createAndBindUi(this));

        sm = null;
        wms = null;
        qcAnalyteList = null;

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Sample QC Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<String> fields;

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
                if (sm != null)
                    id.setText(DataBaseUtil.toString(sm.getSample().getAccessionNumber()));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : id;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(DISPLAY));
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(DISPLAY) || event.getCol() != 0)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                checkAllAnalytes((Integer)table.getRowAt(event.getRow()).getData(),
                                 (String)table.getRowAt(event.getRow()).getCell(0));
            }
        });

        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(isState(DISPLAY));
            }
        });

        tree.setVisible(true);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                checkAllButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                uncheckAllButton.setEnabled(isState(DISPLAY));
            }
        });

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

        worksheetFormatName = new HashMap<Integer, String>();
        worksheetFormatCategories = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("test_worksheet_format")) {
            worksheetFormatName.put(d.getId(), d.getEntry());
            worksheetFormatCategories.put(d.getId(), d.getSystemName());
        }

        worksheetFormat = new HashMap<String, ArrayList<String>>();
        for (Integer key : worksheetFormatCategories.keySet()) {
            fields = new ArrayList<String>();
            for (DictionaryDO d : CategoryCache.getBySystemName(worksheetFormatCategories.get(key))) {
                fields.add(d.getEntry());
            }
            worksheetFormat.put(worksheetFormatCategories.get(key), fields);
        }

        qcType = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("qc_type")) {
            qcType.put(d.getId(), d.getEntry());
        }
    }

    /*
     * basic button methods
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        sm = null;
        wms = null;
        qcAnalyteList = null;
        worksheetNodes = null;
        tree.setRoot(getRoot());
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
                wms = null;
                qcAnalyteList = null;
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

    @UiHandler("checkAllButton")
    protected void checkAll(ClickEvent event) {
        check("Y");
    }

    @UiHandler("uncheckAllButton")
    protected void uncheckAll(ClickEvent event) {
        check("N");
    }

    /**
     * check all the analytes of a QC
     */
    protected void check(String reportable) {
        int index;
        Node node, child;

        clearErrors();
        if (tree.getSelectedNodes().length > 1) {
            getWindow().setError(Messages.get().qc_multiQcCheckNotAllowed());
            return;
        }
        index = tree.getSelectedNode();

        if (index == -1)
            return;
        node = tree.getNodeAt(index);
        /*
         * if an analyte node is selected, get the QC node that is the parent
         */
        if (ANALYTE_LEAF.equals(node.getType())) {
            node = node.getParent();
        } else if (QC_LEAF.equals(node.getType())) {

        } else {
            getWindow().setError(Messages.get().qc_qcCheckNodeNotAllowed());
            return;
        }

        tree.open(node);
        for (int i = 0; i < node.getChildCount(); i++ ) {
            child = node.getChildAt(i);
            child.setCell(0, reportable);
            tree.refreshNode(child);
        }
    }

    // TODO
    /**
     * create a list of worksheet managers an results with QC data for the
     * checked QCs
     */
    protected HashMap<Integer, ArrayList<Object>> runReport() {
        int i, j, k, l;
        Node anode, wnode, qnode, qanode;
        HashMap<Integer, ArrayList<Object>> qcs;

        qcs = new HashMap<Integer, ArrayList<Object>>();
        for (i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            anode = tree.getRoot().getChildAt(i);
            for (j = 0; j < anode.getChildCount(); j++ ) {
                wnode = anode.getChildAt(j);
                for (k = 0; k < wnode.getChildCount(); k++ ) {
                    qnode = wnode.getChildAt(k);
                    for (l = 0; l < qnode.getChildCount(); l++ ) {
                        qanode = qnode.getChildAt(l);
                        if ("Y".equals(qanode.getCell(0))) {
                            if (qcs.get( ((AnalysisViewDO)anode.getData()).getId()) == null)
                                qcs.put( ((AnalysisViewDO)anode.getData()).getId(),
                                        new ArrayList<Object>());
                            qcs.get( ((AnalysisViewDO)anode.getData()).getId())
                               .add(wms.get(wnode.getCell(0)));
                        }
                    }
                }
            }
        }
        return qcs;
    }

    /**
     * query for all of the QC data related to the accession number
     */
    private void fetchByAccession(Integer accession) {
        if (accession == null) {
            sm = null;
            wms = null;
            qcAnalyteList = null;
            worksheetNodes = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            /*
             * fetch the sample manager
             */
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
                        analysisRelatedWorksheets = new HashMap<Integer, HashSet<Integer>>();
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
                        if (wids.size() > 0) {
                            qcIds = new HashSet<Integer>();
                            /*
                             * fetch worksheet managers associated with the
                             * sample
                             */
                            if (fetchWorksheetsByIdsCall == null) {
                                fetchWorksheetsByIdsCall = new AsyncCallbackUI<ArrayList<WorksheetManager1>>() {
                                    public void success(ArrayList<WorksheetManager1> result) {
                                        Integer qcId;
                                        ArrayList<WorksheetManager1> worksheets;
                                        ArrayList<Integer> wids;

                                        worksheets = result;

                                        if (sm == null || worksheets == null ||
                                            worksheets.size() < 1) {
                                            sm = null;
                                            return;
                                        }
                                        if (wms == null)
                                            wms = new HashMap<Integer, WorksheetManager1>();
                                        wids = new ArrayList<Integer>();
                                        for (WorksheetManager1 wm : worksheets) {
                                            getQcIds(wm);
                                            if (wms.get(wm.getWorksheet().getId()) == null)
                                                wms.put(wm.getWorksheet().getId(), wm);
                                            if (wm.getWorksheet().getRelatedWorksheetId() != null) {
                                                relatedWorksheets.put(wm.getWorksheet().getId(),
                                                                      wm.getWorksheet()
                                                                        .getRelatedWorksheetId());
                                                if ( !wms.containsKey(wm.getWorksheet()
                                                                        .getRelatedWorksheetId()))
                                                    wids.add(wm.getWorksheet()
                                                               .getRelatedWorksheetId());
                                            }
                                        }
                                        /*
                                         * keep querying for more worksheets
                                         * while there are more related
                                         * worksheets that haven't been queried
                                         * for yet
                                         */
                                        if (wids.size() > 0)
                                            WorksheetService1.get()
                                                             .fetchByIds(wids,
                                                                         worksheetElements,
                                                                         fetchWorksheetsByIdsCall);
                                        else {
                                            /*
                                             * fetch the QC managers for the QCs
                                             * on all of the worksheet managers
                                             */
                                            qms = new HashMap<Integer, QcManager>();

                                            Iterator<Integer> iter = qcIds.iterator();
                                            while (iter.hasNext()) {
                                                qcId = iter.next();
                                                try {
                                                    qms.put(qcId, QcService.get().fetchById(qcId));
                                                } catch (Exception e) {
                                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                                    Window.alert("There was a problem getting QC data for QC: " +
                                                                 qcId);
                                                }
                                            }

                                            qcAnalyteList = new HashMap<Integer, String>();
                                            worksheetNodes = new HashMap<Integer, Node>();
                                            for (Integer id : wms.keySet()) {
                                                worksheetNodes.put(id,
                                                                   createWorksheetNode(wms.get(id)));
                                            }
                                            tree.setRoot(getRoot());
                                        }
                                    }

                                    public void notFound() {
                                        setDone(Messages.get().gen_noRecordsFound());
                                    }

                                    public void failure(Throwable e) {
                                        Window.alert(Messages.get().gen_fetchFailed() +
                                                     e.getMessage());
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
                        } else {
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

    /**
     * add all QC IDs that are in the worksheet manager to a global list
     */
    private void getQcIds(WorksheetManager1 wm) {
        int i, j;
        WorksheetItemDO item;

        for (i = 0; i < wm.item.count(); i++ ) {
            item = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(item); j++ ) {
                if (wm.analysis.get(item, j).getQcId() != null)
                    qcIds.add(wm.analysis.get(item, j).getQcId());
            }
        }
    }

    /**
     * create the tree
     */
    private Node getRoot() {
        int i, j, k;
        Node root, wnode, anode;
        AnalysisViewDO analysis;
        WorksheetViewDO worksheet;
        HashSet<Integer> addedWorksheets;

        root = new Node();
        if (sm == null)
            return root;

        for (i = 0; i < sm.item.count(); i++ ) {
            for (j = 0; j < sm.analysis.count(sm.item.get(i)); j++ ) {
                analysis = sm.analysis.get(sm.item.get(i), j);
                anode = new Node(2);
                anode.setType(ANALYSIS_LEAF);

                anode.setCell(0, analysis.getTestName() +
                                 ", " +
                                 analysis.getMethodName() +
                                 "(" +
                                 analysisStatus.get(analysis.getStatusId()) +
                                 ", " +
                                 sm.item.get(i).getTypeOfSample() +
                                 ", " +
                                 analysisUnit.get(analysis.getUnitOfMeasureId()) +
                                 ")" +
                                 (analysis.getStartedDate() == null ? ""
                                                                   : "[" +
                                                                     analysis.getStartedDate() +
                                                                     "]"));
                if (analysis.getPreAnalysisTest() != null)
                    anode.setCell(1, "Prep Test - " + analysis.getPreAnalysisTest() + " : " +
                                     analysis.getPreAnalysisMethod());
                anode.setData(analysis);
                root.add(anode);

                if (wms == null || wms.size() == 0)
                    continue;
                addedWorksheets = new HashSet<Integer>();
                for (k = 0; k < sm.worksheet.count(analysis); k++ ) {
                    worksheet = wms.get(sm.worksheet.get(analysis, k).getId()).getWorksheet();
                    anode.add(copyNode(worksheetNodes.get(worksheet.getId())));
                    addedWorksheets.add(worksheet.getId());
                    while (worksheet.getRelatedWorksheetId() != null) {
                        if ( !addedWorksheets.contains(worksheet.getRelatedWorksheetId())) {
                            wnode = copyNode(worksheetNodes.get(worksheet.getRelatedWorksheetId()));
                            wnode.setCell(0, "Related " + wnode.getCell(0));
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
        setState(DISPLAY);
        table.setModel(getTableModel());
        return root;
    }

    /**
     * create the table
     */
    private ArrayList<Row> getTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (qcAnalyteList == null)
            return model;
        for (Integer id : qcAnalyteList.keySet()) {
            row = new Row(2);
            row.setCell(0, "Y");
            row.setCell(1, qcAnalyteList.get(id));
            row.setData(id);
            model.add(row);
        }

        return model;
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

    /**
     * Create nodes from the data in this worksheet manager
     */
    private Node createWorksheetNode(WorksheetManager1 wm) {
        int i, j, k, l;
        Node qanode, qnode, wnode, vnode;
        WorksheetQcResultViewDO wq;
        WorksheetViewDO worksheet;
        WorksheetItemDO item;
        WorksheetAnalysisViewDO wa;

        worksheet = wm.getWorksheet();
        wnode = new Node(2);
        wnode.setType(WORKSHEET_LEAF);
        wnode.setCell(0, "Worksheet:  ");
        wnode.setCell(1,
                      worksheet.getId() +
                                      "(" +
                                      worksheetStatus.get(worksheet.getStatusId()) +
                                      ", " +
                                      worksheet.getSystemUser() +
                                      "|" +
                                      worksheet.getDescription() +
                                      ")" +
                                      "[" +
                                      worksheet.getCreatedDate() +
                                      "]" +

                                      (worksheet.getFormatId() == null ? ""
                                                                      : " - " +
                                                                        worksheetFormatName.get(worksheet.getFormatId())));
        wnode.setData(worksheet);

        for (i = 0; i < wm.item.count(); i++ ) {
            item = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(item); j++ ) {
                wa = wm.analysis.get(item, j);
                if (wa.getQcLotId() == null)
                    continue;
                qnode = new Node(2);
                qnode.setType(QC_LEAF);
                qnode.setCell(0, wa.getDescription() + "(" + item.getPosition() + ", " +
                                 wa.getAccessionNumber() + ")");
                qnode.setCell(1, qcType.get(qms.get(wa.getQcId()).getQc().getTypeId()));
                qnode.setData(wa);
                wnode.add(qnode);

                for (k = 0; k < wm.qcResult.count(wa); k++ ) {
                    wq = wm.qcResult.get(wa, k);
                    qanode = new Node(2);
                    qanode.setType(ANALYTE_LEAF);
                    qanode.setCell(0, "Y");
                    qanode.setCell(1, wq.getAnalyteName());
                    l = 0;
                    for (String field : worksheetFormat.get(worksheetFormatCategories.get(worksheet.getFormatId()))) {
                        if (wq.getValueAt(l) != null) {
                            vnode = new Node(1);
                            vnode.setType(VALUE_LEAF);
                            vnode.setCell(0, field + ":" + wq.getValueAt(l));
                            qanode.add(vnode);
                        }
                        l++ ;
                    }
                    if (qcAnalyteList.get(wq.getQcAnalyteId()) == null)
                        qcAnalyteList.put(wq.getQcAnalyteId(), wq.getAnalyteName());
                    qanode.setData(wq);
                    qnode.add(qanode);
                }
            }
        }
        return wnode;
    }

    /**
     * Check all of a certain analyte in the QCs for the sample
     */
    private void checkAllAnalytes(Integer qcAnalyteId, String check) {
        int i, j, k, l;
        Node anode, wnode, qnode, qanode;

        for (i = 0; i < tree.getRoot().getChildCount(); i++ ) {
            anode = tree.getRoot().getChildAt(i);
            for (j = 0; j < anode.getChildCount(); j++ ) {
                wnode = anode.getChildAt(j);
                for (k = 0; k < wnode.getChildCount(); k++ ) {
                    qnode = wnode.getChildAt(k);
                    for (l = 0; l < qnode.getChildCount(); l++ ) {
                        qanode = qnode.getChildAt(l);
                        if (qcAnalyteId.equals( ((WorksheetQcResultViewDO)qanode.getData()).getQcAnalyteId())) {
                            qanode.setCell(0, check);
                            if (tree.isDisplayed(qanode))
                                tree.refreshNode(qanode);
                        }
                    }
                }
            }
        }
    }
}