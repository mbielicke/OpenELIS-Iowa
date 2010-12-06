package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
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
import org.openelis.domain.OptionListItem;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.SectionLocal;
import org.openelis.local.TestLocal;
import org.openelis.remote.TestReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.ReportStatus;
import org.openelis.utils.PermissionInterceptor;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class TestReportBean implements TestReportRemote {


    @Resource
    private SessionContext  ctx;
    
    @EJB
    private SessionCacheInt session;
    
    @EJB
    private SectionLocal section; 
    
    @EJB
    private TestLocal test; 

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, detail;
        ArrayList<Prompt> p;

        try {            
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Section Name:")
                                                          .setWidth(200)
                                                          .setOptionList(getSections())
                                                          .setMutiSelect(true));

            p.add(new Prompt("TEST", Prompt.Type.ARRAY).setPrompt("Test Name:")
                                                       .setWidth(200)
                                                       .setOptionList(getTests())
                                                       .setMutiSelect(true));

            detail = new ArrayList<OptionListItem>();
            detail.add(new OptionListItem("NL", "Name List"));
            detail.add(new OptionListItem("BC", "Sample Type Barcode"));
            detail.add(new OptionListItem("FD", "Full Detail"));            

            p.add(new Prompt("DETAIL", Prompt.Type.ARRAY).setPrompt("Detail:")
                                                         .setWidth(200)
                                                         .setOptionList(detail)                                                        
                                                         .setMutiSelect(false)
                                                         .setRequired(true));            

            prn = PrinterList.getInstance().getListByType("pdf");
            prn.add(0,new OptionListItem("-view-", "View PDF"));
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
        String detail, test ,section, where;
        HashMap<String, QueryData> param;
        File tempFile;
        URL url;
        String dir, printer;
        JasperReport jreport;
        JasperPrint jprint;
        JRExporter jexport;
        HashMap jparam;
        Connection con;
        ReportStatus status;
        
        
        status = new ReportStatus();        
        session.setAttribute("TestReport", status);
        
        param = ReportUtil.parameterMap(paramList);
        detail = ReportUtil.getSingleParameter(param, "DETAIL");
        
        where = "";
        section = ReportUtil.getListParameter(param, "SECTION");        
        if (!DataBaseUtil.isEmpty(section))
            where += " and s.id " + section;
        
        test = ReportUtil.getListParameter(param, "TEST");
        if (!DataBaseUtil.isEmpty(test))
            where += " and t.id " + test;
        
        jparam = new HashMap();
        
        jparam.put("DETAIL", detail);   
        jparam.put("WHERE", where);        

        con = null;
        try {
            con = ((DataSource)ctx.lookup("jdbc/OpenELISDB")).getConnection();
            url = ReportUtil.getResourceURL("org/openelis/report/test/main.jasper");            
            dir = ReportUtil.getResourcePath(url);
            jparam.put("SUBREPORT_DIR", dir);            
            
            status.setMessage("Initializing report");
            jreport = (JasperReport)JRLoader.loadObject(url);   
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            jexport = new JRPdfExporter();
            tempFile = File.createTempFile("test", ".pdf", new File("/tmp"));
            
            status.setMessage("Outputing report")
                  .setPercentComplete(20);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
            jexport.exportReport();
            
            Runtime.getRuntime().exec("chmod 666 " + tempFile.getPath());
            
            status.setMessage(tempFile.getName())
                  .setPercentComplete(100)
                  .setStatus(ReportStatus.Status.SAVED);                       
            
            printer = ReportUtil.getSingleParameter(param, "PRINTER");
            if (!printer.startsWith("-") && !printer.endsWith("-")) {
                Runtime.getRuntime().exec("lpr -P"+printer+" -U "+ PermissionInterceptor.getSystemUserName()+" -# 1 "+ tempFile);
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
        ArrayList<TestMethodVO> t;
        ArrayList<OptionListItem> l;
        
        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {            
            t = test.fetchList();
            for (TestMethodVO n : t) 
                l.add(new OptionListItem(n.getTestId().toString(),
                                         n.getTestName() + ", "+ n.getMethodName()));            
        } catch (Exception e) {
            e.printStackTrace();            
        }
        
        return l;
    }
    
}
