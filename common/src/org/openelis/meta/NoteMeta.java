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
 * Note META Data
 */

import java.util.Arrays;
import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class NoteMeta implements Meta {

    private static final String    ID = "id",
                                   REFERENCE_ID = "referenceId",
                                   REFERENCE_TABLE_ID = "referenceTableId",
                                   TIMESTAMP = "timestamp",
                                   IS_EXTERNAL = "isExternal",
                                   SYSTEM_USER_ID = "systemUserId",
                                   SUBJECT = "subject",
                                   TEXT = "text";

    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID, REFERENCE_ID, REFERENCE_TABLE_ID,
                                                  TIMESTAMP, IS_EXTERNAL, SYSTEM_USER_ID,
                                                  SUBJECT, TEXT));
    }

    public static String getId() {
        return ID;
    }

    public static String getReferenceId() {
        return REFERENCE_ID;
    }

    public static String getReferenceTableId() {
        return REFERENCE_TABLE_ID;
    }

    public static String getTimestamp() {
        return TIMESTAMP;
    }

    public static String getIsExternal() {
        return IS_EXTERNAL;
    }

    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }

    public static String getSubject() {
        return SUBJECT;
    }

    public static String getText() {
        return TEXT;
    }

    public boolean hasColumn(String name) {
        return names.contains(name);
    }

    public String buildFrom(String where) {
        return "Note ";
    }
}
