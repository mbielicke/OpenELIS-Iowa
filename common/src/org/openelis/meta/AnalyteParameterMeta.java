/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class AnalyteParameterMeta implements Meta, MetaMap {

    private static final String    ID = "_analyteParameter.id",
                                   REFERENCE_ID = "_analyteParameter.referenceId",
                                   REFERENCE_TABLE_ID = "_analyteParameter.referenceTableId",
                                   ANALYTE_ID = "_analyteParameter.analyteId",
                                   TYPE_OF_SAMPLE_ID = "_analyteParameter.typeOfSampleId",
                                   IS_ACTIVE = "_analyteParameter.isActive",
                                   ACTIVE_BEGIN = "_analyteParameter.activeBegin",
                                   ACTIVE_END = "_analyteParameter.activeEnd",
                                   P1 = "_analyteParameter.p1",
                                   P2 = "_analyteParameter.p2",
                                   P3 = "_analyteParameter.p3",
                                   REFERENCE_NAME = "referenceName",
                                   TEST_NAME = "_test.name",
                                   TEST_METHOD_NAME = "_test.method.name",
                                   QC_NAME = "_qc.name",
                                   PROVIDER_NAME = "_provider.name",
                                   ANALYTE_NAME = "_analyte.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, REFERENCE_ID, REFERENCE_TABLE_ID,
                                                  ANALYTE_ID, TYPE_OF_SAMPLE_ID,
                                                  IS_ACTIVE, ACTIVE_BEGIN, ACTIVE_END,
                                                  P1, P2, P3, REFERENCE_NAME, TEST_NAME,
                                                  TEST_METHOD_NAME, QC_NAME,
                                                  PROVIDER_NAME, ANALYTE_NAME));
    }
    
    public static String getId() {
        return ID;
    }
 
    public static String getReferenceId() {
        return REFERENCE_ID;
    }
    
    public static String getReferenceName() {
        return REFERENCE_NAME;
    }
    
    public static String getTestName() {
       return TEST_NAME;
    }
    
    public static String getTestMethodName() {
        return TEST_METHOD_NAME;
    }
    
    public static String getQcName() {
        return QC_NAME;
    }
    
    public static String getProviderName() {
        return PROVIDER_NAME;
    }
    
    public static String getReferenceTableId() {
        return REFERENCE_TABLE_ID;
    }
    
    public static String getAnalyteId() {
        return ANALYTE_ID;
    }
    
    public static String getAnalyteName() {
        return ANALYTE_NAME;
    }
    
    public static String getTypeOfSampleId() {
        return TYPE_OF_SAMPLE_ID;
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
    
    public static String getP1() {
        return P1;
    }
    
    public static String getP2() {
        return P2;
    }
    
    public static String getP3() {
        return P3;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {      
        String from;
        
        from = "AnalyteParameter _analyteParameter ";       
        
        if (where.indexOf("test.") > -1)
            from += ", IN (_analyteParameter.test) _test ";
        
        if (where.indexOf("test.method.") > -1)
            from += ", IN (_test.method) _method ";
        
        if (where.indexOf("qc.") > -1)
            from += ", IN (_analyteParameter.qc) _qc ";
        
        if (where.indexOf("provider.") > -1)
            from += ", IN (_analyteParameter.provider) _provider ";
        
        return from;
    }
}
