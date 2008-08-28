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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryReceiptDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.FillOrderRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
//@RolesAllowed("fillOrder-select")
public class FillOrderBean implements FillOrderRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final OrderMetaMap OrderMap = new OrderMetaMap();
    
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

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(OrderMap);
        
        /*qb.setSelect("distinct new org.openelis.domain.InventoryReceiptDO("+
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
                         InventoryReceiptMap.TRANS_RECEIPT_ORDER_META.getId()+" ) ");*/
        qb.setSelect("distinct new org.openelis.domain.OrderDO(" +
                     OrderMap.getId()+", "+
                     OrderMap.getOrderedDate()+") ");
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        /*qb.addWhere(InventoryReceiptMap.getInventoryItemId() + " = " + InventoryReceiptMap.INVENTORY_ITEM_META.getId());
        qb.addWhere(InventoryReceiptMap.getOrganizationId() + " = " + InventoryReceiptMap.ORGANIZATION_META.getId());
        qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getStoreId()+" = "+InventoryReceiptMap.DICTIONARY_STORE_META.getId());
        qb.addWhere(InventoryReceiptMap.INVENTORY_ITEM_META.getDispensedUnitsId()+" = "+InventoryReceiptMap.DICTIONARY_DISPENSED_UNITS_META.getId());
        */
        
        qb.setOrderBy(OrderMap.getId());
        
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

    public List validateForAdd(List orders) {
        // TODO Auto-generated method stub
        return null;
    }

    public List validateForUpdate(List orders) {
        // TODO Auto-generated method stub
        return null;
    }

    private void lockRecords(List orders) throws Exception{
        /*if(receipts.size() == 0)
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
            */
    }
    
    private void unlockRecords(List orders) throws Exception{
        /*if(receipts.size() == 0)
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
            */
    }
}
