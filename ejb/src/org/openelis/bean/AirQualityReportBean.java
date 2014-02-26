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

import static org.openelis.manager.SampleManager1Accessor.getResults;

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
    private SystemVariableBean  systemVariable;

    @EJB
    private SessionCacheBean    session;

    @EJB
    private SampleManager1Bean  sample;

    @EJB
    private CategoryCacheBean   categoryCache;

    private static final String delim           = "|";

    private static final String rawDataType     = "RD", time = "00:00";

    private static final Double nitrateConstant = 4.4;

    private static final Logger log             = Logger.getLogger("openelis");

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
        HashMap<String, QueryData> param;
        ReportStatus status;
        String val, frDate, tDate, accession, action, reportTo, attention, reportToArray[];
        QueryData field;
        Query query;
        ArrayList<SampleManager1> sms;
        ArrayList<QueryData> fields;
        ArrayList<String> stringList;
        HashMap<String, ArrayList<SampleManager1>> samples;
        HashMap<String, String> strings;
        HashMap<String, ArrayList<String>> sulfateStrings, nitrateStrings, airToxicStrings, metalStrings;

        try {
            val = systemVariable.fetchByName("air_report_to").getValue();
            reportToArray = val.split(";");
            reportTo = DataBaseUtil.trim(reportToArray[0]);
            attention = DataBaseUtil.trim(reportToArray[1]);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    Messages.get().systemVariable_missingInvalidSystemVariable("air_report_to"),
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
         * separate the samples for each site
         */
        samples = new HashMap<String, ArrayList<SampleManager1>>();
        for (SampleManager1 sm : sms) {
            if ( !samples.keySet().contains(sm.getSampleEnvironmental().getLocation()))
                samples.put(sm.getSampleEnvironmental().getLocation(),
                            new ArrayList<SampleManager1>());
            samples.get(sm.getSampleEnvironmental().getLocation()).add(sm);
        }

        sulfateStrings = nitrateStrings = airToxicStrings = metalStrings = null;
        for (String key : samples.keySet()) {
            for (SampleManager1 sm : samples.get(key)) {
                if (getResults(sm) != null) {
                    for (ResultViewDO data : getResults(sm)) {
                        if ("sulfate".equals(data.getAnalyte()) ||
                            "nitrate".equals(data.getAnalyte())) {
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
                        } else if ("".equals(data.getAnalyte())) {
                            /*
                             * get air toxics strings
                             */
                            stringList = getAirToxicsStrings(sm, action);
                            if (airToxicStrings == null)
                                airToxicStrings = new HashMap<String, ArrayList<String>>();
                            if (airToxicStrings.get(key) == null)
                                airToxicStrings.put(key, new ArrayList<String>());
                            airToxicStrings.get(key).addAll(stringList);
                        } else if ("".equals(data.getAnalyte())) {
                            /*
                             * get metals strings
                             */
                            stringList = getMetalsStrings(sm, action);
                            if (metalStrings == null)
                                metalStrings = new HashMap<String, ArrayList<String>>();
                            if (airToxicStrings.get(key) == null)
                                metalStrings.put(key, new ArrayList<String>());
                            metalStrings.get(key).addAll(stringList);
                        }
                    }
                }
            }
        }

        return status;
    }

    /**
     * Get sulfate and nitrate air quality strings for sample
     */
    private HashMap<String, String> getSulfateNitrateString(SampleManager1 sm, String action) {
        String sulfateString, nitrateString, stateCd, countyCd, siteId, poc, volume, nullDataCd, collectionFreq, date, end;
        SimpleDateFormat dateTimeFormat;
        HashMap<String, String> sulfateNitrateStrings;
        // TODO
        String parameter, durationCd, reportedUnit, methodCd, alternateMethodDetectableLimit;
        parameter = durationCd = reportedUnit = methodCd = alternateMethodDetectableLimit = null;

        dateTimeFormat = new SimpleDateFormat("YYYYMMDD");
        sulfateString = rawDataType + delim;
        sulfateString += action + delim;
        stateCd = countyCd = siteId = poc = volume = nullDataCd = collectionFreq = null;
        AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                null,
                                                null,
                                                volume,
                                                null,
                                                nullDataCd,
                                                null,
                                                null,
                                                null,
                                                stateCd,
                                                countyCd,
                                                siteId,
                                                poc,
                                                collectionFreq);
        sulfateString += stateCd + delim;
        sulfateString += countyCd + delim;
        sulfateString += siteId + delim;
        sulfateString += parameter + delim;
        sulfateString += poc + delim;
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
                    if ("sulfate".equals(data.getAnalyte())) {
                        sulfateString += getSulfateValue(data.getValue(), volume) + delim;
                        /*
                         * null data code is empty
                         */
                        sulfateString += delim;
                    } else if ("nitrate".equals(data.getAnalyte())) {
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
         * fields #16 through #26 are not used and are empty
         */
        end += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim +
               delim;
        end += alternateMethodDetectableLimit + delim;
        /*
         * field #28 is not used and is empty
         */

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
    private ArrayList<String> getAirToxicsStrings(SampleManager1 sm, String action) {
        String airToxicsString, stateCd, countyCd, siteId, poc, volume, nullDataCd, collectionFreq, date;
        SimpleDateFormat dateTimeFormat;
        ArrayList<String> airToxicsStrings;
        // TODO
        String parameter, durationCd, reportedUnit, methodCd, alternateMethodDetectableLimit;
        parameter = durationCd = reportedUnit = methodCd = alternateMethodDetectableLimit = null;

        airToxicsStrings = new ArrayList<String>();
        if (getResults(sm) != null) {
            for (ResultViewDO data : getResults(sm)) {

                dateTimeFormat = new SimpleDateFormat("YYYYMMDD");
                airToxicsString = rawDataType + delim;
                airToxicsString += action + delim;
                stateCd = countyCd = siteId = poc = volume = nullDataCd = collectionFreq = null;
                AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                        null,
                                                        null,
                                                        volume,
                                                        null,
                                                        nullDataCd,
                                                        null,
                                                        null,
                                                        null,
                                                        stateCd,
                                                        countyCd,
                                                        siteId,
                                                        poc,
                                                        collectionFreq);
                airToxicsString += stateCd + delim;
                airToxicsString += countyCd + delim;
                airToxicsString += siteId + delim;
                airToxicsString += parameter + delim;
                airToxicsString += poc + delim;
                airToxicsString += durationCd + delim;
                airToxicsString += reportedUnit + delim;
                airToxicsString += methodCd + delim;
                date = dateTimeFormat.format(sm.getSample().getCollectionDate());
                airToxicsString += date + delim;
                airToxicsString += time + delim;
                if (DataBaseUtil.isEmpty(nullDataCd)) {
                    if (data.getValue().contains("<"))
                        /*
                         * the value is less than the minimum value, so we round
                         * down to zero
                         */
                        airToxicsString += "0" + delim;
                    else
                        airToxicsString += data.getValue() + delim;
                    /*
                     * null data code is empty
                     */
                    airToxicsString += delim;
                } else {
                    /*
                     * there is a null data code, so the reported sample value
                     * is left empty
                     */
                    airToxicsString += delim + nullDataCd + delim;
                }
                airToxicsString = collectionFreq + delim;
                /*
                 * fields #16 through #26 are not used and are empty
                 */
                airToxicsString += delim + delim + delim + delim + delim + delim + delim + delim +
                                   delim + delim + delim;
                airToxicsString += alternateMethodDetectableLimit + delim;
                /*
                 * field #28 is not used and is empty
                 */
                airToxicsStrings.add(airToxicsString);
            }
        }
        return airToxicsStrings;
    }

    /**
     * Get air toxics air quality strings for sample
     */
    private ArrayList<String> getMetalsStrings(SampleManager1 sm, String action) {
        String metalsString, stateCd, countyCd, siteId, poc, volume, nullDataCd, collectionFreq, date;
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
                stateCd = countyCd = siteId = poc = volume = nullDataCd = collectionFreq = null;
                AuxDataHelperBean.fillAirQualityAuxData(sm,
                                                        null,
                                                        null,
                                                        volume,
                                                        null,
                                                        nullDataCd,
                                                        null,
                                                        null,
                                                        null,
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
