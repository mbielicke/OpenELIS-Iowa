
package org.openelis.meta;

/**
  * Attachment META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AttachmentMeta implements Meta {
  	private static final String tableName = "attachment";
	private static final String entityName = "Attachment";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="attachment.id",
              TYPE_ID					="attachment.typeId",
              FILENAME					="attachment.filename",
              DESCRIPTION					="attachment.description",
              STORAGE_REFERENCE					="attachment.storageReference";


  	private static final String[] columnNames = {
  	  ID,TYPE_ID,FILENAME,DESCRIPTION,STORAGE_REFERENCE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AttachmentMeta attachmentMeta = new AttachmentMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AttachmentMeta() {
        
    }
    
    public static AttachmentMeta getInstance() {
        return attachmentMeta;
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

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getFilename() {
    return FILENAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getStorageReference() {
    return STORAGE_REFERENCE;
  } 

  
}   
