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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;

/**
 * This class implements the common functionality for all scriptlets
 */
public class ScriptletUtility {

    private Proxy                                        proxy;
    private HashMap<String, HashMap<Integer, QaEventDO>> qaTestMap;

    public final String                                  QA_EC = "early collection",
                    QA_ECU = "early collection unk", QA_U = "unknown weight",
                    QA_UE = "unknown weight elev", QA_TRAN = "transfused",
                    QA_TRANU = "transfused unknown", QA_PQ = "poor quality";

    public Integer                                       INTER_N, INTER_PP_NR, INTER_BORD_NR,
                    INTER_DFNT, INTER_AA_NR, INTER_EC, INTER_UE, INTER_U, INTER_FETAL, INTER_BARTS,
                    INTER_TRAN, INTER_TRAIT_NR, INTER_DIS, INTER_D_B, INTER_ECU, INTER_TRANU,
                    INTER_T_B, INTER_PSBL, INTER_PQ, INTER_PP, INTER_NEG, INTER_PS, INTER_INC,
                    INTER_INS, INTER_INV, INTER_SNTL, INTER_AA, INTER_BORD, INTER_TRAIT;

    public ScriptletUtility(Proxy proxy) throws Exception {
        ArrayList<String> names;
        ArrayList<QaEventDO> qas;
        HashMap<Integer, QaEventDO> tmap;

        this.proxy = proxy;

        proxy.log(Level.FINE, "In ScriptletUtility. Initializing dictionary constants");

        INTER_N = proxy.getDictionaryBySystemName("newborn_inter_n").getId();
        INTER_PP_NR = proxy.getDictionaryBySystemName("newborn_inter_pp_nr").getId();
        INTER_BORD_NR = proxy.getDictionaryBySystemName("newborn_inter_bord_nr").getId();
        INTER_DFNT = proxy.getDictionaryBySystemName("newborn_inter_dfnt").getId();
        INTER_AA_NR = proxy.getDictionaryBySystemName("newborn_inter_aa_nr").getId();
        INTER_EC = proxy.getDictionaryBySystemName("newborn_inter_ec").getId();
        INTER_UE = proxy.getDictionaryBySystemName("newborn_inter_ue").getId();
        INTER_U = proxy.getDictionaryBySystemName("newborn_inter_u").getId();
        INTER_FETAL = proxy.getDictionaryBySystemName("newborn_inter_fetal").getId();
        INTER_BARTS = proxy.getDictionaryBySystemName("newborn_inter_barts").getId();
        INTER_TRAN = proxy.getDictionaryBySystemName("newborn_inter_tran").getId();
        INTER_TRAIT_NR = proxy.getDictionaryBySystemName("newborn_inter_trait_nr").getId();
        INTER_DIS = proxy.getDictionaryBySystemName("newborn_inter_dis").getId();
        INTER_D_B = proxy.getDictionaryBySystemName("newborn_inter_d_b").getId();
        INTER_ECU = proxy.getDictionaryBySystemName("newborn_inter_ecu").getId();
        INTER_TRANU = proxy.getDictionaryBySystemName("newborn_inter_tranu").getId();
        INTER_T_B = proxy.getDictionaryBySystemName("newborn_inter_t_b").getId();
        INTER_PSBL = proxy.getDictionaryBySystemName("newborn_inter_psbl").getId();
        INTER_PQ = proxy.getDictionaryBySystemName("newborn_inter_pq").getId();
        INTER_PP = proxy.getDictionaryBySystemName("newborn_inter_pp").getId();
        INTER_NEG = proxy.getDictionaryBySystemName("newborn_inter_neg").getId();
        INTER_PS = proxy.getDictionaryBySystemName("newborn_inter_ps").getId();
        INTER_INC = proxy.getDictionaryBySystemName("newborn_inter_inc").getId();
        INTER_INS = proxy.getDictionaryBySystemName("newborn_inter_ins").getId();
        INTER_INV = proxy.getDictionaryBySystemName("newborn_inter_inv").getId();
        INTER_SNTL = proxy.getDictionaryBySystemName("newborn_inter_sntl").getId();
        INTER_AA = proxy.getDictionaryBySystemName("newborn_inter_aa").getId();
        INTER_BORD = proxy.getDictionaryBySystemName("newborn_inter_bord").getId();
        INTER_TRAIT = proxy.getDictionaryBySystemName("newborn_inter_trait").getId();

        if (qaTestMap == null) {
            proxy.log(Level.FINE, "Initializing qa events");

            names = new ArrayList<String>();
            names.add(QA_EC);
            names.add(QA_ECU);
            names.add(QA_U);
            names.add(QA_UE);
            names.add(QA_TRAN);
            names.add(QA_TRANU);
            names.add(QA_PQ);

            /*
             * the following mapping allows looking up a qa event using its name
             * and the id of a test that it's linked to; for generic qa events,
             * the test id is null
             */
            qas = proxy.fetchByNames(names);
            qaTestMap = new HashMap<String, HashMap<Integer, QaEventDO>>();
            for (QaEventDO qa : qas) {
                tmap = qaTestMap.get(qa.getName());
                if (tmap == null) {
                    tmap = new HashMap<Integer, QaEventDO>();
                    qaTestMap.put(qa.getName(), tmap);
                }
                tmap.put(qa.getTestId(), qa);
            }
        }
    }

