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
  * Analyte META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class AnalyteMeta implements Meta , MetaMap{

	private static final String ID = "_analyte.id",
                                NAME = "_analyte.name",
                                IS_ACTIVE = "_analyte.isActive",
                                PARENT_ANALYTE_ID = "_analyte.parentAnalyteId",
                                EXTERNAL_ID	= "_analyte.externalId",
                                
                                PARENT_ANALYTE_NAME = "_analyte.parentAnalyte.name";

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,IS_ACTIVE,PARENT_ANALYTE_ID,
                                                  EXTERNAL_ID,PARENT_ANALYTE_NAME));
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {        
        return "Analyte _analyte ";
    }
    
    public String getId() {
        return ID;
    } 

    public String getName() {
        return NAME;
    } 

    public String getIsActive() {
        return IS_ACTIVE;
    } 

    public String getParentAnalyteId() {
        return PARENT_ANALYTE_ID;
    } 

    public String getExternalId() {
        return EXTERNAL_ID;
    } 

    public String getParentAnalyteName() {
        return PARENT_ANALYTE_NAME;
    }
}   
