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

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class ChlGcToCDCExportBean {

    @Resource
    private SessionContext            ctx;

    @EJB
    private SessionCacheBean          session;
    @EJB
    private CategoryCacheBean         categoryCache;
    @EJB
    private DictionaryCacheBean       dictionaryCache;
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

            p.add(new Prompt("BEGIN_COLLECTION", Prompt.Type.DATETIME).setPrompt("Begin Collection:")
                                                                      .setWidth(130)
                                                                      .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                      .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                      .setRequired(true));

            p.add(new Prompt("END_COLLECTION", Prompt.Type.DATETIME).setPrompt("End Collection:")
                                                                    .setWidth(130)
                                                                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                                    .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                                    .setRequired(true));

            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        ArrayList<HashMap<String, Object>> rows;
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;
        Connection con;
        HashMap<String, QueryData> param;
        Integer orgReportToId, orgTypeId;
        QueryData field;
        ReportStatus status;
        Date fromDate, toDate;
        String exportDirectory, summary;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("ChlGcToCDCExport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        fromDate = ReportUtil.getDateParameter(param, "BEGIN_COLLECTION");
        toDate = ReportUtil.getDateParameter(param, "END_COLLECTION");

        try {
            exportDirectory = systemVariable.fetchByName("chlgc_cdc_directory").getValue();
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "System variable 'chlgc_cdc_directory' is not available");
            throw new Exception("System variable 'chlgc_cdc_directory' is not available");
        }

        try {
            orgReportToId = dictionaryCache.getIdBySystemName("org_report_to");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Dictionary with system name 'org_report_to' is not available");
            throw new Exception("Dictionary with system name 'org_report_to' is not available");
        }

        try {
            orgTypeId = dictionaryCache.getIdBySystemName("org_type");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Dictionary with system name 'org_type' is not available");
            throw new Exception("Dictionary with system name 'org_type' is not available");
        }

        /*
         * start the report
         */
        try {
            status.setMessage("Initializing report");
            session.setAttribute("ChlGcToCDCExport", status);

            fields = new ArrayList<QueryData>();

            field = new QueryData();
            field.setKey(SampleMeta.getDomain());
            field.setQuery("C");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getCollectionDate());
            field.setQuery(fromDate + ".." + toDate);
            field.setType(QueryData.Type.DATE);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getStatusId());
            field.setQuery("!" + Constants.dictionary().SAMPLE_NOT_VERIFIED);
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getSampleOrgTypeId());
            field.setQuery(orgReportToId.toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getOrgParamTypeId());
            field.setQuery(orgTypeId.toString());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getOrgParamValue());
            field.setQuery("Family Planning Clinic|STD Clinic|Student Health Services|Correctional Facility|Prenatal Clinic|Indian Health Services|Community Health Center|Other");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisTestName());
            field.setQuery("chl-gc cbss");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisIsReportable());
            field.setQuery("Y");
            field.setType(QueryData.Type.STRING);
            fields.add(field);

            field = new QueryData();
            field.setKey(SampleMeta.getAnalysisStatusId());
            field.setQuery("!" + Constants.dictionary().ANALYSIS_CANCELLED);
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);

            status.setMessage("Fetching records").setPercentComplete(20);
            session.setAttribute("ChlGcToCDCExport", status);

            sms = new ArrayList<SampleManager1>();
            sms = sampleManager.fetchByQuery(fields,
                                             0,
                                             10000,
                                             SampleManager1.Load.ORGANIZATION,
                                             SampleManager1.Load.QA,
                                             SampleManager1.Load.RESULT);

            status.setMessage("Building dataset").setPercentComplete(50);
            session.setAttribute("ChlGcToCDCExport", status);

            con = ReportUtil.getConnection(ctx);
            rows = new ArrayList<HashMap<String, Object>>();
            summary = buildDataSet(sms, rows, con);

            status.setMessage("Outputing report").setPercentComplete(75);
            session.setAttribute("ChlGcToCDCExport", status);

            export(rows, exportDirectory);

            status.setPercentComplete(100)
                  .setMessage(summary)
                  .setStatus(ReportStatus.Status.PRINTED);
            session.setAttribute("ChlGcToCDCExport", status);
        } catch (NotFoundException nfE) {
            log.log(Level.INFO, "No samples found for ChlGCToCDC Export");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return status;
    }

    private String buildDataSet(ArrayList<SampleManager1> sms,
                                ArrayList<HashMap<String, Object>> rows, Connection con) throws Exception {
        boolean sOverride, aOverride;
        int numPrint, numTotal, numUnreleased;
        AddressDO adrDO;
        ArrayList<OrganizationParameterDO> opDOs;
        DictionaryDO dictDO;
        HashMap<Integer, DictionaryDO> dictionaryDOs;
        HashMap<Integer, String> orgTypes;
        HashMap<String, Integer> cityCountyMap;
        HashMap<String, Object> row;
        Integer countyNumber;
        PatientDO patDO;
        PreparedStatement ccS;
        ResultSet rs;
        SampleDO sDO;
        SampleOrganizationViewDO reportToVDO;
        String countyFips, orgType;

        log.log(Level.FINE, "Preloading dictionary entries");
        dictionaryDOs = new HashMap<Integer, DictionaryDO>();
        for (DictionaryDO ddo : categoryCache.getBySystemName("analysis_status")
                                             .getDictionaryList())
            dictionaryDOs.put(ddo.getId(), ddo);
        for (DictionaryDO ddo : categoryCache.getBySystemName("ethnicity").getDictionaryList())
            dictionaryDOs.put(ddo.getId(), ddo);
        for (DictionaryDO ddo : categoryCache.getBySystemName("gender").getDictionaryList())
            dictionaryDOs.put(ddo.getId(), ddo);
        for (DictionaryDO ddo : categoryCache.getBySystemName("race").getDictionaryList())
            dictionaryDOs.put(ddo.getId(), ddo);
        for (DictionaryDO ddo : categoryCache.getBySystemName("type_of_sample").getDictionaryList())
            dictionaryDOs.put(ddo.getId(), ddo);

        try {
            ccS = con.prepareStatement("select name, county_number from phims@rambaldi_trust:city");
            rs = ccS.executeQuery();
            cityCountyMap = new HashMap<String, Integer>();
            while (rs.next())
                cityCountyMap.put(rs.getString(1), rs.getInt(2));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error preparing statetment for county lookup.", e);
            throw new Exception("Error preparing statetment for county lookup.");
        }

        numTotal = 0;
        numPrint = 0;
        numUnreleased = 0;
        if (sms != null && sms.size() > 0) {
            orgTypes = new HashMap<Integer, String>();
            log.log(Level.FINE, "Processing samples");
            for (SampleManager1 sm : sms) {
                sDO = SampleManager1Accessor.getSample(sm);

                sOverride = false;
                if (SampleManager1Accessor.getSampleQAs(sm) != null) {
                    for (SampleQaEventViewDO sqeVDO : SampleManager1Accessor.getSampleQAs(sm)) {
                        if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqeVDO.getTypeId())) {
                            sOverride = true;
                            break;
                        }
                    }
                }

                reportToVDO = null;
                if (SampleManager1Accessor.getOrganizations(sm) != null) {
                    for (SampleOrganizationViewDO soVDO : SampleManager1Accessor.getOrganizations(sm)) {
                        if (Constants.dictionary().ORG_REPORT_TO.equals(soVDO.getTypeId())) {
                            reportToVDO = soVDO;
                            break;
                        }
                    }
                }

                if (reportToVDO == null)
                    throw new Exception("Accession #" + sDO.getAccessionNumber() +
                                        "does not have a 'Report To' organization.");

                orgType = orgTypes.get(reportToVDO.getOrganizationId());
                if (orgType == null) {
                    opDOs = organizationParameter.fetchByOrgIdAndDictSystemName(reportToVDO.getOrganizationId(),
                                                                                "org_type");
                    if (opDOs != null && opDOs.size() > 0) {
                        for (OrganizationParameterDO opDO : opDOs) {
                            if ("Family Planning Clinic".equals(opDO.getValue()) ||
                                "STD Clinic".equals(opDO.getValue()) ||
                                "Student Health Services".equals(opDO.getValue()) ||
                                "Correctional Facility".equals(opDO.getValue()) ||
                                "Prenatal Clinic".equals(opDO.getValue()) ||
                                "Indian Health Services".equals(opDO.getValue()) ||
                                "Community Health Center".equals(opDO.getValue()) ||
                                "Other".equals(opDO.getValue())) {
                                orgType = opDO.getValue();
                                orgTypes.put(reportToVDO.getOrganizationId(), orgType);
                                break;
                            }
                        }
                    }
                    if (orgType == null)
                        continue;
                }

                if (SampleManager1Accessor.getAnalyses(sm) != null) {
                    for (AnalysisViewDO aVDO : SampleManager1Accessor.getAnalyses(sm)) {
                        if ("chl-gc cbss".equals(aVDO.getTestName()) &&
                            "Y".equals(aVDO.getIsReportable()) &&
                            !Constants.dictionary().ANALYSIS_CANCELLED.equals(aVDO.getStatusId())) {
                            row = new HashMap<String, Object>();
                            row.put("accession_number", sDO.getAccessionNumber());
                            row.put("collection_date", ReportUtil.toString(sDO.getCollectionDate(),
                                                                           "MM/dd/yyyy"));
                            row.put("received_date", ReportUtil.toString(sDO.getReceivedDate(),
                                                                         "MM/dd/yyyy"));

                            if (SampleManager1Accessor.getSampleClinical(sm).getPatient() != null) {
                                patDO = SampleManager1Accessor.getSampleClinical(sm).getPatient();
                                if (patDO != null) {
                                    row.put("birth_date", ReportUtil.toString(patDO.getBirthDate(),
                                                                              "MM/dd/yyyy"));
                                    if (patDO.getGenderId() != null)
                                        row.put("gender", dictionaryDOs.get(patDO.getGenderId())
                                                                       .getCode());
                                    if (patDO.getRaceId() != null)
                                        row.put("race", dictionaryDOs.get(patDO.getRaceId())
                                                                     .getCode());
                                    if (patDO.getEthnicityId() != null)
                                        row.put("ethnicity",
                                                dictionaryDOs.get(patDO.getEthnicityId()).getCode());
                                    adrDO = patDO.getAddress();
                                    if (adrDO != null) {
                                        if (adrDO.getState() != null)
                                            row.put("state_fips", getStateFIPS(adrDO.getState()));
                                        if ("IA".equals(adrDO.getState()) && adrDO.getCity() != null) {
                                            countyNumber = cityCountyMap.get(adrDO.getCity()
                                                                                  .toLowerCase());
                                            if (countyNumber != null) {
                                                countyNumber = countyNumber * 2 - 1;
                                                countyFips = countyNumber.toString();
                                                if (countyFips.length() < 3) {
                                                    while (countyFips.length() < 3)
                                                        countyFips = "0" + countyFips;
                                                }
                                                row.put("county_fips", countyFips);
                                            }
                                        }
                                    }
                                }
                            }

                            row.put("o_id", reportToVDO.getOrganizationId());
                            row.put("zip_code", reportToVDO.getOrganizationZipCode());
                            row.put("org_type", orgType);

                            for (SampleItemViewDO siVDO : SampleManager1Accessor.getItems(sm)) {
                                if (siVDO.getId().equals(aVDO.getSampleItemId())) {
                                    if (siVDO.getTypeOfSampleId() != null)
                                        row.put("source",
                                                getSourceFromCode(dictionaryDOs.get(siVDO.getTypeOfSampleId())
                                                                               .getCode()));
                                    break;
                                }
                            }

                            if (aVDO.getStatusId() != null) {
                                row.put("status", dictionaryDOs.get(aVDO.getStatusId()).getEntry());
                                if (!Constants.dictionary().ANALYSIS_RELEASED.equals(aVDO.getStatusId()))
                                    numUnreleased++;
                            }
                            row.put("printed_date", ReportUtil.toString(aVDO.getPrintedDate(),
                                                                        "MM/dd/yyyy"));
                            row.put("method", aVDO.getMethodName().substring(0, 1).toUpperCase());

                            aOverride = false;
                            if (SampleManager1Accessor.getAnalysisQAs(sm) != null) {
                                for (AnalysisQaEventViewDO aqeVDO : SampleManager1Accessor.getAnalysisQAs(sm)) {
                                    if (aqeVDO.getAnalysisId().equals(aVDO.getId()) &&
                                        Constants.dictionary().QAEVENT_OVERRIDE.equals(aqeVDO.getTypeId())) {
                                        aOverride = true;
                                        break;
                                    }
                                }
                            }

                            row.put("risk_new", "N");
                            row.put("risk_multiple", "N");
                            row.put("risk_contact", "N");
                            row.put("risk_msm", "N");
                            row.put("risk_none", "N");
                            row.put("sign_cervical", "N");
                            row.put("sign_cervicitis", "N");
                            row.put("sign_pid", "N");
                            row.put("sign_urethritis", "N");
                            row.put("sign_no_exam", "N");
                            row.put("sign_none", "N");
                            if (SampleManager1Accessor.getResults(sm) != null) {
                                for (ResultViewDO rVDO : SampleManager1Accessor.getResults(sm)) {
                                    if (rVDO.getAnalysisId().equals(aVDO.getId())) {
                                        dictDO = null;
                                        if (rVDO.getValue() != null && rVDO.getValue().length() > 0) {
                                            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(rVDO.getTypeId())) {
                                                dictDO = dictionaryDOs.get(Integer.valueOf(rVDO.getValue()));
                                                if (dictDO == null) {
                                                    dictDO = dictionaryCache.getById(Integer.valueOf(rVDO.getValue()));
                                                    dictionaryDOs.put(Integer.valueOf(rVDO.getValue()),
                                                                      dictDO);
                                                }
                                                if (dictDO == null)
                                                    throw new Exception("Dictionary entry not found for ID " +
                                                                        rVDO.getValue());
                                            }

                                            if ("visit_reason".equals(rVDO.getAnalyteExternalId())) {
                                                row.put("visit_reason", dictDO.getCode());
                                            } else if ("risk_history".equals(rVDO.getAnalyteExternalId())) {
                                                if (dictDO != null) {
                                                    if ("risk_new".equals(dictDO.getSystemName()))
                                                        row.put("risk_new", "Y");
                                                    else if ("risk_multiple".equals(dictDO.getSystemName()))
                                                        row.put("risk_multiple", "Y");
                                                    else if ("risk_contact".equals(dictDO.getSystemName()))
                                                        row.put("risk_contact", "Y");
                                                    else if ("risk_msm".equals(dictDO.getSystemName()))
                                                        row.put("risk_msm", "Y");
                                                    else if ("none_of_the_above".equals(dictDO.getSystemName()))
                                                        row.put("risk_none", "Y");
                                                }
                                            } else if ("symptom".equals(rVDO.getAnalyteExternalId())) {
                                                row.put("symptom", dictDO.getCode());
                                            } else if ("sign".equals(rVDO.getAnalyteExternalId())) {
                                                if (dictDO != null) {
                                                    if ("sign_cervical".equals(dictDO.getSystemName()))
                                                        row.put("sign_cervical", "Y");
                                                    else if ("sign_cervicitis".equals(dictDO.getSystemName()))
                                                        row.put("sign_cervicitis", "Y");
                                                    else if ("sign_pid".equals(dictDO.getSystemName()))
                                                        row.put("sign_pid", "Y");
                                                    else if ("sign_urethritis".equals(dictDO.getSystemName()))
                                                        row.put("sign_urethritis", "Y");
                                                    else if ("sign_no_exam".equals(dictDO.getSystemName()))
                                                        row.put("sign_no_exam", "Y");
                                                    else if ("none_of_the_above".equals(dictDO.getSystemName()))
                                                        row.put("sign_none", "Y");
                                                }
                                            } else if ("insurance_type".equals(rVDO.getAnalyteExternalId())) {
                                                row.put("insurance_type", dictDO.getCode());
                                            } else if ("chl_result".equals(rVDO.getAnalyteExternalId())) {
                                                if (sOverride || aOverride) {
                                                    row.put("chl_result", "U");
                                                } else {
                                                    if (dictDO.getEntry().startsWith("D"))
                                                        row.put("chl_result", "P");
                                                    else
                                                        row.put("chl_result",
                                                                dictDO.getEntry().substring(0, 1));
                                                }
                                            } else if ("gc_result".equals(rVDO.getAnalyteExternalId())) {
                                                if (sOverride || aOverride) {
                                                    row.put("gc_result", "U");
                                                } else {
                                                    if (dictDO.getEntry().startsWith("D"))
                                                        row.put("gc_result", "P");
                                                    else
                                                        row.put("gc_result", dictDO.getEntry()
                                                                                   .substring(0, 1));
                                                }
                                            }
                                        }
                                    }
                                }
                                rows.add(row);
                                numTotal++;
                                numPrint += 2;
                            }
                        }
                    }
                }
            }
        }

        return "Processed = " + numTotal + " Reported = " + numPrint + " Unreleased = " +
               numUnreleased;
    }

    /*
     * Exports the data into a zip file
     */
    private void export(ArrayList<HashMap<String, Object>> rows, String exportDirectory) throws Exception {
        Path path;
        PrintWriter writer;
        String race;
        StringBuilder line;
        ZipOutputStream out;

        try {
            path = Paths.get(exportDirectory, "cdc.zip");
        } catch (Exception anyE) {
            log.log(Level.SEVERE, "Could not open zip file for writing.");
            throw new Exception("Could not open zip file for writing.");
        }

        writer = null;
        out = null;
        try {
            out = new ZipOutputStream(Files.newOutputStream(path));
            out.putNextEntry(new ZipEntry("cdc.dat"));

            writer = new PrintWriter(out);

            line = new StringBuilder();

            /*
             * Write the header line
             */
            line.append("Accession #")
                .append("|")
                .append("County FIPS")
                .append("|")
                .append("State FIPS")
                .append("|")
                .append("RaceW")
                .append("|")
                .append("RaceB")
                .append("|")
                .append("RaceA")
                .append("|")
                .append("RaceI")
                .append("|")
                .append("RaceP")
                .append("|")
                .append("RaceU")
                .append("|")
                .append("Ethnicity (H,N,U)")
                .append("|")
                .append("Birth")
                .append("|")
                .append("Gender")
                .append("|")
                .append("Source")
                .append("|")
                .append("Collected")
                .append("|")
                .append("Received")
                .append("|")
                .append("Reported")
                .append("|")
                .append("Result (P,N,E,U)")
                .append("|")
                .append("Method")
                .append("|")
                .append("Test (CHL,GC)")
                .append("|")
                .append("Visit-Reason (F,S,P,R,I)")
                .append("|")
                .append("Risk-new-partner")
                .append("|")
                .append("Risk-multiple-partner")
                .append("|")
                .append("Risk-contact-std")
                .append("|")
                .append("Risk-msm")
                .append("|")
                .append("Risk-none")
                .append("|")
                .append("Symptoms (Y,N)")
                .append("|")
                .append("Sign-Cervical")
                .append("|")
                .append("Sign-Cervicitis")
                .append("|")
                .append("Sign-PID")
                .append("|")
                .append("Sign-Urethritis")
                .append("|")
                .append("Sign-No-Signs")
                .append("|")
                .append("Sign-No-Exam")
                .append("|")
                .append("Insurance (N,D,P,C,M)")
                .append("|")
                .append("Requester-ID")
                .append("|")
                .append("Req-Zip")
                .append("|")
                .append("Req-Type")
                .append("|")
                .append("CLIA #");
            writer.println(line.toString());
            line.setLength(0);

            /*
             * Write data lines
             */
            for (HashMap<String, Object> row : rows) {
                line.append(row.get("accession_number")).append("|");
                if (row.get("county_fips") != null)
                    line.append(row.get("county_fips"));
                line.append("|");
                if (row.get("state_fips") != null)
                    line.append(row.get("state_fips"));
                line.append("|");

                race = (String)row.get("race");
                if (race != null) {
                    if (race.indexOf("W") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("B") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("A") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("I") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("H") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("U") != -1)
                        line.append("1");
                    line.append("|");
                } else {
                    line.append("|").append("|").append("|").append("|").append("|").append("|");
                }

                if (row.get("ethnicity") != null)
                    line.append(row.get("ethnicity"));
                line.append("|").append(row.get("birth_date")).append("|");
                if (row.get("gender") != null)
                    line.append(row.get("gender"));
                line.append("|");
                if (row.get("source") != null)
                    line.append(row.get("source"));
                line.append("|")
                    .append(row.get("collection_date"))
                    .append("|")
                    .append(row.get("received_date"))
                    .append("|")
                    .append(row.get("printed_date"))
                    .append("|");
                if (row.get("chl_result") != null)
                    line.append(row.get("chl_result"));
                line.append("|").append(row.get("method")).append("|").append("CHL").append("|");
                if (row.get("visit_reason") != null)
                    line.append(row.get("visit_reason"));
                line.append("|")
                    .append(row.get("risk_new"))
                    .append("|")
                    .append(row.get("risk_multiple"))
                    .append("|")
                    .append(row.get("risk_contact"))
                    .append("|")
                    .append(row.get("risk_msm"))
                    .append("|")
                    .append(row.get("risk_none"))
                    .append("|");
                if (row.get("symptom") != null)
                    line.append(row.get("symptom"));
                line.append("|")
                    .append(row.get("sign_cervical"))
                    .append("|")
                    .append(row.get("sign_cervicitis"))
                    .append("|")
                    .append(row.get("sign_pid"))
                    .append("|")
                    .append(row.get("sign_urethritis"))
                    .append("|")
                    .append(row.get("sign_none"))
                    .append("|")
                    .append(row.get("sign_no_exam"))
                    .append("|");
                if (row.get("insurance_type") != null)
                    line.append(row.get("insurance_type"));
                line.append("|")
                    .append(row.get("o_id"))
                    .append("|")
                    .append(row.get("zip_code"))
                    .append("|")
                    .append(row.get("org_type"))
                    .append("|")
                    .append("16D0648109");
                writer.println(line.toString());
                line.setLength(0);

                line.append(row.get("accession_number")).append("|");
                if (row.get("county_fips") != null)
                    line.append(row.get("county_fips"));
                line.append("|");
                if (row.get("state_fips") != null)
                    line.append(row.get("state_fips"));
                line.append("|");

                race = (String)row.get("race");
                if (race != null) {
                    if (race.indexOf("W") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("B") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("A") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("I") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("H") != -1)
                        line.append("1");
                    line.append("|");
                    if (race.indexOf("U") != -1)
                        line.append("1");
                    line.append("|");
                } else {
                    line.append("|").append("|").append("|").append("|").append("|").append("|");
                }

                if (row.get("ethnicity") != null)
                    line.append(row.get("ethnicity"));
                line.append("|").append(row.get("birth_date")).append("|");
                if (row.get("gender") != null)
                    line.append(row.get("gender"));
                line.append("|");
                if (row.get("source") != null)
                    line.append(row.get("source"));
                line.append("|")
                    .append(row.get("collection_date"))
                    .append("|")
                    .append(row.get("received_date"))
                    .append("|")
                    .append(row.get("printed_date"))
                    .append("|");
                if (row.get("gc_result") != null)
                    line.append(row.get("gc_result"));
                line.append("|").append(row.get("method")).append("|").append("GC").append("|");
                if (row.get("visit_reason") != null)
                    line.append(row.get("visit_reason"));
                line.append("|")
                    .append(row.get("risk_new"))
                    .append("|")
                    .append(row.get("risk_multiple"))
                    .append("|")
                    .append(row.get("risk_contact"))
                    .append("|")
                    .append(row.get("risk_msm"))
                    .append("|")
                    .append(row.get("risk_none"))
                    .append("|");
                if (row.get("symptom") != null)
                    line.append(row.get("symptom"));
                line.append("|")
                    .append(row.get("sign_cervical"))
                    .append("|")
                    .append(row.get("sign_cervicitis"))
                    .append("|")
                    .append(row.get("sign_pid"))
                    .append("|")
                    .append(row.get("sign_urethritis"))
                    .append("|")
                    .append(row.get("sign_none"))
                    .append("|")
                    .append(row.get("sign_no_exam"))
                    .append("|");
                if (row.get("insurance_type") != null)
                    line.append(row.get("insurance_type"));
                line.append("|")
                    .append(row.get("o_id"))
                    .append("|")
                    .append(row.get("zip_code"))
                    .append("|")
                    .append(row.get("org_type"))
                    .append("|")
                    .append("16D0648109");
                writer.println(line.toString());
                line.setLength(0);
            }

            /*
             * Write the summary line
             */
            writer.println("# of Records = " + (rows.size() * 2));
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                log.severe("Could not close print writer for chlGCToCDC export");
            }

            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                log.severe("Could not close output stream for chlGCToCDC export");
            }
        }

        return;
    }

    private String getStateFIPS(String state) {
        String stateFIPS;

        switch (state) {
            case "IA":
                stateFIPS = "19";
                break;

            case "IL":
                stateFIPS = "17";
                break;

            case "MN":
                stateFIPS = "27";
                break;

            case "MO":
                stateFIPS = "29";
                break;

            case "NE":
                stateFIPS = "31";
                break;

            case "SD":
                stateFIPS = "46";
                break;

            case "WI":
                stateFIPS = "55";
                break;

            default:
                stateFIPS = "";
        }

        return stateFIPS;
    }

    private String getSourceFromCode(String code) {
        String source;

        switch (code) {
            case "CX":
                source = "Cervix";
                break;

            case "RS":
                source = "Rectal";
                break;

            case "TS":
                source = "Throat";
                break;

            case "UR":
                source = "Urethra";
                break;

            case "UN":
                source = "Urine";
                break;

            case "VG":
                source = "Vaginal";
                break;

            case "OT":
                source = "Other";
                break;

            default:
                source = "";
        }

        return source;
    }
}