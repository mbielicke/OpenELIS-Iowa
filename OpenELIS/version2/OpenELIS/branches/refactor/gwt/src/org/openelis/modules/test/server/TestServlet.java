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
package org.openelis.modules.test.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.TestBean;
import org.openelis.bean.TestManagerBean;
import org.openelis.bean.TestSectionBean;
import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.domain.PanelVO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.modules.test.client.TestServiceInt;

@WebServlet("/openelis/test")
public class TestServlet extends RemoteServlet implements TestServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    TestManagerBean      testManager;
    
    @EJB
    TestBean             test;
    
    @EJB
    TestSectionBean      testSection;
    
    @EJB
    TestTypeOfSampleBean testType;

    public TestManager fetchById(Integer testId) throws Exception {
        try {
            return testManager.fetchById(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestManager> fetchByIds(ArrayList<Integer> ids) throws Exception {
        try {
        	return testManager.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        try {        
            return test.fetchByName(name + "%", 1000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestMethodVO> fetchActiveByName(String name) throws Exception {
        try {        
            return test.fetchActiveByName(name + "%", 1000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }    
    
    public ArrayList<TestMethodVO> fetchByNameSampleType(String name, Integer typeId) throws Exception {
        try { 
            return test.fetchByNameSampleType(name, typeId, 1000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public TestViewDO fetchActiveByNameMethodName(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData            field;
        String               testName, methodName;

        fields = query.getFields();
        field = fields.get(0);
        if (field.getQuery() != null)
            testName = field.getQuery();
        else
            testName = null; 
        field = fields.get(1);
        if (field.getQuery() != null)
            methodName = field.getQuery();
        else
            methodName = null;
        
        try {        
            return test.fetchActiveByNameMethodName(testName, methodName);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception {
        try {        
            return test.fetchByPanelId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception {
        try {        
            return test.fetchNameMethodSectionByName(name + "%", 1000000);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        try {        
            return test.fetchTestMethodSampleTypeList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestViewDO> fetchList() throws Exception {
        try {        
            return test.fetchList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<TestSectionViewDO> fetchTestSectionsByTestId(Integer id) throws Exception {
        try {
            return testSection.fetchByTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchSampleTypeByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchTestAnalytesByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchTestResultsByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchPrepTestsByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchReflexiveTestsByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        try {        
            return testManager.fetchWorksheetByTestId(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        try {        
            return testManager.fetchWithSampleTypes(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        try {        
            return testManager.fetchWithAnalytesAndResults(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        try {        
            return testManager.fetchWithPrepTestsSampleTypes(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        try {        
            return testManager.fetchWithPrepTestsAndReflexTests(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        try {        
            return testManager.fetchWithWorksheet(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        try {        
            return test.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager add(TestManager man) throws Exception {
        try {        
            return testManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager update(TestManager man) throws Exception {
        try {        
            return testManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        try {        
            return testManager.fetchForUpdate(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        try {        
            return testManager.abortUpdate(testId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}