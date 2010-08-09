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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.domain.InventoryAdjustmentViewDO;
import org.openelis.entity.InventoryAdjustment;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.InventoryAdjustmentLocal;
import org.openelis.meta.InventoryAdjustmentMeta;
import org.openelis.remote.InventoryAdjustmentRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryadjustment-select")
public class InventoryAdjustmentBean implements InventoryAdjustmentRemote, InventoryAdjustmentLocal{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB
    private SystemUserUtilLocal systemUserBean;        
    
    private static final InventoryAdjustmentMeta meta = new InventoryAdjustmentMeta();
    
    public InventoryAdjustmentViewDO fetchById(Integer id) throws Exception {
        Query query;
        InventoryAdjustmentViewDO data;
        SystemUserDO user;
        
        query = manager.createNamedQuery("InventoryAdjustment.FetchById");
        query.setParameter("id", id);
        try {
            data = (InventoryAdjustmentViewDO)query.getSingleResult();            
            user = systemUserBean.getSystemUser(data.getSystemUserId());
            data.setSystemUserName(user.getLoginName());
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<InventoryAdjustmentDO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.InventoryAdjustmentDO(" +
                          InventoryAdjustmentMeta.getId() + ", " +
                          InventoryAdjustmentMeta.getDescription() + ", " +
                          InventoryAdjustmentMeta.getSystemUserId() + ", " +
                          InventoryAdjustmentMeta.getAdjustmentDate() +  ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(InventoryAdjustmentMeta.getId() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<InventoryAdjustmentDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<InventoryAdjustmentDO>)list;
    }
    
    public InventoryAdjustmentViewDO add(InventoryAdjustmentViewDO data) throws Exception {
        InventoryAdjustment entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new InventoryAdjustment();     
        entity.setDescription(data.getDescription());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setAdjustmentDate(data.getAdjustmentDate());
               
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public InventoryAdjustmentViewDO update(InventoryAdjustmentViewDO data) throws Exception {
        InventoryAdjustment entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(InventoryAdjustment.class, data.getId());
        entity.setDescription(data.getDescription());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setAdjustmentDate(data.getAdjustmentDate());
        
        return data;
    }

    public void validate(InventoryAdjustmentViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getDescription()));
        
        if (DataBaseUtil.isEmpty(data.getAdjustmentDate()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getAdjustmentDate()));
        
        if (DataBaseUtil.isEmpty(data.getSystemUserId()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMeta.getSystemUserId()));
               
        if (list.size() > 0)
            throw list;                
    }   
    
    /*public InventoryAdjustmentAddAutoFillDO getAddAutoFillValues() throws Exception {
        InventoryAdjustmentAddAutoFillDO autoFillDO = new InventoryAdjustmentAddAutoFillDO();
        //adjustment date
        autoFillDO.setAdjustmentDate(new Date());

        //system user information
        /*SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        autoFillDO.setSystemUser(systemUserDO.getLoginName());
        autoFillDO.setSystemUserId(systemUserDO.getId());
        
        return autoFillDO;
    }*/

    public List getChildRecords(Integer inventoryAdjustmentId) {
        Query query = manager.createNamedQuery("InventoryXAdjust.InventoryXAdjust");
        query.setParameter("id", inventoryAdjustmentId);
        
        return query.getResultList();
    }
    
    public List getChildRecordsAndLock(Integer inventoryAdjustmentId) throws Exception {
        List children = getChildRecords(inventoryAdjustmentId);
        
        lockRecords(children, false);
        
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
        
        SystemUserDO sysUserDO = systemUserBean.getSystemUser(adjDO.getSystemUserId());
        //adjDO.setSystemUser(sysUserDO.getLoginName());       
        
        return adjDO;
    }

    /*public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }     
    }*/
/*
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception{
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
        lockRecords(children, true);
        
        //validate the records
        validateAdjustment(inventoryAdjustmentDO, children);
        
        InventoryAdjustment inventoryAdjustment = null;
    
         if (inventoryAdjustmentDO.getId() == null)
             inventoryAdjustment = new InventoryAdjustment();
        else
            inventoryAdjustment = manager.find(InventoryAdjustment.class, inventoryAdjustmentDO.getId());

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

            if (childDO.getId() == null)
                transaction = new InventoryXAdjust();
            else
                transaction = manager.find(InventoryXAdjust.class, childDO.getId());

            /*if(!childDO.getDelete()){
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
    }*/

    public void validateAdjustment(InventoryAdjustmentDO inventoryAdjustmentDO, List children) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
/*
        //desc required
        if(inventoryAdjustmentDO.getDescription() == null || "".equals(inventoryAdjustmentDO.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMetaMap.getDescription()));
        
        //adj date required
        if(inventoryAdjustmentDO.getAdjustmentDate() == null)
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMetaMap.getAdjustmentDate()));
        
        //system user name required
        if(inventoryAdjustmentDO.getSystemUserId() == null)
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMetaMap.getSystemUserId()));
        
        //store id required
        if(inventoryAdjustmentDO.getStoreId() == null)
            list.add(new FieldErrorException("fieldRequiredException",InventoryAdjustmentMetaMap.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId()));
        
        for(int i=0; i<children.size(); i++)
            validateAdjItem((InventoryAdjustmentChildDO)children.get(i), i, list);
        
        if(list.size() > 0)
            throw list;
*/
    }
    
    /* private void validateAdjItem(InventoryAdjustmentChildDO childDO, int rowIndex, ValidationErrorsList exceptionList){
        //loc required

        if(childDO.getLocationId() == null)
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryAdjustmentMetaMap.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.getId()));
        
        //count required
        if(childDO.getPhysicalCount() == null)
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, InventoryAdjustmentMetaMap.TRANS_ADJUSTMENT_LOCATION_META.getPhysicalCount()));

    }*/
    
    public List getInventoryitemDataAndLockLoc(Integer inventoryLocationId, Integer oldLocId, Integer storeId) throws Exception{
        //if there is an old lock we need to unlock this record
        //if(oldLocId != null)
          //  lockBean.giveUpLock(invLocRefTableId, oldLocId);
        
        Query query = manager.createNamedQuery("InventoryLocation.LocationInfoForAdjustmentFromId");
        query.setParameter("id", inventoryLocationId);
        query.setParameter("store", storeId);
        List resultList = query.getResultList();
        
        //we need to only get the lock if we bring something back in the query.
        //if they dont get anything back we can assume they entered an invalid loc id
        //if(resultList != null && resultList.size() > 0)
          //  lockBean.getLock(invLocRefTableId, inventoryLocationId);
        
        return resultList;
    }
    
    private void lockRecords(List childRows, boolean validate) throws Exception{
        if(childRows.size() == 0)
            return;
        
        for(int i=0; i<childRows.size(); i++){
        
            //InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childRows.get(i);
        
            /*if(childDO.getLocationId() != null){
                if(validate)
                    lockBean.validateLock(invLocRefTableId, childDO.getLocationId());
                else
                    lockBean.getLock(invLocRefTableId, childDO.getLocationId());
            }*/
        }
    }
    
    private void unlockRecords(List childRows){
        if(childRows.size() == 0)
            return;
        
        for(int i=0; i<childRows.size(); i++){
        
            //InventoryAdjustmentChildDO childDO = (InventoryAdjustmentChildDO)childRows.get(i);
        
            //if(childDO.getLocationId() != null)
              //  lockBean.giveUpLock(invLocRefTableId, childDO.getLocationId());
        }
    }
}
