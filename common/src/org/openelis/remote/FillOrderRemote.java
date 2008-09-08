package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface FillOrderRemote {
    //commit a change to inventory receipt, or insert a new inventory receipt
    //public void updateInventoryReceipt(List inventoryReceipts) throws Exception;
    
    //method to query for orders
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //method to query for orders..and also lock the necessary records
     public List queryAndLock(HashMap fields, int first, int max) throws Exception;
     
     //method to query for orders..and also unlock the necessary records
     public List queryAndUnlock(HashMap fields, int first, int max) throws Exception;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
          
     //method to validate the fields before the backend updates it in the database
     public List validateForProcess(List orders);
}
