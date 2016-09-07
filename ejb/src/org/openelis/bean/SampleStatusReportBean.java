/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.AnalysisQAEventBean;
import org.openelis.bean.ProjectBean;
import org.openelis.bean.SampleQAEventBean;
import org.openelis.bean.UserCacheBean;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleStatusReportBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

    @EJB
    private ProjectBean                 project;

    @EJB
    private SampleQAEventBean           sampleQa;

    @EJB
    private AnalysisQAEventBean         analysisQa;

    @EJB
    private UserCacheBean               userCache;

    @EJB
    private DictionaryCacheBean         dictionaryCache;

    @EJB
    private EventLogBean                eventLog;

    private static final SampleViewMeta meta = new SampleViewMeta();

    private static final Logger         log  = Logger.getLogger("openelis");

    /**
     * fetch samples that match the search criteria
     */
    @RolesAllowed("w_status-select")
    public ArrayList<SampleViewVO> getSampleListForSampleStatusReport(ArrayList<QueryData> fields) throws Exception {
        String clause, orgIds, source, text;
        HashMap<String, String> clauseMap;
        HashSet<Integer> accNumSet;
        ArrayList<SampleViewVO> returnList;

        returnList = new ArrayList<SampleViewVO>();

        /*
         * Retrieve the sql clause that limits what the user can access. Don't
         * allow an empty clause
         */
        clause = userCache.getPermission().getModule("w_status").getClause();
        if (clause == null)
            return returnList;

        /*
         * Create a string of organization ids from the clause in a format which
         * the QueryBuilder can understand.
         */
        clauseMap = ReportUtil.parseClauseAsString(clause);
        orgIds = clauseMap.get("organizationId");
        if (DataBaseUtil.isEmpty(orgIds))
            return returnList;

        returnList = getSamples(fields, orgIds);
        if (returnList.size() > 0) {
            /*
             * the event log records the accession numbers for all samples shown
             * in the report
             */
            accNumSet = new HashSet<Integer>();
            text = null;
            for (SampleViewVO data : returnList) {
                if ( !accNumSet.contains(data.getAccessionNumber())) {
                    accNumSet.add(data.getAccessionNumber());
                    text = DataBaseUtil.concatWithSeparator(text, ", ", data.getAccessionNumber());
                }
            }
            source = Messages.get().sampleStatusReport_eventLogMessage(userCache.getSystemUser()
                                                                                .getLoginName());
            try {
                eventLog.add(new EventLogDO(null,
                                            dictionaryCache.getIdBySystemName("log_type_report"),
                                            source,
                                            null,
                                            null,
                                            Constants.dictionary().LOG_LEVEL_INFO,
                                            null,
                                            null,
                                            text));
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to add log entry for: " + source, e);
            }
        }

        return returnList;
    }

    /**
     * fetch samples that match the search criteria
     */
    private ArrayList<SampleViewVO> getSamples(ArrayList<QueryData> fields, String clause) throws Exception {
        String range;
        QueryBuilderV2 builder;
        Query query;
        ArrayList<Object[]> resultList;
        ArrayList<SampleViewVO> returnList;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleViewMeta.getId() + "," + SampleViewMeta.getDomain() +
                          "," + SampleViewMeta.getAccessionNumber() + ", " +
                          SampleViewMeta.getReceivedDate() + ", " +
                          SampleViewMeta.getCollectionDate() + ", " +
                          SampleViewMeta.getCollectionTime() + ", " + SampleViewMeta.getStatusId() +
                          ", " + SampleViewMeta.getClientReference() + ", " +
                          SampleViewMeta.getReportToId() + ", " + SampleViewMeta.getReportTo() +
                          ", " + SampleViewMeta.getCollector() + ", " +
                          SampleViewMeta.getLocation() + "," + SampleViewMeta.getPwsNumber0() +
                          "," + SampleViewMeta.getPatientLastName() + "," +
                          SampleViewMeta.getPatientFirstName() + "," +
                          SampleViewMeta.getAnalysisId() + ", " +
                          SampleViewMeta.getAnalysisStatusId() + ", " +
                          SampleViewMeta.getTestReportingDescription() + ", " +
                          SampleViewMeta.getMethodReportingDescription());
        range = getReleasedDateRange(fields);
        builder.constructWhere(fields);
        if (range != null) {
            builder.addWhere("(" + SampleViewMeta.getReleasedDate() + " between '" + range +
                             "' OR " + SampleViewMeta.getAnalysisReleasedDate() + " between '" +
                             range + "')");
        }
        builder.addWhere(SampleViewMeta.getStatusId() + " != " +
                         Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.addWhere(SampleViewMeta.getAnalysisStatusId() + " != " +
                         Constants.dictionary().ANALYSIS_CANCELLED);
        builder.addWhere(SampleViewMeta.getReportToId() + clause);

        builder.setOrderBy(SampleViewMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        resultList = DataBaseUtil.toArrayList(query.getResultList());
        returnList = new ArrayList<SampleViewVO>();
        for (Object[] result : resultList) {
            returnList.add(new SampleViewVO((Integer)result[0],// id
                                            (String)result[1],// domain
                                            (Integer)result[2],// accession
                                            null,// revision
                                            (Date)result[3],// released
                                            (Date)result[4],// collected date
                                            (Date)result[5],// collected time
                                            (Integer)result[6],// sample status
                                            (String)result[7],// client ref
                                            null,// released
                                            (Integer)result[8],// org id
                                            (String)result[9],// org name
                                            (String)result[10],// collector
                                            (String)result[11],// location
                                            null,// location street address
                                            null,// location city
                                            null,// project id
                                            null,// project name
                                            (String)result[12],// number0
                                            null,// pws name
                                            null,// facility id
                                            (String)result[13],// patient last
                                            (String)result[14],// patient first
                                            null,// patient birth
                                            null,// provider
                                            (Integer)result[15],// analysis id
                                            null,// analysis revision
                                            null,// reportable
                                            (Integer)result[16],// analysis
                                                                // status
                                            null,// analysis released
                                            (String)result[17],// test desc
                                            (String)result[18]));// method desc
        }

        return returnList;
    }

    /**
     * fetch project list for the organizations that the user is allowed to
     * access
     */
    @RolesAllowed("w_status-select")
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        String clause;
        ArrayList<Integer> orgIds;
        HashMap<String, ArrayList<Integer>> orgMapArr;

        /*
         * Retrieve the sql clause that limits what the user can access. Don't
         * allow an empty clause
         */
        clause = userCache.getPermission().getModule("w_status").getClause();

        if (clause == null)
            return new ArrayList<IdNameVO>();

        /*
         * Create an ArrayList of organization ids from the clause in a format
         * which the QueryBuilder can understand.
         */
        orgMapArr = ReportUtil.parseClauseAsArrayList(clause);
        orgIds = orgMapArr.get("organizationId");

        /*
         * Adding projects for organizations from all domains into projectList.
         */
        return project.fetchForSampleStatusReport(orgIds);
    }

    /**
     * fetch sample QA events for the given samples
     */
    @RolesAllowed("w_status-select")
    public HashMap<Integer, ArrayList<String>> getSampleQaEvents(ArrayList<Integer> sampleIds) throws Exception {
        ArrayList<SampleQaEventViewDO> list;
        HashMap<Integer, ArrayList<String>> qaMap;

        list = sampleQa.fetchBySampleIds(sampleIds);
        qaMap = new HashMap<Integer, ArrayList<String>>();
        for (SampleQaEventViewDO sqeVDO : list) {
            if ( !Constants.dictionary().QAEVENT_INTERNAL.equals(sqeVDO.getTypeId())) {
                if (qaMap.get(sqeVDO.getSampleId()) == null)
                    qaMap.put(sqeVDO.getSampleId(), new ArrayList<String>());
                qaMap.get(sqeVDO.getSampleId()).add(sqeVDO.getQaEventReportingText());
            }
        }

        return qaMap;
    }

    /**
     * fetch analysis QA events for the given analyses
     */
    @RolesAllowed("w_status-select")
    public HashMap<Integer, ArrayList<String>> getAnalysisQaEvents(ArrayList<Integer> analysisIds) throws Exception {
        ArrayList<AnalysisQaEventViewDO> list;
        HashMap<Integer, ArrayList<String>> qaMap;

        list = analysisQa.fetchByAnalysisIds(analysisIds);
        qaMap = new HashMap<Integer, ArrayList<String>>();
        for (AnalysisQaEventViewDO aqeVDO : list) {
            if ( !Constants.dictionary().QAEVENT_INTERNAL.equals(aqeVDO.getTypeId())) {
                if (qaMap.get(aqeVDO.getAnalysisId()) == null)
                    qaMap.put(aqeVDO.getAnalysisId(), new ArrayList<String>());
                qaMap.get(aqeVDO.getAnalysisId()).add(aqeVDO.getQaEventReportingText());
            }
        }

        return qaMap;
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