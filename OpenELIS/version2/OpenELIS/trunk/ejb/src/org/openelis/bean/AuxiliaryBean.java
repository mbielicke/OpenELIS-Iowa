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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.AuxField;
import org.openelis.entity.AuxFieldGroup;
import org.openelis.exception.InconsistentException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AuxFieldGroupMetaMap;
import org.openelis.remote.AuxiliaryRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.NumericRange;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
public class AuxiliaryBean implements AuxiliaryRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    @EJB
    private DictionaryLocal dictionaryBean;

    @EJB
    private LockLocal lockBean;
    
    private static Integer auxFGRefTableId;
    
    public AuxiliaryBean() {
        auxFGRefTableId = ReferenceTable.AUX_FIELD_GROUP;
    }
    
    private static final AuxFieldGroupMetaMap AuxFieldGroupMeta = new AuxFieldGroupMetaMap();     

    public List<AuxFieldValueDO> getAuxFieldValues(Integer auxFieldId) {        
        List<AuxFieldValueDO> auxfieldValues;
        AuxFieldValueDO valueDO;
        Integer typeId,val;
        String sysName,entry;       
        DictionaryViewDO snDO, entDO;
        
        Query query = manager.createNamedQuery("AuxFieldValue.FetchById");
        query.setParameter("auxFieldId", auxFieldId);        
            auxfieldValues = query.getResultList();
            for (Iterator iter = auxfieldValues.iterator(); iter.hasNext();) {
                 valueDO = (AuxFieldValueDO)iter.next();
                 typeId = valueDO.getTypeId();
                 query = manager.createNamedQuery("Dictionary.FetchById");             
                 query.setParameter("id", typeId);
                 try {
                     snDO = (DictionaryViewDO)query.getSingleResult();
                     sysName = snDO.getSystemName();
                     if("aux_dictionary".equals(sysName)) {
                         val = Integer.parseInt(valueDO.getValue());
                         query = manager.createNamedQuery("Dictionary.FetchById");
                         query.setParameter("id", val);
                         entDO = (DictionaryViewDO)query.getSingleResult();
                         entry = entDO.getEntry();
                         valueDO.setValue(entry);  
                     } 
                 } catch(Exception ex) {
                     ex.printStackTrace();
                 } 
            }
        
        return auxfieldValues;
    }

    public List<AuxFieldViewDO> getAuxFields(Integer auxFieldGroupId) {
        Query query = manager.createNamedQuery("AuxField.FetchAllByGroupId");
        query.setParameter("auxFieldGroupId", auxFieldGroupId);
        List<AuxFieldViewDO> auxfields = query.getResultList();
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
        lockBean.giveUpLock(auxFGRefTableId, auxFieldGroupId);
        return getAuxFieldGroup(auxFieldGroupId);
    }
    
    public AuxFieldGroupDO getAuxFieldGroupAndLock(Integer auxFieldGroupId,
                                                   String session) throws Exception {         
        lockBean.getLock(auxFGRefTableId, auxFieldGroupId);
        return getAuxFieldGroup(auxFieldGroupId);
    }

    public Integer updateAuxiliary(AuxFieldGroupDO auxFieldGroupDO,
                                   List<AuxFieldViewDO> auxFields) throws Exception {
     try {           
        
        AuxFieldGroup auxFieldGroup = null;        
        List<AuxFieldValueDO> auxFieldValues = null;
                
        if (auxFieldGroupDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.validateLock(auxFGRefTableId,auxFieldGroupDO.getId());                         
        }

        validateAuxiliary(auxFieldGroupDO, auxFields);
        
        manager.setFlushMode(FlushModeType.COMMIT);        
        
        if(auxFieldGroupDO.getId() == null) {
          auxFieldGroup = new AuxFieldGroup();  
        } else {
            auxFieldGroup = manager.find(AuxFieldGroup.class,auxFieldGroupDO.getId()); 
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
             AuxFieldViewDO afDO = auxFields.get(i);
             AuxField af =null;
             if(afDO.getId() == null) {
                 af = new AuxField();
             } else {
                 af = manager.find(AuxField.class, afDO.getId());
             }
             
             //this has been commented out because the do has been updated and there is no delete flag
             //this will be fixed when the screen is rewritten
             /*if(afDO.getDelete() && afDO.getId() !=null) {
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
         }*/     
        }
      } 
        lockBean.giveUpLock(auxFGRefTableId, auxFieldGroup.getId());
        return auxFieldGroup.getId();
    } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
    }
   }

    private void validateAuxFieldGroup(ValidationErrorsList exceptionList,
                                       AuxFieldGroupDO auxFieldGroupDO) {
        boolean checkDuplicate = true;
        if (auxFieldGroupDO.getName() == null || "".equals(auxFieldGroupDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      AuxFieldGroupMeta.getName()));
            checkDuplicate = false;
        }
        
        if(auxFieldGroupDO !=null) { 
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
    
    private void validateAuxField(ValidationErrorsList exceptionList,List<AuxFieldViewDO> auxFields) {
        TableFieldErrorException ex, auxfvEx;
        List<Exception> exList = null;
        
        if(auxFields == null)
            return;
        /*
        for(int i = 0; i < auxFields.size(); i++) {
            AuxFieldDO afDO = auxFields.get(i);
            if(!afDO.getDelete()) {
               if(afDO.getAnalyteId() == null) {
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
        }*/
    }
    
    public List getMatchingEntries(String name, int maxResults,String cat) {
        Query query = null;
        List entryList = null;
        if ("analyte".equals(cat)) {
            query = manager.createNamedQuery("Analyte.AutoCompleteByName");
        } else if ("method".equals(cat)) {
            query = manager.createNamedQuery("Method.AutoCompleteByName");
            query.setParameter("isActive", "Y");
        }
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
        try {
            entryList = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return entryList;
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
    
    public ArrayList<AuxFieldGroupDO> fetchActive(){
        Query query = manager.createNamedQuery("AuxFieldGroup.FetchActive");
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    private void validateAuxiliary(AuxFieldGroupDO auxFieldGroupDO,
                                             List<AuxFieldViewDO> auxFields) throws Exception{
        ValidationErrorsList exceptionList = new ValidationErrorsList();
        validateAuxFieldGroup(exceptionList, auxFieldGroupDO);
        validateAuxField(exceptionList, auxFields);        
        if(exceptionList.size() > 0)
            throw exceptionList;
    }
    
    private List<Exception> validateAuxFieldValue(List<AuxFieldValueDO> auxFieldValueDOList) {        
        AuxFieldValueDO valueDO;
        Integer numId, dictId, typeId, yesNoId, dateId, dtId, timeId, alcId, 
                amcId, aucId,entryId;;
        ArrayList<Integer> dvl;
        List<Exception> exList;
        List<NumericRange> nrList;
        String value, fieldName,typeName,valueName;
        NumericRange nr;
        boolean hasDateType,hasYesNoType,hasAlphaType;

        exList = null;
        valueDO = null;
        nr = null;

        if (auxFieldValueDOList == null)
            return null;

        dvl = new ArrayList<Integer>();
        nrList = new ArrayList<NumericRange>();
        exList = new ArrayList<Exception>();        
        
        try {
            dictId = (dictionaryBean.fetchBySystemName("aux_dictionary")).getId();
            numId = (dictionaryBean.fetchBySystemName("aux_numeric")).getId();
            yesNoId = (dictionaryBean.fetchBySystemName("aux_yes_no")).getId();
            dateId = (dictionaryBean.fetchBySystemName("aux_date")).getId();
            dtId = (dictionaryBean.fetchBySystemName("aux_date_time")).getId();
            timeId = (dictionaryBean.fetchBySystemName("aux_time")).getId();
            alcId = (dictionaryBean.fetchBySystemName("aux_alpha_lower")).getId();
            amcId = (dictionaryBean.fetchBySystemName("aux_alpha_mixed")).getId();
            aucId = (dictionaryBean.fetchBySystemName("aux_alpha_upper")).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        typeName = AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getTypeId();
        valueName = AuxFieldGroupMeta.getAuxField().getAuxFieldValue().getValue();
        hasDateType = false;
        hasYesNoType = false;
        hasAlphaType = false;
        /*
        for (int i = 0; i < auxFieldValueDOList.size(); i++) {
            valueDO = auxFieldValueDOList.get(i);

            if (valueDO.getDelete())
                continue;

            value = valueDO.getValue();
            typeId = valueDO.getTypeId();

            fieldName = valueName;
            //
            // dictionary, numeric require a value
            //
            if (value == null && (numId.equals(typeId) || dictId.equals(typeId))) {
                exList.add(new TableFieldErrorException("fieldRequiredException",
                                                        i,fieldName));
                continue;
            }

            try {
                if (numId.equals(typeId)) {
                    nr = new NumericRange(value);
                    addNumericIfNoOverLap(nrList, nr);
                } else if (yesNoId.equals(typeId)) {
                    TestResultValidator.validateYesNoValue(value);
                    if (hasYesNoType) {
                        fieldName = typeName;
                        throw new InconsistentException("auxYesNoTypeMoreThanOnceException");
                    }
                    hasYesNoType = true;
                } else if (dateId.equals(typeId)) {
                    TestResultValidator.validateDateTime(value);
                    if (hasDateType) {
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneDateTypeException");
                    }
                    hasDateType = true;
                } else if (dtId.equals(typeId)) {
                    TestResultValidator.validateDateTime(value);
                    if (hasDateType) {
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneDateTypeException");
                    }
                    hasDateType = true;
                } else if (timeId.equals(typeId)) {
                    TestResultValidator.validateTime(value);
                    if (hasDateType) {
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneDateTypeException");
                    }
                    hasDateType = true;
                } else if (dictId.equals(typeId)) {
                    entryId = categoryBean.getEntryIdForEntry(value);                    
                    if (entryId == null)
                        throw new ParseException("illegalDictEntryException");

                    if (!dvl.contains(entryId))
                        dvl.add(entryId);
                    else
                        throw new InconsistentException("auxDictEntryNotUniqueException");
                } else if (alcId.equals(typeId)) {
                    if(hasAlphaType){
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneAlphaTypeException");
                    }
                    hasAlphaType = true;
                } else if (amcId.equals(typeId)) {
                    if(hasAlphaType){
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneAlphaTypeException");
                    }
                    hasAlphaType = true;
                } else if (aucId.equals(typeId)) {
                    if(hasAlphaType){
                        fieldName = typeName;
                        throw new InconsistentException("auxMoreThanOneAlphaTypeException");
                    }
                    hasAlphaType = true;
                } else {
                    fieldName = typeName;
                    throw new ParseException("fieldRequiredException");
                }
            } catch (ParseException pe) {
                exList.add(new TableFieldErrorException(pe.getMessage(),i,fieldName));
            } catch (InconsistentException oe) {
                exList.add(new TableFieldErrorException(oe.getMessage(),i,fieldName));
            }
        }*/

        if (exList.size() == 0)
            exList = null;

        return exList;
      }     
    
    private void addNumericIfNoOverLap(List<NumericRange> nrList,
                                       NumericRange nr) throws InconsistentException{
         NumericRange lr;                  
         
         for(int i = 0; i < nrList.size(); i++) {
             lr = nrList.get(i);
             if(lr.isOverlapping(nr))                    
                 throw new InconsistentException("auxNumRangeOverlapException");             
         }
         
         nrList.add(nr);         
    }    
  }