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

import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.NotFoundException;

public class OrderManager implements Serializable, HasAuxDataInt {

    private static final long                    serialVersionUID = 1L;

    protected OrderViewDO                        order;
    protected OrderOrganizationManager           organizations;
    protected OrderItemManager                   items;
    protected OrderFillManager                   fills;
    protected OrderReceiptManager                receipts;
    protected OrderContainerManager              containers; 
    protected OrderTestManager                   tests; 
    protected NoteManager                        shipNotes, customerNotes, 
                                                 internalNotes, sampleNotes;
    protected AuxDataManager                     auxData;
    protected OrderRecurrenceDO                  recurrence;

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
        organizations = null;
        items = null;
        fills = null;
        receipts = null;
        shipNotes = null;
        customerNotes = null;
        internalNotes = null;
        recurrence = null;
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
    
    public static OrderManager fetchWithOrganizations(Integer id) throws Exception {
        return proxy().fetchWithOrganizations(id);
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
    
    public static OrderManager fetchWithTests(Integer id) throws Exception {
        return proxy().fetchWithTests(id);
    }
    
    public static OrderManager fetchWithContainers(Integer id) throws Exception {
        return proxy().fetchWithContainers(id);
    }
    
    public static OrderManager fetchWithRecurrence(Integer id) throws Exception {
        return proxy().fetchWithRecurring(id);
    }
    
    /*
     * this method enables us to get the OrderRecurrenceDO associated with a
     * manager in OrderManagerBean or anywhere else without creating a new instance
     * of the manager       
     */
    public static OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        return proxy().fetchRecurrenceByOrderId(id);
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
    public OrderOrganizationManager getOrganizations() throws Exception {
        if (organizations == null) {
            if (order.getId() != null) {
                try {
                    organizations = OrderOrganizationManager.fetchByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (organizations == null)
                organizations = OrderOrganizationManager.getInstance();
        }

        return organizations;
    }
    
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
                    shipNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER_SHIPPING_NOTE, order.getId(), true);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (shipNotes == null){
                shipNotes = NoteManager.getInstance();
                shipNotes.setIsExternal(true);
            }
        }
        return shipNotes;
    }

    public NoteManager getCustomerNotes() throws Exception {
        if (customerNotes == null) {
            if (order.getId() != null) {
                try {
                    customerNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER_CUSTOMER_NOTE, order.getId(), true);
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
    
    public NoteManager getInternalNotes() throws Exception {
        if (internalNotes == null) {
            if (order.getId() != null) {
                try {
                    internalNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER, order.getId(), false);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (internalNotes == null) {
                internalNotes = NoteManager.getInstance();
                internalNotes.setIsExternal(false);
            }
        }

        return internalNotes;
    }
    
    public NoteManager getSampleNotes() throws Exception {
        if (sampleNotes == null) {
            if (order.getId() != null) {
                try {
                    sampleNotes = NoteManager.fetchByRefTableRefIdIsExt(ReferenceTable.ORDER_SAMPLE_NOTE, order.getId(), true);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (sampleNotes == null) {
                sampleNotes = NoteManager.getInstance();
                sampleNotes.setIsExternal(true);
            }
        }

        return sampleNotes;
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
    
    public OrderRecurrenceDO getRecurrence() throws Exception {
        if (recurrence == null) {
            if (order.getId() != null) {
                try {
                    recurrence = fetchRecurrenceByOrderId(order.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (recurrence == null) 
                recurrence = new OrderRecurrenceDO();            
        }
    
        return recurrence;
    }

    private static OrderManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderManagerProxy();

        return proxy;
    }
}