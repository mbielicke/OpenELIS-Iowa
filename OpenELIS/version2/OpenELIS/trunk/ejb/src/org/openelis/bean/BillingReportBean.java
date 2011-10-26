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

import java.io.BufferedWriter;
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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisReportFlagsLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
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
    private SamplePrivateWellLocal  samplePrivateWell;
    
    @EJB
    private SampleEnvironmentalLocal  sampleEnvironmental;
    
    @EJB
    private SampleSDWISLocal  sampleSDWIS;
    
    @EJB
    private SampleProjectLocal sampleProject;

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

    /*
     * Execute the report and email its output to specified addresses
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        int i;
        ReportStatus status;
        Object[] result;
        ArrayList<Object[]> resultList;
        Integer samId, prevSamId, samAcc, anaId, reportToOrgId, clientCode ;
        String domain, email, owner, collector, clientReference, location, clientName, billingType, lastName, firstName, pws,
        clientAddress1, clientAddress2, clientCity, clientState, clientZip, clientPatientId1, clientPatientId2, projectName, orgMultipleUnit, orgStAddress, receivedDate,
        accessionNo, procedureCode, procedureDescription, transactionType, transactionCode, priceOverride, section, labCode, labDept;
        String[] tokens;
        Date stDate, endDate;
        SystemVariableDO data;
        Timestamp rcDate;
        Calendar cal;
        DateFormat df;
        ArrayList<Object[]> l;
        SampleEnvironmentalDO envDO;
        SampleSDWISViewDO sdwisDO;
        SamplePrivateWellViewDO privateWell;
        SampleOrganizationViewDO sampleOrg;
        ArrayList<SampleOrganizationViewDO> sampleOrgReportToList;
        ArrayList<SampleProjectViewDO> sampleProjectList;
        StringBuilder text;
        FileWriter out;
        File tempFile;
        BufferedWriter writer;
        
        samId = null;
        samAcc = null;
        owner = null;
        anaId = null;
        clientCode = null;
        procedureCode = procedureDescription = transactionType = transactionCode = priceOverride = section = labCode = labDept = null;
        resultList = null;
        orgMultipleUnit = firstName = clientPatientId2 = null;
        orgStAddress = null;
        clientPatientId1 = null;
        receivedDate = null;
        out = null;
        writer = null;
        text = new StringBuilder();
        envDO = new SampleEnvironmentalDO();
        sdwisDO = new SampleSDWISViewDO();
        privateWell = new SamplePrivateWellViewDO();
        sampleOrg = new SampleOrganizationViewDO();
        envDO = new SampleEnvironmentalDO();
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("BillingReport", status);

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1);
        endDate = cal.getTime();

        try {
            data = systemVariable.fetchByName("last_billing_report_run");
            stDate = df.parse(data.getValue());
        } catch (Exception e) {
            throw e;
        }

        if (stDate.compareTo(endDate) < 0)
            resultList = sample.fetchForBillingReport(stDate, endDate);
        else
            throw new InconsistencyException("Start Date should be earlier than End Date");
        
        prevSamId = null;
       
        for (i = 0; i < resultList.size(); i++ ) {
            result = resultList.get(i);
            samId = (Integer)result[0];
            if (!samId.equals(prevSamId)) {
                samAcc = (Integer)result[1];
                domain = (String)result[2];
                clientReference = (String)result[3];
                rcDate = (Timestamp)result[4];
                anaId = (Integer)result[5];                
                receivedDate = df.format(rcDate);
                billingType = "Recur";
                if ("W".equals(domain)) {
                    privateWell = samplePrivateWell.fetchBySampleId(samId);
                    owner = privateWell.getOwner();
                    collector = privateWell.getCollector();
                    location = privateWell.getLocation();
                    if (clientReference != null)
                        clientPatientId1 = "REF-" + DataBaseUtil.trim(clientReference);
                    if (owner != null)
                        clientPatientId1 += ", OWN-" + DataBaseUtil.trim(owner);
                    if (collector != null)
                        clientPatientId1 += ", COL-" + DataBaseUtil.trim(collector);
                    if (location != null)
                        clientPatientId1 += ", LOC-" + DataBaseUtil.trim(location);
                } else if ("E".equals(domain)) {
                    envDO = sampleEnvironmental.fetchBySampleId(samId);
                    collector = envDO.getCollector();
                    location = envDO.getLocation();
                    if (clientReference != null)
                        clientPatientId1 = "REF-" + DataBaseUtil.trim(clientReference);
                    if (collector != null)
                        clientPatientId1 += ", COL-" + DataBaseUtil.trim(collector);
                    if (location != null)
                        clientPatientId1 += ", LOC-" + DataBaseUtil.trim(location);
                } else if ("S".equals(domain)) {
                    sdwisDO = sampleSDWIS.fetchBySampleId(samId);
                    collector = sdwisDO.getCollector();
                    location = sdwisDO.getLocation();
                    pws = sdwisDO.getPwsNumber0();
                    if (pws != null)
                        clientPatientId1 = "PWS-" + DataBaseUtil.trim(pws);
                    if (clientReference != null)
                        clientPatientId1 = ", REF-" + DataBaseUtil.trim(clientReference);
                    if (collector != null)
                        clientPatientId1 += ", COL-" + DataBaseUtil.trim(collector);
                    if (location != null)
                        clientPatientId1 += ", LOC-" + DataBaseUtil.trim(location);
                }
                /*
                 * Primary Project
                 */
                try {
                    sampleProjectList = sampleProject.fetchPermanentBySampleId(samId);
                    projectName = sampleProjectList.get(0).getProjectName();
                } catch (NotFoundException e) {
                    projectName = null;
                }
                /*
                 * If sample has BillTo organization id, make it the clientCode,
                 * if not, then make reportTo organization id the clientCode.
                 */
                try {
                    sampleOrg = sampleOrganization.fetchBillToBySampleId(samId);
                    clientCode = sampleOrg.getOrganizationId();
                    clientName = sampleOrg.getOrganizationName();                   
                    lastName = samAcc.toString();
                    orgMultipleUnit = sampleOrg.getOrganizationMultipleUnit();
                    orgStAddress = sampleOrg.getOrganizationStreetAddress();
                    clientCity = sampleOrg.getOrganizationCity();
                    clientState = sampleOrg.getOrganizationState();
                    clientZip = sampleOrg.getOrganizationZipCode();
                } catch (NotFoundException e) {
                    if("W".equals(domain)){
                        if(privateWell.getOrganizationId() != null){
                            clientCode = (Integer) (privateWell.getOrganizationId());
                            clientName = (String) (privateWell.getOrganization().getName());
                            lastName = samAcc.toString();
                            orgMultipleUnit = (String) (privateWell.getOrganization().getAddress().getMultipleUnit());
                            orgStAddress = (String) (privateWell.getOrganization().getAddress().getStreetAddress());
                            clientCity = privateWell.getOrganization().getAddress().getCity();
                            clientState =  privateWell.getOrganization().getAddress().getState();
                            clientZip =  privateWell.getOrganization().getAddress().getZipCode();
                        } else {
                        
                        /*
                         * If the sample doesn't have any billTo or reportTo
                         * organization id, then its billing type is "OneTime",
                         * else it will be of type Recur.
                         */
                        billingType = "OneTime";
                        if (owner != null)
                            lastName = owner;
                        else
                            lastName = privateWell.getReportToName();

                        clientName = privateWell.getReportToName();
                        orgMultipleUnit = privateWell.getReportToAddress().getMultipleUnit();
                        orgStAddress = privateWell.getReportToAddress().getStreetAddress();
                        clientCity = privateWell.getReportToAddress().getCity();
                        clientState = privateWell.getReportToAddress().getState();
                        clientZip = privateWell.getReportToAddress().getZipCode();
                        }
                    
                    } else {
                        sampleOrgReportToList = sampleOrganization.fetchReportToBySampleId(samId);
                        clientCode = sampleOrgReportToList.get(0).getOrganizationId();
                        clientName = sampleOrgReportToList.get(0).getOrganizationName();
                        lastName = samAcc.toString();
                        orgMultipleUnit = sampleOrgReportToList.get(0)
                                                               .getOrganizationMultipleUnit();
                        orgStAddress = sampleOrgReportToList.get(0).getOrganizationStreetAddress();
                        clientCity = sampleOrgReportToList.get(0).getOrganizationCity();
                        clientState = sampleOrgReportToList.get(0).getOrganizationState();
                        clientZip = sampleOrgReportToList.get(0).getOrganizationZipCode();
                    }
                }
                clientAddress1 = null;
                clientAddress2 = null;
                if (!DataBaseUtil.isEmpty(orgMultipleUnit)) {
                    clientAddress1 = orgMultipleUnit;
                    clientAddress2 = orgStAddress;
                } else if (!DataBaseUtil.isEmpty(orgStAddress) ) {
                    clientAddress1 = orgStAddress;
                }                
                text.append("HDR")
                    .append("|")
                    .append(endDate)
                    .append("|")
                    .append(samAcc)
                    .append("|")
                    .append(billingType)
                    .append("|")
                    .append(receivedDate)
                    .append("|");
                if(clientCode != null)
                    text.append(clientCode);
                text.append("|")
                    .append(lastName)
                    .append("|");
                if(firstName != null)
                    text.append(firstName);
                text.append("|")
                    .append(clientName)
                    .append("|")
                    .append(clientAddress1)
                    .append("|");
                if(clientAddress2 != null)
                    text.append(clientAddress2);
                text.append("|")
                    .append(clientCity)
                    .append("|")
                    .append(clientState)
                    .append("|")
                    .append(clientZip)
                    .append("|");
                if(clientPatientId1 != null)
                    text.append(clientPatientId1);
                text.append("|");
                if(clientPatientId2 != null)
                    text.append(clientPatientId1);
                text.append("|"); 
                if(projectName != null)
                    text.append(projectName);
                text.append("|");
                text.append("\n");
        }
            //process DTR
            
            prevSamId = samId;
            samAcc = (Integer)result[1];
            procedureCode = ((Integer)result[6]).toString();
            procedureDescription = DataBaseUtil.trim((String)result[7])+ " by "+ DataBaseUtil.trim((String)result[8]);
            section = DataBaseUtil.trim((String)result[9]);
            tokens = section.split("-");
            if(tokens.length == 2){
                labDept = tokens[0];
                labCode = tokens[1];
            }
            text.append("DTR")
            .append("|")
            .append(endDate)
            .append("|")
            .append(samAcc.toString())
            .append("|")
            .append(procedureCode)
            .append("|")
            .append(procedureDescription)
            .append("|")
            .append(labCode)
            .append("|")
            .append(labDept)
            .append("|")
            //.append(anaId.toString())
            //.append("|")
            .append("\n");
    }
        try {
            status.setMessage("Outputing report").setPercentComplete(20);
            tempFile = File.createTempFile("billingReport", ".txt", new File("/tmp"));
            
            status.setPercentComplete(100);

            out = new FileWriter(tempFile);
            writer = new BufferedWriter(out);
            out.write(text.toString());
            out.close();
            tempFile = ReportUtil.saveForUpload(tempFile);
            status.setMessage(tempFile.getName())
                .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                .setStatus(ReportStatus.Status.SAVED);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
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
                anaList = anaReportFlagsBean.fetchForUpdateBySampleId(sampleId);
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