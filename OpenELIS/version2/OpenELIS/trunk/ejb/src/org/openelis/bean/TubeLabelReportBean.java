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
import javax.naming.LimitExceededException;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SystemVariableDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.StringInt;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class TubeLabelReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  sysvar;

    @EJB
    private PrinterCacheBean    printer;

    @EJB
    private LabelReportBean     labelReport;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for tube labels
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SAMPLES", Prompt.Type.INTEGER).setPrompt("# of Samples:")
                                                            .setWidth(25)
                                                            .setLength(3)
                                                            .setRequired(true)
                                                            .setDefaultValue("1"));

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
    @RolesAllowed("r_tubelabel-select")
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int samples;
        Path path;
        String printer, printstat;
        ReportStatus status;
        HashMap<String, QueryData> param;
        SystemVariableDO data;
        PrintStream ps;
        StringInt nextTubeNumber, lastTubeNumber;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("TubeLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        samples = 0;
        try {
            samples = Integer.parseInt(ReportUtil.getSingleParameter(param, "SAMPLES"));
        } catch (Exception e) {
            throw new InconsistencyException("You must specify valid number for samples (<= 300)");
        } finally {
            if (samples > 300)
                throw new InconsistencyException("Number of sample labels can not exceed 300");
        }
        printer = ReportUtil.getSingleParameter(param, "BARCODE");

        if (DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify # of samples and printer for this report");

        status.setMessage("Outputing report").setPercentComplete(0);

        /*
         * fetch tube number counter and increment it
         */
        data = null;
        try {
            data = sysvar.fetchForUpdateByName("last_tube_number");
            lastTubeNumber = new StringInt(3, 3, data.getValue());
            nextTubeNumber = (StringInt) lastTubeNumber.clone();
            nextTubeNumber.add(1);
            lastTubeNumber.add(samples);
            data.setValue(lastTubeNumber.toString());
            sysvar.updateAsSystem(data);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "System variable 'last_tube_number' is not available or valid",
                    e);
            if (data != null)
                sysvar.abortUpdate(data.getId());
            throw e;
        }

        log.fine("Starting at tube # " + data.getValue() + " for " + samples + " labels");

        status.setPercentComplete(50);
        /*
         * print the labels and send it to printer
         */
        path = ReportUtil.createTempFile("tubelabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        while (samples-- > 0) {
            labelReport.tubeLabel(ps, nextTubeNumber.toString());
            try {
                nextTubeNumber.add(1);
            } catch (LimitExceededException e) {
                ps.close();
                throw new InconsistencyException("Tube number increment error: "+e.getMessage());
            }    
        }            
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }
}