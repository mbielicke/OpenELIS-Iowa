
package org.openelis.meta;

/**
  * AuxField META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AuxFieldMeta implements Meta {
  	private static final String tableName = "aux_field";
	private static final String entityName = "AuxField";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="aux_field.id",
              SORT_ORDER_ID					="aux_field.sort_order_id",
              ANALYTE_ID					="aux_field.analyte_id",
              REFERENCE_TABLE_ID					="aux_field.reference_table_id",
              IS_REQUIRED					="aux_field.is_required",
              IS_ACTIVE					="aux_field.is_active",
              IS_REPORTABLE					="aux_field.is_reportable",
              SCRIPTLET_ID					="aux_field.scriptlet_id";


  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,ANALYTE_ID,REFERENCE_TABLE_ID,IS_REQUIRED,IS_ACTIVE,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AuxFieldMeta aux_fieldMeta = new AuxFieldMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AuxFieldMeta() {
        
    }
    
    public static AuxFieldMeta getInstance() {
        return aux_fieldMeta;
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

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getAnalyteId() {
    return ANALYTE_ID;
  } 

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getIsRequired() {
    return IS_REQUIRED;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
