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
import org.openelis.domain.BuildKitDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryReceiptOrderItem;
import org.openelis.entity.InventoryXPut;
import org.openelis.entity.InventoryXUse;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.security.domain.SystemUserDO;
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
    private static int orderRefTableId, invLocRefTableId;
    private static final InventoryReceiptMetaMap InventoryReceiptMap = new InventoryReceiptMetaMap();
    
    public InventoryReceiptBean(){
        invLocRefTableId = ReferenceTable.INVENTORY_LOCATION;
        orderRefTableId = ReferenceTable.ORDER;
    }
    
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
    
    public List getInventoryReceiptRecordsAndLock(Integer orderId) throws Exception{
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
        
        //if we have records we need to lock the order
        if(inventoryReceiptList.size() > 0)
            lockBean.getLock(orderRefTableId, orderId);
       
        return inventoryReceiptList;
    }

    @RolesAllowed("receipt-update")
    public List queryAndLock(ArrayList<AbstractField> fields, int first, int max, boolean receipt) throws Exception {
        
        List queryResultList = query(fields, first, max, receipt);
        
        //try and lock the necessary records
        lockRecords(queryResultList, false);
                
        return queryResultList;
    }
    
    public InventoryLocationDO lockLocationAndFetch(Integer oldLocId, Integer newLocId) throws Exception {
        lockBean.getLock(invLocRefTableId, newLocId);

        //if there is an old lock we need to unlock this record
        if(oldLocId != null)
            lockBean.giveUpLock(invLocRefTableId, oldLocId);

        //get the current qty on hand
        Query query = manager.createNamedQuery("InventoryLocation.InventoryLocation");
        query.setParameter("id", newLocId);
        
        List resultList = query.getResultList();
        
        if(resultList != null && resultList.size() == 1)
            return (InventoryLocationDO)resultList.get(0);
        else
            return null;
    }
    
    public List queryAndUnlock(ArrayList<AbstractField> fields, int first, int max, boolean receipt) throws Exception {
        List queryResultList = query(fields, first, max, receipt);
        
        //try and unlock the necessary records
        unlockRecords(queryResultList);
                
        return queryResultList;
    }
    
    public void unlockLocations(TableDataModel<TableDataRow<Integer>> locIds) {
        for(int i=0; i<locIds.size(); i++)
            lockBean.giveUpLock(invLocRefTableId, locIds.get(i).key);
    }
    
    public void unlockOrders(TableDataModel<TableDataRow<Integer>> orderIds) {
        for(int i=0; i<orderIds.size(); i++)
            lockBean.giveUpLock(orderRefTableId, orderIds.get(i).key);
    }
    
    public List query(ArrayList<AbstractField> fields, int first, int max, boolean receipt) throws Exception {
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
                      InventoryReceiptMap.DICTIONARY_FROM_STORE_META.getEntry()+", "+//from store id
                      InventoryReceiptMap.DICTIONARY_FROM_DISPENSED_UNITS_META.getEntry()+", "+//from dis units
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.getStorageLocationId()+", "+ //from storage loc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getName()+", "+ //from loc name 
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.STORAGE_UNIT_META.getDescription()+", "+ //from loc desc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation()+", "+ //from loc
                      InventoryReceiptMap.TRANS_LOC_ORDER_META.INVENTORY_LOCATION_META.getQuantityOnhand()+")"; //from qty on hand
                  }else
                      selectString+=")";
                 

        qb.setSelect(selectString);
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        qb.addWhere(InventoryReceiptMap.getInventoryItemId() + " = " + InventoryReceiptMap.INVENTORY_ITEM_META.getId());
        
        if(receipt){
            qb.addWhere(InventoryReceiptMap.getOrganizationId() + " = " + InventoryReceiptMap.ORGANIZATION_META.getId());
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
            qb.addWhere(InventoryReceiptMap.getOrganizationId()+" is not null ");
        }else{
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
            qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
            
            qb.addWhere(InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_FROM_STORE_META.getId());
            qb.addWhere(InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_FROM_DISPENSED_UNITS_META.getId());
            
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
            fromClause+=", InventoryXUse locOrderTrans, Dictionary dictFromStore, Dictionary dictFromDis ";
        
        sb.append(qb.getSelectClause()).append(fromClause).append(qb.getWhereClause()).append(qb.getOrderBy());
        
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

        //validate the data before we start the transaction
        validateReceipts(inventoryReceipts);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //lock the necessary records
        lockRecords(inventoryReceipts, true);

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

            if(!receiptDO.getDelete() && receiptDO.getQuantityReceived() != null && receiptDO.getQuantityReceived() > 0){
                receipt.setExternalReference(receiptDO.getExternalReference());
                receipt.setInventoryItemId(receiptDO.getInventoryItemId());
                //TODO may not need this anymore receipt.setOrderItemId(receiptDO.getOrderItemId());
                receipt.setOrganizationId(receiptDO.getOrganizationId());
                receipt.setQcReference(receiptDO.getQcReference());
                receipt.setQuantityReceived(receiptDO.getQuantityReceived());
                receipt.setReceivedDate(receiptDO.getReceivedDate());
                receipt.setUnitCost(receiptDO.getUnitCost());
                receipt.setUpc(receiptDO.getUpc());

                if (receipt.getId() == null) {
                    manager.persist(receipt);
                }
                
                if(receiptDO.getOrderItemId() != null){
                    InventoryReceiptOrderItem invReceiptOrderItem = null;
                    
                    if (receiptDO.getInventoryReceiptOrderItemId() == null)
                        invReceiptOrderItem = new InventoryReceiptOrderItem();
                    else
                        invReceiptOrderItem = manager.find(InventoryReceiptOrderItem.class, receiptDO.getInventoryReceiptOrderItemId());
                    
                    invReceiptOrderItem.setInventoryReceiptId(receipt.getId());
                    invReceiptOrderItem.setOrderItemId(receiptDO.getOrderItemId());
                    
                    if(invReceiptOrderItem.getId() == null)
                        manager.persist(invReceiptOrderItem);
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
            
            int numberOfLocs=1;
            List locTransLocIds = null;

            //we need to get the loc trans and the loc ids
            if(receiptDO.getId() != null){
                
                Query query = manager.createNamedQuery("InventoryXPut.TransIdsLocIdsByReceiptId");
                query.setParameter("id", receiptDO.getId());
                locTransLocIds = query.getResultList();
                numberOfLocs = locTransLocIds.size();
                
                //we need to make sure we have the highest amount of locations
                if("Y".equals(receiptDO.getIsSerialMaintained()) && newQuantityReceived > numberOfLocs)
                    numberOfLocs = newQuantityReceived;
                
            }else if(receiptDO.getQuantityReceived() != null && "Y".equals(receiptDO.getIsSerialMaintained())){
                
                numberOfLocs = receiptDO.getQuantityReceived();
            }
            
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
        DictionaryDO dictDO;
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
                        query = manager.createNamedQuery("Dictionary.FetchBySystemName");
                        query.setParameter("name","order_status_processed");
                        dictDO = (DictionaryDO)query.getResultList().get(0);
                        completedStatusValue = dictDO.getId();
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
        //validate the data before we start the transaction
        validateTransfers(inventoryTransfers);
        
       manager.setFlushMode(FlushModeType.COMMIT);
        
       lockTransfers(inventoryTransfers);
       
        if(inventoryTransfers.size() == 0)
            return;
        
        //create a new internal order record only on add
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name","order_status_processed");
        DictionaryDO dictDO = (DictionaryDO)query.getResultList().get(0);
        Integer completedStatusValue = dictDO.getId();
        
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
            
            Integer qtyInto = -1;
            if(transferDO.getParentRatio() != null)
                qtyInto = transferDO.getQuantityReceived() * transferDO.getParentRatio();
            else if(transferDO.getChildRatio() != null)
                qtyInto = transferDO.getQuantityReceived() / transferDO.getChildRatio();
            
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
            
            //subtract quantity from FROM inv loc record for each order item
            InventoryLocation location = null;
            location = manager.find(InventoryLocation.class, transferDO.getFromStorageLocationId());
            location.setQuantityOnhand(location.getQuantityOnhand()-transferDO.getQuantityReceived());

            //create new trans_location_order record with each transfer from inv_loc
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
            
            //create new inv receipt records
            InventoryReceipt receipt = null;
            
            if(transferDO.getId() != null)
                receipt = manager.find(InventoryReceipt.class, transferDO.getId());
            else
                receipt = new InventoryReceipt();
            
            receipt.setInventoryItemId(transferDO.getInventoryItemId());
            receipt.setOrderItemId(orderItem.getId());
            receipt.setQuantityReceived(qtyInto);
            if(receipt.getReceivedDate() == null)
                receipt.setReceivedDate(Datetime.getInstance(Datetime.YEAR, Datetime.DAY));

            if(receipt.getId() == null)
                manager.persist(receipt);
           
            //create inventory_receipt_order_item record
            InventoryReceiptOrderItem invReceiptOrderItem = null;
            
            if (transferDO.getInventoryReceiptOrderItemId() == null)
                invReceiptOrderItem = new InventoryReceiptOrderItem();
            else
                invReceiptOrderItem = manager.find(InventoryReceiptOrderItem.class, transferDO.getInventoryReceiptOrderItemId());
            
            invReceiptOrderItem.setInventoryReceiptId(receipt.getId());
            invReceiptOrderItem.setOrderItemId(orderItem.getId());
            
            if(invReceiptOrderItem.getId() == null)
                manager.persist(invReceiptOrderItem);
            
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
            
            if(location.getExpirationDate() != null)
                toLoc.setExpirationDate(location.getExpirationDate());
            toLoc.setInventoryItemId(transferDO.getInventoryItemId());
            toLoc.setLotNumber(location.getLotNumber());
            toLoc.setQuantityOnhand(qtyInto);
            toLoc.setStorageLocationId(transferDO.getStorageLocationId());
            
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
            transReceiptLoc.setQuantity(qtyInto);
            
            if(transReceiptLoc.getId() == null)
                manager.persist(transReceiptLoc);
        }
                
        unlockTransfers(inventoryTransfers);
    }
    
    @RolesAllowed("receipt-update")
    public void updateBuildKits(BuildKitDO kitDO, List kitComponents) throws Exception {
        
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
    
    public void validateReceipts(List<InventoryReceiptDO> inventoryReceipts) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();

        for(int i=0; i<inventoryReceipts.size(); i++)
           validateReceiptAndLocation(inventoryReceipts.get(i), i, list);
        
        if(list.size() > 0)
            throw list;
    }
    
    public void validateTransfers(List<InventoryReceiptDO> inventoryTransfers) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();

        for(int i=0; i<inventoryTransfers.size(); i++)
            validateTransferAndLocation(inventoryTransfers.get(i), i, list);
        
        if(list.size() > 0)
            throw list;
    }
    
    private void validateTransferAndLocation(InventoryReceiptDO transferDO, int rowIndex, ValidationErrorsList exceptionList){
        //from item required
        if(transferDO.getFromInventoryItemId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.TRANS_LOC_ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getName()));
        }
        
        //from loc required
        if(transferDO.getFromStorageLocationId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, "fromLoc"));
        }
        
        //on hand required
        if(transferDO.getQuantityReceived() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, "qtyOnHand"));
        }
        
        //to item required
        if(transferDO.getInventoryItemId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.INVENTORY_ITEM_META.getName()));
        }
        
        //make sure check is only for bulk
        if("N".equals(transferDO.getIsBulk()) && transferDO.isAddToExisting()){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, "addToExisting"));
        }
        
        //to loc required
        if(transferDO.getStorageLocationId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId()));
        }
        
        //qty required
        if(transferDO.getQuantityReceived() == null || new Integer(0).compareTo(transferDO.getQuantityReceived()) >= 0){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }
        
        //validate qtys    
        if(transferDO.getQuantityReceived().compareTo(transferDO.getFromQtyOnHand()) > 0){
            exceptionList.add(new TableFieldErrorException("notEnoughQuantityOnHand", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }
        
        if(transferDO.getChildRatio() != null && transferDO.getParentRatio() == null && transferDO.getQuantityReceived()%transferDO.getChildRatio() > 0)
            exceptionList.add(new TableFieldErrorException("qtyToParentRatioInvalid", rowIndex, InventoryReceiptMap.getQuantityReceived()));
            
    }
    
    private void validateReceiptAndLocation(InventoryReceiptDO receiptDO, int rowIndex, ValidationErrorsList exceptionList){
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
                         (receiptDO.getOrderNumber() == null))){
            
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }

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
    
    private void lockRecords(List receipts, boolean validate) throws Exception{
        if(receipts.size() == 0)
            return;
        
        List orderIds = new ArrayList();
        
        for(int i=0; i<receipts.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)receipts.get(i);
        
            //put the order id in the list if it is there
            if(receiptDO.getOrderNumber() != null && !orderIds.contains(receiptDO.getOrderNumber()))
                orderIds.add(receiptDO.getOrderNumber());
                
            //get a list of all the locations
            Query query = manager.createNamedQuery("InventoryReceipt.LocationIdsByReceiptId");
            query.setParameter("id", receiptDO.getId());
            List locationIds = query.getResultList();
                
            //lock all the location records
            for(int j=0; j < locationIds.size(); j++){
                if(validate)
                    lockBean.validateLock(invLocRefTableId, (Integer)locationIds.get(j));
                else
                    lockBean.getLock(invLocRefTableId, (Integer)locationIds.get(j));
            }
        }
        
        //we need to lock the orders
        for(int j=0; j<orderIds.size(); j++){
            if(validate)
                lockBean.validateLock(orderRefTableId, (Integer)orderIds.get(j));
            else
                lockBean.getLock(orderRefTableId, (Integer)orderIds.get(j));
        }
    }
    
    private void unlockRecords(List receipts) throws Exception{
        if(receipts.size() == 0)
            return;
        
        List orderIds = new ArrayList();
        
        for(int i=0; i<receipts.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)receipts.get(i);
            
            //put the order id in the list if it is there
            if(receiptDO.getOrderNumber() != null && !orderIds.contains(receiptDO.getOrderNumber()))
                orderIds.add(receiptDO.getOrderNumber());
                
            //get a list of all the locations
            Query query = manager.createNamedQuery("InventoryReceipt.LocationIdsByReceiptId");
            query.setParameter("id", receiptDO.getId());
            List locationIds = query.getResultList();
                
            //lock all the location records
            for(int j=0; j < locationIds.size(); j++)
                lockBean.giveUpLock(invLocRefTableId, (Integer)locationIds.get(j));
        }
        
        //we need to unlock the orders
        for(int j=0; j<orderIds.size(); j++)
            lockBean.giveUpLock(orderRefTableId, (Integer)orderIds.get(j));
    }
    
    private void lockTransfers(List transfers) throws Exception{
        if(transfers.size() == 0)
            return;
        
        for(int i=0; i<transfers.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)transfers.get(i);
            lockBean.validateLock(invLocRefTableId, receiptDO.getFromStorageLocationId());
        }
    }
    
    private void unlockTransfers(List transfers) throws Exception{
        if(transfers.size() == 0)
            return;
        
        for(int i=0; i<transfers.size(); i++){
        
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)transfers.get(i);
          
            lockBean.giveUpLock(invLocRefTableId, receiptDO.getFromStorageLocationId());
        }
    }
}
