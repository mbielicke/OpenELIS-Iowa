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
import java.util.HashMap;

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.gwt.common.RPC;

public class OrderFillManager implements RPC {

    private static final long                                  serialVersionUID = 1L;

    protected Integer                                          orderId;
    protected ArrayList<InventoryXUseViewDO>                   fills, deleted;

    protected transient static OrderFillManagerProxy proxy;

    protected OrderFillManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrderFillManager getInstance() {
        return new OrderFillManager();
    }

    public InventoryXUseViewDO getFillAt(int i) {
        return fills.get(i);
    }   

    public void setFillAt(InventoryXUseViewDO item, int i) {
        if (fills == null)
            fills = new ArrayList<InventoryXUseViewDO>();
        fills.set(i, item);
    }

    public int addFill() {
        if (fills == null)
            fills = new ArrayList<InventoryXUseViewDO>();
        fills.add(new InventoryXUseViewDO());

        return count() - 1;
    }

    public int addFillAt(int i) {
        if (fills == null)
            fills = new ArrayList<InventoryXUseViewDO>();
        fills.add(i, new InventoryXUseViewDO());

        return i;
    }

    public void removeFillAt(int i) {
        InventoryXUseViewDO tmp;

        if (fills == null || i >= fills.size())
            return;

        tmp = fills.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InventoryXUseViewDO>();
            deleted.add(tmp);
        }
    }
    
    public void removeFill(InventoryXUseViewDO data) {
        int index;
        
        if (fills == null || fills.size() == 0 || data == null)
            return;
        
        index = fills.indexOf(data);
        
        if(index >= 0)
            removeFillAt(index);
    }

    public int count() {
        if (fills == null)
            return 0;

        return fills.size();
    }

    // service methods
    public static OrderFillManager fetchByOrderId(Integer id) throws Exception {
        return proxy().fetchByOrderId(id);
    }

    public OrderFillManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderFillManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getOrderId() {
        return orderId;
    }

    void setOrderId(Integer id) {
        orderId = id;
    }

    ArrayList<InventoryXUseViewDO> getFills() {
        return fills;
    }

    void setFills(ArrayList<InventoryXUseViewDO> receipts) {
        this.fills = receipts;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    InventoryXUseViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrderFillManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderFillManagerProxy();
        return proxy;
    }
}
