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

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.BillingReportLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class BillingReportBean implements BillingReportLocal {

    @EJB
    private SampleLocal              sample;

    @EJB
    private SystemVariableLocal      systemVariable;

    @EJB
    private SampleOrganizationLocal  sampleOrganization;

    @EJB
    private SamplePrivateWellLocal   samplePrivateWell;

    @EJB
    private SampleEnvironmentalLocal sampleEnvironmental;

    @EJB
    private SampleSDWISLocal         sampleSDWIS;

    @EJB
    private SampleProjectLocal       sampleProject;

    @EJB
    private SampleQAEventLocal       sampleQaevent;

    @EJB
    private AnalysisQAEventLocal     analysisQaevent;
    
    @EJB
    private ResultLocal              result;          
    
    @EJB
    private AuxDataLocal             auxData;
    
    @EJB
    private AnalysisReportFlagsLocal analysisReportFlags; 
    
    @EJB
    private DictionaryLocal          dictionary;
    
    private static final String      RECUR = "R", ONE_TIME = "OT", OT_CLIENT_CODE = "PWT",
                                     MISC_BILLING = "billing misc charges by no method",
                                     RUSH_BILLING = "billing rush charges by no method",
                                     EOL = "\r\n", ZERO_BILL = "0.00";
    
    private static Integer           organizationReportToId, organizationBillToId, analysisCancelledId;
    
    private static final Logger      log  = Logger.getLogger(BillingReportBean.class);

    @PostConstruct
    public void init() {
        try {
            organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
            organizationBillToId = dictionary.fetchBySystemName("org_bill_to").getId();
            analysisCancelledId = dictionary.fetchBySystemName("analysis_cancelled").getId();
        } catch (Throwable e) {
            log.error("Failed to lookup constants for dictionary entries", e);
        }
    }
    
    /**
     * Execute the report and email its output to specified addresses
     */

    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() throws Exception {        
        String poAnalyteName, billDir;
        ArrayList<Object[]> resultList;
        Date lastRunDate, currentDate, currentRunDate;
        SystemVariableDO lastRun;
        Calendar cal;
        SimpleDateFormat df;
        FileWriter out;
        File tempFile;        

        // System variable that points to the analyte in the aux group 
        poAnalyteName = ReportUtil.getSystemVariableValue("billing_po");
        
        billDir = ReportUtil.getSystemVariableValue("billing_directory");
        if (billDir == null) {
            log.error("System variable 'billing_directory' is not available");
            return;
        }                
        
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        lastRun = null;
        try {
            lastRun = systemVariable.fetchForUpdateByName("last_billing_report_run");            
            lastRunDate = df.parse(lastRun.getValue());   
        } catch (Exception e) {
            log.error("System variable 'last_billing_report_run' is not available or valid", e);
            if (lastRun != null)
                systemVariable.abortUpdate(lastRun.getId());
            return;
        }        
        
        cal = Calendar.getInstance();
        /*
         * this is the time at which the current run is being executed and it will 
         * be used as the last run time for the next run 
         */
        currentRunDate = cal.getTime();
        
        cal.add(Calendar.MINUTE, -1);
        currentDate = cal.getTime();                  
        
        if (lastRunDate.compareTo(currentDate) >= 0) {
            log.error("Start Date should be earlier than End Date");
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }
            
        resultList = sample.fetchForBillingReport(lastRunDate, currentDate);        
        log.info("Considering "+ resultList.size()+ " cases to run");        
        if (resultList.size() == 0) {
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }
        out = null;
        tempFile = null;
        try {
            tempFile = File.createTempFile("billingReport", ".txt", new File(billDir));
            out = new FileWriter(tempFile);
            outputBilling(out, currentDate, poAnalyteName, resultList);  
            out.close();
            
            lastRun.setValue(df.format(currentRunDate));
            systemVariable.update(lastRun);        
        } catch (Exception e) {
            if (out != null) 
                out.close();
            if (tempFile != null)
                tempFile.delete();
            log.error(e);
            systemVariable.abortUpdate(lastRun.getId());     
            //
            // we need to roll back the entire transaction
            //
            throw new DatabaseException(e);
        }  
    }
    
    private void outputBilling(FileWriter out, Date currentDate, String poAnalyteName,
                              ArrayList<Object[]> resultList) throws Exception {
        int i, billableAnalytes;
        boolean sampleZeroCharge, anaZeroCharge, needCharge, needCredit;
        Integer billedAnalytes, samId, prevSamId, accession, anaId, statusId, testId;
        String billedZero, clientCode;
        Object[] row;
        String domain, clientReference, billingType, lastName, projectName,
               domainInfo, testName, methodName, section, labCode, currentDateStr,
               po, procedure, anaReportable, tokens[];
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
        StringBuilder hdr, dtrcr, dtrch;

        hdr = new StringBuilder();
        dtrcr = new StringBuilder();
        dtrch = new StringBuilder();
        org = null;
        prevSamId = null;
        
        currDateTime = Datetime.getInstance(Datetime.YEAR , Datetime.MINUTE, currentDate);    
        df = new SimpleDateFormat("yyyyMMddHHmm");
        currentDateStr = df.format(currentDate);
                                               
        sampleZeroCharge = false;
        for (i = 0; i < resultList.size(); i++) {
            row = resultList.get(i);
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
            billedZero = (String)row[12];
            anaReportable = (String)row[13];  
            statusId = (Integer)row[14];
            
            if (!samId.equals(prevSamId))  {                  
                lastName = null;
                domainInfo = null;
                sampleZeroCharge = false;
                po = getBillingPO(samId, poAnalyteName);  
                
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
                            sampleZeroCharge = true;
                            break;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    log.error("Problem with fetching sample qa events for id " + samId);
                    throw e;
                }
                
                hdr.setLength(0);
                df.applyPattern("yyyyMMdd");
                hdr.append("HDR").append("|")
                    .append(currentDateStr).append("|")
                    .append(accession).append("|")
                    .append(billingType).append("|")
                    .append(df.format(recieved)).append("|")
                    .append(clientCode).append("|")                
                    .append(lastName).append("|")
                    .append("|")                                // reserved for patient first name
                    .append(org.getName()).append("|");                
                if (addr.getMultipleUnit() != null) {
                    hdr.append(addr.getMultipleUnit()).append("|"); 
                    if (addr.getStreetAddress() != null)
                        hdr.append(addr.getStreetAddress()).append("|");
                    else 
                        hdr.append("|");
                } else if (addr.getStreetAddress() != null) {
                    hdr.append(addr.getStreetAddress()).append("|")
                        .append("|");
                } else {
                    hdr.append("||");
                }
                
                hdr.append(DataBaseUtil.toString(addr.getCity())).append("|")
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
            anaZeroCharge = sampleZeroCharge;
            if (!sampleZeroCharge) {                
                try {
                    anaQas = analysisQaevent.fetchByAnalysisId(anaId);
                    for (AnalysisQaEventViewDO aqa : anaQas) {                        
                        if ("N".equals(aqa.getIsBillable())) {
                            anaZeroCharge = true;
                            break;
                        }
                    }
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    log.error("Problem with fetching analysis qa events for id "+ anaId);
                    throw e;
                }
            }

            try {
                billableAnalytes = result.fetchForBillingByAnalysisId(anaId).size();
            } catch (NotFoundException e) {
                billableAnalytes = 0;
            } catch (Exception e) {
                log.error("Problem with fetching results for analysis with id "+ anaId);
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
             * if the values set for the fields billed_analytes and
             * billed_zero in AnalysisReportFlags for this analysis are
             * different from the ones determined right now, then we create
             * a row with "CR" as the transaction type with the old values
             */ 
            dtrcr.setLength(0);
            dtrch.setLength(0);            
            df.applyPattern("yyyyMMddHHmm");
            
            needCredit = false;
            if (billed != null) {
                if (billableAnalytes != billedAnalytes)  
                    needCredit = true;
                else if (!billedZero.equals(anaZeroCharge ? "Y" : "N")) 
                    needCredit = true;
                else if ("N".equals(anaReportable) && !MISC_BILLING.equals(procedure) &&
                                !RUSH_BILLING.equals(procedure)) 
                    needCredit = true;
                else if (analysisCancelledId.equals(statusId))
                    needCredit = true;
            }
                        
            //
            //  we need to figure out if we charge for this analysis
            //
            needCharge = false;
            if (billed == null || billableAnalytes != billedAnalytes ||
                !billedZero.equals(anaZeroCharge ? "Y" : "N")) {                
                if (!analysisCancelledId.equals(statusId) && ("Y".equals(anaReportable) ||
                    MISC_BILLING.equals(procedure) || RUSH_BILLING.equals(procedure))) 
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
                .append("Y".equals(billedZero) ? ZERO_BILL: "").append("|")
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
                     .append(billableAnalytes).append("|")
                     .append(anaZeroCharge ? ZERO_BILL: "").append("|")
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
                anaRepFlags.setBilledAnalytes(billableAnalytes);
                anaRepFlags.setBilledZero(anaZeroCharge ? "Y" : "N");
                analysisReportFlags.update(anaRepFlags);
            } catch (EntityLockedException e) {
                /*
                 * if we can't lock the record then we need to abandon the
                 * process of filling the file and delete the file 
                 */                                      
                log.error("Could not lock analysis report flag for id "+ anaId);
                throw e;
            } catch (Exception e) {
                /*
                 * if we can't lock the record then we need to abandon the
                 * process of filling the file and delete the file 
                 */                                      
                log.error("Could not lock analysis report flag for id "+ anaId);
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
            for (int i = 0; i < list.size(); i++ ) {
                so = list.get(i);
                if (organizationBillToId.equals(so.getTypeId())) {
                    index = i;
                    break;
                } else if (organizationReportToId.equals(so.getTypeId())) {
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
            auxList = auxData.fetchByIdAnalyteName(id, ReferenceTable.SAMPLE, analyteName);
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
    
    private void append(StringBuffer buf, String prefix, String value) {
        if (!DataBaseUtil.isEmpty(value)) {
            if (buf.length() > 0) 
                buf.append(", ");
            buf.append(prefix).append(value);
        }
    }    
}