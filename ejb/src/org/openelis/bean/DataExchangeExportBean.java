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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.EOrderDO;
import org.openelis.domain.EOrderLinkDO;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleQcVO;
import org.openelis.domain.WorksheetQcResultViewVO;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.QueryBuilderV2;
import org.openelis.utils.ReportUtil;
import org.w3c.dom.Document;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class DataExchangeExportBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager             manager;

    @EJB
    private SessionCacheBean          session;

    @EJB
    private SampleBean                sample;

    @EJB
    private EventLogBean              eventLog;

    @EJB
    private DataExchangeXMLMapperBean dataExchangeXMLMapper;

    @EJB
    private SystemVariableBean        systemVariable;

    @EJB
    private SampleManager1Bean        sampleManager;

    @EJB
    private DictionaryBean            dictionary;

    @EJB
    private WorksheetQcResultBean     worksheetQcResult;

    @EJB
    private EOrderBean                eOrder;

    @EJB
    private EOrderLinkBean            eOrderLink;

    private Transformer               transformer;

    private ByteArrayOutputStream     transformerStream;

    private Date                      releaseStart, releaseEnd;

    private static final Logger       log = Logger.getLogger("openelis");

    /**
     * This method is called from the cron bean to execute a defined exchange
     * criteria specified by name. The criteria generates a list of samples and
     * exports them to a location specified in the exchange criteria. The method
     * also add an entry to the event log containing the list of samples or a
     * message stating any problems encountered during the process.
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void export(String name) {
        int accession;
        EOrderDO eo;
        Calendar cal;
        Date now;
        StringBuilder sb;
        ArrayList<Integer> ids;
        ArrayList<EOrderLinkDO> eols;
        ArrayList<EventLogDO> elogs;
        ArrayList<SampleManager1> sms;
        ExchangeCriteriaManager cm;

        try {
            cm = ExchangeCriteriaManager.fetchByName(name);
        } catch (NotFoundException e) {
            addEventLog(Messages.get().dataExchange_noCriteriaFoundException(name),
                        Constants.dictionary().LOG_LEVEL_ERROR);
            log.log(Level.SEVERE, Messages.get().dataExchange_noCriteriaFoundException(name), e);
            return;
        } catch (Exception e) {
            addEventLog(e.getMessage(), Constants.dictionary().LOG_LEVEL_ERROR);
            log.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        /*
         * we need to figure out the last time this exchange criteria "job" was
         * run. For first time job, just set the start time to ending time (no
         * samples). Ending time of released samples/analysis is a minute ago.
         */
        cal = Calendar.getInstance();
        now = cal.getTime();
        cal.add(Calendar.MINUTE, -1);
        releaseEnd = cal.getTime();
        try {
            elogs = eventLog.fetchByRefTableIdRefId(Constants.table().EXCHANGE_CRITERIA,
                                                    cm.getExchangeCriteria().getId());
            releaseStart = elogs.get(0).getTimeStamp().getDate();
        } catch (NotFoundException e) {
            releaseStart = releaseEnd;
        } catch (Exception e) {
            addEventLog(Messages.get().dataExchange_lastRunFetchException(name),
                        Constants.dictionary().LOG_LEVEL_ERROR);
            log.log(Level.SEVERE, Messages.get().dataExchange_lastRunFetchException(name), e);
            return;
        }

        /*
         * since we don't have "or" capability in our generic query data, we
         * have to do this twice; get the sample ids and then get manager for
         * all the samples with everything loaded.
         */
        try {
            ids = getSamples(SampleMeta.getId(), cm.getExchangeCriteria().getFields());
            sms = sampleManager.fetchByIds(ids,
                                           SampleManager1.Load.ORGANIZATION,
                                           SampleManager1.Load.PROJECT,
                                           SampleManager1.Load.QA,
                                           SampleManager1.Load.AUXDATA,
                                           SampleManager1.Load.STORAGE,
                                           SampleManager1.Load.NOTE,
                                           SampleManager1.Load.ANALYSISUSER,
                                           SampleManager1.Load.RESULT,
                                           SampleManager1.Load.PROVIDER);

            /*
             * log all the accession numbers; the ones generated with the
             * successful execution are logged as is whereas the ones with a
             * failure are logged as negative numbers
             */
            sb = new StringBuilder();
            messageStart();
            for (SampleManager1 sm : sms) {
                eo = null;
                eols = null;
                accession = getSample(sm).getAccessionNumber();
                if ( (getSampleClinical(sm) != null || getSampleNeonatal(sm) != null) &&
                    getSample(sm).getOrderId() != null) {
                    try {
                        eo = eOrder.fetchById(getSample(sm).getOrderId());
                        eols = eOrderLink.fetchByEOrderId(getSample(sm).getOrderId());
                    } catch (NotFoundException e) {
                        log.log(Level.SEVERE,
                                "E-Order/E-OrderLink record not found for accession " + accession,
                                e);
                    }
                }
                try {
                    messageOutput(sm, cm, eo, eols);
                } catch (Exception e) {
                    accession = -accession;
                }
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(accession);
            }

            addEventLog(Messages.get().dataExchange_executedCriteria(name),
                        cm.getExchangeCriteria().getId(),
                        Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION,
                        now,
                        sb.toString());
        } catch (NotFoundException e) {
            addEventLog(Messages.get().dataExchange_noSamplesFound(name),
                        cm.getExchangeCriteria().getId(),
                        Constants.dictionary().LOG_LEVEL_INFO,
                        null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            messageEnd();
            releaseStart = null;
            releaseEnd = null;
        }
    }

    /**
     * The method is called from the screen interface to message a set of
     * accession numbers. The exchange criteria is used for creating the
     * message; it is not used to query for samples since that is already given
     * by the list.
     */
    public ReportStatus export(ArrayList<Integer> accessions, ExchangeCriteriaManager cm) throws Exception {
        EOrderDO eo;
        ReportStatus status;
        ArrayList<Integer> ids;
        ArrayList<EOrderLinkDO> eols;
        ArrayList<SampleManager1> sms;

        if (accessions == null || accessions.size() == 0)
            throw new Exception(Messages.get().dataExchange_noAccessionException());

        if (DataBaseUtil.isEmpty(cm.getExchangeCriteria().getDestinationUri()))
            throw new Exception(Messages.get().dataExchange_noUriException());

        status = new ReportStatus();
        status.setMessage(Messages.get().gen_initializing());
        session.setAttribute("DataExchange", status);

        ids = getSamples(accessions);
        if (ids.size() == 0)
            throw new NotFoundException();

        log.log(Level.FINE, "Fetching" + ids.size() + " samples");
        sms = sampleManager.fetchByIds(ids,
                                       SampleManager1.Load.ORGANIZATION,
                                       SampleManager1.Load.PROJECT,
                                       SampleManager1.Load.QA,
                                       SampleManager1.Load.AUXDATA,
                                       SampleManager1.Load.STORAGE,
                                       SampleManager1.Load.NOTE,
                                       SampleManager1.Load.ANALYSISUSER,
                                       SampleManager1.Load.RESULT,
                                       SampleManager1.Load.PROVIDER);

        try {
            releaseStart = null;
            releaseEnd = null;
            messageStart();
            status.setMessage(Messages.get().gen_generatingReport());
            for (SampleManager1 sm : sms) {
                eo = null;
                eols = null;
                if ( (getSampleClinical(sm) != null || getSampleNeonatal(sm) != null) &&
                    getSample(sm).getOrderId() != null) {
                    log.log(Level.FINE, "Fetching eorder with id " + getSample(sm).getOrderId());
                    try {
                        eo = eOrder.fetchById(getSample(sm).getOrderId());
                        eols = eOrderLink.fetchByEOrderId(getSample(sm).getOrderId());
                    } catch (NotFoundException e) {
                        status.setMessage(status.getMessage() +
                                          "; E-Order/E-OrderLink record not found for accession " +
                                          getSample(sm).getAccessionNumber());
                    }
                }
                messageOutput(sm, cm, eo, eols);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to generate xml", e);
            throw e;
        } finally {
            messageEnd();
        }

        return status;
    }

    /**
     * The method is called from the screen interface to message a set of QC
     * data and an accession number. The exchange criteria is used for creating
     * the message; it is not used to query for samples since that is already
     * given.
     */
    public ReportStatus export(SampleQcVO sqc) throws Exception {
        String name;
        EOrderDO eo;
        ReportStatus status;
        ExchangeCriteriaManager cm;
        SampleManager1 sm;
        ArrayList<String> names;
        ArrayList<Integer> ids, accessions, qcAnalyteIds;
        ArrayList<EOrderLinkDO> eols;
        ArrayList<WorksheetQcResultViewVO> qcAnalytes, analysisQcAnalytes;
        ArrayList<DictionaryDO> dictList;
        HashSet<Integer> formatIds;
        HashMap<Integer, String[]> formats;

        if (sqc == null || sqc.getAccession() == null)
            throw new Exception(Messages.get().dataExchange_noAccessionException());

        try {
            name = systemVariable.fetchByName("sample_qc_exchange_criteria").getValue();
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("sample_qc_exchange_criteria"),
                    e);
            throw e;
        }

        status = new ReportStatus();
        session.setAttribute("DataExchange", status);

        try {
            cm = ExchangeCriteriaManager.fetchByName(name);
        } catch (NotFoundException e) {
            addEventLog(Messages.get().dataExchange_noCriteriaFoundException(name),
                        Constants.dictionary().LOG_LEVEL_ERROR);
            log.log(Level.SEVERE, Messages.get().dataExchange_noCriteriaFoundException(name), e);
            throw e;
        } catch (Exception e) {
            addEventLog(e.getMessage(), Constants.dictionary().LOG_LEVEL_ERROR);
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        if (DataBaseUtil.isEmpty(cm.getExchangeCriteria().getDestinationUri()))
            throw new Exception(Messages.get().dataExchange_noUriException());

        status.setMessage(Messages.get().gen_initializing());

        accessions = new ArrayList<Integer>();
        accessions.add(sqc.getAccession());
        ids = getSamples(accessions);
        if (ids.size() == 0)
            throw new NotFoundException();

        sm = sampleManager.fetchById(ids.get(0),
                                     SampleManager1.Load.ORGANIZATION,
                                     SampleManager1.Load.PROJECT,
                                     SampleManager1.Load.QA,
                                     SampleManager1.Load.AUXDATA,
                                     SampleManager1.Load.STORAGE,
                                     SampleManager1.Load.NOTE,
                                     SampleManager1.Load.ANALYSISUSER,
                                     SampleManager1.Load.RESULT);

        formatIds = new HashSet<Integer>();
        qcAnalyteIds = new ArrayList<Integer>();

        /*
         * get the analytes
         */
        for (ArrayList<Integer> qcaids : sqc.getQcAnalyteIds())
            qcAnalyteIds.addAll(qcaids);
        qcAnalytes = worksheetQcResult.fetchViewByIds(qcAnalyteIds);
        for (ArrayList<Integer> qcaids : sqc.getQcAnalyteIds()) {
            analysisQcAnalytes = new ArrayList<WorksheetQcResultViewVO>();
            for (Integer qcaid : qcaids) {
                for (WorksheetQcResultViewVO wqcrvvo : qcAnalytes) {
                    if (wqcrvvo.getId().equals(qcaid))
                        analysisQcAnalytes.add(wqcrvvo);
                }
            }
            sqc.addQcAnalytes(analysisQcAnalytes);
        }

        /*
         * get the analyte result column names
         */
        for (WorksheetQcResultViewVO wqcrvvo : qcAnalytes)
            formatIds.add(wqcrvvo.getFormatId());
        formats = new HashMap<Integer, String[]>();
        for (Integer i : formatIds) {
            dictList = dictionary.fetchByCategorySystemName(dictionary.fetchById(i).getSystemName());
            names = new ArrayList<String>();
            for (DictionaryDO dict : dictList)
                names.add(dict.getEntry());
            formats.put(i, names.toArray(new String[names.size()]));
        }

        for (WorksheetQcResultViewVO wqcrvvo : qcAnalytes) {
            wqcrvvo.setNames(formats.get(wqcrvvo.getFormatId()));
        }

        eo = null;
        eols = null;
        if ( (getSampleClinical(sm) != null || getSampleNeonatal(sm) != null) &&
            getSample(sm).getOrderId() != null) {
            try {
                eo = eOrder.fetchById(getSample(sm).getOrderId());
                eols = eOrderLink.fetchByEOrderId(getSample(sm).getOrderId());
            } catch (NotFoundException e) {
                log.log(Level.SEVERE, "E-Order/E-OrderLink record not found for accession " +
                                      getSample(sm).getAccessionNumber(), e);
            }
        }

        try {
            releaseStart = null;
            releaseEnd = null;
            messageStart();
            messageOutput(sm, cm, eo, eols, sqc);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to generate xml for accession number:  " +
                                  getSample(sm).getAccessionNumber(), e);
            status.setMessage("Failed to generate xml");
            return status;
        } finally {
            messageEnd();
        }

        status.setStatus(ReportStatus.Status.SAVED);
        status.setMessage("XML writted to " + cm.getExchangeCriteria().getDestinationUri());
        return status;
    }

    /**
     * This method is called from the screen during the testing to retrieve a
     * list of accession numbers that match the query.
     */
    public ArrayList<Integer> getSamples(ExchangeCriteriaManager cm) throws Exception {
        return getSamples(SampleMeta.getAccessionNumber(), cm.getExchangeCriteria().getFields());
    }

    /**
     * This method returns a list of keys (sample ids or accession numbers) that
     * match the query. The start and end dates are added to the query if the
     * The query does not have a date released.
     */
    private ArrayList<Integer> getSamples(String select, ArrayList<QueryData> fields) throws Exception {
        int i;
        Query query;
        String dates[];
        Datetime dt;
        QueryData field;
        QueryFieldUtil qf;
        ArrayList<String> range;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(new SampleMeta());
        builder.setSelect("distinct " + select);

        /*
         * map organization to report_to if released date is part of the query,
         * parse it and remove it; the dates are parsed simply either as a range
         * date..date or >date.
         */
        i = 0;
        while (i < fields.size()) {
            field = fields.get(i);
            if (SampleMeta.getOrgId().equals(field.getKey())) {
                builder.constructWhere(field);
                builder.addWhere(SampleMeta.getSampleOrgTypeId() + "=" +
                                 Constants.dictionary().ORG_REPORT_TO);
                i++ ;
            } else if (SampleMeta.getAnalysisReleasedDate().equals(field.getKey())) {
                qf = new QueryFieldUtil();
                qf.parse(field.getQuery());
                range = qf.getParameter();
                if (range.size() > 0 && !"NULL".equals(range.get(0))) {
                    dates = range.get(0).split("\\.\\.");
                    if (dates.length > 0) {
                        dt = ReportUtil.getDatetime(dates[0]);
                        releaseStart = dt != null ? dt.getDate() : null;
                    }
                    if (dates.length > 1) {
                        dt = ReportUtil.getDatetime(dates[1]);
                        releaseEnd = dt != null ? dt.getDate() : null;
                    }
                    if (releaseEnd == null)
                        releaseEnd = new Date();
                }
                fields.remove(i);
            } else {
                builder.constructWhere(field);
                i++ ;
            }
        }

        /*
         * search for any sample OR any analysis released between the specified
         * dates
         */
        if (releaseStart != null && releaseEnd != null) {
            builder.addWhere("(" +
                             SampleMeta.getReleasedDate() +
                             " between '" +
                             ReportUtil.toString(releaseStart, Messages.get()
                                                                       .dateTimeSecondPattern()) +
                             "' and '" +
                             ReportUtil.toString(releaseEnd, Messages.get().dateTimeSecondPattern()) +
                             "' or " +
                             SampleMeta.getAnalysisReleasedDate() +
                             " between '" +
                             ReportUtil.toString(releaseStart, Messages.get()
                                                                       .dateTimeSecondPattern()) +
                             "' and '" +
                             ReportUtil.toString(releaseEnd, Messages.get().dateTimeSecondPattern()) +
                             "')");
        }
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + Constants.dictionary().SAMPLE_ERROR);
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                         Constants.dictionary().ANALYSIS_RELEASED);
        builder.setOrderBy(select);

        query = manager.createQuery(builder.getEJBQL());
        QueryBuilderV2.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    /**
     * This method returns a list of sample ids that match the list of
     * accessions.
     */
    private ArrayList<Integer> getSamples(ArrayList<Integer> accessions) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>(accessions.size());
        for (SampleDO data : sample.fetchByAccessionNumbers(accessions))
            ids.add(data.getId());

        return ids;
    }

    /**
     * initializes the xslt transformer and the output buffer
     */
    private void messageStart() throws Exception {
        ClassLoader loader;
        InputStream xslt;

        loader = Thread.currentThread().getContextClassLoader();
        xslt = loader.getResourceAsStream("dataExchangeDefault.xsl");
        transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));

        transformerStream = new ByteArrayOutputStream();
    }

    private void messageEnd() {
        try {
            if (transformerStream != null)
                transformerStream.close();
        } catch (Exception e) {
        } finally {
            transformer = null;
            transformerStream = null;
        }
    }

    /**
     * The method exports a sample using the criteria manager's information.
     */
    private void messageOutput(SampleManager1 sm, ExchangeCriteriaManager cm, Object... optional) throws Exception {
        Integer accession;
        URI uri;
        Document doc;
        DOMSource dom;
        File outfile;
        Socket outsocket;
        OutputStream out;

        out = null;
        outsocket = null;
        accession = sm.getSample().getAccessionNumber();

        try {
            /*
             * generate a simple xml and use a simple buffer in case we have an
             * error
             */
            log.log(Level.FINE, "Generating xml for accession number:  " +
                                getSample(sm).getAccessionNumber());
            doc = dataExchangeXMLMapper.getXML(sm, cm, releaseStart, releaseEnd, optional);
            dom = new DOMSource(doc);
            transformer.transform(dom, new StreamResult(transformerStream));

            /*
             * open and copy to destination
             */
            log.log(Level.FINE, "Sending transformed xml to the destination:  " +
                                cm.getExchangeCriteria().getDestinationUri());
            uri = new URI(cm.getExchangeCriteria().getDestinationUri());
            if ("file".equals(uri.getScheme())) {
                outfile = new File(uri.getPath() + File.separator + accession.toString() +
                                   "_exchange.xml");
                outfile.setExecutable(false, false);
                outfile.setReadable(true, false);
                outfile.setWritable(true, false);
                outfile.createNewFile();
                out = new FileOutputStream(outfile);
                out.write(transformerStream.toByteArray());
            } else if ("socket".equals(uri.getScheme())) {
                outsocket = new Socket(uri.getHost(), uri.getPort());
                out = outsocket.getOutputStream();
                out.write(transformerStream.toByteArray());
            }
            transformerStream.reset();
            log.log(Level.FINE, "XML sent to the destination");
        } finally {
            if (out != null)
                out.close();
            if (outsocket != null)
                outsocket.close();
        }
    }

    /*
     * Convenience methods for adding event logs
     */
    private void addEventLog(String source, Integer referenceId, Integer levelId, Date timestamp,
                             String text) {
        try {
            eventLog.add(new EventLogDO(null,
                                        Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION,
                                        source,
                                        Constants.table().EXCHANGE_CRITERIA,
                                        referenceId,
                                        levelId,
                                        null,
                                        timestamp,
                                        text));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add log entry for: " + source, e);
        }
    }

    private void addEventLog(String source, Integer referenceId, Integer levelId, String text) {
        addEventLog(source, referenceId, levelId, null, text);
    }

    private void addEventLog(String source, Integer levelId) {
        addEventLog(source, null, levelId, null, null);
    }
}