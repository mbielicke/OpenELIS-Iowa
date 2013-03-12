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
  * SystemVariable META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class SystemVariableMeta implements Meta, MetaMap {
	
	private static final String ID = "_systemVariable.id",
                                NAME = "_systemVariable.name",
                                VALUE = "_systemVariable.value";

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,VALUE));
    }
    
    public static String getId() {
        return ID;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getValue() {
        return VALUE;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {        
        return "SystemVariable _systemVariable ";
    } 

  
}   
