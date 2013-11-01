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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.entity.Order;
import org.openelis.meta.OrderMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class OrderBean {

    @Resource
    private SessionContext         ctx;

    @PersistenceContext(unitName = "openelis")
    private EntityManager          manager;

    @EJB
    private OrganizationBean       organization;

    private static final OrderMeta meta = new OrderMeta();

    public OrderViewDO fetchById(Integer id) throws Exception {
        Query query;
        OrderViewDO data;

        query = manager.createNamedQuery("Order.FetchById");
        query.setParameter("id", id);
        try {
            data = (OrderViewDO)query.getSingleResult();
            if (data.getOrganizationId() != null)
                data.setOrganization(organization.fetchById(data.getOrganizationId()));
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<OrderViewDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("Order.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<OrderViewDO> fetchByShippingId(Integer shippingId) throws Exception {
        Query query;

        query = manager.createNamedQuery("Order.FetchByShippingId");
        query.setParameter("referenceTableId", Constants.table().ORDER_ITEM);
        query.setParameter("shippingId", shippingId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrderViewDO fetchByOrderItemId(Integer id) throws Exception {
        Query query;
        OrderViewDO data;

        query = manager.createNamedQuery("Order.FetchByOrderItemId");
        query.setParameter("id", id);

        try {
            data = (OrderViewDO)query.getSingleResult();
            if (data.getOrganizationId() != null)
                data.setOrganization(organization.fetchById(data.getOrganizationId()));
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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + OrderMeta.getId() + ", " +
                          OrderMeta.getRequestedBy() + ") ");
        builder.constructWhere(fields);
        if (builder.getWhereClause().indexOf("auxData.") > -1)
            builder.addWhere(SampleMeta.getAuxDataReferenceTableId() + " = " +
                             Constants.table().ORDER);
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
        builder.setSelect("distinct new org.openelis.domain.OrderViewDO(" + OrderMeta.getId() +
                          ", " + OrderMeta.getParentOrderId() + ", " + OrderMeta.getDescription() +
                          ", " + OrderMeta.getStatusId() + ", " + OrderMeta.getOrderedDate() +
                          ", " + OrderMeta.getNeededInDays() + ", " + OrderMeta.getRequestedBy() +
                          ", " + OrderMeta.getCostCenterId() + ", " +
                          OrderMeta.getOrganizationId() + ", " +
                          OrderMeta.getOrganizationAttention() + ", " + OrderMeta.getType() + ", " +
                          OrderMeta.getExternalOrderNumber() + ", " + OrderMeta.getShipFromId() +
                          ", " + OrderMeta.getNumberOfForms() + ") ");
        builder.constructWhere(fields);
        builder.addWhere(OrderMeta.getType() + " <> 'V'");

        builder.setOrderBy(OrderMeta.getId() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        if (list == null)
            throw new LastPageException();

        try {
            for (int i = 0; i < list.size(); i++) {
                data = (OrderViewDO)list.get(i);
                if (data.getOrganizationId() != null)
                    data.setOrganization(organization.fetchById(data.getOrganizationId()));
            }
            return DataBaseUtil.toArrayList(list);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public OrderDO add(OrderDO data) throws Exception {
        Order entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Order();
        entity.setParentOrderId(data.getParentOrderId());
        entity.setDescription(data.getDescription());
        entity.setStatusId(data.getStatusId());
        entity.setOrderedDate(data.getOrderedDate());
        entity.setNeededInDays(data.getNeededInDays());
        if (data.getRequestedBy() == null)
            data.setRequestedBy(User.getName(ctx));
        entity.setRequestedBy(data.getRequestedBy());
        entity.setCostCenterId(data.getCostCenterId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setType(data.getType());
        entity.setExternalOrderNumber(data.getExternalOrderNumber());
        entity.setShipFromId(data.getShipFromId());
        entity.setNumberOfForms(data.getNumberOfForms());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrderDO update(OrderDO data) throws Exception {
        Order entity;

        if (!data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Order.class, data.getId());
        entity.setParentOrderId(data.getParentOrderId());
        entity.setDescription(data.getDescription());
        entity.setStatusId(data.getStatusId());
        entity.setOrderedDate(data.getOrderedDate());
        entity.setNeededInDays(data.getNeededInDays());
        if (data.getRequestedBy() == null)
            data.setRequestedBy(User.getName(ctx));
        entity.setRequestedBy(data.getRequestedBy());
        entity.setCostCenterId(data.getCostCenterId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setType(data.getType());
        entity.setExternalOrderNumber(data.getExternalOrderNumber());
        entity.setShipFromId(data.getShipFromId());
        entity.setNumberOfForms(data.getNumberOfForms());

        return data;
    }

    public void validate(OrderDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getStatusId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_statusRequiredException(DataBaseUtil.toString(data.getId()))));

        if (DataBaseUtil.isEmpty(data.getNeededInDays()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_neededInDaysRequiredException(DataBaseUtil.toString(data.getId()))));

        if (Constants.order().SEND_OUT.equals(data.getType())) {
            if (DataBaseUtil.isEmpty(data.getNumberOfForms()))
                list.add(new FormErrorException(Messages.get()
                                                        .order_numFormsRequiredException(DataBaseUtil.toString(data.getId()))));

            if (DataBaseUtil.isEmpty(data.getShipFromId()))
                list.add(new FormErrorException(Messages.get()
                                                        .order_shipFromRequiredException(DataBaseUtil.toString(data.getId()))));

            if (DataBaseUtil.isEmpty(data.getCostCenterId()))
                list.add(new FormErrorException(Messages.get()
                                                        .order_costCenterRequiredException(DataBaseUtil.toString(data.getId()))));
        }
        if (list.size() > 0)
            throw list;
    }
}