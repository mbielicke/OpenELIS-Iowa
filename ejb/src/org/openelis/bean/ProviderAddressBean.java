package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AddressLocal;
import org.openelis.local.ProviderAddressLocal;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")

public class ProviderAddressBean implements ProviderAddressLocal {
    
	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
    @EJB
    private AddressLocal                     addressBean;

    private static final ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
	public ProviderAddressDO add(ProviderAddressDO data) throws Exception {
		manager.setFlushMode(FlushModeType.COMMIT);
		
		addressBean.add(data.getAddressDO());
		
		ProviderAddress entity = new ProviderAddress();	
		entity.setAddressId(data.getAddressDO().getId());
		entity.setLocation(data.getLocation());
		entity.setExternalId(data.getExternalId());
		entity.setProviderId(data.getProviderId());
		
		manager.persist(entity);
		data.setId(entity.getId());
		
		return data;
	}

	public void delete(ProviderAddressDO data) throws Exception {

		manager.setFlushMode(FlushModeType.COMMIT);
		
		ProviderAddress entity = manager.find(ProviderAddress.class, data.getId());
		
		if(entity != null) {
			addressBean.delete(entity.getAddressId());
			manager.remove(entity);
		}
		
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ProviderAddressDO> fetchByProviderId(Integer id) throws Exception {		
        Query query;
        List list;

        query = manager.createNamedQuery("ProviderAddress.FetchByProviderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
		
	}

	public ProviderAddressDO update(ProviderAddressDO data) throws Exception {
		
		if(!data.isChanged())
			return data;
		
		manager.setFlushMode(FlushModeType.COMMIT);
		
		addressBean.update(data.getAddressDO());
		
		ProviderAddress entity = manager.find(ProviderAddress.class, data.getId());	
		entity.setLocation(data.getLocation());
		entity.setExternalId(data.getExternalId());
		
		return data;
	}

	public void validate(ProviderAddressDO data) throws Exception {
		ValidationErrorsList vl = new ValidationErrorsList();
		String location = data.getLocation();  
		String city = data.getAddressDO().getCity();
		String state = data.getAddressDO().getState();
		String zipcode = data.getAddressDO().getZipCode();
		String country = data.getAddressDO().getCountry();

		if(location == null || "".equals(location)){            
			vl.add(new FieldErrorException("fieldRequiredException", ProvMeta.getProviderAddress().getLocation()));
		}
		if(state == null || "".equals(state)){            
			vl.add(new FieldErrorException("fieldRequiredException", ProvMeta.getProviderAddress().getAddress().getState()));
		}
		if(city == null || "".equals(city)){            
			vl.add(new FieldErrorException("fieldRequiredException", ProvMeta.getProviderAddress().getAddress().getCity()));
		}
		if(zipcode == null || "".equals(zipcode)){            
			vl.add(new FieldErrorException("fieldRequiredException", ProvMeta.getProviderAddress().getAddress().getZipCode()));
		}

		if(country == null || "".equals(country)){            
			vl.add(new FieldErrorException("fieldRequiredException", ProvMeta.getProviderAddress().getAddress().getCountry()));
		}
	}
	

}
