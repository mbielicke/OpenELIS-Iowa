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

import static org.openelis.manager.IOrderManager1Accessor.addAnalyte;
import static org.openelis.manager.IOrderManager1Accessor.getTests;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.IOrderManager1;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

/**
 * This class is used to provide various functionalities related to analyses in
 * a generic manner
 */

@Stateless
@SecurityDomain("openelis")
public class IOrderTestHelperBean {

    @EJB
    private TestManagerBean testManager;

    @EJB
    private TestAnalyteBean testAnalyte;

    /**
     * Returns TestManagers for given test ids. For those tests that are not
     * active, the method looks for the active version of the same tests.
     */
    public HashMap<Integer, TestManager> getTestManagers(ArrayList<Integer> testIds,
                                                         ValidationErrorsList e) throws Exception {
        TestViewDO t;
        ArrayList<TestManager> tms;
        HashMap<Integer, TestManager> map;

        tms = testManager.fetchByIds(testIds);
        map = new HashMap<Integer, TestManager>();
        for (TestManager tm : tms) {
            t = tm.getTest();

            /*
             * if test not active, try to find an active test with this name and
             * method, make sure the old id points to the new manager
             */
            if ("N".equals(t.getIsActive())) {
                try {
                    tm = testManager.fetchActiveByNameMethodName(t.getName(), t.getMethodName());
                    map.put(t.getId(), tm);
                    t = tm.getTest();
                } catch (NotFoundException ex) {
                    e.add(new FormErrorWarning(Messages.get()
                                                       .test_inactiveTestException(t.getName(),
                                                                                   t.getMethodName())));
                    continue;
                }
            }
            map.put(t.getId(), tm);
        }

        return map;
    }

    /**
     * Adds the test in the test manager to the order
     */
    public IOrderTestViewDO addTest(IOrderManager1 om, TestManager tm, Integer index) throws Exception {
        IOrderTestViewDO ot;
        TestViewDO t;

        t = tm.getTest();
        ot = new IOrderTestViewDO();
        ot.setId(om.getNextUID());
        ot.setItemSequence(0);
        ot.setTestId(t.getId());
        ot.setTestName(t.getName());
        ot.setMethodName(t.getMethodName());
        ot.setDescription(t.getDescription());

        getTests(om).add(index, ot);

        return ot;
    }

    /**
     * Adds analytes for this test from the TestManager
     */
    public void addAnalytes(IOrderManager1 om, TestManager tm, Integer orderTestId) throws Exception {
        IOrderTestAnalyteViewDO ota;
        TestAnalyteManager tam;
        TestAnalyteViewDO ta;

        tam = tm.getTestAnalytes();
        /*
         * By default add analytes that are not supplemental. Add supplemental
         * analytes that are in id list. The id list overrides reportable flag.
         */
        for (ArrayList<TestAnalyteViewDO> list : tam.getAnalytes()) {
            ta = list.get(0);

            ota = new IOrderTestAnalyteViewDO();
            ota.setId(om.getNextUID());
            ota.setAnalyteId(ta.getAnalyteId());
            ota.setAnalyteName(ta.getAnalyteName());
            ota.setIorderTestId(orderTestId);
            if ("Y".equals(ta.getIsReportable()) &&
                !Constants.dictionary().TEST_ANALYTE_SUPLMTL.equals(ta.getTypeId()))
                ota.setTestAnalyteIsReportable("Y");
            else
                ota.setTestAnalyteIsReportable("N");
            ota.setTestAnalyteIsPresent("Y");
            addAnalyte(om, ota);
        }
    }

