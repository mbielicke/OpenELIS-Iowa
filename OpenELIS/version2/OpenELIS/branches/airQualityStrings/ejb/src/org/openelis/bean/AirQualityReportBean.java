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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
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
    private SystemVariableBean               systemVariable;

    @EJB
    private SessionCacheBean                 session;

    @EJB
    private SampleManager1Bean               sample;

    @EJB
    private CategoryCacheBean                categoryCache;

    private static final String              delim           = "|", rawDataType = "RD",
                    precisionType = "RP", accuracyDataType = "RA", blanksDataType = "RB",
                    time = "00:00", precisionId = "1", to11 = "to-11", to12 = "to-12",
                    tolualdehyde = "Tolualdehyde";

    private static final Double              nitrateConstant = 4.4;

    private ArrayList<String>                sulfateAnalytes, nitrateAnalytes;

    private HashMap<Integer, SampleManager1> originalSamples;

    private static final Logger              log             = Logger.getLogger("openelis");

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
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * Execute the report and send its output to specified location
     */
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception {
        Integer analyteCount;
        ReportStatus status;
        String val, frDate, tDate, accession, action, reportTo, attention, tempArray[], analyteArray[], qualifierCode;
        QueryData field;
        Query query;
        ArrayList<SampleManager1> sms;
        ArrayList<QueryData> fields;
        ArrayList<String> stringList, qualifierStrings;
        ArrayList<Integer> sulfateNitrateTests, airToxicsTests, metalsTests;
        ArrayList<Integer> problemSamples;
        HashMap<String, ArrayList<SampleManager1>> samples;
        HashMap<String, String> strings;
        HashMap<String, ArrayList<String>> sulfateStrings, nitrateStrings, airToxicRawStrings, airToxicPrecisionStrings, metalRawStrings, metalAccuracyStrings, metalBlanksStrings;
        HashMap<String, QueryData> param;

        /*
         * get system variables
         */
        try {
            val = systemVariable.fetchByName("air_report_to").getValue();
            tempArray = val.split(";");
            reportTo = DataBaseUtil.trim(tempArray[0]);
            attention = DataBaseUtil.trim(tempArray[1]);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("air_report_to"),
                    e);
            throw e;
        }

        try {
            val = systemVariable.fetchByName("air_sulfate_nitrate_tests").getValue();
            tempArray = val.split(delim);
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
            val = systemVariable.fetchByName("air_toxics_tests").getValue();
            tempArray = val.split(delim);
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
            val = systemVariable.fetchByName("air_metals_tests").getValue();
            tempArray = val.split(delim);
            metalsTests = new ArrayList<Integer>();
            for (int i = 0; i < tempArray.length; i++ ) {
                metalsTests.add(Integer.parseInt(tempArray[i]));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("air_metals_tests"),
                    e);
            throw e;
        }

        try {
            val = systemVariable.fetchByName("air_qualifier_code").getValue();
            tempArray = val.split(delim);
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

        /*
         * separate the samples for each site, find duplicate samples, and find
         * problem samples
         */
        samples = new HashMap<String, ArrayList<SampleManager1>>();
        originalSamples = new HashMap<Integer, SampleManager1>();
        problemSamples = null;
        for (SampleManager1 sm : sms) {
            analyteCount = 0;
            if (DataBaseUtil.isDifferent(Constants.dictionary().SAMPLE_RELEASED, sm.getSample()
                                                                                   .getStatusId())) {
                if (problemSamples == null)
                    problemSamples = new ArrayList<Integer>();
                problemSamples.add(sm.getSample().getAccessionNumber());
                continue;
            }

            /*
             * find air toxic duplicates
             */
            if (getAnalyses(sm) != null) {
                for (AnalysisViewDO data : getAnalyses(sm)) {
                    if (DataBaseUtil.isDifferent(Constants.dictionary().ANALYSIS_RELEASED,
                                                 data.getStatusId())) {
                        analyteCount++ ;
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
            if (analyteCount < 1) {
                if (problemSamples == null)
                    problemSamples = new ArrayList<Integer>();
                problemSamples.add(sm.getSample().getAccessionNumber());
                continue;
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
         * create a list of original samples associated with the duplicates
         */
        for (SampleManager1 sm : sample.fetchByIds(new ArrayList<Integer>(originalSamples.keySet()))) {
            originalSamples.put(sm.getSample().getAccessionNumber(), sm);
        }

        sulfateStrings = nitrateStrings = airToxicRawStrings = airToxicPrecisionStrings = metalRawStrings = null;
        for (String key : samples.keySet()) {
            for (SampleManager1 sm : samples.get(key)) {
                if (getResults(sm) != null) {
                    for (AnalysisViewDO data : getAnalyses(sm)) {
                        if (sulfateNitrateTests.contains(data.getTestId())) {
                            /*
                             * get sulfate and nitrate strings
                             */
                            strings = getSulfateNitrateString(sm, action);
                            if (sulfateStrings == null)
                                sulfateStrings = new HashMap<String, ArrayList<String>>();
                            if (sulfateStrings.get(key) == null)
                                sulfateStrings.put(key, new ArrayList<String>());
                            sulfateStrings.get(key).add(strings.get("sulfate"));
                            if (nitrateStrings == null)
                                nitrateStrings = new HashMap<String, ArrayList<String>>();
                            if (nitrateStrings.get(key) == null)
                                nitrateStrings.put(key, new ArrayList<String>());
                            nitrateStrings.get(key).add(strings.get("nitrate"));
                        } else if (airToxicsTests.contains(data.getTestId())) {
                            /*
                             * get air toxics strings
                             */
                            stringList = getAirToxicsStrings(sm,
                                                             action,
                                                             qualifierCode,
                                                             qualifierStrings);
                            if (airToxicRawStrings == null)
                                airToxicRawStrings = new HashMap<String, ArrayList<String>>();
                            if (airToxicRawStrings.get(key) == null)
                                airToxicRawStrings.put(key, new ArrayList<String>());
                            airToxicRawStrings.get(key).addAll(stringList);
                        } else if (metalsTests.contains(data.getTestId())) {
                            /*
                             * get metals strings
                             */
                            stringList = getMetalsStrings(sm, action);
                            if (metalRawStrings == null)
                                metalRawStrings = new HashMap<String, ArrayList<String>>();
                            if (airToxicRawStrings.get(key) == null)
                                metalRawStrings.put(key, new ArrayList<String>());
                            metalRawStrings.get(key).addAll(stringList);
                        } else {
                            if (problemSamples == null)
                                problemSamples = new ArrayList<Integer>();
                            problemSamples.add(sm.getSample().getAccessionNumber());
                        }
                    }
                }
            }
        }

        return status;
    }

    private String buildSiteInfo(String type, String action, String stateCode, String countyCode,
                                 String siteId, String parameter, String poc) {
        return type + delim + action.substring(0, 1) + delim + stateCode + delim + countyCode +
               delim + siteId + delim + parameter + delim + poc + delim;
    }

    /**
     * Get sulfate and nitrate air quality strings for sample
     */
    private HashMap<String, String> getSulfateNitrateString(SampleManager1 sm, String action) {
        String sulfateString, nitrateString, stateCode, countyCode, siteId, poc, volume, nullDataCd, collectionFreq, date, end;
        SimpleDateFormat dateTimeFormat;
        HashMap<String, String> sulfateNitrateStrings;
        // TODO
        String parameter, durationCd, reportedUnit, methodCd, alternateMethodDetectableLimit;
        parameter = durationCd = reportedUnit = methodCd = alternateMethodDetectableLimit = null;

        dateTimeFormat = new SimpleDateFormat("YYYYMMDD");
        stateCode = countyCode = siteId = poc = volume = nullDataCd = collectionFreq = null;
        AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                null,
                                                null,
                                                volume,
                                                null,
                                                nullDataCd,
                                                null,
                                                null,
                                                null,
                                                stateCode,
                                                countyCode,
                                                siteId,
                                                poc,
                                                collectionFreq);

        sulfateString = buildSiteInfo(rawDataType,
                                      action,
                                      stateCode,
                                      countyCode,
                                      siteId,
                                      parameter,
                                      poc);

        sulfateString += durationCd + delim;
        sulfateString += reportedUnit + delim;
        sulfateString += methodCd + delim;
        date = dateTimeFormat.format(sm.getSample().getCollectionDate());
        sulfateString += date + delim;
        sulfateString += time + delim;
        nitrateString = sulfateString;
        if (DataBaseUtil.isEmpty(nullDataCd)) {
            if (getResults(sm) != null) {
                for (ResultViewDO data : getResults(sm)) {
                    if (sulfateAnalytes.contains(data.getAnalyte())) {
                        sulfateString += getSulfateValue(data.getValue(), volume) + delim;
                        /*
                         * null data code is empty
                         */
                        sulfateString += delim;
                    } else if (nitrateAnalytes.contains(data.getAnalyte())) {
                        nitrateString += getNitrateValue(data.getValue(), volume) + delim;
                        /*
                         * null data code is empty
                         */
                        nitrateString += delim;
                    }
                }
            }
            return null;
        } else {
            /*
             * there is a null data code, so the reported sample value is left
             * empty
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
        end += alternateMethodDetectableLimit + delim;

        sulfateString += end;
        nitrateString += end;

        sulfateNitrateStrings = new HashMap<String, String>();
        sulfateNitrateStrings.put("sulfate", sulfateString);
        sulfateNitrateStrings.put("nitrate", nitrateString);
        return sulfateNitrateStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private ArrayList<String> getAirToxicsStrings(SampleManager1 sm, String action,
                                                  String qualifierCode,
                                                  ArrayList<String> qualifierStrings) throws Exception {
        boolean addTolualdehydes, addAll;
        Double totalTolualdehydes, tmnocSpeciated;
        String airToxicsString, originalSampleNumber, stateCode, countyCode, siteId, poc, volume, nullDataCd, collectionFreq, date;
        SimpleDateFormat dateTimeFormat;
        ArrayList<String> airToxicsStrings;
        // TODO
        String parameter, durationCode, reportedUnit, methodCode, alternateMethodDetectableLimit;
        parameter = durationCode = reportedUnit = methodCode = alternateMethodDetectableLimit = null;

        /*
         * find if there needs to be any extra strings created
         */
        addAll = addTolualdehydes = false;
        tmnocSpeciated = totalTolualdehydes = null;
        if (getAnalyses(sm) != null) {
            for (AnalysisViewDO data : getAnalyses(sm)) {
                if (data.getMethodName().contains(to11)) {
                    addTolualdehydes = true;
                    totalTolualdehydes = (double)0;
                    break;
                } else if (data.getMethodName().contains(to12)) {
                    addAll = true;
                    tmnocSpeciated = (double)0;
                    break;
                }
            }
        }

        /*
         * get the data for the sample
         */
        dateTimeFormat = new SimpleDateFormat("YYYYMMDD");
        originalSampleNumber = stateCode = countyCode = siteId = poc = volume = nullDataCd = collectionFreq = null;
        AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                originalSampleNumber,
                                                null,
                                                volume,
                                                null,
                                                nullDataCd,
                                                null,
                                                null,
                                                null,
                                                stateCode,
                                                countyCode,
                                                siteId,
                                                poc,
                                                collectionFreq);
        date = dateTimeFormat.format(sm.getSample().getCollectionDate());

        /*
         * if the original sample number is not empty, then precision strings
         * need to be created instead of raw data strings
         */
        if ( !DataBaseUtil.isEmpty(originalSampleNumber)) {
            return getAirToxicsPrecisionString(sm,
                                               originalSampleNumber,
                                               action,
                                               stateCode,
                                               countyCode,
                                               siteId,
                                               parameter,
                                               poc,
                                               durationCode,
                                               reportedUnit,
                                               methodCode,
                                               date,
                                               addAll,
                                               addTolualdehydes);
        }

        /*
         * go through all the analytes and create a string for each one
         */
        airToxicsStrings = new ArrayList<String>();
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {

                airToxicsString = buildSiteInfo(rawDataType,
                                                action,
                                                stateCode,
                                                countyCode,
                                                siteId,
                                                parameter,
                                                poc);

                airToxicsString += durationCode + delim;
                airToxicsString += reportedUnit + delim;
                airToxicsString += methodCode + delim;
                airToxicsString += date + delim;
                airToxicsString += time + delim;
                if (DataBaseUtil.isEmpty(nullDataCd)) {
                    if (data.getValue().contains("<")) {
                        /*
                         * the value is less than the minimum value, so we round
                         * down to zero
                         */
                        airToxicsString += "0" + delim;
                    } else {
                        airToxicsString += data.getValue() + delim;
                        /*
                         * null data code is empty
                         */
                        airToxicsString += delim;
                        if (addAll)
                            tmnocSpeciated += Double.parseDouble(data.getValue());
                        if (addTolualdehydes && data.getAnalyte().contains(tolualdehyde))
                            totalTolualdehydes += Double.parseDouble(data.getValue());
                    }
                } else {
                    /*
                     * there is a null data code, so the reported sample value
                     * is left empty
                     */
                    airToxicsString += delim + nullDataCd + delim;
                }
                airToxicsString = collectionFreq + delim;
                /*
                 * fields #16 and #18 through #26 and #28 are not used and are
                 * empty
                 */
                airToxicsString += delim;
                // TODO check if the qualifier is "under limit"
                if (qualifierStrings.contains(data.getValue()))
                    airToxicsString += qualifierCode + delim;
                airToxicsString += delim + delim + delim + delim + delim + delim + delim + delim +
                                   delim;
                airToxicsString += alternateMethodDetectableLimit + delim;
                airToxicsStrings.add(airToxicsString);
            }
        }
        return airToxicsStrings;
    }

    /**
     * creates air toxics precision strings from a duplicate or replicate sample
     */
    private ArrayList<String> getAirToxicsPrecisionString(SampleManager1 sm,
                                                          String originalSampleNumber,
                                                          String action, String stateCode,
                                                          String countyCode, String siteId,
                                                          String parameter, String poc,
                                                          String durationCode, String reportedUnit,
                                                          String methodCode, String date,
                                                          boolean addAll, boolean addTolualdehydes) throws Exception {
        Double originalTotalTolualdehydes, totalTolualdehydes, originalTmnocSpeciated, tmnocSpeciated;
        ResultViewDO sr, osr;
        SampleManager1 osm;
        String precisionString, siteInfo;
        ArrayList<String> precisionStrings;
        HashMap<Integer, ResultViewDO> sampleResults, originalSampleResults;

        originalTmnocSpeciated = tmnocSpeciated = originalTotalTolualdehydes = totalTolualdehydes = null;
        if (addAll) {
            originalTmnocSpeciated = new Double(0);
            tmnocSpeciated = new Double(0);
        } else if (addTolualdehydes) {
            originalTotalTolualdehydes = new Double(0);
            totalTolualdehydes = new Double(0);
        }
        if ( !DataBaseUtil.isEmpty(originalSampleNumber))
            osm = originalSamples.get(Integer.parseInt(originalSampleNumber));

        precisionStrings = new ArrayList<String>();
        sampleResults = new HashMap<Integer, ResultViewDO>();
        originalSampleResults = new HashMap<Integer, ResultViewDO>();
        for (ResultViewDO data : getResults(sm)) {
            sampleResults.put(data.getAnalyteId(), data);
        }
//        for (ResultViewDO data : getResults(osm)) {
//            originalSampleResults.put(data.getAnalyteId(), data);
//        }
        siteInfo = buildSiteInfo(precisionType,
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
                   methodCode +
                   delim + date + delim;

        /*
         * create a string for each analyte
         */
        for (Integer key : sampleResults.keySet()) {
            sr = sampleResults.get(key);
            osr = originalSampleResults.get(key);
            if (osr == null)
                continue;
            precisionString = siteInfo;
            if (osr.getValue().contains("<")) {
                precisionString += "0" + delim;
            } else {
                precisionString += osr.getValue() + delim;
                if (addAll)
                    originalTmnocSpeciated += Double.parseDouble(osr.getValue());
                if (addTolualdehydes)
                    originalTotalTolualdehydes += Double.parseDouble(osr.getValue());
            }
            precisionString += methodCode + delim;
            if (sr.getValue().contains("<")) {
                precisionString += "0" + delim;
            } else {
                precisionString += sr.getValue() + delim;
                if (addAll)
                    tmnocSpeciated += Double.parseDouble(sr.getValue());
                if (addTolualdehydes)
                    totalTolualdehydes += Double.parseDouble(sr.getValue());
            }
            /*
             * fields #16 through #18 are not used and are empty
             */
            precisionString += delim + delim + delim;

            precisionStrings.add(precisionString);
        }

        /*
         * in specific cases, a string needs to be created for a new analyte
         */
        if (addAll || addTolualdehydes) {
            precisionString = buildSiteInfo(precisionType,
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
                              methodCode + delim + date + delim;
            if (addAll)
                precisionString += originalTmnocSpeciated + delim;
            if (addTolualdehydes)
                precisionString += originalTotalTolualdehydes + delim;
            precisionString += methodCode + delim;
            if (addAll)
                precisionString += tmnocSpeciated + delim;
            if (addTolualdehydes)
                precisionString += totalTolualdehydes + delim;
            /*
             * fields #16 through #18 are not used and are empty
             */
            precisionString += delim + delim + delim;
            precisionStrings.add(precisionString);
        }
        return precisionStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private ArrayList<String> getMetalsStrings(SampleManager1 sm, String action) {
        String metalsString, stateCd, countyCd, siteId, poc, volume, nullDataCd, filterLotBlank, stripsPerFilter, blankSampleNumber, collectionFreq, date;
        SimpleDateFormat dateTimeFormat;
        ArrayList<String> metalsStrings;
        // TODO
        String parameter, durationCd, reportedUnit, methodCd, alternateMethodDetectableLimit;
        parameter = durationCd = reportedUnit = methodCd = alternateMethodDetectableLimit = null;

        metalsStrings = new ArrayList<String>();
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {

                dateTimeFormat = new SimpleDateFormat("YYYYMMDD");
                metalsString = rawDataType + delim;
                metalsString += action + delim;
                stateCd = countyCd = siteId = poc = volume = nullDataCd = collectionFreq = filterLotBlank = stripsPerFilter = blankSampleNumber = null;
                AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                        null,
                                                        null,
                                                        volume,
                                                        null,
                                                        nullDataCd,
                                                        filterLotBlank,
                                                        stripsPerFilter,
                                                        blankSampleNumber,
                                                        stateCd,
                                                        countyCd,
                                                        siteId,
                                                        poc,
                                                        collectionFreq);

                metalsString += stateCd + delim;
                metalsString += countyCd + delim;
                metalsString += siteId + delim;
                metalsString += parameter + delim;
                metalsString += poc + delim;
                metalsString += durationCd + delim;
                metalsString += reportedUnit + delim;
                metalsString += methodCd + delim;
                date = dateTimeFormat.format(sm.getSample().getCollectionDate());
                metalsString += date + delim;
                metalsString += time + delim;
                if (DataBaseUtil.isEmpty(nullDataCd)) {
                    if (data.getValue().contains("<"))
                        metalsString += "0" + delim;
                    else
                        metalsString += data.getValue() + delim;
                    /*
                     * null data code is empty
                     */
                    metalsString += delim;
                } else {
                    /*
                     * there is a null data code, so the reported sample value
                     * is left empty
                     */
                    metalsString += delim + nullDataCd + delim;
                }
                metalsString = collectionFreq + delim;
                /*
                 * fields #16 through #26 are not used and are empty
                 */
                metalsString += delim + delim + delim + delim + delim + delim + delim + delim +
                                delim + delim + delim;
                metalsString += alternateMethodDetectableLimit + delim;
                /*
                 * field #28 is not used and is empty
                 */
                metalsStrings.add(metalsString);
            }
        }
        return metalsStrings;
    }

    private String getMetalsSampleString(SampleManager1 sm, String siteInfo, String durationCode,
                                         String reportedUnit, String methodCode, String date,
                                         String nullDataCode, String collectionFreq) {

        return null;
    }

    private String getMetalsBlankString(SampleManager1 sm, String siteInfo) {

        return null;
    }

    private String getMetalsAccuracyString(SampleManager1 sm, String siteInfo) {

        return null;
    }

    /**
     * Get sulfate air quality string for sample
     */
    private String getSulfateValue(String result, String volume) {
        return DataBaseUtil.toString(Double.parseDouble(result) / Double.parseDouble(volume));
    }

    /**
     * Get nitrate air quality string for sample
     */
    private String getNitrateValue(String result, String volume) {
        return DataBaseUtil.toString( (Double.parseDouble(result) * nitrateConstant) /
                                     Double.parseDouble(volume));
    }

    private ArrayList<OptionListItem> getDictionaryList(String systemName) {
        ArrayList<OptionListItem> l;
        ArrayList<DictionaryDO> d;

        l = new ArrayList<OptionListItem>();
        try {
            d = categoryCache.getBySystemName(systemName).getDictionaryList();
            for (DictionaryDO data : d)
                l.add(new OptionListItem(data.getId().toString(), data.getEntry()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return l;
    }
}
