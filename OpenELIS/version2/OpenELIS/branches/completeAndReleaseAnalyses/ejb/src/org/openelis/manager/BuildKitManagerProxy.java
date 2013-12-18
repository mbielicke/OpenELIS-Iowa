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

import org.openelis.bean.InventoryItemBean;
import org.openelis.bean.InventoryLocationBean;
import org.openelis.bean.InventoryReceiptBean;
import org.openelis.bean.InventoryXPutBean;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.User;

public class BuildKitManagerProxy {

    private static int statusProcessed;

    public BuildKitManagerProxy() {
        DictionaryDO data;

        if (statusProcessed == 0) {
            try {
                data = EJBFactory.getDictionary().fetchBySystemName("order_status_processed");
                statusProcessed = data.getId();
            } catch (Throwable e) {
                statusProcessed = 0;
                e.printStackTrace();
            }
        }
    }

    public BuildKitManager add(BuildKitManager kitMan) throws Exception {
        int i;
        Double totalCost;
        InventoryComponentManager compMan;
        InventoryReceiptManager recMan;
        InventoryReceiptViewDO kitReceipt, compReceipt;
        InventoryItemViewDO invItem;
        InventoryComponentViewDO component;
        ArrayList<InventoryXPutViewDO> xputList;
        InventoryReceiptBean rl;
        InventoryXPutBean xl;
        InventoryLocationBean ll;
        InventoryItemBean il;
        OrderManager orderMan;
        OrderViewDO order;
        OrderItemViewDO orderItem;
        ArrayList<OrderItemViewDO> orderItems;
        ArrayList<Integer> locationIdList;
        Datetime date;

        date = Datetime.getInstance(Datetime.YEAR, Datetime.DAY);

        //
        // we create an internal order in order to use up kit components
        //
        orderMan = OrderManager.getInstance();
        order = orderMan.getOrder();
        order.setNeededInDays(0);
        order.setOrderedDate(date);
        order.setRequestedBy(null);
        order.setType(OrderManager.TYPE_INTERNAL);
        order.setStatusId(statusProcessed);
        orderMan.add();
        kitMan.order = orderMan.getOrder();

        compMan = kitMan.getInventoryItem().getComponents();
        orderItems = new ArrayList<OrderItemViewDO>();
        locationIdList = new ArrayList<Integer>();
        ll = EJBFactory.getInventoryLocation();
        for (i = 0; i < compMan.count(); i++ ) {
            component = compMan.getComponentAt(i);
            //
            // it is possible that since the inventory item associated with this
            // order item is flagged as "is not inventoried" there isn't any
            // inventory location associated with the inventory item
            //
            if (component.getInventoryLocationId() == null)
                continue;

            ll.fetchForUpdate(component.getInventoryLocationId());
            orderItem = new OrderItemViewDO();
            orderItem.setOrderId(order.getId());
            orderItem.setInventoryItemId(component.getComponentId());
            orderItem.setQuantity(component.getTotal());
            locationIdList.add(component.getInventoryLocationId());
            orderItems.add(orderItem);
        }

        EJBFactory.getOrderItem().add(order, orderItems);
        //
        // fill the order
        //
        EJBFactory.getInventoryXUse().add(orderItems, locationIdList);

        //
        // calculate the total cost of the kit
        //
        xl = EJBFactory.getInventoryXPut();
        rl = EJBFactory.getInventoryReceipt();
        il = EJBFactory.getInventoryItem();
        totalCost = 0.0;
        for (i = 0; i < compMan.count(); i++ ) {
            component = compMan.getComponentAt(i);
            invItem = il.fetchById(component.getComponentId());
            //
            // there's no unit cost for inventory items that are marked as either
            // "labor" or "do not inventory",
            //
            if ( !"Y".equals(invItem.getIsLabor()) && !"Y".equals(invItem.getIsNotInventoried())) {
                xputList = xl.fetchByInventoryLocationId(component.getInventoryLocationId());
                compReceipt = rl.fetchById(xputList.get(0).getInventoryReceiptId());
                if (compReceipt.getUnitCost() != null)
                    totalCost += (compReceipt.getUnitCost() * component.getTotal());
            }
        }

        /*
         * Create an inventory receipt record for all the kits that were built. 
         * Also, since all the inventory locations updated by an inventory receipt
         * are locked and unlocked in InventoryReceiptManagerProxy, we don't need 
         * to lock and unlock them here. Locking them here causes a problem
         * with kits that are marked as "Add to existing" because the locations
         * for them are locked in that class and if they are already locked here
         * then an exception is thrown on trying to lock them again. 
         */
        kitReceipt = kitMan.getInventoryReceipt();
        kitReceipt.setInventoryItemId(kitMan.getInventoryItemId());
        kitReceipt.setReceivedDate(date);
        kitReceipt.setUnitCost(totalCost);

        recMan = InventoryReceiptManager.getInstance();
        recMan.addReceipt(kitReceipt);
        recMan.add();

        for (i = 0; i < locationIdList.size(); i++ ) 
            ll.abortUpdate(locationIdList.get(i));
        

        return kitMan;
    }

