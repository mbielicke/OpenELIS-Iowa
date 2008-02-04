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

import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class StandardNoteBean implements StandardNoteRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    //private String className = this.getClass().getName();
   // private Logger log = Logger.getLogger(className);
	
	
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
            
        }
    }

	public void deleteStandardNote(Integer standardNoteId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public StandardNoteDO getStandardNote(Integer standardNoteId, boolean unlock) {
		if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "storage_unit");
            lockBean.giveUpLock((Integer)query.getSingleResult(),standardNoteId);
        }
		
		Query query = manager.createNamedQuery("getStandardNote");
		query.setParameter("id", standardNoteId);
		StandardNoteDO standardNoteRecord = (StandardNoteDO) query.getResultList().get(0);// getting first storage unit record

        return standardNoteRecord;
	}

	public Integer getSystemUserId() {
		try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }  
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        
        sb.append("select distinct s.id,s.name " + "from StandardNote s where 1=1 ");
        
        //***append the abstract fields to the string buffer
        if(fields.containsKey("id"))
        	sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("id"), "s.id"));
		if(fields.containsKey("name"))
			sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("name"), "s.name"));
		if(fields.containsKey("description"))
			sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("description"), "s.description"));
		if(fields.containsKey("type") && ((ArrayList)((CollectionField)fields.get("type")).getValue()).size()>0 &&
	       		 !(((ArrayList)((CollectionField)fields.get("type")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("type")).getValue()).get(0))))
			sb.append(QueryBuilder.getQuery((CollectionField)fields.get("type"), "s.type"));
		if(fields.containsKey("text"))
			sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("text"), "s.text"));
				
		Query query = manager.createQuery(sb.toString()+" order by s.name");

        if(first > -1 && max > -1)
       	 query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        if(fields.containsKey("id"))
        	QueryBuilder.setParameters((QueryNumberField)fields.get("id"), "s.id", query);
		if(fields.containsKey("name"))
			QueryBuilder.setParameters((QueryStringField)fields.get("name"), "s.name", query);
		if(fields.containsKey("description"))
			QueryBuilder.setParameters((QueryStringField)fields.get("description"), "s.description", query);
		if(fields.containsKey("type") && ((ArrayList)((CollectionField)fields.get("type")).getValue()).size()>0 &&
	       		 !(((ArrayList)((CollectionField)fields.get("type")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("type")).getValue()).get(0))))
			QueryBuilder.setParameters((CollectionField)fields.get("type"), "s.type", query);
		if(fields.containsKey("text"))
			QueryBuilder.setParameters((QueryStringField)fields.get("text"), "s.text", query);
		
		List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
       	 throw new LastPageException();
        else
       	 return returnList;
	}

	public Integer updateStandardNote(StandardNoteDO standardNoteDO) {
		manager.setFlushMode(FlushModeType.COMMIT);
		StandardNote standardNote = null;
		
		try {
            if (standardNoteDO.getId() == null)
            	standardNote = new StandardNote();
            else
            	standardNote = manager.find(StandardNote.class, standardNoteDO.getId());
            
            standardNote.setDescription(standardNoteDO.getDescription());
            standardNote.setName(standardNoteDO.getName());
            standardNote.setText(standardNoteDO.getText());
            standardNote.setType(standardNoteDO.getType());
         
         if (standardNote.getId() == null) {
	        	manager.persist(standardNote);
         }
         
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return standardNote.getId();
	}

}
