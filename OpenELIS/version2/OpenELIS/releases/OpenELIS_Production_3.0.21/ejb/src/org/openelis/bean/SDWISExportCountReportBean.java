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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SDWISUnloadReportVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.Counter;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class SDWISExportCountReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;
    @EJB
    private PrinterCacheBean    printers;
    @EJB
    private AnalysisBean        analysis;
    @EJB
    private DictionaryCacheBean dictionaryCache;
    @EJB
    private SampleBean          sample;
    @EJB
    private SampleSDWISBean     sampleSdwis;
    @EJB
    private SectionCacheBean    sectionCache;
    @EJB
    private SystemVariableBean  systemVariable;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("BEGIN_RELEASED", Prompt.Type.DATETIME).setPrompt("Begin Released:")
                                                                    .setWidth(130)
                                                                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                    .setRequired(true));

            p.add(new Prompt("END_RELEASED", Prompt.Type.DATETIME).setPrompt("End Released:")
                                                                  .setWidth(130)
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                  .setRequired(true));

            prn = printers.getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
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
        SDWISUnloadReportVO surVO;
        ArrayList<SampleDO> samples;
        ArrayList<SDWISUnloadReportVO> analyses;
        Calendar fromDate, toDate;
        Counter sampleCounts;
        Date beginReleased, endReleased;
        DictionaryDO sampCatDO;
        Path stat;
        HashMap<String, Object> jparam;
        HashMap<String, QueryData> param;
        Iterator<SampleDO> sIter;
        Iterator<SDWISUnloadReportVO> aIter;
        JRExporter jexport;
        JasperPrint jprint;
        JasperReport jreport;
        OutputStream out;
        ReportStatus status;
        SampleDO sDO;
        SampleSDWISViewDO ssVDO;
        SectionViewDO secVDO;
        String location, printer, printstat;
        URL url;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SDWISExportCountReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        beginReleased = null;
        endReleased = null;
        printer = null;
        if (paramList != null) {
            param = ReportUtil.getMapParameter(paramList);
            beginReleased = ReportUtil.getTimestampParameter(param, "BEGIN_RELEASED");
            endReleased = ReportUtil.getTimestampParameter(param, "END_RELEASED");
            printer = ReportUtil.getStringParameter(param, "PRINTER");
        }

        if (beginReleased == null || endReleased == null) {
            /*
             * take all the released specimens between yesterday at 12 pm to 
             * today at 11:59 am
             */
            fromDate = Calendar.getInstance();
            fromDate.set(Calendar.HOUR_OF_DAY, 12);
            fromDate.set(Calendar.MINUTE, 00);
            fromDate.add(Calendar.DAY_OF_MONTH, -1);
            beginReleased = fromDate.getTime();

            toDate = Calendar.getInstance();
            toDate.set(Calendar.HOUR_OF_DAY, 11);
            toDate.set(Calendar.MINUTE, 59);
            endReleased = toDate.getTime();
        }

        if (printer == null) {
            try {
                printer = systemVariable.fetchByName("sdwis_export_printer").getValue();
            } catch (Exception anyE) {
                log.log(Level.SEVERE, "System variable 'sdwis_export_printer' is not available");
                throw new Exception("System variable 'sdwis_export_printer' is not available");
            }
        }

        try {
            stat = ReportUtil.createTempFile("sdwisExportCount", ".pdf", null);
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Could not open temp file for writing.");
            throw new Exception("Could not open temp file for writing.");
        }

        /*
         * start the report
         */
        out = null;
        try {
            status.setMessage("Initializing report");

            status.setMessage("Outputing report").setPercentComplete(10);

            sampleCounts = new Counter();

            samples = sample.fetchSDWISByReleased(beginReleased, endReleased);
            sIter = samples.iterator();
            while (sIter.hasNext()) {
                sDO = sIter.next();
                try {
                    ssVDO = sampleSdwis.fetchBySampleId(sDO.getId());
                } catch (Exception anyE) {
                    log.log(Level.WARNING, "Error looking up SDWIS domain data for accession #" + sDO.getAccessionNumber() + "; " + anyE.getMessage());
                    sampleCounts.set("ERROR", true);
                    continue;
                }

                try {
                    sampCatDO = dictionaryCache.getById(ssVDO.getSampleCategoryId());
                } catch (Exception anyE) {
                    throw new Exception("Error looking up dictionary entry for Sample Category or Sample Type; " +
                                        anyE.getMessage());
                }

                location = "NONE";
                try {
                    analyses = analysis.fetchBySampleIdForSDWISUnloadReport(sDO.getId());
                    aIter = analyses.iterator();
                    while (aIter.hasNext()) {
                        surVO = aIter.next();
                        if (Constants.dictionary().ANALYSIS_RELEASED.equals(surVO.getStatusId()) &&
                            "Y".equals(surVO.getIsReportable())) {
                            secVDO = sectionCache.getById(surVO.getSectionId());
                            if (secVDO != null && !secVDO.getName().endsWith(location)) {
                                location = secVDO.getName().substring(secVDO.getName().indexOf("-"));
                                sampleCounts.set(sampCatDO.getCode() + location, true);
                            }
                        }
                    }
                } catch (NotFoundException nfE) {
                    log.log(Level.WARNING, "No analysis data found for accession #" + sDO.getAccessionNumber());
                    sampleCounts.set("ERROR", true);
                }

                status.setPercentComplete( (sampleCounts.getTotal() / samples.size()) * 80 + 10);
            }

            /*
             * Print the status report
             */
            jparam = new HashMap<String, Object>();
            jparam.put("USER_NAME", User.getName(ctx));
            jparam.put("BEGIN_RELEASED", ReportUtil.toString(beginReleased, "yyyy-MM-dd HH:mm"));
            jparam.put("END_RELEASED", ReportUtil.toString(endReleased, "yyyy-MM-dd HH:mm"));
            jparam.put("SAMPLE_COUNTS", sampleCounts);

            url = ReportUtil.getResourceURL("org/openelis/report/sdwisExportCount/main.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, new JREmptyDataSource());
            jexport = new JRPdfExporter();
            out = Files.newOutputStream(stat);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setPercentComplete(90);

            jexport.exportReport();

            status.setPercentComplete(100);

            printstat = ReportUtil.print(stat, User.getName(ctx), printer, 1, true);
            status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
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
                log.severe("Could not close outout stream for sdwis export count");
            }
        }

        return status;
    }
}