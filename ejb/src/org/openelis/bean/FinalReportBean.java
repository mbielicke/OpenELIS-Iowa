package org.openelis.bean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.remote.FinalReportBeanRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.Printer;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class FinalReportBean implements FinalReportBeanRemote {
	
	int progress;
	
	@EJB
	SessionCacheInt session;
	
	/*
	 * Returns the prompt for a single re-print
	 */
	public ArrayList<Prompt> getPrompts() throws Exception {
	    ArrayList<Printer> prn;
	    ArrayList<Prompt> p;
	    
	    prn = PrinterList.getInstance().getListByType("pdf");
	    p = new ArrayList<Prompt>();
	    p.add(new Prompt("ACCESSION_NUMBER", Prompt.Type.INTEGER)
                .setPrompt("Accession Number:")
                .setLength(10)
                .setRequired(true));

        p.add(new Prompt("ORGANIZATION_ID", Prompt.Type.INTEGER)
            .setPrompt("Organization ID:")
            .setLength(10)
            .setRequired(false));
        
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
	public void runReport(ArrayList<QueryData> paramList) throws Exception {
	    String reportType, printer, orgId, accession; 
	    HashMap<String, QueryData> param;
        File tempFile;
        JasperReport jreport;
        JasperPrint  jprint;
        JRExporter   jexport;
        HashMap      jparam;
        Connection   jdbc;
	    
        //
//        jdbc = ctx.
        
        
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
        jparam.put("REPORT_TYPE", "B");
        jparam.put("SAMPLE_ACCESSION_NUMBER", accession);
        jparam.put("ORGANIZATION_ID", orgId);
        
        tempFile = File.createTempFile("/home/akampoow/Desktop/", "pdf");
        jreport = (JasperReport) JRLoader.loadObject("finalReport/main.jasper");
        jprint = JasperFillManager.fillReport(jreport, jparam);
        jexport = getPdfExporter();
        jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
        jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
        jexport.exportReport();
	}
	
	
	public byte[] doFinalReport() throws Exception {
		File pdfFile = new File("/home/tschmidt/jfreechart-1.0.0-rc1-US.pdf");
		FileInputStream inStream = new FileInputStream(pdfFile);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		int next;
		long size = pdfFile.length();
		System.out.println("size = "+size);
		long count = 0l;
		while((next = inStream.read()) > -1) {
			count++;
			stream.write(next);
			session.setAttribute("progress", (int)(count/(double)size*100.0));
		}
		inStream.close();
		return stream.toByteArray();
	}
	
	public int getProgress() {
		if(session.getAttribute("progress") == null)
			return 0;
		return (Integer)session.getAttribute("progress");
	}
	
    protected JRExporter getPdfExporter() {
        return new JRPdfExporter();
    }

}
