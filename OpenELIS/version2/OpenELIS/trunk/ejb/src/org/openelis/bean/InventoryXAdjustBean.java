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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.InventoryXAdjustDO;
import org.openelis.domain.InventoryXAdjustViewDO;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryXAdjust;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.InventoryAdjustmentMeta;

@Stateless
@SecurityDomain("openelis")

public class InventoryXAdjustBean  {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<InventoryXAdjustViewDO> fetchByInventoryAdjustmentId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryXAdjust.FetchByInventoryAdjustmentId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public InventoryXAdjustDO add(InventoryXAdjustDO data) throws Exception {
        Integer inventoryLocationId;
        InventoryXAdjust entity;
        InventoryLocation inventorylocation;

        manager.setFlushMode(FlushModeType.COMMIT);        
        entity = new InventoryXAdjust();

        entity.setInventoryAdjustmentId(data.getInventoryAdjustmentId());
        inventoryLocationId = data.getInventoryLocationId();
        entity.setInventoryLocationId(inventoryLocationId);
        entity.setPhysicalCount(data.getPhysicalCount());
        entity.setQuantity(data.getQuantity());

        inventorylocation = manager.find(InventoryLocation.class, data.getInventoryLocationId());
        inventorylocation.setQuantityOnhand(data.getPhysicalCount());

        manager.persist(entity);

        return data;
    }

    public InventoryXAdjustDO update(InventoryXAdjustDO data) throws Exception {
        Integer inventoryLocationId;
        InventoryXAdjust entity;
        InventoryLocation inventorylocation;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(InventoryXAdjust.class, data.getId());
        entity.setInventoryAdjustmentId(data.getInventoryAdjustmentId());
        inventoryLocationId = data.getInventoryLocationId();
        entity.setInventoryLocationId(inventoryLocationId);
        entity.setPhysicalCount(data.getPhysicalCount());
        entity.setQuantity(data.getQuantity());

        inventorylocation = manager.find(InventoryLocation.class, data.getInventoryLocationId());
        inventorylocation.setQuantityOnhand(data.getPhysicalCount());

        return data;
    }
    
    public void delete(InventoryXAdjustDO data) throws Exception {
        InventoryXAdjust entity;     
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryXAdjust.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(InventoryXAdjustDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getInventoryLocationId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             InventoryAdjustmentMeta.getInventoryLocationInventoryItemName()));
        
        if (DataBaseUtil.isEmpty(data.getPhysicalCount()))  
            list.add(new FieldErrorException("fieldRequiredException",
                                             InventoryAdjustmentMeta.getInventoryXAdjustPhysicalCount()));
        else if (!DataBaseUtil.isEmpty(data.getQuantity()) && data.getQuantity() == 0) 
            list.add(new FieldErrorException("physCountNotEqualToOnHandException",
                                             InventoryAdjustmentMeta.getInventoryXAdjustPhysicalCount()));
        
        
        if (list.size() > 0)
            throw list;
        
    }

}
