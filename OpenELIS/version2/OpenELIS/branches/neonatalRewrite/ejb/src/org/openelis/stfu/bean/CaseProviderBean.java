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
import org.openelis.stfu.domain.CaseProviderDO;
import org.openelis.stfu.entity.CaseProvider;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseProviderBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CaseProviderDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseProvider.fetchById");
    	query.setParameter("id", id);
    	try {
    		return (CaseProviderDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseProviderDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseProvider.fetchByIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CaseProviderDO fetchByCaseId(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CaseProvider.fetchByCaseId");
    	query.setParameter("id", id);
    	try {
    		return (CaseProviderDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CaseProviderDO> fetchByCaseIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CaseProvider.fetchByCaseIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }    
    
    public CaseProviderDO add(CaseProviderDO data) throws Exception {
    	CaseProvider entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = new CaseProvider();
    	entity.setCaseId(data.getCaseId());
    	entity.setCaseContactId(data.getCaseContactId());
    	entity.setTypeId(data.getTypeId());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CaseProviderDO update(CaseProviderDO data) throws Exception {
    	CaseProvider entity;
    	
    	if(!data.isChanged())
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CaseProvider.class,data.getId());
    	entity.setCaseId(data.getCaseId());
    	entity.setCaseContactId(data.getCaseContactId());
    	entity.setTypeId(data.getTypeId());
    	
    	return data;
    }
}
