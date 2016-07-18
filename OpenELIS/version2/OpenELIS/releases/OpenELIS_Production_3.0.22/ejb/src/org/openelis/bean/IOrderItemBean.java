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
import org.openelis.domain.IOrderItemDO;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.entity.IOrderItem;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class IOrderItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<IOrderItemViewDO> fetchByIorderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("IOrderItem.FetchByIorderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IOrderItemViewDO> fetchByIorderIds(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("IOrderItem.FetchByIorderIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public IOrderItemViewDO fetchById(Integer id) throws Exception {
        Query query;
        IOrderItemViewDO data;

        query = manager.createNamedQuery("IOrderItem.FetchById");
        query.setParameter("id", id);
        try {
            data = (IOrderItemViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public IOrderItemDO add(IOrderItemDO data) throws Exception {
        IOrderItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new IOrderItem();
        entity.setIorderId(data.getIorderId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setQuantity(data.getQuantity());
        entity.setCatalogNumber(data.getCatalogNumber());
        entity.setUnitCost(data.getUnitCost());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public IOrderItemDO update(IOrderItemDO data) throws Exception {
        IOrderItem entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderItem.class, data.getId());
        entity.setIorderId(data.getIorderId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setQuantity(data.getQuantity());
        entity.setCatalogNumber(data.getCatalogNumber());
        entity.setUnitCost(data.getUnitCost());

        return data;
    }

    public ArrayList<IOrderItemViewDO> add(IOrderViewDO iorder, ArrayList<IOrderItemViewDO> items) throws Exception {
        for (int i = 0; i < items.size(); i++ )
            add(items.get(i));

        return items;
    }

    public void delete(IOrderItemDO data) throws Exception {
        IOrderItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderItem.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(IOrderItemDO data) throws Exception {
        Integer iorderId;
        ValidationErrorsList list;

        /*
         * for display
         */
        iorderId = data.getIorderId();
        if (iorderId == null)
            iorderId = 0;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getInventoryItemId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_inventoryItemRequiredException(iorderId)));
        if (DataBaseUtil.isEmpty(data.getQuantity()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_inventoryQuantityRequiredException(iorderId)));
        if (list.size() > 0)
            throw list;
    }
}
