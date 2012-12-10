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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OrderTestAnalyteDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.entity.OrderTestAnalyte;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderTestAnalyteLocal;

@Stateless
@SecurityDomain("openelis")

public class OrderTestAnalyteBean implements OrderTestAnalyteLocal {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    public ArrayList<OrderTestAnalyteViewDO> fetchByOrderTestId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrderTestAnalyte.FetchByOrderTestId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<OrderTestAnalyteViewDO> fetchRowAnalytesByOrderTestId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrderTestAnalyte.FetchRowAnalytesByOrderTestId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<OrderTestAnalyteViewDO> fetchRowAnalytesByTestId(Integer testId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrderTestAnalyte.FetchRowAnalytesByTestId");
        query.setParameter("testId", testId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public OrderTestAnalyteDO add(OrderTestAnalyteDO data) throws Exception {
        OrderTestAnalyte entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new OrderTestAnalyte();       
        entity.setOrderTestId(data.getOrderTestId());
        entity.setAnalyteId(data.getAnalyteId());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrderTestAnalyteDO update(OrderTestAnalyteDO data) throws Exception {
        OrderTestAnalyte entity;
        
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderTestAnalyte.class, data.getId());       
        entity.setOrderTestId(data.getOrderTestId());
        entity.setAnalyteId(data.getAnalyteId());
        
        return data;
    }

    public void delete(OrderTestAnalyteDO data) throws Exception {
        OrderTestAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrderTestAnalyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
    
    public void deleteByOrderTestId(Integer id) throws Exception {
        ArrayList<OrderTestAnalyteViewDO> list;

        try {
            list = fetchByOrderTestId(id);
            for (OrderTestAnalyteViewDO data : list)
                delete(data);
        } catch (NotFoundException e) {
            // the order test may not have any analytes linked to it
        }
    }

    public void validate(OrderTestAnalyteViewDO data, int index) throws Exception {
        String indexStr;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        indexStr = String.valueOf(index);
        if ("N".equals(data.getTestAnalyteIsPresent()) && "Y".equals(data.getTestAnalyteIsReportable()))
            /*
             * this analyte is not present in the original test thus it needs
             * to be removed from this order test 
             */
            list.add(new FieldErrorException("analyteNotPresentInTestException", null, indexStr, data.getAnalyteName()));            
        
        if (list.size() > 0)
            throw list;
    }
}