    /**
     * Returns the qa event with the passed name and linked to the test with the
     * passed id. Returns the generic qa event with this name if either the test
     * id is null or a qa event is not found for the test id.
     */
    public QaEventDO getQaEvent(String name, Integer testId) {
        QaEventDO qa;
        HashMap<Integer, QaEventDO> tmap;

        tmap = qaTestMap.get(name);
        if (tmap == null)
            return null;

        qa = tmap.get(testId);
        if (testId != null && qa == null)
            qa = tmap.get(null);

        return qa;
    }

    /**
     * Returns true if the sample has any sample qa event of type warning or
     * result override; false otherwise
     */
    public boolean sampleHasRejectQA(SampleManager1 sm) {
        SampleQaEventDO sqa;

        for (int i = 0; i < sm.qaEvent.count(); i++ ) {
            sqa = sm.qaEvent.get(i);
            if (Constants.dictionary().QAEVENT_OVERRIDE.equals(sqa.getTypeId()) ||
                Constants.dictionary().QAEVENT_WARNING.equals(sqa.getTypeId()))
                return true;
        }

        return false;
    }

    /**
     * Returns true if the passed analysis has a qa event with the passed name;
     * false otherwise
     */
    public boolean analysisHasQA(SampleManager1 sm, AnalysisViewDO ana, String name) {
        AnalysisQaEventViewDO aqa;

        for (int i = 0; i < sm.qaEvent.count(ana); i++ ) {
            aqa = sm.qaEvent.get(ana, i);
            if (aqa.getQaEventName().equals(name))
                return true;
        }

        return false;
    }

    /**
     * Returns the result whose value was changed to make the scriptlet for the
     * to the active version of the test with the passed name and method ;
     * returns null if such a result couldn't be found
     */
    public ResultViewDO getChangedResult(SampleSO data, String testName, String methodName) {
        ResultViewDO res;
        TestViewDO test;
        TestManager tm;

        res = null;
        proxy.log(Level.FINE,
                  "Going through the SO to find the result that trigerred the scriptlet");
        for (Map.Entry<Integer, TestManager> entry : data.getResults().entrySet()) {
            tm = entry.getValue();
            test = tm.getTest();
            if (test.getName().equals(testName) && test.getMethodName().equals(methodName) &&
                "Y".equals(test.getIsActive())) {
                res = (ResultViewDO)data.getManager()
                                        .getObject(Constants.uid().getResult(entry.getKey()));
                break;
            }
        }

        return res;
    }

    /**
     * Returns the analysis corresponding to the active version of the test with
     * the passed name and method; returns null if such an analysis couldn't be
     * found
     */
    public AnalysisViewDO getAnalysis(SampleSO data, String testName, String methodName) {
        AnalysisViewDO ana;
        TestViewDO test;
        TestManager tm;

        ana = null;
        proxy.log(Level.FINE, "Going through the SO to find the analysis for this test");
        for (Map.Entry<Integer, TestManager> entry : data.getAnalyses().entrySet()) {
            tm = entry.getValue();
            test = tm.getTest();
            if (test.getName().equals(testName) && test.getMethodName().equals(methodName) &&
                "Y".equals(test.getIsActive())) {
                ana = (AnalysisViewDO)data.getManager()
                                          .getObject(Constants.uid().getAnalysis(entry.getKey()));
                break;
            }
        }

        return ana;
    }

    public static interface Proxy {
        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public ArrayList<QaEventDO> fetchByNames(ArrayList<String> names) throws Exception;

        public void log(Level level, String message);
    }
}