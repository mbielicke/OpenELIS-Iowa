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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.InventoryXPutDO;
import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.InventoryXPutLocal;
import org.openelis.local.LoginLocal;
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
       Integer storeId;
       Double totalCost;
       InventoryComponentManager compMan;
       InventoryReceiptManager recMan;
       InventoryReceiptViewDO kitReceipt, compReceipt;
       InventoryItemViewDO invItem;
       InventoryComponentViewDO component;
       InventoryXUseViewDO fill;
       InventoryXPutDO xput;
       InventoryReceiptLocal rl;
       InventoryXPutLocal xl;
       InventoryItemLocal il;
       OrderManager orderMan;
       OrderItemManager itemMan; 
       OrderViewDO order;
       OrderItemViewDO orderItem;
       OrderFillManager orderFillMan;
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
       
       compMan = kitMan.getInventoryItem().getComponents();
       itemMan = orderMan.getItems();
       storeId =  kitMan.getInventoryItem().getInventoryItem().getStoreId();
       for (i = 0; i < compMan.count(); i++) {
           component = compMan.getComponentAt(i);
           orderItem = itemMan.getItemAt(itemMan.addItem());
           orderItem.setInventoryItemId(component.getComponentId());
           orderItem.setQuantity(component.getTotal());
           orderItem.setStoreId(storeId);  
       }       
       orderMan.add();
       
       //
       // fill the order 
       //
       itemMan = orderMan.getItems();
       orderFillMan = orderMan.getFills();
       for (i = 0; i < itemMan.count(); i++) {
           fill = orderFillMan.getFillAt(orderFillMan.addFill());           
           orderItem = itemMan.getItemAt(i);      
           component = compMan.getComponentAt(i);
           fill.setInventoryLocationId(component.getInventoryLocationId());
           fill.setOrderItemId(orderItem.getId());
           fill.setQuantity(orderItem.getQuantity());
       }
       orderMan.update();
       
       //
       // calculate the total cost of the kit 
       //
       xl = xputLocal();
       rl = receiptLocal();
       il = itemLocal();
       totalCost = 0.0;
       for (i = 0; i < compMan.count(); i++) {
           component = compMan.getComponentAt(i);        
           invItem = il.fetchById(component.getComponentId());
           //
           // there's no unit cost for inventory items that are marked as either
           // "labor" or "do not inventory", 
           //
           if (!"Y".equals(invItem.getIsLabor()) && !"Y".equals(invItem.getIsNotInventoried())) {
               xput = xl.fetchByInventoryLocationId(component.getInventoryLocationId());
               compReceipt = rl.fetchById(xput.getInventoryReceiptId());
               if (compReceipt.getUnitCost() != null) 
                   totalCost += (compReceipt.getUnitCost() * component.getTotal());               
           }
       }
       
       //
       // create an inventory receipt record for all the kits that were built 
       //
       kitReceipt = kitMan.getInventoryReceipt();
       kitReceipt.setInventoryItemId(kitMan.getInventoryItemId());
       kitReceipt.setReceivedDate(date);
       kitReceipt.setUnitCost(totalCost);
       
       recMan = InventoryReceiptManager.getInstance();
       recMan.addReceipt(kitReceipt);
       recMan.add();      
       
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
       il = itemLocal();
       storeId = null;
       
       if (DataBaseUtil.isEmpty(itemMan)) {
           list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getName()));
       } else {
           item = itemMan.getInventoryItem();
           compMan = itemMan.getComponents();
           storeId = item.getStoreId();
       }
       
       if (receipt.getInventoryLocations() != null) {
           location = receipt.getInventoryLocations().get(0);
           if (location.getStorageLocationId() == null) 
               list.add(new FieldErrorException("fieldRequiredException", InventoryItemMeta.getLocationStorageLocationName()));
           
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
       
       if (DataBaseUtil.isEmpty(compMan) || compMan.count() == 0) {
           list.add(new FormErrorException("kitAtleastOneComponentException"));
       } else {
           for (int i = 0; i < compMan.count(); i++) {
               component = compMan.getComponentAt(i);
               if (component.getInventoryLocationId() == null) 
                   list.add(new TableFieldErrorException("fieldRequiredException", i, InventoryItemMeta.getLocationStorageLocationName(), "componentTable"));
               
               compItem = il.fetchById(component.getComponentId());
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
   
   private InventoryItemLocal itemLocal() {
       try {
           InitialContext ctx = new InitialContext();
           return (InventoryItemLocal)ctx.lookup("openelis/InventoryItemBean/local")  ;
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
