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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
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
    
    public CategoryDO getCategoryAndUnlock(Integer categoryId, String session);
    
    public CategoryDO getCategoryAndLock(Integer categoryId, String session)throws Exception;
    
    // commit a change to category, or insert a new category
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries)throws Exception;
    
    //  method to query for category
    public List query(HashMap fields, int first, int max) throws Exception;
    
     //  method to return just dictionary entries
    public List getDictionaryEntries(Integer categoryId);
        
    public List getMatchingEntries(String entry,int maxResults);
    
    public Integer getEntryIdForSystemName(String systemName) throws Exception;
    
    public Integer getEntryIdForEntry(String entry) throws Exception;
    
    public Integer getCategoryId(String systemName); 
    
    public List getDropdownValues(Integer categoryId);
    
    public List validateForUpdate(CategoryDO categoryDO, List<DictionaryDO> dictDOList);
    
    public List validateForAdd(CategoryDO categoryDO, List<DictionaryDO> dictDOList);
}
