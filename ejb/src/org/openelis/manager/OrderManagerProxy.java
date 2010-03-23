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

import javax.naming.InitialContext;

import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class OrderManagerProxy {

    public OrderManager fetchById(Integer id) throws Exception {
        OrderViewDO data;
        OrderManager m;

        data = local().fetchById(id);
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

    public OrderManager add(OrderManager man) throws Exception {
        Integer id;

        local().add(man.getOrder());
        id = man.getOrder().getId();

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().add();
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

        return man;
    }

    public OrderManager update(OrderManager man) throws Exception {
        Integer id;

        local().update(man.getOrder());
        id = man.getOrder().getId();

        if (man.items != null) {
            man.getItems().setOrderId(id);
            man.getItems().update();
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
            local().validate(man.getOrder());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.items != null)
                man.getItems().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        if (list.size() > 0)
            throw list;
    }

    private OrderLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (OrderLocal)ctx.lookup("openelis/OrderBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}