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

import static org.openelis.manager.SampleManager1Accessor.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ExchangeExternalTermViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

/**
 * This class is used for generating strings with a specific format for Air
 * Quality samples. It retrieves all Air Quality samples within the specified
 * date range, generates all relevant strings, and writes them to a new file.
 * 
 * The following system variables and their formats are used in this class:
 * air_metal_lead_test ("test id"|"lead filter lot blank")
 * 
 * air_metal_manganese_test ("test id"|"manganese filter lot blank")
 * 
 * air_metal_mn_accuracy_test ("test id")
 * 
 * air_pressure_constants
 * ("parameter code"|"duration code"|"reported unit code"|"method code")
 * 
 * air_qualifier_code ("qualifier code"|"Qualifier result string",repeating)
 * 
 * air_report_directory ("directory location")
 * 
 * air_report_to ("organization id"|"organization attention")
 * 
 * air_sulfate_nitrate_tests
 * ("test id";"sulfate analyte";"nitrate analyte"|repeat if necessary)
 * 
 * air_temperature_constants
 * ("parameter code"|"duration code"|"reported unit code"|"method code")
 * 
 * air_tnmoc_speciated ("parameter code"|"duration code"|"method code"|"MDL")
 * 
 * air_total_tolualdehydes
 * ("parameter code"|"duration code"|"method code"|"MDL")
 * 
 * air_toxics_tests ("test id"|"test id"|repeating)
 * 
 */
