package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class QaEventMeta implements Meta {
        
    private String tableName = "qaevent";
    private String entityName = "QaEvent";
    private boolean includeInFrom = true;
    
    public static final String
    ID                      = "qaevent.id",
    NAME                    = "qaevent.name",
    DESCRIPTION             = "qaevent.description",  
    TEST                    = "qaevent.test",
    TYPE                    = "qaevent.type", 
    IS_BILLABLE             = "qaevent.isBillable",
    REPORTING_SEQUENCE      = "qaevent.reportingSequence",
    REPORTING_TEXT          = "qaevent.reportingText";
        
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, NAME, DESCRIPTION, TEST, TYPE, IS_BILLABLE,REPORTING_SEQUENCE,REPORTING_TEXT};
    
    private static HashMap<String,String> columnHashList;

    private static QaEventMeta qaEventMeta =  new QaEventMeta();
    
    private QaEventMeta(){
        
    }
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(8), "");
       }
    
    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        return includeInFrom;
    }
    
    public static QaEventMeta getInstance(){
        return qaEventMeta;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String name(){
        return columnNames[1];
    }
    
    public static String description(){
        return columnNames[2];
    }
    
    public static String test(){
        return columnNames[3];
    }
    
    public static String type(){
        return columnNames[4];
    }
    
    public static String isBillable(){
        return columnNames[5];
    }
    
    public static String reportingSequence(){
        return columnNames[6];
    }
    
    public static String reportingText(){
        return columnNames[7];
    }


}
