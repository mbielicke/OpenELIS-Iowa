package org.openelis.bean;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.SystemVariableLocal;
import org.openelis.remote.SampleLoginLabelReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class SampleLoginLabelReportBean implements SampleLoginLabelReportRemote {

	@EJB
	private SessionCacheInt session;

	@EJB
	private SystemVariableLocal sysvar;

	/*
	 * Returns the prompt for new setup accession login labels
	 */
	public ArrayList<Prompt> getPrompts() throws Exception {
		ArrayList<OptionListItem> prn, l;
		ArrayList<Prompt> p;

		try {
			p = new ArrayList<Prompt>();

			p.add(new Prompt("SAMPLES", Prompt.Type.INTEGER)
					.setPrompt("# of Samples:")
					.setWidth(25)
					.setRequired(true));

			p.add(new Prompt("CONTAINERS", Prompt.Type.INTEGER)
					.setPrompt("# of Containers:")
					.setWidth(25)
					.setRequired(true)
					.setDefaultValue("1"));

			p.add(new Prompt("RECEIVED", Prompt.Type.DATETIME)
					.setPrompt("Date Received:")
					.setWidth(120)
					.setDatetimeStartCode(Prompt.Datetime.YEAR)
					.setDatetimeEndCode(Prompt.Datetime.MINUTE)
					.setDefaultValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE).toString())
					.setRequired(true));

            l = new ArrayList<OptionListItem>();
            l.add(new OptionListItem("I", "Iowa City"));
            l.add(new OptionListItem("A", "Ankeny"));
            l.add(new OptionListItem("L", "Lakeside"));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY)
					.setPrompt("Location:")
					.setWidth(120)
					.setOptionList(l)
					.setRequired(true));

			prn = PrinterList.getInstance().getListByType("zpl");
			p.add(new Prompt("PRINTER", Prompt.Type.ARRAY)
					.setPrompt("Printer:")
					.setWidth(120)
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
	 * Prints the new sample login labels starting at last printed accession number 
	 */
	@RolesAllowed("r_loginlabel-select")
	public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
    	int i, j, samples, containers, laccession;
    	File tempFile;
    	String received, location, printer, printstat;
    	ReportStatus status;
        HashMap<String, QueryData> param;
        SystemVariableDO data;
        PrintStream ps;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SampleLoginLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.parameterMap(paramList);

    	samples = Integer.parseInt(ReportUtil.getSingleParameter(param, "SAMPLES"));
    	containers = Integer.parseInt(ReportUtil.getSingleParameter(param, "CONTAINERS"));
    	received = ReportUtil.getSingleParameter(param, "RECEIVED");
    	location = ReportUtil.getSingleParameter(param, "LOCATION");
    	printer = ReportUtil.getSingleParameter(param, "PRINTER");
    	if (samples > 300 || containers > 10)
    		throw new Exception("Number of requested sample or container lables exceed 300 and 10");
        /*
         * fetch accession number counter and increment it 
         */
    	tempFile = File.createTempFile("loginlabel", ".txt", new File("/tmp"));
        status.setMessage("Outputing report").setPercentComplete(0);

        try {
        	data = sysvar.fetchForUpdateByName("last_accession_number");
        	laccession = Integer.parseInt(data.getValue());
        	data.setValue(String.valueOf(laccession + samples));
        	sysvar.update(data);
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
        status.setPercentComplete(100);

        /*
         * print the labels and send it to printer
         */
        ps = new PrintStream(tempFile);
        for (i = 0; i < samples; i++, ++laccession) { 
    		printlabel(ps, laccession, -1, received, location);
        	for (j = 0; j < containers; j++)
        		printlabel(ps, laccession, j, received, location);
        }
        ps.close();
    	
        printstat = ReportUtil.print(tempFile, printer, 1);
        status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

	/*
	 * Returns the prompt for new setup accession login labels
	 */
	public ArrayList<Prompt> getReprintPrompts() throws Exception {
		ArrayList<OptionListItem> prn, l;
		ArrayList<Prompt> p;

		try {
			p = new ArrayList<Prompt>();

			p.add(new Prompt("ACCESSION", Prompt.Type.INTEGER)
					.setPrompt("Accession number:")
					.setWidth(70)
					.setRequired(true));

			p.add(new Prompt("STARTING_CONTAINER", Prompt.Type.INTEGER)
					.setPrompt("Starting Container:")
					.setWidth(25)
					.setRequired(true)
					.setDefaultValue("2"));

			p.add(new Prompt("CONTAINERS", Prompt.Type.INTEGER)
					.setPrompt("# of Containers:")
					.setWidth(25)
					.setRequired(true)
					.setDefaultValue("1"));

			p.add(new Prompt("RECEIVED", Prompt.Type.DATETIME)
					.setPrompt("Date Received:")
					.setWidth(120)
					.setDatetimeStartCode(Prompt.Datetime.YEAR)
					.setDatetimeEndCode(Prompt.Datetime.MINUTE)
					.setDefaultValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE).toString())
					.setRequired(true));

            l = new ArrayList<OptionListItem>();
            l.add(new OptionListItem("I", "Iowa City"));
            l.add(new OptionListItem("A", "Ankeny"));
            l.add(new OptionListItem("L", "Lakeside"));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY)
					.setPrompt("Location:")
					.setWidth(120)
					.setOptionList(l)
					.setRequired(true));

			prn = PrinterList.getInstance().getListByType("zpl");
			p.add(new Prompt("PRINTER", Prompt.Type.ARRAY)
					.setPrompt("Printer:")
					.setWidth(120)
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
	 * Re-prints a lab number label
	 */
	@RolesAllowed("r_loginlabelrep-select")
	public ReportStatus runReprintReport(ArrayList<QueryData> paramList) throws Exception {
    	int i, accession, starting, containers, laccession;
    	File tempFile;
    	String received, location, printer, printstat;
    	ReportStatus status;
        HashMap<String, QueryData> param;
        SystemVariableDO data;
        PrintStream ps;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SampleLoginLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.parameterMap(paramList);

    	accession = Integer.parseInt(ReportUtil.getSingleParameter(param, "ACCESSION"));
    	starting = Integer.parseInt(ReportUtil.getSingleParameter(param, "STARTING_CONTAINER"));
    	containers = Integer.parseInt(ReportUtil.getSingleParameter(param, "CONTAINERS"));
    	received = ReportUtil.getSingleParameter(param, "RECEIVED");
    	location = ReportUtil.getSingleParameter(param, "LOCATION");
    	printer = ReportUtil.getSingleParameter(param, "PRINTER");
    	if (starting+containers > 99)
    		throw new Exception("Number of requested container lables exceed 99");
        /*
         * fetch accession number and make sure we have issues the it before 
         */
    	tempFile = File.createTempFile("loginlabel", ".txt", new File("/tmp"));
        status.setMessage("Outputing report").setPercentComplete(0);

        try {
        	data = sysvar.fetchByName("last_accession_number");
        	laccession = Integer.parseInt(data.getValue());
        	if (accession > laccession)
        		throw new Exception("The accession # "+accession+" has never been issued");
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
        status.setPercentComplete(100);

        /*
         * print the labels and send it to printer
         */
        ps = new PrintStream(tempFile);
        printlabel(ps, laccession, -1, received, location);
    	for (i = 0; i < containers; i++)
    		printlabel(ps, laccession, i+starting, received, location);
        ps.close();
    	
        printstat = ReportUtil.print(tempFile, printer, 1);
        status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

	
	/*
	 * Prints a zpl barcode label for sample login
	 */
	private void printlabel(PrintStream f, int accession, int container, String received, String location) {
		String s;
		
		if (container == -1)
			s = String.valueOf(accession);
		else
			s = accession + "-" + container;
		f.print("^XA");
		f.print("^LH0,0");
		f.print("^FO10,35^AE^BCN,50,N,N,N^FD"+s+"^FS");				// barcoded accession
		f.print("^FO10,85^AE^FD"+s+"   ("+location+")^FS");			// readable accession + location
		f.print("^FO10,130^AE^BCN,50,Y,N,N^FD"+received+"^FS");		// barcoded/readable received
		f.print("^PQ1,,1,^XZ");
	}
}