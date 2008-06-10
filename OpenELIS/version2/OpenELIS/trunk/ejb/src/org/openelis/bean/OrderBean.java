package org.openelis.bean;

import java.util.Date;
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
import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.entity.InventoryTransaction;
import org.openelis.entity.Note;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.OrderItemInventoryItemMeta;
import org.openelis.meta.OrderItemMeta;
import org.openelis.meta.OrderItemStoreMeta;
import org.openelis.meta.OrderMeta;
import org.openelis.remote.OrderRemote;
import org.openelis.util.Datetime;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("order-select")
public class OrderBean implements OrderRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            
        }
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
    public OrderDO getOrderAndLock(Integer orderId, String orderType) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        lockBean.getLock((Integer)query.getSingleResult(),orderId);
        
        return getOrder(orderId, orderType);
    }

    public OrderDO getOrderAndUnlock(Integer orderId , String orderType) {
        //unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "order");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),orderId);
            
        return getOrder(orderId, orderType);
    }

    public List getOrderItems(Integer orderId, boolean withLocation) {
        Query query;
        if(withLocation)
            query = manager.createNamedQuery("OrderItem.OrderItemsWithLocByOrderId");
        else
            query = manager.createNamedQuery("OrderItem.OrderItemsByOrderId");
        
        query.setParameter("id", orderId);
        
        List orderItems = query.getResultList();// getting list of order Items
    
        return orderItems;
    }

    public List getOrderReceipts(Integer orderId) {
        // TODO Auto-generated method stub
        return null;
    }

    public NoteDO getOrderShippingNote(Integer orderId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order_shipping_note");
        Integer orderShipNoteReferenceId = (Integer)query.getSingleResult();
        
        query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable",orderShipNoteReferenceId);
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteDO)results.get(0);
        else 
            return null;
    }

    public NoteDO getCustomerNote(Integer orderId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order_customer_note");
        Integer orderCustNoteReferenceId = (Integer)query.getSingleResult();
        
        query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable",orderCustNoteReferenceId);
        query.setParameter("id", orderId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteDO)results.get(0);
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

    public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List query(HashMap fields, int first, int max, String orderType) throws Exception {       
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        OrderMeta orderMeta = OrderMeta.getInstance();
        OrderItemMeta orderItemMeta = OrderItemMeta.getInstance();
        OrderItemInventoryItemMeta invItemMeta = OrderItemInventoryItemMeta.getInstance();
        OrderItemStoreMeta storeMeta = OrderItemStoreMeta.getInstance();

        qb.addMeta(new Meta[]{orderMeta, orderItemMeta, invItemMeta, storeMeta});
 
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+orderMeta.ID+") ");
        qb.addTable(orderMeta);
        qb.addTable(orderItemMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(orderMeta.ID);
        
        if(orderType.equals(OrderRemote.EXTERNAL)){
            qb.addWhere(orderMeta.IS_EXTERNAL + " = 'Y'");
            qb.addWhere(orderMeta.ORGANIZATION + " is not null");
            
        }else if(orderType.equals(OrderRemote.INTERNAL)){
            qb.addWhere(orderMeta.IS_EXTERNAL + " = 'N'");
            qb.addWhere(orderMeta.ORGANIZATION + " is null");
            
        }else if(orderType.equals(OrderRemote.KITS)){
            qb.addWhere(orderMeta.IS_EXTERNAL + " = 'N'");
            qb.addWhere(orderMeta.ORGANIZATION + " is not null");
            
        }
        
        if(qb.hasTable(storeMeta.getTable())){
            qb.addTable(invItemMeta);
            String fromClause = qb.getFromClause();
            fromClause+=", "+storeMeta.getEntity()+" "+storeMeta.getTable()+" ";
            String whereClause = qb.getWhereClause();
            whereClause+=" and("+OrderItemInventoryItemMeta.STORE+" = "+OrderItemStoreMeta.ID+") ";
            
            sb.append(qb.getSelectClause()).append(fromClause).append(whereClause).append(qb.getOrderBy());
        }else{
            sb.append(qb.getEJBQL());
        }
       
        Query query = manager.createQuery(sb.toString());

         if(first > -1 && max > -1)
             query.setMaxResults(first+max);

//       ***set the parameters in the query
         qb.setQueryParams(query);

         List returnList = GetPage.getPage(query.getResultList(), first, max);
         
         if(returnList == null)
             throw new LastPageException();
         else
             return returnList;
    }

    @RolesAllowed("order-update")
    public Integer updateOrder(OrderDO orderDO, String orderType, List items, NoteDO customerNoteDO, NoteDO orderShippingNotes) throws Exception {
        //order reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        Integer orderReferenceId = (Integer)query.getSingleResult();
        
        query.setParameter("name", "order_customer_note");
        Integer orderCustNoteReferenceId = (Integer)query.getSingleResult();
        
        query.setParameter("name", "order_shipping_note");
        Integer orderShipNoteReferenceId = (Integer)query.getSingleResult();
        
        if(orderDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(orderReferenceId,orderDO.getId());
        }
        
         manager.setFlushMode(FlushModeType.COMMIT);
         Order order = null;
    
         if (orderDO.getId() == null)
            order = new Order();
        else
            order = manager.find(Order.class, orderDO.getId());

        //validate the order record
        //List exceptionList = new ArrayList();
        //validateOrganizationAndAddress(organizationDO, exceptionList);
        //if(exceptionList.size() > 0){
        //    throw (RPCException)exceptionList.get(0);
        //}
        
        //update order record
         order.setBillToId(orderDO.getBillToId());
         order.setCostCenter(orderDO.getCostCenter());
         order.setExternalOrderNumber(orderDO.getExternalOrderNumber());
         order.setIsExternal(orderDO.getIsExternal());
         order.setNeededInDays(orderDO.getNeededInDays());
         order.setOrderedDate(orderDO.getOrderedDate());
         order.setOrganizationId(orderDO.getOrganizationId());
         order.setReportToId(orderDO.getReportToId());
         order.setRequestedBy(orderDO.getRequestedBy());
         order.setStatus(orderDO.getStatus());

        if (order.getId() == null) {
            manager.persist(order);
        }
 
        //lookup the order transaction type to be used later
        query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", "inv_trans_order");
        Integer orderTypeId = (Integer)query.getSingleResult();
        
        //update order items
        for (int i=0; i<items.size();i++) {
            OrderItemDO orderItemDO = (OrderItemDO) items.get(i);
            OrderItem orderItem = null;
            
            //validate the order item record
            //exceptionList = new ArrayList();
            //validateContactAndAddress(contactDO, i, exceptionList);
            //if(exceptionList.size() > 0){
            //    throw (RPCException)exceptionList.get(0);
            //}
            
            if (orderItemDO.getId() == null)
                orderItem = new OrderItem();
            else
                orderItem = manager.find(OrderItem.class, orderItemDO.getId());

            if(orderItemDO.getDelete() && orderItemDO.getId() != null){
                //delete the order item record from the database
                manager.remove(orderItem);
                
            }else{   
                orderItem.setInventoryItemId(orderItemDO.getInventoryItemId());
                orderItem.setOrder(order.getId());
                orderItem.setQuantityRequested(orderItemDO.getQuantityRequested());
                    
                if (orderItem.getId() == null) {
                    manager.persist(orderItem);
                }
           }
            
           //we may need to add transaction records for this order item
           if(orderItemDO.getLocationId() != null){
               InventoryTransaction trans = null;
               if (orderItemDO.getTransactionId() == null)
                   trans = new InventoryTransaction();
               else
                   trans = manager.find(InventoryTransaction.class, orderItemDO.getTransactionId());
               
               trans.setFromLocationId(orderItemDO.getLocationId());
               trans.setToOrderId(orderItem.getId());
               trans.setType(orderTypeId);
               trans.setQuantity(orderItem.getQuantityRequested());
               
               if (trans.getId() == null) {
                   manager.persist(trans);
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
            custNote.setReferenceTable(orderCustNoteReferenceId);
            custNote.setText(customerNoteDO.getText());
            if(custNote.getId() == null){
                systemUserId = getSystemUserId();
                custNote.setSystemUser(systemUserId);
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
            shippingNote.setReferenceTable(orderShipNoteReferenceId);
            shippingNote.setText(orderShippingNotes.getText());
            if(shippingNote.getId() == null){
                if(systemUserId == null)
                    systemUserId = getSystemUserId();
                shippingNote.setSystemUser(systemUserId);
            }
            shippingNote.setTimestamp(Datetime.getInstance());
            
            if (shippingNote.getId() == null) {
                manager.persist(shippingNote);
            }
        }

        lockBean.giveUpLock(orderReferenceId, order.getId()); 
   
        return order.getId();        
    }

    public List validateForAdd(OrderDO orderDO, String orderType, List items) {
        // TODO Auto-generated method stub
        return null;
    }

    public List validateForUpdate(OrderDO orderDO, String orderType, List items) {
        // TODO Auto-generated method stub
        return null;
    }

    public OrderAddAutoFillDO getAddAutoFillValues() throws Exception{
        OrderAddAutoFillDO autoFillDO = new OrderAddAutoFillDO();
        //status integer
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", "order_status_pending");
        autoFillDO.setStatus((Integer)query.getSingleResult());
        //order date
        autoFillDO.setOrderedDate(new Date());
        //requested by string
        SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        autoFillDO.setRequestedBy(systemUserDO.getLoginName());
        
        return autoFillDO;
    }

}
