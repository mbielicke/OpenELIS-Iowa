/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.meta;

/**
  * Note META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class NoteMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Note";
	
	private static final String
              ID					="id",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId",
              TIMESTAMP					="timestamp",
              IS_EXTERNAL					="isExternal",
              SYSTEM_USER_ID					="systemUserId",
              SUBJECT					="subject",
              TEXT					="text";

  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,TIMESTAMP,IS_EXTERNAL,SYSTEM_USER_ID,SUBJECT,TEXT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public NoteMeta() {
		init();        
    }
    
    public NoteMeta(String path) {
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

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getTimestamp() {
        return path + TIMESTAMP;
    } 

    public String getIsExternal() {
        return path + IS_EXTERNAL;
    } 

    public String getSystemUserId() {
        return path + SYSTEM_USER_ID;
    } 

    public String getSubject() {
        return path + SUBJECT;
    } 

    public String getText() {
        return path + TEXT;
    } 

  
}   
