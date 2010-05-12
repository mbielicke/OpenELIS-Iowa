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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.entity.Order;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.meta.OrderMeta;
import org.openelis.remote.OrderRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("order-select")
public class OrderBean implements OrderRemote, OrderLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    @EJB
    private  OrganizationLocal       organizationBean;

    private static final OrderMeta meta = new OrderMeta();
    
    public OrderViewDO fetchById(Integer id) throws Exception {
        Query query;
        OrderViewDO data;
        
        query = manager.createNamedQuery("Order.FetchById");
        query.setParameter("id", id);
        try {
            data = (OrderViewDO)query.getSingleResult();          
            setOrganizationReportToBillTo(data);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<IdNameVO> fetchByDescription(String match, int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("Order.FetchByDescription");
        query.setParameter("description", match);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + 
                          OrderMeta.getId() + ", " +
                          OrderMeta.getRequestedBy() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(OrderMeta.getId() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<OrderViewDO> queryOrderFill(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;
        OrderViewDO data;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.OrderViewDO(" +
                          OrderMeta.getId()+", " +
                          OrderMeta.getDescription()+", " +                          
                          OrderMeta.getStatusId()+", " +
                          OrderMeta.getOrderedDate()+", " +
                          OrderMeta.getNeededInDays()+", " +
                          OrderMeta.getRequestedBy()+", " +
                          OrderMeta.getCostCenterId()+", " +
                          OrderMeta.getOrganizationId()+", " +
                          OrderMeta.getOrganizationAttention()+", " +
                          OrderMeta.getType()+", " +
                          OrderMeta.getExternalOrderNumber()+", " +
                          OrderMeta.getReportToId()+", " +
                          OrderMeta.getReportToAttention()+", " +
                          OrderMeta.getBillToId()+", " +
                          OrderMeta.getBillToAttention()+", " +
                          OrderMeta.getShipFromId() + ") ");
        builder.constructWhere(fields);       
        builder.addWhere(OrderMeta.getType()+" <> 'V'");        
        
        builder.setOrderBy(OrderMeta.getId() + " DESC");       

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        //list = (ArrayList<OrderViewDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();        
        
        try {
            for (int i = 0; i < list.size(); i++ ) {
                data = (OrderViewDO)list.get(i);
                setOrganizationReportToBillTo(data);
            }
            return DataBaseUtil.toArrayList(list);
        } catch (NoResultException e) {            
            throw new NotFoundException();
        } catch (Exception e) {            
            throw new DatabaseException(e);
        }
    }    

    public OrderViewDO add(OrderViewDO data) throws Exception {
        Order entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Order();
        entity.setDescription(data.getDescription());
        entity.setStatusId(data.getStatusId());
        entity.setOrderedDate(data.getOrderedDate());
        entity.setNeededInDays(data.getNeededInDays());
        entity.setRequestedBy(data.getRequestedBy());
        entity.setCostCenterId(data.getCostCenterId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setType(data.getType());
        entity.setExternalOrderNumber(data.getExternalOrderNumber());
        entity.setReportToId(data.getReportToId());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setBillToId(data.getBillToId());
        entity.setBillToAttention(data.getBillToAttention());
        entity.setShipFromId(data.getShipFromId());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public OrderViewDO update(OrderViewDO data) throws Exception {
        Order entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Order.class, data.getId());
        entity.setDescription(data.getDescription());
        entity.setStatusId(data.getStatusId());
        entity.setOrderedDate(data.getOrderedDate());
        entity.setNeededInDays(data.getNeededInDays());
        entity.setRequestedBy(data.getRequestedBy());
        entity.setCostCenterId(data.getCostCenterId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setType(data.getType());
        entity.setExternalOrderNumber(data.getExternalOrderNumber());
        entity.setReportToId(data.getReportToId());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setBillToId(data.getBillToId());
        entity.setBillToAttention(data.getBillToAttention());
        entity.setShipFromId(data.getShipFromId());

        return data;
    }

    public void validate(OrderViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getStatusId()))
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getStatusId()));

        if (DataBaseUtil.isEmpty(data.getNeededInDays()))
            list.add(new FieldErrorException("fieldRequiredException", OrderMeta.getNeededInDays()));

        if (list.size() > 0)
            throw list;
    }
    
    private void setOrganizationReportToBillTo(OrderViewDO data) throws Exception {
        int i;
        Integer ids[];   
        List<OrganizationViewDO> list;
        OrganizationViewDO organization;
        
        ids = new Integer[3];
        i = 0;
        if (data.getOrganizationId() != null)
            ids[i++] = data.getOrganizationId();
        if (data.getReportToId() != null) 
            ids[i++] = data.getReportToId();
        if (data.getBillToId() != null) 
            ids[i++] = data.getBillToId();
        if (i != 0) {
            list = organizationBean.fetchByIds(ids);
            for (i = 0; i < list.size(); i++) {
                organization = list.get(i);
                if (organization.getId().equals(data.getOrganizationId()) && data.getOrganization() == null)
                    data.setOrganization(organization);
                if (organization.getId().equals(data.getReportToId()) && data.getReportTo() == null)
                    data.setReportTo(organization);
                if (organization.getId().equals(data.getBillToId()) && data.getBillTo() == null)
                    data.setBillTo(organization);
            }
        }
    }
}

/*
    @PersistenceContext(name = "openelis")
    private EntityManager  manager;

    private LockLocal      lockBean;
    private static int     orderRefTable, orderShippingNoteRefTableId, orderCustNoteRefTableId;

    // private static final OrderMetaMap OrderMetaMap = new OrderMetaMap();

    public OrderBean() {
        orderRefTable = ReferenceTable.ORDER;
        orderShippingNoteRefTableId = ReferenceTable.ORDER_SHIPPING_NOTE;
        orderCustNoteRefTableId = ReferenceTable.ORDER_CUSTOMER_NOTE;
    }

    @PostConstruct
    private void init() {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");
    }

    public OrderDO getOrder(Integer orderId, String orderType) {
        Query query;

        if (orderType.equals(OrderRemote.INTERNAL))
            query = manager.createNamedQuery("Order.OrderInternal");
        else
            query = manager.createNamedQuery("Order.OrderExternalKit");

        query.setParameter("id", orderId);

        OrderDO orderDO = (OrderDO)query.getResultList().get(0);// getting order
                                                                // record

        return orderDO;
    }

    @RolesAllowed("order-update")
    public OrderDO getOrderAndLock(Integer orderId, String orderType, String session)
                                                                                     throws Exception {
        lockBean.getLock(orderRefTable, orderId);

        return getOrder(orderId, orderType);
    }

    public OrderDO getOrderAndUnlock(Integer orderId, String orderType, String session) {
        // unlock the entity
        lockBean.giveUpLock(orderRefTable, orderId);

        return getOrder(orderId, orderType);
    }

    public List getOrderItems(Integer orderId) {
        Query query;
        // if(withLocation)
        // query =
        // manager.createNamedQuery("OrderItem.OrderItemsWithLocByOrderId");
        // else
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

        if (results.size() > 0)
            return (NoteViewDO)results.get(0);
        else
            return null;
    }

    public NoteViewDO getCustomerNote(Integer orderId) {
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", orderCustNoteRefTableId);
        query.setParameter("id", orderId);

        List results = query.getResultList();

        if (results.size() > 0)
            return (NoteViewDO)results.get(0);
        else
            return null;
    }

    public BillToReportToDO getBillToReportTo(Integer orderId) {
        Query query = manager.createNamedQuery("Order.ReportToBillTo");
        query.setParameter("id", orderId);

        List results = query.getResultList();

        if (results.size() > 0)
            return (BillToReportToDO)results.get(0);
        else
            return null;
    }

    
     * public List query(ArrayList<AbstractField> fields, int first, int max,
     * String orderType) throws Exception { StringBuffer sb = new
     * StringBuffer(); QueryBuilder qb = new QueryBuilder(); /
     * qb.setMeta(OrderMetaMap);
     * qb.setSelect("distinct new org.openelis.domain.IdNameDO("
     * +OrderMetaMap.getId()+") "); //this method is going to throw an exception
     * if a column doesnt match qb.addWhere(fields);
     * qb.setOrderBy(OrderMetaMap.getId());
     * if(orderType.equals(OrderRemote.EXTERNAL)){
     * qb.addWhere(OrderMetaMap.getIsExternal() + " = 'Y'");
     * qb.addWhere(OrderMetaMap.getOrganizationId() + " is not null"); }else
     * if(orderType.equals(OrderRemote.INTERNAL)){
     * qb.addWhere(OrderMetaMap.getIsExternal() + " = 'N'");
     * qb.addWhere(OrderMetaMap.getOrganizationId() + " is null"); }else
     * if(orderType.equals(OrderRemote.KITS)){
     * qb.addWhere(OrderMetaMap.getIsExternal() + " = 'N'");
     * qb.addWhere(OrderMetaMap.getOrganizationId() + " is not null"); } //need
     * to see if Store is in the from clause String whereClause =
     * qb.getWhereClause(); if(whereClause.indexOf("store.") > -1){
     * whereClause+=
     * " and("+OrderMetaMap.getOrderItem().getInventoryItem().getStoreId
     * ()+" = "+OrderMetaMap.getStore().getId()+") "; //TODO taking this out for
     * now...need to look into this //if(whereClause.indexOf("inventoryTrans.")
     * > -1) // whereClause +=
     * " and ("+OrderMetaMap.ORDER_INV_TRANS_META.getOrderItemId
     * ()+" = "+OrderMetaMap.ORDER_ITEM_META.getId()+") ";
     * sb.append(qb.getSelectClause
     * ()).append(qb.getFromClause(whereClause)).append
     * (whereClause).append(qb.getOrderBy()); }else{ sb.append(qb.getEJBQL()); }
     * Query query = manager.createQuery(sb.toString()); if(first > -1 && max >
     * -1) query.setMaxResults(first+max); //set the parameters in the query
     * qb.setQueryParams(query); List returnList =
     * GetPage.getPage(query.getResultList(), first, max); if(returnList ==
     * null) throw new LastPageException(); else return returnList; }
     
    public OrderAddAutoFillDO getAddAutoFillValues() throws Exception {
        OrderAddAutoFillDO autoFillDO = new OrderAddAutoFillDO();
        // status integer
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", "order_status_pending");
        DictionaryDO dictDO = (DictionaryDO)query.getSingleResult();
        autoFillDO.setStatus(dictDO.getId());
        // order date
        autoFillDO.setOrderedDate(new Date());
        // requested by string
        // SystemUserDO systemUserDO = sysUser.getSystemUser();
        autoFillDO.setRequestedBy(ctx.getCallerPrincipal().getName());

        return autoFillDO;
    }

    @RolesAllowed("order-update")
    public Integer updateOrder(OrderDO orderDO,
                               String orderType,
                               List items,
                               NoteViewDO customerNoteDO,
                               NoteViewDO orderShippingNotes) throws Exception {
        if (orderDO.getId() != null)
            lockBean.validateLock(orderRefTable, orderDO.getId());

        validateOrder(orderDO, orderType, items);

        manager.setFlushMode(FlushModeType.COMMIT);
        Order order = null;

        if (orderDO.getId() == null)
            order = new Order();
        else
            order = manager.find(Order.class, orderDO.getId());

        // update order record
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

        // update order items
        for (int i = 0; i < items.size(); i++ ) {
            OrderItemDO orderItemDO = (OrderItemDO)items.get(i);
            OrderItem orderItem = null;

            if (orderItemDO.getId() == null)
                orderItem = new OrderItem();
            else
                orderItem = manager.find(OrderItem.class, orderItemDO.getId());

            if (orderItemDO.getDelete() && orderItemDO.getId() != null) {
                // delete the order item record from the database
                manager.remove(orderItem);

            } else {
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
        // update the customer note field
        if (customerNoteDO.getText() != null) {
            Note custNote = null;

            if (customerNoteDO.getId() == null)
                custNote = new Note();
            else
                custNote = manager.find(Note.class, customerNoteDO.getId());

            custNote.setIsExternal(customerNoteDO.getIsExternal());
            custNote.setReferenceId(order.getId());
            custNote.setReferenceTableId(orderCustNoteRefTableId);
            custNote.setText(customerNoteDO.getText());
            if (custNote.getId() == null) {
                systemUserId = lockBean.getSystemUserId();
                custNote.setSystemUserId(systemUserId);
            }
            custNote.setTimestamp(Datetime.getInstance());

            if (custNote.getId() == null) {
                manager.persist(custNote);
            }
        }

        // update the shipping note field
        if (orderShippingNotes.getText() != null) {
            Note shippingNote = null;

            if (orderShippingNotes.getId() == null)
                shippingNote = new Note();
            else
                shippingNote = manager.find(Note.class, orderShippingNotes.getId());

            shippingNote.setIsExternal(orderShippingNotes.getIsExternal());
            shippingNote.setReferenceId(order.getId());
            shippingNote.setReferenceTableId(orderShippingNoteRefTableId);
            shippingNote.setText(orderShippingNotes.getText());
            if (shippingNote.getId() == null) {
                if (systemUserId == null)
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

    private void validateOrder(OrderDO orderDO, String orderType, List items) throws Exception {
        ValidationErrorsList list = new ValidationErrorsList();
        
         * //status required for all order types if(orderDO.getStatusId() ==
         * null || "".equals(orderDO.getStatusId())){ list.add(new
         * FieldErrorException
         * ("fieldRequiredException",OrderMetaMap.getStatusId())); } //needed in
         * days required for all order types if(orderDO.getNeededInDays() ==
         * null || "".equals(orderDO.getNeededInDays())){ list.add(new
         * FieldErrorException
         * ("fieldRequiredException",OrderMetaMap.getNeededInDays())); }
         * //organization required for vendor orders and kit orders
         * if((orderDO.getOrganizationId() == null ||
         * "".equals(orderDO.getOrganizationId())) &&
         * (OrderRemote.EXTERNAL.equals(orderType) ||
         * OrderRemote.KITS.equals(orderType))){ list.add(new
         * FieldErrorException
         * ("fieldRequiredException",OrderMetaMap.getOrganizationId())); }
         * //ordered date required for all order types
         * if(orderDO.getOrderedDate() == null ||
         * "".equals(orderDO.getOrderedDate())){ list.add(new
         * FieldErrorException
         * ("fieldRequiredException",OrderMetaMap.getOrderedDate())); }
         * //requested by required for all order types
         * if(orderDO.getRequestedBy() == null ||
         * "".equals(orderDO.getRequestedBy())){ list.add(new
         * FieldErrorException
         * ("fieldRequiredException",OrderMetaMap.getRequestedBy())); } //TODO
         * lazy load kind of messes this up if the table isnt loaded //number of
         * order items needs to be > 0 //if(validateOrderQty && items.size() <
         * 1){ // list.add(new FormErrorException("zeroOrderItemsException"));
         * //} for(int i=0; i<items.size();i++)
         * validateOrderItems((OrderItemDO)items.get(i), i, list);
         * if(list.size() > 0) throw list;
         
    }

    private void validateOrderItems(OrderItemDO orderItemDO,
                                    int rowIndex,
                                    ValidationErrorsList exceptionList) {
        if (orderItemDO.getDelete())
            return;
        /*
         * //quantity is required for all order types
         * if(orderItemDO.getQuantity() == null ||
         * "".equals(orderItemDO.getQuantity())){ exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * OrderMetaMap.ORDER_ITEM_META.getQuantity())); } //inventory item is
         * required for all order types if(orderItemDO.getInventoryItemId() ==
         * null || "".equals(orderItemDO.getInventoryItemId())){
         * exceptionList.add(new
         * TableFieldErrorException("fieldRequiredException", rowIndex,
         * OrderMetaMap.ORDER_ITEM_META.INVENTORY_ITEM_META.getName())); }
         
    }
*/