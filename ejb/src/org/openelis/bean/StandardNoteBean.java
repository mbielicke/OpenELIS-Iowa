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

import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.meta.StandardNoteMeta;
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
		manager.setFlushMode(FlushModeType.COMMIT);
		StandardNote standardNote = null;
		
		//we delete it
		try {
			standardNote = manager.find(StandardNote.class, standardNoteId);
            	if(standardNote != null)
            		manager.remove(standardNote);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }			
	}

	public StandardNoteDO getStandardNote(Integer standardNoteId) {		
		Query query = manager.createNamedQuery("getStandardNote");
		query.setParameter("id", standardNoteId);
		StandardNoteDO standardNoteRecord = (StandardNoteDO) query.getResultList().get(0);// getting first storage unit record

        return standardNoteRecord;
	}
	
	public StandardNoteDO getStandardNoteAndLock(Integer standardNoteId) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "standard_note");
        lockBean.getLock((Integer)query.getSingleResult(),standardNoteId);
        
        return getStandardNote(standardNoteId);
	}
	
	public StandardNoteDO getStandardNoteAndUnlock(Integer standardNoteId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "standard_note");
        lockBean.giveUpLock((Integer)query.getSingleResult(),standardNoteId);
		
		return getStandardNote(standardNoteId);
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
		QueryBuilder qb = new QueryBuilder();
		
		StandardNoteMeta snMeta = StandardNoteMeta.getInstance();
		
		qb.addMeta(snMeta);
		
		qb.setSelect("distinct " + snMeta.ID + ", " + snMeta.NAME);
		qb.addTable(snMeta);
		
//		this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(snMeta.NAME);
        
        sb.append(qb.getEJBQL());
        
        Query query = manager.createQuery(sb.toString());
       
        if(first > -1 && max > -1)
       	 query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
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
//			organization reference table id
        	Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "standard_note");
            Integer standardNoteReferenceId = (Integer)query.getSingleResult();
            
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
         
         lockBean.giveUpLock(standardNoteReferenceId,standardNote.getId()); 
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return standardNote.getId();
	}
    
    public List queryForType(HashMap fields) throws Exception {
        StringBuffer sb = new StringBuffer();
        //select distinct d.id,d.entry from standard_note s, dictionary d where s.type=d.id and (s.description like '%Called%' or s.description like '%Opening%' or s.description like '%All%')
        sb.append("select distinct d.id,d.entry " + "from StandardNote s left join s.dictionary d where 1=1 ");
        
        //***append the abstract fields to the string buffer
        String nameFrom = QueryBuilder.getQuery((QueryStringField)fields.get("name"), "s.name");
        if(!"".equals(nameFrom))
            nameFrom = " and (("+nameFrom.substring(6);
        sb.append(nameFrom);

        String descFrom = QueryBuilder.getQuery((QueryStringField)fields.get("description"), "s.description");
        if(!"".equals(descFrom)){
            descFrom = " or"+descFrom.substring(4);
            descFrom+=")";
        }
        sb.append(descFrom);
        
        Query query = manager.createQuery(sb.toString()+" order by d.entry");
        
//      ***set the parameters in the query
        if(fields.containsKey("name"))
            QueryBuilder.setParameters((QueryStringField)fields.get("name"), "s.name", query);
        if(fields.containsKey("description"))
            QueryBuilder.setParameters((QueryStringField)fields.get("description"), "s.description", query);
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }
    
    public List getStandardNoteByType(HashMap fields) throws Exception{
        /////
        StringBuffer sb = new StringBuffer();
    
        sb.append("select new org.openelis.domain.StandardNoteDO(s.id,s.name,s.description,s.type,s.text) from StandardNote s where 1=1 ");
        
        //***append the abstract fields to the string buffer
        String nameFrom = QueryBuilder.getQuery((QueryStringField)fields.get("name"), "s.name");
        if(!"".equals(nameFrom))
            nameFrom = " and (("+nameFrom.substring(6);
        sb.append(nameFrom);

        String descFrom = QueryBuilder.getQuery((QueryStringField)fields.get("description"), "s.description");
        if(!"".equals(descFrom)){
            descFrom = " or"+descFrom.substring(4);
            descFrom+=")";
        }
        sb.append(descFrom);
        
        if(fields.containsKey("type"))
            sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("type"), "s.type"));
        if(fields.containsKey("text"))
            sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("text"), "s.text"));
                
        Query query = manager.createQuery(sb.toString()+" order by s.name");
        
    //  ***set the parameters in the query
        if(fields.containsKey("name"))
            QueryBuilder.setParameters((QueryStringField)fields.get("name"), "s.name", query);
        if(fields.containsKey("description"))
            QueryBuilder.setParameters((QueryStringField)fields.get("description"), "s.description", query);
        if(fields.containsKey("type"))
            QueryBuilder.setParameters((QueryNumberField)fields.get("type"), "s.type", query);
        if(fields.containsKey("text"))
            QueryBuilder.setParameters((QueryStringField)fields.get("text"), "s.text", query);
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

}
