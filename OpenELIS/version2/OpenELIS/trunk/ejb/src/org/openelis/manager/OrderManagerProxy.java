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
package org.openelis.manager;

import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrderManagerProxy {

    public OrderManager fetchById(Integer id) throws Exception {
        OrderViewDO data;
        OrderManager m;

        data = EJBFactory.getOrder().fetchById(id);
        m = OrderManager.getInstance();

        m.setOrder(data);

        return m;
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        OrderManager m;

        m = fetchById(id);
        m.getItems();

        return m;
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        OrderManager m;

        m = fetchById(id);
        m.getFills();

        return m;
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        OrderManager m;

        m = fetchById(id);
        m.getShippingNotes();
        m.getCustomerNotes();

        return m;
    }
    
    public OrderManager fetchWithTestsAndContainers(Integer id) throws Exception {
        OrderManager m;

        m = fetchById(id);
        m.getContainers();
        m.getTests();

        return m;
    }

    public OrderManager add(OrderManager man) throws Exception {
        Integer id;

        EJBFactory.getOrder().add(man.getOrder());
        id = man.getOrder().getId();

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().add();
        }
        
        if (man.fills != null) {
            man.getFills().setOrderId(id);
            man.getFills().add();
        }
        
        if (man.shipNotes != null) {
            man.getShippingNotes().setReferenceId(id);
            man.getShippingNotes().setReferenceTableId(ReferenceTable.ORDER);
            man.getShippingNotes().add();
        }
        
        if (man.customerNotes != null) {
            man.getCustomerNotes().setReferenceId(id);
            man.getCustomerNotes().setReferenceTableId(ReferenceTable.ORDER);
            man.getCustomerNotes().add();
        }
        
        if (man.auxData != null) {
            man.getAuxData().setReferenceId(id);
            man.getAuxData().setReferenceTableId(ReferenceTable.ORDER);
            man.getAuxData().add();
        }
        
        if (man.containers != null) {
            man.getContainers().setOrderId(id);
            man.getContainers().add();
        }
        
        if (man.tests != null) {
            man.getTests().setOrderId(id);
            man.getTests().add();
        }

        return man;
    }

    public OrderManager update(OrderManager man) throws Exception {
        Integer id;

        EJBFactory.getOrder().update(man.getOrder());
        id = man.getOrder().getId();

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().update();
        }
        
        if (man.fills != null) {
            man.getFills().setOrderId(id);
            man.getFills().update();
        }
        
        if (man.shipNotes != null) {
            man.getShippingNotes().setReferenceId(id);
            man.getShippingNotes().setReferenceTableId(ReferenceTable.ORDER);
            man.getShippingNotes().update();
        }
        
        if (man.customerNotes != null) {
            man.getCustomerNotes().setReferenceId(id);
            man.getCustomerNotes().setReferenceTableId(ReferenceTable.ORDER);
            man.getCustomerNotes().update();
        }
        
        if (man.auxData != null) {
            man.getAuxData().setReferenceId(id);
            man.getAuxData().setReferenceTableId(ReferenceTable.ORDER);
            man.getAuxData().update();
        }
        
        if (man.containers != null) {
            man.getContainers().setOrderId(id);
            man.getContainers().update();
        }
        
        if (man.tests != null) {
            man.getTests().setOrderId(id);
            man.getTests().update();
        }

        return man;
    }

    public OrderManager fetchForUpdate(OrderManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrderManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        try {
            EJBFactory.getOrder().validate(man.getOrder());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.items != null)
                man.getItems().validate(man.getOrder().getType());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.fills != null)
                man.getFills().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.tests != null)
                man.getTests().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        try {
            if (man.containers != null)
                man.getContainers().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if(man.auxData != null)
            man.getAuxData().validate(list);
        
        if (list.size() > 0)
            throw list;
    }
}