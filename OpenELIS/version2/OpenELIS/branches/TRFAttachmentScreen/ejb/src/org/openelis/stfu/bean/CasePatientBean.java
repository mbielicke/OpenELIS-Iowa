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
import org.openelis.stfu.domain.CasePatientDO;
import org.openelis.stfu.entity.CasePatient;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class CasePatientBean {
	
    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
    public CasePatientDO fetchById(Integer id) throws Exception {
    	Query query = manager.createNamedQuery("CasePatient.fetchById");
    	query.setParameter("id",id);
    	try {
    		return (CasePatientDO)query.getSingleResult();
    	} catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }	
    }
    
    public ArrayList<CasePatientDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
    	Query query = manager.createNamedQuery("CasePatient.fetchByIds");
    	query.setParameter("ids",ids);
    	
    	return toArrayList(query.getResultList());
    }
    
    public CasePatientDO add(CasePatientDO data) throws Exception {
    	CasePatient entity;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = new CasePatient();
    	entity.setLastName(data.getLastName());
    	entity.setFirstName(data.getFirstName());
    	entity.setMaidenName(data.getMaidenName());
    	entity.setAddressId(data.getAddressId());
    	entity.setBirthDate(data.getBirthDate());
    	entity.setBirthTime(data.getBirthTime());
    	entity.setGenderId(data.getGenderId());
    	entity.setRaceId(data.getRaceId());
    	entity.setEthnicityId(data.getEthnicityId());
    	entity.setNationalId(data.getNationalId());
    	
    	manager.persist(entity);
    	data.setId(entity.getId());
    	
    	return data;
    }
    
    public CasePatientDO update(CasePatientDO data) throws Exception {
    	CasePatient entity;
    	
    	if(!data.isChanged())
    		return data;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	entity = manager.find(CasePatient.class,data.getId());
    	entity.setLastName(data.getLastName());
    	entity.setFirstName(data.getFirstName());
    	entity.setMaidenName(data.getMaidenName());
    	entity.setAddressId(data.getAddressId());
    	entity.setBirthDate(data.getBirthDate());
    	entity.setBirthTime(data.getBirthTime());
    	entity.setGenderId(data.getGenderId());
    	entity.setRaceId(data.getRaceId());
    	entity.setEthnicityId(data.getEthnicityId());
    	entity.setNationalId(data.getNationalId());
    	    	
    	return data;
    }
    
}
