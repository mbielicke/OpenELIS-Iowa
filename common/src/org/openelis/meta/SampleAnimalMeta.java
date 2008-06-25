
package org.openelis.meta;

/**
  * SampleAnimal META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class SampleAnimalMeta implements Meta {
  	private String path = "";
	private static final String entityName = "SampleAnimal";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              ANIMAL_COMMON_NAME_ID					="animalCommonNameId",
              ANIMAL_SCIENTIFIC_NAME_ID					="animalScientificNameId",
              COLLECTOR					="collector",
              COLLECTOR_PHONE					="collectorPhone",
              SAMPLING_LOCATION					="samplingLocation",
              ADDRESS_ID					="addressId";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,ANIMAL_COMMON_NAME_ID,ANIMAL_SCIENTIFIC_NAME_ID,COLLECTOR,COLLECTOR_PHONE,SAMPLING_LOCATION,ADDRESS_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleAnimalMeta() {
		init();        
    }
    
    public SampleAnimalMeta(String path) {
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

    public String getAnimalCommonNameId() {
        return path + ANIMAL_COMMON_NAME_ID;
    } 

    public String getAnimalScientificNameId() {
        return path + ANIMAL_SCIENTIFIC_NAME_ID;
    } 

    public String getCollector() {
        return path + COLLECTOR;
    } 

    public String getCollectorPhone() {
        return path + COLLECTOR_PHONE;
    } 

    public String getSamplingLocation() {
        return path + SAMPLING_LOCATION;
    } 

    public String getAddressId() {
        return path + ADDRESS_ID;
    } 

  
}   
