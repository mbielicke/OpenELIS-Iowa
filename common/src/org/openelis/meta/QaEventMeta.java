package org.openelis.meta;

import java.util.HashMap;
import org.openelis.util.Meta;

public class QaEventMeta implements Meta {
    private static final String tableName = "qaevent";
    private static final String entityName = "Qaevent";
    private boolean includeInFrom = true;
    
    public static final String
              ID                    ="qaevent.id",
              NAME                  ="qaevent.name",
              DESCRIPTION                   ="qaevent.description",
              TEST_ID                   ="qaevent.test_id",
              TYPE_ID                   ="qaevent.type_id",
              IS_BILLABLE                   ="qaevent.is_billable",
              REPORTING_SEQUENCE                    ="qaevent.reporting_sequence",
              REPORTING_TEXT                    ="qaevent.reporting_text";


    private static final String[] columnNames = {
      ID,NAME,DESCRIPTION,TEST_ID,TYPE_ID,IS_BILLABLE,REPORTING_SEQUENCE,REPORTING_TEXT};
      
    private static HashMap<String,String> columnHashList;

    private static final QaEventMeta qaEventMeta = new QaEventMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private QaEventMeta() {
        
    }
    
    public static QaEventMeta getInstance() {
        return qaEventMeta;
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
        // TODO Auto-generated method stub
        return includeInFrom;
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

}
