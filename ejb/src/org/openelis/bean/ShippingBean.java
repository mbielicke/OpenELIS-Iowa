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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.entity.Shipping;
import org.openelis.entity.ShippingItem;
import org.openelis.entity.ShippingTracking;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.persistence.JBossCachingManager;
import org.openelis.remote.ShippingRemote;
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
//@RolesAllowed("qaevent-select")
public class ShippingBean implements ShippingRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private SystemUserUtilLocal sysUser;
    
    private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }

    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception {
        ShippingAddAutoFillDO autoFillDO = new ShippingAddAutoFillDO();
        //status integer
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", "shipping_status_processed");

        //default to shipped status
        autoFillDO.setStatus((Integer)query.getSingleResult());

        //default to today
        autoFillDO.setProcessedDate(new Date());

        //default to current user
        SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        autoFillDO.setProcessedBy(systemUserDO.getLoginName());
        autoFillDO.setSystemUserId(systemUserDO.getId());
        
        
        return autoFillDO;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(ShippingMeta);
        
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+ShippingMeta.getId()+") ");
       
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(ShippingMeta.getId());
       
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

    public Integer updateShipment(ShippingDO shippingDO, List<ShippingItemDO> shippingItems, List<ShippingTrackingDO> trackingNumbers) throws Exception {
        //shipping reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "shipping");
        Integer shippingReferenceId = (Integer)query.getSingleResult();
        
        if(shippingDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(shippingReferenceId,shippingDO.getId());
        }
        
         manager.setFlushMode(FlushModeType.COMMIT);
         Shipping shipping = null;
    
         if (shippingDO.getId() == null)
            shipping = new Shipping();
        else
            shipping = manager.find(Shipping.class, shippingDO.getId());

        //validate the organization record and its address
        //List exceptionList = new ArrayList();
        //validateOrganizationAndAddress(organizationDO, exceptionList);
        //if(exceptionList.size() > 0){
        //    throw (RPCException)exceptionList.get(0);
        //}
        
        //update shipping record
         shipping.setCost(shippingDO.getCost());
         shipping.setNumberOfPackages(shippingDO.getNumberOfPackages());
         shipping.setProcessedById(shippingDO.getProcessedById());
         shipping.setProcessedDate(shippingDO.getProcessedDate());
         shipping.setShippedDate(shippingDO.getShippedDate());
         shipping.setShippedFromId(shippingDO.getShippedFromId());
         shipping.setShippedMethodId(shippingDO.getShippedMethodId());
         shipping.setShippedToId(shippingDO.getShippedToId());
         shipping.setStatusId(shippingDO.getStatusId());
                
        if (shipping.getId() == null)
            manager.persist(shipping);
        
        //update tracking numbers
        if(trackingNumbers != null) {
            for (ShippingTrackingDO trackingDO : trackingNumbers) {
                ShippingTracking shippingTracking = null;
            
                if (trackingDO.getId() == null)
                    shippingTracking = new ShippingTracking();
                else
                    shippingTracking = manager.find(ShippingTracking.class, trackingDO.getId());

                if(trackingDO.isDelete() && shippingTracking.getId() != null){
                    //delete the tracking number record from the database
                    manager.remove(shippingTracking);
                
                }else{
                    shippingTracking.setShippingId(shipping.getId());
                    shippingTracking.setTrackingNumber(trackingDO.getTrackingNumber());
                    
                    if (shippingTracking.getId() == null) {
                        manager.persist(shippingTracking);
                    }
                }
            }
        }
        
        //update shipping items
        if(shippingItems != null) {
            for (ShippingItemDO itemDO : shippingItems) {
                ShippingItem shippingItem = null;
            
                if (itemDO.getId() == null)
                    shippingItem = new ShippingItem();
                else
                    shippingItem = manager.find(ShippingItem.class, itemDO.getId());

                shippingItem.setReferenceId(itemDO.getReferenceId());
                shippingItem.setReferenceTableId(itemDO.getReferenceTableId());
                shippingItem.setShippingId(shipping.getId());
                    
                if (shippingItem.getId() == null) {
                    manager.persist(shippingItem);
                }
            }
        }
        
        return shipping.getId();
    }

    public ShippingDO getShipment(Integer shippingId) {
        Query query = manager.createNamedQuery("Shipping.Shipping");
        query.setParameter("id", shippingId);
        ShippingDO shippingDO = (ShippingDO) query.getResultList().get(0);// getting shipping record

        SystemUserDO sysUserDO = sysUser.getSystemUser(shippingDO.getProcessedById());
        shippingDO.setProcessedBy(sysUserDO.getLoginName());       
        
        return shippingDO;
    }

    public ShippingDO getShipmentAndLock(Integer shippingId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "shipping");
        lockBean.getLock((Integer)query.getSingleResult(),shippingId);
        
        return getShipment(shippingId);
    }

    public ShippingDO getShipmentAndUnlock(Integer shippingId) {
        //unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "shipping");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),shippingId);
            
        return getShipment(shippingId);
    }
    
    public List getTrackingNumbers(Integer shippingId) {
        Query query = manager.createNamedQuery("ShippingTracking.Tracking");
        query.setParameter("id", shippingId);
        return query.getResultList();

    }
    
    public List getShippingItems(Integer shippingId) {
        Query query = manager.createNamedQuery("ShippingItem.ShippingItem");
        query.setParameter("id", shippingId);
        
        List resultList = query.getResultList();
        
        Integer orderItemReferenceTableId = (Integer)JBossCachingManager.getElement("openelis","beans", "referenceTableOrderItemId"); 
        Integer sampleItemReferenceTableId = (Integer)JBossCachingManager.getElement("openelis","beans", "referenceTableSampleItemId");
        
        if(orderItemReferenceTableId == null){
            query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "order_item");
            orderItemReferenceTableId = (Integer)query.getSingleResult();
            JBossCachingManager.putElement("openelis", "beans", "referenceTableOrderItemId", orderItemReferenceTableId);
        }
        
        if(sampleItemReferenceTableId == null){
            query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "sample_item");
            sampleItemReferenceTableId = (Integer)query.getSingleResult();
            JBossCachingManager.putElement("openelis", "beans", "referenceTableSampleItemId", sampleItemReferenceTableId);
        }        
        
        for(int i=0; i<resultList.size(); i++){
            ShippingItemDO itemDO = (ShippingItemDO)resultList.get(i);

            if(itemDO.getReferenceTableId().equals(orderItemReferenceTableId)){
                query = manager.createNamedQuery("OrderItem.OrderItemName");
                query.setParameter("id", itemDO.getReferenceId());
                itemDO.setItemDescription((String)query.getSingleResult());
            
            }else if(itemDO.getReferenceTableId().equals(sampleItemReferenceTableId)){
                
            }
        }
        
        return resultList;
    }

    public List validateForAdd(ShippingDO shippingDO, List shippingItems, List trackngNumbers) {
        List exceptionList = new ArrayList();
        return exceptionList;
    }

    public List validateForUpdate(ShippingDO shippingDO, List shippingItems, List trackngNumbers) {
        List exceptionList = new ArrayList();
        return exceptionList;
    }
}
