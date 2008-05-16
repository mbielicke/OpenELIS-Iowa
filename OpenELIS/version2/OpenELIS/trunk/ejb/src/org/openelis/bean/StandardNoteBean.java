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
import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
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
@SecurityDomain("openelis")
@RolesAllowed("standardnote-select")
public class StandardNoteBean implements StandardNoteRemote{

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
            
        }
    }

    @RolesAllowed("standardnote-delete")
	public void deleteStandardNote(Integer standardNoteId) throws Exception {
    	Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "standard_note");
		Integer standardNoteTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(standardNoteTableId, standardNoteId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StandardNote standardNote = null;
		
//		validate the standard note record
        List exceptionList = new ArrayList();
        exceptionList = validateForDelete(standardNoteId);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
		//we delete it
		try {
			standardNote = manager.find(StandardNote.class, standardNoteId);
            	if(standardNote != null)
            		manager.remove(standardNote);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }	
		
		lockBean.giveUpLock(standardNoteTableId, standardNoteId);
	}

	public StandardNoteDO getStandardNote(Integer standardNoteId) {		
		Query query = manager.createNamedQuery("getStandardNote");
		query.setParameter("id", standardNoteId);
		StandardNoteDO standardNoteRecord = (StandardNoteDO) query.getResultList().get(0);// getting first storage unit record

        return standardNoteRecord;
	}
	
    @RolesAllowed("standardnote-update")
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
		
		qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + snMeta.ID + ", " + snMeta.NAME + ") ");
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

    @RolesAllowed("standardnote-update")
	public Integer updateStandardNote(StandardNoteDO standardNoteDO) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "standard_note");
        Integer standardNoteReferenceId = (Integer)query.getSingleResult();
        
        if(standardNoteDO.getId() != null){
            lockBean.getLock(standardNoteReferenceId, standardNoteDO.getId());
        }
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StandardNote standardNote = null;
        
        //validate the standard note record
        List exceptionList = new ArrayList();
        validateStandardNote(standardNoteDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
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
		    
		return standardNote.getId();
	}
    
    public List queryForType(HashMap fields) throws Exception {
        Query query = manager.createNamedQuery("getStandardNoteTypes");
        
        query.setParameter("name", ((QueryStringField)fields.get(StandardNoteMeta.NAME)).getParameter().get(0));
        query.setParameter("desc", ((QueryStringField)fields.get(StandardNoteMeta.DESCRIPTION)).getParameter().get(0));
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }
    
    public List getStandardNoteByType(HashMap fields) throws Exception{
    	Query query = manager.createNamedQuery("getStandardNoteByType");
        
        query.setParameter("name", ((QueryStringField)fields.get(StandardNoteMeta.NAME)).getParameter().get(0));
        query.setParameter("desc", ((QueryStringField)fields.get(StandardNoteMeta.DESCRIPTION)).getParameter().get(0));
        query.setParameter("type", new Integer(((String)((QueryNumberField)fields.get(StandardNoteMeta.TYPE)).getValue()).trim()));
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

	public List validateForAdd(StandardNoteDO standardNoteDO) {
		List exceptionList = new ArrayList();
		
		validateStandardNote(standardNoteDO, exceptionList);
		
		return exceptionList;
	}

	public List validateForDelete(Integer standardNoteId) {
		// not required at this time...
		return new ArrayList();
	}

	public List validateForUpdate(StandardNoteDO standardNoteDO) {
		List exceptionList = new ArrayList();
		
		validateStandardNote(standardNoteDO, exceptionList);
		
		return exceptionList;
	}
	
	private void validateStandardNote(StandardNoteDO standardNoteDO, List exceptionList){
		//name required (duplicates allowed)
		if(standardNoteDO.getName() == null || "".equals(standardNoteDO.getName())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMeta.NAME));
		}
		
		//description required
		if(standardNoteDO.getDescription() == null || "".equals(standardNoteDO.getDescription())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMeta.DESCRIPTION));
		}
		
		//type required
		if(standardNoteDO.getType() == null || "".equals(standardNoteDO.getType())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMeta.TYPE));
		}
		
		//text required
		if(standardNoteDO.getText() == null || "".equals(standardNoteDO.getText())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMeta.TEXT));
		}		
	}
}
