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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.InventoryXPutDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryXPut;
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
import org.openelis.local.OrderItemLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.manager.OrderManager;
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryreceipt-select")
public class InventoryReceiptBean implements InventoryReceiptRemote, InventoryReceiptLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                     manager;

    @EJB
    private LockLocal                         lockBean;

    @EJB
    private DictionaryLocal                   dictionaryBean;

    @EJB
    private OrderLocal                        orderBean;

    @EJB
    private OrderItemLocal                    orderItemBean;

    @EJB
    private OrganizationLocal                 organizationBean;

    @EJB
    private InventoryItemLocal                inventoryItemBean;

    @EJB
    private InventoryLocationLocal            inventoryLocationBean;

    @EJB
    private InventoryXPutLocal                inventoryXPutBean;

    private static int                        statusPending, statusBackOrdered;

    private static final Logger               log  = Logger.getLogger(InventoryReceiptBean.class.getName());

    private static final InventoryReceiptMeta meta = new InventoryReceiptMeta();

    @PostConstruct
    public void init() {
        DictionaryDO data;

        try {
            data = dictionaryBean.fetchBySystemName("order_status_pending");
            statusPending = data.getId();
        } catch (Throwable e) {
            statusPending = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='order_status_pending'", e);
        }

        try {
            data = dictionaryBean.fetchBySystemName("order_status_back_ordered");
            statusBackOrdered = data.getId();
        } catch (Throwable e) {
            statusBackOrdered = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='order_status_back_ordered'",
                    e);
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
                           InventoryReceiptMeta.getInventoryItemId() + " ASC");
        
        
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

        item = inventoryItemBean.fetchById(data.getInventoryItemId());
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
        List<InventoryXPutDO> xputs;

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

        itemData = inventoryItemBean.fetchById(data.getInventoryItemId());
        xputs = inventoryXPutBean.fetchByInventoryReceiptId(data.getId());

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
        lockBean.getLock(ReferenceTable.INVENTORY_RECEIPT, id);
        return fetchById(id);
    }

    public InventoryReceiptViewDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.INVENTORY_RECEIPT, id);
        return fetchById(id);
    }

    public void delete(InventoryReceiptViewDO data) throws Exception {
        // TODO Auto-generated method stub
    }

    public void validate(InventoryReceiptViewDO data) throws Exception {
        ValidationErrorsList list;
        InventoryItemViewDO item;
        InventoryLocationViewDO locationData;
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
            item = inventoryItemBean.fetchById(data.getInventoryItemId());            
        }
            
        if (data.getOrganizationId() == null)
            list.add(new FieldErrorException("fieldRequiredException", InventoryReceiptMeta.getOrganizationName()));
        
        if (data.getInventoryLocations() != null) {
            locationData = data.getInventoryLocations().get(0);
            if (locationData.getStorageLocationId() == null) 
                list.add(new FieldErrorException("storageLocReqForItemException", InventoryReceiptMeta.getInventoryItemName()));
            
            if (item != null && locationData.getLotNumber() == null && "Y".equals(item.getIsLotMaintained())) 
                list.add(new FieldErrorException("lotNumRequiredForOrderItemException", 
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

    private ArrayList<InventoryReceiptManager> getManagers(ArrayList<InventoryReceiptViewDO> list)
                                                                                                  throws Exception {
        Integer ordId, ordItemId, orgId;
        OrderViewDO order;
        InventoryReceiptManager man;
        ArrayList<InventoryReceiptManager> managers;
        OrderItemViewDO item;
        InventoryReceiptViewDO data;
        OrganizationDO org;
        HashMap<Integer, OrderViewDO> orderMap;
        HashMap<Integer, OrderItemViewDO> orderItemMap;
        HashMap<Integer, OrganizationDO> orgMap;
        ArrayList<InventoryLocationViewDO> locations;

        managers = new ArrayList<InventoryReceiptManager>();
        orderMap = new HashMap<Integer, OrderViewDO>();
        orderItemMap = new HashMap<Integer, OrderItemViewDO>();
        orgMap = new HashMap<Integer, OrganizationDO>();
        man = null;

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            ordId = data.getOrderItemOrderId();
            if (ordId == null) {
                orgId = data.getOrganizationId();
                if (orgId != null) {
                    org = orgMap.get(orgId);
                    if (org == null)
                        org = organizationBean.fetchById(orgId);
                    data.setOrganization(org);
                    orgMap.put(orgId, org);
                }
                man = InventoryReceiptManager.getInstance();
                man.addReceipt(data);
            } else {
                order = orderMap.get(ordId);
                if (order == null) {
                    order = orderBean.fetchById(ordId);
                    man = InventoryReceiptManager.getInstance();
                    orderMap.put(ordId, order);
                    managers.add(man);
                }

                ordItemId = data.getOrderItemId();
                item = orderItemMap.get(ordItemId);
                if (item == null)
                    item = orderItemBean.fetchById(ordItemId);
                orderItemMap.put(ordItemId, item);
                if (data.getId() == null) {
                    data.setInventoryItemId(item.getInventoryItemId());
                    data.setOrganizationId(order.getOrganizationId());
                }
                data.setOrganization(order.getOrganization());
                man.addReceipt(data);
            }
            if (data.getId() != null) {
                locations = inventoryLocationBean.fetchByInventoryReceiptId(data.getId());
                data.setInventoryLocations(locations);
            }
        }

        return managers;
    }

    @RolesAllowed("receipt-update")
    public void updateInventoryTransfer(List inventoryTransfers) throws Exception {
        // validate the data before we start the transaction
        /*
         * validateTransfers(inventoryTransfers);
         * manager.setFlushMode(FlushModeType.COMMIT);
         * lockTransfers(inventoryTransfers); if(inventoryTransfers.size() == 0)
         * return; //create a new internal order record only on add Query query
         * = manager.createNamedQuery("Dictionary.FetchBySystemName");
         * query.setParameter("name","order_status_processed"); DictionaryDO
         * dictDO = (DictionaryDO)query.getResultList().get(0); Integer
         * completedStatusValue = dictDO.getId(); Order internalOrder = null;
         * if(((InventoryReceiptDO)inventoryTransfers.get(0)).getOrderNumber()
         * != null) internalOrder = manager.find(Order.class,
         * (Integer)((InventoryReceiptDO
         * )inventoryTransfers.get(0)).getOrderNumber()); else internalOrder =
         * new Order(); internalOrder.setStatusId(completedStatusValue);
         * internalOrder.setRequestedBy(ctx.getCallerPrincipal().getName());
         * internalOrder.setOrderedDate(Datetime.getInstance());
         * internalOrder.setNeededInDays(0); //
         * internalOrder.setIsExternal("N"); if(internalOrder.getId() == null)
         * manager.persist(internalOrder); for(int i=0;
         * i<inventoryTransfers.size(); i++){ InventoryReceiptDO transferDO =
         * (InventoryReceiptDO)inventoryTransfers.get(i); Integer qtyInto = -1;
         * if(transferDO.getParentRatio() != null) qtyInto =
         * transferDO.getQuantityReceived() transferDO.getParentRatio(); else
         * if(transferDO.getChildRatio() != null) qtyInto =
         * transferDO.getQuantityReceived() / transferDO.getChildRatio();
         * //create new order item records with FROM inv item ids OrderItem
         * orderItem = null; if(transferDO.getOrderItemId() != null) orderItem =
         * manager.find(OrderItem.class, transferDO.getOrderItemId()); else
         * orderItem = new OrderItem();
         * orderItem.setInventoryItemId(transferDO.getFromInventoryItemId());
         * orderItem.setOrderId(internalOrder.getId());
         * orderItem.setQuantity(transferDO.getQuantityReceived());
         * if(orderItem.getId() == null) manager.persist(orderItem); //subtract
         * quantity from FROM inv loc record for each order item
         * InventoryLocation location = null; location =
         * manager.find(InventoryLocation.class,
         * transferDO.getFromStorageLocationId());
         * location.setQuantityOnhand(location
         * .getQuantityOnhand()-transferDO.getQuantityReceived()); //create new
         * trans_location_order record with each transfer from inv_loc //we need
         * to get the loc trans and the loc ids InventoryXUse transLocation =
         * null; if(transferDO.getTransLocationOrderId() != null) transLocation
         * = manager.find(InventoryXUse.class,
         * transferDO.getTransLocationOrderId()); else transLocation = new
         * InventoryXUse();
         * transLocation.setInventoryLocationId(location.getId());
         * transLocation.setOrderItemId(orderItem.getId());
         * transLocation.setQuantity(transferDO.getQuantityReceived());
         * if(transLocation.getId() == null) manager.persist(transLocation);
         * //create new inv receipt records InventoryReceipt receipt = null;
         * if(transferDO.getId() != null) receipt =
         * manager.find(InventoryReceipt.class, transferDO.getId()); else
         * receipt = new InventoryReceipt();
         * receipt.setInventoryItemId(transferDO.getInventoryItemId());
         * receipt.setOrderItemId(orderItem.getId());
         * receipt.setQuantityReceived(qtyInto); if(receipt.getReceivedDate() ==
         * null) receipt.setReceivedDate(Datetime.getInstance(Datetime.YEAR,
         * Datetime.DAY)); if(receipt.getId() == null) manager.persist(receipt);
         * //create inventory_receipt_order_item record
         * InventoryReceiptOrderItem invReceiptOrderItem = null; if
         * (transferDO.getInventoryReceiptOrderItemId() == null)
         * invReceiptOrderItem = new InventoryReceiptOrderItem(); else
         * invReceiptOrderItem = manager.find(InventoryReceiptOrderItem.class,
         * transferDO.getInventoryReceiptOrderItemId());
         * invReceiptOrderItem.setInventoryReceiptId(receipt.getId());
         * invReceiptOrderItem.setOrderItemId(orderItem.getId());
         * if(invReceiptOrderItem.getId() == null)
         * manager.persist(invReceiptOrderItem); //create inventory x put record
         * query =
         * manager.createNamedQuery("InventoryXPut.TransIdsLocIdsByReceiptId");
         * query.setParameter("id", receipt.getId()); List locTransLocIds =
         * query.getResultList(); int numberOfLocs = locTransLocIds.size();
         * //create/update TO inv location records InventoryLocation toLoc =
         * null; if(numberOfLocs > 0) toLoc =
         * manager.find(InventoryLocation.class,
         * (Integer)((Object[])locTransLocIds.get(0))[1]); else toLoc = new
         * InventoryLocation(); if(location.getExpirationDate() != null)
         * toLoc.setExpirationDate(location.getExpirationDate());
         * toLoc.setInventoryItemId(transferDO.getInventoryItemId());
         * toLoc.setLotNumber(location.getLotNumber());
         * toLoc.setQuantityOnhand(qtyInto);
         * toLoc.setStorageLocationId(transferDO.getStorageLocationId());
         * if(toLoc.getId() == null) manager.persist(toLoc); //create
         * trans_receipt_location records InventoryXPut transReceiptLoc = null;
         * if(numberOfLocs > 0) transReceiptLoc =
         * manager.find(InventoryXPut.class,
         * (Integer)((Object[])locTransLocIds.get(0))[0]); else transReceiptLoc
         * = new InventoryXPut();
         * transReceiptLoc.setInventoryLocationId(toLoc.getId());
         * transReceiptLoc.setInventoryReceiptId(receipt.getId());
         * transReceiptLoc.setQuantity(qtyInto); if(transReceiptLoc.getId() ==
         * null) manager.persist(transReceiptLoc); }
         * unlockTransfers(inventoryTransfers);
         */
    }

    public List getInventoryItemsByUPC(String upc) {
        Query query = null;
        query = manager.createNamedQuery("InventoryReceipt.InventoryItemByUPC");
        query.setParameter("upc", upc);

        return query.getResultList();
    }

    public void validateTransfers(List<InventoryReceiptDO> inventoryTransfers) throws Exception {
        ValidationErrorsList list = new ValidationErrorsList();

        for (int i = 0; i < inventoryTransfers.size(); i++ )
            validateTransferAndLocation(inventoryTransfers.get(i), i, list);

        if (list.size() > 0)
            throw list;
    }

    private void validateTransferAndLocation(InventoryReceiptDO transferDO,
                                             int rowIndex,
                                             ValidationErrorsList exceptionList) {
        /*
         * //from item required if(transferDO.getFromInventoryItemId() == null){
         * exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * InventoryReceiptMap
         * .TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META
         * .getName())); } //from loc required
         * if(transferDO.getFromStorageLocationId() == null){
         * exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * "fromLoc")); } //on hand required if(transferDO.getQuantityReceived()
         * == null){ exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * "qtyOnHand")); } //to item required
         * if(transferDO.getInventoryItemId() == null){ exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * InventoryReceiptMap.INVENTORY_ITEM_META.getName())); } //make sure
         * check is only for bulk if("N".equals(transferDO.getIsBulk()) &&
         * transferDO.isAddToExisting()){ exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * "addToExisting")); } //to loc required
         * if(transferDO.getStorageLocationId() == null){ exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * InventoryReceiptMap
         * .TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META
         * .getStorageLocationId())); } //qty required
         * if(transferDO.getQuantityReceived() == null || new
         * Integer(0).compareTo(transferDO.getQuantityReceived()) >= 0){
         * exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * InventoryReceiptMap.getQuantityReceived())); } //validate qtys
         * if(transferDO
         * .getQuantityReceived().compareTo(transferDO.getFromQtyOnHand()) > 0){
         * exceptionList.add(new
         * TableFieldErrorException("notEnoughQuantityOnHand", rowIndex,
         * InventoryReceiptMap.getQuantityReceived())); }
         * if(transferDO.getChildRatio() != null && transferDO.getParentRatio()
         * == null &&
         * transferDO.getQuantityReceived()%transferDO.getChildRatio() > 0)
         * exceptionList.add(new
         * TableFieldErrorException("qtyToParentRatioInvalid", rowIndex,
         * InventoryReceiptMap.getQuantityReceived()));
         */
    }

    private void lockRecords(List receipts, boolean validate) throws Exception {
        /*
         * if(receipts.size() == 0) return; List orderIds = new ArrayList();
         * for(int i=0; i<receipts.size(); i++){ InventoryReceiptDO receiptDO =
         * (InventoryReceiptDO)receipts.get(i); //put the order id in the list
         * if it is there if(receiptDO.getOrderNumber() != null &&
         * !orderIds.contains(receiptDO.getOrderNumber()))
         * orderIds.add(receiptDO.getOrderNumber()); //get a list of all the
         * locations Query query =
         * manager.createNamedQuery("InventoryReceipt.LocationIdsByReceiptId");
         * query.setParameter("id", receiptDO.getId()); List locationIds =
         * query.getResultList(); //lock all the location records for(int j=0; j
         * < locationIds.size(); j++){ if(validate)
         * lockBean.validateLock(invLocRefTableId, (Integer)locationIds.get(j));
         * else lockBean.getLock(invLocRefTableId, (Integer)locationIds.get(j));
         * } } //we need to lock the orders for(int j=0; j<orderIds.size();
         * j++){ if(validate) lockBean.validateLock(orderRefTableId,
         * (Integer)orderIds.get(j)); else lockBean.getLock(orderRefTableId,
         * (Integer)orderIds.get(j)); }
         */
    }

    private void lockTransfers(List transfers) throws Exception {
        /*
         * if(transfers.size() == 0) return; for(int i=0; i<transfers.size();
         * i++){ InventoryReceiptDO receiptDO =
         * (InventoryReceiptDO)transfers.get(i);
         * lockBean.validateLock(invLocRefTableId,
         * receiptDO.getFromStorageLocationId()); }
         */
    }

    private void unlockTransfers(List transfers) throws Exception {
        /*
         * if(transfers.size() == 0) return; for(int i=0; i<transfers.size();
         * i++){ InventoryReceiptDO receiptDO =
         * (InventoryReceiptDO)transfers.get(i);
         * lockBean.giveUpLock(invLocRefTableId,
         * receiptDO.getFromStorageLocationId()); }
         */
    }
}
