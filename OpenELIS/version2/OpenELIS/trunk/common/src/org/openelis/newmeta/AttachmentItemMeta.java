
package org.openelis.newmeta;

/**
  * AttachmentItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class AttachmentItemMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "AttachmentItem";
	
	private static final String
              ID					="id",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId",
              ATTACHMENT_ID					="attachmentId";

  	private static final String[] columnNames = {
  	  ID,REFERENCE_ID,REFERENCE_TABLE_ID,ATTACHMENT_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AttachmentItemMeta() {
		init();        
    }
    
    public AttachmentItemMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getAttachmentId() {
        return path + ATTACHMENT_ID;
    } 

  
}   
