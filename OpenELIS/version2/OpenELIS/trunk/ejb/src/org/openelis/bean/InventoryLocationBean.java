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
import org.openelis.domain.InventoryLocationDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.InventoryLocation;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.LockLocal;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.remote.InventoryLocationRemote;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryitem-select")
public class InventoryLocationBean implements InventoryLocationLocal, InventoryLocationRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;
    
    @EJB
    private LockLocal                        lockBean;

    @SuppressWarnings("unchecked")
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryLocation.FetchByInventoryItemId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<InventoryLocationViewDO> fetchByInventoryReceiptId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryLocation.FetchByInventoryReceiptId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public InventoryLocationViewDO fetchById(Integer id) throws Exception {
        Query query;
        InventoryLocationViewDO data;

        query = manager.createNamedQuery("InventoryLocation.FetchById");
        query.setParameter("id", id);

        try {
            data = (InventoryLocationViewDO)query.getSingleResult();          
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }       
    
    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemId(String match,Integer id, int maxResults) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryLocation.FetchByLocationNameAndItemId");
        query.setParameter("name", match);
        query.setParameter("id", id);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemIdStoreId(String match, Integer inventoryItemId,
                                                                                        Integer storeId, int maxResults) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryLocation.FetchByLocationNameItemIdAndStoreId");
        query.setParameter("name", match);
        query.setParameter("inventoryItemId", inventoryItemId);
        query.setParameter("storeId", storeId);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemName(String match, int maxResults) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryLocation.FetchByInventoryItemName");
        query.setParameter("name", match);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemNameStoreId(String match,Integer storeId, int maxResults) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryLocation.FetchByInventoryItemNameStoreId");
        query.setParameter("name", match);
        query.setParameter("id", storeId);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public InventoryLocationViewDO add(InventoryLocationViewDO data) throws Exception {
        InventoryLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new InventoryLocation();
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setLotNumber(data.getLotNumber());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setQuantityOnhand(data.getQuantityOnhand());
        entity.setExpirationDate(data.getExpirationDate());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public InventoryLocationViewDO update(InventoryLocationViewDO data) throws Exception {
        InventoryLocation entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryLocation.class, data.getId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setLotNumber(data.getLotNumber());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setQuantityOnhand(data.getQuantityOnhand());
        entity.setExpirationDate(data.getExpirationDate());

        return data;
    }
    
    public InventoryLocationViewDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.INVENTORY_LOCATION, id);
        return fetchById(id);
    }
    
    public InventoryLocationViewDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.INVENTORY_LOCATION, id);
        return fetchById(id);
    }

    public void delete(InventoryLocationDO data) throws Exception {
        InventoryLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryLocation.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(InventoryLocationDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getStorageLocationId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             InventoryItemMeta.getLocationStorageLocationId()));
        if (DataBaseUtil.isEmpty(data.getQuantityOnhand()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             InventoryItemMeta.getLocationQuantityOnhand()));
        
        if (list.size() > 0)
            throw list;
    }    
}