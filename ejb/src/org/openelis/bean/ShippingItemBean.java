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

import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.ShippingItemDO;
import org.openelis.entity.ShippingItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ShippingItemLocal;
import org.openelis.meta.ShippingMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("shipping-select")
public class ShippingItemBean implements ShippingItemLocal {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;   
    
    public ArrayList<ShippingItemDO> fetchByShippingId(Integer id) throws Exception {
        Query query;
        ArrayList<ShippingItemDO> list;

        query = manager.createNamedQuery("ShippingItem.FetchByShippingId");
        query.setParameter("id", id);

        list = DataBaseUtil.toArrayList(query.getResultList());        

        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }
    
    public ShippingItemDO add(ShippingItemDO data) throws Exception {
        ShippingItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new ShippingItem();
        entity.setShippingId(data.getShippingId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setReferenceId(data.getReferenceId());
        entity.setQuantity(data.getQuantity());
        entity.setDescription(data.getDescription());
                
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }
    
    public ShippingItemDO update(ShippingItemDO data) throws Exception {
        ShippingItem entity;
        
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(ShippingItem.class, data.getId());
        entity.setShippingId(data.getShippingId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setReferenceId(data.getReferenceId());
        entity.setQuantity(data.getQuantity());
        entity.setDescription(data.getDescription());
        
        return data;
    }

    public void delete(ShippingItemDO data) throws Exception {
        ShippingItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);        
        entity = manager.find(ShippingItem.class, data.getId());
        
        if (entity != null)
            manager.remove(entity);
    }
    
    public void validate(ShippingItemDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
       
        if(data.getQuantity() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getItemQuantity()));     
        
        if(data.getDescription() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getItemDescription()));
        
        if(list.size() > 0)
            throw list;
    }

}
