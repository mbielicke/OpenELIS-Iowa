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
import java.util.logging.Level;

import org.openelis.domain.QaEventDO;

/**
 * This class provides quick access to the data used by neonatal scriptlets e.g.
 * the QA events used for neonatal tests
 */
public class NBSCache1 {

    private static NBSCache1                                     nbsCache1;

    private static HashMap<String, HashMap<Integer, QaEventDO>> qaTestMap;

    public final static String                                  QA_EC = "early collection",
                    QA_ECU = "early collection unk", QA_U = "unknown weight",
                    QA_UE = "unknown weight elev", QA_TRAN = "transfused",
                    QA_TRANU = "transfused unknown", QA_PQ = "poor quality";

    private NBSCache1(NBSScriptlet1Proxy proxy) {
        ArrayList<String> names;
        ArrayList<QaEventDO> qas;
        HashMap<Integer, QaEventDO> tmap;

        proxy.log(Level.FINE, "Initializing NBSCache1", null);
        if (qaTestMap == null) {
            names = new ArrayList<String>();
            names.add(QA_EC);
            names.add(QA_ECU);
            names.add(QA_U);
            names.add(QA_UE);
            names.add(QA_TRAN);
            names.add(QA_TRANU);
            names.add(QA_PQ);
            try {
                /*
                 * the following mapping allows looking up a qa event using its
                 * name and the id of a test that it's linked to; for generic qa
                 * events, the test id is null
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
                proxy.log(Level.FINE, "Initialized NBSCache1", null);
            } catch (Exception e) {
                proxy.log(Level.SEVERE, "Failed to initialize qa events", e);
            }
        }
    }

    public static NBSCache1 getInstance(NBSScriptlet1Proxy proxy) {
        if (nbsCache1 == null)
            nbsCache1 = new NBSCache1(proxy);

        return nbsCache1;
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
}
