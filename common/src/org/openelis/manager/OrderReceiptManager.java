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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.InventoryXPutViewDO;


public class OrderReceiptManager implements Serializable {

    private static final long                           serialVersionUID = 1L;

    protected Integer                                   orderId;
    protected ArrayList<InventoryXPutViewDO>                receipts, deleted;

    protected transient static OrderReceiptManagerProxy proxy;

    protected OrderReceiptManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrderReceiptManager getInstance() {
        return new OrderReceiptManager();
    }

    public InventoryXPutViewDO getReceiptAt(int i) {
        return receipts.get(i);
    }

    public void setReceiptAt(InventoryXPutViewDO data, int i) {
        if (receipts == null)
            receipts = new ArrayList<InventoryXPutViewDO>();
        receipts.set(i, data);
    }

    public int addReceipt() {
        if (receipts == null)
            receipts = new ArrayList<InventoryXPutViewDO>();
        receipts.add(new InventoryXPutViewDO());

        return count() - 1;
    }

    public int addReceiptAt(int i) {
        if (receipts == null)
            receipts = new ArrayList<InventoryXPutViewDO>();
        receipts.add(i, new InventoryXPutViewDO());

        return i;
    }

    public void removeReceiptAt(int i) {
        InventoryXPutViewDO tmp;

        if (receipts == null || i >= receipts.size())
            return;

        tmp = receipts.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InventoryXPutViewDO>();
            deleted.add(tmp);
        }
    }

    public void removeReceipt(InventoryXPutViewDO data) {
        int index;

        if (receipts == null || receipts.size() == 0 || data == null)
            return;

        index = receipts.indexOf(data);

        if (index >= 0)
            removeReceiptAt(index);
    }

    public int count() {
        if (receipts == null)
            return 0;

        return receipts.size();
    }

    // service methods
    public static OrderReceiptManager fetchByOrderId(Integer id) throws Exception {
        return proxy().fetchByOrderId(id);
    }

    public OrderReceiptManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderReceiptManager update() throws Exception {
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

    ArrayList<InventoryXPutViewDO> getReceipts() {
        return receipts;
    }

    void setReceipts(ArrayList<InventoryXPutViewDO> receipts) {
        this.receipts = receipts;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    InventoryXPutViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrderReceiptManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderReceiptManagerProxy();
        return proxy;
    }
}
