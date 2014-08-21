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
  * Address META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class AddressMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Address";
	
	private static final String
              ID					="id",
              MULTIPLE_UNIT			="multipleUnit",
              STREET_ADDRESS		="streetAddress",
              CITY					="city",
              STATE					="state",
              ZIP_CODE				="zipCode",
              WORK_PHONE			="workPhone",
              HOME_PHONE			="homePhone",
              CELL_PHONE			="cellPhone",
              FAX_PHONE				="faxPhone",
              EMAIL					="email",
              COUNTRY				="country";

  	private static final String[] columnNames = {
  	  ID,MULTIPLE_UNIT,STREET_ADDRESS,CITY,STATE,ZIP_CODE,WORK_PHONE,HOME_PHONE,CELL_PHONE,FAX_PHONE,EMAIL,COUNTRY};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AddressMeta() {
		init();        
    }
    
    public AddressMeta(String path) {
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

    public String getMultipleUnit() {
        return path + MULTIPLE_UNIT;
    } 

    public String getStreetAddress() {
        return path + STREET_ADDRESS;
    } 

    public String getCity() {
        return path + CITY;
    } 

    public String getState() {
        return path + STATE;
    } 

    public String getZipCode() {
        return path + ZIP_CODE;
    } 

    public String getWorkPhone() {
        return path + WORK_PHONE;
    } 

    public String getHomePhone() {
        return path + HOME_PHONE;
    } 

    public String getCellPhone() {
        return path + CELL_PHONE;
    } 

    public String getFaxPhone() {
        return path + FAX_PHONE;
    } 

    public String getEmail() {
        return path + EMAIL;
    } 

    public String getCountry() {
        return path + COUNTRY;
    } 

  
}   
