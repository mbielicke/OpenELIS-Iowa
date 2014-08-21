/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.xml.transform.stream.StreamResult;

import org.jboss.security.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.Constants;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.SampleManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.openelis.utils.EJBFactory;
import org.w3c.dom.Document;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)

public class DataExchangeReportBean {

    @EJB
    private SessionCacheBean          session;
    
    @EJB
    private SampleBean                 sample;

    @EJB
    private EventLogBean               eventLog;
    
    @EJB
    private DictionaryBean             dictionary;

    @EJB
    private DataExchangeXMLMapperBean dataExchangeXMLMapper;
    
    @EJB
    private ExchangeCriteriaBean      exchangeCriteria;
    
    @EJB
    private UserCacheBean             userCache;

    private static final Logger      log = Logger.getLogger("openelis");

    private static String             FILE_PREFIX = "file://", SOCKET_PREFIX = "socket://";

    private static UTFResource        resource;    
    
    @PostConstruct
    public void init() {
        String locale;
        
        try {
            if (resource == null) {
                try {
                    locale = userCache.getLocale();
                } catch (Exception e) {
                    locale = "en";
                }
                resource = UTFResource.getBundle("org.openelis.constants.OpenELISConstants",  new Locale(locale));
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to initialize resource bundle", e);
        }
    }
    
    public ArrayList<Prompt> getPrompts() throws Exception {
        throw new InconsistencyException("Method not supported");        
    }
    
    /**
     * Execute the query defined in the exchange criteria specified by the parameter
     * "name" to generate the list of samples that would be used to create the message
     * sent to the location specified in the exchange criteria. Add an entry to 
     * the event log containing the list of samples if there were found any or a 
     * message stating any problems encountered duing the whole process.     
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport(String name) {
        String text;
        Calendar cal;        
        Date lastRunDate;
        ArrayList<EventLogDO> elogs;
        ArrayList<Integer> accNums;
        ArrayList<QueryData> fields;
        ArrayList<IdAccessionVO> samples;
        ExchangeCriteriaManager man;
        ExchangeCriteriaViewDO data;        

        try {
            man = ExchangeCriteriaManager.fetchByName(name);
            data = man.getExchangeCriteria();
        } catch (NotFoundException e) {
            addLogEntry("dataExchangeCriteriaNotFound", name, null, Constants.dictionary().LOG_LEVEL_ERROR, null);
            log.log(Level.SEVERE, getSource("dataExchangeCriteriaNotFound", name), e);
            return;            
        } catch (Exception e) {
            addLogEntry("dataExchangeCriteriaFetchFailed", name, null, Constants.dictionary().LOG_LEVEL_ERROR, null);
            log.log(Level.SEVERE, getSource("dataExchangeCriteriaFetchFailed", name), e);
            return;
        }
        
        cal = Calendar.getInstance();
        try {
            elogs = eventLog.fetchByRefTableIdRefId(Constants.table().EXCHANGE_CRITERIA, data.getId());
            lastRunDate = elogs.get(0).getTimeStamp().getDate();
        } catch (NotFoundException e) {
            /*
             * if this is the first time that this query is being executed then
             * set the last run date to one day before right now
             */
            cal.add(Calendar.DAY_OF_MONTH, -1);
            lastRunDate = cal.getTime();         
        } catch (Exception e) {
            addLogEntry("dataExchangeLastRunDateFetchFailed", name, null, Constants.dictionary().LOG_LEVEL_ERROR, null);
            log.log(Level.SEVERE, getSource("dataExchangeLastRunDateFetchFailed", name), e);
            return;
        }
        
