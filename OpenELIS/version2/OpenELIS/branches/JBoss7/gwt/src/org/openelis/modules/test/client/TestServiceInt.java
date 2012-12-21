package org.openelis.modules.test.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelVO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("test")
public interface TestServiceInt extends RemoteService {

    TestManager fetchById(Integer testId) throws Exception;

    ArrayList<TestMethodVO> fetchByName(String name) throws Exception;

    TestViewDO fetchActiveByNameMethodName(Query query) throws Exception;

    ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception;

    ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception;

    ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception;

    ArrayList<TestMethodVO> fetchList() throws Exception;

    TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception;

    ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Query query) throws Exception;

    TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception;

    TestResultManager fetchTestResultByTestId(Integer testId) throws Exception;

    TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception;

    TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception;

    TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception;

    TestManager fetchWithSampleTypes(Integer testId) throws Exception;

    TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception;

    TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception;

    TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception;

    TestManager fetchWithWorksheet(Integer testId) throws Exception;

    ArrayList<TestMethodVO> query(Query query) throws Exception;

    TestManager add(TestManager man) throws Exception;

    TestManager update(TestManager man) throws Exception;

    TestManager fetchForUpdate(Integer testId) throws Exception;

    TestManager abortUpdate(Integer testId) throws Exception;

}