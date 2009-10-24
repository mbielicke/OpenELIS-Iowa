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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.entity.Note;
import org.openelis.entity.Shipping;
import org.openelis.entity.ShippingItem;
import org.openelis.entity.ShippingTracking;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.remote.ShippingRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

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
    
    private static int shippingRefTableId, orderItemRefTableId, sampleItemRefTableId;
    private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

    public ShippingBean(){
        shippingRefTableId = ReferenceTableCache.getReferenceTable("shipping");
        orderItemRefTableId = ReferenceTableCache.getReferenceTable("order_item");
        sampleItemRefTableId = ReferenceTableCache.getReferenceTable("sample_item");
    }
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }

    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception {
        ShippingAddAutoFillDO autoFillDO = new ShippingAddAutoFillDO();       
        //status integer
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", "shipping_status_processed");

        //default to shipped status
        DictionaryDO dictDO = (DictionaryDO)query.getSingleResult();
        autoFillDO.setStatus(dictDO.getId());

        //default to today
        autoFillDO.setProcessedDate(new Date());

        //default to current user
        SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
        autoFillDO.setProcessedBy(systemUserDO.getLoginName());
        autoFillDO.setSystemUserId(systemUserDO.getId());
        
        
        return autoFillDO;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
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
 public Integer updateShipment(ShippingDO shippingDO, List<ShippingItemDO> shippingItems, List<ShippingTrackingDO> trackingNumbers, NoteViewDO shippingNote) throws Exception {
        validateShipping(shippingDO, shippingItems);
        
        if(shippingDO.getId() != null)
            lockBean.validateLock(shippingRefTableId,shippingDO.getId());
        
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
        note.setReferenceTableId(shippingRefTableId);
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
       
       if(shippingDO.getId() != null)
           lockBean.giveUpLock(shippingRefTableId, shippingDO.getId());
        
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
        lockBean.getLock(shippingRefTableId, shippingId);
        
        return getShipment(shippingId);
    }

    public ShippingDO getShipmentAndUnlock(Integer shippingId) {
        //unlock the entity
        lockBean.giveUpLock(shippingRefTableId, shippingId);
        
        return getShipment(shippingId);
    }
    
    public NoteViewDO getShippingNote(Integer shippingId) {
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", shippingRefTableId);
        query.setParameter("id", shippingId);
        
        List results = query.getResultList();
        
        if(results.size() > 0)
            return (NoteViewDO)results.get(0);
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
        
        for(int i=0; i<resultList.size(); i++){
            ShippingItemDO itemDO = (ShippingItemDO)resultList.get(i);

            if(itemDO.getReferenceTableId().equals(orderItemRefTableId)){
                query = manager.createNamedQuery("OrderItem.OrderItemName");
                query.setParameter("id", itemDO.getReferenceId());
                itemDO.setItemDescription((String)query.getSingleResult());
            
            }else if(itemDO.getReferenceTableId().equals(sampleItemRefTableId)){
                
            }
        }
        
        return resultList;
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
