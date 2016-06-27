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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.entity.TestResult;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultValidator.Type;

@Stateless
@SecurityDomain("openelis")
public class TestResultBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @EJB
    private DictionaryCacheBean           dictionaryCache;

    private static HashMap<Integer, Type> types;

    @PostConstruct
    public void init() {
        if (types == null) {
            types = new HashMap<Integer, Type>();
            types.put(Constants.dictionary().TEST_RES_TYPE_DICTIONARY, Type.DICTIONARY);
            types.put(Constants.dictionary().TEST_RES_TYPE_NUMERIC, Type.NUMERIC);
            types.put(Constants.dictionary().TEST_RES_TYPE_TITER, Type.TITER);
            types.put(Constants.dictionary().TEST_RES_TYPE_DATE, Type.DATE);
            types.put(Constants.dictionary().TEST_RES_TYPE_DATE_TIME, Type.DATE_TIME);
            types.put(Constants.dictionary().TEST_RES_TYPE_TIME, Type.TIME);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER, Type.ALPHA_LOWER);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER, Type.ALPHA_UPPER);
            types.put(Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED, Type.ALPHA_MIXED);
        }
    }

    public TestResultViewDO fetchById(Integer id) throws Exception {
        Query query = manager.createNamedQuery("TestResult.FetchById");
        query.setParameter("id", id);
        try {
            return (TestResultViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<TestResultViewDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List<TestResultViewDO> t;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("TestResult.FetchByIds");
        t = new ArrayList<TestResultViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            t.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(t);
    }

    public ArrayList<ArrayList<TestResultViewDO>> fetchByTestId(Integer testId) throws Exception {
        ArrayList<ArrayList<TestResultViewDO>> grid;
        List<TestResultViewDO> list;
        TestResultViewDO data;
        Integer rg;
        Query query;
        DictionaryDO dict;
        Type type;

        list = null;
        grid = new ArrayList<ArrayList<TestResultViewDO>>();
        rg = 1;

        while (rg != null) {
            query = manager.createNamedQuery("TestResult.FetchByTestIdResultGroup");
            query.setParameter("testId", testId);
            query.setParameter("resultGroup", rg);

            try {
                list = query.getResultList();
                if (list.isEmpty()) {
                    rg = null;
                    break;
                }

                list = DataBaseUtil.toArrayList(list);
                for (int i = 0; i < list.size(); i++ ) {
                    data = (TestResultViewDO)list.get(i);

                    /*
                     * for entries that are dictionary, we want to fetch the
                     * dictionary text and set it for display; the flag
                     * "isActive" is also set
                     */
                    type = types.get(data.getTypeId());
                    if (type == Type.DICTIONARY) {
                        dict = dictionaryCache.getById(Integer.parseInt(data.getValue()));
                        if (dict != null) {
                            data.setDictionary(dict.getEntry());
                            data.setDictionaryIsActive(dict.getIsActive());
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            grid.add((ArrayList<TestResultViewDO>)list);
            rg++ ;
        }
        return grid;
    }

    public ArrayList<TestResultViewDO> fetchTestResultsByTestIdsAndFlagPattern(ArrayList<Integer> tids,
                                                                               String pattern) {
        Query query;

        query = manager.createNamedQuery("TestResult.FetchTestResultsByTestIdsAndFlagPattern");
        query.setParameter("testIds", tids);
        query.setParameter("pattern", pattern);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public TestResultDO add(TestResultDO data) throws Exception {
        TestResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new TestResult();

        entity.setFlagsId(data.getFlagsId());
        entity.setResultGroup(data.getResultGroup());
        entity.setRoundingMethodId(data.getRoundingMethodId());
        entity.setSignificantDigits(data.getSignificantDigits());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public TestResultDO update(TestResultDO data) throws Exception {
        TestResult entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestResult.class, data.getId());

        entity.setFlagsId(data.getFlagsId());
        entity.setResultGroup(data.getResultGroup());
        entity.setRoundingMethodId(data.getRoundingMethodId());
        entity.setSignificantDigits(data.getSignificantDigits());
        entity.setSortOrder(data.getSortOrder());
        entity.setTestId(data.getTestId());
        entity.setTypeId(data.getTypeId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setValue(data.getValue());

        return data;

    }

    public void delete(TestResultDO data) throws Exception {
        TestResult entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(TestResult.class, data.getId());

        if (entity != null)
            manager.remove(entity);

    }

    public void validate(TestResultDO data) throws Exception {
        ValidationErrorsList list;
        Integer typeId;
        String value;
        Type type;

        list = new ValidationErrorsList();
        value = data.getValue();
        typeId = data.getTypeId();

        if (DataBaseUtil.isEmpty(typeId))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getResultTypeId()));
        //
        // dictionary, titers, numeric require a value
        //
        type = types.get(data.getTypeId());
        if (DataBaseUtil.isEmpty(value) &&
            (type == Type.NUMERIC || type == Type.TITER || type == Type.DICTIONARY)) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getResultValue()));
        } else if ( !DataBaseUtil.isEmpty(value) &&
                   (type == Type.DATE_TIME || type == Type.TIME || type == Type.DATE ||
                    type == Type.ALPHA_LOWER || type == Type.ALPHA_UPPER || type == Type.ALPHA_MIXED)) {
            list.add(new FieldErrorException(Messages.get().valuePresentForTypeException(),
                                             TestMeta.getResultValue()));
        }

        if (list.size() > 0)
            throw list;
    }
}