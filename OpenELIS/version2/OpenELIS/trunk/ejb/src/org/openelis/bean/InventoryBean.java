package org.openelis.bean;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryItemDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.InventoryComponentMeta;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.InventoryItemNoteMeta;
import org.openelis.meta.InventoryLocationMeta;
import org.openelis.remote.InventoryRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventory-select")
public class InventoryBean implements InventoryRemote{

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
		//getInventoryComponents
		
		return null;
	}

	@RolesAllowed("inventory-update")
	public InventoryItemDO getInventoryItem(Integer inventoryItemId) {
		Query query = manager.createNamedQuery("getInventoryItem");
		query.setParameter("id", inventoryItemId);
		InventoryItemDO inventoryItem = (InventoryItemDO) query.getResultList().get(0);  // get the parent inventory item

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
		// TODO Auto-generated method stub
		return null;
	}

	public List getInventoryNotes(Integer inventoryItemId) {
		// TODO Auto-generated method stub
		return null;
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
        InventoryLocationMeta locationMeta = InventoryLocationMeta.getInstance();        
        InventoryItemNoteMeta noteMeta = InventoryItemNoteMeta.getInstance();

        qb.addMeta(new Meta[]{itemMeta, componentMeta, locationMeta, noteMeta});
 
        qb.setSelect("distinct "+itemMeta.ID+", "+itemMeta.NAME);
        qb.addTable(itemMeta);

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(itemMeta.NAME);
        
       // if(qb.hasTable(orgContactAddressMeta.getTable()))
        //	qb.addTable(orgContactMeta);
        
        //TODO we need to put these values in cache to remove this from where statement
        if(qb.hasTable(noteMeta.getTable())){
        	qb.addWhere(noteMeta.REFERENCE_TABLE+" = "+inventoryItemReferenceId+" or "+noteMeta.REFERENCE_TABLE+" is null");
        }
        
        sb.append(qb.getEJBQL());

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

	public Integer updateInventory(InventoryItemDO inventoryItemDO, List components, List locations) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List validateForAdd(InventoryItemDO inventoryItemDO, List components, List locations) {
		// TODO Auto-generated method stub
		return null;
	}

	public List validateForUpdate(InventoryItemDO inventoryItemDO, List components, List locations) {
		// TODO Auto-generated method stub
		return null;
	}
}
