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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.entity.Shipping;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.ShippingMeta;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class ShippingBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager             manager;

    @EJB
    private OrganizationBean         organizationBean;   

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
    
    public ShippingViewDO fetchByOrderId(Integer orderId) throws Exception {
        Query query;
        ShippingViewDO data;
        OrganizationViewDO organization;
        List list;
        
        query = manager.createNamedQuery("Shipping.FetchByOrderId");
        query.setParameter("referenceTableId", Constants.table().ORDER_ITEM);
        query.setParameter("orderId", orderId);
        data = null;
        
        list = query.getResultList();
        if(!list.isEmpty()) {
            data = (ShippingViewDO)list.get(0);
            organization = organizationBean.fetchById(data.getShippedToId());           
            data.setShippedTo(organization);
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

    public ShippingDO add(ShippingDO data) throws Exception {
        Shipping entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Shipping();
        entity.setStatusId(data.getStatusId());
        entity.setShippedFromId(data.getShippedFromId());
        entity.setShippedToId(data.getShippedToId());
        entity.setShippedToAttention(data.getShippedToAttention());
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

    public ShippingDO update(ShippingDO data) throws Exception {
        Shipping entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Shipping.class, data.getId());
        entity.setStatusId(data.getStatusId());
        entity.setShippedFromId(data.getShippedFromId());
        entity.setShippedToId(data.getShippedToId());
        entity.setShippedToAttention(data.getShippedToAttention());
        entity.setProcessedBy(data.getProcessedBy());
        entity.setProcessedDate(data.getProcessedDate());
        entity.setShippedMethodId(data.getShippedMethodId());
        entity.setShippedDate(data.getShippedDate());
        entity.setNumberOfPackages(data.getNumberOfPackages());
        entity.setCost(data.getCost());
        
        return data;
    }

    public void validate(ShippingDO data) throws Exception {
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
}
