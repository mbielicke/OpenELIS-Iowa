
package org.openelis.meta;

/**
  * AttachmentItem META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AttachmentItemMeta implements Meta {
  	private static final String tableName = "attachment_item";
	private static final String entityName = "AttachmentItem";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="attachment_item.id",
              REFERENCE_ID					="attachment_item.reference_id",
              REFERENCE_TABLE_ID					="attachment_item.reference_table_id",
              ATTACHMENT_ID					="attachment_item.attachment_id";


  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,ATTACHMENT_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AttachmentItemMeta attachment_itemMeta = new AttachmentItemMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AttachmentItemMeta() {
        
    }
    
    public static AttachmentItemMeta getInstance() {
        return attachment_itemMeta;
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

  public static String getReferenceId() {
    return REFERENCE_ID;
  } 

  public static String getReferenceTableId() {
    return REFERENCE_TABLE_ID;
  } 

  public static String getAttachmentId() {
    return ATTACHMENT_ID;
  } 

  
}   
