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
package org.openelis.scriptlet;

import java.util.Map;
import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO.Operation;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for performing operations for
 * "nbs cah (Congenital Adrenal Hyperplasia)" test
 */
public class NbsCahScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy proxy;

    private static final String TEST_NAME = "nbs cah", METHOD_NAME = "immunoassay",
                    CAH = "nbs_cah", OVERRIDE_INTER = "nbs_override_inter",
                    INTERPRETATION = "nbs_cah_inter", NO = "no",
                    WITHIN_NORMAL_LIMITS = "newborn_inter_within_normal",
                    PRESUMPTIVE_POSITIVE = "newborn_inter_presumptive_pos",
                    BORDERLINE = "newborn_inter_borderline";

    public NbsCahScriptlet1(Proxy proxy) {
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing NbsCahScriptlet1");
    }

    @Override
    public SampleSO run(SampleSO data) {
        ResultViewDO res;

        proxy.log(Level.FINE, "In NbsCahScriptlet1.run");
        /*
         * don't do anything if a result was not changed
         */
        if ( !data.getOperations().contains(Operation.RESULT_CHANGED))
            return data;

        /*
         * get result that made this scriptlet get executed
         */
        res = getChangedResult(data);

        if (res == null)
            /*
             * the result doesn't belong to this test
             */
            return data;

        /*
         * set the value of interpretation based on the value of this result
         */
        setInterpretion(data, res);

        return data;
    }

    /**
     * Returns the result whose value was changed to make this scriptlet get
     * executed, but only if belongs to the active version of this test;
     * otherwise returns null
     */
    private ResultViewDO getChangedResult(SampleSO data) {
        ResultViewDO res;
        TestViewDO test;
        TestManager tm;

        res = null;
        proxy.log(Level.FINE,
                  "Going through the scriptlet object to find the result that trigerred the scriptlet");
        for (Map.Entry<Integer, TestManager> entry : data.getResults().entrySet()) {
            tm = entry.getValue();
            test = tm.getTest();
            if (TEST_NAME.equals(test.getName()) && METHOD_NAME.equals(test.getMethodName()) &&
                "Y".equals(test.getIsActive())) {
                res = (ResultViewDO)data.getManager()
                                        .getObject(Constants.uid().getResult(entry.getKey()));
                break;
            }
        }

        return res;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * the passed result, if the value to be set is different from the current
     * value
     */
    private void setInterpretion(SampleSO data, ResultViewDO resChanged) {
        int i, j;
        Integer weight;
        String val, overVal;
        Integer cahVal;
        TestManager tm;
        SampleManager1 sm;
        ResultViewDO res, resInter;
        DictionaryDO dict;
        AnalysisViewDO ana;
        FormattedValue fv;
        ResultFormatter rf;

        sm = data.getManager();
        weight = sm.getSampleNeonatal().getWeight() != null ? sm.getSampleNeonatal().getWeight()
                                                           : -1;
        /*
         * find the analysis for the result
         */
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(resChanged.getAnalysisId()));
        cahVal = null;
        overVal = null;
        resInter = null;
        proxy.log(Level.FINE,
                  "Going through the scriptlet object to find the result that trigerred the scriptlet");
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (CAH.equals(res.getAnalyteExternalId())) {
                    val = res.getValue();
                    if (val != null) {
                        if (val.startsWith(">") || val.startsWith("<"))
                            val = val.substring(1);
                        cahVal = Integer.valueOf(val);
                    }
                } else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId())) {
                    overVal = res.getValue();
                } else if (INTERPRETATION.equals(res.getAnalyteExternalId())) {
                    resInter = res;
                }
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the values of override interretation");
            /*
             * proceed only if the value for override interpretation is "No"
             */
            dict = getDictionaryByValue(overVal);
            if (dict == null || !NO.equals(dict.getSystemName()) || cahVal == null)
                return;

            proxy.log(Level.FINE,
                      "Getting the value for interpretation based on the value for 17-Hydroxyprogesterone and weight");
            if (weight < 0) {
            } else if (weight <= 1500) {
                if (cahVal < 90)
                    dict = proxy.getDictionaryBySystemName(WITHIN_NORMAL_LIMITS);
                else if (cahVal < 155)
                    dict = proxy.getDictionaryBySystemName(BORDERLINE);
                else
                    dict = proxy.getDictionaryBySystemName(PRESUMPTIVE_POSITIVE);
            } else if (weight <= 2500) {
                if (cahVal < 50)
                    dict = proxy.getDictionaryBySystemName(WITHIN_NORMAL_LIMITS);
                else if (cahVal < 75)
                    dict = proxy.getDictionaryBySystemName(BORDERLINE);
                else
                    dict = proxy.getDictionaryBySystemName(PRESUMPTIVE_POSITIVE);
            } else {
                if (cahVal < 30)
                    dict = proxy.getDictionaryBySystemName(WITHIN_NORMAL_LIMITS);
                else if (cahVal < 75)
                    dict = proxy.getDictionaryBySystemName(BORDERLINE);
                else
                    dict = proxy.getDictionaryBySystemName(PRESUMPTIVE_POSITIVE);
            }

            tm = data.getResults().get(resChanged.getId());
            rf = tm.getFormatter();
            /*
             * get the value to be set in the interpretation from this test
             * manager's result formatter and set it only if it's different from
             * the current value
             */
            fv = rf.format(resInter.getResultGroup(), ana.getUnitOfMeasureId(), dict.getEntry());
            if ( !DataBaseUtil.isSame(resInter.getTestResultId(), fv.getId())) {
                proxy.log(Level.FINE, "Setting the value of interpretion as: " + dict.getEntry());
                ResultHelper.formatValue(resInter, dict.getEntry(), ana.getUnitOfMeasureId(), rf);
                data.addRerun(resInter.getAnalyteExternalId());
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns the dictionary entry whose id's string equivalent is the passed
     * value
     */
    private DictionaryDO getDictionaryByValue(String value) throws Exception {
        if (value == null)
            return null;

        return proxy.getDictionaryById(Integer.valueOf(value));
    }

    public static interface Proxy {
        public DictionaryDO getDictionaryById(Integer id) throws Exception;

        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public void log(Level level, String message);
    }
}