
package org.openelis.newmeta;

/**
  * Instrument META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class InstrumentMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Instrument";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              MODEL_NUMBER					="modelNumber",
              SERIAL_NUMBER					="serialNumber",
              TYPE_ID					="typeId",
              LOCATION					="location",
              IS_ACTIVE					="isActive",
              ACTIVE_BEGIN					="activeBegin",
              ACTIVE_END					="activeEnd",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,MODEL_NUMBER,SERIAL_NUMBER,TYPE_ID,LOCATION,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InstrumentMeta() {
		init();        
    }
    
    public InstrumentMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getModelNumber() {
        return path + MODEL_NUMBER;
    } 

    public String getSerialNumber() {
        return path + SERIAL_NUMBER;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getLocation() {
        return path + LOCATION;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getActiveBegin() {
        return path + ACTIVE_BEGIN;
    } 

    public String getActiveEnd() {
        return path + ACTIVE_END;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
