package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultValidator;

public class AnalysisResultManager implements RPC {
    private static final long                               serialVersionUID = 1L;

    protected Integer                                       analysisId;
    protected ArrayList<ArrayList<ResultViewDO>>            results;
    protected ArrayList<ResultViewDO>                       deletedResults;

    protected HashMap<Integer, AnalyteDO>         analyteList;
    protected HashMap<Integer, TestAnalyteViewDO> testAnalyteList;
    protected HashMap<Integer, TestResultDO> testResultList;
    protected ResultValidator      resultValidator;
    
    protected transient TestManager                         testManager;
    protected transient static AnalysisResultManagerProxy   proxy;

    public static AnalysisResultManager getInstance() {
        AnalysisResultManager arm;

        arm = new AnalysisResultManager();
        arm.results = new ArrayList<ArrayList<ResultViewDO>>();

        return arm;
    }

    /**
     * Creates a new instance of this object with the specified analysis id. Use
     * this function to load an instance of this object from database.
     */
    public static AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId) throws Exception {
        return proxy().fetchByAnalysisId(analysisId, testId);
    }
    
    public static AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        return proxy().fetchByAnalysisIdForDisplay(analysisId);
    }

    public static AnalysisResultManager fetchByTestId(Integer testId) throws Exception {
        return proxy().fetchNewByTestId(testId);
    }
    
    public int rowCount() {
        if (results == null)
            return 0;

        return results.size();
    }

    // getters/setters
    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public ArrayList<ArrayList<ResultViewDO>> getResults() {
        return results;
    }

    public ResultViewDO getResultAt(int row, int col) {
        return results.get(row).get(col);

    }

    public ArrayList<ResultViewDO> getRowAt(int row) {
        return results.get(row);

    }

    public void setResultAt(ResultViewDO result, int row, int col) {
        results.get(row).set(col, result);
    }

    public void addRow(ArrayList<ResultViewDO> row) {
        results.add(row);
    }

    public void removeRowAt(int row) {
        ArrayList<ResultViewDO> list;
        ResultViewDO resultDO;
        Integer id;

        if (results == null || row >= results.size())
            return;

        list = results.get(row);

        if (deletedResults == null)
            deletedResults = new ArrayList<ResultViewDO>();

        for (int i = 0; i < list.size(); i++ ) {
            resultDO = list.get(i);
            id = resultDO.getId();
            if (id != null && id > 0)
                deletedResults.add(resultDO);
        }

        results.remove(row);
    }

    public HashMap<Integer, AnalyteDO> getAnalyteList() {
        return analyteList;
    }

    public void setAnalyteList(HashMap<Integer, AnalyteDO> analyteList) {
        this.analyteList = analyteList;
    }

    public HashMap<Integer, TestAnalyteViewDO> getTestAnalyteList() {
        return testAnalyteList;
    }

    public void setTestAnalyteList(HashMap<Integer, TestAnalyteViewDO> testAnalyteList) {
        this.testAnalyteList = testAnalyteList;
    }

    public ResultValidator getResultValidator() {
        return resultValidator;
    }

    public void setResultValidator(ResultValidator resultValidator) {
        this.resultValidator = resultValidator;
    }

    /*
     * public int getIndex(AnalysisViewDO aDO){ for(int i=0; i<count(); i++)
     * if(items.get(i).analysis == aDO) return i; return -1; }
     */

    // service methods
    public AnalysisResultManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisResultManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    private static AnalysisResultManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisResultManagerProxy();

        return proxy;
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    // ArrayList<SampleItemListItem> getItems() {
    // return items;
    // }

    void setResults(ArrayList<ArrayList<ResultViewDO>> results) {
        this.results = results;
    }

    int deleteCount() {
        if (deletedResults == null)
            return 0;

        return deletedResults.size();
    }

    ResultViewDO getDeletedAt(int i) {
        return deletedResults.get(i);
    }

    public TestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
    }

    public HashMap<Integer, TestResultDO> getTestResultList() {
        return testResultList;
    }

    public void setTestResultList(HashMap<Integer, TestResultDO> testResultList) {
        this.testResultList = testResultList;
    }
}
