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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.SystemVariable;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("systemvariable-select")
public class SystemVariableBean implements SystemVariableRemote{


    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final SystemVariableMetaMap Meta = new SystemVariableMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    public SystemVariableDO getSystemVariable(Integer sysVarId) {
        Query query = manager.createNamedQuery("SystemVariable.SystemVariable");
        query.setParameter("id", sysVarId);
        SystemVariableDO sysVarDO = (SystemVariableDO) query.getSingleResult();
        
        return sysVarDO;
    }

    @RolesAllowed("systemvariable-update")
    public SystemVariableDO getSystemVariableAndLock(Integer sysVarId, String session) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "system_variable");
        lockBean.getLock((Integer)query.getSingleResult(),sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public SystemVariableDO getSystemVariableAndUnlock(Integer sysVarId, String sesssion) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "system_variable");
        lockBean.giveUpLock((Integer)query.getSingleResult(),sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();        
        
        QueryBuilder qb = new QueryBuilder();
        //qb.addMeta(new Meta[]{sysVarMeta});
        qb.setMeta(Meta);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+Meta.getId()+", "+Meta.getName() + ") ");
        //qb.addTable(sysVarMeta);
        qb.addWhere(fields);
        
        qb.setOrderBy(Meta.getName());
        
        sb.append(qb.getEJBQL());            
        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
            query.setMaxResults(first+max);
                
        //***set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
        
    }

   @RolesAllowed("systemvariable-update")
    public Integer updateSystemVariable(SystemVariableDO sysVarDO) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "system_variable");
        Integer sysVarReferenceId = (Integer)query.getSingleResult();
        
        if(sysVarDO.getId() != null){
            lockBean.getLock(sysVarReferenceId, sysVarDO.getId());   
        }
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SystemVariable sysVar = null;
        List<Exception> exceptionList = new ArrayList<Exception>();
        validate(sysVarDO,exceptionList);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        if(sysVarDO.getId()==null){
            sysVar = new SystemVariable();            
        }else{
            sysVar = manager.find(SystemVariable.class, sysVarDO.getId());
        }
                    
        sysVar.setName(sysVarDO.getName());            
        sysVar.setValue(sysVarDO.getValue());
       
        if(sysVar.getId() == null){
            manager.persist(sysVar);
        }
                
        lockBean.giveUpLock(sysVarReferenceId,sysVar.getId()); 
        
        return sysVar.getId();
    }  
    
   @RolesAllowed("systemvariable-delete")    
    public void deleteSystemVariable(Integer sysVarId) throws Exception {
     Query lockQuery = manager.createNamedQuery("getTableId");
     lockQuery.setParameter("name", "system_variable");
     Integer sysvarTableId = (Integer)lockQuery.getSingleResult();
     lockBean.getLock(sysvarTableId, sysVarId);  
     
     manager.setFlushMode(FlushModeType.COMMIT);
      SystemVariable sysVar = null;
      try{
          sysVar = manager.find(SystemVariable.class, sysVarId);
          if(sysVar !=null){
              manager.remove(sysVar);
          }
       }catch (Exception e) {
          e.printStackTrace();
          throw e;
      } 
       
       lockBean.giveUpLock(sysvarTableId, sysVarId);
    }
    
    public List<Exception> validateforAdd(SystemVariableDO sysVarDO){
       List<Exception> exceptionList = new ArrayList<Exception>(); 
       validate(sysVarDO,exceptionList);        
       return exceptionList; 
    }

    public List<Exception> validateforUpdate(SystemVariableDO sysVarDO) {
        List<Exception> exceptionList = new ArrayList<Exception>(); 
        validate(sysVarDO,exceptionList);        
        return exceptionList; 
    }
    
    private void validate(SystemVariableDO sysVarDO,List<Exception> exceptionList){
        if(sysVarDO.getName()==null || "".equals(sysVarDO.getName())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getName()));
        }
        if(sysVarDO.getValue()==null|| "".equals(sysVarDO.getValue())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getValue()));
        }
    }
    
   

}
