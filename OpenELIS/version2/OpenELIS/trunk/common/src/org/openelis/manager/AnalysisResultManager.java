package org.openelis.manager;

import java.io.Serializable;
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
    private static final long                             serialVersionUID = 1L;

    protected Integer                                     analysisId;
    protected ArrayList<ArrayList<ResultViewDO>>          results;
    protected ArrayList<ResultViewDO>                     deletedResults;

    protected HashMap<Integer, AnalyteDO>                 analyteList;
    protected HashMap<Integer, TestAnalyteListItem>       testAnalyteList;
    protected HashMap<Integer, TestResultDO>              testResultList;
    protected ArrayList<ResultValidator>                  resultValidators;

    protected transient TestManager                       testManager;
    protected transient static AnalysisResultManagerProxy proxy;

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
    public static AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId)
                                                                                             throws Exception {
        return proxy().fetchByAnalysisId(analysisId, testId);
    }

    public static AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId)
                                                                                       throws Exception {
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

    public void addRowAt(int index, ArrayList<ResultViewDO> row) {
        if (results == null)
            results = new ArrayList<ArrayList<ResultViewDO>>();

        if (results.size() > index)
            results.add(index, row);
        else
            results.add(row);
    }

    public void addRowAt(int index,
                         Integer rowGroup,
                         Integer firstColTestAnalyteId,
                         Integer firstColAnalyteId,
                         String firstColAnalyteName) {
        ArrayList<ResultViewDO> currlist;
        currlist = createNewDataListAt(index, rowGroup, firstColTestAnalyteId, firstColAnalyteId,
                                       firstColAnalyteName);

        addRowAt(index, currlist);
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

    public HashMap<Integer, TestAnalyteListItem> getTestAnalyteList() {
        return testAnalyteList;
    }

    public void setTestAnalyteList(HashMap<Integer, TestAnalyteListItem> testAnalyteList) {
        this.testAnalyteList = testAnalyteList;
    }

    public ArrayList<TestAnalyteViewDO> getNonColumnTestAnalytes(Integer rowGroup) {
        TestAnalyteListItem li;
        ArrayList<TestAnalyteViewDO> returnList, list;
        ArrayList<AnalyteDO> aliasList;
        boolean loaded;
        TestAnalyteViewDO taDO, newAliasDo;
        AnalyteDO anDO;

        li = testAnalyteList.get(rowGroup);
        list = li.testAnalytes;
        loaded = li.aliasLoaded;

        if (li == null)
            return null;

        returnList = new ArrayList<TestAnalyteViewDO>();
        if ( !loaded) {
            aliasList = new ArrayList<AnalyteDO>();
            // clean the list of column anaytes
            for (int i = 0; i < list.size(); i++ ) {
                taDO = list.get(i);
                if ("Y".equals(taDO.getIsColumn()))
                    list.remove(i);
            }

            try {
                aliasList = proxy().getAlias(list);
            } catch (Exception e) {
                aliasList = new ArrayList<AnalyteDO>();
            }

            int j = 0;
            for (int i = 0; i < list.size(); i++ ) {
                taDO = list.get(i);
                returnList.add(taDO);

                while (j < aliasList.size() &&
                       aliasList.get(j).getParentAnalyteId().equals(taDO.getAnalyteId())) {
                    anDO = aliasList.get(j);

                    newAliasDo = new TestAnalyteViewDO();
                    newAliasDo.setId(taDO.getId());
                    newAliasDo.setAnalyteId(anDO.getId());
                    newAliasDo.setAnalyteName(anDO.getName());
                    newAliasDo.setIsColumn("N");
                    newAliasDo.setResultGroup(taDO.getResultGroup());
                    newAliasDo.setRowGroup(taDO.getRowGroup());
                    newAliasDo.setSortOrder(taDO.getSortOrder());
                    newAliasDo.setTestId(taDO.getTestId());
                    newAliasDo.setTypeId(taDO.getTypeId());
                    newAliasDo.setIsAlias("Y");

                    returnList.add(newAliasDo);
                    j++ ;
                }
            }

            li.testAnalytes = returnList;
            li.aliasLoaded = true;
        } else
            returnList = list;

        return returnList;
    }

    public ArrayList<ResultValidator> getResultValidators() {
        return resultValidators;
    }
    
    public ResultValidator getResultValidator(Integer resultGroup) { 
        return resultValidators.get(resultGroup.intValue()-1);
    }
    
    public String getDefaultValue(Integer resultGroup, Integer unitOfMeasureId) { 
        return resultValidators.get(resultGroup.intValue()-1).getDefault(unitOfMeasureId);
    }

    public void setResultValidators(ArrayList<ResultValidator> resultValidators) {
        this.resultValidators = resultValidators;
    }

    public void addResultValidator(ResultValidator resultValidator) {
        resultValidators.add(resultValidator);
    }

    public Integer validateResultValue(Integer resultGroup, Integer unitId, String value)
                                                                                         throws Exception {
        return resultValidators.get(resultGroup.intValue() - 1).validate(unitId, value);
    }

    private ArrayList<ResultViewDO> createNewDataListAt(int row,
                                                        Integer rowGroup,
                                                        Integer testAnalyteId,
                                                        Integer analyteId,
                                                        String analyteName) {
        ResultViewDO currDO;
        ArrayList<ResultViewDO> prevlist, currlist;

        currlist = new ArrayList<ResultViewDO>(1);
        prevlist = null;

        prevlist = results.get(row);

        for (int i = 0; i < prevlist.size(); i++ ) {
            if (i == 0)
                currDO = createResultViewDO("N", rowGroup, testAnalyteId, analyteId, analyteName);
            else
                currDO = createResultViewDO("Y", rowGroup, testAnalyteId, null, null);
            currlist.add(currDO);
        }

        return currlist;
    }

    private ResultViewDO createResultViewDO(String isColumn,
                                            Integer rowGroup,
                                            Integer testAnalyteId,
                                            Integer analyteId,
                                            String analyteName) {
        ResultViewDO currDO;
        ArrayList<TestAnalyteViewDO> taList;
        TestAnalyteViewDO ta, tmp;

        currDO = new ResultViewDO();
        taList = testAnalyteList.get(rowGroup).testAnalytes;
        ta = null;

        for (int i = 0; i < taList.size(); i++ ) {
            tmp = taList.get(i);
            if (tmp.getId().equals(testAnalyteId)) {
                ta = tmp;
                break;
            }
        }

        if (ta == null)
            return null;

        currDO.setAnalysisId(analysisId);

        if (analyteName != null)
            currDO.setAnalyte(analyteName);
        else
            currDO.setAnalyte(ta.getAnalyteName());

        if (analyteId != null)
            currDO.setAnalyteId(analyteId);
        else
            currDO.setAnalyteId(ta.getAnalyteId());

        currDO.setIsColumn(isColumn);
        currDO.setIsReportable(ta.getIsReportable());
        currDO.setResultGroup(ta.getResultGroup());
        currDO.setRowGroup(ta.getRowGroup());
        currDO.setTestAnalyteId(ta.getId());
        currDO.setTypeId(ta.getTypeId());

        return currDO;
    }

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

    public static class TestAnalyteListItem implements Serializable {

        private static final long           serialVersionUID = 1L;
        public boolean                      aliasLoaded;
        public ArrayList<TestAnalyteViewDO> testAnalytes;

        public TestAnalyteListItem() {
            aliasLoaded = false;
        }
    }
}
