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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.bean;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
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
@RolesAllowed("standardnote-select")
public class StandardNoteBean implements StandardNoteRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final StandardNoteMetaMap StandardNoteMap = new StandardNoteMetaMap();
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
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
		Query query = manager.createNamedQuery("StandardNote.StandardNote");
		query.setParameter("id", standardNoteId);
		StandardNoteDO standardNoteRecord = (StandardNoteDO) query.getResultList().get(0);// getting first storage unit record

        return standardNoteRecord;
	}
	
    @RolesAllowed("standardnote-update")
	public StandardNoteDO getStandardNoteAndLock(Integer standardNoteId, String session) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "standard_note");
        lockBean.getLock((Integer)query.getSingleResult(),standardNoteId);
        
        return getStandardNote(standardNoteId);
	}
	
	public StandardNoteDO getStandardNoteAndUnlock(Integer standardNoteId, String session) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "standard_note");
        lockBean.giveUpLock((Integer)query.getSingleResult(),standardNoteId);
		
		return getStandardNote(standardNoteId);
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
		QueryBuilder qb = new QueryBuilder();
		
        qb.setMeta(StandardNoteMap);
		
		qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + StandardNoteMap.getId() + ", " + StandardNoteMap.getName() + ") ");
		
//		this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(StandardNoteMap.getName());
        
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
        standardNote.setTypeId(standardNoteDO.getType());
         
        if (standardNote.getId() == null) {
	       	manager.persist(standardNote);
        }
         
        lockBean.giveUpLock(standardNoteReferenceId,standardNote.getId()); 
		    
		return standardNote.getId();
	}
    
    public List queryForType(HashMap fields) throws Exception {
        Query query = manager.createNamedQuery("StandardNote.TypeByNameDesc");
        
        query.setParameter("name", ((QueryStringField)fields.get(StandardNoteMap.getName())).getParameter().get(0));
        query.setParameter("desc", ((QueryStringField)fields.get(StandardNoteMap.getDescription())).getParameter().get(0));
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }
    
    public List getStandardNoteByType(HashMap fields) throws Exception{
    	Query query = manager.createNamedQuery("StandardNote.StandardNoteByType");
        
        query.setParameter("name", ((QueryStringField)fields.get(StandardNoteMap.getName())).getParameter().get(0));
        query.setParameter("desc", ((QueryStringField)fields.get(StandardNoteMap.getDescription())).getParameter().get(0));
        query.setParameter("type", new Integer(((String)((QueryNumberField)fields.get(StandardNoteMap.getTypeId())).getValue()).trim()));
        
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
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getName()));
		}
		
		//description required
		if(standardNoteDO.getDescription() == null || "".equals(standardNoteDO.getDescription())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getDescription()));
		}
		
		//type required
		if(standardNoteDO.getType() == null || "".equals(standardNoteDO.getType())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getTypeId()));
		}
		
		//text required
		if(standardNoteDO.getText() == null || "".equals(standardNoteDO.getText())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getText()));
		}		
	}
}
