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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.meta.InventoryReceiptMeta;

public class InventoryReceiptManagerProxy {

    private static int statusProcessed;

    public InventoryReceiptManagerProxy() {
        DictionaryDO data;

        if (statusProcessed == 0) {
            try {
                data = dictLocal().fetchBySystemName("order_status_processed");
                statusProcessed = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                statusProcessed = 0;
            }
        }
    }

    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        int i;
        Integer qtyRec, invLocId;
        ArrayList<Integer> invLocIdList;
        InventoryReceiptLocal rl;
        InventoryLocationLocal il;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;

        rl = local();
        il = invLocLocal();
        location = null;
        invLocIdList = new ArrayList<Integer>();

        for (i = 0; i < man.count(); i++ ) {
            receipt = man.getReceiptAt(i);
            qtyRec = receipt.getQuantityReceived();
            if (receipt.getInventoryLocations() != null)
                location = receipt.getInventoryLocations().get(0);
            if (qtyRec != null && qtyRec > 0)
                //
                // if it is a new record then the inventory item in it must
                // have a valid inventory location associated with it if
                // "addToExisting" is true
                //
                if ( !"Y".equals(receipt.getAddToExistingLocation())) {
                    rl.add(receipt);
                } else if (location != null && location.getId() != null) {
                    //
                    // we lock all the inventory locations that are going to
                    // be updated
                    //
                    invLocId = location.getId();
                    il.fetchForUpdate(invLocId);
                    invLocIdList.add(invLocId);
                    rl.add(receipt);
                }
        }

        //
        // we unlock all the inventory locations that were updated
        //
        for (i = 0; i < invLocIdList.size(); i++ ) {
            invLocId = invLocIdList.get(i);
            il.abortUpdate(invLocId);
        }

        return man;
    }

    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        int sumQRec, sumQReq, i;
        Integer qtyRec, invLocId;
        ArrayList<Integer> invLocIdList;
        InventoryReceiptLocal rl;
        InventoryLocationLocal il;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;
        OrderManager orderMan;
        OrderItemManager orderItemMan;
        OrderItemViewDO orderItem;

        rl = local();
        il = invLocLocal();
        sumQRec = 0;
        location = null;
        invLocIdList = new ArrayList<Integer>();

        for (i = 0; i < man.deleteCount(); i++ )
            rl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            receipt = man.getReceiptAt(i);
            qtyRec = receipt.getQuantityReceived();
            if (receipt.getInventoryLocations() != null)
                location = receipt.getInventoryLocations().get(0);
            if (receipt.getId() == null) {
                if (qtyRec != null && qtyRec > 0) {
                    //
                    // if it's a new record then the inventory item in it must
                    // have a valid inventory location associated with it if
                    // "addToExisting" is true
                    //
                    if ( !"Y".equals(receipt.getAddToExistingLocation())) {
                        rl.add(receipt);
                    } else if (location != null && location.getId() != null) {
                        //
                        // we lock all the inventory locations that are going to
                        // be updated
                        //
                        invLocId = location.getId();
                        il.fetchForUpdate(invLocId);
                        invLocIdList.add(invLocId);
                        rl.add(receipt);
                    }
                }
            } else {
                //
                // we lock all the inventory locations that are going to
                // be updated
                //
                invLocId = location.getId();
                il.fetchForUpdate(invLocId);
                invLocIdList.add(invLocId);
                rl.update(receipt);
            }

            if (qtyRec != null)
                sumQRec += qtyRec;
        }

        //
        // we unlock all the inventory locations that were updated
        //
        for (i = 0; i < invLocIdList.size(); i++ ) {
            invLocId = invLocIdList.get(i);
            il.abortUpdate(invLocId);
        }

        orderMan = man.getOrder();

        if (orderMan == null)
            return man;

        //
        // if the total of the quantities received of all the inventory items in
        // this order is equal to the total of the quantities required by the
        // order
        // items of the order then we set the status of the order to "Processed"
        //
        sumQReq = 0;
        orderItemMan = orderMan.getItems();
        for (i = 0; i < orderItemMan.count(); i++ ) {
            orderItem = orderItemMan.getItemAt(i);
            sumQReq += orderItem.getQuantity();
        }

        if (sumQRec == sumQReq)
            orderMan.getOrder().setStatusId(statusProcessed);

        //
        // we need to update the OrderManager every time because a new note
        // may have been added to it through Inventory Receipt screen
        //
        orderMan.update();

        return man;
    }

    public void validate(InventoryReceiptManager man) throws Exception {
        Integer qtyReceived, qtyOrdered, itemId, prevItemId;
        ValidationErrorsList list;
        InventoryReceiptLocal cl;
        InventoryReceiptViewDO data;
        OrderManager orderMan;

        cl = local();
        list = new ValidationErrorsList();
        orderMan = man.getOrder();
        data = null;
        prevItemId = null;
        qtyReceived = 0;
        qtyOrdered = 0;

        for (int i = 0; i < man.count(); i++ ) {
            try {
                data = man.getReceiptAt(i);
                cl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "receiptTable", i);
            }

            if (orderMan == null)
                continue;

            itemId = data.getInventoryItemId();
            if ( !itemId.equals(prevItemId)) {
                qtyOrdered = data.getOrderItemQuantity();
                if (data.getQuantityReceived() != null)
                    qtyReceived = data.getQuantityReceived();
                else
                    qtyReceived = 0;
            } else {
                if (data.getQuantityReceived() != null) {
                    qtyReceived += data.getQuantityReceived();

                    if (qtyOrdered < qtyReceived) {
                        list.add(new TableFieldErrorException(
                                                              "numReqLessThanNumRecException",
                                                              i,
                                                              InventoryReceiptMeta.getQuantityReceived(),
                                                              "receiptTable"));
                    }
                }

            }

            prevItemId = itemId;
        }
        if (list.size() > 0)
            throw list;
    }

    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    private InventoryReceiptLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryReceiptLocal)ctx.lookup("openelis/InventoryReceiptBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private InventoryLocationLocal invLocLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryLocationLocal)ctx.lookup("openelis/InventoryLocationBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
