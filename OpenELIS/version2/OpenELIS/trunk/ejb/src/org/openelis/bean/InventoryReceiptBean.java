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
import java.util.HashMap;
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
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryXPut;
import org.openelis.entity.InventoryXUse;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("receipt-select")
public class InventoryReceiptBean implements InventoryReceiptRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final InventoryReceiptMetaMap InventoryReceiptMap = new InventoryReceiptMetaMap();
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = (SystemUserDO)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"userdo");
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }
    
    public List getInventoryReceiptRecords(Integer orderId) {
        //get order item id for things that have yet to be received
        Query query = manager.createNamedQuery("InventoryReceipt.OrderItemListByOrderNum");
        query.setParameter("id", orderId);
        List orderItemIdsNotRecieved = query.getResultList();
        
        List inventoryReceiptList = new ArrayList();
        
        for(int j=0; j<orderItemIdsNotRecieved.size(); j++){
            //this query should bring back minimal information because we dont have a transaction record to join to
            query = manager.createNamedQuery("InventoryReceipt.InventoryReceiptNotRecByOrderId");
            query.setParameter("id", (Integer)orderItemIdsNotRecieved.get(j));
            inventoryReceiptList.add((InventoryReceiptDO)query.getResultList().get(0));
        }
        
        return inventoryReceiptList;
    }

    @RolesAllowed("receipt-update")
    public List queryAndLock(HashMap fields, int first, int max, boolean receipt) throws Exception {
        
        List queryResultList = query(fields, first, max, receipt);
        
        //try and lock the necessary records
        lockRecords(queryResultList);
                
        return queryResultList;
    }
    
    public List queryAndUnlock(HashMap fields, int first, int max, boolean receipt) throws Exception {
        List queryResultList = query(fields, first, max, receipt);
        
        //try and unlock the necessary records
        unlockRecords(queryResultList);
                
        return queryResultList;
    }
    
    public List query(HashMap fields, int first, int max, boolean receipt) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(InventoryReceiptMap);
        
        String selectString = "distinct new org.openelis.domain.InventoryReceiptDO("+
                 InventoryReceiptMap.getId()+", "+
                 InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()+", "+
                 InventoryReceiptMap.getReceivedDate()+", "+
                 InventoryReceiptMap.getUpc()+", "+                         
                 InventoryReceiptMap.getInventoryItemId()+", "+
                 InventoryReceiptMap.INVENTORY_ITEM_META.getName()+", "+
                 InventoryReceiptMap.ORDER_ITEM_META.getId()+", "+
                 InventoryReceiptMap.getOrganizationId()+", ";
                 if(receipt)
                     selectString += InventoryReceiptMap.ORGANIZATION_META.getName()+", ";
                 selectString += InventoryReceiptMap.getQuantityReceived()+", "+
                 InventoryReceiptMap.ORDER_ITEM_META.getUnitCost()+", "+
                 InventoryReceiptMap.getQcReference()+", "+
                 InventoryReceiptMap.getExternalReference()+", ";
                 if(receipt){
                     selectString += InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getStreetAddress()+", "+
                     InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getMultipleUnit()+", "+
                     InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getCity()+", "+
                     InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getState()+", "+
                     InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getZipCode()+", ";
                 }
                 selectString += InventoryReceiptMap.INVENTORY_ITEM_META.getDescription()+", "+
                 InventoryReceiptMap.DICTIONARY_STORE_META.getEntry()+", "+
                 InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getEntry()+", "+
                 InventoryReceiptMap.ORDER_ITEM_META.getQuantity()+", "+
                 InventoryReceiptMap.INVENTORY_ITEM_META.getIsBulk()+", "+
                 InventoryReceiptMap.INVENTORY_ITEM_META.getIsLotMaintained()+", "+
                 InventoryReceiptMap.INVENTORY_ITEM_META.getIsSerialMaintained()+", "+
                 InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId()+", "+
                 InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getName()+", "+
                 InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.STORAGE_UNIT_META.getDescription()+", "+
                 InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()+", ";
                 if(receipt){
                     selectString += InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber()+", "+
                     InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate();
                 }else{
                     selectString += InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber()+", "+ 
                     InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate();
                 }
                  if(!receipt){
                      selectString+=", "+InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getId()+", "+ //from id
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getName()+", "+ //from name
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getDescription()+", "+ //from desc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.getStorageLocationId()+", "+ //from storage loc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getName()+", "+ //from loc name 
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.STORAGE_UNIT_META.getDescription()+", "+ //from loc desc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()+")"; //from loc
                  }else
                      selectString+=")";
                 
        qb.setSelect(selectString);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        qb.addWhere(InventoryReceiptMap.getInventoryItemId() + " = " + InventoryReceiptMap.INVENTORY_ITEM_META.getId());
        //if(receipt)
            
        
        if(receipt){
            qb.addWhere(InventoryReceiptMap.getOrganizationId() + " = " + InventoryReceiptMap.ORGANIZATION_META.getId());
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
            qb.addWhere(InventoryReceiptMap.getOrganizationId()+" is not null ");
        }else{
            qb.addWhere(InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
            qb.addWhere(InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
            qb.addWhere(InventoryReceiptMap.getOrganizationId()+" is null ");
            //tie trans loc order to the receipt somehow
            qb.addWhere(InventoryReceiptMap.TRANS_LOC_ORDER_META.getOrderItemId()+" = "+InventoryReceiptMap.ORDER_ITEM_META.getId());
        }
        
        if(receipt)
            qb.setOrderBy(InventoryReceiptMap.getReceivedDate()+" DESC, "+InventoryReceiptMap.ORGANIZATION_META.getName());
        else
            qb.setOrderBy(InventoryReceiptMap.getReceivedDate()+" DESC");
        
        String fromClause = qb.getFromClause(qb.getWhereClause());
        if(!receipt)
            fromClause+=", TransLocationOrder locOrderTrans ";
        
        sb.append(qb.getSelectClause()).append(fromClause).append(qb.getWhereClause()).append(qb.getOrderBy());
        System.out.println(sb.toString());
//        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
    
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
        //set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    @RolesAllowed("receipt-update")
    public void updateInventoryReceipt(List inventoryReceipts) throws Exception {

        manager.setFlushMode(FlushModeType.COMMIT);
        
        //lock the necessary records
        lockRecords(inventoryReceipts);

        List orderIds = new ArrayList();
        
        for (int i=0; i<inventoryReceipts.size();i++) {
            //
            //insert/update the receipt record
            //
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)inventoryReceipts.get(i);   
            Integer newQuantityReceived = receiptDO.getQuantityReceived();
            
            if(receiptDO.getOrderNumber() != null && !orderIds.contains(receiptDO.getOrderNumber()) && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0)
                orderIds.add(receiptDO.getOrderNumber());
                
            InventoryReceipt receipt = null;
    
            if (receiptDO.getId() == null)
                receipt = new InventoryReceipt();
            else
                receipt = manager.find(InventoryReceipt.class, receiptDO.getId());

            //validate the receipt record
            List exceptionList = new ArrayList();
            validateReceiptAndLocation(receiptDO, i, exceptionList);
            if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
            }
            
            System.out.println("1");
            if(!receiptDO.getDelete() && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                receipt.setExternalReference(receiptDO.getExternalReference());
                receipt.setInventoryItemId(receiptDO.getInventoryItemId());
                receipt.setOrderItemId(receiptDO.getOrderItemId());
                receipt.setOrganizationId(receiptDO.getOrganizationId());
                receipt.setQcReference(receiptDO.getQcReference());
                receipt.setQuantityReceived(receiptDO.getQuantityReceived());
                receipt.setReceivedDate(receiptDO.getReceivedDate());
                receipt.setUnitCost(receiptDO.getUnitCost());
                receipt.setUpc(receiptDO.getUpc());

                if (receipt.getId() == null) {
                    manager.persist(receipt);
                }
            }
            
            //
            //insert/update the inventory transaction record that points to the order item
            //
         /*   if(!receiptDO.getDelete() && receiptDO.getOrderItemId() != null && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                TransReceiptOrder transReceiptOrder = null;
                
                if (receiptDO.getTransReceiptOrderId() == null)
                    transReceiptOrder = new TransReceiptOrder();
                else
                    transReceiptOrder = manager.find(TransReceiptOrder.class, receiptDO.getTransReceiptOrderId());
                
                transReceiptOrder.setInventoryReceiptId(receipt.getId());
                transReceiptOrder.setOrderItemId(receiptDO.getOrderItemId());
                transReceiptOrder.setQuantity(receiptDO.getQuantityReceived());
                
                manager.persist(transReceiptOrder);
            }*/
            
            System.out.println("2");
            int numberOfLocs=1;
            List locTransLocIds = null;

            //we need to get the loc trans and the loc ids
            if(receiptDO.getId() != null){
                System.out.println("2a");
                Query query = manager.createNamedQuery("InventoryXPut.TransIdsLocIdsByReceiptId");
                query.setParameter("id", receiptDO.getId());
                locTransLocIds = query.getResultList();
                numberOfLocs = locTransLocIds.size();
                
                //we need to make sure we have the highest amount of locations
                if("Y".equals(receiptDO.getIsSerialMaintained()) && newQuantityReceived > numberOfLocs)
                    numberOfLocs = newQuantityReceived;
                
            }else if(receiptDO.getQuantityReceived() != null && "Y".equals(receiptDO.getIsSerialMaintained())){
                System.out.println("2b");
                numberOfLocs = receiptDO.getQuantityReceived();
            }
            
            System.out.println("3");
            if(newQuantityReceived != null){
                int numberOfZeroQtys = numberOfLocs - newQuantityReceived;
                int j=0;
                while(j<numberOfLocs){
                    //
                    //insert/update the location record
                    //
                    InventoryLocation invLocation = null;
                    
                    if (receiptDO.getId() == null || (receiptDO.getId() != null && j>=locTransLocIds.size()))
                        invLocation = new InventoryLocation();
                    else
                        invLocation = manager.find(InventoryLocation.class, (Integer)((Object[])locTransLocIds.get(j))[1]);
                    
                    if(!receiptDO.getDelete() && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                        invLocation.setExpirationDate(receiptDO.getExpDate());
                        invLocation.setInventoryItemId(receiptDO.getInventoryItemId());
                        invLocation.setLotNumber(receiptDO.getLotNumber());
                        
                        if("Y".equals(receiptDO.getIsSerialMaintained())){
                            if(numberOfZeroQtys > 0 && j<numberOfZeroQtys)
                                invLocation.setQuantityOnhand(0);
                            else
                                invLocation.setQuantityOnhand(1);
                        }else{
                            invLocation.setQuantityOnhand(receiptDO.getQuantityReceived());
                        }
                        
                        invLocation.setStorageLocationId(receiptDO.getStorageLocationId());
                            
                        if (invLocation.getId() == null)
                            manager.persist(invLocation);
                    }
                    
                    //
                    //insert/update the transaction record that points to inventory location
                    //
                    InventoryXPut transReceiptLocation =  null;
                    
                    if (receiptDO.getId() == null || (receiptDO.getId() != null && j>=locTransLocIds.size()))
                        transReceiptLocation = new InventoryXPut();
                    else
                        transReceiptLocation = manager.find(InventoryXPut.class, (Integer)((Object[])locTransLocIds.get(j))[0]);
                    
                    if(!receiptDO.getDelete() && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                        transReceiptLocation.setInventoryReceiptId(receipt.getId());
                        transReceiptLocation.setInventoryLocationId(invLocation.getId());
                        
                        if("Y".equals(receiptDO.getIsSerialMaintained())){
                            if(numberOfZeroQtys > 0 && j<numberOfZeroQtys)
                                transReceiptLocation.setQuantity(0);
                            else
                                transReceiptLocation.setQuantity(1);
                        }else{
                            transReceiptLocation.setQuantity(receiptDO.getQuantityReceived());
                        }
                        
                        manager.persist(transReceiptLocation);
                    }                        
                    j++;
                } 
            }
        }
        
        Integer completedStatusValue = null;
        //we need to run a query to see if we should set the order to completed
        for(int j=0; j < orderIds.size(); j++){
            Query query = manager.createNamedQuery("InventoryReceipt.OrdersNotCompletedCanceled");
            query.setParameter("id",orderIds.get(j)); //order id
            
            //if the size > 0 the order isnt cancelled or complete so we need check and set the order status
            if(query.getResultList().size() > 0){
                query = manager.createNamedQuery("InventoryReceipt.OrderItemsNotFilled");
                query.setParameter("id",orderIds.get(j)); //order id
                
                //if the size is 0 we need to set this order to completed
                if(query.getResultList().size() == 0){
                    if(completedStatusValue == null){
                        query = manager.createNamedQuery("Dictionary.IdBySystemName");
                        query.setParameter("systemName","order_status_processed");
                        completedStatusValue = (Integer)query.getResultList().get(0);
                    }
                    
                    Order order = manager.find(Order.class, orderIds.get(j));
                    order.setStatusId(completedStatusValue);
                }
            }
        }
        
        unlockRecords(inventoryReceipts);
    }

    @RolesAllowed("receipt-update")
    public void updateInventoryTransfer(List inventoryTransfers) throws Exception {
        System.out.println("INVENTORY TRANSFER!");
       manager.setFlushMode(FlushModeType.COMMIT);
        
        if(inventoryTransfers.size() == 0)
            return;
        
        //lock the necessary records
        //TODO remove for now   lockRecords(inventoryReceipts);
        
        System.out.println("order ["+((InventoryReceiptDO)inventoryTransfers.get(0)).getOrderNumber()+"]");
        //create a new internal order record only on add
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName","order_status_processed");
        Integer completedStatusValue = (Integer)query.getResultList().get(0);
        
        Order internalOrder = null;
        if(((InventoryReceiptDO)inventoryTransfers.get(0)).getOrderNumber() != null)
            internalOrder = manager.find(Order.class, (Integer)((InventoryReceiptDO)inventoryTransfers.get(0)).getOrderNumber());
        else
            internalOrder  = new Order();
         
        internalOrder.setStatusId(completedStatusValue);
        internalOrder.setRequestedBy(ctx.getCallerPrincipal().getName());
        internalOrder.setOrderedDate(Datetime.getInstance());
        internalOrder.setNeededInDays(0);
        internalOrder.setIsExternal("N");
            
        if(internalOrder.getId() == null)
            manager.persist(internalOrder);
        
        for(int i=0; i<inventoryTransfers.size(); i++){
            InventoryReceiptDO transferDO = (InventoryReceiptDO)inventoryTransfers.get(i);
            
            System.out.println("order item ["+transferDO.getOrderItemId()+"]");
            //create new order item records with FROM inv item ids
            OrderItem orderItem = null;
            if(transferDO.getOrderItemId() != null)
                orderItem = manager.find(OrderItem.class, transferDO.getOrderItemId());
            else
                orderItem = new OrderItem();
            
            orderItem.setInventoryItemId(transferDO.getFromInventoryItemId());
            orderItem.setOrderId(internalOrder.getId());
            orderItem.setQuantity(transferDO.getQuantityReceived());
            
            if(orderItem.getId() == null)
                manager.persist(orderItem);
            
            System.out.println("inv loc ["+transferDO.getStorageLocationId()+"]");
            //subtract quantity from FROM inv loc record for each order item
            InventoryLocation location = null;
            location = manager.find(InventoryLocation.class, transferDO.getFromStorageLocationId());
            location.setQuantityOnhand(location.getQuantityOnhand()-transferDO.getQuantityReceived());

            //create new trans_location_order record with each transfer from inv_loc
            System.out.println("loc trans loc ["+transferDO.getTransLocationOrderId()+"]");
            //we need to get the loc trans and the loc ids
            InventoryXUse transLocation = null;
            if(transferDO.getTransLocationOrderId() != null)
                transLocation = manager.find(InventoryXUse.class, transferDO.getTransLocationOrderId());
            else
                transLocation = new InventoryXUse();
            
            transLocation.setInventoryLocationId(location.getId());
            transLocation.setOrderItemId(orderItem.getId());
            transLocation.setQuantity(transferDO.getQuantityReceived());
            
            if(transLocation.getId() == null)
                manager.persist(transLocation);
            
            System.out.println("receipt ["+transferDO.getId()+"]");
            //create new inv receipt records
            InventoryReceipt receipt = null;
            
            if(transferDO.getId() != null)
                receipt = manager.find(InventoryReceipt.class, transferDO.getId());
            else
                receipt = new InventoryReceipt();
            
            receipt.setInventoryItemId(transferDO.getInventoryItemId());
            receipt.setOrderItemId(orderItem.getId());
            receipt.setQuantityReceived(transferDO.getQuantityReceived());
            if(receipt.getReceivedDate() == null)
                receipt.setReceivedDate(Datetime.getInstance(Datetime.YEAR, Datetime.DAY));

            if(receipt.getId() == null)
                manager.persist(receipt);
            
            query = manager.createNamedQuery("InventoryXPut.TransIdsLocIdsByReceiptId");
            query.setParameter("id", receipt.getId());
            List locTransLocIds = query.getResultList();
            int numberOfLocs = locTransLocIds.size();
            System.out.println("to inv loc ["+numberOfLocs+"]");
            //create/update TO inv location records
            InventoryLocation toLoc = null;

            if(numberOfLocs > 0)
                toLoc = manager.find(InventoryLocation.class, (Integer)((Object[])locTransLocIds.get(0))[1]);
            else
            toLoc = new InventoryLocation();
            
            if(location.getExpirationDate() != null)
                toLoc.setExpirationDate(location.getExpirationDate());
            toLoc.setInventoryItemId(transferDO.getInventoryItemId());
            toLoc.setLotNumber(location.getLotNumber());
            toLoc.setQuantityOnhand(transferDO.getQuantityReceived());
            toLoc.setStorageLocationId(transferDO.getStorageLocationId());
            
            if(toLoc.getId() == null)
                manager.persist(toLoc);
            
            System.out.println("trans receipt loc");
            //create trans_receipt_location records
            InventoryXPut transReceiptLoc = null;
            
            if(numberOfLocs > 0)
                transReceiptLoc = manager.find(InventoryXPut.class, (Integer)((Object[])locTransLocIds.get(0))[0]);
            else
                transReceiptLoc = new InventoryXPut();
            
            transReceiptLoc.setInventoryLocationId(toLoc.getId());
            transReceiptLoc.setInventoryReceiptId(receipt.getId());
            transReceiptLoc.setQuantity(transferDO.getQuantityReceived());
            
            if(transReceiptLoc.getId() == null)
                manager.persist(transReceiptLoc);
        }
                
        //TODO remove for now  unlockRecords(inventoryReceipts);
    }

    public List autoCompleteLocationLookupByName(String name, int maxResults){
        Query query = manager.createNamedQuery("InventoryLocation.AutoCompleteByName");
        query.setParameter("name",name);
        query.setParameter("loc",name);
        query.setParameter("desc",name);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    public List autoCompleteLocationLookupByNameInvId(String name, Integer invId, int maxResults){
        Query query = manager.createNamedQuery("InventoryLocation.AutoCompleteByNameInvId");
        query.setParameter("name",name);
        query.setParameter("loc",name);
        query.setParameter("desc",name);
        query.setParameter("id",invId);
        
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    public List getInventoryItemsByUPC(String upc){
        Query query = null;
        query = manager.createNamedQuery("InventoryReceipt.InventoryItemByUPC");
        query.setParameter("upc",upc);
        
        return query.getResultList();
    }
    
    public List validateForAdd(List inventoryReceipts) {
        List exceptionList = new ArrayList();
        
        for(int i=0; i<inventoryReceipts.size();i++){            
            InventoryReceiptDO receiptDO = (InventoryReceiptDO) inventoryReceipts.get(i);
            
            validateReceiptAndLocation(receiptDO, i, exceptionList);
        }
        
        return exceptionList;
    }

    public List validateForUpdate(List inventoryReceipts) {
        List exceptionList = new ArrayList();
        
        for(int i=0; i<inventoryReceipts.size();i++){            
            InventoryReceiptDO receiptDO = (InventoryReceiptDO) inventoryReceipts.get(i);
            
            validateReceiptAndLocation(receiptDO, i, exceptionList);
        }
        
        return exceptionList;
    }
    
    private void validateReceiptAndLocation(InventoryReceiptDO receiptDO, int rowIndex, List exceptionList){
        System.out.println("1");
        //date received required
        if(receiptDO.getReceivedDate() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getReceivedDate()));
        }
        System.out.println("2");
        //inventory item required
        if(receiptDO.getInventoryItemId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getInventoryItemId()));
        }
        System.out.println("3");
        //org required
        if(receiptDO.getOrganizationId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getOrganizationId()));
        }
        
        //qty required
        System.out.println("4");        
        if((receiptDO.getQuantityReceived() == null || "".equals(receiptDO.getQuantityReceived())) && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null))){
            
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }

        System.out.println("5");
        //location required 
        if(receiptDO.getStorageLocationId() == null && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null) || 
                          receiptDO.getQuantityReceived() != null)){
            exceptionList.add(new TableFieldErrorException("locationRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()));
        }
        
        //lot num required when checked on inventory item record 
        if("Y".equals(receiptDO.getIsLotMaintained()) && receiptDO.getLotNumber() == null && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null) || 
                          receiptDO.getQuantityReceived() != null)){
            exceptionList.add(new TableFieldErrorException("lotNumRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()));
        }
        
        
        //add to existing not allow when bulk is not checked on inventory item record
        if("N".equals(receiptDO.getIsBulk()) && receiptDO.isAddToExisting()){
            exceptionList.add(new TableFieldErrorException("cantAddToExistingException", rowIndex, InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()));
        }
        
        //order number has to be valid
        if(receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)){
            exceptionList.add(new TableFieldErrorException("inventoryReceiptInvalidOrderIdException", rowIndex, InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()));
        }
        
    }
    
    private void lockRecords(List receipts) throws Exception{
        if(receipts.size() == 0)
            return;
        
        Integer inventoryReceiptId = null;
        Integer inventoryLocationId = null;
        Integer orderId = null;
        List orderIds = new ArrayList();
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_receipt");
        inventoryReceiptId = (Integer)query.getSingleResult();
        query.setParameter("name", "inventory_location");
        inventoryLocationId = (Integer)query.getSingleResult();
        query.setParameter("name", "order");
        orderId = (Integer)query.getSingleResult();
        
        for(int i=0; i<receipts.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)receipts.get(i);
        
            if(receiptDO.getId() != null){
                lockBean.getLock(inventoryReceiptId,receiptDO.getId());
            
                //put the order id in the list if it is there
                if(receiptDO.getOrderNumber() != null && !orderIds.contains(receiptDO.getOrderNumber()))
                    orderIds.add(receiptDO.getOrderNumber());
                
                //get a list of all the locations
                query = manager.createNamedQuery("InventoryReceipt.LocationIdsByReceiptId");
                query.setParameter("id", receiptDO.getId());
                List locationIds = query.getResultList();
                
                //lock all the location records
                for(int j=0; j < locationIds.size(); j++)
                    lockBean.getLock(inventoryLocationId, (Integer)locationIds.get(j));
            }
        }
        
        //we need to lock the orders
        for(int j=0; j<orderIds.size(); j++)
            lockBean.getLock(orderId, (Integer)orderIds.get(j));
    }
    
    private void unlockRecords(List receipts) throws Exception{
        if(receipts.size() == 0)
            return;
        
        Integer inventoryReceiptId = null;
        Integer inventoryLocationId = null;
        Integer orderId = null;
        List orderIds = new ArrayList();
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_receipt");
        inventoryReceiptId = (Integer)query.getSingleResult();
        query.setParameter("name", "inventory_location");
        inventoryLocationId = (Integer)query.getSingleResult();
        query.setParameter("name", "order");
        orderId = (Integer)query.getSingleResult();
        
        for(int i=0; i<receipts.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)receipts.get(i);
        
            if(receiptDO.getId() != null){
                lockBean.giveUpLock(inventoryReceiptId,receiptDO.getId());
            
                //put the order id in the list if it is there
                if(receiptDO.getOrderNumber() != null && !orderIds.contains(receiptDO.getOrderNumber()))
                    orderIds.add(receiptDO.getOrderNumber());
                
                //get a list of all the locations
                query = manager.createNamedQuery("InventoryReceipt.LocationIdsByReceiptId");
                query.setParameter("id", receiptDO.getId());
                List locationIds = query.getResultList();
                
                //lock all the location records
                for(int j=0; j < locationIds.size(); j++)
                    lockBean.giveUpLock(inventoryLocationId, (Integer)locationIds.get(j));
            }
        }
        
        //we need to unlock the orders
        for(int j=0; j<orderIds.size(); j++)
            lockBean.giveUpLock(orderId, (Integer)orderIds.get(j));
    }
}
