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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.QueryField;
import org.openelis.gwt.common.data.deprecated.QueryIntegerField;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

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
    private static int standardNoteRefTableId;
    private static final StandardNoteMetaMap StandardNoteMap = new StandardNoteMetaMap();
    
    public StandardNoteBean(){
        standardNoteRefTableId = ReferenceTableCache.getReferenceTable("standard_note");
    }
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    @RolesAllowed("standardnote-delete")
	public void deleteStandardNote(Integer standardNoteId) throws Exception {
    	lockBean.getLock(standardNoteRefTableId, standardNoteId);
        
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
		
		lockBean.giveUpLock(standardNoteRefTableId, standardNoteId);
	}

	public StandardNoteDO getStandardNote(Integer standardNoteId) {		
		Query query = manager.createNamedQuery("StandardNote.StandardNote");
		query.setParameter("id", standardNoteId);
		StandardNoteDO standardNoteRecord = (StandardNoteDO) query.getResultList().get(0);// getting first storage unit record

        return standardNoteRecord;
	}
	
    @RolesAllowed("standardnote-update")
	public StandardNoteDO getStandardNoteAndLock(Integer standardNoteId, String session) throws Exception{
		lockBean.getLock(standardNoteRefTableId, standardNoteId);
        
        return getStandardNote(standardNoteId);
	}
	
	public StandardNoteDO getStandardNoteAndUnlock(Integer standardNoteId, String session) {
        lockBean.giveUpLock(standardNoteRefTableId, standardNoteId);
		
		return getStandardNote(standardNoteId);
	}

	public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
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
        if(standardNoteDO.getId() != null)
            lockBean.validateLock(standardNoteRefTableId, standardNoteDO.getId());
        
        validateStandardNote(standardNoteDO);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StandardNote standardNote = null;
        
        if (standardNoteDO.getId() == null)
           	standardNote = new StandardNote();
        else
         	standardNote = manager.find(StandardNote.class, standardNoteDO.getId());
            
        standardNote.setDescription(standardNoteDO.getDescription());
        standardNote.setName(standardNoteDO.getName());
        standardNote.setText(standardNoteDO.getText());
        standardNote.setTypeId(standardNoteDO.getTypeId());
         
        if (standardNote.getId() == null) {
	       	manager.persist(standardNote);
        }
         
        lockBean.giveUpLock(standardNoteRefTableId, standardNote.getId()); 
		    
		return standardNote.getId();
	}
    
    public List<StandardNoteDO> newQuery(ArrayList<QueryData> fields) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(StandardNoteMap);

        qb.setSelect("new org.openelis.domain.StandardNoteDO(" + StandardNoteMap.getId()+", "+
                     StandardNoteMap.getName()+", "+
                     StandardNoteMap.getDescription()+", "+
                     StandardNoteMap.getTypeId()+", "+
                     StandardNoteMap.getText()+") ");
        
        String whereClause = " WHERE ";
        if(fields.size() == 2){
            QueryField f = new QueryField();
            f.setValue(fields.get(0).query);
            whereClause += " (" + QueryBuilder.getQueryNoOperand(f, StandardNoteMap.getName()) + " OR " + 
            QueryBuilder.getQueryNoOperand(f, StandardNoteMap.getDescription())+")";
        }else{
            QueryField f = new QueryField();
            f.setValue(fields.get(0).query);
            whereClause += QueryBuilder.getQueryNoOperand(f, StandardNoteMap.getTypeId());
        }
        
        qb.setOrderBy(StandardNoteMap.getTypeId()+", "+StandardNoteMap.getName());

        sb.append(qb.getSelectClause()).append(qb.getFromClause(whereClause)).append(whereClause).append(qb.getOrderBy());

        Query query = manager.createQuery(sb.toString());
        
        qb.setNewQueryParams(query, fields);
        
        List<StandardNoteDO> returnList = query.getResultList();
        
        if (returnList == null)
            return new ArrayList<StandardNoteDO>();
        else
            return returnList;
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
        query.setParameter("type", new Integer(((String)((QueryIntegerField)fields.get(StandardNoteMap.getTypeId())).getValue()).trim()));
        
        List returnList = query.getResultList();
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

	private void validateStandardNote(StandardNoteDO standardNoteDO) throws Exception{
	    ValidationErrorsList list = new ValidationErrorsList();
	    
		//name required (duplicates allowed)
		if(standardNoteDO.getName() == null || "".equals(standardNoteDO.getName())){
		    list.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getName()));
		}
		
		//description required
		if(standardNoteDO.getDescription() == null || "".equals(standardNoteDO.getDescription())){
		    list.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getDescription()));
		}
		
		//type required
		if(standardNoteDO.getTypeId() == null || "".equals(standardNoteDO.getTypeId())){
		    list.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getTypeId()));
		}
		
		//text required
		if(standardNoteDO.getText() == null || "".equals(standardNoteDO.getText())){
		    list.add(new FieldErrorException("fieldRequiredException",StandardNoteMap.getText()));
		}
		
		if(list.size() > 0)
            throw list;
	}
}
