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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryReceipt;
import org.openelis.entity.InventoryTransaction;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.remote.InventoryReceiptRemote;
import org.openelis.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("receipt-select")
public class InventoryReceiptBean implements InventoryReceiptRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final InventoryReceiptMetaMap InventoryReceiptMap = new InventoryReceiptMetaMap();
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            
        }
    }
    
    public List getInventoryReceiptRecords(Integer orderId) {
        Query query = manager.createNamedQuery("InventoryReceipt.InventoryReceiptByOrderNum");
        
        query.setParameter("id", orderId);
        
        return query.getResultList();
    }

    @RolesAllowed("receipt-update")
    public List getInventoryReceiptRecordsAndLock(Integer orderId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_receipt");
        lockBean.getLock((Integer)query.getSingleResult(),orderId);
        
        return getInventoryReceiptRecords(orderId);
    }

    public List getInventoryReceiptRecordsAndUnlock(Integer orderId) {
//      unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "inventory_receipt");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),orderId);
            
        return getInventoryReceiptRecords(orderId);
    }

    public Integer getSystemUserId() {
        // TODO Auto-generated method stub
        return null;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @RolesAllowed("receipt-update")
    public void updateInventoryReceipt(List inventoryReceipts) throws Exception {

        manager.setFlushMode(FlushModeType.COMMIT);
        
        //not sure what needs to be locked
        /*if(organizationDO.getOrganizationId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(organizationReferenceId,organizationDO.getOrganizationId());
        }*/
        
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", "inv_trans_receipt");
        Integer orderTypeId = (Integer)query.getSingleResult();
        
        for (int i=0; i<inventoryReceipts.size();i++) {
            //
            //insert/update the receipt record
            //
            InventoryReceiptDO receiptDO = (InventoryReceiptDO)inventoryReceipts.get(i);     
         
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
            
            if(receiptDO.getDelete() && receipt.getId() != null){
                //delete the receipt record from the database
                manager.remove(receipt);
                
            }else{
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
            //insert/update the location record
            //
            InventoryLocation invLocation = null;
            
            if (!receiptDO.isAddToExisting())
                invLocation = new InventoryLocation();
            else
                invLocation = manager.find(InventoryLocation.class, receiptDO.getStorageLocationId());
            
            if(!receiptDO.getDelete()){
                invLocation.setExpirationDate(receiptDO.getExpDate());
                invLocation.setInventoryItemId(receiptDO.getInventoryItemId());
                invLocation.setLotNumber(receiptDO.getLotNumber());
                invLocation.setQuantityOnhand(receiptDO.getQuantityReceived());
                invLocation.setStorageLocationId(receiptDO.getStorageLocationId());
                    
                if (invLocation.getId() == null) {
                    manager.persist(invLocation);
                }
            }
            
            //
            //insert/update the transaction record
            //
            if(!receiptDO.getDelete()){
                InventoryTransaction orderItemTransaction = null;
                InventoryTransaction locationTransaction = null;
                
                //FIXME only insert for now...
                orderItemTransaction = new InventoryTransaction();
                locationTransaction = new InventoryTransaction();
                
                orderItemTransaction.setFromReceiptId(receipt.getId());
                orderItemTransaction.setToOrderId(receiptDO.getOrderItemId());
                orderItemTransaction.setTypeId(orderTypeId);
                orderItemTransaction.setQuantity(receiptDO.getQuantityReceived().doubleValue());
                
                locationTransaction.setFromReceiptId(receipt.getId());
                locationTransaction.setToLocationId(invLocation.getId());
                locationTransaction.setTypeId(orderTypeId);
                locationTransaction.setQuantity(receiptDO.getQuantityReceived().doubleValue());
                
                //insert the inventory transaction records
                manager.persist(orderItemTransaction);
                manager.persist(locationTransaction);
            }
        }
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
        if(receiptDO.getQuantityReceived() == null || "".equals(receiptDO.getQuantityReceived())){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getQuantityReceived()));
        }
        //cost required
        if(receiptDO.getUnitCost() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryReceiptMap.getUnitCost()));
        }
        
        //location required 
        if(receiptDO.getStorageLocationId() == null){
            exceptionList.add(new TableFieldErrorException("locationRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_META.getId()));
        }
        
        //add to existing required when bulk is checked on inventory item record
        //maybe..not how the first bulk item is added if this is true..leaving out for now
        /*if("Y".equals(receiptDO.getIsBulk()) && !receiptDO.isAddToExisting()){
            exceptionList.add(new TableFieldErrorException("lotNumRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_META.getId()));
        }*/
        
        //add to existing not allow when bulk is not checked on inventory item record
        if("N".equals(receiptDO.getIsBulk()) && receiptDO.isAddToExisting()){
            exceptionList.add(new TableFieldErrorException("cantAddToExistingException", rowIndex, InventoryReceiptMap.ORDER_META.getId()));
        }
        
        //lot num required when checked on inventory item record 
        if("Y".equals(receiptDO.getIsLotMaintained()) && receiptDO.getLotNumber() == null){
            exceptionList.add(new TableFieldErrorException("lotNumRequiredForRowException", rowIndex, InventoryReceiptMap.ORDER_META.getId()));
        }
    }
}
