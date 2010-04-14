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
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.entity.Shipping;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.ShippingLocal;
import org.openelis.meta.ShippingMeta;
import org.openelis.remote.ShippingRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("shipping-select")
public class ShippingBean implements ShippingRemote, ShippingLocal{

    @PersistenceContext(name = "openelis")
    private EntityManager             manager;

    @EJB
    private OrganizationLocal         organizationBean;   

    private static final ShippingMeta meta = new ShippingMeta();

    public ShippingViewDO fetchById(Integer id) throws Exception {
        Query query;
        ShippingViewDO data;
        OrganizationViewDO organization;
        
        query = manager.createNamedQuery("Shipping.FetchById");
        query.setParameter("id", id);
        try {
            data = (ShippingViewDO)query.getSingleResult();
            organization = organizationBean.fetchById(data.getShippedToId());           
            data.setShippedTo(organization);
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + 
                          ShippingMeta.getId() + ",'') ");
        builder.constructWhere(fields);
        builder.setOrderBy(ShippingMeta.getId() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public ShippingViewDO add(ShippingViewDO data) throws Exception {
        Shipping entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Shipping();
        entity.setStatusId(data.getStatusId());
        entity.setShippedFromId(data.getShippedFromId());
        entity.setShippedToId(data.getShippedToId());
        entity.setProcessedBy(data.getProcessedBy());
        entity.setProcessedDate(data.getProcessedDate());
        entity.setShippedMethodId(data.getShippedMethodId());
        entity.setShippedDate(data.getShippedDate());
        entity.setNumberOfPackages(data.getNumberOfPackages());
        entity.setCost(data.getCost());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public ShippingViewDO update(ShippingViewDO data) throws Exception {
        Shipping entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Shipping.class, data.getId());
        entity.setStatusId(data.getStatusId());
        entity.setShippedFromId(data.getShippedFromId());
        entity.setShippedToId(data.getShippedToId());
        entity.setProcessedBy(data.getProcessedBy());
        entity.setProcessedDate(data.getProcessedDate());
        entity.setShippedMethodId(data.getShippedMethodId());
        entity.setShippedDate(data.getShippedDate());
        entity.setNumberOfPackages(data.getNumberOfPackages());
        entity.setCost(data.getCost());
        
        return data;
    }

    public void validate(ShippingViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
       
        if(data.getStatusId() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getStatusId()));        
                
        if(data.getNumberOfPackages() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getNumberOfPackages()));        
                
        if(data.getShippedFromId() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getShippedFromId()));        
                
        if(data.getShippedToId() == null)
            list.add(new FieldErrorException("fieldRequiredException",ShippingMeta.getShippedToName()));        
                
        if(data.getCost() != null && data.getCost().doubleValue() <= 0)
            list.add(new FieldErrorException("invalidCostException",ShippingMeta.getCost()));
        
        if(list.size() > 0)
            throw list;
    }

    /* public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception {
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
    } */
}
