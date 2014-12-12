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
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.entity.CaseTag;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseTagBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CaseTagDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseTag.fetchById");
    	query.setParameter("id",id);
    	
    	try {
    		return (CaseTagDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
    
    public ArrayList<CaseTagDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseTag.fetchByIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public ArrayList<CaseTagDO> fetchByCaseId(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseTag.fetchByCaseId");
    	query.setParameter("id",id);
    	
    	return toArrayList(query.getResultList());
    }
    
    public ArrayList<CaseTagDO> fetchByCaseIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseTag.fetchByCaseIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseTagDO add(CaseTagDO data) throws Exception {
    	CaseTag entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = new CaseTag();
    	entity.setCaseId(data.getCaseId());
    	entity.setTypeID(data.getTypeId());
    	entity.setSystemUserId(data.getSystemUserId());
    	entity.setCreateDate(data.getCreatedDate());
    	entity.setRemindDate(data.getReminderDate());
    	entity.setCompletedDate(data.getCompletedDate());
    	entity.setNote(data.getNote());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CaseTagDO update(CaseTagDO data) throws Exception {
    	CaseTag entity;
    	
    	if(!data.isChanged())
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CaseTag.class,data.getId());
    	entity.setCaseId(data.getCaseId());
    	entity.setTypeID(data.getTypeId());
    	entity.setSystemUserId(data.getSystemUserId());
    	entity.setCreateDate(data.getCreatedDate());
    	entity.setRemindDate(data.getReminderDate());
    	entity.setCompletedDate(data.getCompletedDate());
    	entity.setNote(data.getNote());
    	
    	return data;
    }
    
    public void delete(CaseTagDO data) throws Exception {
    	CaseTag entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	entity = manager.find(CaseTag.class, data.getId());
    	if(entity != null)
    		manager.remove(entity);
    }
}
