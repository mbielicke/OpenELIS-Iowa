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
import org.openelis.domain.Constants;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.NotFoundException;

public class IOrderManager implements Serializable, HasAuxDataInt {

    private static final long                    serialVersionUID = 1L;

    protected IOrderViewDO                       iorder;
    protected IOrderOrganizationManager           organizations;
    protected IOrderItemManager                  items;
    protected IOrderFillManager                  fills;
    protected IOrderReceiptManager                receipts;
    protected IOrderContainerManager             containers; 
    protected IOrderTestManager                   tests; 
    protected NoteManager                        shipNotes, customerNotes, 
                                                 internalNotes, sampleNotes;
    protected AuxDataManager                     auxData;
    protected IOrderRecurrenceDO                 recurrence;

    public static final String   TYPE_INTERNAL = "I",
                                 TYPE_VENDOR   = "V",
                                 TYPE_SEND_OUT = "S";

    protected transient static IOrderManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected IOrderManager() {
        iorder = null;
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
    public static IOrderManager getInstance() {
        IOrderManager manager;

        manager = new IOrderManager();
        manager.iorder = new IOrderViewDO();

        return manager;
    }

    public IOrderViewDO getIorder() {
        return iorder;
    }

    public void setIorder(IOrderViewDO iorder) {
        this.iorder = iorder;
    }

    // service methods
    public static IOrderManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        return proxy().fetchWithOrganizations(id);
    }
    
    public static IOrderManager fetchWithItems(Integer id) throws Exception {
        return proxy().fetchWithItems(id);
    }

    public static IOrderManager fetchWithFills(Integer id) throws Exception {
        return proxy().fetchWithFills(id);
    }

    public static IOrderManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }
    
    public static IOrderManager fetchWithTests(Integer id) throws Exception {
        return proxy().fetchWithTests(id);
    }
    
    public static IOrderManager fetchWithContainers(Integer id) throws Exception {
        return proxy().fetchWithContainers(id);
    }
    
    public static IOrderManager fetchWithRecurrence(Integer id) throws Exception {
        return proxy().fetchWithRecurring(id);
    }
    
    /*
     * this method enables us to get the OrderRecurrenceDO associated with a
     * manager in OrderManagerBean or anywhere else without creating a new instance
     * of the manager       
     */
    public static IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        return proxy().fetchRecurrenceByIorderId(id);
    }

    public IOrderManager add() throws Exception {
        return proxy().add(this);
    }

    public IOrderManager update() throws Exception {
        return proxy().update(this);
    }

    public IOrderManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(iorder.getId());
    }

    public IOrderManager abortUpdate() throws Exception {
        return proxy().abortUpdate(iorder.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //    
    public IOrderOrganizationManager getOrganizations() throws Exception {
        if (organizations == null) {
            if (iorder.getId() != null) {
                try {
                    organizations = IOrderOrganizationManager.fetchByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }

            if (organizations == null)
                organizations = IOrderOrganizationManager.getInstance();
        }

        return organizations;
    }
    
    public IOrderItemManager getItems() throws Exception {
        if (items == null) {
            if (iorder.getId() != null) {
                try {
                    items = IOrderItemManager.fetchByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (items == null)
                items = IOrderItemManager.getInstance();
        }
        return items;
    }

    public IOrderFillManager getFills() throws Exception {
        if (fills == null) {
            if (iorder.getId() != null) {
                try {
                    fills = IOrderFillManager.fetchByOrderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (fills == null)
                fills = IOrderFillManager.getInstance();
        }
        return fills;
    }
    
    public IOrderReceiptManager getReceipts() throws Exception {
        if (receipts == null) {
            if (iorder.getId() != null) {
                try {
                    receipts = IOrderReceiptManager.fetchByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (receipts == null)
                receipts = IOrderReceiptManager.getInstance();
        }
        return receipts;
    }

    public NoteManager getShippingNotes() throws Exception {
        if (shipNotes == null) {
            if (iorder.getId() != null) {
                try {
                    shipNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().IORDER_SHIPPING_NOTE, iorder.getId(), true);
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
            if (iorder.getId() != null) {
                try {
                    customerNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().IORDER_CUSTOMER_NOTE, iorder.getId(), true);
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
            if (iorder.getId() != null) {
                try {
                    internalNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().IORDER, iorder.getId(), false);
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
            if (iorder.getId() != null) {
                try {
                    sampleNotes = NoteManager.fetchByRefTableRefIdIsExt(Constants.table().IORDER_SAMPLE_NOTE, iorder.getId(), true);
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
    
    public IOrderTestManager getTests() throws Exception {
        if (tests == null) {
            if (iorder.getId() != null) {
                try {
                    tests = IOrderTestManager.fetchByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (tests == null)
                tests = IOrderTestManager.getInstance();
        }
        return tests;
    }
    
    public IOrderContainerManager getContainers() throws Exception {
        if (containers == null) {
            if (iorder.getId() != null) {
                try {
                    containers = IOrderContainerManager.fetchByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (containers == null)
                containers = IOrderContainerManager.getInstance();
        }
        return containers;
    }
    
    public AuxDataManager getAuxData() throws Exception {
        if (auxData == null) {
            if (iorder.getId() != null) {
                try {
                    auxData = AuxDataManager.fetchById(iorder.getId(), Constants.table().IORDER);
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
    
    public IOrderRecurrenceDO getRecurrence() throws Exception {
        if (recurrence == null) {
            if (iorder.getId() != null) {
                try {
                    recurrence = fetchRecurrenceByIorderId(iorder.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
    
            if (recurrence == null) 
                recurrence = new IOrderRecurrenceDO();            
        }
    
        return recurrence;
    }

    private static IOrderManagerProxy proxy() {
        if (proxy == null)
            proxy = new IOrderManagerProxy();

        return proxy;
    }
}