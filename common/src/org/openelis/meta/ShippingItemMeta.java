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
  * ShippingItem META Data
  */

import java.util.HashSet;

import org.openelis.ui.common.Meta;

public class ShippingItemMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "ShippingItem";
	
	private static final String
              ID					="id",
              SHIPPING_ID					="shippingId",
              REFERENCE_TABLE_ID					="referenceTableId",
              REFERENCE_ID					="referenceId",
              QUANTITY      ="quantity",
              DESCRIPTION   ="description";

  	private static final String[] columnNames = {
  	  ID,SHIPPING_ID,REFERENCE_TABLE_ID,REFERENCE_ID,QUANTITY,DESCRIPTION};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ShippingItemMeta() {
		init();        
    }
    
    public ShippingItemMeta(String path) {
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

    public String getShippingId() {
        return path + SHIPPING_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 
    
    public String getQuantity() {
        return path + QUANTITY;
    } 
    
    public String getDescription() {
        return path + DESCRIPTION;
    }   
}   
