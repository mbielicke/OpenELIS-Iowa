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

import org.openelis.bean.InventoryLocationBean;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.IOrderItemViewDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class InventoryTransferManagerProxy {

    private static int statusProcessed;

    public InventoryTransferManagerProxy() {
        DictionaryDO data;

        if (statusProcessed == 0) {
            try {
                data = EJBFactory.getDictionary().fetchBySystemName("order_status_processed");
                statusProcessed = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                statusProcessed = 0;
            }
        }
    }

    public InventoryTransferManager add(InventoryTransferManager man) throws Exception {
        int i;
        Integer fromItemId, toItemId, fromLocId, toLocId, quantity, parentRatio;
        InventoryReceiptManager recMan;
        InventoryReceiptViewDO receipt;
        IOrderManager iorderMan;
        IOrderViewDO iorder;
        IOrderItemViewDO iorderItem;
        ArrayList<IOrderItemViewDO> iorderItems;
        ArrayList<Integer> locationIdList;
        InventoryLocationBean ll;
        Datetime date;

        date = Datetime.getInstance(Datetime.YEAR, Datetime.MONTH);

        //
        // we create an internal order in order to use up kit components
        //
        iorderMan = IOrderManager.getInstance();
        iorder = iorderMan.getIorder();
        iorder.setNeededInDays(0);
        iorder.setOrderedDate(date);
        iorder.setRequestedBy(null);
        iorder.setType(IOrderManager.TYPE_INTERNAL);
        iorder.setStatusId(statusProcessed);
        iorderMan.add();

        iorderItems = new ArrayList<IOrderItemViewDO>();
        locationIdList = new ArrayList<Integer>();
        recMan = InventoryReceiptManager.getInstance();
        ll = EJBFactory.getInventoryLocation();
        for (i = 0; i < man.count(); i++ ) {
            iorderItem = new IOrderItemViewDO();
            iorderItem.setIorderId(iorder.getId());
            fromItemId = man.getFromInventoryItemAt(i).getId();
            iorderItem.setInventoryItemId(fromItemId);
            quantity = man.getQuantityAt(i);
            iorderItem.setQuantity(quantity);
            fromLocId = man.getFromInventoryLocationAt(i).getId();
            ll.fetchForUpdate(fromLocId);
            locationIdList.add(fromLocId);
            //
            // create an inventory receipt record for each "to inventory item"
            //
            receipt = new InventoryReceiptViewDO();
            toItemId = man.getToInventoryItemAt(i).getId();
            receipt.setInventoryItemId(toItemId);
            receipt.setInventoryLocations(new ArrayList<InventoryLocationViewDO>());
            toLocId = man.getToInventoryLocationAt(i).getId();
            if (toLocId != null)
                ll.fetchForUpdate(toLocId);
            receipt.getInventoryLocations().add(man.getToInventoryLocationAt(i));
            //
            // quantities of the same item can be transferred between locations,
            // in which case parent ratio is 1
            //
            if (fromItemId.equals(toItemId))
                parentRatio = 1;
            else
                parentRatio = man.getToInventoryItemAt(i).getParentRatio();
            receipt.setQuantityReceived(quantity * parentRatio);
            receipt.setReceivedDate(date);
            receipt.setAddToExistingLocation(man.getAddtoExistingAt(i));
            recMan.addReceipt(receipt);
            iorderItems.add(iorderItem);
        }

        EJBFactory.getIOrderItem().add(iorder, iorderItems);
        //
        // fill the order
        //
        EJBFactory.getInventoryXUse().add(iorderItems, locationIdList);

        //
        // add all the inventory receipts
        //
        recMan.add();

        for (i = 0; i < man.count(); i++ ) {
            fromLocId = man.getFromInventoryLocationAt(i).getId();
            ll.abortUpdate(fromLocId);

            toLocId = man.getToInventoryLocationAt(i).getId();
            if (toLocId != null)
                ll.abortUpdate(toLocId);
        }
        return man;
    }

    public void validate(InventoryTransferManager man) throws Exception {
        ValidationErrorsList list;
        Integer onHandQ, receivedQ;

        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            onHandQ = null;
            if (DataBaseUtil.isEmpty(man.getFromInventoryItemAt(i))) {
                list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i, "fromItemName",
                                                      "receiptTable"));
                onHandQ = null;
            }

            if (DataBaseUtil.isEmpty(man.getFromInventoryLocationAt(i)))
                list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i, "fromLoc",
                                                      "receiptTable"));
            else
                onHandQ = man.getFromInventoryLocationAt(i).getQuantityOnhand();

            if (DataBaseUtil.isEmpty(man.getToInventoryItemAt(i)))
                list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i, "toItemName",
                                                      "receiptTable"));

            if (DataBaseUtil.isEmpty(man.getToInventoryLocationAt(i)))
                list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i, "toLoc",
                                                      "receiptTable"));

            receivedQ = man.getQuantityAt(i);
            if (DataBaseUtil.isEmpty(receivedQ)) {
                list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i, "qtyReceived",
                                                      "receiptTable"));
            } else if (receivedQ < 1) {
                list.add(new TableFieldErrorException(Messages.get().qtyRecMoreThanZeroException(), i,
                                                      "qtyReceived", "receiptTable"));
            } else if ( !DataBaseUtil.isEmpty(onHandQ) && onHandQ < receivedQ) {
                list.add(new TableFieldErrorException(Messages.get().qtyOnHandLessThanQtyRecException(), i,
                                                      "qtyReceived", "receiptTable"));
            }
        }

        if (list.size() > 0)
            throw list;
    }
}
