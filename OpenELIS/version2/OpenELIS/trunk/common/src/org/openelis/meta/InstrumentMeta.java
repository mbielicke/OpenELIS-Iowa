
package org.openelis.meta;

/**
  * Instrument META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InstrumentMeta implements Meta {
  	private static final String tableName = "instrument";
	private static final String entityName = "Instrument";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="instrument.id",
              NAME					="instrument.name",
              DESCRIPTION					="instrument.description",
              MODEL_NUMBER					="instrument.modelNumber",
              SERIAL_NUMBER					="instrument.serialNumber",
              TYPE_ID					="instrument.typeId",
              LOCATION					="instrument.location",
              IS_ACTIVE					="instrument.isActive",
              ACTIVE_BEGIN					="instrument.activeBegin",
              ACTIVE_END					="instrument.activeEnd",
              SCRIPTLET_ID					="instrument.scriptletId";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,MODEL_NUMBER,SERIAL_NUMBER,TYPE_ID,LOCATION,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InstrumentMeta instrumentMeta = new InstrumentMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InstrumentMeta() {
        
    }
    
    public static InstrumentMeta getInstance() {
        return instrumentMeta;
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

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getModelNumber() {
    return MODEL_NUMBER;
  } 

  public static String getSerialNumber() {
    return SERIAL_NUMBER;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getLocation() {
    return LOCATION;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getActiveBegin() {
    return ACTIVE_BEGIN;
  } 

  public static String getActiveEnd() {
    return ACTIVE_END;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
