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
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestManagerRemote;
import org.openelis.remote.TestRemote;

public class TestService {

    private static final int rowPP = 26;

    public TestManager fetchById(Integer testId) throws Exception {
        return remoteManager().fetchById(testId);
    }

    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        return remote().fetchByName(name + "%", 10);
    }
    
    public ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception {
        return remote().fetchByPanelId(id);
    }

    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception {
        return remote().fetchNameMethodSectionByName(name + "%", 1000000);
    }
    
    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        return remote().fetchTestMethodSampleTypeList();
    }

    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        return remoteManager().fetchSampleTypeByTestId(testId);
    }

    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception {
        return remoteManager().fetchTestAnalytesByTestId(testId);
    }

    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception {
        return remoteManager().fetchTestResultsByTestId(testId);
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        return remoteManager().fetchPrepTestsByTestId(testId);
    }

    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception {
        return remoteManager().fetchReflexiveTestsByTestId(testId);
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        return remoteManager().fetchWorksheetByTestId(testId);
    }

    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return remoteManager().fetchWithSampleTypes(testId);
    }

    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return remoteManager().fetchWithAnalytesAndResults(testId);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        return remoteManager().fetchWithPrepTestsSampleTypes(testId);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        return remoteManager().fetchWithPrepTestsAndReflexTests(testId);
    }

    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        return remoteManager().fetchWithWorksheet(testId);
    }

    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }

    public TestManager add(TestManager man) throws Exception {
        return remoteManager().add(man);
    }

    public TestManager update(TestManager man) throws Exception {
        return remoteManager().update(man);
    }

    public TestManager fetchForUpdate(Integer testId) throws Exception {
        return remoteManager().fetchForUpdate(testId);
    }

    public TestManager abortUpdate(Integer testId) throws Exception {
        return remoteManager().abortUpdate(testId);
    }

    private TestRemote remote() {
        return (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
    }

    private TestManagerRemote remoteManager() {
        return (TestManagerRemote)EJBFactory.lookup("openelis/TestManagerBean/remote");
    }

}
