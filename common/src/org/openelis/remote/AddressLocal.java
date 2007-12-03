package org.openelis.remote;

import javax.ejb.Local;

import org.openelis.domain.AddressDO;

@Local
public interface AddressLocal {
	
//	commit a change to address, or insert a new address
	public Integer updateAddress(AddressDO addressDO);
	
	//delete an addressDO
	public void deleteAddress(AddressDO addressDO);

}
