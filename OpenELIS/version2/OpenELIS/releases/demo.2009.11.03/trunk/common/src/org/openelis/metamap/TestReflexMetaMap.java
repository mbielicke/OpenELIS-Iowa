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
import org.openelis.meta.TestMeta;
import org.openelis.meta.TestReflexMeta;
import org.openelis.meta.TestResultMeta;

public class TestReflexMetaMap extends TestReflexMeta implements MetaMap {

   public TestMeta ADD_TEST;
   public TestResultMeta TEST_RESULT;
   public TestAnalyteMetaMap TEST_ANALYTE;
    
   public String buildFrom(String where) {        
        return "TestReflex ";
   }

   public TestReflexMetaMap(){
       super();
       ADD_TEST = new TestMeta(path+"addTest.");
       TEST_RESULT = new TestResultMeta(path+"testResult.");
       TEST_ANALYTE = new TestAnalyteMetaMap(path+"testAnalyte.");
   }
   
   public TestReflexMetaMap(String path){
       super(path);
       ADD_TEST = new TestMeta(path+"addTest.");
       TEST_RESULT = new TestResultMeta(path+"testResult.");
       TEST_ANALYTE = new TestAnalyteMetaMap(path+"testAnalyte.");
   }
   
   public boolean hasColumn(String name){        
       if(name.startsWith(path+"addTest."))
           return ADD_TEST.hasColumn(name);
       if(name.startsWith(path+"testResult."))
           return TEST_RESULT.hasColumn(name);
       if(name.startsWith(path+"testAnalyte."))
           return TEST_ANALYTE.hasColumn(name);
       return super.hasColumn(name);
   }
   
   public TestMeta getAddTest() {
       return ADD_TEST;
   }
   
   public TestResultMeta getTestResult() {
       return TEST_RESULT;
   }
   
   public TestAnalyteMetaMap getTestAnalyte() {
       return TEST_ANALYTE;
   }
   
}
