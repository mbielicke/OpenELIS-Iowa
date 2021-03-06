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
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.ui.common.DataBaseUtil;
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
public class AbnormalsReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SectionBean         section;

    @EJB
    private TestBean            test;

    @EJB
    private PrinterCacheBean    printers;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM", Prompt.Type.DATETIME).setPrompt("Starting Entered Date:")
                                                          .setWidth(150)
                                                          .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                          .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                          .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                               Messages.get()
                                                                                                       .dateTimePattern()))
                                                          .setRequired(true));

            p.add(new Prompt("TO", Prompt.Type.DATETIME).setPrompt("Ending Entered Date:")
                                                        .setWidth(150)
                                                        .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                        .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                        .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                             Messages.get()
                                                                                                     .dateTimePattern()))
                                                        .setRequired(true));

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(200)
                                                          .setOptionList(getSections())
                                                          .setMultiSelect(true));

            p.add(new Prompt("TEST", Prompt.Type.ARRAY).setPrompt("Test Name:")
                                                       .setWidth(300)
                                                       .setOptionList(getTests())
                                                       .setMultiSelect(true));

            prn = printers.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
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
        String dir, printer, printstat, section, test, userName;
        Timestamp fromDate, toDate;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("AbnormalsReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        fromDate = ReportUtil.getTimestampParameter(param, "FROM");
        toDate = ReportUtil.getTimestampParameter(param, "TO");
        section = ReportUtil.getListParameter(param, "SECTION");
        test = ReportUtil.getListParameter(param, "TEST");
        printer = ReportUtil.getStringParameter(param, "PRINTER");

        if (!DataBaseUtil.isEmpty(section))
            section = " and a.section_id " + section;
        else
            section = "";
        if (!DataBaseUtil.isEmpty(test))
            test = " and t.id " + test;
        else
            test = "";

        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/abnormals/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            userName = User.getName(ctx);

            jparam = new HashMap<String, Object>();
            jparam.put("FROM_DATE", fromDate);
            jparam.put("TO_DATE", toDate);
            jparam.put("SECTION", section);
            jparam.put("TEST", test);
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
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
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
            s = section.fetchList();
            for (SectionViewDO n : s)
                l.add(new OptionListItem(n.getId().toString(), n.getName()));
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            path = ReportUtil.createTempFile("abnormals", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for abnormals report");
            }
        }

        return path;
    }
}