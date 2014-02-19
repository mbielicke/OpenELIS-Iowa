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

import org.openelis.bean.OrderItemBean;
import org.openelis.constants.Messages;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.meta.OrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrderItemManagerProxy {

    public OrderItemManager fetchByOrderId(Integer id) throws Exception {
        OrderItemManager m;
        ArrayList<OrderItemViewDO> items;

        items = EJBFactory.getOrderItem().fetchByOrderId(id);
        m = OrderItemManager.getInstance();
        m.setOrderId(id);
        m.setItems(items);

        return m;
    }

    public OrderItemManager add(OrderItemManager man) throws Exception {
        OrderItemBean cl;
        OrderItemViewDO item;

        cl = EJBFactory.getOrderItem();
        for (int i = 0; i < man.count(); i++ ) {
            item = man.getItemAt(i);
            item.setOrderId(man.getOrderId());
            cl.add(item);
        }

        return man;
    }

    public OrderItemManager update(OrderItemManager man) throws Exception {
        OrderItemBean cl;
        OrderItemViewDO item;

        cl = EJBFactory.getOrderItem();
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
    
    public void validate(OrderItemManager man, String type) throws Exception {
        Integer invItemId;
        ArrayList<Integer> invItemIdList;
        ValidationErrorsList list;
        OrderItemBean cl;        

        cl = EJBFactory.getOrderItem();
        list = new ValidationErrorsList();
        invItemIdList = null;
        if (OrderManager.TYPE_VENDOR.equals(type))
            invItemIdList = new ArrayList<Integer>();
        
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getItemAt(i));                
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
                        
            if (!OrderManager.TYPE_VENDOR.equals(type))
                continue;
            
            invItemId = man.getItemAt(i).getInventoryItemId();
            if (invItemId != null) {
                if (invItemIdList.contains(invItemId)) 
                    list.add(new TableFieldErrorException(Messages.get().duplicateInvItemVendorOrderException(),i,
                                                          OrderMeta.getOrderItemInventoryItemName(), "itemTable"));
                else
                    invItemIdList.add(invItemId);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
