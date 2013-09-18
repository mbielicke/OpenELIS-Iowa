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

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.modules.result.client.ResultService;

public class AnalysisResultManagerProxy {

    protected static Integer      dictTypeId;

    public AnalysisResultManagerProxy() {
    }

    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        return ResultService.get().fetchByAnalysisIdForDisplay(analysisId);
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return ResultService.get().fetchByAnalysisId(analysisId);
    }

    public AnalysisResultManager fetchByTestId(Integer testId, Integer unitId) throws Exception {
        AnalysisDO data;

        data = new AnalysisDO();
        data.setTestId(testId);
        data.setUnitOfMeasureId(unitId);

        return ResultService.get().fetchByTestId(data);
    }
    
    public AnalysisResultManager fetchByTestIdForOrderImport(Integer testId, Integer unitId) throws Exception {
        AnalysisDO data;

        data = new AnalysisDO();
        data.setTestId(testId);
        data.setUnitOfMeasureId(unitId);

        return ResultService.get().fetchByTestIdForOrderImport(data);
    }

    public AnalysisResultManager add(AnalysisResultManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public AnalysisResultManager update(AnalysisResultManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public AnalysisResultManager merge(AnalysisResultManager oldMan) throws Exception {
        return ResultService.get().merge(oldMan);
    }

    public ArrayList<AnalyteDO> getAliasList(ArrayList<TestAnalyteViewDO> analytes)
                                                                                   throws Exception {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();
        query = new Query();

        for (int i = 0; i < analytes.size(); i++ ) {
            field = new QueryData();
            field.query = analytes.get(i).getAnalyteId().toString();
            fields.add(field);
        }

        query.setFields(fields);
        return ResultService.get().getAliasList(query);
    }

    public void validate(AnalysisResultManager man,
                         AnalysisViewDO data,
                         ValidationErrorsList errorsList) throws Exception {
        ArrayList<ResultViewDO> results;
        ResultViewDO result;
        TestResultDO testResult;
        Integer testResultId;

        // go through the results and put a form error if one is found to be
        // invalid
        try {
            for (int i = 0; i < man.rowCount(); i++ ) {
                results = man.getRowAt(i);

                for (int j = 0; j < results.size(); j++ ) {
                    result = results.get(j);

                    if (!DataBaseUtil.isEmpty(result.getValue())) {
                        testResultId = man.validateResultValue(result.getResultGroup(),
                                                               data.getUnitOfMeasureId(),
                                                               result.getValue());
                        testResult = man.getTestResultList().get(testResultId);

                        result.setTypeId(testResult.getTypeId());
                        result.setTestResultId(testResult.getId());
                        /*
                         * If the tab for results on the screen was never opened
                         * after an analysis was either added or its test was changed
                         * then the code on the screen wouldn't have had the chance
                         * to put the dictionary entry's id as the value for the
                         * results of the type dictionary. This code makes sure
                         * that the correct value gets set. For the results of the
                         * other types, the value doesn't need to be changed.   
                         */
                        if (testResult.getTypeId().equals(Constants.dictionary().TEST_RES_TYPE_DICTIONARY))
                            result.setValue(testResult.getValue());                     
                    }
                }
            }

        } catch (ParseException e) {
            errorsList.add(new FormErrorException("oneOrMoreResultValuesInvalid",
                                                  data.getTestName(), data.getMethodName()));
        }
    }

    public void validateForComplete(AnalysisResultManager man,
                                    AnalysisViewDO data,
                                    ValidationErrorsList errorsList) throws Exception {
        ArrayList<ResultViewDO> results;
        ResultViewDO result, rowResult;
        TestResultDO testResult;
        Integer testResultId;
        int i, j;

        i = 0;
        // go through the results look for empty required and invalid results
        while (i < man.rowCount()) {
            results = man.getRowAt(i);

            j = 0;
            rowResult = null;
            while (j < results.size()) {
                result = results.get(j);
                if (j == 0)
                    rowResult = result;

                // if required if needs to have a value
                if (DataBaseUtil.isSame(Constants.dictionary().TEST_ANALYTE_REQ, result.getTestAnalyteTypeId()) &&
                    (DataBaseUtil.isEmpty(result.getValue()))) {
                    if (j == 0)
                        errorsList.add(new FormErrorException("completeStatusRequiredResultsException",
                                                              data.getTestName(), data.getMethodName(),
                                                              rowResult.getAnalyte(), "Value"));
                    else
                        errorsList.add(new FormErrorException("completeStatusRequiredResultsException",
                                                              data.getTestName(), data.getMethodName(),
                                                              rowResult.getAnalyte(), result.getAnalyte()));
                    throw errorsList;
                }

                // make sure the result is valid if its filled out
                try {
                    if (!DataBaseUtil.isEmpty(result.getValue())) {
                        testResultId = man.validateResultValue(result.getResultGroup(),
                                                               data.getUnitOfMeasureId(),
                                                               result.getValue());
                        testResult = man.getTestResultList().get(testResultId);

                        result.setTypeId(testResult.getTypeId());
                        result.setTestResultId(testResult.getId());
                    }
                } catch (ParseException e) {
                    if (j == 0)
                        errorsList.add(new FormErrorException("completeStatusInvalidResultsException",
                                                              data.getTestName(), data.getMethodName(),
                                                              rowResult.getAnalyte(), "Value"));
                    else
                        errorsList.add(new FormErrorException("completeStatusInvalidResultsException",
                                                              data.getTestName(), data.getMethodName(),
                                                              rowResult.getAnalyte(), result.getAnalyte()));
                    throw errorsList;
                }

                j++ ;
            }

            i++ ;
        }

    }
}