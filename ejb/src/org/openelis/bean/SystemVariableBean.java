package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.SystemVariable;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.meta.SystemVariableMeta;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("systemvariable-select")
public class SystemVariableBean implements SystemVariableRemote{


    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Integer getSystemUserId() {
      try {
           SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                             .getName());
           return systemUserDO.getId();
         } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
       }
    }

    public SystemVariableDO getSystemVariable(Integer sysVarId) {
        Query query = manager.createNamedQuery("getSystemVariable");
        query.setParameter("id", sysVarId);
        SystemVariableDO sysVarDO = (SystemVariableDO) query.getSingleResult();
        
        return sysVarDO;
    }

    @RolesAllowed("systemvariable-update")
    public SystemVariableDO getSystemVariableAndLock(Integer sysVarId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "system_variable");
        lockBean.getLock((Integer)query.getSingleResult(),sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public SystemVariableDO getSystemVariableAndUnlock(Integer sysVarId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "system_variable");
        lockBean.giveUpLock((Integer)query.getSingleResult(),sysVarId);
        
        return getSystemVariable(sysVarId);
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        SystemVariableMeta sysVarMeta = SystemVariableMeta.getInstance();
        
        QueryBuilder qb = new QueryBuilder();
        qb.addMeta(new Meta[]{sysVarMeta});
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+SystemVariableMeta.ID+" , "+SystemVariableMeta.NAME + ") ");
        qb.addTable(sysVarMeta);
        qb.addWhere(fields);
        
        qb.setOrderBy(SystemVariableMeta.NAME);
        
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
            exceptionList.add(new FieldErrorException("fieldRequiredException",SystemVariableMeta.NAME));
        }
        if(sysVarDO.getValue()==null|| "".equals(sysVarDO.getValue())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",SystemVariableMeta.VALUE));
        }
    }
    
   

}
