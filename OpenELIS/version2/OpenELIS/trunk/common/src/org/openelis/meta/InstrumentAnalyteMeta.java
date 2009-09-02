
package org.openelis.meta;

/**
  * InstrumentAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class InstrumentAnalyteMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "InstrumentAnalyte";
	
	private static final String
              ID					        ="id",
              INSTRUMENT_ID					="instrumentId",
              ANALYTE_ID					="analyteId";

  	private static final String[] columnNames = {
  	  ID,INSTRUMENT_ID,ANALYTE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InstrumentAnalyteMeta() {
		init();        
    }
    
    public InstrumentAnalyteMeta(String path) {
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

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

  
}   
