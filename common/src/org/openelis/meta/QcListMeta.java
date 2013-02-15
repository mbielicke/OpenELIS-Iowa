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

public class QcListMeta implements Meta, MetaMap {

    private static final String     QC_NAME = "_qc.name",
                    QC_TYPE = "_qc.typeId",
                    WORKSHEET_CREATED_DATE  = "_worksheet.createdDate",
                    WORKSHEET_CREATED_DATE_FROM = "_worksheet.createdDateFrom",
                    WORKSHEET_CREATED_DATE_TO = "_worksheet.createdDateTo";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(QC_NAME,
                                                  QC_TYPE,
                                                  WORKSHEET_CREATED_DATE,
                                                  WORKSHEET_CREATED_DATE_FROM,
                                                  WORKSHEET_CREATED_DATE_TO
                                                  ));
    }
    
    public static String getQCName() {
        return QC_NAME;
    }

    public static String getQCType() {
        return QC_TYPE;
    }

    public static String getWorksheetCreatedDate() {
        return WORKSHEET_CREATED_DATE;
    }
    
    public static String getWorksheetCreatedDateFrom() {
        return WORKSHEET_CREATED_DATE_FROM;
    }
    
    public static String getWorksheetCreatedDateTo() {
        return WORKSHEET_CREATED_DATE_TO;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        return null;
    }
}
