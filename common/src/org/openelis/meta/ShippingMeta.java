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
  * Shipping META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ShippingMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Shipping";
	
	private static final String
              ID					="id",
              STATUS_ID					="statusId",
              SHIPPED_FROM_ID					="shippedFromId",
              SHIPPED_TO_ID                 ="shippedToId",
              PROCESSED_BY_ID					="processedById",
              PROCESSED_DATE					="processedDate",
              SHIPPED_METHOD_ID					="shippedMethodId",
              SHIPPED_DATE					="shippedDate",
              NUMBER_OF_PACKAGES					="numberOfPackages",
              COST                           ="cost";

  	private static final String[] columnNames = {
  	  ID,STATUS_ID,SHIPPED_FROM_ID,SHIPPED_TO_ID,PROCESSED_BY_ID,PROCESSED_DATE,SHIPPED_METHOD_ID,SHIPPED_DATE,NUMBER_OF_PACKAGES,COST};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ShippingMeta() {
		init();        
    }
    
    public ShippingMeta(String path) {
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

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getShippedFromId() {
        return path + SHIPPED_FROM_ID;
    } 
    
    public String getShippedToId(){
        return path + SHIPPED_TO_ID;
    }

    public String getProcessedById() {
        return path + PROCESSED_BY_ID;
    } 

    public String getProcessedDate() {
        return path + PROCESSED_DATE;
    } 

    public String getShippedMethodId() {
        return path + SHIPPED_METHOD_ID;
    } 

    public String getShippedDate() {
        return path + SHIPPED_DATE;
    } 

    public String getNumberOfPackages() {
        return path + NUMBER_OF_PACKAGES;
    } 
    public String getCost(){
        return path + COST;
    }  
}   
