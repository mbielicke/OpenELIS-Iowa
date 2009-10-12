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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestViewDO;
import org.openelis.domain.TestWorksheetAnalyteDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
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
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.CategoryLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.TestLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.metamap.TestSectionMetaMap;
import org.openelis.remote.TestRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.NumericRange;
import org.openelis.utilcommon.TestResultValidator;
import org.openelis.utilcommon.TiterRange;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestBean implements TestRemote, TestLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    @Resource
    private SessionContext           ctx;

    @EJB
    private LockLocal                lockBean;

    @EJB
    private CategoryLocal            categoryBean;

    private static final TestMetaMap TestMeta = new TestMetaMap();

    private static Integer           testRefTableId;

    public TestBean() {
        testRefTableId = ReferenceTableCache.getReferenceTable("test");
    }

    public TestViewDO fetchById(Integer testId) {
        Query query = manager.createNamedQuery("Test.Test");
        query.setParameter("id", testId);
        TestViewDO testDO = (TestViewDO)query.getSingleResult();
        return testDO;
    }

    public List<TestPrepViewDO> getTestPreps(Integer testId) {
        Query query = manager.createNamedQuery("TestPrep.TestPrep");
        query.setParameter("id", testId);
        List<TestPrepViewDO> testPrepDOList = (List<TestPrepViewDO>)query.getResultList();
        return testPrepDOList;
    }

    public List<TestReflexViewDO> getTestReflexes(Integer testId) {
        Query query = manager.createNamedQuery("TestReflex.TestReflexViewDOList");
        query.setParameter("testId", testId);
        List<TestReflexViewDO> testRefDOList = query.getResultList();
        return testRefDOList;
    }
    
    public TestWorksheetViewDO getTestWorksheet(Integer testId) {
        Query query = manager.createNamedQuery("TestWorksheet.TestWorksheetDOByTestId");
        query.setParameter("testId", testId);
        TestWorksheetViewDO worksheetDO = null;
        try {
            worksheetDO = (TestWorksheetViewDO)query.getSingleResult();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
        return worksheetDO;
    }

    public ArrayList<TestWorksheetItemDO> getTestWorksheetItems(Integer worksheetId) {
        Query query = manager.createNamedQuery("TestWorksheetItem.TestWorksheetItemsByTestWSId");
        query.setParameter("testWorksheetId", worksheetId);
        ArrayList<TestWorksheetItemDO> list = (ArrayList<TestWorksheetItemDO>)query.getResultList();
        return list;
    }

    public ArrayList<TestWorksheetAnalyteViewDO> getTestWorksheetAnalytes(Integer testId) {
        Query query = manager.createNamedQuery("TestWorksheetAnalyte.TestWorksheetAnalyteDOByTestId");
        query.setParameter("testId", testId);
        ArrayList<TestWorksheetAnalyteViewDO> list = (ArrayList<TestWorksheetAnalyteViewDO>)query.getResultList();
        return list;
    }

    public ArrayList<ArrayList<TestAnalyteViewDO>> fetchTestAnalytesById(Integer testId)
                                                                                    throws Exception {
        Query query;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        ArrayList<TestAnalyteViewDO> list;
        int i, j, rg;
        TestAnalyteViewDO ado;
        ArrayList<TestAnalyteViewDO> ar;

        j = -1;
        ar = null;
        grid = null;
        query = manager.createNamedQuery("TestAnalyte.TestAnalyteDOListByTestId");

        query.setParameter("testId", testId);
        try {
            list = (ArrayList<TestAnalyteViewDO>)query.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

        if (list == null || list.size() == 0)
            throw new NotFoundException();

        grid = new ArrayList<ArrayList<TestAnalyteViewDO>>();

        for (i = 0; i < list.size(); i++ ) {
            ado = list.get(i);
            rg = ado.getRowGroup();

            if (j != rg) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(ado);
                grid.add(ar);
                j = rg;
                continue;
            }
            if ("N".equals(ado.getIsColumn())) {
                ar = new ArrayList<TestAnalyteViewDO>(1);
                ar.add(ado);
                grid.add(ar);
                continue;
            }

            ar.add(ado);
        }

        return grid;
    }

    public ArrayList<TestSectionViewDO> getTestSections(Integer testId) throws Exception {
        Query query = manager.createNamedQuery("TestSection.TestSectionsByTestId");
        query.setParameter("testId", testId);
        List<TestSectionViewDO> list = query.getResultList();

        if (list == null || list.size() == 0)
            throw new NotFoundException("Test has no sections assigned to it.");

        return (ArrayList<TestSectionViewDO>)list;
    }

    /**
     * This method returns the data for all test results belonging to a test
     * with id "testId". The test results are arranged by sequentially
     * increasing result group numbers.
     */
    public List<List<TestResultDO>> getTestResults(Integer testId) {
        List<List<TestResultDO>> listCollection;
        List<TestResultDO> list;
        TestResultDO resDO;
        Integer typeId, val, resultGroup;
        String sysName, entry;
        Query query;
        Iterator iter;

        list = null;
        listCollection = new ArrayList<List<TestResultDO>>();
        resultGroup = 1;
        while (resultGroup != null) {
            query = manager.createNamedQuery("TestResult.TestResultDOList");
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", resultGroup);

            try {
                list = query.getResultList();
                if (list.size() == 0) {
                    resultGroup = null;
                    break;
                }
                for (iter = list.iterator(); iter.hasNext();) {
                    resDO = (TestResultDO)iter.next();
                    typeId = resDO.getTypeId();
                    query = manager.createNamedQuery("Dictionary.SystemNameById");
                    query.setParameter("id", typeId);
                    sysName = (String)query.getResultList().get(0);
                    if ("test_res_type_dictionary".equals(sysName)) {
                        val = Integer.parseInt(resDO.getValue());
                        query = manager.createNamedQuery("Dictionary.EntryById");
                        query.setParameter("id", val);
                        entry = (String)query.getResultList().get(0);
                        resDO.setValue(entry);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            listCollection.add(list);
            resultGroup++ ;
        }
        return listCollection;
    }
    
    public ArrayList<TestMethodViewDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;        
        QueryBuilderV2 builder;
        List returnList;
        
        builder = new QueryBuilderV2();
        builder.setMeta(TestMeta);

        builder.setSelect("distinct new org.openelis.domain.TestMethodViewDO(" + TestMeta.getId() +
                     ", " + (TestMeta.getName() + ", " + TestMeta.getDescription() +
                     ", " + TestMeta.getMethod().getId() +
                     ", " + TestMeta.getMethod().getName() +
                     ", " + TestMeta.getMethod().getDescription()) + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(TestMeta.getName() + ", " + TestMeta.getMethod().getName());
        
        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        QueryBuilderV2.setQueryParams(query, fields);

        returnList = query.getResultList();
        if (returnList.isEmpty())
            throw new NotFoundException();
        returnList = (ArrayList<TestMethodViewDO>)DataBaseUtil.subList(returnList, first, max);
        if (returnList == null)
            throw new LastPageException();        
        return (ArrayList<TestMethodViewDO>)returnList;
    }

    public List getTestAutoCompleteByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Test.TestMethodAutoByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }
    
    public List getTestWithActiveAutoCompleteByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Test.TestMethodActiveAutoByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    public List getTestAutoCompleteByNameSampleItemType(String name,
                                                        Integer sampleItemType,
                                                        int maxResults) {
        Query query = manager.createNamedQuery("Test.TestMethodAutoByNameSampleItemType");
        query.setParameter("name", name);
        query.setParameter("typeId", sampleItemType);
        query.setMaxResults(maxResults);

        List testList = query.getResultList();
        
        /*
        for(int i=0; i<testList.size(); i++){
            SampleTestMethodDO testDO = (SampleTestMethodDO)testList.get(i);
            // query for test sections
            try {
                testDO.setSections(getTestSections(testDO.getTest().getId()));

            } catch (Exception e) {
                testDO.setSections(new ArrayList<TestSectionViewDO>());
            }
            
            //query for pre tests
            testDO.setPrepTests((ArrayList<TestPrepDO>)getTestPreps(testDO.getTest().getId()));
        }*/
        
        return testList;
    }

    public void add(TestDO testDO) throws Exception {
        Test test;

        checkSecurity(ModuleFlags.ADD);
        
        manager.setFlushMode(FlushModeType.COMMIT);

        test = new Test();

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

        manager.persist(test);

        testDO.setId(test.getId());
    }

    @RolesAllowed("test-update")
    public void update(TestDO testDO) throws Exception {
        Test test;

        if(!testDO.isChanged()) {            
            lockBean.giveUpLock(testRefTableId, testDO.getId());
            return;        
        }
        
        checkSecurity(ModuleFlags.UPDATE);
        
        lockBean.validateLock(testRefTableId, testDO.getId());
        
        manager.setFlushMode(FlushModeType.COMMIT);
        test = manager.find(Test.class, testDO.getId());

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

        lockBean.giveUpLock(testRefTableId, testDO.getId());

    }

    public void addTestSection(TestSectionViewDO tsDO) throws Exception {
        TestSection ts;

        manager.setFlushMode(FlushModeType.COMMIT);

        ts = new TestSection();

        ts.setFlagId(tsDO.getFlagId());
        ts.setSectionId(tsDO.getSectionId());
        ts.setTestId(tsDO.getTestId());

        manager.persist(ts);
        tsDO.setId(ts.getId());
    }

    public void updateTestSection(TestSectionViewDO tsDO) throws Exception {
        TestSection ts;

        if(!tsDO.isChanged())
            return;            
        
        manager.setFlushMode(FlushModeType.COMMIT);

        ts = manager.find(TestSection.class, tsDO.getId());

        ts.setFlagId(tsDO.getFlagId());
        ts.setSectionId(tsDO.getSectionId());
        ts.setTestId(tsDO.getTestId());
    }

    public void deleteTestSection(TestSectionViewDO tsDO) throws Exception {
        TestSection ts;

        manager.setFlushMode(FlushModeType.COMMIT);

        ts = manager.find(TestSection.class, tsDO.getId());

        if (ts != null)
            manager.remove(ts);

    }

    public void validate(TestViewDO testDO,
                             List<TestSectionViewDO> sections,
                             List<TestTypeOfSampleDO> sampleTypes,
                             ArrayList<ArrayList<TestAnalyteViewDO>> analytes,
                             ArrayList<ArrayList<TestResultDO>> results,
                             ArrayList<TestPrepViewDO> prepTests,
                             ArrayList<TestReflexViewDO> reflexTests,
                             TestWorksheetViewDO worksheet, 
                             ArrayList<TestWorksheetItemDO> items,
                             ArrayList<TestWorksheetAnalyteViewDO> wsanalytes) throws Exception {

        ValidationErrorsList exceptionList;
        HashMap<Integer, Integer> anaResGrpMap;
        HashMap<Integer, List<Integer>> resGrpRsltMap;
        boolean anaListValid, resListValid;
        
        exceptionList = new ValidationErrorsList();

        anaResGrpMap = new HashMap<Integer, Integer>();
        resGrpRsltMap = new HashMap<Integer, List<Integer>>();
        
        validateTest(exceptionList, testDO);
        validateSections(exceptionList, sections);
        validateTypeOfSample(exceptionList, sampleTypes);
        anaListValid = validateAnalyte(exceptionList,analytes,results,anaResGrpMap);
        resListValid = validateResult(exceptionList,results,sampleTypes,resGrpRsltMap);
        validatePrep(exceptionList, prepTests);
        validateReflex(exceptionList,reflexTests,anaListValid,resListValid,
                           anaResGrpMap,resGrpRsltMap);
        validateWorksheet(exceptionList, worksheet);
        validateWorksheetItems(exceptionList, items, worksheet);
        validateWorksheetAnalytes(exceptionList,wsanalytes);
        if (exceptionList.size() > 0)
            throw exceptionList;

    }
    
    public ArrayList<TestTypeOfSampleDO> fetchSampleTypesById(Integer testId) throws Exception {
        Query query;
        ArrayList<TestTypeOfSampleDO> sampleTypeList;

        query = manager.createNamedQuery("TestTypeOfSample.TestTypeOfSample");
        query.setParameter("id", testId);
        sampleTypeList = (ArrayList<TestTypeOfSampleDO>)query.getResultList();

        if (sampleTypeList == null || sampleTypeList.size() == 0)
            throw new NotFoundException();

        return sampleTypeList;
    }

    public void addSampleType(TestTypeOfSampleDO sampleTypeDO) throws Exception {
        TestTypeOfSample sampleType;
        manager.setFlushMode(FlushModeType.COMMIT);

        sampleType = new TestTypeOfSample();

        sampleType.setTestId(sampleTypeDO.getTestId());
        sampleType.setTypeOfSampleId(sampleTypeDO.getTypeOfSampleId());
        sampleType.setUnitOfMeasureId(sampleTypeDO.getUnitOfMeasureId());

        manager.persist(sampleType);
        sampleTypeDO.setId(sampleType.getId());
    }

    public void deleteSampleType(TestTypeOfSampleDO sampleTypeDO) throws Exception {
        TestTypeOfSample sampleType;
        manager.setFlushMode(FlushModeType.COMMIT);

        sampleType = manager.find(TestTypeOfSample.class, sampleTypeDO.getId());
        if (sampleType != null)
            manager.remove(sampleType);
    }

    public void updateSampleType(TestTypeOfSampleDO sampleTypeDO) throws Exception {
        TestTypeOfSample sampleType;
    
        if(!sampleTypeDO.isChanged())
            return;       
        
        manager.setFlushMode(FlushModeType.COMMIT);
    
        sampleType = manager.find(TestTypeOfSample.class, sampleTypeDO.getId());

        sampleType.setTestId(sampleTypeDO.getTestId());
        sampleType.setTypeOfSampleId(sampleTypeDO.getTypeOfSampleId());
        sampleType.setUnitOfMeasureId(sampleTypeDO.getUnitOfMeasureId());
    }

    public void updateTestAnalyte(TestAnalyteDO analyteDO) throws Exception {
        TestAnalyte analyte;
        
        if(!analyteDO.isChanged())
            return;        
        
        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = manager.find(TestAnalyte.class, analyteDO.getId());
        analyte.setRowGroup(analyteDO.getRowGroup());
        analyte.setAnalyteId(analyteDO.getAnalyteId());
        analyte.setIsReportable(analyteDO.getIsReportable());
        analyte.setResultGroup(analyteDO.getResultGroup());
        analyte.setScriptletId(analyteDO.getScriptletId());
        analyte.setSortOrder(analyteDO.getSortOrder());
        analyte.setTestId(analyteDO.getTestId());
        analyte.setTypeId(analyteDO.getTypeId());
        analyte.setIsColumn(analyteDO.getIsColumn());
    }

    public void addTestAnalyte(TestAnalyteDO analyteDO) throws Exception {
        TestAnalyte analyte;

        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = new TestAnalyte();

        analyte.setRowGroup(analyteDO.getRowGroup());
        analyte.setAnalyteId(analyteDO.getAnalyteId());
        analyte.setIsReportable(analyteDO.getIsReportable());
        analyte.setResultGroup(analyteDO.getResultGroup());
        analyte.setScriptletId(analyteDO.getScriptletId());
        analyte.setSortOrder(analyteDO.getSortOrder());
        analyte.setTestId(analyteDO.getTestId());
        analyte.setTypeId(analyteDO.getTypeId());
        analyte.setIsColumn(analyteDO.getIsColumn());

        manager.persist(analyte);
        analyteDO.setId(analyte.getId());

    }

    public void deleteTestAnalyte(TestAnalyteDO analyteDO) throws Exception {
        TestAnalyte analyte;
        manager.setFlushMode(FlushModeType.COMMIT);

        analyte = manager.find(TestAnalyte.class, analyteDO.getId());
        if (analyte != null)
            manager.remove(analyte);

    }

    public ArrayList<ArrayList<TestResultDO>> fetchTestResultsById(Integer testId) throws Exception {
        ArrayList<ArrayList<TestResultDO>> listCollection;
        ArrayList<TestResultDO> list;
        TestResultDO resDO;
        Integer typeId, val, resultGroup;
        String sysName, entry;
        Query query;
        Iterator iter;

        list = null;
        listCollection = new ArrayList<ArrayList<TestResultDO>>();
        resultGroup = 1;
        while (resultGroup != null) {
            query = manager.createNamedQuery("TestResult.TestResultDOList");
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", resultGroup);

            try {
                list = (ArrayList<TestResultDO>)query.getResultList();
                if (list.size() == 0) {
                    resultGroup = null;
                    break;
                }
                for (iter = list.iterator(); iter.hasNext();) {
                    resDO = (TestResultDO)iter.next();
                    typeId = resDO.getTypeId();
                    query = manager.createNamedQuery("Dictionary.SystemNameById");
                    query.setParameter("id", typeId);
                    sysName = (String)query.getResultList().get(0);
                    if ("test_res_type_dictionary".equals(sysName)) {
                        val = Integer.parseInt(resDO.getValue());
                        query = manager.createNamedQuery("Dictionary.EntryById");
                        query.setParameter("id", val);
                        entry = (String)query.getResultList().get(0);
                        resDO.setValue(entry);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            listCollection.add(list);
            resultGroup++ ;
        }

        if (listCollection == null || listCollection.size() == 0)
            throw new NotFoundException();

        return listCollection;
    }

    public void addTestResult(TestResultDO testResultDO) throws Exception {
        TestResult testResult;

        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = new TestResult();

        testResult.setFlagsId(testResultDO.getFlagsId());
        testResult.setResultGroup(testResultDO.getResultGroup());
        testResult.setRoundingMethodId(testResultDO.getRoundingMethodId());
        testResult.setSignificantDigits(testResultDO.getSignificantDigits());
        testResult.setSortOrder(testResultDO.getSortOrder());
        testResult.setTestId(testResultDO.getTestId());
        testResult.setTypeId(testResultDO.getTypeId());
        testResult.setUnitOfMeasureId(testResultDO.getUnitOfMeasureId());
        testResult.setValue(testResultDO.getValue());

        manager.persist(testResult);
        testResultDO.setId(testResult.getId());

    }

    public void deleteTestResult(TestResultDO deletedAt) throws Exception {
        TestResult testResult;

        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = manager.find(TestResult.class, deletedAt.getId());

        if (testResult != null)
            manager.remove(testResult);
    }

    public void updateTestResult(TestResultDO testResultDO) throws Exception {
        TestResult testResult;

        if(!testResultDO.isChanged())
            return;               
        
        manager.setFlushMode(FlushModeType.COMMIT);

        testResult = manager.find(TestResult.class, testResultDO.getId());

        testResult.setFlagsId(testResultDO.getFlagsId());
        testResult.setResultGroup(testResultDO.getResultGroup());
        testResult.setRoundingMethodId(testResultDO.getRoundingMethodId());
        testResult.setSignificantDigits(testResultDO.getSignificantDigits());
        testResult.setSortOrder(testResultDO.getSortOrder());
        testResult.setTestId(testResultDO.getTestId());
        testResult.setTypeId(testResultDO.getTypeId());
        testResult.setUnitOfMeasureId(testResultDO.getUnitOfMeasureId());
        testResult.setValue(testResultDO.getValue());

    }

    public ArrayList<TestPrepViewDO> fetchPrepTestsById(Integer testId) throws Exception {
        Query query = manager.createNamedQuery("TestPrep.TestPrep");
        query.setParameter("id", testId);
        ArrayList<TestPrepViewDO> testPrepDOList = (ArrayList<TestPrepViewDO>)query.getResultList();
        return testPrepDOList;
    }

    public void addPrepTest(TestPrepViewDO prepTestDO) throws Exception {
        TestPrep prepTest;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        prepTest = new TestPrep();

        prepTest.setIsOptional(prepTestDO.getIsOptional());
        prepTest.setPrepTestId(prepTestDO.getPrepTestId());
        prepTest.setTestId(prepTestDO.getTestId());

        manager.persist(prepTest);

        prepTestDO.setId(prepTest.getId());

    }

    public void deletePrepTest(TestPrepViewDO deletedAt) throws Exception {
        TestPrep prepTest;

        manager.setFlushMode(FlushModeType.COMMIT);

        prepTest = manager.find(TestPrep.class, deletedAt.getId());

        if (prepTest != null)
            manager.remove(prepTest);

    }

    public void updatePrepTest(TestPrepViewDO prepTestDO) throws Exception {
        TestPrep prepTest;

        if(!prepTestDO.isChanged())
            return;               
                
        manager.setFlushMode(FlushModeType.COMMIT);

        prepTest = manager.find(TestPrep.class, prepTestDO.getId());

        prepTest.setIsOptional(prepTestDO.getIsOptional());
        prepTest.setPrepTestId(prepTestDO.getPrepTestId());
        prepTest.setTestId(prepTestDO.getTestId());
    }

    public ArrayList<TestReflexViewDO> fetchReflexTestsById(Integer testId) throws Exception {
        Query query = manager.createNamedQuery("TestReflex.TestReflexViewDOList");
        query.setParameter("testId", testId);
        ArrayList<TestReflexViewDO> testRefDOList = (ArrayList<TestReflexViewDO>)query.getResultList();
        return testRefDOList;
    }

    public void addReflexTest(TestReflexViewDO reflexTest) throws Exception {
        TestReflex testReflex;

        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = new TestReflex();

        testReflex.setTestId(reflexTest.getTestId());
        testReflex.setAddTestId(reflexTest.getAddTestId());
        testReflex.setTestAnalyteId(reflexTest.getTestAnalyteId());
        testReflex.setTestResultId(reflexTest.getTestResultId());
        testReflex.setFlagsId(reflexTest.getFlagsId());

        manager.persist(testReflex);

        reflexTest.setId(testReflex.getId());
    }

    public void deleteReflexTest(TestReflexViewDO deletedAt) throws Exception {
        TestReflex testReflex;

        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = manager.find(TestReflex.class, deletedAt.getId());

        if (testReflex != null)
            manager.remove(testReflex);
    }

    public void updateReflexTest(TestReflexViewDO testReflexDO) throws Exception {
        TestReflex testReflex;

        if(!testReflexDO.isChanged())
            return;               
        
        manager.setFlushMode(FlushModeType.COMMIT);

        testReflex = manager.find(TestReflex.class, testReflexDO.getId());

        testReflex.setTestId(testReflexDO.getTestId());
        testReflex.setAddTestId(testReflexDO.getAddTestId());
        testReflex.setTestAnalyteId(testReflexDO.getTestAnalyteId());
        testReflex.setTestResultId(testReflexDO.getTestResultId());
        testReflex.setFlagsId(testReflexDO.getFlagsId());

    }
    
    public void addTestWorksheet(TestWorksheetViewDO worksheet) throws Exception {
        TestWorksheet testWorksheet;      
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheet = new TestWorksheet();
        
        testWorksheet.setBatchCapacity(worksheet.getBatchCapacity());
        testWorksheet.setFormatId(worksheet.getFormatId());
        testWorksheet.setScriptletId(worksheet.getScriptletId());
        testWorksheet.setTestId(worksheet.getTestId());
        testWorksheet.setTotalCapacity(worksheet.getTotalCapacity());
        
        manager.persist(testWorksheet);

        worksheet.setId(testWorksheet.getId());
    }
    
    public void updateTestWorksheet(TestWorksheetViewDO worksheet) throws Exception {
        TestWorksheet testWorksheet;
        
        if(!worksheet.isChanged())
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheet = manager.find(TestWorksheet.class, worksheet.getId());
        
        testWorksheet.setBatchCapacity(worksheet.getBatchCapacity());
        testWorksheet.setFormatId(worksheet.getFormatId());
        testWorksheet.setScriptletId(worksheet.getScriptletId());
        testWorksheet.setTestId(worksheet.getTestId());
        testWorksheet.setTotalCapacity(worksheet.getTotalCapacity());                        
    }

    public void addTestWorksheetItem(TestWorksheetItemDO item) throws Exception {
        TestWorksheetItem testWorksheetItem;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = new TestWorksheetItem();
        
        testWorksheetItem.setPosition(item.getPosition());
        testWorksheetItem.setQcName(item.getQcName());
        testWorksheetItem.setTestWorksheetId(item.getTestWorksheetId());
        testWorksheetItem.setTypeId(item.getTypeId()); 
        
        manager.persist(testWorksheetItem);
        
        item.setId(testWorksheetItem.getId());        
    }

    public void deleteTestWorksheetItem(TestWorksheetItemDO deletedItemAt) throws Exception {
        TestWorksheetItem testWorksheetItem;       
            
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = manager.find(TestWorksheetItem.class, deletedItemAt.getId());
        
        if(testWorksheetItem != null)
            manager.remove(testWorksheetItem);
        
    }

    public void updateTestWorksheetItem(TestWorksheetItemDO item) throws Exception {
        TestWorksheetItem testWorksheetItem;
       
        if(!item.isChanged())
            return;
            
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheetItem = manager.find(TestWorksheetItem.class, item.getId());
        
        testWorksheetItem.setPosition(item.getPosition());
        testWorksheetItem.setQcName(item.getQcName());
        testWorksheetItem.setTestWorksheetId(item.getTestWorksheetId());
        testWorksheetItem.setTypeId(item.getTypeId());                
        
    }
    
    public void addTestWorksheetAnalyte(TestWorksheetAnalyteViewDO analyteDO) throws Exception {
        TestWorksheetAnalyte analyte;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = new TestWorksheetAnalyte();
        
        analyte.setFlagId(analyteDO.getFlagId());
        analyte.setRepeat(analyteDO.getRepeat());
        analyte.setTestAnalyteId(analyteDO.getTestAnalyteId());
        analyte.setTestId(analyteDO.getTestId());
        
        manager.persist(analyte);
        
        analyteDO.setId(analyte.getId());
    }

    public void deleteTestWorksheetAnalyte(TestWorksheetAnalyteViewDO deletedAnalyteAt) throws Exception {
        TestWorksheetAnalyte analyte;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = manager.find(TestWorksheetAnalyte.class, deletedAnalyteAt.getId());
        
        if(analyte != null)
            manager.remove(analyte);
    }

    public void updateTestWorksheetAnalyte(TestWorksheetAnalyteViewDO analyteDO) throws Exception {
        TestWorksheetAnalyte analyte;
        
        if(!analyteDO.isChanged()) 
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        analyte = manager.find(TestWorksheetAnalyte.class, analyteDO.getId());
        
        analyte.setFlagId(analyteDO.getFlagId());
        analyte.setRepeat(analyteDO.getRepeat());
        analyte.setTestAnalyteId(analyteDO.getTestAnalyteId());
        analyte.setTestId(analyteDO.getTestId());        
    }

    private void validateTest(ValidationErrorsList exceptionList, TestDO testDO) {
        boolean checkDuplicate = true;
        List list;
        Query query;
        Test test;

        if (testDO.getName() == null || "".equals(testDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException", TestMeta.getName()));
            checkDuplicate = false;
        }

        if (testDO.getMethodId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getMethodId()));
            checkDuplicate = false;
        }

        if (testDO.getDescription() == null || "".equals(testDO.getDescription())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getDescription()));
            checkDuplicate = false;
        }

        if (testDO.getIsActive() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getIsActive()));
            checkDuplicate = false;
        } else if ("N".equals(testDO.getIsActive())) {

            query = manager.createNamedQuery("TestPrep.TestPrepByPrepTestId");
            query.setParameter("testId", testDO.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FormErrorException("testUsedAsPrepTestException"));
                checkDuplicate = false;
            }

            query = manager.createNamedQuery("TestReflex.TestReflexesByAddTestId");
            query.setParameter("testId", testDO.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FormErrorException("testUsedAsReflexTestException"));
                checkDuplicate = false;
            }
        }

        if (testDO.getIsReportable() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getIsReportable()));
        }        
        
        if(testDO.getTimeTaMax() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTaMax()));
        }
        
        if(testDO.getTimeTransit() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTransit()));
        }
        
        if(testDO.getTimeTaAverage()  == null) {            
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTaAverage()));
        }
        
        if(testDO.getTimeHolding()  == null) {            
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeHolding()));
        }
        
        if(testDO.getTimeTaWarning()  == null) {            
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTaWarning()));
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

        if (checkDuplicate) {
            if (testDO.getActiveEnd().before(testDO.getActiveBegin())) {
                exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));
                checkDuplicate = false;
            }
        }

        if (checkDuplicate) {
            query = manager.createNamedQuery("Test.TestByName");
            query.setParameter("name", testDO.getName());
            list = query.getResultList();
            for (int iter = 0; iter < list.size(); iter++ ) {
                boolean overlap = false;
                test = (Test)list.get(iter);
                if ( !test.getId().equals(testDO.getId())) {
                    if (test.getMethodId().equals(testDO.getMethodId())) {
                        if (test.getIsActive().equals(testDO.getIsActive())) {
                            if ("Y".equals(testDO.getIsActive())) {
                                exceptionList.add(new FormErrorException("testActiveException"));
                                break;
                            }
                        }
                        if (test.getActiveBegin().before(testDO.getActiveEnd()) &&
                            (test.getActiveEnd().after(testDO.getActiveBegin()))) {
                            overlap = true;
                        } else if (test.getActiveBegin().before(testDO.getActiveBegin()) &&
                                   (test.getActiveEnd().after(testDO.getActiveEnd()))) {
                            overlap = true;
                        } else if (test.getActiveBegin().equals(testDO.getActiveEnd()) ||
                                   (test.getActiveEnd().equals(testDO.getActiveBegin()))) {
                            overlap = true;
                        } else if (test.getActiveBegin().equals(testDO.getActiveBegin()) ||
                                   (test.getActiveEnd().equals(testDO.getActiveEnd()))) {
                            overlap = true;
                        }

                        if (overlap) {
                            exceptionList.add(new FormErrorException("testTimeOverlapException"));
                        }
                    }
                }
            }
        }
    }

    private void validateTypeOfSample(ValidationErrorsList exceptionList,
                                      List<TestTypeOfSampleDO> typeOfSampleDOList) {
        TestTypeOfSampleDO typeDO;
        TableFieldErrorException exc;

        if (typeOfSampleDOList == null)
            return;

        for (int i = 0; i < typeOfSampleDOList.size(); i++ ) {
            typeDO = typeOfSampleDOList.get(i);
            if (typeDO.getTypeOfSampleId() == null) {
                exc = new TableFieldErrorException("fieldRequiredException", i,
                                                   TestMeta.getTestTypeOfSample()
                                                           .getTypeOfSampleId());
                exc.setTableKey("sampleTypeTable");
                exceptionList.add(exc);
            }

        }
    }

    private boolean validateAnalyte(ValidationErrorsList exceptionList,
                                     ArrayList<ArrayList<TestAnalyteViewDO>> grid,
                                     ArrayList<ArrayList<TestResultDO>> results,
                                     HashMap<Integer, Integer> anaResGrpMap) {
        int i, j;
        boolean valid;        
        List<TestAnalyteViewDO> list;
        TestAnalyteViewDO anaDO;
        GridFieldErrorException exc;  
        Integer rg;
        
        valid = true;

        for (i = 0; i < grid.size(); i++ ) {
            list = grid.get(i);
            
            for (j = 0; j < list.size(); j++ ) {
                anaDO = list.get(j);
                rg = anaDO.getResultGroup();
                if(j == 0)
                    anaResGrpMap.put(anaDO.getId(), rg);
                
                if (anaDO.getAnalyteId() == null) {
                    exc = new GridFieldErrorException("fieldRequiredException", i, j,
                                                      TestMeta.getTestAnalyte().getAnalyteId(),
                                                      "analyteTable");
                    exceptionList.add(exc);
                    valid = false;
                }
                if (anaDO.getTypeId() == null) {
                    exc = new GridFieldErrorException("analyteTypeRequiredException", i, j,
                                                      TestMeta.getTestAnalyte().getTypeId(),
                                                      "analyteTable");
                    exceptionList.add(exc);
                    valid = false;
                }
                if (rg == null) {
                    exc = new GridFieldErrorException("fieldRequiredException", i, j,
                                                      TestMeta.getTestAnalyte().getResultGroup(),
                                                      "analyteTable");
                    exceptionList.add(exc);
                    valid = false;
                } else if(rg > results.size()){
                    exc = new GridFieldErrorException("invalidResultGroupException", i, j,
                                                      TestMeta.getTestAnalyte().getResultGroup(),
                                                      "analyteTable");
                    exceptionList.add(exc);
                    valid = false;   
                }
                
                
            }
        }
        
        return valid;
    }

    private boolean validateResult(ValidationErrorsList exceptionList,
                                ArrayList<ArrayList<TestResultDO>> results,
                                List<TestTypeOfSampleDO> sampleTypes, 
                                HashMap<Integer, List<Integer>> resGrpRsltMap) {
        TestResultDO resDO;
        Integer numId, dictId, titerId, typeId, dateId, dtId, timeId, unitId, entryId, defId;
        int i, j;
        String value, fieldName;
        boolean hasDateType, valid;
        NumericRange nr;
        TiterRange tr;
        HashMap<Integer, List<TiterRange>> trMap;
        HashMap<Integer, List<NumericRange>> nrMap;
        List<Integer> dictList, unitsWithDefault;
        List<Integer> resIdList;

        value = null;
        valid = true;

        dictId = categoryBean.getEntryIdForSystemName("test_res_type_dictionary");
        numId = categoryBean.getEntryIdForSystemName("test_res_type_numeric");
        titerId = categoryBean.getEntryIdForSystemName("test_res_type_titer");
        dateId = categoryBean.getEntryIdForSystemName("test_res_type_date");
        dtId = categoryBean.getEntryIdForSystemName("test_res_type_date_time");
        timeId = categoryBean.getEntryIdForSystemName("test_res_type_time");
        defId = categoryBean.getEntryIdForSystemName("test_res_type_default");

        trMap = new HashMap<Integer, List<TiterRange>>();
        nrMap = new HashMap<Integer, List<NumericRange>>();
        dictList = new ArrayList<Integer>();
        unitsWithDefault = new ArrayList<Integer>();
        hasDateType = false;
        

        for (i = 0; i < results.size(); i++ ) {
            trMap.clear();
            nrMap.clear();
            dictList.clear();
            hasDateType = false;
            unitsWithDefault.clear();
            resIdList = new ArrayList<Integer>();        
            resGrpRsltMap.put(i+1, resIdList);
            
            for (j = 0; j < results.get(i).size(); j++ ) {
                resDO = results.get(i).get(j);
                value = resDO.getValue();
                typeId = resDO.getTypeId();
                unitId = resDO.getUnitOfMeasureId();    
                
                resIdList.add(resDO.getId());
                
                //
                // units need to be valid for every result type because
                // their use is dependent on the unit
                //
                if (!unitIsValid(unitId, sampleTypes)) {                    
                    Query query;
                    String unitText;

                    query = manager.createNamedQuery("Dictionary.EntryById");
                    query.setParameter("id", unitId);
                    unitText = (String)query.getResultList().get(0);

                    exceptionList.add(new GridFieldErrorException("illegalUnitOfMeasureException:" +
                                                                  unitText, i, j,
                                                                  TestMeta.getTestResult()
                                                                          .getUnitOfMeasureId(),
                                                                  "resultTable"));    
                    valid = false;
                    continue;
                }

                fieldName = TestMeta.getTestResult().getValue();
                //
                // dictionary, titers, numeric require a value
                //
                if ( (value == null || "".equals(value)) &&
                    (numId.equals(typeId) || titerId.equals(typeId) || dictId.equals(typeId) || defId.equals(typeId))) {
                    exceptionList.add(new GridFieldErrorException("fieldRequiredException", i, j,
                                                                  fieldName,
                                                                  "resultTable"));

                    continue;
                } else if (!(value == null || "".equals(value)) &&
                           (dtId.equals(typeId) || timeId.equals(typeId) || dateId.equals(typeId))) {
                    exceptionList.add(new GridFieldErrorException(
                                                                  "valuePresentForDateTypesException",
                                                                  i, j, fieldName,
                                                                  "resultTable"));
                    valid = false;
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
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (dtId.equals(typeId)) {                        
                        if (hasDateType) {
                            fieldName = TestMeta.getTestResult().getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (timeId.equals(typeId)) {                        
                        if (hasDateType) {
                            fieldName = TestMeta.getTestResult().getTypeId();
                            throw new InconsistencyException("testMoreThanOneDateTypeException");
                        }
                        hasDateType = true;
                    } else if (dictId.equals(typeId)) {
                        entryId = categoryBean.getEntryIdForEntry(value);
                        if (entryId == null)
                            throw new ParseException("illegalDictEntryException");

                        if (!dictList.contains(entryId))
                            dictList.add(entryId);
                        else
                            throw new InconsistencyException("testDictEntryNotUniqueException");
                    } else if (defId.equals(typeId)) {
                        if (unitsWithDefault.indexOf(unitId) == -1)
                            unitsWithDefault.add(unitId);
                        else
                            throw new InconsistencyException(
                                                            "testMoreThanOneDefaultForUnitException");
                    } 
                } catch (ParseException ex) {
                    exceptionList.add(new GridFieldErrorException(ex.getMessage(), i, j, fieldName,
                                                                  "resultTable"));
                    valid = false;

                } catch (InconsistencyException ex) {
                    exceptionList.add(new GridFieldErrorException(ex.getMessage(), i, j, fieldName,
                                                                  "resultTable"));
                    valid = false;

                }
            }
        }
        
        return valid;
    }

    private void validatePrep(ValidationErrorsList exceptionList,
                                  List<TestPrepViewDO> testPrepDOList) {
        List<Integer> testPrepIdList;
        TableFieldErrorException exc;
        TestPrepViewDO prepDO;
        int numReq, i;

        testPrepIdList = new ArrayList<Integer>();
        numReq = 0;

        if (testPrepDOList == null)
            return;

        for (i = 0; i < testPrepDOList.size(); i++ ) {
            prepDO = testPrepDOList.get(i);
            if (prepDO.getPrepTestId() == null) {
                exc = new TableFieldErrorException("fieldRequiredException", i,
                                                   TestMeta.getTestPrep().getPrepTest().getName());
                exc.setTableKey("testPrepTable");
                exceptionList.add(exc);
            } else {
                if ( !testPrepIdList.contains(prepDO.getPrepTestId())) {
                    testPrepIdList.add(prepDO.getPrepTestId());
                } else {
                    exc = new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                       TestMeta.getTestPrep()
                                                               .getPrepTest()
                                                               .getName());
                    exc.setTableKey("testPrepTable");
                    exceptionList.add(exc);
                }
            }
            if ( !"Y".equals(prepDO.getIsOptional())) {
                if (numReq >= 1) {
                    exc = new TableFieldErrorException("moreThanOnePrepTestOptionalException", i,
                                                       TestMeta.getTestPrep()
                                                               .getPrepTest()
                                                               .getName());
                    exc.setTableKey("testPrepTable");
                    exceptionList.add(exc);
                }
                numReq++ ;
            }
        }
    }

    private void validateReflex(ValidationErrorsList exceptionList,
                                    List<TestReflexViewDO> testReflexDOList,
                                    boolean anaListValid,
                                    boolean resListValid,
                                    HashMap<Integer, Integer> anaResGrpMap,
                                    HashMap<Integer, List<Integer>> resGrpRsltMap) {
        TestReflexViewDO refDO;
        List<List<Integer>> idsList;
        List<Integer> ids;
        int i;
        String fieldName;

        fieldName = null;
        idsList = new ArrayList<List<Integer>>();

        for (i = 0; i < testReflexDOList.size(); i++ ) {
            refDO = testReflexDOList.get(i);

            ids = new ArrayList<Integer>();

            if (refDO.getAddTestId() != null && refDO.getTestAnalyteId() != null &&
                refDO.getTestResultId() != null) {
                ids.add(refDO.getAddTestId());
                ids.add(refDO.getTestAnalyteId());
                ids.add(refDO.getTestResultId());
            }
            try {
                if (refDO.getAddTestId() == null) {
                    fieldName = TestMeta.getTestReflex().getAddTest().getName();
                    throw new InconsistencyException("fieldRequiredException");
                }

                if (refDO.getTestAnalyteId() == null) {
                    fieldName = TestMeta.getTestReflex().getTestAnalyteId();
                    throw new InconsistencyException("fieldRequiredException");
                }

                if (refDO.getTestResultId() == null) {
                    fieldName = TestMeta.getTestReflex().getTestResult().getValue();
                    throw new InconsistencyException("fieldRequiredException");
                }

                if (refDO.getFlagsId() == null) {
                    fieldName = TestMeta.getTestReflex().getFlagsId();
                    throw new InconsistencyException("fieldRequiredException");
                }

                if ( !idsList.contains(ids)) {
                    idsList.add(ids);
                } else {
                    fieldName = TestMeta.getTestReflex().getAddTest().getName();
                    throw new InconsistencyException("fieldUniqueOnlyException");
                }

                fieldName = TestMeta.getTestReflex().getTestResult().getValue();
                validateAnalyteResultMapping(anaListValid, resListValid, anaResGrpMap,
                                             resGrpRsltMap, refDO);
            } catch (InconsistencyException ex) {
                addErrorToTableField(ex.getMessage(), "testReflexTable", fieldName,
                                     i, exceptionList);
            }

        }
    }

    private void validateWorksheet(ValidationErrorsList exceptionList,
                                       TestWorksheetDO worksheetDO) {
        boolean checkForMultiple = true;
        
        //
        // This check is put here in order to distinguish between the cases where 
        // the TestWorksheetDO was changed on the screen and where it was not.
        // This is necessary because it is possible for the users to enter no 
        // information on the screen in the fields related to the DO and 
        // commit the data and since the DO can't be null because then the fields
        // on the screen won't get refreshed on fetch, the validation code below 
        // will make error messages get displayed on the screen when there was 
        // no fault of the user.    
        //
        if(!worksheetDO.isChanged())
            return;
        
        if (worksheetDO.getBatchCapacity() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTestWorksheet()
                                                                              .getBatchCapacity()));
            checkForMultiple = false;
        }
        if (worksheetDO.getTotalCapacity() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTestWorksheet()
                                                                              .getTotalCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getBatchCapacity() != null && worksheetDO.getBatchCapacity() <= 0) {
            exceptionList.add(new FieldErrorException("batchCapacityMoreThanZeroException",
                                                      TestMeta.getTestWorksheet()
                                                                              .getBatchCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getTotalCapacity() != null && worksheetDO.getTotalCapacity() <= 0) {
            exceptionList.add(new FieldErrorException("totalCapacityMoreThanZeroException",
                                                      TestMeta.getTestWorksheet()
                                                                              .getTotalCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getFormatId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTestWorksheet()
                                                                              .getFormatId()));
        }

        if (checkForMultiple) {
            if ((worksheetDO.getTotalCapacity() % worksheetDO.getBatchCapacity()) != 0) {
                exceptionList.add(new FieldErrorException("totalCapacityMultipleException",
                                                          TestMeta.getTestWorksheet()
                                                                                  .getTotalCapacity()));
            }
        }
    }

    private void validateWorksheetItems(ValidationErrorsList exceptionList,
                                            List<TestWorksheetItemDO> itemDOList,
                                            TestWorksheetDO worksheetDO) {
        Integer bc, tc, position, batchId, totalId, formatId, fixedId, duplId;
        ArrayList<Integer> posList;
        int i, size;
        TestWorksheetItemDO currDO, prevDO;
        boolean checkPosition;
        Query query;
        String sysName, name;

        if (itemDOList == null)
            return;

        bc = null;
        tc = null;
        formatId = null;

        size = itemDOList.size();

        if (worksheetDO != null) {
            bc = worksheetDO.getBatchCapacity();
            tc = worksheetDO.getTotalCapacity();
            formatId = worksheetDO.getFormatId();
        } else if (size > 0) {
            // 
            // if there's no data in worksheetDO it means that the user didn't
            // specify any details about the kind of worksheet it will be and so
            // if there are qcs present then this is an erroneous situation and
            // the errors related to the worksheet information must be added to
            // the list of errors and since the validation for a TestWorksheetDO
            // won't be carried out unless the _changed flag is set, we set the 
            // 3 fields that are required for a TestWorksheet 
            //
            worksheetDO = new TestWorksheetDO();
            worksheetDO.setBatchCapacity(bc);
            worksheetDO.setFormatId(formatId);
            worksheetDO.setTotalCapacity(tc);
            validateWorksheet(exceptionList, worksheetDO);
        }

        posList = new ArrayList<Integer>();
        checkPosition = false;

        batchId = categoryBean.getEntryIdForSystemName("batch");
        totalId = categoryBean.getEntryIdForSystemName("total");
        fixedId = categoryBean.getEntryIdForSystemName("pos_fixed");
        duplId = categoryBean.getEntryIdForSystemName("pos_duplicate");

        prevDO = null;

        for (i = 0; i < size; i++ ) {
            currDO = itemDOList.get(i);

            if (i > 0)
                prevDO = itemDOList.get(i - 1);

            position = currDO.getPosition();
            checkPosition = true;
            name = currDO.getQcName();

            if (name == null || "".equals(name)) {
                exceptionList.add(new TableFieldErrorException(
                                                               "fieldRequiredException",
                                                               i,
                                                               TestMeta.getTestWorksheetItem()
                                                                       .getQcName(),
                                                               "worksheetTable"));
            }
            if (currDO.getTypeId() == null) {
                exceptionList.add(new TableFieldErrorException(
                                                               "fieldRequiredException",
                                                               i,
                                                               TestMeta.getTestWorksheetItem()
                                                                       .getTypeId(),
                                                               "worksheetTable"));
                checkPosition = false;
            }

            if (position != null) {
                if (position <= 0) {
                    exceptionList.add(new TableFieldErrorException(
                                                                   "posMoreThanZeroException",
                                                                   i,
                                                                   TestMeta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (bc != null && batchId.equals(formatId) && position > bc) {
                    exceptionList.add(new TableFieldErrorException(
                                                                   "posExcBatchCapacityException",
                                                                   i,
                                                                   TestMeta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else if (tc != null && totalId.equals(formatId) && position > tc) {
                    exceptionList.add(new TableFieldErrorException(
                                                                   "posExcTotalCapacityException",
                                                                   i,
                                                                   TestMeta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                   "worksheetTable"));
                    checkPosition = false;
                } else {
                    if ( !posList.contains(position)) {
                        posList.add(position);
                    } else {
                        exceptionList.add(new TableFieldErrorException(
                                                                       "duplicatePosForQCsException",
                                                                       i,
                                                                       TestMeta.getTestWorksheetItem()
                                                                               .getPosition(),
                                                                       "worksheetTable"));
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
                        exceptionList.add(new TableFieldErrorException(
                                                                       "fixedDuplicatePosException",
                                                                       i,
                                                                       TestMeta.getTestWorksheetItem()
                                                                               .getPosition(),
                                                                       "worksheetTable"));
                    }
                } else {
                    if (position == 1 && "pos_duplicate".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException(
                                                                       "posOneDuplicateException",
                                                                       i,
                                                                       TestMeta.getTestWorksheetItem()
                                                                               .getTypeId(),
                                                                       "worksheetTable"));
                    } else if ( !"pos_duplicate".equals(sysName) && !"pos_fixed".equals(sysName)) {
                        exceptionList.add(new TableFieldErrorException(
                                                                       "posSpecifiedException",
                                                                       i,
                                                                       TestMeta.getTestWorksheetItem()
                                                                               .getPosition(),
                                                                               "worksheetTable"));
                    }
                }

                if (duplicateAfterFixedOrDuplicate(currDO, prevDO, fixedId, duplId)) {
                    exceptionList.add(new TableFieldErrorException(
                                                                   "duplPosAfterFixedOrDuplPosException",
                                                                   i,
                                                                   TestMeta.getTestWorksheetItem()
                                                                           .getPosition(),
                                                                   "worksheetTable"));
                }
            }

        }
    }

    private void validateWorksheetAnalytes(ValidationErrorsList exceptionList,
                                               ArrayList<TestWorksheetAnalyteViewDO> twsaDOList) {
        TestWorksheetAnalyteDO twsaDO;
        Integer repeat,anaId;
        ArrayList<Integer> idlist;

        idlist = new ArrayList<Integer>();
        for (int i = 0; i < twsaDOList.size(); i++ ) {
            twsaDO = twsaDOList.get(i);
            anaId = twsaDO.getTestAnalyteId();
            repeat = twsaDO.getRepeat();            
            if(!idlist.contains(anaId)) {
                idlist.add(anaId);
            } else {
                exceptionList.add(new TableFieldErrorException(
                                                               "duplicateWSAnalyteException",i,
                                                               TestMeta.getTestWorksheetAnalyte()
                                                                       .getAnalyteId(),
                                                               "worksheetAnalyteTable"));
            }
            
            if (repeat == null || repeat < 1) {
                    exceptionList.add(new TableFieldErrorException("repeatNullForAnalyteException",i,
                                                                   TestMeta.getTestWorksheetAnalyte()
                                                                           .getRepeat(),
                                                                   "worksheetAnalyteTable"));
            }
            
        }

    }

    private void validateSections(ValidationErrorsList exceptionList,
                                      List<TestSectionViewDO> sectionDOList) {
        Integer defId, askId, matchId, flagId, sectId;
        List<Integer> idList;
        int size, numDef, numAsk, numMatch, numBlank, iter;
        TestSectionViewDO secDO;
        if (sectionDOList == null || sectionDOList.size() == 0) {
            exceptionList.add(new FormErrorException("atleastOneSection"));
            return;
        }
        size = sectionDOList.size();

        defId = categoryBean.getEntryIdForSystemName("test_section_default");
        askId = categoryBean.getEntryIdForSystemName("test_section_ask");
        matchId = categoryBean.getEntryIdForSystemName("test_section_match");

        numDef = 0;
        numAsk = 0;
        numMatch = 0;
        numBlank = 0;

        if (size > 0) {
            idList = new ArrayList<Integer>();
            for (iter = 0; iter < size; iter++ ) {
                secDO = sectionDOList.get(iter);
                flagId = secDO.getFlagId();
                sectId = secDO.getSectionId();

                if (sectId == null) {
                    addErrorToTableField("fieldRequiredException",
                                         TestSectionMetaMap.getTableName(),
                                         TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                } else if (idList.contains(sectId)) {
                    addErrorToTableField("fieldUniqueOnlyException",
                                         TestSectionMetaMap.getTableName(),
                                         TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                } else {
                    idList.add(sectId);
                }

                if (flagId == null) {
                    numBlank++ ;
                } else if (defId.equals(flagId)) {
                    numDef++ ;
                } else if (askId.equals(flagId)) {
                    numAsk++ ;
                } else if (matchId.equals(flagId)) {
                    numMatch++ ;
                }

            }

            if (numBlank == size) {
                for (iter = 0; iter < size; iter++ ) {
                    addErrorToTableField("allSectCantBeBlankException",
                                         TestSectionMetaMap.getTableName(),
                                         TestMeta.getTestSection().getFlagId(), iter, exceptionList);
                }
            } else if (numDef > 1) {
                for (iter = 0; iter < size; iter++ ) {
                    secDO = sectionDOList.get(iter);
                    flagId = secDO.getFlagId();
                    if (flagId != null) {
                        addErrorToTableField("allSectBlankIfDefException",
                                             TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter,
                                             exceptionList);
                    }
                }
            } else if (numDef == 1 && numBlank != (size - 1)) {
                for (iter = 0; iter < size; iter++ ) {
                    secDO = sectionDOList.get(iter);
                    flagId = secDO.getFlagId();
                    if (flagId != null && !defId.equals(flagId)) {
                        addErrorToTableField("allSectBlankIfDefException",
                                             TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter,
                                             exceptionList);
                    }
                }
            } else if (numMatch > 0 && numMatch != size) {
                for (iter = 0; iter < size; iter++ ) {
                    secDO = sectionDOList.get(iter);
                    flagId = secDO.getFlagId();

                    if (flagId == null || (flagId != null && !matchId.equals(flagId))) {
                        addErrorToTableField("allSectMatchFlagException",
                                             TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter,
                                             exceptionList);
                    }

                }
            } else if (numAsk > 0 && numAsk != size) {
                for (iter = 0; iter < size; iter++ ) {
                    secDO = sectionDOList.get(iter);
                    flagId = secDO.getFlagId();

                    if ( (flagId == null) || (flagId != null && !askId.equals(flagId))) {
                        addErrorToTableField("allSectAskFlagException",
                                             TestSectionMetaMap.getTableName(),
                                             TestMeta.getTestSection().getFlagId(), iter,
                                             exceptionList);
                    }

                }
            }
        }
    }

    /**
     * This method checks to see if a unit of measure (resultUnitId) assigned to
     * a test result belongs to the list of units added to the test
     */
    private boolean unitIsValid(Integer resultUnitId, List<TestTypeOfSampleDO> sampleTypeDOList) {
        int i, numMatch;
        TestTypeOfSampleDO sampleDO;
        Integer unitId;

        numMatch = 0;

        if (resultUnitId == null)
            return true;

        if (sampleTypeDOList == null) {
            return false;
        } else {
            for (i = 0; i < sampleTypeDOList.size(); i++ ) {
                sampleDO = sampleTypeDOList.get(i);
                unitId = sampleDO.getUnitOfMeasureId();
                if (unitId != null && unitId.equals(resultUnitId)) {
                    numMatch++ ;
                }
            }
        }

        return (numMatch != 0);
    }

    private void addErrorToTableField(String error,
                                      String tableKey,
                                      String fieldName,
                                      int index,
                                      ValidationErrorsList exceptionList) {
        TableFieldErrorException exc;
        exc = new TableFieldErrorException(error, index, fieldName);
        exc.setTableKey(tableKey);
        exceptionList.add(exc);
    }

    private void addTiterIfNoOverLap(HashMap<Integer, List<TiterRange>> trMap,
                                     Integer unitId,
                                     TiterRange tr) throws InconsistencyException {
        TiterRange lr;
        List<TiterRange> trList;

        trList = trMap.get(unitId);
        if (trList != null) {
            for (int i = 0; i < trList.size(); i++ ) {
                lr = trList.get(i);
                if (lr.isOverlapping(tr))
                    throw new InconsistencyException("testTiterRangeOverlapException");
            }
            trList.add(tr);
        } else {
            trList = new ArrayList<TiterRange>();
            trList.add(tr);
            trMap.put(unitId, trList);
        }
    }

    private void addNumericIfNoOverLap(HashMap<Integer, List<NumericRange>> nrMap,
                                       Integer unitId,
                                       NumericRange nr) throws InconsistencyException {
        NumericRange lr;
        List<NumericRange> nrList;

        nrList = nrMap.get(unitId);
        if (nrList != null) {
            for (int i = 0; i < nrList.size(); i++ ) {
                lr = nrList.get(i);
                if (lr.isOverlapping(nr))
                    throw new InconsistencyException("testNumRangeOverlapException");
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
     * result group that has been selected for the test analyte represented by
     * its id
     */
    private void validateAnalyteResultMapping(boolean anaListValid,
                                              boolean resListValid,
                                              HashMap<Integer, Integer> anaResGrpMap,
                                              HashMap<Integer, List<Integer>> resGrpRsltMap,
                                              TestReflexViewDO refDO) throws InconsistencyException {
        Integer rg, resId, anaId;
        List<Integer> resIdList;

        resId = refDO.getTestResultId();
        anaId = refDO.getTestAnalyteId();

        if (!anaListValid || !resListValid)
            return;

        //
        // find the result group selected for the test analyte from anaResGrpMap
        // using its id set in refDO,
        //        
        rg = anaResGrpMap.get(anaId);
        if (rg != null) {
            //
            // if the list obtained from anaResGrpMap does not contain the
            // test result id in refDO then that implies that this test result
            // doesn't belong to the result group selected for the test analyte
            // and thus an exception is thrown containing this message.
            //
            resIdList = resGrpRsltMap.get(rg);
            if (resIdList == null)
                throw new InconsistencyException("resultDoesntBelongToAnalyteException");
            else if ( !resIdList.contains(resId))
                throw new InconsistencyException("resultDoesntBelongToAnalyteException");
        }
    }

    /**
     * This method will return true if the type specified in currDO is duplicate
     * and if the type specified in prevDO is either fixed or duplicate and,
     * such that the position specified in prevDO is one less than the position
     * in currDO. The two integers, fixedId and duplId, are the ids of the
     * dictionary records that contain the entries for the fixed and duplicate
     * types respectively
     */
    private boolean duplicateAfterFixedOrDuplicate(TestWorksheetItemDO currDO,
                                                   TestWorksheetItemDO prevDO,
                                                   Integer fixedId,
                                                   Integer duplId) {
        Integer ptId, ctId, ppos, cpos;

        if (prevDO == null || currDO == null)
            return false;

        ptId = prevDO.getTypeId();
        ctId = currDO.getTypeId();
        cpos = currDO.getPosition();
        ppos = prevDO.getPosition();

        if (ppos != null && cpos != null && ppos == cpos - 1) {
            if (duplId.equals(ctId) && (duplId.equals(ptId) || fixedId.equals(ptId)))
                return true;
        }

        return false;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "test", flag);
    }
}