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
import static org.openelis.manager.SampleManager1Accessor.getAuxilliary;
import static org.openelis.manager.SampleManager1Accessor.getResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
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
import org.openelis.domain.AuxDataViewDO;
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

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB",
          type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class AirQualityReportBean {

    @EJB
    private SystemVariableBean                       systemVariable;

    @EJB
    private SessionCacheBean                         session;

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
                    rawDataType = "RD", precisionType = "RP", accuracyDataType = "RA",
                    blanksDataType = "RB", time = "00:00", precisionId = "1", to11 = "to-11",
                    to12 = "to-12", tolualdehyde = "Tolualdehyde", md = "MD", sq = "SQ", cb = "CB",
                    da = "DA", fb = "FB", tripBlankType = "FIELD", fieldBlankType = "FIELD24HR";

    private String                                   ttdsDurationCode, ttdsMethodCode, ttdsMDL,
                    ttdsParameterCode, tnmocDurationCode, tnmocMethodCode, tnmocMDL,
                    tnmocParameterCode, pressureParameter, pressureDurationCode,
                    pressureReportedUnit, pressureMethodCode, temperatureParameter,
                    temperatureDurationCode, temperatureReportedUnit, temperatureMethodCode;

    private static final Double                      nitrateConstant = 4.4;

    private ArrayList<String>                        sulfateAnalytes, nitrateAnalytes;

    private HashMap<Integer, SampleManager1>         originalSamples;

    private HashMap<Integer, String>                 analyteCodes, unitCodes;

    private HashMap<Integer, AnalyteParameterViewDO> analyteParameters;

    private HashMap<String, ArrayList<String>>       airToxicRawStrings, airToxicPrecisionStrings,
                    pbRawStrings, pbAccuracyStrings, pbBlankStrings, mnRawStrings,
                    mnAccuracyStrings, mnBlankStrings, pressureStrings, temperatureStrings;

    private Integer                                  metalLeadTest, metalManganeseTest,
                    metalManganeseAccuracyTest;

    private DecimalFormat                            df;

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
        boolean pressureTempString;
        Integer analysisCount;
        ReportStatus status;
        String val, frDate, tDate, accession, action, reportTo, attention, tempArray[], analyteArray[], qualifierCode, airDir, metal;
        QueryData field;
        Query query;
        ArrayList<SampleManager1> sms;
        ArrayList<QueryData> fields;
        ArrayList<String> stringList, qualifierStrings;
        ArrayList<Integer> sulfateNitrateTests, airToxicsTests;
        ArrayList<Integer> problemSamples;
        HashMap<String, ArrayList<SampleManager1>> samples;
        HashMap<String, String> strings;
        HashMap<String, ArrayList<String>> sulfateStrings, nitrateStrings;
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
             * Test id for metal lead test.
             */
            metalLeadTest = Integer.parseInt(systemVariable.fetchByName("air_metal_lead_test")
                                                           .getValue());
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get()
                            .systemVariable_missingInvalidSystemVariable("air_metal_lead_test"),
                    e);
            throw e;
        }

        try {
            /*
             * Test id for metal manganese test.
             */
            metalManganeseTest = Integer.parseInt(systemVariable.fetchByName("air_metal_manganese_test")
                                                                .getValue());
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

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("AirQualityReport", status);

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
            throw new InconsistencyException("You must specify Action for this report");

        if (frDate == null || tDate == null) {
            if (accession == null)
                /*
                 * This is the case where there is not enough data to query for
                 * samples. One or both of the dates is missing, and the
                 * accession number is missing.
                 */
                throw new InconsistencyException("You must specify From Date and To Date or accession number for this report");

            sms = new ArrayList<SampleManager1>();
            sms.add(sample.fetchByAccession(Integer.parseInt(accession)));
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
            sms = sample.fetchByQuery(fields,
                                      0,
                                      -1,
                                      SampleManager1.Load.AUXDATA,
                                      SampleManager1.Load.RESULT);
        } else {
            /*
             * This is the case where there is too much data to query for
             * samples. Both dates are filled and the accession number is
             * filled.
             */
            throw new InconsistencyException("You may only specify From Date and To Date or accession number for this report");
        }

        samples = new HashMap<String, ArrayList<SampleManager1>>();
        originalSamples = new HashMap<Integer, SampleManager1>();
        analyteIds = new HashSet<Integer>();
        unitIds = new HashSet<Integer>();
        testIds = new HashSet<Integer>();
        problemSamples = new ArrayList<Integer>();
        df = new DecimalFormat("#");
        df.setMaximumFractionDigits(6);

        /*
         * separate the samples for each site, find duplicate samples, and find
         * problem samples
         */
        for (SampleManager1 sm : sms) {
            analysisCount = 0;
            if (DataBaseUtil.isDifferent(Constants.dictionary().SAMPLE_RELEASED, sm.getSample()
                                                                                   .getStatusId())) {
                problemSamples.add(sm.getSample().getAccessionNumber());
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
                        if (airToxicsTests.contains(data.getTestId())) {
                            if (getAuxilliary(sm) != null) {
                                for (AuxDataViewDO aux : getAuxilliary(sm)) {
                                    /*
                                     * original sample number is only not empty
                                     * when the sample is a duplicate
                                     */
                                    if (AuxDataHelperBean.ORIG_SAMPLE_NUMBER.equals(aux.getAnalyteExternalId()) &&
                                        !DataBaseUtil.isEmpty(aux.getValue())) {
                                        if (originalSamples.get(Integer.parseInt(aux.getValue())) == null)
                                            originalSamples.put(Integer.parseInt(aux.getValue()),
                                                                null);
                                    }
                                }
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
                problemSamples.add(sm.getSample().getAccessionNumber());
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
        getAirAnalyteCodes(analyteIds);
        getAirUnitCodes(unitIds);
        getAirAnalyteParameters(testIds);

        /*
         * create a list of original samples associated with the duplicates
         */
        if (originalSamples.size() > 1) {
            for (SampleManager1 sm : sample.fetchByIds(new ArrayList<Integer>(originalSamples.keySet()))) {
                originalSamples.put(sm.getSample().getAccessionNumber(), sm);
            }
        }

        sulfateStrings = new HashMap<String, ArrayList<String>>();
        nitrateStrings = new HashMap<String, ArrayList<String>>();
        pbRawStrings = new HashMap<String, ArrayList<String>>();
        pbAccuracyStrings = new HashMap<String, ArrayList<String>>();
        pbBlankStrings = new HashMap<String, ArrayList<String>>();
        mnRawStrings = new HashMap<String, ArrayList<String>>();
        mnAccuracyStrings = new HashMap<String, ArrayList<String>>();
        mnBlankStrings = new HashMap<String, ArrayList<String>>();
        airToxicRawStrings = new HashMap<String, ArrayList<String>>();
        pressureStrings = new HashMap<String, ArrayList<String>>();
        temperatureStrings = new HashMap<String, ArrayList<String>>();
        /*
         * create strings for each valid analysis
         */
        for (String key : samples.keySet()) {
            for (SampleManager1 sm : samples.get(key)) {
                pressureTempString = false;
                if (getResults(sm) != null) {
                    for (AnalysisViewDO data : getAnalyses(sm)) {
                        if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
                            continue;
                        if (sulfateNitrateTests.contains(data.getTestId())) {
                            strings = null;
                            try {
                                strings = getSulfateNitrateStrings(sm, action, data.getId());
                            } catch (Exception e) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (strings == null || strings.size() < 1) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            val = strings.get("sulfate");
                            if (val != null) {
                                if (sulfateStrings.get(key) == null)
                                    sulfateStrings.put(key, new ArrayList<String>());
                                sulfateStrings.get(key).add(val);
                            }
                            val = strings.get("nitrate");
                            if (val != null) {
                                if (nitrateStrings.get(key) == null)
                                    nitrateStrings.put(key, new ArrayList<String>());
                                nitrateStrings.get(key).add(val);
                            }
                        } else if (airToxicsTests.contains(data.getTestId())) {
                            try {
                                stringList = getAirToxicsStrings(sm,
                                                                 data.getTestId(),
                                                                 action,
                                                                 qualifierCode,
                                                                 qualifierStrings,
                                                                 data.getId(),
                                                                 key);
                            } catch (Exception e) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (stringList == null || stringList.size() < 1) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (airToxicRawStrings.get(key) == null)
                                airToxicRawStrings.put(key, new ArrayList<String>());
                            airToxicRawStrings.get(key).addAll(stringList);
                            /*
                             * there should only be one string created for an
                             * air toxics sample, even if there is more than one
                             * valid analysis
                             */
                            break;
                        } else if (metalLeadTest.equals(data.getTestId())) {
                            try {
                                stringList = getMetalsStrings(sm,
                                                              data.getTestId(),
                                                              action,
                                                              data.getId(),
                                                              pressureTempString);
                            } catch (Exception e) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (stringList == null || stringList.size() < 1) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }

                            for (int i = 0; i < stringList.size(); i++ ) {
                                metal = stringList.get(i);
                                if (metal == null)
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
                                    if (pbRawStrings.get(key) == null)
                                        pbRawStrings.put(key, new ArrayList<String>());
                                    pbRawStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(accuracyDataType)) {
                                    if (pbAccuracyStrings.get(key) == null)
                                        pbAccuracyStrings.put(key, new ArrayList<String>());
                                    pbAccuracyStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(blanksDataType)) {
                                    if (pbBlankStrings.get(key) == null)
                                        pbBlankStrings.put(key, new ArrayList<String>());
                                    pbBlankStrings.get(key).add(metal);
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
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (stringList == null) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            for (int i = 0; i < stringList.size(); i++ ) {
                                metal = stringList.get(i);
                                if (metal == null)
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
                                    if (mnRawStrings.get(key) == null)
                                        mnRawStrings.put(key, new ArrayList<String>());
                                    mnRawStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(accuracyDataType)) {
                                    if (mnAccuracyStrings.get(key) == null)
                                        mnAccuracyStrings.put(key, new ArrayList<String>());
                                    mnAccuracyStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(blanksDataType)) {
                                    if (mnBlankStrings.get(key) == null)
                                        mnBlankStrings.put(key, new ArrayList<String>());
                                    mnBlankStrings.get(key).add(metal);
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
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            if (stringList == null) {
                                problemSamples.add(sm.getSample().getAccessionNumber());
                                continue;
                            }
                            for (int i = 0; i < stringList.size(); i++ ) {
                                metal = stringList.get(i);
                                if (metal == null)
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
                                    if (mnRawStrings.get(key) == null)
                                        mnRawStrings.put(key, new ArrayList<String>());
                                    mnRawStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(accuracyDataType)) {
                                    if (mnAccuracyStrings.get(key) == null)
                                        mnAccuracyStrings.put(key, new ArrayList<String>());
                                    mnAccuracyStrings.get(key).add(metal);
                                } else if (metal.substring(0, 2).equals(blanksDataType)) {
                                    if (mnBlankStrings.get(key) == null)
                                        mnBlankStrings.put(key, new ArrayList<String>());
                                    mnBlankStrings.get(key).add(metal);
                                }
                            }
                            pressureTempString = true;
                        }
                    }
                }
            }
        }

        // TODO develop strings that are not raw data type
        /*
         * write strings out to text files
         */
        for (String key : sulfateStrings.keySet()) {
            File file = File.createTempFile(key + "_sulfate", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : sulfateStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : nitrateStrings.keySet()) {
            File file = File.createTempFile(key + "_nitrate", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : nitrateStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : airToxicRawStrings.keySet()) {
            File file = File.createTempFile(key + "_raw_air_toxics", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : airToxicRawStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        // for (String key : airToxicPrecisionStrings.keySet()) {
        // File file = new File(key + "_precision_air_toxics.txt");
        // BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        // for (String s : airToxicPrecisionStrings.get(key)) {
        // writer.write(s + "\n");
        // }
        //
        // writer.close();
        // }
        for (String key : pbRawStrings.keySet()) {
            File file = File.createTempFile(key + "_raw_lead", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : pbRawStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : mnRawStrings.keySet()) {
            File file = File.createTempFile(key + "_raw_manganese", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : mnRawStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        // for (String key : pbAccuracyStrings.keySet()) {
        // File file = new File(key + "_accuracy_metals.txt");
        // BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        // for (String s : pbAccuracyStrings.get(key)) {
        // writer.write(s + "\n");
        // }
        //
        // writer.close();
        // }
        // for (String key : mnAccuracyStrings.keySet()) {
        // File file = new File(key + "_accuracy_metals.txt");
        // BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        // for (String s : mnAccuracyStrings.get(key)) {
        // writer.write(s + "\n");
        // }
        //
        // writer.close();
        // }
        // for (String key : pbBlankStrings.keySet()) {
        // File file = new File(key + "_blank_metals.txt");
        // BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        // for (String s : pbBlankStrings.get(key)) {
        // writer.write(s + "\n");
        // }
        //
        // writer.close();
        // }
        // for (String key : mnBlankStrings.keySet()) {
        // File file = new File(key + "_blank_metals.txt");
        // BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        // for (String s : mnBlankStrings.get(key)) {
        // writer.write(s + "\n");
        // }
        //
        // writer.close();
        // }
        for (String key : pressureStrings.keySet()) {
            File file = File.createTempFile(key + "_pressure", ".txt", new File(airDir));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : pressureStrings.get(key)) {
                writer.write(s + "\n");
            }

            writer.close();
        }
        for (String key : temperatureStrings.keySet()) {
            File file = File.createTempFile(key + "_temperature", ".txt", new File(airDir));
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
            String errorMessage = "Strings were not created for the following samples:\n";
            for (Integer i : problemSamples)
                errorMessage += i + ", ";
            errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
            status.setMessage(errorMessage);
            throw new Exception(errorMessage);
        }
        return status;
    }

    /**
     * Get sulfate and nitrate air quality strings for sample
     */
    private HashMap<String, String> getSulfateNitrateStrings(SampleManager1 sm, String action,
                                                             Integer analysisId) throws Exception {
        int sulfateDurationCd, nitrateDurationCd, sulfateMethodCd, nitrateMethodCd;
        String sulfateString, nitrateString, sulfateValue, nitrateValue, sulfateCode, nitrateCode, stateCode, countyCode, siteId, poc, volume, nullDataCd, collectionFreq, date, reportedUnit, end, sulfateAlternateMethodDetectableLimit, nitrateAlternateMethodDetectableLimit;
        SimpleDateFormat dateTimeFormat;
        AnalyteParameterViewDO sulfateParameter, nitrateParameter;
        DecimalFormat threeDigits;
        HashMap<String, String> sulfateNitrateStrings, auxData;

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
                        sulfateValue = getSulfateValue(data.getValue(), volume);
                    sulfateCode = analyteCodes.get(data.getAnalyteId());
                    for (AnalysisViewDO a : getAnalyses(sm)) {
                        if (a.getId().equals(data.getAnalysisId()))
                            reportedUnit = unitCodes.get(a.getUnitOfMeasureId());
                    }
                } else if (nitrateAnalytes.contains(data.getAnalyte()) &&
                           data.getAnalysisId().equals(analysisId) &&
                           ( !DataBaseUtil.isEmpty(data.getValue()) || !DataBaseUtil.isEmpty(nullDataCd))) {
                    nitrateParameter = analyteParameters.get(data.getAnalyteId());
                    nitrateAlternateMethodDetectableLimit = nitrateParameter.getP1().toString();
                    nitrateMethodCd = nitrateParameter.getP2().intValue();
                    nitrateDurationCd = nitrateParameter.getP3().intValue();
                    if (DataBaseUtil.isEmpty(nullDataCd))
                        nitrateValue = getNitrateValue(data.getValue(), volume);
                    nitrateCode = analyteCodes.get(data.getAnalyteId());
                }
            }
        } else {
            return null;
        }

        /*
         * build the strings
         */
        sulfateString = buildSiteInfo(rawDataType,
                                      action,
                                      stateCode,
                                      countyCode,
                                      siteId,
                                      sulfateCode,
                                      poc);

        nitrateString = buildSiteInfo(rawDataType,
                                      action,
                                      stateCode,
                                      countyCode,
                                      siteId,
                                      nitrateCode,
                                      poc);

        sulfateString += sulfateDurationCd + delim;
        nitrateString += nitrateDurationCd + delim;
        sulfateString += reportedUnit + delim;
        nitrateString += reportedUnit + delim;
        sulfateString += threeDigits.format(sulfateMethodCd) + delim;
        nitrateString += threeDigits.format(nitrateMethodCd) + delim;
        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());
        sulfateString += date + delim;
        nitrateString += date + delim;
        sulfateString += time + delim;
        nitrateString += time + delim;
        if (DataBaseUtil.isEmpty(nullDataCd)) {
            /*
             * the value is filled and the null data code is empty
             */
            sulfateString += sulfateValue + delim + delim;
            nitrateString += nitrateValue + delim + delim;
        } else {
            /*
             * the value is empty and the null data code is filled
             */
            sulfateString += delim + nullDataCd + delim;
            nitrateString += delim + nullDataCd + delim;
        }

        end = collectionFreq + delim;
        /*
         * fields #16 through #26 and #28 are not used and are empty
         */
        end += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim +
               delim;
        sulfateString += end;
        nitrateString += end;
        sulfateString += sulfateAlternateMethodDetectableLimit + delim;
        nitrateString += nitrateAlternateMethodDetectableLimit + delim;

        sulfateNitrateStrings = new HashMap<String, String>();
        /*
         * check if the strings are valid before returning them
         */
        if (sulfateValue != null || nullDataCd != null)
            sulfateNitrateStrings.put("sulfate", sulfateString);
        if (nitrateValue != null || nullDataCd != null)
            sulfateNitrateStrings.put("nitrate", nitrateString);
        return sulfateNitrateStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private ArrayList<String> getAirToxicsStrings(SampleManager1 sm, Integer testId, String action,
                                                  String qualifierCode,
                                                  ArrayList<String> qualifierStrings,
                                                  Integer analysisId, String location) throws Exception {
        boolean addTds, addAll;
        int durationCode, methodCode;
        Integer analysisCount;
        Double ttds, tmnoc;
        String airToxicsString, originalAccession, stateCode, countyCode, siteId, parameter, poc, nullDataCd, collectionFreq, date, reportedUnit, value, mdl, qualifier, analyte;
        SimpleDateFormat dateTimeFormat;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits;
        ArrayList<String> airToxicsStrings;
        HashMap<String, String> auxData;

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
                    Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
                    analysisCount++ ;
            }
        }

        /*
         * get auxiliary data from the sample
         */
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");
        originalAccession = null;
        auxData = auxDataHelper.fillAirQualityAuxData(sm);
        nullDataCd = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
        stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
        countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
        siteId = auxData.get(AuxDataHelperBean.SITE_ID);
        poc = auxData.get(AuxDataHelperBean.POC);
        collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);
        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());

        /*
         * if the original sample number is not empty or there is more than one
         * released analysis with this test on this sample, then it is a
         * replicate or duplicate and precision strings need to be created
         * instead of raw data strings
         */
        if ( !DataBaseUtil.isEmpty(originalAccession) || analysisCount > 1) {
            if (airToxicPrecisionStrings == null)
                airToxicPrecisionStrings = new HashMap<String, ArrayList<String>>();
            if (airToxicPrecisionStrings.get(location) == null)
                airToxicPrecisionStrings.put(location, new ArrayList<String>());

            airToxicPrecisionStrings.get(location)
                                    .addAll(getAirToxicsPrecisionString(sm,
                                                                        testId,
                                                                        originalAccession,
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
        airToxicsStrings = new ArrayList<String>();
        value = mdl = qualifier = analyte = null;
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
                        value = data.getValue();
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
                    mdl = data.getValue();
                    continue;
                } else if (qualifier == null) {
                    qualifier = data.getValue();
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
                airToxicsString = buildSiteInfo(rawDataType,
                                                action,
                                                stateCode,
                                                countyCode,
                                                siteId,
                                                parameter,
                                                poc);

                airToxicsString += durationCode + delim;
                airToxicsString += reportedUnit + delim;
                airToxicsString += threeDigits.format(methodCode) + delim;
                airToxicsString += date + delim;
                airToxicsString += time + delim;
                if (DataBaseUtil.isEmpty(nullDataCd)) {
                    if (value.contains("<")) {
                        /*
                         * the value is less than the minimum value, so we round
                         * down to zero
                         */
                        airToxicsString += "0" + delim;
                        /*
                         * null data code is empty
                         */
                        airToxicsString += delim;
                    } else {
                        airToxicsString += value + delim;
                        /*
                         * null data code is empty
                         */
                        airToxicsString += delim;
                        if (addAll)
                            tmnoc += Double.parseDouble(value);
                    }
                } else {
                    /*
                     * there is a null data code, so the reported sample value
                     * is left empty
                     */
                    airToxicsString += delim + nullDataCd + delim;
                }
                airToxicsString += collectionFreq + delim;
                /*
                 * fields #16 and #18 through #26 and #28 are not used and are
                 * empty
                 */
                airToxicsString += delim;
                if (qualifierStrings.contains(qualifier))
                    airToxicsString += qualifierCode + delim;
                airToxicsString += delim + delim + delim + delim + delim + delim + delim + delim +
                                   delim;
                airToxicsString += mdl + delim;
                airToxicsStrings.add(airToxicsString);
                value = null;
                mdl = null;
                qualifier = null;
                parameter = null;
                analyte = null;
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
            airToxicsString = buildSiteInfo(rawDataType,
                                            action,
                                            stateCode,
                                            countyCode,
                                            siteId,
                                            parameter,
                                            poc);
            if (addAll)
                airToxicsString += tnmocDurationCode + delim;
            else if (addTds)
                airToxicsString += ttdsDurationCode + delim;
            airToxicsString += reportedUnit + delim;
            if (addAll)
                airToxicsString += tnmocMethodCode + delim;
            else if (addTds)
                airToxicsString += ttdsMethodCode + delim;
            airToxicsString += date + delim;
            airToxicsString += time + delim;
            if (DataBaseUtil.isEmpty(nullDataCd)) {
                if (addAll)
                    airToxicsString += tmnoc + delim;
                else if (addTds)
                    airToxicsString += ttds + delim;
                /*
                 * null data code is empty
                 */
                airToxicsString += delim;
            } else {
                /*
                 * there is a null data code, so the reported sample value is
                 * left empty
                 */
                airToxicsString += delim + nullDataCd + delim;
            }
            airToxicsString += collectionFreq + delim;
            /*
             * fields #16 through #26 and #28 are not used and are empty
             */
            airToxicsString += delim + delim + delim + delim + delim + delim + delim + delim +
                               delim + delim + delim;
            if (addAll)
                airToxicsString += tnmocMDL + delim;
            else if (addTds)
                airToxicsString += ttdsMDL + delim;
            airToxicsStrings.add(airToxicsString);
        }
        return airToxicsStrings;
    }

    /**
     * creates air toxics precision strings from a duplicate or replicate sample
     */
    private ArrayList<String> getAirToxicsPrecisionString(SampleManager1 sm, Integer testId,
                                                          String originalAccession, String action,
                                                          String stateCode, String countyCode,
                                                          String siteId, String poc,
                                                          String reportedUnit, String date,
                                                          boolean addAll, boolean addTds,
                                                          Integer analysisId) throws Exception {
        int durationCode, methodCode;
        Double originalTtds, ttds, originalTmnoc, tmnoc;
        Integer oaid;
        ResultViewDO sr, osr;
        SampleManager1 osm;
        String precision, parameter;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits;
        ArrayList<String> precisions;
        HashMap<Integer, ResultViewDO> results, originalResults;
        HashMap<Integer, Integer> analyses;

        originalTmnoc = tmnoc = originalTtds = ttds = null;
        if (addAll) {
            originalTmnoc = 0.0;
            tmnoc = 0.0;
        } else if (addTds) {
            originalTtds = 0.0;
            ttds = 0.0;
        }
        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);

        /*
         * find if the sample is a replicate or a duplicate
         */
        oaid = null;
        analyses = new HashMap<Integer, Integer>();
        for (AnalysisViewDO data : getAnalyses(sm)) {
            analyses.put(data.getId(), data.getTestId());
            if ( !data.getId().equals(analysisId) && testId.equals(data.getTestId()) &&
                Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
                oaid = data.getId();
        }

        /*
         * get the original sample of a duplicate
         */
        osm = null;
        if ( !DataBaseUtil.isEmpty(originalAccession)) {
            osm = originalSamples.get(Integer.parseInt(originalAccession));
            for (AnalysisViewDO data : getAnalyses(osm)) {
                if (testId.equals(data.getTestId()) &&
                    Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()))
                    oaid = data.getId();
            }
        }

        /*
         * create a map of all results for the original sample and the
         * duplicate/replicate sample
         */
        precisions = new ArrayList<String>();
        results = new HashMap<Integer, ResultViewDO>();
        originalResults = new HashMap<Integer, ResultViewDO>();
        for (ResultViewDO data : getResults(sm)) {
            if (data.getIsColumn().equals("Y"))
                continue;
            if (data.getAnalysisId().equals(analysisId))
                results.put(data.getTestAnalyteId(), data);
            if (data.getAnalysisId().equals(oaid))
                originalResults.put(data.getTestAnalyteId(), data);
        }
        if (osm != null) {
            for (ResultViewDO data : getResults(osm)) {
                if (data.getIsColumn().equals("Y"))
                    continue;
                if (data.getAnalysisId().equals(oaid))
                    originalResults.put(data.getTestAnalyteId(), data);
            }
        }

        durationCode = methodCode = 0;
        /*
         * create a string for each analyte
         */
        for (Integer key : results.keySet()) {
            sr = results.get(key);
            osr = originalResults.get(key);
            if (osr == null)
                continue;
            parameter = analyteCodes.get(sr.getAnalyteId());
            ap = analyteParameters.get(sr.getAnalyteId());
            if (ap.getP2() != null && ap.getP3() != null) {
                methodCode = ap.getP2().intValue();
                durationCode = ap.getP3().intValue();
            }
            precision = buildSiteInfo(precisionType,
                                      action,
                                      stateCode,
                                      countyCode,
                                      siteId,
                                      parameter,
                                      poc) +
                        precisionId +
                        delim +
                        durationCode +
                        delim +
                        reportedUnit +
                        delim +
                        threeDigits.format(methodCode) + delim + date + delim;

            if (osr.getValue().contains("<")) {
                precision += "0" + delim;
            } else {
                precision += osr.getValue() + delim;
                if (addAll)
                    originalTmnoc += Double.parseDouble(osr.getValue());
                if (addTds)
                    originalTtds += Double.parseDouble(osr.getValue());
            }
            precision += methodCode + delim;
            if (sr.getValue().contains("<")) {
                precision += "0" + delim;
            } else {
                precision += sr.getValue() + delim;
                if (addAll)
                    tmnoc += Double.parseDouble(sr.getValue());
                if (addTds)
                    ttds += Double.parseDouble(sr.getValue());
            }

            /*
             * if either of the values is zero, a string is not created, but the
             * values are still added to the total for the extra analytes
             */
            if (osr.getValue().contains("<") || sr.getValue().contains("<"))
                continue;
            /*
             * fields #16 through #18 are not used and are empty
             */
            precision += delim + delim + delim;

            precisions.add(precision);
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
            precision = buildSiteInfo(precisionType,
                                      action,
                                      stateCode,
                                      countyCode,
                                      siteId,
                                      parameter,
                                      poc);
            precision += precisionId + delim;
            if (addAll)
                precision += tnmocDurationCode + delim;
            else if (addTds)
                precision += ttdsDurationCode + delim;
            precision += reportedUnit + delim;
            if (addAll)
                precision += tnmocMethodCode + delim;
            else if (addTds)
                precision += ttdsMethodCode + delim;
            precision = date + delim;
            if (addAll)
                precision += originalTmnoc + delim;
            else if (addTds)
                precision += originalTtds + delim;
            if (addAll)
                precision += tnmocMethodCode + delim + tmnoc + delim;
            else if (addTds)
                precision += ttdsMethodCode + delim + ttds + delim;
            /*
             * fields #16 through #18 are not used and are empty
             */
            precision += delim + delim + delim;
            precisions.add(precision);
        }
        return precisions;
    }

    /**
     * Get metals air quality strings for sample
     */
    private ArrayList<String> getMetalsStrings(SampleManager1 sm, Integer testId, String action,
                                               Integer analysisId, boolean pressureTempString) throws Exception {
        String stateCode, countyCode, siteId, poc, volume, nullDataCode, filterLotBlank, stripsPerFilter, collectionFreq, date, reportedUnit, pressure, temperature;
        SimpleDateFormat dateTimeFormat;
        ArrayList<String> metalsStrings;
        HashMap<String, String> auxData;

        reportedUnit = null;

        /*
         * replicate and performance evaluation samples do not produce strings
         */
        if ("r".equalsIgnoreCase(sm.getSample().getClientReference().substring(2, 3)) ||
            "pep".equalsIgnoreCase(sm.getSample().getClientReference().substring(0, 3)))
            return null;

        for (AnalysisViewDO data : getAnalyses(sm)) {
            if (data.getId().equals(analysisId))
                reportedUnit = unitCodes.get(data.getUnitOfMeasureId());
        }
        metalsStrings = new ArrayList<String>();
        dateTimeFormat = new SimpleDateFormat("yyyyMMdd");

        /*
         * get auxiliary data from the sample
         */
        auxData = auxDataHelper.fillAirQualityAuxData(sm);
        volume = auxData.get(AuxDataHelperBean.VOLUME);
        pressure = auxData.get(AuxDataHelperBean.PRESSURE);
        temperature = auxData.get(AuxDataHelperBean.TEMPERATURE);
        nullDataCode = auxData.get(AuxDataHelperBean.NULL_DATA_CODE);
        filterLotBlank = auxData.get(AuxDataHelperBean.FILTER_LOT_BLANK);
        stripsPerFilter = auxData.get(AuxDataHelperBean.STRIPS_PER_FILTER);
        stateCode = auxData.get(AuxDataHelperBean.STATE_CODE);
        countyCode = auxData.get(AuxDataHelperBean.COUNTY_CODE);
        siteId = auxData.get(AuxDataHelperBean.SITE_ID);
        poc = auxData.get(AuxDataHelperBean.POC);
        collectionFreq = auxData.get(AuxDataHelperBean.COLLECTION_FREQUENCY);

        date = dateTimeFormat.format(sm.getSample().getCollectionDate().getDate());
        if (pressureTempString == false) {
            metalsStrings.addAll(getPressureTemperatureStrings(action,
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
            metalsStrings.add(getMetalsSampleString(sm,
                                                    testId,
                                                    stripsPerFilter,
                                                    volume,
                                                    filterLotBlank,
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
            metalsStrings.add(getMetalsBlankString(sm,
                                                   testId,
                                                   stripsPerFilter,
                                                   volume,
                                                   filterLotBlank,
                                                   action,
                                                   stateCode,
                                                   countyCode,
                                                   siteId,
                                                   poc,
                                                   reportedUnit,
                                                   date,
                                                   nullDataCode,
                                                   analysisId));
            metalsStrings.add(getMetalsAccuracyString(sm,
                                                      testId,
                                                      action,
                                                      stateCode,
                                                      countyCode,
                                                      siteId,
                                                      poc,
                                                      reportedUnit,
                                                      date,
                                                      analysisId));
        } else if (testId.equals(metalManganeseTest)) {
            metalsStrings.add(getMetalsSampleString(sm,
                                                    testId,
                                                    stripsPerFilter,
                                                    volume,
                                                    filterLotBlank,
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
            metalsStrings.add(getMetalsBlankString(sm,
                                                   testId,
                                                   stripsPerFilter,
                                                   volume,
                                                   filterLotBlank,
                                                   action,
                                                   stateCode,
                                                   countyCode,
                                                   siteId,
                                                   poc,
                                                   reportedUnit,
                                                   date,
                                                   nullDataCode,
                                                   analysisId));
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            metalsStrings.add(getMetalsAccuracyString(sm,
                                                      testId,
                                                      action,
                                                      stateCode,
                                                      countyCode,
                                                      siteId,
                                                      poc,
                                                      reportedUnit,
                                                      date,
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
        String pressureString, temperatureString;
        ArrayList<String> metalStrings;

        metalStrings = new ArrayList<String>();

        pressureString = buildSiteInfo(rawDataType,
                                       action,
                                       stateCode,
                                       countyCode,
                                       siteId,
                                       pressureParameter,
                                       poc);
        temperatureString = buildSiteInfo(rawDataType,
                                          action,
                                          stateCode,
                                          countyCode,
                                          siteId,
                                          temperatureParameter,
                                          poc);
        pressureString += pressureDurationCode + delim + pressureReportedUnit + delim +
                          pressureMethodCode + delim + date + delim + time + delim;
        temperatureString += temperatureDurationCode + delim + temperatureReportedUnit + delim +
                             temperatureMethodCode + delim + date + delim + time + delim;
        if (DataBaseUtil.isEmpty(nullDataCode)) {
            pressureString += pressure + delim + delim;
            temperatureString += temperature + delim + delim;
        } else {
            pressureString += delim + nullDataCode + delim;
            temperatureString += delim + nullDataCode + delim;
        }
        pressureString += collectionFreq + delim + delim + delim + delim + delim + delim + delim +
                          delim + delim + delim + delim + delim + delim;
        temperatureString += collectionFreq + delim + delim + delim + delim + delim + delim +
                             delim + delim + delim + delim + delim + delim + delim;

        if (pressure == null)
            metalStrings.add(null);
        else
            metalStrings.add(pressureString);
        if (temperature == null)
            metalStrings.add(null);
        else
            metalStrings.add(temperatureString);
        return metalStrings;
    }

    /**
     * create a string for metal raw data
     */
    private String getMetalsSampleString(SampleManager1 sm, Integer testId, String stripsPerFilter,
                                         String volume, String filterLotBlank, String action,
                                         String stateCode, String countyCode, String siteId,
                                         String poc, String reportedUnit, String date,
                                         String nullDataCode, String collectionFreq,
                                         Integer analysisId) {
        boolean lead, cbFlag;
        int durationCode, methodCode;
        Double sampleMdl, filterMdl, sampleValue, sampleVolume, filterBlank;
        Integer spf;
        String metalString, value, mdl, parameter, alternateMethodDetectableLimit;
        AnalyteParameterViewDO ap;
        DecimalFormat threeDigits;

        threeDigits = new DecimalFormat("#");
        threeDigits.setMinimumIntegerDigits(3);

        lead = false;
        cbFlag = false;
        if (testId.equals(metalLeadTest)) {
            lead = true;
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            lead = false;
        }

        value = mdl = parameter = alternateMethodDetectableLimit = null;
        durationCode = methodCode = 0;
        metalString = null;
        for (ResultViewDO data : getResults(sm)) {
            if ( !DataBaseUtil.isSame(data.getAnalysisId(), analysisId))
                continue;
            if (DataBaseUtil.isSame(data.getIsColumn(), "N")) {
                value = data.getValue();
                parameter = analyteCodes.get(data.getAnalyteId());
                ap = analyteParameters.get(data.getAnalyteId());
                alternateMethodDetectableLimit = df.format(ap.getP1().doubleValue());
                methodCode = ap.getP2().intValue();
                durationCode = ap.getP3().intValue();
            } else if (DataBaseUtil.isSame(data.getAnalyte(), "MDL")) {
                mdl = data.getValue();
            }
            if (value == null || mdl == null)
                continue;

            filterMdl = Double.parseDouble(mdl);
            filterBlank = Double.parseDouble(filterLotBlank);
            sampleValue = Double.parseDouble(value);
            sampleVolume = Double.parseDouble(volume);
            spf = Integer.parseInt(stripsPerFilter);

            if (filterBlank - filterMdl > 0)
                cbFlag = true;
            metalString = buildSiteInfo(rawDataType,
                                        action,
                                        stateCode,
                                        countyCode,
                                        siteId,
                                        parameter,
                                        poc);
            metalString += durationCode + delim + reportedUnit + delim +
                           threeDigits.format(methodCode) + delim + date + delim + time + delim;
            if (DataBaseUtil.isEmpty(nullDataCode)) {
                if (lead) {
                    metalString += formatLeadValue( (sampleValue * spf - filterBlank) /
                                                   sampleVolume) +
                                   delim + delim;
                } else if (cbFlag) {
                    metalString += formatManganseseValue(1000 * (sampleValue * spf - filterBlank) /
                                                         sampleVolume) +
                                   delim + delim;
                } else {
                    metalString += formatManganseseValue(1000 * sampleValue * spf / sampleVolume) +
                                   delim + delim;
                }
            } else {
                metalString += delim + nullDataCode + delim;
            }
            metalString += collectionFreq + delim + delim;

            if (lead) {
                sampleMdl = filterMdl / sampleVolume;
                if (sampleValue < filterMdl)
                    metalString += md;
                metalString += delim;
                if (filterMdl < sampleValue && sampleValue < (10 * filterMdl))
                    metalString += sq;
                metalString += delim + delim + delim + delim;
            } else {
                sampleMdl = 1000 * (filterMdl / sampleVolume);
                if (cbFlag)
                    metalString += cb;
                metalString += delim;
                if (sampleValue < filterMdl)
                    metalString += md;
                metalString += delim;
                if (filterMdl < sampleValue && sampleValue < (10 * filterMdl))
                    metalString += sq;
                metalString += delim;
                if (sampleValue < 0 && Math.abs(sampleValue) > sampleMdl)
                    metalString += da;
                metalString += delim;
                if (sampleValue > 10 * sampleMdl &&
                    sm.getSample().getClientReference() != null &&
                    (sm.getSample().getClientReference().contains("qtb") || sm.getSample()
                                                                              .getClientReference()
                                                                              .contains("qfb"))) {
                    metalString += fb;
                }
                metalString += delim;
            }
            metalString += delim + delim + delim + delim + delim + alternateMethodDetectableLimit +
                           delim;
            if ( !lead) {
                metalString += 1000 * filterBlank / sampleVolume;
            }
        }
        return metalString;
    }

    /**
     * create a string for metal blank data
     */
    private String getMetalsBlankString(SampleManager1 sm, Integer testId, String stripsPerFilter,
                                        String volume, String filterLotBlank, String action,
                                        String stateCode, String countyCode, String siteId,
                                        String poc, String reportedUnit, String date,
                                        String nullDataCode, Integer analysisId) {
        boolean lead;
        int durationCode, methodCode;
        String metalString, value, mdl, parameter;
        AnalyteParameterViewDO ap;

        lead = false;
        if (testId.equals(metalLeadTest)) {
            lead = true;
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            lead = false;
        }

        metalString = value = mdl = parameter = null;
        durationCode = methodCode = 0;
        for (ResultViewDO data : getResults(sm)) {
            if ( !DataBaseUtil.isSame(data.getAnalysisId(), analysisId))
                continue;
            if (DataBaseUtil.isSame(data.getIsColumn(), "N")) {
                value = data.getValue();
                parameter = analyteCodes.get(data.getAnalyteId());
                ap = analyteParameters.get(data.getAnalyteId());
                methodCode = ap.getP2().intValue();
                durationCode = ap.getP3().intValue();
            } else if (DataBaseUtil.isSame(data.getAnalyte(), "MDL")) {
                mdl = data.getValue();
            }
            if (value == null || mdl == null)
                continue;

            metalString = buildSiteInfo(blanksDataType,
                                        action,
                                        stateCode,
                                        countyCode,
                                        siteId,
                                        parameter,
                                        poc);
            metalString += durationCode + delim + reportedUnit + delim + methodCode + delim;
            if (sm.getSample().getClientReference() != null &&
                sm.getSample().getClientReference().contains("qtb")) {
                metalString += tripBlankType;
            } else if (sm.getSample().getClientReference() != null &&
                       sm.getSample().getClientReference().contains("qfb")) {
                metalString += fieldBlankType;
            }
            metalString += delim + date + delim + time + delim + filterLotBlank + delim + delim;

            metalString += cb + delim + md + delim + sq + delim + da + delim + fb + delim + delim +
                           delim + delim + delim + delim + delim;
        }
        return metalString;
    }

    /**
     * create a string for metal accuracy data
     */
    private String getMetalsAccuracyString(SampleManager1 sm, Integer testId, String action,
                                           String stateCode, String countyCode, String siteId,
                                           String poc, String reportedUnit, String date,
                                           Integer analysisId) {
        boolean lead, commercial;
        int durationCode, methodCode;
        String metalString, value, mdl, quarter, year, parameter;
        Datetime d;
        AnalyteParameterViewDO ap;

        metalString = value = mdl = parameter = null;
        durationCode = methodCode = 0;
        commercial = lead = false;
        if (testId.equals(metalLeadTest)) {
            lead = true;
        } else if (testId.equals(metalManganeseAccuracyTest)) {
            lead = false;
        }
        d = sm.getSample().getCollectionDate();
        year = DataBaseUtil.toString(d.get(Datetime.YEAR));
        if (d.get(Datetime.MONTH) <= 3)
            quarter = "Q1";
        else if (d.get(Datetime.MONTH) <= 6)
            quarter = "Q2";
        else if (d.get(Datetime.MONTH) <= 9)
            quarter = "Q3";
        else
            quarter = "Q4";

        for (ResultViewDO data : getResults(sm)) {
            if ( !DataBaseUtil.isSame(data.getAnalysisId(), analysisId))
                continue;
            if (DataBaseUtil.isSame(data.getIsColumn(), "N")) {
                value = data.getValue();
                parameter = analyteCodes.get(data.getAnalyteId());
                ap = analyteParameters.get(data.getAnalyteId());
                methodCode = ap.getP2().intValue();
                durationCode = ap.getP3().intValue();
            } else if (DataBaseUtil.isSame(data.getAnalyte(), "MDL")) {
                mdl = data.getValue();
            }
            if (value == null || mdl == null)
                continue;

            metalString = buildSiteInfo(accuracyDataType,
                                        action,
                                        stateCode,
                                        countyCode,
                                        siteId,
                                        parameter,
                                        poc);
            if (sm.getSample().getClientReference().startsWith("qfs") &&
                !sm.getSample().getClientReference().startsWith("qfs rti")) {
                commercial = true;
                metalString += "1";
            } else if (sm.getSample().getClientReference().startsWith("qfs rti") && lead) {
                commercial = false;
                metalString += "2";
            }
            metalString += delim + durationCode + delim + reportedUnit + delim + methodCode +
                           delim + year + delim + quarter + delim + date + delim +
                           "AUDIT ONLY BY RO" + delim;
            if (commercial) {
                metalString += "COMMERCIAL CRM";
            } else if (lead) {
                metalString += "OTHER CHEMICAL STANDARDS";
            }
            metalString += delim + "ANALYTICAL" + delim + "PE" + delim;
            if (commercial)
                metalString += "1";
            else if (lead)
                metalString += "2";
            metalString += delim + delim + delim;
            // TODO level 1 and level 2 actual value and indicated value
        }
        return metalString;
    }

    /**
     * compute and format sulfate air quality value
     */
    private String getSulfateValue(String result, String volume) {
        DecimalFormat df = new DecimalFormat("#.00");
        return DataBaseUtil.toString(df.format(Double.parseDouble(result) /
                                               Double.parseDouble(volume)));
    }

    /**
     * compute and format nitrate air quality value
     */
    private String getNitrateValue(String result, String volume) {
        DecimalFormat df = new DecimalFormat("#.00");
        return DataBaseUtil.toString(df.format( (Double.parseDouble(result) * nitrateConstant) /
                                               Double.parseDouble(volume)));
    }

    /**
     * compute and format lead air quality value
     */
    private String formatLeadValue(Double value) {
        return truncateDecimal(value, 3);
    }

    /**
     * compute and format manganese air quality value
     */
    private String formatManganseseValue(Double value) {
        DecimalFormat df = new DecimalFormat("###.0");
        return DataBaseUtil.toString(df.format(value));
    }

    /**
     * truncate a double to the given number of digits after the decimal
     */
    private static String truncateDecimal(double x, int numberofDecimals) {
        if (x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
                                                              BigDecimal.ROUND_FLOOR).toString();
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
                                                              BigDecimal.ROUND_CEILING).toString();
        }
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
    private void getAirAnalyteCodes(Set<Integer> analyteIds) throws Exception {
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
    private void getAirUnitCodes(Set<Integer> unitIds) throws Exception {
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
    private void getAirAnalyteParameters(Set<Integer> testIds) throws Exception {
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
    private String buildSiteInfo(String type, String action, String stateCode, String countyCode,
                                 String siteId, String parameter, String poc) {
        return type + delim + action.substring(0, 1) + delim + stateCode + delim + countyCode +
               delim + siteId + delim + parameter + delim + poc + delim;
    }
}
