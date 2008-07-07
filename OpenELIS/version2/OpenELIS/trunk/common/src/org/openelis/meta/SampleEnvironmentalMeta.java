/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.meta;

/**
  * SampleEnvironmental META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class SampleEnvironmentalMeta implements Meta {
  	private String path = "";
	private static final String entityName = "SampleEnvironmental";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              IS_HAZARDOUS					="isHazardous",
              DESCRIPTION					="description",
              COLLECTOR					="collector",
              COLLECTOR_PHONE					="collectorPhone",
              SAMPLING_LOCATION					="samplingLocation",
              ADDRESS_ID					="addressId";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,IS_HAZARDOUS,DESCRIPTION,COLLECTOR,COLLECTOR_PHONE,SAMPLING_LOCATION,ADDRESS_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleEnvironmentalMeta() {
		init();        
    }
    
    public SampleEnvironmentalMeta(String path) {
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

    public String getIsHazardous() {
        return path + IS_HAZARDOUS;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
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
