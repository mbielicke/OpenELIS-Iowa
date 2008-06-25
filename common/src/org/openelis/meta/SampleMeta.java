
package org.openelis.meta;

/**
  * Sample META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class SampleMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Sample";
	
	private static final String
              ID					="id",
              NEXT_ITEM_SEQUENCE					="nextItemSequence",
              DOMAIN_ID					="domainId",
              ACCESSION_NUMBER					="accessionNumber",
              REVISION					="revision",
              ENTERED_DATE					="enteredDate",
              RECEIVED_DATE					="receivedDate",
              RECEIVED_BY_ID					="receivedById",
              COLLECTION_DATE					="collectionDate",
              STATUS_ID					="statusId",
              PACKAGE_ID					="packageId",
              CLIENT_REFERENCE					="clientReference",
              RELEASED_DATE					="releasedDate";

  	private static final String[] columnNames = {
  	  ID,NEXT_ITEM_SEQUENCE,DOMAIN_ID,ACCESSION_NUMBER,REVISION,ENTERED_DATE,RECEIVED_DATE,RECEIVED_BY_ID,COLLECTION_DATE,STATUS_ID,PACKAGE_ID,CLIENT_REFERENCE,RELEASED_DATE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleMeta() {
		init();        
    }
    
    public SampleMeta(String path) {
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

    public String getNextItemSequence() {
        return path + NEXT_ITEM_SEQUENCE;
    } 

    public String getDomainId() {
        return path + DOMAIN_ID;
    } 

    public String getAccessionNumber() {
        return path + ACCESSION_NUMBER;
    } 

    public String getRevision() {
        return path + REVISION;
    } 

    public String getEnteredDate() {
        return path + ENTERED_DATE;
    } 

    public String getReceivedDate() {
        return path + RECEIVED_DATE;
    } 

    public String getReceivedById() {
        return path + RECEIVED_BY_ID;
    } 

    public String getCollectionDate() {
        return path + COLLECTION_DATE;
    } 

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getPackageId() {
        return path + PACKAGE_ID;
    } 

    public String getClientReference() {
        return path + CLIENT_REFERENCE;
    } 

    public String getReleasedDate() {
        return path + RELEASED_DATE;
    } 

  
}   
