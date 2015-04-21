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
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAuxDataFetch1VO;
import org.openelis.domain.DataViewResultFetch1VO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.TestAnalyteDataViewVO;
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

    @EJB
    private AnalyteBean                analyte;

    @EJB
    private DictionaryBean             dictionary;

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
    public DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data) throws Exception {
        ArrayList<QueryData> fields;

        if (data == null)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return fetchTestAnalyteAndAuxField(data, null);
    }

    @TransactionTimeout(180)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataView1VO fetchAnalyteAndAuxFieldForPortal(DataView1VO data) throws Exception {
        ArrayList<QueryData> fields;

        if (data == null)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return fetchTestAnalyteAndAuxField(data, "w_dataview");
    }

    @RolesAllowed("w_dataview-select")
    @TransactionTimeout(600)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReportForPortal(DataView1VO data) throws Exception {
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException(Messages.get().gen_emptyQueryException());

        return runReport(data, "w_dataview", true);
    }

    @TransactionTimeout(600)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReport(DataView1VO data) throws Exception {
        return runReport(data, null, false);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus saveQuery(DataView1VO data) {
        // TODO Auto-generated method stub
        return null;
    }

    private DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data, String moduleName) throws Exception {
        boolean excludeOverride, excludeResults, excludeAuxData;
        QueryData reportTo;
        QueryBuilderV2 builder;
        ArrayList<QueryData> fields;
        List<DataViewResultFetch1VO> results;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<SampleQaEventViewDO> sqas;
        ArrayList<AnalysisQaEventViewDO> aqas;
        ArrayList<AnalyteViewDO> analytes;
        ArrayList<DictionaryViewDO> dictionaries;
        HashSet<Integer> sampleIds, analysisIds, analyteIds, dictIds;
        HashMap<Integer, AnalyteViewDO> analyteMap;
        HashMap<Integer, DictionaryViewDO> dictMap;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        fields = data.getQueryFields();
        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        fields = data.getQueryFields();

        /*
         * find out if the user if there is query field for report to, because
         * its key will need to be changed
         */
        reportTo = null;
        for (QueryData f : fields) {
            if ("reportTo".equals(f.getKey())) {
                reportTo = f;
                reportTo.setKey(SampleWebMeta.getSampleOrgOrganizationName());
                break;
            }
        }

        results = null;
        auxiliary = null;
        if ( !excludeResults) {
            /*
             * fetch results
             */
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                              SampleWebMeta.getId() + "," + SampleWebMeta.getAnalysisId() + "," +
                              SampleWebMeta.getResultAnalyteId() + "," +
                              SampleWebMeta.getResultTypeId() + "," +
                              SampleWebMeta.getResultValue() + ") ");
            buildWhere(builder, data, moduleName, true, reportTo);
            results = (List<DataViewResultFetch1VO>)fetchAnalytesAndValues(SampleWebMeta.getId(),
                                                                           builder,
                                                                           fields);
        }

        builder.clearWhereClause();

        if ( !excludeAuxData) {
            /*
             * fetch aux data
             */
            builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetch1VO(" +
                              SampleWebMeta.getId() + ", " +
                              SampleWebMeta.getAuxDataFieldAnalyteId() + ", " +
                              SampleWebMeta.getAuxDataTypeId() + ", " +
                              SampleWebMeta.getAuxDataValue() + ") ");
            buildWhere(builder, data, moduleName, false, reportTo);
            auxiliary = (List<DataViewAuxDataFetch1VO>)fetchAnalytesAndValues(SampleWebMeta.getId(),
                                                                              builder,
                                                                              fields);
        }

        if ( (results == null && auxiliary == null) ||
            ( (results != null && results.isEmpty()) && (auxiliary != null && auxiliary.isEmpty())))
            throw new NotFoundException();

        /*
         * go through the fetched results and aux data to make lists of their
         * sample, analysis ids, analyte and dictionary ids so that any qa
         * events etc. linked to them can be fetched
         */
        sampleIds = new HashSet<Integer>();
        analysisIds = new HashSet<Integer>();
        analyteIds = new HashSet<Integer>();
        dictIds = new HashSet<Integer>();
        if (results != null) {
            for (DataViewResultFetch1VO res : results) {
                sampleIds.add(res.getSampleId());
                analysisIds.add(res.getAnalysisId());
                analyteIds.add(res.getAnalyteId());
                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId()))
                    dictIds.add(Integer.valueOf(res.getValue()));
            }
        }

        if (auxiliary != null) {
            for (DataViewAuxDataFetch1VO aux : auxiliary) {
                sampleIds.add(aux.getSampleId());
                analyteIds.add(aux.getAnalyteId());
                if (Constants.dictionary().AUX_DICTIONARY.equals(aux.getTypeId()))
                    dictIds.add(Integer.valueOf(aux.getValue()));

            }
        }

        if (excludeOverride) {
            /*
             * the user wants to exclude results and aux data linked to samples
             * or analyses with result override qa events; fetch the sample and
             * analysis qa events; keep the ids of only those samples and
             * analyses that don't have any result override qa events
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

        if ( (sampleIds.size() == 0 || ( !excludeResults && excludeAuxData && analysisIds.size() == 0)))
            throw new NotFoundException();

        analytes = analyte.fetchByIds(analyteIds);
        analyteMap = new HashMap<Integer, AnalyteViewDO>();
        for (AnalyteViewDO ana : analytes)
            analyteMap.put(ana.getId(), ana);

        dictMap = null;
        if (dictIds.size() > 0) {
            dictionaries = dictionary.fetchByIds(dictIds);
            dictMap = new HashMap<Integer, DictionaryViewDO>();
            for (DictionaryViewDO dict : dictionaries)
                dictMap.put(dict.getId(), dict);
        }

        if ( !excludeResults)
            data.setTestAnalytes(getTestAnalytes(results,
                                                 sampleIds,
                                                 analysisIds,
                                                 analyteMap,
                                                 dictMap));

        if ( !excludeAuxData)
            data.setAuxFields(getAuxFields(auxiliary, sampleIds, analyteMap, dictMap));

        return data;
    }

    private ReportStatus runReport(DataView1VO data, String moduleName,
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
    private void buildWhere(QueryBuilderV2 builder, DataView1VO data, String moduleName,
                            boolean forResult, QueryData reportTo) throws Exception {
        builder.constructWhere(data.getQueryFields());
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
            if ("Y".equals(data.getIncludeOnlyReportableResults()))
                builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
            builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
        } else {
            if ("Y".equals(data.getIncludeOnlyReportableAuxData()))
                builder.addWhere(SampleWebMeta.getAuxDataIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getAuxDataValue() + "!=" + "null");
            /*
             * this is done so that the alias for "auxField" gets added to the
             * query; otherwise the query will not execute
             */
            builder.addWhere(SampleWebMeta.getAuxDataAuxFieldId() + "=" +
                             SampleWebMeta.getAuxDataFieldId());
        }

        /*
         * this is done so that the alias for "sampleItem" gets added to the
         * query; otherwise the query will not execute
         */
        if (moduleName != null || forResult)
            builder.addWhere(SampleWebMeta.getItemId() + "=" +
                             SampleWebMeta.getAnalysisSampleItemId());

        if (reportTo != null)
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + " = " +
                             Constants.dictionary().ORG_REPORT_TO);
    }

    private List fetchAnalytesAndValues(String key, QueryBuilderV2 builder,
                                        ArrayList<QueryData> fields) throws Exception {
        Query query;

        builder.setOrderBy(key);
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    private ArrayList<TestAnalyteDataViewVO> getTestAnalytes(List<DataViewResultFetch1VO> results,
                                                             HashSet<Integer> sampleIds,
                                                             HashSet<Integer> analysisIds,
                                                             HashMap<Integer, AnalyteViewDO> analyteMap,
                                                             HashMap<Integer, DictionaryViewDO> dictMap) {
        Integer analyteId, dictId;
        String value;
        TestAnalyteDataViewVO testAnalyte;
        ResultDataViewVO result;
        HashSet<String> values;
        ArrayList<TestAnalyteDataViewVO> testAnalytes;
        ArrayList<ResultDataViewVO> dispResults;
        HashMap<Integer, TestAnalyteDataViewVO> testAnalyteMap;
        HashMap<Integer, HashSet<String>> analyteValuesMap;

        /*
         * a TestAnalyteDataViewVO is created for an analyte only once, no
         * matter how many times it appears in the list of
         * DataViewResultFetch1VOs
         */
        testAnalytes = new ArrayList<TestAnalyteDataViewVO>();
        testAnalyteMap = new HashMap<Integer, TestAnalyteDataViewVO>();
        analyteValuesMap = new HashMap<Integer, HashSet<String>>();
        values = null;
        for (DataViewResultFetch1VO res : results) {
            if ( !sampleIds.contains(res.getSampleId()) ||
                !analysisIds.contains(res.getAnalysisId()))
                continue;
            analyteId = res.getAnalyteId();
            testAnalyte = testAnalyteMap.get(analyteId);
            if (testAnalyte == null) {
                testAnalyte = new TestAnalyteDataViewVO();
                testAnalyte.setAnalyteId(analyteId);
                testAnalyte.setAnalyteName(analyteMap.get(analyteId).getName());
                testAnalyte.setIsIncluded("N");
                dispResults = new ArrayList<ResultDataViewVO>();
                testAnalyte.setResults(dispResults);
                testAnalytes.add(testAnalyte);
                testAnalyteMap.put(analyteId, testAnalyte);
                values = new HashSet<String>();
                analyteValuesMap.put(analyteId, values);
            } else {
                dispResults = testAnalyte.getResults();
                values = analyteValuesMap.get(analyteId);
            }

            value = res.getValue();
            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId())) {
                dictId = Integer.parseInt(value);
                value = dictMap.get(dictId).getEntry();
            }

            /*
             * don't allow the same value to be shown more than once for the
             * same analyte
             */
            if (values.contains(value))
                continue;
            values.add(value);

            result = new ResultDataViewVO();
            result.setValue(value);
            result.setIsIncluded("N");
            dispResults.add(result);
        }
        return testAnalytes;
    }

    /**
     * Returns a list of aux fields and the aux data for each of those fields in
     * the passed list; if for any  
     */
    private ArrayList<AuxFieldDataViewVO> getAuxFields(List<DataViewAuxDataFetch1VO> auxiliary,
                                                       HashSet<Integer> sampleIds,
                                                       HashMap<Integer, AnalyteViewDO> analyteMap,
                                                       HashMap<Integer, DictionaryViewDO> dictMap) throws Exception {
        Integer analyteId, dictId;
        String value;
        AuxFieldDataViewVO auxField;
        AuxDataDataViewVO auxData;
        HashSet<String> values;
        ArrayList<AuxFieldDataViewVO> auxFields;
        ArrayList<AuxDataDataViewVO> auxDataList;
        HashMap<Integer, AuxFieldDataViewVO> auxFieldMap;
        HashMap<Integer, HashSet<String>> analyteValuesMap;

        /*
         * an AuxFieldDataViewVO is created for an analyte only once, no matter
         * how many times it appears in the list of DataViewAuxDataFetchVO1s
         */
        auxDataList = null;
        auxFields = new ArrayList<AuxFieldDataViewVO>();
        auxFieldMap = new HashMap<Integer, AuxFieldDataViewVO>();
        analyteValuesMap = new HashMap<Integer, HashSet<String>>();
        values = null;
        for (DataViewAuxDataFetch1VO aux : auxiliary) {
            if ( !sampleIds.contains(aux.getSampleId()))
                continue;
            analyteId = aux.getAnalyteId();
            auxField = auxFieldMap.get(analyteId);
            if (auxField == null) {
                auxField = new AuxFieldDataViewVO();
                auxField.setAnalyteId(aux.getAnalyteId());
                auxField.setAnalyteName(analyteMap.get(analyteId).getName());
                auxField.setIsIncluded("N");
                auxDataList = new ArrayList<AuxDataDataViewVO>();
                auxField.setValues(auxDataList);
                auxFields.add(auxField);
                auxFieldMap.put(analyteId, auxField);
                values = new HashSet<String>();
                analyteValuesMap.put(analyteId, values);
            } else {
                auxDataList = auxField.getValues();
                values = analyteValuesMap.get(analyteId);
            }

            value = aux.getValue();

            if (Constants.dictionary().AUX_DICTIONARY.equals(aux.getTypeId())) {
                dictId = Integer.parseInt(value);
                value = dictMap.get(dictId).getEntry();
            }

            /*
             * don't allow the same value to be shown more than once for the
             * same analyte
             */
            if (values.contains(value))
                continue;
            values.add(value);

            auxData = new AuxDataDataViewVO();
            auxData.setValue(value);
            auxData.setIsIncluded("N");
            auxDataList.add(auxData);
        }
        return auxFields;
    }
}