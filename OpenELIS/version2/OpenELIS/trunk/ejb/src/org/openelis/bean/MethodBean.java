package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.MethodDO;
import org.openelis.entity.Method;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.remote.MethodRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
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
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        
    }

    public MethodDO getMethod(Integer methodId) {
        Query query = manager.createNamedQuery("Method.MethodById");
        query.setParameter("id", methodId);
        MethodDO methodDO = (MethodDO)query.getSingleResult();        
        return methodDO;
    }

    public MethodDO getMethodAndLock(Integer methodId, String session)throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "method");

        lockBean.getLock((Integer)query.getSingleResult(),methodId);
        
        return getMethod(methodId);
    }

    public MethodDO getMethodAndUnlock(Integer methodId, String session) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "method");
        
        lockBean.giveUpLock((Integer)query.getSingleResult(),methodId);
        
        return getMethod(methodId);
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();        
        
        //CategoryMeta categoryMeta = CategoryMeta.getInstance();
        //DictionaryMeta dictionaryMeta = DictionaryMeta.getInstance();
        //DictionaryRelatedEntryMeta dicRelatedEntryMeta = DictionaryRelatedEntryMeta.getInstance();
        
        //qb.addMeta(new Meta[]{categoryMeta, dictionaryMeta, dicRelatedEntryMeta});
        qb.setMeta(MethodMeta);
        
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+MethodMeta.getId()+", "+MethodMeta.getName() + ") ");
        
        //qb.addTable(categoryMeta);
        
        qb.addWhere(fields);      

        qb.setOrderBy(MethodMeta.getName());
        
        //if(qb.hasTable(dicRelatedEntryMeta.getTable()))
        //  qb.addTable(dictionaryMeta);
        
        sb.append(qb.getEJBQL());
        
        Query query = manager.createQuery(sb.toString());
       
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    public Integer updateMethod(MethodDO methodDO) throws Exception {
       try{ 
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "method");
        Integer methodReferenceId = (Integer)query.getSingleResult();
        
        if(methodDO.getId() != null){
         //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(methodReferenceId, methodDO.getId());
         }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        Method method = null;
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateMethod(exceptionList,methodDO);  
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        if(methodDO.getId()==null){
            method = new Method();
        }else{
            method = manager.find(Method.class, methodDO.getId());
        }
        
        method.setActiveBegin(methodDO.getActiveBegin());
        method.setActiveEnd(methodDO.getActiveEnd());
        method.setDescription(methodDO.getDescription());
        method.setIsActive(methodDO.getIsActive());
        method.setName(methodDO.getName());
        method.setReportingDescription(methodDO.getReportingDescription());
        
        if(method.getId() == null){
            manager.persist(method);
        }
        
        lockBean.giveUpLock(methodReferenceId, method.getId());
        return method.getId();
       } catch (Exception ex) {
           ex.printStackTrace();
           throw ex;
       }
    }

    public List validateForAdd(MethodDO methodDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateMethod(exceptionList,methodDO);
        return exceptionList;
    }

    public List validateForUpdate(MethodDO methodDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateMethod(exceptionList,methodDO);
        return exceptionList;
    }
    
    private void validateMethod(List<Exception> exceptionList,MethodDO methodDO){
        boolean checkDuplicate = true;
        
        if (methodDO.getName() == null || "".equals(methodDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getName()));    
            checkDuplicate = false;
        }
        if (methodDO.getIsActive() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getIsActive()));    
            checkDuplicate = false;
        }
        if (methodDO.getActiveBegin() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getActiveBegin()));
            checkDuplicate = false;
        }
        if (methodDO.getActiveEnd() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      MethodMeta.getActiveEnd()));  
            checkDuplicate = false;
        }
        
        if(checkDuplicate){
            if(methodDO.getActiveEnd().before(methodDO.getActiveBegin())){
                exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));  
                checkDuplicate = false;
            }
        }
        
        if(checkDuplicate){
            Query query = manager.createNamedQuery("Method.MethodByName");
            query.setParameter("name", methodDO.getName());
            List<Method> list = query.getResultList();
            
            for(int iter = 0; iter < list.size(); iter++){
                boolean overlap = false;
                Method method = (Method)list.get(iter);
                if(!method.getId().equals(methodDO.getId())){
                    if(method.getIsActive().equals(methodDO.getIsActive())){
                        if("Y".equals(methodDO.getIsActive())){
                            exceptionList.add(new FormErrorException("methodActiveException"));                               
                            break;  
                        }
                        if(method.getActiveBegin().before(methodDO.getActiveEnd())&&
                                        (method.getActiveEnd().after(methodDO.getActiveBegin()))){
                            overlap = true;  
                         }else if(method.getActiveBegin().equals(methodDO.getActiveEnd())||
                                     (method.getActiveEnd().equals(methodDO.getActiveBegin()))){
                                  overlap = true;                  
                         }
                        
                       if(overlap){
                           exceptionList.add(new FormErrorException("methodTimeOverlapException"));
                       }  
                        
                }
           }
         }
        
     }
   }
}
