
package org.openelis.meta;

/**
  * InstrumentLog META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class InstrumentLogMeta implements Meta {
  	private String path = "";
	private static final String entityName = "InstrumentLog";
	
	private static final String
              ID					="id",
              INSTRUMENT_ID					="instrumentId",
              TYPE_ID					="typeId",
              WORKSHEET_ID					="worksheetId",
              EVENT_BEGIN					="eventBegin",
              EVENT_END					="eventEnd";

  	private static final String[] columnNames = {
  	  ID,INSTRUMENT_ID,TYPE_ID,WORKSHEET_ID,EVENT_BEGIN,EVENT_END};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InstrumentLogMeta() {
		init();        
    }
    
    public InstrumentLogMeta(String path) {
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

    public String getInstrumentId() {
        return path + INSTRUMENT_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getWorksheetId() {
        return path + WORKSHEET_ID;
    } 

    public String getEventBegin() {
        return path + EVENT_BEGIN;
    } 

    public String getEventEnd() {
        return path + EVENT_END;
    } 

  
}   
