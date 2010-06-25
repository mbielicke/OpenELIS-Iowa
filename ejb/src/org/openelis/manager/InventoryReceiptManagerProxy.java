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
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrderItemViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.OrderLocal;
import org.openelis.local.OrganizationContactLocal;
import org.openelis.utilcommon.DataBaseUtil;


public class InventoryReceiptManagerProxy {    
    
    private static int               statusProcessed;
    
    private static final Logger      log  = Logger.getLogger(InventoryReceiptManagerProxy.class.getName());
    
    public InventoryReceiptManagerProxy() {
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
    
    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        InventoryReceiptLocal rl;     
        Integer qtyRec;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;
        
        rl = local();        
        for (int i = 0; i < man.count(); i++ ) {             
            receipt = man.getReceiptAt(i);
            qtyRec = receipt.getQuantityReceived();
            location = receipt.getInventoryLocations().get(0);
            if((qtyRec != null && qtyRec > 0))
                //
                // if it's a new record then the inventory item in it must 
                // have a valid inventory location associated with it if 
                // "addToExisting" is true
                //
                if(!"Y".equals(receipt.getAddToExistingLocation()))
                    rl.add(receipt);
                else if(location.getId() != null)
                    rl.add(receipt);
        }
        
        return man;
    }
    
    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        int sumQRec, sumQReq, i;
        Integer qtyRec;
        InventoryReceiptLocal rl;
        InventoryReceiptViewDO receipt;
        InventoryLocationViewDO location;
        OrderManager orderMan;
        OrderItemManager orderItemMan;
        OrderItemViewDO orderItem;
                
        rl = local();
        sumQRec = 0;
        for (i = 0; i < man.deleteCount(); i++ )
            rl.delete(man.getDeletedAt(i));
        
        for (i = 0; i < man.count(); i++ ) {                         
            receipt = man.getReceiptAt(i);
            qtyRec = receipt.getQuantityReceived();
            location = receipt.getInventoryLocations().get(0);
            if (receipt.getId() == null) {
                if((qtyRec != null && qtyRec > 0)) {
                    //
                    // if it's a new record then the inventory item in it must 
                    // have a valid inventory location associated with it if 
                    // "addToExisting" is true
                    //
                    if(!"Y".equals(receipt.getAddToExistingLocation()))
                        rl.add(receipt);
                    else if(location.getId() != null)
                        rl.add(receipt);
                }
            } else {
                rl.update(receipt);
            }
            
            if(qtyRec != null) 
                sumQRec += qtyRec;            
        }
        
        orderMan = man.getOrder();        
        if(orderMan != null) {
            sumQReq = 0;
            orderItemMan = orderMan.getItems();
            for (i = 0; i < orderItemMan.count(); i++) {
                orderItem = orderItemMan.getItemAt(i);
                sumQReq += orderItem.getQuantity();
            }
            
            if(sumQRec == sumQReq) 
                orderMan.getOrder().setStatusId(statusProcessed);   
            
            //
            // we need to update the OrderManager every time because a new note
            // may have been added to it through Inventory Receipt screen 
            //
            orderMan.update();
        }                       
        
        return man;
    }
    
    public void validate(InventoryReceiptManager man) throws Exception {
        ValidationErrorsList list;
        InventoryReceiptLocal cl;

        cl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getReceiptAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "receiptTable", i);
            }
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
}
