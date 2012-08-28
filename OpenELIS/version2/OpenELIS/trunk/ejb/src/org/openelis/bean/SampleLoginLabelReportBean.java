package org.openelis.bean;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.LabelReportLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.remote.SampleLoginLabelReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleLoginLabelReportBean implements SampleLoginLabelReportRemote {

    @EJB
    private CategoryCacheLocal   category;

    @EJB
    private DictionaryCacheLocal dictionary;

    @EJB
    private SessionCacheLocal    session;

    @EJB
    private SystemVariableLocal  sysvar;

    @EJB
    private SampleLocal          sample;

    @EJB
    private PrinterCacheLocal    printer;

    @EJB
    private LabelReportLocal     labelReport;

    private static final Logger  log = Logger.getLogger(SampleLoginLabelReportBean.class);

    /*
     * Returns the prompt for new setup accession login labels
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn, l;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SAMPLES", Prompt.Type.INTEGER).setPrompt("# of Samples:")
                                                            .setWidth(25)
                                                            .setRequired(true));

            p.add(new Prompt("CONTAINERS", Prompt.Type.INTEGER).setPrompt("# of Containers:")
                                                               .setWidth(25)
                                                               .setRequired(true)
                                                               .setDefaultValue("1"));

            p.add(new Prompt("RECEIVED", Prompt.Type.DATETIME).setPrompt("Date Received:")
                                                              .setWidth(120)
                                                              .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                              .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                              .setDefaultValue(Datetime.getInstance(Datetime.YEAR,
                                                                                                    Datetime.MINUTE)
                                                                                       .toString())
                                                              .setRequired(true));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(120)
                                                           .setOptionList(getLocations())
                                                           .setRequired(true));

            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(120)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            throw e;
        }
    }

    /*
     * Prints the new sample login labels starting at last printed accession
     * number
     */
    @RolesAllowed("r_loginlabel-select")
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i, j, samples, containers, laccession;
        File tempFile;
        String received, location, locationId, printer, printstat;
        ReportStatus status;
        HashMap<String, QueryData> param;
        DictionaryDO locationDO;
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
        param = ReportUtil.getMapParameter(paramList);

        samples = 0;
        containers = 0;
        try {
            samples = Integer.parseInt(ReportUtil.getSingleParameter(param, "SAMPLES"));
            containers = Integer.parseInt(ReportUtil.getSingleParameter(param, "CONTAINERS"));
        } catch (Exception e) {
            throw new InconsistencyException("You must specify valid number for samples (<= 300) and continers (<= 50)");
        } finally {
            if (samples > 300 || containers > 50)
                throw new InconsistencyException("Number of sample labels or container labels\ncan not exceed 300 and 50 respectively");
        }
        received = ReportUtil.getSingleParameter(param, "RECEIVED");
        locationId = ReportUtil.getSingleParameter(param, "LOCATION");
        printer = ReportUtil.getSingleParameter(param, "BARCODE");

        if (DataBaseUtil.isEmpty(received) || DataBaseUtil.isEmpty(locationId) ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify # of samples, # of continers, date received,\nlocation, and printer for this report");

        location = "";
        try {
            locationDO = dictionary.getById(Integer.valueOf(locationId));
            location = locationDO.getEntry().substring(0, 1);
        } catch (Exception e) {
            log.error("Error looking up dictionary for location", e);
            throw e;
        }

        status.setMessage("Outputing report").setPercentComplete(0);

        /*
         * fetch accession number counter and increment it
         */
        data = null;
        try {
            data = sysvar.fetchForUpdateByName("last_accession_number");
            laccession = Integer.parseInt(data.getValue());
            data.setValue(String.valueOf(laccession + samples));
            sysvar.updateAsSystem(data);
        } catch (Exception e) {
            log.error("System variable 'last_accession_number' is not available or valid", e);
            if (data != null)
                sysvar.abortUpdate(data.getId());
            throw e;
        }
        
        log.info("Starting at accession # "+laccession+" for "+samples+" labels");

        status.setPercentComplete(50);
        /*
         * print the labels and send it to printer
         */
        tempFile = File.createTempFile("loginlabel", ".txt", new File("/tmp"));
        ps = new PrintStream(tempFile);
        for (i = 0; i < samples; i++ ) {
            laccession++ ;
            labelReport.sampleLoginLabel(ps, laccession, -1, received, location);
            for (j = 0; j < containers; j++ )
                labelReport.sampleLoginLabel(ps, laccession, j, received, location);
        }
        ps.close();

        printstat = ReportUtil.print(tempFile, printer, 1);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    /*
     * Returns the prompt for new setup accession login labels
     */
    public ArrayList<Prompt> getAdditionalPrompts() throws Exception {
        ArrayList<OptionListItem> prn, l;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("ACCESSION", Prompt.Type.STRING).setPrompt("Accession number:")
                                                             .setWidth(100)
                                                             .setRequired(true));

            p.add(new Prompt("CONTAINERS", Prompt.Type.INTEGER).setPrompt("# of Containers:")
                                                               .setWidth(25)
                                                               .setRequired(true)
                                                               .setDefaultValue("1"));

            p.add(new Prompt("RECEIVED", Prompt.Type.DATETIME).setPrompt("Date Received:")
                                                              .setWidth(120)
                                                              .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                              .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                              .setRequired(false));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(120)
                                                           .setOptionList(getLocations())
                                                           .setRequired(true));

            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(120)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            throw e;
        }
    }

    /*
     * Re-prints a lab number label
     */
    @RolesAllowed("r_loginlabelrep-select")
    public ReportStatus runAdditionalReport(ArrayList<QueryData> paramList) throws Exception {
        int i, accession, starting, containers, laccession;
        String accStr, received, location, locationId, printer, printstat, parts[];
        ReportStatus status;
        HashMap<String, QueryData> param;
        DictionaryDO locationDO;
        SampleDO samdata;
        SystemVariableDO sysdata;
        PrintStream ps;
        File tempFile;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SampleLoginLabelReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        accStr = ReportUtil.getSingleParameter(param, "ACCESSION");
        containers = 0;
        try {
            containers = Integer.parseInt(ReportUtil.getSingleParameter(param, "CONTAINERS"));
        } catch (Exception e) {
            throw new InconsistencyException("You must specify valid number of continers (<=50)");
        } finally {
            if (containers > 50)
                throw new InconsistencyException("Number of requested container lables 50");
        }
        received = ReportUtil.getSingleParameter(param, "RECEIVED");
        locationId = ReportUtil.getSingleParameter(param, "LOCATION");
        printer = ReportUtil.getSingleParameter(param, "BARCODE");

        if (DataBaseUtil.isEmpty(accStr) || DataBaseUtil.isEmpty(locationId) ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify accession number, location, and printer for this report");

        /*
         * accession string might be in nnnn-xx (accession-container)
         */
        parts = accStr.split("-", 2);
        try {
            accession = Integer.parseInt(parts[0]);
            if (parts.length == 2)
                starting = Integer.parseInt(parts[1]);
            else
                starting = 0;
        } catch (Exception e) {
            throw new InconsistencyException("Accession number must be in format 12345 or 12345-01\nwhere 01 is the starting container");
        }

        location = "";
        try {
            locationDO = dictionary.getById(Integer.valueOf(locationId));
            location = locationDO.getEntry().substring(0, 1);
        } catch (Exception e) {
            log.error("Error looking up dictionary for location", e);
            
        }

        status.setMessage("Outputing report").setPercentComplete(0);

        /*
         * find the sample get the received date
         */
        try {
            samdata = sample.fetchByAccessionNumber(accession);
            received = samdata.getReceivedDate().toString();
        } catch (Exception e) {
            if (DataBaseUtil.isEmpty(received))
                throw new InconsistencyException("Sample with accession # " + accession +
                                                 " has not been logged in.\nYou need to either login the sample or\nspecify the date received");
            /*
             * have we issued this accession # before?
             */
            sysdata = sysvar.fetchByName("last_accession_number");
            laccession = Integer.parseInt(sysdata.getValue());
            if (accession > laccession)
                throw new Exception("The login label with accession # " + accession +
                                    " has never been issued.\nYou need to run sample login label rather than additional label");
        }

        log.info("Reprinting accession # "+accession+" for "+starting+" labels");

        status.setPercentComplete(50);
        /*
         * print the labels and send it to printer
         */
        tempFile = File.createTempFile("loginlabel", ".txt", new File("/tmp"));
        ps = new PrintStream(tempFile);
        for (i = 0; i < containers; i++ )
            labelReport.sampleLoginLabel(ps, accession, i + starting, received, location);
        ps.close();

        printstat = ReportUtil.print(tempFile, printer, 1);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    private ArrayList<OptionListItem> getLocations() {
        ArrayList<DictionaryDO> d;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            d = category.getBySystemName("order_ship_from").getDictionaryList();
            for (DictionaryDO e : d)
                l.add(new OptionListItem(e.getId().toString(), e.getEntry()));
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }

        return l;
    }
}