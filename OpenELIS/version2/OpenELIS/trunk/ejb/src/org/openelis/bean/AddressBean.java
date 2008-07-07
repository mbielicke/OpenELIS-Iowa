/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
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
