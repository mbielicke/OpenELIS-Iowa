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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AnalyteMeta;


public class AnalyteMetaMap extends AnalyteMeta implements MetaMap {

    public AnalyteMetaMap() {
        super();
        PARENT_ANALYTE = new AnalyteMeta("parentAnalyte.");
    }
    
    public AnalyteMetaMap(String path){
        super(path);
        PARENT_ANALYTE = new AnalyteMeta(path+"parentAnalyte.");
    }
    
    public AnalyteMeta PARENT_ANALYTE; 
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"parentAnalyte."))
            return PARENT_ANALYTE.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String where) {
        // TODO Auto-generated method stub
        return "Analyte ";
    }
    
    public AnalyteMeta getParentAnalyte(){
        return PARENT_ANALYTE;
    }

}
