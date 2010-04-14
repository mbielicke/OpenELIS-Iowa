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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.services.ScreenService;

public class AnalysisResultManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.result.server.ResultService";
    protected ScreenService       service;

    public AnalysisResultManagerProxy() {
        service = new ScreenService("OpenELISServlet?service=" + SAMPLE_SERVICE_URL);
    }

    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception {
        return service.call("fetchByAnalysisIdForDisplay", analysisId);
    }

    public AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId)
                                                                                      throws Exception {
        AnalysisDO anDO;

        anDO = new AnalysisDO();
        anDO.setTestId(testId);
        anDO.setId(analysisId);

        return service.call("fetchByAnalysisId", anDO);
    }

    public AnalysisResultManager fetchByTestId(Integer testId, Integer unitId) throws Exception {
        AnalysisDO anDO;

        anDO = new AnalysisDO();
        anDO.setTestId(testId);
        anDO.setUnitOfMeasureId(unitId);
        
        return service.call("fetchByTestId", anDO);
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
        return service.call("merge", oldMan);
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
        return service.callList("getAliasList", query);
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
        requiredEx = false;
        invalidEx = false;
        // go through the results look for empty required and invalid results
        while (i < man.rowCount() && ( !requiredEx || !invalidEx)) {
            results = man.getRowAt(i);

            j = 0;
            while (j < results.size() && ( !requiredEx || !invalidEx)) {
                result = results.get(j);

                // if required if needs to have a value
                if ( !requiredEx && resultRequiredId.equals(result.getTypeId()) &&
                    (result.getValue() == null || "".equals(result.getValue())))
                    requiredEx = true;

                // make sure the result is valid if its filled out
                if ( !invalidEx) {
                    try {
                        if (result.getValue() != null && !"".equals(result.getValue())) {
                            testResultId = man.validateResultValue(result.getResultGroup(),
                                                                   anDO.getUnitOfMeasureId(),
                                                                   result.getValue());
                            testResultDO = man.getTestResultList().get(testResultId);

                            result.setTypeId(testResultDO.getTypeId());
                            result.setTestResultId(testResultDO.getId());
                        }
                    } catch (ParseException e) {
                        invalidEx = true;
                    }
                }
                j++ ;
            }

            i++ ;
        }

        if (requiredEx)
            errorsList.add(new FormErrorException("completeStatusRequiredResultsException",
                                                  anDO.getTestName(), anDO.getMethodName()));

        if (invalidEx)
            errorsList.add(new FormErrorException("completeStatusInvalidResultsException",
                                                  anDO.getTestName(), anDO.getMethodName()));
    }

    public Integer getIdFromSystemName(String systemName) throws Exception {
        return DictionaryCache.getIdFromSystemName(systemName);
    }
}
