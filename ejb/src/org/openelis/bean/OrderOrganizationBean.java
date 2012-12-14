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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.OrderOrganizationDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.entity.OrderOrganization;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderOrganizationLocal;
import org.openelis.meta.OrderMeta;

@Stateless
@SecurityDomain("openelis")

public class OrderOrganizationBean implements OrderOrganizationLocal {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    public ArrayList<OrderOrganizationViewDO> fetchByOrderId(Integer orderId) throws Exception {
        List<OrderOrganizationViewDO> returnList;
        Query query;

        query = manager.createNamedQuery("OrderOrganization.FetchByOrderId");
        query.setParameter("id", orderId);
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public OrderOrganizationDO add(OrderOrganizationDO data) throws Exception {
        OrderOrganization entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new OrderOrganization();
        entity.setOrderId(data.getOrderId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public OrderOrganizationDO update(OrderOrganizationDO data) throws Exception {
        OrderOrganization entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(OrderOrganization.class, data.getId());
        entity.setOrderId(data.getOrderId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());
        
        return data;
    }
    
    public void delete(OrderOrganizationDO data) throws Exception {
        OrderOrganization entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(OrderOrganization.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
    
    public void validate(OrderOrganizationDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (data.getTypeId() == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrderMeta.getOrderOrganizationTypeId()));
        if (data.getOrganizationId() == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrderMeta.getOrderOrganizationOrganizationName()));
        
        if (list.size() > 0)
            throw list;    
    }
}