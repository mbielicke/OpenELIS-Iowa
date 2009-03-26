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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.entity.InventoryLocation;
import org.openelis.entity.InventoryXUse;
import org.openelis.entity.Note;
import org.openelis.entity.Order;
import org.openelis.entity.OrderItem;
import org.openelis.entity.Shipping;
import org.openelis.entity.ShippingItem;
import org.openelis.entity.ShippingTracking;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.persistence.JBossCachingManager;
import org.openelis.remote.ShippingRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

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

@Stateless
@EJBs({
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class),
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("shipping-select")
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

    @RolesAllowed("shipping-update")
    public Integer updateShipment(ShippingDO shippingDO, List<ShippingItemDO> shippingItems, List<ShippingTrackingDO> trackingNumbers, DataModel unlockList, NoteDO shippingNote) throws Exception {
        validateShipping(shippingDO, shippingItems);
        
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
        
        //update shipping note
        Note note = null;
        
        if (shippingNote.getId() == null)
            note = new Note();
        else
            note = manager.find(Note.class, shippingNote.getId());
        
        note.setIsExternal(shippingNote.getIsExternal());
        note.setReferenceId(shipping.getId());
        note.setReferenceTableId(shippingReferenceId);
        note.setText(shippingNote.getText());
        
        if(note.getId() == null)
            note.setSystemUserId(lockBean.getSystemUserId());

        note.setTimestamp(Datetime.getInstance());
        
        if (note.getId() == null) {
            manager.persist(note);
        }
        
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
        
       if(shippingItems != null) {
           ArrayList listOfOrderIds = new ArrayList();
            for (ShippingItemDO itemDO : shippingItems) {
                //update shipping items
                ShippingItem shippingItem = null;
            
                if (itemDO.getId() == null)
                    shippingItem = new ShippingItem();
                else
                    shippingItem = manager.find(ShippingItem.class, itemDO.getId());

                shippingItem.setReferenceId(itemDO.getReferenceId());
                shippingItem.setReferenceTableId(itemDO.getReferenceTableId());
                shippingItem.setShippingId(shipping.getId());
                shippingItem.setQuantity(itemDO.getQuantity());
                shippingItem.setDescription(itemDO.getDescription());
                    
                if (shippingItem.getId() == null) {
                    manager.persist(shippingItem);
                }
            }
        }
        
       //unlock the order records
//       unlockOrderRecords(unlockList);
       
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

    @RolesAllowed("shipping-update")
    public ShippingDO getShipmentAndLock(Integer shippingId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "shipping");
        lockBean.getLock((Integer)query.getSingleResult(),shippingId);
        
        return getShipment(shippingId);
    }

    public ShippingDO getShipmentAndUnlock(Integer shippingId, DataModel unlockList) {
        //unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "shipping");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),shippingId);
        
        return getShipment(shippingId);
    }
    
    public void unlockOrders(DataModel unlockList){
        //unlock the order records
        unlockOrderRecords(unlockList);
    }
    
    public NoteDO getShippingNote(Integer shippingId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "shipping");
        Integer shippingReferenceId = (Integer)query.getSingleResult();
        
        query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable",shippingReferenceId);
        query.setParameter("id", shippingId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteDO)results.get(0);
        else 
            return null;
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
    
    private void unlockOrderRecords(DataModel<Integer> orders) {
        if(orders == null || orders.size() == 0)
            return;
        
        Integer orderTableId = null;
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        orderTableId = (Integer)query.getSingleResult();
        
        for(int i=0; i<orders.size(); i++){
            Integer orderId = orders.get(i).getKey();
        
            if(orderId != null)
                lockBean.giveUpLock(orderTableId, orderId);
        }
    }
    
    private void validateShipping(ShippingDO shippingDO, List shippingItems) throws Exception{
        ValidationErrorsList list = new ValidationErrorsList();
        //status required
        if(shippingDO.getStatusId() == null){
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getStatusId()));
        }
        
        //num packages required
        if(shippingDO.getNumberOfPackages() == null){
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getNumberOfPackages()));
        }
        
        //shipped from required
        if(shippingDO.getShippedFromId() == null){
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getShippedFromId()));
        }
        
        //shipped to required
        if(shippingDO.getShippedToId() == null){
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getShippedToId()));
        }
        
        //cost not below 0
        if(shippingDO.getCost() != null && shippingDO.getCost().doubleValue() <= 0){
            list.add(new FieldErrorException("invalidCostException",ShippingMeta.getCost()));
        }
        
        //num of packages not below 1
        if(shippingDO.getNumberOfPackages() != null && shippingDO.getNumberOfPackages().intValue() <= 0){
            list.add(new FieldErrorException("invalidNumPackagesException",ShippingMeta.getNumberOfPackages()));
        }
        
        //at least 1 item required
        if(shippingItems.size() == 0)
            list.add(new FormErrorException("noShippingItemsException"));
        
        if(list.size() > 0)
            throw list;
    }
}
