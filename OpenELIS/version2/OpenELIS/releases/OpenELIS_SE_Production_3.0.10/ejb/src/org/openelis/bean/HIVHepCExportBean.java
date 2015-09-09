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
package org.openelis.bean;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class HIVHepCExportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;
    @EJB
    private SystemVariableBean  systemVariable;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("BEGIN_RELEASED", Prompt.Type.DATETIME).setPrompt("Begin Released:")
                                                                    .setWidth(130)
                                                                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                    .setDatetimeEndCode(Prompt.Datetime.SECOND)
                                                                    .setRequired(true));

            p.add(new Prompt("END_RELEASED", Prompt.Type.DATETIME).setPrompt("End Released:")
                                                                  .setWidth(130)
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.SECOND)
                                                                  .setRequired(true));

            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /*
     * Method for calling from cron job
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() {
        try {
            runReport(null);
        } catch (Exception ignE) {
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        Calendar fromDate, toDate;
        Connection con;
        Date beginReleased, endReleased;
        Path tmp;
        HashMap<String, Object> jparam;
        HashMap<String, QueryData> param;
        JRExporter jexport;
        JasperPrint jprint;
        JasperReport jreport;
        OutputStream out;
        ReportStatus status;
        SimpleDateFormat format, fileFormat;
        String analyteIds, dir, exportDirectory;
        URL url;

        format = new SimpleDateFormat(Messages.get().dateTimeSecondPattern());
        fileFormat = new SimpleDateFormat("yyyyMMdd");

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("HIVHepCExport", status);

        /*
         * recover all the params and build a specific where clause
         */
        beginReleased = null;
        endReleased = null;
        if (paramList != null) {
            param = ReportUtil.getMapParameter(paramList);
            beginReleased = ReportUtil.getTimestampParameter(param, "BEGIN_RELEASED");
            endReleased = ReportUtil.getTimestampParameter(param, "END_RELEASED");
        }

        if (beginReleased == null || endReleased == null) {
            /*
             * take all the released specimens between yesterday at 5:01 pm to 
             * today at 5 pm
             */
            fromDate = Calendar.getInstance();
            fromDate.set(Calendar.HOUR_OF_DAY, 17);
            fromDate.set(Calendar.MINUTE, 01);
            fromDate.set(Calendar.SECOND, 00);
            fromDate.add(Calendar.DAY_OF_MONTH, -1);
            beginReleased = fromDate.getTime();

            toDate = Calendar.getInstance();
            toDate.set(Calendar.HOUR_OF_DAY, 17);
            toDate.set(Calendar.MINUTE, 00);
            toDate.set(Calendar.SECOND, 00);
            endReleased = toDate.getTime();
        }

        try {
            analyteIds = systemVariable.fetchByName("hiv_hepc_transfer_analytes").getValue();
            analyteIds = " and r.analyte_id in (" + analyteIds + ")";
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'hiv_hepc_transfer_analytes' is not available");
            throw new Exception("System variable 'hiv_hepc_transfer_analytes' is not available");
        }

        try {
            exportDirectory = systemVariable.fetchByName("hiv_hepc_transfer_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'hiv_hepc_transfer_directory' is not available");
            throw new Exception("System variable 'hiv_hepc_transfer_directory' is not available");
        }

        try {
            tmp = Paths.get(exportDirectory, "hivHepCExport"+fileFormat.format(new Date())+".csv");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Could not open temp file for writing.");
            throw new Exception("Could not open temp file for writing.");
        }

        /*
         * start the report
         */
        con = null;
        out = null;
        try {
            status.setMessage("Initializing report");

            /*
             * Print the report
             */
            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/hivHepCTransfer/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("FROM_DATE", ReportUtil.toString(beginReleased, Messages.get().dateTimeSecondPattern()));
            jparam.put("TO_DATE", ReportUtil.toString(endReleased, Messages.get().dateTimeSecondPattern()));
            jparam.put("ANALYTE_IDS", analyteIds);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Outputing report").setPercentComplete(20);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            jexport = new JRCsvExporter();
            out = Files.newOutputStream(tmp);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setPercentComplete(90);

            jexport.exportReport();

            status.setPercentComplete(100);

            status.setMessage("Data exported.").setStatus(ReportStatus.Status.PRINTED);
        } catch (NotFoundException nfE) {
            log.log(Level.INFO, "No samples found for SDWIS Export");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e1) {
                log.severe("Could not close outout stream for hiv_hepc_export");
            }
        }

        return status;
    }
}