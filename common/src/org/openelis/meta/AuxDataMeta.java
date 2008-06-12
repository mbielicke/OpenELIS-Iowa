
package org.openelis.meta;

/**
  * AuxData META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AuxDataMeta implements Meta {
  	private static final String tableName = "aux_data";
	private static final String entityName = "AuxData";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="aux_data.id",
              SORT_ORDER_ID					="aux_data.sortOrderId",
              AUX_FIELD_ID					="aux_data.auxFieldId",
              REFERENCE_ID					="aux_data.referenceId",
              REFERENCE_TABLE_ID					="aux_data.referenceTableId",
              IS_REPORTABLE					="aux_data.isReportable",
              TYPE_ID					="aux_data.typeId",
              VALUE					="aux_data.value";


  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,AUX_FIELD_ID,REFERENCE_ID,REFERENCE_TABLE_ID,IS_REPORTABLE,TYPE_ID,VALUE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AuxDataMeta aux_dataMeta = new AuxDataMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AuxDataMeta() {
        
    }
    
    public static AuxDataMeta getInstance() {
        return aux_dataMeta;
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

  public static String getAuxFieldId() {
    return AUX_FIELD_ID;
  } 

  public static String getReferenceId() {
    return REFERENCE_ID;
  } 

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getValue() {
    return VALUE;
  } 

  
}   
