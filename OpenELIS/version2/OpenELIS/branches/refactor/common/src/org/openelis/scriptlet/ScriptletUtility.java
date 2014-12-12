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

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.manager.SampleManager1;

/**
 * This class implements the common functionality for all scriptlets
 */
public class ScriptletUtility {

    /**
     * Returns true if the sample has any "reject" sample qa events i.e. the
     * ones with type result override; considers qa events of type warning as
     * "reject" also, if the passed flag is true. Returns false otherwise.
     */
    public static boolean sampleHasRejectQA(SampleManager1 sm, boolean includeWarning) {
        SampleQaEventDO sqa;

        for (int i = 0; i < sm.qaEvent.count(); i++ ) {
            sqa = sm.qaEvent.get(i);
            if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId()) ||
                (includeWarning && Constants.dictionary().QAEVENT_WARNING.equals(sqa.getTypeId())))
                return true;
        }

        return false;
    }

    /**
     * Returns true if the analysis has any "reject" qa events i.e. the ones
     * with type result override; considers qa events of type warning as
     * "reject" also, if the passed flag is true. Returns false otherwise.
     */
    public static boolean analysisHasRejectQA(SampleManager1 sm, AnalysisViewDO ana,
                                              boolean includeWarning) {
        AnalysisQaEventViewDO aqa;

        for (int i = 0; i < sm.qaEvent.count(ana); i++ ) {
            aqa = sm.qaEvent.get(ana, i);
            if (Constants.dictionary().QAEVENT_OVERRIDE.equals(aqa.getTypeId()) ||
                (includeWarning && Constants.dictionary().QAEVENT_WARNING.equals(aqa.getTypeId())))
                return true;
        }

        return false;
    }

    /**
     * Returns true if the passed analysis has a qa event with the passed name;
     * false otherwise
     */
    public static boolean analysisHasQA(SampleManager1 sm, AnalysisViewDO ana, String name) {
        AnalysisQaEventViewDO aqa;

        for (int i = 0; i < sm.qaEvent.count(ana); i++ ) {
            aqa = sm.qaEvent.get(ana, i);
            if (aqa.getQaEventName().equals(name))
                return true;
        }

        return false;
    }

    /**
     * Returns true if the result specified by the uid in the passed SO belongs
     * to the analysis with the passed id; returns false otherwise
     */
    public static boolean isManagedResult(SampleSO data, Integer analysisId) {
        ResultViewDO res;
        AnalysisViewDO ana;
        SampleManager1 sm;

        sm = data.getManager();
        /*
         * get the result specified by the uid in the SO and find out if its
         * analysis' test is linked to the analysis specified by the passed id
         */
        res = (ResultViewDO)sm.getObject(data.getUid());
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(res.getAnalysisId()));
        return ana.getId().equals(analysisId);
    }
}