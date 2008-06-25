
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
              ORGANIZATION_ID		="organizationId",
              RECEIVED_DATE			="receivedDate",
              QUANTITY_RECEIVED		="quantityReceived",
              UNIT_COST				="unitCost",
              QC_REFERENCE			="qcReference",
              EXTERNAL_REFERENCE	="externalReference",
              UPC					="upc";


  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,ORGANIZATION_ID,RECEIVED_DATE,QUANTITY_RECEIVED,UNIT_COST,QC_REFERENCE,EXTERNAL_REFERENCE,UPC};
  	  
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
