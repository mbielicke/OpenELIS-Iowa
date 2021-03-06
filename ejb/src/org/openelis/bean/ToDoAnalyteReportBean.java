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
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestViewDO;
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
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class ToDoAnalyteReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private TestBean            test;

    @EJB
    private SectionCacheBean    section;

    @EJB
    private CategoryCacheBean   categoryCache;

    @EJB
    private PrinterCacheBean    printers;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for To-Do Analyte Report
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, orderBy;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM_ENTERED", Prompt.Type.DATETIME).setPrompt("Starting Entered Date:")
                                                                  .setWidth(150)
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                  .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                                       Messages.get()
                                                                                                               .dateTimePattern()))
                                                                  .setRequired(true));

            p.add(new Prompt("TO_ENTERED", Prompt.Type.DATETIME).setPrompt("Ending Entered Date:")
                                                                .setWidth(150)
                                                                .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                                     Messages.get()
                                                                                                             .dateTimePattern()))
                                                                .setRequired(true));

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(250)
                                                          .setOptionList(getSections())
                                                          .setMultiSelect(true));

            p.add(new Prompt("TEST", Prompt.Type.ARRAY).setPrompt("Test Name:")
                                                       .setWidth(250)
                                                       .setOptionList(getTests())
                                                       .setMultiSelect(true));

            p.add(new Prompt("PREP_TEST", Prompt.Type.ARRAY).setPrompt("Prep Test Name:")
                                                            .setWidth(250)
                                                            .setOptionList(getTests())
                                                            .setMultiSelect(true));

            p.add(new Prompt("STATUS", Prompt.Type.ARRAY).setPrompt("Analysis Status:")
                                                         .setWidth(250)
                                                         .setOptionList(getStatus())
                                                         .setMultiSelect(true));

            orderBy = new ArrayList<OptionListItem>();
            orderBy.add(0, new OptionListItem("accession_number", "Accession Number"));
            orderBy.add(1, new OptionListItem("collection_date,collection_time", "Collection Date"));
            orderBy.add(2, new OptionListItem("t_name,m_name", "Test & Method"));
            orderBy.add(3, new OptionListItem("status", "Analysis Status"));
            orderBy.add(4, new OptionListItem("t1_name,m1_name", "Prep Test & Method"));

            p.add(new Prompt("ORDER_BY", Prompt.Type.ARRAY).setPrompt("Sort By:")
                                                           .setWidth(250)
                                                           .setOptionList(orderBy)
                                                           .setMultiSelect(true)
                                                           .setRequired(true));

            prn = printers.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(250)
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
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        URL url;
        Path path;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        String section, test, prepTest, prepTestLimitter, analysisStatus, userName, orderBy, printer, printstat, dir;
        Timestamp fromDate, toDate;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("toDoAnalyteReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        fromDate = ReportUtil.getTimestampParameter(param, "FROM_ENTERED");
        toDate = ReportUtil.getTimestampParameter(param, "TO_ENTERED");
        section = ReportUtil.getListParameter(param, "SECTION");
        test = ReportUtil.getListParameter(param, "TEST");
        prepTest = ReportUtil.getListParameter(param, "PREP_TEST");
        analysisStatus = ReportUtil.getListParameter(param, "STATUS");
        orderBy = ReportUtil.getStringParameter(param, "ORDER_BY");
        printer = ReportUtil.getStringParameter(param, "PRINTER");

        if (fromDate == null || toDate == null ||
            DataBaseUtil.isEmpty(orderBy) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify From Date, To Date, Order By, and Printer for this report");

        if ( !DataBaseUtil.isEmpty(section))
            section = " and sec.id " + section;
        else
            section = "";

        if ( !DataBaseUtil.isEmpty(analysisStatus))
            analysisStatus = " and a.status_id " + analysisStatus;
        else
            analysisStatus = "";

        if ( !DataBaseUtil.isEmpty(test))
            test = " and t.id " + test;
        else
            test = "";

        if ( !DataBaseUtil.isEmpty(prepTest)) {
            prepTest = " and t1.id " + prepTest;
            prepTestLimitter = " and a1.test_id = t1.id";
        } else {
            prepTest = "";
            prepTestLimitter = "";
        }

        userName = User.getName(ctx);

        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/todoanalyte/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("FROM", fromDate);
            jparam.put("TO", toDate);
            jparam.put("SECTION", section);
            jparam.put("TEST", test);
            jparam.put("PREP_TEST", prepTest);
            jparam.put("PREP_TEST_LIMITTER", prepTestLimitter);
            jparam.put("STATUS", analysisStatus);
            jparam.put("ORDER_BY", orderBy);
            jparam.put("USER_NAME", userName);
            jparam.put("SUBREPORT_DIR", dir);

            status.setMessage("Outputing report").setPercentComplete(20);

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            if (ReportUtil.isPrinter(printer))
                path = export(jprint, null);
            else
                path = export(jprint, "upload_stream_directory");

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(path, userName, printer, 1, true);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                status.setMessage(path.getFileName().toString())
                      .setPath(path.toString())
                      .setStatus(ReportStatus.Status.SAVED);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                log.severe("Could not close connection");
            }
        }

        return status;
    }

    private ArrayList<OptionListItem> getSections() {
        ArrayList<SectionViewDO> s;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            s = section.getList();
            for (SectionViewDO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getName()));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not fetch sections", e);
        }

        return l;
    }

    private ArrayList<OptionListItem> getTests() {
        ArrayList<TestViewDO> t;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            t = test.fetchList();
            for (TestViewDO n : t)
                if ("N".equals(n.getIsActive()))
                    l.add(new OptionListItem(n.getId().toString(), n.getName() + ", " +
                                                                   n.getMethodName() + " [" +
                                                                   n.getActiveBegin() + ".." +
                                                                   n.getActiveEnd() + "]"));
                else
                    l.add(new OptionListItem(n.getId().toString(), n.getName() + ", " +
                                                                   n.getMethodName()));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not fetch tests", e);
        }

        return l;
    }

    private ArrayList<OptionListItem> getStatus() {
        ArrayList<DictionaryDO> statusDO;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        statusDO = new ArrayList<DictionaryDO>();
        try {
            statusDO = categoryCache.getBySystemName("analysis_status").getDictionaryList();
            for (DictionaryDO n : statusDO)
                l.add(new OptionListItem(n.getId().toString(), n.getEntry().toString()));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not fetch entries for category 'analysis_status'", e);
        }

        return l;
    }

    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("todoAnalyte", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for todo analyte report");
            }
        }
        return path;
    }
}