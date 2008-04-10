package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class LabelMeta implements Meta {
    
    private String tableName = "label";
    private String entityName = "Label";
    private boolean includeInFrom = true;
    
    private static LabelMeta labelMeta = new LabelMeta();   
    
    public static final String
    ID                      = "label.id",
    NAME                    = "label.name",
    DESCRIPTION             = "label.description",
    PRINTER_TYPE            = "label.printerType",
    SCRIPTLET               = "label.scriptlet";
    
    private static final String[] columnNames = {ID, NAME, DESCRIPTION, PRINTER_TYPE,SCRIPTLET};
    
    private static HashMap<String,String> columnHashList;
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(6), "");
       }
    
    private LabelMeta(){
        
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
    
    public static LabelMeta getInstance(){
        return labelMeta;
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
    
    public static String printerType(){
        return columnNames[3];
        
    }
    
    public static String scriptlet(){
        return columnNames[4];
    }

}
