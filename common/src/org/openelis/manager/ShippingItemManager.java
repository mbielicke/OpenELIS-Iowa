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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.ShippingItemDO;

public class ShippingItemManager implements Serializable {

    private static final long                     serialVersionUID = 1L;

    protected Integer                             shippingId;
    protected ArrayList<ShippingItemDO>       items, deleted;

    protected transient static ShippingItemManagerProxy proxy;

    protected ShippingItemManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static ShippingItemManager getInstance() {
        return new ShippingItemManager();
    }

    public ShippingItemDO getItemAt(int i) {
        return items.get(i);
    }

    public void setItemAt(ShippingItemDO item, int i) {
        if (items == null)
            items = new ArrayList<ShippingItemDO>();
        items.set(i, item);
    }

    public int addItem() {
        if (items == null)
            items = new ArrayList<ShippingItemDO>();
        items.add(new ShippingItemDO());

        return count() - 1;
    }

    public int addItemAt(int i) {
        if (items == null)
            items = new ArrayList<ShippingItemDO>();
        items.add(i, new ShippingItemDO());

        return i;
    }

    public void removeItemAt(int i) {
        ShippingItemDO tmp;

        if (items == null || i >= items.size())
            return;

        tmp = items.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<ShippingItemDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }

    // service methods
    public static ShippingItemManager fetchByShippingId(Integer id) throws Exception {
        return proxy().fetchByShippingId(id);
    }

    public ShippingItemManager add() throws Exception {
        return proxy().add(this);
    }

    public ShippingItemManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getShippingId() {
        return shippingId;
    }

    void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }
    
    ArrayList<ShippingItemDO> getItems() {
        return items;
    }

    void setItems(ArrayList<ShippingItemDO> items) {
        this.items = items;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    ShippingItemDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ShippingItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new ShippingItemManagerProxy();
        return proxy;
    }
}
