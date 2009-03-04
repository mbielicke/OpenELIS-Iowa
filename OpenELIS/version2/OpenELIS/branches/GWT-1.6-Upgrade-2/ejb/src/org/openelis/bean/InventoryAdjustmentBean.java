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
import java.util.Date;
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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.InventoryAdjustmentAddAutoFillDO;
import org.openelis.domain.InventoryAdjustmentChildDO;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.entity.InventoryAdjustment;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryXAdjust;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class),
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("inventoryadjustment-select")
public class InventoryAdjustmentBean implements InventoryAdjustmentRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final InventoryAdjustmentMetaMap InventoryAdjustmentMetaMap = new InventoryAdjustmentMetaMap();
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
    public InventoryAdjustmentAddAutoFillDO getAddAutoFillValues() throws Exception {
        InventoryAdjustmentAddAutoFillDO autoFillDO = new InventoryAdjustmentAddAutoFillDO();
        //adjustment date
        autoFillDO.setAdjustmentDate(new Date());

        //system user information
        SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        autoFillDO.setSystemUser(systemUserDO.getLoginName());
        autoFillDO.setSystemUserId(systemUserDO.getId());
        
        return autoFillDO;
    }

    public List getChildRecords(Integer inventoryAdjustmentId) {
        Query query = manager.createNamedQuery("InventoryXAdjust.InventoryXAdjust");
        query.setParameter("id", inventoryAdjustmentId);
        
        return query.getResultList();
    }
    
    public List getChildRecordsAndLock(Integer inventoryAdjustmentId) throws Exception {
        List children = getChildRecords(inventoryAdjustmentId);
        
        lockRecords(children);
        
        return children;
    }
    
    public List getChildRecordsAndUnlock(Integer inventoryAdjustmentId){
        List children = getChildRecords(inventoryAdjustmentId);
        
        unlockRecords(children);
        
        return children;
    }

    public InventoryAdjustmentDO getInventoryAdjustment(Integer inventoryAdjustmentId) {
        Query query = manager.createNamedQuery("InventoryAdjustment.InventoryAdjustment");
        query.setParameter("id", inventoryAdjustmentId);
        InventoryAdjustmentDO adjDO = (InventoryAdjustmentDO) query.getResultList().get(0);
        
        SystemUserDO sysUserDO = sysUser.getSystemUser(adjDO.getSystemUserId());
        adjDO.setSystemUser(sysUserDO.getLoginName());       
        
        return adjDO;
    }

    public InventoryAdjustmentDO getInventoryAdjustmentAndLock(Integer inventoryAdjustmentId) throws Exception {
        //lock the entity
        Query lockQuery = manager.createNamedQuery("getTableId");
        lockQuery.setParameter("name", "inventory_adjustment");
        lockBean.getLock((Integer)lockQuery.getSingleResult(),inventoryAdjustmentId);
            
        return getInventoryAdjustment(inventoryAdjustmentId);
    }

    public InventoryAdjustmentDO getInventoryAdjustmentAndUnlock(Integer inventoryAdjustmentId) {
        //unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "inventory_adjustment");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),inventoryAdjustmentId);
            
        return getInventoryAdjustment(inventoryAdjustmentId);
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

    public List query(HashMap fields, int first, int max) throws Exception{
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(InventoryAdjustmentMetaMap);
        qb.setSelect("distinct new org.openelis.domain.IdNameDateDO("+InventoryAdjustmentMetaMap.getId()+", "+InventoryAdjustmentMetaMap.getDescription()+", " +
                         InventoryAdjustmentMetaMap.getAdjustmentDate()+") ");

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(InventoryAdjustmentMetaMap.getAdjustmentDate()+" , "+InventoryAdjustmentMetaMap.getId());

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

    public Integer updateInventoryAdjustment(InventoryAdjustmentDO inventoryAdjustmentDO, List children) throws Exception {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //lock the necessary records
        lockRecords(children);
        
        InventoryAdjustment inventoryAdjustment = null;
    
         if (inventoryAdjustmentDO.getId() == null)
             inventoryAdjustment = new InventoryAdjustment();
        else
            inventoryAdjustment = manager.find(InventoryAdjustment.class, inventoryAdjustmentDO.getId());

        //validate the inventory adjustment record
        //List exceptionList = new ArrayList();
        //validateOrganizationAndAddress(organizationDO, exceptionList);
        //if(exceptionList.size() > 0){
        //    throw (RPCException)exceptionList.get(0);
        //}
        
        //update inventory adjustment
         inventoryAdjustment.setDescription(inventoryAdjustmentDO.getDescription());
         inventoryAdjustment.setSystemUserId(inventoryAdjustmentDO.getSystemUserId());
         inventoryAdjustment.setAdjustmentDate(inventoryAdjustmentDO.getAdjustmentDate());
                
        if (inventoryAdjustment.getId() == null) {
            manager.persist(inventoryAdjustment);
        }
        
        //update child records
        for (int i=0; i<children.size();i++) {
            InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO) children.get(i);
            InventoryXAdjust transaction = null;
            
            //validate the child record
            //exceptionList = new ArrayList();
            //validateContactAndAddress(contactDO, i, exceptionList);
            //if(exceptionList.size() > 0){
            //    throw (RPCException)exceptionList.get(0);
            //}
            
            if (childDO.getId() == null)
                transaction = new InventoryXAdjust();
            else
                transaction = manager.find(InventoryXAdjust.class, childDO.getId());

            if(!childDO.getDelete()){
                transaction.setInventoryAdjustmentId(inventoryAdjustment.getId());
                transaction.setInventoryLocationId(childDO.getLocationId());
                transaction.setPhysicalCount(childDO.getPhysicalCount());
                transaction.setQuantity(childDO.getAdjustedQuantity());
                
                //we need to update the location record
                InventoryLocation loc = manager.find(InventoryLocation.class, childDO.getLocationId());
                loc.setQuantityOnhand(childDO.getPhysicalCount());
                    
                if (transaction.getId() == null) {
                    manager.persist(transaction);
                }
           }else{
               manager.remove(transaction);
           }
        }
        
        unlockRecords(children);
   
        return inventoryAdjustment.getId();        
    }

    public List validateForAdd(InventoryAdjustmentDO inventoryAdjustmentDO, List children) {
        List exceptionList = new ArrayList();
        return exceptionList;
    }

    public List validateForUpdate(InventoryAdjustmentDO inventoryAdjustmentDO, List children) {
        List exceptionList = new ArrayList();
        return exceptionList;
    }
    
    public List getInventoryitemData(Integer inventoryLocationId, Integer storeId){
        Query query = manager.createNamedQuery("InventoryLocation.LocationInfoForAdjustmentFromId");
        query.setParameter("id", inventoryLocationId);
        query.setParameter("store", storeId);
        
        return  query.getResultList();
    }
    
    private void lockRecords(List childRows) throws Exception{
        if(childRows.size() == 0)
            return;
        
        Integer inventoryLocationTableId = null;
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_location");
        inventoryLocationTableId = (Integer)query.getSingleResult();
        
        for(int i=0; i<childRows.size(); i++){
        
            InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childRows.get(i);
        
            if(childDO.getLocationId() != null)
                lockBean.getLock(inventoryLocationTableId, childDO.getLocationId());
        }
    }
    
    private void unlockRecords(List childRows){
        if(childRows.size() == 0)
            return;
        
        Integer inventoryLocationTableId = null;
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "inventory_location");
        inventoryLocationTableId = (Integer)query.getSingleResult();
        
        for(int i=0; i<childRows.size(); i++){
        
            InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childRows.get(i);
        
            if(childDO.getLocationId() != null)
                lockBean.giveUpLock(inventoryLocationTableId, childDO.getLocationId());
        }
    }
}
