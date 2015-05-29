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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataView1VO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAuxDataFetch1VO;
import org.openelis.domain.DataViewAuxDataFetchVO;
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
import org.openelis.ui.util.XMLUtil;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    private static final SampleWebMeta meta               = new SampleWebMeta();

    private static final Logger        log                = Logger.getLogger("openelis");

    private static final int           QUERY_RESULT_LIMIT = 1000000;

    private static final String        DATA_VIEW          = "data_view", FILTERS = "filters",
                    EXCLUDE_RES_OVERRIDE = "excludeResultOverride", EXCLUDE_RES = "excludeResults",
                    INCLUDE_NOT_REP_RES = "includeNotReportableResults",
                    EXCLUDE_AUX = "excludeAuxData",
                    INCLUDE_NOT_REP_AUX = "includeNotReportableAuxData",
                    QUERY_FIELDS = "query_fields", COLUMNS = "columns";

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

    /**
     * Returns a VO filled from the data in the xml file located at the passed
     * url; the VO contains the "include" and "exclude" filters, the query
     * fields and columns
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataView1VO openQuery(String url) throws Exception {
        int i, j;
        DataView1VO data;
        Document doc;
        Node root;
        Element parent, child;

        doc = XMLUtil.load(url);

        data = new DataView1VO();
        data.setQueryFields(new ArrayList<QueryData>());
        data.setColumns(new ArrayList<String>());

        root = doc.getDocumentElement();
        for (i = 0; i < root.getChildNodes().getLength(); i++ ) {
            parent = (Element)root.getChildNodes().item(i);

            switch (parent.getTagName()) {
            /*
             * set "include" and "exclude" filters
             */
                case FILTERS:
                    for (j = 0; j < parent.getChildNodes().getLength(); j++ ) {
                        child = (Element)parent.getChildNodes().item(j);
                        switch (child.getTagName()) {
                            case EXCLUDE_RES_OVERRIDE:
                                data.setExcludeResultOverride(child.getTextContent());
                                break;
                            case EXCLUDE_RES:
                                data.setExcludeResults(child.getTextContent());
                                break;
                            case INCLUDE_NOT_REP_RES:
                                data.setIncludeNotReportableResults(child.getTextContent());
                                break;
                            case EXCLUDE_AUX:
                                data.setExcludeAuxData(child.getTextContent());
                                break;
                            case INCLUDE_NOT_REP_AUX:
                                data.setIncludeNotReportableAuxData(child.getTextContent());
                                break;
                        }
                    }
                    break;
                /*
                 * set query fields
                 */
                case QUERY_FIELDS:
                    for (j = 0; j < parent.getChildNodes().getLength(); j++ ) {
                        child = (Element)parent.getChildNodes().item(j);
                        data.getQueryFields().add(new QueryData(child.getTagName(),
                                                                null,
                                                                child.getTextContent()));
                    }
                    break;
                /*
                 * set columns
                 */
                case COLUMNS:
                    for (j = 0; j < parent.getChildNodes().getLength(); j++ ) {
                        child = (Element)parent.getChildNodes().item(j);
                        data.getColumns().add(child.getTagName());
                    }
                    break;
            }
        }

        return data;
    }

    /**
     * Creates an xml document from the passed VO and saves it to a file;
     * elements are created for the "include" and "exclude" filters, the query
     * fields and columns; the returned status contains the path to the file
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus saveQuery(DataView1VO data) throws Exception {
        Document doc;
        Element root, elm;
        ReportStatus status;
        Path path;
        OutputStream out;
        ByteArrayOutputStream byteOut;
        Transformer transformer;
        DOMSource source;
        StreamResult result;
        ArrayList<QueryData> fields;
        ArrayList<String> columns;

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataViewQuery", status);
        out = null;
        byteOut = null;
        try {
            /*
             * create elements for the "include" and "exclude" filters, the
             * query fields and columns
             */
            doc = XMLUtil.createNew(DATA_VIEW);
            root = doc.getDocumentElement();

            elm = doc.createElement(FILTERS);
            elm.appendChild(toXML(doc, EXCLUDE_RES_OVERRIDE, data.getExcludeResultOverride()));
            elm.appendChild(toXML(doc, EXCLUDE_RES, data.getExcludeResults()));
            elm.appendChild(toXML(doc, INCLUDE_NOT_REP_RES, data.getIncludeNotReportableResults()));
            elm.appendChild(toXML(doc, EXCLUDE_AUX, data.getExcludeAuxData()));
            elm.appendChild(toXML(doc, INCLUDE_NOT_REP_AUX, data.getIncludeNotReportableAuxData()));
            root.appendChild(elm);

            fields = data.getQueryFields();
            if (fields != null && fields.size() > 0) {
                elm = doc.createElement(QUERY_FIELDS);
                for (QueryData f : fields)
                    elm.appendChild(toXML(doc, f.getKey(), f.getQuery()));
                root.appendChild(elm);
            }

            columns = data.getColumns();
            if (columns != null && columns.size() > 0) {
                elm = doc.createElement(COLUMNS);
                for (String c : columns)
                    elm.appendChild(toXML(doc, c, null));
                root.appendChild(elm);
            }

            /*
             * write the document to a byte output stream; write the bytes to a
             * file
             */
            source = new DOMSource(doc);
            byteOut = new ByteArrayOutputStream();
            result = new StreamResult(byteOut);
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

            status.setMessage("Saving query").setPercentComplete(20);
            path = ReportUtil.createTempFile("query", ".xml", "upload_stream_directory");
            status.setPercentComplete(100);
            out = Files.newOutputStream(path);
            out.write(byteOut.toByteArray());

            status.setMessage(path.getFileName().toString())
                  .setPath(path.toString())
                  .setStatus(ReportStatus.Status.SAVED);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (byteOut != null)
                    byteOut.close();
            } catch (Exception e) {
                log.severe("Could not close byte output stream for saving data view query");
            }

            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for saving data view query");
            }
        }

        return status;
    }

    /**
     * Executes a query created from the query fields in the VO to fetch results
     * and aux data; if moduleName is not null, includes the clause for the
     * module with that name in the query; doesn't fetch results or aux data if
     * excludeResults or excludeAuxData is set to "Y"; the returned VO contains
     * two lists, one for analytes linked to results and another one for
     * analytes linked to aux data; each element of these lists also has the
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
                              ", " + SampleWebMeta.getItemId() + ", " +
                              SampleWebMeta.getResultAnalysisid() + "," +
                              SampleWebMeta.getResultAnalyteId() + "," +
                              SampleWebMeta.getResultAnalyteName() + "," +
                              SampleWebMeta.getResultTypeId() + "," +
                              SampleWebMeta.getResultValue() + ") ");
            buildWhere(builder, data, moduleName, true);
            results = (List<DataViewResultFetch1VO>)fetchAnalytesAndValues(builder, fields);
            log.log(Level.INFO, "Fetched " + results.size() + " results");

            if (results.size() > QUERY_RESULT_LIMIT)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(results.size(),
                                                                                        QUERY_RESULT_LIMIT));
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

            if (auxiliary.size() > QUERY_RESULT_LIMIT)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(results.size(),
                                                                                        QUERY_RESULT_LIMIT));
        }

        if ( (results == null || results.isEmpty()) && (auxiliary == null || auxiliary.isEmpty()))
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
        boolean excludeRes, excludeAux, fetchQa, fetchUser, fetchProv;
        ReportStatus status;
        QueryBuilderV2 builder;
        HSSFWorkbook wb;
        OutputStream out;
        Path path;
        ArrayList<String> headers, hds;
        List<DataViewResultFetch1VO> results, noResAux;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<Integer> unselAnalyteIds;
        ArrayList<TestAnalyteDataView1VO> testAnalytes;
        ArrayList<AuxFieldDataView1VO> auxFields;
        ArrayList<SampleManager1> sms;
        ArrayList<SampleManager1.Load> load;
        HashSet<String> columns, resultValues, auxValues;
        HashSet<Integer> analysisIds, sampleIds;
        HashMap<Integer, String> colFields;
        HashMap<Integer, HashSet<String>> testAnaResMap, auxFieldValMap;
        HashMap<Integer, SampleManager1> smMap;

        status = new ReportStatus();
        if (stopReport(status))
            return status;

        excludeRes = "Y".equals(data.getExcludeResults());
        excludeAux = "Y".equals(data.getExcludeAuxData());

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        results = null;
        auxiliary = null;
        noResAux = null;
        testAnaResMap = null;
        auxFieldValMap = null;

        load = new ArrayList<SampleManager1.Load>();
        headers = new ArrayList<String>();
        colFields = new HashMap<Integer, String>();
        columns = new HashSet<String>(data.getColumns());

        /*
         * get the labels to be displayed in the headers for the various fields;
         * keep track of which column is showing which field; get sample
         * headers; fetch projects only if they'll be shown in the output
         */
        hds = getSampleHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0) {
            headers.addAll(hds);
            if (columns.contains(SampleWebMeta.PROJECT_NAME))
                load.add(SampleManager1.Load.PROJECT);
        }

        /*
         * get organization headers; fetch organizations only if they'll be
         * shown in the output
         */
        hds = getOrganizationHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0) {
            headers.addAll(hds);
            load.add(SampleManager1.Load.ORGANIZATION);
        }

        /*
         * get headers for sample item and analysis
         */
        hds = getSampleItemHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0)
            headers.addAll(hds);

        fetchQa = "Y".equals(data.getExcludeResultOverride());
        fetchUser = false;
        hds = getAnalysisHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0) {
            headers.addAll(hds);
            if ( !fetchQa && columns.contains(SampleWebMeta.ANALYSISSUBQA_NAME))
                fetchQa = true;
            fetchUser = columns.contains(SampleWebMeta.ANALYSIS_COMPLETED_BY) ||
                        columns.contains(SampleWebMeta.ANALYSIS_RELEASED_BY);
        }
        /*
         * fetch qa events only if they're needed for excluding overridden
         * samples or analyses or if they'll be shown in the output; fetch
         * analysis users only if they'll be shown in the output
         */
        if (fetchQa)
            load.add(SampleManager1.Load.QA);
        if (fetchUser)
            load.add(SampleManager1.Load.ANALYSISUSER);

        /*
         * get headers for domains
         */
        hds = getEnvironmentalHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0)
            headers.addAll(hds);

        hds = getSDWISHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0)
            headers.addAll(hds);

        fetchProv = false;
        hds = getClinicalHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0) {
            headers.addAll(hds);
            fetchProv = columns.contains(SampleWebMeta.CLIN_PROVIDER_LAST_NAME) ||
                        columns.contains(SampleWebMeta.CLIN_PROVIDER_FIRST_NAME);
        }

        hds = getNeonatalHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0) {
            headers.addAll(hds);
            if ( !fetchProv)
                fetchProv = columns.contains(SampleWebMeta.NEO_PROVIDER_LAST_NAME) ||
                            columns.contains(SampleWebMeta.NEO_PROVIDER_FIRST_NAME);
        }
        /*
         * fetch providers only if they'll be shown in the output
         */
        if (fetchProv)
            load.add(SampleManager1.Load.PROVIDER);

        hds = getPTHeaders(columns, headers.size(), colFields);
        if (hds.size() > 0)
            headers.addAll(hds);

        /*
         * the headers for analyte and value are always shown if results and/or
         * aux data are not excluded
         */
        if ( !excludeRes || !excludeAux) {
            headers.add(Messages.get().gen_analyte());
            headers.add(Messages.get().gen_value());
        }

        status.setMessage(Messages.get().report_fetchingData());
        session.setAttribute("DataViewReportStatus", status);

        if (excludeRes && excludeAux) {
            noResAux = fetchNoResultAuxData(moduleName, builder, data);
        } else {
            if ( !excludeRes) {
                load.add(SampleManager1.Load.SINGLERESULT);
                unselAnalyteIds = new ArrayList<Integer>();
                testAnalytes = data.getTestAnalytes();
                if (testAnalytes != null) {
                    /*
                     * the analytes and results selected by the user are stored
                     * in this map so that they can be used later on to select
                     * or reject adding a row for a result based on whether or
                     * not it belongs in the map
                     */
                    testAnaResMap = new HashMap<Integer, HashSet<String>>();
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
                        testAnaResMap.put(ana.getAnalyteId(), resultValues);
                    }
                }

                /*
                 * fetch fields related to results based on the analytes and
                 * values selected by the user from the lists associated with
                 * test analytes
                 */
                if (testAnaResMap != null && testAnaResMap.size() > 0) {
                    if (testAnaResMap.size() > QUERY_RESULT_LIMIT / 5)
                        throw new InconsistencyException(Messages.get()
                                                                 .dataView_queryTooBigException(testAnaResMap.size(),
                                                                                                QUERY_RESULT_LIMIT / 5));
                    log.log(Level.INFO, "Before fetching results");
                    results = fetchResults(moduleName,
                                           builder,
                                           testAnaResMap,
                                           unselAnalyteIds,
                                           data);
                    log.log(Level.INFO, "Fetched " + results.size() + " results");
                }
            }

            if (stopReport(status))
                return status;

            if ( !excludeAux) {
                load.add(SampleManager1.Load.AUXDATA);
                unselAnalyteIds = new ArrayList<Integer>();
                auxFields = data.getAuxFields();
                if (auxFields != null) {
                    /*
                     * the analytes and values selected by the user are stored
                     * in this hashmap so that they can be used later on to
                     * select or reject adding a row for an aux data based on
                     * whether or not it belongs in the hashmap
                     */
                    auxFieldValMap = new HashMap<Integer, HashSet<String>>();
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
                        auxFieldValMap.put(af.getAnalyteId(), auxValues);
                    }
                }
                /*
                 * fetch fields related to aux data based on the analytes and
                 * values selected by the user from the lists associated with
                 * aux fields
                 */
                builder.clearWhereClause();
                if (auxFieldValMap != null && auxFieldValMap.size() > 0) {
                    if (auxFieldValMap.size() > (QUERY_RESULT_LIMIT / 5))
                        throw new InconsistencyException(Messages.get()
                                                                 .dataView_queryTooBigException(auxFieldValMap.size(),
                                                                                                QUERY_RESULT_LIMIT / 5));
                    log.log(Level.INFO, "Before fetching aux data");
                    auxiliary = fetchAuxData(moduleName,
                                             builder,
                                             auxFieldValMap,
                                             unselAnalyteIds,
                                             data);
                    log.log(Level.INFO, "Fetched " + auxiliary.size() + " aux data");
                }
            }
        }

        if ( (results == null || results.size() == 0) &&
            (auxiliary == null || auxiliary.size() == 0) &&
            (noResAux == null || noResAux.size() == 0))
            throw new NotFoundException();

        if (stopReport(status))
            return status;

        status.setPercentComplete(20);

        /*
         * make a list of analysis or sample ids from the fetched results and
         * aux data and fetch the managers
         */
        analysisIds = new HashSet<Integer>();
        sampleIds = new HashSet<Integer>();

        if (noResAux != null) {
            for (DataViewResultFetch1VO nra : noResAux)
                analysisIds.add(nra.getAnalysisId());
        } else {
            if (results != null) {
                for (DataViewResultFetch1VO res : results)
                    analysisIds.add(res.getAnalysisId());
            }

            if (auxiliary != null) {
                for (DataViewAuxDataFetch1VO aux : auxiliary)
                    sampleIds.add(aux.getSampleId());
            }
        }

        log.log(Level.INFO, "Before fetching managers");

        if (analysisIds.size() > 0) {
            load.add(SampleManager1.Load.SINGLEANALYSIS);
            sms = sampleManager1.fetchByAnalyses(DataBaseUtil.toArrayList(analysisIds),
                                                 load.toArray(new SampleManager1.Load[load.size()]));
        } else {
            sms = sampleManager1.fetchByIds(DataBaseUtil.toArrayList(sampleIds),
                                            load.toArray(new SampleManager1.Load[load.size()]));
        }

        log.log(Level.INFO, "Fetched " + sms.size() + " managers");

        status.setPercentComplete(100);

        smMap = new HashMap<Integer, SampleManager1>();
        for (SampleManager1 sm : sms)
            smMap.put(getSample(sm).getId(), sm);

        sms = null;

        wb = getWorkbook(results,
                         auxiliary,
                         noResAux,
                         testAnaResMap,
                         auxFieldValMap,
                         moduleName,
                         showReportableColumnsOnly,
                         headers,
                         colFields,
                         data,
                         smMap,
                         status);

        /*
         * if (wb != null) { out = null; try {
         * status.setMessage("Outputing report").setPercentComplete(20); path =
         * ReportUtil.createTempFile("dataview", ".xls",
         * "upload_stream_directory");
         * 
         * status.setPercentComplete(100);
         * 
         * out = Files.newOutputStream(path); wb.write(out); out.close();
         * status.setMessage(path.getFileName().toString())
         * .setPath(path.toString()) .setStatus(ReportStatus.Status.SAVED); }
         * catch (Exception e) { e.printStackTrace(); throw e; } finally { try {
         * if (out != null) out.close(); } catch (Exception e) { // ignore } } }
         */

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
        Query query;
        ArrayList<QueryData> fields;
        ArrayList<String> orderBy;

        fields = data.getQueryFields();

        builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + "," +
                          SampleWebMeta.getItemId() + "," + SampleWebMeta.getResultAnalysisid() +
                          "," + SampleWebMeta.getResultAnalyteId() + "," +
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
        /*
         * the user wants to see only reportable results
         */
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

        /*
         * the user wants to see only reportable aux data
         */
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

    private List<DataViewResultFetch1VO> fetchNoResultAuxData(String moduleName,
                                                              QueryBuilderV2 builder,
                                                              DataView1VO data) throws Exception {
        Query query;
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();

        builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getItemId() + ", " + SampleWebMeta.getAnalysisId() + ")");

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

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());
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

    private HSSFWorkbook getWorkbook(List<DataViewResultFetch1VO> results,
                                     List<DataViewAuxDataFetch1VO> auxiliary,
                                     List<DataViewResultFetch1VO> noResAux,
                                     HashMap<Integer, HashSet<String>> testAnaResMap,
                                     HashMap<Integer, HashSet<String>> auxFieldValMap,
                                     String moduleName, boolean showReportableColumnsOnly,
                                     ArrayList<String> headers, HashMap<Integer, String> colFields,
                                     DataView1VO data, HashMap<Integer, SampleManager1> smMap,
                                     ReportStatus status) {
        boolean excludeOverride, excludeRes, excludeAux, sampleOverridden, anaOverridden, addResRow, addAuxRow, addNoResAuxRow;
        int i, resIndex, auxIndex, noResAuxIndex, rowIndex, numRes, numAux, numNoResAux;
        Integer sampleId, prevSamId, itemId, analysisId, prevAnaId;
        SampleManager1 sm;
        HSSFWorkbook wb;
        HSSFSheet sheet;
        DataViewResultFetch1VO res, noRA;
        DataViewAuxDataFetch1VO aux;
        Row headerRow, resRow, auxRow, noResAuxRow, currRow, prevRow;
        Cell cell;
        CellStyle headerStyle;

        wb = new HSSFWorkbook();
        sheet = wb.createSheet();

        headerRow = sheet.createRow(0);
        headerStyle = createStyle(wb);
        /*
         * create the header row and set its style
         */
        for (i = 0; i < headers.size(); i++ ) {
            cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        numRes = results == null ? 0 : results.size();
        numAux = auxiliary == null ? 0 : auxiliary.size();
        numNoResAux = noResAux == null ? 0 : noResAux.size();

        resIndex = 0;
        auxIndex = 0;
        noResAuxIndex = 0;
        rowIndex = 0;
        sampleId = null;
        prevSamId = null;
        itemId = null;
        analysisId = null;
        prevAnaId = null;
        sampleOverridden = false;
        anaOverridden = false;
        addResRow = false;
        addAuxRow = false;
        addNoResAuxRow = false;
        sm = null;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeRes = "Y".equals(data.getExcludeResults());
        excludeAux = "Y".equals(data.getExcludeAuxData());

        status.setMessage(Messages.get().report_genDataView());
        status.setPercentComplete(0);
        session.setAttribute("DataViewReportStatus", status);

        /*
         * the lists of results and aux data are iterated through until there
         * are no more elements left in each of them to read from
         */
        while (resIndex < numRes || auxIndex < numAux || noResAuxIndex < numNoResAux) {
            if (stopReport(status))
                return null;

            status.setPercentComplete(100 * (resIndex + auxIndex + noResAuxIndex) /
                                      (numRes + numAux + numNoResAux));
            session.setAttribute("DataViewReportStatus", status);
            if (excludeRes && excludeAux) {
                if (noResAuxIndex < numNoResAux) {
                    noRA = noResAux.get(noResAuxIndex++ );
                    sampleId = noRA.getSampleId();
                    itemId = noRA.getSampleItemId();
                    analysisId = noRA.getAnalysisId();
                    addNoResAuxRow = true;
                }
            } else {
                if (resIndex < numRes && auxIndex < numAux) {
                    res = results.get(resIndex);
                    aux = auxiliary.get(auxIndex);
                    /*
                     * If this result's accession number is less than or equal
                     * to this aux data's then add a row for this result,
                     * otherwise add a row for the aux data. This makes sure
                     * that the results for a sample are shown before the aux
                     * data. Accession numbers are compared instead of sample
                     * ids because the former is the field shown in the output
                     * and not the latter.
                     */
                    if (res.getSampleAccessionNumber() <= aux.getSampleAccessionNumber()) {
                        addResRow = true;
                        addAuxRow = false;
                        resIndex++ ;
                        sampleId = res.getSampleId();
                        itemId = res.getSampleItemId();
                        analysisId = res.getAnalysisId();
                    } else {
                        addAuxRow = true;
                        addResRow = false;
                        auxIndex++ ;
                        sampleId = aux.getSampleId();
                    }
                } else if (resIndex < numRes) {
                    /*
                     * no more aux data left to show
                     */
                    addResRow = true;
                    addAuxRow = false;
                    res = results.get(resIndex);
                    resIndex++ ;
                    sampleId = res.getSampleId();
                    itemId = res.getSampleItemId();
                    analysisId = res.getAnalysisId();
                } else if (auxIndex < numAux) {
                    /*
                     * no more results left to show
                     */
                    addAuxRow = true;
                    addResRow = false;
                    aux = auxiliary.get(auxIndex);
                    auxIndex++ ;
                    sampleId = aux.getSampleId();
                }
            }

            /*
             * don't show any data for this sample if it's overridden and such
             * samples are to be excluded
             */
            if ( !sampleId.equals(prevSamId)) {
                sm = smMap.get(sampleId);
                sampleOverridden = false;
                if (excludeOverride && (getSampleQAs(sm) != null)) {
                    for (SampleQaEventViewDO sqa : getSampleQAs(sm)) {
                        if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId())) {
                            sampleOverridden = true;
                            prevSamId = sampleId;
                            break;
                        }
                    }
                }
                if (sampleOverridden)
                    continue;
            } else if (sampleOverridden && excludeOverride) {
                continue;
            }

            if (addResRow) {
                /*
                 * don't show any data for this analysis if it's overridden and
                 * such analyses are to be excluded
                 */
                if ( !analysisId.equals(prevAnaId)) {
                    anaOverridden = false;
                    if (excludeOverride && (getAnalysisQAs(sm) != null)) {
                        for (AnalysisQaEventViewDO aqa : getAnalysisQAs(sm)) {
                            if (aqa.getAnalysisId().equals(analysisId) &&
                                Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId())) {
                                anaOverridden = true;
                                addResRow = false;
                                prevAnaId = analysisId;
                                break;
                            }
                        }
                    }
                } else if (anaOverridden && excludeOverride) {
                    addResRow = false;
                }
            }

            resRow = null;
            if (addResRow) {
                /*
                 * if the value of this result was selected by the user, add a
                 * row for it
                 /
                resVal = getResultValue(testAnaResMap, res);
                if (resVal != null)
                    currRow = resRow = sheet.createRow(rowIndex++ );
                else
                    addResRow = false;*/
            }

            auxRow = null;
            if (addAuxRow) {
                /*
                 * check to see if the value of this aux data was selected by
                 * the user to be shown in the sheet and if it was add a row for
                 * it to the sheet otherwise don't
                 /
                auxVal = getAuxDataValue(auxFieldValMap, aux);
                if (auxVal != null)
                    currRow = auxRow = sheet.createRow(rowIndex++ );
                else
                    addAuxRow = false;*/
            }

            noResAuxRow = null;
            if (addNoResAuxRow)
                currRow = noResAuxRow = sheet.createRow(rowIndex++ );

            if ( !addResRow && !addAuxRow && !addNoResAuxRow)
                continue;
        }

        return wb;
    }

    /**
     * Checks whether the attribute for stopping the report has been set in the
     * session; returns true if it is; also sets the message in the passed
     * status to inform the user that the report has been stopped and removes
     * that attribute from the session; returns false otherwise
     */
    private boolean stopReport(ReportStatus status) {
        Object stop;

        stop = (Boolean)session.getAttribute("DataViewStopReport");
        if (Boolean.TRUE.equals(stop)) {
            status.setMessage(Messages.get().report_stopped());
            session.removeAttribute("DataViewStopReport");
            return true;
        }

        return false;
    }

    private CellStyle createStyle(HSSFWorkbook wb) {
        CellStyle headerStyle;
        Font font;

        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        headerStyle.setFont(font);

        return headerStyle;
    }

    private ArrayList<String> getSampleHeaders(HashSet<String> columns, int startCol,
                                               HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.ACCESSION_NUMBER:
                    headers.add(Messages.get().sample_accessionNum());
                    break;
                case SampleWebMeta.REVISION:
                    headers.add(Messages.get().dataView_sampleRevision());
                    break;
                case SampleWebMeta.COLLECTION_DATE:
                    headers.add(Messages.get().sample_collectedDate());
                    break;
                case SampleWebMeta.RECEIVED_DATE:
                    headers.add(Messages.get().gen_receivedDate());
                    break;
                case SampleWebMeta.ENTERED_DATE:
                    headers.add(Messages.get().gen_enteredDate());
                    break;
                case SampleWebMeta.RELEASED_DATE:
                    headers.add(Messages.get().sample_releasedDate());
                    break;
                case SampleWebMeta.STATUS_ID:
                    headers.add(Messages.get().sample_status());
                    break;
                case SampleWebMeta.PROJECT_NAME:
                    headers.add(Messages.get().project_project());
                    break;
                case SampleWebMeta.CLIENT_REFERENCE_HEADER:
                    headers.add(Messages.get().sample_clntRef());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getOrganizationHeaders(HashSet<String> columns, int startCol,
                                                     HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.SAMPLE_ORG_ID:
                    headers.add(Messages.get().organization_num());
                    break;
                case SampleWebMeta.ORG_NAME:
                    headers.add(Messages.get().organization_name());
                    break;
                case SampleWebMeta.SAMPLE_ORG_ATTENTION:
                    headers.add(Messages.get().order_attention());
                    break;
                case SampleWebMeta.ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().address_aptSuite());
                    break;
                case SampleWebMeta.ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().address_address());
                    break;
                case SampleWebMeta.ADDR_CITY:
                    headers.add(Messages.get().address_city());
                    break;
                case SampleWebMeta.ADDR_STATE:
                    headers.add(Messages.get().address_state());
                    break;
                case SampleWebMeta.ADDR_ZIP_CODE:
                    headers.add(Messages.get().address_zipcode());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getSampleItemHeaders(HashSet<String> columns, int startCol,
                                                   HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID:
                    headers.add(Messages.get().gen_sampleType());
                    break;
                case SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID:
                    headers.add(Messages.get().gen_source());
                    break;
                case SampleWebMeta.ITEM_SOURCE_OTHER:
                    headers.add(Messages.get().sampleItem_sourceOther());
                    break;
                case SampleWebMeta.ITEM_CONTAINER_ID:
                    headers.add(Messages.get().gen_container());
                    break;
                case SampleWebMeta.ITEM_CONTAINER_REFERENCE:
                    headers.add(Messages.get().sampleItem_containerReference());
                    break;
                case SampleWebMeta.ITEM_ITEM_SEQUENCE:
                    headers.add(Messages.get().gen_sequence());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getAnalysisHeaders(HashSet<String> columns, int startCol,
                                                 HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.ANALYSIS_ID:
                    headers.add(Messages.get().analysis_id());
                    break;
                case SampleWebMeta.ANALYSIS_TEST_NAME_HEADER:
                    headers.add(Messages.get().gen_test());
                    break;
                case SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER:
                    headers.add(Messages.get().gen_method());
                    break;
                case SampleWebMeta.ANALYSIS_STATUS_ID_HEADER:
                    headers.add(Messages.get().analysis_status());
                    break;
                case SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER:
                    headers.add(Messages.get().gen_reportable());
                    break;
                case SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID:
                    headers.add(Messages.get().gen_unit());
                    break;
                case SampleWebMeta.ANALYSISSUBQA_NAME:
                    headers.add(Messages.get().qaEvent_qaEvent());
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_DATE:
                    headers.add(Messages.get().gen_completedDate());
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_BY:
                    headers.add(Messages.get().dataView_completedBy());
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_DATE:
                    headers.add(Messages.get().analysis_releasedDate());
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_BY:
                    headers.add(Messages.get().dataView_releasedBy());
                    break;
                case SampleWebMeta.ANALYSIS_STARTED_DATE:
                    headers.add(Messages.get().gen_startedDate());
                    break;
                case SampleWebMeta.ANALYSIS_PRINTED_DATE:
                    headers.add(Messages.get().gen_printedDate());
                    break;
                case SampleWebMeta.ANALYSIS_SECTION_NAME:
                    headers.add(Messages.get().gen_section());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getEnvironmentalHeaders(HashSet<String> columns, int startCol,
                                                      HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.ENV_IS_HAZARDOUS:
                    headers.add(Messages.get().sampleEnvironmental_hazardous());
                    break;
                case SampleWebMeta.ENV_PRIORITY:
                    headers.add(Messages.get().gen_priority());
                    break;
                case SampleWebMeta.ENV_COLLECTOR_HEADER:
                    headers.add(Messages.get().env_collector());
                    break;
                case SampleWebMeta.ENV_COLLECTOR_PHONE:
                    headers.add(Messages.get().address_phone());
                    break;
                case SampleWebMeta.ENV_LOCATION_HEADER:
                    headers.add(Messages.get().env_location());
                    break;
                case SampleWebMeta.ENV_DESCRIPTION:
                    headers.add(Messages.get().sample_description());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getPrivateWellHeaders(HashSet<String> columns) {
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        if (columns.contains(SampleWebMeta.getWellOwner()))
            headers.add(Messages.get().owner());
        if (columns.contains(SampleWebMeta.getWellCollector()))
            headers.add(Messages.get().collector());
        if (columns.contains(SampleWebMeta.getWellWellNumber()))
            headers.add(Messages.get().wellNum());
        if (columns.contains(SampleWebMeta.getWellReportToAddressWorkPhone()))
            headers.add(Messages.get().phone());
        if (columns.contains(SampleWebMeta.getWellReportToAddressFaxPhone()))
            headers.add(Messages.get().faxNumber());
        if (columns.contains(SampleWebMeta.getWellLocation()))
            headers.add(Messages.get().location());
        if (columns.contains(SampleWebMeta.getWellLocationAddrMultipleUnit()))
            headers.add(Messages.get().locationAptSuite());
        if (columns.contains(SampleWebMeta.getWellLocationAddrStreetAddress()))
            headers.add(Messages.get().locationAddress());
        if (columns.contains(SampleWebMeta.getWellLocationAddrCity()))
            headers.add(Messages.get().locationCity());
        if (columns.contains(SampleWebMeta.getWellLocationAddrState()))
            headers.add(Messages.get().locationState());
        if (columns.contains(SampleWebMeta.getWellLocationAddrZipCode()))
            headers.add(Messages.get().locationZipCode());

        return headers;
    }

    private ArrayList<String> getSDWISHeaders(HashSet<String> columns, int startCol,
                                              HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.SDWIS_PWS_ID:
                    headers.add(Messages.get().pws_id());
                    break;
                case SampleWebMeta.PWS_NAME:
                    headers.add(Messages.get().sampleSDWIS_pwsName());
                    break;
                case SampleWebMeta.SDWIS_STATE_LAB_ID:
                    headers.add(Messages.get().sampleSDWIS_stateLabNo());
                    break;
                case SampleWebMeta.SDWIS_FACILITY_ID:
                    headers.add(Messages.get().sampleSDWIS_facilityId());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_TYPE_ID:
                    headers.add(Messages.get().sampleSDWIS_sampleType());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID:
                    headers.add(Messages.get().sampleSDWIS_category());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_POINT_ID:
                    headers.add(Messages.get().sampleSDWIS_samplePtId());
                    break;
                case SampleWebMeta.SDWIS_LOCATION:
                    headers.add(Messages.get().sampleSDWIS_location());
                    break;
                case SampleWebMeta.SDWIS_PRIORITY:
                    headers.add(Messages.get().gen_priority());
                    break;
                case SampleWebMeta.SDWIS_COLLECTOR:
                    headers.add(Messages.get().sampleSDWIS_collector());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getClinicalHeaders(HashSet<String> columns, int startCol,
                                                 HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.CLIN_PATIENT_ID:
                    headers.add(Messages.get().dataView_patientId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_LAST_NAME:
                    headers.add(Messages.get().dataView_patientLastName());
                    break;
                case SampleWebMeta.CLIN_PATIENT_FIRST_NAME:
                    headers.add(Messages.get().dataView_patientFirstName());
                    break;
                case SampleWebMeta.CLIN_PATIENT_BIRTH_DATE:
                    headers.add(Messages.get().patient_birth());
                    break;
                case SampleWebMeta.CLIN_PATIENT_NATIONAL_ID:
                    headers.add(Messages.get().patient_nationalId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().dataView_patientAptSuite());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().dataView_patientAddress());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_CITY:
                    headers.add(Messages.get().dataView_patientCity());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STATE:
                    headers.add(Messages.get().dataView_patientState());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE:
                    headers.add(Messages.get().dataView_patientZipcode());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE:
                    headers.add(Messages.get().dataView_patientPhone());
                    break;
                case SampleWebMeta.CLIN_PATIENT_GENDER_ID:
                    headers.add(Messages.get().dataView_patientGender());
                    break;
                case SampleWebMeta.CLIN_PATIENT_RACE_ID:
                    headers.add(Messages.get().patient_race());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID:
                    headers.add(Messages.get().patient_ethnicity());
                    break;
                case SampleWebMeta.CLIN_PROVIDER_LAST_NAME:
                    headers.add(Messages.get().provider_lastName());
                    break;
                case SampleWebMeta.CLIN_PROVIDER_FIRST_NAME:
                    headers.add(Messages.get().provider_firstName());
                    break;
                case SampleWebMeta.CLIN_PROVIDER_PHONE:
                    headers.add(Messages.get().provider_firstName());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getNeonatalHeaders(HashSet<String> columns, int startCol,
                                                 HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.NEO_PATIENT_ID:
                    headers.add(Messages.get().dataView_patientId());
                    break;
                case SampleWebMeta.NEO_PATIENT_LAST_NAME:
                    headers.add(Messages.get().dataView_patientLastName());
                    break;
                case SampleWebMeta.NEO_PATIENT_FIRST_NAME:
                    headers.add(Messages.get().dataView_patientFirstName());
                    break;
                case SampleWebMeta.NEO_PATIENT_BIRTH_DATE:
                    headers.add(Messages.get().dataView_patientBirthDate());
                    break;
                case SampleWebMeta.NEO_PATIENT_BIRTH_TIME:
                    headers.add(Messages.get().dataView_patientBirthTime());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().dataView_patientAptSuite());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().dataView_patientAddress());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_CITY:
                    headers.add(Messages.get().dataView_patientCity());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STATE:
                    headers.add(Messages.get().dataView_patientState());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE:
                    headers.add(Messages.get().dataView_patientZipcode());
                    break;
                case SampleWebMeta.NEO_PATIENT_GENDER_ID:
                    headers.add(Messages.get().dataView_patientGender());
                    break;
                case SampleWebMeta.NEO_PATIENT_RACE_ID:
                    headers.add(Messages.get().patient_race());
                    break;
                case SampleWebMeta.NEO_PATIENT_ETHNICITY_ID:
                    headers.add(Messages.get().patient_ethnicity());
                    break;
                case SampleWebMeta.NEO_IS_NICU:
                    headers.add(Messages.get().sampleNeonatal_nicu());
                    break;
                case SampleWebMeta.NEO_BIRTH_ORDER:
                    headers.add(Messages.get().sampleNeonatal_birthOrder());
                    break;
                case SampleWebMeta.NEO_GESTATIONAL_AGE:
                    headers.add(Messages.get().sampleNeonatal_gestAge());
                    break;
                case SampleWebMeta.NEO_FEEDING_ID:
                    headers.add(Messages.get().sampleNeonatal_feeding());
                    break;
                case SampleWebMeta.NEO_WEIGHT:
                    headers.add(Messages.get().sampleNeonatal_weight());
                    break;
                case SampleWebMeta.NEO_IS_TRANSFUSED:
                    headers.add(Messages.get().sampleNeonatal_transfused());
                    break;
                case SampleWebMeta.NEO_TRANSFUSION_DATE:
                    headers.add(Messages.get().sampleNeonatal_transDate());
                    break;
                case SampleWebMeta.NEO_IS_REPEAT:
                    headers.add(Messages.get().gen_repeat());
                    break;
                case SampleWebMeta.NEO_COLLECTION_AGE:
                    headers.add(Messages.get().sampleNeonatal_collectAge());
                    break;
                case SampleWebMeta.NEO_IS_COLLECTION_VALID:
                    headers.add(Messages.get().sampleNeonatal_collectValid());
                    break;
                case SampleWebMeta.NEO_FORM_NUMBER:
                    headers.add(Messages.get().gen_barcode());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ID:
                    headers.add(Messages.get().dataView_nextOfKinId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME:
                    headers.add(Messages.get().dataView_nextOfKinLastName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME:
                    headers.add(Messages.get().dataView_nextOfKinMaidenName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME:
                    headers.add(Messages.get().dataView_nextOfKinFirstName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID:
                    headers.add(Messages.get().patient_relation());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE:
                    headers.add(Messages.get().dataView_nextOfKinBirthDate());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID:
                    headers.add(Messages.get().patient_nationalId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().dataView_nextOfKinAptSuite());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().dataView_nextOfKinAddress());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY:
                    headers.add(Messages.get().dataView_nextOfKinCity());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE:
                    headers.add(Messages.get().dataView_nextOfKinState());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE:
                    headers.add(Messages.get().dataView_nextOfKinZipcode());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID:
                    headers.add(Messages.get().dataView_nextOfKinGender());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID:
                    headers.add(Messages.get().patient_race());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID:
                    headers.add(Messages.get().patient_ethnicity());
                    break;
                case SampleWebMeta.NEO_PROVIDER_LAST_NAME:
                    headers.add(Messages.get().provider_lastName());
                    break;
                case SampleWebMeta.NEO_PROVIDER_FIRST_NAME:
                    headers.add(Messages.get().provider_firstName());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    private ArrayList<String> getPTHeaders(HashSet<String> columns, int startCol,
                                           HashMap<Integer, String> colFields) {
        boolean allHeadersSet;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allHeadersSet = false;
        for (String c : columns) {
            switch (c) {
                case SampleWebMeta.PT_PT_PROVIDER_ID:
                    headers.add(Messages.get().provider_provider());
                    break;
                case SampleWebMeta.PT_SERIES:
                    headers.add(Messages.get().gen_series());
                    break;
                case SampleWebMeta.PT_DUE_DATE:
                    headers.add(Messages.get().gen_due());
                    break;
                case SampleWebMeta.RECEIVED_BY_ID:
                    headers.add(Messages.get().gen_receivedBy());
                    break;
                default:
                    allHeadersSet = true;
                    break;

            }
            if (allHeadersSet)
                break;
            colFields.put(startCol++ , c);
        }

        return headers;
    }

    /**
     * Creates an element from the passed document and sets its name and text
     * value as the passed arguments
     */
    private Element toXML(Document doc, String name, String value) {
        Element elm;

        elm = doc.createElement(name);
        if ( !DataBaseUtil.isEmpty(value))
            elm.setTextContent(value);
        return elm;
    }
    
    private String getValue(HashMap<Integer, HashMap<String, String>> auxFieldValueMap,
                                   Integer analyteId, String value, Integer typeId) throws Exception {
        Integer id;
        HashMap<String, String> valMap;

        valMap = auxFieldValueMap.get(analyteId);
        //value = getValue(value, typeId);
        if (DataBaseUtil.isEmpty(value))
            return "";
        if (Constants.dictionary().AUX_DICTIONARY.equals(typeId) ||
            Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(typeId)) {
            id = Integer.parseInt(value);
            //value = dictionaryCache.getById(id).getEntry();
        }

        if (valMap == null || valMap.get(value) == null)
            return null;

        return value;
    }

    /**
     * This class is used to hold the data for cells belonging to a group like
     * sample or analysis etc.
     */
    private class CellGroup {
        Integer           id;
        ArrayList<String> cells;
    }
}