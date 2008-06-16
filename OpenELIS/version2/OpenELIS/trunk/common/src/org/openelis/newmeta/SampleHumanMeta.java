
package org.openelis.newmeta;

/**
  * SampleHuman META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class SampleHumanMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "SampleHuman";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              PATIENT_ID					="patientId",
              PROVIDER_ID					="providerId",
              PROVIDER_PHONE					="providerPhone";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,PATIENT_ID,PROVIDER_ID,PROVIDER_PHONE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleHumanMeta() {
		init();        
    }
    
    public SampleHumanMeta(String path) {
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

    public String getSampleId() {
        return path + SAMPLE_ID;
    } 

    public String getPatientId() {
        return path + PATIENT_ID;
    } 

    public String getProviderId() {
        return path + PROVIDER_ID;
    } 

    public String getProviderPhone() {
        return path + PROVIDER_PHONE;
    } 

  
}   
