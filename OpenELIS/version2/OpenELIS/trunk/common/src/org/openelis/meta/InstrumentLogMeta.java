
package org.openelis.meta;

/**
  * InstrumentLog META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InstrumentLogMeta implements Meta {
  	private static final String tableName = "instrument_log";
	private static final String entityName = "InstrumentLog";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="instrument_log.id",
              INSTRUMENT_ID					="instrument_log.instrument_id",
              TYPE_ID					="instrument_log.type_id",
              WORKSHEET_ID					="instrument_log.worksheet_id",
              EVENT_BEGIN					="instrument_log.event_begin",
              EVENT_END					="instrument_log.event_end";


  	private static final String[] columnNames = {
  	  ID,INSTRUMENT_ID,TYPE_ID,WORKSHEET_ID,EVENT_BEGIN,EVENT_END};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InstrumentLogMeta instrument_logMeta = new InstrumentLogMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InstrumentLogMeta() {
        
    }
    
    public static InstrumentLogMeta getInstance() {
        return instrument_logMeta;
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

  public static String getInstrumentId() {
    return INSTRUMENT_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getWorksheetId() {
    return WORKSHEET_ID;
  } 

  public static String getEventBegin() {
    return EVENT_BEGIN;
  } 

  public static String getEventEnd() {
    return EVENT_END;
  } 

  
}   
