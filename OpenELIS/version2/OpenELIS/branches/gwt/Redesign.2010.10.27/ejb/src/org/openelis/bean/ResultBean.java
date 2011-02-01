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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.entity.Result;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.RoundingMethod;
import org.openelis.utilcommon.ResultValidator.Type;

@Stateless
@SecurityDomain("openelis")
public class ResultBean implements ResultLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager   manager;

    @EJB
    private DictionaryLocal dictionary;

    private static Integer supplementalTypeId, epaMethodId, qaEventOverrideId;
    private static HashMap<Integer, Type> types;

    @PostConstruct
    public void init() {
        if (types == null) {
            types = new HashMap<Integer, Type>();
            try {
                types.put(dictionary.fetchBySystemName("test_res_type_dictionary").getId(), Type.DICTIONARY);
                types.put(dictionary.fetchBySystemName("test_res_type_numeric").getId(), Type.NUMERIC);
                types.put(dictionary.fetchBySystemName("test_res_type_titer").getId(), Type.TITER);
                types.put(dictionary.fetchBySystemName("test_res_type_date").getId(), Type.DATE);
                types.put(dictionary.fetchBySystemName("test_res_type_date_time").getId(), Type.DATE_TIME);
                types.put(dictionary.fetchBySystemName("test_res_type_time").getId(), Type.TIME);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_lower").getId(), Type.ALPHA_LOWER);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_upper").getId(), Type.ALPHA_UPPER);
                types.put(dictionary.fetchBySystemName("test_res_type_alpha_mixed").getId(), Type.ALPHA_MIXED);
                types.put(dictionary.fetchBySystemName("test_res_type_default").getId(), Type.DEFAULT);

                supplementalTypeId = dictionary.fetchBySystemName("test_analyte_suplmtl").getId();
                qaEventOverrideId = dictionary.fetchBySystemName("qaevent_override").getId();
                epaMethodId = dictionary.fetchBySystemName("round_epa").getId();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchByTestIdNoResults(Integer testId, Integer unitId,
                                       ArrayList<ArrayList<ResultViewDO>> results,
                                       HashMap<Integer, TestResultDO> testResultList,
                                       HashMap<Integer, AnalyteDO> analyteList,
                                       HashMap<Integer, TestAnalyteListItem> testAnalyteList,
                                       ArrayList<ResultValidator> resultValidators) throws Exception {
        List<AnalyteDO> analytes;
        List<TestResultDO> testResults;
        List<TestAnalyteViewDO> testAnalytes;
        int i, j, k, rg;
        Integer rowGroup;
        TestAnalyteViewDO data;
        AnalyteDO analyte;
        TestAnalyteListItem taLI;
        ArrayList<ResultViewDO> ar;
        TestResultDO testResult;
        ArrayList<TestAnalyteViewDO> tmpList;
        boolean suppRow;

        // get test_analytes by test id
        Query query = manager.createNamedQuery("TestAnalyte.FetchByTestId");
        query.setParameter("testId", testId);
        testAnalytes = query.getResultList();

        // get analytes for test
        query = manager.createNamedQuery("Analyte.FetchByTest");
        query.setParameter("testId", testId);
        analytes = query.getResultList();

        // get test_results by test id
        query = manager.createNamedQuery("TestResult.FetchByTestId");
        query.setParameter("testId", testId);
        testResults = query.getResultList();

        // convert the lists to hashmaps
        analyteList.clear();

        for (k = 0; k < analytes.size(); k++ ) {
            analyte = analytes.get(k);
            analyteList.put(analyte.getId(), analyte);
        }
        
        rowGroup = -1;
        tmpList = null;
        for (k = 0; k < testAnalytes.size(); k++ ) {
            data = testAnalytes.get(k);
            if(!rowGroup.equals(data.getRowGroup())){
                rowGroup = data.getRowGroup();
                taLI = new TestAnalyteListItem();
                tmpList = new ArrayList<TestAnalyteViewDO>();

                taLI.testAnalytes = tmpList;
                testAnalyteList.put(rowGroup, taLI);
            }
            
            tmpList.add(data);
        }

        testResultList.clear();

        for (k = 0; k < testResults.size(); k++ ) {
            testResult = testResults.get(k);
            testResultList.put(testResult.getId(), testResult);
        }

        createTestResultHash(testResults, resultValidators);

        // build the grid
        j = -1;
        ar = null;
        results.clear();

        if (testAnalyteList == null || testAnalyteList.size() == 0)
            throw new NotFoundException();

        suppRow = false;
        for (i = 0; i < testAnalytes.size(); i++ ) {
            data = testAnalytes.get(i);

            if ("N".equals(data.getIsColumn()))
                suppRow = false;

            //
            //we are assuming there will be at least 1 non supplemental
            //if there are only supplementals in a row group it will not
            //show a header so the user wont be able to add any analytes
            //
            if ( !suppRow && !DataBaseUtil.isSame(supplementalTypeId, data.getTypeId())) {
                // create a new resultDO
                ResultViewDO resultDO = new ResultViewDO();
                resultDO.setTestAnalyteId(data.getId());
                resultDO.setIsColumn(data.getIsColumn());
                resultDO.setIsReportable(data.getIsReportable());
                resultDO.setAnalyteId(data.getAnalyteId());
                resultDO.setAnalyte(data.getAnalyteName());
                resultDO.setTypeId(data.getTypeId());
                resultDO.setResultGroup(data.getResultGroup());
                
                rg = data.getRowGroup();
                resultDO.setRowGroup(rg);
                
                //we need to set the default
                resultDO.setValue(resultValidators.get(resultDO.getResultGroup() - 1).getDefault(unitId));
                
                if (j != rg) {
                    ar = new ArrayList<ResultViewDO>(1);
                    ar.add(resultDO);
                    results.add(ar);
                    j = rg;
                    continue;
                }
                if ("N".equals(data.getIsColumn())) {
                    ar = new ArrayList<ResultViewDO>(1);
                    ar.add(resultDO);
                    results.add(ar);
                    continue;
                }

                ar.add(resultDO);
            } else
                suppRow = true;
        }
    }

    public void fetchByAnalysisIdForDisplay(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results) throws Exception {
        int i;
        Integer j, rg;
        ResultViewDO data;
        ArrayList<ResultViewDO> ar;
        List<ResultViewDO> list;

        list = null;
        // get results by analysis id
        Query query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        list = query.getResultList();

        // build the grid
        j = -1;
        ar = null;
        results.clear();

        if (list == null || list.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            
            rg = data.getRowGroup();

            if (!DataBaseUtil.isSame(j,rg)) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);  
                if (rg != null)
                    j = rg;
                continue;
            }
            if ("N".equals(data.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);
                continue;
            }

            ar.add(data);
        }
    }

    public void fetchByAnalysisId(Integer analysisId,
                                  ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, TestResultDO> testResultList,
                                  HashMap<Integer, AnalyteDO> analyteList,
                                  HashMap<Integer, TestAnalyteListItem> testAnalyteList,
                                  ArrayList<ResultValidator> resultValidators) throws Exception {

        // build the grid
        int i, j, rg;
        ResultViewDO rdo;
        TestResultDO testResultDO;
        List<AnalyteDO> analytes;
        List<TestResultDO> testResults;
        List<ResultViewDO> rslts;
        List<TestAnalyteViewDO> testAnalytes;
        Integer rowGroup;
        TestAnalyteViewDO data;
        TestAnalyteListItem taLI;
        ArrayList<ResultViewDO> ar;
        ArrayList<TestAnalyteViewDO> tmpList;

        // get analytes by analysis id
        Query query = manager.createNamedQuery("Result.FetchAnalyteByAnalysisId");
        query.setParameter("id", analysisId);
        analytes = query.getResultList();

        // get test analytes by analysis id
        query = manager.createNamedQuery("TestAnalyte.FetchByAnalysisId");
        query.setParameter("analysisId", analysisId);
        testAnalytes = query.getResultList();

        // get results by analysis id
        query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        rslts = query.getResultList();

        // get test_results by test id
        query = manager.createNamedQuery("TestResult.FetchByAnalysisId");
        query.setParameter("analysisId", analysisId);
        testResults = query.getResultList();

        // convert the lists to hashmaps
        analyteList.clear();
        AnalyteDO analyteDO;
        for (int k = 0; k < analytes.size(); k++ ) {
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        rowGroup = -1;
        tmpList = null;
        for (int k = 0; k < testAnalytes.size(); k++ ) {
            data = testAnalytes.get(k);
            if(!rowGroup.equals(data.getRowGroup())){
                rowGroup = data.getRowGroup();
                taLI = new TestAnalyteListItem();
                tmpList = new ArrayList<TestAnalyteViewDO>();

                taLI.testAnalytes = tmpList;
                testAnalyteList.put(rowGroup, taLI);
            }
            
            tmpList.add(data);
        }

        testResultList.clear();
        for (int k = 0; k < testResults.size(); k++ ) {
            testResultDO = testResults.get(k);
            testResultList.put(testResultDO.getId(), testResultDO);
        }

        createTestResultHash(testResults, resultValidators);
        
        j = -1;
        ar = null;
        results.clear();

        if (rslts == null || rslts.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < rslts.size(); i++ ) {
            rdo = rslts.get(i);
            rg = rdo.getRowGroup();

            if (j != rg) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(rdo);
                results.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(rdo.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(rdo);
                results.add(ar);
                continue;
            }

            ar.add(rdo);
        }
    }
    /**
     * Fetches results for analysis that are reportable and do not have sample or analysis level
     * QA event override.
     */
    public ArrayList<ArrayList<ResultViewDO>> fetchReportableByAnalysisId(Integer sampleId, Integer analysisId) throws Exception {
        int i;
        Integer j, rg;
        ResultViewDO data;
        ArrayList<ResultViewDO> ar;
        List<ResultViewDO> list;
        ArrayList<ArrayList<ResultViewDO>> results;
        Query query;

        list = null;
        // get results by analysis id
        query = manager.createNamedQuery("Result.FetchReportableByAnalysisId");
        query.setParameter("sid", sampleId);
        query.setParameter("aid", analysisId);
        query.setParameter("overrideid", qaEventOverrideId);
        list = query.getResultList();

        // build the grid
        j = -1;
        ar = null;        

        if (list == null || list.size() == 0)
            throw new NotFoundException();

        results = new ArrayList<ArrayList<ResultViewDO>>();
        for (i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            
            rg = data.getRowGroup();

            if (!DataBaseUtil.isSame(j,rg)) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);  
                if (rg != null)
                    j = rg;
                continue;
            }
            if ("N".equals(data.getIsColumn())) {
                ar = new ArrayList<ResultViewDO>(1);
                ar.add(data);
                results.add(ar);
                continue;
            }

            ar.add(data);
        }
        
        return results;
    }

    public ResultViewDO add(ResultViewDO data) {
        Result entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Result();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setIsReportable(data.getIsReportable());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ResultViewDO update(ResultViewDO data) {
        Result entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Result.class, data.getId());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
        entity.setIsColumn(data.getIsColumn());
        entity.setSortOrder(data.getSortOrder());
        entity.setIsReportable(data.getIsReportable());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(ResultViewDO data) {
        Result entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Result.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    private void createTestResultHash(List<TestResultDO> testResultList,
                                      ArrayList<ResultValidator> resultValidators) {
        Integer rg;
        String dictEntry;
        ResultValidator rv;
        Type type;
        RoundingMethod method;
        DictionaryDO dict;
        
        
        rg = null;
        rv = null;
        try {
            for (TestResultDO data: testResultList) {
                dictEntry = null;
                method = null;

                if ( !DataBaseUtil.isSame(data.getResultGroup(), rg)) {
                    rv = new ResultValidator();
                    rg = data.getResultGroup();
                    resultValidators.add(rv);
                }
                
                if (DataBaseUtil.isSame(epaMethodId, data.getRoundingMethodId()))
                    method = RoundingMethod.EPA_METHOD;
                
                type = types.get(data.getTypeId());
                if (type == Type.DICTIONARY) {
                    dict = dictionary.fetchById(new Integer(data.getValue()));
                    dictEntry = dict.getEntry();
                }
                rv.addResult(data.getId(), data.getUnitOfMeasureId(), type, method, 
                             data.getSignificantDigits(), data.getValue(), dictEntry);
            }
        } catch (Exception e) {
            resultValidators.clear();
            e.printStackTrace();
        }
    }
}
