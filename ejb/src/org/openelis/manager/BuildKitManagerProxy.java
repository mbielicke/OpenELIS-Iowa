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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.InventoryXPutDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryLocationLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.InventoryXUseLocal;
import org.openelis.local.LoginLocal;
import org.openelis.local.OrderItemLocal;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.utilcommon.DataBaseUtil;

public class BuildKitManagerProxy {
    
    private static int               statusProcessed;
        
    private static final Logger  log  = Logger.getLogger(InventoryReceiptManagerProxy.class.getName());
    
    public BuildKitManagerProxy() {
        DictionaryDO data;
        
        try {
            data = dictLocal().fetchBySystemName("order_status_processed");
            statusProcessed = data.getId();
        } catch (Throwable e) {
            statusProcessed = 0;
            log.log(Level.SEVERE,
                    "Failed to lookup dictionary entry by system name='order_status_processed'", e);
        }
    }
    
   public BuildKitManager add(BuildKitManager kitMan) throws Exception {
        int i;
        Integer kitLocId;
        Double totalCost;
        InventoryComponentManager compMan;
        InventoryReceiptManager recMan;
        InventoryReceiptViewDO kitReceipt, compReceipt;
        InventoryItemViewDO invItem;
        InventoryComponentViewDO component;
        ArrayList<InventoryXPutDO> xputList;
        InventoryReceiptLocal rl;
        InventoryXPutLocal xl;
        InventoryLocationLocal ll;
        InventoryItemLocal il;
        OrderManager orderMan;
        OrderViewDO order;
        OrderItemViewDO orderItem;
        ArrayList<OrderItemViewDO> orderItems;
        ArrayList<Integer> locationIdList;

        Datetime date;

        date = Datetime.getInstance(Datetime.YEAR, Datetime.MONTH);

        //
        // we create an internal order in order to use up kit components
        //
        orderMan = OrderManager.getInstance();
        order = orderMan.getOrder();
        order.setNeededInDays(0);
        order.setOrderedDate(date);
        order.setRequestedBy(loginLocal().getSecurityUtil().getSystemUserName());
        order.setType(OrderManager.TYPE_INTERNAL);
        order.setStatusId(statusProcessed);
        orderMan.add();

        compMan = kitMan.getInventoryItem().getComponents();
        orderItems = new ArrayList<OrderItemViewDO>();
        locationIdList = new ArrayList<Integer>();
        ll = invLocLocal();
        for (i = 0; i < compMan.count(); i++ ) {
            component = compMan.getComponentAt(i);
            orderItem = new OrderItemViewDO();
            orderItem.setOrderId(order.getId());
            orderItem.setInventoryItemId(component.getComponentId());
            orderItem.setQuantity(component.getTotal());
            ll.fetchForUpdate(component.getInventoryLocationId());
            locationIdList.add(component.getInventoryLocationId());
            orderItems.add(orderItem);
        }

        orderItemLocal().add(order, orderItems);
        //
        // fill the order
        //
        xuseLocal().add(orderItems, locationIdList);

        //
        // calculate the total cost of the kit
        //
        xl = xputLocal();
        rl = receiptLocal();
        il = invItemLocal();
        totalCost = 0.0;
        for (i = 0; i < compMan.count(); i++ ) {
            component = compMan.getComponentAt(i);
            invItem = il.fetchById(component.getComponentId());
            //
            // there's no unit cost for inventory items that are marked as
            // either
            // "labor" or "do not inventory",
            //
            if ( !"Y".equals(invItem.getIsLabor()) && !"Y".equals(invItem.getIsNotInventoried())) {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%1");
                xputList = xl.fetchByInventoryLocationId(component.getInventoryLocationId());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%2");
                compReceipt = rl.fetchById(xputList.get(0).getInventoryReceiptId());
                if (compReceipt.getUnitCost() != null)
                    totalCost += (compReceipt.getUnitCost() * component.getTotal());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%3");
            }
        }

        //
        // create an inventory receipt record for all the kits that were built
        //
        kitReceipt = kitMan.getInventoryReceipt();
        kitLocId = kitReceipt.getInventoryLocations().get(0).getId();
        if (kitLocId != null)
            ll.fetchForUpdate(kitLocId);
        kitReceipt.setInventoryItemId(kitMan.getInventoryItemId());        
        kitReceipt.setReceivedDate(date);
        kitReceipt.setUnitCost(totalCost);

        recMan = InventoryReceiptManager.getInstance();
        recMan.addReceipt(kitReceipt);
        recMan.add();

        for (i = 0; i < locationIdList.size(); i++ ) {
            ll.abortUpdate(locationIdList.get(i));
            if (kitLocId != null)
                ll.abortUpdate(kitLocId);
        }

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
       InventoryItemLocal il;
       ValidationErrorsList list;
       
       item = null;
       compMan = null;
       itemMan = man.getInventoryItem();       
       receipt = man.getInventoryReceipt();
       list = new ValidationErrorsList();
       il = invItemLocal();
       storeId = null;
       
       if (DataBaseUtil.isEmpty(itemMan)) {
           list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getName()));
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
               list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getLocationStorageLocationName()));
           
           //
           // if the inventory item is flagged as "lot numnber required" then 
           // lot number must be specified
           //
           if (item != null && location.getLotNumber() == null && "Y".equals(item.getIsLotMaintained())) 
                list.add(new FieldErrorException("lotNumRequiredForOrderItemException", 
                                                 InventoryItemMeta.getLocationLotNumber()));                                
       } else {
           list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getLocationStorageLocationName()));
       }   
       
       receivedQ = receipt.getQuantityReceived();
       if (DataBaseUtil.isEmpty(receivedQ))                    
           list.add(new FieldErrorException("fieldRequiredException", "numRequested"));
       else if (receivedQ < 0)
           list.add(new FieldErrorException("numRequestedMoreThanZeroException", "numRequested"));
       
       //
       // only the inventory items that have components can be chosen to be added
       // as kits
       //
       if (DataBaseUtil.isEmpty(compMan) || compMan.count() == 0) {
           list.add(new FormErrorException("kitAtleastOneComponentException"));
       } else {
           for (int i = 0; i < compMan.count(); i++) {
               component = compMan.getComponentAt(i);
               if (component.getInventoryLocationId() == null) 
                   list.add(new TableFieldErrorException("fieldRequiredException", i, InventoryItemMeta.getLocationStorageLocationName(), "componentTable"));
               
               compItem = il.fetchById(component.getComponentId());
               //
               // the stores for all the components and the kit must be the same  
               //
               if (!storeId.equals(compItem.getStoreId())) 
                   list.add(new TableFieldErrorException("kitAndComponentSameStoreException", i, InventoryItemMeta.getLocationStorageLocationName(), "componentTable"));               
           }
       }
       
       if (list.size() > 0)
           throw list;
   }
    
   private DictionaryLocal dictLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }  
   
   private InventoryItemLocal invItemLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryItemLocal)ctx.lookup("openelis/InventoryItemBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private OrderItemLocal orderItemLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (OrderItemLocal)ctx.lookup("openelis/OrderItemBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private LoginLocal loginLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (LoginLocal)ctx.lookup("openelis/LoginBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private InventoryXUseLocal xuseLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryXUseLocal)ctx.lookup("openelis/InventoryXUseBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private InventoryLocationLocal invLocLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryLocationLocal)ctx.lookup("openelis/InventoryLocationBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private InventoryXPutLocal xputLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryXPutLocal)ctx.lookup("openelis/InventoryXPutBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
   
   private InventoryReceiptLocal receiptLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryReceiptLocal)ctx.lookup("openelis/InventoryReceiptBean/local")  ;
       } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
       }
   }
}
