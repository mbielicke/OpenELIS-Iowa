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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.entity.InventoryItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class InventoryItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
    @EJB
    private InventoryItemCacheBean  itemCache; 
    
    private static InventoryItemMeta meta = new InventoryItemMeta();
    
    public InventoryItemViewDO fetchById(Integer id) throws Exception {
        Query query;
        InventoryItemViewDO data;
        
        query = manager.createNamedQuery("InventoryItem.FetchById");
        query.setParameter("id", id);
        try {
            data = (InventoryItemViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public InventoryItemDO fetchActiveById(Integer id) throws Exception {
        Query query;
        InventoryItemDO data;
        
        query = manager.createNamedQuery("InventoryItem.FetchActiveById");
        query.setParameter("id", id);
        try {
            data = (InventoryItemDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<InventoryItemDO> fetchActiveByName(String name, int max) {
        Query query;
        
        query = manager.createNamedQuery("InventoryItem.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(String name, Integer storeId, int max) {
        Query query;
        
        query = manager.createNamedQuery("InventoryItem.FetchActiveByNameAndStore");
        query.setParameter("name", name);
        query.setParameter("storeId", storeId);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<InventoryItemDO> fetchActiveByNameStoreAndParentInventoryItem(String name, Integer parentInventoryItemId,
                                                                                   int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("InventoryItem.FetchActiveByNameAndParentInventoryItem");
        query.setParameter("name", name);
        query.setParameter("parentInventoryItemId", parentInventoryItemId);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
   

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameStoreVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameStoreVO(" + 
                          InventoryItemMeta.getId() + "," + InventoryItemMeta.getName() + 
                          "," + InventoryItemMeta.getStoreId() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(InventoryItemMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameStoreVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameStoreVO>)list;
    }


    public InventoryItemDO add(InventoryItemDO data) throws Exception {
        InventoryItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new InventoryItem();
        entity.setName(data.getName());        
        entity.setDescription(data.getDescription());
        entity.setCategoryId(data.getCategoryId());
        entity.setStoreId(data.getStoreId());
        entity.setQuantityMinLevel(data.getQuantityMinLevel());
        entity.setQuantityMaxLevel(data.getQuantityMaxLevel());
        entity.setQuantityToReorder(data.getQuantityToReorder());
        entity.setDispensedUnitsId(data.getDispensedUnitsId());
        entity.setIsReorderAuto(data.getIsReorderAuto());
        entity.setIsLotMaintained(data.getIsLotMaintained());
        entity.setIsSerialMaintained(data.getIsSerialMaintained());
        entity.setIsActive(data.getIsActive());
        entity.setIsBulk(data.getIsBulk());
        entity.setIsNotForSale(data.getIsNotForSale());
        entity.setIsSubAssembly(data.getIsSubAssembly());
        entity.setIsLabor(data.getIsLabor());
        entity.setIsNotInventoried(data.getIsNotInventoried());
        entity.setProductUri(data.getProductUri());
        entity.setAverageLeadTime(data.getAverageLeadTime());
        entity.setAverageCost(data.getAverageCost());
        entity.setAverageDailyUse(data.getAverageDailyUse());
        entity.setParentInventoryItemId(data.getParentInventoryItemId());
        entity.setParentRatio(data.getParentRatio());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public InventoryItemDO update(InventoryItemDO data) throws Exception {
        InventoryItem entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);        
        entity = manager.find(InventoryItem.class, data.getId());
        
        // need to remove it before we change it
        itemCache.evict(entity.getId());
        
        entity.setName(data.getName());        
        entity.setDescription(data.getDescription());
        entity.setCategoryId(data.getCategoryId());
        entity.setStoreId(data.getStoreId());
        entity.setQuantityMinLevel(data.getQuantityMinLevel());
        entity.setQuantityMaxLevel(data.getQuantityMaxLevel());
        entity.setQuantityToReorder(data.getQuantityToReorder());
        entity.setDispensedUnitsId(data.getDispensedUnitsId());
        entity.setIsReorderAuto(data.getIsReorderAuto());
        entity.setIsLotMaintained(data.getIsLotMaintained());
        entity.setIsSerialMaintained(data.getIsSerialMaintained());
        entity.setIsActive(data.getIsActive());
        entity.setIsBulk(data.getIsBulk());
        entity.setIsNotForSale(data.getIsNotForSale());
        entity.setIsSubAssembly(data.getIsSubAssembly());
        entity.setIsLabor(data.getIsLabor());
        entity.setIsNotInventoried(data.getIsNotInventoried());
        entity.setProductUri(data.getProductUri());
        entity.setAverageLeadTime(data.getAverageLeadTime());
        entity.setAverageCost(data.getAverageCost());
        entity.setAverageDailyUse(data.getAverageDailyUse());
        entity.setParentInventoryItemId(data.getParentInventoryItemId());
        entity.setParentRatio(data.getParentRatio());

        return data;
    }

    public void validate(InventoryItemDO data) throws Exception {
        ArrayList<InventoryItemDO> dup;
        InventoryItemViewDO parentItem;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getName()));

        if (DataBaseUtil.isEmpty(data.getStoreId()))
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getStoreId()));
        
        if (DataBaseUtil.isEmpty(data.getQuantityMinLevel()))
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getQuantityMinLevel()));

        if (DataBaseUtil.isEmpty(data.getDispensedUnitsId()))
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getDispensedUnitsId()));
        
        if (DataBaseUtil.isEmpty(data.getQuantityToReorder()))
            list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getQuantityToReorder()));

        //
        // check for duplicate lot #
        //
        if (! DataBaseUtil.isEmpty(data.getName()) && ! DataBaseUtil.isEmpty(data.getStoreId())) {
            dup = fetchActiveByNameAndStore(data.getName(), data.getStoreId(), 1);
            if (dup.size() > 0 && DataBaseUtil.isDifferent(dup.get(0).getId(), data.getId()))
                list.add(new FieldErrorException("fieldUniqueException", InventoryItemMeta.getName()));
        }        

        if ("Y".equals(data.getIsBulk()) && "Y".equals(data.getIsSerialMaintained()))
            list.add(new FieldErrorException("itemCantBeBulkAndSerialReqException", null)); 
        
        if ("Y".equals(data.getIsBulk()) && "Y".equals(data.getIsLotMaintained()))
            list.add(new FieldErrorException("itemCantBeBulkAndLotReqException", null));
                
        if ( !DataBaseUtil.isEmpty(data.getParentInventoryItemId())) {
            parentItem = fetchById(data.getParentInventoryItemId());            
            if ("Y".equals(data.getIsLotMaintained()) && !"Y".equals(parentItem.getIsLotMaintained()))
                list.add(new FieldErrorException("parentNotFlaggedLotReqException", InventoryItemMeta.getIsLotMaintained()));    
            
            if ( !DataBaseUtil.isEmpty(data.getParentRatio())) {
                if (data.getParentRatio() <= 0)
                    list.add(new FieldErrorException("parentRatioMoreThanZeroException", InventoryItemMeta.getParentRatio()));
            } else {
                list.add(new FieldErrorException("parentRatioReqIfParentItemSpecException", InventoryItemMeta.getParentRatio()));
            }
        }               
        
        if (list.size() > 0)
            throw list;
    }
}
