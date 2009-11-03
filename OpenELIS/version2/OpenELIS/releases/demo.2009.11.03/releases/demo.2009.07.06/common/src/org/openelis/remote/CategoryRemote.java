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
package org.openelis.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.QueryStringField;

@Remote
public interface CategoryRemote {
    
    //  method to return category without performing locking
    public CategoryDO getCategory(Integer categoryId);
    
    public List getCategoryList();
    
    public CategoryDO getCategoryAndUnlock(Integer categoryId, String session);
    
    public CategoryDO getCategoryAndLock(Integer categoryId, String session)throws Exception;
    
    // commit a change to category, or insert a new category
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries)throws Exception;
    
    //  method to query for category
    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
    
     //  method to return just dictionary entries
    public List getDictionaryEntries(Integer categoryId);
    
    public List getDictionaryListByPatternAndCategory(QueryStringField pattern,Integer categoryId);       
        
    public List getMatchingEntries(String entry,int maxResults);
    
    public Integer getEntryIdForSystemName(String systemName);
    
    public DictionaryDO getDictionaryDOBySystemName(String systemName);
    
    public String getSystemNameForEntryId(Integer entryId);
    
    public DictionaryDO getDictionaryDOByEntryId(Integer entryId);
    
    public List getListByCategoryName(String categoryName);
    
    public Integer getEntryIdForEntry(String entry) throws Exception;
    
    public Integer getCategoryId(String systemName); 
    
    public List getDropdownValues(Integer categoryId);
    
    public List getDropdownAbbreviations(Integer categoryId);
    
    public List getIdEntrySystemNames(Integer categoryId);        
    
    public Integer getNumResultsAffected(String entry, Integer id);
    
    public String getEntryById(Integer id);
}
