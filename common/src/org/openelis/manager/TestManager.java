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
package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.utilcommon.ResultFormatter;

public class TestManager implements Serializable {

    private static final long                   serialVersionUID = 1L;

    protected TestViewDO                        test;
    protected TestSectionManager                testSections;
    protected TestTypeOfSampleManager           sampleTypes;
    protected TestAnalyteManager                testAnalytes;
    protected TestResultManager                 testResults;
    protected TestPrepManager                   prepTests;
    protected TestReflexManager                 reflexTests;
    protected TestWorksheetManager              worksheet;

    protected transient ResultFormatter         formatter;

    protected transient static TestManagerProxy proxy;

    /**
     * This is a protected constructor
     */
    protected TestManager() {
        test = null;
        testSections = null;
        sampleTypes = null;
        testAnalytes = null;
        testResults = null;
        prepTests = null;
        reflexTests = null;
        worksheet = null;
    }

    /**
     * Creates a new instance of this object. A default TestDO object is also
     * created.
     */
    public static TestManager getInstance() {
        TestManager tm;

        tm = new TestManager();
        tm.test = new TestViewDO();
        tm.testSections = new TestSectionManager();
        return tm;
    }

    public TestViewDO getTest() {
        return test;
    }

    public void setTest(TestViewDO test) {
        this.test = test;
    }

    public static TestManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static TestManager fetchWithSampleTypes(Integer id) throws Exception {
        return proxy().fetchWithSampleTypes(id);
    }

    public static TestManager fetchWithAnalytesAndResults(Integer id) throws Exception {
        return proxy().fetchWithAnalytesAndResults(id);
    }

    public static TestManager fetchWithPrepTestsSampleTypes(Integer id) throws Exception {
        return proxy().fetchWithPrepTestsSampleTypes(id);
    }

    public static TestManager fetchWithPrepTestAndReflexTests(Integer id) throws Exception {
        return proxy().fetchWithPrepTestsAndReflexTests(id);
    }

    public static TestManager fetchWithWorksheet(Integer id) throws Exception {
        return proxy().fetchWithWorksheet(id);
    }

    // service methods
    public TestManager add() throws Exception {
        return proxy().add(this);
    }

    public TestManager update() throws Exception {
        return proxy().update(this);
    }

    public TestManager abortUpdate() throws Exception {
        return proxy.abortUpdate(test.getId());
    }

    public TestManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(test.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public TestTypeOfSampleManager getSampleTypes() throws Exception {
        if (sampleTypes == null) {
            if (test.getId() != null) {
                try {
                    sampleTypes = TestTypeOfSampleManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (sampleTypes == null)
                sampleTypes = TestTypeOfSampleManager.getInstance();
        }

        return sampleTypes;
    }

    public TestAnalyteManager getTestAnalytes() throws Exception {
        if (testAnalytes == null) {
            if (test.getId() != null) {
                try {
                    testAnalytes = TestAnalyteManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (testAnalytes == null)
                testAnalytes = TestAnalyteManager.getInstance();
        }

        return testAnalytes;
    }

    public TestResultManager getTestResults() throws Exception {
        if (testResults == null) {
            if (test.getId() != null) {
                try {
                    testResults = TestResultManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (testResults == null)
                testResults = TestResultManager.getInstance();
        }

        return testResults;
    }

    public TestPrepManager getPrepTests() throws Exception {
        if (prepTests == null) {
            if (test.getId() != null) {
                try {
                    prepTests = TestPrepManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (prepTests == null)
                prepTests = TestPrepManager.getInstance();
        }

        return prepTests;
    }

    public TestReflexManager getReflexTests() throws Exception {
        if (reflexTests == null) {
            if (test.getId() != null) {
                try {
                    reflexTests = TestReflexManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (reflexTests == null)
                reflexTests = TestReflexManager.getInstance();
        }

        return reflexTests;
    }

    public TestWorksheetManager getTestWorksheet() throws Exception {
        if (worksheet == null) {
            if (test.getId() != null) {
                try {
                    worksheet = TestWorksheetManager.fetchByTestId(test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (worksheet == null)
                worksheet = TestWorksheetManager.getInstance();
        }

        return worksheet;
    }

    public TestSectionManager getTestSections() {
        return testSections;
    }

    public boolean canAssign() {
        return canAssignThisSection(null);
    }

    public boolean canAssignThisSection(TestSectionViewDO tsVDO) {
        TestSectionViewDO data;
        SystemUserPermission perm;
        SectionPermission sp;

        perm = proxy().getSystemUserPermission();

        for (int i = 0; i < testSections.count(); i++ ) {
            data = testSections.getSectionAt(i);
            sp = perm.getSection(data.getSection());

            if (sp != null && sp.hasAssignPermission() &&
                (tsVDO == null || data.getSectionId().equals(tsVDO.getSectionId())))
                return true;
        }
        return false;
    }

    /**
     * Returns the formatter used to format and validate this test's results
     */
    public ResultFormatter getFormatter() throws Exception {
        TestResultManager trm;
        TestResultViewDO tr;

        if (formatter != null)
            return formatter;

        trm = getTestResults();
        formatter = new ResultFormatter();
        for (int i = 0; i < trm.groupCount(); i++ ) {
            for (int j = 0; j < trm.getResultGroupSize(i + 1); j++ ) {
                tr = trm.getResultAt(i + 1, j);

                formatter.add(tr.getId(),
                              tr.getResultGroup(),
                              tr.getUnitOfMeasureId(),
                              tr.getTypeId(),
                              tr.getSignificantDigits(),
                              tr.getRoundingMethodId(),
                              tr.getValue(),
                              tr.getDictionary(),
                              tr.getDictionaryIsActive());
            }
        }
        return formatter;
    }

    private static TestManagerProxy proxy() {
        if (proxy == null)
            proxy = new TestManagerProxy();

        return proxy;
    }
}