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
import java.util.Date;
import java.util.Iterator;
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
import org.openelis.entity.AuxFieldValue;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.metamap.AuxFieldMetaMap;
import org.openelis.metamap.AuxFieldValueMetaMap;
import org.openelis.metamap.TestResultMetaMap;
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
        List<AuxFieldValueDO> auxfieldValues = null;
        AuxFieldValueDO valueDO = null;
        Integer typeId = null;
        String sysName = null;
        String entry = null;
        Integer val = null;
        
        Query query = manager.createNamedQuery("AuxFieldValue.AuxFieldValueDOList");
        query.setParameter("auxFieldId", auxFieldId);        
            auxfieldValues = query.getResultList();
            for (Iterator iter = auxfieldValues.iterator(); iter.hasNext();) {
             valueDO = (AuxFieldValueDO)iter.next();
             typeId = valueDO.getTypeId();
             query = manager.createNamedQuery("Dictionary.SystemNameById");             
             query.setParameter("id", typeId);
             try {
             sysName = (String)query.getSingleResult();
             if("aux_dictionary".equals(sysName)) {
                 val = Integer.parseInt(valueDO.getValue());
                 query = manager.createNamedQuery("Dictionary.EntryById");
                 query.setParameter("id", val);
                 entry = (String)query.getSingleResult();
                 valueDO.setDictEntry(entry);  
               } else {
                 valueDO.setDictEntry(null);  
              } 
             } catch(Exception ex) {
                 ex.printStackTrace();
             } 
            }
        
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
        Integer tableId = (Integer)query.getSingleResult();        
        lockBean.getLock(tableId, auxFieldGroupId);
        return getAuxFieldGroup(auxFieldGroupId);
    }

    public Integer updateAuxiliary(AuxFieldGroupDO auxFieldGroupDO,
                                   List<AuxFieldDO> auxFields) throws Exception {
     try {  
         
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "aux_field_group");
        Integer afgReferenceId = (Integer)query.getSingleResult();
        
        AuxFieldGroup auxFieldGroup = null;        
        List<AuxFieldValueDO> auxFieldValues = null;
        
        
        if (auxFieldGroupDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
         try {
               lockBean.validateLock(afgReferenceId,auxFieldGroupDO.getId());
             } catch(Exception ex) {
                throw ex;
           }  
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
                 af.setAuxFieldGroupId(auxFieldGroup.getId());
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
              
              auxFieldValues = afDO.getAuxFieldValues();
              if(auxFieldValues != null) {
                for(int j = 0 ; j < auxFieldValues.size(); j++) {
                    AuxFieldValueDO valueDO = auxFieldValues.get(j);
                    AuxFieldValue value = null;
                    if(valueDO.getId()==null) {
                        value =  new AuxFieldValue();                        
                    }else {
                        value = manager.find(AuxFieldValue.class, valueDO.getId());
                    }
                    
                    if(valueDO.getDelete() && valueDO.getId() !=null) {
                        manager.remove(value);
                    } else { 
                       if(!valueDO.getDelete()) { 
                         value.setAuxFieldId(af.getId());
                         value.setTypeId(valueDO.getTypeId());
                         value.setValue(valueDO.getValue());
                        
                        if(value.getId()== null) {
                            manager.persist(value);
                        }
                       } 
                    }
                }  
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
        TableFieldErrorException ex = null, auxfvEx = null;
        List<RPCException> exList = null;        
        for(int i = 0; i < auxFields.size(); i++) {
            AuxFieldDO afDO = auxFields.get(i);
            if(!afDO.getDelete()) {
               if(afDO.getAnalyteId() == null || afDO.getAnalyteId() == -1) {
                 ex = new TableFieldErrorException("fieldRequiredException", i,
                                                   AuxFieldGroupMeta.getAuxField().getAnalyte().getName(),
                                                   AuxFieldMetaMap.getTableName()); 
                 exceptionList.add(ex);                
             }  
               exList = validateAuxFieldValue(afDO.getAuxFieldValues());
               if(exList != null) {                   
                   auxfvEx = new TableFieldErrorException("errorsWithAuxFieldValuesException", i,
                                                     AuxFieldGroupMeta.getAuxField().getAnalyte().getName(),
                                                     AuxFieldMetaMap.getTableName());
                   auxfvEx.setChildExceptionList(exList);
                   exceptionList.add(auxfvEx); 
               }
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
                                          List<AuxFieldDO> auxFields) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateAuxFieldGroup(exceptionList, auxFieldGroupDO);
        if(auxFields!=null)
            validateAuxField(exceptionList, auxFields);        
        return exceptionList;
    }

    public List<Exception> validateForUpdate(AuxFieldGroupDO auxFieldGroupDO,
                                             List<AuxFieldDO> auxFields) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateAuxFieldGroup(exceptionList, auxFieldGroupDO);
        if(auxFields!=null)
            validateAuxField(exceptionList, auxFields);        
        return exceptionList;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception { 
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
    
    private List<RPCException> validateAuxFieldValue(List<AuxFieldValueDO> auxFieldValueDOList) {        
      AuxFieldValueDO valueDO = null;  
      Integer numId = null;
      Integer dictId = null;
      Integer typeId = null;
      Integer yesNoId = null; 
      Integer dateId = null;
      Integer dtId = null;
      Integer timeId = null;
      Integer blankId = new Integer(-1);
      String[] st = null;      
      ArrayList<String> dvl = new ArrayList<String>();;  
      ArrayList<Integer> rlist = null;
      List<RPCException> exList = new ArrayList<RPCException>();
      Date date = null;
               
      Double pnMax = null;
      Double cnMin = null;
      Double cnMax = null;                   
      
      String value = null;
      String hhmm = null;
      String defDate = "2000-01-01 ";
      String dateStr = null;
      
      Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
       
      query.setParameter("systemName", "aux_dictionary");
      dictId = (Integer)query.getSingleResult();
       
      query.setParameter("systemName", "aux_numeric");
      numId = (Integer)query.getSingleResult();
      
      query.setParameter("systemName", "aux_yes_no");
      yesNoId = (Integer)query.getSingleResult(); 
      
      query.setParameter("systemName", "aux_date");
      dateId = (Integer)query.getSingleResult(); 
      
      query.setParameter("systemName", "aux_date_time");
      dtId = (Integer)query.getSingleResult();
      
      query.setParameter("systemName", "aux_time");
      timeId = (Integer)query.getSingleResult();
      
      if(auxFieldValueDOList != null) {
        for(int i = 0 ; i < auxFieldValueDOList.size(); i++) {
            valueDO = auxFieldValueDOList.get(i);
            if(!valueDO.getDelete()) {  
               value = valueDO.getValue();
               typeId = valueDO.getTypeId(); 
               
            if(typeId == null || blankId.equals(typeId)) {                              
                exList.add(new TableFieldErrorException("fieldRequiredException", i,
                   AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getTypeId()));
            } else if(numId.equals(typeId)) {           
                if(value != null && !"".equals(value.trim())) {
                    st = value.split(",");                 
                    if(st.length != 2) {                                             
                      exList.add(new TableFieldErrorException("illegalNumericFormatException", i,
                        AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));    
                    } else {
                       try {                                      
                        cnMin = Double.valueOf(st[0]); 
                        cnMax = Double.valueOf(st[1]);
                                           
                        if(!(cnMin < cnMax)) {
                           exList.add(new TableFieldErrorException("illegalNumericRangeException", i,
                              AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));  
                        }else {                                                                                 
                            if(pnMax != null && !(cnMin > pnMax)) {
                              exList.add(new TableFieldErrorException("auxNumRangeOverlapException", i,
                                AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue())); 
                            }                                               
                        }
                        
                        pnMax = cnMax;                                                          
                        
                      } catch (NumberFormatException ex) {
                          exList.add(new TableFieldErrorException("illegalNumericRangeException", i,
                           AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));   
                      }                   
                    }
                 }else {
                     exList.add(new TableFieldErrorException("fieldRequiredException", i,
                      AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));       
                 }
              } else if(yesNoId.equals(typeId)) {           
                  if(value != null && !"".equals(value.trim())) {
                     if(!"Y".equals(value) && !"N".equals(value)) {
                       exList.add(new TableFieldErrorException("illegalYesNoValueException", i,
                        AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue())); 
                     }                      
                   }else {
                       exList.add(new TableFieldErrorException("fieldRequiredException", i,
                        AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));       
                   }
                } else if(dateId.equals(typeId)) {           
                    if(value != null && !"".equals(value.trim())) {
                     try{
                         date = new Date(value.replaceAll("-", "/"));
                     } catch (IllegalArgumentException ex) {
                         exList.add(new TableFieldErrorException("illegalDateValueException", i,
                          AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));   
                     }
                                                                                           
                    }else {
                          exList.add(new TableFieldErrorException("fieldRequiredException", i,
                           AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));       
                   }
                    date = null;
                } else if(dtId.equals(typeId)) {           
                    if(value != null && !"".equals(value.trim())) {
                        try{
                            st = value.split(" ");                             
                            if(st.length != 2)
                             throw new IllegalArgumentException();
                            
                            hhmm = st[1];
                            if(hhmm.split(":").length != 2) 
                                throw new IllegalArgumentException(); 
                            
                            date = new Date(value.replaceAll("-", "/"));
                        } catch (IllegalArgumentException ex) {
                            exList.add(new TableFieldErrorException("illegalDateTimeValueException", i,
                             AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));   
                        }
                                                                                              
                       }else {
                             exList.add(new TableFieldErrorException("fieldRequiredException", i,
                              AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));       
                      }
                       date = null;
                  } else if(timeId.equals(typeId)) {           
                      if(value != null && !"".equals(value.trim())) {
                          try{
                              st = value.split(":");                             
                              if(st.length != 2)
                               throw new IllegalArgumentException();
                              
                              dateStr = defDate + value;                                
                              date = new Date(dateStr.replaceAll("-", "/"));
                          } catch (IllegalArgumentException ex) {
                              exList.add(new TableFieldErrorException("illegalTimeValueException", i,
                               AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));   
                          }
                                                                                                
                         }else {
                               exList.add(new TableFieldErrorException("fieldRequiredException", i,
                                AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));       
                        }
                         date = null;
                    } else if(dictId.equals(typeId)) {
                        value = valueDO.getDictEntry();
                        if(value != null && !"".equals(value.trim())) {
                          if(!dvl.contains(value)) {
                            dvl.add(value); 
                          } else {
                            exList.add(new TableFieldErrorException("auxDictEntryNotUniqueException", i,
                             AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue())); 
                         }
                      
                         query = manager.createNamedQuery("Dictionary.IdByEntry");
                         query.setParameter("entry", value);
                         rlist = (ArrayList<Integer>)query.getResultList();
                     
                         if(rlist.size() == 0) {
                          exList.add(new TableFieldErrorException("illegalDictEntryException", i,
                           AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));  
                    }
                  } else {
                      exList.add(new TableFieldErrorException("fieldRequiredException", i,
                       AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue()));    
                  }
              }
          }     
        }  
      } 
      if(exList.size() == 0)
        exList = null;
      
      return exList;
    }
    
    

}
