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

import org.openelis.gwt.common.data.DataModel;

@Remote
public interface FillOrderRemote {
    //commit a change to inventory receipt, or insert a new inventory receipt
    //public void updateInventoryReceipt(List inventoryReceipts) throws Exception;
    
    //method to query for orders
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //method to query for orders..and also unlock the necessary records
     public List queryAndUnlock(HashMap fields, DataModel model, int first, int max) throws Exception;
     
     public List getOrderAndLock(Integer orderId) throws Exception;
     
     public List getOrderAndUnlock(Integer orderId) throws Exception;
     
     //method to query order items
     public List getOrderItems(Integer orderId);
     
     //method to update internal orders
     public void updateInternalOrders(List orders) throws Exception;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
     
     public Integer getOrderItemReferenceTableId();
          
     //method to validate the fields before the backend updates it in the database
     public List validateForProcess(List orders);
}
