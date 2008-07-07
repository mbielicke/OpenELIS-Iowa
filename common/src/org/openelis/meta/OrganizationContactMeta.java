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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.meta;

/**
  * OrganizationContact META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrganizationContactMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "OrganizationContact";
	
	private static final String
              ID					="id",
              ORGANIZATION_ID					="organizationId",
              CONTACT_TYPE_ID					="contactTypeId",
              NAME					="name",
              ADDRESS_ID					="addressId";

  	private static final String[] columnNames = {
  	  ID,ORGANIZATION_ID,CONTACT_TYPE_ID,NAME,ADDRESS_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrganizationContactMeta() {
		init();        
    }
    
    public OrganizationContactMeta(String path) {
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

    public String getOrganizationId() {
        return path + ORGANIZATION_ID;
    } 

    public String getContactTypeId() {
        return path + CONTACT_TYPE_ID;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getAddressId() {
        return path + ADDRESS_ID;
    } 

  
}   
