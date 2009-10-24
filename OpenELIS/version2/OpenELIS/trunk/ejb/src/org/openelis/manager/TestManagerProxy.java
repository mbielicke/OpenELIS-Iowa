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

import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestLocal;
import org.openelis.local.TestSectionLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class TestManagerProxy {

    public TestManager fetchById(Integer testId) throws Exception {
        TestViewDO testDO;
        TestManager man;
        TestSectionManager tsm;
        ArrayList<TestSectionViewDO> sections;

        testDO = local().fetchById(testId);
        man = TestManager.getInstance();
        man.setTest(testDO);

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
    
    public TestManager fetchWithPrepTests(Integer testId) throws Exception {
        TestManager man;

        man = fetchById(testId);
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
        TestSectionManager tsm;
        TestTypeOfSampleManager ttsm;
        TestAnalyteManager tam;
        TestResultManager trm;
        TestPrepManager tpm;
        TestReflexManager tfm;
        TestWorksheetManager twm;
        
        Integer testId;
        HashMap<Integer,Integer> analyteMap, resultMap;        
        
        analyteMap = new HashMap<Integer, Integer>();
        resultMap = new HashMap<Integer, Integer>();

        local().add(man.getTest());

        testId = man.getTest().getId();

        tsm = man.getTestSections();
        tsm.setTestId(testId);
        tsm.add();

        ttsm = man.getSampleTypes();
        ttsm.setTestId(testId);
        ttsm.add();

        tam = man.getTestAnalytes();
        tam.setTestId(testId);
        tam.add(analyteMap);
        
        trm = man.getTestResults();
        trm.setTestId(testId);
        trm.add(resultMap);
        
        tpm = man.getPrepTests();
        tpm.setTestId(testId);
        tpm.add();
        
        tfm = man.getReflexTests();
        tfm.setTestId(testId);
        tfm.add(analyteMap, resultMap);
        
        twm = man.getTestWorksheet();
        twm.setTestId(testId);
        twm.add(analyteMap);        

        return man;
    }

    public TestManager update(TestManager man) throws Exception {
        TestSectionManager tsm;
        TestTypeOfSampleManager ttsm;
        TestAnalyteManager tam;
        TestResultManager trm;
        TestPrepManager tpm;
        TestReflexManager tfm;
        TestWorksheetManager twm;
        
        Integer testId;
        HashMap<Integer,Integer> analyteMap, resultMap;
        
        analyteMap = new HashMap<Integer, Integer>();
        resultMap = new HashMap<Integer, Integer>();

        local().update(man.getTest());

        testId = man.getTest().getId();

        tsm = man.getTestSections();
        tsm.setTestId(testId);
        tsm.update();

        ttsm = man.getSampleTypes();
        ttsm.setTestId(testId);
        ttsm.update();

        tam = man.getTestAnalytes();
        tam.setTestId(testId);
        tam.update(analyteMap);

        trm = man.getTestResults();
        trm.setTestId(testId);
        trm.update(resultMap);
        
        tpm = man.getPrepTests();
        tpm.setTestId(testId);
        tpm.update();
        
        tfm = man.getReflexTests();
        tfm.setTestId(testId);
        tfm.update(analyteMap, resultMap);
        
        twm = man.getTestWorksheet();
        twm.setTestId(testId);
        twm.update(analyteMap);   
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
        ValidationErrorsList exceptionList;
        HashMap<Integer, Integer> anaResGrpMap;
        HashMap<Integer, List<Integer>> resGrpRsltMap;
        boolean anaListValid, resListValid;
        TestViewDO testDO;
        
        exceptionList = new ValidationErrorsList();

        anaResGrpMap = new HashMap<Integer, Integer>();
        resGrpRsltMap = new HashMap<Integer, List<Integer>>();        
        testDO = man.getTest();
        anaListValid = true;
        resListValid = true;
        
        try {
            local().validate(testDO);
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        try {
            man.getTestSections().validate();
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        try {
            man.getSampleTypes().validate();
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        try {
            man.getTestAnalytes().validate(man.getTestResults(), anaResGrpMap);
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
            anaListValid = false;
        }
        
        try {
            man.getTestResults().validate(man.getSampleTypes(), resGrpRsltMap);
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
            resListValid = false;
        }
        
        try {
            man.getPrepTests().validate();
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        try {
            man.getReflexTests().validate(anaListValid, resListValid, anaResGrpMap, resGrpRsltMap);
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        try {
            man.getTestWorksheet().validate();
        } catch(Exception e) {
            e.printStackTrace();
            DataBaseUtil.mergeException(exceptionList, e);
        }
        
        if (exceptionList.size() > 0)
            throw exceptionList;
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
