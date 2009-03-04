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
  * Test META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Test";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              REPORTING_DESCRIPTION					="reportingDescription",
              METHOD_ID					="methodId",
              IS_ACTIVE					="isActive",
              ACTIVE_BEGIN					="activeBegin",
              ACTIVE_END					="activeEnd",
              IS_REPORTABLE					="isReportable",
              TIME_TRANSIT					="timeTransit",
              TIME_HOLDING					="timeHolding",
              TIME_TA_AVERAGE					="timeTaAverage",
              TIME_TA_WARNING					="timeTaWarning",
              TIME_TA_MAX					="timeTaMax",
              LABEL_ID					="labelId",
              LABEL_QTY					="labelQty",
              TEST_TRAILER_ID					="testTrailerId",
              SCRIPTLET_ID					="scriptletId",
              TEST_FORMAT_ID					="testFormatId",
              REVISION_METHOD_ID					="revisionMethodId",
              REPORTING_METHOD_ID                   ="reportingMethodId",
              SORTING_METHOD_ID                     = "sortingMethodId",
              REPORTING_SEQUENCE                    = "reportingSequence";
              
              

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,METHOD_ID,IS_ACTIVE,ACTIVE_BEGIN
      ,ACTIVE_END,IS_REPORTABLE,TIME_TRANSIT,TIME_HOLDING,TIME_TA_AVERAGE,TIME_TA_WARNING,
      TIME_TA_MAX,LABEL_ID,LABEL_QTY,TEST_TRAILER_ID,SCRIPTLET_ID,TEST_FORMAT_ID,
      REVISION_METHOD_ID,REPORTING_METHOD_ID,SORTING_METHOD_ID,REPORTING_SEQUENCE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestMeta() {
		init();        
    }
    
    public TestMeta(String path) {
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

    public String getReportingDescription() {
        return path + REPORTING_DESCRIPTION;
    } 

    public String getMethodId() {
        return path + METHOD_ID;
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

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getTimeTransit() {
        return path + TIME_TRANSIT;
    } 

    public String getTimeHolding() {
        return path + TIME_HOLDING;
    } 

    public String getTimeTaAverage() {
        return path + TIME_TA_AVERAGE;
    } 

    public String getTimeTaWarning() {
        return path + TIME_TA_WARNING;
    } 

    public String getTimeTaMax() {
        return path + TIME_TA_MAX;
    } 

    public String getLabelId() {
        return path + LABEL_ID;
    } 

    public String getLabelQty() {
        return path + LABEL_QTY;
    } 

    public String getTestTrailerId() {
        return path + TEST_TRAILER_ID;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

    public String getTestFormatId() {
        return path + TEST_FORMAT_ID;
    } 

    public String getRevisionMethodId() {
        return path + REVISION_METHOD_ID;
    }

    public String getReportingMethodId() {
        return path + REPORTING_METHOD_ID;
    }

    public String getReportingSequence() {
        return path + REPORTING_SEQUENCE;
    }
    
    public String getSortingMethodId(){
        return path + SORTING_METHOD_ID;
    }
 

  
}   
