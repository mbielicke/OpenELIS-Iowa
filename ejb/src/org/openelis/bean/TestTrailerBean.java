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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestTrailerDO;
import org.openelis.entity.TestTrailer;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.meta.TestTrailerMeta;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("testtrailer-select")
public class TestTrailerBean implements TestTrailerRemote{

    @PersistenceContext(unitName = "openelis")
    private EntityManager                      manager;

    @Resource
    private SessionContext                     ctx;

    @EJB
    private LockLocal                          lockBean;

    private static final TestTrailerMeta    meta = new TestTrailerMeta();

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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + TestTrailerMeta.getId() + ", " +
                          TestTrailerMeta.getName() + ") ");
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

        if ( !data.isChanged())
            return data;

        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lockBean.validateLock(ReferenceTable.TEST_TRAILER, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(TestTrailer.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setText(data.getText());

        lockBean.giveUpLock(ReferenceTable.TEST_TRAILER, data.getId());

        return data;
    }

    public TestTrailerDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.TEST_TRAILER, id);
        return fetchById(id);
    }

    public TestTrailerDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.TEST_TRAILER, id);
        return fetchById(id);
    }

    public void delete(TestTrailerDO data) throws Exception {
        Query query;
        List<Long> list;
        TestTrailer entity;

        checkSecurity(ModuleFlags.DELETE);

        lockBean.validateLock(ReferenceTable.TEST_TRAILER, data.getId());

        // reference check
        query = manager.createNamedQuery("TestTrailer.ReferenceCount");
        query.setParameter("id", data.getId());
        list = query.getResultList();
        for (Long i : list) {
            if (i > 0)
                throw new FormErrorException("testTrailerDeleteException");
        }

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(TestTrailer.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lockBean.giveUpLock(ReferenceTable.TEST_TRAILER, data.getId());
    }

    public void validate(TestTrailerDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", TestTrailerMeta.getName()));
        } else {
            ArrayList<IdNameVO> dups;
            
            dups = fetchByName(data.getName(), 1);
            if (dups.size() > 0 && ! dups.get(0).getId().equals(data.getId()))
                list.add(new FieldErrorException("fieldUniqueException", TestTrailerMeta.getName()));
        }

        if (DataBaseUtil.isEmpty(data.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException", TestTrailerMeta.getDescription()));

        if (DataBaseUtil.isEmpty(data.getText()))
            list.add(new FieldErrorException("fieldRequiredException", TestTrailerMeta.getText()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity("testtrailer", flag);
    }
}
