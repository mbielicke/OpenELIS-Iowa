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
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.manager.WorksheetManager1;
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
    private WorksheetManager1Bean worksheetManager;

    @EJB
    private PrinterCacheBean      printer;

    @EJB
    private LabelReportBean       labelReport;

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
        int i, j, index;
        HashMap<String, QueryData> param;
        Integer worksheetId;
        Path path;
        PrintStream ps;
        ReportStatus status;
        String accession, format, name1, name2, printer, printstat, started, users, worksheetPosition;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;
        WorksheetManager1 wMan;

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

        /*
         * fetch accession number counter and increment it
         */
        try {
            wMan = worksheetManager.fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching worksheet for id "+worksheetId, e);
            throw e;
        }

        status.setPercentComplete(50);
        session.setAttribute("WorksheetLabelReport", status);

        /*
         * print the labels and send it to printer
         */
        path = ReportUtil.createTempFile("worksheetLabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        for (i = 0; i < wMan.item.count(); i++) {
            wiDO = wMan.item.get(i);
            for (j = 0; j < wMan.analysis.count(wiDO); j++) {
                waVDO = wMan.analysis.get(wiDO, j);
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
                    
                if ("SM".equals(format))
                    labelReport.worksheetAnalysisSmallLabel(ps, accession, worksheetPosition,
                                                            name1, name2, started,
                                                            users);
                else
                    throw new Exception("Invalid report type.");
            }
        }
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }
}