package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultValidator;

public class AnalysisResultManager implements RPC {
    private static final long                             serialVersionUID = 1L;

    protected Integer                                     analysisId, mergeTestId, mergeUnitId;
    protected ArrayList<ArrayList<ResultViewDO>>          results;
    protected ArrayList<ResultViewDO>                     deletedResults;
    protected HashMap<Integer, AnalyteDO>                 analyteList;
    protected HashMap<Integer, TestAnalyteListItem>       testAnalyteList;
    protected HashMap<Integer, TestResultDO>              testResultList;
    protected ArrayList<ResultValidator>                  resultValidators;
    protected boolean                                     defaultsLoaded;
    protected AnalysisManager                             analysisManager;

    protected transient TestManager                       testManager;
    protected transient static AnalysisResultManagerProxy proxy;

    private AnalysisResultManager() {
        analysisManager = null;
        testManager = null;
    }

    public static AnalysisResultManager getInstance() {
        AnalysisResultManager arm;

        arm = new AnalysisResultManager();
        arm.results = new ArrayList<ArrayList<ResultViewDO>>();
        arm.setDefaultsLoaded(false);

        return arm;
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

    public ArrayList<ArrayList<ResultViewDO>> getResults() {
        return results;
    }

    public void addRow(ArrayList<ResultViewDO> row) {
        results.add(row);
    }

    public void addRowAt(int index, ArrayList<ResultViewDO> row) {
        if (results == null)
            results = new ArrayList<ArrayList<ResultViewDO>>();

        if (results.size() > index) {
            results.add(index, row);
        } else {
            results.add(row);
            index = results.size() - 1;
        }
    }

    public void addRowAt(int index, Integer rowGroup, Integer firstColTestAnalyteId,
                         Integer firstColAnalyteId, String firstColAnalyteName) {
        ArrayList<ResultViewDO> currlist;
        
        currlist = createNewDataListAt(rowGroup, firstColTestAnalyteId, firstColAnalyteId,
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

    public static AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return proxy().fetchByAnalysisIdForDisplay(analysisId);
    }

    public static AnalysisResultManager merge(AnalysisResultManager manager) throws Exception {
        return proxy().merge(manager);
    }

    /**
     * Creates a new instance of this object with the specified analysis id. Use
     * this function to load an instance of this object from database.
     */
    public static AnalysisResultManager fetchForUpdateWithAnalysisId(Integer analysisId,
                                                                     Integer testId)
                                                                                    throws Exception {
        return proxy().fetchByAnalysisId(analysisId, testId);
    }

    public static AnalysisResultManager fetchForUpdateWithTestId(Integer testId, Integer unitId)
                                                                                                throws Exception {
        return proxy().fetchByTestId(testId, unitId);
    }

    public AnalysisResultManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisResultManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate(AnalysisViewDO anDO) throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, anDO, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(AnalysisViewDO anDO, ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, anDO, errorsList);
    }

    public void validateForComplete(AnalysisViewDO anDO) throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validateForComplete(this, anDO, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    // getters/setters
    public TestManager getTestManager() {
        return testManager;
    }

    public void setTestManager(TestManager testManager) {
        this.testManager = testManager;
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

    public TestAnalyteViewDO getTestAnalyte(Integer rowGroup, Integer testAnalyteId) {
        TestAnalyteViewDO returnDO, tmpDO;
        ArrayList<TestAnalyteViewDO> anList;

        returnDO = null;
        if (rowGroup == null)
            return returnDO;
        anList = testAnalyteList.get(rowGroup).testAnalytes;
        for (int i = 0; i < anList.size(); i++ ) {
            tmpDO = anList.get(i);

            if (testAnalyteId.equals(tmpDO.getId())) {
                returnDO = tmpDO;
                break;
            }
        }

        return returnDO;
    }

    public HashMap<Integer, TestResultDO> getTestResultList() {
        return testResultList;
    }

    public void setTestResultList(HashMap<Integer, TestResultDO> testResultList) {
        this.testResultList = testResultList;
    }

    public ArrayList<ResultValidator> getResultValidators() {
        return resultValidators;
    }

    public ResultValidator getResultValidator(Integer resultGroup) {
        return resultValidators.get(resultGroup.intValue() - 1);
    }

    public void setResultValidators(ArrayList<ResultValidator> resultValidators) {
        this.resultValidators = resultValidators;
    }

    public void addResultValidator(ResultValidator resultValidator) {
        resultValidators.add(resultValidator);
    }

    public Integer validateResultValue(Integer resultGroup, Integer unitId, String value)
                                                                                         throws Exception {
        if (resultGroup == null)
            throw new ParseException("testAnalyteDefinitionChanged");
        return resultValidators.get(resultGroup.intValue() - 1).validate(unitId, value);
    }

    public String formatResultValue(Integer resultGroup, Integer unitId,
                                    Integer testResultId, String value) throws Exception {
        return resultValidators.get(resultGroup.intValue() - 1).getValue(unitId, testResultId,
                                                                         value);
    }

    public String getDefaultValue(Integer resultGroup, Integer unitOfMeasureId) {
        if (isDefaultsLoaded())
            return null;
        else
            return resultValidators.get(resultGroup.intValue() - 1).getDefault(unitOfMeasureId);
    }

    public ArrayList<TestAnalyteViewDO> getNonColumnTestAnalytes(Integer rowGroup) {
        TestAnalyteListItem li;
        ArrayList<TestAnalyteViewDO> returnList, noColList, origAnalyteList, aliasSubList;
        ArrayList<AnalyteDO> everyAliasList;
        HashMap<Integer, ArrayList<TestAnalyteViewDO>> aliasList;
        boolean loaded, newAnalyteId;
        TestAnalyteViewDO taDO, newAliasDo;
        AnalyteDO anDO;
        Integer analyteId;

        li = testAnalyteList.get(rowGroup);

        if (li == null)
            return null;

        origAnalyteList = li.testAnalytes;
        aliasList = li.aliasList;
        loaded = aliasList != null;
        
        noColList = new ArrayList<TestAnalyteViewDO>();
        // clean the list of column anaytes
        for (int i = 0; i < origAnalyteList.size(); i++ ) {
            taDO = origAnalyteList.get(i);
            if ("N".equals(taDO.getIsColumn()))
                noColList.add(origAnalyteList.get(i));
        }

        if ( !loaded) {
            // get the aliases for all the non column analytes
            everyAliasList = new ArrayList<AnalyteDO>();
            try {
                everyAliasList = proxy().getAliasList(noColList);
            } catch (Exception e) {
                everyAliasList = new ArrayList<AnalyteDO>();
            }

            // fill the alias hash for this row group record
            aliasList = new HashMap<Integer, ArrayList<TestAnalyteViewDO>>();
            aliasSubList = new ArrayList<TestAnalyteViewDO>();
            analyteId = -1;
            int a = 0;
            for (int j = 0; j < noColList.size(); j++ ) {
                taDO = noColList.get(j);
                newAnalyteId = true;

                while (a < everyAliasList.size() &&
                       everyAliasList.get(a).getParentAnalyteId().equals(taDO.getAnalyteId())) {
                    anDO = everyAliasList.get(a);
                    if (newAnalyteId) {
                        analyteId = anDO.getParentAnalyteId();
                        aliasSubList = new ArrayList<TestAnalyteViewDO>();
                        aliasList.put(analyteId, aliasSubList);
                    }

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
                    aliasSubList.add(newAliasDo);

                    a++ ;
                }
            }

            li.aliasList = aliasList;
        }

        // create the return list
        returnList = new ArrayList<TestAnalyteViewDO>();
        for (int i = 0; i < noColList.size(); i++ ) {
            taDO = noColList.get(i);
            returnList.add(taDO);

            // check for analytes
            aliasSubList = aliasList.get(taDO.getAnalyteId());

            if (aliasSubList != null) {
                for (int j = 0; j < aliasSubList.size(); j++ )
                    returnList.add(aliasSubList.get(j));
            }
        }

        return returnList;
    }

    public void setDefaultsLoaded(boolean defaultsLoaded) {
        this.defaultsLoaded = defaultsLoaded;
    }

    public int rowCount() {
        if (results == null)
            return 0;

        return results.size();
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getAnalysisId() {
        return analysisId;
    }

    void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    Integer getMergeTestId() {
        return mergeTestId;
    }

    void setMergeTestId(Integer mergeTestId) {
        this.mergeTestId = mergeTestId;
    }

    Integer getMergeUnitId() {
        return mergeUnitId;
    }

    void setMergeUnitId(Integer mergeUnitId) {
        this.mergeUnitId = mergeUnitId;
    }

    boolean isDefaultsLoaded() {
        return defaultsLoaded;
    }

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

    ArrayList<ResultViewDO> getDeleted() {
        return deletedResults;
    }

    void setDeleted(ArrayList<ResultViewDO> deletedResults) {
        this.deletedResults = deletedResults;
    }

    private ArrayList<ResultViewDO> createNewDataListAt(Integer rowGroup,
                                                        Integer testAnalyteId,
                                                        Integer analyteId,
                                                        String analyteName) {
        ArrayList<ResultViewDO> currlist;
        ArrayList<TestAnalyteViewDO> analyteList;
        TestAnalyteViewDO taDO;
        TestAnalyteListItem li;
        int index;

        li = testAnalyteList.get(rowGroup);
        analyteList = li.testAnalytes;

        // find the analyte we want to add
        taDO = null;
        for (index = 0; index < analyteList.size(); index++ ) {
            taDO = analyteList.get(index);

            if ("N".equals(taDO.getIsColumn()) && testAnalyteId.equals(taDO.getId()))
                break;
        }

        currlist = new ArrayList<ResultViewDO>(1);
        do {
            if ("N".equals(taDO.getIsColumn()))
                currlist.add(createResultViewDO("N", taDO, analyteId, analyteName));
            else
                currlist.add(createResultViewDO("Y", taDO, null, null));

            index++ ;
            if (index < analyteList.size())
                taDO = analyteList.get(index);
            else
                taDO = null;
        } while (taDO != null && !"N".equals(taDO.getIsColumn()));

        return currlist;
    }

    private ResultViewDO createResultViewDO(String isColumn, TestAnalyteViewDO taDO,
                                            Integer analyteId, String analyteName) {
        ResultViewDO currDO;

        currDO = new ResultViewDO();
        currDO.setAnalysisId(analysisId);

        if (analyteName != null)
            currDO.setAnalyte(analyteName);
        else
            currDO.setAnalyte(taDO.getAnalyteName());

        if (analyteId != null)
            currDO.setAnalyteId(analyteId);
        else
            currDO.setAnalyteId(taDO.getAnalyteId());

        currDO.setIsColumn(isColumn);
        currDO.setIsReportable(taDO.getIsReportable());
        currDO.setResultGroup(taDO.getResultGroup());
        currDO.setRowGroup(taDO.getRowGroup());
        currDO.setTestAnalyteId(taDO.getId());
        currDO.setTypeId(taDO.getTypeId());

        return currDO;
    }

    private static AnalysisResultManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisResultManagerProxy();

        return proxy;
    }

    public static class TestAnalyteListItem implements Serializable {

        private static final long                             serialVersionUID = 1L;
        public ArrayList<TestAnalyteViewDO>                   testAnalytes;
        public HashMap<Integer, ArrayList<TestAnalyteViewDO>> aliasList;
    }
}
