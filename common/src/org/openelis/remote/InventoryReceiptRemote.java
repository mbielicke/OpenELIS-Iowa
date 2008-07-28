/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.OrderDO;

@Remote
public interface InventoryReceiptRemote {
    //method to return inventory records by order number
    public List getInventoryReceiptRecords(Integer orderId);
    
    //commit a change to inventory receipt, or insert a new inventory receipt
    public void updateInventoryReceipt(List inventoryReceipts) throws Exception;
    
    //method to query for inventory receipts
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //method to query for inventory receipts..and also lock the necessary records
     public List queryAndLock(HashMap fields, int first, int max) throws Exception;
     
     //method to query for inventory receipts..and also unlock the necessary records
     public List queryAndUnlock(HashMap fields, int first, int max) throws Exception;
     
     //auto complete lookup
     public List autoCompleteLocationLookupByName(String name, int maxResults);
     
     public List getInventoryItemsByUPC(String upc);
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
          
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(List inventoryReceipts);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(List inventoryReceipts);
}
