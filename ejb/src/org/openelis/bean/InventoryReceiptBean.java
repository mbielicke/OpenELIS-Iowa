/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
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
import org.openelis.entity.Order;
import org.openelis.entity.TransReceiptLocation;
import org.openelis.entity.TransReceiptOrder;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
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
    public List queryAndLock(HashMap fields, int first, int max) throws Exception {
        
        List queryResultList = query(fields, first, max);
        
        //try and lock the necessary records
        lockRecords(queryResultList);
                
        return queryResultList;
    }
    
    public List queryAndUnlock(HashMap fields, int first, int max) throws Exception {
        List queryResultList = query(fields, first, max);
        
        //try and unlock the necessary records
        unlockRecords(queryResultList);
                
        return queryResultList;
    }
    
    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(InventoryReceiptMap);
        
        qb.setSelect("distinct new org.openelis.domain.InventoryReceiptDO("+
                         InventoryReceiptMap.getId()+", "+
                         InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()+", "+
                         InventoryReceiptMap.getReceivedDate()+", "+
                         InventoryReceiptMap.getUpc()+", "+                         
                         InventoryReceiptMap.getInventoryItemId()+", "+
                         InventoryReceiptMap.INVENTORY_ITEM_META.getName()+", "+
                         InventoryReceiptMap.ORDER_ITEM_META.getId()+", "+
                         InventoryReceiptMap.getOrganizationId()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.getName()+", "+
                         InventoryReceiptMap.getQuantityReceived()+", "+
                         InventoryReceiptMap.ORDER_ITEM_META.getUnitCost()+", "+
                         InventoryReceiptMap.getQcReference()+", "+
                         InventoryReceiptMap.getExternalReference()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getStreetAddress()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getMultipleUnit()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getCity()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getState()+", "+
                         InventoryReceiptMap.ORGANIZATION_META.ADDRESS.getZipCode()+", "+
                         InventoryReceiptMap.INVENTORY_ITEM_META.getDescription()+", "+
                         InventoryReceiptMap.DICTIONARY_STORE_META.getEntry()+", "+
                         InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getEntry()+", "+
                         InventoryReceiptMap.ORDER_ITEM_META.getQuantityRequested()+", "+
                         InventoryReceiptMap.INVENTORY_ITEM_META.getIsBulk()+", "+
                         InventoryReceiptMap.INVENTORY_ITEM_META.getIsLotMaintained()+", "+
                         InventoryReceiptMap.INVENTORY_ITEM_META.getIsSerialMaintained()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getName()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.STORAGE_UNIT_META.getDescription()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber()+", "+
                         InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate()+", " + 
                         InventoryReceiptMap.TRANS_RECEIPT_ORDER_META.getId()+" ) ");
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        qb.addWhere(InventoryReceiptMap.getInventoryItemId() + " = " + InventoryReceiptMap.INVENTORY_ITEM_META.getId());
        qb.addWhere(InventoryReceiptMap.getOrganizationId() + " = " + InventoryReceiptMap.ORGANIZATION_META.getId());
        qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
        qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
        
        qb.setOrderBy(InventoryReceiptMap.getReceivedDate()+", "+InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId());
        
        sb.append(qb.getEJBQL());

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
            if(!receiptDO.getDelete() && receiptDO.getOrderItemId() != null && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                TransReceiptOrder transReceiptOrder = null;
                
                if (receiptDO.getTransReceiptOrderId() == null)
                    transReceiptOrder = new TransReceiptOrder();
                else
                    transReceiptOrder = manager.find(TransReceiptOrder.class, receiptDO.getTransReceiptOrderId());
                
                transReceiptOrder.setInventoryReceiptId(receipt.getId());
                transReceiptOrder.setOrderItemId(receiptDO.getOrderItemId());
                transReceiptOrder.setQuantity(receiptDO.getQuantityReceived());
                
                manager.persist(transReceiptOrder);
            }
            
            System.out.println("2");
            int numberOfLocs=1;
            List locTransLocIds = null;

            //we need to get the loc trans and the loc ids
            if(receiptDO.getId() != null){
                System.out.println("2a");
                Query query = manager.createNamedQuery("TransReceiptLocation.TransIdsLocIdsByReceiptId");
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
                    TransReceiptLocation transReceiptLocation =  null;
                    
                    if (receiptDO.getId() == null || (receiptDO.getId() != null && j>=locTransLocIds.size()))
                        transReceiptLocation = new TransReceiptLocation();
                    else
                        transReceiptLocation = manager.find(TransReceiptLocation.class, (Integer)((Object[])locTransLocIds.get(j))[0]);
                    
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
                        query.setParameter("systemName","order_status_completed");
                        completedStatusValue = (Integer)query.getResultList().get(0);
                    }
                    
                    Order order = manager.find(Order.class, orderIds.get(j));
                    order.setStatusId(completedStatusValue);
                }
            }
        }
        
        unlockRecords(inventoryReceipts);
    }

    public List autoCompleteLocationLookupByName(String name, int maxResults){
        Query query = null;
        query = manager.createNamedQuery("InventoryLocation.AutoCompleteByName");
        query.setParameter("name",name);
        query.setParameter("loc",name);
        query.setParameter("desc",name);
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
        //date received required
        if(receiptDO.getReceivedDate() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getReceivedDate()));
        }
        
        //inventory item required
        if(receiptDO.getInventoryItemId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getInventoryItemId()));
        }
        
        //org required
        if(receiptDO.getOrganizationId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getOrganizationId()));
        }
        
        //qty required
        if((receiptDO.getQuantityReceived() == null || "".equals(receiptDO.getQuantityReceived())) && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null) || 
                        receiptDO.getUnitCost() != null)){
            
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }

        
        //location required 
        if(receiptDO.getStorageLocationId() == null && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null) || 
                          receiptDO.getQuantityReceived() != null || receiptDO.getUnitCost() != null)){
            exceptionList.add(new TableFieldErrorException("locationRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_ITEM_META.ORDER_META.getId()));
        }
        
        //lot num required when checked on inventory item record 
        if("Y".equals(receiptDO.getIsLotMaintained()) && receiptDO.getLotNumber() == null && 
                        ((receiptDO.getOrderNumber() != null && receiptDO.getOrderNumber().equals(-1)) ||
                         (receiptDO.getOrderNumber() == null) || 
                          receiptDO.getQuantityReceived() != null || receiptDO.getUnitCost() != null)){
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