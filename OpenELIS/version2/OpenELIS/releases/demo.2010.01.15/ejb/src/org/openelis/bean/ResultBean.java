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
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.entity.Result;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.Type;

@Stateless
@SecurityDomain("openelis")
// @RolesAllowed("LOCKINGtest")
public class ResultBean implements ResultLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager   manager;

    @Resource
    private SessionContext  ctx;

    @EJB
    private DictionaryLocal dictionaryBean;

    private static Integer  typeDictionary, typeRange, typeTiter, typeDate, typeDateTime, typeTime,
                    typeDefault, supplementalTypeId;

    @PostConstruct
    private void init() {
        DictionaryDO dictDO;
        try {
            dictDO = dictionaryBean.fetchBySystemName("test_res_type_dictionary");
            typeDictionary = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_numeric");
            typeRange = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_titer");
            typeTiter = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_date");
            typeDate = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_date_time");
            typeDateTime = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_time");
            typeTime = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_res_type_default");
            typeDefault = dictDO.getId();

            dictDO = dictionaryBean.fetchBySystemName("test_analyte_suplmtl");
            supplementalTypeId = dictDO.getId();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchByTestIdNoResults(Integer testId,
                                       ArrayList<ArrayList<ResultViewDO>> results,
                                       HashMap<Integer, TestResultDO> testResultList,
                                       HashMap<Integer, AnalyteDO> analyteList,
                                       HashMap<Integer, TestAnalyteListItem> testAnalyteList,
                                       ArrayList<ResultValidator> resultValidators) throws Exception {
        List<AnalyteDO> analytes = null;
        List<TestResultDO> testResults = null;
        List<TestAnalyteViewDO> testAnalytes = null;
        int i, j, rg;
        Integer rowGroup;
        TestAnalyteViewDO tado;
        TestAnalyteListItem taLI;
        ArrayList<ResultViewDO> ar;
        ArrayList<TestAnalyteViewDO> tmpList;
        boolean suppRow = false;

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
        AnalyteDO analyteDO;
        for (int k = 0; k < analytes.size(); k++ ) {
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        rowGroup = -1;
        tmpList = null;
        for (int k = 0; k < testAnalytes.size(); k++ ) {
            tado = testAnalytes.get(k);
            if(!rowGroup.equals(tado.getRowGroup())){
                rowGroup = tado.getRowGroup();
                taLI = new TestAnalyteListItem();
                tmpList = new ArrayList<TestAnalyteViewDO>();

                taLI.testAnalytes = tmpList;
                testAnalyteList.put(rowGroup, taLI);
            }
            
            tmpList.add(tado);
        }

        testResultList.clear();
        TestResultDO testResultDO;
        for (int k = 0; k < testResults.size(); k++ ) {
            testResultDO = testResults.get(k);
            testResultList.put(testResultDO.getId(), testResultDO);
        }

        createTestResultHash(testResults, resultValidators);

        // build the grid
        j = -1;
        ar = null;
        results.clear();

        if (testAnalyteList == null || testAnalyteList.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < testAnalytes.size(); i++ ) {
            tado = testAnalytes.get(i);

            if ("N".equals(tado.getIsColumn()))
                suppRow = false;

            //
            //we are assuming there will be at least 1 non supplemental
            //if there are only supplementals in a row group it will not
            //show a header so the user wont be able to add any analytes
            //
            if ( !suppRow && !supplementalTypeId.equals(tado.getTypeId())) {
                // create a new resultDO
                ResultViewDO resultDO = new ResultViewDO();
                resultDO.setTestAnalyteId(tado.getId());
                resultDO.setIsColumn(tado.getIsColumn());
                resultDO.setIsReportable(tado.getIsReportable());
                resultDO.setAnalyteId(tado.getAnalyteId());
                resultDO.setAnalyte(tado.getAnalyteName());
                resultDO.setTypeId(tado.getTypeId());
                resultDO.setResultGroup(tado.getResultGroup());
                
                rg = tado.getRowGroup();
                resultDO.setRowGroup(rg);

                if (j != rg) {
                    ar = new ArrayList<ResultViewDO>(1);
                    ar.add(resultDO);
                    results.add(ar);
                    j = rg;
                    continue;
                }
                if ("N".equals(tado.getIsColumn())) {
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
        int i, j, rg;
        ResultViewDO rdo;
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
            rdo = list.get(i);

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

    public void fetchByAnalysisId(Integer analysisId,
                                  ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, TestResultDO> testResultList,
                                  HashMap<Integer, AnalyteDO> analyteList,
                                  HashMap<Integer, TestAnalyteListItem> testAnalyteList,
                                  ArrayList<ResultValidator> resultValidators) throws Exception {
        List<AnalyteDO> analytes = null;
        List<TestResultDO> testResults = null;
        List<ResultViewDO> rslts = null;
        List<TestAnalyteViewDO> testAnalytes = null;
        Integer rowGroup;
        TestAnalyteViewDO tado;
        TestAnalyteListItem taLI;
        ArrayList<ResultViewDO> ar;
        ArrayList<TestAnalyteViewDO> tmpList;

        // get analytes by analysis id
        Query query = manager.createNamedQuery("Result.AnalyteByAnalysisId");
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
            tado = testAnalytes.get(k);
            if(!rowGroup.equals(tado.getRowGroup())){
                rowGroup = tado.getRowGroup();
                taLI = new TestAnalyteListItem();
                tmpList = new ArrayList<TestAnalyteViewDO>();

                taLI.testAnalytes = tmpList;
                testAnalyteList.put(rowGroup, taLI);
            }
            
            tmpList.add(tado);
        }

        testResultList.clear();
        TestResultDO testResultDO;
        for (int k = 0; k < testResults.size(); k++ ) {
            testResultDO = testResults.get(k);
            testResultList.put(testResultDO.getId(), testResultDO);
        }

        createTestResultHash(testResults, resultValidators);

        // build the grid
        int i, j, rg;
        ResultViewDO rdo;
        
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

    public ResultViewDO add(ResultViewDO data) {
        Result entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Result();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setAnalyteId(data.getAnalyteId());
        entity.setIsColumn(data.getIsColumn());
        entity.setIsReportable(data.getIsReportable());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
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
        entity.setAnalyteId(data.getAnalyteId());
        entity.setIsColumn(data.getIsColumn());
        entity.setIsReportable(data.getIsReportable());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestAnalyteId(data.getTestAnalyteId());
        entity.setTestResultId(data.getTestResultId());
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
        String validRange;
        ResultValidator rv;
        TestResultDO testResult;
        Integer typeId;
        Type type;
        DictionaryDO dictDO;
        
        
        rg = null;
        rv = null;
        type = null;
        validRange = null;
        try {
            for (int i = 0; i < testResultList.size(); i++ ) {
                testResult = testResultList.get(i);

                if ( !testResult.getResultGroup().equals(rg)) {
                    rv = new ResultValidator();
                    rg = testResult.getResultGroup();
                    
                    resultValidators.add(rv);
                }

                // need to figure this out by type id
                typeId = testResult.getTypeId();
                if(typeDictionary.equals(typeId)){
                    type = Type.DICTIONARY;
                    
                    // need to lookup the entry
                    dictDO = dictionaryBean.fetchById(new Integer(testResult.getValue()));
                    validRange = dictDO.getEntry();
                }else if(typeRange.equals(typeId)){
                    type = Type.NUMERIC;
                    validRange = testResult.getValue();
                }else if(typeTiter.equals(typeId)){
                    type = Type.TITER;
                    validRange = testResult.getValue();
                }else if(typeDate.equals(typeId)){
                    type = Type.DATE;
                    validRange = testResult.getValue();
                }else if(typeDateTime.equals(typeId)){                        
                    type = Type.DATE_TIME;
                    validRange = testResult.getValue();
                }else if(typeTime.equals(typeId)){                        
                    type = Type.TIME;
                    validRange = testResult.getValue();
                }
                
                rv.addResult(testResult.getId(), testResult.getUnitOfMeasureId(), type, validRange);
                
            }
        } catch (Exception e) {
            resultValidators.clear();
            e.printStackTrace();
        }
    }
}
