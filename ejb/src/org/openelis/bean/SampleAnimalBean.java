package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SampleAnimalDO;
import org.openelis.entity.SampleAnimal;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class SampleAnimalBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;
    
    @EJB
    private AddressBean  address;

    @SuppressWarnings("unchecked")
    public ArrayList<SampleAnimalDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleAnimalDO> c;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleAnimal.FetchBySampleIds");
        c = new ArrayList<SampleAnimalDO>();         
        r = DataBaseUtil.createSubsetRange(sampleIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", sampleIds.subList(r.get(i), r.get(i + 1)));
            c.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(c);
    }

    public SampleAnimalDO add(SampleAnimalDO data) throws Exception {
        SampleAnimal entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new SampleAnimal();
        
        // first insert the address so we can reference its id        
        if (address.isEmpty(data.getLocationAddress()))     
            data.getLocationAddress().setId(null);                                       
        else  
            address.add(data.getLocationAddress());   
        
        entity.setSampleId(data.getSampleId());
        entity.setAnimalCommonNameId(data.getAnimalCommonNameId());
        entity.setAnimalScientificNameId(data.getAnimalScientificNameId());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        entity.setProviderId(data.getProviderId());
        entity.setProviderPhone(data.getProviderPhone());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public SampleAnimalDO update(SampleAnimalDO data) throws Exception {
        Integer deleteId;
        SampleAnimal entity;
        
        if (!data.isChanged() && !data.getLocationAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        deleteId = null;
        entity = manager.find(SampleAnimal.class, data.getId());
        if (address.isEmpty(data.getLocationAddress())) {
            if (data.getLocationAddress().getId() != null) {
                deleteId = data.getLocationAddress().getId();
                data.getLocationAddress().setId(null);
            }
        } else {
            if (data.getLocationAddress().isChanged()) {
                if (data.getLocationAddress().getId() != null)
                    address.update(data.getLocationAddress());
                else
                    address.add(data.getLocationAddress());
            }                       
        }
        
        entity.setSampleId(data.getSampleId());
        entity.setAnimalCommonNameId(data.getAnimalCommonNameId());
        entity.setAnimalScientificNameId(data.getAnimalScientificNameId());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        entity.setProviderId(data.getProviderId());
        entity.setProviderPhone(data.getProviderPhone());
        
        if (deleteId != null)
            address.delete(deleteId);

        return data;
    }

    public void delete(SampleAnimalDO data) throws Exception {
        Integer deleteId;
        SampleAnimal entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleAnimal.class, data.getId());
        if (entity != null) {
            deleteId = entity.getLocationAddressId();
            manager.remove(entity);
            if (deleteId != null)
                address.delete(deleteId);
        }
    }

    public void validate(SampleAnimalDO data, Integer accession) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (data.getAnimalCommonNameId() == null)
            list.add(new FormErrorException(Messages.get()
                                                    .sampleAnimal_commonNameRequiredException(accession == null ? 0
                                                                                                      : accession)));
        if (list.size() > 0)
            throw list;
    }
}