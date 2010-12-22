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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;

import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestLocal;
import org.openelis.local.TestSectionLocal;
import org.openelis.utils.PermissionInterceptor;

public class TestManagerProxy {

    public TestManager fetchById(Integer testId) throws Exception {
        TestViewDO data;
        TestManager man;
        TestSectionManager tsm;
        ArrayList<TestSectionViewDO> sections;

        data = local().fetchById(testId);
        man = TestManager.getInstance();
        man.setTest(data);

        sections = (ArrayList<TestSectionViewDO>)sectionLocal().fetchByTestId(testId);

        tsm = man.getTestSections();
        tsm.sections = sections;

        return man;
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
        man.getSampleTypes();

        return man;
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
        man.getTestAnalytes();
        man.getTestResults();

        return man;
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
        man.getSampleTypes();
        man.getPrepTests();

        return man;
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
        man.getPrepTests();
        man.getReflexTests();

        return man;
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
        man.getTestWorksheet();

        return man;
    }

    public TestManager add(TestManager man) throws Exception {
        Integer testId;
        HashMap<Integer, Integer> analyteMap, resultMap;

        analyteMap = new HashMap<Integer, Integer>();
        resultMap = new HashMap<Integer, Integer>();

        local().add(man.getTest());

        testId = man.getTest().getId();

        if (man.testSections != null) {
            man.getTestSections().setTestId(testId);
            man.getTestSections().add();
        }

        if (man.sampleTypes != null) {
            man.getSampleTypes().setTestId(testId);
            man.getSampleTypes().add();
        }

        if (man.testAnalytes != null) {
            man.getTestAnalytes().setTestId(testId);
            man.getTestAnalytes().add(analyteMap);
        }

        if (man.testResults != null) {
            man.getTestResults().setTestId(testId);
            man.getTestResults().add(resultMap);
        }

        if (man.prepTests != null) {
            man.getPrepTests().setTestId(testId);
            man.getPrepTests().add();
        }

        if (man.reflexTests != null) {
            man.getReflexTests().setTestId(testId);
            man.getReflexTests().add(analyteMap, resultMap);
        }

        if (man.worksheet != null) {
            man.getTestWorksheet().setTestId(testId);
            man.getTestWorksheet().add(analyteMap);
        }

        return man;
    }

    public TestManager update(TestManager man) throws Exception {
        Integer testId;
        HashMap<Integer, Integer> analyteMap, resultMap;

        analyteMap = new HashMap<Integer, Integer>();
        resultMap = new HashMap<Integer, Integer>();

        local().update(man.getTest());

        testId = man.getTest().getId();

        if (man.testSections != null) {
            man.getTestSections().setTestId(testId);
            man.getTestSections().update();
        }

        if (man.sampleTypes != null) {
            man.getSampleTypes().setTestId(testId);
            man.getSampleTypes().update();
        }

        if (man.testAnalytes != null) {
            man.getTestAnalytes().setTestId(testId);
            man.getTestAnalytes().update(analyteMap);
        }

        if (man.testResults != null) {
            man.getTestResults().setTestId(testId);
            man.getTestResults().update(resultMap);
        }

        if (man.prepTests != null) {
            man.getPrepTests().setTestId(testId);
            man.getPrepTests().update();
        }

        if (man.reflexTests != null) {
            man.getReflexTests().setTestId(testId);
            man.getReflexTests().update(analyteMap, resultMap);
        }

        if (man.worksheet != null) {
            man.getTestWorksheet().setTestId(testId);
            man.getTestWorksheet().update(analyteMap);
        }

        return man;
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(TestManager man) throws Exception {
        ValidationErrorsList list;
        HashMap<Integer, Integer> anaResGrpMap;
        HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap;
        boolean anaListValid, resListValid;

        list = new ValidationErrorsList();

        anaResGrpMap = new HashMap<Integer, Integer>();
        resGrpRsltMap = new HashMap<Integer, List<TestResultViewDO>>();
        anaListValid = true;
        resListValid = true;

        try {
            local().validate(man.getTest());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.testSections != null)
                man.getTestSections().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.sampleTypes != null)
                man.getSampleTypes().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.testAnalytes != null)
                man.getTestAnalytes().validate(man.getTestResults(), anaResGrpMap);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
            anaListValid = false;
        }

        try {
            if (man.testResults != null)
                man.getTestResults().validate(man.getSampleTypes(), resGrpRsltMap);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
            resListValid = false;
        }

        try {
            if (man.prepTests != null)
                man.getPrepTests().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.reflexTests != null)
                man.getReflexTests().validate(anaListValid, resListValid, anaResGrpMap,
                                              resGrpRsltMap);
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        try {
            if (man.worksheet != null)
                man.getTestWorksheet().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }

        if (list.size() > 0)
            throw list;
    }

    protected SystemUserPermission getSystemUserPermission() throws Exception {
        return PermissionInterceptor.getSystemUserPermission();
    }

    private TestLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestLocal)ctx.lookup("openelis/TestBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private TestSectionLocal sectionLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestSectionLocal)ctx.lookup("openelis/TestSectionBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
