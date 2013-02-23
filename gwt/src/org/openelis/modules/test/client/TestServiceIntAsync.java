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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TestServiceIntAsync {

    void abortUpdate(Integer testId, AsyncCallback<TestManager> callback);

    void add(TestManager man, AsyncCallback<TestManager> callback);

    void fetchActiveByNameMethodName(Query query, AsyncCallback<TestViewDO> callback);

    void fetchById(Integer testId, AsyncCallback<TestManager> callback);

    void fetchByName(String name, AsyncCallback<ArrayList<TestMethodVO>> callback);

    void fetchByPanelId(Integer id, AsyncCallback<ArrayList<TestMethodVO>> callback);

    void fetchForUpdate(Integer testId, AsyncCallback<TestManager> callback);

    void fetchList(AsyncCallback<ArrayList<TestMethodVO>> callback);

    void fetchNameMethodSectionByName(String name, AsyncCallback<ArrayList<PanelVO>> callback);

    void fetchPrepTestsByTestId(Integer testId, AsyncCallback<TestPrepManager> callback);

    void fetchReflexiveTestByTestId(Integer testId, AsyncCallback<TestReflexManager> callback);

    void fetchSampleTypeByTestId(Integer testId, AsyncCallback<TestTypeOfSampleManager> callback);

    void fetchTestAnalyteByTestId(Integer testId, AsyncCallback<TestAnalyteManager> callback);

    void fetchTestMethodSampleTypeList(AsyncCallback<ArrayList<TestMethodSampleTypeVO>> callback);

    void fetchTestResultByTestId(Integer testId, AsyncCallback<TestResultManager> callback);

    void fetchUnitsForWorksheetAutocomplete(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchWithAnalytesAndResults(Integer testId, AsyncCallback<TestManager> callback);

    void fetchWithPrepTestsAndReflexTests(Integer testId, AsyncCallback<TestManager> callback);

    void fetchWithPrepTestsSampleTypes(Integer testId, AsyncCallback<TestManager> callback);

    void fetchWithSampleTypes(Integer testId, AsyncCallback<TestManager> callback);

    void fetchWithWorksheet(Integer testId, AsyncCallback<TestManager> callback);

    void fetchWorksheetByTestId(Integer testId, AsyncCallback<TestWorksheetManager> callback);

    void query(Query query, AsyncCallback<ArrayList<TestMethodVO>> callback);

    void update(TestManager man, AsyncCallback<TestManager> callback);

}
