package org.openelis.bean;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SystemVariableDO;
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
public class SampleLoginLabelReportBean {

    @Resource
    private SessionContext      ctx;

    @EJB
    private CategoryCacheBean   category;

    @EJB
    private DictionaryCacheBean dictionary;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SystemVariableBean  sysvar;

    @EJB
    private SampleBean          sample;

    @EJB
    private PrinterCacheBean    printer;

    @EJB
    private LabelReportBean     labelReport;

    private static final Logger log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for new setup accession login labels
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
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
                                                              .setWidth(128)
                                                              .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                              .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                              .setDefaultValue(ReportUtil.toString(new Date(),
                                                                                                   Messages.get()
                                                                                                           .dateTimePattern()))
                                                              .setRequired(true));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(124)
                                                           .setOptionList(getLocations())
                                                           .setRequired(true));

            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(124)
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
     * Prints the new sample login labels starting at last printed accession
     * number
     */
    @RolesAllowed("r_loginlabel-select")
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i, j, laccession;
        Integer samples, containers, locationId;
        Path path;
        String received, location, printer, printstat;
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

        samples = ReportUtil.getIntegerParameter(param, "SAMPLES");
        containers = ReportUtil.getIntegerParameter(param, "CONTAINERS");
        received = ReportUtil.getStringParameter(param, "RECEIVED");
        locationId = ReportUtil.getIntegerParameter(param, "LOCATION");
        printer = ReportUtil.getStringParameter(param, "BARCODE");

        if (samples == null || samples > 300 || containers == null || containers > 50)
            throw new InconsistencyException("Sample labels must be < 300 and\ncontainer labels < 50");

        if (DataBaseUtil.isEmpty(received) || locationId == null ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify # of samples, # of continers, date received,\nlocation, and printer for this report");

        location = "";
        try {
            locationDO = dictionary.getById(locationId);
            location = locationDO.getEntry().substring(0, 1);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error looking up dictionary for location", e);
            throw e;
        }

        /*
         * fetch accession number counter and increment it
         */
        status.setMessage("Outputing report").setPercentComplete(0);

        data = null;
        try {
            data = sysvar.fetchForUpdateByName("last_accession_number");
            laccession = Integer.parseInt(data.getValue());
            data.setValue(String.valueOf(laccession + samples));
            sysvar.updateAsSystem(data);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "System variable 'last_accession_number' is not available or valid",
                    e);
            if (data != null)
                sysvar.abortUpdate(data.getId());
            throw e;
        }

        log.fine("Starting at accession # " + laccession + " for " + samples + " labels");

        /*
         * print the labels and send it to printer
         */
        status.setPercentComplete(50);

        path = ReportUtil.createTempFile("loginlabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        for (i = 0; i < samples; i++) {
            laccession++;
            labelReport.sampleLoginLabel(ps, laccession, -1, received, location);
            for (j = 0; j < containers; j++)
                labelReport.sampleLoginLabel(ps, laccession, j, received, location);
        }
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    /*
     * Returns the prompt for new setup accession login labels
     */
    public ArrayList<Prompt> getAdditionalPrompts() throws Exception {
        ArrayList<OptionListItem> prn;
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
                                                              .setWidth(128)
                                                              .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                              .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                              .setRequired(false));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(124)
                                                           .setOptionList(getLocations())
                                                           .setRequired(true));

            prn = printer.getListByType("zpl");
            p.add(new Prompt("BARCODE", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(124)
                                                          .setOptionList(prn)
                                                          .setMultiSelect(false)
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
        int i, starting, laccession;
        Integer accession, containers, locationId;
        String accStr, received, location, printer, printstat, parts[];
        ReportStatus status;
        HashMap<String, QueryData> param;
        DictionaryDO locationDO;
        SampleDO samdata;
        SystemVariableDO sysdata;
        PrintStream ps;
        Path path;

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

        accStr = ReportUtil.getStringParameter(param, "ACCESSION");
        containers = ReportUtil.getIntegerParameter(param, "CONTAINERS");
        received = ReportUtil.getStringParameter(param, "RECEIVED");
        locationId = ReportUtil.getIntegerParameter(param, "LOCATION");
        printer = ReportUtil.getStringParameter(param, "BARCODE");

        if (DataBaseUtil.isEmpty(accStr) || locationId == null ||
            DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify accession number, location, and printer for this report");

        if (containers == null || containers > 50)
            throw new InconsistencyException("You must specify valid number of continers (<=50)");

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
            locationDO = dictionary.getById(locationId);
            location = locationDO.getEntry().substring(0, 1);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error looking up dictionary for location", e);
        }

        status.setMessage("Outputing report").setPercentComplete(0);

        /*
         * find the sample get the received date
         */
        try {
            samdata = sample.fetchByAccessionNumber(accession);
            received = ReportUtil.toString(samdata.getReceivedDate(), Messages.get().dateTimePattern());
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

        log.info("Reprinting accession # " + accession + " for " + starting + " labels");

        /*
         * print the labels and send it to printer
         */
        status.setPercentComplete(50);

        path = ReportUtil.createTempFile("loginlabel", ".txt", null);
        ps = new PrintStream(Files.newOutputStream(path));
        for (i = 0; i < containers; i++)
            labelReport.sampleLoginLabel(ps, accession, i + starting, received, location);
        ps.close();

        printstat = ReportUtil.print(path, User.getName(ctx), printer, 1, true);
        status.setPercentComplete(100).setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);

        return status;
    }

    private ArrayList<OptionListItem> getLocations() {
        ArrayList<DictionaryDO> d;
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("", ""));
        try {
            d = category.getBySystemName("laboratory_location").getDictionaryList();
            for (DictionaryDO e : d)
                l.add(new OptionListItem(e.getId().toString(), e.getEntry()));
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }

        return l;
    }
}