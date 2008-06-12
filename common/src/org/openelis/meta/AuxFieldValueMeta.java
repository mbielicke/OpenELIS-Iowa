
package org.openelis.meta;

/**
  * AuxFieldValue META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AuxFieldValueMeta implements Meta {
  	private static final String tableName = "aux_field_value";
	private static final String entityName = "AuxFieldValue";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="aux_field_value.id",
              AUX_FIELD_ID					="aux_field_value.auxFieldId",
              TYPE_ID					="aux_field_value.typeId",
              VALUE					="aux_field_value.value";


  	private static final String[] columnNames = {
  	  ID,AUX_FIELD_ID,TYPE_ID,VALUE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AuxFieldValueMeta aux_field_valueMeta = new AuxFieldValueMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AuxFieldValueMeta() {
        
    }
    
    public static AuxFieldValueMeta getInstance() {
        return aux_field_valueMeta;
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

  public static String getAuxFieldId() {
    return AUX_FIELD_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getValue() {
    return VALUE;
  } 

  
}   
