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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.OrderItemDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.entity.OrderItem;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class OrderItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<OrderItemViewDO> fetchByOrderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrderItem.FetchByOrderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<OrderItemViewDO> fetchByOrderIds(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("OrderItem.FetchByOrderIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrderItemViewDO fetchById(Integer id) throws Exception {
        Query query;
        OrderItemViewDO data;

        query = manager.createNamedQuery("OrderItem.FetchById");
        query.setParameter("id", id);
        try {
            data = (OrderItemViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public OrderItemDO add(OrderItemDO data) throws Exception {
        OrderItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new OrderItem();
        entity.setOrderId(data.getOrderId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setQuantity(data.getQuantity());
        entity.setCatalogNumber(data.getCatalogNumber());
        entity.setUnitCost(data.getUnitCost());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrderItemDO update(OrderItemDO data) throws Exception {
        OrderItem entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderItem.class, data.getId());
        entity.setOrderId(data.getOrderId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setQuantity(data.getQuantity());
        entity.setCatalogNumber(data.getCatalogNumber());
        entity.setUnitCost(data.getUnitCost());

        return data;
    }

    public ArrayList<OrderItemViewDO> add(OrderViewDO order, ArrayList<OrderItemViewDO> items) throws Exception {
        for (int i = 0; i < items.size(); i++ )
            add(items.get(i));

        return items;
    }

    public void delete(OrderItemDO data) throws Exception {
        OrderItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderItem.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(OrderItemDO data) throws Exception {
        Integer orderId;
        ValidationErrorsList list;

        /*
         * for display
         */
        orderId = data.getOrderId();
        if (orderId == null)
            orderId = 0;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getInventoryItemId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_inventoryItemRequiredException(orderId)));
        if (DataBaseUtil.isEmpty(data.getQuantity()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_inventoryQuantityRequiredException(orderId)));
        if (list.size() > 0)
            throw list;
    }
}
