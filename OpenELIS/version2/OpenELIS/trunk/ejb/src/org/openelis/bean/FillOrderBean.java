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
import java.util.Date;
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
import org.openelis.gwt.common.data.NumberField;
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
System.out.println("1");
        qb.setSelect("distinct new org.openelis.domain.FillOrderDO(" +
                     OrderMap.getId()+", " +
                     OrderMap.getStatusId()+", " +
                     OrderMap.getOrderedDate()+", " +
                     OrderMap.getShipFromId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getName()+", " +
                     OrderMap.getDescription()+", " +
                     OrderMap.getNeededInDays()+", "+
                     //"("+OrderMap.getNeededInDays()+"-(" +
                            //"current_date()-date("+OrderMap.getOrderedDate()+"), "+
                     OrderMap.getRequestedBy()+", "+
                     OrderMap.getCostCenterId()+" ,"+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getCity()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getState()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getZipCode()+") ");
        System.out.println("2");
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 
System.out.println("3");
        qb.addWhere(OrderMap.ORDER_ITEM_META.getOrderId() + " = " + OrderMap.getId());
        qb.addWhere(OrderMap.ORDER_ORGANIZATION_META.getId() + " = " + OrderMap.getOrganizationId());
        qb.addWhere(OrderMap.getOrganizationId()+" is not null");
        qb.addWhere(OrderMap.getIsExternal()+"='N'");
        
        qb.setOrderBy(OrderMap.getId());
        System.out.println("4");
        sb.append(qb.getEJBQL());
System.out.println("5");
        Query query = manager.createQuery(sb.toString());
    System.out.println("6");
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
        //set the parameters in the query
        qb.setQueryParams(query);
        System.out.println("7");
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        System.out.println("8");
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
    
    public List getOrderItems(Integer orderId) {
        Query query = manager.createNamedQuery("OrderItem.OrderItemsWithLocByOrderId");
        query.setParameter("id", orderId);
        
        return query.getResultList();
    }

    public List validateForProcess(List orders) {
        List exceptionList = new ArrayList();
        //all ship froms need to match
        
        //all ship tos need to match
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

    public Integer getOrderItemReferenceTableId() {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order_item");
        
        return (Integer)query.getSingleResult();
    }
}