    /**
     * retrieves all test analytes for the given tests and merges them with the
     * order test analytes
     */
    public void mergeAnalytes(ArrayList<Integer> testIds,
                              HashMap<Integer, ArrayList<IOrderTestAnalyteViewDO>> otaMap,
                              ArrayList<IOrderManager1> oms) throws Exception {
        HashMap<Integer, ArrayList<TestAnalyteViewDO>> taMap;
        ArrayList<IOrderTestAnalyteViewDO> otas;
        ArrayList<TestAnalyteViewDO> tas;

        /*
         * fetch test analytes for all of the tests and create mapping between
         * tests and their test analytes
         */
        taMap = new HashMap<Integer, ArrayList<TestAnalyteViewDO>>();
        for (TestAnalyteViewDO ta : testAnalyte.fetchByTestIds(testIds)) {
            if ("Y".equals(ta.getIsColumn()))
                continue;
            tas = taMap.get(ta.getTestId());
            if (tas == null) {
                tas = new ArrayList<TestAnalyteViewDO>();
                taMap.put(ta.getTestId(), tas);
            }
            tas.add(ta);
        }

        for (IOrderManager1 om : oms) {
            /*
             * merge each order test's analytes with the analytes added to its
             * test
             */
            for (IOrderTestViewDO ot : getTests(om)) {
                otas = otaMap.get(ot.getId());
                tas = taMap.get(ot.getTestId());
                mergeAnalytes(om, ot.getId(), otas, tas);
            }
        }
    }

    /**
     * merges test analytes and order test analytes into a single list of order
     * test analytes
     */
    private void mergeAnalytes(IOrderManager1 om, Integer otid,
                               ArrayList<IOrderTestAnalyteViewDO> otas,
                               ArrayList<TestAnalyteViewDO> tas) {
        String reportable;
        IOrderTestAnalyteViewDO ota;
        HashMap<Integer, ArrayList<IOrderTestAnalyteViewDO>> map;

        /*
         * Since the list of analytes for a test is assumed to not change, if
         * there are no analytes for a test right now, then there wouldn't have
         * been any in the past, so there isn't anything to merge.
         */
        if (tas == null)
            return;

        map = null;
        if (otas != null) {
            /*
             * some analytes were added to the order from this test previously
             */
            map = new HashMap<Integer, ArrayList<IOrderTestAnalyteViewDO>>();
            for (IOrderTestAnalyteViewDO d1 : otas) {
                if (map.get(d1.getAnalyteId()) == null)
                    map.put(d1.getAnalyteId(), new ArrayList<IOrderTestAnalyteViewDO>());
                map.get(d1.getAnalyteId()).add(d1);
            }
        }
        for (TestAnalyteViewDO ta : tas) {
            reportable = "N";
            /*
             * The merged list contains all analytes from the test definition.
             * If no analytes were added to the order test, then all
             * non-supplemental analytes are checked based on the test
             * definition; supplemental ones are unchecked. Otherwise only the
             * analytes added to the order test are checked.
             */
            if (map != null) {
                if (map.get(ta.getAnalyteId()) != null) {
                    ota = null;
                    for (int i = 0; i < map.get(ta.getAnalyteId()).size(); i++ ) {
                        if (map.get(ta.getAnalyteId()).get(i) != null) {
                            ota = map.get(ta.getAnalyteId()).get(i);
                            map.get(ta.getAnalyteId()).set(i, null);
                            break;
                        }
                    }
                    if (ota != null) {
                        addAnalyte(om, ota);
                        continue;
                    }
                }
            } else if ( !Constants.dictionary().TEST_ANALYTE_SUPLMTL.equals(ta.getTypeId())) {
                reportable = ta.getIsReportable();
            }

            ota = new IOrderTestAnalyteViewDO();
            ota.setId(om.getNextUID());
            ota.setIorderTestId(otid);
            ota.setAnalyteId(ta.getAnalyteId());
            ota.setAnalyteName(ta.getAnalyteName());
            ota.setTestAnalyteIsReportable(reportable);
            ota.setTestAnalyteSortOrder(ta.getSortOrder());
            ota.setTestAnalyteTypeId(ta.getTypeId());
            addAnalyte(om, ota);
        }
    }
}