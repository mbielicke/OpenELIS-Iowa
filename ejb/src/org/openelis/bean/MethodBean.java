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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.Method;
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
import org.openelis.local.MethodLocal;
import org.openelis.meta.MethodMeta;
import org.openelis.remote.MethodRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class MethodBean implements MethodRemote, MethodLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private LockLocal               lock;

    private static final MethodMeta meta = new MethodMeta();
    
    public MethodDO fetchById(Integer id) throws Exception {
    	Query query;
    	MethodDO data;
    	
        query = manager.createNamedQuery("Method.FetchById");
        query.setParameter("id",id);
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
    public MethodDO fetchByName(String name) throws Exception {
        Query query;
        MethodDO data;
        
        query = manager.createNamedQuery("Method.FetchByName");
        query.setParameter("name", name);

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
    public ArrayList<MethodDO> fetchActiveByName(String name, int maxResults) {
        Query query = null;
        
        query = manager.createNamedQuery("Method.FetchActiveByName");        
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {        
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + MethodMeta.getId()
                          + "," + MethodMeta.getName() + ") ");
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
    	
    	if( !data.isChanged()) {
    	    lock.unlock(ReferenceTable.METHOD, data.getId());
    		return data;
    	}
    	checkSecurity(ModuleFlags.UPDATE);
    	
        validate(data);
    	
        lock.validateLock(ReferenceTable.METHOD, data.getId());
           
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Method.class, data.getId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setReportingDescription(data.getReportingDescription());

        lock.unlock(ReferenceTable.METHOD, data.getId());            
        
        return data;
    }
    
    public MethodDO fetchForUpdate(Integer id) throws Exception {        
        try {
            lock.lock(ReferenceTable.METHOD, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }
    
    public MethodDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.METHOD, id);
        return fetchById(id);
    }
    
    public void validate(MethodDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", MethodMeta.getName()));
        } else {
            MethodDO dup;
            
            try {
                dup = fetchByName(data.getName());
                if (DataBaseUtil.isDifferent(data.getId(), dup.getId()))
                    list.add(new FieldErrorException("fieldUniqueException", MethodMeta.getName()));
            } catch (NotFoundException ignE) {
            }
        }
        
        if (DataBaseUtil.isEmpty(data.getActiveBegin())) 
            list.add(new FieldErrorException("fieldRequiredException", MethodMeta.getActiveBegin()));
        
        if (DataBaseUtil.isEmpty(data.getActiveEnd())) 
            list.add(new FieldErrorException("fieldRequiredException", MethodMeta.getActiveEnd()));                
                
        if (DataBaseUtil.isAfter(data.getActiveBegin(),data.getActiveEnd())) 
            list.add(new FormErrorException("endDateAfterBeginDateException"));                   

        if (list.size() > 0)
            throw list;
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("method", flag);
    }
}
