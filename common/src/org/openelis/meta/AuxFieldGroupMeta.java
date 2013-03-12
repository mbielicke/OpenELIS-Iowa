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

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class AuxFieldGroupMeta implements Meta, MetaMap {

    private static final String    ID = "_auxFieldGroup.id", 
                                   NAME = "_auxFieldGroup.name",
                                   DESCRIPTION = "_auxFieldGroup.description",
                                   IS_ACTIVE = "_auxFieldGroup.isActive",
                                   ACTIVE_BEGIN = "_auxFieldGroup.activeBegin",
                                   ACTIVE_END = "_auxFieldGroup.activeEnd",
                
                                   FIELD_ID = "_auxField.id",
                                   FIELD_AUX_FIELD_GROUP_ID = "_auxField.auxFieldGroupId",
                                   FIELD_SORT_ORDER = "_auxField.sortOrder",
                                   FIELD_ANALYTE_ID = "_auxField.analyteId",
                                   FIELD_DESCRIPTION = "_auxField.description",
                                   FIELD_METHOD_ID = "_auxField.methodId",
                                   FIELD_UNIT_OF_MEASURE_ID = "_auxField.unitOfMeasureId",
                                   FIELD_IS_REQUIRED = "_auxField.isRequired",
                                   FIELD_IS_ACTIVE = "_auxField.isActive",
                                   FIELD_IS_REPORTABLE = "_auxField.isReportable",
                                   FIELD_SCRIPTLET_ID = "_auxField.scriptletId",
                                   
                                   FIELD_VALUE_ID = "_auxField.auxFieldValue.id",
                                   FIELD_VALUE_AUX_FIELD_ID = "_auxField.auxFieldValue.auxFieldId",
                                   FIELD_VALUE_TYPE_ID = "_auxField.auxFieldValue.typeId",
                                   FIELD_VALUE_VALUE = "_auxField.auxFieldValue.value",
    
                                   FIELD_ANALYTE_NAME = "_auxField.analyte.name",
                                   FIELD_METHOD_NAME = "_auxField.method.name",                                   
                                   FIELD_SCRIPTLET_NAME = "_auxField.scriptlet.name";  
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,DESCRIPTION,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,
                                                  FIELD_ID, FIELD_AUX_FIELD_GROUP_ID, FIELD_SORT_ORDER,
                                                  FIELD_ANALYTE_ID, FIELD_DESCRIPTION, FIELD_METHOD_ID, 
                                                  FIELD_UNIT_OF_MEASURE_ID, FIELD_IS_REQUIRED,
                                                  FIELD_IS_ACTIVE, FIELD_IS_REPORTABLE, FIELD_SCRIPTLET_ID,
                                                  FIELD_VALUE_ID, FIELD_VALUE_AUX_FIELD_ID,
                                                  FIELD_VALUE_TYPE_ID, FIELD_VALUE_VALUE, FIELD_ANALYTE_NAME,
                                                  FIELD_METHOD_NAME, FIELD_SCRIPTLET_NAME));
    }
    
    public static String getId() {
        return ID;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getDescription() {
        return DESCRIPTION;
    } 

    public static String getIsActive() {
        return IS_ACTIVE;
    } 

    public static String getActiveBegin() {
        return ACTIVE_BEGIN;
    } 

    public static String getActiveEnd() {
        return ACTIVE_END;
    } 
    
    public static String getFieldId() {
        return FIELD_ID;
    } 
    
    public static String getFieldAuxFieldGroupId() {
        return FIELD_AUX_FIELD_GROUP_ID;
    }

    public static String getFieldSortOrder() {
        return FIELD_SORT_ORDER;
    } 

    public static String getFieldAnalyteId() {
        return FIELD_ANALYTE_ID;
    } 

    public static String getFieldDescription() {
        return FIELD_DESCRIPTION;
    } 

    public static String getFieldMethodId() {
        return FIELD_METHOD_ID;
    } 

    public static String getFieldUnitOfMeasureId() {
        return FIELD_UNIT_OF_MEASURE_ID;
    } 

    public static String getFieldIsRequired() {
        return FIELD_IS_REQUIRED;
    } 

    public static String getFieldIsActive() {
        return FIELD_IS_ACTIVE;
    } 

    public static String getFieldIsReportable() {
        return FIELD_IS_REPORTABLE;
    } 

    public static String getFieldScriptletId() {
        return FIELD_SCRIPTLET_ID;
    } 
    
    public static String getFieldValueId() {
        return FIELD_VALUE_ID;
    } 

    public static String getFieldValueAuxFieldId() {
        return FIELD_VALUE_AUX_FIELD_ID;
    } 

    public static String getFieldValueTypeId() {
        return FIELD_VALUE_TYPE_ID;
    } 

    public static String getFieldValueValue() {
        return FIELD_VALUE_VALUE;
    } 
    
    public static String getFieldAnalyteName() {
        return FIELD_ANALYTE_NAME;
    }
    
    public static String getFieldMethodName() {
        return FIELD_METHOD_NAME;
    } 
    
    public static String getFieldScriptletName() {
        return FIELD_SCRIPTLET_NAME;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "AuxFieldGroup _auxFieldGroup ";
        if (where.indexOf("auxField.") > -1)
            from += ",IN (_auxFieldGroup.auxField) _auxField ";
        if (where.indexOf("auxFieldValue.") > -1)
            from += ", IN (_auxField.auxFieldValue) _auxFieldValue ";

        return from;
    }

}
