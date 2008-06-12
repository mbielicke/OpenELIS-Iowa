
package org.openelis.meta;

/**
  * Sample META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleMeta implements Meta {
  	private static final String tableName = "sample";
	private static final String entityName = "Sample";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample.id",
              NEXT_ITEM_SEQUENCE					="sample.next_item_sequence",
              DOMAIN_ID					="sample.domain_id",
              ACCESSION_NUMBER					="sample.accession_number",
              REVISION					="sample.revision",
              ENTERED_DATE					="sample.entered_date",
              RECEIVED_DATE					="sample.received_date",
              RECEIVED_BY_ID					="sample.received_by_id",
              COLLECTION_DATE					="sample.collection_date",
              STATUS_ID					="sample.status_id",
              PACKAGE_ID					="sample.package_id",
              CLIENT_REFERENCE					="sample.client_reference",
              RELEASED_DATE					="sample.released_date";


  	private static final String[] columnNames = {
  	  ID,NEXT_ITEM_SEQUENCE,DOMAIN_ID,ACCESSION_NUMBER,REVISION,ENTERED_DATE,RECEIVED_DATE,RECEIVED_BY_ID,COLLECTION_DATE,STATUS_ID,PACKAGE_ID,CLIENT_REFERENCE,RELEASED_DATE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleMeta sampleMeta = new SampleMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleMeta() {
        
    }
    
    public static SampleMeta getInstance() {
        return sampleMeta;
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

  public static String getNextItemSequence() {
    return NEXT_ITEM_SEQUENCE;
  } 

  public static String getDomainId() {
    return DOMAIN_ID;
  } 

  public static String getAccessionNumber() {
    return ACCESSION_NUMBER;
  } 

  public static String getRevision() {
    return REVISION;
  } 

  public static String getEnteredDate() {
    return ENTERED_DATE;
  } 

  public static String getReceivedDate() {
    return RECEIVED_DATE;
  } 

  public static String getReceivedById() {
    return RECEIVED_BY_ID;
  } 

  public static String getCollectionDate() {
    return COLLECTION_DATE;
  } 

  public static String getStatusId() {
    return STATUS_ID;
  } 

  public static String getPackageId() {
    return PACKAGE_ID;
  } 

  public static String getClientReference() {
    return CLIENT_REFERENCE;
  } 

  public static String getReleasedDate() {
    return RELEASED_DATE;
  } 

  
}   
