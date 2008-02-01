package org.openelis.bean;

import java.util.ArrayList;
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
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.QaEventRemote;
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
    
    public QaEventDO getQaEvent(Integer qaEventId, boolean unlock) {
        if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "qaevent");
            lockBean.giveUpLock((Integer)query.getSingleResult(),qaEventId);            
        }
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
    

    public QaEventDO getQaEventUpdate(Integer id) throws Exception {
        // TODO Auto-generated method stub
        return null;
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
        sb.append("select distinct q.id, q.name,t.name,m.name from QaEvent q left join q.testLink t left join t.methodLink m where 1=1 " );
        if(fields.containsKey("name"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("name"), "q.name"));
        if(fields.containsKey("sequence"))
         sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("sequence"), "q.reportingSequence"));
        if(fields.containsKey("description"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("description"), "q.description"));     
        if(fields.containsKey("reportingText"))
            sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("reportingText"), "q.reportingText"));
        if(fields.containsKey("qaEventType") && ((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).get(0))))
            sb.append(QueryBuilder.getQuery((CollectionField)fields.get("qaEventType"), "q.type"));
        if(fields.containsKey("test") && ((ArrayList)((CollectionField)fields.get("test")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("test")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("test")).getValue()).get(0))))
            sb.append(QueryBuilder.getQuery((CollectionField)fields.get("test"), "q.test"));        
        if(fields.containsKey("billable") && ((ArrayList)((CollectionField)fields.get("billable")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("billable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("billable")).getValue()).get(0))))
            sb.append(QueryBuilder.getQuery((CollectionField)fields.get("billable"), "q.isBillable"));
        
        
        Query query = manager.createQuery(sb.toString()+" order by q.name, t.name, m.name");
        
        if(first > -1 && max > -1)
            query.setMaxResults(first+max);
        
        if(fields.containsKey("name"))
            QueryBuilder.setParameters((QueryStringField)fields.get("name"), "q.name",query);
        if(fields.containsKey("sequence"))
            QueryBuilder.setParameters((QueryNumberField)fields.get("sequence"), "q.reportingSequence",query);
        if(fields.containsKey("description"))
            QueryBuilder.setParameters((QueryStringField)fields.get("description"), "q.description",query);
        if(fields.containsKey("reportingText"))
            QueryBuilder.setParameters((QueryStringField)fields.get("reportingText"), "q.reportingText",query);
        if(fields.containsKey("qaEventType")&& ((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("qaEventType")).getValue()).get(0))))
               QueryBuilder.setParameters((CollectionField)fields.get("qaEventType"), "q.type",query);
        if(fields.containsKey("test")&& ((ArrayList)((CollectionField)fields.get("test")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("test")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("test")).getValue()).get(0))))
               QueryBuilder.setParameters((CollectionField)fields.get("test"), "q.test",query);
        if(fields.containsKey("billable")&& ((ArrayList)((CollectionField)fields.get("billable")).getValue()).size()>0 &&
                        !(((ArrayList)((CollectionField)fields.get("billable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("billable")).getValue()).get(0))))
               QueryBuilder.setParameters((CollectionField)fields.get("billable"), "q.isBillable",query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    public Integer updateQaEvent(QaEventDO qaEventDO){        
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
        qaEvent.setType(qaEventDO.getType());
        
        if(qaEvent.getId() == null){
            manager.persist(qaEvent);
        }
                
        lockBean.giveUpLock(qaEventReferenceId,qaEvent.getId()); 
        return qaEvent.getId();
    }
 
}
