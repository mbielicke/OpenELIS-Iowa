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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class UIHCFinalReportExportBean {
    @EJB
    private SessionCacheBean          session;
    @EJB
    private FinalReportBean           finalReport;
    @EJB
    private OrganizationParameterBean organizationParameter;
    @EJB
    private SampleManager1Bean        sampleManager;
    @EJB
    private SystemVariableBean        systemVariable;

    private static final Logger       log = Logger.getLogger("openelis");

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("BEGIN_RELEASED", Prompt.Type.DATETIME).setPrompt("Begin Released:")
                                                                    .setWidth(130)
                                                                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                    .setRequired(true));

            p.add(new Prompt("END_RELEASED", Prompt.Type.DATETIME).setPrompt("End Released:")
                                                                  .setWidth(130)
                                                                  .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                  .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                                                                  .setRequired(true));

            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /*
     * Method for calling from cron job
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void runReport() {
        try {
            runReport(null);
        } catch (Exception ignE) {
        }
    }

    /*
     * Generate Final Report PDFs for UIHC, send them via SecureFTP, and email 
     * a notification to UIHC.
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        boolean error;
        ArrayList<OrganizationParameterDO> orgParams;
        Calendar fromDate, toDate;
        Date beginReleased, endReleased;
        HashMap<String, QueryData> param;
        HashSet<Integer> uihcOrgIds, pathologyOrgIds;
        ReportStatus status;
        SimpleDateFormat format;

        format = new SimpleDateFormat(Messages.get().dateTimePattern());

        status = new ReportStatus();
        session.setAttribute("UIHCFinalReportExport", status);

        /*
         * recover all the params and build a specific where clause
         */
        beginReleased = null;
        endReleased = null;
        if (paramList != null) {
            param = ReportUtil.getMapParameter(paramList);
            beginReleased = format.parse(ReportUtil.getSingleParameter(param, "BEGIN_RELEASED"));
            endReleased = format.parse(ReportUtil.getSingleParameter(param, "END_RELEASED"));
        }

        if (beginReleased == null || endReleased == null) {
            /*
             * take all the released specimens between yesterday at 5 pm to 
             * today at 5 pm
             */
            fromDate = Calendar.getInstance();
            fromDate.set(Calendar.HOUR_OF_DAY, 17);
            fromDate.set(Calendar.MINUTE, 00);
            fromDate.add(Calendar.DAY_OF_MONTH, -1);
            beginReleased = fromDate.getTime();

            toDate = Calendar.getInstance();
            toDate.set(Calendar.HOUR_OF_DAY, 16);
            toDate.set(Calendar.MINUTE, 59);
            endReleased = toDate.getTime();
        }
        
        uihcOrgIds = new HashSet<Integer>();
        pathologyOrgIds = new HashSet<Integer>();
        orgParams = organizationParameter.fetchByDictionarySystemName("org_type");
        for (OrganizationParameterDO opDO : orgParams) {
            if ("UIHC TRANSFER".equals(opDO.getValue()))
                uihcOrgIds.add(opDO.getOrganizationId());
            else if ("UIHC PATHOLOGY TRANSFER".equals(opDO.getValue()))
                pathologyOrgIds.add(opDO.getOrganizationId());
        }
        
        error = false;
        if (uihcOrgIds != null && uihcOrgIds.size() > 0) {
            exportUIHC(beginReleased, endReleased, uihcOrgIds);
        } else {
            log.log(Level.SEVERE, "No Organzation ID found for UIHC Transfer");
            error = true;
        }

        if (pathologyOrgIds != null && pathologyOrgIds.size() > 0) {
            exportUIHCPathology(beginReleased, endReleased, pathologyOrgIds);
        } else {
            log.log(Level.SEVERE, "No Organzation ID found for UIHC Pathology Transfer");
            error = true;
        }
        
        if (error) {
            status.setMessage("Error exporting reports.");
        } else {
            status.setMessage("Reports exported.");
            status.setStatus(ReportStatus.Status.PRINTED);
        }
        
        return status;
    }
    
    private void exportUIHC(Date beginReleased, Date endReleased, HashSet<Integer> orgIds) {
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;
        ArrayList<SampleOrganizationViewDO> sampleOrgs;
        Integer orgId;
        OutputStream out;
        Path srcFile, destFile;
        QueryData field;
        ReportStatus status;
        String excludedTestIds, fileName, orgIdsString, tempDirectory, uploadStreamDirectory;
        StringBuilder emailBody;

        try {
            tempDirectory = systemVariable.fetchByName("uihc_transfer_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'uihc_transfer_directory' is not available");
            return;
        }

        try {
            uploadStreamDirectory = systemVariable.fetchByName("upload_stream_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'upload_stream_directory' is not available");
            return;
        }

        try {
            excludedTestIds = systemVariable.fetchByName("uihc_transfer_exclude_test_ids").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'uihc_transfer_exclude_test_ids' is not available");
            return;
        }

        fields = new ArrayList<QueryData>();
        
        field = new QueryData();
        field.setKey(SampleMeta.getDomain());
        field.setQuery("C");
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getAnalysisReleasedDate());
        field.setQuery(ReportUtil.toString(beginReleased, Messages.get().dateTimePattern())+".."+
                       ReportUtil.toString(endReleased, Messages.get().dateTimePattern()));
        field.setType(QueryData.Type.DATE);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getOrgId());
        orgIdsString = orgIds.toString();
        orgIdsString = orgIdsString.substring(1, orgIdsString.length() - 1).replace(',', '|');
        field.setQuery(orgIdsString);
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        
        excludedTestIds = "!" + excludedTestIds.replaceAll(",", "&!");
        field = new QueryData();
        field.setKey(SampleMeta.getAnalysisTestId());
        field.setQuery(excludedTestIds);
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        sms = new ArrayList<SampleManager1>();
        try {
            sms = sampleManager.fetchByQuery(fields, 0, 1000, SampleManager1.Load.ORGANIZATION);
        } catch (NotFoundException nfE) {
            // ignore
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Error fetching records for UIHC Final Report Transfer", anyE);
        }

        emailBody = new StringBuilder();
        emailBody.append("The following is a list of accession #s and UIHC references reported through the FTP interface\n")
                 .append("Accession#\tReference #\n")
                 .append("----------\t--------------------\n");
        
        if (sms.size() > 0) {
            for (SampleManager1 sm : sms) {
                orgId = null;
                sampleOrgs = sm.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
                if (sampleOrgs != null && sampleOrgs.size() > 0 &&
                    orgIds.contains(sampleOrgs.get(0).getOrganizationId())) {
                    orgId = sampleOrgs.get(0).getOrganizationId();
                } else {
                    sampleOrgs = sm.organization.getByType(Constants.dictionary().ORG_SECOND_REPORT_TO);
                    if (sampleOrgs != null && sampleOrgs.size() > 0) {
                        for (SampleOrganizationViewDO soVDO : sampleOrgs) {
                            if (orgIds.contains(soVDO.getOrganizationId())) {
                                orgId = soVDO.getOrganizationId();
                                break;
                            }
                        }
                    }
                }

                if (orgId == null)
                    continue;
                
                fields = new ArrayList<QueryData>();
                
                field = new QueryData();
                field.setKey("ACCESSION_NUMBER");
                field.setQuery(sm.getSample().getAccessionNumber().toString());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setKey("ORGANIZATION_ID");
                field.setQuery(orgId.toString());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setKey("PRINTER");
                field.setQuery("-pdf-");
                field.setType(QueryData.Type.STRING);
                fields.add(field);

                if (sm.getSample().getClientReference() != null)
                    fileName = sm.getSample().getClientReference() + "_" + sm.getSample().getAccessionNumber();
                else
                    fileName = "unknown_" + sm.getSample().getAccessionNumber();

                try {
                    status = finalReport.runReportForSingle(fields);
                    srcFile = Paths.get(uploadStreamDirectory, status.getMessage());
                    destFile = Paths.get(tempDirectory, fileName+".pdf");
                    Files.move(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                    Files.setPosixFilePermissions(destFile, EnumSet.of(PosixFilePermission.OWNER_READ,
                                                                       PosixFilePermission.OWNER_WRITE,
                                                                       PosixFilePermission.GROUP_READ,
                                                                       PosixFilePermission.OTHERS_READ));
                } catch (Exception anyE) {
                    log.log(Level.SEVERE, "Error generating UIHC Final Report for accession #" +
                                          sm.getSample().getAccessionNumber(), anyE);
                    continue;
                }
                
                emailBody.append(sm.getSample().getAccessionNumber()).append("  \t")
                         .append(fileName).append("\n");
            }
        }
        
        emailBody.append("----------\t------------------------------\n");
        
        out = null;
        try {
            out = Files.newOutputStream(Paths.get(tempDirectory, "UIHC_Email.txt"));
            out.write(emailBody.toString().getBytes());
        } catch (IOException ioE) {
            log.log(Level.SEVERE, "Error creating email text for UIHC Final Report Transfer", ioE);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e1) {
                log.severe("Could not close outout stream for uihc export");
            }
        }
        log.log(Level.FINE, "UIHC Final Report Transfer exported");
    }

    private void exportUIHCPathology(Date beginReleased, Date endReleased, HashSet<Integer> orgIds) {
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;
        ArrayList<SampleOrganizationViewDO> sampleOrgs;
        Integer orgId;
        OutputStream out;
        Path srcFile, destFile;
        QueryData field;
        ReportStatus status;
        String fileName, orgIdsString, tempDirectory, uploadStreamDirectory;
        StringBuilder emailBody;

        try {
            tempDirectory = systemVariable.fetchByName("uipathology_transfer_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'uipathology_transfer_directory' is not available");
            return;
        }

        try {
            uploadStreamDirectory = systemVariable.fetchByName("upload_stream_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'upload_stream_directory' is not available");
            return;
        }

        fields = new ArrayList<QueryData>();
        
        field = new QueryData();
        field.setKey(SampleMeta.getDomain());
        field.setQuery("C");
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getReleasedDate());
        field.setQuery(ReportUtil.toString(beginReleased, Messages.get().dateTimePattern())+".."+
                       ReportUtil.toString(endReleased, Messages.get().dateTimePattern()));
        field.setType(QueryData.Type.DATE);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getOrgId());
        orgIdsString = orgIds.toString();
        orgIdsString = orgIdsString.substring(1, orgIdsString.length() - 1).replace(',', '|');
        field.setQuery(orgIdsString);
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        sms = new ArrayList<SampleManager1>();
        try {
            sms = sampleManager.fetchByQuery(fields, 0, 1000, SampleManager1.Load.ORGANIZATION);
        } catch (NotFoundException nfE) {
            // ignore
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Error fetching records for UIHC Pathology Final Report Transfer", anyE);
        }

        emailBody = new StringBuilder();
        emailBody.append("The following is a list of accession #s and UIHC references reported through the FTP interface\n")
                 .append("Accession#\tReference #\n")
                 .append("----------\t--------------------\n");
        
        if (sms.size() > 0) {
            for (SampleManager1 sm : sms) {
                orgId = null;
                sampleOrgs = sm.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
                if (sampleOrgs != null && sampleOrgs.size() > 0 &&
                    orgIds.contains(sampleOrgs.get(0).getOrganizationId())) {
                    orgId = sampleOrgs.get(0).getOrganizationId();
                } else {
                    sampleOrgs = sm.organization.getByType(Constants.dictionary().ORG_SECOND_REPORT_TO);
                    if (sampleOrgs != null && sampleOrgs.size() > 0) {
                        for (SampleOrganizationViewDO soVDO : sampleOrgs) {
                            if (orgIds.contains(soVDO.getOrganizationId())) {
                                orgId = soVDO.getOrganizationId();
                                break;
                            }
                        }
                    }
                }

                if (orgId == null)
                    continue;
                
                fields = new ArrayList<QueryData>();
                
                field = new QueryData();
                field.setKey("ACCESSION_NUMBER");
                field.setQuery(sm.getSample().getAccessionNumber().toString());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setKey("ORGANIZATION_ID");
                field.setQuery(orgId.toString());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setKey("PRINTER");
                field.setQuery("-pdf-");
                field.setType(QueryData.Type.STRING);
                fields.add(field);

                if (sm.getSample().getClientReference() != null)
                    fileName = sm.getSample().getClientReference() + "_" + sm.getSample().getAccessionNumber();
                else
                    fileName = "unknown_" + sm.getSample().getAccessionNumber();

                try {
                    status = finalReport.runReportForSingle(fields);
                    srcFile = Paths.get(uploadStreamDirectory, status.getMessage());
                    destFile = Paths.get(tempDirectory, fileName+".pdf");
                    Files.move(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                    Files.setPosixFilePermissions(destFile, EnumSet.of(PosixFilePermission.OWNER_READ,
                                                                       PosixFilePermission.OWNER_WRITE,
                                                                       PosixFilePermission.GROUP_READ,
                                                                       PosixFilePermission.OTHERS_READ));
                } catch (Exception anyE) {
                    log.log(Level.SEVERE, "Error generating UIHC Pathology Final Report for accession #" +
                                          sm.getSample().getAccessionNumber(), anyE);
                    continue;
                }
                
                emailBody.append(sm.getSample().getAccessionNumber()).append("  \t")
                         .append(fileName).append("\n");
            }
        }
        
        emailBody.append("----------\t------------------------------\n");
        
        out = null;
        try {
            out = Files.newOutputStream(Paths.get(tempDirectory, "UIHC_Pathology_Email.txt"));
            out.write(emailBody.toString().getBytes());
            out.close();
            out = null;
        } catch (IOException ioE) {
            log.log(Level.SEVERE, "Error creating email text for UIHC Pathology Final Report Transfer", ioE);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e1) {
                log.severe("Could not close outout stream for uihc pathology export");
            }
        }
        log.log(Level.FINE, "UIHC Pathology Final Report Transfer exported");
    }

    protected void sendEmail(String toEmail, String subject, String body) {
        try {
            ReportUtil.sendEmail("do-not-reply@shl.uiowa.edu", toEmail, subject, body);
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Error sending email for UIHC/Pathology Final Report Transfer", anyE);
        }
    }
}