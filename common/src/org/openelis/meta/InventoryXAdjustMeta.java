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

package org.openelis.meta;

/**
  * TransAdjustmentLocation META Data
  */

import java.util.HashSet;

import org.openelis.ui.common.Meta;

public class InventoryXAdjustMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InventoryXAdjust";
	
	private static final String
              ID					="id",
              INVENTORY_ADJUSTMENT_ID					="inventoryAdjustmentId",
              INVENTORY_LOCATION_ID					="inventoryLocationId",
              QUANTITY					="quantity",
              PHYSICAL_COUNT					="physicalCount";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_ADJUSTMENT_ID,INVENTORY_LOCATION_ID,QUANTITY,PHYSICAL_COUNT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryXAdjustMeta() {
		init();        
    }
    
    public InventoryXAdjustMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getInventoryAdjustmentId() {
        return path + INVENTORY_ADJUSTMENT_ID;
    } 

    public String getInventoryLocationId() {
        return path + INVENTORY_LOCATION_ID;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

    public String getPhysicalCount() {
        return path + PHYSICAL_COUNT;
    } 

  
}   
