/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.entity.Test;
import org.openelis.entity.TestAnalyte;
import org.openelis.entity.TestPrep;
import org.openelis.entity.TestReflex;
import org.openelis.entity.TestTypeOfSample;
import org.openelis.entity.TestWorksheet;
import org.openelis.entity.TestWorksheetItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestReflexMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.remote.TestRemote;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs( {@EJB(name = "ejb/SystemUser", beanInterface = SystemUserUtilLocal.class),
        @EJB(name = "ejb/Lock", beanInterface = LockLocal.class),})

@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestBean implements TestRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @EJB
    private SystemUserUtilLocal sysUser;

    @Resource
    private SessionContext ctx;

    private LockLocal lockBean;

    private static final TestMetaMap TestMeta = new TestMetaMap();

    @PostConstruct
    private void init() {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
    public TestIdNameMethodIdDO getTestIdNameMethod(Integer testId) {
        Query query = manager.createNamedQuery("Test.TestIdNameMethodId");
        query.setParameter("id", testId);
        TestIdNameMethodIdDO testDO = (TestIdNameMethodIdDO)query.getSingleResult();
        return testDO;
    }
    
    
    public TestIdNameMethodIdDO getTestIdNameMethodAndUnlock(Integer testId,
                                                             String session) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "test");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), testId);
        return getTestIdNameMethod(testId);
    }

    public TestIdNameMethodIdDO getTestIdNameMethodAndLock(Integer testId,
                                                           String session) throws Exception {
        // SecurityInterceptor.applySecurity("test", ModuleFlags.UPDATE);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test");
        lockBean.getLock((Integer)query.getSingleResult(), testId);

        return getTestIdNameMethod(testId);
    }
    
    public Integer updateTest(TestIdNameMethodIdDO testIdNameMethodDO,
                              TestDetailsDO testDetailsDO,
                              List<TestPrepDO> prepTestDOList,
                              List<TestTypeOfSampleDO> typeOfSampleDOList,
                              List<TestReflexDO> testReflexDOList,
                              TestWorksheetDO worksheetDO,
                              List<TestWorksheetItemDO> itemDOList) throws Exception {
        try {
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "test");
            Integer testReferenceId = (Integer)query.getSingleResult();

            if (testIdNameMethodDO.getId() != null) {
                // we need to call lock one more time to make sure their lock
                // didn't expire and someone else grabbed the record
                lockBean.getLock(testReferenceId, testIdNameMethodDO.getId());
            }

            manager.setFlushMode(FlushModeType.COMMIT);
            Test test = null;

            if (testIdNameMethodDO.getId() == null) {
                test = new Test();
            } else {
                test = manager.find(Test.class, testIdNameMethodDO.getId());
            }
            
            List<Exception> exceptionList = new ArrayList<Exception>();                        
            
            validateTest(exceptionList,testIdNameMethodDO,testDetailsDO);
            if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
            }

            test.setName(testIdNameMethodDO.getName());
            test.setMethodId(testIdNameMethodDO.getMethodId());
            
            if(testDetailsDO!=null){                                      
            test.setActiveBegin(testDetailsDO.getActiveBegin());
            test.setActiveEnd(testDetailsDO.getActiveEnd());
            test.setDescription(testDetailsDO.getDescription());
            test.setIsActive(testDetailsDO.getIsActive());
            test.setIsReportable(testDetailsDO.getIsReportable());
            test.setLabelId(testDetailsDO.getLabelId());
            test.setLabelQty(testDetailsDO.getLabelQty());
            test.setReportingDescription(testDetailsDO.getReportingDescription());
            test.setRevisionMethodId(testDetailsDO.getRevisionMethodId());
            test.setScriptletId(testDetailsDO.getScriptletId());
            test.setSectionId(testDetailsDO.getSectionId());
            test.setTestFormatId(testDetailsDO.getTestFormatId());
            test.setTestTrailerId(testDetailsDO.getTestTrailerId());
            test.setTimeHolding(testDetailsDO.getTimeHolding());
            test.setTimeTaAverage(testDetailsDO.getTimeTaAverage());
            test.setTimeTaMax(testDetailsDO.getTimeTaMax());
            test.setTimeTaWarning(testDetailsDO.getTimeTaWarning());
            test.setTimeTransit(testDetailsDO.getTimeTransit());
           }

            if (test.getId() == null) {
                manager.persist(test);
            }
            
             if(prepTestDOList!=null){      
                     exceptionList = new ArrayList<Exception>();
                     validateTestPrep(exceptionList,prepTestDOList);
                     if(exceptionList.size() > 0){
                         throw (RPCException)exceptionList.get(0);
                     }
                     
                     for(int i = 0; i < prepTestDOList.size() ; i++){
                         TestPrepDO testPrepDO = prepTestDOList.get(i);
                         TestPrep testPrep = null;
                         if (testPrepDO.getId() == null) {
                             testPrep = new TestPrep();
                         } else {
                             testPrep = manager.find(TestPrep.class, testPrepDO.getId());                             
                         } 
                         if(testPrepDO.getDelete() && testPrepDO.getId() != null){                           
                             manager.remove(testPrep);                                                     
                         }else{                           
                          testPrep.setIsOptional(testPrepDO.getIsOptional());
                          testPrep.setPrepTestId(testPrepDO.getPrepTestId());
                          testPrep.setTestId(test.getId());
                          
                          if(testPrep.getId()==null){
                              manager.persist(testPrep);
                          }
                         } 
                     }
                 }
                                
                  if(typeOfSampleDOList!=null){
                      exceptionList = new ArrayList<Exception>();
                      validateTypeOfSample(exceptionList,typeOfSampleDOList);
                      if(exceptionList.size() > 0){
                          throw (RPCException)exceptionList.get(0);
                      }
                      
                     for(int i = 0; i < typeOfSampleDOList.size(); i++){
                         TestTypeOfSampleDO typeOfSampleDO = typeOfSampleDOList.get(i);
                        
                         if(typeOfSampleDO !=null){
                             TestTypeOfSample typeOfSample = null;
                              if(typeOfSampleDO.getId() == null){
                                  typeOfSample = new TestTypeOfSample();
                              } else {
                                  typeOfSample = manager.find(TestTypeOfSample.class, typeOfSampleDO.getId());
                              }
                              if(typeOfSampleDO.getDelete() && typeOfSampleDO.getId() != null){                           
                                  manager.remove(typeOfSample);                                                     
                              }else{                                
                                typeOfSample.setTestId(test.getId());
                                typeOfSample.setTypeOfSampleId(typeOfSampleDO.getTypeOfSampleId());
                                typeOfSample.setUnitOfMeasureId(typeOfSampleDO.getUnitOfMeasureId());
                              
                              if(typeOfSample.getId() == null){
                                  manager.persist(typeOfSample);
                              }
                            } 
                         }
 
                     }
                  }
                  
                if(testReflexDOList!=null){
                    exceptionList = new ArrayList<Exception>();
                    validateTestReflex(exceptionList,testReflexDOList);
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(int iter = 0; iter < testReflexDOList.size(); iter++){
                       TestReflexDO refDO = testReflexDOList.get(iter);
                       TestReflex testReflex = null;
                       if(refDO.getId() == null){
                           testReflex = new TestReflex();
                       } else {
                           testReflex = manager.find(TestReflex.class, refDO.getId());
                       }
                       if(refDO.getDelete() && refDO.getId() != null){                           
                           manager.remove(testReflex);                                                     
                       }else{                                
                           testReflex.setTestId(test.getId());
                           testReflex.setAddTestId(refDO.getAddTestId());
                           testReflex.setTestAnalyteId(refDO.getTestAnalyteId());
                           testReflex.setFlagsId(refDO.getFlagsId());
                           testReflex.setTestResultId(refDO.getTestResultId());                           
                       
                       if(testReflex.getId() == null){
                           manager.persist(testReflex);
                       }
                    }
                  }
                }   
                
                TestWorksheet testWorksheet = null;
                if(worksheetDO!=null){
                    exceptionList = new ArrayList<Exception>();
                    validateTestWorksheet(exceptionList,worksheetDO);
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                                       
                    if(worksheetDO.getId()!=null){
                        testWorksheet = manager.find(TestWorksheet.class, worksheetDO.getId());
                    }else{
                        testWorksheet = new TestWorksheet();
                    }
                    
                    testWorksheet.setTestId(worksheetDO.getTestId());
                    testWorksheet.setBatchCapacity(worksheetDO.getBatchCapacity());
                    testWorksheet.setNumberFormatId(worksheetDO.getNumberFormatId());
                    testWorksheet.setTotalCapacity(worksheetDO.getTotalCapacity());       
                    testWorksheet.setScriptletId(worksheetDO.getScriptletId());          
                    
                    if(testWorksheet.getId() == null){
                        manager.persist(testWorksheet);
                    }                                                          
                }
                
                if(itemDOList!=null){
                    exceptionList = new ArrayList<Exception>();
                    validateTestWorksheetItems(exceptionList,itemDOList);
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(int iter = 0; iter < itemDOList.size(); iter++){
                        TestWorksheetItemDO itemDO = itemDOList.get(iter);
                        TestWorksheetItem testWorksheetItem = null;
                        if(itemDO.getId() == null){
                            testWorksheetItem = new TestWorksheetItem();
                        } else {
                            testWorksheetItem = manager.find(TestWorksheetItem.class, itemDO.getId());
                        }
                        if(itemDO.getDelete() && itemDO.getId() != null){                           
                            manager.remove(testWorksheetItem);                                                     
                        }else{                                
                            testWorksheetItem.setPosition(itemDO.getPosition());
                            testWorksheetItem.setQcName(itemDO.getQcName());
                            testWorksheetItem.setTestWorksheetId(testWorksheet.getId());
                            testWorksheetItem.setTypeId(itemDO.getTypeId());                                                  
                        
                        if(testWorksheetItem.getId() == null){
                            manager.persist(testWorksheetItem);
                        }
                     }
                   }
                    
                }
                                                       
            lockBean.giveUpLock(testReferenceId, test.getId());
            return test.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
        
    
    public TestDetailsDO getTestDetails(Integer testId) {
        Query query = manager.createNamedQuery("Test.TestDetails");
        query.setParameter("id", testId);
        TestDetailsDO testDetailsDO = (TestDetailsDO)query.getSingleResult();
        return testDetailsDO;
    }    
    
    public List<TestPrepDO> getTestPreps(Integer testId) {
        Query query = manager.createNamedQuery("TestPrep.TestPrep");
        query.setParameter("id", testId);
        List<TestPrepDO> testPrepDOList = (List<TestPrepDO>)query.getResultList();
        return testPrepDOList;
    }

    public List<TestReflexDO> getTestReflexes(Integer testId) {
        Query query = manager.createNamedQuery("TestReflex.TestReflexDOList");
        query.setParameter("testId", testId);
        List<TestReflexDO> testRefDOList = query.getResultList();         
        return testRefDOList;
    }
    
    public List getTestResultsForTestAnalyte(Integer testId,Integer analyteId){
        Query query = manager.createNamedQuery("TestResult.IdValueByTestAnalyteId");
        query.setParameter("testId", testId);
        query.setParameter("analyteId", analyteId);
        List<IdNameDO> idValues = query.getResultList();
        return idValues;
    }
    
    public HashMap<Integer,List<IdNameDO>> getAnalyteResultsMap(Integer testId){
        Query query = manager.createNamedQuery("TestAnalyte.TestAnalyteByTestId");
        query.setParameter("testId", testId);
        List<TestAnalyte> analyteList = query.getResultList();
        HashMap<Integer,List<IdNameDO>> listMap = new HashMap<Integer,List<IdNameDO>>();
          for(int iter = 0; iter < analyteList.size();iter++){
              TestAnalyte ta = analyteList.get(iter);
              Integer id  = ta.getId();
              listMap.put(id,getTestResultsForTestAnalyte(testId,id));
          }
         return listMap;
    }
    
    public List<IdNameDO> getTestResultsforTest(Integer testId){
        Query query = manager.createNamedQuery("TestResult.IdValueByTestId");
        query.setParameter("testId", testId);
        List<IdNameDO> resultsList = query.getResultList();          
        return resultsList;
    }

    public List<TestTypeOfSampleDO> getTestTypeOfSamples(Integer testId) {
        Query query = manager.createNamedQuery("TestTypeOfSample.TestTypeOfSample");
        query.setParameter("id", testId);
        List<TestTypeOfSampleDO> testTypeOfSampleDOList = (List<TestTypeOfSampleDO>)query.getResultList();
        return testTypeOfSampleDOList;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(TestMeta);

        /*qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + TestMeta.getId()
                     + ", "
                     + TestMeta.getName()                     
                     + ") ");*/
        qb.setSelect("distinct new org.openelis.domain.IdLastNameFirstNameDO("
                     +TestMeta.getId()+", "+TestMeta.getName()+", "
                     +TestMeta.getMethod().getName() + ") ");       
        
        qb.addWhere(fields);

        qb.setOrderBy(TestMeta.getName()+", "+TestMeta.getMethod().getName());

        sb.append(qb.getEJBQL());                

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }
    
    public List getMethodDropDownValues() {
        Query query = manager.createNamedQuery("Method.MethodIdName");
        List methodList = query.getResultList();
        return methodList;
    }

    public List getLabelDropDownValues() {
        Query query = manager.createNamedQuery("Label.LabelIdName");
        List labelList = query.getResultList();
        return labelList;
    }

    public List getTestTrailerDropDownValues() {
        Query query = manager.createNamedQuery("TestTrailer.TestTrailerIdName");
        List testTrailerList = query.getResultList();
        return testTrailerList;
    }

    public List getScriptletDropDownValues() {
        Query query = manager.createNamedQuery("Scriptlet.Scriptlet");
        List scriptletList = query.getResultList();
        return scriptletList;
    }

    public List getPrepTestDropDownValues() {        
        Query query = manager.createNamedQuery("Test.Names");
        List preptestList = query.getResultList();
        return preptestList;
    }    
    
    public List getTestAnalyteDropDownValues(Integer testId) {        
        Query query = manager.createNamedQuery("TestAnalyte.IdName");
        query.setParameter("testId",testId);
        List testAnalytesList = query.getResultList();
        return testAnalytesList;
    }
    

    public List validateForAdd(TestIdNameMethodIdDO testIdNameMethodDO,
                               TestDetailsDO testDetailsDO,
                               List<TestPrepDO> prepTestDOList,
                               List<TestTypeOfSampleDO> typeOfSampleDOList,
                               List<TestReflexDO> testReflexDOList,
                               TestWorksheetDO worksheetDO,
                               List<TestWorksheetItemDO> itemDOList) {
     List<Exception> exceptionList = new ArrayList<Exception>();
     validateTest(exceptionList, testIdNameMethodDO, testDetailsDO);
     if(typeOfSampleDOList!=null)
      validateTypeOfSample(exceptionList,typeOfSampleDOList);
     if(prepTestDOList!=null)
      validateTestPrep(exceptionList,prepTestDOList);
     if(testReflexDOList!=null)
      validateTestReflex(exceptionList,testReflexDOList);  
     if(worksheetDO!=null)
         validateTestWorksheet(exceptionList,worksheetDO);
     if(itemDOList!=null)
         validateTestWorksheetItems(exceptionList,itemDOList);
     return exceptionList;
    }

    public List validateForUpdate(TestIdNameMethodIdDO testIdNameMethodDO,
                                  TestDetailsDO testDetailsDO,
                                  List<TestPrepDO> prepTestDOList,
                                  List<TestTypeOfSampleDO> typeOfSampleDOList,
                                  List<TestReflexDO> testReflexDOList,
                                  TestWorksheetDO worksheetDO,
                                  List<TestWorksheetItemDO> itemDOList) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateTest(exceptionList, testIdNameMethodDO,testDetailsDO);
        if(typeOfSampleDOList!=null)
         validateTypeOfSample(exceptionList,typeOfSampleDOList);
        if(prepTestDOList!=null)
         validateTestPrep(exceptionList,prepTestDOList);
        if(testReflexDOList!=null)
            validateTestReflex(exceptionList,testReflexDOList); 
        if(worksheetDO!=null)
            validateTestWorksheet(exceptionList,worksheetDO);
        if(itemDOList!=null)
            validateTestWorksheetItems(exceptionList,itemDOList);
        return exceptionList;
    }    
    

    private void validateTest(List<Exception> exceptionList,
                                        TestIdNameMethodIdDO testIdNameMethodIdDO,
                                        TestDetailsDO testDetailsDO) {
        boolean checkDuplicate = true;
        if (testIdNameMethodIdDO.getName() == null || "".equals(testIdNameMethodIdDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getName()));
            checkDuplicate = false;
        }

        if (testIdNameMethodIdDO.getMethodId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getMethodId()));
            checkDuplicate = false;
        }
        
        if(testDetailsDO !=null){ 
            if (testDetailsDO.getDescription() == null || "".equals(testDetailsDO.getDescription())) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          "details:" + TestMeta.getDescription()));
                checkDuplicate = false;
            }

            if (testDetailsDO.getActiveBegin() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          "details:" + TestMeta.getActiveBegin()));
                checkDuplicate = false;
            }

            if (testDetailsDO.getActiveEnd() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          "details:" + TestMeta.getActiveEnd()));
                checkDuplicate = false;
            }
            
          if(checkDuplicate){
             Query query = manager.createNamedQuery("Test.TestByName");
             query.setParameter("name", testIdNameMethodIdDO.getName());
             List<Test> list = query.getResultList();
             for(int iter = 0; iter < list.size(); iter++){
                 boolean overlap = false;
                 Test test = (Test)list.get(iter);
                 if(!test.getId().equals(testIdNameMethodIdDO.getId())){
                  if(test.getMethodId().equals(testIdNameMethodIdDO.getMethodId())){                  
                      if(test.getIsActive().equals(testDetailsDO.getIsActive())){
                          if("Y".equals(testDetailsDO.getIsActive())){
                              exceptionList.add(new FormErrorException("testActiveException"));                                   
                            }else{
                             // exceptionList.add(new FormErrorException("testInactiveTimeOverlap"));  
                            }
                            break;  
                     }
                      if(test.getActiveBegin().before(testDetailsDO.getActiveEnd())&&
                                      (test.getActiveEnd().after(testDetailsDO.getActiveBegin()))){
                          overlap = true;  
                       }else if(test.getActiveBegin().equals(testDetailsDO.getActiveEnd())||
                                   (test.getActiveEnd().equals(testDetailsDO.getActiveBegin()))){
                                overlap = true;                  
                       }
                      
                     if(overlap){
                         exceptionList.add(new FormErrorException("testTimeOverlapException"));
                     } 
                 }
               } 
             }
          }
        }
        
        
    }

    
    private void validateTypeOfSample(List<Exception> exceptionList,List<TestTypeOfSampleDO> typeOfSampleDOList){        
            for(int i = 0; i < typeOfSampleDOList.size(); i++){
                TestTypeOfSampleDO typeDO = typeOfSampleDOList.get(i);
                if(typeDO.getTypeOfSampleId()==null){
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                         TestTypeOfSampleMetaMap.getTableName()+":"+TestMeta.getTestTypeOfSample().getTypeOfSampleId()));
                }            
        }
    }
    
    private void validateTestPrep(List<Exception> exceptionList,List<TestPrepDO> testPrepDOList){    
            List<Integer> testPrepIdList = new ArrayList<Integer>();
            for(int i = 0; i < testPrepDOList.size(); i++){
                TestPrepDO prepDO = testPrepDOList.get(i);
                if(prepDO.getPrepTestId()==null){
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                          TestPrepMetaMap.getTableName()+":"+TestMeta.getTestPrep().getPrepTestId()));
                }else {
                  if(!testPrepIdList.contains(prepDO.getPrepTestId())){                
                      testPrepIdList.add(prepDO.getPrepTestId());
                   }else{        
                   exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                          TestPrepMetaMap.getTableName()+":"+TestMeta.getTestPrep().getPrepTestId()));
                  }                                              
              }   
            }    
                
    }
    
    private void validateTestReflex(List<Exception> exceptionList,List<TestReflexDO> testReflexDOList){
        List<List<Integer>> idsList = new ArrayList<List<Integer>>();
        for(int i = 0; i < testReflexDOList.size(); i++){
            TestReflexDO refDO = testReflexDOList.get(i);
            boolean checkForDuplicate = false;
            List<Integer> ids = new ArrayList<Integer>();
            if(refDO.getAddTestId()==null){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                      TestReflexMetaMap.getTableName()+":"+TestMeta.getTestReflex().getAddTestId()));                    
            }else {
                ids.add(refDO.getAddTestId());
                checkForDuplicate = true;
            }
            
            if(refDO.getTestAnalyteId()==null){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                      TestReflexMetaMap.getTableName()+":"+TestMeta.getTestReflex().getTestAnalyteId()));
                checkForDuplicate = false;  
            }else{
                ids.add(refDO.getTestAnalyteId());
                checkForDuplicate = true;
            }
            
            if(refDO.getTestResultId()==null){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                      TestReflexMetaMap.getTableName()+":"+TestMeta.getTestReflex().getTestResultId()));
                checkForDuplicate = false;
            }else{
                ids.add(refDO.getTestResultId());
                checkForDuplicate = true;
            }
           
            if(checkForDuplicate){
                if(!idsList.contains(ids)){                
                    idsList.add(ids);
                 }else{
                     exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                      TestReflexMetaMap.getTableName()+":"+TestMeta.getTestReflex().getAddTestId())); 
                 }
            }
            
        }       
      }
    
    public void validateTestWorksheet(List<Exception> exceptionList,TestWorksheetDO worksheetDO){
        boolean checkForMultiple = true;
        if(worksheetDO.getBatchCapacity()==null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
               "worksheet:" + TestMeta.getTestWorksheet().getBatchCapacity()));
            checkForMultiple = false;
        }
        if(worksheetDO.getTotalCapacity()==null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
               "worksheet:" + TestMeta.getTestWorksheet().getTotalCapacity()));
            checkForMultiple = false;
        }
        
        if(worksheetDO.getBatchCapacity()!=null && worksheetDO.getBatchCapacity() <= 0){
            exceptionList.add(new FieldErrorException("batchCapacityMoreThanZeroException",
               "worksheet:" + TestMeta.getTestWorksheet().getBatchCapacity()));
            checkForMultiple = false;
        }
        
        if(worksheetDO.getTotalCapacity()!=null && worksheetDO.getTotalCapacity() <= 0){
            exceptionList.add(new FieldErrorException("totalCapacityMoreThanZeroException",
               "worksheet:" + TestMeta.getTestWorksheet().getTotalCapacity()));
            checkForMultiple = false;
        }
        
        if(worksheetDO.getNumberFormatId()==null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
               "worksheet:" + TestMeta.getTestWorksheet().getNumberFormatId()));
        }
        
        if(checkForMultiple){
            if((worksheetDO.getTotalCapacity()%worksheetDO.getBatchCapacity())!=0){
                exceptionList.add(new FieldErrorException("totalCapacityMultipleException",
                 "worksheet:" + TestMeta.getTestWorksheet().getTotalCapacity()));
            }
        }
        
        
    }
    
    public void validateTestWorksheetItems(List<Exception> exceptionList,
                                           List<TestWorksheetItemDO> itemDOList) {
        for(int i = 0; i < itemDOList.size(); i++){
            TestWorksheetItemDO itemDO = itemDOList.get(i);
            boolean checkPosition = true; 
            if(itemDO.getQcName()==null || ("").equals(itemDO.getQcName())){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                TestWorksheetItemMetaMap.getTableName()+":"+TestMeta.getTestWorksheet()
                .getTestWorksheetItem().getQcName()));
            }
            if(itemDO.getTypeId()==null){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                 TestWorksheetItemMetaMap.getTableName()+":"+TestMeta.getTestWorksheet()
                 .getTestWorksheetItem().getTypeId()));
                checkPosition = false;    
            }
            
            
            if(itemDO.getPosition()!=null && itemDO.getPosition()<= 0){
                exceptionList.add(new TableFieldErrorException("posMoreThanZeroException", i,
                 TestWorksheetItemMetaMap.getTableName()+":"+TestMeta.getTestWorksheet()
                 .getTestWorksheetItem().getPosition()));  
                checkPosition = false;
            }
            
            if(checkPosition){
                Query query = manager.createNamedQuery("Dictionary.SystemNameById");
                query.setParameter("id", itemDO.getTypeId());
                String sysName = (String)query.getSingleResult();
             if(itemDO.getPosition()==null){
                    if("pos_duplicate".equals(sysName)||"pos_fixed".equals(sysName)){
                        exceptionList.add(new TableFieldErrorException("fixedDuplicatePosException", i,
                         TestWorksheetItemMetaMap.getTableName()+":"+TestMeta.getTestWorksheet()
                         .getTestWorksheetItem().getPosition()));
                    }
                }   
             if(itemDO.getPosition()!=null && itemDO.getPosition() == 1){               
               if("pos_duplicate".equals(sysName)){
                   exceptionList.add(new TableFieldErrorException("posOneDuplicateException", i,
                    TestWorksheetItemMetaMap.getTableName()+":"+TestMeta.getTestWorksheet()
                    .getTestWorksheetItem().getTypeId()));
               }                              
            }
             
          } 
        }
    }

    public TestWorksheetDO getTestWorksheet(Integer testId) {
        Query query = manager.createNamedQuery("TestWorksheet.TestWorksheetDOByTestId");
        query.setParameter("testId", testId);
        TestWorksheetDO worksheetDO = null;
        try{
            worksheetDO = (TestWorksheetDO)query.getSingleResult();
        }catch(NoResultException ex){
            ex.printStackTrace();
        }
        return worksheetDO;         
    }

    public List<TestWorksheetItemDO> getTestWorksheetItems(Integer testId) {
        Query query = manager.createNamedQuery("TestWorksheet.TestWorksheetItemsByTestId");
        query.setParameter("testId", testId);
        List<TestWorksheetItemDO> list = query.getResultList();
        return list;
    }

    
}
