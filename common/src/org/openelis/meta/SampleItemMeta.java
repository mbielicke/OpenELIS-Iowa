/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.meta;

/**
  * SampleItem META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class SampleItemMeta implements Meta {
  	protected String path = "";
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
