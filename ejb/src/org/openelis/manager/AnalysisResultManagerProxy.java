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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.utilcommon.ResultValidator;

public class AnalysisResultManagerProxy {

    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results;
        AnalysisResultManager man;

        results = new ArrayList<ArrayList<ResultViewDO>>();
        local().fetchByAnalysisIdForDisplay(analysisId, results);        
        man = AnalysisResultManager.getInstance();
        man.setResults(results);

        return man;
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId)
                                                                                      throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results;
        HashMap<Integer, TestResultDO> testResultList;
        HashMap<Integer, AnalyteDO> analyteList;
        HashMap<Integer, TestAnalyteListItem> testAnalyteList;
        ArrayList<ResultValidator> resultValidators;
        AnalysisResultManager man;

        results = new ArrayList<ArrayList<ResultViewDO>>();
        testResultList = new HashMap<Integer, TestResultDO>();
        analyteList = new HashMap<Integer, AnalyteDO>();
        testAnalyteList = new HashMap<Integer, TestAnalyteListItem>();
        resultValidators = new ArrayList<ResultValidator>();

        local().fetchByAnalysisId(analysisId, results, testResultList, analyteList,
                                  testAnalyteList, resultValidators);
        man = AnalysisResultManager.getInstance();
        man.setResults(results);
        man.setTestResultList(testResultList);
        man.setAnalyteList(analyteList);
        man.setTestAnalyteList(testAnalyteList);
        man.setResultValidators(resultValidators);
        man.setTestManager(TestManager.fetchWithAnalytesAndResults(testId));

        return man;
    }

    public AnalysisResultManager fetchByTestId(Integer testId, Integer unitId) throws Exception {
        ArrayList<ArrayList<ResultViewDO>> results;
        HashMap<Integer, TestResultDO> testResultList;
        HashMap<Integer, AnalyteDO> analyteList;
        HashMap<Integer, TestAnalyteListItem> testAnalyteList;
        ArrayList<ResultValidator> resultValidators;
        AnalysisResultManager man;

        results = new ArrayList<ArrayList<ResultViewDO>>();
        testResultList = new HashMap<Integer, TestResultDO>();
        analyteList = new HashMap<Integer, AnalyteDO>();
        testAnalyteList = new HashMap<Integer, TestAnalyteListItem>();
        resultValidators = new ArrayList<ResultValidator>();

        local().fetchByTestIdNoResults(testId, unitId, results, testResultList, analyteList,
                                       testAnalyteList, resultValidators);
        man = AnalysisResultManager.getInstance();
        man.setResults(results);
        man.setTestResultList(testResultList);
        man.setAnalyteList(analyteList);
        man.setTestAnalyteList(testAnalyteList);
        man.setResultValidators(resultValidators);
        man.setTestManager(TestManager.fetchWithAnalytesAndResults(testId));

        return man;
    }

    public AnalysisResultManager add(AnalysisResultManager man) throws Exception {
        ArrayList<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> grid;
        ResultViewDO data;
        int i, j, so;
        ResultLocal l;

        grid = man.getResults();
        so = 0;
        l = local();
        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);

                data.setAnalysisId(man.getAnalysisId());
                data.setSortOrder( ++so);

                l.add(data);
            }
        }

        return man;
    }

    public AnalysisResultManager update(AnalysisResultManager man) throws Exception {
        ArrayList<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> grid;
        ResultViewDO data;
        int i, j, so;
        ResultLocal l;

        grid = man.getResults();
        so = 0;
        l = local();
        for (i = 0; i < man.deleteCount(); i++ ) {
            l.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.rowCount(); i++ ) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++ ) {
                data = list.get(j);
                data.setSortOrder( ++so);
                if (data.getId() == null) {
                    data.setAnalysisId(man.getAnalysisId());
                    l.add(data);
                } else {
                    l.update(data);
                }
            }
        }

        return man;
    }

    public AnalysisResultManager merge(AnalysisResultManager oldMan) throws Exception {
        AnalysisResultManager newMan;

        newMan = fetchByTestId(oldMan.getMergeTestId(), oldMan.getMergeUnitId());
        newMan.setAnalysisId(oldMan.getAnalysisId());
        mergeResultGrid(oldMan.getResults(), newMan.getResults());

        // delete the old results and set the list in the new manager
        for (int i = 0; i < oldMan.rowCount(); i++ )
            oldMan.removeRowAt(i);

        newMan.setDeleted(oldMan.getDeleted());

        return newMan;
    }

    public ArrayList<AnalyteDO> getAliasList(Integer analyteId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(AnalysisResultManager man,
                         AnalysisViewDO anDO,
                         ValidationErrorsList errorsList) throws Exception {
        ArrayList<ResultViewDO> results;
        ResultViewDO result;
        TestResultDO testResultDO;
        Integer testResultId;

        // go through the results and put a form error if one is found to be
        // invalid
        try {
            for (int i = 0; i < man.rowCount(); i++ ) {
                results = man.getRowAt(i);

                for (int j = 0; j < results.size(); j++ ) {
                    result = results.get(j);

                    if (result.getValue() != null && !"".equals(result.getValue())) {
                        testResultId = man.validateResultValue(result.getResultGroup(),
                                                               anDO.getUnitOfMeasureId(),
                                                               result.getValue());
                        testResultDO = man.getTestResultList().get(testResultId);

                        result.setTypeId(testResultDO.getTypeId());
                        result.setTestResultId(testResultDO.getId());
                    }
                }
            }

        } catch (ParseException e) {
            errorsList.add(new FormErrorException("oneOrMoreResultValuesInvalid",
                                                  anDO.getTestName(), anDO.getMethodName()));
        }
    }

    public void validateForComplete(AnalysisResultManager man,
                                    AnalysisViewDO anDO,
                                    ValidationErrorsList errorsList) throws Exception {
        ArrayList<ResultViewDO> results;
        ResultViewDO result;
        TestResultDO testResultDO;
        Integer testResultId;
        Integer resultRequiredId;
        boolean requiredEx, invalidEx;
        int i, j;

        resultRequiredId = getIdFromSystemName("test_analyte_req");
        i = 0;
        // go through the results look for empty required and invalid results
        while (i < man.rowCount()) {
            results = man.getRowAt(i);

            j = 0;
            while (j < results.size()) {
                result = results.get(j);

                // if required if needs to have a value
                if (resultRequiredId.equals(result.getTypeId()) &&
                    (DataBaseUtil.isEmpty(result.getValue()))) {
                    errorsList.add(new FormErrorException("completeStatusRequiredResultsException",
                                                          anDO.getTestName(), anDO.getMethodName()));
                }

                // make sure the result is valid if its filled out
                try {
                    if (!DataBaseUtil.isEmpty(result.getValue())) {
                        testResultId = man.validateResultValue(result.getResultGroup(),
                                                               anDO.getUnitOfMeasureId(),
                                                               result.getValue());
                        testResultDO = man.getTestResultList().get(testResultId);

                        result.setTypeId(testResultDO.getTypeId());
                        result.setTestResultId(testResultDO.getId());
                    }
                } catch (ParseException e) {
                    errorsList.add(new FormErrorException("completeStatusInvalidResultsException",
                                                          anDO.getTestName(), anDO.getMethodName()));
                }

                j++ ;
            }

            i++ ;
        }

    }

    public Integer getIdFromSystemName(String systemName) throws Exception {
        DictionaryDO dictDO = dictionaryLocal().fetchBySystemName(systemName);

        return dictDO.getId();
    }

    private void mergeResultGrid(ArrayList<ArrayList<ResultViewDO>> oldGrid,
                                 ArrayList<ArrayList<ResultViewDO>> newGrid) {
        ResultViewDO r1, r2;
        boolean found;
        int k;

        k = 0;
        for (int i = 0; i < newGrid.size(); i++ ) {
            // try and find an analyte that matches
            for (int j = 0; j < oldGrid.size(); j++ ) {
                k = 0;
                found = false;

                while (k < oldGrid.get(j).size() && k < newGrid.get(i).size()) {
                    r1 = newGrid.get(i).get(k);
                    r2 = oldGrid.get(j).get(k);

                    if (r1.getAnalyteId().equals(r2.getAnalyteId())) {
                        r1.setValue(r2.getValue());
                        found = true;
                    } else if (k == 0)
                        break;

                    k++ ;
                }

                if (found)
                    break;
            }
        }
    }

    private static DictionaryLocal dictionaryLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private ResultLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (ResultLocal)ctx.lookup("openelis/ResultBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
