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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.Constants;
import org.openelis.entity.Cron;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.CronMeta;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.FixedPeriodCron;

@Stateless
@SecurityDomain("openelis")
public class CronBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager         manager;

    @EJB
    private LockBean             lock;
    
    @EJB
    private UserCacheBean         userCache;

    @Resource
    SessionContext                ctx;

    private static final CronMeta meta = new CronMeta();

    public CronDO fetchById(Integer id) throws Exception {
        Query query;
        CronDO data;

        query = manager.createNamedQuery("Cron.FetchById");
        query.setParameter("id", id);

        try {
            data = (CronDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public List<Cron> fetchActive() {
        Query query = null;

        query = manager.createNamedQuery("Cron.FetchActive");

        return query.getResultList();
    }

    @SuppressWarnings("uncheked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 qb;
        List list;

        qb = new QueryBuilderV2();

        qb.setMeta(meta);

        qb.setSelect("distinct new org.openelis.domain.IdNameVO(" + CronMeta.getId() +
                     "," + CronMeta.getName() + ") ");

        qb.constructWhere(fields);

        qb.setOrderBy(CronMeta.getName());

        query = manager.createQuery(qb.getEJBQL());
        query.setMaxResults(first + max);
        QueryBuilderV2.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public CronDO add(CronDO data) throws Exception {
        Cron entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        validate(data);

        entity = new Cron();
        entity.setName(data.getName());
        entity.setIsActive(data.getIsActive());
        entity.setBean(data.getBean());
        entity.setCronTab(data.getCronTab());
        entity.setLastRun(data.getLastRunDate());
        entity.setMethod(data.getMethod());
        entity.setParameters(data.getParameters());
        try {
            manager.persist(entity);
            data.setId(entity.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return data;
    }

    public CronDO update(CronDO data) throws Exception {
        Cron entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().CRON, data.getId());
            return data;
        }

        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(Constants.table().CRON, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Cron.class, data.getId());
        entity.setName(data.getName());
        entity.setIsActive(data.getIsActive());
        entity.setBean(data.getBean());
        entity.setCronTab(data.getCronTab());
        entity.setLastRun(data.getLastRunDate());
        entity.setMethod(data.getMethod());
        entity.setParameters(data.getParameters());

        lock.unlock(Constants.table().CRON, data.getId());

        return data;
    }

    public void delete(CronDO data) throws Exception {
        Cron entity;

        checkSecurity(ModuleFlags.DELETE);

        lock.validateLock(Constants.table().CRON, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Cron.class, data.getId());

        if (entity != null)
            manager.remove(entity);

        lock.unlock(Constants.table().CRON, data.getId());
    }

    public CronDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().CRON, id);
            return fetchById(id);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public CronDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().CRON, id);
        return fetchById(id);
    }

    public void validate(CronDO data) throws Exception {
        ValidationErrorsList list;
        Object bean = null;
        Method[] methods;
        Method method = null;
        Object[] params;
        Class[] classes;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException", CronMeta.getName()));

        if (DataBaseUtil.isEmpty(data.getCronTab()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             CronMeta.getCronTab()));
        else {
            try {
                new FixedPeriodCron(data.getCronTab().trim());
            } catch (Exception e) {
                list.add(new FieldErrorException("invalidCronTab", CronMeta.getCronTab()));
            }
        }

        if (DataBaseUtil.isEmpty(data.getBean()))
            list.add(new FieldErrorException("fieldRequiredException", CronMeta.getBean()));
        else {
            try {
                bean = ctx.lookup(data.getBean());
            } catch (Exception e) {
                list.add(new FieldErrorException("invalidBeanPath", CronMeta.getBean()));
            }
        }

        if (DataBaseUtil.isEmpty(data.getMethod()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             CronMeta.getMethod()));
        else if (bean != null) {
            if ( !DataBaseUtil.isEmpty(data.getParameters())) {
                params = data.getParameters().split(";");
                classes = new Class[params.length];
                for (int i = 0; i < params.length; i++ ) {
                    classes[i] = String.class;
                }
            } else {
                classes = new Class[] {};
                params = new String[] {};
            }
            methods = bean.getClass().getMethods();
            for (Method mthd : methods) {
                if (mthd.getName().equals(data.getMethod().trim())) {
                    method = mthd;
                    break;
                }
            }
            if (method == null)
                list.add(new FieldErrorException("invalidMethod", CronMeta.getMethod()));
            else if (method.getParameterTypes().length != params.length)
                list.add(new FieldErrorException("invalidNumParams",
                                                 CronMeta.getParameters()));
        }

        if (list.size() > 0)
            throw list;

    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("cron", flag);
    }
}