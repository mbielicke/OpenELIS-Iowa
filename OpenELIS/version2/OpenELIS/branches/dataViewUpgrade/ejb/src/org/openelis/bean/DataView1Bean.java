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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataViewAuxDataFetchVO1;
import org.openelis.domain.DataViewResultFetchVO1;
import org.openelis.domain.DataViewVO1;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class DataView1Bean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

    @EJB
    private UserCacheBean              userCache;

    @EJB
    private ProjectBean                project;

    @EJB
    private SampleQAEventBean          sampleQAEvent;

    @EJB
    private AnalysisQAEventBean        analysisQAEvent;

    private static final SampleWebMeta meta = new SampleWebMeta();

    private static final Logger        log  = Logger.getLogger("openelis");

    @RolesAllowed("w_dataview-select")
    public ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception {
        String clause;

        clause = userCache.getPermission().getModule("w_dataview").getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not. We only return the
         * list of projects
         */
        if (clause != null)
            return project.fetchForOrganizations(clause);

        return new ArrayList<IdNameVO>();
    }

    @TransactionTimeout(180)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataViewVO1 fetchAnalyteAndAuxField(DataViewVO1 data) throws Exception {
        ArrayList<QueryData> fields;

        if (data == null)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return fetchAnalyteAndAuxField(data, null);
    }

    @TransactionTimeout(180)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataViewVO1 fetchAnalyteAndAuxFieldForPortal(DataViewVO1 data) throws Exception {
        ArrayList<QueryData> fields;

        if (data == null)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return fetchAnalyteAndAuxField(data, "w_dataview");
    }

    @RolesAllowed("w_dataview-select")
    @TransactionTimeout(600)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReportForPortal(DataViewVO1 data) throws Exception {
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return runReport(data, "w_dataview", true);
    }

    @TransactionTimeout(600)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReport(DataViewVO1 data) throws Exception {
        return runReport(data, null, false);
    }

    private DataViewVO1 fetchAnalyteAndAuxField(DataViewVO1 data, String moduleName) throws Exception {
        boolean excludeOverride, excludeResults, excludeAuxData;
        QueryBuilderV2 builder;
        QueryData reportTo;
        ArrayList<QueryData> fields;
        List<DataViewResultFetchVO1> results;
        List<DataViewAuxDataFetchVO1> auxiliary;
        ArrayList<SampleQaEventViewDO> sqas;
        ArrayList<AnalysisQaEventViewDO> aqas;
        HashSet<Integer> sampleIds, analysisIds, analyteIds, dictIds;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        fields = data.getQueryFields();
        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        /*
         * find out if the user if there is query field for report to, because
         * its key will need to be changed
         */
        reportTo = null;
        for (QueryData f : fields) {
            if ("reportTo".equals(f.getKey())) {
                reportTo = f;
                break;
            }
        }

        results = null;
        auxiliary = null;
        if ( !excludeResults) {
            /*
             * fetch results
             */
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetchVO1(" +
                              SampleWebMeta.getId() + "," + SampleWebMeta.getAnalysisId() + "," +
                              SampleWebMeta.getResultAnalyteId() + "," +
                              SampleWebMeta.getResultValue() + "," +
                              SampleWebMeta.getResultTypeId() + ") ");
            buildWhere(builder, fields, moduleName, reportTo, true);
            results = (List<DataViewResultFetchVO1>)fetchAnalytesAndValues(SampleWebMeta.getId(),
                                                                           builder,
                                                                           fields);
        }

        builder.clearWhereClause();

        if ( !excludeAuxData) {
            /*
             * fetch aux data
             */
            builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetchVO1(" +
                              SampleWebMeta.getId() + ", " +
                              SampleWebMeta.getAuxDataAuxFieldAnalyteId() + ", " +
                              SampleWebMeta.getAuxDataTypeId() + ", " +
                              SampleWebMeta.getAuxDataValue() + ") ");
            buildWhere(builder, fields, moduleName, reportTo, false);
            auxiliary = (List<DataViewAuxDataFetchVO1>)fetchAnalytesAndValues(SampleWebMeta.getId(),
                                                                              builder,
                                                                              fields);
        }

        if ( (results != null && results.isEmpty()) || (auxiliary != null && auxiliary.isEmpty()))
            throw new NotFoundException();

        sampleIds = null;
        analysisIds = null;
        if (excludeOverride) {
            /*
             * the user wants to exclude results and aux data linked to samples
             * or analyses with result override qa events; go through the
             * fetched results and aux data to make lists of their sample and
             * analysis ids so that any qa events linked to them can be fetched
             */
            sampleIds = new HashSet<Integer>();
            analysisIds = new HashSet<Integer>();
            if (results != null) {
                for (DataViewResultFetchVO1 res : results) {
                    sampleIds.add(res.getSampleId());
                    analysisIds.add(res.getAnalysisId());
                }
            }

            if (auxiliary != null) {
                for (DataViewAuxDataFetchVO1 aux : auxiliary)
                    sampleIds.add(aux.getSampleId());
            }

            /*
             * fetch the sample and analysis qa events; keep the ids of only
             * those samples and analyses that don't have any result override qa
             * events
             */
            sqas = sampleQAEvent.fetchBySampleIds(new ArrayList<Integer>(sampleIds));
            for (SampleQaEventViewDO sqa : sqas) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId()) &&
                    sampleIds.contains(sqa.getSampleId()))
                    sampleIds.remove(sqa.getSampleId());
            }

            aqas = analysisQAEvent.fetchByAnalysisIds(new ArrayList<Integer>(analysisIds));
            for (AnalysisQaEventViewDO aqa : aqas) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId()) &&
                    analysisIds.contains(aqa.getAnalysisId()))
                    analysisIds.remove(aqa.getAnalysisId());
            }
        }

        /*
         * 
         */
        if (results != null) {
            for (DataViewResultFetchVO1 res : results) {
                sampleIds.add(res.getSampleId());
                analysisIds.add(res.getAnalysisId());
            }
        }

        if (auxiliary != null) {
            for (DataViewAuxDataFetchVO1 aux : auxiliary)
                sampleIds.add(aux.getSampleId());
        }

        return null;
    }

    private ReportStatus runReport(DataViewVO1 data, String moduleName,
                                   boolean showReportableColumnsOnly) throws Exception {
        return null;
    }

    private String getClause(String moduleName) throws Exception {
        /*
         * retrieving the organization Ids to which the user belongs from the
         * security clause in the userPermission
         */
        return userCache.getPermission().getModule(moduleName).getClause();
    }

    /**
     * Creates a "where" clause from the passed arguments and sets it in the
     * passed query builder; if "moduleName" is specified, the clause from
     * security for that module for the logged in user is added to the "where"
     * clause; if the boolean flag is true it means that query is for results,
     * otherwise it's for aux data and the appropriate where
     */
    private void buildWhere(QueryBuilderV2 builder, ArrayList<QueryData> fields, String moduleName,
                            QueryData reportTo, boolean forResult) throws Exception {
        builder.constructWhere(fields);
        /*
         * if moduleName is not null, then this query is being executed for the
         * web and we need to report only released analyses
         */
        if (moduleName != null) {
            builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                             Constants.dictionary().ANALYSIS_RELEASED);
            builder.addWhere("(" + getClause(moduleName) + ")");
            builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");
        }

        if (forResult) {
            builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
            builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
        } else {
            builder.addWhere(SampleWebMeta.getAuxDataIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getAuxDataValue() + "!=" + "null");
        }

        /*
         * this is done to establish the link between aliases to be able to
         * query for analyses and/or results
         */
        if (moduleName != null || forResult)
            builder.addWhere(SampleWebMeta.getItemId() + "=" +
                             SampleWebMeta.getAnalysisSampleItemId());

        if (reportTo != null) {
            reportTo.setKey(SampleWebMeta.getSampleOrgOrganizationName());
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + " = " +
                             Constants.dictionary().ORG_REPORT_TO);
        }
    }

    private List fetchAnalytesAndValues(String key, QueryBuilderV2 builder,
                                        ArrayList<QueryData> fields) throws Exception {
        Query query;

        builder.setOrderBy(key);
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }
}