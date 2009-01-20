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
  * InventoryReceipt META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class InventoryReceiptMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InventoryReceipt";
	
	public static final String
              ID					="id",
              INVENTORY_ITEM_ID		="inventoryItemId",
              ORDER_ITEM_ID         ="orderItemId",
              ORGANIZATION_ID		="organizationId",
              RECEIVED_DATE			="receivedDate",
              QUANTITY_RECEIVED		="quantityReceived",
              UNIT_COST				="unitCost",
              QC_REFERENCE			="qcReference",
              EXTERNAL_REFERENCE	="externalReference",
              UPC					="upc";


  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,ORDER_ITEM_ID,ORGANIZATION_ID,RECEIVED_DATE,QUANTITY_RECEIVED,UNIT_COST,QC_REFERENCE,EXTERNAL_REFERENCE,UPC};
  	  
    private HashSet<String> columnHashList;

    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryReceiptMeta() {
        init();        
    }
    
    public InventoryReceiptMeta(String path) {
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

  public String getInventoryItemId() {
    return path + INVENTORY_ITEM_ID;
  } 
  
  public String getOrderItemId() {
      return path + ORDER_ITEM_ID;
    } 

  public String getOrganizationId() {
    return path + ORGANIZATION_ID;
  } 

  public String getReceivedDate() {
    return path + RECEIVED_DATE;
  } 

  public String getQuantityReceived() {
    return path + QUANTITY_RECEIVED;
  } 

  public String getUnitCost() {
    return path + UNIT_COST;
  } 

  public String getQcReference() {
    return path + QC_REFERENCE;
  } 

  public String getExternalReference() {
    return path + EXTERNAL_REFERENCE;
  } 

  public String getUpc() {
    return path + UPC;
  } 

  
}   
