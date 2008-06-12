
package org.openelis.meta;

/**
  * QcAnalyte META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class QcAnalyteMeta implements Meta {
  	private static final String tableName = "qc_analyte";
	private static final String entityName = "QcAnalyte";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="qc_analyte.id",
              QC_ID					="qc_analyte.qc_id",
              ANALYTE_ID					="qc_analyte.analyte_id",
              TYPE_ID					="qc_analyte.type_id",
              VALUE					="qc_analyte.value",
              IS_TRENDABLE					="qc_analyte.is_trendable";


  	private static final String[] columnNames = {
  	  ID,QC_ID,ANALYTE_ID,TYPE_ID,VALUE,IS_TRENDABLE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final QcAnalyteMeta qc_analyteMeta = new QcAnalyteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private QcAnalyteMeta() {
        
    }
    
    public static QcAnalyteMeta getInstance() {
        return qc_analyteMeta;
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

  public static String getQcId() {
    return QC_ID;
  } 

  public static String getAnalyteId() {
    return ANALYTE_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getValue() {
    return VALUE;
  } 

  public static String getIsTrendable() {
    return IS_TRENDABLE;
  } 

  
}   
