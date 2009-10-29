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
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.Analyte;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.remote.AnalyteRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("analyte-select")

public class AnalyteBean implements AnalyteRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
	@Resource
	private SessionContext ctx;
	
	@EJB
	private LockLocal lockBean; 
    
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();
	
    public AnalyteViewDO fetchById(Integer analyteId) throws Exception{
		Query query;
		AnalyteViewDO data;
		
		query = manager.createNamedQuery("Analyte.FetchById");
		query.setParameter("id", analyteId);
        try {
            data = (AnalyteViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
	}   
      
	@SuppressWarnings("unchecked")
	public ArrayList<IdNameVO> fetchByName(String name, int maxResults) {
		Query query = null;
		
		query = manager.createNamedQuery("Analyte.FetchByName");
		
		query.setParameter("name",name);
		query.setMaxResults(maxResults);
        
		return DataBaseUtil.toArrayList(query.getResultList());
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		Query query;
		QueryBuilderV2 qb;
		List list;
		
		qb = new QueryBuilderV2();
		
		qb.setMeta(Meta);
		
		qb.setSelect("distinct new org.openelis.domain.IdNameVO("+Meta.getId()+", "+Meta.getName() + ") ");
	        
		qb.constructWhere(fields);      

	    qb.setOrderBy(Meta.getName());
        
        query = manager.createQuery(qb.getEJBQL());
        query.setMaxResults(first+max);
        QueryBuilderV2.setQueryParams(query,fields);
         
        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
	}
	
	public AnalyteViewDO add(AnalyteViewDO data) throws Exception {
		Analyte entity;

		checkSecurity(ModuleFlags.ADD);
		
		validate(data);
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = new Analyte();
        entity.setExternalId(data.getExternalId());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setParentAnalyteId(data.getParentAnalyteId());
        
        manager.persist(entity);
        data.setId(entity.getId());
		
		return data;
	}

	public AnalyteViewDO update(AnalyteViewDO data) throws Exception {
        Analyte entity;
        
        checkSecurity(ModuleFlags.UPDATE);
        
        validate(data);
        
        lockBean.validateLock(ReferenceTable.ANALYTE, data.getId());
        
		manager.setFlushMode(FlushModeType.COMMIT);
        

      	entity = manager.find(Analyte.class, data.getId());
        entity.setExternalId(data.getExternalId());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setParentAnalyteId(data.getParentAnalyteId());
                  
        lockBean.giveUpLock(ReferenceTable.ANALYTE, data.getId()); 
		    
		return data;
		
	}
	
	public void delete(AnalyteViewDO data) throws Exception {
        Analyte entity;

        checkSecurity(ModuleFlags.DELETE);
        
        validateForDelete(data.getId());

        lockBean.validateLock(ReferenceTable.ANALYTE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Analyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lockBean.giveUpLock(ReferenceTable.ANALYTE, data.getId());
	}

	public AnalyteViewDO fetchForUpdate(Integer id) throws Exception {
		lockBean.getLock(ReferenceTable.ANALYTE, id); 
        return fetchById(id);
	}

	public AnalyteViewDO abortUpdate(Integer id) throws Exception{
		lockBean.giveUpLock(ReferenceTable.ANALYTE, id);
        return fetchById(id);
	}

    
	public void validateForDelete(Integer id) throws Exception{
		Query query; 
        ValidationErrorsList list;
        List result;
        
        list = new ValidationErrorsList();
        
        query = manager.createNamedQuery("Analyte.ReferenceCheck");
        query.setParameter("id",id);
        result = query.getResultList();
        
        if(result.size() > 0) {
        	list.add(new FormErrorException("analyteDeleteException"));
        	throw list;
        }
                
	}

	public void validate(AnalyteViewDO data) throws Exception {
		Analyte analyte;
		Query query;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
		
		if(DataBaseUtil.isEmpty(data.getName()))
			list.add(new FieldErrorException("fieldRequiredException",Meta.getName()));
	
		try {
			query = manager.createQuery("from Analyte where name = :name");
			query.setParameter("name", data.getName());
			analyte = (Analyte)query.getSingleResult();
			if(data.getId() == null || !data.getId().equals(analyte.getId()))
				list.add(new FieldErrorException("fieldUniqueException",Meta.getName()));
		}catch(EntityNotFoundException e) {
			//Do nothing here, this is what we expect and do not want this 
			//exception thrown.
		}catch(NoResultException e) {
			//Do nothing here, this is what we expect and do not want this 
			//exception thrown.
		}
			
        if(list.size() > 0)
             throw list;
	}
	
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "analyte", flag);
    }
}
