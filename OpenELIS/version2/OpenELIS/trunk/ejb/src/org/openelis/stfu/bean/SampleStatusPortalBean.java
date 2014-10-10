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
package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
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
public class SampleStatusPortalBean {

    @EJB
    private ProjectBean                 project;

    @EJB
    private SampleQAEventBean           sampleQa;

    @EJB
    private AnalysisQAEventBean         analysisQa;

    @EJB
    private UserCacheBean               userCache;

    @PersistenceContext(unitName = "openelis")
    private EntityManager               manager;

    private static final SampleViewMeta meta = new SampleViewMeta();

    @RolesAllowed("w_status-select")
    public ArrayList<SampleViewVO> getSampleListForSampleStatusReport(ArrayList<QueryData> fields) throws Exception {
        int analysisId;
        Integer prevSampleId;
        String clause, orgIds;
        HashMap<String, String> clauseMap;
        ArrayList<SampleViewVO> returnList;
        ArrayList<SampleQaEventViewDO> sampleQAList;
        ArrayList<AnalysisQaEventViewDO> analysisQAList;

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
        // orgIds = clauseMap.get("organizationId");
        // if (DataBaseUtil.isEmpty(orgIds))
        // return returnList;

        returnList = getSamples(fields, clause);

        prevSampleId = -1;
        for (SampleViewVO vo : returnList) {
            if ( !prevSampleId.equals(vo.getSampleId())) {
                // try {
                // sampleQAList =
                // sampleQa.fetchExternalBySampleId(vo.getSampleId());
                // for (SampleQaEventViewDO sq : sampleQAList) {
                // if
                // (Constants.dictionary().QAEVENT_WARNING.equals(sq.getTypeId()))
                // {
                // vo.setSampleQA(QAEventType.WARNING);
                // } else if
                // (Constants.dictionary().QAEVENT_OVERRIDE.equals(sq.getTypeId()))
                // {
                // vo.setSampleQA(QAEventType.OVERRIDE);
                // break;
                // }
                // }
                // } catch (NotFoundException e) {
                // // ignore
                // }
            }
            analysisId = vo.getAnalysisId();
            // try {
            // analysisQAList =
            // analysisQa.fetchExternalByAnalysisId(analysisId);
            // for (AnalysisQaEventViewDO aq : analysisQAList) {
            // if
            // (Constants.dictionary().QAEVENT_WARNING.equals(aq.getTypeId())) {
            // vo.setAnalysisQA(QAEventType.WARNING);
            // } else if
            // (Constants.dictionary().QAEVENT_OVERRIDE.equals(aq.getTypeId()))
            // {
            // vo.setAnalysisQA(QAEventType.OVERRIDE);
            // break;
            // }
            // }
            // } catch (NotFoundException e) {
            // // ignore
            // }
            prevSampleId = vo.getSampleId();
        }
        return returnList;
    }

    private ArrayList<SampleViewVO> getSamples(ArrayList<QueryData> fields, String clause) throws Exception {
        QueryBuilderV2 builder;
        Query query;
        ArrayList<Object[]> resultList;
        ArrayList<SampleViewVO> returnList;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleViewMeta.getId() + "," +
                          SampleViewMeta.getAccessionNumber() + ", " +
                          SampleViewMeta.getReceivedDate() + ", " +
                          SampleViewMeta.getCollectionDate() + ", " +
                          SampleViewMeta.getCollectionTime() + ", " + SampleViewMeta.getStatusId() +
                          ", " + SampleViewMeta.getClientReference() + ", " +
                          SampleViewMeta.getReportToId() + ", " + SampleViewMeta.getReportTo() +
                          ", " + SampleViewMeta.getCollector() + ", " +
                          SampleViewMeta.getAnalysisId() + ", " +
                          SampleViewMeta.getAnalysisStatusId() + ", " +
                          SampleViewMeta.getTestReportingDescription() + ", " +
                          SampleViewMeta.getMethodReportingDescription());
        builder.constructWhere(fields);
        builder.addWhere(SampleViewMeta.getStatusId() + " != " +
                         Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.addWhere("(" + clause + ")");

        // TODO
        // builder.addWhere(SampleViewMeta.getReportToId() + clause);
        builder.setOrderBy(SampleViewMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        resultList = DataBaseUtil.toArrayList(query.getResultList());
        returnList = new ArrayList<SampleViewVO>();
        for (Object[] result : resultList) {
            returnList.add(new SampleViewVO((Integer)result[0],
                                            null,
                                            (Integer)result[1],
                                            null,
                                            (Date)result[2],
                                            (Date)result[3],
                                            (Date)result[4],
                                            (Integer)result[5],
                                            (String)result[6],
                                            null,
                                            (Integer)result[7],
                                            (String)result[8],
                                            (String)result[9],
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            (Integer)result[10],
                                            null,
                                            null,
                                            (Integer)result[11],
                                            (String)result[12],
                                            (String)result[13]));
        }

        return returnList;
    }

    @RolesAllowed("w_status-select")
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        String clause;
        ArrayList<Integer> orgIds;
        ArrayList<IdNameVO> projectList;
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
        projectList = project.fetchForSampleStatusReport(orgIds);

        return projectList;
    }

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
}
