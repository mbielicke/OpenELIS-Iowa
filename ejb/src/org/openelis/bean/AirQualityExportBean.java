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

import static org.openelis.manager.SampleManager1Accessor.getAnalyses;
import static org.openelis.manager.SampleManager1Accessor.getResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private static final String                      delim           = "|", escape = "\\",
                    rawDataType = "RD", precisionType = "QA", time = "00:00",
                    assessmentType = "Duplicate", assessmentNumber = "1", to11 = "to-11",
                    to12 = "to-12", tolualdehyde = "Tolualdehyde", md = "MD", sq = "SQ", cb = "CB",
                    da = "DA", fb = "FB", primaryPOC = "01", secondaryPOC = "02",
                    duplicatePOC = "03", mdlAnalyte = "MDL";

    private String                                   ttdsDurationCode, ttdsMethodCode, ttdsMDL,
                    ttdsParameterCode, tnmocDurationCode, tnmocMethodCode, tnmocMDL,
                    tnmocParameterCode, pressureParameter, pressureDurationCode,
                    pressureReportedUnit, pressureMethodCode, temperatureParameter,
                    temperatureDurationCode, temperatureReportedUnit, temperatureMethodCode;

    private static final Double                      nitrateConstant = 4.4;

    private Double                                   metalLeadFilterLotBlank,
                    metalManganeseFilterLotBlank;

    private ArrayList<String>                        sulfateAnalytes, nitrateAnalytes;

    private HashMap<Integer, String>                 analyteCodes, unitCodes;

    private HashMap<Integer, AnalyteParameterViewDO> analyteParameters;

    private HashMap<String, ArrayList<String>>       airToxicRawStrings, airToxicPrecisionStrings,
                    metalRawStrings, pressureStrings, temperatureStrings;

    private Integer                                  metalLeadTest, metalManganeseTest,
                    metalManganeseAccuracyTest;

    private boolean                                  replicate;

    private static final Logger                      log             = Logger.getLogger("openelis");

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
                                                         .setMutiSelect(false)
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
        Integer analysisCount;
        ReportStatus status;
        String val, frDate, tDate, accession, action, reportTo, attention, tempArray[], analyteArray[], qualifierCode, airDir, metal, errorMessage;
        Datetime d;
        SimpleDateFormat dateTimeFormat;
        QueryData field;
        Query query;
        ArrayList<SampleManager1> sms;
        ArrayList<QueryData> fields;
        ArrayList<String> qualifierStrings;
        ArrayList<Integer> sulfateNitrateTests, airToxicsTests;
        HashMap<Integer, Datetime> problemSamples;
        HashMap<String, ArrayList<SampleManager1>> samples;
        HashMap<String, ArrayList<String>> speciationStrings, stringList;
        HashMap<String, QueryData> param;
        HashSet<Integer> analyteIds, unitIds, testIds;

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
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("air_report_to"),
                    e);
            throw e;
        }

        airDir = null;
        try {
            /*
             * Location that all files are output to.
             */
            airDir = systemVariable.fetchByName("air_report_directory").getValue();
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_report_directory"),
                    e);
            throw e;
        }

        try {
            /*
             * Test ids and analyte names for sulfate and nitrate string
             * creation.
             * 
             * Format: "test id";"sulfate analyte";"nitrate analyte"|repeat if
             * necessary
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
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_sulfate_nitrate_tests"),
                    e);
            throw e;
        }

        try {
            /*
             * Test ids for air toxics tests.
             * 
             * Format: "test id"|"test id"|repeating
             */
            val = systemVariable.fetchByName("air_toxics_tests").getValue();
            tempArray = val.split(escape + delim);
            airToxicsTests = new ArrayList<Integer>();
            for (int i = 0; i < tempArray.length; i++ ) {
                airToxicsTests.add(Integer.parseInt(tempArray[i]));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("air_toxics_tests"),
                    e);
            throw e;
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
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_metal_lead_test"),
                    e);
            throw e;
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
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_metal_manganese_test"),
                    e);
            throw e;
        }

        try {
            /*
             * Test id for metal manganese accuracy test.
             */
            metalManganeseAccuracyTest = Integer.parseInt(systemVariable.fetchByName("air_metal_mn_accuracy_test")
                                                                        .getValue());
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_metal_mn_accuracy_test"),
                    e);
            throw e;
        }

        try {
            /*
             * Constant data for "Total Tolualdehydes" analyte.
             * 
             * Format: "parameter code"|"duration code"|"method code"|"MDL"
             * 
             * MDL is optional
             */
            val = systemVariable.fetchByName("air_total_tolualdehydes").getValue();
            tempArray = val.split(escape + delim);
            ttdsParameterCode = tempArray[0];
            ttdsDurationCode = tempArray[1];
            ttdsMethodCode = tempArray[2];
            try {
                ttdsMDL = tempArray[3];
            } catch (ArrayIndexOutOfBoundsException e) {
                ttdsMDL = "";
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_total_tolualdehydes"),
                    e);
            throw e;
        }

        try {
            /*
             * Constant data for "TNMOC,Speciated" analyte.
             * 
             * Format: "parameter code"|"duration code"|"method code"|"MDL"
             * 
             * MDL is optional
             */
            val = systemVariable.fetchByName("air_tnmoc_speciated").getValue();
            tempArray = val.split(escape + delim);
            tnmocParameterCode = tempArray[0];
            tnmocDurationCode = tempArray[1];
            tnmocMethodCode = tempArray[2];
            try {
                tnmocMDL = tempArray[3];
            } catch (ArrayIndexOutOfBoundsException e) {
                tnmocMDL = "";
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_tnmoc_speciated"),
                    e);
            throw e;
        }

        try {
            /*
             * Qualifier code constants for air toxics "under limit" qualifier
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
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_qualifier_code"),
                    e);
            throw e;
        }

        try {
            /*
             * Constants for pressure strings.
             * 
             * Format:
             * "parameter code"|"duration code"|"reported unit code"|"method code"
             */
            val = systemVariable.fetchByName("air_pressure_constants").getValue();
            tempArray = val.split(escape + delim);
            pressureParameter = tempArray[0];
            pressureDurationCode = tempArray[1];
            pressureReportedUnit = tempArray[2];
            pressureMethodCode = tempArray[3];
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_pressure_constants"),
                    e);
            throw e;
        }

        try {
            /*
             * Constants for temperature strings.
             * 
             * Format:
             * "parameter code"|"duration code"|"reported unit code"|"method code"
             */
            val = systemVariable.fetchByName("air_temperature_constants").getValue();
            tempArray = val.split(escape + delim);
            temperatureParameter = tempArray[0];
            temperatureDurationCode = tempArray[1];
            temperatureReportedUnit = tempArray[2];
            temperatureMethodCode = tempArray[3];
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_temperature_constants"),
                    e);
            throw e;
        }

        status = new ReportStatus();

        /*
         * recover all the params and build a specific where clause
         */
        param = ReportUtil.getMapParameter(paramList);

        frDate = ReportUtil.getSingleParameter(param, "FROM_DATE");
        tDate = ReportUtil.getSingleParameter(param, "TO_DATE");
        accession = ReportUtil.getSingleParameter(param, "ACCESSION");
        action = ReportUtil.getSingleParameter(param, "ACTION");
        action = dictionary.fetchById(Integer.parseInt(action)).getEntry();

        if (DataBaseUtil.isEmpty(action))
            throw new InconsistencyException(Messages.get().airQuality_noActionException());

        if (frDate == null || tDate == null) {
            if (accession == null)
                /*
                 * This is the case where there is not enough data to query for
                 * samples. One or both of the dates is missing, and the
                 * accession number is missing.
                 */
                throw new InconsistencyException(Messages.get().airQuality_noDataException());

            sms = new ArrayList<SampleManager1>();
            sms.add(sample.fetchByAccession(Integer.parseInt(accession),
                                            SampleManager1.Load.AUXDATA,
                                            SampleManager1.Load.RESULT));
        } else if (accession == null) {
            fields = new ArrayList<QueryData>();
            field = new QueryData();
            field.setQuery(DataBaseUtil.concatWithSeparator(frDate, "..", tDate));
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
                                          SampleManager1.Load.RESULT);
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

        samples = new HashMap<String, ArrayList<SampleManager1>>();
        analyteIds = new HashSet<Integer>();
        unitIds = new HashSet<Integer>();
        testIds = new HashSet<Integer>();
        problemSamples = new HashMap<Integer, Datetime>();
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");

        /*
         * separate the samples for each site, find duplicate samples, and find
         * problem samples
         */
        for (SampleManager1 sm : sms) {
            analysisCount = 0;
            if (DataBaseUtil.isDifferent(Constants.dictionary().SAMPLE_RELEASED, sm.getSample()
                                                                                   .getStatusId())) {
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
                if (data.getIsColumn().equals("N"))
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
         * get the air quality codes corresponding with the analytes and units
         * that are used
         */
        try {
            getAnalyteCodes(analyteIds);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new Exception("There was a problem retrieving analyte codes");
        }

        try {
            getUnitCodes(unitIds);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new Exception("There was a problem retrieving unit codes");
        }

        try {
            getAnalyteParameters(testIds);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            throw new Exception("There was a problem retrieving analyte parameters");
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
                pressureTempString = false;
                if (getResults(sm) != null) {
                    for (AnalysisViewDO data : getAnalyses(sm)) {
                        if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
                            continue;
                        if (sulfateNitrateTests.contains(data.getTestId())) {
                            try {
                                stringList = getSulfateNitrateStrings(sm, action, data.getId());
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
                            for (String key : stringList.keySet()) {
                                if (speciationStrings.get(key) == null)
                                    speciationStrings.put(key, new ArrayList<String>());
                                speciationStrings.get(key).addAll(stringList.get(key));
                            }
                        } else if (airToxicsTests.contains(data.getTestId())) {
                            try {
                                stringList = getAirToxicsStrings(sm,
                                                                 data.getTestId(),
                                                                 action,
                                                                 qualifierCode,
                                                                 qualifierStrings,
                                                                 data.getId());
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
                            for (String key : stringList.keySet()) {
                                if (airToxicRawStrings.get(key) == null)
                                    airToxicRawStrings.put(key, new ArrayList<String>());
                                airToxicRawStrings.get(key).addAll(stringList.get(key));
                            }
                            /*
                             * there should only be one set of raw data strings
                             * for a replicate air toxics sample
                             */
                            if (replicate) {
                                replicate = false;
                                break;
                            }
                        } else if (metalLeadTest.equals(data.getTestId())) {
                            try {
                                stringList = getMetalsStrings(sm,
                                                              data.getTestId(),
                                                              action,
                                                              data.getId(),
                                                              pressureTempString);
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

                            for (String key : stringList.keySet()) {
                                for (int i = 0; i < stringList.get(key).size(); i++ ) {
                                    metal = stringList.get(key).get(i);
                                    if (DataBaseUtil.isEmpty(metal))
                                        continue;
                                    if (pressureTempString == false && i == 0) {
                                        if (pressureStrings.get(key) == null)
                                            pressureStrings.put(key, new ArrayList<String>());
                                        pressureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (pressureTempString == false && i == 1) {
                                        if (temperatureStrings.get(key) == null)
                                            temperatureStrings.put(key, new ArrayList<String>());
                                        temperatureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (metal.substring(0, 2).equals(rawDataType)) {
                                        if (metalRawStrings.get(key) == null)
                                            metalRawStrings.put(key, new ArrayList<String>());
                                        metalRawStrings.get(key).add(metal);
                                    }
                                }
                            }
                            pressureTempString = true;
                        } else if (metalManganeseAccuracyTest.equals(data.getTestId())) {
                            try {
                                stringList = getMetalsStrings(sm,
                                                              data.getTestId(),
                                                              action,
                                                              data.getId(),
                                                              pressureTempString);
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
                            for (String key : stringList.keySet()) {
                                for (int i = 0; i < stringList.get(key).size(); i++ ) {
                                    metal = stringList.get(key).get(i);
                                    if (DataBaseUtil.isEmpty(metal))
                                        continue;
                                    if (pressureTempString == false && i == 0) {
                                        if (pressureStrings.get(key) == null)
                                            pressureStrings.put(key, new ArrayList<String>());
                                        pressureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (pressureTempString == false && i == 1) {
                                        if (temperatureStrings.get(key) == null)
                                            temperatureStrings.put(key, new ArrayList<String>());
                                        temperatureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (metal.substring(0, 2).equals(rawDataType)) {
                                        if (metalRawStrings.get(key) == null)
                                            metalRawStrings.put(key, new ArrayList<String>());
                                        metalRawStrings.get(key).add(metal);
                                    }
                                }
                            }
                            pressureTempString = true;
                        } else if (metalManganeseTest.equals(data.getTestId())) {
                            try {
                                stringList = getMetalsStrings(sm,
                                                              data.getTestId(),
                                                              action,
                                                              data.getId(),
                                                              pressureTempString);
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
                            for (String key : stringList.keySet()) {
                                for (int i = 0; i < stringList.get(key).size(); i++ ) {
                                    metal = stringList.get(key).get(i);
                                    if (DataBaseUtil.isEmpty(metal))
                                        continue;
                                    if (pressureTempString == false && i == 0) {
                                        if (pressureStrings.get(key) == null)
                                            pressureStrings.put(key, new ArrayList<String>());
                                        pressureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (pressureTempString == false && i == 1) {
                                        if (temperatureStrings.get(key) == null)
                                            temperatureStrings.put(key, new ArrayList<String>());
                                        temperatureStrings.get(key).add(metal);
                                        continue;
                                    }
                                    if (metal.substring(0, 2).equals(rawDataType)) {
                                        if (metalRawStrings.get(key) == null)
                                            metalRawStrings.put(key, new ArrayList<String>());
                                        metalRawStrings.get(key).add(metal);
                                    }
                                }
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
        fileWritten = false;
        for (String key : speciationStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("speciation_" + key + "_", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : speciationStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : airToxicRawStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("airtoxics_raw_" + key + "_", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : airToxicRawStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : airToxicPrecisionStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("airtoxics_precision_" + key + "_",
                                            ".txt",
                                            new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : airToxicPrecisionStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : metalRawStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("metals_raw_" + key + "_", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : metalRawStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : pressureStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("pressure_" + key + "_", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : pressureStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : temperatureStrings.keySet()) {
            fileWritten = true;
            File file = File.createTempFile("temperature_" + key + "_", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : temperatureStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        /*
         * if there are any problem samples, throw an exception to let the user
         * know the accession numbers
         */
        if (problemSamples.size() > 0) {
            errorMessage = "Strings were not created for the following samples:\n";
            for (Integer i : problemSamples.keySet()) {
                d = problemSamples.get(i);
                errorMessage += i + " (" + dateTimeFormat.format(d.getDate()) + ")\n";
            }
            if (fileWritten)
                errorMessage += "Files were written to: " + airDir;
            status.setMessage(errorMessage);
            throw new Exception(errorMessage);
        }
        if (fileWritten)
            status.setMessage("Files were written to: " + airDir);
        return status;
    }

    /**
     * Get sulfate and nitrate air quality strings for sample
     */
    private HashMap<String, ArrayList<String>> getSulfateNitrateStrings(SampleManager1 sm,
                                                                        String action,
                                                                        Integer analysisId) throws Exception {
        int sulfateDurationCd, nitrateDurationCd, sulfateMethodCd, nitrateMethodCd;
        String sulfateValue, nitrateValue, sulfateCode, nitrateCode, stateCode, countyCode, siteId, poc, volume, nullDataCd, collectionFreq, date, reportedUnit, sulfateAlternateMethodDetectableLimit, nitrateAlternateMethodDetectableLimit, key;
        StringBuilder ssb, nsb;
        SimpleDateFormat dateTimeFormat;
        AnalyteParameterViewDO sulfateParameter, nitrateParameter;
        DecimalFormat threeDigits;
        HashMap<String, String> auxData;
        HashMap<String, ArrayList<String>> sulfateNitrateStrings;

        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);
        sulfateValue = nitrateValue = sulfateCode = nitrateCode = reportedUnit = sulfateAlternateMethodDetectableLimit = nitrateAlternateMethodDetectableLimit = null;
        sulfateDurationCd = nitrateDurationCd = sulfateMethodCd = nitrateMethodCd = 0;
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");

        /*
         * get auxiliary data from the sample
         */
        auxData = auxDataHelper.fillAirQualityAuxData(sm);
        volume = auxData.get(AuxDataHelperBean.VOLUME);
        nullDataCd = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
        stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
        countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
        siteId = auxData.get(AuxDataHelperBean.SITE_ID);
        poc = auxData.get(AuxDataHelperBean.POC);
        collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);
        key = stateCode + "_" + countyCode + "_" + siteId;

        /*
         * get sulfate and nitrate data from the results
         */
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {
                if (sulfateAnalytes.contains(data.getAnalyte()) &&
                    data.getAnalysisId().equals(analysisId) &&
                    ( !DataBaseUtil.isEmpty(data.getValue()) || !DataBaseUtil.isEmpty(nullDataCd))) {
                    sulfateParameter = analyteParameters.get(data.getAnalyteId());
                    sulfateAlternateMethodDetectableLimit = sulfateParameter.getP1().toString();
                    sulfateMethodCd = sulfateParameter.getP2().intValue();
                    sulfateDurationCd = sulfateParameter.getP3().intValue();
                    if (DataBaseUtil.isEmpty(nullDataCd))
                        sulfateValue = truncateDecimal(Double.parseDouble(data.getValue()) /
                                                       Double.parseDouble(volume), 2);
                    sulfateCode = analyteCodes.get(data.getAnalyteId());
                    if (reportedUnit == null) {
                        for (AnalysisViewDO a : getAnalyses(sm)) {
                            if (a.getId().equals(data.getAnalysisId()))
                                reportedUnit = unitCodes.get(a.getUnitOfMeasureId());
                        }
                    }
                } else if (nitrateAnalytes.contains(data.getAnalyte()) &&
                           data.getAnalysisId().equals(analysisId) &&
                           ( !DataBaseUtil.isEmpty(data.getValue()) || !DataBaseUtil.isEmpty(nullDataCd))) {
                    nitrateParameter = analyteParameters.get(data.getAnalyteId());
                    nitrateAlternateMethodDetectableLimit = nitrateParameter.getP1().toString();
                    nitrateMethodCd = nitrateParameter.getP2().intValue();
                    nitrateDurationCd = nitrateParameter.getP3().intValue();
                    if (DataBaseUtil.isEmpty(nullDataCd))
                        nitrateValue = truncateDecimal( (Double.parseDouble(data.getValue()) * nitrateConstant) /
                                                                       Double.parseDouble(volume),
                                                       2);
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
        buildSiteInfo(ssb, rawDataType, action, stateCode, countyCode, siteId, sulfateCode, poc);
        buildSiteInfo(nsb, rawDataType, action, stateCode, countyCode, siteId, nitrateCode, poc);

        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());
        ssb.append(sulfateDurationCd)
           .append(delim)
           .append(reportedUnit)
           .append(delim)
           .append(threeDigits.format(sulfateMethodCd))
           .append(delim)
           .append(date)
           .append(delim)
           .append(time)
           .append(delim);
        nsb.append(nitrateDurationCd)
           .append(delim)
           .append(reportedUnit)
           .append(delim)
           .append(threeDigits.format(nitrateMethodCd))
           .append(delim)
           .append(date)
           .append(delim)
           .append(time)
           .append(delim);

        if (DataBaseUtil.isEmpty(nullDataCd)) {
            /*
             * the value is filled and the null data code is empty
             */
            ssb.append(sulfateValue).append(delim).append(delim);
            nsb.append(nitrateValue).append(delim).append(delim);
        } else {
            /*
             * the value is empty and the null data code is filled
             */
            ssb.append(delim).append(nullDataCd).append(delim);
            nsb.append(delim).append(nullDataCd).append(delim);
        }

        ssb.append(collectionFreq).append(delim);
        nsb.append(collectionFreq).append(delim);
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

        sulfateNitrateStrings = new HashMap<String, ArrayList<String>>();
        /*
         * check if the strings are valid before returning them
         */
        if (sulfateNitrateStrings.get(key) == null)
            sulfateNitrateStrings.put(key, new ArrayList<String>());
        if (sulfateValue != null || nullDataCd != null) {

            sulfateNitrateStrings.get(key).add(ssb.toString());
        }
        if (nitrateValue != null || nullDataCd != null)
            sulfateNitrateStrings.get(key).add(nsb.toString());
        return sulfateNitrateStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private HashMap<String, ArrayList<String>> getAirToxicsStrings(SampleManager1 sm,
                                                                   Integer testId,
                                                                   String action,
                                                                   String qualifierCode,
                                                                   ArrayList<String> qualifierStrings,
                                                                   Integer analysisId) throws Exception {
        boolean addTds, addAll;
        int durationCode, methodCode;
        Integer analysisCount;
        Double ttds, tmnoc;
        String stateCode, countyCode, siteId, parameter, poc, nullDataCd, collectionFreq, date, reportedUnit, value, mdl, qualifier, analyte, key;
        StringBuilder sb;
        SimpleDateFormat dateTimeFormat;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits;
        HashMap<String, ArrayList<String>> airToxicsStrings;
        HashMap<String, String> auxData;

        /*
         * only create strings for samples with descriptions of "sample" or
         * "replicate"
         */
        if ( !"sample".equals(sm.getSampleEnvironmental().getDescription()) &&
            !"duplicate".equals(sm.getSampleEnvironmental().getDescription()))
            return null;

        /*
         * find if there needs to be any extra strings created for extra
         * analytes ("total tolualdehydes", "tnmoc,speciated")
         */
        addAll = addTds = false;
        tmnoc = ttds = null;
        reportedUnit = parameter = null;
        analysisCount = durationCode = methodCode = 0;
        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);

        /*
         * get auxiliary data from the sample
         */
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");
        auxData = auxDataHelper.fillAirQualityAuxData(sm);
        nullDataCd = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
        stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
        countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
        siteId = auxData.get(AuxDataHelperBean.SITE_ID);
        poc = auxData.get(AuxDataHelperBean.POC);
        collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);
        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());
        key = stateCode + "_" + countyCode + "_" + siteId;

        if (getAnalyses(sm) != null) {
            for (AnalysisViewDO data : getAnalyses(sm)) {
                if (data.getId().equals(analysisId)) {
                    reportedUnit = unitCodes.get(data.getUnitOfMeasureId());
                    if (data.getMethodName().contains(to11)) {
                        addTds = true;
                        ttds = (double)0;
                    } else if (data.getMethodName().contains(to12)) {
                        addAll = true;
                        tmnoc = (double)0;
                    }
                }
                if (testId.equals(data.getTestId()) &&
                    Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId())) {
                    analysisCount++ ;
                    if (analysisCount > 1) {
                        if (airToxicPrecisionStrings.get(key) == null)
                            airToxicPrecisionStrings.put(key, new ArrayList<String>());

                        airToxicPrecisionStrings.get(key)
                                                .addAll(getAirToxicsPrecisionString(sm,
                                                                                    testId,
                                                                                    action,
                                                                                    stateCode,
                                                                                    countyCode,
                                                                                    siteId,
                                                                                    poc,
                                                                                    reportedUnit,
                                                                                    date,
                                                                                    addAll,
                                                                                    addTds,
                                                                                    data.getId()));
                        replicate = true;
                    }
                }
            }
        }

        if ("duplicate".equals(sm.getSampleEnvironmental().getDescription()) &&
            DataBaseUtil.isEmpty(nullDataCd)) {
            airToxicPrecisionStrings.get(key).addAll(getAirToxicsPrecisionString(sm,
                                                                                 testId,
                                                                                 action,
                                                                                 stateCode,
                                                                                 countyCode,
                                                                                 siteId,
                                                                                 poc,
                                                                                 reportedUnit,
                                                                                 date,
                                                                                 addAll,
                                                                                 addTds,
                                                                                 analysisId));
        }

        /*
         * go through all the analytes and create a string for each one
         */
        airToxicsStrings = new HashMap<String, ArrayList<String>>();
        value = mdl = qualifier = analyte = null;
        sb = new StringBuilder();
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {
                /*
                 * skip the result if it is not part of this analysis
                 */
                if ( !data.getAnalysisId().equals(analysisId))
                    continue;
                /*
                 * get all result data for this analyte before creating the
                 * screen
                 */
                if (value == null) {
                    if (data.getIsColumn().equals("N")) {
                        if (data.getValue() != null)
                            value = data.getValue();
                        else
                            value = "0";
                        parameter = analyteCodes.get(data.getAnalyteId());
                        analyte = data.getAnalyte();
                        ap = analyteParameters.get(data.getAnalyteId());
                        if (ap.getP2() != null && ap.getP3() != null) {
                            methodCode = ap.getP2().intValue();
                            durationCode = ap.getP3().intValue();
                        }
                        continue;
                    } else {
                        continue;
                    }
                } else if (mdl == null) {
                    if (data.getValue() != null)
                        mdl = data.getValue();
                    else
                        mdl = "";
                    continue;
                } else if (qualifier == null) {
                    if (data.getValue() != null)
                        qualifier = data.getValue();
                    else
                        qualifier = "";
                }

                /*
                 * we do not create a string for this analyte
                 */
                if (parameter == null) {
                    /*
                     * add the result value to the extra analyte values if
                     * applicable
                     */
                    if (addTds && analyte.contains(tolualdehyde) && !value.contains("<"))
                        ttds += Double.parseDouble(value);
                    if (addAll && !value.contains("<"))
                        tmnoc += Double.parseDouble(value);
                    value = null;
                    mdl = null;
                    qualifier = null;
                    parameter = null;
                    analyte = null;
                    continue;
                }

                /*
                 * if the data isn't there, a string cannot be created
                 */
                if (durationCode == 0 || methodCode == 0) {
                    value = null;
                    mdl = null;
                    qualifier = null;
                    parameter = null;
                    analyte = null;
                    continue;
                }
                buildSiteInfo(sb,
                              rawDataType,
                              action,
                              stateCode,
                              countyCode,
                              siteId,
                              parameter,
                              poc);

                sb.append(durationCode).append(delim);
                sb.append(reportedUnit).append(delim);
                sb.append(threeDigits.format(methodCode)).append(delim);
                sb.append(date).append(delim);
                sb.append(time).append(delim);
                if (DataBaseUtil.isEmpty(nullDataCd)) {
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
                    sb.append(collectionFreq).append(delim);
                    /*
                     * fields #16 and #18 through #26 and #28 are not used and
                     * are empty
                     */
                    sb.append(delim);
                    if (qualifierStrings.contains(qualifier))
                        sb.append(qualifierCode);
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
                    sb.append(delim).append(nullDataCd).append(delim);
                    sb.append(collectionFreq)
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

                if (airToxicsStrings.get(key) == null)
                    airToxicsStrings.put(key, new ArrayList<String>());
                airToxicsStrings.get(key).add(sb.toString());
                sb.setLength(0);
                value = null;
                mdl = null;
                qualifier = null;
                parameter = null;
                analyte = null;
                methodCode = 0;
            }
        }

        /*
         * in specific cases, a string needs to be created for a new analyte
         */
        if (addAll || addTds) {
            parameter = null;
            if (addAll)
                parameter = tnmocParameterCode;
            else if (addTds)
                parameter = ttdsParameterCode;
            buildSiteInfo(sb, rawDataType, action, stateCode, countyCode, siteId, parameter, poc);
            if (addAll)
                sb.append(tnmocDurationCode).append(delim);
            else if (addTds)
                sb.append(ttdsDurationCode).append(delim);
            sb.append(reportedUnit).append(delim);
            if (addAll)
                sb.append(tnmocMethodCode).append(delim);
            else if (addTds)
                sb.append(ttdsMethodCode).append(delim);
            sb.append(date).append(delim);
            sb.append(time).append(delim);
            if (DataBaseUtil.isEmpty(nullDataCd)) {
                if (addAll) {
                    if (tmnoc == 0)
                        sb.append("0").append(delim);
                    else
                        sb.append(truncateDecimal(tmnoc, 2)).append(delim);
                } else if (addTds) {
                    if (ttds == 0)
                        sb.append("0").append(delim);
                    else
                        sb.append(truncateDecimal(ttds, 4)).append(delim);
                }
                /*
                 * null data code is empty
                 */
                sb.append(delim);
                sb.append(collectionFreq).append(delim);
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
                    sb.append(tnmocMDL).append(delim);
                else if (addTds)
                    sb.append(ttdsMDL).append(delim);
            } else {
                /*
                 * there is a null data code, so the reported sample value is
                 * left empty
                 */
                sb.append(delim).append(nullDataCd).append(delim);
                sb.append(collectionFreq)
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
            if (airToxicsStrings.get(key) == null)
                airToxicsStrings.put(key, new ArrayList<String>());
            airToxicsStrings.get(key).add(sb.toString());
        }
        return airToxicsStrings;
    }

    /**
     * creates air toxics precision strings from a duplicate or replicate sample
     */
    private ArrayList<String> getAirToxicsPrecisionString(SampleManager1 sm, Integer testId,
                                                          String action, String stateCode,
                                                          String countyCode, String siteId,
                                                          String poc, String reportedUnit,
                                                          String date, boolean addAll,
                                                          boolean addTds, Integer analysisId) throws Exception {
        int methodCode;
        Double ttds, tmnoc;
        String parameter, value, analyte;
        StringBuilder sb;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits;
        ArrayList<String> precisions;

        tmnoc = ttds = null;
        if (addAll) {
            tmnoc = 0.0;
        } else if (addTds) {
            ttds = 0.0;
        }
        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);

        precisions = new ArrayList<String>();
        sb = new StringBuilder();
        value = analyte = parameter = null;
        methodCode = 0;
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {
                /*
                 * skip the result if it is not part of this analysis
                 */
                if ( !data.getAnalysisId().equals(analysisId))
                    continue;
                /*
                 * get all result data for this analyte before creating the
                 * screen
                 */
                if (data.getIsColumn().equals("N")) {
                    value = data.getValue();
                    /*
                     * if the value is insignificant, a string does not need to
                     * be created
                     */
                    if (value == null || value.contains("<")) {
                        value = null;
                        continue;
                    }
                    parameter = analyteCodes.get(data.getAnalyteId());
                    analyte = data.getAnalyte();
                    ap = analyteParameters.get(data.getAnalyteId());
                    if (ap.getP2() != null && ap.getP3() != null) {
                        methodCode = ap.getP2().intValue();
                    }
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
                    if (addTds && analyte.contains(tolualdehyde) && !value.contains("<"))
                        ttds += Double.parseDouble(value);
                    if (addAll && !value.contains("<"))
                        tmnoc += Double.parseDouble(value);
                    value = null;
                    parameter = null;
                    analyte = null;
                    continue;
                }

                /*
                 * if the data isn't there, a string cannot be created
                 */
                if (methodCode == 0) {
                    value = null;
                    parameter = null;
                    analyte = null;
                    continue;
                }

                sb.append(precisionType)
                  .append(delim)
                  .append(action.substring(0, 1))
                  .append(delim)
                  .append(assessmentType)
                  .append(delim)
                  .append(delim)
                  .append(stateCode)
                  .append(delim)
                  .append(countyCode)
                  .append(delim)
                  .append(siteId)
                  .append(delim)
                  .append(parameter)
                  .append(delim)
                  .append(poc)
                  .append(delim)
                  .append(date)
                  .append(delim)
                  .append(assessmentNumber)
                  .append(delim)
                  .append(methodCode)
                  .append(delim)
                  .append(reportedUnit)
                  .append(delim)
                  .append(value)
                  .append(delim)
                  .append(delim)
                  .append(delim)
                  .append(delim);
                if (addAll)
                    tmnoc += Double.parseDouble(value);
                precisions.add(sb.toString());
                sb.setLength(0);
                value = null;
                parameter = null;
                analyte = null;
                methodCode = 0;
            }
        }

        /*
         * in specific cases, a string needs to be created for a new analyte
         */
        if (addAll || addTds) {
            parameter = null;
            if (addAll) {
                if (tmnoc == 0)
                    return precisions;
                parameter = tnmocParameterCode;
            } else if (addTds) {
                if (ttds == 0)
                    return precisions;
                parameter = ttdsParameterCode;
            }
            sb.append(precisionType)
              .append(delim)
              .append(action.substring(0, 1))
              .append(delim)
              .append(assessmentType)
              .append(delim)
              .append(delim)
              .append(stateCode)
              .append(delim)
              .append(countyCode)
              .append(delim)
              .append(siteId)
              .append(delim)
              .append(parameter)
              .append(delim)
              .append(poc)
              .append(delim)
              .append(date)
              .append(delim)
              .append(assessmentNumber)
              .append(delim);
            if (addAll)
                sb.append(tnmocMethodCode).append(delim);
            else if (addTds)
                sb.append(ttdsMethodCode).append(delim);
            sb.append(reportedUnit).append(delim);
            if (addAll)
                sb.append(truncateDecimal(tmnoc, 2));
            else if (addTds)
                sb.append(truncateDecimal(ttds, 4));
            sb.append(delim).append(delim).append(delim).append(delim);
            precisions.add(sb.toString());
        }
        return precisions;
    }

    /**
     * Get metals air quality strings for sample
     */
    private HashMap<String, ArrayList<String>> getMetalsStrings(SampleManager1 sm, Integer testId,
                                                                String action, Integer analysisId,
                                                                boolean pressureTempString) throws Exception {
        String sampleType, stateCode, countyCode, siteId, poc, volume, nullDataCode, stripsPerFilter, collectionFreq, date, reportedUnit, pressure, temperature, key;
        SimpleDateFormat dateTimeFormat;
        HashMap<String, ArrayList<String>> metalsStrings;
        HashMap<String, String> auxData;

        reportedUnit = null;

        /*
         * replicate and performance evaluation samples do not produce strings
         */
        if (sm.getSample().getClientReference() == null ||
            "r".equalsIgnoreCase(sm.getSample().getClientReference().substring(2, 3)) ||
            "pep".equalsIgnoreCase(sm.getSample().getClientReference().substring(0, 3)))
            return null;

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
        metalsStrings = new HashMap<String, ArrayList<String>>();
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");

        /*
         * get auxiliary data from the sample
         */
        auxData = auxDataHelper.fillAirQualityAuxData(sm);
        volume = auxData.get(AuxDataHelperBean.VOLUME);
        pressure = auxData.get(AuxDataHelperBean.PRESSURE);
        temperature = auxData.get(AuxDataHelperBean.TEMPERATURE);
        nullDataCode = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
        stripsPerFilter = auxData.get(AuxDataHelperBean.STRIPS_PER_FILTER);
        stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
        countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
        siteId = auxData.get(AuxDataHelperBean.SITE_ID);
        key = stateCode + "_" + countyCode + "_" + siteId;

        if (poc == null)
            poc = auxData.get(AuxDataHelperBean.POC);
        collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);

        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());
        if (metalsStrings.get(key) == null)
            metalsStrings.put(key, new ArrayList<String>());
        if (pressureTempString == false) {
            metalsStrings.get(key).addAll(getPressureTemperatureStrings(action,
                                                                        stateCode,
                                                                        countyCode,
                                                                        siteId,
                                                                        poc,
                                                                        date,
                                                                        nullDataCode,
                                                                        collectionFreq,
                                                                        pressure,
                                                                        temperature));
        }
        if (testId.equals(metalLeadTest)) {
            metalsStrings.get(key).add(getMetalsSampleString(sm,
                                                             testId,
                                                             stripsPerFilter,
                                                             volume,
                                                             action,
                                                             stateCode,
                                                             countyCode,
                                                             siteId,
                                                             poc,
                                                             reportedUnit,
                                                             date,
                                                             nullDataCode,
                                                             collectionFreq,
                                                             analysisId));
        } else if (testId.equals(metalManganeseTest)) {
            metalsStrings.get(key).add(getMetalsSampleString(sm,
                                                             testId,
                                                             stripsPerFilter,
                                                             volume,
                                                             action,
                                                             stateCode,
                                                             countyCode,
                                                             siteId,
                                                             poc,
                                                             reportedUnit,
                                                             date,
                                                             nullDataCode,
                                                             collectionFreq,
                                                             analysisId));
        }
        return metalsStrings;
    }

    /**
     * create strings for pressure and temperature data for metal samples
     */
    private ArrayList<String> getPressureTemperatureStrings(String action, String stateCode,
                                                            String countyCode, String siteId,
                                                            String poc, String date,
                                                            String nullDataCode,
                                                            String collectionFreq, String pressure,
                                                            String temperature) {
        StringBuilder psb, tsb;
        ArrayList<String> metalStrings;

        metalStrings = new ArrayList<String>();
        psb = new StringBuilder();
        tsb = new StringBuilder();
        buildSiteInfo(psb,
                      rawDataType,
                      action,
                      stateCode,
                      countyCode,
                      siteId,
                      pressureParameter,
                      poc);
        buildSiteInfo(tsb,
                      rawDataType,
                      action,
                      stateCode,
                      countyCode,
                      siteId,
                      temperatureParameter,
                      poc);
        psb.append(pressureDurationCode)
           .append(delim)
           .append(pressureReportedUnit)
           .append(delim)
           .append(pressureMethodCode)
           .append(delim)
           .append(date)
           .append(delim)
           .append(time)
           .append(delim);
        tsb.append(temperatureDurationCode)
           .append(delim)
           .append(temperatureReportedUnit)
           .append(delim)
           .append(temperatureMethodCode)
           .append(delim)
           .append(date)
           .append(delim)
           .append(time)
           .append(delim);
        if (DataBaseUtil.isEmpty(nullDataCode)) {
            psb.append(pressure).append(delim).append(delim);
            tsb.append(temperature).append(delim).append(delim);
        } else {
            psb.append(delim).append(nullDataCode).append(delim);
            tsb.append(delim).append(nullDataCode).append(delim);
        }
        psb.append(collectionFreq)
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
        tsb.append(collectionFreq)
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

        if (pressure == null)
            metalStrings.add(null);
        else
            metalStrings.add(psb.toString());
        if (temperature == null)
            metalStrings.add(null);
        else
            metalStrings.add(tsb.toString());
        return metalStrings;
    }

    /**
     * create a string for metal raw data
     */
    private String getMetalsSampleString(SampleManager1 sm, Integer testId, String stripsPerFilter,
                                         String volume, String action, String stateCode,
                                         String countyCode, String siteId, String poc,
                                         String reportedUnit, String date, String nullDataCode,
                                         String collectionFreq, Integer analysisId) {
        boolean lead, cbFlag;
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

        lead = false;
        cbFlag = false;
        if (testId.equals(metalLeadTest)) {
            lead = true;
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            lead = false;
        }

        value = mdl = parameter = null;
        durationCode = methodCode = 0;
        sb = new StringBuilder();
        for (ResultViewDO data : getResults(sm)) {
            if ( !DataBaseUtil.isSame(data.getAnalysisId(), analysisId))
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
            if ( (value == null || mdl == null) && DataBaseUtil.isEmpty(nullDataCode))
                continue;

            buildSiteInfo(sb, rawDataType, action, stateCode, countyCode, siteId, parameter, poc);
            sb.append(durationCode)
              .append(delim)
              .append(reportedUnit)
              .append(delim)
              .append(threeDigits.format(methodCode))
              .append(delim)
              .append(date)
              .append(delim)
              .append(time)
              .append(delim);
            if (DataBaseUtil.isEmpty(nullDataCode)) {
                filterMdl = Double.parseDouble(mdl);
                sampleValue = Double.parseDouble(value);
                sampleVolume = Double.parseDouble(volume);
                spf = Integer.parseInt(stripsPerFilter);

                if (metalManganeseFilterLotBlank - filterMdl > 0)
                    cbFlag = true;

                if (lead) {
                    sb.append(truncateDecimal( ( (sampleValue * spf) - metalLeadFilterLotBlank) /
                                              sampleVolume, 3)).append(delim).append(delim);
                } else if (cbFlag) {
                    sb.append(truncateDecimal(1000 *
                                                              ( (sampleValue * spf) - metalManganeseFilterLotBlank) /
                                                              sampleVolume,
                                              2))
                      .append(delim)
                      .append(delim);
                } else {
                    sb.append(truncateDecimal(1000 * sampleValue * spf / sampleVolume, 2))
                      .append(delim)
                      .append(delim);
                }
                sb.append(collectionFreq).append(delim).append(delim);

                if (lead) {
                    sampleMdl = filterMdl * spf / sampleVolume;
                    if (sampleValue < filterMdl)
                        sb.append(md);
                    sb.append(delim);
                    if (filterMdl < sampleValue * spf && sampleValue * spf < (10 * filterMdl))
                        sb.append(sq);
                    sb.append(delim).append(delim).append(delim).append(delim);
                } else {
                    sampleMdl = 1000 * (filterMdl / sampleVolume);
                    if (cbFlag)
                        sb.append(cb);
                    sb.append(delim);
                    if (sampleValue < filterMdl)
                        sb.append(md);
                    sb.append(delim);
                    if (filterMdl < sampleValue * spf && sampleValue * spf < (10 * filterMdl))
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
                if (lead)
                    sb.append(fourDecimals.format(sampleMdl)).append(delim);
                else
                    sb.append(twoDecimals.format(sampleMdl)).append(delim);
                if ( !lead) {
                    sb.append(truncateDecimal(1000 * metalManganeseFilterLotBlank / sampleVolume, 2));
                }
            } else {
                sb.append(delim)
                  .append(nullDataCode)
                  .append(delim)
                  .append(collectionFreq)
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

        }
        return sb.toString();
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
                                                                                                                  analyteIds,
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
                                                                                                                  unitIds,
                                                                                                                  profileIds)) {
            unitCodes.put(data.getExchangeLocalTermReferenceId(), data.getExternalTerm());
        }
    }

    /**
     * get the analyte parameters for the tests in the valid analyses
     */
    private void getAnalyteParameters(Set<Integer> testIds) throws Exception {
        analyteParameters = new HashMap<Integer, AnalyteParameterViewDO>();

        if (testIds.size() < 1)
            return;

        for (Integer testId : testIds) {
            for (AnalyteParameterViewDO data : analyteParameter.fetchActiveByReferenceIdReferenceTableId(testId,
                                                                                                         Constants.table().TEST)) {
                analyteParameters.put(data.getAnalyteId(), data);
            }
        }
    }

    /**
     * creates a string for the beginning of an air quality string (all strings
     * have these fields)
     */
    private void buildSiteInfo(StringBuilder sb, String type, String action, String stateCode,
                               String countyCode, String siteId, String parameter, String poc) {
        sb.append(type)
          .append(delim)
          .append(action.substring(0, 1))
          .append(delim)
          .append(stateCode)
          .append(delim)
          .append(countyCode)
          .append(delim)
          .append(siteId)
          .append(delim)
          .append(parameter)
          .append(delim)
          .append(poc)
          .append(delim);
    }
}
