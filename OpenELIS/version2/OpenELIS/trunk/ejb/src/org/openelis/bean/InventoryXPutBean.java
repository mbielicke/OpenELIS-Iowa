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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.InventoryXPutDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
public class InventoryXPutBean implements InventoryXPutLocal {

    @PersistenceContext(unitName = "openelis")
    EntityManager          manager;
    
    @EJB
    InventoryLocationLocal inventoryLocation;

    public ArrayList<InventoryXPutDO> fetchByInventoryReceiptId(Integer id) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("InventoryXPut.FetchByInventoryReceiptId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public InventoryXPutDO fetchByInventoryLocationId(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryXPut.FetchByInventoryLocationId");
        query.setParameter("id", id);
        try {
            return (InventoryXPutDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }  
    
    public ArrayList<InventoryXPutDO> fetchByOrderId(Integer id) throws Exception {
        /*Query query;
        List list;
        
        query = manager.createNamedQuery("InventoryXUse.FetchByOrderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);*/
        
        return null;
    }    

    public InventoryXPutDO add(InventoryXPutDO data) throws Exception {
        /*InventoryXUse entity;
        InventoryLocation loc; 
        

        manager.setFlushMode(FlushModeType.COMMIT);               

        loc = manager.find(InventoryLocation.class, data.getInventoryLocationId());      
        if(loc.getQuantityOnhand() <  data.getQuantity()) {
            throw new FieldErrorException("qtyMoreThanQtyOnhandException", null);
        }
        loc.setQuantityOnhand(loc.getQuantityOnhand() - data.getQuantity());
        
        entity = new InventoryXUse();
        entity.setInventoryLocationId(data.getInventoryLocationId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setQuantity(data.getQuantity());
                        
        manager.persist(entity);

        data.setId(entity.getId());*/
        return data;
    }

    public InventoryXPutDO update(InventoryXPutDO data) throws Exception {
        /*InventoryXUse entity;
        InventoryLocation newLoc, prevLoc;
        Integer newQty, oldQty; 
       
        if (! data.isChanged()) 
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        newLoc = manager.find(InventoryLocation.class, data.getInventoryLocationId());        
        entity = manager.find(InventoryXUse.class, data.getId());
        oldQty = entity.getQuantity();
        newQty = data.getQuantity();
        
        if(!newLoc.getId().equals(entity.getInventoryLocationId())) {
            //
            // if the inventory location that this inventory-x-use record refers
            // to is not the same as the one that it was previously, then we
            // need to add the quantity taken from the previous location back to it
            //            
            prevLoc = manager.find(InventoryLocation.class, entity.getInventoryLocationId());
            prevLoc.setQuantityOnhand(prevLoc.getQuantityOnhand() + oldQty);
        } else if(!oldQty.equals(newQty)) {
            //
            // even if the inventory location is the same as the one this inventory-x-use
            // record was referring to, if the quantity taken from it is different 
            // from before then we need to add the previous quantity to the location
            // and subtract the new quantity from it        
            //
            newLoc.setQuantityOnhand(newLoc.getQuantityOnhand() + oldQty);                    
        }
        
        if(newLoc.getQuantityOnhand() < newQty) {
            throw new FieldErrorException("qtyMoreThanQtyOnhandException", null);
        }
        
        newLoc.setQuantityOnhand(newLoc.getQuantityOnhand() - newQty);        
        
        entity.setInventoryLocationId(data.getInventoryLocationId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setQuantity(newQty);  */            

        return data;
    }
    
    public void delete(InventoryXPutDO data) throws Exception {
        /*InventoryXUse entity;
        InventoryLocation loc;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(InventoryXUse.class, data.getId());
                                        
        if (entity != null) {        
            loc = manager.find(InventoryLocation.class, data.getInventoryLocationId());
            loc.setQuantityOnhand(loc.getQuantityOnhand() + data.getQuantity());           
            manager.remove(entity);
        }*/
    }      

}
