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

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

/**
 * Test Analyte View META Data
 */

public class TestAnalyteViewMeta implements Meta, MetaMap {
    private static final String    ID = "_testAnalyteView.id", 
                    TEST_ID = "_testAnalyteView.testId",
                    TEST_NAME = "_testAnalyteView.testName",
                    METHOD_ID = "_testAnalyteView.methodId",
                    METHOD_NAME = "_testAnalyteView.methodName",
                    TEST_IS_ACTIVE = "_testAnalyteView.testIsActive",
                    TEST_ACTIVE_BEGIN = "_testAnalyteView.testActiveBegin",
                    TEST_ACTIVE_END = "_testAnalyteView.testActiveEnd",
                    ROW_TEST_ANALYTE_ID = "_testAnalyteView.rowTestAnalyteId",
                    ROW_ANALYTE_ID = "_testAnalyteView.rowAnalyteId",
                    ROW_ANALYTE_NAME = "_testAnalyteView.rowAnalyteName",
                    COL_ANALYTE_ID = "_testAnalyteView.colAnalyteId",
                    COL_ANALYTE_NAME = "_testAnalyteView.colAnalyteName";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, TEST_ID, TEST_NAME, METHOD_ID,
                                                  METHOD_NAME, TEST_IS_ACTIVE,
                                                  TEST_ACTIVE_BEGIN, TEST_ACTIVE_END,
                                                  ROW_TEST_ANALYTE_ID, ROW_ANALYTE_ID,
                                                  ROW_ANALYTE_NAME, COL_ANALYTE_ID,
                                                  COL_ANALYTE_NAME));
    }

    public static String getId() {
        return ID;
    }
    
    public static String getTestId() {
        return TEST_ID;
    }

    public static String getTestName() {
        return TEST_NAME;
    }

    public static String getMethodId() {
        return METHOD_ID;
    }

    public static String getMethodName() {
        return METHOD_NAME;
    }

    public static String getTestIsActive() {
        return TEST_IS_ACTIVE;
    }

    public static String getTestActiveBegin() {
        return TEST_ACTIVE_BEGIN;
    }

    public static String getTestActiveEnd() {
        return TEST_ACTIVE_END;
    }

    public static String getRowTestAnalyteId() {
        return ROW_TEST_ANALYTE_ID;
    }

    public static String getRowAnalyteId() {
        return ROW_ANALYTE_ID;
    }

    public static String getRowAnalyteName() {
        return ROW_ANALYTE_NAME;
    }

    public static String getColAnalyteId() {
        return COL_ANALYTE_ID;
    }

    public static String getColAnalyteName() {
        return COL_ANALYTE_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "TestAnalyteView _testAnalyteView ";

        return from;
    }
}