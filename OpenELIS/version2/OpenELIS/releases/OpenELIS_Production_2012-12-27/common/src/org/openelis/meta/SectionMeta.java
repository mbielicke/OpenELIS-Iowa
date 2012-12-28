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
                                
                                PAR_ID = "_sectionParameter.id",
	                            PAR_SECTION_ID =  "_sectionParameter.sectionId",
	                            PAR_TYPE_ID =  "_sectionParameter.typeId",
	                            PAR_VALUE =  "_sectionParameter.value",
                                
                                PARENT_SECTION_NAME = "_section.parentSection.name",
                                ORGANIZATION_NAME = "_section.organization.name";
	
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,PARENT_SECTION_ID,NAME,
                                                  DESCRIPTION,IS_EXTERNAL,ORGANIZATION_ID,
                                                  PAR_ID,PAR_SECTION_ID,PAR_TYPE_ID,PAR_VALUE,                                                  
                                                  PARENT_SECTION_NAME,ORGANIZATION_NAME));
    }
    
    public static String getId() {
        return ID;
    } 

    public static String getParentSectionId() {
        return PARENT_SECTION_ID;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getDescription() {
        return DESCRIPTION;
    } 

    public static String getIsExternal() {
        return IS_EXTERNAL;
    } 

    public static String getOrganizationId() {
        return ORGANIZATION_ID;
    }
    
    public static String getParameterId() {
        return PAR_ID;
    }
    
    public static String getParameterSectionId() {
        return PAR_SECTION_ID;
    }
    
    public static String getParameterTypeId() {
        return PAR_TYPE_ID;
    } 
    
    public static String getParameterValue() {   
        return PAR_VALUE;
    }
    
    public static String getParentSectionName() {
        return PARENT_SECTION_NAME;
    }
    
    public static String getOrganizationName() {
        return ORGANIZATION_NAME;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from;
        
        from = "Section _section ";
        
        if (where.indexOf("sectionParameter.") > -1)
            from += ",IN (_section.sectionParameter) _sectionParameter ";
        
        return from;
    }
  
}   
