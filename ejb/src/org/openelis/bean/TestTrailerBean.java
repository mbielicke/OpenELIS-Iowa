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
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.entity.TestTrailer;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.local.TestTrailerLocal;
import org.openelis.meta.TestTrailerMeta;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
public class TestTrailerBean implements TestTrailerRemote, TestTrailerLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                manager;

    @EJB
    private LockLocal                    lock;

    private static final TestTrailerMeta meta = new TestTrailerMeta();

    public TestTrailerBean() {
    }

    public TestTrailerDO fetchById(Integer id) throws Exception {
        Query query;
        TestTrailerDO data;

        query = manager.createNamedQuery("TestTrailer.FetchById");
        query.setParameter("id", id);
        try {
            data = (TestTrailerDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<TestTrailerDO> fetchByIds(Collection<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("TestTrailer.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> fetchByName(String name, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("TestTrailer.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          TestTrailerMeta.getId() + ", " + TestTrailerMeta.getName() +
                          ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(TestTrailerMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public TestTrailerDO add(TestTrailerDO data) throws Exception {
        TestTrailer entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new TestTrailer();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setText(data.getText());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public TestTrailerDO update(TestTrailerDO data) throws Exception {
        TestTrailer entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().TEST_TRAILER, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(Constants.table().TEST_TRAILER, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(TestTrailer.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setText(data.getText());

        lock.unlock(Constants.table().TEST_TRAILER, data.getId());

        return data;
    }

    public TestTrailerDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().TEST_TRAILER, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public TestTrailerDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().TEST_TRAILER, id);
        return fetchById(id);
    }

    public void delete(TestTrailerDO data) throws Exception {
        Query query;
        List<Long> list;
        TestTrailer entity;
        ValidationErrorsList errors;

        checkSecurity(ModuleFlags.DELETE);
        errors = new ValidationErrorsList();
        // reference check
        query = manager.createNamedQuery("TestTrailer.ReferenceCount");
        query.setParameter("id", data.getId());
        list = query.getResultList();
        for (Long i : list) {
            if (i > 0) {
                errors.add(new FormErrorException("testTrailerDeleteException"));
                throw errors;
            }
        }
        lock.validateLock(Constants.table().TEST_TRAILER, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(TestTrailer.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lock.unlock(Constants.table().TEST_TRAILER, data.getId());
    }

    public void validate(TestTrailerDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestTrailerMeta.getName()));
        } else {
            ArrayList<IdNameVO> dups;

            dups = fetchByName(data.getName(), 1);
            if (dups.size() > 0 && !dups.get(0).getId().equals(data.getId()))
                list.add(new FieldErrorException("fieldUniqueException",
                                                 TestTrailerMeta.getName()));
        }

        if (DataBaseUtil.isEmpty(data.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestTrailerMeta.getDescription()));

        if (DataBaseUtil.isEmpty(data.getText()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestTrailerMeta.getText()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("testtrailer", flag);
    }
}
