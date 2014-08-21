package org.openelis.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PatientDO;
import org.openelis.entity.Patient;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")

public class PatientBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private AddressBean   address;
   
    public PatientDO fetchById(Integer id) throws Exception {
        Query query;
        PatientDO data;
        
        query = manager.createNamedQuery("Patient.FetchById");
        query.setParameter("id", id);
        
        try {
            data = (PatientDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public PatientDO add(PatientDO data) throws Exception {
        Patient entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        address.add(data.getAddress());
        entity = new Patient();
        entity.setLastName(data.getLastName());
        entity.setFirstName(data.getFirstName());
        entity.setMiddleName(data.getMiddleName());
        entity.setAddressId(data.getAddress().getId());
        entity.setBirthDate(data.getBirthDate());
        entity.setBirthTime(data.getBirthTime());
        entity.setGenderId(data.getGenderId());
        entity.setRaceId(data.getRaceId());
        entity.setEthnicityId(data.getEthnicityId());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }  
    
    public PatientDO update(PatientDO data) throws Exception {
        Patient entity;
        
        if (!data.isChanged() && !data.getAddress().isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Patient.class, data.getId());
        entity.setLastName(data.getLastName());
        entity.setFirstName(data.getFirstName());
        entity.setMiddleName(data.getMiddleName());
        entity.setBirthDate(data.getBirthDate());
        entity.setBirthTime(data.getBirthTime());
        entity.setGenderId(data.getGenderId());
        entity.setRaceId(data.getRaceId());
        entity.setEthnicityId(data.getEthnicityId());
        
        if (data.getAddress().isChanged())
            address.update(data.getAddress());

        return data;
    }
    
    public void validate(PatientDO data) throws Exception {
        //TODO add logic for validation
    }
}