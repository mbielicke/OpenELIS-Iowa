package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseTagMeta implements Meta, MetaMap {
    
    private static final String ID = "id", 
                         CASE_ID = "case_id", 
                         TYPE_ID = "type_id",
                         SYSTEM_USER_ID = "system_user_id",
                         CREATED_DATE = "created_date",
                         REMINDER_DATE = "reminder_date",
                         COMPLETED_DATE = "completed_date",
                         NOTE = "note";
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,CASE_ID,TYPE_ID,SYSTEM_USER_ID,CREATED_DATE,REMINDER_DATE,COMPLETED_DATE,NOTE));
    }
    
    public static String getId() {
        return ID;
    }
    
    public static String getCaseId() {
        return CASE_ID;
    }
    
    public static String getTypeId() {
        return TYPE_ID;
    }
    
    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    }
    
    public static String getCreatedDate() {
        return CREATED_DATE;
    }
    
    public static String getReminderDate() {
        return REMINDER_DATE;
    }
    
    public static String getCompletedDate() {
        return COMPLETED_DATE;
    }
    
    public static String getNote() {
        return NOTE;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;

        from = "CaseTag _caseTag ";

        return from;
    }

}
