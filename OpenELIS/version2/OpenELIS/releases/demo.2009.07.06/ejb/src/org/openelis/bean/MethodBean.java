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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.MethodDO;
import org.openelis.entity.Method;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.remote.MethodRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs( {@EJB(name = "ejb/Lock", beanInterface = LockLocal.class)})
@SecurityDomain("openelis")
public class MethodBean implements MethodRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;

    private LockLocal lockBean;

    private static MethodMetaMap MethodMeta = new MethodMetaMap();

    @PostConstruct
    private void init()

    {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");

    }

    public MethodDO getMethod(Integer methodId) {
        Query query = manager.createNamedQuery("Method.MethodById");
        query.setParameter("id", methodId);
        MethodDO methodDO = (MethodDO)query.getSingleResult();
        return methodDO;
    }

    public MethodDO getMethodAndLock(Integer methodId, String session) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "method");

        lockBean.getLock((Integer)query.getSingleResult(), methodId);

        return getMethod(methodId);
    }

    public MethodDO getMethodAndUnlock(Integer methodId, String session) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "method");

        lockBean.giveUpLock((Integer)query.getSingleResult(), methodId);

        return getMethod(methodId);
    }

    public List autoCompleteLookupByName(String name, int maxResults) {
        Query query = null;
        List entryList = null;
        query = manager.createNamedQuery("Method.AutoCompleteByName");        
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        try {
            entryList = (List)query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return entryList;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(MethodMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + MethodMeta.getId()
                     + ", "
                     + MethodMeta.getName()
                     + ") ");

        qb.addWhere(fields);

        qb.setOrderBy(MethodMeta.getName());

        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }

    public Integer updateMethod(MethodDO methodDO) throws Exception {
        try {
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "method");
            Integer methodReferenceId = (Integer)query.getSingleResult();
            Integer methodId = methodDO.getId();

            if (methodId != null) {
                // we need to call lock one more time to make sure their lock
                // didnt expire and someone else grabbed the record
                lockBean.validateLock(methodReferenceId, methodId);
            }
            
            validateMethod(methodDO);

            manager.setFlushMode(FlushModeType.COMMIT);
            Method method = null;           

            if (methodId == null) {
                method = new Method();
            } else {
                method = manager.find(Method.class, methodId);
            }

            method.setActiveBegin(methodDO.getActiveBegin());
            method.setActiveEnd(methodDO.getActiveEnd());
            method.setDescription(methodDO.getDescription());
            method.setIsActive(methodDO.getIsActive());
            method.setName(methodDO.getName());
            method.setReportingDescription(methodDO.getReportingDescription());

            if (method.getId() == null) {
                manager.persist(method);
            }

            lockBean.giveUpLock(methodReferenceId, method.getId());
            return method.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
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
            query = manager.createNamedQuery("Test.TestListByMethodId");
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
            query = manager.createNamedQuery("Method.MethodByName");
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
