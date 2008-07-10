/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
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
import org.openelis.domain.QaEventDO;
import org.openelis.entity.QaEvent;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.remote.QaEventRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("qaevent-select")
public class QaEventBean implements QaEventRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final QaEventMetaMap QaeMeta = new QaEventMetaMap();
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public QaEventDO getQaEvent(Integer qaEventId) {
        
             
        
        Query query = manager.createNamedQuery("QaEvent.QaEvent");
        query.setParameter("id", qaEventId);
        QaEventDO qaEvent = (QaEventDO) query.getSingleResult();
        
        return qaEvent;
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

    public List<Object[]> getTestNames() {
        Query query = manager.createNamedQuery("Test.Names");                               
        List<Object[]> testNames = query.getResultList();         
        return testNames;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        //QueryBuilder qb = new QueryBuilder();     
        QueryBuilder qb = new QueryBuilder();
        
        //QaEventMeta qaEventMeta  = QaEventMeta.getInstance();
        //QaEventTestMeta qaEventTestMeta = QaEventTestMeta.getInstance();
        //QaEventMethodMeta qaEventMethodMeta = QaEventMethodMeta.getInstance();
        
        //qb.addMeta(new Meta[]{qaEventMeta, qaEventTestMeta, qaEventMethodMeta});
        qb.setMeta(QaeMeta);
        //qb.setSelect("distinct new org.openelis.domain.IdNameTestMethodDO("+ QaEventMeta.ID+", "+QaEventMeta.NAME+", "+QaEventTestMeta.NAME+", "+QaEventMethodMeta.NAME + ") ");
        qb.setSelect("distinct new org.openelis.domain.IdNameTestMethodDO("+ QaeMeta.getId()+", "+QaeMeta.getName()+", "+QaeMeta.getTest().getName()+", "+QaeMeta.getTest().getMethod().getName() + ") ");
        //qb.addTable(qaEventMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);
        
        //qb.setOrderBy(QaEventMeta.NAME+", "+QaEventTestMeta.NAME+", "+QaEventMethodMeta.NAME);
        qb.setOrderBy(QaeMeta.getName()+", "+QaeMeta.getTest().getName()+", "+QaeMeta.getTest().getMethod().getName());        
        
           // qb.addTable(qaEventTestMeta);
           // qb.addTable(qaEventMethodMeta);
                
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
   
    @RolesAllowed("qaevent-update")
    public Integer updateQaEvent(QaEventDO qaEventDO)throws Exception{ 
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qaevent");
        Integer qaEventReferenceId = (Integer)query.getSingleResult();
        
        if(qaEventDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(qaEventReferenceId, qaEventDO.getId());
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateQaEvent(qaEventDO,exceptionList);  
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        QaEvent qaEvent = null;
        
        if(qaEventDO.getId()==null){
            qaEvent = new QaEvent();            
        }else{
            qaEvent = manager.find(QaEvent.class, qaEventDO.getId());
        }
        
        qaEvent.setDescription(qaEventDO.getDescription());
        qaEvent.setIsBillable(qaEventDO.getIsBillable());
        qaEvent.setName(qaEventDO.getName());
        qaEvent.setReportingSequence(qaEventDO.getReportingSequence());
        qaEvent.setReportingText(qaEventDO.getReportingText());
        qaEvent.setTestId(qaEventDO.getTest());
        qaEvent.setTypeId(qaEventDO.getType());
        
        if(qaEvent.getId() == null){
            manager.persist(qaEvent);
        }
                
        lockBean.giveUpLock(qaEventReferenceId,qaEvent.getId()); 
        return qaEvent.getId();
    }

    @RolesAllowed("qaevent-update")
    public QaEventDO getQaEventAndLock(Integer qaEventId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qaevent");
        lockBean.getLock((Integer)query.getSingleResult(),qaEventId);
        
        return getQaEvent(qaEventId);
    }

    public QaEventDO getQaEventAndUnlock(Integer qaEventId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qaevent");
        lockBean.giveUpLock((Integer)query.getSingleResult(),qaEventId);           
        
        return getQaEvent(qaEventId);
    }
    
    public List validateForAdd(QaEventDO qaeDO){
        List<Exception> exceptionList = new ArrayList<Exception>();
        
        validateQaEvent(qaeDO, exceptionList);
        
        return exceptionList;
    }
    
    public List validateForUpdate(QaEventDO qaeDO){
        List<Exception> exceptionList = new ArrayList<Exception>();
        
        validateQaEvent(qaeDO, exceptionList);
        
        return exceptionList;
    }
    
    private void validateQaEvent(QaEventDO qaEventDO,List<Exception> exceptionList){
        if(qaEventDO.getType()==null){                       
            exceptionList.add(new FieldErrorException("fieldRequiredException",QaeMeta.getTypeId()));
           } 
        if("".equals(qaEventDO.getName())){                       
            exceptionList.add(new FieldErrorException("fieldRequiredException",QaeMeta.getName()));
           } 
        if("".equals(qaEventDO.getReportingText())){                       
            exceptionList.add(new FieldErrorException("fieldRequiredException",QaeMeta.getReportingText()));
           } 
    } 
 
}
