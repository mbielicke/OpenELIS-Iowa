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
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.SampleStatusReportRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
public class SampleStatusReportBean implements SampleStatusReportRemote {

    @EJB
    private SampleLocal                sample;

    @EJB
    private DictionaryLocal            dictionary;

    @EJB
    private SampleQAEventLocal         sampleQa;

    @EJB
    private AnalysisQAEventLocal       analysisQa;

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

    private static Integer             sampleNotVerifiedId, organizationReportToId, qaWarningId,
                    qaOverrideId;

    private static final SampleWebMeta meta = new SampleWebMeta();

    @PostConstruct
    public void init() {
        try {
            sampleNotVerifiedId = dictionary.fetchBySystemName("sample_not_verified").getId();
            organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
            qaWarningId = dictionary.fetchBySystemName("qaevent_warning").getId();
            qaOverrideId = dictionary.fetchBySystemName("qaevent_override").getId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @RolesAllowed("w_status-select")
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(ArrayList<QueryData> fields) throws Exception {
        int id, sampleId, prevSampleId, analysisId;
        boolean hasOverride, hasWarning;
        String clause;
        ArrayList<SampleStatusWebReportVO> returnList;
        ArrayList<IdAccessionVO> tempList;
        ArrayList<Integer> idList;
        ArrayList<SampleQaEventViewDO> sampleQAList;
        ArrayList<AnalysisQaEventViewDO> analysisQAList;

        /*
         * Retrive the sql clause that limits what the user can access. Don't
         * allow an empty clause
         */
        clause = EJBFactory.getUserCache().getPermission().getModule("w_status").getClause();
        if (clause == null)
            return new ArrayList<SampleStatusWebReportVO>();

        /*
         * We need to queries since private well data's report to organization
         * id is in the private_sample information but the other domains have
         * the info in sample_organization table. Order the list based on
         * accession #s
         */
        tempList = new ArrayList<IdAccessionVO>();
        returnList = new ArrayList<SampleStatusWebReportVO>();
        tempList.addAll(getSampleIds(fields, clause, false));
        tempList.addAll(getSampleIds(fields, clause, true));
        Collections.sort(tempList, new AccessionIdComparator());

        if (tempList == null || tempList.size() == 0)
            return returnList;

        /*
         * From tempList we create an arrayList of sample ids, which is sent as
         * a parameter for getting sample/analysis information.
         */
        idList = new ArrayList<Integer>();
        for (int i = 0; i < tempList.size(); i++ )
            idList.add(tempList.get(i).getId());

        /*
         * Query all the three domains to get the sample/analysis information
         * for the list of sample ids
         */
        returnList.addAll(sample.fetchSampleAnalysisInfoForSampleStatusReportEnvironmental(idList));
        returnList.addAll(sample.fetchSampleAnalysisInfoForSampleStatusReportPrivateWell(idList));
        returnList.addAll(sample.fetchSampleAnalysisInfoForSampleStatusReportSDWIS(idList));

        prevSampleId = -1;
        for (SampleStatusWebReportVO vo : returnList) {
            sampleId = vo.getSampleId();
            hasOverride = hasWarning = false;
            if (sampleId != prevSampleId) {
                try {
                    sampleQAList = sampleQa.fetchExternalBySampleId(sampleId);
                    for (SampleQaEventViewDO sq : sampleQAList) {
                        if (qaOverrideId.equals(sq.getTypeId())) {
                            hasOverride = true;
                            break;
                        }
                        if (qaWarningId.equals(sq.getTypeId())) {
                            hasWarning = true;
                        }
                    }
                    vo.setHasSampleQAEvent(true);
                    if (hasOverride) {
                        vo.setHasSampleOverride(true);
                        vo.setHasSampleWarning(false);
                    }
                    if (hasWarning && (hasOverride == false)) {
                        vo.setHasSampleWarning(true);
                        vo.setHasSampleOverride(false);
                    }
                } catch (NotFoundException e) {
                    vo.setHasSampleQAEvent(false);
                }
            }
            hasOverride = hasWarning = false;
            analysisId = vo.getAnalysisId();
            try {
                analysisQAList = analysisQa.fetchExternalByAnalysisId(analysisId);
                for (AnalysisQaEventViewDO aq : analysisQAList) {
                    if (qaOverrideId.equals(aq.getTypeId())) {
                        hasOverride = true;
                        break;
                    }
                    if (qaWarningId.equals(aq.getTypeId())) {
                        hasWarning = true;
                    }
                }
                vo.setHasAnalysisQAEvent(true);
                if (hasOverride) {
                    vo.setHasAnalysisOverride(true);
                    vo.setHasAnalysisWarning(false);
                }
                if (hasWarning && (hasOverride == false)) {
                    vo.setHasAnalysisWarning(true);
                    vo.setHasAnalysisOverride(false);
                }
            } catch (NotFoundException e) {
                vo.setHasAnalysisQAEvent(false);
            }
            prevSampleId = sampleId;
        }
        /*
         * Sort the list by accession # of samples. We do the sorting in the
         * back end instead of doing it in the front, since the comparator for
         * rows in the table cannot sort correctly if the value in column based
         * on which the comparison is made is null. In this case, since the
         * analysis rows don't show accession number on the screen, the value in
         * the first column of these rows is null and that is the column used
         * for sorting.
         */
        Collections.sort(returnList, new SampleComparator());
        return returnList;
    }

    @RolesAllowed("w_status-select")
    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        String clause;
        ArrayList<IdNameVO> projectList;

        projectList = new ArrayList<IdNameVO>();
        clause = EJBFactory.getUserCache().getPermission().getModule("w_status").getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not.
         */
        if (clause == null)
            return new ArrayList<IdNameVO>();
        /*
         * Adding projects from environment and private domain into projectList.
         * Since Project name is of type char, we can't use a named native query
         */
        projectList.addAll(sample.fetchProjectsForOrganizations(clause));

        Collections.sort(projectList, new ProjectComparator());
        return projectList;
    }

    private ArrayList<IdAccessionVO> getSampleIds(ArrayList<QueryData> fields, String clause,
                                                  boolean fromPrivates) throws Exception {
        QueryBuilderV2 builder;
        Query query;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + ") ");
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleNotVerifiedId);
        builder.constructWhere(fields);
        builder.addWhere("(" + clause + ")");
        if ( !fromPrivates)
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    class AccessionIdComparator implements Comparator<IdAccessionVO> {
        public int compare(IdAccessionVO a1, IdAccessionVO a2) {
            return a1.getAccessionNumber() - a2.getAccessionNumber();
        }
    }

    class SampleComparator implements Comparator<SampleStatusWebReportVO> {
        public int compare(SampleStatusWebReportVO s1, SampleStatusWebReportVO s2) {
            return s1.getAccessionNumber() - s2.getAccessionNumber();
        }
    }

    class ProjectComparator implements Comparator<IdNameVO> {
        public int compare(IdNameVO p1, IdNameVO p2) {
            return p1.getId() - p2.getId();
        }
    }
}
