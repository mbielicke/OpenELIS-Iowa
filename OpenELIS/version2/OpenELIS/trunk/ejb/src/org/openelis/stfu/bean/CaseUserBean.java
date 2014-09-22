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
import org.openelis.stfu.domain.CaseUserDO;
import org.openelis.stfu.entity.CaseUser;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseUserBean {
	
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CaseUserDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseUser.fetchById");
    	query.setParameter("id",id);
    	try {
    		return (CaseUserDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseUserDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseUser.fetchByIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseUserDO fetchByCaseId(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseUser.fetchByCaseId");
    	query.setParameter("id", id);
    	try {
    		return (CaseUserDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseUserDO> fetchByCaseIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseUser.fetchByCaseIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseUserDO add(CaseUserDO data) throws Exception {
    	CaseUser entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = new CaseUser();
    	entity.setCaseId(data.getCaseId());
    	entity.setSystemUserId(data.getSystemUserId());
    	entity.setSectionId(data.getSectionId());
    	entity.setActionId(data.getActionId());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CaseUserDO update(CaseUserDO data) throws Exception {
    	CaseUser entity;
    	
    	if(!data.isChanged())
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CaseUser.class,data.getId());
    	entity.setCaseId(data.getCaseId());
    	entity.setSystemUserId(data.getSystemUserId());
    	entity.setSectionId(data.getSectionId());
    	entity.setActionId(data.getActionId());
    	    	
    	return data;
    }
}
