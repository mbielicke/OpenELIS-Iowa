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
import org.openelis.domain.AuxFieldDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.entity.AuxFieldGroup;
import org.openelis.entity.Test;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.remote.AuxiliaryRemote;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
})
@SecurityDomain("openelis")
public class AuxiliaryBean implements AuxiliaryRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @EJB
    private SystemUserUtilLocal sysUser;

    @Resource
    private SessionContext ctx;

    private LockLocal lockBean;
    
    private static final AuxFieldGroupMetaMap AuxFieldGroupMeta = new AuxFieldGroupMetaMap();
    
    @PostConstruct
    private void init() {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }

    public List<AuxFieldValueDO> getAuxFieldValues(Integer auxFieldId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<AuxFieldDO> getAuxFields(Integer auxFieldGroupId) {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxFieldGroupDO getAuxFieldGroup(Integer auxFieldGroupId) {
        Query query = manager.createNamedQuery("AuxFieldGroup.AuxFieldGroupDO");
        query.setParameter("id", auxFieldGroupId);
        AuxFieldGroupDO axfgDO = (AuxFieldGroupDO)query.getSingleResult();
        return axfgDO;
    }
    
    public AuxFieldGroupDO getAuxFieldGroupAndUnlock(Integer auxFieldGroupId,
                                                     String session) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "aux_field_group");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), auxFieldGroupId);
        return getAuxFieldGroup(auxFieldGroupId);
    }
    
    public AuxFieldGroupDO getAuxFieldGroupAndLock(Integer auxFieldGroupId,
                                                   String session) throws Exception {
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "aux_field_group");
        lockBean.getLock((Integer)query.getSingleResult(), auxFieldGroupId);
        return getAuxFieldGroup(auxFieldGroupId);
    }

    public Integer updateAuxiliary(AuxFieldGroupDO auxFieldGroupDO,
                                   List<AuxFieldDO> auxFields,
                                   List<AuxFieldValueDO> auxFieldValues) throws Exception {
     try {  
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "aux_field_group");
        Integer afgReferenceId = (Integer)query.getSingleResult();

        if (auxFieldGroupDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.getLock(afgReferenceId, auxFieldGroupDO.getId());
        }

        manager.setFlushMode(FlushModeType.COMMIT);
        AuxFieldGroup auxFieldGroup =null;
        
        if(auxFieldGroupDO.getId() == null) {
          auxFieldGroup = new AuxFieldGroup();  
        } else {
            auxFieldGroup = manager.find(AuxFieldGroup.class,auxFieldGroupDO.getId()); 
        } 
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateAuxFieldGroup(exceptionList,auxFieldGroupDO);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        auxFieldGroup.setActiveBegin(auxFieldGroupDO.getActiveBegin());
        auxFieldGroup.setActiveEnd(auxFieldGroupDO.getActiveEnd());
        auxFieldGroup.setDescription(auxFieldGroupDO.getDescription());
        auxFieldGroup.setIsActive(auxFieldGroupDO.getIsActive());
        auxFieldGroup.setName(auxFieldGroupDO.getName());        
        
        if (auxFieldGroup.getId() == null) {
            manager.persist(auxFieldGroup);
        }
        lockBean.giveUpLock(afgReferenceId, auxFieldGroup.getId());
        return auxFieldGroup.getId();
    } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
    }
   }

    private void validateAuxFieldGroup(List<Exception> exceptionList,
                                       AuxFieldGroupDO auxFieldGroupDO) {
        boolean checkDuplicate = true;
        if (auxFieldGroupDO.getName() == null || "".equals(auxFieldGroupDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      AuxFieldGroupMeta.getName()));
            checkDuplicate = false;
        }
        
        if(auxFieldGroupDO !=null){ 
            if (auxFieldGroupDO.getDescription() == null || "".equals(auxFieldGroupDO.getDescription())) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          AuxFieldGroupMeta.getDescription()));
                checkDuplicate = false;
            }

            if (auxFieldGroupDO.getIsActive() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          AuxFieldGroupMeta.getIsActive()));
                checkDuplicate = false;
            }
            
            if (auxFieldGroupDO.getActiveBegin() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                           AuxFieldGroupMeta.getActiveBegin()));
                checkDuplicate = false;
            }

            if (auxFieldGroupDO.getActiveEnd() == null) {
                exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                          AuxFieldGroupMeta.getActiveEnd()));
                checkDuplicate = false;
            }
            
            if(checkDuplicate){
                if(auxFieldGroupDO.getActiveEnd().before(auxFieldGroupDO.getActiveBegin())){
                    exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));  
                    checkDuplicate = false;
                }
            }
            
          if(checkDuplicate){
             Query query = manager.createNamedQuery("AuxFieldGroup.AuxFieldGroupByName");
             query.setParameter("name", auxFieldGroupDO.getName());
             List<AuxFieldGroup> list = query.getResultList();
             for(int iter = 0; iter < list.size(); iter++){
                 boolean overlap = false;
                 AuxFieldGroup auxFieldGroup = (AuxFieldGroup)list.get(iter);
                 if(!auxFieldGroup.getId().equals(auxFieldGroupDO.getId())){                 
                      if(auxFieldGroup.getIsActive().equals(auxFieldGroupDO.getIsActive())){
                          if("Y".equals(auxFieldGroupDO.getIsActive())){
                              exceptionList.add(new FormErrorException("auxFieldGroupActiveException"));                                   
                              break; 
                           }else{
                             // exceptionList.add(new FormErrorException("testInactiveTimeOverlap"));  
                            }                              
                     }
                      if(auxFieldGroup.getActiveBegin().before(auxFieldGroupDO.getActiveEnd())&&
                                      (auxFieldGroup.getActiveEnd().after(auxFieldGroupDO.getActiveBegin()))){
                          overlap = true;  
                       }else if(auxFieldGroup.getActiveBegin().equals(auxFieldGroupDO.getActiveEnd())||
                                   (auxFieldGroup.getActiveEnd().equals(auxFieldGroupDO.getActiveBegin()))){
                                overlap = true;                  
                       }
                      
                     if(overlap){
                         exceptionList.add(new FormErrorException("auxFieldGroupTimeOverlapException"));
                     } 
                 }
               } 
          }
        }
        
    }

    public List<Exception> validateForAdd(AuxFieldGroupDO auxFieldGroupDO,
                                          List<AuxFieldDO> auxFields,
                                          List<AuxFieldValueDO> auxFieldValues) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Exception> validateForUpdate(AuxFieldGroupDO auxFieldGroupDO,
                                             List<AuxFieldDO> auxFields,
                                             List<AuxFieldValueDO> auxFieldValues) {
        return null;
    }

    public List query(HashMap fields, int first, int max) throws Exception { 
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(AuxFieldGroupMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO("
                     +AuxFieldGroupMeta.getId()+", "+AuxFieldGroupMeta.getName()+") ");               
        
        qb.addWhere(fields);

        qb.setOrderBy(AuxFieldGroupMeta.getName());

        sb.append(qb.getEJBQL());                

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }
}
