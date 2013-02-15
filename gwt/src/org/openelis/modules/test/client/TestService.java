package org.openelis.modules.test.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelVO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TestService implements TestServiceInt, TestServiceIntAsync {
    
    static TestService instance;
    
    TestServiceIntAsync service;
    
    public static TestService get() {
        if(instance == null)
            instance = new TestService();
        
        return instance;
    }
    
    private TestService() {
        service = (TestServiceIntAsync)GWT.create(TestServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer testId, AsyncCallback<TestManager> callback) {
        service.abortUpdate(testId, callback);
    }

    @Override
    public void add(TestManager man, AsyncCallback<TestManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActiveByNameMethodName(Query query, AsyncCallback<TestViewDO> callback) {
        service.fetchActiveByNameMethodName(query, callback);
    }

    @Override
    public void fetchById(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchById(testId, callback);
    }

    @Override
    public void fetchByName(String name, AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchByName(name, callback);
    }

    @Override
    public void fetchByPanelId(Integer id, AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchByPanelId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchForUpdate(testId, callback);
    }

    @Override
    public void fetchList(AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchList(callback);
    }

    @Override
    public void fetchNameMethodSectionByName(String name, AsyncCallback<ArrayList<PanelVO>> callback) {
        service.fetchNameMethodSectionByName(name, callback);
    }

    @Override
    public void fetchPrepTestsByTestId(Integer testId, AsyncCallback<TestPrepManager> callback) {
        service.fetchPrepTestsByTestId(testId, callback);
    }

    @Override
    public void fetchReflexiveTestByTestId(Integer testId, AsyncCallback<TestReflexManager> callback) {
        service.fetchReflexiveTestByTestId(testId, callback);
    }

    @Override
    public void fetchSampleTypeByTestId(Integer testId,
                                        AsyncCallback<TestTypeOfSampleManager> callback) {
        service.fetchSampleTypeByTestId(testId, callback);
    }

    @Override
    public void fetchTestAnalyteByTestId(Integer testId, AsyncCallback<TestAnalyteManager> callback) {
        service.fetchTestAnalyteByTestId(testId, callback);
    }

    @Override
    public void fetchTestMethodSampleTypeList(AsyncCallback<ArrayList<TestMethodSampleTypeVO>> callback) {
        service.fetchTestMethodSampleTypeList(callback);
    }

    @Override
    public void fetchTestResultByTestId(Integer testId, AsyncCallback<TestResultManager> callback) {
        service.fetchTestResultByTestId(testId, callback);
    }

    @Override
    public void fetchUnitsForWorksheetAutocomplete(Query query,
                                                   AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchUnitsForWorksheetAutocomplete(query, callback);
    }

    @Override
    public void fetchWithAnalytesAndResults(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchWithAnalytesAndResults(testId, callback);
    }

    @Override
    public void fetchWithPrepTestsAndReflexTests(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchWithPrepTestsAndReflexTests(testId, callback);
    }

    @Override
    public void fetchWithPrepTestsSampleTypes(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchWithPrepTestsSampleTypes(testId, callback);
    }

    @Override
    public void fetchWithSampleTypes(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchWithSampleTypes(testId, callback);
    }

    @Override
    public void fetchWithWorksheet(Integer testId, AsyncCallback<TestManager> callback) {
        service.fetchWithWorksheet(testId, callback);
    }

    @Override
    public void fetchWorksheetByTestId(Integer testId, AsyncCallback<TestWorksheetManager> callback) {
        service.fetchWorksheetByTestId(testId, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(TestManager man, AsyncCallback<TestManager> callback) {
        service.update(man, callback);
    }

    @Override
    public TestManager fetchById(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchById(testId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchByName(String name) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchByName(name, callback);
        return callback.getResult();
    }

    @Override
    public TestViewDO fetchActiveByNameMethodName(Query query) throws Exception {
        Callback<TestViewDO> callback;
        
        callback = new Callback<TestViewDO>();
        service.fetchActiveByNameMethodName(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchByPanelId(Integer id) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchByPanelId(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name) throws Exception {
        Callback<ArrayList<PanelVO>> callback;
        
        callback = new Callback<ArrayList<PanelVO>>();
        service.fetchNameMethodSectionByName(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        Callback<ArrayList<TestMethodSampleTypeVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodSampleTypeVO>>();
        service.fetchTestMethodSampleTypeList(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchList() throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchList(callback);
        return callback.getResult();
    }

    @Override
    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer testId) throws Exception {
        Callback<TestTypeOfSampleManager> callback;
        
        callback = new Callback<TestTypeOfSampleManager>();
        service.fetchSampleTypeByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchUnitsForWorksheetAutocomplete(query, callback);
        return callback.getResult();
    }

    @Override
    public TestAnalyteManager fetchTestAnalyteByTestId(Integer testId) throws Exception {
        Callback<TestAnalyteManager> callback;
        
        callback = new Callback<TestAnalyteManager>();
        service.fetchTestAnalyteByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestResultManager fetchTestResultByTestId(Integer testId) throws Exception {
        Callback<TestResultManager> callback;
        
        callback = new Callback<TestResultManager>();
        service.fetchTestResultByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestPrepManager fetchPrepTestsByTestId(Integer testId) throws Exception {
        Callback<TestPrepManager> callback;
        
        callback = new Callback<TestPrepManager>();
        service.fetchPrepTestsByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestReflexManager fetchReflexiveTestByTestId(Integer testId) throws Exception {
        Callback<TestReflexManager> callback;
        
        callback = new Callback<TestReflexManager>();
        service.fetchReflexiveTestByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestWorksheetManager fetchWorksheetByTestId(Integer testId) throws Exception {
        Callback<TestWorksheetManager> callback;
        
        callback = new Callback<TestWorksheetManager>();
        service.fetchWorksheetByTestId(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchWithSampleTypes(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchWithAnalytesAndResults(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchWithPrepTestsSampleTypes(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchWithPrepTestsSampleTypes(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchWithPrepTestsAndReflexTests(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchWithPrepTestsAndReflexTests(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchWithWorksheet(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchWithWorksheet(testId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> query(Query query) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public TestManager add(TestManager man) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public TestManager update(TestManager man) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public TestManager fetchForUpdate(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.fetchForUpdate(testId, callback);
        return callback.getResult();
    }

    @Override
    public TestManager abortUpdate(Integer testId) throws Exception {
        Callback<TestManager> callback;
        
        callback = new Callback<TestManager>();
        service.abortUpdate(testId, callback);
        return callback.getResult();
    }

}
