
package org.openelis.meta;

/**
  * SampleAnimal META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleAnimalMeta implements Meta {
  	private static final String tableName = "sample_animal";
	private static final String entityName = "SampleAnimal";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample_animal.id",
              SAMPLE_ID					="sample_animal.sampleId",
              ANIMAL_COMMON_NAME_ID					="sample_animal.animalCommonNameId",
              ANIMAL_SCIENTIFIC_NAME_ID					="sample_animal.animalScientificNameId",
              COLLECTOR					="sample_animal.collector",
              COLLECTOR_PHONE					="sample_animal.collectorPhone",
              SAMPLING_LOCATION					="sample_animal.samplingLocation",
              ADDRESS_ID					="sample_animal.addressId";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,ANIMAL_COMMON_NAME_ID,ANIMAL_SCIENTIFIC_NAME_ID,COLLECTOR,COLLECTOR_PHONE,SAMPLING_LOCATION,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleAnimalMeta sample_animalMeta = new SampleAnimalMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleAnimalMeta() {
        
    }
    
    public static SampleAnimalMeta getInstance() {
        return sample_animalMeta;
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

  public static String getAnimalCommonNameId() {
    return ANIMAL_COMMON_NAME_ID;
  } 

  public static String getAnimalScientificNameId() {
    return ANIMAL_SCIENTIFIC_NAME_ID;
  } 

  public static String getCollector() {
    return COLLECTOR;
  } 

  public static String getCollectorPhone() {
    return COLLECTOR_PHONE;
  } 

  public static String getSamplingLocation() {
    return SAMPLING_LOCATION;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  
}   
