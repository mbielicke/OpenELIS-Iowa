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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.Test;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.TestLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.remote.TestRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestBean implements TestRemote, TestLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;

    private static final TestMetaMap TestMeta = new TestMetaMap();

    public TestViewDO fetchById(Integer testId) throws Exception {
        TestViewDO testDO;
        Query query;

        query = manager.createNamedQuery("Test.FetchById");
        query.setParameter("id", testId);
        try {
            testDO = (TestViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return testDO;
    }

    public List<TestMethodVO> fetchByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Test.FetchWithMethodByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    public List<TestMethodVO> fetchActiveByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Test.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    public List<TestMethodVO> fetchByNameSampleItemType(String name,
                                                            Integer sampleItemType,
                                                            int maxResults) {
        Query query = manager.createNamedQuery("Test.FetchByNameSampleItemType");
        query.setParameter("name", name);
        query.setParameter("typeId", sampleItemType);
        query.setMaxResults(maxResults);

        List testList = query.getResultList();

        /*
         * for(int i=0; i<testList.size(); i++){ SampleTestMethodDO testDO =
         * (SampleTestMethodDO)testList.get(i); // query for test sections try {
         * testDO.setSections(getTestSections(testDO.getTest().getId())); }
         * catch (Exception e) { testDO.setSections(new
         * ArrayList<TestSectionViewDO>()); } //query for pre tests
         * testDO.setPrepTests
         * ((ArrayList<TestPrepDO>)getTestPreps(testDO.getTest().getId())); }
         */

        return testList;
    }

    public ArrayList<TestMethodVO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                             throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List returnList;

        builder = new QueryBuilderV2();
        builder.setMeta(TestMeta);

        builder.setSelect("distinct new org.openelis.domain.TestMethodVO(" +
                          TestMeta.getId() +
                          ", " +
                          (TestMeta.getName() + ", " + TestMeta.getDescription() + ", " +
                           TestMeta.getMethod().getId() + ", " + TestMeta.getMethod().getName() +
                           ", " + TestMeta.getMethod().getDescription()) + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(TestMeta.getName() + ", " + TestMeta.getMethod().getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        QueryBuilderV2.setQueryParams(query, fields);

        returnList = query.getResultList();
        if (returnList.isEmpty())
            throw new NotFoundException();
        returnList = (ArrayList<TestMethodVO>)DataBaseUtil.subList(returnList, first, max);
        if (returnList == null)
            throw new LastPageException();
        return (ArrayList<TestMethodVO>)returnList;
    }

    public TestViewDO add(TestViewDO data) throws Exception {
        Test test;

        manager.setFlushMode(FlushModeType.COMMIT);

        test = new Test();

        test.setName(data.getName());
        test.setMethodId(data.getMethodId());
        test.setActiveBegin(data.getActiveBegin());
        test.setActiveEnd(data.getActiveEnd());
        test.setDescription(data.getDescription());
        test.setIsActive(data.getIsActive());
        test.setIsReportable(data.getIsReportable());
        test.setLabelId(data.getLabelId());
        test.setLabelQty(data.getLabelQty());
        test.setReportingDescription(data.getReportingDescription());
        test.setRevisionMethodId(data.getRevisionMethodId());
        test.setScriptletId(data.getScriptletId());
        test.setTestFormatId(data.getTestFormatId());
        test.setTestTrailerId(data.getTestTrailerId());
        test.setTimeHolding(data.getTimeHolding());
        test.setTimeTaAverage(data.getTimeTaAverage());
        test.setTimeTaMax(data.getTimeTaMax());
        test.setTimeTaWarning(data.getTimeTaWarning());
        test.setTimeTransit(data.getTimeTransit());
        test.setReportingMethodId(data.getReportingMethodId());
        test.setSortingMethodId(data.getSortingMethodId());
        test.setReportingSequence(data.getReportingSequence());

        manager.persist(test);
        data.setId(test.getId());

        return data;
    }

    @RolesAllowed("test-update")
    public TestViewDO update(TestViewDO data) throws Exception {
        Test test;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        test = manager.find(Test.class, data.getId());

        test.setName(data.getName());
        test.setMethodId(data.getMethodId());
        test.setActiveBegin(data.getActiveBegin());
        test.setActiveEnd(data.getActiveEnd());
        test.setDescription(data.getDescription());
        test.setIsActive(data.getIsActive());
        test.setIsReportable(data.getIsReportable());
        test.setLabelId(data.getLabelId());
        test.setLabelQty(data.getLabelQty());
        test.setReportingDescription(data.getReportingDescription());
        test.setRevisionMethodId(data.getRevisionMethodId());
        test.setScriptletId(data.getScriptletId());
        test.setTestFormatId(data.getTestFormatId());
        test.setTestTrailerId(data.getTestTrailerId());
        test.setTimeHolding(data.getTimeHolding());
        test.setTimeTaAverage(data.getTimeTaAverage());
        test.setTimeTaMax(data.getTimeTaMax());
        test.setTimeTaWarning(data.getTimeTaWarning());
        test.setTimeTransit(data.getTimeTransit());
        test.setReportingMethodId(data.getReportingMethodId());
        test.setSortingMethodId(data.getSortingMethodId());
        test.setReportingSequence(data.getReportingSequence());

        return data;

    }

    public void validate(TestViewDO testDO) throws Exception {
        boolean checkDuplicate = true;
        List list;
        Query query;
        TestViewDO test;
        ValidationErrorsList exceptionList;

        exceptionList = new ValidationErrorsList();

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

            query = manager.createNamedQuery("TestPrep.FetchByPrepTestId");
            query.setParameter("testId", testDO.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FieldErrorException("testUsedAsPrepTestException",null));
                checkDuplicate = false;
            }

            query = manager.createNamedQuery("TestReflex.FetchByAddTestId");
            query.setParameter("testId", testDO.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FieldErrorException("testUsedAsReflexTestException",null));
                checkDuplicate = false;
            }
        }

        if (testDO.getIsReportable() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getIsReportable()));
        }

        if (testDO.getTimeTaMax() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTaMax()));
        }

        if (testDO.getTimeTransit() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTransit()));
        }

        if (testDO.getTimeTaAverage() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeTaAverage()));
        }

        if (testDO.getTimeHolding() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      TestMeta.getTimeHolding()));
        }

        if (testDO.getTimeTaWarning() == null) {
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
                exceptionList.add(new FieldErrorException("endDateAfterBeginDateException",null));
                checkDuplicate = false;
            }
        }

        if (checkDuplicate) {
            query = manager.createNamedQuery("Test.FetchByName");
            query.setParameter("name", testDO.getName());
            list = query.getResultList();
            for (int iter = 0; iter < list.size(); iter++ ) {
                boolean overlap = false;
                test = (TestViewDO)list.get(iter);
                if ( !test.getId().equals(testDO.getId())) {
                    if (test.getMethodId().equals(testDO.getMethodId())) {
                        if (test.getIsActive().equals(testDO.getIsActive())) {
                            if ("Y".equals(testDO.getIsActive())) {
                                exceptionList.add(new FieldErrorException("testActiveException",null));
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
                            exceptionList.add(new FieldErrorException("testTimeOverlapException",null));
                        }
                    }
                }
            }
        }

        if (exceptionList.size() > 0) {
            throw exceptionList;
        }
    }
}