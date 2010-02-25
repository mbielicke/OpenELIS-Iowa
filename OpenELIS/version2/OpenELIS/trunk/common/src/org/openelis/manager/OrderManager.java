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
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class OrderManager implements RPC, HasNotesInt {

    private static final long                    serialVersionUID = 1L;

    protected OrderViewDO                        order;
    protected OrderItemManager                   items;
    protected NoteManager                        notes;

    public static final String   TYPE_INTERNAL = "I",
                                 TYPE_VENDOR   = "V",
                                 TYPE_KIT      = "K";

    protected transient static OrderManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected OrderManager() {
        order = null;
        items = null;
        notes = null;
    }

    /**
     * Creates a new instance of this object. A default organization object is
     * also created.
     */
    public static OrderManager getInstance() {
        OrderManager manager;

        manager = new OrderManager();
        manager.order = new OrderViewDO();

        return manager;
    }

    public OrderViewDO getOrder() {
        return order;
    }

    public void setOrder(OrderViewDO order) {
        this.order = order;
    }

    // service methods
    public static OrderManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static OrderManager fetchWithItems(Integer id) throws Exception {
        return proxy().fetchWithItems(id);
    }

    public static OrderManager fetchWithReceipts(Integer id) throws Exception {
        return proxy().fetchWithReceipts(id);
    }

    public static OrderManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public OrderManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderManager update() throws Exception {
        return proxy().update(this);
    }

    public OrderManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(order.getId());
    }

    public OrderManager abortUpdate() throws Exception {
        return proxy().abortUpdate(order.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public OrderItemManager getItems() throws Exception {
        if (items == null) {
            if (order.getId() != null) {
                try {
                    items = OrderItemManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (items == null)
                items = OrderItemManager.getInstance();
        }
        return items;
    }

    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (order.getId() != null) {
                try {
                    notes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER, order.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null){
                notes = NoteManager.getInstance();
                notes.setIsExternal(false);
            }
        }
        return notes;
    }

    private static OrderManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderManagerProxy();

        return proxy;
    }
}