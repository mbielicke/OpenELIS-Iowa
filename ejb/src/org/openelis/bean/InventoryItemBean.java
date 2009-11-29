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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.entity.InventoryItem;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.InventoryItemLocal;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryitem-select")
public class InventoryItemBean implements InventoryItemRemote, InventoryItemLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
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

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameStoreVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameStoreVO(" + 
                          meta.getId() + "," + meta.getName() + "," + meta.getStoreId() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(meta.getName());

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


    public InventoryItemViewDO add(InventoryItemViewDO data) throws Exception {
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

    public InventoryItemViewDO update(InventoryItemViewDO data) throws Exception {
        InventoryItem entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(InventoryItem.class, data.getId());
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

    public void validate(InventoryItemViewDO data) throws Exception {
        ArrayList<InventoryItemDO> dup;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));

        if (DataBaseUtil.isEmpty(data.getStoreId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getStoreId()));

        if (DataBaseUtil.isEmpty(data.getDispensedUnitsId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getDispensedUnitsId()));

        //
        // check for duplicate lot #
        //
        if (! DataBaseUtil.isEmpty(data.getName()) && ! DataBaseUtil.isEmpty(data.getStoreId())) {
            dup = fetchActiveByNameAndStore(data.getName(), data.getStoreId(), 1);
            if (dup.size() > 0 && DataBaseUtil.isDifferent(dup.get(0).getId(), data.getId()))
                list.add(new FieldErrorException("fieldUniqueException", meta.getName()));
        }
        if (list.size() > 0)
            throw list;
    }
}

