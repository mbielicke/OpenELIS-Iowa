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

public class MethodMetaMap extends MethodMeta implements MetaMap {
    
    public MethodMetaMap(){
        super("m.");
    }
            
    public MethodMetaMap(String path){
        super(path);        
        
    }

    public boolean hasColumn(String name){                
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name) {        
        String from = "Method m ";        
        return from;
    }

}
