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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.remote.BillingReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.JasperUtil;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class BillingReportBean implements BillingReportRemote {

    @EJB
    private SessionCacheLocal        session;

    @EJB
    private SampleLocal              sample;

    @EJB
    private SystemVariableLocal      systemVariable;

    @EJB
    private AnalysisReportFlagsLocal anaReportFlagsBean;

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
        
    private static final String      RECUR = "R", ONE_TIME = "OT", OT_CLIENT_CODE = "PWT";
    
    private static final Logger      log  = Logger.getLogger(BillingReportBean.class);

    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("SECTION", Prompt.Type.ARRAY).setPrompt("Do you want to run this report:")
                                                          .setWidth(150)
                                                          .setOptionList(getOptions())
                                                          .setMutiSelect(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Execute the report and email its output to specified addresses
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i, billableSQa, billableAnalytes, numDTR;
        Integer billedAnalytes, samId, prevSamId, nextSamId, samAcc, anaId;
        boolean showCid1, createCharge, createCredit, dataInFile;
        String billedZero, billableZero, poAnalyteName, message, clientCode;
        ReportStatus status;
        Object[] row, nextRow;
        ArrayList<Object[]> resultList;
        String domain, owner, collector, clientReference, location, clientName,
               billingType, lastName, firstName, pws, clientCity, clientState, 
               clientZip, clientPatientId2, projectName, orgMultipleUnit, orgStAddress,
               rcDateStr, procedureCode, procedureDescription, section, labCode, 
               labDept, endDateStr, value, tokens[];
        Date stDate, endDate;
        Datetime currDateTime;
        SystemVariableDO sysVarLastReportRun;
        Timestamp rcDate, billedDate;
        Calendar cal;
        SimpleDateFormat df;
        AddressDO address;
        SampleEnvironmentalDO env;
        SampleSDWISViewDO sdwis;
        SamplePrivateWellViewDO well;
        SampleOrganizationViewDO sampleOrg;
        AnalysisReportFlagsDO anaRepFlags;
        ArrayList<SampleOrganizationViewDO> sampleOrgReportToList;
        ArrayList<SampleProjectViewDO> sampleProjList;
        ArrayList<SampleQaEventDO> sampleQaList;
        ArrayList<ResultDO> results;
        ArrayList<AuxDataViewDO> auxList;
        StringBuilder text;
        FileWriter out;
        File tempFile;        

        owner = null;
        clientCode = null;
        labCode = null;
        labDept = null;
        firstName = null;
        clientPatientId2 = null;
        out = null;
        text = new StringBuilder();
        well = null;
        address = null;
        sampleOrg = null;
        orgMultipleUnit = null;
        orgStAddress = null;
        clientCity = null;
        clientState = null;
        clientZip = null;
        billableSQa = 0;
        billableAnalytes = 0;       
        sysVarLastReportRun = null;
        tempFile = null;
        dataInFile = false;
        
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("BillingReport", status);

        try {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            endDate = cal.getTime();
            currDateTime = Datetime.getInstance(Datetime.YEAR , Datetime.MINUTE, endDate);
            
            sysVarLastReportRun = systemVariable.fetchForUpdateByName("last_billing_report_run");
            stDate = df.parse(sysVarLastReportRun.getValue());   
            
            poAnalyteName = ReportUtil.getSystemVariableValue("billing_po");
            
            if (stDate.compareTo(endDate) < 0)
                resultList = sample.fetchForBillingReport(stDate, endDate);
            else
                throw new InconsistencyException("Start Date should be earlier than End Date");

            prevSamId = null;

            df.applyPattern("yyyyMMddHHmm");
            endDateStr = df.format(endDate);
            
            status.setMessage("Outputing report").setPercentComplete(20);
            tempFile = File.createTempFile("billingReport", ".txt", new File("/tmp"));
            out = new FileWriter(tempFile);
            numDTR = 0; 
            dataInFile = false;
            for (i = 0; i < resultList.size(); i++ ) {
                row = resultList.get(i);
                samId = (Integer)row[0];
                samAcc = (Integer)row[1];
                domain = (String)row[2];
                clientReference = DataBaseUtil.trim((String)row[3]);
                rcDate = (Timestamp)row[4];
                anaId = (Integer)row[5];
                
                if (!samId.equals(prevSamId)) {
                    numDTR = 0;                 
                    text.setLength(0);
                    showCid1 = false;
                    df.applyPattern("yyyyMMdd");
                    rcDateStr = df.format(rcDate);
                    billingType = RECUR;
                    
                    // primary project                    
                    try {
                        sampleProjList = sampleProject.fetchPermanentBySampleId(samId);
                        projectName = sampleProjList.get(0).getProjectName();
                    } catch (NotFoundException e) {
                        projectName = null;
                    }
                    /*
                     * If sample has BillTo organization id, make it the
                     * clientCode, if not, then make reportTo organization id
                     * the clientCode.
                     */
                    try {
                        sampleOrg = sampleOrganization.fetchBillToBySampleId(samId);
                        clientCode = sampleOrg.getOrganizationId().toString();
                        clientName = sampleOrg.getOrganizationName();
                        lastName = samAcc.toString();
                        address = null;
                    } catch (NotFoundException e) {
                        if ("W".equals(domain)) {
                            well = samplePrivateWell.fetchBySampleId(samId);
                            if (well.getOrganizationId() != null) {
                                clientCode = well.getOrganizationId().toString();
                                clientName = well.getOrganization().getName();
                                lastName = samAcc.toString();
                                address = well.getOrganization().getAddress();
                                sampleOrg = null;
                            } else {
                                /*
                                 * If the sample doesn't have any billTo or
                                 * reportTo organization id, then its billing
                                 * type is "OneTime", else it will be of type
                                 * "Recur".
                                 */
                                billingType = ONE_TIME;
                                if (owner != null)
                                    lastName = owner;
                                else
                                    lastName = well.getReportToName();

                                clientName = well.getReportToName();
                                address = well.getReportToAddress();
                                clientCode = OT_CLIENT_CODE;
                                sampleOrg = null;
                            }
                        } else {
                            sampleOrgReportToList = sampleOrganization.fetchReportToBySampleId(samId);
                            clientCode = sampleOrgReportToList.get(0).getOrganizationId().toString();
                            clientName = sampleOrgReportToList.get(0).getOrganizationName();
                            lastName = samAcc.toString();
                            sampleOrg = sampleOrgReportToList.get(0);
                            address = null;
                        }
                    }
                    if (sampleOrg != null) {
                        orgMultipleUnit = sampleOrg.getOrganizationMultipleUnit();
                        orgStAddress = sampleOrg.getOrganizationStreetAddress();
                        clientCity = sampleOrg.getOrganizationCity();
                        clientState = sampleOrg.getOrganizationState();
                        clientZip = sampleOrg.getOrganizationZipCode();
                    } else if (address != null) {
                        orgMultipleUnit = address.getMultipleUnit();
                        orgStAddress = address.getStreetAddress();
                        clientCity = address.getCity();
                        clientState = address.getState();
                        clientZip = address.getZipCode();
                    }

                    text.append("HDR").append("|").append(endDateStr).append("|")
                        .append(samAcc).append("|").append(billingType).append("|")
                        .append(rcDateStr).append("|");
                    if (clientCode != null)
                        text.append(clientCode);
                    text.append("|");
                    text.append(lastName).append("|");
                    if (firstName != null)
                        text.append(firstName);
                    text.append("|");
                    text.append(clientName).append("|");
                    /*
                     * we can show either the apt/suite # or the street address 
                     * if apt/suite # isn't present as client address #1
                     */
                    if (orgMultipleUnit != null) {
                        text.append(orgMultipleUnit);
                    } else if (orgStAddress != null) {
                        text.append(orgStAddress);
                        orgStAddress = null;
                    }
                    text.append("|");
                    if (orgStAddress != null)
                        text.append(orgStAddress);
                    text.append("|");
                    if (clientCity != null)
                        text.append(clientCity);
                    text.append("|");
                    if (clientState != null)
                        text.append(clientState);
                    text.append("|");
                    if (clientZip != null)
                        text.append(clientZip);
                    text.append("|");

                    //
                    // create the "Mush" fields for the various domains
                    //
                    if (poAnalyteName != null) {
                        try {
                            auxList = auxData.fetchByIdAnalyteName(samId, ReferenceTable.SAMPLE,
                                                                   poAnalyteName);
                            value = auxList.get(0).getValue();
                            if ( !DataBaseUtil.isEmpty(value)) {
                                text.append("PO-").append(value);
                                showCid1 = true;
                            }
                        } catch (NotFoundException e) {
                            // ignore
                        }
                    }

                    if ("W".equals(domain)) {
                        if (well == null)
                            well = samplePrivateWell.fetchBySampleId(samId);
                        owner = well.getOwner();
                        collector = well.getCollector();
                        location = well.getLocation();
                        if (clientReference != null) {
                            if (showCid1)
                                text.append(",");
                            else
                                showCid1 = true;
                            text.append("REF-").append(clientReference);
                        }
                        if (owner != null) {
                            if (showCid1)
                                text.append(",");
                            else
                                showCid1 = true;
                            text.append("OWNER-").append(owner);
                        }
                        if (collector != null) {
                            if (showCid1)
                                text.append(",");
                            else
                                showCid1 = true;
                            text.append("COL-").append(collector);
                        }
                        if (location != null) {
                            if (showCid1)
                                text.append(",");
                            text.append("LOC-").append(location);
                        }
                    } else if ("E".equals(domain)) {
                        env = sampleEnvironmental.fetchBySampleId(samId);
                        collector = env.getCollector();
                        location = env.getLocation();
                        if (clientReference != null) {
                            if (showCid1)
                                text.append(",");
                            else
                                showCid1 = true;
                            text.append("REF-").append(clientReference);
                        }
                        if (collector != null) {
                            if (showCid1)
                                text.append(",");
                            else
                                showCid1 = true;
                            text.append("COL-").append(collector);
                        }
                        if (location != null) {
                            if (showCid1)
                                text.append(",");
                            text.append("LOC-").append(location);
                        }
                    } else if ("S".equals(domain)) {
                        sdwis = sampleSDWIS.fetchBySampleId(samId);
                        collector = sdwis.getCollector();
                        location = sdwis.getLocation();
                        pws = sdwis.getPwsNumber0();
                        if (showCid1)
                            text.append(",");
                        text.append("PWS-").append(pws);
                        if (clientReference != null)
                            text.append(",").append("REF-").append(clientReference);
                        if (collector != null)
                            text.append(",").append("COL-").append(collector);
                        if (location != null)
                            text.append(",").append("LOC-").append(location);
                    }
                    text.append("|");

                    if (clientPatientId2 != null)
                        text.append(clientPatientId2);
                    text.append("|");

                    if (projectName != null) {
                        projectName = projectName.toUpperCase();
                        text.append(projectName);
                    }
                    text.append("|");
                    text.append("\n");

                    try {
                        sampleQaList = sampleQaevent.fetchNotBillableBySampleId(samId);
                        billableSQa = sampleQaList.size();
                    } catch (NotFoundException e) {
                        billableSQa = 0;
                    }
                }

                // process DTR
                billableZero = "N";
                /*
                 * here we determine if the number of billable analytes for an
                 * analysis is to be overridden by 0
                 */
                if (billableSQa > 0) {
                    /*
                     * if the sample has even one qa event that's not billable
                     * then the analytes for all analyses are overridden
                     */
                    billableZero = "Y";
                } else {
                    /*
                     * if the analysis has even one qa event that's not billable
                     * then the analytes for this analysis are overridden
                     */
                    try {
                        analysisQaevent.fetchNotBillableByAnalysisId(anaId);
                        billableZero = "Y";
                    } catch (NotFoundException e) {
                        billableZero = "N";
                    }
                }

                try {
                    results = result.fetchForBillingByAnalysisId(anaId);
                    billableAnalytes = results.size();
                } catch (NotFoundException e1) {
                    billableAnalytes = 0;
                }

                procedureCode = ((Integer)row[6]).toString();
                procedureDescription = DataBaseUtil.trim((String)row[7]) + " by " +
                                       DataBaseUtil.trim((String)row[8]);
                section = DataBaseUtil.trim((String)row[9]);
                tokens = section.split("-");
                if (tokens.length == 2) {
                    labDept = tokens[0];
                    labCode = tokens[1];
                }

                createCredit = false;
                createCharge = false;

                /*
                 * if the values set for the fields billed_analytes and
                 * billed_zero in AnalysisReportFlags for this analysis are
                 * different from the ones determined right now, then we create
                 * a row with "CR" as the transaction type with the old values
                 */
                billedDate = (Timestamp)row[10];
                billedAnalytes = (Integer)row[11];
                billedZero = (String)row[12];                       
                if ( (billedDate != null && billedAnalytes != null && billedZero != null) &&
                    (billableAnalytes != billedAnalytes || !billableZero.equals(billedZero))) {
                    createCredit = true;
                    createCharge = true;
                } else if (billedDate == null) {
                    createCharge = true;
                }

                if (createCharge) {
                    /*
                     * if we can add the DTR with the charge (CH) then we try to
                     * lock the record in AnalysisReportFlags for this analysis
                     * and set its billed_date to the current date
                     */
                    try {
                        anaRepFlags = analysisReportFlags.fetchForUpdateByAnalysisId(anaId);
                        anaRepFlags.setBilledDate(currDateTime);
                        anaRepFlags.setBilledAnalytes(billableAnalytes);
                        anaRepFlags.setBilledZero(billableZero);
                        analysisReportFlags.update(anaRepFlags);
                        analysisReportFlags.abortUpdate(anaId);
                    } catch (Exception e) {
                        /*
                         * if we can't lock the record then we need to abandon the
                         * process of filling the file and delete the file 
                         */                        
                        tempFile.delete();
                        throw e;
                    }
                    if (createCredit) {
                        text.append("DTR").append("|").append(df.format(billedDate))
                            .append("|").append(samAcc).append("|").append(procedureCode)
                            .append("|").append(procedureDescription).append("|")
                            .append("CR").append("|").append(billedAnalytes).append("|");
                        if ("Y".equals(billedZero))
                            text.append(0);
                        text.append("|");
                        text.append(labCode.toUpperCase()).append("|").append(labDept)
                            .append("|").append("\n");
                    }
                    /*
                     * create a record with transaction type "CH" if either the
                     * client wasn't billed for this analysis or there's a
                     * change between the current number of billable analytes
                     * and previous one
                     */
                    text.append("DTR").append("|").append(df.format(endDate))
                        .append("|").append(samAcc).append("|").append(procedureCode)
                        .append("|").append(procedureDescription).append("|")
                        .append("CH").append("|").append(billableAnalytes).append("|");
                    if ("Y".equals(billableZero))
                        text.append(0);
                    text.append("|");
                    text.append(labCode.toUpperCase()).append("|").append(labDept)
                        .append("|").append("\n");
                    
                    numDTR++;
                }               
                /*
                 * The data for a sample is written to the file either if the next
                 * record in the list returned by the query belongs to a different
                 * sample or if the end of the list has been reached. An "HDR" is 
                 * only written if there are one or more "DTR" for the sample.                
                 */
                if (numDTR > 0) {
                    if (i+1 < resultList.size()) {
                        nextRow = resultList.get(i+1);
                        nextSamId = (Integer)nextRow[0];
                        if ( !samId.equals(nextSamId)) {
                            out.write(text.toString());
                            text.setLength(0);
                        }
                    } else {
                        out.write(text.toString());
                    }
                    dataInFile = true;
                }
                prevSamId = samId;
            }
            status.setPercentComplete(100);
            out.close();
            if (dataInFile) {
                sysVarLastReportRun.setValue(currDateTime.toString());
                systemVariable.update(sysVarLastReportRun);                

                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                      .setStatus(ReportStatus.Status.SAVED);
            } else {
                tempFile.delete();
                message = "No data written to billing report. File not generated";
                log.info(message);
                status.setMessage(message);
            }
            systemVariable.abortUpdate(sysVarLastReportRun.getId());
        } catch (Exception e) {
            if (sysVarLastReportRun != null)
                systemVariable.abortUpdate(sysVarLastReportRun.getId());
            if (tempFile != null)
               tempFile.delete();
            log.error("Failed to generate billing report ", e);
            throw e;
        } finally {
            try {                
                if (out != null)
                    out.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return status;
    }

    private ArrayList<OptionListItem> getOptions() {
        ArrayList<OptionListItem> l;

        l = new ArrayList<OptionListItem>();
        l.add(new OptionListItem("Yes", "Yes"));
        l.add(new OptionListItem("No", "No"));

        return l;
    }
    
    protected ReportStatus generateEmailsFromList(HashMap<String, ArrayList<Object[]>> map) throws Exception {
        int i,j;
        ReportStatus status;
        Object[] result;
        Integer samId;
        String email, rcvd_email, rcvd_date, col_dt, domain, ref, ref_f1, ref_f2, ref_f3;
        String text;
        StringBuilder contents;
        Timestamp rc_date, col_date_time, col_date, col_time;
        Time cl_time;
        SystemVariableDO data;
        DateFormat df, df1;
        ArrayList<Object[]> l;
        ArrayList<Integer> sampleId;
        ArrayList<AnalysisReportFlagsDO> anaList;
        AnalysisReportFlagsDO anaData;

        rcvd_date = col_dt = null;
        contents = null;
        rcvd_email = null;
        text = null;
        sampleId = new ArrayList<Integer>();
        
        status = new ReportStatus();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df1 = new SimpleDateFormat("yyyy-MM-dd");
        for (i = 0; i < map.size(); i++ ) {
            email = (String) (map.keySet().toArray())[i];
            contents = new StringBuilder();
            printHeader(contents);
            l = map.get(email);
            for (j = 0; j < l.size(); j++ ) {
                result = (Object[])l.get(j);
                samId = (Integer)result[1];
                col_date = (Timestamp)result[2];
                cl_time = (Time)result[3];
                rc_date = (Timestamp)result[4];
                rcvd_email = (String)result[5];
                domain = Character.toString((Character)result[6]);
                rcvd_date = df.format(rc_date);
                ref_f1 = (String)result[7];
                ref_f2 = (String)result[8];
                ref_f3 = (String)result[9];
                if (col_date != null) {
                    if (cl_time != null) {
                        col_time = new Timestamp(cl_time.getTime());
                        col_date_time = JasperUtil.concatDateAndTime(col_date, col_time);
                        col_dt = df.format(col_date_time);
                    } else {
                        col_date_time = col_date;
                        col_dt = df1.format(col_date_time);
                    }
                }
                ref = getReferenceFields(domain, ref_f1, ref_f2, ref_f3);               
                
                printBody(contents, samId, rcvd_date, col_dt, ref);

                col_dt = null;
                rcvd_date = null;
                sampleId.add(samId);
            }
            printFooter(contents);
            text = contents.toString();
            try {
                sendemail(rcvd_email, text);
                anaList = anaReportFlagsBean.fetchForUpdateBySampleAccessionNumbers(sampleId);
                for (int k = 0; k < anaList.size(); k++ ) {
                    anaData = anaList.get(k);
                    anaData.setNotifiedReleased("Y");
                    anaReportFlagsBean.update(anaData);
                    anaReportFlagsBean.abortUpdate(anaData.getAnalysisId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            contents = null;
        }

        status.setMessage("emailed").setStatus(ReportStatus.Status.PRINTED);
        /*
         * update system variable last_receivable_report_run with current date
         * and time.
         */
        try {
            data = systemVariable.fetchForUpdateByName("last_released_report_run");
            data.setValue(df.format(new Date()));
            systemVariable.updateAsSystem(data);
        } catch (Exception e) {
            throw e;
        }
        status.setStatus(ReportStatus.Status.SAVED);
        return status;
    
    }

    protected void printHeader(StringBuilder con) {
        con.append("\n")
           .append("We would like to thank you for using our services. The following sample(s) reports or new results are available:")
           .append("<br><br>\r\n")
           .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
           .append("    <tr><td>SHL Accession No.</td>")
           .append("<td>Date Collected</td>")
           .append("<td>Date Received</td>")
           .append("<td>Reference Information</td></tr>\r\n");
    }

    protected void printBody(StringBuilder body, Integer accNum, String dateReceived,
                             String dateCollected, String refInfo) {
        body.append("    <tr><td>").append(accNum).append("</td>").append("<td>");

        if (dateCollected != null)
            body.append(dateCollected);

        body.append("</td>").append("<td>");

        if (dateReceived != null)
            body.append(dateReceived);

        body.append("</td>").append("<td>");

        if (refInfo != null && refInfo.length() > 0 && !"null".equals(refInfo))
            body.append(refInfo);

        body.append("</td></tr>\r\n");
    }

    protected void printFooter(StringBuilder body) {
        body.append("</table>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>This report is intended to provide sample information status at the time of generation. If you have web access, please check for the most current sample results at: <a href='http://www.shl.uiowa.edu/results/index.html'>OpenElis Web Access</a></li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>For questions, please contact us at <a href='mailto:ask-shl@uiowa.edu'>ask-shl@uiowa.edu</a> or call 319-335-4500/1-800-421-IOWA (4692).</li></ul>\r\n")
            .append("<br>\r\n")
            .append("<b>Additional Information:</b>\r\n")
            .append("<br>\r\n")
            .append("<ul><li>This mail was sent from an automated e-mail server. Please do not reply to this message.</li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>You are receiving this courtesy e-mail because you are a valued SHL customer. If you prefer not to receive this e-mail, you can unsubscribe by updating your e-mail preferences at <a href='http://www.shl.uiowa.edu/results/index.html'>OpenElis Web Access</a> or call 319-335-4358.</li>\r\n")
            .append("    <br>\r\n")
            .append("    <li><div style='color:#006400'>Save the Environment, Go GREEN. For Paperless result delivery, please call 319-335-4358.</div></li>\r\n")
            .append("    <br>\r\n")
            .append("    <li>For information on available clinical, environmental, newborn screening, research and training services offered by the State Hygienic Laboratory, please visit our website at <a href='http://www.shl.uiowa.edu'>www.shl.uiowa.edu</a>.</li></ul>\r\n")
            .append("<br>\r\n")
            .append("<b>Disclaimer:</b>\r\n")
            .append("<br><br>\r\n")
            .append("<i>This email and any files transmitted with it are confidential and intended solely for the use of the individual or entity to whom they are addressed. If you have received this email in error please notify the system manager. This message contains confidential information and is intended only for the individual named. If you are not the named addressee you should not disseminate, distribute or copy this e-mail. Please notify <a href='mailto:ask-shl@uiowa.edu'>ask-shl@uiowa.edu</a> immediately by e-mail if you have received this e-mail by mistake and delete this e-mail from your system. If you are not the intended recipient you are notified that disclosing, copying, distributing or taking any action in reliance on the contents of this information is strictly prohibited.</i>");
    }

    protected void sendemail(String to_email, String body) throws AddressException,
                    MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", "mailserver.uhl.uiowa.edu");
        props.put("mail.smtp.port", "25");
        Session session = Session.getDefaultInstance(props, null);

        Message msg = new MimeMessage(session);
        msg.setContent(body, "text/html; charset=ISO-8859-1");
        msg.setFrom(new InternetAddress("do-not-reply@shl.uiowa.edu"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
        msg.setSubject("Your Results are available from the State Hygienic Laboratory at the University of Iowa");
        msg.saveChanges();

        try {
            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    
    protected String getReferenceFields(String domain, String field1, String field2, String field3){        
        String project, cl_ref, col_site, owner, collector, ref;        
        ref = null;
        if (domain.equals("E")) {
            cl_ref = field1;
            col_site = field2;
            project = field3;
            if ( (project == null) && (cl_ref == null) && (col_site == null))
                ref = null;
            else if ( (project != null) && (cl_ref != null) && (col_site != null))
                ref = project + ", " + cl_ref + ", " + col_site;
            else if ( (project != null) && (cl_ref != null) && (col_site == null))
                ref = project + ", " + cl_ref;
            else if ( (project != null) && (cl_ref == null) && (col_site == null))
                ref = project;
            else if ( (project == null) && (cl_ref != null) && (col_site != null))
                ref = cl_ref + ", " + col_site;
            else if ( (project == null) && (cl_ref != null) && (col_site == null))
                ref = cl_ref;
            else if ( (project == null) && (cl_ref == null) && (col_site != null))
                ref = col_site;
            else if ( (project == null) && (cl_ref == null) && (col_site != null))
                ref = project + ", " + col_site;

        } else {
            owner = field1;
            col_site = field2;
            collector = field3;
            if ( (collector == null) && (owner == null) && (col_site == null))
                ref = null;
            else if ( (collector != null) && (owner != null) && (col_site != null))
                ref = collector + ", " + owner + ", " + col_site;
            else if ( (collector != null) && (owner != null) && (col_site == null))
                ref = collector + ", " + owner;
            else if ( (collector != null) && (owner == null) && (col_site == null))
                ref = collector;
            else if ( (collector == null) && (owner != null) && (col_site == null))
                ref = owner + ", " + col_site;
            else if ( (collector == null) && (owner != null) && (col_site == null))
                ref = owner;
            else if ( (collector == null) && (owner == null) && (col_site != null))
                ref = col_site;
            else if ( (collector != null) && (owner == null) && (col_site != null))
                ref = collector + ", " + col_site;
        }
        return ref;
    }

}