/*
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    public InventoryItemBean(){
        invItemRefTableId = ReferenceTable.INVENTORY_ITEM_MANUFACTURING;
        invItemManufacRefTableId = ReferenceTable.INVENTORY_ITEM;
        
    }

	public List getInventoryComponents(Integer inventoryItemId) {
        Query query = manager.createNamedQuery("InventoryComponent.InventoryComponentsByItem");
        query.setParameter("id", inventoryItemId);
        
        List components = query.getResultList();// getting list of components from the item id
    
        return components;
	}
    
	public InventoryItemDO getInventoryItem(Integer inventoryItemId) {
		Query query = manager.createNamedQuery("InventoryItem.InventoryItem");
		query.setParameter("id", inventoryItemId);
		InventoryItemDO inventoryItem = (InventoryItemDO) query.getResultList().get(0);  // get the inventory item

        return inventoryItem;
	}

	@RolesAllowed("inventory-update")
	public InventoryItemDO getInventoryItemAndLock(Integer inventoryItemId, String session) throws Exception {
        lockBean.getLock(invItemRefTableId, inventoryItemId);
        
        return getInventoryItem(inventoryItemId);
	}

	public InventoryItemDO getInventoryItemAndUnlock(Integer inventoryItemId, String session) {
		//unlock the entity
        lockBean.giveUpLock(invItemRefTableId, inventoryItemId);
       		
        return getInventoryItem(inventoryItemId);
	}

	public List getInventoryLocations(Integer inventoryItemId) {
        Query query = manager.createNamedQuery("InventoryLocation.InventoryLocationByItem");
        query.setParameter("id", inventoryItemId);
        
        List components = query.getResultList();// getting list of locations from the item id
    
        return components;
	}

	public List getInventoryNotes(Integer inventoryItemId) {
	    Query query = null;
        
        query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", invItemRefTableId);
        query.setParameter("id", inventoryItemId);
        
        List notes = query.getResultList();// getting list of noteDOs from the item id

        for(int i=0; i<notes.size(); i++){
            NoteViewDO noteDO = (NoteViewDO)notes.get(i);
            SystemUserDO userDO = userLocal.getSystemUser(noteDO.getSystemUserId());
            noteDO.setSystemUser(userDO.getLoginName());
        }
        
        return notes;
	}
	
	public NoteViewDO getInventoryMaunfacturingRecipe(Integer inventoryItemId){
	    Query query = null;
        
	    query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", invItemManufacRefTableId);
        query.setParameter("id", inventoryItemId);
        
        List notes = query.getResultList();// getting list of noteDOs from the item id

        if(notes != null && notes.size() > 0)
            return (NoteViewDO)notes.get(0);
        else 
            return null;
	}

	public List query(ArrayList fields, int first, int max) throws Exception {
        
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(invItemMap);

        qb.setSelect("distinct new org.openelis.domain.IdNameStoreDO("+
                         invItemMap.getId()+", "+
                         invItemMap.getName()+", "+
                         invItemMap.DICTIONARY_STORE_META.getEntry()+") ");

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.addWhere(invItemMap.getStoreId()+" = "+invItemMap.DICTIONARY_STORE_META.getId());

        qb.setOrderBy(invItemMap.getName());

        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
	}

    @RolesAllowed("inventory-update")
	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, NoteViewDO noteDO, NoteViewDO manufacturingNote) throws Exception {
        if(inventoryItemDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.validateLock(invItemRefTableId,inventoryItemDO.getId());
        }
        
        //validate inventory item
        validateInventoryItem(inventoryItemDO, components);
        
         manager.setFlushMode(FlushModeType.COMMIT);
         InventoryItem inventoryItem = null;
    
         if (inventoryItemDO.getId() == null)
             inventoryItem = new InventoryItem();
        else
            inventoryItem = manager.find(InventoryItem.class, inventoryItemDO.getId());
         
         //update the inventory record
         inventoryItem.setAverageCost(inventoryItemDO.getAveCost());
         inventoryItem.setAverageDailyUse(inventoryItemDO.getAveDailyUse());
         inventoryItem.setAverageLeadTime(inventoryItemDO.getAveLeadTime());
//         inventoryItem.setCategoryId(inventoryItemDO.getCategory());
         inventoryItem.setDescription(inventoryItemDO.getDescription());
//         inventoryItem.setDispensedUnitsId(inventoryItemDO.getDispensedUnits());
         inventoryItem.setIsActive(inventoryItemDO.getIsActive());
         inventoryItem.setIsBulk(inventoryItemDO.getIsBulk());
         inventoryItem.setIsLabor(inventoryItemDO.getIsLabor());
         inventoryItem.setIsLotMaintained(inventoryItemDO.getIsLotMaintained());
         inventoryItem.setIsNoInventory(inventoryItemDO.getIsNotInventoried());
         inventoryItem.setIsNotForSale(inventoryItemDO.getIsNotForSale());
         inventoryItem.setIsReorderAuto(inventoryItemDO.getIsReorderAuto());
         inventoryItem.setIsSerialMaintained(inventoryItemDO.getIsSerialMaintained());
         inventoryItem.setIsSubAssembly(inventoryItemDO.getIsSubAssembly());
         inventoryItem.setName(inventoryItemDO.getName());
         inventoryItem.setProductUri(inventoryItemDO.getProductUri());
         inventoryItem.setQuantityMaxLevel(inventoryItemDO.getQuantityMaxLevel());
         inventoryItem.setQuantityMinLevel(inventoryItemDO.getQuantityMinLevel());
         inventoryItem.setQuantityToReorder(inventoryItemDO.getQuantityToReorder());
//         inventoryItem.setStoreId(inventoryItemDO.getStore());
         inventoryItem.setParentInventoryItemId(inventoryItemDO.getParentInventoryItemId());
         inventoryItem.setParentRatio(inventoryItemDO.getParentRatio());
         
         if (inventoryItem.getId() == null) {
            manager.persist(inventoryItem);
         }
         
         //update the components
         for (int i = 0; i < components.size(); i++) {
            
            InventoryComponentDO componentDO = (InventoryComponentDO) components.get(i);
             InventoryComponent component = null;
             
             if (componentDO.getId() == null)
                 component = new InventoryComponent();
             else
                 component = manager.find(InventoryComponent.class, componentDO.getId());

             if(componentDO.getDelete() && component.getId() != null){
                //delete the component record from the database
                manager.remove(component);
                
             }else{
                //update the record
                component.setComponentId(componentDO.getComponentNameId());
                component.setInventoryItemId(inventoryItem.getId());
                component.setQuantity(componentDO.getQuantity());
                    
                if (component.getId() == null) {
                    manager.persist(component);
                }
            }
         }
         
         //update note
         Note note = null;
         //we need to make sure the note is filled out...
         if(noteDO.getText() != null || noteDO.getSubject() != null){
            note = new Note();
             note.setIsExternal(noteDO.getIsExternal());
             note.setReferenceId(inventoryItem.getId());
             note.setReferenceTableId(invItemRefTableId);
             note.setSubject(noteDO.getSubject());
             note.setSystemUserId(lockBean.getSystemUserId());
             note.setText(noteDO.getText());
            note.setTimestamp(Datetime.getInstance());
        }
         
         //insert into note table if necessary
         if(note != null && note.getId() == null){
            manager.persist(note);
         }
         
         //update manufacturing note
         Note manNote = null;
         //we need to make sure the note is filled out...
         if(manufacturingNote.getText() != null){
             if (manufacturingNote.getId() == null)
                 manNote = new Note();
             else
                 manNote = manager.find(Note.class, manufacturingNote.getId());
             
             manNote.setIsExternal(manufacturingNote.getIsExternal());
             manNote.setReferenceId(inventoryItem.getId());
             manNote.setReferenceTableId(invItemManufacRefTableId);
             manNote.setSystemUserId(lockBean.getSystemUserId());
             manNote.setText(manufacturingNote.getText());
             manNote.setTimestamp(Datetime.getInstance());
        }
         
         //insert into note table if necessary
         if(manNote != null && manNote.getId() == null){
            manager.persist(manNote);
         }

         lockBean.giveUpLock(invItemRefTableId, inventoryItem.getId()); 
         
		return inventoryItem.getId();
	}

    public List inventoryComponentAutoCompleteLookupByName(String itemName, Integer storeId, String currentName, int maxResults){
        Query query = null;
        if(storeId == null && currentName == null){
            query = manager.createNamedQuery("InventoryItem.AutocompleteByName");
            query.setParameter("name",itemName);
            
        } else {
            query = manager.createNamedQuery("InventoryItem.AutocompleteByNameStoreCurrentName");
            query.setParameter("name",itemName);
            query.setParameter("store",storeId);
            query.setParameter("currentName",currentName);
            
        }
        
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    public List inventoryItemStoreAutoCompleteLookupByName(String itemName, int maxResults, boolean limitToMainStore, boolean allowSubAssembly){
        Query query = null;
        if(allowSubAssembly && !limitToMainStore)
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreByNameReceipt");
        else if(allowSubAssembly && limitToMainStore)
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreByNameMainStoreSubItems");
        else 
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreByName");
           
        query.setParameter("name",itemName);
        
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    public List inventoryItemStoreLocAutoCompleteLookupByName(String itemName, int maxResults, boolean limitToMainStore, boolean allowSubAssembly) {
        Query query = null;
    
        if(allowSubAssembly && !limitToMainStore)
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreLocByNameSubItems");
        else if(!allowSubAssembly && !limitToMainStore)
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreLocByName");
        else
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreLocByName");
        
        query.setParameter("name",itemName);
        
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    public List inventoryItemStoreChildAutoCompleteLookupByName(String itemName, Integer parentId, Integer id, int maxResults){
        Query query = null;
        if(parentId != null){
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreChildrenByNameParentId");
            query.setParameter("id", parentId);
        }else if(id != null){
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreChildrenByNameId");
            query.setParameter("id", id);
        }
        query.setParameter("name", itemName);
        
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    public List inventoryAdjItemAutoCompleteLookupByName(String itemName, Integer storeId, int maxResults){
        Query query = manager.createNamedQuery("InventoryItem.AutocompleteItemByNameStore");
       
        query.setParameter("name",itemName);
        query.setParameter("store",storeId);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
    
    public List inventoryItemWithComponentsAutoCompleteLookupByName(String itemName, int maxResults){
        Query query = manager.createNamedQuery("InventoryItem.AutocompleteItemByNameKits");
        query.setParameter("name",itemName);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }

    public String getInventoryDescription(Integer inventoryItemId){
        Query query = null;
        query = manager.createNamedQuery("InventoryItem.DescriptionById");
        query.setParameter("id",inventoryItemId);
        
        return (String)query.getSingleResult();
    }
    
	public void validateInventoryItem(InventoryItemDO inventoryItemDO, List components) throws Exception{
	    ValidationErrorsList list = new ValidationErrorsList();
        
        //name required
        if(inventoryItemDO.getName() == null || "".equals(inventoryItemDO.getName())){
            list.add(new FieldErrorException("fieldRequiredException",invItemMap.getName()));
        }
        
        //store required
//       if(inventoryItemDO.getStore() == null){
  //          list.add(new FieldErrorException("fieldRequiredException",invItemMap.getStoreId()));
    //    }
        
        //dispensed units required
//        if(inventoryItemDO.getDispensedUnits() == null){
//            list.add(new FieldErrorException("fieldRequiredException",invItemMap.getDispensedUnitsId()));
//        }
        
        //item has to have unique name,store duplicates
        Query query = null;
        //its an add if its null
        if(inventoryItemDO.getId() == null){
            query = manager.createNamedQuery("InventoryItem.AddNameStoreCompare");
            query.setParameter("name", inventoryItemDO.getName());
//            query.setParameter("store", inventoryItemDO.getStore());
        }else{
            query = manager.createNamedQuery("InventoryItem.UpdateNameStoreCompare");
            query.setParameter("name", inventoryItemDO.getName());
//            query.setParameter("store", inventoryItemDO.getStore());
            query.setParameter("id",inventoryItemDO.getId());
        }
        
        if(query.getResultList().size() > 0)
            list.add(new FieldErrorException("inventoryItemNameUniqueException",invItemMap.getName()));
        
        //for(int i=0; i<components.size();i++)            
          //  validateInventoryComponent((InventoryComponentDO)components.get(i), inventoryItemDO.getStore(), i, list);
        
        if(list.size() > 0)
            throw list;
	}
    
    private void validateInventoryComponent(InventoryComponentDO componentDO, Integer inventoryItemStoreId, int rowIndex, ValidationErrorsList exceptionList){
        //if the component is flagged for deletion dont validate
        if(componentDO.getDelete())
            return;
        
        //component required
        if(componentDO.getComponentNameId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, invItemMap.INVENTORY_COMPONENT.getComponentId()));
        }
        
        //quantity required
        if(componentDO.getQuantity() == 0){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, invItemMap.INVENTORY_COMPONENT.getQuantity()));
        }
        
        //components store needs to match the inventory items store
        if(componentDO.getComponentNameId() != null){
            Query query = null;
                query = manager.createNamedQuery("InventoryItem.ValidateComponentWithItemStore");
                query.setParameter("id", componentDO.getComponentNameId());
                query.setParameter("store", inventoryItemStoreId);
            
            if(query.getResultList().size() == 0)
                exceptionList.add(new TableFieldErrorException("inventoryComponentStoreException", rowIndex, invItemMap.INVENTORY_COMPONENT.getComponentId()));
        }
    }
*/
