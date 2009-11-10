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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.Method;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.remote.MethodRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@RolesAllowed("method-select")
@SecurityDomain("openelis")
public class MethodBean implements MethodRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal lockBean;

    private static MethodMetaMap MethodMeta = new MethodMetaMap();

    private static Integer methodRefTableId; 
    
    public MethodBean() {
        methodRefTableId = ReferenceTable.METHOD;
    } 
    
    public MethodDO fetchById(Integer id) {
    	Query query;
    	MethodDO methodDO;
    	
        query = manager.createNamedQuery("Method.FetchById");
        query.setParameter("id",id);
        methodDO = (MethodDO)query.getSingleResult();
        
        return methodDO;
    }
    
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        QueryBuilderV2 qb = new QueryBuilderV2();
        List list;

        qb.setMeta(MethodMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameVO(" + MethodMeta.getId()
                     + ", "
                     + MethodMeta.getName()
                     + ") ");

        qb.constructWhere(fields);

        qb.setOrderBy(MethodMeta.getName());

        Query query = manager.createQuery(qb.getEJBQL());

        if (first > -1 && max > -1)
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
    
    public MethodDO add(MethodDO data) throws Exception {
    	Method entity;
    	
        Integer methodId = data.getId();

        checkSecurity(ModuleFlags.ADD);
                    
        validateMethod(data);

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
    	
    	if( !data.isChanged())
    		return data;
    	
    	checkSecurity(ModuleFlags.UPDATE);
    	
        validateMethod(data);
    	
        lockBean.validateLock(methodRefTableId, data.getId());
           
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Method.class, data.getId());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setReportingDescription(data.getReportingDescription());

        lockBean.giveUpLock(methodRefTableId, data.getId());            
        
        return data;
    }
    
    public MethodDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(methodRefTableId, id);
        return fetchById(id);
    }
    
    public MethodDO abortUpdate(Integer id) {
        lockBean.giveUpLock(methodRefTableId, id);
        return fetchById(id);
    }

    @SuppressWarnings("unchecked")
	public ArrayList<IdNameVO> findByName(String name, int maxResults) {
        Query query = null;
        
        query = manager.createNamedQuery("Method.FetchActiveByName");        
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "method", flag);
    }
    
    private void validateMethod(MethodDO methodDO) throws Exception {
        ValidationErrorsList exceptionList;
        boolean checkDuplicate, overlap;
        Datetime activeEnd, activeBegin;
        Integer id;
        String active;
        Method method;
        int iter;
        List list;
        Query query;

        checkDuplicate = true;
        activeBegin = methodDO.getActiveBegin();
        activeEnd = methodDO.getActiveEnd();
        id = methodDO.getId();
        active = methodDO.getIsActive();
        exceptionList = new ValidationErrorsList();

        if (methodDO.getName() == null || "".equals(methodDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getName()));
            checkDuplicate = false;
        }
        
        if (active == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getIsActive()));
            checkDuplicate = false;
        } else if("N".equals(active)) {
            query = manager.createNamedQuery("Test.FetchByMethod");
            query.setParameter("id", methodDO.getId());
            list = query.getResultList();
            if(list.size() > 0) {
                exceptionList.add(new FormErrorException("methodAssignedToActiveTestException"));
                checkDuplicate = false;
            }
        } 
        
        if (activeBegin == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getActiveBegin()));
            checkDuplicate = false;
        }
        if (activeEnd == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getActiveEnd()));
            checkDuplicate = false;
        }
        
        
        
        if (checkDuplicate) {
            if (activeEnd.before(activeBegin)) {
                exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));
                checkDuplicate = false;
            }
        }               

        if (checkDuplicate) {
            query = manager.createNamedQuery("Method.FetchEntityByName");
            query.setParameter("name", methodDO.getName());
            list = query.getResultList();

            for (iter = 0; iter < list.size(); iter++) {
                overlap = false;
                method = (Method)list.get(iter);
                if (!method.getId().equals(id)) {
                    if (method.getIsActive().equals(active)) {
                        if ("Y".equals(active)) {
                            exceptionList.add(new FormErrorException("methodActiveException"));
                            break;
                        }
                        if (method.getActiveBegin().before(activeEnd) && (method.getActiveEnd().after(activeBegin))) {
                            overlap = true;
                        } else if(method.getActiveBegin().before(methodDO.getActiveBegin())&&
                                        (method.getActiveEnd().after(methodDO.getActiveEnd()))){
                            overlap = true;  
                        } else if (method.getActiveBegin().equals(activeEnd) || (method.getActiveEnd().equals(activeBegin))) {
                            overlap = true;
                        } else if (method.getActiveBegin().equals(activeBegin) || (method.getActiveEnd().equals(activeEnd))) {
                            overlap = true;
                        }

                        if (overlap) {
                            exceptionList.add(new FormErrorException("methodTimeOverlapException"));
                        }

                    }
                }
            }
        }
        if (exceptionList.size() > 0)
            throw exceptionList;
    }

}
