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
import org.openelis.stfu.domain.CaseContactDO;
import org.openelis.stfu.entity.CaseContact;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CaseContactBean {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
	public CaseContactDO fetchById(Integer id) throws Exception {
		Query query = manager.createNamedQuery("CaseContact.fetchById");
		query.setParameter("id", id);
		
		try {
			return (CaseContactDO)query.getSingleResult();
		} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
	}
	
	public ArrayList<CaseContactDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
		Query query = manager.createNamedQuery("CaseContact.fetchById");
		query.setParameter("ids",ids);
		
		return toArrayList(query.getResultList());
	}
	
	public CaseContactDO add(CaseContactDO data) throws Exception {
		CaseContact entity;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = new CaseContact();
		entity.setSource(data.getSource());
		entity.setSourceReference(data.getSourceReference());
		entity.setLastName(data.getLastName());
		entity.setFirstName(data.getFirstName());
		entity.setTypeId(data.getTypeId());
		entity.setNpi(data.getNpi());
		
		manager.persist(entity);
		data.setId(entity.getId());
		
		return data;
	}
	
	public CaseContactDO update(CaseContactDO data) throws Exception {
		CaseContact entity;
		
		if(!data.isChanged())
			return data;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		entity = manager.find(CaseContact.class,data.getId());
		entity.setSource(data.getSource());
		entity.setSourceReference(data.getSourceReference());
		entity.setLastName(data.getLastName());
		entity.setFirstName(data.getFirstName());
		entity.setTypeId(data.getTypeId());
		entity.setNpi(data.getNpi());
				
		return data;
	}
	
	
}
