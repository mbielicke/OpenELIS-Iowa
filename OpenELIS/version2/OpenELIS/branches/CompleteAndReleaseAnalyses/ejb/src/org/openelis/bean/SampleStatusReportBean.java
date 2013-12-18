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
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.domain.SampleStatusWebReportVO.QAEventType;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class SampleStatusReportBean {

    @EJB
    private SampleBean                sample;

    @EJB
    private ProjectBean                project;
    
    @EJB
    private SampleQAEventBean         sampleQa;

    @EJB
    private AnalysisQAEventBean        analysisQa;
    
    @EJB
    private UserCacheBean              userCache;

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

    private static final SampleWebMeta meta = new SampleWebMeta();

    @RolesAllowed("w_status-select")
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(ArrayList<QueryData> fields) throws Exception {
        int analysisId;
        Integer prevSampleId;
        String clause, orgIds;
        HashMap<String, String> clauseMap;
        ArrayList<SampleStatusWebReportVO> returnList;
        ArrayList<IdAccessionVO> sampleIdList;
        ArrayList<SampleQaEventViewDO> sampleQAList;
        ArrayList<AnalysisQaEventViewDO> analysisQAList;
        HashMap<Integer, Integer> idMap;

        returnList = new ArrayList<SampleStatusWebReportVO>();

        /*
         * Retrieve the sql clause that limits what the user can access. Don't
         * allow an empty clause
         */
        clause = userCache.getPermission()
                          .getModule("w_status")
                          .getClause();
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

        /*
         * We need two queries since private well data's report to organization
         * id is in the private_sample information but the other domains have
         * the info in sample_organization table.
         */
        sampleIdList = new ArrayList<IdAccessionVO>();

        sampleIdList.addAll(getPrivateSamples(fields, orgIds));
        sampleIdList.addAll(getNonPrivateSamples(fields, orgIds));
        if (sampleIdList.size() == 0)
            return returnList;

        /*
         * We need to send unique sample ids from tempList as parameter for
         * getting sample/analysis information.
         */
        idMap = new HashMap<Integer, Integer>();
        for (IdAccessionVO vo : sampleIdList)
            idMap.put(vo.getId(), vo.getId());

        /*
         * Query the three domains to get the sample/analysis information for
         * the list of sample ids
         */
        returnList = sample.fetchForSampleStatusReport(new ArrayList<Integer>(idMap.values()));

        prevSampleId = -1;
        for (SampleStatusWebReportVO vo : returnList) {
            if ( !prevSampleId.equals(vo.getSampleId())) {
                try {
                    sampleQAList = sampleQa.fetchExternalBySampleId(vo.getSampleId());
                    for (SampleQaEventViewDO sq : sampleQAList) {
                        if (Constants.dictionary().QAEVENT_WARNING.equals(sq.getTypeId())) {
                            vo.setSampleQA(QAEventType.WARNING);
                        } else if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sq.getTypeId())) {
                            vo.setSampleQA(QAEventType.OVERRIDE);
                            break;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
                }
            }
            analysisId = vo.getAnalysisId();
            try {
                analysisQAList = analysisQa.fetchExternalByAnalysisId(analysisId);
                for (AnalysisQaEventViewDO aq : analysisQAList) {
                    if (Constants.dictionary().QAEVENT_WARNING.equals(aq.getTypeId())) {
                        vo.setAnalysisQA(QAEventType.WARNING);
                    } else if (Constants.dictionary().QAEVENT_OVERRIDE.equals(aq.getTypeId())) {
                        vo.setAnalysisQA(QAEventType.OVERRIDE);
                        break;
                    }
                }
            } catch (NotFoundException e) {
                // ignore
            }
            prevSampleId = vo.getSampleId();
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
        clause = userCache.getPermission()
                          .getModule("w_status")
                          .getClause();
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
    public ArrayList<SampleQaEventViewDO> getSampleQaEventsBySampleId(Integer id) throws Exception {
        ArrayList<SampleQaEventViewDO> list, extList;
        
        extList = new ArrayList<SampleQaEventViewDO>();
        list = sampleQa.fetchBySampleId(id);
        for (SampleQaEventViewDO sqeVDO : list) {
            if (!Constants.dictionary().QAEVENT_INTERNAL.equals(sqeVDO.getTypeId()))
                extList.add(sqeVDO);
        }
        
        return extList;
    }

    @RolesAllowed("w_status-select")
    public ArrayList<AnalysisQaEventViewDO> getAnalysisQaEventsByAnalysisId(Integer id) throws Exception {
        ArrayList<AnalysisQaEventViewDO> list, extList;
        
        extList = new ArrayList<AnalysisQaEventViewDO>();
        list = analysisQa.fetchByAnalysisId(id);
        for (AnalysisQaEventViewDO aqeVDO : list) {
            if (!Constants.dictionary().QAEVENT_INTERNAL.equals(aqeVDO.getTypeId()))
                extList.add(aqeVDO);
        }
        
        return extList;
    }

    private ArrayList<IdAccessionVO> getPrivateSamples(ArrayList<QueryData> fields,
                                                       String clause) throws Exception {
        QueryBuilderV2 builder;
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleWebMeta.getId() + "," +
                          SampleWebMeta.getAccessionNumber() + ") ");
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                         Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.constructWhere(fields);
        builder.addWhere(SampleWebMeta.getWellOrganizationId() + clause);
        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    private ArrayList<IdAccessionVO> getNonPrivateSamples(ArrayList<QueryData> fields,
                                                          String clause) throws Exception {
        QueryBuilderV2 builder;
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleWebMeta.getId() + ", " +
                          SampleWebMeta.getAccessionNumber() + ") ");
        builder.addWhere(SampleWebMeta.getStatusId() + " != " +
                         Constants.dictionary().SAMPLE_NOT_VERIFIED);
        builder.constructWhere(fields);

        builder.addWhere(SampleWebMeta.getSampleOrgOrganizationId() + clause);
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" +
                         Constants.dictionary().ORG_REPORT_TO);
        builder.setOrderBy(SampleWebMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
}
