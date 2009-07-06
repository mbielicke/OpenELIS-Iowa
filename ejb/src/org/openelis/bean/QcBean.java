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
import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameLotNumberDO;
import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.entity.Qc;
import org.openelis.entity.QcAnalyte;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.CategoryLocal;
import org.openelis.local.LockLocal;
import org.openelis.metamap.QcMetaMap;
import org.openelis.remote.QcRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utilcommon.InconsistentException;
import org.openelis.utilcommon.NumericRange;
import org.openelis.utilcommon.ParseException;
import org.openelis.utilcommon.TiterRange;
import org.openelis.utils.GetPage;

@Stateless
@EJBs( {
        @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class),
        @EJB(name = "ejb/Lock", beanInterface = LockLocal.class)        
        })
@SecurityDomain("openelis")
public class QcBean implements QcRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;

    private LockLocal lockBean;
    
    private SystemUserUtilLocal sysUser;    
    
    @EJB
    private CategoryLocal categoryBean;
    
    private static QcMetaMap QcMeta = new QcMetaMap(); 
    
    @PostConstruct
    private void init() {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
    public QcDO getQc(Integer qcId) {
        QcDO qcDO;
        Query query;
        SystemUserDO userDO;
        
        query = manager.createNamedQuery("Qc.QcDOById");
        query.setParameter("id", qcId);
        qcDO = (QcDO)query.getSingleResult(); 
        userDO = sysUser.getSystemUser(qcDO.getPreparedById());
        qcDO.setPreparedByName(userDO.getLoginName());
        return qcDO;
    }

    public List<QcAnalyteDO> getQcAnalytes(Integer qcId) {
        Query query;
        List<QcAnalyteDO> qcAnaDOList;
        QcAnalyteDO qcaDO;
        List results;
        Integer typeId;
        String systemName,value;
        
        query = manager.createNamedQuery("QcAnalyte.QcAnalyteDOsByQcId");
        query.setParameter("id", qcId);     
        qcAnaDOList = query.getResultList();
        for(int i = 0; i < qcAnaDOList.size(); i++) {
            qcaDO = qcAnaDOList.get(i);
            typeId = qcaDO.getTypeId();
                       
            query = manager.createNamedQuery("Dictionary.SystemNameById");
            query.setParameter("id", typeId);                    
            results = query.getResultList();
            systemName = (String)results.get(0);
            
            if("qc_analyte_dictionary".equals(systemName)) {
                query = manager.createNamedQuery("Dictionary.EntryById");
                value = qcaDO.getValue();
                query.setParameter("id", Integer.parseInt(value));
                results = query.getResultList();                        
                value = (String)results.get(0);
                qcaDO.setValue(value);
            }
        }
        return qcAnaDOList;
    }

    public QcDO getQcAndLock(Integer qcId, String session) throws Exception {
        Query query; 
        
        query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qc");
        lockBean.getLock((Integer)query.getSingleResult(), qcId);
        return getQc(qcId);
    }

    public QcDO getQcAndUnlock(Integer qcId, String session) {
        Query unlockQuery; 
        
        unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "qc");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), qcId);
        return getQc(qcId);
    }

    public List<SecuritySystemUserDO> preparedByAutocompleteByName(String loginName,
                                                              int numResult) {
        SecuritySystemUserDO secUserDO;
        SystemUserDO userDO;
        List<SystemUserDO> userDOList;
        List<SecuritySystemUserDO> secUserDOList;
                        
        userDOList = sysUser.systemUserAutocompleteByLoginName(loginName,numResult);
        secUserDOList = new ArrayList<SecuritySystemUserDO>();
        for(int i=0; i < userDOList.size(); i++) {
            userDO = userDOList.get(i);
            secUserDO = new SecuritySystemUserDO(userDO.getId(),userDO.getLoginName(),
                                                 userDO.getLastName(),userDO.getFirstName(),
                                                 userDO.getInitials(),userDO.getIsEmployee(),
                                                 userDO.getIsActive());
            secUserDOList.add(secUserDO);
        } 
        return secUserDOList;
    }

    public List<IdNameLotNumberDO> query(ArrayList<AbstractField> fields,
                                int first,
                                int max) throws Exception {
        StringBuffer sb;
        QueryBuilder qb;
        List returnList;
        Query query;
        
        sb = new StringBuffer();
        qb = new QueryBuilder();
        
        qb.setMeta(QcMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameLotNumberDO(" +QcMeta.getId()
                     + ", "
                     + QcMeta.getName()
                     + ", "
                     + QcMeta.getLotNumber()
                     + ") ");               
        
        qb.addWhere(fields);        
        qb.setOrderBy(QcMeta.getName());

        sb.append(qb.getEJBQL());                                
        query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;        
    }

    public Integer updateQc(QcDO qcDO, List<QcAnalyteDO> qcAnaDOList) throws Exception {
        Query query;
        Integer qcReferenceId,qcId,qcaId,typeId,dictId;
        Qc qc;
        QcAnalyteDO qcaDO;
        QcAnalyte qca;
        String systemName;
        List results;
                
        query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qc");
        qcReferenceId = (Integer)query.getSingleResult();
        qcId = qcDO.getId();
        
        if (qcId != null) {
            // we need to call lock one more time to make sure their lock
            // didnt expire and someone else grabbed the record
            lockBean.validateLock(qcReferenceId, qcId);
        }

        validateQc(qcDO, qcAnaDOList);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        qc = null;
        
        if(qcId == null) {
            qc = new Qc();
        } else {
            qc = manager.find(Qc.class, qcId);                        
        }
        
        qc.setExpireDate(qcDO.getExpireDate());
        qc.setIsSingleUse(qcDO.getIsSingleUse());
        qc.setLotNumber(qcDO.getLotNumber());
        qc.setName(qcDO.getName());
        qc.setPreparedById(qcDO.getPreparedById());
        qc.setPreparedDate(qcDO.getPreparedDate());
        qc.setPreparedUnitId(qcDO.getPreparedUnitId());
        qc.setPreparedVolume(qcDO.getPreparedVolume());
        qc.setSource(qcDO.getSource());
        qc.setTypeId(qcDO.getTypeId());
        qc.setUsableDate(qcDO.getUsableDate());  
        qc.setInventoryItemId(qcDO.getInventoryItemId());
        
        qcId = qc.getId();        
        if(qcId == null) {
            manager.persist(qc);
        }
        
        if(qcAnaDOList != null) {
            for(int i=0; i < qcAnaDOList.size(); i++) {
                qcaDO = qcAnaDOList.get(i);
                qcaId = qcaDO.getId();
                if(qcaId == null) {
                    qca = new QcAnalyte();                    
                } else {
                    qca = manager.find(QcAnalyte.class, qcaId);
                }
                
                if(qcaDO.getDelete() && qcaId != null) {
                    manager.remove(qca);
                } else if(!qcaDO.getDelete()) {
                    qca.setAnalyteId(qcaDO.getAnalyteId());
                    qca.setQcId(qc.getId());
                    qca.setIsTrendable(qcaDO.getIsTrendable());
                    
                    typeId = qcaDO.getTypeId();
                    qca.setTypeId(typeId);
                    
                    query = manager.createNamedQuery("Dictionary.SystemNameById");
                    query.setParameter("id", typeId);                    
                    results = query.getResultList();
                    systemName = (String)results.get(0);
                    
                    if("qc_analyte_dictionary".equals(systemName)) {                                               
                        dictId = categoryBean.getEntryIdForEntry(qcaDO.getValue());
                        qca.setValue(dictId.toString());
                    } else {
                        qca.setValue(qcaDO.getValue());
                    }
                                           
                    if(qca.getId() == null) {
                        manager.persist(qca);
                    }
                }
            }
        }
        
        lockBean.giveUpLock(qcReferenceId, qcId);        
        return qc.getId();
    }
    
    public List<IdNameDO> qcAutocompleteByName(String name, int numResult) {
       List<IdNameDO> list;
       Query query;
       
       query = manager.createNamedQuery("Qc.QcAutoCompleteByName");
       query.setParameter("name", name);
       query.setMaxResults(numResult);
       list = query.getResultList();       
       return list;
    }
    
    private void validateQc(QcDO qcDO, List<QcAnalyteDO> qcAnaDOList) throws Exception {
        ValidationErrorsList exceptionList;
        
        exceptionList = new ValidationErrorsList();
        validateQc(qcDO,exceptionList);
        validateQcAnalytes(qcAnaDOList,exceptionList);
        
        if(exceptionList.size() > 0) 
            throw exceptionList;
    }
    
    private void validateQc(QcDO qcDO, ValidationErrorsList exceptionList) {
        String name,source,lotNumber;
        Datetime prepDate,expDate,usbDate;
        Query query;
        boolean checkOverlap;
        Double volume;
        List<Qc> list;
        Qc qc;
        
        checkOverlap = true;
        
        name = qcDO.getName();
        if(name == null || "".equals(name)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getName()));
        }
                        
        source = qcDO.getSource();
        if(source == null || "".equals(source)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getSource()));
        }
        
        lotNumber = qcDO.getLotNumber();
        if(lotNumber == null || "".equals(lotNumber)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getLotNumber()));
        } else {
            query = manager.createNamedQuery("Qc.QcByLotNumber");
            query.setParameter("lotNumber", lotNumber);
            list = query.getResultList();
            for(int i = 0; i < list.size(); i++) {
                qc = list.get(i);
                if(!qc.getId().equals(qcDO.getId())) {
                    exceptionList.add(new FieldErrorException("fieldUniqueException",
                                                              QcMeta.getLotNumber()));
                    break;
                }
            }
        }
        
        
        volume = qcDO.getPreparedVolume();
        if(volume == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getPreparedVolume()));
        } else if(volume <= 0.0){
            exceptionList.add(new FieldErrorException("invalidPrepVolumeException",
                                                      QcMeta.getPreparedVolume()));
        }
        
        prepDate = qcDO.getPreparedDate();
        expDate = qcDO.getExpireDate();
        usbDate = qcDO.getUsableDate();
        
        if(prepDate == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getPreparedDate()));
            checkOverlap = false;
        }
        
        if(expDate == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getExpireDate()));
            checkOverlap = false;
        }
        
        if(usbDate == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      QcMeta.getUsableDate()));
            checkOverlap = false;
        }
        
        if(checkOverlap) {
            if(usbDate.before(prepDate)) {
                exceptionList.add(new FormErrorException("usbDateBeforePrepDateException"));                               
            }
            
            if(expDate.before(usbDate) || expDate.equals(usbDate)) {
                exceptionList.add(new FormErrorException("expDateBeforeUsbDateException"));
                                
            }
        }
        
    }
    
    private void validateQcAnalytes(List<QcAnalyteDO> qcAnaDOList, ValidationErrorsList exceptionList) {
        QcAnalyteDO qcaDO;
        Integer numId,dictId,titerId,typeId,entryId;
        String value, fieldName;
        NumericRange nr;
        TiterRange tr;
        List<Integer> dictList;
        List<TiterRange> trlist;
        List<NumericRange> nrlist;
        TableFieldErrorException exc;
        int i;                
        
        value = null;             
                 
        dictId = categoryBean.getEntryIdForSystemName("qc_analyte_dictionary");                 
        numId = categoryBean.getEntryIdForSystemName("qc_analyte_numeric");                
        titerId = categoryBean.getEntryIdForSystemName("qc_analyte_titer");
        
        trlist = new ArrayList<TiterRange>();
        nrlist = new ArrayList<NumericRange>();
        dictList = new ArrayList<Integer>();
        
        for(i = 0; i < qcAnaDOList.size(); i++) {           
            qcaDO = qcAnaDOList.get(i);               
            if(qcaDO.getDelete())
                continue;                                                 
            
            value = qcaDO.getValue();               
            typeId = qcaDO.getTypeId();
            //
            // units need to be valid for every result type because
            // their use is dependent on the unit
            //
            
            fieldName = QcMeta.getQcAnalyte().getValue();
            //
            // dictionary, titers, numeric require a value
            //
            if (value == null || "".equals(value)) {
                exc = new TableFieldErrorException("fieldRequiredException", i,fieldName);
                exceptionList.add(exc);  
                continue;
            }
                                      
            try {
                if (numId.equals(typeId)) {                  
                    nr = new NumericRange(value);
                    addNumericIfNoOverLap(nrlist, nr);
                } else if (titerId.equals(typeId)) {                   
                    tr = new TiterRange(value);
                    addTiterIfNoOverLap(trlist,tr);
                } else if (dictId.equals(typeId)) {
                    entryId = categoryBean.getEntryIdForEntry(value);
                    if (entryId == null)
                        throw new ParseException("illegalDictEntryException");

                    if (!dictList.contains(entryId))
                        dictList.add(entryId);
                    else
                        throw new InconsistentException("qcDictEntryNotUniqueException");                                              
                } else {
                    fieldName = QcMeta.getQcAnalyte().getTypeId();
                    throw new ParseException("fieldRequiredException");
                }
            } catch (ParseException ex) {                 
                exc = new TableFieldErrorException(ex.getMessage(), i,fieldName);
                exceptionList.add(exc); 
            } catch (InconsistentException ex) {               
                exc = new TableFieldErrorException(ex.getMessage(), i,fieldName);
                exceptionList.add(exc);
            }                                                
         }
    }
    
    private void addNumericIfNoOverLap(List<NumericRange> nrList,
                                       NumericRange nr) throws InconsistentException{
         NumericRange lr;                  
         
         for(int i = 0; i < nrList.size(); i++) {
             lr = nrList.get(i);
             if(lr.isOverlapping(nr))                    
                 throw new InconsistentException("qcNumRangeOverlapException");             
         }
         
         nrList.add(nr);         
    }
    
    private void addTiterIfNoOverLap(List<TiterRange> trList,
                                       TiterRange tr) throws InconsistentException{
         TiterRange lr;                  
         
         for(int i = 0; i < trList.size(); i++) {
             lr = trList.get(i);
             if(lr.isOverlapping(tr))                    
                 throw new InconsistentException("qcTiterRangeOverlapException");             
         }
         
         trList.add(tr);         
    }
    


}
