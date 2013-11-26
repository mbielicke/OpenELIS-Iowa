package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.FinalReportVO;
import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.report.finalreport.OrganizationPrintDataSource;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
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
public class FinalReportBean {

    @Resource
    private SessionContext            ctx;

    @EJB
    private SessionCacheBean          session;

    @EJB
    private SampleBean                sample;

    @EJB
    private LockBean                  lock;

    @EJB
    private AnalysisBean              analysis;

    @EJB
    private PrinterCacheBean          printer;

    @EJB
    private AuxDataBean               auxData;

    @EJB
    private OrganizationParameterBean organizationParameter;

    @EJB
    private SystemVariableBean        systemVariable;

    @EJB
    private AttachmentBean            attachment;

    @EJB
    private AttachmentItemBean        attachmentItem;

    @EJB
    private SectionCacheBean          section;

    private static final Logger       log = Logger.getLogger("openelis");

    /**
     * Returns the prompt for a batch print
     */
    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            prn = printer.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create result prompts", e);
            throw e;
        }
    }

    /**
     * Returns the prompt for a batch re-print
     */
    public ArrayList<Prompt> getPromptsForBatchReprint() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("BEGIN_PRINTED", Prompt.Type.DATETIME).setPrompt("Begin Printed:")
                                                                   .setWidth(130)
                                                                   .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                   .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                   .setRequired(true)
                                                                   .setDefaultValue(null));

            p.add(new Prompt("END_PRINTED", Prompt.Type.DATETIME).setPrompt("End Printed:")
                                                                 .setWidth(130)
                                                                 .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                 .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                 .setRequired(true)
                                                                 .setDefaultValue(null));

            prn = printer.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create result prompts", e);
            throw e;
        }
    }

    /**
     * Final report for a single or reprint. The report is printed for the
     * primary or secondary organization(s) ordered by organization.
     */
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
        Integer orgId, typeId, zero;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String orgParam, accession, printer, faxNumber, fromCompany, faxAttention, toCompany, faxNote;
        HashMap<String, QueryData> param;
        ArrayList<FinalReportVO> results;
        ArrayList<OrganizationPrint> orgPrintList;

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");
        orgParam = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
        faxNumber = ReportUtil.getSingleParameter(param, "FAX_NUMBER");
        fromCompany = ReportUtil.getSingleParameter(param, "FROM_COMPANY");
        toCompany = ReportUtil.getSingleParameter(param, "TO_COMPANY");
        faxAttention = ReportUtil.getSingleParameter(param, "FAX_ATTENTION");
        faxNote = ReportUtil.getSingleParameter(param, "FAX_NOTE");

        if (DataBaseUtil.isEmpty(accession) ||
            (DataBaseUtil.isEmpty(printer) && DataBaseUtil.isEmpty(faxNumber)))
            throw new InconsistencyException("You must specify the accession number and either the printer or the fax number for this report");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * find the sample
         */
        try {
            results = sample.fetchForFinalReportSingle(Integer.parseInt(accession));
            /*
             * if the user didn't specify an id for an organization then a
             * report is created for all the organizations associated with the
             * sample, otherwise a report is created for the organization, the
             * id for which is specified by the user if it can be found in the
             * list of organizations for that sample
             */
            orgId = null;
            if (orgParam != null)
                orgId = Integer.parseInt(orgParam);

            orgPrint = null;
            /*
             * find all the report to organizations for given sample
             */
            orgPrintList = new ArrayList<OrganizationPrint>();
            if ( !DataBaseUtil.isEmpty(printer)) {
                /*
                 * if the user didn't select an organization then print the
                 * report for all organizations otherwise print it for only the
                 * organization selected
                 */
                for (FinalReportVO result : results) {
                    if (orgId == null || DataBaseUtil.isSame(orgId, result.getOrganizationId())) {
                        orgPrint = new OrganizationPrint();
                        orgPrint.setOrganizationId(result.getOrganizationId());
                        orgPrint.setSampleId(result.getSampleId());
                        orgPrint.setAccessionNumber(result.getAccessionNumber());
                        orgPrint.setDomain(result.getDomain());
                        orgPrint.setRevision(result.getRevision());
                        orgPrintList.add(orgPrint);
                    }
                }
            } else {
                zero = new Integer(0);
                for (FinalReportVO result : results) {
                    typeId = result.getOrganizationTypeId();
                    /*
                     * If the organization was not specified by the user (orgId
                     * == null) then the report is faxed only for the primary
                     * report to, which will have the org type returned from the
                     * query as zero for private well samples and a specific id
                     * for the other domains. Otherwise the report is faxed for
                     * the organization specified by the user.
                     */
                    if ( (orgId == null && (Constants.dictionary().ORG_REPORT_TO.equals(typeId) || zero.equals(typeId))) ||
                        DataBaseUtil.isSame(orgId, result.getOrganizationId())) {
                        orgPrint = new OrganizationPrint();
                        orgPrint.setOrganizationId(result.getOrganizationId());
                        orgPrint.setOrganizationName(result.getOrganizationName());
                        orgPrint.setSampleId(result.getSampleId());
                        orgPrint.setAccessionNumber(result.getAccessionNumber());
                        orgPrint.setDomain(result.getDomain());
                        orgPrint.setRevision(result.getRevision());
                        orgPrint.setFaxNumber(faxNumber);
                        orgPrint.setFromCompany(fromCompany);
                        orgPrint.setFaxAttention(faxAttention);
                        orgPrint.setToCompany(toCompany);
                        orgPrint.setFaxNote(faxNote);
                        orgPrintList.add(orgPrint);
                    }
                }
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Trying to find a sample", e);
            throw e;
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("Final report for accession number " + accession +
                                             " has incorrect status,\nmissing information, or has no analysis ready to be printed or faxed");

        print(orgPrintList, "R", false, status, printer);

        return status;
    }

    /**
     * Final report for preview. In preview mode the analyses can be in
     * completed or released status. Additionally the preview mode adds a
     * watermark to the background.
     */
    public ReportStatus runReportForPreview(ArrayList<QueryData> paramList) throws Exception {
        ReportStatus status;
        OrganizationPrint print;
        String accession;
        HashMap<String, QueryData> param;
        FinalReportVO result;
        ArrayList<OrganizationPrint> printList;

        /*
         * Recover the accession number
         */
        param = ReportUtil.getMapParameter(paramList);
        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * find the sample
         */
        try {
            result = sample.fetchForFinalReportPreview(Integer.parseInt(accession));

            print = new OrganizationPrint(result.getOrganizationId(),
                                          result.getOrganizationName(),
                                          result.getOrganizationAttention(),
                                          result.getSampleId(),
                                          result.getAccessionNumber(),
                                          result.getRevision(),
                                          result.getDomain());
            printList = new ArrayList<OrganizationPrint>();
            printList.add(print);
        } catch (NotFoundException e) {
            throw new InconsistencyException("Final report for accession number " + accession +
                                             " has incorrect status,\nmissing information, or has no analysis ready to be printed");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Trying to find a sample", e);
            throw e;
        }

        print(printList, "C", false, status, "-view-");

        return status;
    }

    /**
     * Final report for e-save. This method saves a copy of the final report as
     * attachment to the sample. The method is called before un-release of
     * analysis or sample to save a previous copy before changes are committed.
     */
    public void runReportForESave(Integer accession) throws Exception {
        boolean esave;
        Integer sid;
        String sname;
        ReportStatus status;
        AttachmentDO att;
        FinalReportVO result;
        OrganizationPrint print;
        ArrayList<FinalReportVO> reportList;
        ArrayList<OrganizationPrint> printList;

        /*
         * skip if they don't want to save previous versions of final report
         */
        try {
            esave = Boolean.parseBoolean(systemVariable.fetchByName("final_report_esave").getValue());
            if ( !esave)
                return;
        } catch (Exception e) {
            log.fine("No 'final_report_esave' system variable defined; will not save previous copies");
            return;
        }
        /*
         * all the esave attachments need to be owned by section "system"
         */
        try {
            sname = systemVariable.fetchByName("system_section").getValue();
            sid = section.getByName(sname).getId();
        } catch (Exception e) {
            log.severe("No 'system_section' system variable defined");
            sid = null;
        }

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * find the sample
         */
        try {
            reportList = sample.fetchForFinalReportSingle(accession);
            if (reportList.size() > 0) {
                result = reportList.get(0);
                print = new OrganizationPrint(result.getOrganizationId(),
                                              result.getOrganizationName(),
                                              result.getOrganizationAttention(),
                                              result.getSampleId(),
                                              result.getAccessionNumber(),
                                              result.getRevision(),
                                              result.getDomain());
                printList = new ArrayList<OrganizationPrint>();
                printList.add(print);
            } else {
                log.warning("Final report (esave) for accession number " + accession +
                            " has incorrect status,\nmissing information, or has no analysis ready to be printed");
                return;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Trying to find a sample", e);
            throw e;
        }

        print(printList, "R", false, status, "-attachment-");

        /*
         * save it as attachment to the sample
         */
        att = attachment.add(status.getPath(),
                             Messages.get().finalreport_attachmentEsaveDescription(result.getAccessionNumber(),
                                                                                   result.getRevision()),
                             sid);
        attachmentItem.add(new AttachmentItemDO(0, result.getSampleId(), Constants.table().SAMPLE,
                                                att.getId()));
    }

    /**
     * This method is called from cron to print all the environmental type
     * samples.
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReportForBatch() {
        ArrayList<QueryData> list;
        String printer;
        QueryData field;

        try {
            printer = systemVariable.fetchByName("final_report_env_printer").getValue();
        } catch (Exception e) {
            log.severe("No 'final_report_env_printer' system variable defined");
            return;
        }

        field = new QueryData();
        field.setKey("PRINTER");
        field.setQuery(printer);
        field.setType(QueryData.Type.STRING);

        list = new ArrayList<QueryData>();
        list.add(field);

        try {
            runReportForBatch(list);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Final Report batch", e);
        }
    }

    /**
     * This method prints final reports for all ready to be printed samples. The
     * routine time stamps all the analyses' printed date with current time and
     * groups the output by organization.
     */

    @RolesAllowed("r_final-select")
    @TransactionTimeout(600)
    public ReportStatus runReportForBatch(ArrayList<QueryData> paramList) throws Exception {
        Integer cachedOrgId, zero;
        Boolean lockSucceeded, orgPrint;
        String printer, orgFaxNumber, faxGrpName;
        Datetime timeStamp;
        ReportStatus status;
        ArrayList<FinalReportVO> resultList;
        OrganizationPrint print;
        ArrayList<OrganizationPrint> printList;
        HashMap<String, QueryData> param;
        HashMap<Integer, Boolean> samLockMap;
        HashSet<Integer> anaSet;
        ArrayList<AuxDataViewDO> auxList;
        ArrayList<OrganizationParameterDO> orgParamList;

        /*
         * Recover the printer
         */
        param = ReportUtil.getMapParameter(paramList);
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * obtain the list of sample ids, organization ids and analysis ids
         */
        resultList = sample.fetchForFinalReportBatch();

        log.fine("Considering " + resultList.size() + " cases to run");
        if (resultList.size() == 0)
            return status;

        anaSet = new HashSet<Integer>();
        samLockMap = new HashMap<Integer, Boolean>();
        printList = new ArrayList<OrganizationPrint>();

        cachedOrgId = null;
        orgPrint = false;
        orgFaxNumber = null;

        try {
            faxGrpName = systemVariable.fetchByName("sample_fax_aux_data").getValue();
        } catch (Exception e) {
            log.fine("No 'sample_fax_aux_data' system variable defined");
            faxGrpName = null;
        }

        /*
         * loop through the list and lock all the samples obtained from
         * resultList that can be locked; the ones that can't be locked, skip
         * all the analysis in that sample (skip the sample)
         */
        log.fine("Locking all samples");

        print = null;
        zero = new Integer(0);
        for (FinalReportVO result : resultList) {
            lockSucceeded = samLockMap.get(result.getSampleId());
            if (lockSucceeded == null) {
                try {
                    lock.lock(Constants.table().SAMPLE, result.getSampleId());
                    lockSucceeded = Boolean.TRUE;
                } catch (Exception e) {
                    log.finer("Sample id " + result.getSampleId() + " can't be locked");
                    lockSucceeded = Boolean.FALSE;
                }
                samLockMap.put(result.getSampleId(), lockSucceeded);
            }

            if ( !lockSucceeded)
                continue;
            /*
             * List of analyses to timestamp.
             */
            anaSet.add(result.getAnalysisId());

            /*
             * only unique sample & org pairs
             */
            if (print != null &&
                print.getOrganizationId().equals(result.getOrganizationId()) &&
                ( !result.getOrganizationId().equals(zero) || print.getOrganizationName()
                                                                   .equals(result.getOrganizationName())) &&
                print.getSampleId().equals(result.getSampleId()))
                continue;

            print = new OrganizationPrint(result.getOrganizationId(),
                                          result.getOrganizationName(),
                                          result.getOrganizationAttention(),
                                          result.getSampleId(),
                                          result.getAccessionNumber(),
                                          result.getRevision(),
                                          result.getDomain());
            if ( !print.getOrganizationId().equals(cachedOrgId) ||
                print.getOrganizationId().equals(zero)) {
                orgPrint = true;
                orgFaxNumber = null;
                cachedOrgId = print.getOrganizationId();
                if ( !cachedOrgId.equals(zero)) {
                    try {
                        orgParamList = organizationParameter.fetchByOrganizationId(cachedOrgId);
                        for (OrganizationParameterDO orgParam : orgParamList) {
                            if (Constants.dictionary().ORG_FINALREP_FAX_NUMBER.equals(orgParam.getTypeId()))
                                orgFaxNumber = orgParam.getValue();
                            else if (Constants.dictionary().ORG_NO_FINALREPORT.equals(orgParam.getTypeId()))
                                orgPrint = false;
                        }
                    } catch (NotFoundException e) {
                        // ignore
                    }
                }
            }

            /*
             * Find out if this sample has the aux group assigned to it that
             * defines the values for faxing.Auxiliary fax info on sample is
             * applied only to the primary report to.
             */
            if ( (DataBaseUtil.isSame(Constants.dictionary().ORG_REPORT_TO,
                                      result.getOrganizationTypeId()) || zero.equals(result.getOrganizationTypeId())) &&
                !DataBaseUtil.isEmpty(faxGrpName)) {
                try {
                    auxList = auxData.fetchByRefIdRefTableIdGroupName(result.getSampleId(),
                                                                      Constants.table().SAMPLE,
                                                                      faxGrpName);
                    for (AuxDataViewDO aux : auxList) {
                        if ("final_report_fax_num".equals(aux.getAnalyteExternalId()))
                            print.setFaxNumber(aux.getValue());
                        else if ("fax_to_attention".equals(aux.getAnalyteExternalId()))
                            print.setFaxAttention(aux.getValue());
                    }
                } catch (NotFoundException e) {
                    // ignore
                }
            }
            /*
             * Fax info on sample overrides organization fax info. Additionally
             * organization fax info overrides do-not-print flag for
             * organization.
             */
            if (DataBaseUtil.isEmpty(print.getFaxNumber()) && !DataBaseUtil.isEmpty(orgFaxNumber))
                print.setFaxNumber(orgFaxNumber);

            if (orgPrint || !DataBaseUtil.isEmpty(print.getFaxNumber()))
                printList.add(print);
        }

        /*
         * update all the analyses with date printed
         */
        timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        analysis.updatePrintedDate(anaSet, timeStamp);

        log.fine("Printing the reports");
        print(printList, "R", true, status, printer);

        /*
         * unlock all the samples
         */
        log.fine("Unlocking all samples");
        for (Map.Entry<Integer, Boolean> entry : samLockMap.entrySet()) {
            if (entry.getValue())
                lock.unlock(Constants.table().SAMPLE, entry.getKey());
        }

        return status;
    }

    /**
     * Reprints a batch report for the specified printed date range. This
     * routine does not re-timestamp the analyses nor does it lock the samples
     */
    @RolesAllowed("r_final-select")
    @TransactionTimeout(600)
    public ReportStatus runReportForBatchReprint(ArrayList<QueryData> paramList) throws Exception {
        boolean orgPrint;
        Integer cachedOrgId, zero;
        String printer, orgFaxNumber, faxGrpName;
        ReportStatus status;
        ArrayList<FinalReportVO> resultList;
        ArrayList<OrganizationPrint> printList;
        Date beginPrinted, endPrinted;
        HashMap<String, QueryData> param;
        OrganizationPrint print;
        SimpleDateFormat format;
        ArrayList<AuxDataViewDO> auxList;
        ArrayList<OrganizationParameterDO> orgParamList;

        /*
         * Recover the printer and printed date range
         */
        param = ReportUtil.getMapParameter(paramList);
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        beginPrinted = format.parse(ReportUtil.getSingleParameter(param, "BEGIN_PRINTED"));
        endPrinted = format.parse(ReportUtil.getSingleParameter(param, "END_PRINTED"));

        /*
         * obtain the list of sample ids and organization ids
         */
        resultList = sample.fetchForFinalReportBatchReprint(beginPrinted, endPrinted);

        log.fine("Considering " + resultList.size() + " cases to run");
        if (resultList.size() == 0)
            return status;

        printList = new ArrayList<OrganizationPrint>();

        cachedOrgId = null;
        orgPrint = false;
        orgFaxNumber = null;

        try {
            faxGrpName = systemVariable.fetchByName("sample_fax_aux_data").getValue();
        } catch (Exception e) {
            log.fine("No 'sample_fax_aux_data' system variable defined");
            faxGrpName = null;
        }

        /*
         * loop through the list and mark the samples for printing or faxing
         */
        print = null;
        zero = new Integer(0);
        for (FinalReportVO result : resultList) {
            /*
             * only unique sample & org pairs
             */
            if (print != null &&
                print.getOrganizationId().equals(result.getOrganizationId()) &&
                ( !result.getOrganizationId().equals(zero) || print.getOrganizationName()
                                                                   .equals(result.getOrganizationName())) &&
                print.getSampleId().equals(result.getSampleId()))
                continue;

            print = new OrganizationPrint(result.getOrganizationId(),
                                          result.getOrganizationName(),
                                          result.getOrganizationAttention(),
                                          result.getSampleId(),
                                          result.getAccessionNumber(),
                                          result.getRevision(),
                                          result.getDomain());
            if ( !print.getOrganizationId().equals(cachedOrgId) ||
                print.getOrganizationId().equals(zero)) {
                orgPrint = true;
                orgFaxNumber = null;
                cachedOrgId = print.getOrganizationId();
                if ( !cachedOrgId.equals(zero)) {
                    try {
                        orgParamList = organizationParameter.fetchByOrganizationId(cachedOrgId);
                        for (OrganizationParameterDO orgParam : orgParamList) {
                            if (Constants.dictionary().ORG_FINALREP_FAX_NUMBER.equals(orgParam.getTypeId()))
                                orgFaxNumber = orgParam.getValue();
                            else if (Constants.dictionary().ORG_NO_FINALREPORT.equals(orgParam.getTypeId()))
                                orgPrint = false;
                        }
                    } catch (NotFoundException e) {
                        // ignore
                    }
                }
            }

            /*
             * Find out if this sample has the aux group assigned to it that
             * defines the values for faxing.Auxiliary fax info on sample is
             * applied only to the primary report to.
             */
            if (Constants.dictionary().ORG_REPORT_TO.equals(result.getOrganizationTypeId()) ||
                zero.equals(result.getOrganizationTypeId()) && !DataBaseUtil.isEmpty(faxGrpName)) {
                try {
                    auxList = auxData.fetchByRefIdRefTableIdGroupName(result.getSampleId(),
                                                                      Constants.table().SAMPLE,
                                                                      faxGrpName);
                    for (AuxDataViewDO aux : auxList) {
                        if ("final_report_fax_num".equals(aux.getAnalyteExternalId()))
                            print.setFaxNumber(aux.getValue());
                        else if ("fax_to_attention".equals(aux.getAnalyteExternalId()))
                            print.setFaxAttention(aux.getValue());
                    }
                } catch (NotFoundException e) {
                    // ignore
                }
            }
            /*
             * Fax info on sample overrides organization fax info. Additionally
             * organization fax info overrides do-not-print flag for
             * organization.
             */
            if (DataBaseUtil.isEmpty(print.getFaxNumber()) && !DataBaseUtil.isEmpty(orgFaxNumber))
                print.setFaxNumber(orgFaxNumber);

            if (orgPrint || !DataBaseUtil.isEmpty(print.getFaxNumber()))
                printList.add(print);
        }

        print(printList, "R", true, status, printer);

        return status;
    }

    /**
     * Prints final report for a set of sample data stored in user's session.
     * This session list is the result of previously run query that validated
     * user's permission and access.
     */
    @RolesAllowed({"w_final_environmental-select", "w_final_privatewell-select",
                    "w_final_sdwis-select"})
    public ReportStatus runReportForWeb(ArrayList<QueryData> paramList) throws Exception {
        int i, indexList[];
        ReportStatus status;
        OrganizationPrint orgPrint;
        FinalReportWebVO data;
        HashMap<String, QueryData> param;
        ArrayList<OrganizationPrint> orgPrintList;
        ArrayList<FinalReportWebVO> sampleList;

        /*
         * Retrieve the indexes of the sample ids selected for the report as an
         * array of integers.
         */
        param = ReportUtil.getMapParameter(paramList);
        indexList = ReportUtil.getArrayParameter(param, "SAMPLE_ID");
        if (indexList == null)
            throw new InconsistencyException("No sample(s) were selected for the report");

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * Find the organization id and sample id corresponding to the selected
         * sample indices from the list of samples in session.
         */
        sampleList = (ArrayList<FinalReportWebVO>)session.getAttribute("sampleList");
        orgPrintList = new ArrayList<OrganizationPrint>();
        for (i = 0; i < indexList.length; i++ ) {
            try {
                data = sampleList.get(indexList[i]);
            } catch (Exception e) {
                throw new InconsistencyException("The sample search list is no longer valid.\nPlease search again");
            }
            orgPrint = new OrganizationPrint();
            orgPrint.setOrganizationId(data.getOrganizationId());
            orgPrint.setSampleId(data.getId());
            orgPrint.setAccessionNumber(data.getAccessionNumber());
            orgPrint.setDomain(data.getDomain());
            orgPrint.setRevision(data.getRevision());
            orgPrintList.add(orgPrint);
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("No sample(s) were selected for the report");

        print(orgPrintList, "R", false, status, "-view-");

        return status;
    }

    /**
     * This method generates the actual report using jasper. The jasper output
     * is directed to either a printer or the faxing system.
     */
    private void print(ArrayList<OrganizationPrint> orgPrintList, String reportType,
                       boolean forMailing, ReportStatus status, String printer) throws Exception {
        int i;
        URL url;
        File tempFile;
        Connection con;
        JasperReport jreport;
        String dir, uploadDir, printstat, toCompany, faxOwner, faxEmail, userName;
        JasperPrint print, faxPrint;
        List<JRPrintPage> pages;
        HashMap<String, Object> jparam;
        JasperPrint stats;
        OrganizationPrintDataSource ds;
        ArrayList<OrganizationPrint> orgFaxList;

        con = null;
        userName = User.getName(ctx);
        try {
            con = ReportUtil.getConnection(ctx);

            /*
             * get all the report instances
             */
            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/main.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            dir = ReportUtil.getResourcePath(url);

            jparam = new HashMap<String, Object>();
            jparam.put("REPORT_TYPE", reportType);
            jparam.put("SUBREPORT_DIR", dir);
            jparam.put("LOGNAME", userName);
            jparam.put("CONNECTION", con);

            /*
             * fill all the printed reports
             */
            ds = new OrganizationPrintDataSource(OrganizationPrintDataSource.Type.PRINT);
            ds.setData(orgPrintList);
            print = JasperFillManager.fillReport(jreport, jparam, ds);

            /*
             * process all the faxes
             * 
             * when fax is sent for a batch run, the fax system needs to
             * identify a fax owner and email notifier for errors. In case of
             * single faxing, the logged in user is used for both.
             */
            if (forMailing) {
                try {
                    faxOwner = systemVariable.fetchByName("system_fax_owner").getValue();
                    faxEmail = systemVariable.fetchByName("system_fax_email").getValue();
                } catch (Exception e) {
                    log.severe("Missing system_fax_owner and email in system variable");
                    faxOwner = null;
                    faxEmail = null;
                }
            } else {
                faxOwner = userName;
                faxEmail = faxOwner;
            }
            for (OrganizationPrint o : orgPrintList) {
                if (DataBaseUtil.isEmpty(o.getFaxNumber()))
                    continue;

                ds = new OrganizationPrintDataSource(OrganizationPrintDataSource.Type.FAX);
                orgFaxList = new ArrayList<OrganizationPrint>();
                orgFaxList.add(o);
                ds.setData(orgFaxList);
                faxPrint = JasperFillManager.fillReport(jreport, jparam, ds);
                if (faxPrint == null)
                    continue;

                tempFile = export(faxPrint);
                try {
                    /*
                     * For a single sample, the user can specify a different
                     * name for "To Company" than the default organization.
                     */
                    toCompany = o.getToCompany() != null ? o.getToCompany()
                                                        : o.getOrganizationName();
                    printstat = ReportUtil.fax(tempFile,
                                               o.getFaxNumber(),
                                               o.getFromCompany(),
                                               o.getFaxAttention(),
                                               toCompany,
                                               o.getFaxNote(),
                                               faxOwner,
                                               faxEmail);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.FAXED);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Sample Id: " + o.getSampleId() + " Organization: " +
                                          o.getOrganizationName() + ": " + e.getMessage());
                }
            }

            /*
             * generate state page at the end of print run to list all the
             * organizations printed/faxed.
             */
            if (forMailing) {
                ds = new OrganizationPrintDataSource(OrganizationPrintDataSource.Type.STATS);
                ds.setData(orgPrintList);
                url = ReportUtil.getResourceURL("org/openelis/report/finalreport/stats.jasper");
                stats = JasperFillManager.fillReport((JasperReport)JRLoader.loadObject(url),
                                                     jparam,
                                                     ds);
                pages = stats.getPages();
                for (i = 0; i < pages.size(); i++ )
                    print.addPage(pages.get(i));
            }

            if (print.getPages().size() > 0) {
                tempFile = export(print);
                if (ReportUtil.isPrinter(printer)) {
                    printstat = ReportUtil.print(tempFile, userName, printer, 1);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                } else if ("-attachment-".equals(printer)) {
                    status.setMessage(tempFile.getName())
                          .setPath(tempFile.getPath())
                          .setStatus(ReportStatus.Status.SAVED);
                } else {
                    tempFile = ReportUtil.saveForUpload(tempFile);
                    try {
                        uploadDir = systemVariable.fetchByName("upload_stream_directory")
                                                  .getValue();
                    } catch (Exception e) {
                        log.severe("No 'upload_stream_directory' system variable defined");
                        uploadDir = "";
                    }
                    status.setMessage(tempFile.getName())
                          .setPath(uploadDir)
                          .setStatus(ReportStatus.Status.SAVED);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e1) {
                log.severe("Could not close connection");
            }
        }
    }

    /*
     * Exports the filled report to a temp file for printing or faxing.
     */
    private File export(JasperPrint print) throws Exception {
        File tempFile;
        JRExporter jexport;

        jexport = new JRPdfExporter();
        tempFile = File.createTempFile("finalreport", ".pdf", new File("/tmp"));
        jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
        jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
        jexport.exportReport();

        return tempFile;
    }
}