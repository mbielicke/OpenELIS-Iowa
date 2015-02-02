package org.openelis.stfu.bean;

import static org.openelis.ui.common.DataBaseUtil.toArrayList;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.stfu.domain.CaseContactDO;
import org.openelis.stfu.domain.CaseContactLocationDO;
import org.openelis.stfu.entity.CaseContact;
import org.openelis.stfu.entity.CaseContactLocation;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseContactLocationBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
	public CaseContactLocationDO fetchById(Integer id) throws Exception {
		Query query = manager.createNamedQuery("CaseContactLocation.fetchById");
		query.setParameter("id", id);
		
		try {
			return (CaseContactLocationDO)query.getSingleResult();
		} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
	}
	
	public ArrayList<CaseContactLocationDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
		Query query = manager.createNamedQuery("CaseContactLocation.fetchById");
		query.setParameter("ids",ids);
		
		return toArrayList(query.getResultList());
	}
	
	public CaseContactLocationDO add(CaseContactLocationDO data) throws Exception {
		CaseContactLocation entity;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = new CaseContactLocation();
		entity.setCaseContactId(data.getCaseContactId());
		entity.setLocation(data.getLocation());
		entity.setAddressId(data.getAddressId());
		
		manager.persist(entity);
		data.setId(entity.getId());
		
		return data;
	}
	
	public CaseContactLocationDO update(CaseContactLocationDO data) throws Exception {
		CaseContactLocation entity;
		
		if(!data.isChanged())
			return data;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = manager.find(CaseContactLocation.class,data.getId());
		entity.setCaseContactId(data.getCaseContactId());
		entity.setLocation(data.getLocation());
		entity.setAddressId(data.getAddressId());
				
		return data;
	}
	
	public void delete(CaseContactLocationDO data) throws Exception {
		CaseContactLocation entity;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = manager.find(CaseContactLocation.class, data.getId());
		if(entity != null)
			manager.remove(entity);
	}
}
