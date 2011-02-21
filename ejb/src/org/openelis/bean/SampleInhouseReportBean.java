package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.SectionLocal;
import org.openelis.local.TestLocal;
import org.openelis.remote.SampleInhouseReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.PermissionInterceptor;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class SampleInhouseReportBean implements SampleInhouseReportRemote {

    @Resource
    private SessionContext  ctx;

    @EJB
    private SessionCacheInt session;

    @EJB
    private SectionLocal    section;

    @EJB
    private TestLocal       test;

    @EJB
    private DictionaryLocal dict;

    @EJB
    private ProjectLocal    proj;

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM", Prompt.Type.DATETIME).setPrompt("Date From:")
                                                          .setWidth(150)
                                                          .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                          .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                          .setDefaultValue(Datetime.getInstance(Datetime.YEAR,
                                                                                                Datetime.MINUTE)
                                                                                   .toString())
                                                          .setRequired(true));

            p.add(new Prompt("TO", Prompt.Type.DATETIME).setPrompt("Date To:")
                                                        .setWidth(150)
                                                        .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                        .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                        .setDefaultValue(Datetime.getInstance(Datetime.YEAR,
                                                                                              Datetime.MINUTE)
                                                                                 .toString())
                                                        .setRequired(true));

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(200)
                                                          .setOptionList(getSections())
                                                          .setMutiSelect(true));

            p.add(new Prompt("TEST", Prompt.Type.ARRAY).setPrompt("Test Name:")
                                                       .setWidth(200)
                                                       .setOptionList(getTests())
                                                       .setMutiSelect(true));

            p.add(new Prompt("STATUS", Prompt.Type.ARRAY).setPrompt("Status:")
                                                         .setWidth(200)
                                                         .setOptionList(getStatus())
                                                         .setMutiSelect(true)
                                                         .setRequired(true));

            p.add(new Prompt("PROJECT", Prompt.Type.ARRAY).setPrompt("Project:")
                                                          .setWidth(200)
                                                          .setOptionList(getProjects())
                                                          .setMutiSelect(true));

            p.add(new Prompt("ORGANIZATION_ID", Prompt.Type.INTEGER).setPrompt("Organization Id:")
                                                                    .setWidth(200));

            prn = PrinterList.getInstance().getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View in PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
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
        File tempFile;
        HashMap<String, QueryData> param;
        HashMap<String, Object> jparam;
        Connection con;
        ReportStatus status;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        String frDate, tDate, fromDate, toDate, section, test, aStatus, project, orgId, loginName, printer, dir, printstat;
        fromDate=toDate= null;
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SampleInhouseReport", status);    
       
        
        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.parameterMap(paramList);

        loginName = PermissionInterceptor.getSystemUserName();
        
        frDate = ReportUtil.getSingleParameter(param, "FROM");
        tDate = ReportUtil.getSingleParameter(param, "TO");
        section = ReportUtil.getListParameter(param, "SECTION");
        test = ReportUtil.getListParameter(param, "TEST");
        aStatus = ReportUtil.getListParameter(param, "STATUS");
        project = ReportUtil.getListParameter(param, "PROJECT");
        orgId = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(frDate) || DataBaseUtil.isEmpty(tDate) ||
            DataBaseUtil.isEmpty(aStatus) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify From Date, To Date, Status and printer for this report");
        
       if(frDate !=null && frDate.length()>0) {           
            fromDate = frDate + ":00";            
        }
        if(tDate !=null && tDate.length()>0) {           
            toDate = tDate + ":59";
            
        }    
        
        if ( !DataBaseUtil.isEmpty(section))
            section = " and s.id " + section;
        else
            section = "";
        if ( !DataBaseUtil.isEmpty(test))
            test = " and t.id " + test;
        else
            test = "";       
        if ( !DataBaseUtil.isEmpty(project)){
            project = " and sa.id in (select sample_id from sample_project where sample_id = sa.id and project_id " +
                      project + ") ";
        }
        else
            project = "";
        if ( !DataBaseUtil.isEmpty(orgId))
            orgId = " and sd.organization_id = " + orgId + " and sd.sample_id = sa.id ";
        else
            orgId = "";
        /*
         * start the report
         */
        con = null;
        try {
            status.setMessage("Initializing report");

            con = ReportUtil.getConnection(ctx);
            url = ReportUtil.getResourceURL("org/openelis/report/inhouse/main.jasper");
            dir = ReportUtil.getResourcePath(url);

            tempFile = File.createTempFile("inhouse", ".pdf", new File("/tmp"));

            jparam = new HashMap<String, Object>();
            jparam.put("FROM", fromDate);
            jparam.put("TO", toDate);
            jparam.put("SECTION", section);
            jparam.put("TEST", test);
            jparam.put("STATUS", aStatus);
            jparam.put("PROJECT", project);
            jparam.put("ORG_ID", orgId);
            jparam.put("LOGIN_NAME", loginName);

            status.setMessage("Loading report");

            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            jexport = new JRPdfExporter();
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setMessage("Outputing report").setPercentComplete(20);

            jexport.exportReport();

            status.setPercentComplete(100);

            if (ReportUtil.isPrinter(printer)) {
                printstat = ReportUtil.print(tempFile, printer, 1);
                status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
            } else {
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                // ignore
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
        ArrayList<TestMethodVO> t;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            t = test.fetchList();
            for (TestMethodVO n : t)
                l.add(new OptionListItem(n.getTestId().toString(), n.getTestName() + ", " +
                                                                   n.getMethodName()));
        } catch (Exception e) {
            e.printStackTrace();
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
            statusDO = dict.fetchByCategorySystemName("analysis_status");
            for (DictionaryDO n : statusDO)
                l.add(new OptionListItem(n.getId().toString(), n.getEntry().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }

    private ArrayList<OptionListItem> getProjects() {
        ArrayList<ProjectDO> p;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        
        try {
            p = proj.fetchActiveByName("%", 10000000);
            for (ProjectDO n : p)
                l.add(new OptionListItem(n.getId().toString(), n.getName().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }

}
