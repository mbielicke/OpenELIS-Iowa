/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.TestWorksheetMeta;

public class TestWorksheetMetaMap extends TestWorksheetMeta implements MetaMap {

    public String buildFrom(String name) {        
        //String from = "TestWorksheet testWorksheet ";
        //if(name.indexOf("testWorksheetItem.") > -1)
        //from += ", IN (testWorksheet.testWorksheetItem) testWorksheetItem ";
        return "TestWorksheet ";
    }
        
    private TestWorksheetItemMetaMap TEST_WORKSHEET_ITEM ;  
        
    public TestWorksheetItemMetaMap getTestWorksheetItem(){
        return TEST_WORKSHEET_ITEM;
    }
    
    public TestWorksheetMetaMap(){
        super();
    }
    
    public TestWorksheetMetaMap(String path){
        super(path);   
        TEST_WORKSHEET_ITEM = new TestWorksheetItemMetaMap(path+"testWorksheetItem.");
    }
    
    public boolean hasColumn(String name){     
        if(name.startsWith(path+"testWorksheetItem."))
            return TEST_WORKSHEET_ITEM.hasColumn(name); 
        return super.hasColumn(name);
    }    
        

}
