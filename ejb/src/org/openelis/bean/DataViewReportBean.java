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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAnalyteVO;
import org.openelis.domain.DataViewResultVO;
import org.openelis.domain.DataViewValueVO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.IdNameVO;
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

/**
 * @author akampoow
 *
 */
@Stateless
@SecurityDomain("openelis")
public class DataViewReportBean {

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

    @EJB
    private SystemVariableBean         systemVariable;

    @EJB
    private EventLogBean               eventLog;

    private static final SampleWebMeta meta = new SampleWebMeta();

    private static final Logger        log  = Logger.getLogger("openelis");

    private static final String        DATA_VIEW = "data_view", FILTERS = "filters",
                    EXCLUDE_RES_OVERRIDE = "excludeResultOverride", EXCLUDE_RES = "excludeResults",
                    INCLUDE_NOT_REP_RES = "includeNotReportableResults",
                    EXCLUDE_AUX = "excludeAuxData",
                    INCLUDE_NOT_REP_AUX = "includeNotReportableAuxData",
                    QUERY_FIELDS = "query_fields", COLUMNS = "columns";
    
    private static final int DEFAULT_MAX_SAMPLES = 1000000; 

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
    public ReportStatus runReportForInternal(DataView1VO data) throws Exception {
        return runReport(data, null, false);
    }

