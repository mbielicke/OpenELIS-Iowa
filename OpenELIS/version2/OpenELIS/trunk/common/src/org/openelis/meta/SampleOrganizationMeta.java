
package org.openelis.meta;

/**
  * SampleOrganization META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleOrganizationMeta implements Meta {
  	private static final String tableName = "sample_organization";
	private static final String entityName = "SampleOrganization";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample_organization.id",
              SAMPLE_ID					="sample_organization.sampleId",
              ORGANIZATION_ID					="sample_organization.organizationId",
              TYPE_ID					="sample_organization.typeId";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,ORGANIZATION_ID,TYPE_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleOrganizationMeta sample_organizationMeta = new SampleOrganizationMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleOrganizationMeta() {
        
    }
    
    public static SampleOrganizationMeta getInstance() {
        return sample_organizationMeta;
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

  public static String getSampleId() {
    return SAMPLE_ID;
  } 

  public static String getOrganizationId() {
    return ORGANIZATION_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  
}   
