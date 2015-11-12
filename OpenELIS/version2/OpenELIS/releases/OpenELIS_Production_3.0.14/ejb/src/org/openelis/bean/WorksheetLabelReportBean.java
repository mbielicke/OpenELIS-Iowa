package org.openelis.bean;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1Accessor;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class WorksheetLabelReportBean {

    @Resource
    private SessionContext        ctx;

    @EJB
    private SessionCacheBean      session;

    @EJB
    private DictionaryCacheBean   dictionaryCache;

    @EJB
    private LabelReportBean       labelReport;

    @EJB
    private PrinterCacheBean      printer;

    @EJB
    private QcBean                qc;

    @EJB
    private WorksheetAnalysisBean worksheetAnalysis;

    @EJB
    private WorksheetItemBean     worksheetItem;

    @EJB
    private WorksheetManager1Bean worksheetManager;

    private static final Logger   log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for new setup accession login labels
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, format;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("WORKSHEET_ID", Prompt.Type.INTEGER).setPrompt("Worksheet #:")
                                                                 .setWidth(130)
                                                                 .setHidden(true)
                                                                 .setRequired(true));

            format = new ArrayList<OptionListItem>();
            format.add(new OptionListItem("SM", "Small (.5 X 1)"));

            p.add(new Prompt("FORMAT", Prompt.Type.ARRAY).setPrompt("Format:")
                                                         .setWidth(200)
                                                         .setOptionList(format)
                                                         .setMultiSelect(false)
                                                         .setRequired(true));

            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(124)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create result prompts", e);
            throw e;
        }
    }

    /*
     * Prints the new sample login labels starting at last printed accession
     * number
     */
    @RolesAllowed("worksheet-select")
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i, index, dilutionCol;
        ArrayList<IdNameVO> worksheetColumns;
        ArrayList<WorksheetAnalysisViewDO> waVDOs;
        ArrayList<WorksheetQcResultViewDO> wqrVDOs;
        ArrayList<WorksheetResultViewDO> wrVDOs;
        DictionaryDO dDO;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> waVDOsByItemId;
        HashMap<Integer, ArrayList<WorksheetResultViewDO>> wrVDOsByAnalysisId;
        HashMap<Integer, ArrayList<WorksheetQcResultViewDO>> wqrVDOsByAnalysisId;
        HashMap<String, QueryData> param;
        Integer worksheetId;
        Path path;
        PrintStream ps;
        QcViewDO qcVDO;
        ReportStatus status;
        String accession, dilution, format, name1, name2, printer, printstat, qcCode,
               qcLink, started, users, worksheetPosition;
        WorksheetAnalysisViewDO waVDO1;
        WorksheetItemDO wiDO1;
        WorksheetManager1 wMan;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("WorksheetLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        worksheetId = ReportUtil.getIntegerParameter(param, "WORKSHEET_ID");
        format = ReportUtil.getStringParameter(param, "FORMAT");
        printer = ReportUtil.getStringParameter(param, "BARCODE");

        if (worksheetId == null || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the worksheet id and printer for this report");

        status.setMessage("Outputing report").setPercentComplete(0);
        session.setAttribute("WorksheetLabelReport", status);

        try {
            wMan = worksheetManager.fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching worksheet for id "+worksheetId, e);
            throw e;
        }

        status.setPercentComplete(50);
        session.setAttribute("WorksheetLabelReport", status);

        try {
            dilutionCol = -1;
            worksheetColumns = worksheetManager.getColumnNames(wMan.getWorksheet().getFormatId());
            for (i = 0; i < worksheetColumns.size(); i++) {
                if ("dilut_factor".equals(worksheetColumns.get(i))) {
                    dilutionCol = i;
                    break;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching worksheet columns for id "+worksheetId, e);
            throw e;
        }
        
        /*
         * print the labels and send it to printer
         */
        path = ReportUtil.createTempFile("worksheetLabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        waVDOsByItemId = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
        if (WorksheetManager1Accessor.getAnalyses(wMan) != null) {
            for (WorksheetAnalysisViewDO data : WorksheetManager1Accessor.getAnalyses(wMan)) {
                waVDOs = waVDOsByItemId.get(data.getWorksheetItemId());
                if (waVDOs == null) {
                    waVDOs = new ArrayList<WorksheetAnalysisViewDO>();
                    waVDOsByItemId.put(data.getWorksheetItemId(), waVDOs);
                }
                waVDOs.add(data);
            }
        }
        wrVDOsByAnalysisId = new HashMap<Integer, ArrayList<WorksheetResultViewDO>>();
        if (WorksheetManager1Accessor.getResults(wMan) != null) {
            for (WorksheetResultViewDO data : WorksheetManager1Accessor.getResults(wMan)) {
                wrVDOs = wrVDOsByAnalysisId.get(data.getWorksheetAnalysisId());
                if (wrVDOs == null) {
                    wrVDOs = new ArrayList<WorksheetResultViewDO>();
                    wrVDOsByAnalysisId.put(data.getWorksheetAnalysisId(), wrVDOs);
                }
                wrVDOs.add(data);
            }
        }
        wqrVDOsByAnalysisId = new HashMap<Integer, ArrayList<WorksheetQcResultViewDO>>();
        if (WorksheetManager1Accessor.getQcResults(wMan) != null) {
            for (WorksheetQcResultViewDO data : WorksheetManager1Accessor.getQcResults(wMan)) {
                wqrVDOs = wqrVDOsByAnalysisId.get(data.getWorksheetAnalysisId());
                if (wqrVDOs == null) {
                    wqrVDOs = new ArrayList<WorksheetQcResultViewDO>();
                    wqrVDOsByAnalysisId.put(data.getWorksheetAnalysisId(), wqrVDOs);
                }
                wqrVDOs.add(data);
            }
        }
        for (WorksheetItemDO wiDO : WorksheetManager1Accessor.getItems(wMan)) {
            waVDOs = waVDOsByItemId.get(wiDO.getId());
            if (waVDOs != null && waVDOs.size() > 0) {
                for (WorksheetAnalysisViewDO waVDO : waVDOs) {
                    if (!wiDO.getId().equals(waVDO.getWorksheetItemId()))
                        break;
                    
                    /*
                     * Find the first Result or QC Result belonging to this Analysis
                     */
                    wrVDO = null;
                    wqrVDO = null;
                    if (waVDO.getAnalysisId() != null) {
                        wrVDOs = wrVDOsByAnalysisId.get(waVDO.getId());
                        if (wrVDOs != null && wrVDOs.size() > 0)
                            wrVDO = wrVDOs.get(0);
                    } else if (waVDO.getQcLotId() != null) {
                        wqrVDOs = wqrVDOsByAnalysisId.get(waVDO.getId());
                        if (wqrVDOs != null && wqrVDOs.size() > 0)
                            wqrVDO = wqrVDOs.get(0);
                    }
                    accession = waVDO.getAccessionNumber();
                    worksheetPosition = waVDO.getWorksheetId() + "." + wiDO.getPosition();
                    if (waVDO.getAnalysisId() != null) {
                        name1 = waVDO.getTestName();
                        name2 = waVDO.getMethodName();
                    } else if (waVDO.getQcLotId() != null) {
                        index = waVDO.getDescription().indexOf("(");
                        name1 = waVDO.getDescription().substring(0, index - 1);
                        name2 = waVDO.getDescription().substring(index + 1, waVDO.getDescription().length() - 1);
                    } else {
                        name1 = "";
                        name2 = "";
                    }
                    
                    if (waVDO.getStartedDate() != null)
                        started = ReportUtil.toString(waVDO.getStartedDate(), Messages.get().dateTimePattern());
                    else
                        started = ReportUtil.toString(wMan.getWorksheet().getCreatedDate(), Messages.get().dateTimePattern());
                    if (waVDO.getSystemUsers() != null && waVDO.getSystemUsers().length() > 0)
                        users = waVDO.getSystemUsers();
                    else
                        users = wMan.getWorksheet().getSystemUser();
                        
                    qcCode = "";
                    waVDO1 = null;
                    wiDO1 = null;
                    if (waVDO.getWorksheetAnalysisId() != null) {
                        waVDO1 = worksheetAnalysis.fetchViewById(waVDO.getWorksheetAnalysisId());
                        wiDO1 = worksheetItem.fetchById(waVDO1.getWorksheetItemId());
                        qcVDO = qc.fetchById(waVDO.getQcId());
                        if (qcVDO.getTypeId() != null) {
                            dDO = dictionaryCache.getById(qcVDO.getTypeId());
                            if (dDO.getCode() != null)
                                qcCode = dDO.getCode();
                        }
                    }
    
                    if ("SM".equals(format)) {
                        qcLink = "";
                        if (waVDO1 != null && wiDO1 != null)
                            qcLink = waVDO1.getAccessionNumber() + " (" + wiDO1.getPosition() + ")";
                        
                        users = users.substring(0, Math.min(8, users.length()));
                        labelReport.worksheetAnalysisSmallLabel(ps, accession, worksheetPosition,
                                                                name1, name2, started,
                                                                users, qcLink);
                    } else if ("DI".equals(format)) {
                        if (waVDO1 != null)
                            accession = waVDO1.getAccessionNumber() + "@" + qcCode;
    
                        dilution = "";
                        if (waVDO.getAnalysisId() != null) {
                            if (dilutionCol != -1 && wrVDO != null)
                                dilution = wrVDO.getValueAt(dilutionCol);
                            labelReport.worksheetAnalysisDilutionLabel(ps, accession, worksheetPosition,
                                                                       dilution, name1, name2);
                        } else if (waVDO.getQcLotId() != null) {
                            if (dilutionCol != -1 && wqrVDO != null)
                                dilution = wqrVDO.getValueAt(dilutionCol);
                            if (waVDO1 == null && !worksheetPosition.equals(accession))
                                labelReport.worksheetAnalysisDilutionLabel(ps, name1, worksheetPosition,
                                                                           dilution, "(" + accession + ")", name2);
                            else if (waVDO1 != null)
                                labelReport.worksheetAnalysisDilutionLabel(ps, accession, worksheetPosition,
                                                                           dilution, name1, name2);
                            else 
                                labelReport.worksheetAnalysisDilutionLabel(ps, name1, worksheetPosition,
                                                                           dilution, name1, name2);
                        }
                    } else {
                        throw new Exception("Invalid report type.");
                    }
                }
            }
        }
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }
}