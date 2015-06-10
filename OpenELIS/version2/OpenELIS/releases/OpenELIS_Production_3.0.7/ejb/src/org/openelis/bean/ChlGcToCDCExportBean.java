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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.report.chlGCToCDC.AnalysisDataSource;
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
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class ChlGcToCDCExportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;
    @EJB
    private DictionaryCacheBean dictionaryCache;
    @EJB
    private SampleManager1Bean  sampleManager;
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

            p.add(new Prompt("BEGIN_COLLECTION", Prompt.Type.DATETIME).setPrompt("Begin Collection:")
                                                                      .setWidth(130)
                                                                      .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                      .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                      .setRequired(true));

            p.add(new Prompt("END_COLLECTION", Prompt.Type.DATETIME).setPrompt("End Collection:")
                                                                    .setWidth(130)
                                                                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                    .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                    .setRequired(true));

            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;
        Connection con;
        AnalysisDataSource ds;
        HashMap<String, Object> jparam;
        HashMap<String, QueryData> param;
        Integer orgReportToId, orgTypeId;
        JasperPrint jprint;
        JasperReport jreport;
        QueryData field;
        ReportStatus status;
        String dir, exportDirectory, fromDate, toDate;
        URL url;
        ZipOutputStream out;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("ChlGcToCDCExport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        fromDate = ReportUtil.getSingleParameter(param, "BEGIN_COLLECTION");
        toDate = ReportUtil.getSingleParameter(param, "END_COLLECTION");

        try {
            exportDirectory = systemVariable.fetchByName("chlgc_cdc_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'chlgc_cdc_directory' is not available");
            throw new Exception("System variable 'chlgc_cdc_directory' is not available");
        }

        try {
            orgReportToId = dictionaryCache.getIdBySystemName("org_report_to");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Dictionary with system name 'org_report_to' is not available");
            throw new Exception("Dictionary with system name 'org_report_to' is not available");
        }

        try {
            orgTypeId = dictionaryCache.getIdBySystemName("org_type");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Dictionary with system name 'org_type' is not available");
            throw new Exception("Dictionary with system name 'org_type' is not available");
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
            url = ReportUtil.getResourceURL("org/openelis/report/chlGCToCDC/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("FROM_DATE", fromDate);
            jparam.put("TO_DATE", toDate);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Fetching records").setPercentComplete(20);

            fields = new ArrayList<QueryData>();
            
            field = new QueryData();
            field.setKey(SampleMeta.getDomain());
            field.setQuery("C");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getCollectionDate());
            field.setQuery(fromDate+".."+toDate);
            field.setType(QueryData.Type.DATE);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getStatusId());
            field.setQuery("!"+Constants.dictionary().SAMPLE_NOT_VERIFIED);
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getSampleOrgTypeId());
            field.setQuery(orgReportToId.toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getOrgParamTypeId());
            field.setQuery(orgTypeId.toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getOrgParamValue());
            field.setQuery("Family Planning Clinic|STD Clinic|Student Health Services|Correctional Facility|Prenatal Clinic|Indian Health Services|Community Health Center|Other");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisTestName());
            field.setQuery("chl-gc cbss");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisIsReportable());
            field.setQuery("Y");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisStatusId());
            field.setQuery("!"+Constants.dictionary().ANALYSIS_CANCELLED);
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            sms = new ArrayList<SampleManager1>();
            sms = sampleManager.fetchByQuery(fields, 0, 10000, SampleManager1.Load.ORGANIZATION,
                                             SampleManager1.Load.QA, SampleManager1.Load.RESULT);
            ds = AnalysisDataSource.getInstance(sms, con);
            
            status.setMessage("Outputing report").setPercentComplete(50);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, ds);

            export(jprint, exportDirectory);

            status.setPercentComplete(100);

            status.setMessage(ds.getSummaryMessage())
                  .setStatus(ReportStatus.Status.PRINTED);
        } catch (NotFoundException nfE) {
            log.log(Level.INFO, "No samples found for ChlGCToCDC Export");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e1) {
                log.severe("Could not close outout stream for chlGCToCDC export");
            }
        }

        return status;
    }

    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String exportDirectory) throws Exception {
        Path path;
        JRExporter jexport;
        ZipOutputStream out;

        try {
            path = Paths.get(exportDirectory, "cdc.zip");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Could not open zip file for writing.");
            throw new Exception("Could not open zip file for writing.");
        }

        out = null;
        try {
            jexport = new JRCsvExporter();
            out = new ZipOutputStream(Files.newOutputStream(path));
            out.putNextEntry(new ZipEntry("cdc.dat"));
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, "|");
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for chlGCToCDC export");
            }
        }

        return path;
    }
}