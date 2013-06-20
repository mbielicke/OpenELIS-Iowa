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

import org.openelis.domain.InventoryAdjustmentAddAutoFillDO;
import org.openelis.domain.InventoryAdjustmentDO;

@Remote
public interface InventoryAdjustmentRemote {
    //method to return adjustment record
    public InventoryAdjustmentDO getInventoryAdjustment(Integer inventoryAdjustmentId);
    
    //method to unlock entity and return adjustment record
    public InventoryAdjustmentDO getInventoryAdjustmentAndUnlock(Integer inventoryAdjustmentId);
    
    //method to lock entity and return adjustment record
    public InventoryAdjustmentDO getInventoryAdjustmentAndLock(Integer inventoryAdjustmentId) throws Exception;
    
    //commit a change to adjustment, or insert a new adjustment
    public Integer updateInventoryAdjustment(InventoryAdjustmentDO inventoryAdjustmentDO, List children) throws Exception;
    
    //method to return just child adjustment records
    public List getChildRecords(Integer inventoryAdjustmentId);
    
    public List getChildRecordsAndLock(Integer inventoryAdjustmentId) throws Exception;
    
    public List getChildRecordsAndUnlock(Integer inventoryAdjustmentId);
    
    public List getInventoryitemData(Integer inventoryLocationId, Integer storeId);
    
    //method to query for adjustments
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
     
     public InventoryAdjustmentAddAutoFillDO getAddAutoFillValues() throws Exception;
     
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(InventoryAdjustmentDO inventoryAdjustmentDO, List children);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(InventoryAdjustmentDO inventoryAdjustmentDO, List children);
}