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

/**
 * Worksheet Builder META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class WorksheetMeta implements Meta, MetaMap {
    private static final String ID  = "_worksheet.id",
                                CREATED_DATE = "_worksheet.createdDate",
                                SYSTEM_USER_ID = "_worksheet.systemUserId",
                                STATUS_ID = "_worksheet.statusId",
                                FORMAT_ID = "_worksheet.formatId",
                                RELATED_WORKSHEET_ID = "_worksheet.relatedWorksheetId",
//                                WSHT_INSTRUMENT_ID = "_worksheet.instrumentId",
                                DESCRIPTION = "_worksheet.description",
                                INSTRUMENT_NAME = "_instrument.name";
                                

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, CREATED_DATE, SYSTEM_USER_ID,
                                                  STATUS_ID, FORMAT_ID, RELATED_WORKSHEET_ID,
                                                  /*WSHT_INSTRUMENT_ID,*/ DESCRIPTION,
                                                  INSTRUMENT_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getCreatedDate() {
        return CREATED_DATE;
    }

    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }

    public static String getStatusId() {
        return STATUS_ID;
    }

    public static String getFormatId() {
        return FORMAT_ID;
    }

    public static String getRelatedWorksheetId() {
        return RELATED_WORKSHEET_ID;
    }

//    public static String getInstrumentId() {
//        return WSHT_INSTRUMENT_ID;
//    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getInstrumentName() {
        return INSTRUMENT_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Worksheet _worksheet"+
               " LEFT JOIN _worksheet.instrument _instrument ";

        return from;
    }
}