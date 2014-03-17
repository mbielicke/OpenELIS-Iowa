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

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ResultDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class BillingReportBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private SystemVariableBean      systemVariable;

    @EJB
    private SampleOrganizationBean  sampleOrganization;

    @EJB
    private SamplePrivateWellBean   samplePrivateWell;

    @EJB
    private SampleEnvironmentalBean sampleEnvironmental;

    @EJB
    private SampleSDWISBean         sampleSDWIS;

    @EJB
    private SampleProjectBean       sampleProject;

    @EJB
    private SampleQAEventBean       sampleQaevent;

    @EJB
    private AnalysisQAEventBean     analysisQaevent;

    @EJB
    private AnalyteBean             analyte;

    @EJB
    private AuxDataBean             auxData;

    @EJB
    private AnalysisReportFlagsBean analysisReportFlags;
    
    @EJB
    private DictionaryCacheBean     dictionary;

    private static final String     RECUR = "R", ONE_TIME = "OT", OT_CLIENT_CODE = "PWT",
                                    EOL = "\r\n";

    private static final Logger     log   = Logger.getLogger("openelis");

    /**
     * This method is called from a cron job to create and export a billing
     * file. The export file is placed into directory specified by
     * 'billing_directory' system variable. The report tracks the last it was
     * run and only scans for sample that has been released since its last run.
     */

    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {
        String poAnalyteName, billDir;
        ArrayList<Object[]> billables;
        Date lastRunDate, currentRunDate, now;
        SystemVariableDO lastRun;
        Calendar cal;
        SimpleDateFormat df;
        FileWriter out;
        File tempFile;

        // System variable that points to the analyte in the aux group
        try {
            poAnalyteName = systemVariable.fetchByName("billing_po").getValue();
        } catch (Exception anyE) {
            poAnalyteName = null;
            log.warning("System variable 'billing_po' is not available");
        }

        try {
            billDir = systemVariable.fetchByName("billing_directory").getValue();
        } catch (Exception anyE) {
            log.severe("System variable 'billing_directory' is not available");
            return;
        }

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        lastRun = null;
        try {
            lastRun = systemVariable.fetchForUpdateByName("last_billing_report_run");
            lastRunDate = df.parse(lastRun.getValue());
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "System variable 'last_billing_report_run' is not available or valid",
                    e);
            if (lastRun != null)
                systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        cal = Calendar.getInstance();
        /*
         * this is the time at which the current run is being executed and it
         * will be used as the last run time for the next run
         */
        now = cal.getTime();

        cal.add(Calendar.MINUTE, -1);
        currentRunDate = cal.getTime();

        if (lastRunDate.compareTo(currentRunDate) >= 0) {
            log.severe("Start Date should be earlier than End Date");
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        billables = fetchForBillingReport(lastRunDate, currentRunDate);
        log.fine("Considering " + billables.size() + " cases to run");
        if (billables.size() == 0) {
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        out = null;
        tempFile = null;
        try {
            tempFile = File.createTempFile("billingReport", ".txt", new File(billDir));
            out = new FileWriter(tempFile);
            outputBilling(out, currentRunDate, poAnalyteName, billables);
            out.close();

            lastRun.setValue(df.format(now));
            systemVariable.update(lastRun);
        } catch (Exception e) {
            if (out != null)
                out.close();
            if (tempFile != null)
                tempFile.delete();

            log.log(Level.SEVERE, "Could not generate billing report", e);

            systemVariable.abortUpdate(lastRun.getId());
            //
            // we need to roll back the entire transaction
            //
            throw new DatabaseException(e);
        }
    }

    /**
     * The method exports billing records for each sample and analysis. All analysis
     * have to reportable to be billed (with exception of two billing test). The routine
     * tracks previously sent billing information and issues credit records if the
     * analysis such as # of analytes, QAevents, etc. is changed. 
     */
    private void outputBilling(FileWriter out, Date currentDate, String poAnalyteName,
                               ArrayList<Object[]> billables) throws Exception {
        boolean needCharge, needCredit;
        Double sampleCharge, anaCharge, billedOverride;
        Integer samId, prevSamId, accession, anaId, statusId, testId, billedAnalytes, billAnalytes, 
                BILLING_MISC_ID, BILLING_RUSH_ID, SECTION_ANALYTE_ID;
        String clientCode, domain, clientReference, billingType, lastName, projectName, domainInfo,
               testName, methodName, section, labCode, currentDateStr, po, procedure, anaReportable, tokens[];
        Datetime currDateTime;
        Timestamp recieved, billed;
        SimpleDateFormat df;
        AddressDO addr;
        SampleEnvironmentalDO env;
        SampleSDWISViewDO sdwis;
        SamplePrivateWellViewDO well;
        OrganizationDO org;
        AnalysisReportFlagsDO anaRepFlags;
        ArrayList<SampleQaEventViewDO> sampleQas;
        ArrayList<AnalysisQaEventViewDO> anaQas;
        ArrayList<ResultDO> analytes;
        StringBuilder hdr, dtrcr, dtrch;

        hdr = new StringBuilder();
        dtrcr = new StringBuilder();
        dtrch = new StringBuilder();
        org = null;
        prevSamId = null;
        
        currDateTime = Datetime.getInstance(Datetime.YEAR , Datetime.MINUTE, currentDate);    
        df = new SimpleDateFormat("yyyyMMddHHmm");
        currentDateStr = df.format(currentDate);
                                               
        try {
            BILLING_MISC_ID = Integer.valueOf(systemVariable.fetchByName("billing_misc_test_id").getValue());
        } catch (Exception anyE) {
            BILLING_MISC_ID = null;
            log.warning("System variable 'billing_misc_test_id' is not available");
        }
        try {
            BILLING_RUSH_ID = Integer.valueOf(systemVariable.fetchByName("billing_rush_test_id").getValue());
        } catch (Exception anyE) {
            log.warning("System variable 'billing_rush_test_id' is not available");
            BILLING_RUSH_ID = null;
        }
        try {
            SECTION_ANALYTE_ID = analyte.fetchByExternalId("section", 1).get(0).getId();
        } catch (Exception anyE) {
            log.warning("Analyte with external id 'section' not found");
            SECTION_ANALYTE_ID = null;
        }
        
        /*
         * export each billable and update the analysis flags  
         */
        sampleCharge = null;
        for (Object[] row : billables) {
            samId = (Integer)row[0];
            accession = (Integer)row[1];
            domain = (String)row[2];
            clientReference = DataBaseUtil.trim((String)row[3]);
            recieved = (Timestamp)row[4];
            anaId = (Integer)row[5];
            testId = (Integer)row[6];
            testName = DataBaseUtil.trim((String)row[7]);
            methodName = DataBaseUtil.trim((String)row[8]);
            section = DataBaseUtil.trim((String)row[9]);
            billed = (Timestamp)row[10];
            billedAnalytes = (Integer)row[11];
            billedOverride = (Double)row[12];
            anaReportable = (String)row[13];  
            statusId = (Integer)row[14];
            
            if (!samId.equals(prevSamId))  {                  
                lastName = null;
                domainInfo = null;
                sampleCharge = null;
                if (poAnalyteName != null)
                    po = getBillingPO(samId, poAnalyteName);
                else
                    po = null;
                
                switch (domain.charAt(0)) {                    
                    case 'W':
                        well = samplePrivateWell.fetchBySampleId(samId);                                               
                        org = getOrganization(samId, well);  
                        if (org.getId() == null) 
                            lastName = (well.getOwner() != null) ? well.getOwner() : org.getName();                        
                        domainInfo = getDomainInfo(clientReference, po, well);                                               
                        break;
                    case 'E':
                        env = sampleEnvironmental.fetchBySampleId(samId);                                            
                        org = getOrganization(samId, null);                        
                        domainInfo = getDomainInfo(clientReference, po, env);
                        break;
                    case 'S':
                        sdwis = sampleSDWIS.fetchBySampleId(samId);
                        org = getOrganization(samId, null);
                        domainInfo = getDomainInfo(clientReference, po, sdwis);
                        break;
                }                
                clientCode = (org.getId() != null) ? org.getId().toString() : OT_CLIENT_CODE;
                billingType = (org.getId() != null) ? RECUR : ONE_TIME;
                addr = org.getAddress();
                lastName = (lastName != null) ? lastName : accession.toString();                
                projectName = getProject(samId);               
                                                      
                try {
                    sampleQas = sampleQaevent.fetchBySampleId(samId);
                    for (SampleQaEventViewDO sqa : sampleQas) {
                        if ("N".equals(sqa.getIsBillable())) {
                            sampleCharge = 0.0;
                            break;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    log.severe("Problem with fetching sample qa events for id " + samId);
                    throw e;
                }

                df.applyPattern("yyyyMMdd");
                hdr.setLength(0);
                hdr.append("HDR").append("|")
                    .append(currentDateStr).append("|")
                    .append(accession).append("|")
                    .append(billingType).append("|")
                    .append(df.format(recieved)).append("|")
                    .append(clientCode).append("|")                
                    .append(lastName).append("|")
                    .append("|")                                // reserved for patient first name
                    .append(org.getName()).append("|")
                    .append(DataBaseUtil.toString(addr.getMultipleUnit())).append("|")
                    .append(DataBaseUtil.toString(addr.getStreetAddress())).append("|")
                    .append(DataBaseUtil.toString(addr.getCity())).append("|")
                    .append(DataBaseUtil.toString(addr.getState())).append("|")
                    .append(DataBaseUtil.toString(addr.getZipCode())).append("|")
                    .append(domainInfo).append("|")
                    .append("|")                            // reserved for Client Patient Id 2
                    .append(DataBaseUtil.toString(projectName).toUpperCase()).append("|");
            }
            
            /*
             * if the sample has even one qa event that's not billable
             * then the change for all analyses is zero
             */
            anaCharge = sampleCharge;
            if (anaCharge == null) {                
                try {
                    anaQas = analysisQaevent.fetchByAnalysisId(anaId);
                    for (AnalysisQaEventViewDO aqa : anaQas) {                        
                        if ("N".equals(aqa.getIsBillable())) {
                            anaCharge = 0.0;
                            break;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    log.severe("Problem with fetching analysis qa events for id "+ anaId);
                    throw e;
                }
            }

            try {
                analytes = fetchForBillingByAnalysisId(anaId);
                billAnalytes = 0;

                /*
                 * Override the section and calculate the total miscellaneous
                 * charge 
                 */
                if (testId.equals(BILLING_MISC_ID) && analytes.size() > 0) {
                    anaCharge = 0.0;
                    for (ResultDO data : analytes) {
                        if (data.getAnalyteId().equals(SECTION_ANALYTE_ID)) {
                            if (data.getValue() != null)
                                section = dictionary.getById(Integer.parseInt(data.getValue())).getEntry();
                        } else {
                            if ("Y".equals(data.getIsReportable()) && data.getValue() != null) {
                                billAnalytes++;
                                try {
                                    anaCharge += Double.parseDouble(data.getValue());
                                } catch (NumberFormatException numE) {
                                    log.severe("Accession #"+accession+" has an"+
                                               " invalid price for miscellaneous billing");
                                    anaCharge = -9.99;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (ResultDO data : analytes)
                        if ("Y".equals(data.getIsReportable()))
                            billAnalytes++;
                }
            } catch (Exception e) {
                log.severe("Problem with fetching results for analysis with id "+ anaId);
                throw e;
            }          
            
            /*
             * section's name is in the format "virology-ic" 
             */
            tokens = section.split("-");
            labCode = "";
            if (tokens.length >= 2) 
                labCode = tokens[1];            
            procedure = testName + " by " + methodName;
            /*
             * create a credit record (CR) if we have billed this analysis
             * previously and analysis data has changed
             */ 
            dtrcr.setLength(0);
            dtrch.setLength(0);            
            df.applyPattern("yyyyMMddHHmm");
            
            needCredit = false;
            if (billed != null) {
                if (Constants.dictionary().ANALYSIS_CANCELLED.equals(statusId))  
                    needCredit = true;
                else if ("N".equals(anaReportable) && !testId.equals(BILLING_MISC_ID) &&
                                !testId.equals(BILLING_RUSH_ID))  
                    needCredit = true;
                else if (billAnalytes != billedAnalytes)
                    needCredit = true;
                else if (DataBaseUtil.isDifferent(billedOverride, anaCharge))
                    needCredit = true;
            }
                        
            /*
             *  figure out if we charge for this analysis
             */
            needCharge = false;
            if (billed == null || billAnalytes != billedAnalytes ||
                DataBaseUtil.isDifferent(billedOverride, anaCharge)) {                
                if (!Constants.dictionary().ANALYSIS_CANCELLED.equals(statusId) && ("Y".equals(anaReportable) ||
                    testId.equals(BILLING_MISC_ID) || testId.equals(BILLING_RUSH_ID))) 
                    needCharge = true;
            }            
                
            if (needCredit) {
                dtrcr.append("DTR").append("|")
                .append(df.format(billed)).append("|")
                .append(accession).append("|")
                .append(testId).append("|")
                .append(procedure).append("|")
                .append("CR").append("|")
                .append(billedAnalytes).append("|")
                .append(billedOverride).append("|")
                .append(labCode.toUpperCase()).append("|")
                .append(section).append("|");  
            }
            
            if (needCharge) {            
                dtrch.append("DTR").append("|")
                     .append(currentDateStr).append("|")
                     .append(accession).append("|")
                     .append(testId).append("|")
                     .append(procedure).append("|")
                     .append("CH").append("|")
                     .append(billAnalytes).append("|")
                     .append(anaCharge).append("|")
                     .append(labCode.toUpperCase()).append("|")
                     .append(section).append("|");
            }
            
            if (dtrcr.length() == 0 && dtrch.length() == 0)               
                continue;
            
            if (!samId.equals(prevSamId)) {
                prevSamId = samId;
                out.write(hdr.toString());
                out.write(EOL);
            }
            
            if (dtrcr.length() > 0) {
                out.write(dtrcr.toString());
                out.write(EOL);
            }
            
            if (dtrch.length() > 0) {
                out.write(dtrch.toString());
                out.write(EOL);
            }
                        
            /*
             * if we can add the DTR with the charge (CH) then we try to
             * lock the record in AnalysisReportFlags for this analysis
             * and set its billed_date to the current date
             */
            try {                
                anaRepFlags = analysisReportFlags.fetchForUpdateByAnalysisId(anaId);
                anaRepFlags.setBilledDate(needCharge ? currDateTime : null);
                anaRepFlags.setBilledAnalytes(billAnalytes);
                anaRepFlags.setBilledOverride(anaCharge);
                analysisReportFlags.update(anaRepFlags);
            } catch (Exception e) {
                /*
                 * if we can't lock the record then we need to abandon the
                 * process of filling the file
                 */                                      
                log.severe("Could not lock analysis report flag for id "+ anaId);
                throw e;
            }                                                        
        }
    }

    private String getProject(Integer id) throws Exception {
        String projectName;
        ArrayList<SampleProjectViewDO> sampleProjList;

        try {
            sampleProjList = sampleProject.fetchPermanentBySampleId(id);
            projectName = sampleProjList.get(0).getProjectName();
        } catch (NotFoundException e) {
            projectName = null;
        }
        return projectName;
    }

    private OrganizationDO getOrganization(Integer id, SamplePrivateWellViewDO well) throws Exception {
        int index;
        SampleOrganizationViewDO so;
        OrganizationDO org;
        ArrayList<SampleOrganizationViewDO> list;
        AddressDO addr, repAddr;

        list = null;
        index = -1;
        try {
            list = sampleOrganization.fetchBySampleId(id);
            for (int i = 0; i < list.size(); i++) {
                so = list.get(i);
                if (Constants.dictionary().ORG_BILL_TO.equals(so.getTypeId())) {
                    index = i;
                    break;
                } else if (Constants.dictionary().ORG_REPORT_TO.equals(so.getTypeId())) {
                    index = i;
                }
            }
        } catch (NotFoundException e) {
            /*
             * if well == null then this sample's domain must be different from
             * private well and so it must have at least one sample organization
             * defined which is of type "report to"
             */
            if (well == null)
                return null;
        }

        org = null;
        if (index > -1) {
            //
            // a sample organization of type "bill to" or "report to" was found
            //
            so = list.get(index);
            org = new OrganizationDO();
            org.setId(so.getOrganizationId());
            org.setName(so.getOrganizationName());
            addr = org.getAddress();
            addr.setMultipleUnit(so.getOrganizationMultipleUnit());
            addr.setStreetAddress(so.getOrganizationStreetAddress());
            addr.setCity(so.getOrganizationCity());
            addr.setState(so.getOrganizationState());
            addr.setZipCode(so.getOrganizationZipCode());
        } else if (well != null) {
            //
            // find the organization or address specified as "report to"
            //
            org = well.getOrganization();
            if (org == null) {
                repAddr = well.getReportToAddress();
                if (repAddr != null) {
                    org = new OrganizationDO();
                    org.setName(well.getReportToName());
                    addr = org.getAddress();
                    addr.setMultipleUnit(repAddr.getMultipleUnit());
                    addr.setStreetAddress(repAddr.getStreetAddress());
                    addr.setCity(repAddr.getCity());
                    addr.setState(repAddr.getState());
                    addr.setZipCode(repAddr.getZipCode());
                }
            }
        }

        return org;
    }

    private String getBillingPO(Integer id, String analyteName) throws Exception {
        ArrayList<AuxDataViewDO> auxList;
        String value;

        value = null;
        try {
            auxList = auxData.fetchByIdAnalyteName(id, Constants.table().SAMPLE, analyteName);
            value = auxList.get(0).getValue();
        } catch (NotFoundException e) {
            // ignore
        }

        return value;
    }

    private String getDomainInfo(String clientReference, String po, SamplePrivateWellViewDO sample) {
        StringBuffer buf;

        buf = new StringBuffer();
        append(buf, "PO-", po);
        append(buf, "REF-", clientReference);
        append(buf, "OWNER-", sample.getOwner());
        append(buf, "COL-", sample.getCollector());
        append(buf, "LOC-", sample.getLocation());

        return buf.toString();
    }

    private String getDomainInfo(String clientReference, String po, SampleEnvironmentalDO sample) {
        StringBuffer buf;

        buf = new StringBuffer();
        append(buf, "PO-", po);
        append(buf, "REF-", clientReference);
        append(buf, "COL-", sample.getCollector());
        append(buf, "LOC-", sample.getLocation());

        return buf.toString();
    }

    private String getDomainInfo(String clientReference, String po, SampleSDWISViewDO sample) {
        StringBuffer buf;

        buf = new StringBuffer();
        append(buf, "PO-", po);
        append(buf, "PWS-", sample.getPwsNumber0());
        append(buf, "REF-", clientReference);
        append(buf, "COL-", sample.getCollector());
        append(buf, "LOC-", sample.getLocation());

        return buf.toString();
    }

    private ArrayList<Object[]> fetchForBillingReport(Date stDate, Date endDate) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchForBillingReport");
        query.setParameter("startDate", stDate);
        query.setParameter("endDate", endDate);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<ResultDO> fetchForBillingByAnalysisId(Integer analysisId) {
        Query query;

        query = manager.createNamedQuery("Result.FetchForBillingByAnalysisId");
        query.setParameter("id", analysisId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    private void append(StringBuffer buf, String prefix, String value) {
        if (!DataBaseUtil.isEmpty(value)) {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(prefix).append(value);
        }
    }
}