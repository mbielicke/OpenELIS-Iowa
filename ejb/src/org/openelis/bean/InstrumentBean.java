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
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.Instrument;
import org.openelis.entity.InstrumentLog;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InstrumentLogMetaMap;
import org.openelis.metamap.InstrumentMetaMap;
import org.openelis.remote.InstrumentRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
public class InstrumentBean implements InstrumentRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    @EJB
    private LockLocal lockBean;   
    
    private static final InstrumentMetaMap InstMeta = new InstrumentMetaMap();
    
    private static Integer instRefTableId;
    
    public InstrumentBean() {
        instRefTableId = ReferenceTable.INSTRUMENT; 
    }
    
    public InstrumentViewDO getInstrument(Integer instrumentId) {
        Query query = manager.createNamedQuery("Instrument.InstrumentDOById");
        query.setParameter("id", instrumentId);
        return (InstrumentViewDO)query.getResultList().get(0);
    }

    public InstrumentViewDO getInstrumentAndLock(Integer instrumentId,
                                             String session) throws Exception{
        lockBean.getLock(instRefTableId, instrumentId);
        return getInstrument(instrumentId);
    }

    public InstrumentViewDO getInstrumentAndUnlock(Integer instrumentId,
                                               String session) {
        lockBean.giveUpLock(instRefTableId, instrumentId);
        return getInstrument(instrumentId);
    }

    public List<InstrumentLogDO> getInstrumentLogs(Integer instrumentId) {
        Query query = manager.createNamedQuery("InstrumentLog.InstrumentLogDOsByInstrumentId");
        query.setParameter("instrumentId", instrumentId);
        return query.getResultList();
    }


    public Integer updateInstrument(InstrumentViewDO instrumentDO,
                                    List<InstrumentLogDO> logEntries) throws Exception {
        Integer instId,logId;
        Instrument inst;
        int i;
        InstrumentLog log;
        InstrumentLogDO logDO;
        boolean delete;
        
        instId = instrumentDO.getId();
        inst = null;
        
        if (instId != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.validateLock(instRefTableId, instId);
        }
        
        validateInstrument(instrumentDO, logEntries);        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        if(instId == null) {
            inst = new Instrument();            
        } else {
            inst = manager.find(Instrument.class, instId);
        }
                        
        inst.setName(instrumentDO.getName());
        inst.setDescription(instrumentDO.getDescription());
        inst.setModelNumber(instrumentDO.getModelNumber());
        inst.setSerialNumber(instrumentDO.getSerialNumber());
        inst.setTypeId(instrumentDO.getTypeId());
        inst.setLocation(instrumentDO.getLocation());
        inst.setIsActive(instrumentDO.getIsActive());
        inst.setScriptletId(instrumentDO.getScriptletId());
        inst.setActiveBegin(instrumentDO.getActiveBegin());
        inst.setActiveEnd(instrumentDO.getActiveEnd());
        
        if (instId == null) {
            manager.persist(inst);
        }        
        
        if(logEntries != null) {
            for(i = 0; i < logEntries.size();i++) {
                logDO = logEntries.get(i);
                logId = logDO.getId();
                
                delete = false;
                //logDO.getDelete();
                if(logId == null) {
                    log = new InstrumentLog();
                } else {
                    log = manager.find(InstrumentLog.class, logId);
                }
                
                if(delete && logId != null) {
                    manager.remove(log);
                } else {
                    if(!delete) {
                        log.setEventBegin(logDO.getEventBegin());
                        log.setEventEnd(logDO.getEventEnd());
                        log.setInstrumentId(inst.getId());
                        log.setTypeId(logDO.getTypeId());
                        log.setWorksheetId(logDO.getWorksheetId());
                        log.setText(logDO.getText());
                        
                        if(log.getId() == null) {
                            manager.persist(log);
                        }
                    }
                } 
                
            }
        }
        
        lockBean.giveUpLock(instRefTableId, inst.getId());
        return inst.getId();
    }
    
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb;        
        QueryBuilder qb;
        List returnList;
        
        sb = new StringBuffer();        
        qb = new QueryBuilder();
               
        qb.setMeta(InstMeta);  
        
        qb.setSelect("distinct new org.openelis.domain.IdNameSerialNumberDO("
                     + InstMeta.getId()+", "+InstMeta.getName()
                     +", "+InstMeta.getSerialNumber()+ ") ");        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);
                
        qb.setOrderBy(InstMeta.getName()+", "+InstMeta.getSerialNumber());                           
                
        sb.append(qb.getEJBQL());                 
        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
            query.setMaxResults(first+max);
                
        //***set the parameters in the query
        qb.setQueryParams(query);
        
        returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }
    
    private void validateInstrument(InstrumentViewDO instrumentDO,
                                    List<InstrumentLogDO> logEntries) throws Exception {
        ValidationErrorsList errorsList;
     
        errorsList = new ValidationErrorsList();
        validateInstrumentDO(instrumentDO,errorsList);
        validateInstrumentLog(logEntries,errorsList);
        
        if(errorsList.size() > 0)
            throw errorsList;
    }
    
    private void validateInstrumentDO(InstrumentViewDO instrumentDO,ValidationErrorsList exceptionList) {
        String name, serialNumber, isActive,location;
        boolean checkDuplicate;
        Datetime activeBegin, activeEnd;
        Query query;
        List<Instrument> list;
        Instrument inst;
        Integer id;
        
        name = instrumentDO.getName();
        serialNumber = instrumentDO.getSerialNumber();
        isActive = instrumentDO.getIsActive();
        activeBegin = instrumentDO.getActiveBegin();
        activeEnd = instrumentDO.getActiveEnd();
        location = instrumentDO.getLocation();
        id = instrumentDO.getId();
        checkDuplicate = true;
        
        if(name == null || "".equals(name)){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      InstMeta.getName()));
            checkDuplicate = false;
        } 
        
        if(serialNumber == null || "".equals(serialNumber)){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      InstMeta.getSerialNumber()));
            checkDuplicate = false;
        } 
        
        if(isActive == null || "".equals(isActive)){
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      InstMeta.getIsActive()));
            checkDuplicate = false;
        }
        
        if(location == null || "".equals(location)) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      InstMeta.getLocation()));
        }
        
        if(instrumentDO.getTypeId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      InstMeta.getTypeId()));
        }                                
        
        if(checkDuplicate) {
            if(activeEnd != null && activeEnd.before(activeBegin)){
                exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));                  
            }
            
            query = manager.createNamedQuery("Instrument.InstrumentsByNameAndSerialNumber");
            query.setParameter("name", name);
            query.setParameter("serialNumber", serialNumber);
            list = query.getResultList();
            
            for(int i = 0; i < list.size(); i++) {
                inst = list.get(i);
                if((!inst.getId().equals(id))) {
                    exceptionList.add(new FormErrorException("instrumentUniqueException"));                                   
                    break; 
                }
            }
        }
    }      
    
    private void validateInstrumentLog(List<InstrumentLogDO> logDOList,
                                       ValidationErrorsList exceptionList) {
        InstrumentLogDO logDO;
        TableFieldErrorException exc;
        Datetime eb,ee;
        int numEENull;
        boolean checkAfter;
        Integer wsId;
        List<Datetime[]> rangeList;
        Datetime range[];
        Query query;
        
        if(logDOList == null)
            return;
        
        numEENull = 0;
        checkAfter = true;
        rangeList = new ArrayList<Datetime[]>();
        for(int i=0; i < logDOList.size(); i++) {
            logDO = logDOList.get(i);
            
          //  if(logDO.getDelete())
          //      continue;
            
            if(logDO.getTypeId()==null) {
                exc = new TableFieldErrorException("fieldRequiredException", i,
                                                   InstMeta.getInstrumentLog().getTypeId());
                exc.setTableKey(InstrumentLogMetaMap.getTableName());
                exceptionList.add(exc);
            }
            
            wsId = logDO.getWorksheetId();
            if(wsId != null) {
                query = manager.createNamedQuery("Worksheet.WorksheetById");
                query.setParameter("id",wsId );
                if(query.getResultList().size() == 0){
                    exc = new TableFieldErrorException("illegalWorksheetIdException", i,
                                                       InstMeta.getInstrumentLog().getWorksheetId());
                    exc.setTableKey(InstrumentLogMetaMap.getTableName());
                    exceptionList.add(exc);
                }
            }
             
            eb = logDO.getEventBegin();
            ee = logDO.getEventEnd();
                        
            if(ee == null || (ee != null && ee.getDate() == null)) {
                numEENull++;                       
                if(numEENull > 1) {
                    exc = new TableFieldErrorException("moreThanOneEndDateAbsentException", i,
                                                   InstMeta.getInstrumentLog().getEventEnd());                
                    exceptionList.add(exc);
                }
                checkAfter = false;
            }
            
            if(eb == null || (eb != null && eb.getDate() == null)) {
                exc = new TableFieldErrorException("fieldRequiredException", i,
                                                   InstMeta.getInstrumentLog().getEventBegin());                
                exceptionList.add(exc);
            } else if(checkAfter && eb.after(ee)) {
                exc = new TableFieldErrorException("endDateAfterBeginDateException", i,
                                                   InstMeta.getInstrumentLog().getEventEnd());                
                exceptionList.add(exc);
            } else {
                range = new Datetime[2];
                range[0] = eb;
                range[1] = ee;
                if(dateRangeOverlapping(range,rangeList)){
                    exc = new TableFieldErrorException("intervalOverlapException", i,
                                                       InstMeta.getInstrumentLog().getEventBegin());                
                    exceptionList.add(exc);
                    exc = new TableFieldErrorException("intervalOverlapException", i,
                                                       InstMeta.getInstrumentLog().getEventEnd());                
                    exceptionList.add(exc);
                } else {
                    rangeList.add(range);
                }
                
            }
                
        }
    }
    
    private boolean dateRangeOverlapping(Datetime[]range,List<Datetime[]> rangeList) {        
        Datetime[] lrange;        
        
        for(int i =0 ; i < rangeList.size(); i++) {
            lrange = rangeList.get(i);
            if(dateRangesOverlap(lrange[0], lrange[1], range[0], range[1])) {
                return true; 
            }
        }
        
        return false; 
    }
    
    private boolean dateRangesOverlap(Datetime begin1, Datetime end1, 
                                  Datetime begin2, Datetime end2) {
        boolean overlap;
        overlap = false;       
        
        if((begin1 == null || (begin1 != null && begin1.getDate() == null)) 
           ||(begin2 == null || (begin2 != null && begin2.getDate() == null))
           ||(end1 == null || (end1 != null && end1.getDate() == null))
           ||(end2 == null || (end2 != null && end2.getDate() == null))) 
            return overlap;               
        
        if(begin1.equals(begin2) || begin1.equals(end2) || 
                        end1.equals(begin2) || end1.equals(end2)){
            overlap = true;
        } else if((begin1.after(begin2) && begin1.before(end2))||
                        (begin1.before(begin2) && end1.after(begin2))){
            overlap = true;
        } else if((begin1.before(begin2) && end1.after(end2))
                        ||(begin1.after(begin2) && end1.before(end2))){
            overlap = true;
        }
        return overlap; 
    }
    
}