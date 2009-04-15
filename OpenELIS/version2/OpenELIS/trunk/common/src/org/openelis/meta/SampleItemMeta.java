
package org.openelis.meta;

/**
  * SampleItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class SampleItemMeta implements Meta {
  	private String path = "";
	private static final String entityName = "SampleItem";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              SAMPLE_ITEM_ID					="sampleItemId",
              ITEM_SEQUENCE					="itemSequence",
              TYPE_OF_SAMPLE_ID					="typeOfSampleId",
              SOURCE_OF_SAMPLE_ID					="sourceOfSampleId",
              SOURCE_OTHER					="sourceOther",
              CONTAINER_ID					="containerId",
              CONTAINER_REFERENCE					="containerReference",
              QUANTITY					="quantity",
              UNIT_OF_MEASURE_ID					="unitOfMeasureId";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,SAMPLE_ITEM_ID,ITEM_SEQUENCE,TYPE_OF_SAMPLE_ID,SOURCE_OF_SAMPLE_ID,SOURCE_OTHER,CONTAINER_ID,CONTAINER_REFERENCE,QUANTITY,UNIT_OF_MEASURE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleItemMeta() {
		init();        
    }
    
    public SampleItemMeta(String path) {
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

    public String getSampleItemId() {
        return path + SAMPLE_ITEM_ID;
    } 

    public String getItemSequence() {
        return path + ITEM_SEQUENCE;
    } 

    public String getTypeOfSampleId() {
        return path + TYPE_OF_SAMPLE_ID;
    } 

    public String getSourceOfSampleId() {
        return path + SOURCE_OF_SAMPLE_ID;
    } 

    public String getSourceOther() {
        return path + SOURCE_OTHER;
    } 

    public String getContainerId() {
        return path + CONTAINER_ID;
    } 

    public String getContainerReference() {
        return path + CONTAINER_REFERENCE;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

    public String getUnitOfMeasureId() {
        return path + UNIT_OF_MEASURE_ID;
    } 

  
}   
