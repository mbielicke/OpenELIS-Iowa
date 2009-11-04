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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.BuildKitComponentDO;
import org.openelis.domain.BuildKitDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryReceiptOrderItem;
import org.openelis.entity.InventoryXPut;
import org.openelis.entity.InventoryXUse;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.remote.BuildKitsRemote;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
})
@SecurityDomain("openelis")
@RolesAllowed("buildkits-select")
public class BuildKitsBean implements BuildKitsRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    @PostConstruct
    private void init() 
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    private static int invLocRefTableId;
    private static final InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    public BuildKitsBean(){
        invLocRefTableId = ReferenceTable.INVENTORY_LOCATION;
    }
    
    @RolesAllowed("buildkits-update")
    public Integer updateBuildKits(BuildKitDO kitDO, List<BuildKitComponentDO> kitComponents) throws Exception {
//      validate the data before we start the transaction
        validateKits(kitDO, kitComponents);
        
       manager.setFlushMode(FlushModeType.COMMIT);
        
        if(kitComponents.size() == 0)
            return -1;
        
        //lock the necessary records
        lockRecords(kitComponents);
        
        //create a new internal order record only on add
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name","order_status_processed");
        DictionaryDO dictDO = (DictionaryDO)query.getResultList().get(0);
        Integer completedStatusValue = dictDO.getId();
        
        Order internalOrder = null;
        if(kitDO.getOrderId() != null)
            internalOrder = manager.find(Order.class, kitDO.getOrderId());
        else
            internalOrder  = new Order();
         
        internalOrder.setStatusId(completedStatusValue);
        internalOrder.setRequestedBy(ctx.getCallerPrincipal().getName());
        internalOrder.setOrderedDate(Datetime.getInstance());
        internalOrder.setNeededInDays(0);
        internalOrder.setIsExternal("N");
            
        if(internalOrder.getId() == null)
            manager.persist(internalOrder);
        
        //create new inv receipt records
        InventoryReceipt receipt = null;
        
        if(kitDO.getId() != null)
            receipt = manager.find(InventoryReceipt.class, kitDO.getId());
        else
            receipt = new InventoryReceipt();
        
        receipt.setInventoryItemId(kitDO.getInventoryItemId());
        //TODO will remove from DB receipt.setOrderItemId(orderItem.getId());
        receipt.setQuantityReceived(kitDO.getNumberRequested());
        if(receipt.getReceivedDate() == null)
            receipt.setReceivedDate(Datetime.getInstance(Datetime.YEAR, Datetime.DAY));

        if(receipt.getId() == null)
            manager.persist(receipt);
        
        //create inventory x put record
        query = manager.createNamedQuery("InventoryXPut.TransIdsLocIdsByReceiptId");
        query.setParameter("id", receipt.getId());
        List locTransLocIds = query.getResultList();
        int numberOfLocs = locTransLocIds.size();

        //create/update TO inv location records
        InventoryLocation toLoc = null;

        if(numberOfLocs > 0)
            toLoc = manager.find(InventoryLocation.class, (Integer)((Object[])locTransLocIds.get(0))[1]);
        else
        toLoc = new InventoryLocation();
        
        if(kitDO.getExpDate() != null)
            toLoc.setExpirationDate(kitDO.getExpDate());
        toLoc.setInventoryItemId(kitDO.getInventoryItemId());
        toLoc.setLotNumber(kitDO.getLotNumber());
        toLoc.setQuantityOnhand(kitDO.getNumberRequested());
        toLoc.setStorageLocationId(kitDO.getLocationId());
        
        if(toLoc.getId() == null)
            manager.persist(toLoc);
        
        //create trans_receipt_location records
        InventoryXPut transReceiptLoc = null;
        
        if(numberOfLocs > 0)
            transReceiptLoc = manager.find(InventoryXPut.class, (Integer)((Object[])locTransLocIds.get(0))[0]);
        else
            transReceiptLoc = new InventoryXPut();
        
        transReceiptLoc.setInventoryLocationId(toLoc.getId());
        transReceiptLoc.setInventoryReceiptId(receipt.getId());
        transReceiptLoc.setQuantity(kitDO.getNumberRequested());
        
        if(transReceiptLoc.getId() == null)
            manager.persist(transReceiptLoc);

        //
        //loop through components
        //
        for(int i=0; i<kitComponents.size(); i++){
            BuildKitComponentDO componentDO = (BuildKitComponentDO)kitComponents.get(i);
            
            //create new order item records with FROM inv item ids
            OrderItem orderItem = null;
            if(componentDO.getOrderItemId() != null)
                orderItem = manager.find(OrderItem.class, componentDO.getOrderItemId());
            else
                orderItem = new OrderItem();
            
            orderItem.setInventoryItemId(componentDO.getInventoryItemId());
            orderItem.setOrderId(internalOrder.getId());
            orderItem.setQuantity(componentDO.getTotal());
            
            if(orderItem.getId() == null)
                manager.persist(orderItem);
            
            //subtract quantity from FROM inv loc record for each order item
            InventoryLocation location = null;
            location = manager.find(InventoryLocation.class, componentDO.getLocationId());
            location.setQuantityOnhand(location.getQuantityOnhand()-componentDO.getTotal());

            //create new trans_location_order record with each transfer from inv_loc
            
            //we need to get the loc trans and the loc ids
            InventoryXUse transLocation = null;
            if(componentDO.getInventoryXUseId() != null)
                transLocation = manager.find(InventoryXUse.class, componentDO.getInventoryXUseId());
            else
                transLocation = new InventoryXUse();
            
            transLocation.setInventoryLocationId(location.getId());
            transLocation.setOrderItemId(orderItem.getId());
            transLocation.setQuantity(componentDO.getTotal());
            
            if(transLocation.getId() == null)
                manager.persist(transLocation);
            
            //create inventory_receipt_order_item record
            InventoryReceiptOrderItem invReceiptOrderItem = null;
            
            if (componentDO.getInventoryReceiptOrderItemId() == null)
                invReceiptOrderItem = new InventoryReceiptOrderItem();
            else
                invReceiptOrderItem = manager.find(InventoryReceiptOrderItem.class, componentDO.getInventoryReceiptOrderItemId());
            
            invReceiptOrderItem.setInventoryReceiptId(receipt.getId());
            invReceiptOrderItem.setOrderItemId(orderItem.getId());
            
            if(invReceiptOrderItem.getId() == null)
                manager.persist(invReceiptOrderItem);
            
        }
                
        unlockRecords(kitComponents);
        
        return receipt.getId();
    }

    public void validateKits(BuildKitDO buildKitDO, List<BuildKitComponentDO> components) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
        validateKit(buildKitDO, list);
        boolean setNumRequestedError = false;
        for(int i=0; i<components.size(); i++){
            if(validateKitComponent(components.get(i), i, list))
                setNumRequestedError = true;
        }
        
        if(setNumRequestedError)
            list.add(new FieldErrorException("numRequestedIsToHigh", "numRequested"));
        
        if(list.size() > 0)
            throw list;
    }
    
    private void validateKit(BuildKitDO buildKitDO, ValidationErrorsList list) {
        //name required 
        if(buildKitDO.getInventoryItemId() == null){
            list.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.getName()));
        }
          
        //num requested required
        if(buildKitDO.getNumberRequested() == null || buildKitDO.getNumberRequested().compareTo(new Integer(0)) <= 0){
            list.add(new FieldErrorException("fieldRequiredException","numRequested"));
        }
          
        //kit location required
        if(buildKitDO.getLocationId() == null){
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()));
        }
        
        //you cant add to existing if the item isnt bulk
        if(buildKitDO.isAddToExisting() && !buildKitDO.isBulk())
            list.add(new FieldErrorException("cantAddToExistingException", InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()));
        
        //if the item is serialized you can only make one at a time
        if(buildKitDO.isSerialized() && buildKitDO.getNumberRequested() > 1)
            list.add(new FieldErrorException("itemSerializedException", "numRequested"));
    }
    
    //returns true if we need to set an error on number requested
    private boolean validateKitComponent(BuildKitComponentDO componentDO, int rowIndex, ValidationErrorsList list) {
        boolean setNumRequestedError = false;
        //componentname required
        if(componentDO.getComponent() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.getComponentId()));
        }
        
        //location required
        if(componentDO.getLocationId() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.INVENTORY_COMPONENT_ITEM.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()));
        }
        
        //unit required
        if(componentDO.getUnit() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.getQuantity()));
        }
        
        //total required
        if(componentDO.getTotal() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, "total"));
        }
        
        //on hand required
        if(componentDO.getQtyOnHand() == null){
            list.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryItemMeta.INVENTORY_COMPONENT.INVENTORY_COMPONENT_ITEM.INVENTORY_LOCATION.getQuantityOnhand()));
        }
        
        //total cant be more than qty on hand
        if(componentDO.getTotal() != null && componentDO.getQtyOnHand() != null && componentDO.getTotal().compareTo(componentDO.getQtyOnHand()) > 0){
            list.add(new TableFieldErrorException("totalIsGreaterThanOnHandException", rowIndex, "total"));
            setNumRequestedError = true;
        }
        
        //TODO need a lot number check
        
        //TODO need an expiration date check
        
        //TODO check add to exisiting is right
        
        //TODO quantity validation on all components
        
        return setNumRequestedError;
    }
    
    private void lockRecords(List components) throws Exception{
        if(components.size() == 0)
            return;
        
        for(int i=0; i<components.size(); i++){
            BuildKitComponentDO componentDO = (BuildKitComponentDO)components.get(i);
        
            if(componentDO.getLocationId() != null)
                lockBean.validateLock(invLocRefTableId, componentDO.getLocationId());
        }
    }
    
    private void unlockRecords(List components) throws Exception{
        if(components.size() == 0)
            return;
        
        for(int i=0; i<components.size(); i++){
            BuildKitComponentDO componentDO = (BuildKitComponentDO)components.get(i);
        
            if(componentDO.getLocationId() != null)
                lockBean.giveUpLock(invLocRefTableId, componentDO.getLocationId());
        }
    }
}
