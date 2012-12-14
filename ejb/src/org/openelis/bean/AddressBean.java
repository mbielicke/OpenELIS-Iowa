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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AddressDO;
import org.openelis.entity.Address;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AddressLocal;
import org.openelis.remote.AddressRemote;

@Stateless
@SecurityDomain("openelis")

public class AddressBean implements AddressRemote, AddressLocal {

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;

    public AddressDO fetchById(Integer id) throws Exception {
        Query query;
        AddressDO data;
        
        query = manager.createNamedQuery("Address.FetchById");
        query.setParameter("id", id);
        
        try {
            data = (AddressDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public AddressDO add(AddressDO data) throws Exception {
        Address entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Address();

        entity.setCellPhone(data.getCellPhone());
        entity.setCity(data.getCity());
        entity.setCountry(data.getCountry());
        entity.setEmail(data.getEmail());
        entity.setFaxPhone(data.getFaxPhone());
        entity.setHomePhone(data.getHomePhone());
        entity.setMultipleUnit(data.getMultipleUnit());
        entity.setState(data.getState());
        entity.setStreetAddress(data.getStreetAddress());
        entity.setWorkPhone(data.getWorkPhone());
        entity.setZipCode(data.getZipCode());

        manager.persist(entity);

        data.setId(entity.getId());
        return data;
    }

    public AddressDO update(AddressDO data) throws Exception {
        Address entity;
       
        if (! data.isChanged()) 
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Address.class, data.getId());
        entity.setCellPhone(data.getCellPhone());
        entity.setCity(data.getCity());
        entity.setCountry(data.getCountry());
        entity.setEmail(data.getEmail());
        entity.setFaxPhone(data.getFaxPhone());
        entity.setHomePhone(data.getHomePhone());
        entity.setMultipleUnit(data.getMultipleUnit());
        entity.setState(data.getState());
        entity.setStreetAddress(data.getStreetAddress());
        entity.setWorkPhone(data.getWorkPhone());
        entity.setZipCode(data.getZipCode());

        return data;
    }

    public void delete(AddressDO data) throws Exception {
        if (data.getId() != null)
            delete(data.getId());        
    }
    
    public void delete(Integer id) throws Exception {
        Address entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Address.class, id);
        if (entity != null)
            manager.remove(entity);
    }

    public boolean isEmpty(AddressDO data) throws Exception {
        if (data == null)
            return true;
        
        if (DataBaseUtil.isEmpty(data.getCellPhone()) && DataBaseUtil.isEmpty(data.getCity())  &&
            DataBaseUtil.isEmpty(data.getCountry()) && DataBaseUtil.isEmpty(data.getEmail()) &&
            DataBaseUtil.isEmpty(data.getFaxPhone()) && DataBaseUtil.isEmpty(data.getHomePhone()) && 
            DataBaseUtil.isEmpty(data.getMultipleUnit()) && DataBaseUtil.isEmpty(data.getState()) &&
            DataBaseUtil.isEmpty(data.getStreetAddress()) && DataBaseUtil.isEmpty(data.getWorkPhone()) &&
            DataBaseUtil.isEmpty(data.getZipCode()))
            return true;
                        
        return false;
    }
}
