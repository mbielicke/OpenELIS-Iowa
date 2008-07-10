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
import org.openelis.meta.SystemVariableMeta;

public class SystemVariableMetaMap extends SystemVariableMeta implements
                                                             MetaMap {
     public SystemVariableMetaMap(){
         super("sv.");
     }
     
     public static SystemVariableMetaMap getInstance(){
         return new SystemVariableMetaMap();
     }
    
    public String buildFrom(String where) {
        return "SystemVariable sv";
    }
    
    public boolean hasColumn(String name){        
        return super.hasColumn(name);
    }

}
