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
  * Qaevent META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class QaeventMeta implements Meta {
  	private String path = "";
	private static final String entityName = "QaEvent";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              TEST_ID					="testId",
              TYPE_ID					="typeId",
              IS_BILLABLE					="isBillable",
              REPORTING_SEQUENCE					="reportingSequence",
              REPORTING_TEXT					="reportingText";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,TEST_ID,TYPE_ID,IS_BILLABLE,REPORTING_SEQUENCE,REPORTING_TEXT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public QaeventMeta() {
		init();        
    }
    
    public QaeventMeta(String path) {
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

    public String getTestId() {
        return path + TEST_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getIsBillable() {
        return path + IS_BILLABLE;
    } 

    public String getReportingSequence() {
        return path + REPORTING_SEQUENCE;
    } 

    public String getReportingText() {
        return path + REPORTING_TEXT;
    } 

  
}   
