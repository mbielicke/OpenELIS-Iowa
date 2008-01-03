package org.openelis.remote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.CategoryDO;

@Remote
public interface CategoryRemote {
    //  method to return list of category ids and system names by the letter they start with
    public List getProviderNameListByLetter(String letter, int startPos, int maxResults);
    
    //  method to return category 
    public CategoryDO getCategory(Integer categoryId, boolean unlock);
    
    //  update initial call for provider
    public CategoryDO getProviderUpdate(Integer id) throws Exception;
    
    // commit a change to category, or insert a new category
    public Integer updateProvider(CategoryDO categoryDO, List dictEntries);
    
    //  method to query for category
    public List query(HashMap fields, int first, int max) throws RemoteException;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
        
    
}
