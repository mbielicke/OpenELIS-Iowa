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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.QaEventDO;
import org.openelis.entity.QaEvent;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.QaEventMetaMap;
import org.openelis.remote.QaEventRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("qaevent-select")
public class QaEventBean implements QaEventRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final QaEventMetaMap QaeMeta = new QaEventMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    public QaEventDO getQaEvent(Integer qaEventId) {
        Query query = manager.createNamedQuery("QaEvent.QaEvent");
        query.setParameter("id", qaEventId);
        QaEventDO qaEvent = (QaEventDO) query.getSingleResult();
        
        return qaEvent;
    }  

    public List<Object[]> getTestNames() {
        Query query = manager.createNamedQuery("Test.Names");
        query.setParameter("isActive", "Y");
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
    public QaEventDO getQaEventAndLock(Integer qaEventId, String session) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qaevent");
        lockBean.getLock((Integer)query.getSingleResult(),qaEventId);
        
        return getQaEvent(qaEventId);
    }

    public QaEventDO getQaEventAndUnlock(Integer qaEventId, String session) {
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