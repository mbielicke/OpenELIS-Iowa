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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
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
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.CheckLabelValue;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
    protected Button                                               getDataButton, exportButton,
                    printButton, checkAllButton, uncheckAllButton, checkAllQcsButton,
                    uncheckAllQcsButton;

    @UiField
    protected TextBox<Integer>                                     accessionNumber;

    @UiField
    protected Dropdown<Integer>                                    printer;

    @UiField
    protected Table                                                table;

    @UiField
    protected Tree                                                 tree;

    protected SampleQCScreenUI                                     screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>>                 queryCall;

    protected AsyncCallbackUI<SampleManager1>                      fetchByIdCall, unlockCall;

    protected AsyncCallbackUI<ArrayList<WorksheetManager1>>        fetchWorksheetsByIdsCall;

    protected AsyncCallbackUI<QcManager>                           fetchQcByIdCall;

    protected Integer                                              previousAccession;

    protected HashSet<Integer>                                     qcIds;

    protected HashMap<Integer, HashSet<Integer>>                   analysisWorksheets,
                    analysisRelatedWorksheets;

    protected HashMap<Integer, Node>                               worksheetNodes;

    protected HashMap<Integer, WorksheetManager1>                  wms;

    protected HashMap<Integer, QcManager>                          qms;

    protected HashMap<Integer, Integer>                            relatedWorksheets;

    protected HashMap<Integer, String>                             analysisStatus, worksheetStatus,
                    worksheetFormatName, worksheetFormatCategories, qcType;

    protected HashMap<String, ArrayList<String>>                   worksheetFormat;

    protected DateHelper                                           dateHelper;

    private static final String                                    ANALYSIS_LEAF       = "analysis",
                    WORKSHEET_LEAF = "worksheet", QC_LEAF = "qc", ANALYTE_LEAF = "qcAnalyte";

    protected SampleManager1.Load                                  sampleElements[]    = {SampleManager1.Load.WORKSHEET};

    protected WorksheetManager1.Load                               worksheetElements[] = {WorksheetManager1.Load.DETAIL};

    public SampleQCScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Sample QC Screen"));

        try {
            CategoryCache.getBySystemNames("type_of_sample", "analysis_status", "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        initWidget(uiBinder.createAndBindUi(this));

        sm = null;
        wms = null;
        dateHelper = new DateHelper();
        dateHelper.setPattern(Messages.get().dateTimePattern());

        initialize();
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
        // screen fields
        //
        accessionNumber.setEnabled(true);

        accessionNumber.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                fireDataChange();
            }
        });

        table.setVisible(true);

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        tree.setEnabled(true);

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                table.setModel(getTableModel());
            }
        });

        tree.setAllowMultipleSelection(false);
        tree.setVisible(true);

        checkAllButton.setEnabled(false);
        uncheckAllButton.setEnabled(false);
        checkAllQcsButton.setEnabled(false);
        uncheckAllQcsButton.setEnabled(false);
        printButton.setEnabled(false);
        exportButton.setEnabled(false);
        getDataButton.setEnabled(true);

        analysisStatus = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status")) {
            analysisStatus.put(d.getId(), d.getEntry());
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
    @UiHandler("getDataButton")
    protected void getData(ClickEvent event) {
        Validation validation;

        finishEditing();
        validation = validate();

        if (Validation.Status.ERRORS.equals(validation.getStatus())) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        fetchByAccession(accessionNumber.getValue());
    }

    @UiHandler("exportButton")
    protected void export(ClickEvent event) {
        runReport();
    }

    @UiHandler("printButton")
    protected void print(ClickEvent event) {
        // TODO
    }

    @UiHandler("checkAllButton")
    protected void checkAll(ClickEvent event) {
        check("Y");
    }

    @UiHandler("uncheckAllButton")
    protected void uncheckAll(ClickEvent event) {
        check("N");
    }

    @UiHandler("checkAllQcsButton")
    protected void checkSimilarAnalytes(ClickEvent event) {
        checkAllAnalytes( ((WorksheetQcResultViewDO)tree.getNodeAt(tree.getSelectedNode())
                                                        .getData()).getQcAnalyteId(), "Y");
    }

    @UiHandler("uncheckAllQcsButton")
    protected void uncheckSimilarAnalytes(ClickEvent event) {
        checkAllAnalytes( ((WorksheetQcResultViewDO)tree.getNodeAt(tree.getSelectedNode())
                                                        .getData()).getQcAnalyteId(), "N");
    }

    /**
     * check all the analytes of a QC
     */
    protected void check(String check) {
        int index;
        Node node, child;

        clearErrors();
        index = tree.getSelectedNode();

        if (index == -1)
            return;
        node = tree.getNodeAt(index);

        tree.open(node);
        for (int i = 0; i < node.getChildCount(); i++ ) {
            child = node.getChildAt(i);
            ((CheckLabelValue)child.getCell(0)).setChecked(check);
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
                        if ("Y".equals( ((CheckLabelValue)qanode.getCell(0)).getChecked())) {
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
            worksheetNodes = null;
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        int i, j, k;
                        Integer analysisId, worksheetId;
                        ArrayList<Integer> wids;
                        SampleItemViewDO item;
                        AnalysisViewDO analysis;

                        sm = result;
                        wids = new ArrayList<Integer>();
                        analysisWorksheets = new HashMap<Integer, HashSet<Integer>>();
                        analysisRelatedWorksheets = new HashMap<Integer, HashSet<Integer>>();
                        for (i = 0; i < sm.item.count(); i++ ) {
                            item = sm.item.get(i);
                            for (j = 0; j < sm.analysis.count(item); j++ ) {
                                analysis = sm.analysis.get(item, j);
                                analysisId = analysis.getId();
                                analysisWorksheets.put(analysisId, new HashSet<Integer>());
                                analysisRelatedWorksheets.put(analysisId, new HashSet<Integer>());
                                for (k = 0; k < sm.worksheet.count(analysis); k++ ) {
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
                                        Iterator<Integer> iter;
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
                                            /*
                                             * only display worksheets that have
                                             * status working or complete
                                             */
                                            if ( ! (Constants.dictionary().WORKSHEET_COMPLETE.equals(wm.getWorksheet()
                                                                                                       .getStatusId()) || Constants.dictionary().WORKSHEET_WORKING.equals(wm.getWorksheet()
                                                                                                                                                                            .getStatusId())))
                                                continue;
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

                                            iter = qcIds.iterator();
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
                    }
                };
            }

            /*
             * fetch the sample manager
             */
            SampleService1.get().fetchByAccession(accession, sampleElements, fetchByIdCall);
        }
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
        StringBuilder sb;
        Node root, wnode, anode;
        AnalysisViewDO analysis;
        WorksheetViewDO worksheet;
        HashSet<Integer> addedWorksheets;

        root = new Node();
        if (sm == null || wms == null || wms.size() < 1)
            return root;

        sb = new StringBuilder();
        for (i = 0; i < sm.item.count(); i++ ) {
            for (j = 0; j < sm.analysis.count(sm.item.get(i)); j++ ) {
                analysis = sm.analysis.get(sm.item.get(i), j);
                if (analysisWorksheets.get(analysis.getId()) == null ||
                    analysisWorksheets.get(analysis.getId()).size() < 1)
                    continue;
                anode = new Node(1);
                anode.setType(ANALYSIS_LEAF);
                sb.setLength(0);
                sb.append(analysis.getTestName())
                  .append(", ")
                  .append(analysis.getMethodName())
                  .append(" [")
                  .append(analysisStatus.get(analysis.getStatusId()))
                  .append("]");
                anode.setCell(0, sb.toString());
                anode.setData(analysis);
                root.add(anode);

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
        return root;
    }

    /**
     * create the table
     */
    private ArrayList<Row> getTableModel() {
        int i, j;
        Row row;
        Node selected;
        AnalysisViewDO analysis;
        SampleItemViewDO item;
        WorksheetViewDO worksheet;
        WorksheetAnalysisViewDO wa;
        WorksheetItemDO witem;
        WorksheetQcResultViewDO wq;
        WorksheetManager1 wm;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        selected = tree.getNodeAt(tree.getSelectedNode());
        if (selected == null)
            return model;
        switch (selected.getType()) {
            case ANALYSIS_LEAF:
                /*
                 * build the display table for a selected analysis node
                 */
                analysis = selected.getData();
                row = new Row(2);
                row.setCell(0, Messages.get().startedDate());
                row.setCell(1, dateHelper.format(analysis.getStartedDate()));
                row.setData(analysis);
                model.add(row);

                row = new Row(2);
                row.setCell(0, Messages.get().gen_sampleType());
                item = sm.item.getById(analysis.getSampleItemId());
                row.setCell(1, (item != null) ? DataBaseUtil.toString(item.getTypeOfSample()) : "");
                row.setData(item);
                model.add(row);
                checkAllButton.setEnabled(false);
                uncheckAllButton.setEnabled(false);
                checkAllQcsButton.setEnabled(false);
                uncheckAllQcsButton.setEnabled(false);
                break;
            case WORKSHEET_LEAF:
                /*
                 * build the display table for a selected worksheet node
                 */
                worksheet = selected.getData();
                analysis = selected.getParent().getData();
                wm = wms.get(worksheet.getId());

                /*
                 * display the position of the analysis on this worksheet
                 */
                row = null;
                for (i = 0; i < wm.item.count(); i++ ) {
                    for (j = 0; j < wm.analysis.count(wm.item.get(i)); j++ ) {
                        wa = wm.analysis.get(wm.item.get(i), j);
                        if (analysis.getId().equals(wa.getAnalysisId())) {
                            row = new Row(2);
                            row.setCell(0, Messages.get().sampleQc_analysisPosition());
                            row.setCell(1, wm.item.get(i).getPosition());
                            model.add(row);
                            break;
                        }
                    }
                    if (row != null)
                        break;
                }
                row = new Row(2);
                row.setCell(0, Messages.get().gen_description());
                row.setCell(1, DataBaseUtil.toString(worksheet.getDescription()));
                row.setData(worksheet);
                model.add(row);
                checkAllButton.setEnabled(false);
                uncheckAllButton.setEnabled(false);
                checkAllQcsButton.setEnabled(false);
                uncheckAllQcsButton.setEnabled(false);
                break;
            case QC_LEAF:
                /*
                 * build the display table for a selected QC node
                 */
                wa = selected.getData();
                row = new Row(2);
                row.setCell(0, Messages.get().worksheet_position());
                witem = null;
                try {
                    witem = wms.get(wa.getWorksheetId()).item.getById(wa.getWorksheetItemId());
                    row.setCell(1, DataBaseUtil.toString(witem.getPosition()));
                } catch (NullPointerException e) {
                    row.setCell(1, "");
                }
                row.setData(witem);
                model.add(row);

                row = new Row(2);
                row.setCell(0, Messages.get().sample_accessionNum());
                row.setCell(1, DataBaseUtil.toString(wa.getAccessionNumber()));
                row.setData(wa);
                model.add(row);

                row = new Row(2);
                row.setCell(0, Messages.get().gen_type());
                try {
                    row.setCell(1, DataBaseUtil.toString(qcType.get(qms.get(wa.getQcId())
                                                                       .getQc()
                                                                       .getTypeId())));
                } catch (NullPointerException e) {
                    row.setCell(1, "");
                }
                row.setData(qms.get(wa.getQcId()));
                model.add(row);
                checkAllButton.setEnabled(true);
                uncheckAllButton.setEnabled(true);
                checkAllQcsButton.setEnabled(false);
                uncheckAllQcsButton.setEnabled(false);
                break;
            case ANALYTE_LEAF:
                /*
                 * build the display table for a selected QC analyte node
                 */
                wq = selected.getData();
                worksheet = selected.getParent().getParent().getData();
                i = 0;
                for (String field : worksheetFormat.get(worksheetFormatCategories.get(worksheet.getFormatId()))) {
                    if (wq.getValueAt(i) != null) {
                        row = new Row(2);
                        row.setCell(0, field);
                        row.setCell(1, wq.getValueAt(i));
                        model.add(row);
                    }
                    i++ ;
                }
                checkAllButton.setEnabled(false);
                uncheckAllButton.setEnabled(false);
                checkAllQcsButton.setEnabled(true);
                uncheckAllQcsButton.setEnabled(true);
                break;
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
        int i, j, k;
        StringBuilder sb;
        Node qanode, qnode, wnode;
        WorksheetQcResultViewDO wq;
        WorksheetViewDO worksheet;
        WorksheetItemDO item;
        WorksheetAnalysisViewDO wa;

        worksheet = wm.getWorksheet();
        wnode = new Node(1);
        wnode.setType(WORKSHEET_LEAF);
        sb = new StringBuilder();
        sb.append(Messages.get().gen_worksheet())
          .append(" ")
          .append(worksheet.getId())
          .append(" [")
          .append(worksheetStatus.get(worksheet.getStatusId()))
          .append("]");
        wnode.setCell(0, sb.toString());
        wnode.setData(worksheet);

        for (i = 0; i < wm.item.count(); i++ ) {
            item = wm.item.get(i);
            for (j = 0; j < wm.analysis.count(item); j++ ) {
                wa = wm.analysis.get(item, j);
                if (wa.getQcLotId() == null)
                    continue;
                qnode = new Node(1);
                qnode.setType(QC_LEAF);
                sb.setLength(0);
                sb.append(Messages.get().QC()).append(" ").append(wa.getDescription());
                qnode.setCell(0, sb.toString());
                qnode.setData(wa);
                wnode.add(qnode);

                for (k = 0; k < wm.qcResult.count(wa); k++ ) {
                    wq = wm.qcResult.get(wa, k);
                    qanode = new Node(1);
                    qanode.setType(ANALYTE_LEAF);
                    qanode.setCell(0, new CheckLabelValue("Y", wq.getAnalyteName()));
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
                            ((CheckLabelValue)qanode.getCell(0)).setChecked(check);
                            if (tree.isDisplayed(qanode))
                                tree.refreshNode(qanode);
                        }
                    }
                }
            }
        }
    }
}