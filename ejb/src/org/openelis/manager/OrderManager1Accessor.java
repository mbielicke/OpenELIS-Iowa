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

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DataObject;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestAnalyteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;

/**
 * This class is used to bulk load order manager.
 */
public class OrderManager1Accessor {
    /**
     * Set/get objects from order manager
     */
    public static OrderViewDO getOrder(OrderManager1 om) {
        return om.order;
    }

    public static void setOrder(OrderManager1 om, OrderViewDO order) {
        om.order = order;
    }

    public static ArrayList<OrderOrganizationViewDO> getOrganizations(OrderManager1 om) {
        return om.organizations;
    }

    public static void setOrganizations(OrderManager1 om,
                                        ArrayList<OrderOrganizationViewDO> organizations) {
        om.organizations = organizations;
    }

    public static void addOrganization(OrderManager1 om, OrderOrganizationViewDO organization) {
        if (om.organizations == null)
            om.organizations = new ArrayList<OrderOrganizationViewDO>();
        om.organizations.add(organization);
    }

    public static ArrayList<OrderItemViewDO> getItems(OrderManager1 om) {
        return om.items;
    }

    public static void setItems(OrderManager1 om, ArrayList<OrderItemViewDO> items) {
        om.items = items;
    }

    public static void addItem(OrderManager1 om, OrderItemViewDO item) {
        if (om.items == null)
            om.items = new ArrayList<OrderItemViewDO>();
        om.items.add(item);
    }

    public static ArrayList<InventoryXUseViewDO> getFills(OrderManager1 om) {
        return om.fills;
    }

    public static void setFills(OrderManager1 om, ArrayList<InventoryXUseViewDO> fills) {
        om.fills = fills;
    }

    public static void addFill(OrderManager1 om, InventoryXUseViewDO fill) {
        if (om.fills == null)
            om.fills = new ArrayList<InventoryXUseViewDO>();
        om.fills.add(fill);
    }

    public static ArrayList<InventoryXPutViewDO> getReceipts(OrderManager1 om) {
        return om.receipts;
    }

    public static void setReceipts(OrderManager1 om, ArrayList<InventoryXPutViewDO> receipts) {
        om.receipts = receipts;
    }

    public static void addReceipt(OrderManager1 om, InventoryXPutViewDO receipt) {
        if (om.receipts == null)
            om.receipts = new ArrayList<InventoryXPutViewDO>();
        om.receipts.add(receipt);
    }

    public static ArrayList<OrderContainerDO> getContainers(OrderManager1 om) {
        return om.containers;
    }

    public static void setContainers(OrderManager1 om, ArrayList<OrderContainerDO> containers) {
        om.containers = containers;
    }

    public static void addContainer(OrderManager1 om, OrderContainerDO container) {
        if (om.containers == null)
            om.containers = new ArrayList<OrderContainerDO>();
        om.containers.add(container);
    }

    public static ArrayList<OrderTestViewDO> getTests(OrderManager1 om) {
        return om.tests;
    }

    public static void setTests(OrderManager1 om, ArrayList<OrderTestViewDO> tests) {
        om.tests = tests;
    }

    public static void addTest(OrderManager1 om, OrderTestViewDO test) {
        if (om.tests == null)
            om.tests = new ArrayList<OrderTestViewDO>();
        om.tests.add(test);
    }

    public static ArrayList<OrderTestAnalyteViewDO> getAnalytes(OrderManager1 om) {
        return om.analytes;
    }

    public static void setAnalytes(OrderManager1 om, ArrayList<OrderTestAnalyteViewDO> analytes) {
        om.analytes = analytes;
    }

    public static void addAnalyte(OrderManager1 om, OrderTestAnalyteViewDO analyte) {
        if (om.analytes == null)
            om.analytes = new ArrayList<OrderTestAnalyteViewDO>();
        om.analytes.add(analyte);
    }

    public static NoteViewDO getShippingNote(OrderManager1 om) {
        return om.shipNote;
    }

    public static void setShippingNote(OrderManager1 om, NoteViewDO shipNote) {
        om.shipNote = shipNote;
    }

    public static NoteViewDO getCustomerNote(OrderManager1 om) {
        return om.custNote;
    }

    public static void setCustomerNote(OrderManager1 om, NoteViewDO custNote) {
        om.custNote = custNote;
    }

    public static NoteViewDO getSampleNote(OrderManager1 om) {
        return om.sampNote;
    }

    public static void setSampleNote(OrderManager1 om, NoteViewDO sampNote) {
        om.sampNote = sampNote;
    }

    public static ArrayList<NoteViewDO> getInternalNotes(OrderManager1 om) {
        return om.internalNotes;
    }

    public static void setInternalNotes(OrderManager1 om, ArrayList<NoteViewDO> internalNotes) {
        om.internalNotes = internalNotes;
    }

    public static void addInternalNote(OrderManager1 om, NoteViewDO internalNote) {
        if (om.internalNotes == null)
            om.internalNotes = new ArrayList<NoteViewDO>();
        om.internalNotes.add(internalNote);
    }

    public static ArrayList<AuxDataViewDO> getAuxilliary(OrderManager1 om) {
        return om.auxilliary;
    }

    public static void setAuxilliary(OrderManager1 om, ArrayList<AuxDataViewDO> auxilliary) {
        om.auxilliary = auxilliary;
    }

    public static void addAuxilliary(OrderManager1 om, AuxDataViewDO auxilliary) {
        if (om.auxilliary == null)
            om.auxilliary = new ArrayList<AuxDataViewDO>();
        om.auxilliary.add(auxilliary);
    }

    public static OrderRecurrenceDO getRecurrence(OrderManager1 om) {
        return om.recurrence;
    }

    public static void setRecurrence(OrderManager1 om, OrderRecurrenceDO recurrence) {
        om.recurrence = recurrence;
    }

    public static ArrayList<DataObject> getRemoved(OrderManager1 om) {
        return om.removed;
    }

    public static void setRemoved(OrderManager1 om, ArrayList<DataObject> removed) {
        om.removed = removed;
    }
}