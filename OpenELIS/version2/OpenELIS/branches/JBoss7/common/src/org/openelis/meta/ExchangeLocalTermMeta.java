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

/**
 * Exchange Local Term META Data
 *
 */
public class ExchangeLocalTermMeta implements Meta, MetaMap {
    private static final String   ID = "_exchangeLocalTerm.id",
                                  REFERENCE_TABLE_ID = "_exchangeLocalTerm.referenceTableId",
                                  REFERENCE_ID = "_exchangeLocalTerm.referenceId",
                                  
                                  EXT_TERM_ID = "_exchangeExternalTerm.id",
                                  EXT_TERM_EXCHANGE_LOCAL_TERM_ID = "_exchangeExternalTerm.exchangeLocalTermId",
                                  EXT_TERM_EXCHANGE_PROFILE_ID = "_exchangeExternalTerm.profileId",
                                  EXT_TERM_PROFILE_IS_ACTIVE = "_exchangeExternalTerm.isActive",
                                  EXT_TERM_EXTERNAL_TERM = "_exchangeExternalTerm.externalTerm",
                                  EXT_TERM_EXTERNAL_DESCRIPTION = "_exchangeExternalTerm.externalDescription",
                                  EXT_TERM_EXTERNAL_CODING_SYSTEM = "_exchangeExternalTerm.externalCodingSystem",
                                  REFERENCE_NAME = "referenceName",
                                  ANALYTE_NAME = "_analyte.name",
                                  METHOD_NAME = "_method.name",
                                  TEST_NAME = "_test.name",
                                  TEST_METHOD_NAME = "_test.method.name",
                                  ORGANIZATION_NAME = "_organization.name",
                                  DICTIONARY_ENTRY = "_dictionary.entry",
                                  DICTIONARY_CATEGORY_NAME = "_dictionary.category.name";
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, REFERENCE_TABLE_ID, REFERENCE_ID, 
                                                  EXT_TERM_ID, EXT_TERM_EXCHANGE_LOCAL_TERM_ID, 
                                                  EXT_TERM_EXCHANGE_PROFILE_ID, 
                                                  EXT_TERM_PROFILE_IS_ACTIVE, EXT_TERM_EXTERNAL_TERM,
                                                  EXT_TERM_EXTERNAL_DESCRIPTION,
                                                  EXT_TERM_EXTERNAL_CODING_SYSTEM,
                                                  REFERENCE_NAME , ANALYTE_NAME, METHOD_NAME,
                                                  TEST_NAME, TEST_METHOD_NAME, ORGANIZATION_NAME,
                                                  DICTIONARY_ENTRY, DICTIONARY_CATEGORY_NAME));
    }    

    public static String getId() {
        return ID;
    }

    public static String getReferenceTableId() {
        return REFERENCE_TABLE_ID;
    }

    public static String getReferenceId() {
        return REFERENCE_ID;
    }

    public static String getExternalTermId() {
        return EXT_TERM_ID;
    }

    public static String getExternalTermExchangeLocalTermId() {
        return EXT_TERM_EXCHANGE_LOCAL_TERM_ID;
    }

    public static String getExternalTermExchangeProfileId() {
        return EXT_TERM_EXCHANGE_PROFILE_ID;
    }

    public static String getExtTermProfileIsActive() {
        return EXT_TERM_PROFILE_IS_ACTIVE;
    }

    public static String getExternalTermExternalTerm() {
        return EXT_TERM_EXTERNAL_TERM;
    }

    public static String getExternalTermExternalDescription() {
        return EXT_TERM_EXTERNAL_DESCRIPTION;
    }

    public static String getExternalTermExternalCodingSystem() {
        return EXT_TERM_EXTERNAL_CODING_SYSTEM;
    }

    public static String getReferenceName() {
        return REFERENCE_NAME;
    }

    public static String getAnalyteName() {
        return ANALYTE_NAME;
    }

    public static String getMethodName() {
        return METHOD_NAME;
    }

    public static String getTestName() {
        return TEST_NAME;
    }

    public static String getTestMethodName() {
        return TEST_METHOD_NAME;
    }

    public static String getOrganizationName() {
        return ORGANIZATION_NAME;
    }

    public static String getDictionaryEntry() {
        return DICTIONARY_ENTRY;
    }

    public static String getDictionaryCategoryName() {
        return DICTIONARY_CATEGORY_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "ExchangeLocalTerm _exchangeLocalTerm ";
        
        if (where.indexOf("exchangeExternalTerm.") > -1)
            from += ",IN (_exchangeLocalTerm.exchangeExternalTerm) _exchangeExternalTerm ";

        if (where.indexOf("analyte.") > -1)
            from += ", IN (_exchangeLocalTerm.analyte) _analyte ";
        
        if (where.indexOf("dictionary.") > -1)
            from += ", IN (_exchangeLocalTerm.dictionary) _dictionary ";
        
        if (where.indexOf("organization.") > -1)
            from += ", IN (_exchangeLocalTerm.organization) _organization ";
        
        if (where.indexOf("test.") > -1)
            from += ", IN (_exchangeLocalTerm.test) _test ";
        
        if (where.indexOf("method.") > -1)
            from += ", IN (_exchangeLocalTerm.method) _method ";
        
        return from;
    }
}