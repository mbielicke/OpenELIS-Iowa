
package org.openelis.meta;

/**
  * QcAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class QcAnalyteMeta implements Meta {
  	private String path = "";
	private static final String entityName = "QcAnalyte";
	
	private static final String
              ID					="id",
              QC_ID					="qcId",
              ANALYTE_ID					="analyteId",
              TYPE_ID					="typeId",
              VALUE					="value",
              IS_TRENDABLE					="isTrendable";

  	private static final String[] columnNames = {
  	  ID,QC_ID,ANALYTE_ID,TYPE_ID,VALUE,IS_TRENDABLE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public QcAnalyteMeta() {
		init();        
    }
    
    public QcAnalyteMeta(String path) {
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

    public String getQcId() {
        return path + QC_ID;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

    public String getIsTrendable() {
        return path + IS_TRENDABLE;
    } 

  
}   
