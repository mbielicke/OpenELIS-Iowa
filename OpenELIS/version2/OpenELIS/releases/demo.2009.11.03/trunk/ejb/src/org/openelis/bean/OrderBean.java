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
import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.entity.Note;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.remote.OrderRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("order-select")
public class OrderBean implements OrderRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static int orderRefTable, orderShippingNoteRefTableId, orderCustNoteRefTableId;
    private static final OrderMetaMap OrderMetaMap = new OrderMetaMap();
    
    public OrderBean(){
        orderRefTable = ReferenceTableCache.getReferenceTable("order");
        orderShippingNoteRefTableId = ReferenceTableCache.getReferenceTable("order_shipping_note");
        orderCustNoteRefTableId = ReferenceTableCache.getReferenceTable("order_customer_note");
    }
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    public OrderDO getOrder(Integer orderId, String orderType) {
        Query query;
        
        if(orderType.equals(OrderRemote.INTERNAL))
            query = manager.createNamedQuery("Order.OrderInternal");
        else
            query = manager.createNamedQuery("Order.OrderExternalKit");
        
        query.setParameter("id", orderId);
        
        OrderDO orderDO = (OrderDO) query.getResultList().get(0);// getting order record

        return orderDO;
    }

    @RolesAllowed("order-update")
    public OrderDO getOrderAndLock(Integer orderId, String orderType, String session) throws Exception {
        lockBean.getLock(orderRefTable, orderId);
        
        return getOrder(orderId, orderType);
    }

    public OrderDO getOrderAndUnlock(Integer orderId , String orderType, String session) {
        //unlock the entity
        lockBean.giveUpLock(orderRefTable, orderId);
            
        return getOrder(orderId, orderType);
    }

    public List getOrderItems(Integer orderId) {
        Query query;
        //if(withLocation)
        //    query = manager.createNamedQuery("OrderItem.OrderItemsWithLocByOrderId");
        //else
            query = manager.createNamedQuery("OrderItem.OrderItemsByOrderId");
        
        query.setParameter("id", orderId);
        
        List orderItems = query.getResultList();// getting list of order Items
    
        return orderItems;
    }

    public List getOrderReceipts(Integer orderId) {
        Query query = manager.createNamedQuery("Order.ReceiptsForOrder");
        
        query.setParameter("id", orderId);
        
        List receipts = query.getResultList();// getting list of receipts
    
        return receipts;
    }
    
    public List getOrderLocTransactions(Integer orderId) {
        Query query = manager.createNamedQuery("Order.LocsForOrder");
        
        query.setParameter("id", orderId);
        
        List receipts = query.getResultList();// getting list of locs
    
        return receipts;
    }

    public NoteViewDO getOrderShippingNote(Integer orderId) {
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", orderShippingNoteRefTableId);
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteViewDO)results.get(0);
        else 
            return null;
    }

    public NoteViewDO getCustomerNote(Integer orderId) {
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", orderCustNoteRefTableId);
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteViewDO)results.get(0);
        else 
            return null;
    }
    
    public BillToReportToDO getBillToReportTo(Integer orderId){
        Query query = manager.createNamedQuery("Order.ReportToBillTo");
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (BillToReportToDO)results.get(0);
        else 
            return null;        
    }

    public List query(ArrayList<AbstractField> fields, int first, int max, String orderType) throws Exception {       
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(OrderMetaMap);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+OrderMetaMap.getId()+") ");

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(OrderMetaMap.getId());
        
        if(orderType.equals(OrderRemote.EXTERNAL)){
            qb.addWhere(OrderMetaMap.getIsExternal() + " = 'Y'");
            qb.addWhere(OrderMetaMap.getOrganizationId() + " is not null");
            
        }else if(orderType.equals(OrderRemote.INTERNAL)){
            qb.addWhere(OrderMetaMap.getIsExternal() + " = 'N'");
            qb.addWhere(OrderMetaMap.getOrganizationId() + " is null");
            
        }else if(orderType.equals(OrderRemote.KITS)){
            qb.addWhere(OrderMetaMap.getIsExternal() + " = 'N'");
            qb.addWhere(OrderMetaMap.getOrganizationId() + " is not null");
        }
        
        //need to see if Store is in the from clause
        String whereClause = qb.getWhereClause();
        if(whereClause.indexOf("store.") > -1){
            whereClause+=" and("+OrderMetaMap.getOrderItem().getInventoryItem().getStoreId()+" = "+OrderMetaMap.getStore().getId()+") ";
            
            //TODO taking this out for now...need to look into this
            //if(whereClause.indexOf("inventoryTrans.") > -1)
            //    whereClause += " and ("+OrderMetaMap.ORDER_INV_TRANS_META.getOrderItemId()+" = "+OrderMetaMap.ORDER_ITEM_META.getId()+") ";
            
            sb.append(qb.getSelectClause()).append(qb.getFromClause(whereClause)).append(whereClause).append(qb.getOrderBy());
        }else{
            sb.append(qb.getEJBQL());
        }
       
        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    public OrderAddAutoFillDO getAddAutoFillValues() throws Exception{
        OrderAddAutoFillDO autoFillDO = new OrderAddAutoFillDO();        
        //status integer
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", "order_status_pending");
        DictionaryDO dictDO = (DictionaryDO)query.getSingleResult();
        autoFillDO.setStatus(dictDO.getId());
        //order date
        autoFillDO.setOrderedDate(new Date());
        //requested by string
        //SystemUserDO systemUserDO = sysUser.getSystemUser();
        autoFillDO.setRequestedBy(ctx.getCallerPrincipal().getName());
        
        return autoFillDO;
    }

    @RolesAllowed("order-update")
    public Integer updateOrder(OrderDO orderDO, String orderType, List items, NoteViewDO customerNoteDO, NoteViewDO orderShippingNotes) throws Exception {
        if(orderDO.getId() != null)
            lockBean.validateLock(orderRefTable, orderDO.getId());
        
        validateOrder(orderDO, orderType, items);
        
         manager.setFlushMode(FlushModeType.COMMIT);
         Order order = null;
    
         if (orderDO.getId() == null)
            order = new Order();
        else
            order = manager.find(Order.class, orderDO.getId());

        //update order record
         order.setBillToId(orderDO.getBillToId());
         order.setCostCenterId(orderDO.getCostCenter());
         order.setExternalOrderNumber(orderDO.getExternalOrderNumber());
         order.setIsExternal(orderDO.getIsExternal());
         order.setNeededInDays(orderDO.getNeededInDays());
         order.setOrderedDate(orderDO.getOrderedDate());
         order.setOrganizationId(orderDO.getOrganizationId());
         order.setReportToId(orderDO.getReportToId());
         order.setRequestedBy(orderDO.getRequestedBy());
         order.setStatusId(orderDO.getStatusId());
         order.setShipFromId(orderDO.getShipFromId());
         order.setDescription(orderDO.getDescription());
         
        if (order.getId() == null) {
            manager.persist(order);
        }

        //update order items
        for (int i=0; i<items.size();i++) {
            OrderItemDO orderItemDO = (OrderItemDO) items.get(i);
            OrderItem orderItem = null;
       
            if (orderItemDO.getId() == null)
                orderItem = new OrderItem();
            else
                orderItem = manager.find(OrderItem.class, orderItemDO.getId());

            if(orderItemDO.getDelete() && orderItemDO.getId() != null){
                //delete the order item record from the database
                manager.remove(orderItem);
                
            }else{   
                orderItem.setInventoryItemId(orderItemDO.getInventoryItemId());
                orderItem.setOrderId(order.getId());
                orderItem.setQuantity(orderItemDO.getQuantity());
                orderItem.setCatalogNumber(orderItemDO.getCatalogNumber());
                orderItem.setUnitCost(orderItemDO.getUnitCost());
                    
                if (orderItem.getId() == null) {
                    manager.persist(orderItem);
                }
           }
        }
        
        Integer systemUserId = null;
        //update the customer note field
        if(customerNoteDO.getText() != null){
            Note custNote = null;
            
            if (customerNoteDO.getId() == null)
                custNote = new Note();
            else
               custNote = manager.find(Note.class, customerNoteDO.getId());
            
            custNote.setIsExternal(customerNoteDO.getIsExternal());
            custNote.setReferenceId(order.getId());
            custNote.setReferenceTableId(orderCustNoteRefTableId);
            custNote.setText(customerNoteDO.getText());
            if(custNote.getId() == null){
                systemUserId = lockBean.getSystemUserId();
                custNote.setSystemUserId(systemUserId);
            }
            custNote.setTimestamp(Datetime.getInstance());
            
            if (custNote.getId() == null) {
                manager.persist(custNote);
            }
        }
        
        //update the shipping note field
        if(orderShippingNotes.getText() != null){
            Note shippingNote = null;
            
            if (orderShippingNotes.getId() == null)
                shippingNote = new Note();
            else
                shippingNote = manager.find(Note.class, orderShippingNotes.getId());
            
            shippingNote.setIsExternal(orderShippingNotes.getIsExternal());
            shippingNote.setReferenceId(order.getId());
            shippingNote.setReferenceTableId(orderShippingNoteRefTableId);
            shippingNote.setText(orderShippingNotes.getText());
            if(shippingNote.getId() == null){
                if(systemUserId == null)
                    systemUserId = lockBean.getSystemUserId();
                shippingNote.setSystemUserId(systemUserId);
            }
            shippingNote.setTimestamp(Datetime.getInstance());
            
            if (shippingNote.getId() == null) {
                manager.persist(shippingNote);
            }
        }

        lockBean.giveUpLock(orderRefTable, order.getId()); 
   
        return order.getId();        
    }

    public List orderDescriptionAutoCompleteLookup(String desc, int maxResults) {
        Query query = manager.createNamedQuery("Order.descriptionAutoLookup");
        query.setParameter("desc", desc);
        
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    private void validateOrder(OrderDO orderDO, String orderType, List items) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
        
        //status required for all order types
        if(orderDO.getStatusId() == null || "".equals(orderDO.getStatusId())){
            list.add(new FieldErrorException("fieldRequiredException",OrderMetaMap.getStatusId()));
        }
        
        //needed in days required for all order types
        if(orderDO.getNeededInDays() == null || "".equals(orderDO.getNeededInDays())){
            list.add(new FieldErrorException("fieldRequiredException",OrderMetaMap.getNeededInDays()));
        }
        
        //organization required for vendor orders and kit orders
        if((orderDO.getOrganizationId() == null || "".equals(orderDO.getOrganizationId())) && (OrderRemote.EXTERNAL.equals(orderType) || OrderRemote.KITS.equals(orderType))){
            list.add(new FieldErrorException("fieldRequiredException",OrderMetaMap.getOrganizationId()));
        }        
        
        //ordered date required for all order types
        if(orderDO.getOrderedDate() == null || "".equals(orderDO.getOrderedDate())){
            list.add(new FieldErrorException("fieldRequiredException",OrderMetaMap.getOrderedDate()));
        }
        
        //requested by required for all order types
        if(orderDO.getRequestedBy() == null || "".equals(orderDO.getRequestedBy())){
            list.add(new FieldErrorException("fieldRequiredException",OrderMetaMap.getRequestedBy()));
        }        
        
        //TODO lazy load kind of messes this up if the table isnt loaded
        //number of order items needs to be > 0
        //if(validateOrderQty && items.size() < 1){
        //    list.add(new FormErrorException("zeroOrderItemsException"));
        //}
        
        for(int i=0; i<items.size();i++)           
            validateOrderItems((OrderItemDO)items.get(i), i, list);
        
        if(list.size() > 0)
            throw list;
    }
    
    private void validateOrderItems(OrderItemDO orderItemDO, int rowIndex, ValidationErrorsList exceptionList){
        if(orderItemDO.getDelete())
            return;
        
        //quantity is required for all order types
        if(orderItemDO.getQuantity() == null || "".equals(orderItemDO.getQuantity())){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrderMetaMap.ORDER_ITEM_META.getQuantity()));
        }
        
        //inventory item is required for all order types
        if(orderItemDO.getInventoryItemId() == null || "".equals(orderItemDO.getInventoryItemId())){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrderMetaMap.ORDER_ITEM_META.INVENTORY_ITEM_META.getName()));
        }
    }
}