        fields = data.getFields();
        if (fields != null) {
            try {                                
                /*
                 * query for the samples 
                 */
                samples = sample.fetchSamplesByLastRunDate(fields, lastRunDate);
                accNums = new ArrayList<Integer>();
                for (IdAccessionVO s : samples) 
                    accNums.add(s.getAccessionNumber());
                
                sendMessages(accNums, data.getDestinationUri(), man, null);
                
                text = DataBaseUtil.concatWithSeparator(accNums, ",");
                
                /*
                 * generate the message for the successful execution of the criteria
                 */
                addLogEntry("dataExchangeExecutedCriteria", name, data.getId(), Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION, text);
            } catch (NotFoundException e) {
                addLogEntry("dataExchangeNoSamplesFound", name, data.getId(), Constants.dictionary().LOG_LEVEL_INFO, null);
            } catch (Exception e) {
                addLogEntry("dataExchangeCouldNotExecuteCriteria", name, data.getId(), Constants.dictionary().LOG_LEVEL_ERROR, null);
                log.log(Level.SEVERE, getSource("dataExchangeCouldNotExecuteCriteria", name), e);
            }
        }
    }
    
    public ReportStatus exportToLocation(ArrayList<Integer> accessionNumbers, String uri,
                                 Integer exchangeCriteriaId) throws Exception {
        ExchangeCriteriaManager criteriaMan;
        ValidationErrorsList errors;
        ReportStatus status;

        if (accessionNumbers == null || accessionNumbers.size() == 0)
            throw new Exception("The list of accession numbers must not be empty");
        
        if (DataBaseUtil.isEmpty(uri))
            throw new Exception("The URI must not be blank");
        
        errors = new ValidationErrorsList();        
        exchangeCriteria.validateDestinationURI(uri, errors);
        
        if (errors.size() > 0) 
            throw errors;
            
        criteriaMan = null;
        
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataExchange", status);
        
        /*
         * the id of the exchange criteria can be null if the user wants to export 
         * to the location in add or default state 
         */
        if (exchangeCriteriaId != null)
            criteriaMan = ExchangeCriteriaManager.fetchWithProfiles(exchangeCriteriaId);        
        
        sendMessages(accessionNumbers, uri, criteriaMan, status);
        
        return status;
    }

    private void sendMessages(ArrayList<Integer> accessionNumbers,  String uri,
                             ExchangeCriteriaManager criteriaMan, ReportStatus status) throws Exception {
        
        Document doc;
        BufferedWriter writer;
        ByteArrayOutputStream byteOut;
        File tempFile, dir;
        FileOutputStream out;
        InputStream in;
        SampleManager man;
        ClassLoader loader;        
        
        out = null;
        in = null;
        writer = null;        
        byteOut = null;
        
        if (uri.startsWith(FILE_PREFIX)) {
            try {
                uri = uri.replaceAll(FILE_PREFIX, "");
                
                if (!uri.startsWith(File.separator))
                    uri = File.separator + uri;  
                
                dir = new File(uri);                
                
                loader = Thread.currentThread().getContextClassLoader();
                
                /*
                 * iterate through the list of accession numbers and generate the 
                 * xml for each sample
                 */
                for (Integer accNum : accessionNumbers) {
                    man = SampleManager.fetchWithAllDataByAccessionNumber(accNum);
                    
                    tempFile = File.createTempFile(DataBaseUtil.concat(accNum,"_dataExchange"), ".xml", dir);
                    out = new FileOutputStream(tempFile);
                    writer = new BufferedWriter(new FileWriter(tempFile));
                    
                    /*
                     * generate the original i.e. not transformed xml
                     */                    
                    doc = dataExchangeXMLMapper.getXML(man, criteriaMan);
                    /*
                     * apply the transformation based on the xsl file and write 
                     * the final xml to a file in the directory specified by the
                     * user 
                     */
                    byteOut = new ByteArrayOutputStream();        
                    in = loader.getResourceAsStream("dataExchangeDefault.xsl");
                    XMLUtil.transformXML(doc, in, new StreamResult(byteOut));
                    out.write(byteOut.toByteArray());

                    log.fine("Generated xml for accession number: " + accNum);
                    
                    writer.close();
                    in.close();
                    byteOut.close();
                    out.close();
                }
                
                if (status != null)
                    status.setMessage("Exported xml for specified samples to the location");
            } catch (Exception e) {
                throw e;                
            } finally {
                if (writer != null)
                    writer.close();
                if (in != null)
                    in.close();
                if (byteOut != null)
                    byteOut.close();
                if (out != null)
                    out.close();
            }
        } else if (uri.startsWith(SOCKET_PREFIX)) {
            
        }
    }
    
    private void addLogEntry(String key, String name, Integer referenceId, Integer levelId, String text) {
        try {
            eventLog.add(Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION, getSource(key, name), Constants.table().EXCHANGE_CRITERIA,
                     referenceId, levelId, text);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add log entry for: "+ name, e);
        }
    }
    
    private String getSource(String key, String name) {
        String m;

        try {
            m = resource.getString(key);
            return m.replaceFirst("\\{" + 0 + "\\}", name);
        } catch (Throwable any) {
            return key;
        }
    }
}