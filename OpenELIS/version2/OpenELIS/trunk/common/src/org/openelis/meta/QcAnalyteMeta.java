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
  * QcAnalyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class QcAnalyteMeta implements Meta {
  	private String path = "";
	private static final String entityName = "QcAnalyte";
	
	private static final String
              ID					="id",
              QC_ID					="qcId",
              ANALYTE_ID					="analyteId",
              TYPE_ID					="typeId",
              VALUE					="value",
              IS_TRENDABLE					="isTrendable";

  	private static final String[] columnNames = {
  	  ID,QC_ID,ANALYTE_ID,TYPE_ID,VALUE,IS_TRENDABLE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public QcAnalyteMeta() {
		init();        
    }
    
    public QcAnalyteMeta(String path) {
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

    public String getQcId() {
        return path + QC_ID;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

    public String getIsTrendable() {
        return path + IS_TRENDABLE;
    } 

  
}   
