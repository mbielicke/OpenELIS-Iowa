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
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.SystemVariable;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("systemvariable-select")
public class SystemVariableBean implements SystemVariableRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager                      manager;

    @Resource
    private SessionContext                     ctx;

    @EJB
    private LockLocal                          lockBean;

    private static int                         systemVariableRefTableId;
    private static final SystemVariableMetaMap meta = new SystemVariableMetaMap();

    public SystemVariableBean() {
        systemVariableRefTableId = ReferenceTableCache.getReferenceTable("system_variable");
    }

    public SystemVariableDO fetchById(Integer id) throws Exception {
        Query query;
        SystemVariableDO data;

        query = manager.createNamedQuery("SystemVariable.FindById");
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

    public SystemVariableDO fetchByName(String name) throws Exception {
        Query query;
        SystemVariableDO data;

        query = manager.createNamedQuery("SystemVariable.FindByName");
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

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + meta.getId() + ", " +
                          meta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(meta.getName());

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

        if ( !data.isChanged())
            return data;

        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lockBean.validateLock(systemVariableRefTableId, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SystemVariable.class, data.getId());
        entity.setName(data.getName());
        entity.setValue(data.getValue());

        lockBean.giveUpLock(systemVariableRefTableId, data.getId());

        return data;
    }

    public SystemVariableDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(systemVariableRefTableId, id);
        return fetchById(id);
    }

    public SystemVariableDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(systemVariableRefTableId, id);
        return fetchById(id);
    }

    public void delete(Integer id) throws Exception {
        SystemVariable entity;

        checkSecurity(ModuleFlags.DELETE);

        lockBean.validateLock(systemVariableRefTableId, id);

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SystemVariable.class, id);
        if (entity != null)
            manager.remove(entity);

        lockBean.giveUpLock(systemVariableRefTableId, id);
    }

    public void validate(SystemVariableDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (data.getName() == null || data.getName().length() == 0) {
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));
        } else {
            SystemVariableDO dup;
            
            try {
                dup = fetchByName(data.getName());
                if (! dup.getId().equals(data.getId()))
                    list.add(new FieldErrorException("fieldUniqueException", meta.getName()));
            } catch (NotFoundException ignE) {
            }
        }

        if (data.getValue() == null || data.getValue().length() == 0)
            list.add(new FieldErrorException("fieldRequiredException", meta.getValue()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "systemvariable", flag);
    }
}
