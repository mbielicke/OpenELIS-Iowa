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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.PanelVO;
import org.openelis.domain.TestDO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestViewDO;
import org.openelis.entity.Test;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class TestBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager         manager;

    private static final TestMeta meta = new TestMeta();

    public TestViewDO fetchById(Integer testId) throws Exception {
        TestViewDO data;
        Query query;

        query = manager.createNamedQuery("Test.FetchById");
        query.setParameter("id", testId);
        try {
            data = (TestViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<TestViewDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;
        List<TestViewDO> t;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("Test.FetchByIds");
        t = new ArrayList<TestViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            t.addAll(query.getResultList());
        }
        
        return DataBaseUtil.toArrayList(t);
    }
    
    public TestMethodVO fetchActiveById(Integer testId) throws Exception {
        TestMethodVO data;
        Query query;

        query = manager.createNamedQuery("Test.FetchActiveById");
        query.setParameter("id", testId);
        try {
            data = (TestMethodVO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<TestMethodVO> fetchByName(String name, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchWithMethodByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<TestViewDO> fetchByName(String name) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchByName");
        query.setParameter("name", name);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<TestMethodVO> fetchActiveByName(String name, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchActiveWithMethodByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<TestMethodVO> fetchByNameSampleType(String name, Integer typeId, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchByNameSampleItemType");
        query.setParameter("name", name);
        query.setParameter("typeId", typeId);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public TestViewDO fetchActiveByNameMethodName(String testName, String methodName) throws Exception {
        TestViewDO data;
        Query query;

        query = manager.createNamedQuery("Test.FetchActiveByNameMethodName");
        query.setParameter("name", testName);
        query.setParameter("methodName", methodName);
        try {
            data = (TestViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<TestMethodVO> fetchByPanelId(Integer panelId) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchByPanelId");
        query.setParameter("panelId", panelId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<PanelVO> fetchNameMethodSectionByName(String name, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchNameMethodSectionByName");
        query.setParameter("name", name);
        query.setMaxResults(max);
        try {
            return DataBaseUtil.toArrayList(query.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<TestMethodSampleTypeVO> fetchTestMethodSampleTypeList() throws Exception {
        Query query;
        List returnList;

        query = manager.createNamedQuery("Test.FetchTestMethodSampleTypeList");
        returnList = query.getResultList();

        query = manager.createNamedQuery("Panel.FetchPanelSampleTypeList");
        returnList.addAll(query.getResultList());

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<TestViewDO> fetchList() throws Exception {
        Query query;

        query = manager.createNamedQuery("Test.FetchList");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<TestMethodVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List returnList;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.TestMethodVO(" + TestMeta.getId() +
                          "," + TestMeta.getName() + "," + TestMeta.getDescription() + "," +
                          TestMeta.getMethodId() + "," + TestMeta.getMethodName() + "," +
                          TestMeta.getMethodDescription() + "," + TestMeta.getIsActive() + "," +
                          TestMeta.getActiveBegin() + "," + TestMeta.getActiveEnd() + ")");
        builder.constructWhere(fields);
        builder.setOrderBy(TestMeta.getName() + ", " + TestMeta.getMethodName());

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

    public TestDO add(TestDO data) throws Exception {
        Test entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Test();

        entity.setName(data.getName());
        entity.setMethodId(data.getMethodId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setIsReportable(data.getIsReportable());
        entity.setLabelId(data.getLabelId());
        entity.setLabelQty(data.getLabelQty());
        entity.setReportingDescription(data.getReportingDescription());
        entity.setRevisionMethodId(data.getRevisionMethodId());
        entity.setScriptletId(data.getScriptletId());
        entity.setTestFormatId(data.getTestFormatId());
        entity.setTestTrailerId(data.getTestTrailerId());
        entity.setTimeHolding(data.getTimeHolding());
        entity.setTimeTaAverage(data.getTimeTaAverage());
        entity.setTimeTaMax(data.getTimeTaMax());
        entity.setTimeTaWarning(data.getTimeTaWarning());
        entity.setTimeTransit(data.getTimeTransit());
        entity.setReportingMethodId(data.getReportingMethodId());
        entity.setSortingMethodId(data.getSortingMethodId());
        entity.setReportingSequence(data.getReportingSequence());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    @RolesAllowed("test-update")
    public TestDO update(TestDO data) throws Exception {
        Test entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Test.class, data.getId());

        entity.setName(data.getName());
        entity.setMethodId(data.getMethodId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setIsReportable(data.getIsReportable());
        entity.setLabelId(data.getLabelId());
        entity.setLabelQty(data.getLabelQty());
        entity.setReportingDescription(data.getReportingDescription());
        entity.setRevisionMethodId(data.getRevisionMethodId());
        entity.setScriptletId(data.getScriptletId());
        entity.setTestFormatId(data.getTestFormatId());
        entity.setTestTrailerId(data.getTestTrailerId());
        entity.setTimeHolding(data.getTimeHolding());
        entity.setTimeTaAverage(data.getTimeTaAverage());
        entity.setTimeTaMax(data.getTimeTaMax());
        entity.setTimeTaWarning(data.getTimeTaWarning());
        entity.setTimeTransit(data.getTimeTransit());
        entity.setReportingMethodId(data.getReportingMethodId());
        entity.setSortingMethodId(data.getSortingMethodId());
        entity.setReportingSequence(data.getReportingSequence());

        return data;

    }

    public void validate(TestDO data) throws Exception {
        boolean checkDuplicate, overlap;
        List list;
        Query query;
        TestViewDO test;
        ValidationErrorsList exceptionList;

        exceptionList = new ValidationErrorsList();
        checkDuplicate = true;

        if (DataBaseUtil.isEmpty(data.getName())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getName()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getMethodId())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getMethodId()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getDescription())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getDescription()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getIsActive())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getIsActive()));
            checkDuplicate = false;
        } else if ("N".equals(data.getIsActive())) {

            query = manager.createNamedQuery("TestPrep.FetchByPrepTestId");
            query.setParameter("testId", data.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FieldErrorException(Messages.get()
                                                                  .testUsedAsPrepTestException(),
                                                          null));
                checkDuplicate = false;
            }

            query = manager.createNamedQuery("TestReflex.FetchByAddTestId");
            query.setParameter("testId", data.getId());
            list = query.getResultList();
            if (list.size() > 0) {
                exceptionList.add(new FieldErrorException(Messages.get()
                                                                  .testUsedAsReflexTestException(),
                                                          null));
                checkDuplicate = false;
            }
        }

        if (DataBaseUtil.isEmpty(data.getIsReportable()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getIsReportable()));

        if (DataBaseUtil.isEmpty(data.getTimeTaMax()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTimeTaMax()));

        if (DataBaseUtil.isEmpty(data.getTimeTransit()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTimeTransit()));

        if (DataBaseUtil.isEmpty(data.getTimeTaAverage()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTimeTaAverage()));

        if (DataBaseUtil.isEmpty(data.getTimeHolding()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTimeHolding()));

        if (DataBaseUtil.isEmpty(data.getTimeTaWarning()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTimeTaWarning()));

        if (DataBaseUtil.isEmpty(data.getTestFormatId()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getTestFormatId()));

        if (DataBaseUtil.isEmpty(data.getReportingMethodId()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getReportingMethodId()));

        if (DataBaseUtil.isEmpty(data.getSortingMethodId()))
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getSortingMethodId()));

        if (DataBaseUtil.isEmpty(data.getActiveBegin())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getActiveBegin()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      TestMeta.getActiveEnd()));
            checkDuplicate = false;
        }

        if (checkDuplicate) {
            if (data.getActiveEnd().before(data.getActiveBegin())) {
                exceptionList.add(new FieldErrorException(Messages.get()
                                                                  .endDateAfterBeginDateException(),
                                                          null));
                checkDuplicate = false;
            }
        }

        if (checkDuplicate) {
            query = manager.createNamedQuery("Test.FetchByName");
            query.setParameter("name", data.getName());
            list = query.getResultList();
            for (int i = 0; i < list.size(); i++ ) {
                overlap = false;
                test = (TestViewDO)list.get(i);
                if (DataBaseUtil.isDifferent(test.getId(), data.getId()) &&
                    DataBaseUtil.isSame(test.getMethodId(), data.getMethodId()) &&
                    DataBaseUtil.isSame(test.getIsActive(), data.getIsActive())) {
                    if ("Y".equals(data.getIsActive())) {
                        exceptionList.add(new FieldErrorException(Messages.get()
                                                                          .testActiveException(),
                                                                  null));
                        break;
                    }

                    if (DataBaseUtil.isAfter(data.getActiveEnd(), test.getActiveBegin()) &&
                        DataBaseUtil.isAfter(test.getActiveEnd(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveBegin(), test.getActiveBegin()) &&
                               DataBaseUtil.isAfter(test.getActiveEnd(), data.getActiveEnd())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveEnd(), test.getActiveEnd()) &&
                               DataBaseUtil.isAfter(test.getActiveBegin(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(test.getActiveEnd(), data.getActiveEnd()) &&
                               DataBaseUtil.isAfter(data.getActiveBegin(), test.getActiveBegin())) {
                        overlap = true;
                    } else if ( !DataBaseUtil.isDifferentYD(test.getActiveBegin(),
                                                            data.getActiveEnd()) ||
                               !DataBaseUtil.isDifferentYD(test.getActiveEnd(),
                                                           data.getActiveBegin())) {
                        overlap = true;
                    } else if ( !DataBaseUtil.isDifferentYD(test.getActiveBegin(),
                                                            data.getActiveBegin()) ||
                               ( !DataBaseUtil.isDifferentYD(test.getActiveEnd(),
                                                             data.getActiveEnd()))) {
                        overlap = true;
                    }

                    if (overlap) {
                        exceptionList.add(new FieldErrorException(Messages.get()
                                                                          .testTimeOverlapException(),
                                                                  null));
                    }
                }
            }
        }

        if (exceptionList.size() > 0) {
            throw exceptionList;
        }
    }
}
