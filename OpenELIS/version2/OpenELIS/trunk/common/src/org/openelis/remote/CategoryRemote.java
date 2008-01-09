package org.openelis.remote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.CategoryDO;

@Remote
public interface CategoryRemote {
    //  method to return list of category ids and system names by the letter they start with
    public List getCategoryNameListByLetter(String letter, int startPos, int maxResults);
    
    //  method to return category 
    public CategoryDO getCategory(Integer categoryId, boolean unlock);
    
    //  update initial call for category
    public CategoryDO getCategoryUpdate(Integer id) throws Exception;
    
    // commit a change to category, or insert a new category
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries);
    
    //  method to query for category
    public List query(HashMap fields, int first, int max) throws RemoteException;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
        
     //  method to return just dictionary entries
    public List getDictionaryEntries(Integer categoryId);
    
    // method to return the id for a given row,identified by the value "entry" here,in the Dictionary table 
    public Integer getEntryId(String entry);
}
