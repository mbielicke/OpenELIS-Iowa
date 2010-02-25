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

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderItemDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderItemLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class OrderItemManagerProxy {

    public OrderItemManager fetchByOrderId(Integer id) throws Exception {
        OrderItemManager m;
        ArrayList<OrderItemViewDO> items;

        items = local().fetchByOrderId(id);
        m = OrderItemManager.getInstance();
        m.setOrderId(id);
        m.setItems(items);

        return m;
    }

    public OrderItemManager add(OrderItemManager man) throws Exception {
        OrderItemLocal cl;
        OrderItemViewDO item;

        cl = local();
        for (int i = 0; i < man.count(); i++ ) {
            item = man.getItemAt(i);
            item.setOrderId(man.getOrderId());
            cl.add(item);
        }

        return man;
    }

    public OrderItemManager update(OrderItemManager man) throws Exception {
        OrderItemLocal cl;
        OrderItemViewDO item;

        cl = local();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            item = man.getItemAt(i);

            if (item.getId() == null) {
                item.setOrderId(man.getOrderId());
                cl.add(item);
            } else {
                cl.update(item);
            }
        }

        return man;
    }
    
    public void validate(OrderItemManager man) throws Exception {
        ValidationErrorsList list;
        OrderItemLocal cl;

        cl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getItemAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private OrderItemLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (OrderItemLocal)ctx.lookup("openelis/OrderItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
