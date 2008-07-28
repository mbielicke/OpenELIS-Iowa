/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.bean;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.NoteDO;
import org.openelis.entity.InventoryComponent;
import org.openelis.entity.InventoryItem;
import org.openelis.entity.Note;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
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

@Stateless
@EJBs({
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class),
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("inventory-select")
public class InventoryItemBean implements InventoryItemRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
   
	private SystemUserUtilLocal sysUser;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final InventoryItemMetaMap invItemMap = new InventoryItemMetaMap();
   
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
 
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
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_item");
        lockBean.getLock((Integer)query.getSingleResult(),inventoryItemId);
        
        return getInventoryItem(inventoryItemId);
	}

	public InventoryItemDO getInventoryItemAndUnlock(Integer inventoryItemId, String session) {
		//unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "inventory_item");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),inventoryItemId);
       		
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
        
        query = manager.createNamedQuery("InventoryItem.Notes"); 
        query.setParameter("id", inventoryItemId);
        
        List notes = query.getResultList();// getting list of noteDOs from the item id

        return notes;
	}

	public Integer getSystemUserId() {
		try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	public List query(HashMap fields, int first, int max) throws Exception {
        
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        
        qb.setMeta(invItemMap);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+invItemMap.getId()+", "+invItemMap.getName()+") ");
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(invItemMap.getName());
        
        sb.append(qb.getEJBQL());
        
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

    @RolesAllowed("inventory-update")
	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, NoteDO noteDO) throws Exception {
        //inventory item reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_item");
        Integer inventoryItemReferenceId = (Integer)query.getSingleResult();
        
        if(inventoryItemDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(inventoryItemReferenceId,inventoryItemDO.getId());
        }
        
         manager.setFlushMode(FlushModeType.COMMIT);
         InventoryItem inventoryItem = null;
    
         if (inventoryItemDO.getId() == null)
             inventoryItem = new InventoryItem();
        else
            inventoryItem = manager.find(InventoryItem.class, inventoryItemDO.getId());
         
         //validate the inventory item record
         List exceptionList = new ArrayList();
         validateInventoryItem(inventoryItemDO, exceptionList);
         if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
         }
         
         //update the inventory record
         inventoryItem.setAverageCost(inventoryItemDO.getAveCost());
         inventoryItem.setAverageDailyUse(inventoryItemDO.getAveDailyUse());
         inventoryItem.setAverageLeadTime(inventoryItemDO.getAveLeadTime());
         inventoryItem.setCategoryId(inventoryItemDO.getCategory());
         inventoryItem.setDescription(inventoryItemDO.getDescription());
         inventoryItem.setDispensedUnitsId(inventoryItemDO.getDispensedUnits());
         inventoryItem.setIsActive(inventoryItemDO.getIsActive());
         inventoryItem.setIsBulk(inventoryItemDO.getIsBulk());
         inventoryItem.setIsLabor(inventoryItemDO.getIsLabor());
         inventoryItem.setIsLotMaintained(inventoryItemDO.getIsLotMaintained());
         inventoryItem.setIsNoInventory(inventoryItemDO.getIsNoInventory());
         inventoryItem.setIsNotForSale(inventoryItemDO.getIsNotForSale());
         inventoryItem.setIsReorderAuto(inventoryItemDO.getIsReorderAuto());
         inventoryItem.setIsSerialMaintained(inventoryItemDO.getIsSerialMaintained());
         inventoryItem.setIsSubAssembly(inventoryItemDO.getIsSubAssembly());
         inventoryItem.setName(inventoryItemDO.getName());
         inventoryItem.setProductUri(inventoryItemDO.getProductUri());
         inventoryItem.setPurchasedUnitsId(inventoryItemDO.getPurchasedUnits());
         inventoryItem.setQuantityMaxLevel(inventoryItemDO.getQuantityMaxLevel());
         inventoryItem.setQuantityMinLevel(inventoryItemDO.getQuantityMinLevel());
         inventoryItem.setQuantityToReorder(inventoryItemDO.getQuantityToReorder());
         inventoryItem.setStoreId(inventoryItemDO.getStore());
         
         if (inventoryItem.getId() == null) {
            manager.persist(inventoryItem);
         }
         
         //update the components
         for (int i = 0; i < components.size(); i++) {
            
            InventoryComponentDO componentDO = (InventoryComponentDO) components.get(i);
             InventoryComponent component = null;
             
             //validate the component records
             exceptionList = new ArrayList();
             validateInventoryComponent(componentDO, inventoryItemDO.getStore(), i, exceptionList);
             if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
             }
             
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
             note.setReferenceTableId(inventoryItemReferenceId);
             note.setSubject(noteDO.getSubject());
             note.setSystemUserId(getSystemUserId());
             note.setText(noteDO.getText());
            note.setTimestamp(Datetime.getInstance());
        }
         
         //insert into note table if necessary
         if(note != null && note.getId() == null){
            manager.persist(note);
         }

         lockBean.giveUpLock(inventoryItemReferenceId,inventoryItem.getId()); 
         
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

    public String getInventoryDescription(Integer inventoryItemId){
        Query query = null;
        query = manager.createNamedQuery("InventoryItem.DescriptionById");
        query.setParameter("id",inventoryItemId);
        
        return (String)query.getSingleResult();
    }
    
	public List validateForAdd(InventoryItemDO inventoryItemDO, List components) {
	    List exceptionList = new ArrayList();
        
        validateInventoryItem(inventoryItemDO, exceptionList);
        
        for(int i=0; i<components.size();i++){            
            InventoryComponentDO componentDO = (InventoryComponentDO) components.get(i);
            
            validateInventoryComponent(componentDO, inventoryItemDO.getStore(), i, exceptionList);
        }
        
        return exceptionList;
	}

	public List validateForUpdate(InventoryItemDO inventoryItemDO, List components) {
	    List exceptionList = new ArrayList();
        
        validateInventoryItem(inventoryItemDO, exceptionList);
        
        for(int i=0; i<components.size();i++){            
            InventoryComponentDO componentDO = (InventoryComponentDO) components.get(i);
            
            validateInventoryComponent(componentDO, inventoryItemDO.getStore(), i, exceptionList);
        }
        
        return exceptionList;
	}
    
    private void validateInventoryItem(InventoryItemDO inventoryItemDO, List exceptionList){
        //name required
        if(inventoryItemDO.getName() == null || "".equals(inventoryItemDO.getName())){
            exceptionList.add(new FieldErrorException("fieldRequiredException",invItemMap.getName()));
        }
        
        //store required
        if(inventoryItemDO.getStore() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",invItemMap.getStoreId()));
        }
        
        //purchased units required
        if(inventoryItemDO.getPurchasedUnits() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",invItemMap.getPurchasedUnitsId()));
        }
        
        //dispensed units required
        if(inventoryItemDO.getDispensedUnits() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",invItemMap.getDispensedUnitsId()));
        }
        
        //item has to have unique name,store duplicates
        Query query = null;
        //its an add if its null
        if(inventoryItemDO.getId() == null){
            query = manager.createNamedQuery("InventoryItem.AddNameStoreCompare");
            query.setParameter("name", inventoryItemDO.getName());
            query.setParameter("store", inventoryItemDO.getStore());
        }else{
            query = manager.createNamedQuery("InventoryItem.UpdateNameStoreCompare");
            query.setParameter("name", inventoryItemDO.getName());
            query.setParameter("store", inventoryItemDO.getStore());
            query.setParameter("id",inventoryItemDO.getId());
        }
        
        if(query.getResultList().size() > 0)
            exceptionList.add(new FieldErrorException("inventoryItemNameUniqueException",invItemMap.getName()));
    }
    
    private void validateInventoryComponent(InventoryComponentDO componentDO, Integer inventoryItemStoreId, int rowIndex, List exceptionList){
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
}
