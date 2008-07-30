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

package org.openelis.meta;

/**
  * OrderItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrderItemMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "OrderItem";
	
	private static final String
              ID					="id",
              ORDER_ID					="orderId",
              INVENTORY_ITEM_ID					="inventoryItemId",
              QUANTITY_REQUESTED					="quantityRequested",
              CATALOG_NUMBER            ="catalogNumber",
              UNIT_COST                 ="unitCost";

  	private static final String[] columnNames = {
  	  ID,ORDER_ID,INVENTORY_ITEM_ID,QUANTITY_REQUESTED,CATALOG_NUMBER,UNIT_COST};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrderItemMeta() {
		init();        
    }
    
    public OrderItemMeta(String path) {
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

    public String getOrderId() {
        return path + ORDER_ID;
    } 

    public String getInventoryItemId() {
        return path + INVENTORY_ITEM_ID;
    } 

    public String getQuantityRequested() {
        return path + QUANTITY_REQUESTED;
    } 
    
    public String getCatalogNumber() {
        return path + CATALOG_NUMBER;
    } 
    
    public String getUnitCost() {
        return path + UNIT_COST;
    } 
}   
