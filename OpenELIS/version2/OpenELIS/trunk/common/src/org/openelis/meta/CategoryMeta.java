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
  * Category META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class CategoryMeta implements Meta, MetaMap {
	
	private static final String ID = "_category.id",
                                SYSTEM_NAME	= "_category.systemName",
                                NAME = "_category.name",
                                DESCRIPTION	= "_category.description",
                                SECTION_ID = "_category.sectionId",
                                IS_SYSTEM = "_category.isSystem",
	
                            	DICT_ID = "_dictionary.id",
                            	DICT_CATEGORY_ID = "_dictionary.categoryId",
                            	DICT_SORT_ORDER = "_dictionary.sortOrder",              
                            	DICT_RELATED_ENTRY_ID = "_dictionary.relatedEntryId",
                            	DICT_SYSTEM_NAME = "_dictionary.systemName",
                            	DICT_IS_ACTIVE = "_dictionary.isActive",
                            	DICT_CODE = "_dictionary.code",
                            	DICT_ENTRY = "_dictionary.entry",
                            	
                            	DICT_RELATED_ENTRY_ENTRY = "_dictionary.relatedEntry.entry";

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,SYSTEM_NAME,NAME,DESCRIPTION,
                                                  SECTION_ID,IS_SYSTEM,DICT_ID,
                                                  DICT_SORT_ORDER,DICT_CATEGORY_ID,
                                                  DICT_RELATED_ENTRY_ID,DICT_SYSTEM_NAME,
                                                  DICT_IS_ACTIVE,DICT_CODE,
                                                  DICT_ENTRY,DICT_RELATED_ENTRY_ENTRY));
    }
        
    public static String getId() {
        return ID;
    } 

    public static String getSystemName() {
        return SYSTEM_NAME;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getDescription() {
        return DESCRIPTION;
    } 

    public static String getSectionId() {
        return SECTION_ID;
    } 
    
    public static String getIsSystem() {
        return IS_SYSTEM;
    }
    
    public static String getDictionaryId() {
        return DICT_ID;
    } 

    public static String getDictionaryCategoryId() {
        return DICT_CATEGORY_ID;
    } 
    
    public static String getDictionarySortOrder() {
        return DICT_SORT_ORDER;
    }

    public static String getDictionaryRelatedEntryId() {
        return DICT_RELATED_ENTRY_ID;
    } 

    public static String getDictionarySystemName() {
        return DICT_SYSTEM_NAME;
    } 

    public static String getDictionaryIsActive() {
        return DICT_IS_ACTIVE;
    } 

    public static String getDictionaryCode() {
        return DICT_CODE;
    } 

    public static String getDictionaryEntry() {
        return DICT_ENTRY;
    } 
    
    public static String getDictionaryRelatedEntryEntry() {
        return DICT_RELATED_ENTRY_ENTRY;
    } 
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from;
        
        from = "Category _category ";
        if (where.indexOf("dictionary.") > -1)
            from += ",IN (_category.dictionary) _dictionary ";        
        
        return from;
    }
  
}   
