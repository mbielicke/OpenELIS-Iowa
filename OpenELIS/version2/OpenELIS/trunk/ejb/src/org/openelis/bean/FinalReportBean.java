package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
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

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalFinalReportWebVO;
import org.openelis.domain.SampleFinalReportWebVO;
import org.openelis.domain.SamplePrivateWellFinalReportWebVO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISFinalReportWebVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.FinalReportLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.report.finalreport.StatsDataSource;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class FinalReportBean implements FinalReportRemote, FinalReportLocal {

    @EJB
    private SessionCacheLocal          session;

    @EJB
    private SampleLocal                sample;

    @EJB
    private LockLocal                  lock;

    @EJB
    private AnalysisLocal              analysis;

    @EJB
    private DictionaryLocal            dictionary;

    @EJB
    private SampleProjectLocal         sampleProject;
    
    @EJB
    private OrganizationLocal          organization;
    
    @EJB
    private PrinterCacheLocal          printer;
    
    @Resource
    private SessionContext             ctx;

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

    private static int                 UNFOLDABLE_PAGE_COUNT = 6;

    private static Integer             organizationReportToId, sampleInErrorId, analysisReleasedId;

    private static final SampleWebMeta meta = new SampleWebMeta();
    
    private static final Logger log = Logger.getLogger(FinalReportBean.class);

    @PostConstruct
    public void init() {
        try {
            organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
            sampleInErrorId = dictionary.fetchBySystemName("sample_error").getId();
            analysisReleasedId = dictionary.fetchBySystemName("analysis_released").getId();
        } catch (Throwable e) {
            log.error("Failed to lookup constants for dictionary entries", e);
        }
    }

    /**
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPromptsForSingle() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("ACCESSION_NUMBER", Prompt.Type.INTEGER).setPrompt("Accession Number:")
                                                                     .setWidth(75)
                                                                     .setRequired(true));
            prn = printer.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Returns the prompt for a batch print
     */
    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            prn = printer.getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.error(e);
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

            p.add(new Prompt("BEGIN_PRINTED", Prompt.Type.DATETIME)
             .setPrompt("Begin Printed:")
             .setWidth(130)
             .setDatetimeStartCode(Prompt.Datetime.YEAR)
             .setDatetimeEndCode(Prompt.Datetime.MINUTE)
             .setRequired(true));

            p.add(new Prompt("END_PRINTED", Prompt.Type.DATETIME)
             .setPrompt("End Printed:")
             .setWidth(130)
             .setDatetimeStartCode(Prompt.Datetime.YEAR)
             .setDatetimeEndCode(Prompt.Datetime.MINUTE)
             .setRequired(true));

            prn = printer.getListByType("pdf");
            prn.add(0, new OptionListItem("-view-", "View PDF"));
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            return p;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * Final report for a single or reprint. The report is printed for the
     * primary or secondary organization(s) ordered by organization.
     */
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
        SampleDO data;
        Integer orgId;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String orgParam, accession, printer;
        HashMap<String, QueryData> param;
        ArrayList<Object[]> results;
        ArrayList<OrganizationPrint> orgPrintList;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("FinalReport", status);

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");
        orgParam = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(accession) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the accession number and printer for this report");
        /*
         * find the sample
         */
        try {
            data = sample.fetchByAccessionNumber(Integer.parseInt(accession));
        } catch (NotFoundException e) {
            throw new NotFoundException("A sample with accession number " + accession +
                                        " is not valid or does not exist");
        } catch (Exception e) {
            throw e;
        }

        /*
         * find all the report to organizations for given sample
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        try {
            results = sample.fetchSamplesForFinalReportSingle(data.getId());
            status.setMessage("Initializing report");
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
            for (Object[] result : results) {
                if (orgId == null || DataBaseUtil.isSame(orgId, result[1])) {
                    orgPrint = new OrganizationPrint();
                    orgPrint.setOrganizationId((Integer)result[1]);
                    orgPrint.setSampleIds(data.getId());
                    orgPrintList.add(orgPrint);
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw e;
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("Final report for accession number " + accession +
                                             " has incorrect status,\nmissing information, or has no analysis ready to be printed");

        print(orgPrintList, "R", false, status, printer);

        return status;
    }

    /**
     * Final report for a single or reprint. The report is printed for the
     * primary or secondary organization(s) ordered by organization.
     */
    public ReportStatus runReportForPreview(ArrayList<QueryData> paramList) throws Exception {
        SampleDO data;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String accession;
        HashMap<String, QueryData> param;
        Object[] result;
        ArrayList<Object[]> results;
        ArrayList<OrganizationPrint> orgPrintList;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("FinalReport", status);

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");

        /*
         * find the sample
         */
        try {
            data = sample.fetchByAccessionNumber(Integer.parseInt(accession));
        } catch (NotFoundException e) {
            throw new NotFoundException("A sample with accession number " + accession +
                                        " is not valid or does not exist");
        } catch (Exception e) {
            throw e;
        }

        /*
         * find all the report to organizations for given sample
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        try {
            results = sample.fetchSamplesForFinalReportPreview(data.getId());
            status.setMessage("Initializing report");

            if (results.size() < 1)
                throw new InconsistencyException("Final report for accession number " + accession +
                                                 " has incorrect status,\nmissing information, or has no analysis ready to be printed");

            result = results.get(0);
            orgPrint = new OrganizationPrint();
            orgPrint.setOrganizationId((Integer)result[1]);
            orgPrint.setSampleIds(data.getId());
            orgPrintList.add(orgPrint);
        } catch (Exception e) {
            throw e;
        }

        print(orgPrintList, "C", false, status, "-view-");

        return status;
    }

    /**
     * Prints final reports for all ready to be printed samples. The routine
     * time stamps all the analyses' printed date with current time and groups
     * the output by organization.
     * 
     * Additionally, because we use automatic folding machine, the report sorts
     * the entire output by the # of pages for each organization. A BLANK page
     * is inserted between all the reports that have less than 6 pages and the
     * remaining reports to stop the folding process.
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReportForBatch() throws Exception {
        ArrayList<QueryData> list;
        QueryData field;
        
        field = new QueryData();
        field.key = "PRINTER";
        field.query = ReportUtil.getSystemVariableValue("final_report_env_printer");        
        field.type = QueryData.Type.STRING;
        
        if (field.query == null) {
            log.error("No 'final_report_env_printer' system variable defined");
            return;
        }
        
        list = new ArrayList<QueryData>();
        list.add(field);
        
        runReportForBatch(list);        
    }       
    
    @RolesAllowed("r_final-select")
    @TransactionTimeout(600)
    public ReportStatus runReportForBatch(ArrayList<QueryData> paramList) throws Exception {
        int i;
        String printer;
        Datetime timeStamp;
        ReportStatus status;
        Object[] result, list;
        ArrayList<Integer> lockList;
        ArrayList<Object[]> resultList;
        ArrayList<OrganizationPrint> orgPrintList;
        HashMap<String, QueryData> param;
        HashMap<Integer, HashMap<Integer, Integer>> orgMap;
        HashMap<Integer, Integer> anaMap, samMap;
        Integer samId, prevSamId, orgId, anaId;
        Iterator<Integer> orgIter;
        OrganizationPrint orgPrint;

        /*
         * Recover the printer
         */
        param = ReportUtil.getMapParameter(paramList);
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        /*
         * obtain the list of sample ids, organization ids and analysis ids
         */
        samMap = null;
        prevSamId = null;
        anaMap = new HashMap<Integer, Integer>();
        lockList = new ArrayList<Integer>();
        orgMap = new HashMap<Integer, HashMap<Integer, Integer>>();

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * loop through the list and lock all the samples obtained from
         * resultList that can be locked; the ones that can't be locked, skip
         * all the analysis in that sample (skip the sample)
         */
        resultList = sample.fetchSamplesForFinalReportBatch();
        
        log.debug("Considering "+ resultList.size()+ " cases to run");        
        if (resultList.size() == 0)
            return status;
        
        log.debug("Locking all samples");   
        i = 0;        
        while (i < resultList.size()) {
            result = resultList.get(i++);
            samId = (Integer)result[0];
            orgId = (Integer)result[1];
            anaId = (Integer)result[2];

            if ( !samId.equals(prevSamId)) {
                try {
                    lock.lock(ReferenceTable.SAMPLE, samId);
                    lockList.add(samId);
                } catch (Exception e) {
                    /*
                     * skip all the samples that can't be locked.
                     */
                    while (i < resultList.size() && samId.equals(resultList.get(i)[0]))
                        i++ ;
                    prevSamId = null;
                    continue;
                }
            }
            /*
             * we are adding this sample id to the list of samples maintained
             * for this organization
             */
            samMap = orgMap.get(orgId);
            if (samMap == null) {
                samMap = new HashMap<Integer, Integer>();
                orgMap.put(orgId, samMap);
            }
            /*
             * keep a unique analysis id list for update
             */
            samMap.put(samId, samId);
            anaMap.put(anaId, anaId);
            prevSamId = samId;
        }

        /*
         * update all the analyses with date printed
         */
        timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        for (Integer key : anaMap.keySet())
            analysis.updatePrintedDate(key, timeStamp);

        /*
         * create what we need to print and call print
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        orgIter = orgMap.keySet().iterator();
        while (orgIter.hasNext()) {
            orgId = orgIter.next();
            samMap = orgMap.get(orgId);
            list = samMap.values().toArray();
            /*
             * samples with null organizations (such as private well) are
             * managed as single print rather then a batch for null organization
             */
            if (orgId == null) {
                for (i = 0; i < list.length; i++ ) {
                    orgPrint = new OrganizationPrint();
                    orgPrint.setOrganizationId(orgId);
                    orgPrint.setSampleIds((Integer)list[i]);
                    orgPrintList.add(orgPrint);
                }
            } else {
                orgPrint = new OrganizationPrint();
                orgPrint.setOrganizationId(orgId);
                orgPrint.setSampleIds(list);
                orgPrintList.add(orgPrint);
            }
        }
        log.debug("Printing the reports"); 
        print(orgPrintList, "R", true, status, printer);

        /*
         * unlock all the samples
         */
        log.debug("Unlocking all samples"); 
        for (Integer id : lockList)
            lock.unlock(ReferenceTable.SAMPLE, id);

        return status;
    }

    /**
     * Reprints a batch report for the specified printed date range.  This routine
     * does not re-timestamp the analyses nor does it lock the samples
     */
    @RolesAllowed("r_final-select")
    @TransactionTimeout(600)
    public ReportStatus runReportForBatchReprint(ArrayList<QueryData> paramList) throws Exception {
        int i;
        String printer;
        ReportStatus status;
        Object[] result, list;
        ArrayList<Object[]> resultList;
        ArrayList<OrganizationPrint> orgPrintList;
        Date beginPrinted, endPrinted;
        HashMap<String, QueryData> param;
        HashMap<Integer, HashMap<Integer, Integer>> orgMap;
        HashMap<Integer, Integer> anaMap, samMap;
        Integer samId, orgId, anaId;
        Iterator<Integer> orgIter;
        OrganizationPrint orgPrint;
        SimpleDateFormat format;

        /*
         * Recover the printer and printed date range
         */
        param = ReportUtil.getMapParameter(paramList);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        beginPrinted = format.parse(ReportUtil.getSingleParameter(param, "BEGIN_PRINTED"));
        endPrinted = format.parse(ReportUtil.getSingleParameter(param, "END_PRINTED"));
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        /*
         * obtain the list of sample ids, organization ids and analysis ids
         */
        samMap = null;
        anaMap = new HashMap<Integer, Integer>();
        orgMap = new HashMap<Integer, HashMap<Integer, Integer>>();

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * loop through the list and lock all the samples obtained from
         * resultList
         */
        resultList = sample.fetchSamplesForFinalReportBatchReprint(beginPrinted, endPrinted);
        i = 0;
        while (i < resultList.size()) {
            result = resultList.get(i++);
            samId = (Integer)result[0];
            orgId = (Integer)result[1];
            anaId = (Integer)result[2];

            /*
             * we are adding this sample id to the list of samples maintained
             * for this organization
             */
            samMap = orgMap.get(orgId);
            if (samMap == null) {
                samMap = new HashMap<Integer, Integer>();
                orgMap.put(orgId, samMap);
            }
            /*
             * keep a unique analysis id list for update
             */
            samMap.put(samId, samId);
            anaMap.put(anaId, anaId);
        }

        /*
         * create what we need to print and call print
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        orgIter = orgMap.keySet().iterator();
        while (orgIter.hasNext()) {
            orgId = orgIter.next();
            samMap = orgMap.get(orgId);
            list = samMap.values().toArray();
            /*
             * samples with null organizations (such as private well) are
             * managed as single print rather then a batch for null organization
             */
            if (orgId == null) {
                for (i = 0; i < list.length; i++ ) {
                    orgPrint = new OrganizationPrint();
                    orgPrint.setOrganizationId(orgId);
                    orgPrint.setSampleIds((Integer)list[i]);
                    orgPrintList.add(orgPrint);
                }
            } else {
                orgPrint = new OrganizationPrint();
                orgPrint.setOrganizationId(orgId);
                orgPrint.setSampleIds(list);
                orgPrintList.add(orgPrint);
            }
        }
        print(orgPrintList, "R", true, status, printer);

        return status;
    }

    @RolesAllowed({"w_final_environmental-select", "w_final_privatewell-select", "w_final_sdwis-select"})
    public ReportStatus runReportForWeb(ArrayList<QueryData> paramList) throws Exception {
        int i, indexList[];
        Integer samIdList[];
        Object values[];
        Integer orgId, samId;
        ReportStatus status;
        OrganizationPrint orgPrint;
        SampleFinalReportWebVO data;
        HashMap<String, QueryData> param;
        HashMap<Integer, HashMap<Integer, Integer>> orgMap;
        HashMap<Integer, Integer> samMap;
        ArrayList<OrganizationPrint> orgPrintList;
        ArrayList<SampleFinalReportWebVO> sampleList;
        Iterator<Integer> orgIter;

        orgMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        data = new SampleFinalReportWebVO();

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        /*
         * Retrieve the indexes of the sample ids selected for the report as an
         * array of integers.
         */
        param = ReportUtil.getMapParameter(paramList);
        indexList = ReportUtil.getArrayParameter(param, "SAMPLE_ID");
        if (indexList == null)
            throw new InconsistencyException("No sample(s) were selected for the report");

        /*
         * Find the organization id and sample id corresponding to the selected
         * sample indices from the list of samples in session.
         */
        sampleList = (ArrayList<SampleFinalReportWebVO>)session.getAttribute("sampleList");
        for (i = 0; i < indexList.length; i++ ) {
            try {
                data = sampleList.get(indexList[i]);
            } catch (Exception e) {
                throw new InconsistencyException("The sample search list is no longer valid.\nPlease search again"); 
            }
            orgId = data.getOrganizationId();
            samId = data.getId();
            /*
             * creating a HashMap containing organizationId and the list of
             * samples which belongs to it.
             */
            samMap = orgMap.get(orgId);
            if (samMap == null) {
                samMap = new HashMap<Integer, Integer>();
                orgMap.put(orgId, samMap);
            }
            samMap.put(samId, samId);
        }

        /*
         * Create what we need to print
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        orgIter = orgMap.keySet().iterator();
        while (orgIter.hasNext()) {
            orgId = orgIter.next();
            samMap = orgMap.get(orgId);

            orgPrint = new OrganizationPrint();
            orgPrint.setOrganizationId(orgId);
            values = samMap.values().toArray();
            samIdList = new Integer[values.length];
            for (i = 0; i < values.length; i++) 
                samIdList[i] = (Integer)(values[i]);
            
            orgPrint.setSampleIds(samIdList);
            orgPrintList.add(orgPrint);
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("No sample(s) were selected for the report");

        print(orgPrintList, "R", false, status, "-view-");

        return status;
    }

    @RolesAllowed("w_final_environmental-select")
    public ArrayList<SampleEnvironmentalFinalReportWebVO> getSampleEnvironmentalList(ArrayList<QueryData> fields) throws Exception {
        int i, id;
        String clause, projName;
        Query query;
        QueryBuilderV2 builder;
        ArrayList<SampleProjectViewDO> sprjList;
        ArrayList<SampleEnvironmentalFinalReportWebVO> returnList;

        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_environmental")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if orgIds and projIds are empty or not.
         */
        if (clause == null)
            return new ArrayList<SampleEnvironmentalFinalReportWebVO>();

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.SampleEnvironmentalFinalReportWebVO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " + SampleWebMeta.getStatusId() +
                          ", " + SampleWebMeta.getEnvLocation() + ", " +
                          SampleWebMeta.getEnvCollector() + ", " +
                          SampleWebMeta.getLocationAddrCity() + ", " + "''" + ", " +
                          SampleWebMeta.getSampleOrgOrganizationId() + ") ");
        builder.constructWhere(fields);       
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getEnvSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        returnList = DataBaseUtil.toArrayList(query.getResultList());

        /*
         * From the retrieved list of samples, find the first permanent project
         * they belong to
         */
        for (i = 0; i < returnList.size(); i++ ) {
            id = returnList.get(i).getId();
            try {
                sprjList = sampleProject.fetchPermanentBySampleId(id);
                projName = sprjList.get(0).getProjectName();
            } catch (NotFoundException e) {
                projName ="";
            }
            returnList.get(i).setProjectName(projName);
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

    @RolesAllowed("w_final_privatewell-select")
    public ArrayList<SamplePrivateWellFinalReportWebVO> getSamplePrivateWellList(ArrayList<QueryData> fields) throws Exception {
        String clause;
        Query query;
        QueryBuilderV2 builder;
        ArrayList<SamplePrivateWellFinalReportWebVO> returnList;        
        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_privatewell")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if orgIds is empty or not.
         */
        if (clause == null)
            return new ArrayList<SamplePrivateWellFinalReportWebVO>();

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.SamplePrivateWellFinalReportWebVO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " + SampleWebMeta.getStatusId() +
                          ", " + SampleWebMeta.getWellLocation() + ", " +
                          SampleWebMeta.getWellOwner() + ", " + SampleWebMeta.getWellCollector() +
                          ", " + SampleWebMeta.getWellOrganizationAddrCity() + ", " +
                          SampleWebMeta.getWellOrganizationId() + ") ");
        builder.constructWhere(fields);
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getWellSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getWellOrganizationAddressId() + "=" +
                         SampleWebMeta.getWellOrganizationAddrId());
        builder.addWhere(SampleWebMeta.getStatusId() + " !=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        returnList = DataBaseUtil.toArrayList(query.getResultList());
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    @RolesAllowed("w_final_sdwis-select")
    public ArrayList<SampleSDWISFinalReportWebVO> getSampleSDWISList(ArrayList<QueryData> fields) throws Exception {
        String clause;
        Query query;
        QueryBuilderV2 builder;
        ArrayList<SampleSDWISFinalReportWebVO> returnList;
        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache().getPermission().getModule("w_final_sdwis").getClause();

        if (clause == null)
            return new ArrayList<SampleSDWISFinalReportWebVO>();
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.SampleSDWISFinalReportWebVO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " + SampleWebMeta.getStatusId() +
                          ", " + SampleWebMeta.getPwsNumber0() + ", " +
                          SampleWebMeta.getSDWISFacilityId() + ", " +
                          SampleWebMeta.getSDWISLocation() + ", " +
                          SampleWebMeta.getSDWISCollector() + ", " + SampleWebMeta.getPwsName() +
                          ", " + SampleWebMeta.getSampleOrgOrganizationId() + ") ");
        builder.constructWhere(fields);
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getSDWISSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getSDWISPwsId() + "=" + SampleWebMeta.getPwsId());
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        returnList = DataBaseUtil.toArrayList(query.getResultList());
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    @RolesAllowed("w_final_environmental-select")
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_environmental")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not. We only return the
         * list of projects
         */
        if (clause != null)             
            return sample.fetchProjectsForOrganizations(clause);        
        
        return new ArrayList<IdNameVO>();
    }

    @RolesAllowed("w_final_privatewell-select")
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_privatewell")
                           .getClause();

        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not.
         */
        if (clause != null)
            return sample.fetchProjectsForOrganizations(clause);

        return new ArrayList<IdNameVO>();
    }

    private void print(ArrayList<OrganizationPrint> orgPrintList, String reportType,
                       boolean forMailing, ReportStatus status, String printer) throws Exception {
        int i, n;
        boolean needBlank;
        URL url;
        File tempFile;
        Integer zero;
        Connection con;        
        JasperReport jreport;
        JRExporter jexport;
        String dir, printstat;
        StringBuffer sampleIds;
        JasperPrint print;
        List<JRPrintPage> pages;
        HashMap<String, Object> jparam;
        JasperPrint masterJprint, blankJprint, statsJprint;
        StatsDataSource ds;

        con = null;
        zero = new Integer(0);
        try {
            con = ReportUtil.getConnection(ctx);
            /*
             * get all the report instances
             */
            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/blank.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            blankJprint = JasperFillManager.fillReport(jreport, null);
            masterJprint = JasperFillManager.fillReport(jreport, null);

            dir = ReportUtil.getResourcePath(url);
            jparam = new HashMap<String, Object>();
            jparam.put("REPORT_TYPE", reportType);
            jparam.put("SUBREPORT_DIR", dir);
            jparam.put("LOGNAME", EJBFactory.getUserCache().getName());

            url = ReportUtil.getResourceURL("org/openelis/report/finalreport/main.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            /*
             * for each organization, print all the samples
             */
            for (OrganizationPrint o : orgPrintList) {
                if (o.getOrganizationId() == null) {
                    jparam.put("ORGANIZATION_ID", zero);
                } else {
                    jparam.put("ORGANIZATION_ID", o.getOrganizationId());
                    /*
                     * we check to see if this organization chose not to have 
                     * final report printed for or sent to it  
                     */
                    //if (forMailing && organization.hasDontPrintFinalReport(o.getOrganizationId()))
                        //continue;                                        
                }
                sampleIds = new StringBuffer();
                if (o.getSampleIds() != null && o.getSampleIds().length > 1) {
                    sampleIds.append("in (");
                    for (i = 0; i < o.getSampleIds().length; i++ ) {
                        if (i != 0)
                            sampleIds.append(",");
                        sampleIds.append(o.getSampleIds()[i]);
                    }
                    sampleIds.append(")");
                } else {
                    sampleIds.append("=").append(o.getSampleIds()[0]);
                }
                jparam.put("SAMPLE_ID", sampleIds.toString());
                jparam.put("ORGANIZATION_PRINT", o);
                o.setJprint(JasperFillManager.fillReport(jreport, jparam, con));
            }

            try {
                con.close();
                con = null;
            } catch (Exception e) {
                log.error(e);
            }

            /*
             * Sort the print by # of pages.
             */
            if (orgPrintList.size() > 1)
                Collections.sort(orgPrintList, new MyComparator());

            /*
             * assemble all the pages. put in a blank page between 6 and more
             * pages if we are going to mail them (for folding machine)
             */
            needBlank = forMailing;
            for (OrganizationPrint o : orgPrintList) {
                print = o.getJprint();
                if (print == null)
                    continue;
                pages = print.getPages();
                n = pages.size();
                if (n >= UNFOLDABLE_PAGE_COUNT && needBlank) {
                    masterJprint.addPage((JRPrintPage)blankJprint.getPages().get(0));
                    needBlank = false;
                }
                for (i = 0; i < n; i++ )
                    masterJprint.addPage((JRPrintPage)pages.get(i));
            }

            /*
             * the stat page at the end will list all the organizations printed
             * in this run.
             */
            if (forMailing) {
                ds = new StatsDataSource();
                ds.setStats(orgPrintList);
                url = ReportUtil.getResourceURL("org/openelis/report/finalreport/stats.jasper");
                statsJprint = JasperFillManager.fillReport((JasperReport)JRLoader.loadObject(url),
                                                           jparam,
                                                           ds);
                pages = statsJprint.getPages();
                n = pages.size();
                for (i = 0; i < n; i++ )
                    masterJprint.addPage((JRPrintPage)pages.get(i));
            }

            /*
             * Finally, print the pages
             */
            masterJprint.removePage(0);
            jexport = new JRPdfExporter();
            tempFile = File.createTempFile("finalreport", ".pdf", new File("/tmp"));
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, masterJprint);

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
            } catch (Exception e1) {
                log.error(e1);
            }
        }
    }

    class MyComparator implements Comparator<OrganizationPrint> {
        public int compare(OrganizationPrint o1, OrganizationPrint o2) {
            return o1.getJprint().getPages().size() - o2.getJprint().getPages().size();
        }
    }    
}
