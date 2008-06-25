
package org.openelis.meta;

/**
  * AuxData META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AuxDataMeta implements Meta {
  	private String path = "";
	private static final String entityName = "AuxData";
	
	private static final String
              ID					="id",
              SORT_ORDER_ID					="sortOrderId",
              AUX_FIELD_ID					="auxFieldId",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId",
              IS_REPORTABLE					="isReportable",
              TYPE_ID					="typeId",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,AUX_FIELD_ID,REFERENCE_ID,REFERENCE_TABLE_ID,IS_REPORTABLE,TYPE_ID,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AuxDataMeta() {
		init();        
    }
    
    public AuxDataMeta(String path) {
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

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getAuxFieldId() {
        return path + AUX_FIELD_ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
