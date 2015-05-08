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
import java.util.Set;
import java.util.logging.Level;
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
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataView1VO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAuxDataFetch1VO;
import org.openelis.domain.DataViewResultFetch1VO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.TestAnalyteDataView1VO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
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
    private SessionCacheBean           session;

    @EJB
    private UserCacheBean              userCache;

    @EJB
    private ProjectBean                project;

    @EJB
    private SampleQAEventBean          sampleQAEvent;

    @EJB
    private AnalysisQAEventBean        analysisQAEvent;

    @EJB
    private DictionaryBean             dictionary;

    @EJB
    private SampleManager1Bean         sampleManager1;

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

    /**
     * Executes a query created from the query fields in the VO to fetch results
     * and aux data; if moduleName is not null, includes the clause for the
     * module with that name in the query; doesn't fetch results or aux data if
     * the flag in the VO to exclude them is set to "Y"; the returned VO
     * contains two lists, one for analytes linked to results and another one
     * for analytes linked to aux data; each element of these lists also has the
     * list of values for each analyte; if excludeResultOverride is set to "Y",
     * the analytes and values linked to samples and analyses with result
     * override qa events are excluded from the lists
     */
    private DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data, String moduleName) throws Exception {
        boolean excludeOverride, excludeResults, excludeAuxData;
        QueryBuilderV2 builder;
        ArrayList<QueryData> fields;
        List<DataViewResultFetch1VO> results;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<SampleQaEventViewDO> sqas;
        ArrayList<AnalysisQaEventViewDO> aqas;
        ArrayList<DictionaryViewDO> dictionaries;
        HashSet<Integer> sampleIds, analysisIds, dictIds;
        HashMap<Integer, DictionaryViewDO> dictMap;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        fields = data.getQueryFields();
        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        fields = data.getQueryFields();

        results = null;
        auxiliary = null;
        if ( !excludeResults) {
            /*
             * fetch results
             */
            log.log(Level.INFO, "Before fetching results");
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                              SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() +
                              "," + SampleWebMeta.getResultAnalysisid() + "," +
                              SampleWebMeta.getResultAnalyteId() + "," +
                              SampleWebMeta.getResultAnalyteName() + "," +
                              SampleWebMeta.getResultTypeId() + "," +
                              SampleWebMeta.getResultValue() + ") ");
            buildWhere(builder, data, moduleName, true);
            results = (List<DataViewResultFetch1VO>)fetchAnalytesAndValues(builder, fields);
            log.log(Level.INFO, "Fetched " + results.size() + " results");
        }

        builder.clearWhereClause();

        if ( !excludeAuxData) {
            /*
             * fetch aux data
             */
            log.log(Level.INFO, "Before fetching aux data");
            builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetch1VO(" +
                              SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() +
                              "," + SampleWebMeta.getAuxDataFieldAnalyteId() + ", " +
                              SampleWebMeta.getAuxDataFieldAnalyteName() + ", " +
                              SampleWebMeta.getAuxDataTypeId() + ", " +
                              SampleWebMeta.getAuxDataValue() + ") ");
            buildWhere(builder, data, moduleName, false);
            auxiliary = (List<DataViewAuxDataFetch1VO>)fetchAnalytesAndValues(builder, fields);
            log.log(Level.INFO, "Fetched " + auxiliary.size() + " aux data");
        }

        if ( (results == null && auxiliary == null) ||
            ( (results != null && results.isEmpty()) && (auxiliary != null && auxiliary.isEmpty())))
            throw new NotFoundException();

        /*
         * go through the fetched results and aux data to make lists of their
         * sample, analysis ids and dictionary ids so that any qa events etc.
         * linked to them can be fetched
         */
        sampleIds = new HashSet<Integer>();
        analysisIds = new HashSet<Integer>();
        dictIds = new HashSet<Integer>();
        if (results != null) {
            for (DataViewResultFetch1VO res : results) {
                sampleIds.add(res.getSampleId());
                analysisIds.add(res.getAnalysisId());
                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId()))
                    dictIds.add(Integer.valueOf(res.getValue()));
            }
        }

        if (auxiliary != null) {
            for (DataViewAuxDataFetch1VO aux : auxiliary) {
                sampleIds.add(aux.getSampleId());
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
            sqas = sampleQAEvent.fetchBySampleIds(DataBaseUtil.toArrayList(sampleIds));
            for (SampleQaEventViewDO sqa : sqas) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId()) &&
                    sampleIds.contains(sqa.getSampleId()))
                    sampleIds.remove(sqa.getSampleId());
            }

            aqas = analysisQAEvent.fetchByAnalysisIds(DataBaseUtil.toArrayList(analysisIds));
            for (AnalysisQaEventViewDO aqa : aqas) {
                if (Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId()) &&
                    analysisIds.contains(aqa.getAnalysisId()))
                    analysisIds.remove(aqa.getAnalysisId());
            }
        }

        /*
         * if the list of sample ids is empty it means that the user wants to
         * exclude samples and analyses with result override qa events and all
         * fetched samples have such qa events, so no results or aux data will
         * be returned; similarly, nothing will be returned if only results are
         * to be included and all analyses have such qa events
         */
        if ( (sampleIds.size() == 0 || ( !excludeResults && excludeAuxData && analysisIds.size() == 0)))
            throw new NotFoundException();

        /*
         * fetch all dictionary entries linked to values of results and aux data
         */
        dictMap = null;
        if (dictIds.size() > 0) {
            dictionaries = dictionary.fetchByIds(DataBaseUtil.toArrayList(dictIds));
            dictMap = new HashMap<Integer, DictionaryViewDO>();
            for (DictionaryViewDO dict : dictionaries)
                dictMap.put(dict.getId(), dict);
        }

        /*
         * create the lists of analytes and values for the fetched results and
         * aux data and set them in the returned VO
         */
        if ( !excludeResults)
            data.setTestAnalytes(getTestAnalytes(results, sampleIds, analysisIds, dictMap));

        if ( !excludeAuxData)
            data.setAuxFields(getAuxFields(auxiliary, sampleIds, dictMap));

        return data;
    }

    private ReportStatus runReport(DataView1VO data, String moduleName,
                                   boolean showReportableColumnsOnly) throws Exception {
        boolean excludeResults, excludeAuxData;
        ReportStatus status;
        QueryBuilderV2 builder;
        List<DataViewResultFetch1VO> results;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<Integer> unselAnalyteIds;
        ArrayList<TestAnalyteDataView1VO> testAnalytes;
        ArrayList<AuxFieldDataView1VO> auxFields;
        ArrayList<SampleManager1> managers;
        HashSet<String> resultValues, auxValues;
        HashSet<Integer> sampleIds;
        HashMap<Integer, HashSet<String>> testAnaResultMap, auxFieldValueMap;

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataView", status);

        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        results = null;
        auxiliary = null;
        testAnaResultMap = null;
        auxFieldValueMap = null;

        if (excludeResults && excludeAuxData) {

        } else {
            if ( !excludeResults) {
                unselAnalyteIds = new ArrayList<Integer>();
                testAnalytes = data.getTestAnalytes();
                if (testAnalytes != null) {
                    /*
                     * the analytes and results selected by the user are stored
                     * in this map so that they can be used later on to select
                     * or reject adding a row for a result based on whether or
                     * not it belongs in the map
                     */
                    testAnaResultMap = new HashMap<Integer, HashSet<String>>();
                    for (TestAnalyteDataView1VO ana : testAnalytes) {
                        /*
                         * create the list of analytes not selected by the user
                         * so that a decision can be made about including them
                         * or the selected analytes or none of the two in the
                         * query to generate the report
                         */
                        if ("N".equals(ana.getIsIncluded())) {
                            unselAnalyteIds.add(ana.getAnalyteId());
                            continue;
                        }
                        resultValues = new HashSet<String>();
                        for (ResultDataViewVO res : ana.getResults()) {
                            if ("Y".equals(res.getIsIncluded()))
                                resultValues.add(res.getValue());
                        }
                        testAnaResultMap.put(ana.getAnalyteId(), resultValues);
                    }
                }

                /*
                 * fetch fields related to results based on the analytes and
                 * values selected by the user from the lists associated with
                 * test analytes
                 */
                if (testAnaResultMap != null && testAnaResultMap.size() > 0) {
                    log.log(Level.INFO, "Before fetching results");
                    results = fetchResults(moduleName,
                                           builder,
                                           testAnaResultMap,
                                           unselAnalyteIds,
                                           data);
                    log.log(Level.INFO, "Fetched " + results.size() + " results");
                }
            }

            if ( !excludeAuxData) {
                unselAnalyteIds = new ArrayList<Integer>();
                auxFields = data.getAuxFields();
                if (auxFields != null) {
                    /*
                     * the analytes and values selected by the user are stored
                     * in this hashmap so that they can be used later on to
                     * select or reject adding a row for an aux data based on
                     * whether or not it belongs in the hashmap
                     */
                    auxFieldValueMap = new HashMap<Integer, HashSet<String>>();
                    for (AuxFieldDataView1VO af : auxFields) {
                        /*
                         * create the list of analytes not selected by the user
                         * so that a decision can be made about including them
                         * or the selected analytes or none of the two in the
                         * query to generate the report
                         */
                        if ("N".equals(af.getIsIncluded())) {
                            unselAnalyteIds.add(af.getAnalyteId());
                            continue;
                        }
                        auxValues = new HashSet<String>();
                        for (AuxDataDataViewVO val : af.getValues()) {
                            if ("Y".equals(val.getIsIncluded()))
                                auxValues.add(val.getValue());
                        }
                        auxFieldValueMap.put(af.getAnalyteId(), auxValues);
                    }
                }
                /*
                 * fetch fields related to aux data based on the analytes and
                 * values selected by the user from the lists associated with
                 * aux fields
                 */
                builder.clearWhereClause();
                if (auxFieldValueMap != null && auxFieldValueMap.size() > 0) {
                    log.log(Level.INFO, "Before fetching aux data");
                    auxiliary = fetchAuxData(moduleName,
                                             builder,
                                             auxFieldValueMap,
                                             unselAnalyteIds,
                                             data);
                    log.log(Level.INFO, "Fetched " + auxiliary.size() + " aux data");
                }
            }
        }

        if ( (results == null || results.size() == 0) &&
            (auxiliary == null || auxiliary.size() == 0))
            throw new NotFoundException();

        /*
         * make a list of sample ids from the fetched results and aux data and
         * fetch the managers
         */
        sampleIds = new HashSet<Integer>();
        if (results != null) {
            for (DataViewResultFetch1VO res : results)
                sampleIds.add(res.getSampleId());
        }

        if (auxiliary != null) {
            for (DataViewAuxDataFetch1VO aux : auxiliary)
                sampleIds.add(aux.getSampleId());
        }

        results = null;
        auxiliary = null;
        log.log(Level.INFO, "Before fetching " + sampleIds.size() +" managers");

        managers = sampleManager1.fetchByIds(DataBaseUtil.toArrayList(sampleIds),
                                             SampleManager1.Load.ORGANIZATION,
                                             SampleManager1.Load.PROJECT,
                                             SampleManager1.Load.QA,
                                             SampleManager1.Load.AUXDATA,
                                             //SampleManager1.Load.ANALYSISUSER,
                                             //SampleManager1.Load.RESULT,
                                             SampleManager1.Load.EORDER,
                                             SampleManager1.Load.PROVIDER);       

        log.log(Level.INFO, "Fetched " + managers.size() + " managers");

        return status;
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
                            boolean forResult) throws Exception {
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
            if ("N".equals(data.getIncludeNotReportableResults()))
                builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
            builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
        } else {
            if ("N".equals(data.getIncludeNotReportableAuxData()))
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
    }

    private List fetchAnalytesAndValues(QueryBuilderV2 builder, ArrayList<QueryData> fields) throws Exception {
        Query query;

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    /**
     * Returns the list of analytes linked to the results in the passed list;
     * each element in the returned list also has the list of values for the
     * analyte; a result's value is included in the list only if its sample and
     * analysis ids are found in the passed lists of sample and analysis ids;
     * the passed map is used to find dictionary entries for values of that type
     */
    private ArrayList<TestAnalyteDataView1VO> getTestAnalytes(List<DataViewResultFetch1VO> results,
                                                              HashSet<Integer> sampleIds,
                                                              HashSet<Integer> analysisIds,
                                                              HashMap<Integer, DictionaryViewDO> dictMap) {
        Integer analyteId, dictId;
        String value;
        TestAnalyteDataView1VO testAnalyte;
        ResultDataViewVO result;
        HashSet<String> values;
        ArrayList<TestAnalyteDataView1VO> testAnalytes;
        ArrayList<ResultDataViewVO> dispResults;
        HashMap<Integer, TestAnalyteDataView1VO> testAnalyteMap;
        HashMap<Integer, HashSet<String>> analyteValuesMap;

        /*
         * create the list of analytes from the passed list of results; create
         * the list of values for each analyte and set it in the VO for that
         * analyte
         */
        testAnalytes = new ArrayList<TestAnalyteDataView1VO>();
        testAnalyteMap = new HashMap<Integer, TestAnalyteDataView1VO>();
        analyteValuesMap = new HashMap<Integer, HashSet<String>>();
        values = null;
        for (DataViewResultFetch1VO res : results) {
            if ( !sampleIds.contains(res.getSampleId()) ||
                !analysisIds.contains(res.getAnalysisId()))
                continue;
            analyteId = res.getAnalyteId();
            testAnalyte = testAnalyteMap.get(analyteId);
            /*
             * a VO is created for an analyte only once, no matter how many
             * times it appears in the passed list of results
             */
            if (testAnalyte == null) {
                testAnalyte = new TestAnalyteDataView1VO();
                testAnalyte.setAnalyteId(analyteId);
                testAnalyte.setAnalyteName(res.getAnalyteName());
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
             * don't allow the same value to be shown more than once for an
             * analyte
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
     * Returns the list of analytes linked to the aux data in the passed list;
     * each element in the returned list also has the list of values for the
     * analyte; an aux data's value is included in the list only if its sample
     * id is found in the passed lists of sample and analysis ids; the passed
     * map is used to find dictionary entries for values of that type
     */
    private ArrayList<AuxFieldDataView1VO> getAuxFields(List<DataViewAuxDataFetch1VO> auxiliary,
                                                        HashSet<Integer> sampleIds,
                                                        HashMap<Integer, DictionaryViewDO> dictMap) throws Exception {
        Integer analyteId, dictId;
        String value;
        AuxFieldDataView1VO auxField;
        AuxDataDataViewVO auxData;
        HashSet<String> values;
        ArrayList<AuxFieldDataView1VO> auxFields;
        ArrayList<AuxDataDataViewVO> dispAuxiliary;
        HashMap<Integer, AuxFieldDataView1VO> auxFieldMap;
        HashMap<Integer, HashSet<String>> analyteValuesMap;

        /*
         * create the list of analytes from the passed list of aux data; create
         * the list of values for each analyte and set it in the VO for that
         * analyte
         */
        dispAuxiliary = null;
        auxFields = new ArrayList<AuxFieldDataView1VO>();
        auxFieldMap = new HashMap<Integer, AuxFieldDataView1VO>();
        analyteValuesMap = new HashMap<Integer, HashSet<String>>();
        values = null;
        for (DataViewAuxDataFetch1VO aux : auxiliary) {
            if ( !sampleIds.contains(aux.getSampleId()))
                continue;
            analyteId = aux.getAuxFieldAnalyteId();
            auxField = auxFieldMap.get(analyteId);
            /*
             * a VO is created for an analyte only once, no matter how many
             * times it appears in the passed list of aux data
             */
            if (auxField == null) {
                auxField = new AuxFieldDataView1VO();
                auxField.setAnalyteId(aux.getAuxFieldAnalyteId());
                auxField.setAnalyteName(aux.getAuxFieldAnalyteName());
                auxField.setIsIncluded("N");
                dispAuxiliary = new ArrayList<AuxDataDataViewVO>();
                auxField.setValues(dispAuxiliary);
                auxFields.add(auxField);
                auxFieldMap.put(analyteId, auxField);
                values = new HashSet<String>();
                analyteValuesMap.put(analyteId, values);
            } else {
                dispAuxiliary = auxField.getValues();
                values = analyteValuesMap.get(analyteId);
            }

            value = aux.getValue();

            if (Constants.dictionary().AUX_DICTIONARY.equals(aux.getTypeId())) {
                dictId = Integer.parseInt(value);
                value = dictMap.get(dictId).getEntry();
            }

            /*
             * don't allow the same value to be shown more than once for an
             * analyte
             */
            if (values.contains(value))
                continue;
            values.add(value);

            auxData = new AuxDataDataViewVO();
            auxData.setValue(value);
            auxData.setIsIncluded("N");
            dispAuxiliary.add(auxData);
        }
        return auxFields;
    }

    private List<DataViewResultFetch1VO> fetchResults(String moduleName,
                                                      QueryBuilderV2 builder,
                                                      HashMap<Integer, HashSet<String>> testAnaResultMap,
                                                      ArrayList<Integer> unselAnalyteIds,
                                                      DataView1VO data) throws Exception {
        ArrayList<String> orderBy;
        Query query;
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();

        builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + "," +
                          SampleWebMeta.getResultAnalysisid() + "," +
                          SampleWebMeta.getResultAnalyteId() + "," +
                          SampleWebMeta.getResultAnalyteName() + "," +
                          SampleWebMeta.getResultTypeId() + "," + SampleWebMeta.getResultValue() +
                          ") ");

        builder.constructWhere(fields);
        /*
         * If moduleName is present, then it means that this report is being run
         * for the samples belonging to the list of organizations specified in
         * this user's system_user_module for a specific domain.
         */
        if (moduleName != null) {
            builder.addWhere("(" + getClause(moduleName) + ")");
            builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                             Constants.dictionary().SAMPLE_ERROR);
            builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                             Constants.dictionary().ANALYSIS_RELEASED);
            builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");
        }

        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());

        orderBy = new ArrayList<String>();
        orderBy.add(SampleWebMeta.getAccessionNumber());
        if ("N".equals(data.getIncludeNotReportableResults()))
            builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
        builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
        builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
        /*
         * add the clause for limiting the results by analytes only if the user
         * selected some specific analytes and not all of them. This eliminates
         * the unnecessary time spent on excluding those results from the
         * records returned by the query
         */
        if (unselAnalyteIds != null && unselAnalyteIds.size() > 0)
            builder.addWhere(SampleWebMeta.getResultAnalyteId() +
                             getAnalyteClause(testAnaResultMap.keySet(), unselAnalyteIds) + ")");

        orderBy.add(SampleWebMeta.getResultAnalysisid());
        orderBy.add(SampleWebMeta.getResultAnalyteName());

        builder.setOrderBy(DataBaseUtil.concatWithSeparator(orderBy, ", "));
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    private List<DataViewAuxDataFetch1VO> fetchAuxData(String moduleName,
                                                       QueryBuilderV2 builder,
                                                       HashMap<Integer, HashSet<String>> auxFieldValueMap,
                                                       ArrayList<Integer> unselAnalyteIds,
                                                       DataView1VO data) throws Exception {
        Query query;
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();

        builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetch1VO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + "," +
                          SampleWebMeta.getAuxDataFieldAnalyteId() + ", " +
                          SampleWebMeta.getAuxDataFieldAnalyteName() + ", " +
                          SampleWebMeta.getAuxDataTypeId() + ", " +
                          SampleWebMeta.getAuxDataValue() + ") ");
        builder.constructWhere(fields);
        /*
         * If moduleName is present, then it means that this report is being run
         * for the samples belonging to the list of organizations specified in
         * this user's system_user_module for a specific domain.
         */
        if (moduleName != null) {
            builder.addWhere("(" + getClause(moduleName) + ")");
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" +
                             Constants.dictionary().ORG_REPORT_TO);
            builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                             Constants.dictionary().SAMPLE_ERROR);
        }

        if ("N".equals(data.getIncludeNotReportableAuxData()))
            builder.addWhere(SampleWebMeta.getAuxDataIsReportable() + "=" + "'Y'");
        builder.addWhere(SampleWebMeta.getAuxDataValue() + "!=" + "null");

        /*
         * This is done to add the clause for the aux field's analyte's name to
         * the query even if the query is not restricted by analyte id because
         * the user wants to see aux data for all analytes. Otherwise, in that
         * case the clause won't get added.
         */
        builder.addWhere(SampleWebMeta.getAuxDataFieldAnalyteName() + "!=" + "null");

        /*
         * Add the clause for limiting the aux data by analytes only if the user
         * selected some specific analytes and not all of them. This eliminates
         * the unnecessary time spent on excluding those aux data from the
         * records returned by the query
         */
        if (unselAnalyteIds != null && unselAnalyteIds.size() > 0)
            builder.addWhere(SampleWebMeta.getAuxDataFieldAnalyteId() +
                             getAnalyteClause(auxFieldValueMap.keySet(), unselAnalyteIds) + ")");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber() + "," +
                           SampleWebMeta.getAuxDataFieldAnalyteName());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    /**
     * Returns a clause for excluding the unselected ids if more than the
     * selected ids or including analyte ids, such that, if then the clause will
     * begin with "not in" followed by the unselected ids or otherwise with "in"
     * followed by the selected ids
     */
    private String getAnalyteClause(Set<Integer> selAnalytes, ArrayList<Integer> unselAnalytes) {
        StringBuffer buf;
        Object arr[];

        buf = new StringBuffer();
        if (unselAnalytes.size() < selAnalytes.size()) {
            /*
             * create a clause that excludes the analytes not selected
             */
            buf.append(" not in (");
            arr = unselAnalytes.toArray();
        } else {
            /*
             * create a clause that includes the selected analytes
             */
            buf.append(" in (");
            arr = selAnalytes.toArray();
        }

        for (int i = 0; i < arr.length; i++ ) {
            buf.append(arr[i]);
            if (i < arr.length - 1)
                buf.append(",");
        }

        buf.append(") ");

        return buf.toString();
    }
}