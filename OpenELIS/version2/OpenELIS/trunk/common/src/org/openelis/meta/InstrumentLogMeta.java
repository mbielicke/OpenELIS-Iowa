/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

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
