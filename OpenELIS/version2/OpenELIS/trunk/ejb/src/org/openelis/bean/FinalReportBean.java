package org.openelis.bean;

import java.io.File;
import java.io.FileInputStream;
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
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
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
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.PermissionInterceptor;
import org.openelis.utils.Printer;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class FinalReportBean implements FinalReportRemote {
	
	@EJB
	private SessionCacheInt session;	
	
	@Resource
    private SessionContext ctx;
	
	/*
	 * Returns the prompt for a single re-print
	 */
	public ArrayList<Prompt> getPromptsForSingle() throws Exception {
	    ArrayList<OptionListItem> prn;
	    ArrayList<Prompt> p;
	    	    
	    p = new ArrayList<Prompt>();
	    p.add(new Prompt("ACCESSION_NUMBER", Prompt.Type.INTEGER)
                .setPrompt("Accession Number:")                               
                .setRequired(true));

        p.add(new Prompt("ORGANIZATION_ID", Prompt.Type.INTEGER)
            .setPrompt("Organization Id:"));
                
        prn = PrinterList.getInstance().getListByType("pdf");
        prn.add(0,new OptionListItem("-view-", "View PDF"));
        p.add(new Prompt("PRINTER", Prompt.Type.ARRAY)
            .setPrompt("Printer:")
            .setOptionList(prn)
            .setMutiSelect(false)
            .setRequired(true));

        return p;
	}
	
	/*
	 * Execute the report and send its output to specified location 
	 */
	public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
	    String reportType, orgId, accession, printer; 
	    HashMap<String, QueryData> param;
        File tempFile;
        FileInputStream fis;
        URL url;        
        String dir;
        JasperReport jreport;
        JasperPrint  jprint;
        JRExporter   jexport;
        HashMap      jparam;
        Connection   con;
        ReportStatus status;
        Printer prn;
        DocFlavor    fl; 
        Doc doc;
        DocPrintJob pj;
        PrintRequestAttributeSet aset;        
	    
        status = new ReportStatus();        
        session.setAttribute("FinalReport", status);
        //
        // Parse the parameters
	    //
	    param = ReportUtil.parameterMap(paramList);
	    reportType = ReportUtil.getSingleParameter(param, "REPORT_TYPE");
	    accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");
	    orgId = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
	
        //
        // create the file and start the report
        //
        jparam = new HashMap();
        jparam.put("REPORT_TYPE", reportType);
        jparam.put("ACCESSION_NUMBER", Integer.valueOf(accession));
        jparam.put("ORGANIZATION_ID", Integer.valueOf(orgId));                        
        
        con = null;
        try {
            con = ((DataSource)ctx.lookup("jdbc/OpenELISDB")).getConnection();
            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/main.jasper");            
            dir = ReportUtil.getResourcePath(url);
            jparam.put("SUBREPORT_DIR", dir);            
            
            status.setMessage("Initializing report");
            jreport = (JasperReport)JRLoader.loadObject(url);   
            jprint = JasperFillManager.fillReport(jreport, jparam, con);
            jexport = new JRPdfExporter();
            tempFile = File.createTempFile("finalreport", ".pdf", new File("/tmp"));
            
            status.setMessage("Outputing report")
                  .setPercentComplete(20);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);            
            jexport.exportReport();
            
            Runtime.getRuntime().exec("chmod 666 " + tempFile.getPath());                       

            status.setMessage(tempFile.getName())
                  .setPercentComplete(100)
                  .setStatus(ReportStatus.Status.SAVED);                               
            
            //printer = ReportUtil.getSingleParameter(param, "PRINTER");
            //Runtime.getRuntime().exec("lpr -P"+printer+" -U "+ PermissionInterceptor.getSystemUserName()+" -# 1 "+ tempFile);
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
}
