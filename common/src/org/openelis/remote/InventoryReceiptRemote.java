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

import org.openelis.domain.InventoryReceiptDO;

@Remote
public interface InventoryReceiptRemote {
    public static final String  RECEIPT          = "receipt",
                                TRANSFER          = "transfer";
    
    //method to return inventory records by order number
    public List getInventoryReceiptRecords(Integer orderId);
    
    //commit a change to inventory receipt, or insert a new inventory receipt
    public void updateInventoryReceipt(List inventoryReceipts) throws Exception;
    
    //commit a change to inventory transfer, or insert a new inventory transfer
    public void updateInventoryTransfer(List inventorytransfers) throws Exception;
    
    //method to query for inventory receipts
     public List query(HashMap fields, int first, int max, boolean receipt) throws Exception;
     
     //method to query for inventory receipts..and also lock the necessary records
     public List queryAndLock(HashMap fields, int first, int max, boolean receipt) throws Exception;
     
     //method to query for inventory receipts..and also unlock the necessary records
     public List queryAndUnlock(HashMap fields, int first, int max, boolean receipt) throws Exception;
     
     //auto complete lookup
     public List autoCompleteLocationLookupByName(String name, int maxResults);
     
     //auto complete lookup
     public List autoCompleteLocationLookupByNameInvId(String name, Integer invId, int maxResults);
     
     public List getInventoryItemsByUPC(String upc);
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
          
     //method to validate the receipt fields before the backend updates it in the database
     public void validateReceipts(List<InventoryReceiptDO> inventoryReceipts) throws Exception;
     
     //method to validate the transfer fields before the backend updates it in the database     
     public void validateTransfers(List<InventoryReceiptDO> inventoryTransfers) throws Exception;
}
