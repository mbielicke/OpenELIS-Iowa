package org.openelis.stfu.bean;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.entity.Case;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;


@Stateless
@SecurityDomain("openelis")
public class CaseBean {
	
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CaseDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("Case.fetchById");
    	query.setParameter("id", id);
    	try {
    		return (CaseDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query =  manager.createNamedQuery("Case.fetchByIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public ArrayList<CaseDO> fetchActiveCasesByUser(Integer systemUserId) throws Exception {
    	Query query = manager.createNamedQuery("Case.fetchActiveCasesByUser");
    	query.setParameter("systemUserId", systemUserId);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseDO add(CaseDO data) throws Exception {
    	Case entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	 
    	entity = new Case();
    	entity.setCreatedDate(data.getCreated());
    	entity.setPatientId(data.getPatientId());
    	entity.setNextOfKinId(data.getNextkinId());
    	entity.setCasePatientId(data.getCasePatientId());
    	entity.setCaseNextOfKinId(data.getNextkinId());
    	entity.setOrganizationId(data.getOrganizationId());
    	entity.setCompletedDate(data.getCompleteDate());
    	entity.setIsFinalized(data.getIsFinalized());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CaseDO update(CaseDO data) throws Exception {
    	Case entity;
    	
    	if(!data.isChanged()) 
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	 
    	entity = manager.find(Case.class, data.getId());
    	entity.setCreatedDate(data.getCreated());
    	entity.setPatientId(data.getPatientId());
    	entity.setNextOfKinId(data.getNextkinId());
    	entity.setCasePatientId(data.getCasePatientId());
    	entity.setCaseNextOfKinId(data.getNextkinId());
    	entity.setOrganizationId(data.getOrganizationId());
    	entity.setCompletedDate(data.getCompleteDate());
    	entity.setIsFinalized(data.getIsFinalized());
    	
    	return data;
    }

}
