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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.NoteDO;
import org.openelis.entity.InventoryComponent;
import org.openelis.entity.InventoryItem;
import org.openelis.entity.Note;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("inventory-select")
public class InventoryItemBean implements InventoryItemRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
   
	@EJB private SystemUserLocal userLocal;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final InventoryItemMetaMap invItemMap = new InventoryItemMetaMap();
   
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
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

        for(int i=0; i<notes.size(); i++){
            NoteDO noteDO = (NoteDO)notes.get(i);
            SystemUserDO userDO = userLocal.getSystemUser(noteDO.getSystemUserId());
            noteDO.setSystemUser(userDO.getLoginName());
        }
        
        return notes;
	}
	
	public NoteDO getInventoryMaunfacturingRecipe(Integer inventoryItemId){
	    Query query = null;
        
        query = manager.createNamedQuery("InventoryItem.Manufacturing"); 
        query.setParameter("id", inventoryItemId);
        
        List notes = query.getResultList();// getting list of noteDOs from the item id

        if(notes != null && notes.size() > 0)
            return (NoteDO)notes.get(0);
        else 
            return null;
	}

	public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        
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
	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, NoteDO noteDO, NoteDO manufacturingNote) throws Exception {
        //inventory item reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_item");
        Integer inventoryItemReferenceId = (Integer)query.getSingleResult();
        
      //inventory item manufacturing reference table id
        query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_item_manufacturing");
        Integer inventoryItemManRefId = (Integer)query.getSingleResult();
        
        if(inventoryItemDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.validateLock(inventoryItemReferenceId,inventoryItemDO.getId());
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
         inventoryItem.setQuantityMaxLevel(inventoryItemDO.getQuantityMaxLevel());
         inventoryItem.setQuantityMinLevel(inventoryItemDO.getQuantityMinLevel());
         inventoryItem.setQuantityToReorder(inventoryItemDO.getQuantityToReorder());
         inventoryItem.setStoreId(inventoryItemDO.getStore());
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
         System.out.println("update note");
         Note note = null;
         //we need to make sure the note is filled out...
         if(noteDO.getText() != null || noteDO.getSubject() != null){
            note = new Note();
             note.setIsExternal(noteDO.getIsExternal());
             note.setReferenceId(inventoryItem.getId());
             note.setReferenceTableId(inventoryItemReferenceId);
             note.setSubject(noteDO.getSubject());
             note.setSystemUserId(lockBean.getSystemUserId());
             note.setText(noteDO.getText());
            note.setTimestamp(Datetime.getInstance());
        }
         
         //insert into note table if necessary
         if(note != null && note.getId() == null){
            manager.persist(note);
         }
         
         System.out.println("update manu");
         //update manufacturing note
         Note manNote = null;
         //we need to make sure the note is filled out...
         if(manufacturingNote.getText() != null){
             System.out.println("1");
             if (manufacturingNote.getId() == null){
              System.out.println("2a");   
                 manNote = new Note();
             }else{
                 manNote = manager.find(Note.class, manufacturingNote.getId());
                 System.out.println("2b");
             }
             System.out.println("3");
             manNote.setIsExternal(manufacturingNote.getIsExternal());
             System.out.println("4");
             manNote.setReferenceId(inventoryItem.getId());
             System.out.println("5");
             manNote.setReferenceTableId(inventoryItemManRefId);
             System.out.println("6");
             manNote.setSystemUserId(lockBean.getSystemUserId());
             System.out.println("7");
             manNote.setText(manufacturingNote.getText());
             System.out.println("8");
             manNote.setTimestamp(Datetime.getInstance());
             System.out.println("9");
        }
         
         //insert into note table if necessary
         if(manNote != null && manNote.getId() == null){
            manager.persist(manNote);
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
        if(inventoryItemDO.getStore() == null){
            list.add(new FieldErrorException("fieldRequiredException",invItemMap.getStoreId()));
        }
        
        //dispensed units required
        if(inventoryItemDO.getDispensedUnits() == null){
            list.add(new FieldErrorException("fieldRequiredException",invItemMap.getDispensedUnitsId()));
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
            list.add(new FieldErrorException("inventoryItemNameUniqueException",invItemMap.getName()));
        
        for(int i=0; i<components.size();i++)            
            validateInventoryComponent((InventoryComponentDO)components.get(i), inventoryItemDO.getStore(), i, list);
        
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
}
