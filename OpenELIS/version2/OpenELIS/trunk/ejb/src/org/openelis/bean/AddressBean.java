package org.openelis.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.openelis.domain.AddressDO;
import org.openelis.entity.Address;
import org.openelis.remote.AddressLocal;

@Stateless
public class AddressBean implements AddressLocal{

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;
    
	public Integer updateAddress(AddressDO addressDO) {
		 manager.setFlushMode(FlushModeType.COMMIT);

		 Address address = null;
		 
		 if (addressDO.getId() == null)
         	address = new Address();
         else
             address = manager.find(Address.class, addressDO.getId());
		 
		 address.setCellPhone(addressDO.getCellPhone());
		 address.setCity(addressDO.getCity());
		 address.setCountry(addressDO.getCountry());
		 address.setEmail(addressDO.getEmail());
		 address.setFaxPhone(addressDO.getFaxPhone());
		 address.setHomePhone(addressDO.getHomePhone());
		 address.setMultipleUnit(addressDO.getMultipleUnit());
		 address.setState(addressDO.getState());
		 address.setStreetAddress(addressDO.getStreetAddress());
		 address.setWorkPhone(addressDO.getWorkPhone());
		 address.setZipCode(addressDO.getZipCode());
		 
		 //if the address id is null then we need to insert the record
		 if(address.getId() == null){
			 manager.persist(address);
		 }

		return address.getId();
	}

	public void deleteAddress(AddressDO addressDO) {
		manager.setFlushMode(FlushModeType.COMMIT);
		 
		 Address address = null;
		 
		 if (addressDO.getId() == null)
        	address = new Address();
        else
            address = manager.find(Address.class, addressDO.getId());
		 
		 if(address.getId() != null){
			 manager.remove(address);
		 }
		
	}

}