    /**
     * Returns a VO filled from the xml file located at the passed url; the VO
     * contains the "include" and "exclude" filters, the query fields and
     * columns
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataView1VO loadQuery(String url) throws Exception {
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
     * This method is called to show analytes and values to the user, so that
     * one or more of them can be selected to be shown in the generated report.
     * 
     * @param data
     *        the VO that contains the query fields used for fetching the
     *        analytes and values
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations; this parameter is usually specified only for
     *        external users
     * @return a VO that contains the fetched analytes and values
     * @throws Exception
     */
    private DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data, String moduleName) throws Exception {
        boolean excludeOverride, excludeRes, excludeAux;
        Integer max;
        String value;
        QueryBuilderV2 builder;
        List<DataViewResultVO> results, auxiliary;
        ArrayList<SampleQaEventViewDO> sqas;
        ArrayList<AnalysisQaEventViewDO> aqas;
        ArrayList<DictionaryViewDO> dictionaries;
        HashSet<Integer> sampleIds, analysisIds, dictIds;
        HashMap<Integer, DictionaryViewDO> dictMap;

        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeRes = "Y".equals(data.getExcludeResults());
        excludeAux = "Y".equals(data.getExcludeAuxData());

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        results = null;
        auxiliary = null;

        /*
         * get the maximum number of samples allowed by this report
         */
        try {
            value = systemVariable.fetchByName("data_view_max_samples").getValue();
            max = Integer.valueOf(value);
        } catch (Exception e) {
            log.log(Level.INFO,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("data_view_max_samples"),
                    e);
            max = DEFAULT_MAX_SAMPLES;
        }

        if ( !excludeRes) {
            log.log(Level.FINER, "Before fetching results");
            results = fetchResults(moduleName, builder, null, null, data);
            log.log(Level.FINER, "Fetched " + results.size() + " results");
        }

        /*
         * go through the fetched results to make lists of their sample,
         * analysis ids and dictionary ids so that any qa events etc. linked to
         * them can be fetched; throw an exception if the number of samples
         * exceeds the maximum allowed limit
         */
        sampleIds = new HashSet<Integer>();
        analysisIds = new HashSet<Integer>();
        dictIds = new HashSet<Integer>();
        if (results != null) {
            for (DataViewResultVO res : results) {
                sampleIds.add(res.getSampleId());
                analysisIds.add(res.getAnalysisId());
                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId()))
                    dictIds.add(Integer.valueOf(res.getValue()));
            }

            if (sampleIds.size() > max)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(sampleIds.size(),
                                                                                        max));
        }

        if ( !excludeAux) {
            log.log(Level.FINER, "Before fetching aux data");
            auxiliary = fetchAuxData(moduleName, builder, null, null, sampleIds, data);
            log.log(Level.FINER, "Fetched " + auxiliary.size() + " aux data");
        }

        if ( (results == null || results.isEmpty()) && (auxiliary == null || auxiliary.isEmpty()))
            throw new NotFoundException();

        /*
         * go through the fetched aux data to make lists of their dictionary ids
         * so the dictionary entries linked to them can be fetched; throw an
         * exception if the number of samples exceeds the maximum allowed limit
         */
        if (auxiliary != null) {
            for (DataViewResultVO aux : auxiliary) {
                sampleIds.add(aux.getSampleId());
                if (Constants.dictionary().AUX_DICTIONARY.equals(aux.getTypeId()))
                    dictIds.add(Integer.valueOf(aux.getValue()));
            }

            if (sampleIds.size() > max)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(sampleIds.size(),
                                                                                        max));
        }

        if (excludeOverride) {
            /*
             * the user wants to exclude results and aux data linked to
             * overridden samples or analyses; fetch the sample and analysis qa
             * events; keep the ids of only those samples and analyses that
             * don't have any result override qa events
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
            data.setTestAnalytes(getAnalytes(results, sampleIds, analysisIds, dictMap));

        if ( !excludeAux)
            data.setAuxFields(getAnalytes(auxiliary, sampleIds, null, dictMap));

        return data;
    }

    /**
     * Generates an Excel file based on the data in the passed VO, the passed
     * name and the passed flag
     * 
     * @param data
     *        the VO that contains the columns, analytes and values selected by
     *        the user; it also contains the query fields used to fetch the data
     *        for generating the file
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations; this parameter is usually specified only for
     *        external users
     * @param showReportableColumnsOnly
     *        if true, only reportable column analytes are shown; this parameter
     *        is usually specified only for external users
     * @return ReportStatus that contains the path to the generated file
     * @throws Exception
     */
    private ReportStatus runReport(DataView1VO data, String moduleName,
                                   boolean showReportableColumnsOnly) throws Exception {
        boolean excludeRes, excludeAux;
        Integer max;
        String source, value;
        ReportStatus status;
        QueryBuilderV2 builder;
        XSSFWorkbook wb;
        OutputStream out;
        Path path;
        ArrayList<String> headers;
        List<DataViewResultVO> results, noResAux, auxiliary;
        ArrayList<Integer> unselAnalyteIds;
        ArrayList<DataViewAnalyteVO> testAnalytes, auxFields;
        ArrayList<SampleManager1> sms;
        ArrayList<SampleManager1.Load> load;
        HashSet<String> resultValues, auxValues;
        HashSet<Integer> analysisIds, sampleIds;
        HashMap<Integer, HashSet<String>> testAnaResMap, auxFieldValMap;
        HashMap<Integer, SampleManager1> smMap;

        status = new ReportStatus();

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

        /*
         * get the labels to be displayed in the headers for the various
         * columns; Note: load parameter is changed based on selected columns
         */
        headers = getHeaders(data.getColumns(), load);

        /*
         * always fetch sample and analysis qa events to make sure that
         * overridden values are not shown
         */
        load.add(SampleManager1.Load.QA);

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

        /*
         * get the maximum number of samples allowed by this report
         */
        try {
            value = systemVariable.fetchByName("data_view_max_samples").getValue();
            max = Integer.valueOf(value);
        } catch (Exception e) {
            log.log(Level.INFO,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("data_view_max_samples"),
                    e);
            max = DEFAULT_MAX_SAMPLES;
        }

        if (excludeRes && excludeAux) {
            /*
             * fetch the data for the case when both results and aux data are
             * excluded; make a set of analysis ids for fetching managers
             */
            noResAux = fetchNoResultAuxData(moduleName, builder, data);
            for (DataViewResultVO nra : noResAux) {
                analysisIds.add(nra.getAnalysisId());
                sampleIds.add(nra.getSampleId());
            }
        } else {
            if ( !excludeRes) {
                load.add(SampleManager1.Load.SINGLERESULT);
                unselAnalyteIds = new ArrayList<Integer>();
                testAnalytes = data.getTestAnalytes();
                if (testAnalytes != null) {
                    /*
                     * the analytes and results selected by the user are stored
                     * in this map; the row for a result is added to the file if
                     * it's found in the map
                     */
                    testAnaResMap = new HashMap<Integer, HashSet<String>>();
                    for (DataViewAnalyteVO ana : testAnalytes) {
                        /*
                         * create the list of analytes not selected by the user
                         * so that a decision can be made about including either
                         * them or the selected analytes or none of the two in
                         * the query to generate the report
                         */
                        if ("N".equals(ana.getIsIncluded())) {
                            unselAnalyteIds.add(ana.getAnalyteId());
                            continue;
                        }
                        resultValues = new HashSet<String>();
                        for (DataViewValueVO res : ana.getValues()) {
                            if ("Y".equals(res.getIsIncluded()))
                                resultValues.add(res.getValue());
                        }
                        testAnaResMap.put(ana.getAnalyteId(), resultValues);
                    }
                }

                /*
                 * fetch results based on the analytes and values selected by
                 * the user
                 */
                if (testAnaResMap != null && testAnaResMap.size() > 0) {
                    log.log(Level.FINE, "Before fetching results");
                    results = fetchResults(moduleName,
                                           builder,
                                           testAnaResMap,
                                           unselAnalyteIds,
                                           data);
                    log.log(Level.FINE, "Fetched " + results.size() + " results");
                    status.setPercentComplete(5);

                    /*
                     * make a set of analysis ids from the fetched results for
                     * fetching the managers
                     */
                    for (DataViewResultVO res : results) {
                        analysisIds.add(res.getAnalysisId());
                        sampleIds.add(res.getSampleId());
                    }
                }
            }

            /*
             * the user wants to stop the report
             */
            if (ReportStatus.Status.CANCEL.equals(status.getStatus())) {
                status.setMessage(Messages.get().report_stopped());
                return status;
            }

            /*
             * number of samples fetched must not exceed the maximum allowed
             */
            if (sampleIds.size() > max)
                throw new InconsistencyException(Messages.get()
                                                         .dataView_queryTooBigException(sampleIds.size(),
                                                                                        max));

            if ( !excludeAux) {
                unselAnalyteIds = new ArrayList<Integer>();
                auxFields = data.getAuxFields();
                if (auxFields != null) {
                    /*
                     * the analytes and aux values selected by the user are
                     * stored in this map; the row for an aux data is added to
                     * the file if it's found in the map
                     */
                    auxFieldValMap = new HashMap<Integer, HashSet<String>>();
                    for (DataViewAnalyteVO af : auxFields) {
                        /*
                         * create the list of analytes not selected by the user
                         * so that a decision can be made about including either
                         * them or the selected analytes or none of the two in
                         * the query to generate the report
                         */
                        if ("N".equals(af.getIsIncluded())) {
                            unselAnalyteIds.add(af.getAnalyteId());
                            continue;
                        }
                        auxValues = new HashSet<String>();
                        for (DataViewValueVO val : af.getValues()) {
                            if ("Y".equals(val.getIsIncluded()))
                                auxValues.add(val.getValue());
                        }
                        auxFieldValMap.put(af.getAnalyteId(), auxValues);
                    }
                }

                /*
                 * fetch aux data based on the analytes and values selected by
                 * the user
                 */
                builder.clearWhereClause();
                if (auxFieldValMap != null && auxFieldValMap.size() > 0) {
                    log.log(Level.FINE, "Before fetching aux data");
                    auxiliary = fetchAuxData(moduleName,
                                             builder,
                                             auxFieldValMap,
                                             unselAnalyteIds,
                                             sampleIds,
                                             data);
                    log.log(Level.FINE, "Fetched " + auxiliary.size() + " aux data");
                    /*
                     * make a set of sample ids from the fetched aux data for
                     * fetching managers but only if no results were fetched; if
                     * results were fetched, the aux data's sample ids should
                     * already be in the set; this is because aux data were
                     * restricted by the results' sample ids
                     */
                    if (results == null || results.size() == 0) {
                        for (DataViewResultVO aux : auxiliary)
                            sampleIds.add(aux.getSampleId());
                    }
                }
            }
        }

        status.setPercentComplete(25);

        if ( (results == null || results.size() == 0) &&
            (auxiliary == null || auxiliary.size() == 0) &&
            (noResAux == null || noResAux.size() == 0))
            throw new NotFoundException();

        /*
         * the user wants to stop the report
         */
        if (ReportStatus.Status.CANCEL.equals(status.getStatus())) {
            status.setMessage(Messages.get().report_stopped());
            return status;
        }

        /*
         * number of samples fetched must not exceed the allowed limit
         */
        if (sampleIds.size() > max)
            throw new InconsistencyException(Messages.get()
                                                     .dataView_queryTooBigException(sampleIds.size(),
                                                                                    max));

        /*
         * create the event log for data view
         */
        source = Messages.get().dataView_eventLogMessage(userCache.getSystemUser().getLoginName(),
                                                         sampleIds.size());
        try {
            eventLog.add(new EventLogDO(null,
                                        dictionaryCache.getIdBySystemName("log_type_report"),
                                        source,
                                        null,
                                        null,
                                        Constants.dictionary().LOG_LEVEL_INFO,
                                        null,
                                        null,
                                        null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add log entry for: " + source, e);
        }

        log.log(Level.FINE, "Before fetching managers");

        /*
         * if analysis ids are present, managers are fetched by them and with
         * the load element SINGLEANALYSIS; this makes sure that only the
         * analyses and results linked to the analytes selected by the user are
         * fetched; otherwise managers are fetched by sample ids
         */
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

        log.log(Level.FINE, "Fetched " + sms.size() + " managers");

        smMap = new HashMap<Integer, SampleManager1>();
        for (SampleManager1 sm : sms)
            smMap.put(getSample(sm).getId(), sm);

        sms = null;

        /*
         * create a workbook from the data structures created above; the passed
         * status is updated every time a new row is added to the workbook
         */
        wb = getWorkbook(results,
                         auxiliary,
                         noResAux,
                         testAnaResMap,
                         auxFieldValMap,
                         moduleName,
                         showReportableColumnsOnly,
                         headers,
                         data,
                         smMap,
                         status);
        
        /*
         * the user wants to stop the report
         */
        if (ReportStatus.Status.CANCEL.equals(status.getStatus())) {
            status.setMessage(Messages.get().report_stopped());
            return status;
        }
        
        smMap = null;
        results = null;
        auxiliary = null;
        noResAux = null;
        testAnaResMap = null;
        auxFieldValMap = null;
        headers = null;
        
        /*
         * write the workbook to a file and set its path in the status
         */
        if (wb != null) {
            out = null;
            try {
                status.setMessage(Messages.get().report_outputReport()).setPercentComplete(20);
                path = ReportUtil.createTempFile("dataview", ".xlsx", "upload_stream_directory");

                out = Files.newOutputStream(path);
                wb.write(out);

                status.setPercentComplete(100)
                      .setMessage(path.getFileName().toString())
                      .setPath(path.toString())
                      .setStatus(ReportStatus.Status.SAVED);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to output the file for data view", e);
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

    /**
     * Creates a "where" clause from the query fields in the passed VO and sets
     * it in the passed query builder; the "where" clause is used for fetching
     * results
     * 
     * @param builder
     *        the query builder that will be used to fetch data based on the
     *        "where" clause created by this method
     * @param data
     *        the VO that contains the fields that the user wants to query by
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations; this parameter is usually specified only for
     *        external users; if this is specified, the module's clause is added
     *        to the "where" clause
     * @throws Exception
     */
    private void buildWhereForResult(QueryBuilderV2 builder, DataView1VO data, String moduleName) throws Exception {
        builder.constructWhere(data.getQueryFields());
        /*
         * if moduleName is not null then this query is being executed for the
         * web
         */
        if (moduleName != null)
            buildWhereForWeb(builder, moduleName);        

        /*
         * this is done so that the alias for "sampleItem" gets added to the
         * query, otherwise the query will not execute
         */
        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());

        /*
         * the user wants to see only reportable results
         */
        if ("N".equals(data.getIncludeNotReportableResults()))
            builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");

        builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
        builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
    }

    /**
     * Creates a "where" clause from the query fields in the passed VO and sets
     * it in the passed query builder; the "where" clause is used for fetching
     * aux data
     * 
     * @param builder
     *        the query builder that will be used to fetch data based on the
     *        "where" clause created by this method
     * @param data
     *        the VO that contains the fields that the user wants to query by
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations; this parameter is usually specified only for
     *        external users; the module's clause is added to the "where" clause
     * @param sampleIds
     *        a list of sample ids; a clause is added to the "where" clause to
     *        restrict the fetched aux data to only these samples
     * @param analyteClause
     *        a clause for fetching aux data either linked to analytes selected
     *        by the user or not linked to the analytes not selected by the
     *        user; it's added to the "where" clause
     * @throws Exception
     */
    private void buildWhereForAux(QueryBuilderV2 builder, DataView1VO data, String moduleName,
                                  List<Integer> sampleIds,
                                  String analyteClause) throws Exception {
        builder.constructWhere(data.getQueryFields());
        /*
         * if moduleName is not null then this query is being executed for the
         * web
         */
        if (moduleName != null) {
            buildWhereForWeb(builder, moduleName);

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
        
        /*
         * Add the clause for limiting the aux data by analytes only if the user
         * selected some specific analytes and not all of them. This eliminates
         * the unnecessary time spent on excluding those aux data from the
         * records returned by the query
         */
        if ( !DataBaseUtil.isEmpty(analyteClause))
            builder.addWhere(SampleWebMeta.getAuxDataFieldAnalyteId() + analyteClause);

        if (sampleIds != null) {
            builder.addWhere(SampleWebMeta.getAuxDataReferenceId() + " in (" +
                             DataBaseUtil.concatWithSeparator(sampleIds, ",") + ")");
            builder.addWhere(SampleWebMeta.getAuxDataReferenceTableId() + "=" +
                             Constants.table().SAMPLE);
        }
    }

    /**
     * Creates a "where" clause for restricting external users to only certain
     * samples e.g. the ones not in error and sets the clause in the passed
     * query builder
     * 
     * @param builder
     *        the query builder that will be used to fetch data based on the
     *        "where" clause created by this method
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations; the module's clause is added to the "where"
     *        clause
     * @throws Exception
     */
    private void buildWhereForWeb(QueryBuilderV2 builder, String moduleName) throws Exception {
        builder.addWhere("(" + userCache.getPermission().getModule(moduleName).getClause() + ")");
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" +
                         Constants.dictionary().ORG_REPORT_TO);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" +
                         Constants.dictionary().SAMPLE_ERROR);
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                         Constants.dictionary().ANALYSIS_RELEASED);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");
    }

    /**
     * Returns the list of analytes linked to the results in the passed list;
     * each element in the returned list also has the list of values for the
     * analyte; a result's value is included in the list only if its sample and
     * analysis ids are found in the passed lists of sample and analysis ids;
     * the passed map is used to find dictionary entries for values of that type
     */
    private ArrayList<DataViewAnalyteVO> getAnalytes(List<DataViewResultVO> results,
                                                              HashSet<Integer> sampleIds,
                                                              HashSet<Integer> analysisIds,
                                                              HashMap<Integer, DictionaryViewDO> dictMap) {
        Integer analyteId, dictId;
        String val;
        DataViewAnalyteVO dvAna;
        DataViewValueVO dvVal;
        HashSet<String> vals;
        ArrayList<DataViewAnalyteVO> anas;
        ArrayList<DataViewValueVO> dvVals;
        HashMap<Integer, DataViewAnalyteVO> dvAnaMap;
        HashMap<Integer, HashSet<String>> anaValMap;

        /*
         * create the list of analytes from the passed list of results; create
         * the list of values for each analyte and set it in the VO for that
         * analyte
         */
        anas = new ArrayList<DataViewAnalyteVO>();
        dvAnaMap = new HashMap<Integer, DataViewAnalyteVO>();
        anaValMap = new HashMap<Integer, HashSet<String>>();
        vals = null;
        for (DataViewResultVO res : results) {
            if ( !sampleIds.contains(res.getSampleId()) ||
                (analysisIds != null && !analysisIds.contains(res.getAnalysisId())))
                continue;
            analyteId = res.getAnalyteId();
            dvAna = dvAnaMap.get(analyteId);
            /*
             * a VO is created for an analyte only once, no matter how many
             * times it appears in the passed list of results
             */
            if (dvAna == null) {
                dvAna = new DataViewAnalyteVO();
                dvAna.setAnalyteId(analyteId);
                dvAna.setAnalyteName(res.getAnalyteName());
                dvAna.setIsIncluded("N");
                dvVals = new ArrayList<DataViewValueVO>();
                dvAna.setValues(dvVals);
                anas.add(dvAna);
                dvAnaMap.put(analyteId, dvAna);
                vals = new HashSet<String>();
                anaValMap.put(analyteId, vals);
            } else {
                dvVals = dvAna.getValues();
                vals = anaValMap.get(analyteId);
            }

            val = res.getValue();
            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(res.getTypeId())) {
                dictId = Integer.parseInt(val);
                val = dictMap.get(dictId).getEntry();
            }

            /*
             * don't allow the same value to be shown more than once for an
             * analyte
             */
            if (vals.contains(val))
                continue;
            vals.add(val);

            dvVal = new DataViewValueVO();
            dvVal.setValue(val);
            dvVal.setIsIncluded("N");
            dvVals.add(dvVal);
        }
        return anas;
    }

    /**
     * Fetches results for generating the report; if "moduleName" is not null,
     * the clause from security for that module for the logged in user is added
     * to the "where" clause; the passed map and list are used to limit the
     * query by either selected or not selected analytes, based on which is
     * smaller in size
     */
    private List<DataViewResultVO> fetchResults(String moduleName,
                                                      QueryBuilderV2 builder,
                                                      HashMap<Integer, HashSet<String>> testAnaResultMap,
                                                      ArrayList<Integer> unselAnalyteIds,
                                                      DataView1VO data) throws Exception {
        Query query;
        ArrayList<String> orderBy;

        builder.setSelect("distinct new org.openelis.domain.DataViewResultVO(" +
                          SampleWebMeta.getId() + "," + SampleWebMeta.getAccessionNumber() + "," +
                          SampleWebMeta.getItemId() + "," + SampleWebMeta.getResultAnalysisid() +
                          "," + SampleWebMeta.getResultId() + "," +
                          SampleWebMeta.getResultAnalyteId() + "," +
                          SampleWebMeta.getResultAnalyteName() + "," +
                          SampleWebMeta.getResultTypeId() + "," + SampleWebMeta.getResultValue() +
                          ") ");

        buildWhereForResult(builder, data, moduleName);

        /*
         * add the clause for limiting the results by analytes only if the user
         * selected some specific analytes and not all; this eliminates the
         * unnecessary time spent on excluding results linked to not selected
         * analytes from the data returned by the query
         */
        if (unselAnalyteIds != null && unselAnalyteIds.size() > 0)
            builder.addWhere(SampleWebMeta.getResultAnalyteId() +
                             getAnalyteClause(testAnaResultMap.keySet(), unselAnalyteIds));

        orderBy = new ArrayList<String>();
        orderBy.add(SampleWebMeta.getAccessionNumber());
        orderBy.add(SampleWebMeta.getResultAnalysisid());
        orderBy.add(SampleWebMeta.getResultAnalyteName());

        builder.setOrderBy(DataBaseUtil.concatWithSeparator(orderBy, ", "));
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, data.getQueryFields());
        return query.getResultList();
    }

    /**
     * Fetches aux data for generating the report; if "moduleName" is not null,
     * the clause from security for that module for the logged in user is added
     * to the "where" clause; the passed map and list are used to limit the
     * query by either selected or not selected analytes, based on which is
     * smaller in size
     */
    private List<DataViewResultVO> fetchAuxData(String moduleName,
                                                       QueryBuilderV2 builder,
                                                       HashMap<Integer, HashSet<String>> auxFieldValueMap,
                                                       ArrayList<Integer> unselAnalyteIds,
                                                       HashSet<Integer> sampleIds, DataView1VO data) throws Exception {
        String analyteClause;
        Query query;
        ArrayList<Integer> ids, range;
        List<DataViewResultVO> auxiliary;

        builder.setSelect("distinct new org.openelis.domain.DataViewResultVO(" +
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
            auxiliary = new ArrayList<DataViewResultVO>();
            ids = DataBaseUtil.toArrayList(sampleIds);
            range = DataBaseUtil.createSubsetRange(ids.size());
            builder.setOrderBy("");
            for (int i = 0; i < range.size() - 1; i++ ) {
                builder.clearWhereClause();
                buildWhereForAux(builder,
                                       data,
                                       moduleName,
                                       ids.subList(range.get(i), range.get(i + 1)),
                                       analyteClause);
                query = manager.createQuery(builder.getEJBQL());
                builder.setQueryParams(query, data.getQueryFields());
                auxiliary.addAll(query.getResultList());
            }
            /*
             * the final list of aux data is obtained by combining smaller lists
             * returned by the queries run on subsets of sample ids; so a
             * comparator is used here to sort the final list by accession
             * number and analyte name because it isn't sorted by that at the
             * time of combining the lists
             */
            Collections.sort(auxiliary, new DataViewComparator());
        } else {
            builder.clearWhereClause();
            buildWhereForAux(builder, data, moduleName, null, analyteClause);
            builder.setOrderBy(SampleWebMeta.getAccessionNumber() + "," +
                               SampleWebMeta.getAuxDataFieldAnalyteName());
            query = manager.createQuery(builder.getEJBQL());
            builder.setQueryParams(query, data.getQueryFields());
            auxiliary = query.getResultList();
        }

        return auxiliary;
    }

    /**
     * Fetches data for generating the report when both results and aux data are
     * excluded; if "moduleName" is not null, the clause from security for that
     * module for the logged in user is added to the "where" clause
     */
    private List<DataViewResultVO> fetchNoResultAuxData(String moduleName,
                                                              QueryBuilderV2 builder,
                                                              DataView1VO data) throws Exception {
        Query query;
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();

        builder.setSelect("distinct new org.openelis.domain.DataViewResultVO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getItemId() + ", " + SampleWebMeta.getAnalysisId() + ")");

        builder.constructWhere(fields);
        /*
         * If moduleName is present, then it means that this report is being run
         * for the samples belonging to the list of organizations specified in
         * this user's system_user_module for a specific domain.
         */
        if (moduleName != null)
            buildWhereForWeb(builder, moduleName);

        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    /**
     * The passed set contains the ids for selected analytes and the passed list
     * contains the ids for not selected analytes; the method returns a clause
     * for either excluding the unselected analytes or including the selected
     * analytes based on which collection is smaller in size
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

    /**
     * Creates and returns a workbook that gets converted to an Excel file; each
     * row in the workbook shows fields from some part of a sample and analytes
     * and values
     * 
     * @param results
     *        the list of VOs containing result info to be shown
     * @param auxiliary
     *        the list of VOs containing aux data info to be shown
     * @param noResAux
     *        the list of VOs containing info to be shown when both results and
     *        aux data are excluded
     * @param testAnaResMap
     *        the map containing result analytes and values selected by the
     *        user; if an analyte or value is not in the map, the result is not
     *        shown
     * @param auxFieldValMap
     *        the map containing aux data analytes and values selected by the
     *        user; if an analyte or value is not in the map, the aux data is
     *        not shown
     * @param moduleName
     *        the name of a security module for the logged in user; the module's
     *        clause is used to restrict the fetched data to specific records
     *        e.g. organizations
     * @param showReportableColumnsOnly
     *        if true, only reportable column analytes are shown
     * @param headers
     *        the list of labels for the column headers
     * @param data
     *        the VO containing the user's choices for the data shown e.g. the
     *        meta keys for selected columns and "include" and "exclude" flags
     * @param smMap
     *        the map that provides the data for the columns belonging to
     *        various parts of a sample e.g. domain, organization, project etc.
     * @param status
     *        the percent completion in this ReportStatus is updated every time
     *        a new row is added to the workbook
     */
    private XSSFWorkbook getWorkbook(List<DataViewResultVO> results,
                                     List<DataViewResultVO> auxiliary,
                                     List<DataViewResultVO> noResAux,
                                     HashMap<Integer, HashSet<String>> testAnaResMap,
                                     HashMap<Integer, HashSet<String>> auxFieldValMap,
                                     String moduleName, boolean showReportableColumnsOnly,
                                     ArrayList<String> headers,
                                     DataView1VO data,
                                     HashMap<Integer, SampleManager1> smMap, ReportStatus status) throws Exception {
        boolean excludeOverride, excludeRes, excludeAux, samOverridden, anaOverridden, addResRow, addAuxRow, addNoResAuxRow;
        int i, j, resIndex, auxIndex, noResAuxIndex, rowIndex, numRes, numAux, numNoResAux, lastCol, currCol;
        Integer samId, prevSamId, itemId, anaId, prevAnaId, anaIndex;
        String resVal, auxVal;
        SampleManager1 sm;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        DataViewResultVO res, noRA, aux;
        ResultViewDO rowRes, colRes;
        Row headerRow, currRow, prevRow;
        RowData rd;
        Cell cell;
        CellStyle style;
        ArrayList<Integer> maxChars;
        ArrayList<ResultViewDO> smResults;
        HashMap<String, Integer> colAnaMap;

        wb = new XSSFWorkbook();
        sheet = wb.createSheet();

        /*
         * create the header row and set its style
         */
        headerRow = sheet.createRow(0);
        style = createHeaderStyle(wb);
        maxChars = new ArrayList<Integer>();
        for (i = 0; i < headers.size(); i++ ) {
            cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            setMaxChars(cell, maxChars);
            cell.setCellStyle(style);            
        }

        numRes = results == null ? 0 : results.size();
        numAux = auxiliary == null ? 0 : auxiliary.size();
        numNoResAux = noResAux == null ? 0 : noResAux.size();
        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeRes = "Y".equals(data.getExcludeResults());
        excludeAux = "Y".equals(data.getExcludeAuxData());

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
        colAnaMap = new HashMap<String, Integer>();
        rd = new RowData();

        status.setMessage(Messages.get().report_genDataView());
        status.setPercentComplete(0);
        session.setAttribute("DataViewReportStatus", status);

        /*
         * the lists of results and aux data are iterated through until there
         * are no more elements left in each of them to read from
         */
        while (resIndex < numRes || auxIndex < numAux || noResAuxIndex < numNoResAux) {
            /*
             * the user wants to stop the report
             */
            if (ReportStatus.Status.CANCEL.equals(status.getStatus())) {
                status.setMessage(Messages.get().report_stopped());
                return null;
            }

            status.setPercentComplete(100 * (resIndex + auxIndex + noResAuxIndex) /
                                      (numRes + numAux + numNoResAux));
            if (excludeRes && excludeAux) {
                if (noResAuxIndex < numNoResAux) {
                    noRA = noResAux.get(noResAuxIndex++ );
                    samId = noRA.getSampleId();
                    itemId = noRA.getSampleItemId();
                    anaId = noRA.getAnalysisId();
                    addNoResAuxRow = true;
                }
            } else {
                addAuxRow = false;
                addResRow = false;
                if (resIndex < numRes && auxIndex < numAux) {
                    res = results.get(resIndex);
                    aux = auxiliary.get(auxIndex);
                    /*
                     * if this result's accession number is less than or equal
                     * to this aux data's, add a row for this result, otherwise
                     * add a row for the aux data; this makes sure that the
                     * results for a sample are shown before the aux data;
                     * accession numbers are compared instead of sample ids
                     * because the former is the field shown in the report and
                     * not the latter
                     */
                    if (res.getSampleAccessionNumber() <= aux.getSampleAccessionNumber()) {
                        addResRow = true;
                        resIndex++ ;
                        samId = res.getSampleId();
                        itemId = res.getSampleItemId();
                        anaId = res.getAnalysisId();
                    } else {
                        addAuxRow = true;
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
                    aux = auxiliary.get(auxIndex);
                    auxIndex++ ;
                    samId = aux.getSampleId();
                    itemId = null;
                    anaId = null;
                }
            }

            if ( !samId.equals(prevSamId)) {
                /*
                 * don't show any data for this sample if it's overridden and
                 * such samples are excluded; whether the sample is overridden
                 * is checked even if such samples are not excluded because
                 * overridden result values are not shown in the report
                 */
                sm = smMap.get(samId);
                samOverridden = false;
                if ( (getSampleQAs(sm) != null)) {
                    for (SampleQaEventViewDO sqa : getSampleQAs(sm)) {
                        if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId())) {
                            samOverridden = true;
                            if (excludeOverride)
                                prevSamId = samId;
                            break;
                        }
                    }
                }
            }

            if (samOverridden && excludeOverride)
                continue;

            if (addResRow) {
                /*
                 * don't show any data for this analysis if it's overridden and
                 * such analyses are excluded; whether the analysis is
                 * overridden is checked even if such analyses are not excluded
                 * because overridden values are not shown in the report
                 */
                if ( !anaId.equals(prevAnaId)) {
                    anaOverridden = false;
                    if ( (getAnalysisQAs(sm) != null)) {
                        for (AnalysisQaEventViewDO aqa : getAnalysisQAs(sm)) {
                            if (aqa.getAnalysisId().equals(anaId) &&
                                Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId())) {
                                anaOverridden = true;
                                if (excludeOverride) {
                                    addResRow = false;
                                    prevAnaId = anaId;
                                }
                                break;
                            }
                        }
                    }
                }

                if (anaOverridden && excludeOverride) {
                    addResRow = false;
                } else {
                    /*
                     * if this result's value was selected by the user, add a
                     * row for it
                     */
                    resVal = getValue(testAnaResMap,
                                      res.getAnalyteId(),
                                      res.getValue(),
                                      res.getTypeId());
                    if (resVal != null)
                        currRow = sheet.createRow(rowIndex++ );
                    else
                        addResRow = false;
                }
            }

            if (addAuxRow) {
                /*
                 * if this aux data's value of was selected by the user, add a
                 * row for it
                 */
                auxVal = getValue(auxFieldValMap,
                                  aux.getAnalyteId(),
                                  aux.getValue(),
                                  aux.getTypeId());
                if (auxVal != null)
                    currRow = sheet.createRow(rowIndex++ );
                else
                    addAuxRow = false;
            }

            if (addNoResAuxRow)
                currRow = sheet.createRow(rowIndex++ );

            if ( !addResRow && !addAuxRow && !addNoResAuxRow)
                continue;

            /*
             * fill the passed row's cells for all columns except the ones for
             * analytes and values
             */
            setCells(samId, itemId, anaId, smMap, rd, data.getColumns(), moduleName, currRow, maxChars);

            if (addResRow) {
                /*
                 * set the analyte name; set the value if the analysis or sample
                 * is not overridden
                 */
                cell = currRow.createCell(currRow.getPhysicalNumberOfCells());
                cell.setCellValue(res.getAnalyteName());
                setMaxChars(cell, maxChars);
                cell = currRow.createCell(currRow.getPhysicalNumberOfCells());
                if ( !anaOverridden && !samOverridden)
                    cell.setCellValue(resVal);
                setMaxChars(cell, maxChars);

                /*
                 * find out if this analyte has any column analytes; if it does,
                 * add the column analytes to the header and their values in the
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
                         * doesn't have a column yet, that column will be added
                         * after "lastCol"; "currCol" keeps track of the current
                         * column
                         */
                        if (lastCol == 0)
                            lastCol = currRow.getPhysicalNumberOfCells();

                        currCol = currRow.getPhysicalNumberOfCells();
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
                             * shown in this column, find the column in which it
                             * is shown and set the value
                             */
                            if (anaIndex == null) {
                                anaIndex = lastCol++ ;
                                colAnaMap.put(colRes.getAnalyte(), anaIndex);
                                cell = headerRow.createCell(anaIndex);
                                cell.setCellValue(colRes.getAnalyte());
                                setMaxChars(cell, maxChars);
                                cell.setCellStyle(style);
                                cell = currRow.createCell(anaIndex);
                            } else if (anaIndex == currCol) {
                                cell = currRow.createCell(currCol++ );
                            } else {
                                cell = currRow.createCell(anaIndex);
                            }

                            /*
                             * set the value if the analysis or sample is not
                             * overridden
                             */
                            if ( !anaOverridden && !samOverridden)
                                cell.setCellValue(getValue(colRes.getValue(), colRes.getTypeId()));
                            setMaxChars(cell, maxChars);
                        }
                    }
                }
            }

            if (addAuxRow) {
                /*
                 * set the analyte name and aux data value
                 */
                cell = currRow.createCell(currRow.getPhysicalNumberOfCells());
                cell.setCellValue(aux.getAnalyteName());
                setMaxChars(cell, maxChars);
                cell = currRow.createCell(currRow.getPhysicalNumberOfCells());
                cell.setCellValue(auxVal);
                setMaxChars(cell, maxChars);
            }

            prevAnaId = anaId;
            prevSamId = samId;

            /*
             * an empty row can't be created and then added to the sheet, it has
             * to be obtained from the sheet; thus it has to be removed if it
             * shouldn't be shown because it has the same data as the previous
             * row in all cells; this can happen if e.g. a user selects only
             * container and sample type but all sample items in a sample have
             * the same values for these fields
             */
            if (isSameDataInRows(currRow, prevRow)) {
                sheet.removeRow(currRow);
                rowIndex-- ;
            } else {
                prevRow = currRow;
            }
        }

        /*
         * make each column wide enough to show the longest string in it; the
         * width for each column is set as the maximum number of characters in
         * that column multiplied by 256 because the default width of one
         * character is 1/256 units in Excel
         */
        for (i = 0; i < headerRow.getPhysicalNumberOfCells(); i++ )
            sheet.setColumnWidth(i, maxChars.get(i)*256);

        return wb;
    }

    /**
     * Creates the style to distinguish the header row from the other rows in
     * the output
     */
    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style;
        Font font;

        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());        
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);        
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFont(font);

        return style;
    }


    /**
     * Creates the list of header labels for the report by going through
     * "columns"
     * @param columns
     *        is a list of meta keys selected for output
     * @param load
     *        is modified based on selected columns in order for the sample
     *        manager to fetch elements such as organization
     * @return header labels
     * @throws Exception
     */
    private ArrayList<String> getHeaders(ArrayList<String> columns,
                                         ArrayList<SampleManager1.Load> load) throws Exception {
        String column;
        boolean fetchOrg, fetchUser, fetchProv;
        ArrayList<String> headers;

        headers = new ArrayList<String>();
        if (columns == null)
            return headers;

        fetchOrg = false;
        fetchUser = false;
        fetchProv = false;
        for (int i = 0; i < columns.size(); i++ ) {
            column = columns.get(i);
            switch (column) {
            /*
             * sample fields
             */
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
                /*
                 * organization fields
                 */
                case SampleWebMeta.SAMPLE_ORG_ID:
                    headers.add(Messages.get().organization_num());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ORG_NAME:
                    headers.add(Messages.get().organization_name());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.SAMPLE_ORG_ATTENTION:
                    headers.add(Messages.get().order_attention());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ADDR_MULTIPLE_UNIT:
                    headers.add(Messages.get().address_aptSuite());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ADDR_STREET_ADDRESS:
                    headers.add(Messages.get().address_address());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ADDR_CITY:
                    headers.add(Messages.get().address_city());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ADDR_STATE:
                    headers.add(Messages.get().address_state());
                    fetchOrg = true;
                    break;
                case SampleWebMeta.ADDR_ZIP_CODE:
                    headers.add(Messages.get().address_zipcode());
                    fetchOrg = true;
                    break;
                /*
                 * sample item fields
                 */
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
                /*
                 * analysis fields
                 */
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
                /*
                 * environmental fields
                 */
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
                /*
                 * sdwis fields
                 */
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
                case SampleWebMeta.SDWIS_COLLECTOR_HEADER:
                    headers.add(Messages.get().sampleSDWIS_collector());
                    break;
                /*
                 * clinical fields
                 */
                case SampleWebMeta.CLIN_PATIENT_ID:
                    headers.add(Messages.get().dataView_patientId());
                    break;
                case SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER:
                    headers.add(Messages.get().dataView_patientLastName());
                    break;
                case SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER:
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
                /*
                 * neonatal fields
                 */
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
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE:
                    headers.add(Messages.get().dataView_nextOfKinPhone());
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
                /*
                 * pt fields
                 */
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
                    throw new InconsistencyException("Unknown column " + column);
            }
        }

        /*
         * fetch organizations, users, providers only if they'll be shown in the
         * report
         */
        if (fetchOrg)
            load.add(SampleManager1.Load.ORGANIZATION);

        if (fetchUser)
            load.add(SampleManager1.Load.ANALYSISUSER);

        if (fetchProv && !load.contains(SampleManager1.Load.PROVIDER))
            load.add(SampleManager1.Load.PROVIDER);

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

    /**
     * returns the value or the dictionary entry for it, based on the passed
     * type; returns null is the value is empty or null
     */
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
     * Fills the passed row's cells for all columns except the ones for analytes
     * and values; the sample, sample item and analysis for each row are
     * specified by the passed ids; for aux data rows, sample item and analysis
     * ids are null, because those cells need to be blank; "colFieldMap"
     * specifies which column is showing which field; the passed RowData holds
     * data that needs to be recomputed when the sample, item or analysis
     * changes, but stays the same otherwise e.g. analysis received by;
     * "maxChars" is keeps track of the maximum number of characters in each
     * column and is updated as a new value is put in a cell
     */
    private void setCells(Integer sampleId, Integer sampleItemId, Integer analysisId,
                          HashMap<Integer, SampleManager1> smMap, RowData rd,
                          ArrayList<String> columns, String moduleName, Row row,
                          ArrayList<Integer> maxChars) throws Exception {
        boolean runForWeb;
        String column;
        Object value;
        SampleDO s;
        SampleEnvironmentalDO se;
        SampleSDWISViewDO ss;
        SampleManager1 sm;
        SampleClinicalViewDO sc;
        SampleNeonatalViewDO sn;
        SamplePTDO spt;
        Datetime dt;
        Cell cell;
        ArrayList<String> labels;

        sm = smMap.get(sampleId);
        s = getSample(sm);
        se = getSampleEnvironmental(sm);
        ss = getSampleSDWIS(sm);
        sc = getSampleClinical(sm);
        sn = getSampleNeonatal(sm);
        spt = getSamplePT(sm);

        if ( !sampleId.equals(rd.sampleId)) {
            rd.clear();
            rd.sampleId = sampleId;
            /*
             * find the report-to organization
             */
            if (getOrganizations(sm) != null) {
                for (SampleOrganizationViewDO data : getOrganizations(sm)) {
                    if (Constants.dictionary().ORG_REPORT_TO.equals(data.getTypeId())) {
                        rd.repToOrg = data;
                        break;
                    }
                }
            }

            /*
             * find the first permanent project
             */
            if (getProjects(sm) != null) {
                for (SampleProjectViewDO data : getProjects(sm)) {
                    if ("Y".equals(data.getIsPermanent())) {
                        rd.projName = data.getProjectName();
                        break;
                    }
                }
            }
        }

        if (sampleItemId != null && !sampleItemId.equals(rd.sampleItemId)) {
            /*
             * find the item with the passed id
             */
            for (SampleItemViewDO data : getItems(sm)) {
                if (data.getId().equals(sampleItemId)) {
                    rd.sampleItem = data;
                    break;
                }
            }
        } else if (sampleItemId == null) {
            rd.sampleItem = null;
        }

        runForWeb = moduleName != null;

        if (analysisId != null && !analysisId.equals(rd.analysisId)) {
            /*
             * find the analysis with the passed id
             */
            for (AnalysisViewDO data : getAnalyses(sm)) {
                if (data.getId().equals(analysisId)) {
                    rd.analysis = data;
                    break;
                }
            }
            /*
             * find the names of the users who completed and/or released the
             * analysis
             */
            rd.completedBy = null;
            rd.releasedBy = null;
            if (getUsers(sm) != null) {
                labels = new ArrayList<String>();
                for (AnalysisUserViewDO data : getUsers(sm)) {
                    if ( !data.getAnalysisId().equals(analysisId))
                        continue;
                    if (Constants.dictionary().AN_USER_AC_COMPLETED.equals(data.getActionId()))
                        labels.add(data.getSystemUser());
                    else if (Constants.dictionary().AN_USER_AC_RELEASED.equals(data.getActionId()))
                        rd.releasedBy = data.getSystemUser();
                }
                rd.completedBy = DataBaseUtil.concatWithSeparator(labels, ", ");
            }

            /*
             * find the qa events for the analysis; for external clients,
             * internal qa events are not shown and the qa event's reporting
             * text is shown; otherwise internal qa events are shown and the qa
             * event's name is shown
             */
            rd.analysisQAs = null;
            if (getAnalysisQAs(sm) != null) {
                labels = new ArrayList<String>();
                for (AnalysisQaEventViewDO data : getAnalysisQAs(sm)) {
                    if ( !data.getAnalysisId().equals(analysisId))
                        continue;
                    if (runForWeb &&
                        !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId()))
                        labels.add(data.getQaEventReportingText());
                    else
                        labels.add(data.getQaEventName());
                }
                rd.analysisQAs = DataBaseUtil.concatWithSeparator(labels, runForWeb ? " " : ", ");
            }
        } else if (analysisId == null) {
            rd.analysis = null;
        }

        /*
         * set the label for each column
         */
        for (int i = 0; i < columns.size(); i++ ) {
            column = columns.get(i);
            value = null;
            switch (column) {
            /*
             * sample columns
             */
                case SampleWebMeta.ACCESSION_NUMBER:
                    value = s.getAccessionNumber();
                    break;
                case SampleWebMeta.REVISION:
                    value = s.getRevision();
                    break;
                case SampleWebMeta.COLLECTION_DATE:
                    if (rd.collDateTime == null) {
                        /*
                         * combine the collected date and time to form the label
                         * for this column; "collDateTime" is set to empty
                         * string and not null so that it isn't tried to be
                         * created again until the sample changes; at that time
                         * it gets set to null before entering the switch-case
                         */
                        dt = getDateTime(s.getCollectionDate(), s.getCollectionTime());
                        rd.collDateTime = dt != null ? getDateTimeLabel(dt,
                                                                        Messages.get()
                                                                                .gen_dateTimePattern())
                                                    : "";
                    }
                    value = rd.collDateTime;
                    break;
                case SampleWebMeta.RECEIVED_DATE:
                    value = getDateTimeLabel(s.getReceivedDate(), Messages.get()
                                                                          .gen_dateTimePattern());
                    break;
                case SampleWebMeta.ENTERED_DATE:
                    value = getDateTimeLabel(s.getEnteredDate(), Messages.get()
                                                                         .gen_dateTimePattern());
                    break;
                case SampleWebMeta.RELEASED_DATE:
                    value = getDateTimeLabel(s.getReleasedDate(), Messages.get()
                                                                          .gen_dateTimePattern());
                    break;
                case SampleWebMeta.STATUS_ID:
                    value = getDictionaryLabel(s.getStatusId());
                    break;
                case SampleWebMeta.PROJECT_NAME:
                    value = rd.projName;
                    break;
                case SampleWebMeta.CLIENT_REFERENCE_HEADER:
                    value = s.getClientReference();
                    break;
                /*
                 * organization columns
                 */
                case SampleWebMeta.SAMPLE_ORG_ID:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationId() : null;
                    break;
                case SampleWebMeta.ORG_NAME:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationName() : null;
                    break;
                case SampleWebMeta.SAMPLE_ORG_ATTENTION:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationAttention() : null;
                    break;
                case SampleWebMeta.ADDR_MULTIPLE_UNIT:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationMultipleUnit() : null;
                    break;
                case SampleWebMeta.ADDR_STREET_ADDRESS:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationStreetAddress() : null;
                    break;
                case SampleWebMeta.ADDR_CITY:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationCity() : null;
                    break;
                case SampleWebMeta.ADDR_STATE:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationState() : null;
                    break;
                case SampleWebMeta.ADDR_ZIP_CODE:
                    value = rd.repToOrg != null ? rd.repToOrg.getOrganizationZipCode() : null;
                    break;
                /*
                 * sample item columns
                 */
                case SampleWebMeta.ITEM_TYPE_OF_SAMPLE_ID:
                    value = rd.sampleItem != null ? rd.sampleItem.getTypeOfSample() : null;
                    break;
                case SampleWebMeta.ITEM_SOURCE_OF_SAMPLE_ID:
                    value = rd.sampleItem != null ? rd.sampleItem.getSourceOfSample() : null;
                    break;
                case SampleWebMeta.ITEM_SOURCE_OTHER:
                    value = rd.sampleItem != null ? rd.sampleItem.getSourceOther() : null;
                    break;
                case SampleWebMeta.ITEM_CONTAINER_ID:
                    value = rd.sampleItem != null ? rd.sampleItem.getContainer() : null;
                    break;
                case SampleWebMeta.ITEM_CONTAINER_REFERENCE:
                    value = rd.sampleItem != null ? rd.sampleItem.getContainerReference() : null;
                    break;
                case SampleWebMeta.ITEM_ITEM_SEQUENCE:
                    value = rd.sampleItem != null ? rd.sampleItem.getItemSequence() : null;
                    break;
                /*
                 * analysis columns
                 */
                case SampleWebMeta.ANALYSIS_ID:
                    value = rd.analysis != null ? rd.analysis.getId() : null;
                    break;
                case SampleWebMeta.ANALYSIS_TEST_NAME_HEADER:
                    value = null;
                    if (rd.analysis != null)
                        value = runForWeb ? rd.analysis.getTestReportingDescription()
                                         : rd.analysis.getTestName();
                    break;
                case SampleWebMeta.ANALYSIS_METHOD_NAME_HEADER:
                    value = null;
                    if (rd.analysis != null)
                        value = runForWeb ? rd.analysis.getMethodReportingDescription()
                                         : rd.analysis.getMethodName();
                    break;
                case SampleWebMeta.ANALYSIS_STATUS_ID_HEADER:
                    value = getDictionaryLabel(rd.analysis != null ? rd.analysis.getStatusId()
                                                                  : null);
                    break;
                case SampleWebMeta.ANALYSIS_REVISION:
                    value = rd.analysis != null ? rd.analysis.getRevision() : null;
                    break;
                case SampleWebMeta.ANALYSIS_IS_REPORTABLE_HEADER:
                    value = getYesNoLabel(rd.analysis != null ? rd.analysis.getIsReportable()
                                                             : null);
                    break;
                case SampleWebMeta.ANALYSIS_UNIT_OF_MEASURE_ID:
                    value = getDictionaryLabel(rd.analysis != null ? rd.analysis.getUnitOfMeasureId()
                                                                  : null);
                    break;
                case SampleWebMeta.ANALYSISSUBQA_NAME:
                    value = rd.analysisQAs;
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_DATE:
                    dt = rd.analysis != null ? rd.analysis.getCompletedDate() : null;
                    value = getDateTimeLabel(dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_COMPLETED_BY:
                    value = rd.completedBy;
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_DATE:
                    dt = rd.analysis != null ? rd.analysis.getReleasedDate() : null;
                    value = getDateTimeLabel(dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_RELEASED_BY:
                    value = rd.releasedBy;
                    break;
                case SampleWebMeta.ANALYSIS_STARTED_DATE:
                    dt = rd.analysis != null ? rd.analysis.getStartedDate() : null;
                    value = getDateTimeLabel(dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_PRINTED_DATE:
                    dt = rd.analysis != null ? rd.analysis.getPrintedDate() : null;
                    value = getDateTimeLabel(dt, Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.ANALYSIS_SECTION_NAME:
                    value = rd.analysis != null ? rd.analysis.getSectionName() : null;
                    break;
                case SampleWebMeta.ANALYSIS_TYPE_ID:
                    value = getDictionaryLabel(rd.analysis != null ? rd.analysis.getTypeId() : null);
                    break;
                /*
                 * environmental columns
                 */
                case SampleWebMeta.ENV_IS_HAZARDOUS:
                    value = getYesNoLabel(se != null ? se.getIsHazardous() : null);
                    break;
                case SampleWebMeta.ENV_PRIORITY:
                    value = se != null ? se.getPriority() : null;
                    break;
                case SampleWebMeta.ENV_COLLECTOR_HEADER:
                    value = se != null ? se.getCollector() : null;
                    break;
                case SampleWebMeta.ENV_COLLECTOR_PHONE:
                    value = se != null ? se.getCollectorPhone() : null;
                    break;
                case SampleWebMeta.ENV_DESCRIPTION:
                    value = se != null ? se.getDescription() : null;
                    break;
                case SampleWebMeta.ENV_LOCATION:
                    value = se != null ? se.getLocation() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                    value = se != null ? se.getLocationAddress().getMultipleUnit() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS:
                    value = se != null ? se.getLocationAddress().getStreetAddress() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_CITY:
                    value = se != null ? se.getLocationAddress().getCity() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_STATE:
                    value = se != null ? se.getLocationAddress().getState() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_ZIP_CODE:
                    value = se != null ? se.getLocationAddress().getZipCode() : null;
                    break;
                case SampleWebMeta.LOCATION_ADDR_COUNTRY:
                    value = se != null ? se.getLocationAddress().getCountry() : null;
                    break;
                /*
                 * sdwis columns
                 */
                case SampleWebMeta.SDWIS_PWS_ID:
                    value = ss != null ? ss.getPwsNumber0() : null;
                    break;
                case SampleWebMeta.PWS_NAME:
                    value = ss != null ? ss.getPwsName() : null;
                    break;
                case SampleWebMeta.SDWIS_STATE_LAB_ID:
                    value = ss != null ? ss.getStateLabId() : null;
                    break;
                case SampleWebMeta.SDWIS_FACILITY_ID:
                    value = ss != null ? ss.getFacilityId() : null;
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_TYPE_ID:
                    value = getDictionaryLabel(ss != null ? ss.getSampleTypeId() : null);
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID:
                    value = getDictionaryLabel(ss != null ? ss.getSampleCategoryId() : null);
                    break;
                case SampleWebMeta.SDWIS_SAMPLE_POINT_ID:
                    value = ss != null ? ss.getSamplePointId() : null;
                    break;
                case SampleWebMeta.SDWIS_LOCATION:
                    value = ss != null ? ss.getLocation() : null;
                    break;
                case SampleWebMeta.SDWIS_PRIORITY:
                    value = ss != null ? ss.getPriority() : null;
                    break;
                case SampleWebMeta.SDWIS_COLLECTOR_HEADER:
                    value = ss != null ? ss.getCollector() : null;
                    break;
                /*
                 * clinical columns
                 */
                case SampleWebMeta.CLIN_PATIENT_ID:
                    value = sc != null ? sc.getPatientId() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER:
                    value = sc != null ? sc.getPatient().getLastName() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER:
                    value = sc != null ? sc.getPatient().getFirstName() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_BIRTH_DATE:
                    value = getDateTimeLabel(sc != null ? sc.getPatient().getBirthDate() : null,
                                             Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.CLIN_PATIENT_NATIONAL_ID:
                    value = sc != null ? sc.getPatient().getNationalId() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT:
                    value = sc != null ? sc.getPatient().getAddress().getMultipleUnit() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS:
                    value = sc != null ? sc.getPatient().getAddress().getStreetAddress() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_CITY:
                    value = sc != null ? sc.getPatient().getAddress().getCity() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_STATE:
                    value = sc != null ? sc.getPatient().getAddress().getState() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE:
                    value = sc != null ? sc.getPatient().getAddress().getZipCode() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE:
                    value = sc != null ? sc.getPatient().getAddress().getHomePhone() : null;
                    break;
                case SampleWebMeta.CLIN_PATIENT_GENDER_ID:
                    value = getDictionaryLabel(sc != null ? sc.getPatient().getGenderId() : null);
                    break;
                case SampleWebMeta.CLIN_PATIENT_RACE_ID:
                    value = getDictionaryLabel(sc != null ? sc.getPatient().getRaceId() : null);
                    break;
                case SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID:
                    value = getDictionaryLabel(sc != null ? sc.getPatient().getEthnicityId() : null);
                    break;
                case SampleWebMeta.CLIN_PROVIDER_LAST_NAME:
                    if (sc != null)
                        value = sc.getProvider() != null ? sc.getProvider().getLastName() : null;
                    break;
                case SampleWebMeta.CLIN_PROVIDER_FIRST_NAME:
                    if (sc != null)
                        value = sc.getProvider() != null ? sc.getProvider().getFirstName() : null;
                    break;
                case SampleWebMeta.CLIN_PROVIDER_PHONE:
                    value = sc != null ? sc.getProviderPhone() : null;
                    break;
                /*
                 * neonatal columns
                 */
                case SampleWebMeta.NEO_PATIENT_ID:
                    value = sn != null ? sn.getPatientId() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_LAST_NAME:
                    value = sn != null ? sn.getPatient().getLastName() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_FIRST_NAME:
                    value = sn != null ? sn.getPatient().getFirstName() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_BIRTH_DATE:
                    if (rd.birthDateTime == null) {
                        /*
                         * combine the birth date and time to form the label for
                         * this column; "birthDateTime" is set to empty string
                         * and not null so that it isn't tried to be created
                         * again until the sample changes; at that time it gets
                         * set to null before entering the switch-case
                         */
                        dt = getDateTime(sn.getPatient().getBirthDate(), sn.getPatient()
                                                                           .getBirthTime());
                        rd.birthDateTime = dt != null ? getDateTimeLabel(dt,
                                                                         Messages.get()
                                                                                 .gen_dateTimePattern())
                                                     : "";
                    }
                    value = rd.birthDateTime;
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT:
                    value = sn != null ? sn.getPatient().getAddress().getMultipleUnit() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS:
                    value = sn != null ? sn.getPatient().getAddress().getStreetAddress() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_CITY:
                    value = sn != null ? sn.getPatient().getAddress().getCity() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_STATE:
                    value = sn != null ? sn.getPatient().getAddress().getState() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE:
                    value = sn != null ? sn.getPatient().getAddress().getZipCode() : null;
                    break;
                case SampleWebMeta.NEO_PATIENT_GENDER_ID:
                    value = getDictionaryLabel(sn != null ? sn.getPatient().getGenderId() : null);
                    break;
                case SampleWebMeta.NEO_PATIENT_RACE_ID:
                    value = getDictionaryLabel(sn != null ? sn.getPatient().getRaceId() : null);
                    break;
                case SampleWebMeta.NEO_PATIENT_ETHNICITY_ID:
                    value = getDictionaryLabel(sn != null ? sn.getPatient().getEthnicityId() : null);
                    break;
                case SampleWebMeta.NEO_IS_NICU:
                    value = getYesNoLabel(sn != null ? sn.getIsNicu() : null);
                    break;
                case SampleWebMeta.NEO_BIRTH_ORDER:
                    value = sn != null ? sn.getBirthOrder() : null;
                    break;
                case SampleWebMeta.NEO_GESTATIONAL_AGE:
                    value = sn != null ? sn.getGestationalAge() : null;
                    break;
                case SampleWebMeta.NEO_FEEDING_ID:
                    value = getDictionaryLabel(sn != null ? sn.getFeedingId() : null);
                    break;
                case SampleWebMeta.NEO_WEIGHT:
                    value = sn != null ? sn.getWeight() : null;
                    break;
                case SampleWebMeta.NEO_IS_TRANSFUSED:
                    value = getYesNoLabel(sn != null ? sn.getIsTransfused() : null);
                    break;
                case SampleWebMeta.NEO_TRANSFUSION_DATE:
                    value = getDateTimeLabel(sn != null ? sn.getTransfusionDate() : null,
                                             Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.NEO_IS_REPEAT:
                    value = getYesNoLabel(sn != null ? sn.getIsRepeat() : null);
                    break;
                case SampleWebMeta.NEO_COLLECTION_AGE:
                    value = sn != null ? sn.getCollectionAge() : null;
                    break;
                case SampleWebMeta.NEO_IS_COLLECTION_VALID:
                    value = getYesNoLabel(sn != null ? sn.getIsCollectionValid() : null);
                    break;
                case SampleWebMeta.NEO_FORM_NUMBER:
                    value = sn != null ? sn.getFormNumber() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ID:
                    value = sn != null ? sn.getNextOfKinId() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME:
                    value = sn != null ? sn.getNextOfKin().getLastName() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME:
                    value = sn != null ? sn.getNextOfKin().getMiddleName() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME:
                    value = sn != null ? sn.getNextOfKin().getFirstName() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID:
                    value = getDictionaryLabel(sn != null ? sn.getNextOfKinRelationId() : null);
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE:
                    value = getDateTimeLabel(sn != null ? sn.getNextOfKin().getBirthDate() : null,
                                             Messages.get().gen_datePattern());
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID:
                    value = sn != null ? sn.getNextOfKin().getNationalId() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT:
                    value = sn != null ? sn.getNextOfKin().getAddress().getMultipleUnit() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS:
                    value = sn != null ? sn.getNextOfKin().getAddress().getStreetAddress() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY:
                    value = sn != null ? sn.getNextOfKin().getAddress().getCity() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE:
                    value = sn != null ? sn.getNextOfKin().getAddress().getState() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE:
                    value = sn != null ? sn.getNextOfKin().getAddress().getZipCode() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE:
                    value = sn != null ? sn.getNextOfKin().getAddress().getHomePhone() : null;
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID:
                    value = getDictionaryLabel(sn != null ? sn.getNextOfKin().getGenderId() : null);
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID:
                    value = getDictionaryLabel(sn != null ? sn.getNextOfKin().getRaceId() : null);
                    break;
                case SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID:
                    value = getDictionaryLabel(sn != null ? sn.getNextOfKin().getEthnicityId()
                                                         : null);
                    break;
                case SampleWebMeta.NEO_PROVIDER_LAST_NAME:
                    if (sn != null)
                        value = sn.getProvider() != null ? sn.getProvider().getLastName() : null;
                    break;
                case SampleWebMeta.NEO_PROVIDER_FIRST_NAME:
                    if (sn != null)
                        value = sn.getProvider() != null ? sn.getProvider().getFirstName() : null;
                    break;
                /*
                 * pt columns
                 */
                case SampleWebMeta.PT_PT_PROVIDER_ID:
                    value = getDictionaryLabel(spt != null ? spt.getPTProviderId() : null);
                    break;
                case SampleWebMeta.PT_SERIES:
                    value = spt != null ? spt.getSeries() : null;
                    break;
                case SampleWebMeta.PT_DUE_DATE:
                    value = getDateTimeLabel(spt != null ? spt.getDueDate() : null,
                                             Messages.get().gen_dateTimePattern());
                    break;
                case SampleWebMeta.RECEIVED_BY_ID:
                    value = s.getReceivedById() != null ? userCache.getSystemUser(s.getReceivedById())
                                                                   .getLoginName()
                                                       : null;
                    break;
                default:
                    throw new InconsistencyException("Unknown column " + column);
            }
            cell = row.createCell(i);
            cell.setCellValue(DataBaseUtil.toString(value));
            setMaxChars(cell, maxChars);
        }
    }

    /**
     * Returns a Datetime object created by combining the passed date and time;
     * if the passed time is null, the time is set as 0 hours and 0 minutes;
     * returns null if the passed date is null
     */
    private Datetime getDateTime(Datetime date, Datetime time) {
        Date cd;
        Datetime cdt;

        cdt = null;
        if (date != null) {
            cd = (Date)date.getDate().clone();
            if (time == null) {
                cd.setHours(0);
                cd.setMinutes(0);
            } else {
                cd.setHours(time.getDate().getHours());
                cd.setMinutes(time.getDate().getMinutes());
            }

            cdt = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, cd);
        }

        return cdt;
    }

    /**
     * Returns a string version of the passed Datetime object formatted using
     * the passed pattern; returns null if the Datetime object is null
     */
    private String getDateTimeLabel(Datetime dt, String pattern) {
        String val;

        val = null;
        if (dt != null)
            val = ReportUtil.toString(dt, pattern);

        return val;
    }

    /**
     * Returns the dictionary entry for the passed id or null if the id is null
     */
    private String getDictionaryLabel(Integer dictId) throws Exception {
        String val;

        val = null;
        if (dictId != null)
            val = dictionaryCache.getById(dictId).getEntry();

        return val;
    }

    /**
     * Returns "Yes" or "No" based on the value of the passed flag ("Y"/"N");
     * returns null if the flag is null; the flag can be null if the column was
     * selected by the user but is not valid for the row e.g. analysis
     * is_reportable for a row showing aux data
     */
    private String getYesNoLabel(String flag) {
        if (flag == null)
            return null;

        return "Y".equals(flag) ? Messages.get().gen_yes() : Messages.get().gen_no();
    }
    
    /**
     * The passed list contains the maximum number of characters in each column
     * of the sheet; if the passed cell's value has more characters than the
     * current number for the cell's column, the number in the list is updated
     */
    private void setMaxChars(Cell cell, ArrayList<Integer> maxChars) {
        int col, chars;
        String val;

        col = cell.getColumnIndex();
        if (col > maxChars.size()-1)
            maxChars.add(0);
        val = cell.getStringCellValue();
        chars = !DataBaseUtil.isEmpty(val) ? val.length() : 0; 
        maxChars.set(col, Math.max(chars, maxChars.get(col)));
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

            if ( !DataBaseUtil.isSame(prevCell.getStringCellValue(), currCell.getStringCellValue()))
                return false;
        }

        return true;
    }

    /**
     * Some rows in the report have the same data in all columns except the
     * analyte and value, because they belong to the same sample, sample item or
     * analysis; but the data for some of those columns is obtained by looping
     * e.g. analysis received by or combining two fields e.g. collected
     * date-time; this class holds such data so that it can be reused until the
     * sample, sample item or analysis changes
     */ 
    private class RowData {
        Integer                  sampleId, sampleItemId, analysisId;
        String                   collDateTime, projName, completedBy, releasedBy, analysisQAs,
                        birthDateTime;
        SampleOrganizationViewDO repToOrg;
        SampleItemViewDO         sampleItem;
        AnalysisViewDO           analysis;

        public void clear() {
            sampleId = null;
            sampleItemId = null;
            analysisId = null;
            collDateTime = null;
            projName = null;
            completedBy = null;
            releasedBy = null;
            analysisQAs = null;
            birthDateTime = null;
            repToOrg = null;
            sampleItem = null;
            analysis = null;
        }
    }

    /**
     * This class is used for sorting the list of aux data fetched for
     * generating the data view report
     */
    private class DataViewComparator implements Comparator<DataViewResultVO> {
        public int compare(DataViewResultVO aux1, DataViewResultVO aux2) {
            int diff;
            Integer accNum1, accNum2;
            String name1, name2;

            accNum1 = aux1.getSampleAccessionNumber();
            accNum2 = aux2.getSampleAccessionNumber();
            name1 = aux1.getAnalyteName();
            name2 = aux2.getAnalyteName();

            /*
             * names of the analytes are compared only if the accession numbers
             * are different
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