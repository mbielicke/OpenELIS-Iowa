/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.AddressDO;
import org.openelis.entity.Address;
import org.openelis.local.AddressLocal;
import org.openelis.remote.AddressRemote;

@Stateless
public class AddressBean implements AddressRemote, AddressLocal {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;

    public void add(AddressDO addressDO) {
        manager.setFlushMode(FlushModeType.COMMIT);

        Address address = new Address();

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

        manager.persist(address);

        addressDO.setId(address.getId());
    }

    public void update(AddressDO addressDO) {
        manager.setFlushMode(FlushModeType.COMMIT);

        Address address = manager.find(Address.class, addressDO.getId());

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
    }

    public void delete(AddressDO addressDO) {
        manager.setFlushMode(FlushModeType.COMMIT);

        Address address = manager.find(Address.class, addressDO.getId());

        manager.remove(address);
    }

    public AddressDO getAddress(Integer addressId) {
        Query query = manager.createNamedQuery("Address.AddressById");
        query.setParameter("id", addressId);
        List resultList = query.getResultList();

        if (resultList.size() > 0)
            return (AddressDO)resultList.get(0);

        return null;
    }

}
