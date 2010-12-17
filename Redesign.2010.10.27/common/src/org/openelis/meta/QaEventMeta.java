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


public class QaEventMeta implements Meta, MetaMap {

    private static final String   ID                 = "_qae.id",
                                  NAME               = "_qae.name", 
                                  DESCRIPTION        = "_qae.description",
                                  TEST_ID            = "_qae.testId",
                                  TYPE_ID            = "_qae.typeId",
                                  IS_BILLABLE        = "_qae.isBillable",
                                  REPORTING_SEQUENCE = "_qae.reportingSequence",
                                  REPORTING_TEXT     = "_qae.reportingText",
                                  
                                  TEST_NAME          = "_qae.test.name",
                                  TEST_METHOD_NAME   = "_test.method.name";                       

    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,NAME,DESCRIPTION,TEST_ID,
                                                  TYPE_ID,IS_BILLABLE,REPORTING_SEQUENCE,
                                                  REPORTING_TEXT,TEST_NAME));
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

    public static String getTestId() {
        return TEST_ID;
    }

    public static String getTypeId() {
        return TYPE_ID;
    }

    public static String getIsBillable() {
        return IS_BILLABLE;
    }

    public static String getReportingSequence() {
        return REPORTING_SEQUENCE;
    }

    public static String getReportingText() {
        return REPORTING_TEXT;
    }
    
    public static String getTestName() {
        return TEST_NAME;
    }
    
    public static String getTestMethodName() {
        return TEST_METHOD_NAME;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String name) {
        return "QaEvent _qae left join _qae.test _test left join _test.method _method ";
    }

}
