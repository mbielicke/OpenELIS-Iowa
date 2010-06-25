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

public class OrderManager implements RPC, HasAuxDataInt {

    private static final long                    serialVersionUID = 1L;

    protected OrderViewDO                        order;
    protected OrderItemManager                   items;
    protected OrderFillManager                   fills;
    protected OrderReceiptManager                receipts;
    protected OrderContainerManager              containers; 
    protected OrderTestManager                   tests; 
    protected NoteManager                        shipNotes, customerNotes;
    protected AuxDataManager                     auxData;    

    public static final String   TYPE_INTERNAL = "I",
                                 TYPE_VENDOR   = "V",
                                 TYPE_SEND_OUT = "S";

    protected transient static OrderManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected OrderManager() {
        order = null;
        items = null;
        fills = null;
        receipts = null;
        shipNotes = null;
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

    public static OrderManager fetchWithFills(Integer id) throws Exception {
        return proxy().fetchWithFills(id);
    }

    public static OrderManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }
    
    public static OrderManager fetchWithTestsAndContainers(Integer id) throws Exception {
        return proxy().fetchWithTestsAndContainers(id);
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

    public OrderFillManager getFills() throws Exception {
        if (fills == null) {
            if (order.getId() != null) {
                try {
                    fills = OrderFillManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (fills == null)
                fills = OrderFillManager.getInstance();
        }
        return fills;
    }
    
    public OrderReceiptManager getReceipts() throws Exception {
        if (receipts == null) {
            if (order.getId() != null) {
                try {
                    receipts = OrderReceiptManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (receipts == null)
                receipts = OrderReceiptManager.getInstance();
        }
        return receipts;
    }

    public NoteManager getShippingNotes() throws Exception {
        if (shipNotes == null) {
            if (order.getId() != null) {
                try {
                    shipNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER, order.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (shipNotes == null){
                shipNotes = NoteManager.getInstance();
                shipNotes.setIsExternal(false);
            }
        }
        return shipNotes;
    }

    public NoteManager getCustomerNotes() throws Exception {
        if (customerNotes == null) {
            if (order.getId() != null) {
                try {
                    customerNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER, order.getId(), true);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (customerNotes == null){
                customerNotes = NoteManager.getInstance();
                customerNotes.setIsExternal(true);
            }
        }
        return customerNotes;
    }
    
    public OrderContainerManager getContainers() throws Exception {
        if (containers == null) {
            if (order.getId() != null) {
                try {
                    containers = OrderContainerManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (containers == null)
                containers = OrderContainerManager.getInstance();
        }
        return containers;
    }
    
    public OrderTestManager getTests() throws Exception {
        if (tests == null) {
            if (order.getId() != null) {
                try {
                    tests = OrderTestManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (tests == null)
                tests = OrderTestManager.getInstance();
        }
        return tests;
    }
    
    public AuxDataManager getAuxData() throws Exception {
        if (auxData == null) {
            if (order.getId() != null) {
                try {
                    auxData = AuxDataManager.fetchById(order.getId(), ReferenceTable.ORDER);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (auxData == null)
                auxData = AuxDataManager.getInstance();
        }
    
        return auxData;
    }

    private static OrderManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderManagerProxy();

        return proxy;
    }

}