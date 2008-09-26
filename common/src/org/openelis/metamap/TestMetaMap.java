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
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.MethodMeta;
import org.openelis.meta.TestMeta;

public class TestMetaMap extends TestMeta implements MetaMap {

    public TestMetaMap(){
        super("t.");
    }
            
    public TestMetaMap(String path){
        super(path);               
    }
    
    private TestPrepMetaMap TEST_PREP = new TestPrepMetaMap("testPrep.");
    
    private TestTypeOfSampleMetaMap TEST_TYPE_OF_SAMPLE = new TestTypeOfSampleMetaMap("testTypeOfSample."); 
    
    private TestReflexMetaMap TEST_REFLEX = new TestReflexMetaMap("testReflex."); 
    
    private TestWorksheetMetaMap TEST_WORKSHEET = new TestWorksheetMetaMap("testWorksheet.");
    
    private MethodMeta METHOD = new MethodMeta("t.method.");
           
    private TestWorksheetItemMetaMap TEST_WORKSHEET_ITEM = new TestWorksheetItemMetaMap("testWorksheetItem.");  
    
    public TestWorksheetItemMetaMap getTestWorksheetItem(){
        return TEST_WORKSHEET_ITEM;
    }
    
    public MethodMeta getMethod(){
        return METHOD;
    }
    
    public static TestMetaMap getInstance(){
        return new TestMetaMap();
    }
    
    public TestPrepMetaMap getTestPrep(){
       return  TEST_PREP;
    }
    
    public  TestTypeOfSampleMetaMap getTestTypeOfSample(){
        return TEST_TYPE_OF_SAMPLE;
    }
    
    public TestReflexMetaMap getTestReflex(){
        return TEST_REFLEX;
    } 
    
    public TestWorksheetMetaMap getTestWorksheet(){
        return TEST_WORKSHEET;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("testPrep."))
            return TEST_PREP.hasColumn(name);
        if(name.startsWith("testTypeOfSample."))
            return TEST_TYPE_OF_SAMPLE.hasColumn(name);
        if(name.startsWith("testReflex."))
            return TEST_REFLEX.hasColumn(name);
        if(name.startsWith("testWorksheet."))
            return TEST_WORKSHEET.hasColumn(name);
        if(name.startsWith("testWorksheetItem."))
            return TEST_WORKSHEET_ITEM.hasColumn(name);
        if(name.startsWith(path+"method."))
            return METHOD.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name) {        
        String from = "Test t ";
        if(name.indexOf("testPrep.") > -1)
            from += ", IN (t.testPrep) testPrep ";
        if(name.indexOf("testTypeOfSample.") > -1)
            from += ", IN (t.testTypeOfSample) testTypeOfSample "; 
        if(name.indexOf("testReflex.") > -1)
            from += ", IN (t.testReflex) testReflex ";  
        String wsFrom = ", IN (t.testWorksheet) testWorksheet ";
        String wsiFrom = ", IN (testWorksheet.testWorksheetItem) testWorksheetItem ";
        if(name.indexOf("testWorksheet.") > -1)
            from += wsFrom;
        if(name.indexOf("testWorksheetItem.") > -1)
           if(from.indexOf(wsFrom)<0) 
            from += wsFrom + wsiFrom;
           else
            from += wsiFrom;   
        return from;
    }
    
}
