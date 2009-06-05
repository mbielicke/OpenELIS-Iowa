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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetAnalyteDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.entity.Test;
import org.openelis.entity.TestAnalyte;
import org.openelis.entity.TestPrep;
import org.openelis.entity.TestReflex;
import org.openelis.entity.TestResult;
import org.openelis.entity.TestSection;
import org.openelis.entity.TestTypeOfSample;
import org.openelis.entity.TestWorksheet;
import org.openelis.entity.TestWorksheetAnalyte;
import org.openelis.entity.TestWorksheetItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestReflexMetaMap;
import org.openelis.metamap.TestResultMetaMap;
import org.openelis.metamap.TestSectionMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.metamap.TestWorksheetAnalyteMetaMap;
import org.openelis.metamap.TestWorksheetItemMetaMap;
import org.openelis.remote.TestRemote;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.SecurityInterceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    
    public TestDO getTestAndUnlock(Integer testId,String session) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "test");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), testId);
        return getTest(testId);
    }

    @RolesAllowed("test-update")
    public TestDO getTestAndLock(Integer testId,String session) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "test", ModuleFlags.UPDATE);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test");
        lockBean.getLock((Integer)query.getSingleResult(), testId);

        return getTest(testId);
    }
        
    public Integer updateTest(TestDO testDO,
                              List<TestPrepDO> prepTestDOList,
                              List<TestTypeOfSampleDO> typeOfSampleDOList,
                              List<TestReflexDO> testReflexDOList,
                              TestWorksheetDO worksheetDO,
                              List<TestWorksheetItemDO> itemDOList,
                              List<TestWorksheetAnalyteDO> twsaDOList,
                              List<TestAnalyteDO> analyteDOList,
                              List<TestSectionDO> sectionDOList,
                              List<TestResultDO> resultDOList) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "test", ModuleFlags.UPDATE);
        TestWorksheet testWorksheet = null;
        List<TestAnalyteDO> laterProcAnaList = new ArrayList<TestAnalyteDO>(); 
        List<TestResultDO> laterProcResList = new ArrayList<TestResultDO>();
        HashMap<Integer, Integer> tempRealResIdMap = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> tempRealAnaIdMap = new HashMap<Integer, Integer>();
        
        boolean found = false;
        
        try {
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "test");
            Integer testReferenceId = (Integer)query.getSingleResult();

            if (testDO.getId() != null) {
                // we need to call lock one more time to make sure their lock
                // didn't expire and someone else grabbed the record
                try {
                    lockBean.validateLock(testReferenceId,testDO.getId());
                  } catch(Exception ex) {
                     throw ex;
                  }      
                lockBean.getLock(testReferenceId, testDO.getId());
            }

            manager.setFlushMode(FlushModeType.COMMIT);
            Test test = null;

            if (testDO.getId() == null) {
                test = new Test();
            } else {
                test = manager.find(Test.class, testDO.getId());
            }
            
            List<Exception> exceptionList = new ArrayList<Exception>();                        
            
            validateTest(exceptionList,testDO);
            if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
            }

            test.setName(testDO.getName());
            test.setMethodId(testDO.getMethodId());                                         
            test.setActiveBegin(testDO.getActiveBegin());
            test.setActiveEnd(testDO.getActiveEnd());
            test.setDescription(testDO.getDescription());
            test.setIsActive(testDO.getIsActive());
            test.setIsReportable(testDO.getIsReportable());
            test.setLabelId(testDO.getLabelId());
            test.setLabelQty(testDO.getLabelQty());
            test.setReportingDescription(testDO.getReportingDescription());
            test.setRevisionMethodId(testDO.getRevisionMethodId());
            test.setScriptletId(testDO.getScriptletId());            
            test.setTestFormatId(testDO.getTestFormatId());
            test.setTestTrailerId(testDO.getTestTrailerId());
            test.setTimeHolding(testDO.getTimeHolding());
            test.setTimeTaAverage(testDO.getTimeTaAverage());
            test.setTimeTaMax(testDO.getTimeTaMax());
            test.setTimeTaWarning(testDO.getTimeTaWarning());
            test.setTimeTransit(testDO.getTimeTransit());
            test.setReportingMethodId(testDO.getReportingMethodId());
            test.setSortingMethodId(testDO.getSortingMethodId());
            test.setReportingSequence(testDO.getReportingSequence());


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
                    
                    testWorksheet.setTestId(test.getId());
                    testWorksheet.setBatchCapacity(worksheetDO.getBatchCapacity());
                    testWorksheet.setNumberFormatId(worksheetDO.getNumberFormatId());
                    testWorksheet.setTotalCapacity(worksheetDO.getTotalCapacity());       
                    testWorksheet.setScriptletId(worksheetDO.getScriptletId());          
                    
                    if(testWorksheet.getId() == null){
                        manager.persist(testWorksheet);
                    }                                                          
                }
                
                if(itemDOList!=null && itemDOList.size() > 0) {
                    exceptionList = new ArrayList<Exception>();
                    validateTestWorksheetItems(exceptionList,itemDOList,worksheetDO);
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
                        }else {                                
                           if(!itemDO.getDelete()) { 
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
                    
                }
                
                if(twsaDOList != null) {
                    exceptionList = new ArrayList<Exception>();
                    validateTestWorksheetAnalytes(exceptionList, twsaDOList);
                    
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(TestWorksheetAnalyteDO twsaDO : twsaDOList){  
                      TestWorksheetAnalyte twsa = null;  
                      if(twsaDO.getId() == null){
                         twsa = new TestWorksheetAnalyte();
                      } else {
                         twsa = manager.find(TestWorksheetAnalyte.class,twsaDO.getId()); 
                      } 
                      
                      if(twsaDO.getDelete() && twsaDO.getId() != null){                           
                          manager.remove(twsa);                                                     
                      }else { 
                         if(!twsaDO.getDelete()) { 
                          twsa.setFlagId(twsaDO.getFlagId());                          
                          twsa.setTestId(test.getId());
                          twsa.setAnalyteId(twsaDO.getAnalyteId());
                          twsa.setRepeat(twsaDO.getRepeat());
                          
                          if(twsa.getId() == null){
                              manager.persist(twsa);
                          }
                       }
                      }
                    }
                    
                }
                
                if(analyteDOList!=null) {
                    exceptionList = new ArrayList<Exception>();
                    validateTestAnalytes(exceptionList,analyteDOList,resultDOList);
                    
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(int iter = 0; iter < analyteDOList.size();iter++){
                        TestAnalyteDO analyteDO = analyteDOList.get(iter);
                        TestAnalyte analyte = null;
                        
                        if(analyteDO.getId()==null){
                            analyte = new TestAnalyte();                           
                        }else if(analyteDO.getId() > 0){
                            analyte =  manager.find(TestAnalyte.class, analyteDO.getId());
                        }else {
                            laterProcAnaList.add(analyteDO);
                        }
                        
                        if(analyteDO.getDelete() && analyteDO.getId() != null && !laterProcAnaList.contains(analyteDO)){                            
                            manager.remove(analyte);                                 
                        }else if(!analyteDO.getDelete() && !laterProcAnaList.contains(analyteDO)){
                            updateTestAnalyte(analyteDO, analyte, test.getId()); 
                            
                            if(analyte.getId() == null){
                                manager.persist(analyte);
                            }
                        }
                        
                    }
                }
                
                if(sectionDOList!=null) {
                    exceptionList = new ArrayList<Exception>();
                    validateTestSections(exceptionList, sectionDOList);
                    
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(int iter = 0; iter < sectionDOList.size(); iter++){
                        TestSectionDO tsDO = sectionDOList.get(iter);
                        TestSection ts = null;
                        
                        if(tsDO.getId()==null) {
                            ts = new TestSection();                           
                        } else {
                            ts =  manager.find(TestSection.class, tsDO.getId());
                        }
                        
                        if(tsDO.getDelete() && tsDO.getId() != null){                           
                            manager.remove(ts);                                                     
                        }else{
                            ts.setFlagId(tsDO.getFlagId());
                            ts.setSectionId(tsDO.getSectionId());
                            ts.setTestId(test.getId());
                            
                            if(ts.getId() == null){
                                manager.persist(ts);
                            }
                        }
                    }
                }
                
                if(resultDOList!=null) {
                    exceptionList = new ArrayList<Exception>();
                    validateTestResults(exceptionList, resultDOList,typeOfSampleDOList);
                    
                    if(exceptionList.size() > 0){
                        throw (RPCException)exceptionList.get(0);
                    }
                    
                    for(int iter = 0 ; iter < resultDOList.size(); iter++){
                        TestResultDO resultDO = resultDOList.get(iter);
                        TestResult result = null;   
                        
                        if(resultDO.getId()==null) {
                            result = new TestResult();                            
                        } else if(resultDO.getId() > 0) {
                            result = manager.find(TestResult.class, resultDO.getId());
                        } else {
                            laterProcResList.add(resultDO);
                        }
                        
                       if(resultDO.getDelete() && resultDO.getId() != null && !laterProcResList.contains(resultDO)){                                                
                           manager.remove(result);   
                           
                        } else { 
                            if(!laterProcResList.contains(resultDO)){                        
                                updateTestResult(resultDO, result,test.getId());
                            
                            if(result.getId() == null){
                                manager.persist(result);
                            }
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
                            }else if(!typeOfSampleDO.getDelete()){                                     
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
                
                if(testReflexDOList!=null) {
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
                       }else if(!refDO.getDelete()){                                
                           testReflex.setTestId(test.getId());
                           testReflex.setAddTestId(refDO.getAddTestId());
                           
                           if(refDO.getTestAnalyteId() != null && refDO.getTestAnalyteId() < 0) {
                              found = false; 
                              for(int i = 0; i < laterProcAnaList.size(); i++) {
                                 if(refDO.getTestAnalyteId().equals(laterProcAnaList.get(i).getId())) {                                     
                                     TestAnalyteDO analyteDO = laterProcAnaList.get(i);
                                     TestAnalyte analyte = new TestAnalyte();                                    
                                     
                                     if(!analyteDO.getDelete()) {
                                         found = true;
                                         updateTestAnalyte(analyteDO, analyte,test.getId());                                          
                                         manager.persist(analyte);                                                           
                                         refDO.setTestAnalyteId(analyte.getId());
                                         tempRealAnaIdMap.put(analyteDO.getId(),analyte.getId());
                                         laterProcAnaList.remove(analyteDO);
                                    }  
                                 } 
                              }  
                              if(!found)
                                refDO.setTestAnalyteId(tempRealAnaIdMap.get(refDO.getTestAnalyteId()));
                           }  
                                                                                
                          testReflex.setTestAnalyteId(refDO.getTestAnalyteId());
                           
                           if(refDO.getTestResultId() != null && refDO.getTestResultId() < 0) {
                               found = false;
                               for(int i = 0; i < laterProcResList.size(); i++) {                                  
                                  if(refDO.getTestResultId().equals(laterProcResList.get(i).getId())) {
                                      TestResultDO resultDO = laterProcResList.get(i);
                                      TestResult result = new TestResult();                                    
                                      
                                      if(!resultDO.getDelete()) {
                                         found = true; 
                                         updateTestResult(resultDO, result,test.getId());                                                                                 
                                         manager.persist(result);                                         
                                         refDO.setTestResultId(result.getId());
                                         tempRealResIdMap.put(resultDO.getId(),result.getId());
                                         laterProcResList.remove(resultDO);
                                     }  
                                  } 
                               } 
                               if(!found)
                                   refDO.setTestResultId(tempRealResIdMap.get(refDO.getTestResultId()));
                            }
                                                                             
                       testReflex.setTestResultId(refDO.getTestResultId());                                                 
                       testReflex.setFlagsId(refDO.getFlagsId());
                                                                                                     
                       if(testReflex.getId() == null) {
                           manager.persist(testReflex);
                       }
                    }
                  }
                }
                
                for(int i =0; i < laterProcAnaList.size(); i++) {
                    TestAnalyteDO analyteDO = laterProcAnaList.get(i);
                    TestAnalyte analyte = new TestAnalyte();
                    
                    if(!analyteDO.getDelete()){
                        updateTestAnalyte(analyteDO, analyte,test.getId());                        
                        manager.persist(analyte); 
                    }       
                }
                
                for(int i =0; i < laterProcResList.size(); i++) {
                    TestResultDO resultDO = laterProcResList.get(i);
                    TestResult result = new TestResult();
                    
                    if(!resultDO.getDelete()) {
                        updateTestResult(resultDO, result,test.getId());                                                                
                        manager.persist(result);
                    }     
                }
                                                       
            lockBean.giveUpLock(testReferenceId, test.getId());
            return test.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }          

    public TestDO getTest(Integer testId) {
        Query query = manager.createNamedQuery("Test.Test");
        query.setParameter("id", testId);
        TestDO testDO = (TestDO)query.getSingleResult(); 
        return testDO;
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
        List<IdNameDO> idValues = null;
        Integer intVal = null;
        String val = null;
        IdNameDO idnDO = null;
        
        Query query = manager.createNamedQuery("TestResult.IdValueByTestAnalyteId");
        query.setParameter("testId", testId);
        query.setParameter("analyteId", analyteId);
        idValues = query.getResultList();
        
        for(int i = 0; i < idValues.size(); i++) {
          idnDO = idValues.get(i);
          val = idnDO.getName();          
          try{
              intVal = Integer.parseInt(val);
              query = manager.createNamedQuery("Dictionary.EntryById");
              query.setParameter("id",intVal);
              val = (String)query.getSingleResult();
              idnDO.setName(val);
          }catch(NumberFormatException ex) {
              ex.printStackTrace();
          }catch(Exception ex) {
              ex.printStackTrace();
          }
        }
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
    
    public HashMap<Integer,List<Integer>> getResultGroupAnalytesMap(Integer testId){
        Query query = manager.createNamedQuery("TestAnalyte.TestAnalytesByResultGroupAndTestId");
        query.setParameter("testId", testId);
        List<Integer[]> analyteList = query.getResultList();
        HashMap<Integer,List<Integer>> listMap = new HashMap<Integer,List<Integer>>();
        List<Integer> anaList = null;
          for(int iter = 0; iter < analyteList.size();iter++){
              Object[] ta = analyteList.get(iter);
              Integer rg  = (Integer)ta[0];
              Integer anaId =  (Integer)ta[1];
              
              if(rg!=null && rg > 0) {
                 if(listMap.get(rg)!=null) {
                     listMap.get(rg).add(anaId);
                 } else {
                     anaList = new ArrayList<Integer>();
                     anaList.add(anaId);
                     listMap.put(rg,anaList);
                 }
              }
                
          }
         return listMap;
    }
    
    public HashMap<Integer,Integer> getUnitIdNumResMapForTest(Integer testId) {        
       HashMap <Integer, Integer> map = new HashMap<Integer, Integer>(); 
       List list = null;
       Object[] obja = null;
       Query query = manager.createNamedQuery("TestResult.NumResultsforUnitIdByTest");       
       query.setParameter("testId", testId);  
       
       list = query.getResultList();
       
       for(int i = 0; i < list.size(); i++) {
           obja = (Object[])list.get(i);           
           map.put((Integer)obja[0],((Long)obja[1]).intValue());
       }
       
       return map;
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

    public List<TestWorksheetItemDO> getTestWorksheetItems(Integer worksheetId) {
        Query query = manager.createNamedQuery("TestWorksheetItem.TestWorksheetItemsByTestWSId");
        query.setParameter("testWorksheetId", worksheetId);
        List<TestWorksheetItemDO> list = query.getResultList();
        return list;
    }   
    
    public List<TestWorksheetAnalyteDO> getTestWorksheetAnalytes(Integer testId) {
        Query query = manager.createNamedQuery("TestWorksheetAnalyte.TestWorksheetAnalyteDOByTestId");
        query.setParameter("testId", testId);
        List<TestWorksheetAnalyteDO> list = query.getResultList();
        return list;
    }
    
    public List<IdNameDO> getTestAnalytesNotAddedToWorksheet(Integer testId) {
        Query query = manager.createNamedQuery("TestAnalyte.TestAnalytesNotAddedToWorksheet");
        query.setParameter("testId", testId);
        List<IdNameDO> list = null;
        try {
         list = query.getResultList();
        }catch (Exception ex) {
            ex.printStackTrace();
        } 
        return list;
    }
    
    public List<TestAnalyteDO> getTestAnalytes(Integer testId){
        Query query = manager.createNamedQuery("TestAnalyte.TestAnalyteDOListByTestId");
        query.setParameter("testId", testId);
        List<TestAnalyteDO> list = query.getResultList();
        return list;        
    }
    
    public List<TestSectionDO> getTestSections(Integer testId){
        Query query = manager.createNamedQuery("TestSection.TestSectionsByTestId");
        query.setParameter("testId", testId);
        List<TestSectionDO> list = query.getResultList();
        return list;
    }
    
    public List<TestResultDO> getTestResults(Integer testId, Integer resultGroup){
        List<TestResultDO> list = null;
        TestResultDO resDO = null;
        Integer typeId = null;
        Integer val =null;
        String sysName = null;
        String entry = null;
        
        Query query = manager.createNamedQuery("TestResult.TestResultDOList");        
        query.setParameter("testId", testId);
        query.setParameter("resultGroup", resultGroup);
        try {
         list = query.getResultList();
        
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            resDO = (TestResultDO)iter.next();
            typeId = resDO.getTypeId();
            query = manager.createNamedQuery("Dictionary.SystemNameById");
            query.setParameter("id", typeId);
            sysName = (String)query.getSingleResult();
            if("test_res_type_dictionary".equals(sysName)) {
              val = Integer.parseInt(resDO.getValue());
              query = manager.createNamedQuery("Dictionary.EntryById");
              query.setParameter("id", val);
              entry = (String)query.getSingleResult();
              resDO.setDictEntry(entry);  
            } else {
              resDO.setDictEntry(null);  
            }
            
        }
       } catch(Exception ex) {
           ex.printStackTrace();
       }
        return list;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(TestMeta);

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
    
    public List getSectionDropDownValues() {
        Query query = manager.createNamedQuery("Section.IdName");        
        List<IdNameDO> sections = query.getResultList();         
        return sections;
    }

    public List getPrepTestDropDownValues() {        
        Query query = manager.createNamedQuery("Test.Names");
        query.setParameter("isActive", "Y");
        List preptestList = query.getResultList();
        return preptestList;
    }    
    
    public List getTestAnalyteDropDownValues(Integer testId) {        
        Query query = manager.createNamedQuery("TestAnalyte.IdName");
        query.setParameter("testId",testId);
        List testAnalytesList = query.getResultList();
        return testAnalytesList;
    }
    
    public List getTestWSItemTypeDropDownValues() {
      Query query = null;        
      List<IdLastNameFirstNameDO> qlist = null;
      List<IdNameDO> rlist = null; 
      IdLastNameFirstNameDO qDO = null;
      IdNameDO retDO = null;
        
      try{                 
        query = manager.createNamedQuery("Dictionary.SystemNamesByCatSysName");
        query.setParameter("systemName", "test_worksheet_item_type");
        qlist = query.getResultList();
        rlist = new ArrayList<IdNameDO>(qlist.size());                
        
        for(int iter = 0; iter < qlist.size(); iter++) {
            qDO = qlist.get(iter);                   
           if("pos_fixed".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(0, retDO);  
           }else if("pos_duplicate".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(1, retDO);  
           }else if("pos_random".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(2, retDO);  
           }else if("pos_last_of_well".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(3, retDO);  
           }else if("pos_last_of_run".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(4, retDO);  
           }else if("pos_last_of_well_&_run".equals(qDO.getFirstName())) {
               retDO = new IdNameDO(qDO.getId(), qDO.getLastName());
               rlist.add(5, retDO);  
           }
        }
       }catch(Exception ex) {
           ex.printStackTrace();
       } 
        return rlist;
    }
    
    public List<IdNameDO> getResultGroupsForTest(Integer testId) {
        Query query = manager.createNamedQuery("TestResult.ResultGroupsByTestId");
        query.setParameter("testId",testId);
        List testRGList = query.getResultList();
        return testRGList;
    }
    
    public List<IdNameDO> getUnitsOfMeasureForTest(Integer testId) {
        List testumList =null;
       try { 
        Query query = manager.createNamedQuery("TestTypeOfSample.DictEntriesForUnitsByTestId");
        query.setParameter("testId",testId);
        testumList = query.getResultList();
       
       }catch(Exception ex) {
            ex.printStackTrace();
       }
       return testumList;
    }
    
    public List getMatchingEntries(String name, int maxResults,String cat){
        Query query = null;
        List entryList = null;
        if("analyte".equals(cat)) {
         query = manager.createNamedQuery("Analyte.AutoCompleteByName");              
        }else if("method".equals(cat)) {
          query = manager.createNamedQuery("Method.AutoCompleteByName"); 
          query.setParameter("isActive", "Y");
        }
         query.setParameter("name", name);       
         query.setMaxResults(maxResults);            
        try{ 
            entryList = (List)query.getResultList();
        }catch(Exception ex){
            ex.printStackTrace();
           
        }     
        return entryList;
     }
    
    public List getTestAutoCompleteByName(String name, int maxResults){
        Query query = manager.createNamedQuery("Test.TestMethodAutoByName");
        query.setParameter("name", name);       
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    public List<Exception> validateForAdd(TestDO testDO,
                               List<TestPrepDO> prepTestDOList,
                               List<TestTypeOfSampleDO> typeOfSampleDOList,
                               List<TestReflexDO> testReflexDOList,
                               TestWorksheetDO worksheetDO,
                               List<TestWorksheetItemDO> itemDOList,
                               List<TestWorksheetAnalyteDO> twsaDOList,
                               List<TestAnalyteDO> analyteDOList,
                               List<TestSectionDO> sectionDOList,
                               List<TestResultDO> resultDOList) {
     List<Exception> exceptionList = new ArrayList<Exception>();
     validateTest(exceptionList, testDO);     
     if(prepTestDOList!=null)
      validateTestPrep(exceptionList,prepTestDOList);
     if(testReflexDOList!=null)
      validateTestReflex(exceptionList,testReflexDOList);  
     if(worksheetDO!=null)
         validateTestWorksheet(exceptionList,worksheetDO);
     if(itemDOList!=null && itemDOList.size() >0 )
         validateTestWorksheetItems(exceptionList,itemDOList,worksheetDO);
     if(twsaDOList!=null) 
         validateTestWorksheetAnalytes(exceptionList, twsaDOList);
     if(analyteDOList!=null)
         validateTestAnalytes(exceptionList,analyteDOList,resultDOList);
     if(sectionDOList!=null)
         validateTestSections(exceptionList,sectionDOList);
     if(resultDOList!=null) 
      validateTestResults(exceptionList, resultDOList,typeOfSampleDOList);
     if(typeOfSampleDOList!=null)
         validateTypeOfSample(exceptionList,typeOfSampleDOList);
     return exceptionList;
    }

    public List<Exception> validateForUpdate(TestDO testDO,
                                  List<TestPrepDO> prepTestDOList,
                                  List<TestTypeOfSampleDO> typeOfSampleDOList,
                                  List<TestReflexDO> testReflexDOList,
                                  TestWorksheetDO worksheetDO,
                                  List<TestWorksheetItemDO> itemDOList,
                                  List<TestWorksheetAnalyteDO> twsaDOList,
                                  List<TestAnalyteDO> analyteDOList,
                                  List<TestSectionDO> sectionDOList,
                                  List<TestResultDO> resultDOList) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateTest(exceptionList, testDO);
        
        if(prepTestDOList!=null)
         validateTestPrep(exceptionList,prepTestDOList);
        if(testReflexDOList!=null)
            validateTestReflex(exceptionList,testReflexDOList); 
        if(worksheetDO!=null)
            validateTestWorksheet(exceptionList,worksheetDO);
        if(itemDOList!=null && itemDOList.size() >0)
            validateTestWorksheetItems(exceptionList,itemDOList,worksheetDO);
        if(twsaDOList!=null) 
            validateTestWorksheetAnalytes(exceptionList, twsaDOList);
        if(analyteDOList!=null)
            validateTestAnalytes(exceptionList,analyteDOList,resultDOList);
        if(sectionDOList!=null)
            validateTestSections(exceptionList,sectionDOList);
        if(resultDOList!=null) 
            validateTestResults(exceptionList, resultDOList,typeOfSampleDOList);
        if(typeOfSampleDOList!=null)
            validateTypeOfSample(exceptionList,typeOfSampleDOList);
        return exceptionList;
    }    
    

    private void validateTest(List<Exception> exceptionList,TestDO testDO) {
        boolean checkDuplicate = true;
        if (testDO.getName() == null || "".equals(testDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getName()));
            checkDuplicate = false;
        }

        if (testDO.getMethodId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getMethodId()));
            checkDuplicate = false;
        }
        
        if(testDO !=null){ 
            if (testDO.getDescription() == null || "".equals(testDO.getDescription())) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          TestMeta.getDescription()));
                checkDuplicate = false;
            }

            if (testDO.getIsActive() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          TestMeta.getIsActive()));
                checkDuplicate = false;
            }
            
            if (testDO.getActiveBegin() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          TestMeta.getActiveBegin()));
                checkDuplicate = false;
            }

            if (testDO.getActiveEnd() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          TestMeta.getActiveEnd()));
                checkDuplicate = false;
            }
            
            if(checkDuplicate){
                if(testDO.getActiveEnd().before(testDO.getActiveBegin())){
                    exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));  
                    checkDuplicate = false;
                }
            }
            
          if(checkDuplicate){
             Query query = manager.createNamedQuery("Test.TestByName");
             query.setParameter("name", testDO.getName());
             List<Test> list = query.getResultList();
             for(int iter = 0; iter < list.size(); iter++){
                 boolean overlap = false;
                 Test test = (Test)list.get(iter);
                 if(!test.getId().equals(testDO.getId())){
                  if(test.getMethodId().equals(testDO.getMethodId())){                  
                      if(test.getIsActive().equals(testDO.getIsActive())){
                          if("Y".equals(testDO.getIsActive())){
                              exceptionList.add(new FormErrorException("testActiveException"));                                   
                              break; 
                           }else{
                             // exceptionList.add(new FormErrorException("testInactiveTimeOverlap"));  
                            }                              
                     }
                      if(test.getActiveBegin().before(testDO.getActiveEnd())&&
                                      (test.getActiveEnd().after(testDO.getActiveBegin()))){
                          overlap = true;  
                       }else if(test.getActiveBegin().equals(testDO.getActiveEnd())||
                                   (test.getActiveEnd().equals(testDO.getActiveBegin()))){
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
                if(!typeDO.getDelete() && typeDO.getTypeOfSampleId()==null){
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                         TestTypeOfSampleMetaMap.getTableName()+":"+TestMeta.getTestTypeOfSample().getTypeOfSampleId()));
                }            
        }
    }
    
    private void validateTestPrep(List<Exception> exceptionList,List<TestPrepDO> testPrepDOList){    
            List<Integer> testPrepIdList = new ArrayList<Integer>();
        int numReq = 0;
        for (int i = 0; i < testPrepDOList.size(); i++) {
            TestPrepDO prepDO = testPrepDOList.get(i);
            if (!prepDO.getDelete()) {
                if (prepDO.getPrepTestId() == null) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",
                                                                   i,
                                                                   TestPrepMetaMap.getTableName() + ":"
                                                                                   + TestMeta.getTestPrep()
                                                                                             .getPrepTestId()));
                } else {
                    if (!testPrepIdList.contains(prepDO.getPrepTestId())) {
                        testPrepIdList.add(prepDO.getPrepTestId());
                    } else {
                        exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException",
                                                                       i,
                                                                       TestPrepMetaMap.getTableName() + ":"
                                                                                       + TestMeta.getTestPrep()
                                                                                                 .getPrepTestId()));
                    }
                }
                if (!"Y".equals(prepDO.getIsOptional())) {
                    if (numReq >= 1) {
                        exceptionList.add(new TableFieldErrorException("moreThanOnePrepTestOptionalException",
                                                                       i,
                                                                       TestPrepMetaMap.getTableName() + ":"
                                                                                       + TestMeta.getTestPrep()
                                                                                                 .getIsOptional()));
                    }
                    numReq++;
                }
            }
        }       
    }
    
    private void validateTestReflex(List<Exception> exceptionList,List<TestReflexDO> testReflexDOList){
        TestReflexDO refDO;
        List<List<Integer>> idsList;
        List<Integer> ids;
        boolean checkForDuplicate;
        int i;
        TableFieldErrorException exc;
        
        idsList = new ArrayList<List<Integer>>();
        for(i = 0; i < testReflexDOList.size(); i++){
            refDO = testReflexDOList.get(i);
            if(!refDO.getDelete()) {
                checkForDuplicate = false;
                ids = new ArrayList<Integer>();
                if(refDO.getAddTestId()==null) {
                    exc = new TableFieldErrorException("fieldRequiredException", i,
                                                           TestMeta.getTestReflex().getAddTestId());
                    exc.setTableKey(TestReflexMetaMap.getTableName());
                    exceptionList.add(exc);                    
                }else {
                    ids.add(refDO.getAddTestId());
                    checkForDuplicate = true;
                }
            
                if(refDO.getTestAnalyteId()==null){
                    exc = new TableFieldErrorException("fieldRequiredException", i,
                                                        TestMeta.getTestReflex().getTestAnalyteId());
                    exc.setTableKey(TestReflexMetaMap.getTableName());
                    exceptionList.add(exc);
                    checkForDuplicate = false;  
                }else{
                    ids.add(refDO.getTestAnalyteId());
                    checkForDuplicate = true;
                }
            
                if(refDO.getTestResultId()==null){
                    exc = new TableFieldErrorException("fieldRequiredException", i,
                                                           TestMeta.getTestReflex().getTestResultId());
                    exc.setTableKey(TestReflexMetaMap.getTableName());
                    exceptionList.add(exc);
                    checkForDuplicate = false;
                }else{
                    ids.add(refDO.getTestResultId());
                    checkForDuplicate = true;
                }
            
                if(refDO.getFlagsId()==null){
                    exc = new TableFieldErrorException("fieldRequiredException", i,
                                                           TestMeta.getTestReflex().getFlagsId());
                    exc.setTableKey(TestReflexMetaMap.getTableName());
                    exceptionList.add(exc);                    
                }
           
                if(checkForDuplicate){
                    if(!idsList.contains(ids)){                
                        idsList.add(ids);
                    }else{
                        exc = new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                               TestMeta.getTestReflex().getAddTestId());
                        exc.setTableKey(TestReflexMetaMap.getTableName());
                        exceptionList.add(exc); 
                    }
                }
            }  
        }       
      }
    
    private void validateTestWorksheet(List<Exception> exceptionList,TestWorksheetDO worksheetDO){
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
               "worksheet:" + TestMeta.getTestWorksheet().getFormatId()));
        }
        
        if(checkForMultiple){
            if((worksheetDO.getTotalCapacity()%worksheetDO.getBatchCapacity())!=0){
                exceptionList.add(new FieldErrorException("totalCapacityMultipleException",
                 "worksheet:" + TestMeta.getTestWorksheet().getTotalCapacity()));
            }
        }                
    }    
    
    private void validateTestWorksheetItems(List<Exception> exceptionList,
                                           List<TestWorksheetItemDO> itemDOList,
                                           TestWorksheetDO worksheetDO) {        
        Integer bc = null;
        if (worksheetDO != null)
            bc = worksheetDO.getBatchCapacity();
        Integer position = null;
        ArrayList<Integer> posList = new ArrayList<Integer>();

        TestWorksheetItemDO itemDO = null;
        boolean checkPosition = false;

        Query query = null;

        String sysName = null;

        for (int i = 0; i < itemDOList.size(); i++) {
            itemDO = itemDOList.get(i);
            if (!itemDO.getDelete()) {
                position = itemDO.getPosition();
                checkPosition = true;

                if (itemDO.getQcName() == null || ("").equals(itemDO.getQcName())) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",
                                                                   i,
                                                                   TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                   + TestMeta.getTestWorksheetItem()
                                                                                             .getQcName()));
                }
                if (itemDO.getTypeId() == null) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",
                                                                   i,
                                                                   TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                   + TestMeta.getTestWorksheetItem()
                                                                                             .getTypeId()));
                    checkPosition = false;
                }

                if (position != null) {
                    if (position <= 0) {
                        exceptionList.add(new TableFieldErrorException("posMoreThanZeroException",
                                                                       i,
                                                                       TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                       + TestMeta.getTestWorksheetItem()
                                                                                                 .getPosition()));
                        checkPosition = false;
                    } else if (bc != null && position > bc) {
                        exceptionList.add(new TableFieldErrorException("posExcBatchCapacityException",
                                                                       i,
                                                                       TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                       + TestMeta.getTestWorksheetItem()
                                                                                                 .getPosition()));
                        checkPosition = false;
                    } else {
                        if (!posList.contains(position)) {
                            posList.add(position);
                        } else {
                            exceptionList.add(new TableFieldErrorException("duplicatePosForQCsException",
                                                                           i,
                                                                           TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                           + TestMeta.getTestWorksheetItem()
                                                                                                     .getPosition()));
                            checkPosition = false;
                        }
                    }
                }

                if (checkPosition) {
                    query = manager.createNamedQuery("Dictionary.SystemNameById");
                    query.setParameter("id", itemDO.getTypeId());
                    sysName = (String)query.getSingleResult();

                    if (position == null) {
                        if ("pos_duplicate".equals(sysName) || "pos_fixed".equals(sysName)) {
                            exceptionList.add(new TableFieldErrorException("fixedDuplicatePosException",
                                                                           i,
                                                                           TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                           + TestMeta.getTestWorksheetItem()
                                                                                                     .getPosition()));
                        }
                    } else {
                        if (position == 1 && "pos_duplicate".equals(sysName)) {
                            exceptionList.add(new TableFieldErrorException("posOneDuplicateException",
                                                                           i,
                                                                           TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                           + TestMeta.getTestWorksheetItem()
                                                                                                     .getTypeId()));
                        } else if (!"pos_duplicate".equals(sysName) && !"pos_fixed".equals(sysName)) {
                            exceptionList.add(new TableFieldErrorException("posSpecifiedException",
                                                                           i,
                                                                           TestWorksheetItemMetaMap.getTableName() + ":"
                                                                                           + TestMeta.getTestWorksheetItem()
                                                                                                     .getPosition()));
                        }
                    }
                }
            }
        }       
    }
    
    private void validateTestWorksheetAnalytes(List<Exception> exceptionList,
                                               List<TestWorksheetAnalyteDO> twsaDOList) {
      TestWorksheetAnalyteDO twsaDO = null;
        
      for(int i = 0; i < twsaDOList.size();i++) {
         twsaDO =  twsaDOList.get(i);  
         if(!twsaDO.getDelete()) {
          if(twsaDO.getRepeat() != null) {            
            if(twsaDO.getRepeat() < 1) {
                exceptionList.add(new TableFieldErrorException("repeatNullForAnalyteException", i,
                   TestWorksheetAnalyteMetaMap.getTableName()+":"+TestMeta.getTestWorksheetAnalyte().getRepeat())); 
            } 
          }else {
             exceptionList.add(new TableFieldErrorException("repeatNullForAnalyteException", i,
                 TestWorksheetAnalyteMetaMap.getTableName()+":"+TestMeta.getTestWorksheetAnalyte().getRepeat()));
          }
        }
      }  
        
    }
    
    private void validateTestAnalytes(List<Exception> exceptionList,List<TestAnalyteDO> analyteDOList, List<TestResultDO> resultDOList){
        int i;
        TestAnalyteDO testAnalyteDO = null;
        TestResultDO resultDO = null;
        HashMap<Integer, Boolean> resultGroups;
        Integer rg;

        resultGroups = new HashMap<Integer, Boolean>();
        if (resultDOList != null) {
            for (i = 0; i < resultDOList.size(); i++) {
                resultDO = resultDOList.get(i);
                resultGroups.put(resultDO.getResultGroup(), Boolean.TRUE);
            }
        }

        for (i = 0; i < analyteDOList.size(); i++) {
            testAnalyteDO = analyteDOList.get(i);
            rg = testAnalyteDO.getResultGroup();
            if (rg != null && !resultGroups.containsKey(rg)) {
                exceptionList.add(new FormErrorException("emptyResultGroupException"));
                return;
            }    
        }
    }
    
   private void validateTestResults(List<Exception> exceptionList,
                                    List<TestResultDO> resultDOList,
                                    List<TestTypeOfSampleDO> sampleTypeDOList) {
     TestResultDO resDO;
     Integer numId,dictId, titerId , typeId , dateId ,dtId,timeId,resultGroup,unit;
     HashMap<Integer, Double> unitTtrMaxMap, unitNumMaxMap;
     TableFieldErrorException exc;
     Date date;
     
     String[] st;
     
     ArrayList<String> dvlist,dlist,dtlist,tlist;     
     ArrayList<Integer> rlist;
      
     Double pnMax ,cnMin,cnMax,ptMax,ctMin,ctMax;
           
     String value,hhmm,defDate,dateStr, unitText;
      
     Query query;
     
     query = manager.createNamedQuery("Dictionary.IdBySystemName");
     
     resultGroup = null;
     pnMax = null;    
     ptMax = null;
     unitTtrMaxMap = null;
     unitNumMaxMap = null;
     dlist = null;
     tlist = null;
     dvlist = null;
     dtlist = null;
     defDate = "2000-01-01 ";
      
     query.setParameter("systemName", "test_res_type_dictionary");
     dictId = (Integer)query.getSingleResult();
      
     query.setParameter("systemName", "test_res_type_numeric");
     numId = (Integer)query.getSingleResult();
      
     query.setParameter("systemName", "test_res_type_titer");
     titerId = (Integer)query.getSingleResult();
     
     query.setParameter("systemName", "test_res_type_date");
     dateId = (Integer)query.getSingleResult(); 
     
     query.setParameter("systemName", "test_res_type_date_time");
     dtId = (Integer)query.getSingleResult();
     
     query.setParameter("systemName", "test_res_type_time");
     timeId = (Integer)query.getSingleResult();
      
     for(int i = 0; i < resultDOList.size(); i++){
       resDO = resultDOList.get(i);
       typeId = resDO.getTypeId();
       unit = resDO.getUnitOfMeasureId();
       
       if(!unitIsValid(unit, sampleTypeDOList)) {
           query = manager.createNamedQuery("Dictionary.EntryById");
           query.setParameter("id", unit);
           unitText = (String)query.getResultList().get(0);
           exc = new TableFieldErrorException("illegalUnitOfMeasureException:"+unitText, i,
                                                  TestMeta.getTestResult().getUnitOfMeasureId());
           exc.setTableKey(TestResultMetaMap.getTableName());
           exceptionList.add(exc);
       }
       
       if(resultGroup == null){
           resultGroup = resDO.getResultGroup();
           unitTtrMaxMap = new HashMap<Integer, Double>();
           unitNumMaxMap = new HashMap<Integer, Double>();
           dvlist = new ArrayList<String>();
           dlist = new ArrayList<String>(); 
           dtlist = new ArrayList<String>(); 
           tlist = new ArrayList<String>();
       } else if(!resultGroup.equals(resDO.getResultGroup())) {
           resultGroup = resDO.getResultGroup();
           unitTtrMaxMap = new HashMap<Integer, Double>();
           unitNumMaxMap = new HashMap<Integer, Double>();
           dvlist = new ArrayList<String>();
           dlist = new ArrayList<String>(); 
           dtlist = new ArrayList<String>(); 
           tlist = new ArrayList<String>();
           ptMax = null;
           pnMax = null;
       }
       
       
      if(!resDO.getDelete()) {                   
       value = resDO.getValue();           
       
        if(typeId == null) {
            exc = new TableFieldErrorException("fieldRequiredException", i,
                                               TestMeta.getTestResult().getTypeId());
            exc.setTableKey(TestResultMetaMap.getTableName());
            exceptionList.add(exc);  
        }else {
         if(numId.equals(typeId)) {           
            if(value != null && !"".equals(value.trim())) {
               st = value.split(",");                 
               if(st.length != 2) {
                  exc = new TableFieldErrorException("illegalNumericFormatException", i,
                                                         TestMeta.getTestResult().getValue());  
                  exc.setTableKey(TestResultMetaMap.getTableName());
                  exceptionList.add(exc);    
               } else {
                  try {                                      
                   cnMin = Double.valueOf(st[0]); 
                   cnMax = Double.valueOf(st[1]);
                                      
                   if(!(cnMin < cnMax)) {
                      exc =  new TableFieldErrorException("illegalNumericRangeException", i,
                                                              TestMeta.getTestResult().getValue());
                      exc.setTableKey(TestResultMetaMap.getTableName());
                      exceptionList.add(exc);  
                   }else if(pnMax != null && resultGroup.equals(resDO.getResultGroup())){                         
                       if(unit == null) { 
                           pnMax = unitNumMaxMap.get(-1);
                       } else {
                           pnMax = unitNumMaxMap.get(unit); 
                       }
                       
                       if(pnMax != null && !(cnMin > pnMax)) {
                           exc = new TableFieldErrorException("testNumRangeOverlapException", i,
                                                                  TestMeta.getTestResult().getValue());
                           exc.setTableKey(TestResultMetaMap.getTableName());
                           exceptionList.add(exc); 
                       }                                               
                   }
                   
                   pnMax = cnMax;
                   
                   if(unit == null) {
                       unitNumMaxMap.put(-1, cnMax);  
                   } else {
                       unitNumMaxMap.put(unit, cnMax);
                   }            
                   
                   
                   
                 } catch (NumberFormatException ex) {
                     exc = new TableFieldErrorException("illegalNumericFormatException", i,
                                                                TestMeta.getTestResult().getValue());
                     exc.setTableKey(TestResultMetaMap.getTableName());
                     exceptionList.add(exc);   
                 }                   
               }
            }else {
                exc = new TableFieldErrorException("fieldRequiredException", i,
                                                       TestMeta.getTestResult().getValue());
                exc.setTableKey(TestResultMetaMap.getTableName());
                exceptionList.add(exc);    
            }
         } else if(titerId.equals(typeId)) {           
             if(value != null && !"".equals(value.trim())) {
                 st = value.split(":");                 
                 if(st.length != 2) {
                    exc = new TableFieldErrorException("illegalTiterFormatException", i,
                                                           TestMeta.getTestResult().getValue());
                    exc.setTableKey(TestResultMetaMap.getTableName());
                    exceptionList.add(exc);    
                 } else {
                    try {                                        
                     ctMin = Double.valueOf(st[0]); 
                     ctMax = Double.valueOf(st[1]);
                     
                     if(ctMin > ctMax) {
                         exc = new TableFieldErrorException("illegalTiterRangeException", i,
                                                                TestMeta.getTestResult().getValue());
                         exc.setTableKey(TestResultMetaMap.getTableName());
                         exceptionList.add(exc);  
                     }else if(ptMax != null && resultGroup.equals(resDO.getResultGroup())){                         
                         if(unit == null) { 
                             ptMax = unitTtrMaxMap.get(-1);
                         } else {
                             ptMax = unitTtrMaxMap.get(unit); 
                         }
                         
                        if(ptMax != null && !(ctMin > ptMax)) { 
                            exc = new TableFieldErrorException("testTiterRangeOverlapException", i,
                                                                   TestMeta.getTestResult().getValue());
                            exc.setTableKey(TestResultMetaMap.getTableName());
                            exceptionList.add(exc);
                         } 
                     }
                     
                     ptMax = ctMax;                           
                     
                     if(unit == null) {
                         unitTtrMaxMap.put(-1, ptMax);  
                     } else {
                         unitTtrMaxMap.put(unit, ptMax);
                     }
                     
                   } catch (NumberFormatException ex) {
                       exc = new TableFieldErrorException("illegalTiterFormatException", i,
                                                              TestMeta.getTestResult().getValue());
                       exc.setTableKey(TestResultMetaMap.getTableName());
                       exceptionList.add(exc);   
                   }                   
                 }
              } else {
                  exc = new TableFieldErrorException("fieldRequiredException", i,
                                                         TestMeta.getTestResult().getValue());
                  exc.setTableKey(TestResultMetaMap.getTableName());
                  exceptionList.add(exc);    
              }             
           } else if(dateId.equals(typeId)) {           
               if(value != null && !"".equals(value.trim())) {
                   try{
                       date = new Date(value.replaceAll("-", "/"));
                       
                       if(!dlist.contains(value)) {
                           dlist.add(value); 
                        } else {
                            exc = new TableFieldErrorException("testDateValueNotUniqueException", i,
                                                                   TestMeta.getTestResult().getValue());
                            exc.setTableKey(TestResultMetaMap.getTableName());
                            exceptionList.add(exc); 
                       }
                   } catch (IllegalArgumentException ex) {
                       exc = new TableFieldErrorException("illegalDateValueException", i,
                                                              TestMeta.getTestResult().getValue());
                       exc.setTableKey(TestResultMetaMap.getTableName());
                       exceptionList.add(exc);   
                   }
                                                                                         
                  }
                  date = null;
              } else if(dtId.equals(typeId)) {           
                  if(value != null && !"".equals(value.trim())) {
                      try{
                          st = value.split(" ");                             
                          if(st.length != 2)
                           throw new IllegalArgumentException();
                          
                          hhmm = st[1];
                          if(hhmm.split(":").length != 2) 
                              throw new IllegalArgumentException(); 
                          
                          date = new Date(value.replaceAll("-", "/"));
                          
                          if(!dtlist.contains(value)) {
                              dtlist.add(value); 
                           } else {
                               exc = new TableFieldErrorException("testDateTimeValueNotUniqueException", i,
                                                                      TestMeta.getTestResult().getValue());
                               exc.setTableKey(TestResultMetaMap.getTableName());
                               exceptionList.add(exc); 
                          }
                      } catch (IllegalArgumentException ex) {
                          exc = new TableFieldErrorException("illegalDateTimeValueException", i,
                                                                 TestMeta.getTestResult().getValue());
                          exc.setTableKey(TestResultMetaMap.getTableName());
                          exceptionList.add(exc);   
                      }
                                                                                            
                     }
                     date = null;
                } else if(timeId.equals(typeId)) {           
                    if(value != null && !"".equals(value.trim())) {
                        
                        try{
                            st = value.split(":");                             
                            if(st.length != 2)
                             throw new IllegalArgumentException();                                                       
                            
                            dateStr = defDate + value;                                
                            date = new Date(dateStr.replaceAll("-", "/"));
                            
                          if(!tlist.contains(value)) {
                                tlist.add(value); 
                          } else {
                              exc = new TableFieldErrorException("testTimeValueNotUniqueException", i,
                                                                     TestMeta.getTestResult().getValue());
                              exc.setTableKey(TestResultMetaMap.getTableName());
                              exceptionList.add(exc); 
                         }
                        } catch (IllegalArgumentException ex) {
                            exc = new TableFieldErrorException("illegalTimeValueException", i,
                                                                   TestMeta.getTestResult().getValue());
                            exc.setTableKey(TestResultMetaMap.getTableName());
                            exceptionList.add(exc);   
                        }
                                                                                              
                      }
                       date = null;
                  }else if(dictId.equals(typeId)) {
                     value = resDO.getDictEntry();
                     if(value != null && !"".equals(value.trim())) {
                       if(!dvlist.contains(value)) {
                           dvlist.add(value); 
                      } else {
                          exc = new TableFieldErrorException("testDictEntryNotUniqueException", i,
                                                                 TestMeta.getTestResult().getValue());
                          exc.setTableKey(TestResultMetaMap.getTableName());
                          exceptionList.add(exc); 
                    }
                   
                      query = manager.createNamedQuery("Dictionary.IdByEntry");
                      query.setParameter("entry", value);
                      rlist = (ArrayList<Integer>)query.getResultList();
                  
                    if(rlist.size() == 0) {
                        exc = new TableFieldErrorException("illegalDictEntryException", i,
                                                               TestMeta.getTestResult().getValue());
                        exc.setTableKey(TestResultMetaMap.getTableName());
                        exceptionList.add(exc);
                    }

               } else {
                   exc = new TableFieldErrorException("fieldRequiredException", i,
                                                          TestMeta.getTestResult().getValue());
                   exc.setTableKey(TestResultMetaMap.getTableName());
                   exceptionList.add(exc);    
               }
           }
         }            
        } 
      
        resultGroup = resDO.getResultGroup();
      }
        
    }
    
    private void validateTestSections(List<Exception> exceptionList,List<TestSectionDO> sectionDOList){
        Integer defId = null;
        Integer askId = null;
        Integer matchId = null;
        Integer flagId = null;
        Integer sectId = null;
        
        List<Integer> idList = null; 
                 
        int size = sectionDOList.size();
        int numDef = 0;
        int numAsk = 0;
        int numMatch = 0;
        int numBlank = 0;
        
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");  
        TestSectionDO secDO = null;
                
        query.setParameter("systemName", "test_section_default");   
        defId = (Integer)query.getSingleResult();
        
        query.setParameter("systemName", "test_section_ask");   
        askId = (Integer)query.getSingleResult();
        
        query.setParameter("systemName", "test_section_match");   
        matchId = (Integer)query.getSingleResult();
                        
        
       if(size > 0) {
         idList = new ArrayList<Integer>();            
         for(int iter = 0; iter < size; iter++) {
          secDO = sectionDOList.get(iter); 
          if(!secDO.getDelete()) {
           flagId = secDO.getFlagId();          
           sectId = secDO.getSectionId();
          
           if(sectId == null) {
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", iter,
             TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getSectionId()));  
           }else if(idList.contains(sectId)){
              exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException", iter,
               TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getSectionId())); 
           }else {
             idList.add(sectId);
           }
          
          
          if(flagId == null) {
              numBlank++;
          }else if(defId.equals(flagId)) {
              numDef++;
          }else if(askId.equals(flagId)) {
              numAsk++;
          }else if(matchId.equals(flagId)) {
              numMatch++;              
          }
         } 
        }
                
        if(numBlank == size) {
            for(int iter = 0; iter < size; iter++) {
                exceptionList.add(new TableFieldErrorException("allSectCantBeBlankException", iter,
                 TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId()));                  
            }           
         } else if(numDef > 1) {
             for(int iter = 0; iter < size; iter++) {
                 secDO = sectionDOList.get(iter);  
                 flagId = secDO.getFlagId();
                 if(!secDO.getDelete() && (flagId != null)) {
                  exceptionList.add(new TableFieldErrorException("allSectBlankIfDefException", iter,
                   TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId()));
                 } 
             }
         } else if(numDef == 1 && numBlank != (size-1)) {
             for(int iter = 0; iter < size; iter++) {
                 secDO = sectionDOList.get(iter);  
                 flagId = secDO.getFlagId();
                 if(!secDO.getDelete() && (flagId != null) && !defId.equals(flagId)) {
                  exceptionList.add(new TableFieldErrorException("allSectBlankIfDefException", iter,
                   TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId()));
                 } 
             }
         } else if(numMatch > 0 && numMatch != size) {
             for(int iter = 0; iter < size; iter++) {
               secDO = sectionDOList.get(iter);  
               flagId = secDO.getFlagId();
               if(!secDO.getDelete()) { 
                 if((flagId != null) && !matchId.equals(flagId)) {
                  exceptionList.add(new TableFieldErrorException("allSectMatchFlagException", iter,
                   TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId()));
                 }else if(flagId ==null) {
                    exceptionList.add(new TableFieldErrorException("allSectMatchFlagException", iter,
                     TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId())); 
                }                
             }
            }    
         } else if(numAsk > 0 && numAsk != size) {
             for(int iter = 0; iter < size; iter++) {
               secDO = sectionDOList.get(iter);  
               flagId = secDO.getFlagId();
               if(!secDO.getDelete()) {
                 if((flagId != null) && !askId.equals(flagId)) {
                  exceptionList.add(new TableFieldErrorException("allSectAskFlagException", iter,
                   TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId()));
                 }else if(flagId == null) {
                    exceptionList.add(new TableFieldErrorException("allSectAskFlagException", iter,
                     TestSectionMetaMap.getTableName()+":"+TestMeta.getTestSection().getFlagId())); 
                }                
             }
            }     
         } 
       }                                     
                                                                           
    }
     
    
    private void updateTestAnalyte(TestAnalyteDO analyteDO, TestAnalyte analyte, Integer testId) {
        analyte.setAnalyteGroup(analyteDO.getAnalyteGroup());
        analyte.setAnalyteId(analyteDO.getAnalyteId());
        analyte.setIsReportable(analyteDO.getIsReportable());
        analyte.setResultGroup(analyteDO.getResultGroup());
        analyte.setScriptletId(analyteDO.getScriptletId());
        analyte.setSortOrder(analyteDO.getSortOrder());
        analyte.setTestId(testId);
        analyte.setTypeId(analyteDO.getTypeId());
    }
    
    private void updateTestResult(TestResultDO resultDO, TestResult result,Integer testId) {
        result.setUnitOfMeasureId(resultDO.getUnitOfMeasureId());
        result.setContLevel(resultDO.getContLevel());
        result.setValue(resultDO.getValue());
        result.setFlagsId(resultDO.getFlagsId());
        result.setHazardLevel(resultDO.getHazardLevel());
        result.setQuantLimit(resultDO.getQuantLimit());
        result.setResultGroup(resultDO.getResultGroup());
        result.setRoundingMethodId(resultDO.getRoundingMethodId());
        result.setSignificantDigits(resultDO.getSignificantDigits());
        result.setSortOrder(resultDO.getSortOrder());
        result.setTestId(testId);
        result.setTypeId(resultDO.getTypeId());    
    }
    
    /**
     * This method checks to see if a unit of measure (resultUnitId) assigned to a test result    
     * belongs to the list of units added to the test 
     */
    private boolean unitIsValid(Integer resultUnitId,List<TestTypeOfSampleDO> sampleTypeDOList) {
        int i,numMatch;
        TestTypeOfSampleDO sampleDO;
        Integer unitId;
        
        numMatch = 0;
        
        if(resultUnitId == null) 
            return true;
        
        if(sampleTypeDOList == null) {              
            return false;
        }else {
            for(i = 0; i < sampleTypeDOList.size(); i++) {
                sampleDO = sampleTypeDOList.get(i);
                unitId = sampleDO.getUnitOfMeasureId();
                if(!sampleDO.getDelete() && unitId != null && unitId.equals(resultUnitId)) {
                    numMatch++;
                }
            }
        }
        
        return (numMatch != 0) ;
    }
        
     
}