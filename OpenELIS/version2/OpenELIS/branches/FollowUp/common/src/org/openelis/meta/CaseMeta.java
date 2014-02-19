package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.ui.common.Meta;
import org.openelis.ui.common.MetaMap;

public class CaseMeta implements Meta, MetaMap {
    
    private static final String ID = "id",
                                CREATED = "created",
                                PATIENT_ID = "patient_id",
                                NEXTKIN_ID = "nextkin_id",
                                CASE_PATIENT_ID = "case_patient_id",
                                CASE_NEXTKIN_ID = "case_nextkin_id",
                                ORGANIZATION_ID = "organization_id",
                                COMPLETE_DATE = "complete_date",
                                IS_FINALIZED = "is_finalized";
    
    private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,CREATED,PATIENT_ID,NEXTKIN_ID,CASE_PATIENT_ID,CASE_NEXTKIN_ID,ORGANIZATION_ID,COMPLETE_DATE,IS_FINALIZED));
    }
    
    public static String getId() {
        return ID;
    }
    
    public static String getCreated() {
        return CREATED;
    }
    
    public static String getNextkinId() {
        return NEXTKIN_ID;
    }
    
    public static String getCasePatientId() {
        return CASE_PATIENT_ID;
    }
    
    public static String getCaseNextkinId() {
        return CASE_NEXTKIN_ID;
    }
    
    public static String getOrganizationId() {
        return ORGANIZATION_ID;
    }
    
    public static String getCompleteDate() {
        return COMPLETE_DATE;
    }
    
    public static String getIsFinalized() {
        return IS_FINALIZED;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;

        from = "Case _case ";

        return from;
    }
    
    

}
