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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.MCLViolationReportVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SectionParameterDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.MCLViolationReportLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SectionParameterLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.manager.AuxDataManager;
import org.openelis.remote.MCLViolationReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")

public class MCLViolationReportBean implements MCLViolationReportLocal, MCLViolationReportRemote {
    @EJB
    private SessionCacheLocal       session;
    @EJB
    private AnalysisLocal           analysisBean;
    @EJB
    private AnalysisQAEventLocal    analysisQAEventBean;
    @EJB
    private AnalyteLocal            analyteBean;
    @EJB
    DictionaryCacheLocal            dictionaryCache;
    @EJB
    private ResultLocal             resultBean;
    @EJB
    private SampleQAEventLocal      sampleQAEventBean;
    @EJB
    private SectionParameterLocal   sectParamBean;
    @EJB
    private SystemVariableLocal     sysVarBean;

    private static final Logger     log  = Logger.getLogger(MCLViolationReportBean.class);

    private HashMap<String, String> contaminantIds, methodCodes;
    private Integer                 ugPerLId, ngPerLId, ngPerMlId, sectParamTypeId;
    private String                  dnrEmail;

    @PostConstruct
    public void init() {

        initMethodCodes();
        initContaminantIds();
        try {
            ugPerLId = dictionaryCache.getBySystemName("micrograms_per_liter").getId();
            ngPerLId = dictionaryCache.getBySystemName("nanograms_per_liter").getId();
            ngPerMlId = dictionaryCache.getBySystemName("nanograms_per_milliliter").getId();
            sectParamTypeId = dictionaryCache.getBySystemName("section_mcl_violation_email").getId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;
        try {
            p = new ArrayList<Prompt>();
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and email its output to specified addresses
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
     * Execute the report and email its output to specified addresses
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        double resultValue, mclValue;
        int i, j, k;
        AnalyteViewDO analyte;
        ArrayList<AnalysisQaEventDO> anaQAList;
        ArrayList<MCLViolationReportVO> analysisList;
        ArrayList<ResultViewDO> resultRow;
        ArrayList<SampleQaEventDO> samQAList;
        ArrayList<SectionParameterDO> emailList;
        ArrayList<ArrayList<ResultViewDO>> results;
        AuxDataManager adMan;
        Calendar cal;
        Date lastRunDate, currentRunDate, now;
        MCLViolationReportVO analysis;
        ReportStatus status;
        ResultViewDO rowResult, colResult;
        SimpleDateFormat format;
        String resultSign, resultString, toEmail;
        StringBuilder shlBody, dnrBody;
        SystemVariableDO lastRun;

        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        status = new ReportStatus();
        session.setAttribute("MCLViolationReport", status);

        cal = Calendar.getInstance();
        now = cal.getTime();
        /*
         * subtracting 1 minute from the current time for end date
         */
        cal.add(Calendar.MINUTE, -1);
        currentRunDate = cal.getTime();

        lastRun = null;
        toEmail = "";
        try {
            dnrEmail = sysVarBean.fetchByName("mcl_violation_email").getValue();
            lastRun = sysVarBean.fetchForUpdateByName("last_mcl_violation_report_run");
            lastRunDate = format.parse(lastRun.getValue());

            if (lastRunDate.compareTo(currentRunDate) > 0)
                throw new Exception("Start Date should be earlier than End Date");

            try {
                analysisList = analysisBean.fetchForMCLViolationReport(lastRunDate, currentRunDate);
            } catch (NotFoundException nfE) {
                analysisList = new ArrayList<MCLViolationReportVO>();
            }
            
            shlBody = new StringBuilder();
            dnrBody = new StringBuilder();
            analysis = new MCLViolationReportVO();
            for (i = 0; i < analysisList.size(); i++ ) {
                analysis = analysisList.get(i);
                
                // exclude analyses with result override qaevents
                try {
                    anaQAList = analysisQAEventBean.fetchResultOverrideByAnalysisId(analysis.getAnalysisId());
                    if (anaQAList.size() > 0)
                        continue;
                } catch (NotFoundException nfE) {
                    // ignore not found
                }
                
                // exclude analyses on samples with result override qaevents
                try {
                    samQAList = sampleQAEventBean.fetchResultOverrideBySampleId(analysis.getSampleId());
                    if (samQAList.size() > 0)
                        continue;
                } catch (NotFoundException nfE) {
                    // ignore not found
                }
                
                toEmail = "";
                try {
                    emailList = sectParamBean.fetchBySectionIdAndTypeId(analysis.getSectionId(), sectParamTypeId);
                    for (j = 0; j < emailList.size(); j++) {
                        if (toEmail.length() > 0)
                            toEmail += ",";
                        toEmail += emailList.get(j).getValue().trim();
                    }
                } catch (NotFoundException nfE) {
                    log.warn("No MCL Violation Email Address(es) for Section ("+analysis.getSectionId()+").");
                    continue;
                }
                
                adMan = AuxDataManager.fetchById(analysis.getSampleId(), ReferenceTable.SAMPLE);
                
                results = new ArrayList<ArrayList<ResultViewDO>>();
                try {
                    resultBean.fetchByAnalysisIdForDisplay(analysis.getAnalysisId(), results);
                } catch (NotFoundException nfE) {
                    log.error("Analysis ("+analysis.getAnalysisId()+") is missing results.");
                    continue;
                }
                for (j = 0; j < results.size(); j++) {
                    resultRow = results.get(j);
                    rowResult = resultRow.get(0);
                    if (!DataBaseUtil.isEmpty(rowResult.getValue())) {
                        for (k = 1; k < resultRow.size(); k++) {
                            colResult = resultRow.get(k);
                            analyte = analyteBean.fetchById(colResult.getAnalyteId());
                            if ("mcl".equals(analyte.getExternalId()) && !DataBaseUtil.isEmpty(colResult.getValue())) {
                                resultSign = null;
                                resultString = rowResult.getValue();
                                if (resultString.startsWith("<") || resultString.startsWith(">")) {
                                    resultSign = resultString.substring(0, 1);
                                    resultString = resultString.substring(1);
                                }
                                
                                try {
                                    resultValue = Double.parseDouble(resultString);
                                    mclValue = Double.parseDouble(colResult.getValue());
                                    if (resultValue > mclValue ||
                                        (resultValue == mclValue && (resultSign == null ||
                                                                     !"<".equals(resultSign)))) {
                                        if (shlBody.length() <= 0)
                                            printHeader(shlBody, analysis);
                                        printResultRow(shlBody, analysis, rowResult, colResult);
                                        printDNR(dnrBody, analysis, adMan, rowResult);
                                    }
                                } catch (NumberFormatException numE) {
                                    log.error("Value is not parseable as a number", numE);
                                }
                                break;
                            }
                        }
                    }
                }
                
                if (shlBody.length() > 0) {
                    printFooter(shlBody);
                    sendEmail(toEmail, "OpenELIS MCL Violation", shlBody.toString());
                    sendEmail(dnrEmail, "Chemical Exceedance Report for "+analysis.getFieldOffice(), dnrBody.toString());
                    log.info("MCL Violation email sent for Accession #"+analysis.getAccessionNumber());
                    shlBody.setLength(0);
                    dnrBody.setLength(0);
                }
            }
            
            lastRun.setValue(format.format(now));
            sysVarBean.updateAsSystem(lastRun);
        } catch (Exception e) {
            if (lastRun != null)
                sysVarBean.abortUpdate(lastRun.getId());
            log.error("Failed to run MCL Violation Report ", e);
            throw e;
        }
        
        return status;
    }

    protected void printHeader(StringBuilder body, MCLViolationReportVO analysis) {
        body.append("\r\n")
            .append("This is an automatic notification for MCL violation. NO FURTHER ACTION ON YOUR PART IS REQUIRED.<br>\r\n")
            .append("The following analyte(s) exceed the MCL specified by the test and IDNR has been notified.<br>\r\n")
            .append("<br>\r\n")
            .append("Accession Number: ").append("OE").append(analysis.getAccessionNumber()).append("<br>\r\n")
            .append("PWS ID: ").append(analysis.getPwsId()).append("<br>\r\n")
            .append("Client: ").append(analysis.getOrganizationName()).append("<br>\r\n")
            .append("Test: ").append(analysis.getTestName()).append("<br>\r\n")
            .append("Method: ").append(analysis.getMethodName()).append("<br>\r\n")
            .append("Unit: ").append(analysis.getUnitDescription()).append("<br>\r\n")
            .append("Start of Analysis: ").append(ReportUtil.toString(analysis.getAnaStartedDate(), "yyyy-MM-dd HH:mm")).append("<br>\r\n")
            .append("Analysis Released: ").append(ReportUtil.toString(analysis.getAnaReleasedDate(), "yyyy-MM-dd HH:mm")).append("<br>\r\n")
            .append("<br>\r\n")
            .append("<table border='1' cellpadding='2' cellspacing='0'>\r\n")
            .append("    <tr><td>Analyte</td>")
            .append("<td>Result</td>")
            .append("<td>Result in mg/L*</td>")
            .append("<td>MCL</td></tr>\r\n");
    }

    protected void printResultRow(StringBuilder body, MCLViolationReportVO analysis, ResultViewDO rowResult, ResultViewDO mclResult) {
        body.append("    <tr><td>").append(rowResult.getAnalyte()).append("</td>")
            .append("<td>").append(rowResult.getValue()).append("</td>")
            .append("<td>").append(getAdjustedResult(rowResult, analysis)).append("</td>")
            .append("<td>").append(mclResult.getValue()).append("</td></tr>\r\n");
    }

    protected void printFooter(StringBuilder body) {
        body.append("</table>\r\n")
              .append("<br>\r\n")
              .append("* Result values are changed to mg/L if required.<br>\r\n")
              .append("<br>\r\n");
    }
    
    protected void printDNR(StringBuilder body, MCLViolationReportVO analysis, AuxDataManager adMan, ResultViewDO rowResult) {
        int    i;
        AuxDataViewDO  auxDataVDO;
        String pbSampleType, origSampleNumber;
        
        pbSampleType = "";
        origSampleNumber = "";
        for (i = 0; i < adMan.count(); i++) {
            auxDataVDO = adMan.getAuxDataAt(i);
            if ("pb_type".equals(auxDataVDO.getAnalyteExternalId())) {
                try {
                    pbSampleType = dictionaryCache.getById(Integer.valueOf(auxDataVDO.getValue())).getCode();
                } catch (Exception anyE) {
                    anyE.printStackTrace();
                    pbSampleType = "Error Retrieving Sample Type";
                }
            } else if ("orig_sample_number".equals(auxDataVDO.getAnalyteExternalId())) {
                origSampleNumber = auxDataVDO.getValue();
            }
        }
        
        body.append("\r\n")
            .append("PWSID ").append(analysis.getPwsId()).append("<br>\r\n")
            .append("PWSID Name ").append(analysis.getPwsName()).append("<br>\r\n")
            .append("Lab ID ");
        
        if (analysis.getSectionName().endsWith("-ank"))
            body.append("397");
        else if (analysis.getSectionName().endsWith("-ic"))
            body.append("27");
        else if (analysis.getSectionName().endsWith("-lk"))
            body.append("393");

        body.append("<br>\r\n")
            .append("Facility ID ");
        
        if (analysis.getFacilityId() != null)
            body.append(analysis.getFacilityId());
        
        body.append("<br>\r\n")
              .append("Sample Point ID ").append(analysis.getSamplePointId()).append("<br>\r\n")
              .append("Sample Point Description ");
        
        if (analysis.getLocation() != null)
            body.append(analysis.getLocation());
        
        body.append("<br>\r\n")
              .append("Sample Type ").append(analysis.getSampleType().substring(0, 2)).append("<br>\r\n")
              .append("PB Sample Type ").append(pbSampleType).append("<br>\r\n")
              .append("Original Sample # ").append(origSampleNumber).append("<br>\r\n")
              .append("Sample Collection Date ").append(ReportUtil.toString(analysis.getCollectionDate(), "yyyy-MM-dd"));
        
        if (analysis.getCollectionTime() != null)
            body.append(" ").append(ReportUtil.toString(analysis.getCollectionTime(), "HH:mm"));

        body.append("<br>\r\n")
              .append("Lab Sample # ").append("OE").append(analysis.getAccessionNumber()).append("<br>\r\n")
              .append("Contaminant ID ").append(contaminantIds.get(rowResult.getAnalyte())).append("<br>\r\n")
              .append("Contaminant Name ").append(rowResult.getAnalyte()).append("<br>\r\n")
              .append("Method Code ").append(methodCodes.get(analysis.getMethodName())).append("<br>\r\n")
              .append("Start of Analysis ").append(ReportUtil.toString(analysis.getAnaStartedDate(), "yyyy-MM-dd HH:mm")).append("<br>\r\n")
              .append("Analysis Released ").append(ReportUtil.toString(analysis.getAnaReleasedDate(), "yyyy-MM-dd HH:mm")).append("<br>\r\n")
              .append("Result ").append(getAdjustedResult(rowResult, analysis)).append("<br>\r\n")
              .append("<br>\r\n");
    }

    protected void sendEmail(String toEmail, String subject, String body) {
        try {
            ReportUtil.sendEmail("do-not-reply@shl.uiowa.edu", toEmail, subject, body);
        } catch (Exception anyE) {
            anyE.printStackTrace();
        }
    }
    
    protected String getAdjustedResult(ResultViewDO result, MCLViolationReportVO analysis) {
        BigDecimal bdValue;
        String compOp, strValue;

        compOp = null; 
        strValue = result.getValue();
        if (strValue.startsWith("<") || strValue.startsWith(">")) {
            compOp = strValue.substring(0, 1);
            strValue = strValue.substring(1);
        }
        
        bdValue = new BigDecimal(strValue);
        if (ugPerLId.equals(analysis.getUnitOfMeasureId()) || ngPerMlId.equals(analysis.getUnitOfMeasureId())) {
            bdValue = bdValue.divide(new BigDecimal(1000.0));
        } else if (ngPerLId.equals(analysis.getUnitOfMeasureId())) {
            bdValue = bdValue.divide(new BigDecimal(1000000.0));
        }
        
        strValue = bdValue.toString();
        if (compOp != null)
            strValue = compOp + strValue;
        
        return strValue;
    }

    private void initMethodCodes() {
        methodCodes = new HashMap<String, String>();
        
        methodCodes.put("blue pour plate",       "9215B");
        methodCodes.put("colilert mpn sdwa am",  "9223B-QT");
        methodCodes.put("colilert mpn sdwa pm",  "9223B-18QT");
        methodCodes.put("colilert pa sdwa am",   "9223B-PA");
        methodCodes.put("colilert pa sdwa pm",   "9223B-18PA");
        methodCodes.put("epa 00-02/200.8",       "00-02/200.8");
        methodCodes.put("epa 00-02 including",   "00-02");
        methodCodes.put("epa 200.7 drink",       "200.7");
        methodCodes.put("epa 200.8 drink",       "200.8");
        methodCodes.put("epa 200.8 sdwa pb&cu",  "200.8");
        methodCodes.put("epa 200.8 uranium dw",  "200.8");
        methodCodes.put("epa 200.8 mercury dw",  "200.8");
        methodCodes.put("epa 245.2 mercury dw",  "245.2");
        methodCodes.put("epa 300.0",             "300.0");
        methodCodes.put("epa 300.1",             "300.1");
        methodCodes.put("epa 314.0",             "314.0");
        methodCodes.put("epa 350.1",             "350.1");
        methodCodes.put("epa 353.2 as n",        "353.2");
        methodCodes.put("epa 365.1",             "365.1");
        methodCodes.put("epa 508 chi",           "508");
        methodCodes.put("epa 508a",              "508A");
        methodCodes.put("epa 515.3 acid herbs",  "515.3");
        methodCodes.put("epa 524.2",             "524.2");
        methodCodes.put("epa 524.2 thm",         "524.2");
        methodCodes.put("epa 525.2",             "525.2");
        methodCodes.put("epa 531.1",             "531.1");
        methodCodes.put("epa 547",               "547");
        methodCodes.put("epa 548.1",             "548.1");
        methodCodes.put("epa 549.2",             "549.2");
        methodCodes.put("epa 550",               "550");
        methodCodes.put("epa 551.1 edb, dbcp",   "551.1");
        methodCodes.put("epa 552.2 haa",         "552.2");
        methodCodes.put("epa 900.0/200.8",       "900.0/200.8");
        methodCodes.put("epa 900.0 including",   "900.0");
        methodCodes.put("epa 901.1",             "901.1");
        methodCodes.put("epa 903.0",             "903.0");
        methodCodes.put("epa 903.0/904.0",       "903.0/904.0");
        methodCodes.put("epa 904.0",             "904.0");
        methodCodes.put("epa 905.0",             "905.0");
        methodCodes.put("epa 906.0",             "906.0");
        methodCodes.put("lac 10-107-06-1j",      "10-107-06-1J");
        methodCodes.put("sm 2130 b",             "2130B");
        methodCodes.put("sm 2320 b",             "2320B");
        methodCodes.put("sm 2340 b,total hard",  "2340B");
        methodCodes.put("sm 2340 c,total hard",  "2340C");
        methodCodes.put("sm 2510 b",             "2510B");
        methodCodes.put("sm 2540 c dissolved",   "2540C");
        methodCodes.put("sm 4500 cn e",          "4500CN-E");
        methodCodes.put("sm 4500 f c",           "4500F-C");
        methodCodes.put("sm 4500 h+ b",          "4500H-B");
        methodCodes.put("sm 4500 h+ b ph&alk",   "4500H-B");
        methodCodes.put("sm 4500 no2 b",         "4500NO2-B");
        methodCodes.put("sm 4500 no3 d",         "4500NO3-D");
        methodCodes.put("sm 4500 p e",           "4500P-E");
        methodCodes.put("sm 4500 si d",          "4500SI-D");
        methodCodes.put("sm 5310 b",             "5310B");
        methodCodes.put("sm 5910",               "5910B");
        methodCodes.put("sm 7500 i c 19th",      "7500-IC");
    }
    
    private void initContaminantIds() {
        contaminantIds = new HashMap<String, String>();
        
        contaminantIds.put("1,1,1,2-Tetrachloroethane",             "2986");
        contaminantIds.put("1,1,1-Trichloroethane",                 "2981");
        contaminantIds.put("1,1,2,2-Tetrachloroethane",             "2988");
        contaminantIds.put("1,1,2-Trichloroethane",                 "2985");
        contaminantIds.put("1,1-Dichloroethane",                    "2978");
        contaminantIds.put("1,1-Dichloroethene",                    "2977");
        contaminantIds.put("1,1-Dichloropropene",                   "2410");
        contaminantIds.put("1,2,3-Trichlorobenzene",                "2420");
        contaminantIds.put("1,2,3-Trichloropropane",                "2414");
        contaminantIds.put("1,2,4-Trichlorobenzene",                "2378");   
        contaminantIds.put("1,2,4-Trimethylbenzene",                "2418");
        contaminantIds.put("1,2-Dibromo-3-chloropropane",           "2931");
        contaminantIds.put("1,2-Dibromoethane",                     "2946");
        contaminantIds.put("1,2-Dichlorobenzene",                   "2968");
        contaminantIds.put("1,2-Dichloroethane",                    "2980"); 
        contaminantIds.put("1,2-Dichloropropane",                   "2983"); 
        contaminantIds.put("1,3,5-Trimethylbenzene",                "2424");
        contaminantIds.put("1,3-Dichlorobenzene",                   "2967");
        contaminantIds.put("1,3-Dichloropropane",                   "2412");
        contaminantIds.put("1,4-Dichlorobenzene",                   "2969");
        contaminantIds.put("2,2-Dichloropropane",                   "2416"); 
        contaminantIds.put("2,4,5-T",                               "2111");
        contaminantIds.put("2,4-D",                                 "2105");
        contaminantIds.put("2,4-Dinitrotoluene",                    "2270");
        contaminantIds.put("2,6-Dinitrotoluene",                    "2266");
        contaminantIds.put("3-Hydroxycarbofuran",                   "2066");
        contaminantIds.put("4,4'-DDE",                              "2009");
        contaminantIds.put("Acenaphthene",                          "2261");
        contaminantIds.put("Acenaphthylene",                        "2260");
        contaminantIds.put("Acetochlor",                            "2027");
        contaminantIds.put("Alachlor",                              "2051");
        contaminantIds.put("Aldicarb",                              "2047");
        contaminantIds.put("Aldicarb sulfone",                      "2044");
        contaminantIds.put("Aldicarb sulfoxide",                    "2043");
        contaminantIds.put("Aldrin",                                "2356");
        contaminantIds.put("Aluminum",                              "1002");
        contaminantIds.put("Ammonia nitrogen as N",                 "1003");
        contaminantIds.put("AMPA",                                  "2097");
        contaminantIds.put("Anthracene",                            "2280");
        contaminantIds.put("Antimony",                              "1074");
        contaminantIds.put("Aroclor 1016",                          "2388");
        contaminantIds.put("Aroclor 1221",                          "2390");
        contaminantIds.put("Aroclor 1232",                          "2392");
        contaminantIds.put("Aroclor 1242",                          "2394");
        contaminantIds.put("Aroclor 1248",                          "2396");
        contaminantIds.put("Aroclor 1254",                          "2398");
        contaminantIds.put("Aroclor 1260",                          "2400");
        contaminantIds.put("Arsenic",                               "1005");
        contaminantIds.put("Atrazine",                              "2050");
        contaminantIds.put("Barium",                                "1010");
        contaminantIds.put("Bentazon",                              "2625");
        contaminantIds.put("Benzene",                               "2990");
        contaminantIds.put("Benzo(a)anthracene",                    "2300");
        contaminantIds.put("Benzo(a)pyrene",                        "2306");
        contaminantIds.put("Benzo(b)fluoranthene",                  "2302");
        contaminantIds.put("Benzo(g,h,i)perylene",                  "2312");
        contaminantIds.put("Benzo(k)fluoranthene",                  "2304");
        contaminantIds.put("Beryllium",                             "1075");
        contaminantIds.put("Bicarbonate alkalinity",                "1026");
        contaminantIds.put("bis(2-Ethylhexyl)adipate",              "2035");
        contaminantIds.put("bis(2-Ethylhexyl)phthalate",            "2039");
        contaminantIds.put("Bromacil",                              "2098");
        contaminantIds.put("Bromate",                               "1011");
        contaminantIds.put("Bromide",                               "1004");
        contaminantIds.put("Bromoacetic acid",                      "2453");
        contaminantIds.put("Bromobenzene",                          "2993");
        contaminantIds.put("Bromochloromethane",                    "2430");
        contaminantIds.put("Bromodichloromethane",                  "2943");
        contaminantIds.put("Bromoform",                             "2942");
        contaminantIds.put("Bromomethane",                          "2214");
        contaminantIds.put("Butachlor",                             "2076");   
        contaminantIds.put("Butylate",                              "2053");    
        contaminantIds.put("Cadmium",                               "1015");     
        contaminantIds.put("Calcium",                               "1919");  
        contaminantIds.put("Carbaryl",                              "2021");
        contaminantIds.put("Carbofuran",                            "2046");
        contaminantIds.put("Carbon tetrachloride",                  "2982");  
        contaminantIds.put("Cesium-134",                            "4270");
        contaminantIds.put("Chloramben",                            "2620");
        contaminantIds.put("Chlordane",                             "2959"); 
        contaminantIds.put("Chloride",                              "1017");  
        contaminantIds.put("Chlorite",                              "1009");  
        contaminantIds.put("Chloroacetic acid",                     "2450");
        contaminantIds.put("Chlorobenzene",                         "2989");
        contaminantIds.put("Chloroethane",                          "2216"); 
        contaminantIds.put("Chloroform",                            "2941");   
        contaminantIds.put("Chloromethane",                         "2210");
        contaminantIds.put("Chlorpyrifos",                          "2057"); 
        contaminantIds.put("Chlorthal-dimethyl",                    "2099");
        contaminantIds.put("Chromium",                              "1020");
        contaminantIds.put("Chrysene",                              "2296");
        contaminantIds.put("cis-1,2-Dichloroethene",                "2380");
        contaminantIds.put("cis-1,2-Dichloroethylene",              "2380");  
        contaminantIds.put("cis-1,3-Dichloropropene",               "2228");
        contaminantIds.put("Combined Radiums",                      "4010");
        contaminantIds.put("Copper",                                "1022");
        contaminantIds.put("Cyanazine",                             "2054");
        contaminantIds.put("Cyanide",                               "1024");  
        contaminantIds.put("Dalapon",                               "2031");   
        contaminantIds.put("Desethyl atrazine",                     "2006");
        contaminantIds.put("Desisopropyl atrazine",                 "2007");
        contaminantIds.put("Diazinon",                              "2056");
        contaminantIds.put("Dibenzo(a,h)anthracene",                "2310");
        contaminantIds.put("Dibromoacetic acid",                    "2454");
        contaminantIds.put("Dibromochloromethane",                  "2944");
        contaminantIds.put("Dibromomethane",                        "2408");
        contaminantIds.put("Dicamba",                               "2440");
        contaminantIds.put("Dichloroacetic acid",                   "2451");
        contaminantIds.put("Dichlorodifluoromethane",               "2212");
        contaminantIds.put("Dichlorprop",                           "2206");
        contaminantIds.put("Dieldrin",                              "2070");   
        contaminantIds.put("Di-n-butyl phthalate",                  "2290");
        contaminantIds.put("Dinoseb",                               "2041");
        contaminantIds.put("Diquat",                                "2032");
        contaminantIds.put("Dissolved Organic Carbon",              "2919");
        contaminantIds.put("Disulfoton",                            "2102");
        contaminantIds.put("E.coli",                                "3014");
        contaminantIds.put("Endothall",                             "2033"); 
        contaminantIds.put("Endrin",                                "2005");    
        contaminantIds.put("EPTC",                                  "2052");      
        contaminantIds.put("Ethylbenzene",                          "2992");
        contaminantIds.put("Ethylene dibromide",                    "2946");
        contaminantIds.put("Fecal Coliform",                        "3013");
        contaminantIds.put("Fluoranthene",                          "2286");
        contaminantIds.put("Fluorene",                              "2264");
        contaminantIds.put("Fluoride",                              "1025");
        contaminantIds.put("Fonofos",                               "2104"); 
        contaminantIds.put("Glyphosate",                            "2034");
        contaminantIds.put("Gross Alpha excluding Uranium",         "4000");
        contaminantIds.put("Gross Alpha including Uranium",         "4002");
        contaminantIds.put("Gross Beta",                            "4100");
        contaminantIds.put("Heptachlor",                            "2065");
        contaminantIds.put("Heptachlor epoxide",                    "2067");
        contaminantIds.put("Heterotrophic Plate Count",             "3001");
        contaminantIds.put("Hexachlorobenzene",                     "2274"); 
        contaminantIds.put("Hexachlorobutadiene",                   "2246");
        contaminantIds.put("Hexachlorocyclopentadiene",             "2042");
        contaminantIds.put("Indeno(1,2,3-cd)pyrene",                "2308");   
        contaminantIds.put("Iodine-131",                            "4264");
        contaminantIds.put("Iron",                                  "1028");
        contaminantIds.put("Isopropylbenzene",                      "2994");
        contaminantIds.put("Laboratory pH",                         "1925");   
        contaminantIds.put("Lead",                                  "1030");
        contaminantIds.put("Lindane",                               "2010");
        contaminantIds.put("m & p-Xylenes",                         "2963");
        contaminantIds.put("Magnesium",                             "1031");
        contaminantIds.put("Manganese",                             "1032");
        contaminantIds.put("Mercury",                               "1035");  
        contaminantIds.put("Methomyl",                              "2022");
        contaminantIds.put("Methoxychlor",                          "2015"); 
        contaminantIds.put("Methylene chloride",                    "2964");  
        contaminantIds.put("Methyl-t-butyl ether (MtBE)",           "2251");
        contaminantIds.put("Metolachlor",                           "2045");
        contaminantIds.put("Metribuzin",                            "2595"); 
        contaminantIds.put("Molinate",                              "2626");   
        contaminantIds.put("Naphthalene",                           "2248");
        contaminantIds.put("n-Butylbenzene",                        "2422");
        contaminantIds.put("Nickel",                                "1036");
        contaminantIds.put("Nitrate nitrogen as N",                 "1040");
        contaminantIds.put("Nitrite nitrogen as N",                 "1041");
        contaminantIds.put("Nitrobenzene",                          "2254");
        contaminantIds.put("n-Propylbenzene",                       "2998");
        contaminantIds.put("ortho-Phosphate as P",                  "1044");
        contaminantIds.put("Oxamyl",                                "2036");
        contaminantIds.put("o-Dichlorobenzene",                     "2968");  
        contaminantIds.put("o-Xylene",                              "2997");
        contaminantIds.put("PCB Total as DCBP",                     "2383");
        contaminantIds.put("Pentachlorophenol",                     "2326");
        contaminantIds.put("Perchlorate",                           "1039");
        contaminantIds.put("Phenanthrene",                          "2278");
        contaminantIds.put("Phenolphthalein alkalinity",            "1931");
        contaminantIds.put("Picloram",                              "2040");
        contaminantIds.put("p-Dichlorobenzene",                     "2969");  
        contaminantIds.put("p-Isopropyltoluene",                    "2030");
        contaminantIds.put("Potassium",                             "1042");
        contaminantIds.put("Prometon",                              "2029"); 
        contaminantIds.put("Propachlor",                            "2077");
        contaminantIds.put("Pyrene",                                "2288");
        contaminantIds.put("Radium-226",                            "4020");
        contaminantIds.put("Radium-228",                            "4030");
        contaminantIds.put("sec-Butylbenzene",                      "2428");
        contaminantIds.put("Selenium",                              "1045");
        contaminantIds.put("Silica as SiO2",                        "1049");
        contaminantIds.put("Silver",                                "1050");
        contaminantIds.put("Silvex",                                "2110");
        contaminantIds.put("Simazine",                              "2037");  
        contaminantIds.put("Sodium",                                "1052");  
        contaminantIds.put("Specific Conductance",                  "1064");
        contaminantIds.put("Strontium-89",                          "4172");
        contaminantIds.put("Strontium-90",                          "4174");
        contaminantIds.put("Styrene",                               "2996");  
        contaminantIds.put("Sulfate",                               "1055");
        contaminantIds.put("Terbacil",                              "2272");
        contaminantIds.put("Terbufos",                              "2545");  
        contaminantIds.put("tert-Butylbenzene",                     "2426");
        contaminantIds.put("Tetrachloroethene",                     "2987"); 
        contaminantIds.put("Tetrachloroethylene",                   "2987");  
        contaminantIds.put("Thallium",                              "1085");
        contaminantIds.put("Toluene",                               "2991");
        contaminantIds.put("Total 1,3-Dichloropropene",             "2413");
        contaminantIds.put("Total Alkalinity",                      "1927");
        contaminantIds.put("Total Coliform Bacteria",               "3100");
        contaminantIds.put("Total Dissolved Solids",                "1930");
        contaminantIds.put("Total Haloacetic Acids (HAA5)",         "2456");
        contaminantIds.put("Total Hardness",                        "1915");  
        contaminantIds.put("Total Organic Carbon",                  "2920");
        contaminantIds.put("Total Trihalomethanes",                 "2950");
        contaminantIds.put("Total Xylenes",                         "2955");
        contaminantIds.put("Toxaphene",                             "2020");
        contaminantIds.put("trans-1,2-Dichloroethene",              "2979");
        contaminantIds.put("trans-1,2-Dichloroethylene",            "2979");  
        contaminantIds.put("trans-1,3-Dichloropropene",             "2224");
        contaminantIds.put("Trichloroacetic acid",                  "2452");
        contaminantIds.put("Trichloroethene",                       "2984");
        contaminantIds.put("Trichlorofluoromethane",                "2218");
        contaminantIds.put("Triclopyr",                             "2107");  
        contaminantIds.put("Trifluralin",                           "2055");
        contaminantIds.put("Tritium",                               "4102");    
        contaminantIds.put("Turbidity",                             "0100");   
        contaminantIds.put("Uranium",                               "4006");
        contaminantIds.put("UV Absorbance at 254 nm",               "2922");
        contaminantIds.put("Vinyl chloride",                        "2976");  
        contaminantIds.put("Zinc",                                  "1095");  
    }
}
