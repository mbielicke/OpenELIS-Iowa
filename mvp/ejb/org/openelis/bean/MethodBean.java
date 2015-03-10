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
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.messages.Messages;
import org.openelis.messages.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.entity.Method;
import org.openelis.entity.Method_;
import org.openelis.meta.MethodMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.QueryBuilderV2;
import org.openelis.ui.util.QueryUtil;

@Stateless
@SecurityDomain("openelis")
public class MethodBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager           manager;

    @EJB
    LockBean               lock;
    
    @EJB
    UserCacheBean           userCache;

    private static final MethodMeta meta = new MethodMeta();

    public MethodDO fetchById(Integer id) throws Exception {
        Query query;
        MethodDO data;

        query = manager.createNamedQuery("Method.FetchById");
        query.setParameter("id", id);
        try {
            data = (MethodDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MethodDO> fetchByName(String name, int maxResults) {
        Query query;

        query = manager.createNamedQuery("Method.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MethodDO> fetchActiveByName(String name, int maxResults) {
        Query query = null;

        query = manager.createNamedQuery("Method.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MethodDO> fetchByIds(Collection<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("Method.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          MethodMeta.getId() + "," + MethodMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(MethodMeta.getName());

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
    
    @SuppressWarnings({"unchecked","rawtypes"})
	public ArrayList<IdNameVO> critQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        TypedQuery<IdNameVO> query;
        List<IdNameVO> list;
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<IdNameVO> critQuery = builder.createQuery(IdNameVO.class);
        Root<Method> method = critQuery.from(Method.class); 
        
        critQuery.select(builder.construct(IdNameVO.class, method.get(Method_.id),method.get(Method_.name)));
		critQuery.where(new QueryUtil(builder,critQuery,method).createQuery(fields));
        critQuery.orderBy(builder.asc(method.get(Method_.name)));

        query = manager.createQuery(critQuery);
        query.setMaxResults(first + max);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public MethodDO add(MethodDO data) throws Exception {
        Method entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Method();
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setReportingDescription(data.getReportingDescription());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;

    }

    public MethodDO update(MethodDO data) throws Exception {
        Method entity;

        if ( !data.isChanged()) {
            lock.unlock(Constants.table().METHOD, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(Constants.table().METHOD, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Method.class, data.getId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setReportingDescription(data.getReportingDescription());

        lock.unlock(Constants.table().METHOD, data.getId());

        return data;
    }

    public MethodDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().METHOD, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public MethodDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().METHOD, id);
        return fetchById(id);
    }

    public void validate(MethodDO data) throws Exception {
        boolean checkDuplicate, overlap;
        MethodDO dup;
        Query query;
        ValidationErrorsList errors;
        List<MethodDO> dups;        

        errors = new ValidationErrorsList();
        checkDuplicate = true;

        if (DataBaseUtil.isEmpty(data.getName())) {
            errors.add(new FieldErrorException(getMessages().fieldRequiredException(),
                                             MethodMeta.getName()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveBegin())) {
            errors.add(new FieldErrorException(getMessages().fieldRequiredException(),
                                             MethodMeta.getActiveBegin()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            errors.add(new FieldErrorException(getMessages().fieldRequiredException(),
                                             MethodMeta.getActiveEnd()));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isAfter(data.getActiveBegin(), data.getActiveEnd())) {
            errors.add(new FormErrorException(getMessages().endDateAfterBeginDateException()));
            checkDuplicate = false;
        }
        
        if (checkDuplicate) {
            query = manager.createNamedQuery("Method.FetchByName");
            query.setParameter("name", data.getName());
            dups = query.getResultList();
            for (int i = 0; i < dups.size(); i++ ) {
                overlap = false;
                dup = (MethodDO)dups.get(i);
                if (DataBaseUtil.isDifferent(dup.getId(), data.getId())&&
                    DataBaseUtil.isSame(dup.getIsActive(), data.getIsActive())) {
                    if ("Y".equals(data.getIsActive())) {
                        errors.add(new FormErrorException(getMessages().methodActiveException()));
                        break;
                    }

                    if (DataBaseUtil.isAfter(data.getActiveEnd(), dup.getActiveBegin()) &&
                        DataBaseUtil.isAfter(dup.getActiveEnd(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveBegin(), dup.getActiveBegin()) &&
                               DataBaseUtil.isAfter(dup.getActiveEnd(), data.getActiveEnd())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveEnd(), dup.getActiveEnd()) &&
                               DataBaseUtil.isAfter(dup.getActiveBegin(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(dup.getActiveEnd(), data.getActiveEnd()) &&
                               DataBaseUtil.isAfter(data.getActiveBegin(), dup.getActiveBegin())) {
                        overlap = true;
                    } else if (!DataBaseUtil.isDifferentYD(dup.getActiveBegin(),
                                                            data.getActiveEnd()) ||
                               !DataBaseUtil.isDifferentYD(dup.getActiveEnd(),
                                                           data.getActiveBegin())) {
                        overlap = true;
                    } else if (!DataBaseUtil.isDifferentYD(dup.getActiveBegin(),
                                                            data.getActiveBegin()) ||
                               (!DataBaseUtil.isDifferentYD(dup.getActiveEnd(),
                                                             data.getActiveEnd()))) {
                        overlap = true;
                    }

                    if (overlap)
                        errors.add(new FormErrorException(getMessages().methodTimeOverlapException()));                    
                }
            }
        }

        if (errors.size() > 0)
            throw errors;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("method", flag);
    }
    
    protected OpenELISConstants getMessages() {
        return Messages.get();
    }
    
}