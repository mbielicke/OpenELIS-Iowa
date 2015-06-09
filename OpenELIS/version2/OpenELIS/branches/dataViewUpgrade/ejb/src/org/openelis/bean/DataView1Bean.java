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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataView1VO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAuxDataFetch1VO;
import org.openelis.domain.DataViewResultFetch1VO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePTDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.TestAnalyteDataView1VO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
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

    @EJB
    private DictionaryCacheBean        dictionaryCache;

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
        boolean excludeOverride, excludeRes, excludeAux;
        QueryBuilderV2 builder;
        ArrayList<QueryData> fields;
        List<DataViewResultFetch1VO> results;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<SampleQaEventViewDO> sqas;
        ArrayList<AnalysisQaEventViewDO> aqas;
        ArrayList<DictionaryViewDO> dictionaries;
        ArrayList<Integer> ids, range;
        HashSet<Integer> sampleIds, analysisIds, dictIds;
        HashMap<Integer, DictionaryViewDO> dictMap;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeRes = "Y".equals(data.getExcludeResults());
        excludeAux = "Y".equals(data.getExcludeAuxData());

        fields = data.getQueryFields();
        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        fields = data.getQueryFields();

        results = null;
        auxiliary = null;
        if ( !excludeRes) {
            /*
             * fetch results
             */
            log.log(Level.INFO, "Before fetching results");
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetch1VO(" +
                              SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() +
                              ", " + SampleWebMeta.getItemId() + ", " +
                              SampleWebMeta.getResultAnalysisid() + "," +
                              SampleWebMeta.getResultId() + "," +
                              SampleWebMeta.getResultAnalyteId() + "," +
                              SampleWebMeta.getResultAnalyteName() + "," +
                              SampleWebMeta.getResultTypeId() + "," +
                              SampleWebMeta.getResultValue() + ") ");
            buildWhereForResult(builder, data, moduleName);
            results = (List<DataViewResultFetch1VO>)fetchAnalytesAndValues(builder, fields);
            log.log(Level.INFO, "Fetched " + results.size() + " results");

            if (results.size() > QUERY_RESULT_LIMIT)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(results.size(),
                                                                                        QUERY_RESULT_LIMIT));
        }

        /*
         * go through the fetched results to make lists of their sample,
         * analysis ids and dictionary ids so that any qa events etc. linked to
         * them can be fetched
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

        if ( !excludeAux) {
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
            /*
             * if results were fetched, limit the aux data to only the samples
             * that the results belong to
             */
            if (sampleIds.size() > 0) {
                /*
                 * the list of sample ids is broken into subsets and the query
                 * is executed for each subset; the where clause is rebuilt for
                 * each subset because the sample ids are different and there's
                 * no easy way to just reset them and keep the rest of the
                 * clause the same
                 */
                auxiliary = new ArrayList<DataViewAuxDataFetch1VO>();
                ids = DataBaseUtil.toArrayList(sampleIds);
                range = DataBaseUtil.createSubsetRange(ids.size());
                for (int i = 0; i < range.size() - 1; i++ ) {
                    builder.clearWhereClause();
                    buildWhereForAux(builder, data, moduleName, ids.subList(range.get(i),
                                                                            range.get(i + 1)));
                    auxiliary.addAll(fetchAnalytesAndValues(builder, fields));
                }
            } else {
                builder.clearWhereClause();
                buildWhereForAux(builder, data, moduleName, null);
                auxiliary = (List<DataViewAuxDataFetch1VO>)fetchAnalytesAndValues(builder, fields);
            }
            log.log(Level.INFO, "Fetched " + auxiliary.size() + " aux data");

            if (auxiliary.size() > QUERY_RESULT_LIMIT)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(results.size(),
                                                                                        QUERY_RESULT_LIMIT));
        }

        if ( (results == null || results.isEmpty()) && (auxiliary == null || auxiliary.isEmpty()))
            throw new NotFoundException();

        /*
         * go through the fetched aux data to make lists of their dictionary ids
         * so the dictionary entries linked to them can be fetched
         */
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
         * if the list of sample ids is empty it means that overridden samples
         * and analyses are to be excluded and all fetched samples are
         * overridden, so nothing will results or aux data will be returned;
         * similarly, nothing will be returned if only results are to be
         * included and all fetched analyses are overridden
         */
        if ( (sampleIds.size() == 0 || ( !excludeRes && excludeAux && analysisIds.size() == 0)))
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
        if ( !excludeRes)
            data.setTestAnalytes(getTestAnalytes(results, sampleIds, analysisIds, dictMap));

        if ( !excludeAux)
            data.setAuxFields(getAuxFields(auxiliary, sampleIds, dictMap));

        return data;
    }

    private ReportStatus runReport(DataView1VO data, String moduleName,
                                   boolean showReportableColumnsOnly) throws Exception {
        boolean excludeRes, excludeAux;
        ReportStatus status;
        QueryBuilderV2 builder;
        HSSFWorkbook wb;
        OutputStream out;
        Path path;
        ArrayList<String> headers;
        List<DataViewResultFetch1VO> results, noResAux;
        List<DataViewAuxDataFetch1VO> auxiliary;
        ArrayList<Integer> unselAnalyteIds;
        ArrayList<TestAnalyteDataView1VO> testAnalytes;
        ArrayList<AuxFieldDataView1VO> auxFields;
        ArrayList<SampleManager1> sms;
        ArrayList<SampleManager1.Load> load;
        HashSet<String> resultValues, auxValues;
        HashSet<Integer> analysisIds, sampleIds;
        HashMap<Integer, String> colFieldMap;
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
        colFieldMap = new HashMap<Integer, String>();

        /*
         * get the labels to be displayed in the headers for the various fields;
         * keep track of which column is showing which field; get sample headers
         */
        headers.addAll(getSampleHeaders(data.getColumns(), headers.size(), colFieldMap, load));

        /*
         * get organization headers
         */
        headers.addAll(getOrganizationHeaders(data.getColumns(), headers.size(), colFieldMap, load));

        /*
         * get headers for sample item and analysis
         */
        headers.addAll(getSampleItemHeaders(data.getColumns(), headers.size(), colFieldMap));
        headers.addAll(getAnalysisHeaders(data.getColumns(), headers.size(), colFieldMap, load));

        if ("Y".equals(data.getExcludeResultOverride()) && !load.contains(SampleManager1.Load.QA))
            load.add(SampleManager1.Load.QA);

        /*
         * get headers for domains
         */
        headers.addAll(getEnvironmentalHeaders(data.getColumns(), headers.size(), colFieldMap));
        headers.addAll(getSDWISHeaders(data.getColumns(), headers.size(), colFieldMap));
        headers.addAll(getClinicalHeaders(data.getColumns(), headers.size(), colFieldMap, load));
        headers.addAll(getNeonatalHeaders(data.getColumns(), headers.size(), colFieldMap, load));
        headers.addAll(getPTHeaders(data.getColumns(), headers.size(), colFieldMap));

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

        analysisIds = new HashSet<Integer>();
        sampleIds = new HashSet<Integer>();

        if (excludeRes && excludeAux) {
            noResAux = fetchNoResultAuxData(moduleName, builder, data);
            /*
             * make a list of analysis ids from the fetched records for fetching
             * the managers
             */
            for (DataViewResultFetch1VO nra : noResAux)
                analysisIds.add(nra.getAnalysisId());
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
                    status.setPercentComplete(5);

                    /*
                     * make a list of analysis ids from the fetched results for
                     * fetching the managers; the list of sample ids is used to
                     * restrict the aux data to only those samples that the
                     * results belong to
                     */
                    for (DataViewResultFetch1VO res : results) {
                        analysisIds.add(res.getAnalysisId());
                        sampleIds.add(res.getSampleId());
                    }
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
                                             sampleIds,
                                             data);
                    log.log(Level.INFO, "Fetched " + auxiliary.size() + " aux data");
                    /*
                     * make a list of sample ids from the fetched aux data for
                     * fetching the managers
                     */
                    for (DataViewAuxDataFetch1VO aux : auxiliary)
                        sampleIds.add(aux.getSampleId());
                }
            }
        }

        status.setPercentComplete(25);

        if ( (results == null || results.size() == 0) &&
            (auxiliary == null || auxiliary.size() == 0) &&
            (noResAux == null || noResAux.size() == 0))
            throw new NotFoundException();

        if (stopReport(status))
            return status;

        log.log(Level.INFO, "Before fetching managers");

        if (analysisIds.size() > 0) {
            load.add(SampleManager1.Load.SINGLEANALYSIS);
            sms = sampleManager1.fetchByAnalyses(DataBaseUtil.toArrayList(analysisIds),
                                                 status,
                                                 load.toArray(new SampleManager1.Load[load.size()]));
        } else {
            sms = sampleManager1.fetchByIds(DataBaseUtil.toArrayList(sampleIds),
                                            status,
                                            load.toArray(new SampleManager1.Load[load.size()]));
        }

        log.log(Level.INFO, "Fetched " + sms.size() + " managers");

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
                         colFieldMap,
                         data,
                         smMap,
                         status);
        smMap = null;
        results = null;
        auxiliary = null;
        noResAux = null;
        testAnaResMap = null;
        auxFieldValMap = null;

        if (wb != null) {
            out = null;
            try {
                status.setMessage("Outputing report").setPercentComplete(20);
                path = ReportUtil.createTempFile("dataview", ".xls", "upload_stream_directory");

                status.setPercentComplete(100);

                out = Files.newOutputStream(path);
                wb.write(out);
                out.close();
                status.setMessage(path.getFileName().toString())
                      .setPath(path.toString())
                      .setStatus(ReportStatus.Status.SAVED);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

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
    private void buildWhereForResult(QueryBuilderV2 builder, DataView1VO data, String moduleName) throws Exception {
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

        if ("N".equals(data.getIncludeNotReportableResults()))
            builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
        builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
        builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");

        /*
         * this is done so that the alias for "sampleItem" gets added to the
         * query, otherwise the query will not execute
         */
        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());
    }

    /**
     * Creates a "where" clause from the passed arguments and sets it in the
     * passed query builder; if "moduleName" is specified, the clause from
     * security for that module for the logged in user is added to the "where"
     * clause; if the boolean flag is true it means that query is for results,
     * otherwise it's for aux data and the appropriate where
     */
    private void buildWhereForAux(QueryBuilderV2 builder, DataView1VO data, String moduleName,
                                  List<Integer> sampleIds) throws Exception {
        builder.constructWhere(data.getQueryFields());
        /*
         * if moduleName is not null, then this query is being executed for the
         * web and we need to report only released analyses
         */
        if (moduleName != null) {
            builder.addWhere("(" + getClause(moduleName) + ")");
            builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                             Constants.dictionary().ANALYSIS_RELEASED);
            builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

            /*
             * this is done so that the alias for "sampleItem" gets added to the
             * query, otherwise the query will not execute
             */
            builder.addWhere(SampleWebMeta.getItemId() + "=" +
                             SampleWebMeta.getAnalysisSampleItemId());
        }

        /*
         * the user wants to see only reportable aux data
         */
        if ("N".equals(data.getIncludeNotReportableAuxData()))
            builder.addWhere(SampleWebMeta.getAuxDataIsReportable() + "=" + "'Y'");

        builder.addWhere(SampleWebMeta.getAuxDataValue() + "!=" + "null");
        /*
         * this is done so that the alias for "auxField" gets added to the
         * query, otherwise the query will not execute
         */
        builder.addWhere(SampleWebMeta.getAuxDataAuxFieldId() + "=" +
                         SampleWebMeta.getAuxDataFieldId());

        if (sampleIds != null) {
            builder.addWhere(SampleWebMeta.getAuxDataReferenceId() + " in (" +
                             DataBaseUtil.concatWithSeparator(sampleIds, ",") + ")");
            builder.addWhere(SampleWebMeta.getAuxDataReferenceTableId() + "=" +
                             Constants.table().SAMPLE);
        }
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
                          "," + SampleWebMeta.getResultId() + "," +
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
                                                       HashSet<Integer> sampleIds, DataView1VO data) throws Exception {
        String analyteClause;
        Query query;
        ArrayList<Integer> ids, range;
        List<DataViewAuxDataFetch1VO> auxiliary;

        builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetch1VO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + "," +
                          SampleWebMeta.getAuxDataFieldAnalyteId() + ", " +
                          SampleWebMeta.getAuxDataFieldAnalyteName() + ", " +
                          SampleWebMeta.getAuxDataTypeId() + ", " +
                          SampleWebMeta.getAuxDataValue() + ") ");

        /*
         * add the clause for limiting the aux data by analytes only if the user
         * selected some specific analytes and not all of them. This eliminates
         * the unnecessary time spent on excluding those aux data from the
         * records returned by the query
         */
        analyteClause = null;
        if (unselAnalyteIds != null && unselAnalyteIds.size() > 0)
            analyteClause = getAnalyteClause(auxFieldValueMap.keySet(), unselAnalyteIds);

        /*
         * if results were fetched, limit the aux data to only the samples that
         * the results belong to
         */
        if (sampleIds != null && sampleIds.size() > 0) {
            /*
             * the list of sample ids is broken into subsets and the query is
             * executed for each subset; the "where" clause is rebuilt for each
             * subset because the sample ids are different and there's no easy
             * way to just reset them and keep the rest of the clause the same
             */
            auxiliary = new ArrayList<DataViewAuxDataFetch1VO>();
            ids = DataBaseUtil.toArrayList(sampleIds);
            range = DataBaseUtil.createSubsetRange(ids.size());
            builder.setOrderBy("");
            for (int i = 0; i < range.size() - 1; i++ ) {
                builder.clearWhereClause();
                buildWhereForAuxOutput(builder,
                                       data,
                                       moduleName,
                                       ids.subList(range.get(i), range.get(i + 1)),
                                       analyteClause);
                query = manager.createQuery(builder.getEJBQL());
                builder.setQueryParams(query, data.getQueryFields());
                auxiliary.addAll(query.getResultList());
                Collections.sort(auxiliary, new DataViewComparator());
            }
        } else {
            builder.clearWhereClause();
            buildWhereForAuxOutput(builder, data, moduleName, null, analyteClause);
            builder.setOrderBy(SampleWebMeta.getAccessionNumber() + "," +
                               SampleWebMeta.getAuxDataFieldAnalyteName());
            query = manager.createQuery(builder.getEJBQL());
            builder.setQueryParams(query, data.getQueryFields());
            auxiliary = query.getResultList();
        }

        return auxiliary;
    }

    /**
     * Creates a "where" clause from the passed arguments and sets it in the
     * passed query builder; if "moduleName" is specified, the clause from
     * security for that module for the logged in user is added to the "where"
     * clause; if the boolean flag is true it means that query is for results,
     * otherwise it's for aux data and the appropriate where
     */
    private void buildWhereForAuxOutput(QueryBuilderV2 builder, DataView1VO data,
                                        String moduleName, List<Integer> sampleIds,
                                        String analyteClause) throws Exception {
        builder.constructWhere(data.getQueryFields());
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
         * case the clause for name won't get added.
         */
        builder.addWhere(SampleWebMeta.getAuxDataFieldAnalyteName() + "!=" + "null");

        /*
         * Add the clause for limiting the aux data by analytes only if the user
         * selected some specific analytes and not all of them. This eliminates
         * the unnecessary time spent on excluding those aux data from the
         * records returned by the query
         */
        if ( !DataBaseUtil.isEmpty(analyteClause))
            builder.addWhere(SampleWebMeta.getAuxDataFieldAnalyteId() + analyteClause + ")");

        if (sampleIds != null) {
            builder.addWhere(SampleWebMeta.getAuxDataReferenceId() + " in (" +
                             DataBaseUtil.concatWithSeparator(sampleIds, ",") + ")");
            builder.addWhere(SampleWebMeta.getAuxDataReferenceTableId() + "=" +
                             Constants.table().SAMPLE);
        }
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
                                     ArrayList<String> headers,
                                     HashMap<Integer, String> colFieldMap, DataView1VO data,
                                     HashMap<Integer, SampleManager1> smMap, ReportStatus status) throws Exception {
        boolean excludeOverride, excludeRes, excludeAux, samOverridden, anaOverridden, addResRow, addAuxRow, addNoResAuxRow;
        int i, j, resIndex, auxIndex, noResAuxIndex, rowIndex, numRes, numAux, numNoResAux, startCol, lastCol, currCol;
        Integer samId, prevSamId, itemId, anaId, prevAnaId, anaIndex;
        String resVal, auxVal;
        SampleManager1 sm;
        HSSFWorkbook wb;
        HSSFSheet sheet;
        DataViewResultFetch1VO res, noRA;
        DataViewAuxDataFetch1VO aux;
        ResultViewDO rowRes, colRes;
        Row headerRow, resRow, auxRow, noResAuxRow, currRow, prevRow;
        Cell cell;
        CellStyle headerStyle;
        LabelGroup samGrp, orgGrp, itemGrp, anaGrp, envGrp, sdwisGrp, clinGrp, neoGrp, ptGrp;
        ArrayList<LabelGroup> grps;
        ArrayList<ResultViewDO> smResults;
        HashMap<String, Integer> colAnaMap;

        wb = new HSSFWorkbook();
        sheet = wb.createSheet();

        headerRow = sheet.createRow(0);
        headerStyle = createHeaderStyle(wb);
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
        lastCol = 0;
        currCol = 0;
        res = null;
        aux = null;
        noRA = null;
        rowIndex = 1;
        samId = null;
        prevSamId = null;
        itemId = null;
        anaId = null;
        prevAnaId = null;
        anaIndex = null;
        samOverridden = false;
        anaOverridden = false;
        addResRow = false;
        addAuxRow = false;
        addNoResAuxRow = false;
        resVal = null;
        auxVal = null;
        currRow = null;
        prevRow = null;
        sm = null;
        samGrp = null;
        orgGrp = null;
        itemGrp = null;
        anaGrp = null;
        envGrp = null;
        sdwisGrp = null;
        clinGrp = null;
        neoGrp = null;
        ptGrp = null;
        grps = new ArrayList<LabelGroup>();
        colAnaMap = new HashMap<String, Integer>();

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
                    samId = noRA.getSampleId();
                    itemId = noRA.getSampleItemId();
                    anaId = noRA.getAnalysisId();
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
                        samId = res.getSampleId();
                        itemId = res.getSampleItemId();
                        anaId = res.getAnalysisId();
                    } else {
                        addAuxRow = true;
                        addResRow = false;
                        auxIndex++ ;
                        samId = aux.getSampleId();
                        itemId = null;
                        anaId = null;
                    }
                } else if (resIndex < numRes) {
                    /*
                     * no more aux data left to show
                     */
                    addResRow = true;
                    addAuxRow = false;
                    res = results.get(resIndex);
                    resIndex++ ;
                    samId = res.getSampleId();
                    itemId = res.getSampleItemId();
                    anaId = res.getAnalysisId();
                } else if (auxIndex < numAux) {
                    /*
                     * no more results left to show
                     */
                    addAuxRow = true;
                    addResRow = false;
                    aux = auxiliary.get(auxIndex);
                    auxIndex++ ;
                    samId = aux.getSampleId();
                    itemId = null;
                    anaId = null;
                }
            }

            /*
             * don't show any data for this sample if it's overridden and such
             * samples are to be excluded
             */
            if ( !samId.equals(prevSamId)) {
                sm = smMap.get(samId);
                samOverridden = false;
                if (excludeOverride && (getSampleQAs(sm) != null)) {
                    for (SampleQaEventViewDO sqa : getSampleQAs(sm)) {
                        if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId())) {
                            samOverridden = true;
                            prevSamId = samId;
                            break;
                        }
                    }
                }
                if (samOverridden)
                    continue;
            } else if (samOverridden && excludeOverride) {
                continue;
            }

            if (addResRow) {
                /*
                 * don't show any data for this analysis if it's overridden and
                 * such analyses are to be excluded
                 */
                if ( !anaId.equals(prevAnaId)) {
                    anaOverridden = false;
                    if (excludeOverride && (getAnalysisQAs(sm) != null)) {
                        for (AnalysisQaEventViewDO aqa : getAnalysisQAs(sm)) {
                            if (aqa.getAnalysisId().equals(anaId) &&
                                Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId())) {
                                anaOverridden = true;
                                addResRow = false;
                                prevAnaId = anaId;
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
                 * the value of this result was selected by the user; add a row
                 * for it
                 */
                resVal = getValue(testAnaResMap,
                                  res.getAnalyteId(),
                                  res.getValue(),
                                  res.getTypeId());
                if (resVal != null)
                    currRow = resRow = sheet.createRow(rowIndex++ );
                else
                    addResRow = false;
            }

            auxRow = null;
            if (addAuxRow) {
                /*
                 * the value of this aux data was selected by the user; add a
                 * row for it
                 */
                auxVal = getValue(auxFieldValMap,
                                  aux.getAuxFieldAnalyteId(),
                                  aux.getValue(),
                                  aux.getTypeId());
                if (auxVal != null)
                    currRow = auxRow = sheet.createRow(rowIndex++ );
                else
                    addAuxRow = false;
            }

            noResAuxRow = null;
            if (addNoResAuxRow)
                currRow = noResAuxRow = sheet.createRow(rowIndex++ );

            if ( !addResRow && !addAuxRow && !addNoResAuxRow)
                continue;

            startCol = 0;
            /*
             * set the labels for sample fields
             */
            if (samGrp == null) {
                samGrp = new LabelGroup();
                grps.add(samGrp);
            }
            setSampleLabels(samGrp, samId, smMap, colFieldMap, startCol);

            startCol += samGrp.labels.size();
            /*
             * set the labels for organization fields
             */
            if (orgGrp == null) {
                orgGrp = new LabelGroup();
                grps.add(orgGrp);
            }
            setOrganizationLabels(orgGrp, samId, smMap, colFieldMap, startCol);

            startCol += orgGrp.labels.size();
            /*
             * set the labels for sample item fields
             */
            if (itemGrp == null) {
                itemGrp = new LabelGroup();
                grps.add(itemGrp);
            }
            setSampleItemLabels(itemGrp, samId, itemId, smMap, colFieldMap, startCol);

            startCol += itemGrp.labels.size();
            /*
             * set the labels for analysis fields
             */
            if (anaGrp == null) {
                anaGrp = new LabelGroup();
                grps.add(anaGrp);
            }
            setAnalysisLabels(anaGrp, samId, anaId, smMap, colFieldMap, startCol, moduleName);

            startCol += anaGrp.labels.size();
            /*
             * set the labels for environmental fields
             */
            if (envGrp == null) {
                envGrp = new LabelGroup();
                grps.add(envGrp);
            }
            setEnvironmentalLabels(envGrp, samId, smMap, colFieldMap, startCol);

            startCol += envGrp.labels.size();
            /*
             * set the labels for sdwis fields
             */
            if (sdwisGrp == null) {
                sdwisGrp = new LabelGroup();
                grps.add(sdwisGrp);
            }
            setSDWISLabels(sdwisGrp, samId, smMap, colFieldMap, startCol);

            startCol += sdwisGrp.labels.size();
            /*
             * set the labels for clinical fields
             */
            if (clinGrp == null) {
                clinGrp = new LabelGroup();
                grps.add(clinGrp);
            }
            setClinicalLabels(clinGrp, samId, smMap, colFieldMap, startCol);

            startCol += clinGrp.labels.size();
            /*
             * set the labels for neonatal fields
             */
            if (neoGrp == null) {
                neoGrp = new LabelGroup();
                grps.add(neoGrp);
            }
            setNeonatalLabels(neoGrp, samId, smMap, colFieldMap, startCol);

            startCol += neoGrp.labels.size();
            /*
             * set the labels for pt fields
             */
            if (ptGrp == null) {
                ptGrp = new LabelGroup();
                grps.add(ptGrp);
            }
            setPTLabels(ptGrp, samId, smMap, colFieldMap, startCol);

            if (addResRow) {
                setCells(resRow, grps);
                /*
                 * set the analyte's name and the result's value
                 */
                cell = resRow.createCell(resRow.getPhysicalNumberOfCells());
                cell.setCellValue(res.getAnalyteName());
                cell = resRow.createCell(resRow.getPhysicalNumberOfCells());

                /*
                 * results are not shown if the analysis or sample is overridden
                 */
                if ( !anaOverridden && !samOverridden)
                    cell.setCellValue(resVal);

                /*
                 * find out if this analyte has any column analytes; if it does,
                 * add the analytes to the header and their values in the
                 * columns; if an analyte B is found first, it's added to the
                 * header before another analyte A even if A's column appears to
                 * the left of B's in any test
                 */
                smResults = getResults(sm);
                for (i = 0; i < smResults.size(); i++ ) {
                    rowRes = smResults.get(i);
                    if ( !res.getId().equals(rowRes.getId()))
                        continue;
                    j = i + 1;
                    if (j < smResults.size() && "Y".equals(smResults.get(j).getIsColumn())) {
                        /*
                         * this analyte has column analytes; "lastCol" is the
                         * right-most column in the output; if an analyte
                         * doesn't have have a column yet, that column will be
                         * added after "lastCol"; "currCol" keeps track of the
                         * current column
                         */
                        if (lastCol == 0)
                            lastCol = resRow.getPhysicalNumberOfCells();
                        currCol = resRow.getPhysicalNumberOfCells();
                        while (j < smResults.size()) {
                            colRes = smResults.get(j++ );
                            if ("N".equals(colRes.getIsColumn()))
                                break;
                            if (showReportableColumnsOnly && "N".equals(colRes.getIsReportable()))
                                continue;
                            anaIndex = colAnaMap.get(colRes.getAnalyte());

                            /*
                             * if this column analyte name is not found in the
                             * map, create a new column and start adding values
                             * in it; set the value in this cell if the analyte
                             * is shown in this column; if the analyte is not
                             * shown in this column, set the value in the
                             * appropriate column
                             */
                            if (anaIndex == null) {
                                anaIndex = lastCol++ ;
                                colAnaMap.put(colRes.getAnalyte(), anaIndex);
                                cell = headerRow.createCell(anaIndex);
                                cell.setCellValue(colRes.getAnalyte());
                                cell.setCellStyle(headerStyle);
                                cell = resRow.createCell(anaIndex);
                            } else if (anaIndex == currCol) {
                                cell = resRow.createCell(currCol++ );
                            } else {
                                cell = resRow.createCell(anaIndex);
                            }

                            /*
                             * results are not shown if the analysis or sample
                             * is overridden
                             */
                            if ( !anaOverridden && !samOverridden)
                                cell.setCellValue(getValue(colRes.getValue(), colRes.getTypeId()));
                        }
                    }
                }
            }

            if (addAuxRow) {
                setCells(auxRow, grps);
                /*
                 * set the analyte's name and the aux data's value
                 */
                cell = auxRow.createCell(auxRow.getPhysicalNumberOfCells());
                cell.setCellValue(aux.getAuxFieldAnalyteName());
                cell = auxRow.createCell(auxRow.getPhysicalNumberOfCells());
                cell.setCellValue(auxVal);
            }

            if (addNoResAuxRow)
                setCells(noResAuxRow, grps);

            prevAnaId = anaId;
            prevSamId = samId;

            /*
             * An empty row can't be created and then added it to the sheet, it
             * has to be obtained from the sheet. Thus it has to be removed if
             * we don't want to show it. We do so if two consecutive rows have
             * the same data in all cells. It can happen if, for example, a user
             * chose to see sample items but all the ones under a sample have
             * the same container and sample type and those were the only fields
             * chosen to be shown.
             */
            if (isSameDataInRows(currRow, prevRow)) {
                sheet.removeRow(currRow);
                rowIndex-- ;
            } else {
                prevRow = currRow;
            }
        }

        /*
         * make each column wide enough to show the longest string in it
         */
        // for (i = 0; i < headerRow.getPhysicalNumberOfCells(); i++ )
        // sheet.autoSizeColumn(i);

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

    /**
     * Creates the style to distinguish the header row from the other rows in
     * the output
     */
    private CellStyle createHeaderStyle(HSSFWorkbook wb) {
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

    private ArrayList<String> getSampleHeaders(ArrayList<String> columns, int startCol,
                                               HashMap<Integer, String> colFieldMap,
                                               ArrayList<SampleManager1.Load> load) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    /*
                     * fetch projects only if they'll be shown in the output
                     */
                    load.add(SampleManager1.Load.PROJECT);
                    break;
                case SampleWebMeta.CLIENT_REFERENCE_HEADER:
                    headers.add(Messages.get().sample_clntRef());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        return headers;
    }

    private ArrayList<String> getOrganizationHeaders(ArrayList<String> columns, int startCol,
                                                     HashMap<Integer, String> colFieldMap,
                                                     ArrayList<SampleManager1.Load> load) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        /*
         * fetch organizations only if they'll be shown in the output
         */
        if (headers.size() > 0)
            load.add(SampleManager1.Load.ORGANIZATION);

        return headers;
    }

    private ArrayList<String> getSampleItemHeaders(ArrayList<String> columns, int startCol,
                                                   HashMap<Integer, String> colFieldMap) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        return headers;
    }

    private ArrayList<String> getAnalysisHeaders(ArrayList<String> columns, int startCol,
                                                 HashMap<Integer, String> colFieldMap,
                                                 ArrayList<SampleManager1.Load> load) {
        boolean allSet, fetchUser;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        fetchUser = true;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                case SampleWebMeta.ANALYSIS_REVISION:
                    headers.add(Messages.get().analysis_revision());
                    break;
                case SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER:
                    headers.add(Messages.get().gen_reportable());
                    break;
                case SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID:
                    headers.add(Messages.get().gen_unit());
                    break;
                case SampleWebMeta.ANALYSISSUBQA_NAME:
                    headers.add(Messages.get().qaEvent_qaEvent());
                    load.add(SampleManager1.Load.QA);
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_DATE:
                    headers.add(Messages.get().gen_completedDate());
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_BY:
                    headers.add(Messages.get().dataView_completedBy());
                    fetchUser = true;
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_DATE:
                    headers.add(Messages.get().analysis_releasedDate());
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_BY:
                    headers.add(Messages.get().dataView_releasedBy());
                    fetchUser = true;
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
                case SampleWebMeta.ANALYSIS_TYPE_ID:
                    headers.add(Messages.get().analysis_type());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        if (fetchUser)
            load.add(SampleManager1.Load.ANALYSISUSER);

        return headers;
    }

    private ArrayList<String> getEnvironmentalHeaders(ArrayList<String> columns, int startCol,
                                                      HashMap<Integer, String> colFieldMap) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
            switch (c) {
                case SampleWebMeta.ENV_IS_HAZARDOUS:
                    headers.add(Messages.get().sampleEnvironmental_hazardous());
                    break;
                case SampleWebMeta.ENV_PRIORITY:
                    headers.add(Messages.get().gen_priority());
                    break;
                case SampleWebMeta.ENV_COLLECTOR:
                    headers.add(Messages.get().env_collector());
                    break;
                case SampleWebMeta.ENV_COLLECTOR_PHONE:
                    headers.add(Messages.get().address_phone());
                    break;
                case SampleWebMeta.ENV_DESCRIPTION:
                    headers.add(Messages.get().sample_description());
                    break;
                case SampleWebMeta.ENV_LOCATION:
                    headers.add(Messages.get().env_location());
                    break;
                case SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().dataView_locationAptSuite());
                    break;
                case SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().dataView_locationAddress());
                    break;
                case SampleWebMeta.LOCATION_ADDR_CITY:
                    headers.add(Messages.get().dataView_locationCity());
                    break;
                case SampleWebMeta.LOCATION_ADDR_STATE:
                    headers.add(Messages.get().dataView_locationState());
                    break;
                case SampleWebMeta.LOCATION_ADDR_ZIP_CODE:
                    headers.add(Messages.get().dataView_locationZipCode());
                    break;
                case SampleWebMeta.LOCATION_ADDR_COUNTRY:
                    headers.add(Messages.get().dataView_locationCountry());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        return headers;
    }

    private ArrayList<String> getSDWISHeaders(ArrayList<String> columns, int startCol,
                                              HashMap<Integer, String> colFieldMap) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        return headers;
    }

    private ArrayList<String> getClinicalHeaders(ArrayList<String> columns, int startCol,
                                                 HashMap<Integer, String> colFieldMap,
                                                 ArrayList<SampleManager1.Load> load) {
        boolean allSet, fetchProv;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        fetchProv = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    fetchProv = true;
                    break;
                case SampleWebMeta.CLIN_PROVIDER_FIRST_NAME:
                    headers.add(Messages.get().provider_firstName());
                    fetchProv = true;
                    break;
                case SampleWebMeta.CLIN_PROVIDER_PHONE:
                    headers.add(Messages.get().dataView_providerPhone());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        /*
         * fetch providers only if they'll be shown in the output
         */
        if (fetchProv && !load.contains(SampleManager1.Load.PROVIDER))
            load.add(SampleManager1.Load.PROVIDER);

        return headers;
    }

    private ArrayList<String> getNeonatalHeaders(ArrayList<String> columns, int startCol,
                                                 HashMap<Integer, String> colFieldMap,
                                                 ArrayList<SampleManager1.Load> load) {
        boolean allSet, fetchProv;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        fetchProv = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    fetchProv = true;
                    break;
                case SampleWebMeta.NEO_PROVIDER_FIRST_NAME:
                    headers.add(Messages.get().provider_firstName());
                    fetchProv = true;
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFieldMap.put(i, c);
        }

        /*
         * fetch providers only if they'll be shown in the output
         */
        if (fetchProv && !load.contains(SampleManager1.Load.PROVIDER))
            load.add(SampleManager1.Load.PROVIDER);
        return headers;
    }

    private ArrayList<String> getPTHeaders(ArrayList<String> columns, int startCol,
                                           HashMap<Integer, String> colFields) {
        boolean allSet;
        String c;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        allSet = false;
        for (int i = startCol; i < columns.size(); i++ ) {
            c = columns.get(i);
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
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
            colFields.put(i, c);
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

    /**
     * The passed map's key is an analyte and the value is all the results or
     * aux data selected by the user for the analyte; the method checks whether
     * the passed value was selected by the user; if it was, returns the value
     * or the dictionary entry for it, based on the type; otherwise returns null
     */
    private String getValue(HashMap<Integer, HashSet<String>> analyteValMap, Integer analyteId,
                            String value, Integer typeId) throws Exception {
        String val;
        HashSet<String> valSet;

        valSet = analyteValMap.get(analyteId);
        if (valSet == null)
            return null;

        val = getValue(value, typeId);
        if ( !valSet.contains(val))
            return null;

        return val;
    }

    private String getValue(String value, Integer typeId) throws Exception {
        Integer id;

        if (DataBaseUtil.isEmpty(value))
            return null;
        if (Constants.dictionary().AUX_DICTIONARY.equals(typeId) ||
            Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(typeId)) {
            id = Integer.parseInt(value);
            value = dictionaryCache.getById(id).getEntry();
        }

        return value;
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the manager from the passed map and sets the labels for the
     * sample fields in the group; "colFieldMap" is used to find out which field
     * is showing in which column; doesn't do anything if the sample id is the
     * same as the id in the group
     */
    private void setSampleLabels(LabelGroup grp, Integer sampleId,
                                 HashMap<Integer, SampleManager1> smMap,
                                 HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field, name;
        SampleDO s;
        SampleManager1 sm;
        Date cd;
        Datetime ct, cdt;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;

        sm = smMap.get(sampleId);
        s = getSample(sm);
        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.ACCESSION_NUMBER:
                    setNumberLabel(grp, s.getAccessionNumber());
                    break;
                case SampleWebMeta.REVISION:
                    setNumberLabel(grp, s.getRevision());
                    break;
                case SampleWebMeta.COLLECTION_DATE:
                    cdt = null;
                    /*
                     * set the combination of collection date and time
                     */
                    if (s.getCollectionDate() != null) {
                        cd = s.getCollectionDate().getDate();
                        ct = s.getCollectionTime();
                        if (ct == null) {
                            cd.setHours(0);
                            cd.setMinutes(0);
                        } else {
                            cd.setHours(ct.getDate().getHours());
                            cd.setMinutes(ct.getDate().getMinutes());
                        }

                        cdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, cd);
                    }
                    setDateTimeLabel(grp, cdt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.RECEIVED_DATE:
                    setDateTimeLabel(grp, s.getReceivedDate(), Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ENTERED_DATE:
                    setDateTimeLabel(grp, s.getEnteredDate(), Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.RELEASED_DATE:
                    setDateTimeLabel(grp, s.getReleasedDate(), Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.STATUS_ID:
                    setDictionaryLabel(grp, s.getStatusId());
                    break;
                case SampleWebMeta.PROJECT_NAME:
                    /*
                     * set the name of the first permanent project
                     */
                    name = null;
                    if (getProjects(sm) != null) {
                        for (SampleProjectViewDO data : getProjects(sm)) {
                            if ("Y".equals(data.getIsPermanent())) {
                                name = data.getProjectName();
                                break;
                            }
                        }
                    }
                    setLabel(grp, name);
                    break;
                case SampleWebMeta.CLIENT_REFERENCE_HEADER:
                    setLabel(grp, s.getClientReference());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the manager from the passed map and sets the labels for the
     * organization fields in the group; "colFieldMap" is used to find out which
     * field is showing in which column; doesn't do anything if the sample id is
     * the same as the id in the group
     */
    private void setOrganizationLabels(LabelGroup grp, Integer sampleId,
                                       HashMap<Integer, SampleManager1> smMap,
                                       HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleOrganizationViewDO so;
        SampleManager1 sm;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;

        sm = smMap.get(sampleId);
        /*
         * find the report-to organization
         */
        so = null;
        if (getOrganizations(sm) != null) {
            for (SampleOrganizationViewDO data : getOrganizations(sm)) {
                if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId())) {
                    so = data;
                    break;
                }
            }
        }

        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.SAMPLE_ORG_ID:
                    setNumberLabel(grp, so != null ? so.getOrganizationId() : null);
                    break;
                case SampleWebMeta.ORG_NAME:
                    setLabel(grp, so != null ? so.getOrganizationName() : null);
                    break;
                case SampleWebMeta.SAMPLE_ORG_ATTENTION:
                    setLabel(grp, so != null ? so.getOrganizationAttention() : null);
                    break;
                case SampleWebMeta.ADDR_MULTIPLE_UNIT:
                    setLabel(grp, so != null ? so.getOrganizationMultipleUnit() : null);
                    break;
                case SampleWebMeta.ADDR_STREET_ADDRESS:
                    setLabel(grp, so != null ? so.getOrganizationStreetAddress() : null);
                    break;
                case SampleWebMeta.ADDR_CITY:
                    setLabel(grp, so != null ? so.getOrganizationCity() : null);
                    break;
                case SampleWebMeta.ADDR_STATE:
                    setLabel(grp, so != null ? so.getOrganizationState() : null);
                    break;
                case SampleWebMeta.ADDR_ZIP_CODE:
                    setLabel(grp, so != null ? so.getOrganizationZipCode() : null);
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample item id is different from the id in the passed label
     * group, gets the sample and item from the passed map and sets the labels
     * for the item fields in the passed label group; "colFieldMap" is used to
     * find out which field is showing in which column; doesn't do anything if
     * the sample item id is the same as the id in the group; if item id is null
     * (for aux data rows), adds empty labels for each field
     */
    private void setSampleItemLabels(LabelGroup grp, Integer sampleId, Integer itemId,
                                     HashMap<Integer, SampleManager1> smMap,
                                     HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleItemViewDO item;
        SampleManager1 sm;

        if (itemId != null && itemId.equals(grp.id))
            return;

        grp.id = itemId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        item = null;

        if (itemId != null) {
            sm = smMap.get(sampleId);
            /*
             * find the item with the passed id
             */
            for (SampleItemViewDO data : getItems(sm)) {
                if (data.getId().equals(itemId)) {
                    item = data;
                    break;
                }
            }
        }

        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID:
                    setLabel(grp, item != null ? item.getTypeOfSample() : null);
                    break;
                case SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID:
                    setLabel(grp, item != null ? item.getSourceOfSample() : null);
                    break;
                case SampleWebMeta.ITEM_SOURCE_OTHER:
                    setLabel(grp, item != null ? item.getSourceOther() : null);
                    break;
                case SampleWebMeta.ITEM_CONTAINER_ID:
                    setLabel(grp, item != null ? item.getContainer() : null);
                    break;
                case SampleWebMeta.ITEM_CONTAINER_REFERENCE:
                    setLabel(grp, item != null ? item.getContainerReference() : null);
                    break;
                case SampleWebMeta.ITEM_ITEM_SEQUENCE:
                    setNumberLabel(grp, item != null ? item.getItemSequence() : null);
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample item id is different from the id in the passed label
     * group, gets the sample and item from the passed map and sets the labels
     * for the item fields in the passed label group; "colFieldMap" is used to
     * find out which field is showing in which column; doesn't do anything if
     * the sample item id is the same as the id in the group; if item id is null
     * (for aux data rows), adds empty labels for each field
     */
    private void setAnalysisLabels(LabelGroup grp, Integer sampleId, Integer anaId,
                                   HashMap<Integer, SampleManager1> smMap,
                                   HashMap<Integer, String> colFieldMap, int startCol,
                                   String moduleName) throws Exception {
        boolean allSet, runForWeb;
        String field, test, method, rep, comp, rel, qa;
        ArrayList<String> labels;
        AnalysisViewDO ana;
        SampleManager1 sm;
        Datetime dt;

        if (anaId != null && anaId.equals(grp.id))
            return;

        grp.id = anaId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        ana = null;
        comp = null;
        rel = null;
        qa = null;
        runForWeb = moduleName != null;

        if (anaId != null) {
            sm = smMap.get(sampleId);
            /*
             * find the analysis with the passed id
             */
            for (AnalysisViewDO data : getAnalyses(sm)) {
                if (data.getId().equals(anaId)) {
                    ana = data;
                    break;
                }
            }
            /*
             * find the names of the users who completed and/or released the
             * analysis
             */
            if (getUsers(sm) != null) {
                labels = new ArrayList<String>();
                for (AnalysisUserViewDO data : getUsers(sm)) {
                    if ( !data.getAnalysisId().equals(anaId))
                        continue;
                    if (Constants.dictionary().AN_USER_AC_COMPLETED.equals(data.getActionId()))
                        labels.add(data.getSystemUser());
                    else if (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId()))
                        rel = data.getSystemUser();
                }
                comp = DataBaseUtil.concatWithSeparator(labels, ", ");
            }

            /*
             * find the qa events for the analysis; if the report is run for
             * external clients, internal qa events are not shown and the qa
             * event's reporting text is shown, otherwise its name is shown
             */
            if (getAnalysisQAs(sm) != null) {
                labels = new ArrayList<String>();
                for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                    if ( !data.getAnalysisId().equals(anaId))
                        continue;
                    if (runForWeb &&
                        !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId()))
                        labels.add(data.getQaEventReportingText());
                    else
                        labels.add(data.getQaEventName());
                }
                qa = DataBaseUtil.concatWithSeparator(labels, runForWeb ? " " : ", ");
            }
        }

        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.ANALYSIS_ID:
                    setNumberLabel(grp, ana != null ? ana.getId() : null);
                    break;
                case SampleWebMeta.ANALYSIS_TEST_NAME_HEADER:
                    test = null;
                    if (ana != null)
                        test = runForWeb ? ana.getTestReportingDescription() : ana.getTestName();
                    setLabel(grp, test);
                    break;
                case SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER:
                    method = null;
                    if (ana != null)
                        method = runForWeb ? ana.getMethodReportingDescription()
                                          : ana.getMethodName();
                    setLabel(grp, method);
                    break;
                case SampleWebMeta.ANALYSIS_STATUS_ID_HEADER:
                    setDictionaryLabel(grp, ana != null ? ana.getStatusId() : null);
                    break;
                case SampleWebMeta.ANALYSIS_REVISION:
                    setNumberLabel(grp, ana != null ? ana.getRevision() : null);
                    break;
                case SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER:
                    rep = null;
                    if (ana != null)
                        rep = "Y".equals(ana.getIsReportable()) ? Messages.get().gen_yes()
                                                               : Messages.get().gen_no();
                    setLabel(grp, rep);
                    break;
                case SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID:
                    setDictionaryLabel(grp, ana != null ? ana.getUnitOfMeasureId() : null);
                    break;
                case SampleWebMeta.ANALYSISSUBQA_NAME:
                    setLabel(grp, qa);
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_DATE:
                    dt = ana != null ? ana.getCompletedDate() : null;
                    setDateTimeLabel(grp, dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_BY:
                    setLabel(grp, comp);
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_DATE:
                    dt = ana != null ? ana.getReleasedDate() : null;
                    setDateTimeLabel(grp, dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_BY:
                    setLabel(grp, rel);
                    break;
                case SampleWebMeta.ANALYSIS_STARTED_DATE:
                    dt = ana != null ? ana.getStartedDate() : null;
                    setDateTimeLabel(grp, dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_PRINTED_DATE:
                    dt = ana != null ? ana.getPrintedDate() : null;
                    setDateTimeLabel(grp, dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_SECTION_NAME:
                    setLabel(grp, ana != null ? ana.getSectionName() : null);
                    break;
                case SampleWebMeta.ANALYSIS_TYPE_ID:
                    setDictionaryLabel(grp, ana != null ? ana.getTypeId() : null);
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the sample from the passed map and sets the labels for the
     * environmental fields in the passed label group; "colFieldMap" is used to
     * find out which field is showing in which column; doesn't do anything if
     * the sample id is the same as the id in the group
     */
    private void setEnvironmentalLabels(LabelGroup grp, Integer sampleId,
                                        HashMap<Integer, SampleManager1> smMap,
                                        HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleEnvironmentalDO se;
        SampleManager1 sm;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        sm = smMap.get(sampleId);
        se = getSampleEnvironmental(sm);
        if (se == null)
            return;
        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.ENV_IS_HAZARDOUS:
                    setLabel(grp, "Y".equals(se.getIsHazardous()) ? Messages.get().gen_yes()
                                                                 : Messages.get().gen_no());
                    break;
                case SampleWebMeta.ENV_PRIORITY:
                    setNumberLabel(grp, se.getPriority());
                    break;
                case SampleWebMeta.ENV_COLLECTOR:
                    setLabel(grp, se.getCollector());
                    break;
                case SampleWebMeta.ENV_COLLECTOR_PHONE:
                    setLabel(grp, se.getCollectorPhone());
                    break;
                case SampleWebMeta.ENV_DESCRIPTION:
                    setLabel(grp, se.getDescription());
                    break;
                case SampleWebMeta.ENV_LOCATION:
                    setLabel(grp, se.getLocation());
                    break;
                case SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                    setLabel(grp, se.getLocationAddress().getMultipleUnit());
                    break;
                case SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS:
                    setLabel(grp, se.getLocationAddress().getStreetAddress());
                    break;
                case SampleWebMeta.LOCATION_ADDR_CITY:
                    setLabel(grp, se.getLocationAddress().getCity());
                    break;
                case SampleWebMeta.LOCATION_ADDR_STATE:
                    setLabel(grp, se.getLocationAddress().getState());
                    break;
                case SampleWebMeta.LOCATION_ADDR_ZIP_CODE:
                    setLabel(grp, se.getLocationAddress().getZipCode());
                    break;
                case SampleWebMeta.LOCATION_ADDR_COUNTRY:
                    setLabel(grp, se.getLocationAddress().getCountry());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the sample from the passed map and sets the labels for the
     * sdwis fields in the passed label group; "colFieldMap" is used to find out
     * which field is showing in which column; doesn't do anything if the sample
     * id is the same as the id in the group
     */
    private void setSDWISLabels(LabelGroup grp, Integer sampleId,
                                HashMap<Integer, SampleManager1> smMap,
                                HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleSDWISViewDO ss;
        SampleManager1 sm;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        sm = smMap.get(sampleId);
        ss = getSampleSDWIS(sm);
        if (ss == null)
            return;

        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.SDWIS_PWS_ID:
                    setLabel(grp, ss.getPwsNumber0());
                    break;
                case SampleWebMeta.PWS_NAME:
                    setLabel(grp, ss.getPwsName());
                    break;
                case SampleWebMeta.SDWIS_STATE_LAB_ID:
                    setNumberLabel(grp, ss.getStateLabId());
                    break;
                case SampleWebMeta.SDWIS_FACILITY_ID:
                    setLabel(grp, ss.getFacilityId());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_TYPE_ID:
                    setDictionaryLabel(grp, ss.getSampleTypeId());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID:
                    setDictionaryLabel(grp, ss.getSampleCategoryId());
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_POINT_ID:
                    setLabel(grp, ss.getSamplePointId());
                    break;
                case SampleWebMeta.SDWIS_LOCATION:
                    setLabel(grp, ss.getLocation());
                    break;
                case SampleWebMeta.SDWIS_PRIORITY:
                    setNumberLabel(grp, ss.getPriority());
                    break;
                case SampleWebMeta.SDWIS_COLLECTOR:
                    setLabel(grp, ss.getCollector());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the sample from the passed map and sets the labels for the
     * clinical fields in the passed label group; "colFieldMap" is used to find
     * out which field is showing in which column; doesn't do anything if the
     * sample id is the same as the id in the group
     */
    private void setClinicalLabels(LabelGroup grp, Integer sampleId,
                                   HashMap<Integer, SampleManager1> smMap,
                                   HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleClinicalViewDO sc;
        ProviderDO pr;
        SampleManager1 sm;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        sm = smMap.get(sampleId);
        sc = getSampleClinical(sm);
        if (sc == null)
            return;
        pr = sc.getProvider();

        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.CLIN_PATIENT_ID:
                    setNumberLabel(grp, sc.getPatientId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_LAST_NAME:
                    setLabel(grp, sc.getPatient().getLastName());
                    break;
                case SampleWebMeta.CLIN_PATIENT_FIRST_NAME:
                    setLabel(grp, sc.getPatient().getFirstName());
                    break;
                case SampleWebMeta.CLIN_PATIENT_BIRTH_DATE:
                    setDateTimeLabel(grp,
                                     sc.getPatient().getBirthDate(),
                                     Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.CLIN_PATIENT_NATIONAL_ID:
                    setLabel(grp, sc.getPatient().getNationalId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT:
                    setLabel(grp, sc.getPatient().getAddress().getMultipleUnit());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS:
                    setLabel(grp, sc.getPatient().getAddress().getStreetAddress());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_CITY:
                    setLabel(grp, sc.getPatient().getAddress().getCity());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STATE:
                    setLabel(grp, sc.getPatient().getAddress().getState());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE:
                    setLabel(grp, sc.getPatient().getAddress().getZipCode());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE:
                    setLabel(grp, sc.getPatient().getAddress().getHomePhone());
                    break;
                case SampleWebMeta.CLIN_PATIENT_GENDER_ID:
                    setDictionaryLabel(grp, sc.getPatient().getGenderId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_RACE_ID:
                    setDictionaryLabel(grp, sc.getPatient().getRaceId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID:
                    setDictionaryLabel(grp, sc.getPatient().getEthnicityId());
                    break;
                case SampleWebMeta.CLIN_PROVIDER_LAST_NAME:
                    setLabel(grp, pr != null ? sc.getProvider().getLastName() : null);
                    break;
                case SampleWebMeta.CLIN_PROVIDER_FIRST_NAME:
                    setLabel(grp, pr != null ? sc.getProvider().getFirstName() : null);
                    break;
                case SampleWebMeta.CLIN_PROVIDER_PHONE:
                    setLabel(grp, sc.getProviderPhone());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the sample from the passed map and sets the labels for the
     * neonatal fields in the passed label group; "colFieldMap" is used to find
     * out which field is showing in which column; doesn't do anything if the
     * sample id is the same as the id in the group
     */
    private void setNeonatalLabels(LabelGroup grp, Integer sampleId,
                                   HashMap<Integer, SampleManager1> smMap,
                                   HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SampleNeonatalViewDO sc;
        SampleManager1 sm;
        Date bd;
        Datetime bt, bdt;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        sm = smMap.get(sampleId);
        sc = getSampleNeonatal(sm);
        if (sc == null)
            return;
        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.NEO_PATIENT_ID:
                    setNumberLabel(grp, sc.getPatientId());
                    break;
                case SampleWebMeta.NEO_PATIENT_LAST_NAME:
                    setLabel(grp, sc.getPatient().getLastName());
                    break;
                case SampleWebMeta.NEO_PATIENT_FIRST_NAME:
                    setLabel(grp, sc.getPatient().getFirstName());
                    break;
                case SampleWebMeta.NEO_PATIENT_BIRTH_DATE:
                    bdt = null;
                    /*
                     * set the combination of birth date and time
                     */
                    if (sc.getPatient().getBirthDate() != null) {
                        bd = sc.getPatient().getBirthDate().getDate();
                        bt = sc.getPatient().getBirthTime();
                        if (bt == null) {
                            bd.setHours(0);
                            bd.setMinutes(0);
                        } else {
                            bd.setHours(bt.getDate().getHours());
                            bd.setMinutes(bt.getDate().getMinutes());
                        }

                        bdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, bd);
                    }
                    setDateTimeLabel(grp, bdt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT:
                    setLabel(grp, sc.getPatient().getAddress().getMultipleUnit());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS:
                    setLabel(grp, sc.getPatient().getAddress().getStreetAddress());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_CITY:
                    setLabel(grp, sc.getPatient().getAddress().getCity());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STATE:
                    setLabel(grp, sc.getPatient().getAddress().getState());
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE:
                    setLabel(grp, sc.getPatient().getAddress().getZipCode());
                    break;
                case SampleWebMeta.NEO_PATIENT_GENDER_ID:
                    setDictionaryLabel(grp, sc.getPatient().getGenderId());
                    break;
                case SampleWebMeta.NEO_PATIENT_RACE_ID:
                    setDictionaryLabel(grp, sc.getPatient().getRaceId());
                    break;
                case SampleWebMeta.NEO_PATIENT_ETHNICITY_ID:
                    setDictionaryLabel(grp, sc.getPatient().getEthnicityId());
                    break;
                case SampleWebMeta.NEO_IS_NICU:
                    setLabel(grp, sc.getIsNicu());
                    break;
                case SampleWebMeta.NEO_BIRTH_ORDER:
                    setNumberLabel(grp, sc.getBirthOrder());
                    break;
                case SampleWebMeta.NEO_GESTATIONAL_AGE:
                    setNumberLabel(grp, sc.getGestationalAge());
                    break;
                case SampleWebMeta.NEO_FEEDING_ID:
                    setDictionaryLabel(grp, sc.getFeedingId());
                    break;
                case SampleWebMeta.NEO_WEIGHT:
                    setDictionaryLabel(grp, sc.getWeight());
                    break;
                case SampleWebMeta.NEO_IS_TRANSFUSED:
                    setLabel(grp, "Y".equals(sc.getIsTransfused()) ? Messages.get().gen_yes()
                                                                  : Messages.get().gen_no());
                    break;
                case SampleWebMeta.NEO_TRANSFUSION_DATE:
                    setDateTimeLabel(grp, sc.getTransfusionDate(), Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.NEO_IS_REPEAT:
                    setLabel(grp, "Y".equals(sc.getIsRepeat()) ? Messages.get().gen_yes()
                                                              : Messages.get().gen_no());
                    break;
                case SampleWebMeta.NEO_COLLECTION_AGE:
                    setNumberLabel(grp, sc.getCollectionAge());
                    break;
                case SampleWebMeta.NEO_IS_COLLECTION_VALID:
                    setLabel(grp, "Y".equals(sc.getIsCollectionValid()) ? Messages.get().gen_yes()
                                                                       : Messages.get().gen_no());
                    break;
                case SampleWebMeta.NEO_FORM_NUMBER:
                    setLabel(grp, sc.getFormNumber());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ID:
                    setNumberLabel(grp, sc.getNextOfKinId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME:
                    setLabel(grp, sc.getNextOfKin().getLastName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME:
                    setLabel(grp, sc.getNextOfKin().getMiddleName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME:
                    setLabel(grp, sc.getNextOfKin().getFirstName());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID:
                    setDictionaryLabel(grp, sc.getNextOfKinRelationId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE:
                    setDateTimeLabel(grp,
                                     sc.getNextOfKin().getBirthDate(),
                                     Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID:
                    setLabel(grp, sc.getNextOfKin().getNationalId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT:
                    setLabel(grp, sc.getNextOfKin().getAddress().getMultipleUnit());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS:
                    setLabel(grp, sc.getPatient().getAddress().getStreetAddress());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY:
                    setLabel(grp, sc.getPatient().getAddress().getCity());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE:
                    setLabel(grp, sc.getPatient().getAddress().getState());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE:
                    setLabel(grp, sc.getPatient().getAddress().getZipCode());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID:
                    setDictionaryLabel(grp, sc.getNextOfKin().getGenderId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID:
                    setDictionaryLabel(grp, sc.getNextOfKin().getRaceId());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID:
                    setDictionaryLabel(grp, sc.getNextOfKin().getEthnicityId());
                    break;
                case SampleWebMeta.NEO_PROVIDER_LAST_NAME:
                    setLabel(grp, sc.getProvider().getLastName());
                    break;
                case SampleWebMeta.NEO_PROVIDER_FIRST_NAME:
                    setLabel(grp, sc.getProvider().getFirstName());
                    break;
                default:
                    allSet = true;
                    break;
            }
            if (allSet)
                break;
        }
    }

    /**
     * If the passed sample id is different from the id in the passed label
     * group, gets the sample from the passed map and sets the labels for the pt
     * fields in the passed label group; "colFieldMap" is used to find out which
     * field is showing in which column; doesn't do anything if the sample id is
     * the same as the id in the group
     */
    private void setPTLabels(LabelGroup grp, Integer sampleId,
                             HashMap<Integer, SampleManager1> smMap,
                             HashMap<Integer, String> colFieldMap, int startCol) throws Exception {
        boolean allSet;
        String field;
        SamplePTDO sc;
        SampleManager1 sm;

        if (sampleId.equals(grp.id))
            return;

        grp.id = sampleId;
        if (grp.labels == null)
            grp.labels = new ArrayList<String>();
        else
            grp.labels.clear();
        allSet = false;
        sm = smMap.get(sampleId);
        sc = getSamplePT(sm);
        if (sc == null)
            return;
        /*
         * set the label for each column
         */
        while (startCol < colFieldMap.size()) {
            field = colFieldMap.get(startCol++ );
            switch (field) {
                case SampleWebMeta.PT_PT_PROVIDER_ID:
                    setDictionaryLabel(grp, sc.getPTProviderId());
                    break;
                case SampleWebMeta.PT_SERIES:
                    setLabel(grp, sc.getSeries());
                    break;
                case SampleWebMeta.PT_DUE_DATE:
                    setDateTimeLabel(grp, sc.getDueDate(), Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.RECEIVED_BY_ID:
                    setLabel(grp, userCache.getSystemUser(getSample(sm).getReceivedById())
                                           .getLoginName());
                    break;
                default:
                    allSet = true;
                    break;

            }
            if (allSet)
                break;
        }
    }

    /**
     * Converts the passed number to string and adds it to the labels of the
     * passed group
     */
    private void setNumberLabel(LabelGroup grp, Number n) {
        String val;

        val = null;
        if (n != null)
            val = DataBaseUtil.toString(n);
        setLabel(grp, val);
    }

    /**
     * Formats the passed date-time using the passed pattern and adds it to the
     * labels of the passed group
     */
    private void setDateTimeLabel(LabelGroup grp, Datetime dt, String pattern) {
        String val;

        val = null;
        if (dt != null)
            val = ReportUtil.toString(dt, pattern);
        setLabel(grp, val);
    }

    /**
     * Gets the dictionary record for the passed id and adds its entry to the
     * labels of the passed group
     */
    private void setDictionaryLabel(LabelGroup grp, Integer dictId) throws Exception {
        String val;

        val = null;
        if (dictId != null)
            val = dictionaryCache.getById(dictId).getEntry();
        setLabel(grp, val);
    }

    /**
     * Adds the passed string to the labels of the passed group; if the string
     * is empty or null, adds the empty string
     */
    private void setLabel(LabelGroup grp, String val) {
        if ( !DataBaseUtil.isEmpty(val))
            grp.labels.add(val);
        else
            grp.labels.add("");
    }

    /**
     * Add cells to the passed row and fills them with the labels in the passed
     * groups
     */
    private void setCells(Row row, ArrayList<LabelGroup> grps) {
        int start;
        Cell cell;

        for (LabelGroup grp : grps) {
            start = row.getPhysicalNumberOfCells();
            for (String l : grp.labels) {
                cell = row.createCell(start++ );
                cell.setCellValue(l);
            }
        }
    }

    /**
     * Returns true if each cell in the first row has the same data as the
     * corresponding cell in the second row; returns false otherwise
     */
    private boolean isSameDataInRows(Row currRow, Row prevRow) {
        int prevType, currType;
        Cell prevCell, currCell;

        if (currRow == null || prevRow == null)
            return false;

        for (int i = 0; i < prevRow.getPhysicalNumberOfCells(); i++ ) {
            prevCell = prevRow.getCell(i);
            currCell = currRow.getCell(i);

            if (prevCell == null) {
                if (currCell == null)
                    continue;
                else
                    return false;
            } else if (currCell == null) {
                return false;
            }

            prevType = prevCell.getCellType();
            currType = currCell.getCellType();

            if (prevType != currType)
                return false;

            switch (prevType) {
                case Cell.CELL_TYPE_STRING:
                    if ( !DataBaseUtil.isSame(prevCell.getStringCellValue(),
                                              currCell.getStringCellValue()))
                        return false;
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if ( !DataBaseUtil.isSame(prevCell.getNumericCellValue(),
                                              currCell.getNumericCellValue()))
                        return false;
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    if ( !DataBaseUtil.isSame(prevCell.getBooleanCellValue(),
                                              currCell.getBooleanCellValue()))
                        return false;
                    break;
            }
        }

        return true;
    }

    /**
     * This class is used to hold the data for cells belonging to a group like
     * sample or analysis etc.
     */
    private class LabelGroup {
        Integer           id;
        ArrayList<String> labels;
    }

    /**
     * This class is used for sorting the list of aux data fetched for
     * generating the data view report; the class is used when results are also
     * fetched and the query for aux data is restricted to the samples that the
     * results belong to; in that case, the sample ids are divided into subsets,
     * a query is run for each subset and the final list is the combination of
     * the aux data returned by all queries; this class is needed then because
     * the final list needs to be sorted by accession number and analyte name,
     * which isn't the case at the time of combining the lists
     */
    private class DataViewComparator implements Comparator<DataViewAuxDataFetch1VO> {
        public int compare(DataViewAuxDataFetch1VO aux1, DataViewAuxDataFetch1VO aux2) {
            int diff;
            Integer accNum1, accNum2;
            String name1, name2;

            accNum1 = aux1.getSampleAccessionNumber();
            accNum2 = aux2.getSampleAccessionNumber();
            name1 = aux1.getAuxFieldAnalyteName();
            name2 = aux2.getAuxFieldAnalyteName();

            /*
             * if the accession numbers are different then we don't compare the
             * names of the analytes; if the numbers are the same then the names
             * are compared
             */
            diff = accNum1 - accNum2;
            if (diff != 0) {
                return diff;
            } else {
                if (name1 == null) {
                    return (name2 == null) ? 0 : 1;
                } else {
                    return (name2 == null) ? -1 : name1.compareTo(name2);
                }
            }
        }
    }
}