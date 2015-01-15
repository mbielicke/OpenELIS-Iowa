package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.FinalReportBean;
import org.openelis.bean.ProjectBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.bean.UserCacheBean;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class FinalReportPortalBean {
    @EJB
    private SessionCacheBean            session;

    @EJB
    private ProjectBean                 project;

    @EJB
    private UserCacheBean               userCache;

    @EJB
    private FinalReportBean             finalReport;

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

    private static final SampleViewMeta meta = new SampleViewMeta();

    /**
     * Creates a query string from the passed query fields and returns the list
     * of sample views that the query returns
     */
    @RolesAllowed("w_final_report-select")
    public ArrayList<SampleViewVO> getSampleList(ArrayList<QueryData> fields) throws Exception {
        String clause;
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
                          ", " + SampleViewMeta.getCollector() + ", " +
                          SampleViewMeta.getProjectId() + ", " + SampleViewMeta.getProject() +
                          ", " + SampleViewMeta.getPwsNumber0() + ", " +
                          SampleViewMeta.getPatientLastName() + ", " +
                          SampleViewMeta.getPatientFirstName() + ", " +
                          SampleViewMeta.getPatientBirthDate());
        builder.constructWhere(fields);
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
            returnList.add(new SampleViewVO((Integer)result[0],
                                            (String)result[1],
                                            (Integer)result[2],
                                            (Integer)result[3],
                                            null,
                                            (Date)result[4],
                                            (Date)result[5],
                                            (Integer)result[6],
                                            (String)result[7],
                                            (Date)result[8],
                                            (Integer)result[9],
                                            null,
                                            (String)result[10],
                                            null,
                                            null,
                                            (Integer)result[11],
                                            (String)result[12],
                                            (String)result[13],
                                            null,
                                            null,
                                            (String)result[14],
                                            (String)result[15],
                                            (Date)result[16],
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null));
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

        finalReport.print(orgPrintList, "R", false, status, "-view-");

        return status;
    }

    /**
     * Fetches the list of projects that the user has access to
     */
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        String clause;

        clause = userCache.getPermission().getModule("w_final_report").getClause();

        if (clause != null)
            return project.fetchForOrganizations(clause);

        return new ArrayList<IdNameVO>();
    }

}