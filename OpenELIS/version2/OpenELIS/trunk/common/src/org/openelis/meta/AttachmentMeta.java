
package org.openelis.meta;

/**
  * Attachment META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AttachmentMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Attachment";
	
	private static final String
              ID					="id",
              TYPE_ID					="typeId",
              FILENAME					="filename",
              DESCRIPTION					="description",
              STORAGE_REFERENCE					="storageReference";

  	private static final String[] columnNames = {
  	  ID,TYPE_ID,FILENAME,DESCRIPTION,STORAGE_REFERENCE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AttachmentMeta() {
		init();        
    }
    
    public AttachmentMeta(String path) {
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

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getFilename() {
        return path + FILENAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getStorageReference() {
        return path + STORAGE_REFERENCE;
    } 

  
}   
