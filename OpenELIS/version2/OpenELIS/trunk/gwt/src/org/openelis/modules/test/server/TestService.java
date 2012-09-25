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

import org.openelis.domain.PanelVO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.server.EJBFactory;

public class TestService {

    public TestManager fetchById(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchById(testId);
    }

    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        return EJBFactory.getTest().fetchByName(name + "%", 1000);
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
        
        return EJBFactory.getTest().fetchActiveByNameMethodName(testName, methodName);
    }

    public ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception {
        return EJBFactory.getTest().fetchByPanelId(id);
    }

    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception {
        return EJBFactory.getTest().fetchNameMethodSectionByName(name + "%", 1000000);
    }
    
    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        return EJBFactory.getTest().fetchTestMethodSampleTypeList();
    }

    public ArrayList<TestMethodVO> fetchList() throws Exception {
        return EJBFactory.getTest().fetchList();
    }
    
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchSampleTypeByTestId(testId);
    }

    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchTestAnalytesByTestId(testId);
    }

    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchTestResultsByTestId(testId);
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchPrepTestsByTestId(testId);
    }

    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchReflexiveTestsByTestId(testId);
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWorksheetByTestId(testId);
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWithSampleTypes(testId);
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWithAnalytesAndResults(testId);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWithPrepTestsSampleTypes(testId);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWithPrepTestsAndReflexTests(testId);
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchWithWorksheet(testId);
    }

    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        return EJBFactory.getTest().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public TestManager add(TestManager man) throws Exception {
        return EJBFactory.getTestManager().add(man);
    }

    public TestManager update(TestManager man) throws Exception {
        return EJBFactory.getTestManager().update(man);
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        return EJBFactory.getTestManager().fetchForUpdate(testId);
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        return EJBFactory.getTestManager().abortUpdate(testId);
    }
}
