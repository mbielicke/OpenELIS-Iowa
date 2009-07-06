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
import org.openelis.domain.DictionaryIdEntrySysNameDO;
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
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.CategoryLocal;
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
import org.openelis.utilcommon.NumericRange;
import org.openelis.utilcommon.InconsistentException;
import org.openelis.utilcommon.ParseException;
import org.openelis.util.QueryBuilder;
import org.openelis.utilcommon.TestResultValidator;
import org.openelis.utilcommon.TiterRange;
import org.openelis.utils.GetPage;
import org.openelis.utils.SecurityInterceptor;

import java.util.ArrayList;
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
        @EJB(name = "ejb/Lock", beanInterface = LockLocal.class)})

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
    
    @EJB
    private CategoryLocal categoryBean;

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
        List<TestAnalyteDO> laterProcAnaList; 
        List<TestResultDO> laterProcResList;
        HashMap<Integer, Integer> tempRealResIdMap;
        HashMap<Integer, Integer> tempRealAnaIdMap;                      
        
        boolean found = false;

        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test");
        Integer testReferenceId = (Integer)query.getSingleResult();

        if (testDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.validateLock(testReferenceId, testDO.getId());
        }

        validateTest(testDO,prepTestDOList,typeOfSampleDOList,testReflexDOList,
                     worksheetDO,itemDOList,twsaDOList,analyteDOList,
                     sectionDOList,resultDOList);

        laterProcAnaList = new ArrayList<TestAnalyteDO>();
        laterProcResList = new ArrayList<TestResultDO>();
        tempRealResIdMap = new HashMap<Integer, Integer>();
        tempRealAnaIdMap = new HashMap<Integer, Integer>();

        manager.setFlushMode(FlushModeType.COMMIT);
        Test test = null;

        if (testDO.getId() == null) {
            test = new Test();
        } else {
            test = manager.find(Test.class, testDO.getId());
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

        if (prepTestDOList != null) {
            for (int i = 0; i < prepTestDOList.size(); i++) {
                TestPrepDO testPrepDO = prepTestDOList.get(i);
                TestPrep testPrep = null;
                if (testPrepDO.getId() == null) {
                    testPrep = new TestPrep();
                } else {
                    testPrep = manager.find(TestPrep.class, testPrepDO.getId());
                }
                if (testPrepDO.getDelete() && testPrepDO.getId() != null) {
                    manager.remove(testPrep);
                } else {
                    testPrep.setIsOptional(testPrepDO.getIsOptional());
                    testPrep.setPrepTestId(testPrepDO.getPrepTestId());
                    testPrep.setTestId(test.getId());

                    if (testPrep.getId() == null) {
                        manager.persist(testPrep);
                    }
                }
            }
        }

        if (worksheetDO != null) {
            if (worksheetDO.getId() != null) {
                testWorksheet = manager.find(TestWorksheet.class,
                                             worksheetDO.getId());
            } else {
                testWorksheet = new TestWorksheet();
            }

            testWorksheet.setTestId(test.getId());
            testWorksheet.setBatchCapacity(worksheetDO.getBatchCapacity());
            testWorksheet.setNumberFormatId(worksheetDO.getNumberFormatId());
            testWorksheet.setTotalCapacity(worksheetDO.getTotalCapacity());
            testWorksheet.setScriptletId(worksheetDO.getScriptletId());

            if (testWorksheet.getId() == null) {
                manager.persist(testWorksheet);
            }
        }

        if (itemDOList != null) {
            for (int iter = 0; iter < itemDOList.size(); iter++) {
                TestWorksheetItemDO itemDO = itemDOList.get(iter);
                TestWorksheetItem testWorksheetItem = null;
                if (itemDO.getId() == null) {
                    testWorksheetItem = new TestWorksheetItem();
                } else {
                    testWorksheetItem = manager.find(TestWorksheetItem.class,
                                                     itemDO.getId());
                }
                if (itemDO.getDelete() && itemDO.getId() != null) {
                    manager.remove(testWorksheetItem);
                } else {
                    if (!itemDO.getDelete()) {
                        testWorksheetItem.setPosition(itemDO.getPosition());
                        testWorksheetItem.setQcName(itemDO.getQcName());
                        testWorksheetItem.setTestWorksheetId(testWorksheet.getId());
                        testWorksheetItem.setTypeId(itemDO.getTypeId());

                        if (testWorksheetItem.getId() == null) {
                            manager.persist(testWorksheetItem);
                        }
                    }
                }
            }

        }

        if (twsaDOList != null) {
            for (TestWorksheetAnalyteDO twsaDO : twsaDOList) {
                TestWorksheetAnalyte twsa = null;
                if (twsaDO.getId() == null) {
                    twsa = new TestWorksheetAnalyte();
                } else {
                    twsa = manager.find(TestWorksheetAnalyte.class,
                                        twsaDO.getId());
                }

                if (twsaDO.getDelete() && twsaDO.getId() != null) {
                    manager.remove(twsa);
                } else {
                    if (!twsaDO.getDelete()) {
                        twsa.setFlagId(twsaDO.getFlagId());
                        twsa.setTestId(test.getId());
                        twsa.setAnalyteId(twsaDO.getAnalyteId());
                        twsa.setRepeat(twsaDO.getRepeat());

                        if (twsa.getId() == null) {
                            manager.persist(twsa);
                        }
                    }
                }
            }

        }

        if (analyteDOList != null) {
            for (int iter = 0; iter < analyteDOList.size(); iter++) {
                TestAnalyteDO analyteDO = analyteDOList.get(iter);
                TestAnalyte analyte = null;

                if (analyteDO.getId() == null) {
                    analyte = new TestAnalyte();
                } else if (analyteDO.getId() > 0) {
                    analyte = manager.find(TestAnalyte.class, analyteDO.getId());
                } else {
                    laterProcAnaList.add(analyteDO);
                }

                if (analyteDO.getDelete() && analyteDO.getId() != null
                    && !laterProcAnaList.contains(analyteDO)) {
                    manager.remove(analyte);
                } else if (!analyteDO.getDelete() && !laterProcAnaList.contains(analyteDO)) {
                    updateTestAnalyte(analyteDO, analyte, test.getId());

                    if (analyte.getId() == null) {
                        manager.persist(analyte);
                    }
                }

            }
        }

        if (sectionDOList != null) {
            for (int iter = 0; iter < sectionDOList.size(); iter++) {
                TestSectionDO tsDO = sectionDOList.get(iter);
                TestSection ts = null;

                if (tsDO.getId() == null) {
                    ts = new TestSection();
                } else {
                    ts = manager.find(TestSection.class, tsDO.getId());
                }

                if (tsDO.getDelete() && tsDO.getId() != null) {
                    manager.remove(ts);
                } else {
                    ts.setFlagId(tsDO.getFlagId());
                    ts.setSectionId(tsDO.getSectionId());
                    ts.setTestId(test.getId());

                    if (ts.getId() == null) {
                        manager.persist(ts);
                    }
                }
            }
        }

        if (resultDOList != null) {
            for (int iter = 0; iter < resultDOList.size(); iter++) {
                TestResultDO resultDO = resultDOList.get(iter);
                TestResult result = null;

                if (resultDO.getId() == null) {
                    result = new TestResult();
                } else if (resultDO.getId() > 0) {
                    result = manager.find(TestResult.class, resultDO.getId());
                } else {
                    laterProcResList.add(resultDO);
                }

                if (resultDO.getDelete() && resultDO.getId() != null
                    && !laterProcResList.contains(resultDO)) {
                    manager.remove(result);

                } else {
                    if (!laterProcResList.contains(resultDO)) {
                        updateTestResult(resultDO, result, test.getId());

                        if (result.getId() == null) {
                            manager.persist(result);
                        }
                    }
                }
            }
        }

        if (typeOfSampleDOList != null) {
            for (int i = 0; i < typeOfSampleDOList.size(); i++) {
                TestTypeOfSampleDO typeOfSampleDO = typeOfSampleDOList.get(i);

                if (typeOfSampleDO != null) {
                    TestTypeOfSample typeOfSample = null;
                    if (typeOfSampleDO.getId() == null) {
                        typeOfSample = new TestTypeOfSample();
                    } else {
                        typeOfSample = manager.find(TestTypeOfSample.class,
                                                    typeOfSampleDO.getId());
                    }
                    if (typeOfSampleDO.getDelete() && typeOfSampleDO.getId() != null) {
                        manager.remove(typeOfSample);
                    } else if (!typeOfSampleDO.getDelete()) {
                        typeOfSample.setTestId(test.getId());
                        typeOfSample.setTypeOfSampleId(typeOfSampleDO.getTypeOfSampleId());
                        typeOfSample.setUnitOfMeasureId(typeOfSampleDO.getUnitOfMeasureId());

                        if (typeOfSample.getId() == null) {
                            manager.persist(typeOfSample);
                        }
                    }
                }
            }
        }

        if (testReflexDOList != null) {
            for (int iter = 0; iter < testReflexDOList.size(); iter++) {
                TestReflexDO refDO = testReflexDOList.get(iter);
                TestReflex testReflex = null;
                if (refDO.getId() == null) {
                    testReflex = new TestReflex();
                } else {
                    testReflex = manager.find(TestReflex.class, refDO.getId());
                }
                if (refDO.getDelete() && refDO.getId() != null) {
                    manager.remove(testReflex);
                } else if (!refDO.getDelete()) {
                    testReflex.setTestId(test.getId());
                    testReflex.setAddTestId(refDO.getAddTestId());

                    if (refDO.getTestAnalyteId() != null && refDO.getTestAnalyteId() < 0) {
                        found = false;
                        for (int i = 0; i < laterProcAnaList.size(); i++) {
                            if (refDO.getTestAnalyteId()
                                     .equals(laterProcAnaList.get(i).getId())) {
                                TestAnalyteDO analyteDO = laterProcAnaList.get(i);
                                TestAnalyte analyte = new TestAnalyte();

                                if (!analyteDO.getDelete()) {
                                    found = true;
                                    updateTestAnalyte(analyteDO,
                                                      analyte,
                                                      test.getId());
                                    manager.persist(analyte);
                                    refDO.setTestAnalyteId(analyte.getId());
                                    tempRealAnaIdMap.put(analyteDO.getId(),
                                                         analyte.getId());
                                    laterProcAnaList.remove(analyteDO);
                                }
                            }
                        }
                        if (!found)
                            refDO.setTestAnalyteId(tempRealAnaIdMap.get(refDO.getTestAnalyteId()));
                    }

                    testReflex.setTestAnalyteId(refDO.getTestAnalyteId());

                    if (refDO.getTestResultId() != null && refDO.getTestResultId() < 0) {
                        found = false;
                        for (int i = 0; i < laterProcResList.size(); i++) {
                            if (refDO.getTestResultId()
                                     .equals(laterProcResList.get(i).getId())) {
                                TestResultDO resultDO = laterProcResList.get(i);
                                TestResult result = new TestResult();

                                if (!resultDO.getDelete()) {
                                    found = true;
                                    updateTestResult(resultDO,result,test.getId());
                                    manager.persist(result);
                                    refDO.setTestResultId(result.getId());
                                    tempRealResIdMap.put(resultDO.getId(),
                                                         result.getId());
                                    laterProcResList.remove(resultDO);
                                }
                            }
                        }
                        if (!found)
                            refDO.setTestResultId(tempRealResIdMap.get(refDO.getTestResultId()));
                    }

                    testReflex.setTestResultId(refDO.getTestResultId());
                    testReflex.setFlagsId(refDO.getFlagsId());

                    if (testReflex.getId() == null) {
                        manager.persist(testReflex);
                    }
                }
            }
        }

        for (int i = 0; i < laterProcAnaList.size(); i++) {
            TestAnalyteDO analyteDO = laterProcAnaList.get(i);
            TestAnalyte analyte = new TestAnalyte();

            if (!analyteDO.getDelete()) {
                updateTestAnalyte(analyteDO, analyte, test.getId());
                manager.persist(analyte);
            }
        }

        for (int i = 0; i < laterProcResList.size(); i++) {
            TestResultDO resultDO = laterProcResList.get(i);
            TestResult result = new TestResult();

            if (!resultDO.getDelete()) {
                updateTestResult(resultDO, result, test.getId());
                manager.persist(result);
            }
        }

        lockBean.giveUpLock(testReferenceId, test.getId());
        return test.getId();        
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
    
    /**
     * This method returns the data for the test results that belong to a test with
     * id "testId" and a specific result group of that test specified by "resultGroup" 
     */
    public List<TestResultDO> getTestResults(Integer testId, Integer resultGroup){
        List<TestResultDO> list;
        TestResultDO resDO;
        Integer typeId,val;
        String sysName,entry;
        Query query;
               
        list = null;
        query = manager.createNamedQuery("TestResult.TestResultDOList");        
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
                    resDO.setValue(entry);  
                } 
            
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }    
        return list;
    }
    
    /**
     * This method returns the data for all test results belonging to a test with
     * id "testId". The test results are arranged by sequentially increasing 
     * result group numbers.  
     */
    public List<List<TestResultDO>> getTestResults(Integer testId){
        List<List<TestResultDO>> listCollection;
        List<TestResultDO> list;
        TestResultDO resDO;
        Integer typeId,val,resultGroup;
        String sysName,entry;
        Query query;
        Iterator iter;
               
        list = null;
        listCollection = new ArrayList<List<TestResultDO>>();   
        resultGroup = 1;
        while(resultGroup != null) {               
            query = manager.createNamedQuery("TestResult.TestResultDOList");        
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", resultGroup);            
                                        
            try {
                list = query.getResultList();
                if(list.size() == 0) { 
                    resultGroup = null;
                    break;
                }                                             
                for (iter = list.iterator(); iter.hasNext();) {
                    resDO = (TestResultDO)iter.next();
                    typeId = resDO.getTypeId();
                    query = manager.createNamedQuery("Dictionary.SystemNameById");
                    query.setParameter("id", typeId);
                    sysName = (String)query.getResultList().get(0);
                    if("test_res_type_dictionary".equals(sysName)) {
                        val = Integer.parseInt(resDO.getValue());
                        query = manager.createNamedQuery("Dictionary.EntryById");
                        query.setParameter("id", val);
                        entry = (String)query.getResultList().get(0);
                        resDO.setValue(entry);  
                    }           
                }                                      
                
            } catch(Exception ex) {
                ex.printStackTrace();
            } 
            listCollection.add(list);
            resultGroup++;   
        }
        return listCollection;
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
    
    public List getTestAnalyteDropDownValues(Integer testId) {        
        Query query = manager.createNamedQuery("TestAnalyte.IdName");
        query.setParameter("testId",testId);
        List testAnalytesList = query.getResultList();
        return testAnalytesList;
    }
    
    public List getTestWSItemTypeDropDownValues() {
      Query query = null;        
      List<DictionaryIdEntrySysNameDO> qlist,rlist; 
      DictionaryIdEntrySysNameDO qDO = null;      
        
      rlist = null;
      try{                 
        query = manager.createNamedQuery("Dictionary.SystemNamesByCatSysName");
        query.setParameter("systemName", "test_worksheet_item_type");
        qlist = query.getResultList();
        rlist = new ArrayList<DictionaryIdEntrySysNameDO>(qlist.size());                
        
        for(int iter = 0; iter < qlist.size(); iter++) {
            qDO = qlist.get(iter);                   
           if("pos_fixed".equals(qDO.getSystemName())) {
               rlist.add(0, qDO);  
           }else if("pos_duplicate".equals(qDO.getSystemName())) {               
               rlist.add(1, qDO);  
           }else if("pos_random".equals(qDO.getSystemName())) {              
               rlist.add(2, qDO);  
           }else if("pos_last_of_well".equals(qDO.getSystemName())) {
               rlist.add(3, qDO);  
           }else if("pos_last_of_run".equals(qDO.getSystemName())) {
               rlist.add(4, qDO);  
           }else if("pos_last_of_well_&_run".equals(qDO.getSystemName())) {
               rlist.add(5, qDO);  
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
    
    public List getTestAutoCompleteByName(String name, int maxResults){
        Query query = manager.createNamedQuery("Test.TestMethodAutoByName");
        query.setParameter("name", name);       
        query.setMaxResults(maxResults);

        return query.getResultList();
    }
    
    public void validateTest(TestDO testDO,List<TestPrepDO> prepTestDOList,
                             List<TestTypeOfSampleDO> typeOfSampleDOList,
                             List<TestReflexDO> testReflexDOList,
                             TestWorksheetDO worksheetDO,
                             List<TestWorksheetItemDO> itemDOList,
                             List<TestWorksheetAnalyteDO> twsaDOList,
                             List<TestAnalyteDO> analyteDOList,
                             List<TestSectionDO> sectionDOList,
                             List<TestResultDO> resultDOList) throws Exception{
        ValidationErrorsList exceptionList = new ValidationErrorsList();        
        
        boolean anaListValid, resListValid;
        
        //
        // the hashmap that contains mappings such that the key is a test analyte's
        // id and the value is the result group number that corresponds to it
        //
        HashMap<Integer,Integer> anaResGrpMap;
        
        //
        // the hashmap that contains mappings such that the key is a result group 
        // number and the value is the list of test result ids that correspond to it 
        //
        HashMap<Integer, List<Integer>> resGrpRsltMap;
               
        anaResGrpMap = new HashMap<Integer, Integer>();
        resGrpRsltMap = new HashMap<Integer, List<Integer>>();
        anaListValid = false;
        resListValid = false;
        
        validateTestDO(exceptionList, testDO);        
        if(prepTestDOList!=null)
            validateTestPrep(exceptionList,prepTestDOList);        
        if(worksheetDO!=null)
            validateTestWorksheet(exceptionList,worksheetDO);
        if(itemDOList!=null)
            validateTestWorksheetItems(exceptionList,itemDOList,worksheetDO);
        if(twsaDOList!=null) 
            validateTestWorksheetAnalytes(exceptionList, twsaDOList);
        if(analyteDOList!=null) {
            anaListValid = validateTestAnalytes(exceptionList,analyteDOList,
                                                resultDOList,anaResGrpMap);
        }
        if(sectionDOList!=null)
            validateTestSections(exceptionList,sectionDOList);
        if(resultDOList!=null) {
            resListValid = validateTestResults(exceptionList,resultDOList,
                                               typeOfSampleDOList,resGrpRsltMap);
        }
        if(testReflexDOList!=null) {
            validateTestReflex(exceptionList,testReflexDOList,anaListValid,
                               resListValid,anaResGrpMap,resGrpRsltMap);
        }
        if(typeOfSampleDOList!=null)
            validateTypeOfSample(exceptionList,typeOfSampleDOList);
     
        if(exceptionList.size() > 0)
            throw exceptionList;
    }
    

    private void validateTestDO(ValidationErrorsList exceptionList,TestDO testDO) {
        boolean checkDuplicate = true;
        List list;        
        Query query;
        Test test;
        
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
            } else if("N".equals(testDO.getIsActive())) {
                
                query = manager.createNamedQuery("TestPrep.TestPrepByPrepTestId");
                query.setParameter("testId", testDO.getId());
                list = query.getResultList();
                if(list.size() > 0) {
                    exceptionList.add(new FormErrorException("testUsedAsPrepTestException"));
                    checkDuplicate = false;
                }
                
                query = manager.createNamedQuery("TestReflex.TestReflexesByAddTestId");
                query.setParameter("testId", testDO.getId());
                list = query.getResultList();
                if(list.size() > 0) {
                    exceptionList.add(new FormErrorException("testUsedAsReflexTestException"));
                    checkDuplicate = false;
                }
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
             query = manager.createNamedQuery("Test.TestByName");
             query.setParameter("name", testDO.getName());
             list = query.getResultList();
             for(int iter = 0; iter < list.size(); iter++){
                 boolean overlap = false;
                 test = (Test)list.get(iter);
                 if(!test.getId().equals(testDO.getId())){
                     if(test.getMethodId().equals(testDO.getMethodId())){                  
                         if(test.getIsActive().equals(testDO.getIsActive())){
                             if("Y".equals(testDO.getIsActive())){
                                 exceptionList.add(new FormErrorException("testActiveException"));                                   
                                 break; 
                             }                            
                         }
                         if(test.getActiveBegin().before(testDO.getActiveEnd())&&
                                         (test.getActiveEnd().after(testDO.getActiveBegin()))){
                             overlap = true;  
                         } else if(test.getActiveBegin().before(testDO.getActiveBegin())&&
                                         (test.getActiveEnd().after(testDO.getActiveEnd()))){
                             overlap = true;  
                         } else if(test.getActiveBegin().equals(testDO.getActiveEnd())||
                                         (test.getActiveEnd().equals(testDO.getActiveBegin()))){
                             overlap = true;                  
                         } else if(test.getActiveBegin().equals(testDO.getActiveBegin())||
                                         (test.getActiveEnd().equals(testDO.getActiveEnd()))){
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

    
    private void validateTypeOfSample(ValidationErrorsList exceptionList,List<TestTypeOfSampleDO> typeOfSampleDOList){        
            for(int i = 0; i < typeOfSampleDOList.size(); i++){
                TestTypeOfSampleDO typeDO = typeOfSampleDOList.get(i);
                if(!typeDO.getDelete() && typeDO.getTypeOfSampleId()==null){
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                         TestTypeOfSampleMetaMap.getTableName()+":"+TestMeta.getTestTypeOfSample().getTypeOfSampleId()));
                }            
        }
    }
    
    private void validateTestPrep(ValidationErrorsList exceptionList,List<TestPrepDO> testPrepDOList){    
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
    
    private void validateTestReflex(ValidationErrorsList exceptionList,List<TestReflexDO> testReflexDOList,
                                    boolean anaListValid, boolean resListValid,
                                    HashMap<Integer,Integer> anaResGrpMap,
                                    HashMap<Integer, List<Integer>> resGrpRsltMap){
        TestReflexDO refDO;
        List<List<Integer>> idsList;
        List<Integer> ids;
        int i;
        String fieldName;
        
        fieldName = null;        
        idsList = new ArrayList<List<Integer>>();
        
        for(i = 0; i < testReflexDOList.size(); i++) {            
            refDO = testReflexDOList.get(i);
            if (refDO.getDelete())
                continue;

            ids = new ArrayList<Integer>();

            if (refDO.getAddTestId() != null && refDO.getTestAnalyteId() != null
                            && refDO.getTestResultId() != null) {
                ids.add(refDO.getAddTestId());
                ids.add(refDO.getTestAnalyteId());
                ids.add(refDO.getTestResultId());
            }
            try{
                if (refDO.getAddTestId() == null) {
                    fieldName = TestMeta.getTestReflex().getAddTestId();
                    throw new InconsistentException("fieldRequiredException");
                }

                if (refDO.getTestAnalyteId() == null) {
                    fieldName = TestMeta.getTestReflex().getTestAnalyteId();
                    throw new InconsistentException("fieldRequiredException");
                }

                if (refDO.getTestResultId() == null) {
                    fieldName = TestMeta.getTestReflex().getTestResultId();
                    throw new InconsistentException("fieldRequiredException");
                }

                if (refDO.getFlagsId() == null) {
                    fieldName = TestMeta.getTestReflex().getFlagsId();
                    throw new InconsistentException("fieldRequiredException");
                }

                if (!idsList.contains(ids)) {
                    idsList.add(ids);
                } else {
                    fieldName = TestMeta.getTestReflex().getAddTestId();
                    throw new InconsistentException("fieldUniqueOnlyException");
                }

                fieldName = TestMeta.getTestReflex().getTestResultId();
                validateAnalyteResultMapping(anaListValid,resListValid,anaResGrpMap,
                                             resGrpRsltMap,refDO);                
            } catch (InconsistentException ex) {
                addErrorToTableField(ex.getMessage(), TestReflexMetaMap.getTableName(),
                                     fieldName, i, exceptionList);
            } 
            
        }        
    }
    
    private void validateTestWorksheet(ValidationErrorsList exceptionList,TestWorksheetDO worksheetDO){
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
    
    private void validateTestWorksheetItems(ValidationErrorsList exceptionList,
                                           List<TestWorksheetItemDO> itemDOList,
                                           TestWorksheetDO worksheetDO) {        
        Integer bc,tc,position,batchId,totalId, formatId,fixedId,duplId;
        ArrayList<Integer> posList;
        int i, size;
        TestWorksheetItemDO currDO,prevDO;
        boolean checkPosition;
        Query query;
        String sysName,name;
        
        if(itemDOList == null)
            return;
        
        bc = null;
        tc = null;
        formatId = null;
        
        size = itemDOList.size();
        
        if (worksheetDO != null) {
            bc = worksheetDO.getBatchCapacity();
            tc = worksheetDO.getTotalCapacity();
            formatId = worksheetDO.getNumberFormatId(); 
        } else if(size > 0) {
            // 
            // if there's no data in worksheetDO it means that the user didn't 
            // specify any details about the kind of worksheet it will be and so 
            // if there are qcs present then this is an erroneous situation and
            // the errors related to the worksheet information must be added to 
            // the list of errors
            //
            worksheetDO = new TestWorksheetDO();
            validateTestWorksheet(exceptionList, worksheetDO);        
        }
        
        posList = new ArrayList<Integer>();
        checkPosition = false;

        batchId =  categoryBean.getEntryIdForSystemName("batch");
        totalId = categoryBean.getEntryIdForSystemName("total");
        fixedId = categoryBean.getEntryIdForSystemName("pos_fixed");
        duplId = categoryBean.getEntryIdForSystemName("pos_duplicate");
                
        prevDO = null;
        
        for (i = 0; i < size; i++) {
            currDO = itemDOList.get(i);
            
            if(i > 0)
                prevDO = itemDOList.get(i-1);
            
            if (currDO.getDelete()) 
                continue;
                                        
            position = currDO.getPosition();
            checkPosition = true;
            name = currDO.getQcName();
            
            if (name == null || "".equals(name)) {
                exceptionList.add(new TableFieldErrorException("fieldRequiredException",i,
                                                               TestMeta.getTestWorksheetItem().getQcName(),
                                                               TestWorksheetItemMetaMap.getTableName()));
            }
            if (currDO.getTypeId() == null) {
                exceptionList.add(new TableFieldErrorException("fieldRequiredException",i,
                                                               TestMeta.getTestWorksheetItem().getTypeId(),
                                                               TestWorksheetItemMetaMap.getTableName()));
                checkPosition = false;
            }

            if (position != null) {
                if (position <= 0) {
                    exceptionList.add(new TableFieldErrorException("posMoreThanZeroException",i,
                                                                   TestMeta.getTestWorksheetItem().getPosition(),
                                                                   TestWorksheetItemMetaMap.getTableName()));
                    checkPosition = false;
                } else if (bc != null && batchId.equals(formatId) && position > bc ) {
                    exceptionList.add(new TableFieldErrorException("posExcBatchCapacityException",i,
                                                                   TestMeta.getTestWorksheetItem().getPosition(),
                                                                   TestWorksheetItemMetaMap.getTableName()));
                    checkPosition = false;
                } else if(tc != null && totalId.equals(formatId) && position > tc){
                    exceptionList.add(new TableFieldErrorException("posExcTotalCapacityException",i,
                                                                   TestMeta.getTestWorksheetItem().getPosition(),
                                                                   TestWorksheetItemMetaMap.getTableName()));
                    checkPosition = false;
                }else {
                    if (!posList.contains(position)) {
                        posList.add(position);
                    } else {
                        exceptionList.add(new TableFieldErrorException("duplicatePosForQCsException",i,
                                                                       TestMeta.getTestWorksheetItem().getPosition(),
                                                                       TestWorksheetItemMetaMap.getTableName()));
                        checkPosition = false;
                    }
                }
            }

            if (checkPosition) {
                query = manager.createNamedQuery("Dictionary.SystemNameById");
                query.setParameter("id", currDO.getTypeId());                 
                sysName = (String)query.getSingleResult();

                if (position == null) {
                    if ("pos_duplicate".equals(sysName) || "".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException("fixedDuplicatePosException",i,
                                                                       TestMeta.getTestWorksheetItem().getPosition(),
                                                                       TestWorksheetItemMetaMap.getTableName()));
                    }
                } else {
                    if (position == 1 && "pos_duplicate".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException("posOneDuplicateException",i,
                                                                       TestMeta.getTestWorksheetItem().getTypeId(),
                                                                       TestWorksheetItemMetaMap.getTableName()));
                    } else if (!"pos_duplicate".equals(sysName) && !"pos_fixed".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException("posSpecifiedException",i,
                                                                       TestMeta.getTestWorksheetItem().getPosition(),
                                                                       TestWorksheetItemMetaMap.getTableName()));
                    }
                }
                
                if(duplicateAfterFixedOrDuplicate(currDO,prevDO,fixedId,duplId)){
                    exceptionList.add(new TableFieldErrorException("duplPosAfterFixedOrDuplPosException",i,
                                                                   TestMeta.getTestWorksheetItem().getPosition(),
                                                                   TestWorksheetItemMetaMap.getTableName()));
                }
            }
                           
        }       
    }
    
    private void validateTestWorksheetAnalytes(ValidationErrorsList exceptionList,
                                               List<TestWorksheetAnalyteDO> twsaDOList) {
      TestWorksheetAnalyteDO twsaDO;
      Integer repeat;  
      
      for(int i = 0; i < twsaDOList.size();i++) {
         twsaDO =  twsaDOList.get(i);  
         if(twsaDO.getDelete()) 
             continue;
         
         repeat = twsaDO.getRepeat();
         if(repeat != null) {            
             if(repeat < 1) {
                 exceptionList.add(new TableFieldErrorException("repeatNullForAnalyteException", i,
                                                                TestMeta.getTestWorksheetAnalyte().getRepeat(),
                                                                TestWorksheetAnalyteMetaMap.getTableName())); 
             } 
         }else {
             exceptionList.add(new TableFieldErrorException("repeatNullForAnalyteException", i,
                                                            TestMeta.getTestWorksheetAnalyte().getRepeat(),
                                                            TestWorksheetAnalyteMetaMap.getTableName()));
         }
        
      }  
        
    }
    
    private boolean validateTestAnalytes(ValidationErrorsList exceptionList,List<TestAnalyteDO> analyteDOList,
                                      List<TestResultDO> resultDOList, HashMap<Integer,Integer> anaResGrpMap){
        int i;
        TestAnalyteDO testAnalyteDO = null;
        TestResultDO resultDO = null;
        HashMap<Integer, Boolean> resultGroups;
        Integer id,rg;            

        resultGroups = new HashMap<Integer, Boolean>();
        
        
        if (resultDOList != null) {
            for (i = 0; i < resultDOList.size(); i++) {
                resultDO = resultDOList.get(i);
                resultGroups.put(resultDO.getResultGroup(), Boolean.TRUE);
            }
        }

        for (i = 0; i < analyteDOList.size(); i++) {
            testAnalyteDO = analyteDOList.get(i);
            id = testAnalyteDO.getId();
            rg = testAnalyteDO.getResultGroup();             
            if (rg != null ){
                if(!resultGroups.containsKey(rg)) {
                    exceptionList.add(new FormErrorException("emptyResultGroupException"));
                    
                    return false;
                } 
                if(id != null) {
                    anaResGrpMap.put(id, rg);
                }
            }
        }
        return true;
    }
   
   private boolean validateTestResults(ValidationErrorsList exceptionList,
                                       List<TestResultDO> resultDOList,
                                       List<TestTypeOfSampleDO> sampleTypeDOList,
                                       HashMap<Integer, List<Integer>> resGrpRsltMap) {
       TestResultDO resDO;
       Integer numId,dictId,titerId,typeId,dateId,dtId,timeId,unitId,entryId;             
       int i;                   
       String value, fieldName;       
       boolean hasDateType,resListValid;        
       NumericRange nr;
       TiterRange tr;
       HashMap<Integer,List<TiterRange>> trMap;
       HashMap<Integer,List<NumericRange>> nrMap;
       List<Integer> dictList;
       List<Integer> resIdList;
       Integer resultGroup;                                      
       
       value = null;                     
       
       dictId = categoryBean.getEntryIdForSystemName("test_res_type_dictionary");               
       numId = categoryBean.getEntryIdForSystemName("test_res_type_numeric");              
       titerId = categoryBean.getEntryIdForSystemName("test_res_type_titer");              
       dateId = categoryBean.getEntryIdForSystemName("test_res_type_date");               
       dtId = categoryBean.getEntryIdForSystemName("test_res_type_date_time");              
       timeId = categoryBean.getEntryIdForSystemName("test_res_type_time");
                    
       trMap = new HashMap<Integer, List<TiterRange>>();
       nrMap = new HashMap<Integer, List<NumericRange>>();       
       dictList = new ArrayList<Integer>();
       resultGroup = new Integer(-1);
       hasDateType = false;       
       resListValid = true;
              
       for(i = 0; i < resultDOList.size(); i++) {           
           resDO = resultDOList.get(i);               
           if(resDO.getDelete())
               continue;
               
           if(!resultGroup.equals(resDO.getResultGroup())) {
               trMap.clear();
               nrMap.clear();
               dictList.clear();
               hasDateType = false;
               resultGroup = resDO.getResultGroup();                   
           }
                  
           resIdList = resGrpRsltMap.get(resultGroup);
           if(resIdList == null) {
               resIdList = new ArrayList<Integer>();
               resGrpRsltMap.put(resultGroup, resIdList);
           }
           resIdList.add(resDO.getId());       
           
           value = resDO.getValue();               
           typeId = resDO.getTypeId();
           unitId = resDO.getUnitOfMeasureId();
           //
           // units need to be valid for every result type because
           // their use is dependent on the unit
           //
           if (!unitIsValid(unitId, sampleTypeDOList)) {
                addErrorToTestResultUnitField(i, unitId, exceptionList);
                resListValid = false;
                continue;                
           }
           
           fieldName = TestMeta.getTestResult().getValue();
           //
           // dictionary, titers, numeric require a value
           //
           if ((value == null || "".equals(value)) && (numId.equals(typeId) || titerId.equals(typeId) ||
                               dictId.equals(typeId))) {
                   addErrorToTableField("fieldRequiredException",
                                        TestResultMetaMap.getTableName(),
                                        fieldName,i,exceptionList); 
                   resListValid = false;
               continue;
           }
                                     
           try {
               if (numId.equals(typeId)) {                  
                   nr = new NumericRange(value);
                   addNumericIfNoOverLap(nrMap, unitId, nr);
               } else if (titerId.equals(typeId)) {                   
                   tr = new TiterRange(value);
                   addTiterIfNoOverLap(trMap, unitId, tr);
               } else if (dateId.equals(typeId)) {
                   TestResultValidator.validateDate(value);
                   if (hasDateType) {
                       fieldName = TestMeta.getTestResult().getTypeId();
                       throw new InconsistentException("testMoreThanOneDateTypeException");                       
                   }
                   hasDateType = true;                   
               } else if (dtId.equals(typeId)) {              
                   TestResultValidator.validateDateTime(value);
                   if (hasDateType) {
                       fieldName = TestMeta.getTestResult().getTypeId();
                       throw new InconsistentException("testMoreThanOneDateTypeException");                       
                   }                   
                   hasDateType = true;                        
               } else if (dtId.equals(typeId)) {               
                   TestResultValidator.validateDateTime(value);
                   if (hasDateType) {
                       fieldName = TestMeta.getTestResult().getTypeId();
                       throw new InconsistentException("testMoreThanOneDateTypeException");                       
                   }                   
                   hasDateType = true;                   
               } else if (timeId.equals(typeId)) {                   
                   TestResultValidator.validateTime(value);
                   if (hasDateType) {
                       fieldName = TestMeta.getTestResult().getTypeId();
                       throw new InconsistentException("testMoreThanOneDateTypeException");                       
                   }                   
                   hasDateType = true;                   
               } else if (dictId.equals(typeId)) {
                   entryId = categoryBean.getEntryIdForEntry(value);
                   if (entryId == null)
                       throw new ParseException("illegalDictEntryException");

                   if (!dictList.contains(entryId))
                       dictList.add(entryId);
                   else
                       throw new InconsistentException("testDictEntryNotUniqueException");                                              
               } else {
                   fieldName = TestMeta.getTestResult().getTypeId();
                   throw new ParseException("fieldRequiredException");
               }
           } catch (ParseException ex) {
               addErrorToTableField(ex.getMessage(),
                                    TestResultMetaMap.getTableName(),
                                    fieldName,i,exceptionList);
               resListValid = false;
           } catch (InconsistentException ex) {               
               addErrorToTableField(ex.getMessage(),
                                    TestResultMetaMap.getTableName(),
                                    fieldName,i,exceptionList);
               resListValid = false;
           }                                                
        }           
       return resListValid;
   }   

    private void validateTestSections(ValidationErrorsList exceptionList,List<TestSectionDO> sectionDOList){
        Integer defId,askId,matchId,flagId,sectId;        
        List<Integer> idList;                  
        int size,numDef,numAsk,numMatch,numBlank, iter;
        TestSectionDO secDO;
        
        size = sectionDOList.size();
                                                  
        defId = categoryBean.getEntryIdForSystemName("test_section_default");                   
        askId = categoryBean.getEntryIdForSystemName("test_section_ask");                  
        matchId = categoryBean.getEntryIdForSystemName("test_section_match");
                               
        numDef = 0 ;
        numAsk = 0;
        numMatch = 0;
        numBlank = 0;
       
        if(size > 0) {
            idList = new ArrayList<Integer>();            
            for(iter = 0; iter < size; iter++) {
                secDO = sectionDOList.get(iter); 
                if(!secDO.getDelete()) {
                    flagId = secDO.getFlagId();          
                    sectId = secDO.getSectionId();
          
                    if(sectId == null) {  
                        addErrorToTableField("fieldRequiredException", TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                    }else if(idList.contains(sectId)){                        
                        addErrorToTableField("fieldUniqueOnlyException", TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter, exceptionList);
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
                for(iter = 0; iter < size; iter++) {                       
                    addErrorToTableField("allSectCantBeBlankException", TestSectionMetaMap.getTableName(),
                                         TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                }           
            } else if(numDef > 1) {
                for(iter = 0; iter < size; iter++) {
                    secDO = sectionDOList.get(iter);  
                    flagId = secDO.getFlagId();
                    if(!secDO.getDelete() && (flagId != null)) {
                        addErrorToTableField("allSectBlankIfDefException", TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                    } 
                }
            } else if(numDef == 1 && numBlank != (size-1)) {
                for(iter = 0; iter < size; iter++) {
                    secDO = sectionDOList.get(iter);  
                    flagId = secDO.getFlagId();
                    if(!secDO.getDelete() && (flagId != null) && !defId.equals(flagId)) {
                        addErrorToTableField("allSectBlankIfDefException", TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                    } 
                }
            } else if(numMatch > 0 && numMatch != size) {
                for(iter = 0; iter < size; iter++) {
                    secDO = sectionDOList.get(iter);  
                    flagId = secDO.getFlagId();
                    if(!secDO.getDelete()) { 
                        if(flagId == null || (flagId != null && !matchId.equals(flagId))) {
                            addErrorToTableField("allSectMatchFlagException", TestSectionMetaMap.getTableName(),
                                                 TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                        }                
                    }
                }       
            } else if(numAsk > 0 && numAsk != size) {
                for(iter = 0; iter < size; iter++) {
                    secDO = sectionDOList.get(iter);  
                    flagId = secDO.getFlagId();
                    if(!secDO.getDelete()) {
                        if((flagId == null) || (flagId != null && !askId.equals(flagId))) {
                            addErrorToTableField("allSectAskFlagException", TestSectionMetaMap.getTableName(),
                                                 TestMeta.getTestSection().getFlagId(), iter, exceptionList);
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
        } else {
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
    
    private void addErrorToTableField(String error,String tableKey,String fieldName,
                                      int index,ValidationErrorsList exceptionList) {
        TableFieldErrorException exc;
        exc = new TableFieldErrorException(error, index,fieldName);
        exc.setTableKey(tableKey);
        exceptionList.add(exc); 
    }
    
    /**
     * This method finds out what the entry string for the dictionary record is 
     * that has "unitId" as its id. The method then adds an exception, with its 
     * message corresponding to "illegalUnitOfMeasureException" and the entry 
     * string attached at the end of that message, to the list of exceptions.
     */
    private void addErrorToTestResultUnitField(int index,Integer unitId,ValidationErrorsList exceptionList) {
        Query query;
        String unitText;
        
        query = manager.createNamedQuery("Dictionary.EntryById");
        query.setParameter("id", unitId);
        unitText = (String)query.getResultList().get(0);
        
        addErrorToTableField("illegalUnitOfMeasureException:"+unitText,TestResultMetaMap.getTableName(),
                             TestMeta.getTestResult().getUnitOfMeasureId(),index,exceptionList);       
    }        
               
    private void addTiterIfNoOverLap(HashMap<Integer,List<TiterRange>> trMap,
                                        Integer unitId,TiterRange tr) throws InconsistentException{ 
        TiterRange lr;
        List<TiterRange> trList;
        
        trList = trMap.get(unitId);
        if(trList != null) {
            for(int i = 0; i < trList.size(); i++) {
                lr = trList.get(i);
                if(lr.isOverlapping(tr)) 
                   throw new InconsistentException("testTiterRangeOverlapException");            
            } 
            trList.add(tr);
        } else {
            trList = new ArrayList<TiterRange>();
            trList.add(tr);
            trMap.put(unitId, trList);
        }       
    }

    private void addNumericIfNoOverLap(HashMap<Integer,List<NumericRange>> nrMap,
                                          Integer unitId, NumericRange nr) throws InconsistentException{
        NumericRange lr;
        List<NumericRange> nrList;
        
        nrList = nrMap.get(unitId);
        if(nrList != null) {
            for(int i = 0; i < nrList.size(); i++) {
                lr = nrList.get(i);
                if(lr.isOverlapping(nr)) 
                    throw new InconsistentException("testNumRangeOverlapException");            
            } 
            nrList.add(nr);
        } else {
            nrList = new ArrayList<NumericRange>();
            nrList.add(nr);
            nrMap.put(unitId, nrList);
        }
    }
    
    /**
     * This method validates whether the test result id and test analyte id in
     * "refDO" form a valid mapping such that the test result id belongs to the
     * result group that has been selected for the test analyte represented by its id
     */
    private void validateAnalyteResultMapping(boolean anaListValid, boolean resListValid,        
                                              HashMap<Integer,Integer> anaResGrpMap,    
                                              HashMap<Integer, List<Integer>> resGrpRsltMap,
                                              TestReflexDO refDO) throws InconsistentException {
        Integer rg, resId, anaId;
        List<Integer> resIdList;               
        
        resId = refDO.getTestResultId();
        anaId = refDO.getTestAnalyteId();       
        
        if(!anaListValid || !resListValid)
            return;        
                
        //
        // find the result group selected for the test analyte from anaResGrpMap
        // using its id set in refDO,
        //        
        rg = anaResGrpMap.get(anaId);
        if(rg != null) {
            //
            // if the list obtained from anaResGrpMap does not contain the 
            // test result id in refDO then that implies that this the test result
            // doesn't belong to the result group selected for the test analyte
            // and thus an exception is thrown containing this message.
            //
            resIdList = resGrpRsltMap.get(rg);
            if(resIdList == null){
              throw new InconsistentException("resultDoesntBelongToAnalyteException");
            } else if(!resIdList.contains(resId)) 
                throw new InconsistentException("resultDoesntBelongToAnalyteException");                            
        }                            
    }
    
    /**
     * This method will return true if the type specified in currDO is duplicate 
     * and if the type specified in prevDO is either fixed or duplicate and,
     * such that the position specified in prevDO is one less than the
     * position in currDO. The two integers, fixedId and duplId, are the ids of
     * the dictionary records that contain the entries for the fixed and duplicate
     * types respectively
     */
    private boolean duplicateAfterFixedOrDuplicate(TestWorksheetItemDO currDO,
                                                   TestWorksheetItemDO prevDO,
                                                   Integer fixedId, Integer duplId) {
        Integer ptId, ctId,ppos,cpos;
        
        if(prevDO == null || currDO == null)
            return false;
        
        ptId = prevDO.getTypeId();
        ctId = currDO.getTypeId();
        cpos = currDO.getPosition();
        ppos = prevDO.getPosition();
        
        if(ppos !=null && cpos !=null && ppos == cpos-1) { 
            if (duplId.equals(ctId) && (duplId.equals(ptId) || fixedId.equals(ptId))) 
                return true;        
        }
        
        return false;
    }
    
}