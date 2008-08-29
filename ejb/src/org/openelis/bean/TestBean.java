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
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleTypePrepTestListDO;
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.entity.Test;
import org.openelis.entity.TestPrep;
import org.openelis.entity.TestTypeOfSample;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestPrepMetaMap;
import org.openelis.metamap.TestTypeOfSampleMetaMap;
import org.openelis.remote.TestRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs( {@EJB(name = "ejb/SystemUser", beanInterface = SystemUserUtilLocal.class),
        @EJB(name = "ejb/Lock", beanInterface = LockLocal.class),})
@SecurityDomain("openelis")
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
                              SampleTypePrepTestListDO listDO) throws Exception {
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
             validateTestNameMethod(exceptionList,testIdNameMethodDO);
            if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
            }

            test.setName(testIdNameMethodDO.getName());
            test.setMethodId(testIdNameMethodDO.getMethodId());
            
            if(testDetailsDO!=null){
             
             validateTestDetails(exceptionList,testDetailsDO); 
             if(exceptionList.size() > 0){
                   throw (RPCException)exceptionList.get(0);
               } 
             
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

            
            if (listDO != null){
                List<TestPrepDO> testPrepDOList = listDO.getTestPrepDOList();                
                
                 if(testPrepDOList!=null){      
                     exceptionList = new ArrayList<Exception>();
                     validateTestPrep(exceptionList,testPrepDOList);
                     if(exceptionList.size() > 0){
                         throw (RPCException)exceptionList.get(0);
                     }
                     
                     for(int i = 0; i < testPrepDOList.size() ; i++){
                         TestPrepDO testPrepDO = testPrepDOList.get(i);
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
                
                 List<TestTypeOfSampleDO> typeOfSampleDOList = listDO.getTypeOfSampleDOList();
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
    
    public SampleTypePrepTestListDO getSampleTypesAndPrepTests(Integer testId) {
        Query query = manager.createNamedQuery("TestTypeOfSample.TestTypeOfSample");
        query.setParameter("id", testId);
        List<TestTypeOfSampleDO> testTypeOfSampleDOList = (List<TestTypeOfSampleDO>)query.getResultList();

        query = manager.createNamedQuery("TestPrep.TestPrep");
        query.setParameter("id", testId);
        List<TestPrepDO> testPrepDOList = (List<TestPrepDO>)query.getResultList();

        SampleTypePrepTestListDO sampleTypePrepTestListDO = new SampleTypePrepTestListDO();
        sampleTypePrepTestListDO.setTestPrepDOList(testPrepDOList);
        sampleTypePrepTestListDO.setTypeOfSampleDOList(testTypeOfSampleDOList);

        return sampleTypePrepTestListDO;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(TestMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + TestMeta.getId()
                     + ", "
                     + TestMeta.getName()
                     + ") ");

        qb.addWhere(fields);

        qb.setOrderBy(TestMeta.getName());

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
        //Query query = manager.createNamedQuery("Test.IdName");
        Query query = manager.createNamedQuery("Test.Names");
        List preptestList = query.getResultList();
        return preptestList;
    }
    

    public List validateForAdd(TestIdNameMethodIdDO testIdNameMethodDO,
                               TestDetailsDO testDetailsDO,
                               SampleTypePrepTestListDO listDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateTestNameMethod(exceptionList, testIdNameMethodDO);
        validateTestDetails(exceptionList, testDetailsDO);
        validateTypeOfSample(exceptionList,listDO.getTypeOfSampleDOList());
        validateTestPrep(exceptionList,listDO.getTestPrepDOList());
        return exceptionList;
    }

    public List validateForUpdate(TestIdNameMethodIdDO testIdNameMethodDO,
                                  TestDetailsDO testDetailsDO,
                                  SampleTypePrepTestListDO listDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateTestNameMethod(exceptionList, testIdNameMethodDO);
        validateTestDetails(exceptionList, testDetailsDO);
        if(listDO!=null){
         validateTypeOfSample(exceptionList,listDO.getTypeOfSampleDOList());
         validateTestPrep(exceptionList,listDO.getTestPrepDOList());
        }
        return exceptionList;
    }
    

    private void validateTestNameMethod(List<Exception> exceptionList,
                                        TestIdNameMethodIdDO testIdNameMethodIdDO) {
        if (testIdNameMethodIdDO.getName() == null || "".equals(testIdNameMethodIdDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getName()));
        }

        if (testIdNameMethodIdDO.getMethodId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getMethodId()));
        }
    }

    private void validateTestDetails(List<Exception> exceptionList,
                                     TestDetailsDO testDetailsDO) {
       if(testDetailsDO !=null){ 
        if (testDetailsDO.getDescription() == null || "".equals(testDetailsDO.getDescription())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      "details:" + TestMeta.getDescription()));
        }

        if (testDetailsDO.getActiveBegin() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      "details:" + TestMeta.getActiveBegin()));
        }

        if (testDetailsDO.getActiveEnd() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      "details:" + TestMeta.getActiveEnd()));
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
            for(int i = 0; i < testPrepDOList.size(); i++){
                TestPrepDO prepDO = testPrepDOList.get(i);
                if(prepDO.getPrepTestId()==null){
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                          TestPrepMetaMap.getTableName()+":"+TestMeta.getTestPrep().getPrepTestId()));
                }
            }       
    }

}
