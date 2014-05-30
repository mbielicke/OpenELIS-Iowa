package org.openelis.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.PatientDO;
import org.openelis.domain.PatientRelationVO;
import org.openelis.entity.Patient;
import org.openelis.meta.PatientMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission.ModuleFlags;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class PatientBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private AddressBean   address;
    
    @EJB
    private LockBean               lock;
    
    @EJB
    private UserCacheBean           userCache;
   
    private static final PatientMeta meta = new PatientMeta();

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

    @SuppressWarnings("unchecked")
    public ArrayList<PatientDO> fetchByIds(Collection<Integer> ids) {
        Query query;
        
        if (ids.size() == 0)
            return new ArrayList<PatientDO>();
        
        query = manager.createNamedQuery("Patient.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PatientRelationVO> fetchByRelatedPatientId(Integer patientId) throws Exception {
        List<PatientRelationVO> list;
        Query query;
        
        query = manager.createNamedQuery("Patient.FetchByRelatedPatientId");
        query.setParameter("patientId", patientId);
        
        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    @SuppressWarnings({"unchecked", "static-access"})
    public ArrayList<PatientDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<PatientDO> list;
        Query query;
        QueryBuilderV2 builder;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.PatientDO(" +
                          PatientMeta.getId() + ", " +
                          PatientMeta.getLastName() + ", " +
                          PatientMeta.getFirstName() + ", " +
                          PatientMeta.getMiddleName() + ", " +
                          PatientMeta.getAddressId() + ", " +
                          PatientMeta.getBirthDate() + ", " +
                          PatientMeta.getBirthTime() + ", " +
                          PatientMeta.getGenderId() + ", " +
                          PatientMeta.getRaceId() + ", " +
                          PatientMeta.getEthnicityId() + ", " +
                          PatientMeta.getNationalId() + ", " +
                          PatientMeta.getAddressMultipleUnit() + ", " +
                          PatientMeta.getAddressStreetAddress() + ", " +
                          PatientMeta.getAddressCity() + ", " +
                          PatientMeta.getAddressState() + ", " +
                          PatientMeta.getAddressZipCode() + ", " +
                          PatientMeta.getAddressWorkPhone() + ", " +
                          PatientMeta.getAddressHomePhone() + ", " +
                          PatientMeta.getAddressCellPhone() + ", " +
                          PatientMeta.getAddressFaxPhone() + ", " +
                          PatientMeta.getAddressEmail() + ", " +
                          PatientMeta.getAddressCountry() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(PatientMeta.getLastName() + ", " + PatientMeta.getFirstName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = (ArrayList<PatientDO>)query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return list;
    }

    public PatientDO add(PatientDO data) throws Exception {
        Patient entity;
        
        checkSecurity(ModuleFlags.ADD);
        
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
        entity.setNationalId(data.getNationalId());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }  
    
    public PatientDO update(PatientDO data) throws Exception {
        Patient entity;
        
        if (!data.isChanged() && !data.getAddress().isChanged()) {
            lock.unlock(Constants.table().PATIENT, data.getId());
            return data;
        }
        
        checkSecurity(ModuleFlags.UPDATE);
        
        lock.validateLock(Constants.table().PATIENT, data.getId());
        
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
        entity.setNationalId(data.getNationalId());
        
        if (data.getAddress().isChanged())
            address.update(data.getAddress());

        lock.unlock(Constants.table().PATIENT, data.getId());
        
        return data;
    }
    
    public PatientDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().PATIENT, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public PatientDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().PATIENT, id);
        return fetchById(id);
    }
    
    public void validate(PatientDO data) throws Exception {
        ValidationErrorsList e;
        
        e = new ValidationErrorsList();
        
        if (data.getLastName() == null)
            e.add(new FormErrorException(Messages.get()
                                         .patient_lastnameRequiredException()));
        
        if (data.getAddress().getStreetAddress() == null)
            e.add(new FormErrorException(Messages.get()
                                         .patient_streetAddressRequiredException()));
        
        if (data.getAddress().getCity() == null)
            e.add(new FormErrorException(Messages.get()
                                         .patient_cityRequiredException()));
        if (e.size() > 0)
            throw e;
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("patient", flag);
    }
}