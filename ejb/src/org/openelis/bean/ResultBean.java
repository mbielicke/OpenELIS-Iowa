/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ResultLocal;
import org.openelis.utilcommon.Result;
import org.openelis.utilcommon.ResultRangeDate;
import org.openelis.utilcommon.ResultRangeDateTime;
import org.openelis.utilcommon.ResultRangeDictionary;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTime;
import org.openelis.utilcommon.ResultRangeTiter;
import org.openelis.utilcommon.ResultValidator;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class ResultBean implements ResultLocal {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    @EJB
    private DictionaryLocal dictionaryBean;

    private static Integer typeDictionary, typeRange, typeTiter,
                           typeDate, typeDateTime, typeTime, typeDefault,
                           supplementalTypeId;
    
    @PostConstruct
    private void init()
    {
        DictionaryDO dictDO;
        try{
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
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void fetchByTestIdNoResults(Integer testId, ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, AnalyteDO> analyteList, HashMap<Integer, TestAnalyteViewDO> testAnalyteList, 
                                  ResultValidator resultValidator) throws Exception {
        List<TestAnalyteViewDO> testAnalytes = null;
        List<AnalyteDO> analytes = null;
        List<TestResultDO> testResults = null;
        int i, j, rg;
        TestAnalyteViewDO ado;
        ArrayList<ResultViewDO> ar;
        boolean suppRow = false;
        
        //get test_analytes by test id
        Query query = manager.createNamedQuery("TestAnalyte.FetchByTestId");
        query.setParameter("testId", testId);
        testAnalytes = query.getResultList();
        
        //get analytes for test
        query = manager.createNamedQuery("Analyte.FetchByTest");
        query.setParameter("testId", testId);
        analytes = query.getResultList();
        
        //get test_results by test id
        query = manager.createNamedQuery("TestResult.FetchByTestId");
        query.setParameter("testId", testId);
        testResults = query.getResultList();
        
        //convert the lists to hashmaps
        analyteList.clear();
        AnalyteDO analyteDO;
        for(int k=0; k<analytes.size(); k++){
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        testAnalyteList.clear();
        TestAnalyteViewDO testAnalyteDO;
        for(int k=0; k<testAnalytes.size(); k++){
            testAnalyteDO = testAnalytes.get(k);
            testAnalyteList.put(testAnalyteDO.getId(), testAnalyteDO);
        }
        
        resultValidator.clear();
        createTestResultHash(testResults, resultValidator);
        
        //build the grid
        j = -1;
        ar = null;
        results.clear();

        if (testAnalytes == null || testAnalytes.size() == 0)
            throw new NotFoundException();

        for (i = 0; i < testAnalytes.size(); i++ ) {
            ado = testAnalytes.get(i);
            
            if ("N".equals(ado.getIsColumn()))
                suppRow = false;

            if(!suppRow && !supplementalTypeId.equals(ado.getTypeId())){
                //create a new resultDO
                ResultViewDO resultDO = new ResultViewDO();
                resultDO.setTestAnalyteId(ado.getId());
                resultDO.setAnalyte(ado.getAnalyteName());
                resultDO.setIsColumn(ado.getIsColumn());
                resultDO.setSortOrder(ado.getSortOrder());
                resultDO.setIsReportable(ado.getIsReportable());
                resultDO.setTypeId(ado.getTypeId());
                resultDO.setResultGroup(ado.getResultGroup());
                
                rg = ado.getRowGroup();
                resultDO.setRowGroup(rg);
    
                if (j != rg) {
                    ar = new ArrayList<ResultViewDO>(1);
                    ar.add(resultDO);
                    results.add(ar);
                    j = rg;
                    continue;
                }
                if ("N".equals(ado.getIsColumn())) {
                    ar = new ArrayList<ResultViewDO>(1);
                    ar.add(resultDO);
                    results.add(ar);
                    continue;
                }
    
                ar.add(resultDO);
            }else
                suppRow = true;
        }
    }
    
    public void fetchByAnalysisIdForDisplay(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results) throws Exception {
        List<ResultViewDO> rslts;
        
        rslts = null;
        //get results by analysis id
        Query query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        rslts = query.getResultList();
        
        //build the grid
        int i, j, rg;
        ResultViewDO rdo;
        ArrayList<ResultViewDO> ar;

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
    
    public void fetchByAnalysisId(Integer analysisId, ArrayList<ArrayList<ResultViewDO>> results,
                                  HashMap<Integer, AnalyteDO> analyteList, HashMap<Integer, 
                                  TestAnalyteViewDO> testAnalyteList, ResultValidator resultValidator) throws Exception {
        List<TestAnalyteViewDO> testAnalytes = null;
        List<AnalyteDO> analytes = null;
        List<TestResultDO> testResults = null;
        List<ResultViewDO> rslts = null;
        
        //get analytes by analysis id
        Query query = manager.createNamedQuery("Result.AnalyteByAnalysisId");
        query.setParameter("id", analysisId);
        analytes = query.getResultList();
        
        //get test analytes by analysis id
        query = manager.createNamedQuery("TestAnalyte.FetchByAnalysisId");
        query.setParameter("analysisId", analysisId);
        analytes = query.getResultList();
        
        //get results by analysis id
        query = manager.createNamedQuery("Result.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        rslts = query.getResultList();
        
      //get test_results by test id
        query = manager.createNamedQuery("TestResult.FetchByAnalysisId");
        query.setParameter("analysisId", analysisId);
        testResults = query.getResultList();
        
        //convert the lists to hashmaps
        analyteList.clear();
        AnalyteDO analyteDO;
        for(int k=0; k<analytes.size(); k++){
            analyteDO = analytes.get(k);
            analyteList.put(analyteDO.getId(), analyteDO);
        }
        
        testAnalyteList.clear();
        TestAnalyteViewDO testAnalyteDO;
        for(int k=0; k<testAnalytes.size(); k++){
            testAnalyteDO = testAnalytes.get(k);
            testAnalyteList.put(testAnalyteDO.getId(), testAnalyteDO);
        }
        
        resultValidator.clear();
        createTestResultHash(testResults, resultValidator);
        
        //build the grid
        int i, j, rg;
        ResultViewDO rdo;
        ArrayList<ResultViewDO> ar;

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
    
    public TestAnalyteViewDO add(TestAnalyteViewDO itemDO) {
        return null;
        // TODO Auto-generated method stub
        
    }

    public TestAnalyteViewDO update(TestAnalyteViewDO itemDO) {
        return null;
        // TODO Auto-generated method stub
        
    }
    
    public void delete(TestAnalyteViewDO itemDO) {
        // TODO Auto-generated method stub
        
    }
    
    private void createTestResultHash(List<TestResultDO> testResultList, ResultValidator resultValidator){
        ArrayList<Result> results;
        Integer rg;
        TestResultDO testResult;
        Integer typeId;
        DictionaryDO dictDO;
        ResultRangeDictionary dict;
        ResultRangeDate date;
        ResultRangeDateTime dateTime;
        ResultRangeNumeric numericRange;
        ResultRangeTime time;
        ResultRangeTiter titer;
        
        dict = null;
        rg = null;
        results = null;
        
        try{
            for(int i=0; i<testResultList.size(); i++){
                testResult = testResultList.get(i);
                
                if(!testResult.getResultGroup().equals(rg)){
                    if(rg != null)
                        resultValidator.addResultGroup(rg, results);
                    
                    results = new ArrayList<Result>();
                    dict = null;
                    rg = testResult.getResultGroup();
                }
                
                //need to figure this out by type id
                typeId = testResult.getTypeId();
                if(typeId.equals(typeDictionary)){
                    if(dict == null){
                        dict = new ResultRangeDictionary();
                        results.add(dict);
                    }
                    
                    //need to lookup the entry
                    dictDO = dictionaryBean.fetchById(new Integer(testResult.getValue()));
                    
                    dict.addEntry(dictDO.getId(), dictDO.getEntry());
                }else if(typeId.equals(typeRange)){
                    numericRange = new ResultRangeNumeric();
                    numericRange.setRange(testResult.getValue());
                    results.add(numericRange);
                    
                }else if(typeId.equals(typeTiter)){
                    titer = new ResultRangeTiter();
                    titer.setRange(testResult.getValue());
                    results.add(titer);
                    
                }else if(typeId.equals(typeDate)){
                    date = new ResultRangeDate();
                    //date.validate(testResult.getValue());
                    results.add(date);
                    
                }else if(typeId.equals(typeDateTime)){
                    dateTime = new ResultRangeDateTime();
                    //dateTime.setValue(testResult.getValue());
                    results.add(dateTime);
                    
                }else if(typeId.equals(typeTime)){
                    time  = new ResultRangeTime();
                    //time.setValue(testResult.getValue());
                    results.add(time);
                    
                }else if(typeId.equals(typeDefault)){
                    //do nothing for now
                }
            }
        }catch(Exception e){
            resultValidator.clear();
            e.printStackTrace();
        }
    }
}
