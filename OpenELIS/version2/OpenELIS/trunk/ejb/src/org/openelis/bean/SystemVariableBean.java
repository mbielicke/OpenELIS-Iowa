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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.SystemVariable;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("systemvariable-select")
public class SystemVariableBean implements SystemVariableRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    @EJB
    private LockLocal lockBean;
    
    private static int systemVariableRefTableId;
    private static final SystemVariableMetaMap Meta = new SystemVariableMetaMap();

    public SystemVariableBean(){
        systemVariableRefTableId = ReferenceTableCache.getReferenceTable("system_variable");
    }   

    public SystemVariableDO getSystemVariable(Integer sysVarId) {
        Query query = manager.createNamedQuery("SystemVariable.SystemVariable");
        query.setParameter("id", sysVarId);
        SystemVariableDO sysVarDO = (SystemVariableDO) query.getSingleResult();
        
        return sysVarDO;
    }

    @RolesAllowed("systemvariable-update")
    public SystemVariableDO getSystemVariableAndLock(Integer sysVarId, String session) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "system_variable", ModuleFlags.UPDATE);
        lockBean.getLock(systemVariableRefTableId, sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public SystemVariableDO getSystemVariableAndUnlock(Integer sysVarId, String sesssion) {
        lockBean.giveUpLock(systemVariableRefTableId, sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
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
       SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "system_variable", ModuleFlags.UPDATE);
       Integer sysVarId;
       SystemVariable sysVar;
       
       sysVarId = sysVarDO.getId();
       if(sysVarId != null){
           // we need to call lock one more time to make sure their lock
           // didn't expire and someone else grabbed the record
           lockBean.validateLock(systemVariableRefTableId, sysVarId);
       }      
        
                
       sysVar = null;
        
       validateSystemVariable(sysVarDO);
       manager.setFlushMode(FlushModeType.COMMIT);
        
       if(sysVarDO.getId()==null){
           sysVar = new SystemVariable();            
       }else{
           sysVar = manager.find(SystemVariable.class, sysVarId);
       }
                    
       sysVar.setName(sysVarDO.getName());            
       sysVar.setValue(sysVarDO.getValue());
       
       if(sysVar.getId() == null){
           manager.persist(sysVar);
       }
                
       lockBean.giveUpLock(systemVariableRefTableId, sysVar.getId()); 
        
       return sysVar.getId();
    }  
    
   @RolesAllowed("systemvariable-delete")    
   public void deleteSystemVariable(Integer sysVarId) throws Exception {
       SystemVariable sysVar;
     
       lockBean.getLock(systemVariableRefTableId, sysVarId);  
     
       manager.setFlushMode(FlushModeType.COMMIT);
       sysVar = null;
       sysVar = manager.find(SystemVariable.class, sysVarId);
       if(sysVar !=null){
           manager.remove(sysVar);
       }             
       
       lockBean.giveUpLock(systemVariableRefTableId, sysVarId);
   }
    
    private void validateSystemVariable(SystemVariableDO sysVarDO) throws Exception{
        ValidationErrorsList exceptionList;
        
        exceptionList = new ValidationErrorsList();
        if(sysVarDO.getName() == null || "".equals(sysVarDO.getName())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getName()));
        }
        if(sysVarDO.getValue() == null|| "".equals(sysVarDO.getValue())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getValue()));
        }
        
        if(exceptionList.size() > 0)
            throw exceptionList;
    }
    
   

}
