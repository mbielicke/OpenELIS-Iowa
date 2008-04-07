package org.openelis.bean;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.QaEventDO;
import org.openelis.entity.QaEvent;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.QaEventMeta;
import org.openelis.meta.QaEventMethodMeta;
import org.openelis.meta.QaEventTestMeta;
import org.openelis.remote.QaEventRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class QaEventBean implements QaEventRemote{

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
    
    public QaEventDO getQaEvent(Integer qaEventId) {
        
             
        
        Query query = manager.createNamedQuery("getQaEvent");
        query.setParameter("id", qaEventId);
        QaEventDO qaEvent = (QaEventDO) query.getSingleResult();
        
        return qaEvent;
    }

    public List getQaEventNameListByLetter(String letter, int startPos, int maxResults) {
        Query query = manager.createNamedQuery("getQaEventNameRowsByLetter");
        query.setParameter("letter", letter);
        
        if(maxResults > 0){
            query.setFirstResult(startPos);
            query.setMaxResults(maxResults);
        }
        
        List<Object[]> qaeList = query.getResultList(); 
        
        return qaeList;
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
        Query query = manager.createNamedQuery("getTestNames");                               
        List<Object[]> testNames = query.getResultList();         
        return testNames;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        //sb.append("select distinct q.id, q.name,t.name,m.name from QaEvent q left join q.testLink t left join t.methodLink m where 1=1 " );
        QueryBuilder qb = new QueryBuilder();        
        
        QaEventMeta qaEventMeta  = QaEventMeta.getInstance();
        QaEventTestMeta qaEventTestMeta = QaEventTestMeta.getInstance();
        QaEventMethodMeta qaEventMethodMeta = QaEventMethodMeta.getInstance();
        
        qb.addMeta(new Meta[]{qaEventMeta, qaEventTestMeta, qaEventMethodMeta});
        qb.setSelect("distinct "+ QaEventMeta.ID+", "+QaEventMeta.NAME+", "+QaEventTestMeta.NAME+", "+QaEventMethodMeta.NAME);
        qb.addTable(qaEventMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);
        
        qb.setOrderBy(QaEventMeta.NAME+", "+QaEventTestMeta.NAME+", "+QaEventMethodMeta.NAME);
        
        //if(qb.hasTable(qaEventMethodMeta.getTable()))
            qb.addTable(qaEventTestMeta);
            qb.addTable(qaEventMethodMeta);
                
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

    public Integer updateQaEvent(QaEventDO qaEventDO)throws Exception{ 
        
       try{
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "qaevent");
        Integer qaEventReferenceId = (Integer)query.getSingleResult();
        
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
        qaEvent.setTest(qaEventDO.getTest());
        if(qaEventDO.getType()!=null){
         qaEvent.setType(qaEventDO.getType());
        }else{
            throw new Exception("Type must be specified for a QA Event"); 
        } 
        
        if(qaEvent.getId() == null){
            manager.persist(qaEvent);
        }
                
        lockBean.giveUpLock(qaEventReferenceId,qaEvent.getId()); 
        return qaEvent.getId();
       }catch(Exception ex){
           ex.printStackTrace();
           throw ex;
       } 
    }


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
 
}
