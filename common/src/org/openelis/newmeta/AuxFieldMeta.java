
package org.openelis.newmeta;

/**
  * AuxField META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class AuxFieldMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "AuxField";
	
	private static final String
              ID					="id",
              SORT_ORDER_ID					="sortOrderId",
              ANALYTE_ID					="analyteId",
              REFERENCE_TABLE_ID					="referenceTableId",
              IS_REQUIRED					="isRequired",
              IS_ACTIVE					="isActive",
              IS_REPORTABLE					="isReportable",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,ANALYTE_ID,REFERENCE_TABLE_ID,IS_REQUIRED,IS_ACTIVE,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AuxFieldMeta() {
		init();        
    }
    
    public AuxFieldMeta(String path) {
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

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getIsRequired() {
        return path + IS_REQUIRED;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
