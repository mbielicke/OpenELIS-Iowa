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
  * Section META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class SectionMeta implements Meta, MetaMap {
	
	private static final String ID = "_section.id",
                                PARENT_SECTION_ID = "_section.parentSectionId",
                                NAME = "_section.name",
                                DESCRIPTION	= "_section.description",
                                IS_EXTERNAL	= "_section.isExternal",
                                ORGANIZATION_ID = "_section.organizationId",
                                
                                PARENT_SECTION_NAME = "_section.parentSection.name",
                                ORGANIZATION_NAME = "_section.organization.name";

    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,PARENT_SECTION_ID,NAME,
                                                  DESCRIPTION,IS_EXTERNAL,ORGANIZATION_ID,
                                                  PARENT_SECTION_NAME,ORGANIZATION_NAME));
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        return "Section _section ";                 
    }
    
    public String getId() {
        return ID;
    } 

    public String getParentSectionId() {
        return PARENT_SECTION_ID;
    } 

    public String getName() {
        return NAME;
    } 

    public String getDescription() {
        return DESCRIPTION;
    } 

    public String getIsExternal() {
        return IS_EXTERNAL;
    } 

    public String getOrganizationId() {
        return ORGANIZATION_ID;
    }
    
    public String getParentSectionName() {
        return PARENT_SECTION_NAME;
    }
    
    public String getOrganizationName() {
        return ORGANIZATION_NAME;
    }
  
}   
