package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class QaEventTestMeta implements Meta {
    
    private String tableName = "test";
    private String entityName = "qaevent.test";
    private boolean includeInFrom = true;
    
    public static final String 
      ID        =   "test.id",
      NAME      =   "test.name", 
      METHOD    =   "test.methodId"; 

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {ID, NAME, METHOD};
    
    private static HashMap<String,String> columnHashList;

    private static QaEventTestMeta qaEventTestMeta =  new QaEventTestMeta();
    
    private QaEventTestMeta(){
        
    }
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(5), "");
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
    
    public static QaEventTestMeta getInstance(){
        return qaEventTestMeta;
    }

    public static String id(){
        return columnNames[0];
    }
    
    public static String name(){
        return columnNames[1];
    }
    
    public static String methodId(){
        return columnNames[2];
    }
}