    public void validate(BuildKitManager man) throws Exception {
        Integer receivedQ, storeId;
        InventoryItemManager itemMan;
        InventoryComponentManager compMan;
        InventoryItemViewDO item, compItem;
        InventoryComponentViewDO component;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;
        InventoryItemBean il;
        ValidationErrorsList list;

        item = null;
        compMan = null;
        itemMan = man.getInventoryItem();
        receipt = man.getInventoryReceipt();
        list = new ValidationErrorsList();
        il = EJBFactory.getInventoryItem();
        storeId = null;

        if (itemMan == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), InventoryItemMeta.getName()));
        } else {
            item = itemMan.getInventoryItem();
            compMan = itemMan.getComponents();
            storeId = item.getStoreId();
        }

        //
        // location must be specified for the kit
        //       
        if (receipt.getInventoryLocations() != null) {
            location = receipt.getInventoryLocations().get(0);
            if (location.getStorageLocationId() == null)
                list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                 InventoryItemMeta.getLocationStorageLocationName()));

            if (item != null) {
                //
                // if the inventory item is flagged as "lot number required" then
                // lot number must be specified
                //
                if (DataBaseUtil.isEmpty(location.getLotNumber()) &&  "Y".equals(item.getIsLotMaintained()))
                    list.add(new FieldErrorException(Messages.get().lotNumRequiredForOrderItemException(),
                                                 InventoryItemMeta.getLocationLotNumber()));
                /*
                 * if this item is to be added to an existing location then the
                 * location specified here must already exist
                 */
                if ("Y".equals(receipt.getAddToExistingLocation()) && location.getId() == null)
                    list.add(new FieldErrorException(Messages.get().itemNotExistAtLocationException(),
                                                 InventoryItemMeta.getLocationStorageLocationName()));
                else if ("N".equals(receipt.getAddToExistingLocation()) && location.getId() != null)
                    list.add(new FieldErrorException(Messages.get().itemExistAtLocationException(),
                                                     InventoryItemMeta.getLocationStorageLocationName()));
            }
        } else {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             InventoryItemMeta.getLocationStorageLocationName()));
        }

        receivedQ = receipt.getQuantityReceived();
        if (receivedQ == null)
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), "numRequested"));
        else if (receivedQ < 1)
            list.add(new FieldErrorException(Messages.get().numRequestedMoreThanZeroException(), "numRequested"));

        //
        // only the inventory items that have components can be chosen to be
        // added
        // as kits
        //
        if (compMan == null || compMan.count() == 0) {
            list.add(new FormErrorException(Messages.get().kitAtleastOneComponentException()));
        } else {
            for (int i = 0; i < compMan.count(); i++ ) {
                component = compMan.getComponentAt(i);
                compItem = il.fetchById(component.getComponentId());

                if (component.getInventoryLocationId() == null &&
                    !"Y".equals(compItem.getIsNotInventoried()))
                    list.add(new TableFieldErrorException(Messages.get().fieldRequiredException(), i,
                                                          InventoryItemMeta.getLocationStorageLocationName(),
                                                          "componentTable"));

                //
                // the stores for all the components and the kit must be the
                // same
                //
                if ( !DataBaseUtil.isSame(storeId, compItem.getStoreId()))
                    list.add(new TableFieldErrorException(Messages.get().kitAndComponentSameStoreException(),
                                                          i,
                                                          InventoryItemMeta.getLocationStorageLocationName(),
                                                          "componentTable"));
            }
        }

        if (list.size() > 0)
            throw list;
    }
}