/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryXPut;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class InventoryReceiptBean implements InventoryReceiptRemote, InventoryReceiptLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                     manager;

    @EJB
    private LockLocal                         lock;

    @EJB
    private DictionaryLocal                   dictionary;

    @EJB
    private OrganizationLocal                 organization;

    @EJB
    private InventoryItemLocal                inventoryItem;

    @EJB
    private InventoryLocationLocal            inventoryLocation;

    @EJB
    private InventoryXPutLocal                inventoryXPut;

    private static int                        statusPending, statusBackOrdered;

    private static final InventoryReceiptMeta meta = new InventoryReceiptMeta();

    @PostConstruct
    public void init() {
        DictionaryDO data;

        if (statusPending == 0) {
            try {
                data = dictionary.fetchBySystemName("order_status_pending");
                statusPending = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                statusPending = 0;
            }
        }

        if (statusBackOrdered == 0) {
            try {
                data = dictionary.fetchBySystemName("order_status_back_ordered");
                statusBackOrdered = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                statusBackOrdered = 0;
            }
        }
    }

    public InventoryReceiptViewDO fetchById(Integer id) throws Exception {
        Query query;
        InventoryReceiptViewDO data;

        query = manager.createNamedQuery("InventoryReceipt.FetchById");
        query.setParameter("id", id);

        try {
            data = (InventoryReceiptViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    public ArrayList<IdNameVO> fetchByUpc(String upc, int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryReceipt.FetchByUpc");
        query.setParameter("upc", upc);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<InventoryReceiptManager> query(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.InventoryReceiptViewDO(" +
                          InventoryReceiptMeta.getId() + ", " +
                          InventoryReceiptMeta.getInventoryItemId() + ", " +
                          InventoryReceiptMeta.getOrderItemId() + ", " +
                          InventoryReceiptMeta.getOrganizationId() + ", " +
                          InventoryReceiptMeta.getReceivedDate() + ", " +
                          InventoryReceiptMeta.getQuantityReceived() + ", " +
                          InventoryReceiptMeta.getUnitCost() + ", " +
                          InventoryReceiptMeta.getQcReference() + ", " +
                          InventoryReceiptMeta.getExternalReference() + ", " +
                          InventoryReceiptMeta.getUpc() + ", " +
                          InventoryReceiptMeta.getOrderItemQuantity() + ", " +
                          InventoryReceiptMeta.getOrderItemOrderId() + ", " +
                          InventoryReceiptMeta.getOrderItemOrderExternalOrderNumber() + ", " +
                          InventoryReceiptMeta.getOrderItemUnitCost() + ") ");
        builder.constructWhere(fields);
        
        builder.addWhere(InventoryReceiptMeta.getOrderItemOrderType() + "=" + "'"+OrderManager.TYPE_VENDOR+"' and " +
                         InventoryReceiptMeta.getOrderItemOrderStatusId() + " IN (" +
                         statusPending + "," + statusBackOrdered + ")");
        
        builder.setOrderBy(InventoryReceiptMeta.getOrderItemOrderId() + " DESC," +
                           InventoryReceiptMeta.getInventoryItemId() + " DESC,"+
                           InventoryReceiptMeta.getReceivedDate()+ " DESC" );
        
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        if (list == null)
            throw new LastPageException();

        try {
            return getManagers((ArrayList<InventoryReceiptViewDO>)list);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryReceiptViewDO add(InventoryReceiptViewDO data) throws Exception {
        InventoryReceipt entity;
        InventoryLocation entityLocation;
        InventoryLocationViewDO locationData;
        InventoryXPut entityXPut;
        InventoryItemViewDO item;
        Integer quantity, count;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new InventoryReceipt();

        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setQuantityReceived(data.getQuantityReceived());
        entity.setUnitCost(data.getUnitCost());
        entity.setQcReference(data.getQcReference());
        entity.setExternalReference(data.getExternalReference());
        entity.setUpc(data.getUpc());

        manager.persist(entity);
        data.setId(entity.getId());

        item = inventoryItem.fetchById(data.getInventoryItemId());
        if ("Y".equals(item.getIsSerialMaintained())) {
            //
            // The unique identifier for each lot is the inventory location id.
            // Serialized inventory items are unique in the sense that each item
            // has a lot of one-- therefore N serialized items are inserted N
            // times with the quantity of one.
            //
            quantity = 1;
            count = data.getQuantityReceived();
        } else if ("Y".equals(item.getIsBulk()) && "Y".equals(data.getAddToExistingLocation())) {
            //
            // for bulk items, the new received inventory can be added to
            // existing location or a new inventory location can be created
            //
            quantity = data.getQuantityReceived();
            count = 0;
        } else {
            //
            // a new location is created and quantity added
            //
            quantity = data.getQuantityReceived();
            count = 1;
        }

        locationData = data.getInventoryLocations().get(0);
        do {
            if (count != 0) {
                entityLocation = new InventoryLocation();
                entityLocation.setInventoryItemId(data.getInventoryItemId());
                entityLocation.setLotNumber(locationData.getLotNumber());
                entityLocation.setStorageLocationId(locationData.getStorageLocationId());
                entityLocation.setQuantityOnhand(quantity);
                entityLocation.setExpirationDate(locationData.getExpirationDate());
                manager.persist(entityLocation);
                locationData.setId(entityLocation.getId());
            } else {
                entityLocation = manager.find(InventoryLocation.class, locationData.getId());
                entityLocation.setQuantityOnhand(entityLocation.getQuantityOnhand() + quantity);
            }

            entityXPut = new InventoryXPut();
            entityXPut.setInventoryReceiptId(data.getId());
            entityXPut.setInventoryLocationId(entityLocation.getId());
            entityXPut.setQuantity(quantity);
            manager.persist(entityXPut);
        } while ( --count > 0);

        return data;
    }    
    
    public InventoryReceiptViewDO update(InventoryReceiptViewDO data) throws Exception {
        int i, oldQ, currQ, dQ;
        InventoryReceipt entity;
        InventoryLocation oldEntityLocation, currEntityLocation;
        InventoryLocationViewDO locationData;
        InventoryXPut entityXPut;
        InventoryItemViewDO itemData;
        List<InventoryXPutViewDO> xputs;

        locationData = data.getInventoryLocations().get(0);
        currQ = 0;
        oldQ = 0;
        entity = null;

        if ( !data.isChanged() && !locationData.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryReceipt.class, data.getId());
        oldQ = entity.getQuantityReceived();
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setOrderItemId(data.getOrderItemId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setQuantityReceived(data.getQuantityReceived());
        entity.setUnitCost(data.getUnitCost());
        entity.setQcReference(data.getQcReference());
        entity.setExternalReference(data.getExternalReference());
        entity.setUpc(data.getUpc());

        currQ = data.getQuantityReceived();

        itemData = inventoryItem.fetchById(data.getInventoryItemId());
        xputs = inventoryXPut.fetchByInventoryReceiptId(data.getId());

        oldEntityLocation = null;
        entityXPut = null;

        dQ = currQ - oldQ;

        if ("Y".equals(itemData.getIsSerialMaintained())) {
            //
            // we need to make sure that we have at least as many items left
            // in the system as would be necessary to be removed, if required,
            // as part of adjusting the quantity received through this inventory
            // receipt
            //
            if (xputs.size() + dQ < 0)
                throw new FieldErrorException("notSuffcientQtyAtLocException", null);
            for (i = 0; i < Math.abs(dQ); i++ ) {
                if (dQ < 0) {
                    oldEntityLocation = manager.find(InventoryLocation.class,
                                                     xputs.get(i).getInventoryLocationId());
                    entityXPut = manager.find(InventoryXPut.class, xputs.get(i).getId());
                    manager.remove(oldEntityLocation);
                    manager.remove(entityXPut);
                    xputs.remove(i);
                } else if (dQ > 0) {
                    currEntityLocation = new InventoryLocation();
                    entityXPut = new InventoryXPut();

                    currEntityLocation.setInventoryItemId(data.getInventoryItemId());
                    currEntityLocation.setLotNumber(locationData.getLotNumber());
                    currEntityLocation.setStorageLocationId(locationData.getStorageLocationId());
                    currEntityLocation.setQuantityOnhand(1);
                    currEntityLocation.setExpirationDate(locationData.getExpirationDate());
                    manager.persist(currEntityLocation);

                    entityXPut.setInventoryReceiptId(data.getId());
                    entityXPut.setInventoryLocationId(currEntityLocation.getId());
                    entityXPut.setQuantity(1);
                    manager.persist(entityXPut);
                }
            }

            //
            // if current quantity is zero it means that we didn't receive any
            // items and thus we need to delete the record that acts as the
            // receipt for the items; inventory xput records associated with
            // this record would have been deleted by the logic above
            //
            if (currQ == 0)
                manager.remove(entity);

            if (oldEntityLocation == null)
                oldEntityLocation = manager.find(InventoryLocation.class,
                                                 xputs.get(0).getInventoryLocationId());
        } else {
            oldEntityLocation = manager.find(InventoryLocation.class,
                                             xputs.get(0).getInventoryLocationId());
            entityXPut = manager.find(InventoryXPut.class, xputs.get(0).getId());

            //
            // we need to make sure that we have at least as many items left in
            // the system at the inventory location associated with this inventory
            // receipt as would be necessary to be removed, if required, as part
            // of adjusting the quantity on hand at the location
            //
            if (oldEntityLocation.getQuantityOnhand() + dQ < 0)
                throw new FieldErrorException("notSuffcientQtyAtLocException", null);

            //
            // adjust existing xput and inventory location
            //
            oldEntityLocation.setQuantityOnhand(oldEntityLocation.getQuantityOnhand() + dQ);
            entityXPut.setQuantity(currQ);

            //
            // if current quantity is zero it means that we didn't receive any
            // items and thus we need to delete the record that acts as the
            // receipt for the items as well as the xput record associated with it
            //
            if (currQ == 0) {
                manager.remove(entity);
                manager.remove(entityXPut);
            }
        }

        if ( ( !oldEntityLocation.getId().equals(locationData.getId())) && 
                        ( !oldEntityLocation.getStorageLocationId().equals(locationData.getStorageLocationId()))) {
            if ("Y".equals(itemData.getIsSerialMaintained())) {
                for (i = 0; i < xputs.size(); i++ ) {
                    oldEntityLocation = manager.find(InventoryLocation.class,
                                                     xputs.get(i).getInventoryLocationId());                    
                    oldEntityLocation.setStorageLocationId(locationData.getStorageLocationId());
                }
            } else {
                oldEntityLocation.setQuantityOnhand(oldEntityLocation.getQuantityOnhand() - oldQ);
            }

            if ( ! ("Y".equals(data.getAddToExistingLocation()))) {
                currEntityLocation = new InventoryLocation();

                currEntityLocation.setInventoryItemId(data.getInventoryItemId());
                currEntityLocation.setLotNumber(locationData.getLotNumber());
                currEntityLocation.setStorageLocationId(locationData.getStorageLocationId());
                currEntityLocation.setQuantityOnhand(currQ);
                currEntityLocation.setExpirationDate(locationData.getExpirationDate());
                manager.persist(currEntityLocation);

                for (i = 0; i < xputs.size(); i++ ) {
                    entityXPut = manager.find(InventoryXPut.class, xputs.get(i).getId());
                    entityXPut.setInventoryLocationId(currEntityLocation.getId());
                }
            }
        } else {
            //
            // we need to update the old inventory location's lot number and
            // expiration date in case they got changed
            //
            for (i = 0; i < xputs.size(); i++ ) {
                oldEntityLocation = manager.find(InventoryLocation.class,
                                                 xputs.get(i).getInventoryLocationId());
                oldEntityLocation.setLotNumber(locationData.getLotNumber());
                oldEntityLocation.setExpirationDate(locationData.getExpirationDate());
            }
        }

        return data;
    }
    

    public InventoryReceiptViewDO fetchForUpdate(Integer id) throws Exception {
        lock.lock(ReferenceTable.INVENTORY_RECEIPT, id);
        return fetchById(id);
    }

    public InventoryReceiptViewDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.INVENTORY_RECEIPT, id);
        return fetchById(id);
    }

    public void delete(InventoryReceiptDO data) throws Exception {
        // TODO Auto-generated method stub
    }

    public void validate(InventoryReceiptViewDO data) throws Exception {
        ValidationErrorsList list;
        InventoryItemViewDO item;
        InventoryLocationViewDO location;
        Integer orderItemQ, receivedQ;

        //
        // we do this here in order to make sure we don't try to validate a record
        // that the user had no intentions of updating on the screen but was not able
        // to remove because it represented an inventory item no quantity of which
        // was received        
        // 
        if (data.getId() == null && data.getQuantityReceived() == null && data.getReceivedDate() == null)
            return;
        
        list = new ValidationErrorsList();
        item = null;
                
        if (data.getReceivedDate() == null)
            list.add(new FieldErrorException("fieldRequiredException", InventoryReceiptMeta.getReceivedDate()));
        
        if (data.getInventoryItemId() == null) {
            if (data.getId() == null)
                list.add(new FieldErrorException("fieldRequiredException", InventoryReceiptMeta.getInventoryItemName()));
        } else {
            item = inventoryItem.fetchById(data.getInventoryItemId());            
        }
            
        if (data.getOrganizationId() == null)
            list.add(new FieldErrorException("fieldRequiredException", InventoryReceiptMeta.getOrganizationName()));
        
        if (data.getInventoryLocations() != null) {
            location = data.getInventoryLocations().get(0);
            if (location.getStorageLocationId() == null) 
                list.add(new FieldErrorException("storageLocReqForItemException", InventoryReceiptMeta.getInventoryItemName()));
            
            if (item != null && location.getLotNumber() == null && "Y".equals(item.getIsLotMaintained())) 
                list.add(new FieldErrorException("lotNumRequiredForOrderItemException", 
                                                 InventoryReceiptMeta.getInventoryItemName()));
            
            /*
             * if this item is to be added to an existing location then the
             * location specified here must already exist
             */
            if ("Y".equals(data.getAddToExistingLocation()) && location.getId() == null)
                list.add(new FieldErrorException("itemNotExistAtLocationException",
                                                 InventoryReceiptMeta.getInventoryItemName()));
            else if ("N".equals(data.getAddToExistingLocation()) && location.getId() != null)
                list.add(new FieldErrorException("itemExistAtLocationException",
                                                 InventoryReceiptMeta.getInventoryItemName()));
            
        } else {
            list.add(new FieldErrorException("storageLocReqForItemException", InventoryReceiptMeta.getInventoryItemName()));
        }                
               
        orderItemQ = data.getOrderItemQuantity();
        receivedQ = data.getQuantityReceived();
        if (receivedQ == null) {
            if (data.getId() != null)
                list.add(new FieldErrorException("numRecReqForReceivedItemsException", InventoryReceiptMeta.getQuantityReceived()));
        } else if (receivedQ < 0){
            list.add(new FieldErrorException("numRecNotLessThanZeroException", InventoryReceiptMeta.getQuantityReceived()));
        } else if (orderItemQ != null && receivedQ > orderItemQ){
            list.add(new FieldErrorException("numReqLessThanNumRecException", InventoryReceiptMeta.getQuantityReceived()));
        }
        
        if (list.size() > 0)
            throw list;
    }    
    
    private ArrayList<InventoryReceiptManager> getManagers(ArrayList<InventoryReceiptViewDO> list) throws Exception {
        Integer orderId, prevOrderId,prevInvItemId, ordItemId, orgId, qtyReceived;
        Order order;
        InventoryReceiptManager man;
        ArrayList<InventoryReceiptManager> managers;
        OrderItem item;
        InventoryReceiptViewDO data, prevData;
        OrganizationDO org;
        HashMap<Integer, OrganizationDO> orgMap;               
        ArrayList<InventoryLocationViewDO> locations;
        ArrayList<Integer> orderList;        

        prevOrderId = null;
        managers = new ArrayList<InventoryReceiptManager>();
        orderList = new ArrayList<Integer>();
        orgMap = new HashMap<Integer, OrganizationDO>();        
        man = null;
        qtyReceived = 0;        
        data = null;
        prevData = null;
        prevInvItemId = null;

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);                                                                                                                                                                                                                                                 
            orderId = data.getOrderItemOrderId();
            if (!DataBaseUtil.isSame(prevOrderId, orderId))      
                prevInvItemId = null;                
                                           
            if (orderId == null) {
                orgId = data.getOrganizationId();
                if (orgId != null) {
                    org = orgMap.get(orgId);
                    if (org == null)
                        org = organization.fetchById(orgId);
                    data.setOrganization(org);
                    orgMap.put(orgId, org);
                }
                man = InventoryReceiptManager.getInstance();
                man.addReceipt(data);
            } else {
                if (!orderList.contains(orderId)) {
                    //
                    // this is done in order to make sure that if the inventory item in the
                    // last DO in the order has not been received the full quantity of,
                    // the additional DO gets added at the end of the list when the order changes
                    //
                    if (prevData != null && prevData.getOrderItemQuantity() > qtyReceived && qtyReceived != 0)
                        man.addReceipt(getRemainingQtyReceipt(prevData, qtyReceived, prevOrderId));
                    
                    man = InventoryReceiptManager.getInstance();
                    managers.add(man);
                    orderList.add(orderId);
                }
                order = manager.find(Order.class, orderId);

                ordItemId = data.getOrderItemId();
                item = manager.find(OrderItem.class, ordItemId);
                if (data.getId() == null) {
                    data.setInventoryItemId(item.getInventoryItemId());
                    data.setOrganizationId(order.getOrganizationId());
                }
                
                org = organization.fetchById(order.getOrganizationId());
                data.setOrganization(org);               
                //
                // an additional DO is added after the DOs that show a received 
                // inventory item if the total quantity as specified in 
                // the order item for the inventory item has not been received   
                //
                if (!data.getInventoryItemId().equals(prevInvItemId)) {
                    if (prevInvItemId != null && qtyReceived != 0) {                         
                        if (prevData.getOrderItemQuantity() > qtyReceived)
                            man.addReceipt(getRemainingQtyReceipt(prevData, qtyReceived, prevOrderId));                        
                        if (data.getQuantityReceived() != null) 
                            qtyReceived = data.getQuantityReceived(); 
                        else                
                            qtyReceived = 0;
                    } else if (data.getQuantityReceived() != null) {
                       //
                       // this is done so that if the inventory item in the
                       // previous DO hasn't been received at all or if this is
                       // the first DO, qtyReceived doesn't stay as 0 if quantity
                       // received in this DO is non zero
                       //
                       qtyReceived = data.getQuantityReceived(); 
                    }                    
                } else if (data.getQuantityReceived() != null) {
                    qtyReceived += data.getQuantityReceived(); 
                }
                man.addReceipt(data);
            }
            if (data.getId() != null) {
                locations = inventoryLocation.fetchByInventoryReceiptId(data.getId());
                data.setInventoryLocations(locations);
            }
            
            prevData = data;
            prevOrderId = orderId;
            prevInvItemId = data.getInventoryItemId();
        }            
        
        //
        // this is done in order to make sure that if the inventory item in the
        // last DO has not been received the full quantity of, the addtional DO 
        // gets added at the end of the list
        //
        if (prevData.getOrderItemQuantity() > qtyReceived && qtyReceived != 0)
            man.addReceipt(getRemainingQtyReceipt(prevData, qtyReceived, prevOrderId));               
        
        return managers;
    }
    

    private InventoryReceiptViewDO getRemainingQtyReceipt(InventoryReceiptViewDO data,
                                                          Integer totalReceived,Integer orderId) {
        InventoryReceiptViewDO lastData;
        
        lastData = new InventoryReceiptViewDO();
        lastData.setOrderItemId(data.getOrderItemId());
        lastData.setInventoryItemId(data.getInventoryItemId());
        lastData.setOrganizationId(data.getOrganizationId());
        lastData.setOrganization(data.getOrganization());
        lastData.setOrderItemQuantity(data.getOrderItemQuantity() - totalReceived);
        lastData.setUnitCost(data.getOrderItemUnitCost());
        lastData.setOrderItemOrderId(orderId);
        lastData.setOrderItemOrderExternalOrderNumber(data.getOrderItemOrderExternalOrderNumber());
        
        return lastData;
    }  
}
