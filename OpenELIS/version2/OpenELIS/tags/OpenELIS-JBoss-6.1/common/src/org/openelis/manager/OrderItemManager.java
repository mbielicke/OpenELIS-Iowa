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

import org.openelis.domain.OrderItemViewDO;
import org.openelis.gwt.common.RPC;

public class OrderItemManager implements RPC {

    private static final long                        serialVersionUID = 1L;

    protected Integer                                orderId;
    protected ArrayList<OrderItemViewDO>             items, deleted;

    protected transient static OrderItemManagerProxy proxy;

    protected OrderItemManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrderItemManager getInstance() {
        return new OrderItemManager();
    }

    public OrderItemViewDO getItemAt(int i) {
        return items.get(i);
    }

    public void setItemAt(OrderItemViewDO item, int i) {
        if (items == null)
            items = new ArrayList<OrderItemViewDO>();
        items.set(i, item);
    }

    public int addItem() {
        if (items == null)
            items = new ArrayList<OrderItemViewDO>();
        items.add(new OrderItemViewDO());

        return count() - 1;
    }
    
    public int addItemAt(int i) {
        if (items == null)
            items = new ArrayList<OrderItemViewDO>();
        items.add(i, new OrderItemViewDO());

        return i;
    }
    
    public int addItem(OrderItemViewDO item) {
        if (items == null)
            items = new ArrayList<OrderItemViewDO>();
        items.add(item);

        return count() - 1;
    }

    public void removeItemAt(int i) {
        OrderItemViewDO tmp;

        if (items == null || i >= items.size())
            return;

        tmp = items.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrderItemViewDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }

    // service methods
    public static OrderItemManager fetchByOrderId(Integer id) throws Exception {
        return proxy().fetchByOrderId(id);
    }

    public OrderItemManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderItemManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate(String type) throws Exception {
        proxy().validate(this, type);
    }

    // friendly methods used by managers and proxies
    Integer getOrderId() {
        return orderId;
    }

    void setOrderId(Integer id) {
        orderId = id;
    }

    ArrayList<OrderItemViewDO> getItems() {
        return items;
    }

    void setItems(ArrayList<OrderItemViewDO> items) {
        this.items = items;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    OrderItemViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrderItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderItemManagerProxy();
        return proxy;
    }
}
