package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;

@Remote
public interface CategoryRemote {
    
    //  method to return category without performing locking
    public CategoryDO getCategory(Integer categoryId);
    
    public CategoryDO getCategoryAndUnlock(Integer categoryId);
    
    public CategoryDO getCategoryAndLock(Integer categoryId)throws Exception;
    
    // commit a change to category, or insert a new category
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries)throws Exception;
    
    //  method to query for category
    public List query(HashMap fields, int first, int max) throws Exception;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
        
     //  method to return just dictionary entries
    public List getDictionaryEntries(Integer categoryId);
        
    public List getMatchingEntries(String entry,int maxResults);
    
    public Integer getEntryIdForSystemName(String systemName) throws Exception;
    
    public Integer getEntryIdForEntry(String entry) throws Exception;
    
    public Integer getCategoryId(String systemName); 
    
    public List getDropdownValues(Integer categoryId);
    
    public Object[] autoCompleteLookupById(Integer id)throws Exception;
    
    public List validateForUpdate(CategoryDO categoryDO, List<DictionaryDO> dictDOList);
    
    public List validateForAdd(CategoryDO categoryDO, List<DictionaryDO> dictDOList);
}
