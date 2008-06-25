
package org.openelis.meta;

/**
  * Provider META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ProviderMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Provider";
	
	private static final String
              ID					="id",
              LAST_NAME					="lastName",
              FIRST_NAME					="firstName",
              MIDDLE_NAME					="middleName",
              TYPE_ID					="typeId",
              NPI					="npi";

  	private static final String[] columnNames = {
  	  ID,LAST_NAME,FIRST_NAME,MIDDLE_NAME,TYPE_ID,NPI};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ProviderMeta() {
		init();        
    }
    
    public ProviderMeta(String path) {
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

    public String getLastName() {
        return path + LAST_NAME;
    } 

    public String getFirstName() {
        return path + FIRST_NAME;
    } 

    public String getMiddleName() {
        return path + MIDDLE_NAME;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getNpi() {
        return path + NPI;
    } 

  
}   
