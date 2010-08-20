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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.FillOrderDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryXUse;
import org.openelis.entity.Order;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.LockLocal;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.FillOrderRemote;
import org.openelis.security.domain.SystemUserDO;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("fillorder-select")
public class FillOrderBean implements FillOrderRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static int orderRefTableId, orderShippingNoteRefTableId, orderItemRefTableId;
//    private static final OrderMetaMap OrderMap = new OrderMetaMap();
    
    public FillOrderBean(){
        orderRefTableId = ReferenceTable.ORDER;
        orderItemRefTableId = ReferenceTable.ORDER_ITEM;
        orderShippingNoteRefTableId = ReferenceTable.ORDER_SHIPPING_NOTE;
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
/*
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
/*
        qb.setMeta(OrderMap);

        qb.setSelect("distinct new org.openelis.domain.FillOrderDO(" +
                     OrderMap.getId()+", " +
                     OrderMap.getStatusId()+", " +
                     OrderMap.getOrderedDate()+", " +
                     OrderMap.getShipFromId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getName()+", " +
                     OrderMap.getDescription()+", " +
                     OrderMap.getNeededInDays()+") ");

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        qb.addWhere(OrderMap.ORDER_ITEM_META.getOrderId() + " = " + OrderMap.getId());
        //qb.addWhere(OrderMap.ORDER_ORGANIZATION_META.getId() + " = " + OrderMap.getOrganizationId());
        qb.addWhere(OrderMap.getIsExternal()+"='N'");
        
        qb.setOrderBy(OrderMap.ORDER_ORGANIZATION_META.getName()+" DESC, "+OrderMap.getId());

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
*/    
    public List getOrderItems(Integer orderId) {
        Query query = manager.createNamedQuery("OrderItem.OrderItemsByOrderId");
        query.setParameter("id", orderId);
        
        return query.getResultList();
    }
    
    @RolesAllowed("fillorder-update")
    public List getOrderAndLock(Integer orderId) throws Exception {
        lockBean.getLock(orderRefTableId, orderId);

        List<Integer> orderIds = new ArrayList<Integer>();
        orderIds.add(orderId);
        
        return getOrdersById(orderIds);
    }
    
    public List getOrdersById(List orderIds){
        String queryString ="SELECT distinct new org.openelis.domain.FillOrderDO(ordr.id, ordr.statusId, ordr.orderedDate, ordr.shipFromId, organization.id, " +
                            " organization.name, ordr.description, ordr.neededInDays) FROM Order ordr, IN (ordr.orderItem) order_item LEFT JOIN ordr.organization organization LEFT JOIN " +
                            " organization.address organizationAddress WHERE order_item.orderId = ordr.id  and ordr.isExternal='N'  and ordr.id in ";
        
        queryString += buildIdParamFromList(orderIds);
        queryString += " ORDER BY organization.name DESC, ordr.id ";
        Query query = manager.createQuery(queryString);
        
        return query.getResultList();
    }
    
    private String buildIdParamFromList(List orderIds){
        String param = " (";
        for(int i=0; i<orderIds.size(); i++){
            param+=orderIds.get(i);
            
            if(i<(orderIds.size()-1))
                param+=", ";
        }
        
        param += ") ";
        
        return param;
    }
    
    public void unlockOrders(List orderIds) throws Exception {
        for(int i=0; i<orderIds.size(); i++)
            lockBean.giveUpLock(orderRefTableId, (Integer)orderIds.get(i));
    }
    
    public void setOrderToProcessed(List orders) throws Exception{
        validateOrders(orders);
        
        ArrayList<Integer> orderIds = new ArrayList<Integer>();
        //shipping reference table id
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name","order_status_processed");
        DictionaryDO dictDO = (DictionaryDO)query.getResultList().get(0); 
        Integer processedStatusValue = dictDO.getId();
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        for(int i=0; i<orders.size(); i++){
            FillOrderDO fillOrderDO = (FillOrderDO)orders.get(i);
            //insert inventory_x_use record
            InventoryXUse trans = new InventoryXUse();
            
            trans.setInventoryLocationId(fillOrderDO.getInventoryLocationId());
            trans.setOrderItemId(fillOrderDO.getOrderItemId());
            trans.setQuantity(fillOrderDO.getQuantity());
            
            if(trans.getId() == null)
                manager.persist(trans);
        
            //update the qty_on_hand field in the inventory location
            InventoryLocation loc =  manager.find(InventoryLocation.class, fillOrderDO.getInventoryLocationId());
            loc.setQuantityOnhand(loc.getQuantityOnhand() - fillOrderDO.getQuantity());

            //put the order id in the array list if necessary
            if(!orderIds.contains(fillOrderDO.getOrderId()))
                orderIds.add(fillOrderDO.getOrderId());
        }
        
        for(int j=0; j<orderIds.size(); j++){
            //update the order and set it to processed for each order effected
            Order order = manager.find(Order.class, orderIds.get(j));
            
            order.setStatusId(processedStatusValue);
            
            //unlock the orders as they are set to processed
            lockBean.giveUpLock(orderRefTableId, orderIds.get(j));
        }
    }
/*    
    private void unlockRecords(TableDataModel<TableDataRow<Integer>> orders) throws Exception{
        if(orders.size() == 0)
            return;
        
        Integer orderTableId = null;
        
        for(int i=0; i<orders.size(); i++){
            Integer orderId = orders.get(i).key;
        
            if(orderId != null)
                lockBean.giveUpLock(orderRefTableId, orderId);
        }
    }
*/    
    public Integer getOrderItemReferenceTableId() {
        return orderItemRefTableId;
    }

    public FillOrderDO getOrderItemInfoAndOrderNote(Integer orderId) {
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", orderShippingNoteRefTableId);
        query.setParameter("id", orderId);
        
        List noteResults = query.getResultList();
        String orderNote = null;
        if(noteResults.size() > 0)
            orderNote = ((NoteViewDO)noteResults.get(0)).getText();
        
        query = manager.createNamedQuery("Order.FillOrderSubInfo");
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0){
            FillOrderDO fillOrderDO = (FillOrderDO)results.get(0);
            fillOrderDO.setOrderNote(orderNote);
            return fillOrderDO;
            
        }else 
            return null;

    }
    
    public void validateOrders(List orders) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
        boolean addedLocException = false;
        boolean addedQtyException = false;
        boolean addedValidQtyException = false;

        if(orders.size() == 0){}
            //throw error
            
        for(int i=0; i<orders.size(); i++){
            FillOrderDO fillOrderDO = (FillOrderDO)orders.get(i);
            //inv loc required
            if(fillOrderDO.getInventoryLocationId() == null && !addedLocException){
                list.add(new FormErrorException("missingLocException"));    
                addedLocException = true;
            }
            
            //quantity required
            if(fillOrderDO.getQuantity() == null && !addedQtyException){
                list.add(new FormErrorException("missingQuantityException"));
                addedQtyException = true;
            }
            
            //valid quantity
            if(fillOrderDO.getQuantity() != null && fillOrderDO.getQuantity().intValue() < 0 && !addedValidQtyException){
                list.add(new FormErrorException("invalidQuantityException"));
                addedValidQtyException = true;
            }
        }
        
        if(list.size() > 0)
            throw list;
    }
    
}
