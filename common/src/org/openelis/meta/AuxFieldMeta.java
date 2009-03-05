
package org.openelis.meta;

/**
  * AuxField META Data
  */


import org.openelis.gwt.common.Meta;

import java.util.HashSet;

public class AuxFieldMeta implements Meta {
  	private String path = "";
	private static final String entityName = "AuxField";
	
	private static final String
              ID					="id",
              SORT_ORDER					="sortOrder",
              ANALYTE_ID					="analyteId",
              DESCRIPTION					="description",
              REFERENCE_TABLE_ID					="referenceTableId",
              METHOD_ID					="methodId",
              UNIT_OF_MEASURE_ID					="unitOfMeasureId",
              IS_REQUIRED					="isRequired",
              IS_ACTIVE					="isActive",
              IS_REPORTABLE					="isReportable",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER,ANALYTE_ID,DESCRIPTION,REFERENCE_TABLE_ID,METHOD_ID,UNIT_OF_MEASURE_ID,IS_REQUIRED,IS_ACTIVE,IS_REPORTABLE,SCRIPTLET_ID};
  	  
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

    public String getSortOrder() {
        return path + SORT_ORDER;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getMethodId() {
        return path + METHOD_ID;
    } 

    public String getUnitOfMeasureId() {
        return path + UNIT_OF_MEASURE_ID;
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
