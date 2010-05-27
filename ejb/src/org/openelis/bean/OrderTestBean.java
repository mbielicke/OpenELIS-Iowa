/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.entity.OrderTest;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderTestLocal;
import org.openelis.meta.OrderMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("order-select")
public class OrderTestBean implements OrderTestLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<OrderTestViewDO> fetchByOrderId(Integer id) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("OrderTest.FetchByOrderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        list = DataBaseUtil.toArrayList(list);        
        
        return (ArrayList) list;
    }

    public OrderTestViewDO add(OrderTestViewDO data) throws Exception {
        OrderTest entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new OrderTest();
        entity.setOrderId(data.getOrderId());
        entity.setSequence(data.getSortOrder());
        entity.setTestId(data.getTestId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrderTestViewDO update(OrderTestViewDO data) throws Exception {
        OrderTest entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderTest.class, data.getId());
        entity.setOrderId(data.getOrderId());
        entity.setSequence(data.getSortOrder());
        entity.setTestId(data.getTestId());

        return data;
    }

    public void delete(OrderTestViewDO data) throws Exception {
        OrderTest entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderTest.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
    
    public void validate(OrderTestViewDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (data.getTestId() == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrderMeta.getTestName()));        
        
        if (list.size() > 0)
            throw list;
    }
}
