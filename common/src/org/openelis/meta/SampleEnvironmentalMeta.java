
package org.openelis.meta;

/**
  * SampleEnvironmental META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleEnvironmentalMeta implements Meta {
  	private static final String tableName = "sample_environmental";
	private static final String entityName = "SampleEnvironmental";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample_environmental.id",
              SAMPLE_ID					="sample_environmental.sample_id",
              IS_HAZARDOUS					="sample_environmental.is_hazardous",
              DESCRIPTION					="sample_environmental.description",
              COLLECTOR					="sample_environmental.collector",
              COLLECTOR_PHONE					="sample_environmental.collector_phone",
              SAMPLING_LOCATION					="sample_environmental.sampling_location",
              ADDRESS_ID					="sample_environmental.address_id";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,IS_HAZARDOUS,DESCRIPTION,COLLECTOR,COLLECTOR_PHONE,SAMPLING_LOCATION,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleEnvironmentalMeta sample_environmentalMeta = new SampleEnvironmentalMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleEnvironmentalMeta() {
        
    }
    
    public static SampleEnvironmentalMeta getInstance() {
        return sample_environmentalMeta;
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

  public static String getIsHazardous() {
    return IS_HAZARDOUS;
  } 

  public static String getDescription() {
    return DESCRIPTION;
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
