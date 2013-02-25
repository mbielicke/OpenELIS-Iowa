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
 * Event Log META DATA
 *
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class EventLogMeta implements Meta, MetaMap {
    private static final String    ID = "_eventLog.id",
                                   TYPE_ID = "_eventLog.typeId",
                                   SOURCE = "_eventLog.source", 
                                   REFERENCE_TABLE_ID = "_eventLog.referenceTableId",
                                   REFERENCE_ID = "_eventLog.referenceId",
                                   LEVEL_ID = "_eventLog.levelId",
                                   SYSTEM_USER_ID = "_eventLog.systemUserId",
                                   TIME_STAMP = "_eventLog.timeStamp",
                                   TEXT = "_eventLog.text";
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, TYPE_ID, SOURCE, REFERENCE_TABLE_ID,
                                                  REFERENCE_ID, LEVEL_ID, SYSTEM_USER_ID, 
                                                  TIME_STAMP, TEXT));
    }
    
    public static HashSet<String> getNames() {
        return names;
    }

    public static void setNames(HashSet<String> names) {
        EventLogMeta.names = names;
    }

    public static String getId() {
        return ID;
    }

    public static String getTypeId() {
        return TYPE_ID;
    }

    public static String getSource() {
        return SOURCE;
    }

    public static String getReferenceTableId() {
        return REFERENCE_TABLE_ID;
    }

    public static String getReferenceId() {
        return REFERENCE_ID;
    }

    public static String getLevelId() {
        return LEVEL_ID;
    }

    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }

    public static String getTimeStamp() {
        return TIME_STAMP;
    }

    public static String getText() {
        return TEXT;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        return "EventLog _eventLog ";
    }
}
