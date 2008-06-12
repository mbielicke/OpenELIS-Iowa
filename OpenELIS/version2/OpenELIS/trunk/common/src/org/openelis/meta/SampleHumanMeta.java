
package org.openelis.meta;

/**
  * SampleHuman META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleHumanMeta implements Meta {
  	private static final String tableName = "sample_human";
	private static final String entityName = "SampleHuman";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample_human.id",
              SAMPLE_ID					="sample_human.sampleId",
              PATIENT_ID					="sample_human.patientId",
              PROVIDER_ID					="sample_human.providerId",
              PROVIDER_PHONE					="sample_human.providerPhone";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,PATIENT_ID,PROVIDER_ID,PROVIDER_PHONE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleHumanMeta sample_humanMeta = new SampleHumanMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleHumanMeta() {
        
    }
    
    public static SampleHumanMeta getInstance() {
        return sample_humanMeta;
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

  public static String getPatientId() {
    return PATIENT_ID;
  } 

  public static String getProviderId() {
    return PROVIDER_ID;
  } 

  public static String getProviderPhone() {
    return PROVIDER_PHONE;
  } 

  
}   
