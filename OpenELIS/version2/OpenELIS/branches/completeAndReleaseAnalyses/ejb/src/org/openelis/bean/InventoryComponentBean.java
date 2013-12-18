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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.entity.InventoryComponent;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class InventoryComponentBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    @EJB
    InventoryItemCacheBean inventoryCache;

    @SuppressWarnings("unchecked")
    public ArrayList<InventoryComponentViewDO> fetchByInventoryItemId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryComponent.FetchByInventoryItemId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public InventoryComponentDO add(InventoryComponentDO data) throws Exception {
        InventoryComponent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new InventoryComponent();
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setComponentId(data.getComponentId());
        entity.setQuantity(data.getQuantity());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public InventoryComponentDO update(InventoryComponentDO data) throws Exception {
        InventoryComponent entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryComponent.class, data.getId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setComponentId(data.getComponentId());
        entity.setQuantity(data.getQuantity());

        return data;
    }

    public void delete(InventoryComponentDO data) throws Exception {
        InventoryComponent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryComponent.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(InventoryComponentDO data, Integer inventoryItemStoreId) throws Exception {
        Integer compId;
        ValidationErrorsList list;       
        InventoryItemDO item;

        list = new ValidationErrorsList();
        compId = data.getComponentId();

        if (DataBaseUtil.isEmpty(compId))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             InventoryItemMeta.getComponentId()));
        if (DataBaseUtil.isEmpty(data.getQuantity()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             InventoryItemMeta.getComponentQuantity()));
        
        if (inventoryItemStoreId != null) {
            item = inventoryCache.getById(compId);
            if (!inventoryItemStoreId.equals(item.getStoreId())) {
                list.add(new FieldErrorException(Messages.get().compStoreNotSameAsKitStoreException(),
                                                 InventoryItemMeta.getComponentName()));
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
