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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
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
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SectionCacheLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.remote.SDWISUnloadReportRemote;
import org.openelis.report.Prompt;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class, authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER, mappedName = "java:/OpenELISDS")
public class SDWISUnloadReportBean implements SDWISUnloadReportRemote {

    @Resource
    private SessionContext  ctx;

    @EJB
    private SessionCacheLocal session;    

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
        ArrayList<OptionListItem> loc;
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
            loc.add(new OptionListItem("-an", "Ankeny"));
            loc.add(new OptionListItem("-ic", "Iowa City"));
            loc.add(new OptionListItem("-lk", "Lakeside"));

            p.add(new Prompt("LOCATION", Prompt.Type.ARRAY).setPrompt("Location:")
                                                           .setWidth(100)
                                                           .setOptionList(loc)
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
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        boolean reject;
        int sampleCount;
        AnalysisViewDO aVDO;
        ArrayList<SampleDO> samples;
        ArrayList<AnalysisViewDO> analyses;
        Date beginReleased, endReleased;
        File tempFile;
        FileOutputStream out;
        HashMap<String, QueryData> param;
        Iterator<SampleDO> sIter;
        Iterator<AnalysisViewDO> aIter;
        PrintWriter writer;
        ReportStatus status;
        SampleDO sDO;
        SampleSDWISViewDO ssVDO;
        SectionViewDO secVDO;
        SimpleDateFormat format;
        String location;

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
        
        try {
            tempFile = File.createTempFile("sdwisUnload", ".lrr", new File("/tmp"));
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
            
            samples = sample.fetchSDWISByReleasedAndLocation(beginReleased, endReleased, location);
            sIter = samples.iterator();
            while (sIter.hasNext()) {
                sDO = sIter.next();
                ssVDO = sampleSdwis.fetchBySampleId(sDO.getId());
                reject = writeSampleRow(writer, sDO, ssVDO);
                
                if (!reject) {
                    analyses = analysis.fetchBySampleId(sDO.getId());
                    aIter = analyses.iterator();
                    while (aIter.hasNext()) {
                        aVDO = aIter.next();
                        if (releasedStatusId.equals(aVDO.getStatusId())) {
                            secVDO = sectionCache.getById(aVDO.getSectionId());
                            if (secVDO != null && secVDO.getName().endsWith(location))
                                writeResultRows(writer, ssVDO, aVDO);
                        }
                    }
                }
                
                sampleCount++;
                status.setPercentComplete((sampleCount / samples.size()) * 80 + 10);
            }
            
            writeTrailerRow(writer, sampleCount);
            
            writer.close();
            out.close();

            status.setPercentComplete(100);

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
           .append(" ");                                // col 31
        
        if ("-an".equals(location))                     // col 32-36
            row.append("397  ");                        // Ankemy DNR ID
        else if ("-ic".equals(location))
            row.append("027  ");                        // Iowa City DNR ID
        else if ("-lk".equals(location))
            row.append("393  ");                        // Lakeside DNR ID
        else
            row.append("     ");
           
        row.append(" ")                                 // col 37
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

    protected boolean writeSampleRow(PrintWriter writer, SampleDO sVDO, SampleSDWISViewDO ssVDO) throws Exception {
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
            sampleQaList = sampleQA.fetchResultOverrideBySampleIdList(sampleIds);
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
        row.append("#SAM")                                                          // col 1-3
           .append(sampCatDO.getLocalAbbrev())                                      // col 5-6
           .append(sampTypeDO.getLocalAbbrev())                                     // col 7-8
           .append(getPaddedString(pbType, 3))                                      // col 9-11
           .append(getPaddedString(ssVDO.getPwsNumber0(), 9))                       // col 12-20
           .append(getPaddedString(ssVDO.getFacilityId(), 12))                      // col 21-32
           .append(getPaddedString(ssVDO.getSamplePointId(), 11))                   // col 33-43
           .append(getPaddedString(ssVDO.getLocation(), 20));                       // col 44-63
        
        if (sVDO.getCollectionDate() != null)                                       // col 64-71
            row.append(dateFormat.format(sVDO.getCollectionDate().getDate()));
        else
            row.append(getPaddedString("", 8));
            
        if (sVDO.getCollectionTime() != null)                                       // col 72-75
            row.append(timeFormat.format(sVDO.getCollectionTime().getDate()));
        else
            row.append(getPaddedString("", 4));
        
        row.append(getPaddedString(ssVDO.getCollector(), 20))                       // col 76-95
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
        
        writer.println(row.toString());
        
        return sampleOverride.length() > 0;
    }

    protected void writeResultRows(PrintWriter writer, SampleSDWISViewDO sampleSDWIS, AnalysisViewDO analysis) throws Exception {
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
        if (methodCode == null)
            return;
        
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

    private void initMethodCodes() {
        methodCodes = new HashMap<String, String>();
        
        methodCodes.put("colilert mpn sdwa am", "9223B-QT");
        methodCodes.put("colilert mpn sdwa pm", "9223B-18QT");
        methodCodes.put("colilert pa sdwa am",  "9223B-PA");
        methodCodes.put("colilert pa sdwa pm",  "9223B-18PA");
        methodCodes.put("blue pour plate",      "9215B");
    }
    
    private void initContaminantIds() {
        contaminantIds = new HashMap<String, String>();
        
        contaminantIds.put("E.coli",                    "3014");
        contaminantIds.put("Fecal Coliform",            "3013");
        contaminantIds.put("Heterotrophic Plate Count", "3001");
        contaminantIds.put("Total Coliform Bacteria",   "3100");
    }
}
