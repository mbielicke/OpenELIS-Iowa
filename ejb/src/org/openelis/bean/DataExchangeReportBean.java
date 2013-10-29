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
import org.openelis.domain.EventLogDO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
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
public class DataExchangeReportBean {

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
    private SampleManager1Bean        sampleManager;

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
        Calendar cal;
        Date releaseStart, releaseEnd;
        StringBuilder sb;
        ArrayList<Integer> ids;
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
         * run. For first time job, just set the start time to ending time (no samples).
         * Ending time of released samples/analysis is a minute ago.
         */
        cal = Calendar.getInstance();
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
            ids = getSamples(SampleMeta.getId(),
                             cm.getExchangeCriteria().getFields(),
                             releaseStart,
                             releaseEnd);
            sms = sampleManager.fetchByIds(ids,
                                           SampleManager1.Load.ORGANIZATION,
                                           SampleManager1.Load.PROJECT,
                                           SampleManager1.Load.QA,
                                           SampleManager1.Load.AUXDATA,
                                           SampleManager1.Load.STORAGE,
                                           SampleManager1.Load.NOTE,
                                           SampleManager1.Load.ANALYSISUSER,
                                           SampleManager1.Load.RESULT);
            message(sms, cm);

            /*
             * log all the accession numbers that were generated with the
             * successful execution
             */
            sb = new StringBuilder();
            for (SampleManager1 sm : sms) {
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(SampleManager1Accessor.getSample(sm).getAccessionNumber());
            }
            addEventLog(Messages.get().dataExchange_executedCriteria(name),
                        cm.getExchangeCriteria().getId(),
                        Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION,
                        releaseEnd,
                        sb.toString());
        } catch (NotFoundException e) {
            addEventLog(Messages.get().dataExchange_noSamplesFound(name),
                        cm.getExchangeCriteria().getId(),
                        Constants.dictionary().LOG_LEVEL_INFO,
                        null);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * The method is called from the screen interface to message a set of
     * accession numbers. The exchange criteria is used for creating the
     * message; it is not used to query for samples since that is already given
     * by the list.
     */
    public ReportStatus export(ArrayList<Integer> accessions, ExchangeCriteriaManager cm) throws Exception {
        ReportStatus status;
        ArrayList<Integer> ids;
        ArrayList<SampleManager1> sms;

        if (accessions == null || accessions.size() == 0)
            throw new Exception(Messages.get().dataExchange_noAccessionException());

        if (DataBaseUtil.isEmpty(cm.getExchangeCriteria().getDestinationUri()))
            throw new Exception(Messages.get().dataExchange_noUriException());

        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataExchange", status);

        ids = getSamples(accessions);
        sms = sampleManager.fetchByIds(ids,
                                       SampleManager1.Load.ORGANIZATION,
                                       SampleManager1.Load.PROJECT,
                                       SampleManager1.Load.QA,
                                       SampleManager1.Load.AUXDATA,
                                       SampleManager1.Load.STORAGE,
                                       SampleManager1.Load.NOTE,
                                       SampleManager1.Load.ANALYSISUSER,
                                       SampleManager1.Load.RESULT);
        message(sms, cm);

        return status;
    }

    /**
     * This method is called from the screen during the testing to retrieve a
     * list of accession numbers that match the query.
     */
    public ArrayList<Integer> getSamples(ExchangeCriteriaManager cm) throws Exception {
        return getSamples(SampleMeta.getAccessionNumber(),
                          cm.getExchangeCriteria().getFields(),
                          null,
                          null);
    }

    /**
     * This method returns a list of keys (sample ids or accession numbers) that
     * match the query. The start and end dates are added to the query if the
     * The query does not have a date released.
     */
    private ArrayList<Integer> getSamples(String select, ArrayList<QueryData> fields, Date start,
                                          Date end) throws Exception {
        int i;
        Query query;
        String dates[];
        Datetime dt;
        QueryData field;
        QueryFieldUtil qf;
        ArrayList<String> range;
        QueryBuilderV2 builder;

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
                i++;
            } else if (SampleMeta.getReleasedDate().equals(field.getKey())) {
                qf = new QueryFieldUtil();
                qf.parse(field.getQuery());
                range = qf.getParameter();
                if (range.size() > 0 && !"NULL".equals(range.get(0))) {
                    dates = range.get(0).split("\\.\\.");
                    if (dates.length > 0) {
                        dt = ReportUtil.getDatetime(dates[0]);
                        start = dt != null ? dt.getDate() : null;
                    }
                    if (dates.length > 1) {
                        dt = ReportUtil.getDatetime(dates[1]);
                        end = dt != null ? dt.getDate() : null;
                    }
                    if (end == null)
                        end = new Date();
                }
                fields.remove(i);
            } else {
                builder.constructWhere(field);
                i++;
            }
        }

        /*
         * search for any sample OR any analysis released between the specified
         * dates
         */
        if (start != null && end != null) {
            builder.addWhere("(" + SampleMeta.getReleasedDate() + " between '" +
                             ReportUtil.toString(start, Messages.get().dateTimePattern()) +
                             "' and '" +
                             ReportUtil.toString(end, Messages.get().dateTimePattern()) + "' or " +
                             SampleMeta.getAnalysisReleasedDate() + " between '" +
                             ReportUtil.toString(start, Messages.get().dateTimePattern()) +
                             "' and '" +
                             ReportUtil.toString(end, Messages.get().dateTimePattern()) + "')");
        }
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + Constants.dictionary().SAMPLE_ERROR);
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" +
                         Constants.dictionary().ANALYSIS_RELEASED);
        builder.setOrderBy(select);

        query = manager.createQuery(builder.getEJBQL());
        QueryBuilderV2.setQueryParams(query, fields);
        return DataBaseUtil.toArrayList(query.getResultList());
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
     * The method exports each sample using the criteria manager's information.
     */
    private void message(ArrayList<SampleManager1> sms, ExchangeCriteriaManager cm) throws Exception {
        URI uri;
        Document doc;
        ClassLoader loader;
        Transformer transformer;
        DOMSource dom;
        InputStream xslt;
        File outfile;
        Socket outsocket;
        OutputStream out;
        ByteArrayOutputStream ba;

        /*
         * the xslt transforms the simple flat xml to an organized
         * sample/analysis/result nested structure.
         */
        loader = Thread.currentThread().getContextClassLoader();
        xslt = loader.getResourceAsStream("dataExchangeDefault.xsl");
        transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));

        ba = new ByteArrayOutputStream();
        for (SampleManager1 sm : sms) {
            out = null;
            outsocket = null;
            try {
                /*
                 * generate a simple xml and use a simple buffer in case we have
                 * an error
                 */
                doc = dataExchangeXMLMapper.getXML(sm, cm);
                dom = new DOMSource(doc);
                transformer.transform(dom, new StreamResult(ba));

                /*
                 * open and copy to destination
                 */
                uri = new URI(cm.getExchangeCriteria().getDestinationUri());
                if ("file".equals(uri.getScheme())) {
                    outfile = new File(uri.getPath() + File.separator +
                                       sm.getSample().getAccessionNumber().toString() +
                                       "_exchange.xml");
                    outfile.setExecutable(false, false);
                    outfile.setReadable(true, false);
                    outfile.setWritable(true, false);
                    outfile.createNewFile();
                    out = new FileOutputStream(outfile);
                    out.write(ba.toByteArray());
                } else if ("socket".equals(uri.getScheme())) {
                    outsocket = new Socket(uri.getHost(), uri.getPort());
                    out = outsocket.getOutputStream();
                    out.write(ba.toByteArray());
                }
                ba.reset();

                log.fine("Generated xml for accession number: " +
                         sm.getSample().getAccessionNumber());
            } finally {
                if (out != null)
                    out.close();
                if (outsocket != null)
                    outsocket.close();
            }
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