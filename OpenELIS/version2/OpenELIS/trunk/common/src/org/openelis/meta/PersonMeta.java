
package org.openelis.meta;

/**
  * Person META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PersonMeta implements Meta {
  	private static final String tableName = "person";
	private static final String entityName = "Person";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="person.id",
              LAST_NAME					="person.last_name",
              FIRST_NAME					="person.first_name",
              MIDDLE_NAME					="person.middle_name",
              ADDRESS_ID					="person.address_id";


  	private static final String[] columnNames = {
  	  ID,LAST_NAME,FIRST_NAME,MIDDLE_NAME,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PersonMeta personMeta = new PersonMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PersonMeta() {
        
    }
    
    public static PersonMeta getInstance() {
        return personMeta;
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

  public static String getLastName() {
    return LAST_NAME;
  } 

  public static String getFirstName() {
    return FIRST_NAME;
  } 

  public static String getMiddleName() {
    return MIDDLE_NAME;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  
}   
