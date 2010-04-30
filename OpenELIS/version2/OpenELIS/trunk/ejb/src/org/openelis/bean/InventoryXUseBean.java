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

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.entity.InventoryXUse;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.InventoryXUseLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
public class InventoryXUseBean implements InventoryXUseLocal{

    @PersistenceContext(unitName = "openelis")
    EntityManager manager;

    public ArrayList<InventoryXUseViewDO> fetchByOrderId(Integer id) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("InventoryXUse.FetchByOrderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }    

    public InventoryXUseViewDO add(InventoryXUseViewDO data) throws Exception {
        InventoryXUse entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new InventoryXUse();

        entity.setInventoryLocationId(data.getInventoryLocationId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setQuantity(data.getQuantity());
        manager.persist(entity);

        data.setId(entity.getId());
        return data;
    }

    public InventoryXUseViewDO update(InventoryXUseViewDO data) throws Exception {
        InventoryXUse entity;
       
        if (! data.isChanged()) 
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryXUse.class, data.getId());
        entity.setInventoryLocationId(data.getInventoryLocationId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setQuantity(data.getQuantity());

        return data;
    }
    
    public void delete(InventoryXUseViewDO data) throws Exception {
        InventoryXUse entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(InventoryXUse.class, data.getId());     
        if (entity != null)
            manager.remove(entity);
    }

}
