
package org.openelis.meta;

/**
  * Qc META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class QcMeta implements Meta {
  	private static final String tableName = "qc";
	private static final String entityName = "Qc";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="qc.id",
              NAME					="qc.name",
              TYPE_ID					="qc.typeId",
              SOURCE					="qc.source",
              LOT_NUMBER					="qc.lotNumber",
              PREPARED_DATE					="qc.preparedDate",
              PREPARED_VOLUME					="qc.preparedVolume",
              PREPARED_UNIT_ID					="qc.preparedUnitId",
              PREPARED_BY_ID					="qc.preparedById",
              USABLE_DATE					="qc.usableDate",
              EXPIRE_DATE					="qc.expireDate",
              IS_SINGLE_USE					="qc.isSingleUse";


  	private static final String[] columnNames = {
  	  ID,NAME,TYPE_ID,SOURCE,LOT_NUMBER,PREPARED_DATE,PREPARED_VOLUME,PREPARED_UNIT_ID,PREPARED_BY_ID,USABLE_DATE,EXPIRE_DATE,IS_SINGLE_USE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final QcMeta qcMeta = new QcMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private QcMeta() {
        
    }
    
    public static QcMeta getInstance() {
        return qcMeta;
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

  public static String getName() {
    return NAME;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getSource() {
    return SOURCE;
  } 

  public static String getLotNumber() {
    return LOT_NUMBER;
  } 

  public static String getPreparedDate() {
    return PREPARED_DATE;
  } 

  public static String getPreparedVolume() {
    return PREPARED_VOLUME;
  } 

  public static String getPreparedUnitId() {
    return PREPARED_UNIT_ID;
  } 

  public static String getPreparedById() {
    return PREPARED_BY_ID;
  } 

  public static String getUsableDate() {
    return USABLE_DATE;
  } 

  public static String getExpireDate() {
    return EXPIRE_DATE;
  } 

  public static String getIsSingleUse() {
    return IS_SINGLE_USE;
  } 

  
}   
