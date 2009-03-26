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
import org.openelis.domain.IdNameDO;
import org.openelis.entity.AuxField;
import org.openelis.entity.AuxFieldGroup;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.metamap.AuxFieldMetaMap;
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
        Query query = manager.createNamedQuery("AuxFieldValue.AuxFieldValueDOList");
        query.setParameter("auxFieldId", auxFieldId);
        List<AuxFieldValueDO> auxfieldValues = query.getResultList();
        return auxfieldValues;
    }

    public List<AuxFieldDO> getAuxFields(Integer auxFieldGroupId) {
        Query query = manager.createNamedQuery("AuxField.AuxFieldDOList");
        query.setParameter("auxFieldGroupId", auxFieldGroupId);
        List<AuxFieldDO> auxfields = query.getResultList();
        return auxfields;
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
        AuxFieldGroup auxFieldGroup = null;

        if (auxFieldGroupDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.getLock(afgReferenceId, auxFieldGroupDO.getId());
        }

        manager.setFlushMode(FlushModeType.COMMIT);        
        
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
        
        if(auxFields!=null) {
         for(int i = 0 ; i < auxFields.size(); i++) {
             AuxFieldDO afDO = auxFields.get(i);
             AuxField af =null;
             if(afDO.getId() == null) {
                 af = new AuxField();
             } else {
                 af = manager.find(AuxField.class, afDO.getId());
             }
             
             if(afDO.getDelete() && afDO.getId() !=null) {
                 manager.remove(af);
             } else { 
                if(!afDO.getDelete()) {  
                 af.setAnalyteId(afDO.getAnalyteId());
                 af.setAuxFieldGroupId(afDO.getAuxFieldGroupId());
                 af.setDescription(afDO.getDescription());
                 af.setIsActive(afDO.getIsActive());
                 af.setIsReportable(afDO.getIsReportable());
                 af.setIsRequired(afDO.getIsRequired());
                 af.setMethodId(afDO.getMethodId());
                 af.setScriptletId(afDO.getScriptletId());
                 af.setSortOrder(afDO.getSortOrder());
                 af.setUnitOfMeasureId(afDO.getUnitOfMeasureId());
           
              if(af.getId() == null){
                 manager.persist(af);
             }
            } 
         }     
        }
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
    
    private void validateAuxField(List<Exception> exceptionList,List<AuxFieldDO> auxFields) {
        for(int i = 0; i < auxFields.size(); i++){
            AuxFieldDO afDO = auxFields.get(i);
            if(!afDO.getDelete() && afDO.getAnalyteId()==null){
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", i,
                    AuxFieldMetaMap.getTableName()+":"+AuxFieldGroupMeta.getAuxField().getAnalyteId()));
            }            
        }
    }
    
    public List getMatchingEntries(String name, int maxResults,String cat) {
        Query query = null;
        List entryList = null;
        if("analyte".equals(cat)) {
         query = manager.createNamedQuery("Analyte.AutoCompleteByName");              
        }else if("method".equals(cat)) {
          query = manager.createNamedQuery("Method.AutoCompleteByName"); 
          query.setParameter("isActive", "Y");
        }
         query.setParameter("name", name);       
         query.setMaxResults(maxResults);            
        try{ 
            entryList = (List)query.getResultList();
        }catch(Exception ex){
            ex.printStackTrace();
           
        }     
        return entryList;
    }

    public List<Exception> validateForAdd(AuxFieldGroupDO auxFieldGroupDO,
                                          List<AuxFieldDO> auxFields,
                                          List<AuxFieldValueDO> auxFieldValues) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateAuxFieldGroup(exceptionList, auxFieldGroupDO);
        if(auxFields!=null)
            validateAuxField(exceptionList, auxFields);        
        return exceptionList;
    }

    public List<Exception> validateForUpdate(AuxFieldGroupDO auxFieldGroupDO,
                                             List<AuxFieldDO> auxFields,
                                             List<AuxFieldValueDO> auxFieldValues) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateAuxFieldGroup(exceptionList, auxFieldGroupDO);
        return exceptionList;
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

    public List<IdNameDO> getScriptletDropDownValues() {
        Query query = manager.createNamedQuery("Scriptlet.Scriptlet");
        List<IdNameDO> scriptletList = query.getResultList();
        return scriptletList;
    }

}
