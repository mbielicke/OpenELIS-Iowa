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

import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DataObject;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.IOrderViewDO;

/**
 * This class is used to bulk load iorder manager.
 */
public class IOrderManager1Accessor {
    /**
     * Set/get objects from order manager
     */
    public static IOrderViewDO getIorder(IOrderManager1 om) {
        return om.iorder;
    }

    public static void setIorder(IOrderManager1 om, IOrderViewDO iorder) {
        om.iorder = iorder;
    }

    public static ArrayList<IOrderOrganizationViewDO> getOrganizations(IOrderManager1 om) {
        return om.organizations;
    }

    public static void setOrganizations(IOrderManager1 om,
                                        ArrayList<IOrderOrganizationViewDO> organizations) {
        om.organizations = organizations;
    }

    public static void addOrganization(IOrderManager1 om, IOrderOrganizationViewDO organization) {
        if (om.organizations == null)
            om.organizations = new ArrayList<IOrderOrganizationViewDO>();
        om.organizations.add(organization);
    }

    public static ArrayList<IOrderItemViewDO> getItems(IOrderManager1 om) {
        return om.items;
    }

    public static void setItems(IOrderManager1 om, ArrayList<IOrderItemViewDO> items) {
        om.items = items;
    }

    public static void addItem(IOrderManager1 om, IOrderItemViewDO item) {
        if (om.items == null)
            om.items = new ArrayList<IOrderItemViewDO>();
        om.items.add(item);
    }

    public static ArrayList<InventoryXUseViewDO> getFills(IOrderManager1 om) {
        return om.fills;
    }

    public static void setFills(IOrderManager1 om, ArrayList<InventoryXUseViewDO> fills) {
        om.fills = fills;
    }

    public static void addFill(IOrderManager1 om, InventoryXUseViewDO fill) {
        if (om.fills == null)
            om.fills = new ArrayList<InventoryXUseViewDO>();
        om.fills.add(fill);
    }

    public static ArrayList<InventoryXPutViewDO> getReceipts(IOrderManager1 om) {
        return om.receipts;
    }

    public static void setReceipts(IOrderManager1 om, ArrayList<InventoryXPutViewDO> receipts) {
        om.receipts = receipts;
    }

    public static void addReceipt(IOrderManager1 om, InventoryXPutViewDO receipt) {
        if (om.receipts == null)
            om.receipts = new ArrayList<InventoryXPutViewDO>();
        om.receipts.add(receipt);
    }

    public static ArrayList<IOrderContainerDO> getContainers(IOrderManager1 om) {
        return om.containers;
    }

    public static void setContainers(IOrderManager1 om, ArrayList<IOrderContainerDO> containers) {
        om.containers = containers;
    }

    public static void addContainer(IOrderManager1 om, IOrderContainerDO container) {
        if (om.containers == null)
            om.containers = new ArrayList<IOrderContainerDO>();
        om.containers.add(container);
    }

    public static ArrayList<IOrderTestViewDO> getTests(IOrderManager1 om) {
        return om.tests;
    }

    public static void setTests(IOrderManager1 om, ArrayList<IOrderTestViewDO> tests) {
        om.tests = tests;
    }

    public static void addTest(IOrderManager1 om, IOrderTestViewDO test) {
        if (om.tests == null)
            om.tests = new ArrayList<IOrderTestViewDO>();
        om.tests.add(test);
    }

    public static ArrayList<IOrderTestAnalyteViewDO> getAnalytes(IOrderManager1 om) {
        return om.analytes;
    }

    public static void setAnalytes(IOrderManager1 om, ArrayList<IOrderTestAnalyteViewDO> analytes) {
        om.analytes = analytes;
    }

    public static void addAnalyte(IOrderManager1 om, IOrderTestAnalyteViewDO analyte) {
        if (om.analytes == null)
            om.analytes = new ArrayList<IOrderTestAnalyteViewDO>();
        om.analytes.add(analyte);
    }

    public static NoteViewDO getShippingNote(IOrderManager1 om) {
        return om.shipNote;
    }

    public static void setShippingNote(IOrderManager1 om, NoteViewDO shipNote) {
        om.shipNote = shipNote;
    }

    public static NoteViewDO getCustomerNote(IOrderManager1 om) {
        return om.custNote;
    }

    public static void setCustomerNote(IOrderManager1 om, NoteViewDO custNote) {
        om.custNote = custNote;
    }

    public static NoteViewDO getSampleNote(IOrderManager1 om) {
        return om.sampNote;
    }

    public static void setSampleNote(IOrderManager1 om, NoteViewDO sampNote) {
        om.sampNote = sampNote;
    }

    public static ArrayList<NoteViewDO> getInternalNotes(IOrderManager1 om) {
        return om.internalNotes;
    }

    public static void setInternalNotes(IOrderManager1 om, ArrayList<NoteViewDO> internalNotes) {
        om.internalNotes = internalNotes;
    }

    public static void addInternalNote(IOrderManager1 om, NoteViewDO internalNote) {
        if (om.internalNotes == null)
            om.internalNotes = new ArrayList<NoteViewDO>();
        om.internalNotes.add(internalNote);
    }

    public static ArrayList<AuxDataViewDO> getAuxilliary(IOrderManager1 om) {
        return om.auxilliary;
    }

    public static void setAuxilliary(IOrderManager1 om, ArrayList<AuxDataViewDO> auxilliary) {
        om.auxilliary = auxilliary;
    }

    public static void addAuxilliary(IOrderManager1 om, AuxDataViewDO auxilliary) {
        if (om.auxilliary == null)
            om.auxilliary = new ArrayList<AuxDataViewDO>();
        om.auxilliary.add(auxilliary);
    }

    public static IOrderRecurrenceDO getRecurrence(IOrderManager1 om) {
        return om.recurrence;
    }

    public static void setRecurrence(IOrderManager1 om, IOrderRecurrenceDO recurrence) {
        om.recurrence = recurrence;
    }
    
    public static ArrayList<AttachmentItemViewDO> getAttachments(IOrderManager1 om) {
        return om.attachments;
    }
    
    public static void setAttachments(IOrderManager1 om, ArrayList<AttachmentItemViewDO> attachments) {
        om.attachments = attachments;
    }
    
    public static void addAttachment(IOrderManager1 om, AttachmentItemViewDO attachment) {
        if (om.attachments == null)
            om.attachments = new ArrayList<AttachmentItemViewDO>();
        om.attachments.add(attachment);
    }

    public static ArrayList<DataObject> getRemoved(IOrderManager1 om) {
        return om.removed;
    }

    public static void setRemoved(IOrderManager1 om, ArrayList<DataObject> removed) {
        om.removed = removed;
    }
}