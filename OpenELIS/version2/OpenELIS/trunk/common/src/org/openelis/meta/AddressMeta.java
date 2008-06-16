
package org.openelis.meta;

/**
  * Address META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AddressMeta implements Meta {
  	private static final String tableName = "address";
	private static final String entityName = "Address";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="address.id",
              MULTIPLE_UNIT		    ="address.multipleUnit",
              STREET_ADDRESS		="address.streetAddress",
              CITY					="address.city",
              STATE					="address.state",
              ZIP_CODE				="address.zipCode",
              WORK_PHONE			="address.workPhone",
              HOME_PHONE			="address.homePhone",
              CELL_PHONE			="address.cellPhone",
              FAX_PHONE				="address.faxPhone",
              EMAIL					="address.email",
              COUNTRY				="address.country";


  	private static final String[] columnNames = {
  	  ID,MULTIPLE_UNIT,STREET_ADDRESS,CITY,STATE,ZIP_CODE,WORK_PHONE,HOME_PHONE,CELL_PHONE,FAX_PHONE,EMAIL,COUNTRY};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AddressMeta addressMeta = new AddressMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AddressMeta() {
        
    }
    
    public static AddressMeta getInstance() {
        return addressMeta;
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getMultipleUnit() {
    return MULTIPLE_UNIT;
  } 

  public static String getStreetAddress() {
    return STREET_ADDRESS;
  } 

  public static String getCity() {
    return CITY;
  } 

  public static String getState() {
    return STATE;
  } 

  public static String getZipCode() {
    return ZIP_CODE;
  } 

  public static String getWorkPhone() {
    return WORK_PHONE;
  } 

  public static String getHomePhone() {
    return HOME_PHONE;
  } 

  public static String getCellPhone() {
    return CELL_PHONE;
  } 

  public static String getFaxPhone() {
    return FAX_PHONE;
  } 

  public static String getEmail() {
    return EMAIL;
  } 

  public static String getCountry() {
    return COUNTRY;
  } 

  
}   
