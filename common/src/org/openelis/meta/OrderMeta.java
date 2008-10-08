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
  * Order META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrderMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Order";
	
	private static final String
              ID					="id",
              STATUS_ID					="statusId",
              ORDERED_DATE					="orderedDate",
              NEEDED_IN_DAYS					="neededInDays",
              REQUESTED_BY					="requestedBy",
              COST_CENTER_ID					="costCenterId",
              ORGANIZATION_ID					="organizationId",
              IS_EXTERNAL					="isExternal",
              EXTERNAL_ORDER_NUMBER					="externalOrderNumber",
              REPORT_TO_ID					="reportToId",
              BILL_TO_ID					="billToId",
              SHIP_FROM_ID                  ="shipFromId",
              DESCRIPTION                   ="description";

  	private static final String[] columnNames = {
  	  ID,STATUS_ID,ORDERED_DATE,NEEDED_IN_DAYS,REQUESTED_BY,COST_CENTER_ID,ORGANIZATION_ID,IS_EXTERNAL,EXTERNAL_ORDER_NUMBER,REPORT_TO_ID,BILL_TO_ID, SHIP_FROM_ID,DESCRIPTION};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrderMeta() {
		init();        
    }
    
    public OrderMeta(String path) {
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

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getOrderedDate() {
        return path + ORDERED_DATE;
    } 

    public String getNeededInDays() {
        return path + NEEDED_IN_DAYS;
    } 

    public String getRequestedBy() {
        return path + REQUESTED_BY;
    } 

    public String getCostCenterId() {
        return path + COST_CENTER_ID;
    } 

    public String getOrganizationId() {
        return path + ORGANIZATION_ID;
    } 

    public String getIsExternal() {
        return path + IS_EXTERNAL;
    } 

    public String getExternalOrderNumber() {
        return path + EXTERNAL_ORDER_NUMBER;
    } 

    public String getReportToId() {
        return path + REPORT_TO_ID;
    } 

    public String getBillToId() {
        return path + BILL_TO_ID;
    } 

  public String getShipFromId(){
      return path + SHIP_FROM_ID;
  }
  
  public String getDescription(){
      return path + DESCRIPTION;
  }
  
}   
