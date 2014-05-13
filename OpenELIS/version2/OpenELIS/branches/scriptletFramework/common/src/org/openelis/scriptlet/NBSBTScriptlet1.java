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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO.Operation;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for performing operations for "nbs bt (Biotinidase)" test
 */
public class NBSBTScriptlet1 implements ScriptletInt<SampleSO> {

    private Proxy proxy;

    private static final String TEST_NAME = "nbs bt", METHOD_NAME = "enzymatic assay",
                    BIOTINIDASE = "nbs_biotinidase", OVERRIDE_INTER = "nbs_override_inter",
                    INTERPRETATION = "nbs_bt_inter", NO = "no",
                    WITHIN_NORMAL_LIMITS = "newborn_inter_within_normal",
                    PRESUMPTIVE_POSITIVE = "newborn_inter_presumptive_pos";

    public NBSBTScriptlet1(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public SampleSO run(SampleSO data) {
        int i, j;
        String overrideVal, bioVal, sysName;
        ResultViewDO res, resInter;
        AnalysisViewDO ana;
        TestViewDO test;
        DictionaryDO dict;
        TestManager tm;
        SampleManager1 sm;

        /*
         * don't do anything if a result was not changed
         */
        if ( !data.getOperations().contains(Operation.RESULT_CHANGED))
            return data;

        sm = data.getManager();
        tm = null;
        res = null;

        /*
         * find out if any of the changed results belongs to the active version
         * of the test
         */
        for (Map.Entry<Integer, TestManager> entry : data.getResults().entrySet()) {
            tm = entry.getValue();
            test = tm.getTest();
            if (TEST_NAME.equals(test.getName()) && METHOD_NAME.equals(test.getMethodName()) &&
                "Y".equals(test.getIsActive())) {
                res = (ResultViewDO)sm.getObject(Constants.uid().getResult(entry.getKey()));
                break;
            }
        }

        if (res == null)
            return data;

        /*
         * find the analysis for the result and the values for the various
         * analytes
         */
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(res.getAnalysisId()));
        bioVal = null;
        overrideVal = null;
        resInter = null;
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (BIOTINIDASE.equals(res.getAnalyteExternalId()))
                    bioVal = res.getValue();
                else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId()))
                    overrideVal = res.getValue();
                else if (INTERPRETATION.equals(res.getAnalyteExternalId()))
                    resInter = res; 
            }
        }

        try {
            /*
             * proceed only if the value for override interpretation is "No"
             */
            dict = getDictionaryByValue(overrideVal);
            if (dict == null || !NO.equals(dict.getSystemName()))
                return data;

            dict = getDictionaryByValue(bioVal);
            if (dict == null)
                return data;
            /*
             * set the value of interpretation to "presumptive positive" if the
             * value of biotinidase is "1+" or "0", otherwise set it to
             * "within normal limits"
             */
            sysName = dict.getSystemName();
            
            if (sysName.endsWith("1+") || sysName.endsWith("0"))
                dict = proxy.getDictionaryBySystemName(PRESUMPTIVE_POSITIVE);
            else 
                dict = proxy.getDictionaryBySystemName(WITHIN_NORMAL_LIMITS);
            
            ResultHelper.formatValue(resInter,
                                     dict.getEntry(),
                                     ana.getUnitOfMeasureId(),
                                     tm.getFormatter());
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }

        return data;
    }

    private DictionaryDO getDictionaryByValue(String value) throws Exception {
        if (value == null)
            return null;

        return proxy.getDictionaryById(Integer.valueOf(value));
    }

    public static interface Proxy {
        public DictionaryDO getDictionaryById(Integer id) throws Exception;
        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;
    }
}