@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class AirQualityExportBean {

    @EJB
    private SystemVariableBean                       systemVariable;

    @EJB
    private SampleManager1Bean                       sample;

    @EJB
    private CategoryCacheBean                        categoryCache;

    @EJB
    private ExchangeExternalTermBean                 exchangeExternalTerm;

    @EJB
    private AnalyteParameterBean                     analyteParameter;

    @EJB
    private DictionaryBean                           dictionary;

    @EJB
    private AuxDataHelperBean                        auxDataHelper;

    @EJB
    private SectionBean                              section;

    private Boolean                                  addAll, addTds, replicate;

    private static final String                      delim           = "|", escape = "\\",
                    rawDataType = "RD", precisionType = "QA", time = "00:00",
                    duplicateAssessmentType = "Duplicate", replicateAssessmentType = "Replicate",
                    assessmentNumber = "1", to11 = "to-11", to12 = "to-12",
                    tolualdehyde = "Tolualdehyde", md = "MD", sq = "SQ", cb = "CB", da = "DA",
                    fb = "FB", primaryPOC = "01", secondaryPOC = "02", duplicatePOC = "03",
                    mdlAnalyte = "MDL", mnUnitCode = "108";

    private String                                   ttdsDurationCode, ttdsMethodCode,
                    ttdsParameterCode, tnmocDurationCode, tnmocMethodCode, tnmocParameterCode,
                    pressureParameter, pressureDurationCode, pressureReportedUnit,
                    pressureMethodCode, temperatureParameter, temperatureDurationCode,
                    temperatureReportedUnit, temperatureMethodCode, action;

    private static final Double                      nitrateConstant = 4.4;

    private Double                                   metalLeadFilterLotBlank,
                    metalManganeseFilterLotBlank;

    private ArrayList<String>                        sulfateAnalytes, nitrateAnalytes;

    private HashMap<Integer, Integer>                sectionMap;

    private SampleData                               sampleData;

    private HashMap<Integer, String>                 analyteCodes, unitCodes, methodCodes,
                    durationCodes, agencyCodes;

    private HashMap<Integer, AnalyteParameterViewDO> analyteParameters;

    private HashMap<String, ArrayList<String>>       airToxicRawStrings, airToxicPrecisionStrings,
                    metalRawStrings, pressureStrings, temperatureStrings;

    private Integer                                  metalLeadTest, metalManganeseTest,
                    metalManganeseAccuracyTest;

    private static final Logger                      log             = Logger.getLogger("openelis");

    private enum MetalTestType {
        Lead, Manganese, MnAccuracy
    }

    /*
     * Returns the prompt for a single re-print
     */
    public ArrayList<Prompt> getPrompts() throws Exception {
        ArrayList<Prompt> p;
        ArrayList<OptionListItem> options;

        try {
            p = new ArrayList<Prompt>();

            p.add(new Prompt("FROM_DATE", Prompt.Type.DATETIME).setPrompt("Collected Date:")
                                                               .setWidth(150)
                                                               .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                               .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                               .setDefaultValue(null));

            p.add(new Prompt("TO_DATE", Prompt.Type.DATETIME).setPrompt("to:")
                                                             .setWidth(150)
                                                             .setDatetimeStartCode(Prompt.Datetime.YEAR)
                                                             .setDatetimeEndCode(Prompt.Datetime.DAY)
                                                             .setDefaultValue(null));

            p.add(new Prompt("ACCESSION", Prompt.Type.INTEGER).setPrompt("Accession Number:")
                                                              .setWidth(150));

            options = getDictionaryList("air_quality_action");
            p.add(new Prompt("ACTION", Prompt.Type.ARRAY).setPrompt("Action:")
                                                         .setWidth(150)
                                                         .setOptionList(options)
                                                         .setMultiSelect(false)
                                                         .setRequired(true)
                                                         .setDefaultValue(options.get(0).getLabel()));

            return p;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create prompts", e);
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        boolean pressureTempString, fileWritten;
        int added;
        Integer analysisCount, testId, orgId;
        ReportStatus status;
        String val, fromDate, toDate, accession, reportTo, attention, tempArray[], codeArray[], analyteArray[], qualifierCode, airDir, errorMessage;
        StringBuffer eMessage;
        Datetime dt;
        QueryData field;
        Query query;
        ArrayList<SampleManager1> sms;
        ArrayList<QueryData> fields;
        ArrayList<String> qualifierStrings, stringList;
        ArrayList<Integer> sulfateNitrateTests, airToxicsTests;
        HashMap<Integer, Datetime> problemSamples;
        HashMap<String, ArrayList<SampleManager1>> samples;
        HashMap<String, ArrayList<String>> speciationStrings;
        HashMap<String, QueryData> param;
        HashSet<Integer> analyteIds, unitIds, testIds, orgIds;

        try {
            /*
             * get system variables
             */
            try {
                /*
                 * Report to organization for air quality samples.
                 * 
                 * Format: "organization id"|"organization attention"
                 */
                val = systemVariable.fetchByName("air_report_to").getValue();
                tempArray = val.split(escape + delim);
                reportTo = DataBaseUtil.trim(tempArray[0]);
                attention = DataBaseUtil.trim(tempArray[1]);
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_report_to");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            airDir = null;
            try {
                /*
                 * Location that all files are output to.
                 */
                airDir = systemVariable.fetchByName("air_report_directory").getValue();
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_report_directory");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Test ids and analyte names for sulfate and nitrate string
                 * creation.
                 * 
                 * Format: "test id";"sulfate analyte";"nitrate analyte"|repeat
                 * if necessary
                 */
                val = systemVariable.fetchByName("air_sulfate_nitrate_tests").getValue();
                tempArray = val.split(escape + delim);
                sulfateNitrateTests = new ArrayList<Integer>();
                sulfateAnalytes = new ArrayList<String>();
                nitrateAnalytes = new ArrayList<String>();
                for (int i = 0; i < tempArray.length; i++ ) {
                    analyteArray = tempArray[i].split(";");
                    sulfateNitrateTests.add(Integer.parseInt(analyteArray[0]));
                    sulfateAnalytes.add(analyteArray[1]);
                    nitrateAnalytes.add(analyteArray[2]);
                }
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_sulfate_nitrate_tests");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Test ids, method codes, and duration codes for air toxics
                 * tests.
                 * 
                 * Format: "test id";"method code";"duration code"|repeating
                 */
                val = systemVariable.fetchByName("air_toxics_tests").getValue();
                tempArray = val.split(escape + delim);
                airToxicsTests = new ArrayList<Integer>();
                methodCodes = new HashMap<Integer, String>();
                durationCodes = new HashMap<Integer, String>();
                for (int i = 0; i < tempArray.length; i++ ) {
                    codeArray = tempArray[i].split(";");
                    testId = Integer.parseInt(codeArray[0]);
                    methodCodes.put(testId, codeArray[1]);
                    durationCodes.put(testId, codeArray[2]);
                    airToxicsTests.add(testId);
                }
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_toxics_tests");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Test id and filter lot blank for metal lead test.
                 * 
                 * Format: "test id"|"lead filter lot blank"
                 */
                val = systemVariable.fetchByName("air_metal_lead_test").getValue();
                tempArray = val.split(escape + delim);
                metalLeadTest = Integer.parseInt(tempArray[0]);
                metalLeadFilterLotBlank = Double.parseDouble(tempArray[1]);
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_metal_lead_test");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Test id and filter lot blank for metal manganese test.
                 * 
                 * Format: "test id"|"manganese filter lot blank"
                 */
                val = systemVariable.fetchByName("air_metal_manganese_test").getValue();
                tempArray = val.split(escape + delim);
                metalManganeseTest = Integer.parseInt(tempArray[0]);
                metalManganeseFilterLotBlank = Double.parseDouble(tempArray[1]);
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_metal_manganese_test");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Test id for metal manganese accuracy test.
                 */
                metalManganeseAccuracyTest = Integer.parseInt(systemVariable.fetchByName("air_metal_mn_accuracy_test")
                                                                            .getValue());
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_metal_mn_accuracy_test");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Constant data for "Total Tolualdehydes" analyte.
                 * 
                 * Format: "parameter code"|"duration code"|"method code"
                 */
                val = systemVariable.fetchByName("air_total_tolualdehydes").getValue();
                tempArray = val.split(escape + delim);
                ttdsParameterCode = tempArray[0];
                ttdsDurationCode = tempArray[1];
                ttdsMethodCode = tempArray[2];
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_total_tolualdehydes");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Constant data for "TNMOC,Speciated" analyte.
                 * 
                 * Format: "parameter code"|"duration code"|"method code"
                 */
                val = systemVariable.fetchByName("air_tnmoc_speciated").getValue();
                tempArray = val.split(escape + delim);
                tnmocParameterCode = tempArray[0];
                tnmocDurationCode = tempArray[1];
                tnmocMethodCode = tempArray[2];
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_tnmoc_speciated");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Qualifier code constants for air toxics "under limit"
                 * qualifier
                 * 
                 * Format: "qualifier code"|"Qualifier result string",repeating
                 */
                val = systemVariable.fetchByName("air_qualifier_code").getValue();
                tempArray = val.split(escape + delim);
                qualifierCode = tempArray[0];
                tempArray = tempArray[1].split(",");
                qualifierStrings = new ArrayList<String>();
                for (int i = 0; i < tempArray.length; i++ )
                    qualifierStrings.add(tempArray[i]);
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_qualifier_code");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Constants for pressure strings.
                 * 
                 * Format:
                 * "parameter code"|"duration code"|"reported unit code"|
                 * "method code"
                 */
                val = systemVariable.fetchByName("air_pressure_constants").getValue();
                tempArray = val.split(escape + delim);
                pressureParameter = tempArray[0];
                pressureDurationCode = tempArray[1];
                pressureReportedUnit = tempArray[2];
                pressureMethodCode = tempArray[3];
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_pressure_constants");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                /*
                 * Constants for temperature strings.
                 * 
                 * Format:
                 * "parameter code"|"duration code"|"reported unit code"|
                 * "method code"
                 */
                val = systemVariable.fetchByName("air_temperature_constants").getValue();
                tempArray = val.split(escape + delim);
                temperatureParameter = tempArray[0];
                temperatureDurationCode = tempArray[1];
                temperatureReportedUnit = tempArray[2];
                temperatureMethodCode = tempArray[3];
            } catch (Exception e) {
                errorMessage = Messages.get()
                                       .systemVariable_missingInvalidSystemVariable("air_temperature_constants");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            status = new ReportStatus();

            /*
             * recover all the params and build a specific where clause
             */
            param = ReportUtil.getMapParameter(paramList);

            fromDate = ReportUtil.getStringParameter(param, "FROM_DATE");
            toDate = ReportUtil.getStringParameter(param, "TO_DATE");
            accession = ReportUtil.getStringParameter(param, "ACCESSION");
            action = ReportUtil.getStringParameter(param, "ACTION");
            action = dictionary.fetchById(Integer.parseInt(action)).getEntry();

            if (DataBaseUtil.isEmpty(action))
                throw new InconsistencyException(Messages.get().airQuality_noActionException());

            if (fromDate == null || toDate == null) {
                if (accession == null)
                    /*
                     * This is the case where there is not enough data to query
                     * for samples. One or both of the dates is missing, and the
                     * accession number is missing.
                     */
                    throw new InconsistencyException(Messages.get().airQuality_noDataException());

                sms = new ArrayList<SampleManager1>();
                sms.add(sample.fetchByAccession(Integer.parseInt(accession),
                                                SampleManager1.Load.AUXDATA,
                                                SampleManager1.Load.RESULT,
                                                SampleManager1.Load.QA));
            } else if (accession == null) {
                fields = new ArrayList<QueryData>();
                field = new QueryData();
                field.setQuery(DataBaseUtil.concatWithSeparator(fromDate, "..", toDate));
                field.setKey(SampleMeta.getCollectionDate());
                field.setType(QueryData.Type.DATE);
                fields.add(field);

                field = new QueryData();
                field.setQuery(reportTo);
                field.setKey(SampleMeta.getSampleOrgOrganizationId());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setQuery(DataBaseUtil.toString(Constants.dictionary().ORG_REPORT_TO));
                field.setKey(SampleMeta.getSampleOrgTypeId());
                field.setType(QueryData.Type.INTEGER);
                fields.add(field);

                field = new QueryData();
                field.setQuery(attention);
                field.setKey(SampleMeta.getSampleOrgOrganizationAttention());
                field.setType(QueryData.Type.STRING);
                fields.add(field);

                query = new Query();
                query.setFields(fields);
                try {
                    sms = sample.fetchByQuery(fields,
                                              0,
                                              -1,
                                              SampleManager1.Load.AUXDATA,
                                              SampleManager1.Load.RESULT,
                                              SampleManager1.Load.QA);
                } catch (NotFoundException e) {
                    status.setMessage(Messages.get().airQuality_noSamples());
                    return status;
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                    return status;
                }
            } else {
                /*
                 * This is the case where there is too much data to query for
                 * samples. Both dates are filled and the accession number is
                 * filled.
                 */
                throw new InconsistencyException(Messages.get().airQuality_tooMuchDataException());
            }
            status.setMessage(Messages.get().gen_numberCases(DataBaseUtil.toString(sms.size())));
            samples = new HashMap<String, ArrayList<SampleManager1>>();
            analyteIds = new HashSet<Integer>();
            unitIds = new HashSet<Integer>();
            testIds = new HashSet<Integer>();
            orgIds = new HashSet<Integer>();
            sectionMap = new HashMap<Integer, Integer>();
            problemSamples = new HashMap<Integer, Datetime>();

            /*
             * separate the samples for each site, find duplicate samples, and
             * find problem samples
             */
            for (SampleManager1 sm : sms) {
                analysisCount = 0;
                if (DataBaseUtil.isDifferent(Constants.dictionary().SAMPLE_RELEASED,
                                             sm.getSample().getStatusId())) {
                    problemSamples.put(sm.getSample().getAccessionNumber(), sm.getSample()
                                                                              .getCollectionDate());
                    continue;
                }

                /*
                 * find air toxic duplicates
                 */
                if (getAnalyses(sm) != null) {
                    for (AnalysisViewDO data : getAnalyses(sm)) {
                        if ( !DataBaseUtil.isDifferent(Constants.dictionary().ANALYSIS_RELEASED,
                                                       data.getStatusId())) {
                            analysisCount++ ;
                            if (data.getUnitOfMeasureId() != null)
                                unitIds.add(data.getUnitOfMeasureId());
                            testIds.add(data.getTestId());
                            if (sectionMap.get(data.getSectionId()) == null) {
                                orgId = section.fetchById(data.getSectionId()).getOrganizationId();
                                if (orgId != null) {
                                    sectionMap.put(data.getSectionId(), orgId);
                                    orgIds.add(orgId);
                                }
                            }

                        }
                    }
                }

                /*
                 * if this sample doesn't have any analyses that strings can be
                 * created from
                 */
                if (analysisCount < 1) {
                    problemSamples.put(sm.getSample().getAccessionNumber(), sm.getSample()
                                                                              .getCollectionDate());
                    continue;
                }

                /*
                 * get all analyte IDs to find the corresponding codes
                 */
                for (ResultViewDO data : getResults(sm)) {
                    if (DataBaseUtil.isSame(data.getIsColumn(), "N"))
                        analyteIds.add(data.getAnalyteId());
                }

                /*
                 * create separate lists for each sampling site
                 */
                if ( !samples.keySet().contains(sm.getSampleEnvironmental().getLocation()))
                    samples.put(sm.getSampleEnvironmental().getLocation(),
                                new ArrayList<SampleManager1>());
                samples.get(sm.getSampleEnvironmental().getLocation()).add(sm);
            }

            /*
             * get the air quality codes corresponding with the analytes and
             * units that are used
             */
            try {
                getAnalyteCodes(analyteIds);
            } catch (Exception e) {
                errorMessage = Messages.get().airQuality_retrievalError("analyte codes");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                getUnitCodes(unitIds);
            } catch (Exception e) {
                errorMessage = Messages.get().airQuality_retrievalError("unit codes");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                getAnalyteParameters(testIds);
            } catch (Exception e) {
                errorMessage = Messages.get().airQuality_retrievalError("analyte parameters");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            try {
                getAgencyCodes(orgIds);
            } catch (Exception e) {
                errorMessage = Messages.get().airQuality_retrievalError("agency codes");
                log.log(Level.SEVERE, errorMessage, e);
                throw new Exception(errorMessage);
            }

            speciationStrings = new HashMap<String, ArrayList<String>>();
            metalRawStrings = new HashMap<String, ArrayList<String>>();
            airToxicRawStrings = new HashMap<String, ArrayList<String>>();
            airToxicPrecisionStrings = new HashMap<String, ArrayList<String>>();
            pressureStrings = new HashMap<String, ArrayList<String>>();
            temperatureStrings = new HashMap<String, ArrayList<String>>();
            /*
             * create strings for each valid analysis
             */
            for (String location : samples.keySet()) {
                for (SampleManager1 sm : samples.get(location)) {
                    sampleData = new SampleData(sm);
                    testIds.clear();
                    pressureTempString = false;
                    if (getResults(sm) != null) {
                        for (AnalysisViewDO analysis : getAnalyses(sm)) {
                            if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(analysis.getStatusId()))
                                continue;
                            replicate = false;
                            addAll = false;
                            addTds = false;
                            if (sulfateNitrateTests.contains(analysis.getTestId())) {
                                try {
                                    stringList = getSulfateNitrateStrings(sm, analysis.getId());
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (stringList == null || stringList.size() < 1) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (speciationStrings.get(sampleData.key) == null)
                                    speciationStrings.put(sampleData.key, new ArrayList<String>());
                                speciationStrings.get(sampleData.key).addAll(stringList);
                            } else if (airToxicsTests.contains(analysis.getTestId())) {
                                /*
                                 * all results for a certain test are processed
                                 * at once, so we can skip the test after it has
                                 * already appeared once before
                                 */
                                if (testIds.contains(analysis.getTestId()))
                                    continue;
                                testIds.add(analysis.getTestId());
                                /*
                                 * process non replicate analyses
                                 */
                                sampleData.groupResults(analysis.getTestId(), false);
                                if (sampleData.results.size() < 1)
                                    continue;
                                try {
                                    stringList = getAirToxicsStrings(sm,
                                                                     qualifierCode,
                                                                     qualifierStrings);
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (stringList == null) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (stringList.size() > 0) {
                                    if (airToxicRawStrings.get(sampleData.key) == null)
                                        airToxicRawStrings.put(sampleData.key,
                                                               new ArrayList<String>());
                                    airToxicRawStrings.get(sampleData.key).addAll(stringList);
                                }

                                /*
                                 * process replicate analyses
                                 */
                                sampleData.groupResults(analysis.getTestId(), true);
                                if (sampleData.results.size() < 1)
                                    continue;
                                try {
                                    stringList = getAirToxicsPrecisionStrings();
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (stringList == null) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (stringList.size() > 0) {
                                    if (airToxicPrecisionStrings.get(sampleData.key) == null)
                                        airToxicPrecisionStrings.put(sampleData.key,
                                                                     new ArrayList<String>());
                                    airToxicPrecisionStrings.get(sampleData.key).addAll(stringList);
                                }
                            } else if (metalLeadTest.equals(analysis.getTestId())) {
                                try {
                                    added = getMetalsStrings(sm,
                                                             analysis.getTestId(),
                                                             analysis.getId(),
                                                             pressureTempString);
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (added < 1) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                pressureTempString = true;
                            } else if (metalManganeseAccuracyTest.equals(analysis.getTestId())) {
                                try {
                                    added = getMetalsStrings(sm,
                                                             analysis.getTestId(),
                                                             analysis.getId(),
                                                             pressureTempString);
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (added < 1) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                pressureTempString = true;
                            } else if (metalManganeseTest.equals(analysis.getTestId())) {
                                try {
                                    added = getMetalsStrings(sm,
                                                             analysis.getTestId(),
                                                             analysis.getId(),
                                                             pressureTempString);
                                } catch (Exception e) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                if (added < 1) {
                                    problemSamples.put(sm.getSample().getAccessionNumber(),
                                                       sm.getSample().getCollectionDate());
                                    continue;
                                }
                                pressureTempString = true;
                            }
                        }
                    }
                }
            }

            /*
             * write strings out to text files
             */
            fileWritten = writeFile(speciationStrings, "speciation_", airDir) |
                          writeFile(airToxicRawStrings, "airtoxics_raw_", airDir) |
                          writeFile(airToxicPrecisionStrings, "airtoxics_precision_", airDir) |
                          writeFile(metalRawStrings, "metals_raw_", airDir) |
                          writeFile(pressureStrings, "pressure_", airDir) |
                          writeFile(temperatureStrings, "temperature_", airDir);
            /*
             * if there are any problem samples, throw an exception to let the
             * user know the accession numbers
             */
            if (problemSamples.size() > 0) {
                eMessage = new StringBuffer();
                eMessage.append("Strings were not created for the following samples:")
                        .append(System.getProperty("line.separator"));
                for (Integer i : problemSamples.keySet()) {
                    dt = problemSamples.get(i);
                    eMessage.append(i)
                            .append(" (")
                            .append(ReportUtil.toString(dt.getDate(),
                                                        Messages.get().dateCompressedPattern()))
                            .append(")")
                            .append(System.getProperty("line.separator"));
                }
                if (fileWritten)
                    eMessage.append("Files were written to: ").append(airDir);
                status.setMessage(eMessage.toString());
                throw new Exception(eMessage.toString());
            }
            if (fileWritten)
                status.setMessage("Files were written to: " + airDir);
            else
                status.setMessage("No strings were generated");
            return status;
        } finally {
            clearVariables();
        }
    }

    /**
     * Get sulfate and nitrate air quality strings for sample
     */
    private ArrayList<String> getSulfateNitrateStrings(SampleManager1 sm, Integer analysisId) throws Exception {
        int sulfateDurationCd, nitrateDurationCd, sulfateMethodCd, nitrateMethodCd;
        String sulfateValue, nitrateValue, sulfateCode, nitrateCode, reportedUnit, sulfateAlternateMethodDetectableLimit, nitrateAlternateMethodDetectableLimit;
        StringBuilder ssb, nsb;
        AnalyteParameterViewDO sulfateParameter, nitrateParameter;
        DecimalFormat threeDigits, twoDecimals;
        ArrayList<String> sulfateNitrateStrings;

        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);
        twoDecimals = new DecimalFormat("#");
        twoDecimals.setMaximumFractionDigits(2);
        twoDecimals.setMinimumIntegerDigits(1);
        twoDecimals.setRoundingMode(RoundingMode.HALF_UP);
        sulfateValue = nitrateValue = sulfateCode = nitrateCode = reportedUnit = sulfateAlternateMethodDetectableLimit = nitrateAlternateMethodDetectableLimit = null;
        sulfateDurationCd = nitrateDurationCd = sulfateMethodCd = nitrateMethodCd = 0;

        if (sampleData.pocNitrate == null || sampleData.pocSulfate == null)
            return null;

        /*
         * get sulfate and nitrate data from the results
         */
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {
                if ("N".equals(data.getIsReportable()))
                    continue;
                if (sulfateAnalytes.contains(data.getAnalyte()) &&
                    data.getAnalysisId().equals(analysisId) &&
                    ( !DataBaseUtil.isEmpty(data.getValue()) || !DataBaseUtil.isEmpty(sampleData.nullDataCd))) {
                    sulfateParameter = analyteParameters.get(data.getAnalyteId());
                    sulfateAlternateMethodDetectableLimit = sulfateParameter.getP1().toString();
                    sulfateMethodCd = sulfateParameter.getP2().intValue();
                    sulfateDurationCd = sulfateParameter.getP3().intValue();
                    if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
                        if (data.getValue().contains("<"))
                            sulfateValue = "0";
                        else
                            sulfateValue = twoDecimals.format(Double.parseDouble(data.getValue()) /
                                                              Double.parseDouble(sampleData.volume));
                    }
                    sulfateCode = analyteCodes.get(data.getAnalyteId());
                    if (reportedUnit == null) {
                        for (AnalysisViewDO a : getAnalyses(sm)) {
                            if (a.getId().equals(data.getAnalysisId()))
                                reportedUnit = unitCodes.get(a.getUnitOfMeasureId());
                        }
                    }
                } else if (nitrateAnalytes.contains(data.getAnalyte()) &&
                           data.getAnalysisId().equals(analysisId) &&
                           ( !DataBaseUtil.isEmpty(data.getValue()) || !DataBaseUtil.isEmpty(sampleData.nullDataCd))) {
                    nitrateParameter = analyteParameters.get(data.getAnalyteId());
                    nitrateAlternateMethodDetectableLimit = nitrateParameter.getP1().toString();
                    nitrateMethodCd = nitrateParameter.getP2().intValue();
                    nitrateDurationCd = nitrateParameter.getP3().intValue();
                    if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
                        if (data.getValue().contains("<"))
                            nitrateValue = "0";
                        else
                            nitrateValue = twoDecimals.format( (Double.parseDouble(data.getValue()) * nitrateConstant) /
                                                              Double.parseDouble(sampleData.volume));
                    }
                    nitrateCode = analyteCodes.get(data.getAnalyteId());
                    if (reportedUnit == null) {
                        for (AnalysisViewDO a : getAnalyses(sm)) {
                            if (a.getId().equals(data.getAnalysisId()))
                                reportedUnit = unitCodes.get(a.getUnitOfMeasureId());
                        }
                    }
                }
            }
        } else {
            return null;
        }

        if (reportedUnit == null)
            return null;

        /*
         * build the strings
         */
        ssb = new StringBuilder();
        nsb = new StringBuilder();
        buildSiteInfo(ssb, rawDataType, sulfateCode, sampleData.pocSulfate);
        buildSiteInfo(nsb, rawDataType, nitrateCode, sampleData.pocNitrate);

        ssb.append(sulfateDurationCd)
           .append(delim)
           .append(reportedUnit)
           .append(delim)
           .append(threeDigits.format(sulfateMethodCd))
           .append(delim)
           .append(sampleData.date)
           .append(delim)
           .append(time)
           .append(delim);
        nsb.append(nitrateDurationCd)
           .append(delim)
           .append(reportedUnit)
           .append(delim)
           .append(threeDigits.format(nitrateMethodCd))
           .append(delim)
           .append(sampleData.date)
           .append(delim)
           .append(time)
           .append(delim);

        if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
            /*
             * the value is filled and the null data code is empty
             */
            ssb.append(sulfateValue).append(delim).append(delim);
            nsb.append(nitrateValue).append(delim).append(delim);
        } else {
            /*
             * the value is empty and the null data code is filled
             */
            ssb.append(delim).append(sampleData.nullDataCd).append(delim);
            nsb.append(delim).append(sampleData.nullDataCd).append(delim);
        }

        ssb.append(sampleData.collectionFreq).append(delim);
        nsb.append(sampleData.collectionFreq).append(delim);
        /*
         * fields #16 through #26 and #28 are not used and are empty
         */
        ssb.append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(sulfateAlternateMethodDetectableLimit)
           .append(delim);
        nsb.append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(nitrateAlternateMethodDetectableLimit)
           .append(delim);

        sulfateNitrateStrings = new ArrayList<String>();
        /*
         * check if the strings are valid before returning them
         */
        if (sulfateValue != null || sampleData.nullDataCd != null)
            sulfateNitrateStrings.add(ssb.toString());

        if (nitrateValue != null || sampleData.nullDataCd != null)
            sulfateNitrateStrings.add(nsb.toString());
        return sulfateNitrateStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private ArrayList<String> getAirToxicsStrings(SampleManager1 sm, String qualifierCode,
                                                  ArrayList<String> qualifierStrings) throws Exception {
        boolean isTolualdehyde;
        Double tmnoc, ttds, maxMdl;
        String parameter, value, mdl, qualifier, qualifiers[];
        StringBuilder sb;
        DecimalFormat number;
        ArrayList<String> airToxicsStrings;

        /*
         * only create strings for samples with descriptions of "sample" or
         * "replicate"
         */
        if ( !"sample".equalsIgnoreCase(sm.getSampleEnvironmental().getDescription()) &&
            !"duplicate".equalsIgnoreCase(sm.getSampleEnvironmental().getDescription()))
            return null;

        parameter = null;
        ttds = new Double(0);
        tmnoc = new Double(0);
        maxMdl = new Double(0);

        number = new DecimalFormat();
        number.setMaximumFractionDigits(6);

        if (sampleData.poc == null)
            return null;

        if ("duplicate".equalsIgnoreCase(sm.getSampleEnvironmental().getDescription())) {
            if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
                if (airToxicPrecisionStrings.get(sampleData.key) == null)
                    airToxicPrecisionStrings.put(sampleData.key, new ArrayList<String>());
                airToxicPrecisionStrings.get(sampleData.key).addAll(getAirToxicsPrecisionStrings());
            }
            /*
             * Raw data strings do not need to be generated in this case
             */
            return new ArrayList<String>();
        }

        /*
         * go through all the analytes and create a string for each one
         */
        airToxicsStrings = new ArrayList<String>();
        value = mdl = qualifier = null;
        isTolualdehyde = false;
        sb = new StringBuilder();
        if (getResults(sm) != null) {
            for (ResultViewDO result : sampleData.results) {
                /*
                 * get all result data for this analyte before creating the
                 * screen
                 */
                if (value == null) {
                    if (DataBaseUtil.isSame(result.getIsColumn(), "N")) {
                        if (result.getValue() != null)
                            value = result.getValue();
                        else
                            value = "0";
                        parameter = analyteCodes.get(result.getAnalyteId());

                        if (parameter == null) {
                            /*
                             * We do not create a string for this analyte. Add
                             * the result value to the extra analyte values if
                             * applicable.
                             */
                            if (result.getAnalyte().contains(tolualdehyde)) {
                                isTolualdehyde = true;
                            }
                            if (addTds && isTolualdehyde) {
                                if ( !value.contains("<"))
                                    ttds += Double.parseDouble(value);
                            }
                            if ( !isTolualdehyde)
                                value = null;
                            continue;
                        }
                        continue;
                    } else {
                        continue;
                    }
                } else if (mdl == null) {
                    if (result.getValue() != null)
                        mdl = result.getValue();
                    else
                        mdl = "";
                    continue;
                } else if (qualifier == null) {
                    if (result.getValue() != null)
                        qualifier = result.getValue();
                    else
                        qualifier = "";
                }

                /*
                 * find the greatest MDL of the analytes being summed
                 */
                try {
                    if (addAll && maxMdl < Double.valueOf(mdl)) {
                        maxMdl = Double.valueOf(mdl);
                    }
                } catch (Exception e) {
                }
                if (isTolualdehyde) {
                    try {
                        if (maxMdl < Double.valueOf(mdl))
                            maxMdl = Double.valueOf(mdl);
                    } catch (Exception e) {
                    }
                    value = null;
                    mdl = null;
                    qualifier = null;
                    isTolualdehyde = false;
                    continue;
                }

                /*
                 * if the data isn't there, a string cannot be created
                 */
                if (sampleData.durationCode == null || sampleData.methodCode == null) {
                    value = null;
                    mdl = null;
                    qualifier = null;
                    parameter = null;
                    continue;
                }
                buildSiteInfo(sb, rawDataType, parameter, sampleData.poc);

                sb.append(sampleData.durationCode).append(delim);
                sb.append(sampleData.reportedUnit).append(delim);
                sb.append(sampleData.methodCode).append(delim);
                sb.append(sampleData.date).append(delim);
                sb.append(time).append(delim);
                if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
                    if (value.contains("<")) {
                        /*
                         * the value is less than the minimum value, so we round
                         * down to zero
                         */
                        sb.append("0").append(delim);
                        /*
                         * null data code is empty
                         */
                        sb.append(delim);
                    } else {
                        sb.append(value).append(delim);
                        /*
                         * null data code is empty
                         */
                        sb.append(delim);
                        if (addAll)
                            tmnoc += Double.parseDouble(value);
                    }
                    sb.append(sampleData.collectionFreq).append(delim);
                    /*
                     * fields #16 and #18 through #26 and #28 are not used and
                     * are empty
                     */
                    sb.append(delim);
                    // TODO here is where QA flags need to be put
                    /*
                     * determine if any string in the qualifier field matches an
                     * "under limit" string
                     */
                    qualifiers = qualifier.split(";");
                    for (int i = 0; i < qualifiers.length; i++ ) {
                        if (qualifierStrings.contains(qualifiers[i])) {
                            sb.append(qualifierCode);
                            break;
                        }
                    }
                    sb.append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim);
                    sb.append(mdl).append(delim);

                } else {
                    /*
                     * there is a null data code, so the reported sample value
                     * and all fields after are left empty
                     */
                    sb.append(delim).append(sampleData.nullDataCd).append(delim);
                    sb.append(sampleData.collectionFreq)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim)
                      .append(delim);
                }

                airToxicsStrings.add(sb.toString());
                sb.setLength(0);
                value = null;
                mdl = null;
                qualifier = null;
                parameter = null;
            }
        }

        if (addAll) {
            airToxicsStrings.add(getAirToxicsSummedString(sm,
                                                          qualifierCode,
                                                          qualifierStrings,
                                                          tmnoc,
                                                          maxMdl));
        } else if (addTds) {
            airToxicsStrings.add(getAirToxicsSummedString(sm,
                                                          qualifierCode,
                                                          qualifierStrings,
                                                          ttds,
                                                          maxMdl));
        }
        return airToxicsStrings;
    }

    /**
     * creates air toxics raw string for summed results
     */
    private String getAirToxicsSummedString(SampleManager1 sm, String qualifierCode,
                                            ArrayList<String> qualifierStrings, Double value,
                                            Double maxMdl) throws Exception {
        String parameter;
        StringBuilder sb;
        DecimalFormat number;

        sb = new StringBuilder();
        number = null;

        number = new DecimalFormat();
        number.setMaximumFractionDigits(6);

        parameter = null;
        if (addAll)
            parameter = tnmocParameterCode;
        else if (addTds)
            parameter = ttdsParameterCode;
        buildSiteInfo(sb, rawDataType, parameter, sampleData.poc);
        if (addAll)
            sb.append(tnmocDurationCode).append(delim);
        else if (addTds)
            sb.append(ttdsDurationCode).append(delim);
        sb.append(sampleData.reportedUnit).append(delim);
        if (addAll)
            sb.append(tnmocMethodCode).append(delim);
        else if (addTds)
            sb.append(ttdsMethodCode).append(delim);
        sb.append(sampleData.date).append(delim);
        sb.append(time).append(delim);
        if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
            if (addAll) {
                if (value.doubleValue() == 0)
                    sb.append("0").append(delim);
                else
                    sb.append(truncateDecimal(value, 3)).append(delim);
            } else if (addTds) {
                if (value.doubleValue() == 0)
                    sb.append("0").append(delim);
                else
                    sb.append(truncateDecimal(value, 4)).append(delim);
            }
            /*
             * null data code is empty
             */
            sb.append(delim);
            sb.append(sampleData.collectionFreq).append(delim);
            /*
             * fields #16 through #26 and #28 are not used and are empty
             */
            sb.append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim);
            if (addAll)
                sb.append(number.format(maxMdl)).append(delim);
            else if (addTds)
                sb.append(number.format(maxMdl)).append(delim);
        } else {
            /*
             * there is a null data code, so the reported sample value is left
             * empty
             */
            sb.append(delim).append(sampleData.nullDataCd).append(delim);
            sb.append(sampleData.collectionFreq)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim);
        }
        return sb.toString();
    }

    /**
     * creates air toxics precision strings from a duplicate or replicate sample
     */
    private ArrayList<String> getAirToxicsPrecisionStrings() throws Exception {
        Double tmnoc, ttds;
        String parameter, value, analyte;
        StringBuilder sb;
        ArrayList<String> precisions;

        precisions = new ArrayList<String>();
        sb = new StringBuilder();
        value = analyte = parameter = null;
        tmnoc = new Double(0);
        ttds = new Double(0);
        for (ResultViewDO result : sampleData.results) {
            /*
             * get all result data for this analyte before creating the string
             */
            if (DataBaseUtil.isSame(result.getIsColumn(), "N")) {
                value = result.getValue();
                /*
                 * if the value is insignificant, a string does not need to be
                 * created
                 */
                if (value == null || value.contains("<")) {
                    value = null;
                    continue;
                }
                parameter = analyteCodes.get(result.getAnalyteId());
                analyte = result.getAnalyte();
            } else {
                continue;
            }

            /*
             * we do not create a string for this analyte
             */
            if (parameter == null) {
                /*
                 * add the result value to the extra analyte values if
                 * applicable
                 */
                if (addTds && analyte.contains(tolualdehyde)) {
                    if ( !value.contains("<"))
                        ttds += Double.parseDouble(value);
                }
                value = null;
                parameter = null;
                analyte = null;
                continue;
            }

            /*
             * if the data isn't there, a string cannot be created
             */
            if (sampleData.methodCode == null) {
                value = null;
                parameter = null;
                analyte = null;
                continue;
            }

            sb.append(precisionType).append(delim).append(action.substring(0, 1)).append(delim);
            if (replicate)
                sb.append(replicateAssessmentType);
            else
                sb.append(duplicateAssessmentType);
            sb.append(delim);
            if (sampleData.orgId != null && agencyCodes.get(sampleData.orgId) != null)
                sb.append(agencyCodes.get(sampleData.orgId));
            sb.append(delim)
              .append(sampleData.stateCode)
              .append(delim)
              .append(sampleData.countyCode)
              .append(delim)
              .append(sampleData.siteId)
              .append(delim)
              .append(parameter)
              .append(delim)
              .append(sampleData.poc)
              .append(delim)
              .append(sampleData.date)
              .append(delim)
              .append(assessmentNumber)
              .append(delim)
              .append(sampleData.methodCode)
              .append(delim)
              .append(sampleData.reportedUnit)
              .append(delim)
              .append(value)
              .append(delim)
              .append(delim)
              .append(delim)
              .append(delim);
            if (addAll) {
                if ( !value.contains("<"))
                    tmnoc += Double.parseDouble(value);
            }
            precisions.add(sb.toString());
            sb.setLength(0);
            value = null;
            parameter = null;
            analyte = null;
        }

        if (addAll) {
            precisions.add(getAirToxicsSummedPercisionString(tmnoc));
        } else if (addTds) {
            precisions.add(getAirToxicsSummedPercisionString(ttds));
        }

        return precisions;
    }

    /**
     * creates air toxics precision string for the summed results
     */
    private String getAirToxicsSummedPercisionString(Double value) {
        String parameter;
        StringBuffer sb;

        if (value.doubleValue() == 0)
            return null;
        sb = new StringBuffer();
        /*
         * in specific cases, a string needs to be created for a new analyte
         */
        parameter = null;
        if (addAll) {
            parameter = tnmocParameterCode;
        } else if (addTds) {
            parameter = ttdsParameterCode;
        }
        sb.append(precisionType).append(delim).append(action.substring(0, 1)).append(delim);
        if (replicate)
            sb.append(replicateAssessmentType);
        else
            sb.append(duplicateAssessmentType);
        sb.append(delim);
        if (sampleData.orgId != null && agencyCodes.get(sampleData.orgId) != null)
            sb.append(agencyCodes.get(sampleData.orgId));
        sb.append(delim)
          .append(sampleData.stateCode)
          .append(delim)
          .append(sampleData.countyCode)
          .append(delim)
          .append(sampleData.siteId)
          .append(delim)
          .append(parameter)
          .append(delim)
          .append(sampleData.poc)
          .append(delim)
          .append(sampleData.date)
          .append(delim)
          .append(assessmentNumber)
          .append(delim);
        if (addAll)
            sb.append(tnmocMethodCode).append(delim);
        else if (addTds)
            sb.append(ttdsMethodCode).append(delim);
        sb.append(sampleData.reportedUnit).append(delim);
        if (addAll)
            sb.append(truncateDecimal(value, 2));
        else if (addTds)
            sb.append(truncateDecimal(value, 4));
        sb.append(delim).append(delim).append(delim).append(delim);
        return sb.toString();
    }

    /**
     * Get metals air quality strings for sample
     */
    private int getMetalsStrings(SampleManager1 sm, Integer testId, Integer analysisId,
                                 boolean pressureTempString) throws Exception {
        String sampleType, poc, reportedUnit;

        reportedUnit = null;

        /*
         * replicate, performance evaluation, and cutting blank samples do not
         * produce strings
         */
        if (sm.getSample().getClientReference() == null ||
            "r".equalsIgnoreCase(sm.getSample().getClientReference().substring(2, 3)) ||
            "pep".equalsIgnoreCase(sm.getSample().getClientReference().substring(0, 3)) ||
            "qcb".equalsIgnoreCase(sm.getSample().getClientReference().substring(0, 3)))
            return 0;

        /*
         * The POC for metal strings is based on the client reference
         */
        poc = null;
        sampleType = sm.getSample().getClientReference().substring(2, 3);
        if ("p".equalsIgnoreCase(sampleType))
            poc = primaryPOC;
        else if ("s".equalsIgnoreCase(sampleType))
            poc = secondaryPOC;
        else if ("d".equalsIgnoreCase(sampleType))
            poc = duplicatePOC;

        for (AnalysisViewDO data : getAnalyses(sm)) {
            if (data.getId().equals(analysisId))
                reportedUnit = unitCodes.get(data.getUnitOfMeasureId());
        }

        /*
         * if the POC is null, then the client reference string was not in the
         * correct format or the aux data was left empty
         */
        if (poc == null)
            return 0;

        if (metalRawStrings.get(sampleData.key) == null)
            metalRawStrings.put(sampleData.key, new ArrayList<String>());
        if (pressureTempString == false) {
            getPressureTemperatureStrings(poc);
        }
        return getMetalsSampleString(sm, testId, poc, reportedUnit, analysisId);
    }

    /**
     * create strings for pressure and temperature data for metal samples
     */
    private void getPressureTemperatureStrings(String poc) {
        StringBuilder psb, tsb;

        psb = new StringBuilder();
        tsb = new StringBuilder();
        buildSiteInfo(psb, rawDataType, pressureParameter, poc);
        buildSiteInfo(tsb, rawDataType, temperatureParameter, poc);
        psb.append(pressureDurationCode)
           .append(delim)
           .append(pressureReportedUnit)
           .append(delim)
           .append(pressureMethodCode)
           .append(delim)
           .append(sampleData.date)
           .append(delim)
           .append(time)
           .append(delim);
        tsb.append(temperatureDurationCode)
           .append(delim)
           .append(temperatureReportedUnit)
           .append(delim)
           .append(temperatureMethodCode)
           .append(delim)
           .append(sampleData.date)
           .append(delim)
           .append(time)
           .append(delim);
        if ( !DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
            if (DataBaseUtil.isEmpty(sampleData.pressure))
                psb.append(delim).append(sampleData.nullDataCd).append(delim);
            else
                psb.append(sampleData.pressure).append(delim).append(delim);
            if (DataBaseUtil.isEmpty(sampleData.temperature))
                tsb.append(delim).append(sampleData.nullDataCd).append(delim);
            else
                tsb.append(sampleData.temperature).append(delim).append(delim);
        } else {
            psb.append(sampleData.pressure).append(delim).append(delim);
            tsb.append(sampleData.temperature).append(delim).append(delim);
        }
        psb.append(sampleData.collectionFreq)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim);
        tsb.append(sampleData.collectionFreq)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim)
           .append(delim);

        if (sampleData.pressure != null || !DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
            if (pressureStrings.get(sampleData.key) == null)
                pressureStrings.put(sampleData.key, new ArrayList<String>());
            pressureStrings.get(sampleData.key).add(psb.toString());
        }
        if (sampleData.temperature != null || !DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
            if (temperatureStrings.get(sampleData.key) == null)
                temperatureStrings.put(sampleData.key, new ArrayList<String>());
            temperatureStrings.get(sampleData.key).add(tsb.toString());
        }
    }

    /**
     * create a string for metal raw data
     */
    private int getMetalsSampleString(SampleManager1 sm, Integer testId, String poc,
                                      String reportedUnit, Integer analysisId) {
        boolean cbFlag;
        MetalTestType type;
        int durationCode, methodCode;
        Double sampleMdl, filterMdl, sampleValue, sampleVolume;
        Integer spf;
        String value, mdl, parameter;
        StringBuilder sb;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits, twoDecimals, fourDecimals;

        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);
        twoDecimals = new DecimalFormat("#");
        twoDecimals.setMaximumFractionDigits(2);
        twoDecimals.setMinimumIntegerDigits(1);
        fourDecimals = new DecimalFormat("#");
        fourDecimals.setMaximumFractionDigits(4);
        fourDecimals.setMinimumIntegerDigits(1);

        type = null;
        cbFlag = false;
        if (testId.equals(metalLeadTest)) {
            type = MetalTestType.Lead;
        } else if (testId.equals(metalManganeseTest)) {
            type = MetalTestType.Manganese;
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            type = MetalTestType.MnAccuracy;
        } else {
            return 0;
        }

        value = mdl = parameter = null;
        durationCode = methodCode = 0;
        sb = new StringBuilder();
        for (ResultViewDO data : getResults(sm)) {
            if ( !DataBaseUtil.isSame(data.getAnalysisId(), analysisId) ||
                ("N".equals(data.getIsReportable())))
                continue;
            if (DataBaseUtil.isSame(data.getIsColumn(), "N")) {
                value = data.getValue();
                parameter = analyteCodes.get(data.getAnalyteId());
                ap = analyteParameters.get(data.getAnalyteId());
                methodCode = ap.getP2().intValue();
                durationCode = ap.getP3().intValue();
            } else if (DataBaseUtil.isSame(data.getAnalyte(), mdlAnalyte)) {
                mdl = data.getValue();
            }
            if ( (value == null || mdl == null) && DataBaseUtil.isEmpty(sampleData.nullDataCd))
                continue;

            buildSiteInfo(sb, rawDataType, parameter, poc);
            sb.append(durationCode).append(delim);

            /*
             * Manganese uses a different unit code but uses the same unit, so
             * it has to be set separately
             */
            if (type.equals(MetalTestType.Lead))
                sb.append(reportedUnit);
            else
                sb.append(mnUnitCode);
            sb.append(delim)
              .append(threeDigits.format(methodCode))
              .append(delim)
              .append(sampleData.date)
              .append(delim)
              .append(time)
              .append(delim);
            if (DataBaseUtil.isEmpty(sampleData.nullDataCd)) {
                filterMdl = Double.parseDouble(mdl);
                sampleValue = Double.parseDouble(value);
                sampleVolume = Double.parseDouble(sampleData.volume);
                spf = Integer.parseInt(sampleData.stripsPerFilter);
                sampleMdl = 0.0;

                if (type.equals(MetalTestType.Lead)) {
                    sampleMdl = filterMdl * spf / sampleVolume;
                } else {
                    sampleMdl = 1000 * (filterMdl / sampleVolume);
                    if (type.equals(MetalTestType.Manganese) &&
                        metalManganeseFilterLotBlank - sampleMdl > 0)
                        cbFlag = true;
                }

                if (type.equals(MetalTestType.Lead)) {
                    /*
                     * replace the sample value with the calculated sample
                     * result
                     */
                    sampleValue = ( (sampleValue * spf) - metalLeadFilterLotBlank) / sampleVolume;
                    sb.append(truncateDecimal(sampleValue, 3)).append(delim).append(delim);
                } else if (cbFlag) {
                    sampleValue = 1000 * ( (sampleValue * spf) - metalManganeseFilterLotBlank) /
                                  sampleVolume;
                    sb.append(truncateDecimal(sampleValue, 2)).append(delim).append(delim);
                } else {
                    sampleValue = 1000 * sampleValue * spf / sampleVolume;
                    sb.append(truncateDecimal(sampleValue, 2)).append(delim).append(delim);
                }
                sb.append(sampleData.collectionFreq).append(delim).append(delim);

                if (type.equals(MetalTestType.Lead)) {
                    if (sampleValue < sampleMdl)
                        sb.append(md);
                    sb.append(delim);
                    if (sampleMdl < sampleValue && sampleValue < (10 * sampleMdl))
                        sb.append(sq);
                    sb.append(delim).append(delim).append(delim).append(delim);
                } else {
                    if (cbFlag)
                        sb.append(cb);
                    sb.append(delim);
                    if (sampleValue < sampleMdl)
                        sb.append(md);
                    sb.append(delim);
                    if (sampleMdl < sampleValue && sampleValue < (10 * sampleMdl))
                        sb.append(sq);
                    sb.append(delim);
                    if (sampleValue < 0 && Math.abs(sampleValue) > sampleMdl)
                        sb.append(da);
                    sb.append(delim);
                    if (sampleValue > 10 * sampleMdl &&
                        sm.getSample().getClientReference() != null &&
                        (sm.getSample().getClientReference().contains("qtb") || sm.getSample()
                                                                                  .getClientReference()
                                                                                  .contains("qfb"))) {
                        sb.append(fb);
                    }
                    sb.append(delim);
                }
                sb.append(delim).append(delim).append(delim).append(delim).append(delim);
                if (type.equals(MetalTestType.Lead))
                    sb.append(fourDecimals.format(sampleMdl)).append(delim);
                else
                    sb.append(twoDecimals.format(sampleMdl)).append(delim);
                if (type.equals(MetalTestType.Manganese)) {
                    sb.append(truncateDecimal(1000 * metalManganeseFilterLotBlank / sampleVolume, 2));
                }
                break;
            } else {
                sb.append(delim)
                  .append(sampleData.nullDataCd)
                  .append(delim)
                  .append(sampleData.collectionFreq)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim);
                break;
            }

        }
        if (metalRawStrings.get(sampleData.key) == null)
            metalRawStrings.put(sampleData.key, new ArrayList<String>());
        metalRawStrings.get(sampleData.key).add(sb.toString());
        return 1;
    }

    /**
     * truncate a double to the given number of digits after the decimal
     */
    private static String truncateDecimal(double x, int numberofDecimals) {
        DecimalFormat df;
        df = new DecimalFormat();
        df.setMaximumFractionDigits(numberofDecimals);
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(x);
    }

    /**
     * get a dictionary list of terms for a given system name
     */
    private ArrayList<OptionListItem> getDictionaryList(String systemName) throws Exception {
        ArrayList<OptionListItem> l;
        ArrayList<DictionaryDO> d;

        l = new ArrayList<OptionListItem>();
        try {
            d = categoryCache.getBySystemName(systemName).getDictionaryList();
            for (DictionaryDO data : d)
                l.add(new OptionListItem(data.getId().toString(), data.getEntry()));
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Failed retrieve dictionary list for system name:" + systemName,
                    e);
            throw e;
        }

        return l;
    }

    /**
     * get the external analyte codes for the valid analyses
     */
    private void getAnalyteCodes(Set<Integer> analyteIds) throws Exception {
        ArrayList<Integer> profileIds;

        analyteCodes = new HashMap<Integer, String>();

        if (analyteIds.size() < 1)
            return;

        profileIds = new ArrayList<Integer>();
        profileIds.add(Constants.dictionary().PROFILE_AIR_STRING);

        for (ExchangeExternalTermViewDO data : exchangeExternalTerm.fetchByReferenceTableIdReferenceIdsProfileIds(Constants.table().ANALYTE,
                                                                                                                  DataBaseUtil.toArrayList(analyteIds),
                                                                                                                  profileIds)) {
            analyteCodes.put(data.getExchangeLocalTermReferenceId(), data.getExternalTerm());
        }
    }

    /**
     * get the unit codes for the valid analyses
     */
    private void getUnitCodes(Set<Integer> unitIds) throws Exception {
        ArrayList<Integer> profileIds;

        unitCodes = new HashMap<Integer, String>();

        if (unitIds.size() < 1)
            return;

        profileIds = new ArrayList<Integer>();
        profileIds.add(Constants.dictionary().PROFILE_AIR_STRING);

        for (ExchangeExternalTermViewDO data : exchangeExternalTerm.fetchByReferenceTableIdReferenceIdsProfileIds(Constants.table().DICTIONARY,
                                                                                                                  DataBaseUtil.toArrayList(unitIds),
                                                                                                                  profileIds)) {
            unitCodes.put(data.getExchangeLocalTermReferenceId(), data.getExternalTerm());
        }
    }

    /**
     * get the agengy codes for the valid sections/orgs
     */
    private void getAgencyCodes(Set<Integer> orgIds) throws Exception {
        ArrayList<Integer> profileIds;

        if (orgIds.size() < 1)
            return;

        agencyCodes = new HashMap<Integer, String>();
        profileIds = new ArrayList<Integer>();
        profileIds.add(Constants.dictionary().PROFILE_AIR_STRING);

        for (ExchangeExternalTermViewDO data : exchangeExternalTerm.fetchByReferenceTableIdReferenceIdsProfileIds(Constants.table().ORGANIZATION,
                                                                                                                  DataBaseUtil.toArrayList(orgIds),
                                                                                                                  profileIds)) {
            agencyCodes.put(data.getExchangeLocalTermReferenceId(), data.getExternalTerm());
        }
    }

    /**
     * get the analyte parameters for the tests in the valid analyses. Tries to
     * find a parameter with a begin date before the current date and an end
     * date after the current date. If one is not found, the first parameter is
     * set for that analyte.
     */
    private void getAnalyteParameters(Set<Integer> testIds) throws Exception {
        Calendar cal;
        Datetime date;
        analyteParameters = new HashMap<Integer, AnalyteParameterViewDO>();

        if (testIds.size() < 1)
            return;

        cal = Calendar.getInstance();
        date = new Datetime(Datetime.YEAR, Datetime.SECOND, cal.getTime());
        for (Integer testId : testIds) {
            for (AnalyteParameterViewDO data : analyteParameter.fetchByReferenceIdReferenceTableId(testId,
                                                                                                   Constants.table().TEST)) {
                if (DataBaseUtil.isAfter(date, data.getActiveBegin()) &&
                    DataBaseUtil.isAfter(data.getActiveEnd(), date))
                    analyteParameters.put(data.getAnalyteId(), data);
                else if (analyteParameters.get(data.getAnalyteId()) == null) {
                    analyteParameters.put(data.getAnalyteId(), data);
                }
            }
        }
    }

    /**
     * creates a string for the beginning of an air quality string (all strings
     * have these fields)
     */
    private void buildSiteInfo(StringBuilder sb, String type, String parameter, String poc) {
        sb.append(type)
          .append(delim)
          .append(action.substring(0, 1))
          .append(delim)
          .append(sampleData.stateCode)
          .append(delim)
          .append(sampleData.countyCode)
          .append(delim)
          .append(sampleData.siteId)
          .append(delim)
          .append(parameter)
          .append(delim)
          .append(poc)
          .append(delim);
    }

    /**
     * writes the strings to a file
     */
    private boolean writeFile(HashMap<String, ArrayList<String>> map, String fileName,
                              String location) throws Exception {
        boolean fileWritten;
        File file;
        BufferedWriter writer;

        fileWritten = false;
        for (String key : map.keySet()) {
            if (map.get(key).size() > 0) {
                fileWritten = true;
                file = File.createTempFile(fileName + key + "_", ".txt", new File(location));
                writer = new BufferedWriter(new FileWriter(file));
                for (String s : map.get(key)) {
                    writer.write(s);
                    writer.newLine();
                }

                writer.close();
            }
        }
        return fileWritten;
    }

    /**
     * store sample data used in string generation
     */
    private class SampleData {
        public Integer                 orgId;
        public SampleManager1          sm;
        public HashMap<String, String> auxData;
        public String                  volume, pocSulfate, pocNitrate, nullDataCd, stateCode,
                        countyCode, siteId, poc, date, collectionFreq, key, pressure, temperature,
                        stripsPerFilter, reportedUnit, methodCode, durationCode;
        public ArrayList<ResultViewDO> results;

        public SampleData(SampleManager1 sm) throws Exception {
            this.sm = sm;
            auxData = auxDataHelper.fillAirQualityAuxData(sm);
            nullDataCd = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
            stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
            countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
            siteId = auxData.get(AuxDataHelperBean.SITE_ID);
            poc = null;
            collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);
            pocSulfate = auxData.get(AuxDataHelperBean.POC_SULFATE);
            pocNitrate = auxData.get(AuxDataHelperBean.POC_NITRATE);
            volume = auxData.get(AuxDataHelperBean.VOLUME);
            pressure = auxData.get(AuxDataHelperBean.PRESSURE);
            temperature = auxData.get(AuxDataHelperBean.TEMPERATURE);
            stripsPerFilter = auxData.get(AuxDataHelperBean.STRIPS_PER_FILTER);
            date = ReportUtil.toString(sm.getSample().getCollectionDate(),
                                       Messages.get().dateCompressedPattern());
            key = stateCode + "_" + countyCode + "_" + siteId;
        }

        public void groupResults(Integer testId, boolean processReplicate) {
            boolean isReplicate;
            ArrayList<Integer> analysisIds;

            reportedUnit = null;
            orgId = null;
            methodCode = null;
            durationCode = null;
            results = new ArrayList<ResultViewDO>();
            analysisIds = new ArrayList<Integer>();
            if (getAnalyses(sm) != null) {
                for (AnalysisViewDO analysis : getAnalyses(sm)) {
                    isReplicate = false;
                    if ( !DataBaseUtil.isDifferent(Constants.dictionary().ANALYSIS_RELEASED,
                                                   analysis.getStatusId())) {
                        if (getAnalysisQAs(sm) != null) {
                            for (AnalysisQaEventViewDO qa : getAnalysisQAs(sm)) {
                                if (qa.getAnalysisId().equals(analysis.getId()) &&
                                    "replicate".equals(qa.getQaEventName())) {
                                    isReplicate = true;
                                    if (processReplicate)
                                        replicate = true;
                                }
                            }
                        }
                        if ( ( (processReplicate && isReplicate) || ( !processReplicate && !isReplicate)) &&
                            testId.equals(analysis.getTestId())) {
                            analysisIds.add(analysis.getId());
                            if (analysis.getMethodName().contains(to11)) {
                                addTds = true;
                            } else if (analysis.getMethodName().contains(to12)) {
                                addAll = true;
                            }
                            setPoc();
                            if (reportedUnit == null)
                                reportedUnit = unitCodes.get(analysis.getUnitOfMeasureId());
                            if (orgId == null)
                                orgId = sectionMap.get(analysis.getSectionId());
                            if (methodCode == null)
                                methodCode = methodCodes.get(analysis.getTestId());
                            if (durationCode == null)
                                durationCode = durationCodes.get(testId);
                        }
                    }
                }
            }
            if (getResults(sm) != null) {
                for (ResultViewDO result : getResults(sm)) {
                    if ("Y".equals(result.getIsReportable())) {
                        if (analysisIds.contains(result.getAnalysisId())) {
                            results.add(result);
                        }
                    }
                }
            }
        }

        public void setPoc() {
            if (addTds) {
                poc = auxData.get(AuxDataHelperBean.POC_TO11);
            } else if (addAll) {
                poc = auxData.get(AuxDataHelperBean.POC_TO12);
            } else {
                poc = auxData.get(AuxDataHelperBean.POC_TO15);
            }
        }
    }

    /**
     * clears the global variables before the bean returns
     */
    private void clearVariables() {
        addAll = null;
        addTds = null;
        replicate = null;
        ttdsDurationCode = null;
        ttdsMethodCode = null;
        ttdsParameterCode = null;
        tnmocDurationCode = null;
        tnmocMethodCode = null;
        tnmocParameterCode = null;
        pressureParameter = null;
        pressureDurationCode = null;
        pressureReportedUnit = null;
        pressureMethodCode = null;
        temperatureParameter = null;
        temperatureDurationCode = null;
        temperatureReportedUnit = null;
        temperatureMethodCode = null;
        action = null;
        metalLeadFilterLotBlank = null;
        metalManganeseFilterLotBlank = null;
        sulfateAnalytes = null;
        nitrateAnalytes = null;
        sectionMap = null;
        sampleData = null;
        analyteCodes = null;
        unitCodes = null;
        methodCodes = null;
        durationCodes = null;
        agencyCodes = null;
        analyteParameters = null;
        airToxicRawStrings = null;
        airToxicPrecisionStrings = null;
        metalRawStrings = null;
        pressureStrings = null;
        temperatureStrings = null;
        metalLeadTest = null;
        metalManganeseTest = null;
        metalManganeseAccuracyTest = null;
    }
}
