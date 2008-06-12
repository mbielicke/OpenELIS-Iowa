package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryComponentDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.entity.InventoryComponent;
import org.openelis.entity.InventoryItem;
import org.openelis.entity.Note;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.meta.InventoryComponentItemMeta;
import org.openelis.meta.InventoryComponentMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.InventoryItemNoteMeta;
import org.openelis.meta.InventoryLocationMeta;
import org.openelis.meta.InventoryLocationStorageLocationMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.util.Datetime;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventory-select")
public class InventoryItemBean implements InventoryItemRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    
	@EJB
	private SystemUserUtilLocal sysUser;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            
        }
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
	public InventoryItemDO getInventoryItemAndLock(Integer inventoryItemId) throws Exception {
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_item");
        lockBean.getLock((Integer)query.getSingleResult(),inventoryItemId);
        
        return getInventoryItem(inventoryItemId);
	}

	public InventoryItemDO getInventoryItemAndUnlock(Integer inventoryItemId) {
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
//		organization reference table id
    	Query refIdQuery = manager.createNamedQuery("getTableId");
    	refIdQuery.setParameter("name", "inventory_item");
        Integer inventoryItemReferenceId = (Integer)refIdQuery.getSingleResult();
        
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        InventoryItemMeta itemMeta = InventoryItemMeta.getInstance();
        InventoryComponentMeta componentMeta = InventoryComponentMeta.getInstance();
        InventoryComponentItemMeta componentItemMeta = InventoryComponentItemMeta.getInstance();
        InventoryLocationMeta locationMeta = InventoryLocationMeta.getInstance();   
        InventoryLocationStorageLocationMeta locationStorageLocationMeta = InventoryLocationStorageLocationMeta.getInstance();
        InventoryItemNoteMeta noteMeta = InventoryItemNoteMeta.getInstance();

        qb.addMeta(new Meta[]{itemMeta, componentMeta, componentItemMeta, locationMeta, locationStorageLocationMeta, noteMeta});
 
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+itemMeta.ID+", "+itemMeta.NAME + ") ");
        qb.addTable(itemMeta);
        
        //I add this everytime because if they query from the locations table the location meta needs to go first
        //and sometimes it doesnt.  This makes this table come first everytime.
        qb.addTable(locationMeta);

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(itemMeta.NAME);
        
        //TODO we need to put these values in cache to remove this from where statement
        if(qb.hasTable(noteMeta.getTable()))
        	qb.addWhere(noteMeta.REFERENCE_TABLE+" = "+inventoryItemReferenceId+" or "+noteMeta.REFERENCE_TABLE+" is null");
        
        if(qb.hasTable(componentItemMeta.getTable()))
            qb.addTable(componentMeta);
        
        sb.append(qb.getEJBQL());
        
        System.out.println("******QUERY: ["+sb.toString()+"]*********************");

         Query query = manager.createQuery(sb.toString());
        
         if(first > -1 && max > -1)
        	 query.setMaxResults(first+max);
         
//       ***set the parameters in the query
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
    
    public List inventoryItemStoreLocAutoCompleteLookupByName(String itemName, int maxResults, boolean withLocation) {
        Query query = null;
        if(withLocation)
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreLocByName");
        else
            query = manager.createNamedQuery("InventoryItem.AutocompleteItemStoreByName");
        
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
            exceptionList.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.NAME));
        }
        
        //store required
        if(inventoryItemDO.getStore() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.STORE_ID));
        }
        
        //purchased units required
        if(inventoryItemDO.getPurchasedUnits() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.PURCHASED_UNITS_ID));
        }
        
        //dispensed units required
        if(inventoryItemDO.getDispensedUnits() == null){
            exceptionList.add(new FieldErrorException("fieldRequiredException",InventoryItemMeta.DISPENSED_UNITS_ID));
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
            exceptionList.add(new FieldErrorException("inventoryItemNameUniqueException",InventoryItemMeta.NAME));
    }
    
    private void validateInventoryComponent(InventoryComponentDO componentDO, Integer inventoryItemStoreId, int rowIndex, List exceptionList){
        //component required
        if(componentDO.getComponentNameId() == null){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryComponentMeta.COMPONENT_ID));
        }
        
        //quantity required
        if(componentDO.getQuantity() == 0){
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryComponentMeta.QUANTITY));
        }
        
        //components store needs to match the inventory items store
        if(componentDO.getComponentNameId() != null){
            Query query = null;
                query = manager.createNamedQuery("InventoryComponent.ValidateComponentWithItemStore");
                query.setParameter("id", componentDO.getId());
                query.setParameter("store", inventoryItemStoreId);
            
            if(query.getResultList().size() == 0)
                exceptionList.add(new TableFieldErrorException("inventoryComponentStoreException", rowIndex, InventoryComponentMeta.COMPONENT_ID));
        }
    }
}
