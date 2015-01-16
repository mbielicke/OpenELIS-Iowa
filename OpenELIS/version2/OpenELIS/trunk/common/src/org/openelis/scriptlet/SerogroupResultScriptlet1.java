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

import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "nbs bt (Biotinidase)" test. It determines the
 * interpretation if it isn't overridden by setting override interpretation to
 * "Yes". The interpretation is initially based on the value of biotinidase. It
 * gets overridden if the sample has any qa events of type result-override or
 * warning or if it's transfused. The qa event "poor quality" is added to the
 * analysis in the first case and in the second, "transfused" or
 * "transfused unknown" is added depending upon whether the transfusion date is
 * specified.
 */
public class SerogroupResultScriptlet1 implements ScriptletInt<SampleSO> {

    private SerogroupResultScriptlet1Proxy proxy;

    private Integer            analysisId;

    public SerogroupResultScriptlet1(SerogroupResultScriptlet1Proxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing SerogroupResultScriptlet1", null);

        proxy.log(Level.FINE, "Initialized SerogroupResultScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        ResultViewDO res;

        proxy.log(Level.FINE, "In SerogroupResultScriptlet1.run", null);
        ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * manage result changed
         */
        res = null;
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            if (!analysisId.equals(res.getAnalysisId()) || !"serogroup".equals(res.getAnalyteExternalId()))
                return data;
        } else {
            return data;
        }

        /*
         * set the value of result based on serogroup
         */
        setResult(data, ana, res);

        return data;
    }

    /**
     * Sets the value of result based on the value of serogroup
     */
    private void setResult(SampleSO data, AnalysisViewDO ana, ResultViewDO resSero) {
        int i, j;
        SampleManager1 sm;
        ResultViewDO res;
        DictionaryDO dict;
        TestManager tm;
        ResultFormatter rf;

        /*
         * find the values for the various analytes
         */
        sm = data.getManager();
        res = null;
        rowLoop:
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (res.getId().equals(resSero.getId())) {
                    res = sm.result.get(ana, i, 0);
                    break rowLoop;
                }
            }
        }

        try {
            dict = getDictionaryByValue(resSero.getValue());
            if (dict == null || dict.getRelatedEntryId() == null)
                return;

            /*
             * get the value for result
             */
            dict = getDictionaryByValue(dict.getRelatedEntryId().toString());
            if (dict == null)
                return;

            /*
             * set the result
             */
            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            rf = tm.getFormatter();
            if (ResultHelper.formatValue(res, dict.getEntry(), ana.getUnitOfMeasureId(), rf)) {
                proxy.log(Level.FINE, "Setting the value of result as: " +
                                      dict.getEntry(), null);
                data.addRerun(res.getAnalyteExternalId());
                data.addChangedUid(Constants.uid().getResult(res.getId()));
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
}