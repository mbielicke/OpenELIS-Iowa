package org.openelis.bean;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.FinalReportVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.report.finalreport.OrganizationPrintDataSource;
import org.openelis.report.finalreport.PageCounter;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class FinalReportBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

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
    private AttachmentManagerBean     attachmentManager;

    @EJB
    private AttachmentItemBean        attachmentItem;

    @EJB
    private SectionCacheBean          section;

    @EJB
    private ProjectBean                 project;

    @EJB
    private UserCacheBean               userCache;

    private static final Logger       log = Logger.getLogger("openelis");
    private static final SampleViewMeta meta = new SampleViewMeta();

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
                                                          .setMultiSelect(false)
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
                                                          .setMultiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create result prompts", e);
            throw e;
        }
    }

    /**
     * Fetches the list of projects that the user has access to
     */
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        String clause;

        clause = userCache.getPermission().getModule("w_final_report").getClause();

        if (clause != null) {
            clause = replaceClauseForProject(clause);
            return project.fetchForOrganizations(clause);
        }

        return new ArrayList<IdNameVO>();
    }

    /**
     * Creates a query string from the passed query fields and returns the list
     * of sample views that the query returns
     */
    @RolesAllowed("w_final_report-select")
    public ArrayList<SampleViewVO> getSampleList(ArrayList<QueryData> fields) throws Exception {
        String clause, range;
        Query query;
        QueryBuilderV2 builder;
        List<Object[]> results;
        ArrayList<SampleViewVO> returnList;

        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = userCache.getPermission().getModule("w_final_report").getClause();

        if (clause == null)
            return new ArrayList<SampleViewVO>();

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleViewMeta.getId() + ", " + SampleViewMeta.getDomain() +
                          "," + SampleViewMeta.getAccessionNumber() + ", " +
                          SampleViewMeta.getRevision() + ", " + SampleViewMeta.getCollectionDate() +
                          ", " + SampleViewMeta.getCollectionTime() + ", " +
                          SampleViewMeta.getStatusId() + ", " +
                          SampleViewMeta.getClientReference() + ", " +
                          SampleViewMeta.getReleasedDate() + ", " + SampleViewMeta.getReportToId() +
                          ", " + SampleViewMeta.getReportTo() + "," +
                          SampleViewMeta.getCollector() + ", " + SampleViewMeta.getLocation() +
                          ", " + SampleViewMeta.getLocationCity() + ", " +
                          SampleViewMeta.getProjectId() + ", " + SampleViewMeta.getProject() +
                          ", " + SampleViewMeta.getPwsNumber0() + ", " +
                          SampleViewMeta.getPwsName() + ", " + SampleViewMeta.getPatientLastName() +
                          ", " + SampleViewMeta.getPatientFirstName() + ", " +
                          SampleViewMeta.getPatientBirthDate() + ", " +
                          SampleViewMeta.getProvider());
        range = getReleasedDateRange(fields);
        builder.constructWhere(fields);
        if (range != null) {
            builder.addWhere("(" + SampleViewMeta.getReleasedDate() + " between '" + range +
                             "' OR " + SampleViewMeta.getAnalysisReleasedDate() + " between '" +
                             range + "')");
        }
        builder.addWhere("(" + clause + ")");
        builder.addWhere(SampleViewMeta.getStatusId() + " !=" + Constants.dictionary().SAMPLE_ERROR);
        builder.addWhere(SampleViewMeta.getAnalysisStatusId() + "=" +
                         Constants.dictionary().ANALYSIS_RELEASED);
        builder.addWhere(SampleViewMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleViewMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        results = query.getResultList();

        returnList = new ArrayList<SampleViewVO>();
        for (Object[] result : results) {
            returnList.add(new SampleViewVO((Integer)result[0],// id
                                            (String)result[1],// domain
                                            (Integer)result[2],// accession
                                            (Integer)result[3],// revision
                                            null,// received
                                            (Date)result[4],// collected date
                                            (Date)result[5],// collected time
                                            (Integer)result[6],// sample status
                                            (String)result[7],// client ref
                                            (Date)result[8],// released
                                            (Integer)result[9],// org id
                                            (String)result[10],// org name
                                            (String)result[11],// collector
                                            (String)result[12],// location
                                            (String)result[13],// location city
                                            (Integer)result[14],// project id
                                            (String)result[15],// project name
                                            (String)result[16],// number0
                                            (String)result[17],// pws name
                                            null,// facility id
                                            (String)result[18],// patient last
                                            (String)result[19],// patient first
                                            (Date)result[20],// patient birth
                                            (String)result[21],// provider
                                            null,// analysis id
                                            null,// analysis revision
                                            null,// reportable
                                            null,// analysis status
                                            null,// analysis released
                                            null,// test desc
                                            null));// method desc
        }
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    /**
     * Final report for a single or reprint. The report is printed for the
     * primary or secondary organization(s) ordered by organization.
     */
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
        Integer orgId, typeId, accession, zero;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String printer, faxNumber, fromCompany, faxAttention, toCompany, faxNote;
        HashMap<String, QueryData> param;
        ArrayList<FinalReportVO> results;
        ArrayList<OrganizationPrint> orgPrintList;

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        accession = ReportUtil.getIntegerParameter(param, "ACCESSION_NUMBER");
        orgId = ReportUtil.getIntegerParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getStringParameter(param, "PRINTER");
        faxNumber = ReportUtil.getStringParameter(param, "FAX_NUMBER");
        fromCompany = ReportUtil.getStringParameter(param, "FROM_COMPANY");
        toCompany = ReportUtil.getStringParameter(param, "TO_COMPANY");
        faxAttention = ReportUtil.getStringParameter(param, "FAX_ATTENTION");
        faxNote = ReportUtil.getStringParameter(param, "FAX_NOTE");

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
            results = sample.fetchForFinalReportSingle(accession);
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
        Integer accession;
        HashMap<String, QueryData> param;
        FinalReportVO result;
        ArrayList<OrganizationPrint> printList;

        /*
         * Recover the accession number
         */
        param = ReportUtil.getMapParameter(paramList);
        accession = ReportUtil.getIntegerParameter(param, "ACCESSION_NUMBER");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * find the sample
         */
        try {
            result = sample.fetchForFinalReportPreview(accession);

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
     * The method returns a DO containing the data for the attachment and
     * attachment item created for the final report.
     */
    public AttachmentItemViewDO runReportForESave(Integer accession) throws Exception {
        boolean esave;
        Integer sid;
        String sname, filename;
        ReportStatus status;
        AttachmentDO att;
        AttachmentItemDO atti;
        FinalReportVO result;
        OrganizationPrint print;
        Datetime timeStamp;
        ArrayList<FinalReportVO> reportList;
        ArrayList<OrganizationPrint> printList;

        /*
         * skip if they don't want to save previous versions of final report
         */
        try {
            esave = Boolean.parseBoolean(systemVariable.fetchByName("final_report_esave")
                                                       .getValue());
            if ( !esave)
                return null;
        } catch (Exception e) {
            log.fine("No 'final_report_esave' system variable defined; will not save previous copies");
            return null;
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
                return null;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Trying to find a sample", e);
            throw e;
        }

        print(printList, "R", false, status, "-esave-");

        /*
         * save it as attachment to the sample
         */
        timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        filename = Messages.get()
                           .finalreport_attachmentEsaveFileName(result.getAccessionNumber()
                                                                      .toString(),
                                                                result.getRevision(),
                                                                ReportUtil.toString(timeStamp,
                                                                                    Messages.get()
                                                                                            .dateCompressedPattern()));
        att = attachmentManager.put(status.getPath(),
                                    false,
                                    filename,
                                    Messages.get()
                                            .finalreport_attachmentEsaveDescription(result.getAccessionNumber()
                                                                                          .toString(),
                                                                                    result.getRevision()),
                                    sid);
        atti = attachmentItem.add(new AttachmentItemDO(0,
                                                       result.getSampleId(),
                                                       Constants.table().SAMPLE,
                                                       att.getId()));

        return new AttachmentItemViewDO(atti.getId(),
                                        atti.getReferenceId(),
                                        atti.getReferenceTableId(),
                                        atti.getAttachmentId(),
                                        att.getCreatedDate().getDate(),
                                        att.getSectionId(),
                                        att.getDescription(),
                                        Messages.get().attachment_sampleDescription(accession));
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
        printer = ReportUtil.getStringParameter(param, "PRINTER");

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
            /*
             * Exclude PT domain samples
             */
            if (Constants.domain().PT.equals(result.getDomain()))
                continue;
            
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
        if (anaSet.size() > 0) {
            timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
            analysis.updatePrintedDate(anaSet, timeStamp);
        }
        
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
        ArrayList<AuxDataViewDO> auxList;
        ArrayList<OrganizationParameterDO> orgParamList;

        /*
         * Recover the printer and printed date range
         */
        param = ReportUtil.getMapParameter(paramList);
        printer = ReportUtil.getStringParameter(param, "PRINTER");

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        beginPrinted = ReportUtil.getTimestampParameter(param, "BEGIN_PRINTED");
        endPrinted = ReportUtil.getTimestampParameter(param, "END_PRINTED");

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
    @RolesAllowed("w_final_report-select")
    public ReportStatus runReportForPortal(ArrayList<QueryData> paramList) throws Exception {
        int i, indexList[];
        ReportStatus status;
        OrganizationPrint orgPrint;
        SampleViewVO data;
        HashMap<String, QueryData> param;
        ArrayList<OrganizationPrint> orgPrintList;
        ArrayList<SampleViewVO> sampleList;

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
        sampleList = (ArrayList<SampleViewVO>)session.getAttribute("sampleList");
        orgPrintList = new ArrayList<OrganizationPrint>();
        for (i = 0; i < indexList.length; i++ ) {
            try {
                data = sampleList.get(indexList[i]);
            } catch (Exception e) {
                throw new InconsistencyException("The sample search list is no longer valid.\nPlease search again");
            }
            orgPrint = new OrganizationPrint();
            orgPrint.setOrganizationId(data.getReportToId());
            orgPrint.setSampleId(data.getSampleId());
            orgPrint.setAccessionNumber(data.getAccessionNumber());
            orgPrint.setDomain(data.getDomain());
            orgPrint.setRevision(data.getSampleRevision());
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
    public void print(ArrayList<OrganizationPrint> orgPrintList, String reportType,
                      boolean forMailing, ReportStatus status, String printer) throws Exception {
        int i;
        URL url;
        Path path;
        Connection con;
        JasperReport jreport;
        String dir, printstat, toCompany, faxOwner, faxEmail, userName;
        JasperPrint jprint, faxPrint;
        List<JRPrintPage> pages;
        HashMap<String, Object> jparam;
        JasperPrint stats;
        PageCounter pageCounter;
        OrganizationPrintDataSource ds;
        ArrayList<OrganizationPrint> orgFaxList;

        con = null;
        userName = User.getName(ctx);
        pageCounter = new PageCounter();
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
            jparam.put("PAGE_COUNTER", pageCounter);

            /*
             * fill all the printed reports
             */
            ds = new OrganizationPrintDataSource(OrganizationPrintDataSource.Type.PRINT);
            ds.setData(orgPrintList);
            jprint = JasperFillManager.fillReport(jreport, jparam, ds);

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

                path = export(faxPrint, null);
                try {
                    /*
                     * For a single sample, the user can specify a different
                     * name for "To Company" than the default organization.
                     */
                    toCompany = o.getToCompany() != null ? o.getToCompany()
                                                        : o.getOrganizationName();
                    printstat = ReportUtil.fax(path,
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
                    jprint.addPage(pages.get(i));
            }

            if (jprint.getPages().size() > 0) {
                if (ReportUtil.isPrinter(printer)) {
                    path = export(jprint, null);
                    printstat = ReportUtil.print(path, userName, printer, 1, true);
                    status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
                } else if ("-esave-".equals(printer)) {
                    path = export(jprint, null);
                    status.setMessage(path.getFileName().toString())
                          .setPath(path.toString())
                          .setStatus(ReportStatus.Status.SAVED);
                } else {
                    path = export(jprint, "upload_stream_directory");
                    status.setMessage(path.getFileName().toString())
                          .setPath(path.toString())
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
    private Path export(JasperPrint print, String systemVariableDirectory) throws Exception {
        Path path;
        JRExporter jexport;
        OutputStream out;

        out = null;
        try {
            jexport = new JRPdfExporter();
            path = ReportUtil.createTempFile("finalreport", ".pdf", systemVariableDirectory);
            out = Files.newOutputStream(path);
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jexport.exportReport();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for final report");
            }
        }
        return path;
    }

    /**
     * replace table and field names in the clause for project query
     */
    private String replaceClauseForProject(String clause) {
        if (clause == null)
            return clause;
        clause = clause.replace("_sampleView.reportToId", "_sampleOrganization.organizationId");
        clause = clause.replace("_sampleView.projectId", "_sampleProject.projectId");
        return clause;
    }

    /**
     * adds an OR clause to the query fields to include analysis released date
     * along with the sample released date range
     */
    private String getReleasedDateRange(ArrayList<QueryData> fields) {
        String range;
        QueryData field;

        field = null;
        range = null;
        for (QueryData data : fields) {
            if (SampleViewMeta.getReleasedDate().equals(data.getKey())) {
                field = data;
                range = data.getQuery();
                break;
            }
        }
        if (field != null)
            fields.remove(field);
        if (range != null)
            return range.replace("..", ":00' and '").concat(":00");
        return null;
    }
}