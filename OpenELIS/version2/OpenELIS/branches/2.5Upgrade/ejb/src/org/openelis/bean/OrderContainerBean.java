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
import org.openelis.domain.OrderContainerDO;
import org.openelis.entity.OrderContainer;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.OrderMeta;

@Stateless
@SecurityDomain("openelis")

public class OrderContainerBean {

    @PersistenceContext(unitName = "openelis")
    private              EntityManager manager;
    private static final Integer       MAX_QUANTITY = 99;

    @SuppressWarnings("unchecked")
    public ArrayList<OrderContainerDO> fetchByOrderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrderContainer.FetchByOrderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public OrderContainerDO add(OrderContainerDO data) throws Exception {
        OrderContainer entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new OrderContainer();
        entity.setOrderId(data.getOrderId());
        entity.setContainerId(data.getContainerId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }
    
    public OrderContainerDO update(OrderContainerDO data) throws Exception {
        OrderContainer entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(OrderContainer.class, data.getId());
        entity.setOrderId(data.getOrderId());
        entity.setContainerId(data.getContainerId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());        

        return data;
    }

    public void delete(OrderContainerDO data) throws Exception {
        OrderContainer entity;        
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(OrderContainer.class, data.getId());
        if (entity != null)
            manager.remove(entity);

    }

    public void validate(OrderContainerDO data) throws Exception {
        ValidationErrorsList list;
        Integer num;

        list = new ValidationErrorsList();
        if (data.getContainerId() == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrderMeta.getContainerContainerId()));
        num = data.getItemSequence();
        if (num == null) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrderMeta.getContainerItemSequence()));
        } else if(num > MAX_QUANTITY) {
            list.add(new FieldErrorException("qtyNotMoreThanMaxException",
                                             OrderMeta.getContainerItemSequence(), MAX_QUANTITY.toString()));            
        }
        
        if (list.size() > 0)
            throw list;

    }

}
