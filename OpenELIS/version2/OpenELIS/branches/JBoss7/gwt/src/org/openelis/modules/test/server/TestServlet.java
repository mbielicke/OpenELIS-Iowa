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
import org.openelis.bean.TestTypeOfSampleBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelVO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.modules.test.client.TestServiceInt;

@WebServlet("/openelis/test")
public class TestServlet extends AppServlet implements TestServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    TestManagerBean      testManager;
    
    @EJB
    TestBean             test;
    
    @EJB
    TestTypeOfSampleBean testType;

    public TestManager fetchById(Integer testId) throws Exception {
        return testManager.fetchById(testId);
    }

    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        return test.fetchByName(name + "%", 1000);
    }
    
    public TestViewDO fetchActiveByNameMethodName(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData            field;
        String               testName, methodName;

        fields = query.getFields();
        field = fields.get(0);
        if (field.query != null)
            testName = field.query;
        else
            testName = null; 
        field = fields.get(1);
        if (field.query != null)
            methodName = field.query;
        else
            methodName = null;
        
        return test.fetchActiveByNameMethodName(testName, methodName);
    }

    public ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception {
        return test.fetchByPanelId(id);
    }

    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception {
        return test.fetchNameMethodSectionByName(name + "%", 1000000);
    }
    
    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        return test.fetchTestMethodSampleTypeList();
    }

    public ArrayList<TestMethodVO> fetchList() throws Exception {
        return test.fetchList();
    }
    
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        return testManager.fetchSampleTypeByTestId(testId);
    }

    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Query query) throws Exception {
        ArrayList<QueryData> fields;
        QueryData            field;
        Integer              testId, typeOfSampleId;
        String               unitOfMeasure;

        fields = query.getFields();
        field = fields.get(0);
        if (field.query != null)
            testId = Integer.valueOf(field.query);
        else
            testId = null; 
        field = fields.get(1);
        if (field.query != null)
            typeOfSampleId = Integer.valueOf(field.query);
        else
            typeOfSampleId = null;
        field = fields.get(2);
        if (field.query != null)
            unitOfMeasure = field.query;
        else
            unitOfMeasure = null; 
        
        return testType.fetchUnitsForWorksheetAutocomplete(testId, typeOfSampleId, unitOfMeasure);
    }

    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception {
        return testManager.fetchTestAnalytesByTestId(testId);
    }

    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception {
        return testManager.fetchTestResultsByTestId(testId);
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        return testManager.fetchPrepTestsByTestId(testId);
    }

    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception {
        return testManager.fetchReflexiveTestsByTestId(testId);
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        return testManager.fetchWorksheetByTestId(testId);
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return testManager.fetchWithSampleTypes(testId);
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return testManager.fetchWithAnalytesAndResults(testId);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        return testManager.fetchWithPrepTestsSampleTypes(testId);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        return testManager.fetchWithPrepTestsAndReflexTests(testId);
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        return testManager.fetchWithWorksheet(testId);
    }

    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        return test.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public TestManager add(TestManager man) throws Exception {
        return testManager.add(man);
    }

    public TestManager update(TestManager man) throws Exception {
        return testManager.update(man);
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        return testManager.fetchForUpdate(testId);
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        return testManager.abortUpdate(testId);
    }
}
