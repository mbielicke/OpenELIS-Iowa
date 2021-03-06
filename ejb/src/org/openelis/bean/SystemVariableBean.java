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
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.SystemVariable;
import org.openelis.meta.SystemVariableMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class SystemVariableBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                   manager;

    @EJB
    private LockBean                          lock;
    
    @EJB
    private UserCacheBean                      userCache;

    private static final SystemVariableMeta meta = new SystemVariableMeta();

    public SystemVariableDO fetchById(Integer id) throws Exception {
        Query query;
        SystemVariableDO data;

        query = manager.createNamedQuery("SystemVariable.FetchById");
        query.setParameter("id", id);
        try {
            data = (SystemVariableDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public SystemVariableDO fetchByName(String name) throws Exception {
        Query query;
        SystemVariableDO data;

        query = manager.createNamedQuery("SystemVariable.FetchByName");
        query.setParameter("name", name);
        try {
            data = (SystemVariableDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<SystemVariableDO> fetchByName(String name, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("SystemVariable.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          SystemVariableMeta.getId() + ", " +
                          SystemVariableMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(SystemVariableMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public SystemVariableDO add(SystemVariableDO data) throws Exception {
        SystemVariable entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SystemVariable();
        entity.setName(data.getName());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public SystemVariableDO update(SystemVariableDO data) throws Exception {
        SystemVariable entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().SYSTEM_VARIABLE, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(Constants.table().SYSTEM_VARIABLE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SystemVariable.class, data.getId());
        entity.setName(data.getName());
        entity.setValue(data.getValue());

        lock.unlock(Constants.table().SYSTEM_VARIABLE, data.getId());

        return data;
    }

    /**
     * Update the record without checking for user's permission (as system).
     */
    public SystemVariableDO updateAsSystem(SystemVariableDO data) throws Exception {
        SystemVariable entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().SYSTEM_VARIABLE, data.getId());
            return data;
        }

        validate(data);

        lock.validateLock(Constants.table().SYSTEM_VARIABLE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SystemVariable.class, data.getId());
        entity.setName(data.getName());
        entity.setValue(data.getValue());

        lock.unlock(Constants.table().SYSTEM_VARIABLE, data.getId());

        return data;
    }

    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().SYSTEM_VARIABLE, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public SystemVariableDO fetchForUpdateByName(String name) throws Exception {
        SystemVariableDO data;

        data = fetchByName(name);
        return fetchForUpdate(data.getId());
    }

    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().SYSTEM_VARIABLE, id);
        return fetchById(id);
    }

    public void delete(SystemVariableDO data) throws Exception {
        SystemVariable entity;

        checkSecurity(ModuleFlags.DELETE);

        lock.validateLock(Constants.table().SYSTEM_VARIABLE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SystemVariable.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lock.unlock(Constants.table().SYSTEM_VARIABLE, data.getId());
    }

    public void validate(SystemVariableDO data) throws Exception {
        Integer svid;
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        svid = data.getId();
        if (svid == null)
            svid = 0;

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FormErrorException(Messages.get()
                                                    .systemVariable_nameRequiredException(svid)));
        } else {
            ArrayList<SystemVariableDO> dups;

            dups = fetchByName(data.getName(), 1);
            if (dups.size() > 0 && !dups.get(0).getId().equals(data.getId()))
                list.add(new FormErrorException(Messages.get().systemVariable_uniqueException(svid)));
        }

        if (DataBaseUtil.isEmpty(data.getValue()))
            list.add(new FormErrorException(Messages.get()
                                                    .systemVariable_valueRequiredException(svid)));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("systemvariable", flag);
    }
}
