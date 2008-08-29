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
    
    
    public static TestMetaMap getInstance(){
        return new TestMetaMap();
    }
    
    public TestPrepMetaMap getTestPrep(){
       return  TEST_PREP;
    }
    
    public  TestTypeOfSampleMetaMap getTestTypeOfSample(){
        return TEST_TYPE_OF_SAMPLE;
    }
    
    public boolean hasColumn(String name){        
        if(name.startsWith("testPrep."))
            return TEST_PREP.hasColumn(name);
        if(name.startsWith("testTypeOfSample."))
            return TEST_TYPE_OF_SAMPLE.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name) {        
        String from = "Test t ";
        if(name.indexOf("testPrep.") > -1)
            from += ", IN (t.testPrep) testPrep ";
        if(name.indexOf("testTypeOfSample.") > -1)
            from += ", IN (t.testTypeOfSample) testTypeOfSample "; 
        return from;
    }
    
}
