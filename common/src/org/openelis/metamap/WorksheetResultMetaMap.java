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
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.WorksheetResultMeta;

public class WorksheetResultMetaMap extends WorksheetResultMeta implements MetaMap {
    
    public AnalyteMetaMap     ANALYTE;
    public TestAnalyteMetaMap TEST_ANALYTE;
    public TestResultMetaMap  TEST_RESULT;
    
    public WorksheetResultMetaMap() {
        super("wr.");
        TEST_ANALYTE = new TestAnalyteMetaMap();
        TEST_RESULT  = new TestResultMetaMap();
        ANALYTE      = new AnalyteMetaMap();
    }
    
    public WorksheetResultMetaMap(String path) {
        super(path);
        TEST_ANALYTE = new TestAnalyteMetaMap(path+"testAnalyte.");
        TEST_RESULT  = new TestResultMetaMap(path+"testResult.");
        ANALYTE      = new AnalyteMetaMap(path+"analyte");
    }
    
    public static WorksheetResultMetaMap getInstance() {
        return new WorksheetResultMetaMap();
    }
    
    public AnalyteMetaMap getAnalyte() {
        return ANALYTE;
    }
    
    public TestAnalyteMetaMap getTestAnalyte() {
        return TEST_ANALYTE;
    }
    
    public TestResultMetaMap getTestResult() {
        return TEST_RESULT;
    }
    
    public boolean hasColumn(String columnName) {
        if (columnName.startsWith(path+"testAnalyte."))
            return TEST_ANALYTE.hasColumn(columnName);
        if (columnName.startsWith(path+"testResult."))
            return TEST_RESULT.hasColumn(columnName);
        if (columnName.startsWith(path+"analyte."))
            return ANALYTE.hasColumn(columnName);
        return super.hasColumn(columnName);
    }      

    public String buildFrom(String name) {
        String from;
        
        from = "WorksheetResult wr ";
        if (name.indexOf("testAnalyte.") > -1)
            from += ", IN (wr.testAnalyte) testAnalyte ";
        if (name.indexOf("testResult.") > -1)
            from += ", IN (wr.testResult) testResult ";
        if (name.indexOf("analyte.") > -1)
            from += ", IN (wr.analyte) analyte ";

        return from;
    }
}
