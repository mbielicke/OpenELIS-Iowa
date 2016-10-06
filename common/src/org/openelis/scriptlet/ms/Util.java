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
package org.openelis.scriptlet.ms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.AnalyteParameterManager1.AnalyteCombo;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.ms.Constants.Apr_Risk;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * This class is used to provide general functionality to the scriptlet for
 * maternal screening tests
 */
public class Util {

    private static HashMap<String, HashMap<Integer, QaEventDO>> qaTestMap;

    /**
     * Returns the birth date of the egg donor; it's calculated by subtracting
     * the passed egg's age from the passed sample's collection date
     */
    public static Datetime getBirthDate(Double eggAge, ScriptletProxy proxy, SampleSO data,
                                        AnalysisViewDO ana) {
        int months;
        Integer accession;
        Datetime cd;

        if (eggAge == null)
            return null;

        cd = data.getManager().getSample().getCollectionDate();
        if (cd == null) {
            accession = data.getManager().getSample().getAccessionNumber();
            /*
             * for display
             */
            if (accession == null)
                accession = 0;
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(DataBaseUtil.concatWithSeparator(Messages.get()
                                                                                              .sample_accessionAnalysisPrefix(accession,
                                                                                                                              ana.getTestName(),
                                                                                                                              ana.getMethodName()),
                                                                                      " ",
                                                                                      Messages.get()
                                                                                              .result_missingCollectedDateException())));
            return null;
        }
        /*
         * find the total number of months specified by the egg's age and
         * subtract them from the collected date to get the birth date
         */
        months = (int) (Math.ceil(eggAge * 12));
        return proxy.addMonthsToDate(cd, -months);
    }

    /**
     * Returns a map created from the passed analyte parameter manager; the
     * map's key is analyte id and value is the most recent analyte parameter
     * for that analyte that's active for the passed analysis' started date and
     * sample type
     */
    public static HashMap<Integer, AnalyteParameterViewDO> getParameters(AnalyteParameterManager1 apm,
                                                                         SampleSO data,
                                                                         AnalysisViewDO ana) {
        SampleItemViewDO item;

        item = (SampleItemViewDO)data.getManager()
                                     .getObject(org.openelis.domain.Constants.uid()
                                                                             .getSampleItem(ana.getSampleItemId()));
        return getParameters(apm, data, ana, item.getTypeOfSampleId());
    }

    /**
     * Returns a map created from the passed analyte parameter manager; the
     * map's key is analyte id and value is the most recent analyte parameter
     * for that analyte that's active for the passed analysis' started date and
     * for the passed sample type
     */
    public static HashMap<Integer, AnalyteParameterViewDO> getParameters(AnalyteParameterManager1 apm,
                                                                         SampleSO data,
                                                                         AnalysisViewDO ana,
                                                                         Integer sampleTypeId) {
        int i, j;
        Datetime sd;
        AnalyteCombo ac;
        AnalyteParameterViewDO ap;
        HashMap<Integer, AnalyteParameterViewDO> paramMap;

        sd = ana.getStartedDate();
        if (apm == null || sd == null)
            return null;

        paramMap = new HashMap<Integer, AnalyteParameterViewDO>();
        /*
         * find the most recent analyte parameters defined for the passed sample
         * type and whose active range contains the analysis' started date
         */
        for (i = 0; i < apm.analyte.count(); i++ ) {
            ac = apm.analyte.get(i);
            for (j = 0; j < apm.analyteParameter.count(ac.getId()); j++ ) {
                ap = apm.analyteParameter.get(ac.getId(), j);
                if (DataBaseUtil.isSame(sampleTypeId, ap.getTypeOfSampleId()) &&
                    (ap.getActiveBegin().compareTo(sd) <= 0) &&
                    ap.getActiveEnd().compareTo(sd) >= 0) {
                    paramMap.put(ap.getAnalyteId(), ap);
                    break;
                }
            }
        }
        return paramMap;
    }

    /**
     * Formats the passed value to have one or two fractional digits based on
     * "precision"; if "precision" is not one or two, returns the value as is
     */
    public static String format(Double value, int precision, ScriptletProxy proxy) throws Exception {
        if (value == null)
            return null;

        switch (precision) {
            case 1:
                return proxy.format(value, "#0.0");
            case 2:
                return proxy.format(value, "#0.00");
            default:
                return value.toString();
        }
    }

    /**
     * Truncates a double to given decimal digits
     */
    public static double trunc(double value, int dp) {
        double factor;

        factor = Math.pow(10, dp);
        return Math.floor(value * factor) / factor;
    }

    /**
     * Truncates a double to given decimal digits
     */
    public static double setPrecision(double value, int sd) {
        int mag, factor;

        mag = (int) (Math.floor(Math.log10(value)) - sd + 1.0);
        if (mag < 0)
            return value;
        factor = (int)Math.pow(10, mag);
        return Math.floor( (value / factor) + 0.9) * factor;
    }

    /**
     * Returns the interval in days between two date objects
     */
    public static int getIntervalDays(Date a, Date b) {
        double days;

        days = (a.getTime() - b.getTime()) / 86400000.0;
        /*
         * add a fudge factor to cancel the spring/fall clock changes.
         */
        return (int)Math.floor(days + 0.25);
    }

    /**
     * Returns the infant age associated with Biparietal diameter or 0 if not
     * found.
     */
    public static int getAgeforBPD(Integer bpd) {
        double x;

        if (bpd == null)
            return 0;
        /*
         * change mm to cm
         */
        x = bpd.intValue() / 10.0;
        return (int) ( (9.54 + 1.482 * x + 0.1676 * (x * x)) * 7.0);
    }

    /**
     * Returns the gestational age in days associated with crown rump length
     */
    public static int getAgeforCRL(Integer crl) {
        double x;

        if (crl == null)
            return 0;
        /*
         * change mm to cm
         */
        x = crl.intValue() / 10.0;
        return (int) (Math.exp(1.684969 + 0.315646 * x - 0.049306 * x * x + 0.004057 * x * x * x -
                               0.000120456 * x * x * x * x) * 7.0);
    }

    /**
     * Returns mother's apriori risk based on maternal age.
     */
    public static double getAPrioriRisk(double dueAge, Apr_Risk aprRisk) {
        double risk;

        dueAge = Math.min(50, Math.max(15, dueAge));
        /*
         * computes term risk
         */
        risk = Math.exp(7.33 - 4.211 / (1.0 + Math.exp( -0.2815 * (dueAge - 0.5) + 10.480245)));
        switch (aprRisk) {
            case T1:
                risk *= 0.57;
                break;
            case T2:
                risk *= 0.77;
                break;
        }
        return risk;
    }

    /**
     * Computes and returns an ordinal value using passed parameters
     */
    public static double getOrdinal(double mom[], int dimension, double discriminant,
                                    double logMean[], double constant[]) {
        int i, j, n;
        double ordinal;

        ordinal = 0.0;
        for (i = 0, n = 0; i < dimension; i++ )
            for (j = 0; j < dimension; j++ , n++ )
                ordinal += (constant[n] * (Math.log10(mom[i]) - logMean[i]) * (Math.log10(mom[j]) - logMean[j]));

        return Math.pow(2.0 * Math.PI, -dimension / 2) * (1 / Math.sqrt(discriminant)) *
               Math.exp( -0.5 * ordinal);
    }

    /**
     * Returns the passed gestational age converted to weeks and days
     */
    public static String getWeeksAndDays(Integer gestAge) {
        int weeks, days;

        if (gestAge == null)
            return null;
        weeks = gestAge / 7;
        days = gestAge - (weeks * 7);
        return Messages.get().result_gestAgeWeeksAndDays(gestAge, weeks, days);
    }

    /**
     * Returns the qa event with the passed name which is linked to the test
     * with the passed id; returns the generic qa event with this name if the
     * test id is null
     */
    public static QaEventDO getQAEvent(String name, Integer testId, ScriptletProxy proxy) {
        ArrayList<String> names;
        ArrayList<QaEventDO> qas;
        HashMap<Integer, QaEventDO> tmap;

        if (qaTestMap == null) {
            proxy.log(Level.FINE, "Initializing maternal screening qa events", null);
            names = new ArrayList<String>();
            names.add(Constants.QA_EST_MOM);
            names.add(Constants.QA_PAPP_A_MOM);
            names.add(Constants.QA_TWINS);
            names.add(Constants.QA_LESS_THAN_15);
            try {
                /*
                 * the following mapping allows looking up a qa event using its
                 * name and the id of a test that it's linked to; for generic qa
                 * events, the test id is null
                 */
                qas = proxy.fetchByNames(names);
                qaTestMap = new HashMap<String, HashMap<Integer, QaEventDO>>();
                for (QaEventDO data : qas) {
                    tmap = qaTestMap.get(data.getName());
                    if (tmap == null) {
                        tmap = new HashMap<Integer, QaEventDO>();
                        qaTestMap.put(data.getName(), tmap);
                    }
                    tmap.put(data.getTestId(), data);
                }
                proxy.log(Level.FINE, "Initialized qa events", null);
            } catch (Exception e) {
                proxy.log(Level.SEVERE, "Failed to initialize qa events", e);
                return null;
            }
        }
        tmap = qaTestMap.get(name);
        return tmap != null ? tmap.get(testId) : null;
    }

    /**
     * Fetches sample(s) that have the same patient as the passed sample, have a
     * released analysis with the passed test and method and match the criteria
     * specified by the passed query fields; returns managers for those samples,
     * loaded with the passed elements
     */
    public static ArrayList<SampleManager1> fetchRelatedSamples(String testName, String methodName,
                                                                SampleManager1 sm,
                                                                ArrayList<QueryData> extraFields,
                                                                ScriptletProxy proxy,
                                                                SampleManager1.Load... elements) throws Exception {
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();

        field = new QueryData();
        field.setKey(SampleMeta.getClinicalPatientId());
        field.setType(QueryData.Type.INTEGER);
        field.setQuery(sm.getSampleClinical().getPatientId().toString());
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getAnalysisTestName());
        field.setType(QueryData.Type.STRING);
        field.setQuery(testName);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getAnalysisMethodName());
        field.setType(QueryData.Type.STRING);
        field.setQuery(methodName);
        fields.add(field);

        field = new QueryData();
        field.setKey(SampleMeta.getAnalysisStatusId());
        field.setType(QueryData.Type.INTEGER);
        field.setQuery(org.openelis.domain.Constants.dictionary().ANALYSIS_RELEASED.toString());
        fields.add(field);

        if (extraFields != null)
            fields.addAll(extraFields);

        return proxy.fetchByQuery(fields, 0, 1000, elements);
    }
}