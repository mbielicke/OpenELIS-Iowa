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
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;

@Remote
public interface OrderRemote {
    public static final String  INTERNAL          = "internal",
                                EXTERNAL          = "external",
                                KITS              = "kits";
    
    //method to return order record
    public OrderDO getOrder(Integer orderId, String orderType);
    
    //method to unlock entity and return order record
    public OrderDO getOrderAndUnlock(Integer orderId, String orderType, String session);
    
    //method to lock entity and return order
    public OrderDO getOrderAndLock(Integer orderId, String orderType, String session) throws Exception;
    
    //commit a change to order, or insert a new order record
    public Integer updateOrder(OrderDO orderDO, String orderType, List items, NoteDO customerNoteDO, NoteDO orderShippingNotes) throws Exception;
    
    //method to return customer notes
    public NoteDO getCustomerNote(Integer orderId);
    
    //method to return order/shipping notes
    public NoteDO getOrderShippingNote(Integer orderId);
    
    //method to return order items
    public List getOrderItems(Integer orderId);
    
    //method to return receipts
    public List getOrderReceipts(Integer orderId);
    
    public List getOrderLocTransactions(Integer orderId);
    
    //method to return report to bill to addresses
    public BillToReportToDO getBillToReportTo(Integer orderId);
    
    //method to query for orders
     public List query(HashMap fields, int first, int max, String orderType) throws Exception;
     
     public OrderAddAutoFillDO getAddAutoFillValues() throws Exception;
     
     //auto complete order description lookup
     public List orderDescriptionAutoCompleteLookup(String desc, int maxResults);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(OrderDO orderDO, String orderType, List items,  boolean validateOrderQty);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(OrderDO orderDO, String orderType, List items);
     
     //method to validate the quantities on hand
     public List validateQuantities(List items);
}
