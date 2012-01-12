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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalyteLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.remote.SDWISUnloadReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.ReportUtil;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")

public class SDWISUnloadReportBean implements JRDataSource, SDWISUnloadReportRemote {

    @Resource
    private SessionContext  ctx;

    @EJB
    private SessionCacheLocal session;    

    @EJB
    private PrinterCacheLocal printers;
    
    @EJB
    AnalysisLocal        analysis;
    @EJB
    AnalyteLocal         analyte;
    @EJB
    AuxDataLocal         auxData;
    @EJB
    DictionaryCacheLocal dictionaryCache;
    @EJB
    ResultLocal          result;
    @EJB
    SampleLocal          sample;
    @EJB
    SampleQAEventLocal   sampleQA;
    @EJB
    SampleSDWISLocal     sampleSdwis;
    @EJB
    SectionCacheLocal    sectionCache;
    @EJB
    UserCacheLocal       userCache;

    private static Integer releasedStatusId, typeDictionaryId;
    private static HashMap<String, String> methodCodes, contaminantIds;
    
    private int                                statusIndex;
    private ArrayList<HashMap<String, Object>> statusList;
    
    @PostConstruct
    public void init() {
        initMethodCodes();
        initContaminantIds();
        try {
            releasedStatusId = dictionaryCache.getBySystemName("analysis_released").getId();
            typeDictionaryId = dictionaryCache.getBySystemName("test_res_type_dictionary").getId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<OptionListItem> loc, prn;
        ArrayList<Prompt>         p;
        Calendar                  fromDate, toDate;
        SimpleDateFormat          format;

        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        fromDate = Calendar.getInstance();
        fromDate.set(Calendar.HOUR_OF_DAY, 13);
        fromDate.set(Calendar.MINUTE, 00);
        if (fromDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            fromDate.add(Calendar.DAY_OF_MONTH, -3);
        else
            fromDate.add(Calendar.DAY_OF_MONTH, -1);
        
        toDate = Calendar.getInstance();
        toDate.set(Calendar.HOUR_OF_DAY, 12);
        toDate.set(Calendar.MINUTE, 59);
        
        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("BEGIN_RELEASED", Prompt.Type.DATETIME)
                    .setPrompt("Begin Released:")
                    .setWidth(130)
                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                    .setDefaultValue(format.format(fromDate.getTime()))
                    .setRequired(true));

            p.add(new Prompt("END_RELEASED", Prompt.Type.DATETIME)
                    .setPrompt("End Released:")
                    .setWidth(130)
                    .setDatetimeStartCode(Prompt.Datetime.YEAR)
                    .setDatetimeEndCode(Prompt.Datetime.MINUTE)
                    .setDefaultValue(format.format(toDate.getTime()))
                    .setRequired(true));

            loc = new ArrayList<OptionListItem>();
            loc.add(new OptionListItem("-ank", "Ankeny"));
            loc.add(new OptionListItem("-ic", "Iowa City"));
            loc.add(new OptionListItem("-lk", "Lakeside"));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(100)
                                                           .setOptionList(loc)
                                                           .setMutiSelect(false)
                                                           .setRequired(true));
            
            prn = printers.getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY).setPrompt("Printer:")
                                                          .setWidth(200)
                                                          .setOptionList(prn)
                                                          .setMutiSelect(false)
                                                          .setRequired(true));
            
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    @SuppressWarnings("unchecked")
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        boolean reject;
        int sampleCount;
        AnalysisViewDO aVDO;
        ArrayList<SampleDO> samples;
        ArrayList<AnalysisViewDO> analyses;
        Date beginReleased, endReleased;
        File tempFile, statFile;
        FileOutputStream out;
        HashMap<String, Object> jparam, statRow;
        HashMap<String, QueryData> param;
        Iterator<SampleDO> sIter;
        Iterator<AnalysisViewDO> aIter;
        JRExporter jexport;
        JasperPrint jprint;
        JasperReport jreport;
        PrintWriter writer;
        ReportStatus status;
        SampleDO sDO;
        SampleSDWISViewDO ssVDO;
        SectionViewDO secVDO;
        SimpleDateFormat format;
        String location, printer;
        URL url;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("SDWISUnloadReport", status);

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);
        
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        beginReleased = format.parse(ReportUtil.getSingleParameter(param, "BEGIN_RELEASED"));
        endReleased = format.parse(ReportUtil.getSingleParameter(param, "END_RELEASED"));
        location = ReportUtil.getSingleParameter(param, "LOCATION");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");
        
        try {
            tempFile = File.createTempFile("sdwisUnload", ".lrr", new File("/tmp"));
            statFile = File.createTempFile("sdwisUnloadStatus", ".pdf", new File("/tmp"));
            out = new FileOutputStream(tempFile);
        } catch (Exception anyE) {
            anyE.printStackTrace();
            throw new Exception("Could not open temp file for writing.");
        }
        
        writer = new PrintWriter(out);

        /*
         * start the report
         */
        try {
            status.setMessage("Initializing report");

            status.setMessage("Outputing report").setPercentComplete(10);

            writeHeaderRow(writer, location);

            sampleCount = 0;
            
            statusList = new ArrayList<HashMap<String, Object>>();
            statusIndex = -1;
            
            samples = sample.fetchSDWISByReleasedAndLocation(beginReleased, endReleased, location);
            sIter = samples.iterator();
            while (sIter.hasNext()) {
                sDO = sIter.next();
                ssVDO = sampleSdwis.fetchBySampleId(sDO.getId());
                
                if (sDO.getCollectionDate() == null) {                    
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: Sample has no collection date. Sample Skipped");
                } else if (sDO.getCollectionTime() == null) {
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: Sample has no collection time. Sample Skipped");
                } else if (ssVDO.getLocation() == null) {
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: Sample has no location. Sample Skipped");
                } else if (ssVDO.getFacilityId() == null) {
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: SDWIS Facility Id is blank");
                } else if (ssVDO.getSamplePointId() == null) {
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: SDWIS Sampling Point is blank");
                } else if (ssVDO.getSampleTypeId() == null) {
                    addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Error: SDWIS Sample Type is blank");
                } else {
                    if (sDO.getRevision() > 0) {
                        addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Warning: Sample has revision > 0. Please check for previous entry!");
                    }
                    
                    reject = writeSampleRow(writer, sDO, ssVDO, location);
                    if (!reject) {
                        analyses = analysis.fetchBySampleId(sDO.getId());
                        aIter = analyses.iterator();
                        while (aIter.hasNext()) {
                            aVDO = aIter.next();
                            if (releasedStatusId.equals(aVDO.getStatusId())) {
                                secVDO = sectionCache.getById(aVDO.getSectionId());
                                if (secVDO != null && secVDO.getName().endsWith(location))
                                    writeResultRows(writer, sDO, ssVDO, aVDO);
                            }
                        }
                        addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Generated");
                    } else {
                        addStatusRow(ssVDO.getPwsNumber0(), sDO.getAccessionNumber(), "Warning: Sample has a Result Override QAEvent!");
                    }

                    sampleCount++;
                }
                
                status.setPercentComplete((sampleCount / samples.size()) * 80 + 10);
            }
            
            writeTrailerRow(writer, sampleCount);
            
            writer.close();
            out.close();
            
            /*
             * Print the status report
             */
            jparam = new HashMap<String, Object>();
            jparam.put("LOGIN_NAME", EJBFactory.getUserCache().getName());
            jparam.put("BEGIN_RELEASED", format.format(beginReleased));
            jparam.put("END_RELEASED", format.format(endReleased));
            jparam.put("COUNT", sampleCount);

            url = ReportUtil.getResourceURL("org/openelis/report/sdwisunload/main.jasper");
            jreport = (JasperReport)JRLoader.loadObject(url);
            jprint = JasperFillManager.fillReport(jreport, jparam, this);
            jexport = new JRPdfExporter();
            jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(statFile));
            jexport.setParameter(JRExporterParameter.JASPER_PRINT, jprint);

            status.setPercentComplete(90);
            
            jexport.exportReport();

            status.setPercentComplete(100);

            ReportUtil.print(statFile, printer, 1);
            
            tempFile = ReportUtil.saveForUpload(tempFile);
            status.setMessage(tempFile.getName())
                  .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                  .setStatus(ReportStatus.Status.SAVED);
        } catch (Exception e) {
            e.printStackTrace();
            writer.close();
            try {
                out.close();
            } catch (IOException ioE) {
                ioE.printStackTrace();
                throw new Exception("Error finalizing output file.");
            }
            throw e;
        }
        
        return status;
    }
    
    protected void writeHeaderRow(PrintWriter writer, String location) {
        Datetime         today;
        SimpleDateFormat format;
        StringBuilder    row;
        
        today = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);
        format = new SimpleDateFormat("MM/dd/yyyy");
        
        row = new StringBuilder();
        row.append("#HDR")                              // col 1-4
           .append(" ")                                 // col 5
           .append("CREATED")                           // col 6-12
           .append(" ")                                 // col 13
           .append(format.format(today.getDate()))      // col 14-23
           .append(" ")                                 // col 24
           .append("LAB-ID")                            // col 25-30
           .append(" ")                                 // col 31
           .append("     ")                             // col 32-36
           .append(" ")                                 // col 37
           .append("AGENCY")                            // col 38-43
           .append(" ")                                 // col 44
           .append("IA")                                // col 45-46
           .append(" ")                                 // col 47
           .append("PURPOSE")                           // col 48-54
           .append(" ")                                 // col 55
           .append("NEW")                               // col 56-58
           .append(" ")                                 // col 59
           .append("TYPE")                              // col 60-63
           .append(" ")                                 // col 64
           .append("RT")                                // col 65-66
           .append(" ")                                 // col 67
           .append("REFERENCE")                         // col 68-76
           .append(" ")                                 // col 77
           .append("                              ");   // col 78-107 TODO: transaction reference number
        
        writer.println(row.toString());
    }

    protected boolean writeSampleRow(PrintWriter writer, SampleDO sVDO, SampleSDWISViewDO ssVDO, String location) throws Exception {
        ArrayList<AuxDataViewDO>   adList;
        ArrayList<Integer>         sampleIds;
        ArrayList<SampleQaEventDO> sampleQaList;
        AuxDataViewDO              adVDO;
        DictionaryDO               sampCatDO, sampTypeDO;
        Iterator<AuxDataViewDO>    adIter;
        SimpleDateFormat           dateFormat, dateSlashFormat, timeFormat;
        String                     compDateString, compIndicator, compLabNumber,
                                   compQuarter, freeChlorine, pbType, repeatCode,
                                   totalChlorine, origSampleNumber, sampleOverride;
        StringBuilder              row;
        
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateSlashFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HHmm");
        
        pbType = "";
        repeatCode = "";
        freeChlorine = "";
        totalChlorine = "";
        compIndicator = "";
        compLabNumber = "";
        compDateString = "";
        compQuarter = "";
        origSampleNumber = "";
        sampleOverride = "";
        
        try {
            sampCatDO = dictionaryCache.getById(ssVDO.getSampleCategoryId());
            sampTypeDO = dictionaryCache.getById(ssVDO.getSampleTypeId());
        } catch (Exception anyE) {
            throw new Exception("Error looking up dictionary entry for Sample Category or Sample Type; "+anyE.getMessage());
        }
        
        try {
            sampleIds = new ArrayList<Integer>();
            sampleIds.add(sVDO.getId());
            sampleQaList = sampleQA.fetchResultOverrideBySampleIds(sampleIds);
            if (sampleQaList.size() > 0)
                sampleOverride = "S";
        } catch (NotFoundException nfE) {
            // no qa events found means sample is not overridden
        } catch (Exception anyE) {
            throw new Exception("Error looking up result override Sample QAEvents; "+anyE.getMessage());
        }
        
        try {
            adList = auxData.fetchById(sVDO.getId(), ReferenceTable.SAMPLE);
        } catch (Exception anyE) {
            throw new Exception("Error retrieving auxillary data for accession #"+sVDO.getAccessionNumber()+"; "+anyE.getMessage());
        }
        adIter = adList.iterator();
        while (adIter.hasNext()) {
            adVDO = adIter.next();
            if ("pb_type".equals(adVDO.getAnalyteExternalId())) {
                if (adVDO.getValue() != null && adVDO.getValue().length() > 0) {
                    try {
                        pbType = dictionaryCache.getById(Integer.valueOf(adVDO.getValue())).getLocalAbbrev();
                    } catch (Exception anyE) {
                        throw new Exception("Error looking up dictionary entry for Pb Sample Type; "+anyE.getMessage());
                    }
                }
            } else if ("repeat_code".equals(adVDO.getAnalyteExternalId())) {
                if (adVDO.getValue() != null && adVDO.getValue().length() > 0) {
                    try {
                        repeatCode = dictionaryCache.getById(Integer.valueOf(adVDO.getValue())).getLocalAbbrev();
                    } catch (Exception anyE) {
                        throw new Exception("Error looking up dictionary entry for Repeat Code; "+anyE.getMessage());
                    }
                }
            } else if ("free_chlorine".equals(adVDO.getAnalyteExternalId())) {
                freeChlorine = adVDO.getValue();
            } else if ("total_chlorine".equals(adVDO.getAnalyteExternalId())) {
                totalChlorine = adVDO.getValue();
            } else if ("composite_indicator".equals(adVDO.getAnalyteExternalId())) {
                if (adVDO.getValue() != null && adVDO.getValue().length() > 0) {
                    try {
                        compIndicator = dictionaryCache.getById(Integer.valueOf(adVDO.getValue())).getLocalAbbrev();
                    } catch (Exception anyE) {
                        throw new Exception("Error looking up dictionary entry for Composite Indicator; "+anyE.getMessage());
                    }
                }
            } else if ("composite_lab_no".equals(adVDO.getAnalyteExternalId())) {
                compLabNumber = adVDO.getValue();
            } else if ("composite_date".equals(adVDO.getAnalyteExternalId())) {
                compDateString = adVDO.getValue();
            } else if ("composite_qtr".equals(adVDO.getAnalyteExternalId())) {
                compQuarter = adVDO.getValue();
            } else if ("orig_sample_number".equals(adVDO.getAnalyteExternalId())) {
                origSampleNumber = adVDO.getValue();
            }
        }
        
        row = new StringBuilder();
        row.append("#SAM")                                                          // col 1-4
           .append(sampCatDO.getLocalAbbrev())                                      // col 5-6
           .append(sampTypeDO.getLocalAbbrev())                                     // col 7-8
           .append(getPaddedString(pbType, 3))                                      // col 9-11
           .append(getPaddedString(ssVDO.getPwsNumber0(), 9))                       // col 12-20
           .append(getPaddedString(ssVDO.getFacilityId(), 12))                      // col 21-32
           .append(getPaddedString(ssVDO.getSamplePointId(), 11))                   // col 33-43
           .append(getPaddedString(ssVDO.getLocation(), 20))                        // col 44-63
           .append(dateFormat.format(sVDO.getCollectionDate().getDate()))           // col 64-71
           .append(timeFormat.format(sVDO.getCollectionTime().getDate()))           // col 72-75
           .append(getPaddedString(ssVDO.getCollector(), 20))                       // col 76-95
           .append(dateFormat.format(sVDO.getReceivedDate().getDate()))             // col 96-103
           .append(getPaddedString("OE"+sVDO.getAccessionNumber().toString(), 20)); // col 104-123
        
        if (origSampleNumber != null && origSampleNumber.length() > 0)              // col 124-143
            row.append(getPaddedString("OE"+origSampleNumber, 20));
        else
            row.append(getPaddedString("", 20));
        
        row.append(getPaddedString(repeatCode, 2))                                  // col 144-145
           .append(getPaddedString(freeChlorine, 5))                                // col 146-150
           .append(getPaddedString(totalChlorine, 5))                               // col 151-155
           .append(getPaddedString(compIndicator, 1))                               // col 156
           .append(getPaddedString(compLabNumber, 20));                             // col 157-176
        
        if (compDateString != null && compDateString.length() > 0) {                // col 177-184
            try {
                compDateString = dateFormat.format(dateSlashFormat.parse(compDateString));
                row.append(compDateString);
            } catch (ParseException parE) {
                throw new Exception("Invalid Composite Date; "+parE.getMessage());
            }
        } else {
            row.append(getPaddedString(compDateString, 8));
        }
        
        row.append(getPaddedString(compQuarter, 1));                                // col 185
        row.append(getPaddedString(sampleOverride, 1));                             // col 186
        
        if ("-ank".equals(location))                                                // col 187-191
            row.append("397  ");                                                    // Ankeny DNR ID
        else if ("-ic".equals(location))
            row.append("027  ");                                                    // Iowa City DNR ID
        else if ("-lk".equals(location))
            row.append("393  ");                                                    // Lakeside DNR ID
        else
            row.append("     ");

        writer.println(row.toString());
        
        return sampleOverride.length() > 0;
    }

    protected void writeResultRows(PrintWriter writer, SampleDO sample,
                                   SampleSDWISViewDO sampleSDWIS, AnalysisViewDO analysis) throws Exception {
        int i;
        AnalyteViewDO alVDO;
        ArrayList<ResultViewDO> resultRow;
        ArrayList<ArrayList<ResultViewDO>> results;
        ArrayList<HashMap<String,String>> resultData;
        DictionaryDO unitDO, sampCatDO;
        HashMap<String,String> rowData;
        Iterator<ArrayList<ResultViewDO>> rowIter;
        Iterator<ResultViewDO> colIter;
        ResultViewDO rVDO, crVDO;
        SimpleDateFormat dateFormat, timeFormat;
        String           methodCode;
        StringBuilder    row;
        
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        timeFormat = new SimpleDateFormat("HHmm");

        try {
            sampCatDO = dictionaryCache.getById(sampleSDWIS.getSampleCategoryId());
        } catch (Exception anyE) {
            throw new Exception("Error looking up dictionary entry for Sample Category; "+anyE.getMessage());
        }

        methodCode = methodCodes.get(analysis.getMethodName());
        //
        // if we don't have a method code for this analysis we should not be
        // sending it to SDWIS
        //
        if (methodCode == null) {
            addStatusRow(sampleSDWIS.getPwsNumber0(), sample.getAccessionNumber(),
                         "Warning: No method code for test '"+analysis.getTestName()+", "+analysis.getMethodName()+"'");
            return;
        }
        
        try {
            unitDO = dictionaryCache.getById(analysis.getUnitOfMeasureId());
        } catch (Exception anyE) {
            throw new Exception("Error looking up units from dictionary; "+anyE.getMessage());
        }
        
        resultData = new ArrayList<HashMap<String,String>>();
        results = new ArrayList<ArrayList<ResultViewDO>>();
        try {
            result.fetchByAnalysisIdForDisplay(analysis.getId(), results);
        } catch (Exception anyE) {
            throw new Exception("Error retrieving result records; "+anyE.getMessage());
        }
        rowIter = results.iterator();
        while (rowIter.hasNext()) {
            resultRow = rowIter.next();
            rVDO = resultRow.get(0);
            if (contaminantIds.get(rVDO.getAnalyte()) == null) {
                addStatusRow(sampleSDWIS.getPwsNumber0(), sample.getAccessionNumber(),
                             "Error: Compound id not found for '"+rVDO.getAnalyte()+"'");
                continue;
            }
            
            rowData = new HashMap<String,String>();
            rowData.put("contaminantId", contaminantIds.get(rVDO.getAnalyte()));
            if ("TC".equals(sampCatDO.getLocalAbbrev())) {
                if (typeDictionaryId.equals(rVDO.getTypeId())) {
                    try {
                        rowData.put("microbe", dictionaryCache.getById(Integer.valueOf(rVDO.getValue())).getEntry());
                    } catch (Exception anyE) {
                        throw new Exception("Error looking up dictionary result; "+anyE.getMessage());
                    }
                } else {
                    if (rVDO.getValue() != null) {
                        if (rVDO.getValue().startsWith("<"))
                            rowData.put("microbe", "A");
                        else
                            rowData.put("microbe", "P");

                        if (analysis.getMethodName().indexOf("mpn") != -1) {
                            colIter = resultRow.iterator();
                            while (colIter.hasNext()) {
                                crVDO = colIter.next();
                                if ("Well Count".equals(crVDO.getAnalyte())) {
                                    if (crVDO.getValue().indexOf(".") != -1)
                                        crVDO.setValue(crVDO.getValue().substring(0, crVDO.getValue().indexOf(".")));
                                    rowData.put("count", crVDO.getValue());
                                    break;
                                }
                            }
                        } else {
                            if (rVDO.getValue().startsWith(">"))
                                rVDO.setValue(rVDO.getValue().substring(1));
                            if (rVDO.getValue().indexOf(".") != -1)
                                rVDO.setValue(rVDO.getValue().substring(0, rVDO.getValue().indexOf(".")));
                            if (!rVDO.getValue().startsWith("<"))
                                rowData.put("count", rVDO.getValue());
                        }
                    }
                }
                if ("Heterotrophic Plate Count".equals(rVDO.getAnalyte())) {
                    rowData.put("countType", "CFU");
                    rowData.put("countUnits", "mL");
                } else {
                    rowData.put("countType", "Tubes");
                    rowData.put("countUnits", "100 mL");
                }
                resultData.add(rowData);
            } else {
                if (rVDO.getValue() != null && rVDO.getValue().startsWith("<"))
                    rowData.put("ltIndicator", "Y");
                else
                    rowData.put("ltIndicator", "N");
                rowData.put("concentration", rVDO.getValue());
                rowData.put("concentrationUnit", unitDO.getEntry());
                
                colIter = resultRow.iterator();
                while (colIter.hasNext()) {
                    crVDO = colIter.next();
                    try {
                        alVDO = analyte.fetchById(crVDO.getAnalyteId());
                    } catch (Exception anyE) {
                        throw new Exception("Error looking up result column analyte; "+anyE.getMessage());
                    }
                    if ("mcl".equals(alVDO.getExternalId())) {
                        rowData.put("detection", crVDO.getValue());
                        rowData.put("detectionUnit", unitDO.getEntry());
                    } else if ("rad_measure_error".equals(alVDO.getExternalId())) {
                        rowData.put("radMeasureError", crVDO.getValue());
                    }
                }
                
                resultData.add(rowData);
            }
        }
        
        for (i = 0; i < resultData.size(); i++) {
            rowData = resultData.get(i);
            
            row = new StringBuilder();
            row.append("#RES")                                                      // col 1-4
               .append(getPaddedString(rowData.get("contaminantId"), 4))            // col 5-8
               .append(getPaddedString(methodCode, 12));                            // col 9-20
            
            if (analysis.getStartedDate() != null)
                row.append(dateFormat.format(analysis.getStartedDate().getDate()))  // col 21-28
                   .append(timeFormat.format(analysis.getStartedDate().getDate())); // col 29-32
            else
                row.append(getPaddedString("", 8))                                  // col 21-28
                   .append(getPaddedString("", 4));                                 // col 29-32

            if (analysis.getCompletedDate() != null)                                // col 33-40
                row.append(dateFormat.format(analysis.getCompletedDate().getDate()));
            else
                row.append(getPaddedString("", 8));
            
            row.append(getPaddedString(rowData.get("microbe"), 1))                  // col 41
               .append(getPaddedNumber(rowData.get("count"), 5))                    // col 42-46
               .append(getPaddedString(rowData.get("countType"), 10))               // col 47-56
               .append(getPaddedString(rowData.get("countUnits"), 9))               // col 57-65
               .append(getPaddedString(rowData.get("ltIndicator"), 1))              // col 66
               .append("MRL")                                                       // col 67-69
               .append(getPaddedNumber(rowData.get("concentration"), 14))           // col 70-83
               .append(getPaddedString(rowData.get("concentrationUnit"), 9))        // col 84-92
               .append(getPaddedNumber(rowData.get("detection"), 16))               // col 93-108
               .append(getPaddedString(rowData.get("detectionUnit"), 9))            // col 109-117
               .append(getPaddedString(rowData.get("radMeasureError"), 9));         // col 118-126
            
            writer.println(row.toString());
        }
    }

    protected void writeTrailerRow(PrintWriter writer, int count) {
        writer.println("#TLR"+getPaddedString(String.valueOf(count), 4));
    }
    
    protected String getPaddedString(String value, int width) {
        if (value == null)
            value = "";
        
        if (value.length() > width) {
            value = value.substring(0, width);
        } else {
            while (value.length() < width)
                value += " ";
        }

        return value;
    }
    
    protected String getPaddedNumber(String value, int width) {
        if (value == null)
            return getPaddedString(value, width);
        
        if (value.length() > width) {
            value = value.substring(0, width);
        } else {
            while (value.length() < width)
                value = "0"+value;
        }

        return value;
    }
    
    protected void addStatusRow(String pwsId, Integer accessionNumber, String message) {
        HashMap<String, Object> statusRow;
        
        statusRow = new HashMap<String, Object>();
        statusRow.put("pws_id", pwsId);
        statusRow.put("accession_number", accessionNumber);
        statusRow.put("status", message);
        statusList.add(statusRow);
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
        methodCodes.put("epa 245.2 mercury dw",  "245.2");
        methodCodes.put("epa 300.0",             "300.0");
        methodCodes.put("epa 300.1",             "300.1");
        methodCodes.put("epa 314.0",             "314");
        methodCodes.put("epa 350.1",             "350.1");
        methodCodes.put("epa 353.2 as n",        "353.2");
        methodCodes.put("epa 365.1",             "365.1");
        methodCodes.put("epa 508 chi",           "508");
        methodCodes.put("epa 508a",              "508A");
        methodCodes.put("epa 515.3 acid herbs",  "515.3");
        methodCodes.put("epa 524.2",             "524.2");
        methodCodes.put("epa 524.2 thm",         "524.2");
        methodCodes.put("epa 525.2",             "525.2");
        methodCodes.put("epa 548.1",             "548.1");
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
        methodCodes.put("sm 2540 c,dissolved",   "2540C");
        methodCodes.put("sm 4500 cn e",          "4500CN-E");
        methodCodes.put("sm 4500 f c",           "4500F-C");
        methodCodes.put("sm 4500 h+ b",          "4500H-B");
        methodCodes.put("sm 4500 h+ b ph & alk", "4500H-B");
        methodCodes.put("sm 4500 no2 b",         "4500NO2-B");
        methodCodes.put("sm 4500 p e",           "4500P-E");
        methodCodes.put("sm 4500 si d",          "4500SI-D");
        methodCodes.put("sm 5310 b",             "5310B");
        methodCodes.put("sm 7500 i c 19th",      "7500-IC");
        methodCodes.put("epa 547",               "547");
        methodCodes.put("epa 531.1",             "531.1");
        methodCodes.put("epa 549.2",             "549.2");
        methodCodes.put("sm 5910",               "5910B");
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
        contaminantIds.put("4,4'-DDE",                              "2009");
        contaminantIds.put("Acenaphthene",                          "2261");
        contaminantIds.put("Acenaphthylene",                        "2260");
        contaminantIds.put("Acetochlor",                            "2027");
        contaminantIds.put("Alachlor",                              "2051");
        contaminantIds.put("Aldrin",                                "2356");
        contaminantIds.put("Ammonia Nitrogen as N",                 "1003");
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
        contaminantIds.put("Bicarbonate Alkalinity",                "1026");
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
        contaminantIds.put("cis-1,3-Dichloropropene",               "2228");
        contaminantIds.put("Combined Radiums",                      "4010");
        contaminantIds.put("Copper",                                "1022");
        contaminantIds.put("Cyanazine",                             "2054");
        contaminantIds.put("Cyanide",                               "1024");  
        contaminantIds.put("Dalapon",                               "2031");   
        contaminantIds.put("Desethyl Atrazine",                     "2006");
        contaminantIds.put("Desisopropyl Atrazine",                 "2007");
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
        contaminantIds.put("Methoxychlor",                          "2015"); 
        contaminantIds.put("Methyl-t-butyl ether (MtBE)",           "2251");
        contaminantIds.put("Metolachlor",                           "2045");
        contaminantIds.put("Metribuzin",                            "2595"); 
        contaminantIds.put("Molinate",                              "2626");   
        contaminantIds.put("Naphthalene",                           "2248");
        contaminantIds.put("n-Butylbenzene",                        "2422");
        contaminantIds.put("Nickel",                                "1036");
        contaminantIds.put("Nitrate Nitrogen as N",                 "1040");
        contaminantIds.put("Nitrite Nitrogen as N",                 "1041");
        contaminantIds.put("Nitrobenzene",                          "2254");
        contaminantIds.put("n-Propylbenzene",                       "2998");
        contaminantIds.put("Ortho Phosphate as P",                  "1044");
        contaminantIds.put("o-Xylene",                              "2997");
        contaminantIds.put("PCB Total as DCBP",                     "2383");
        contaminantIds.put("Pentachlorophenol",                     "2326");
        contaminantIds.put("Perchlorate",                           "1039");
        contaminantIds.put("Phenanthrene",                          "2278");
        contaminantIds.put("Phenolphthalein Alkalinity",            "1931");
        contaminantIds.put("Picloram",                              "2040");
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
        contaminantIds.put("Sulfate",                               "1055");
        contaminantIds.put("Terbacil",                              "2272");
        contaminantIds.put("Terbufos",                              "2545");  
        contaminantIds.put("tert-Butylbenzene",                     "2426");
        contaminantIds.put("Tetrachloroethene",                     "2987"); 
        contaminantIds.put("Thallium",                              "1085");
        contaminantIds.put("Toluene",                               "2991");
        contaminantIds.put("Total 1,3-Dichloropropene",             "2413");
        contaminantIds.put("Total Alkalinity",                      "1927");
        contaminantIds.put("Total Coliform Bacteria",               "3100");
        contaminantIds.put("Total DCPA mono and diacid degradates", "2108");
        contaminantIds.put("Total Dissolved Solids",                "1930");
        contaminantIds.put("Total Haloacetic Acids (HAA5)",         "2456");
        contaminantIds.put("Total Hardness",                        "1915");  
        contaminantIds.put("Total Organic Carbon",                  "2920");
        contaminantIds.put("Total Trihalomethanes",                 "2950");
        contaminantIds.put("Toxaphene",                             "2020");
        contaminantIds.put("trans-1,2-Dichloroethene",              "2979");
        contaminantIds.put("trans-1,3-Dichloropropene",             "2224");
        contaminantIds.put("Trichloroacetic acid",                  "2452");
        contaminantIds.put("Trichloroethene",                       "2984");
        contaminantIds.put("Trichlorofluoromethane",                "2218");
        contaminantIds.put("Triclopyr",                             "2107");  
        contaminantIds.put("Trifluralin",                           "2055");
        contaminantIds.put("Tritium",                               "4102");    
        contaminantIds.put("Turbidity",                             "100");   
        contaminantIds.put("Uranium",                               "4006");
        contaminantIds.put("Zinc",                                  "1095");  
        contaminantIds.put("AMPA",                                  "2097");
        contaminantIds.put("Glyphosate",                            "2034");
        contaminantIds.put("Diquat",                                "2032");
        contaminantIds.put("UV Absorbance at 254 nm",               "2922");
        contaminantIds.put("Aldicarb",                              "2047");
        contaminantIds.put("Aldicarb sulfone",                      "2044");
        contaminantIds.put("Aldicarb sulfoxide",                    "2043");
        contaminantIds.put("Oxamyl",                                "2036");
        contaminantIds.put("Methomyl",                              "2022");
        contaminantIds.put("3-Hydroxycarbofuran",                   "2066");
    }
    
    public boolean next() throws JRException {
        boolean hasNext = false;
        
        if (statusList != null) {
            statusIndex++;
            hasNext = statusIndex < statusList.size();
        }
        
        return hasNext;
    }
    
    public Object getFieldValue(JRField field) throws JRException {
        Object                  objValue = null;
        HashMap<String, Object> statRow;

        if (field != null && statusList != null) {
            statRow = (HashMap<String, Object>) statusList.get(statusIndex);
            if (!statRow.containsKey(field.getName()))
                throw new JRException("Unable to get value for field '" + field.getName());
            objValue = statRow.get(field.getName());
        }
        
        return objValue;
    }
}
