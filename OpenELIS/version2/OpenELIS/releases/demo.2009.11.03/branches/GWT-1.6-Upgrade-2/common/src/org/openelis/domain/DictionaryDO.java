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
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class DictionaryDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;        
    protected Integer category; 
    protected Integer relatedEntryId;   
    protected String systemName;        
    protected String isActive;         
    protected String localAbbrev;      
    protected String entry;
    protected Boolean delete = false;
    protected String relatedEntryText;
    
    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }
    
    public DictionaryDO(){
        
    }

    public DictionaryDO(Integer id, Integer category, Integer relatedEntryId,String relatedEntryText,
          String systemName,String isActive,  String localAbbrev, String entry){
        setId(id);
        setRelatedEntryId(relatedEntryId);
        setRelatedEntryText(relatedEntryText);
        setSystemName(systemName);
        setIsActive(isActive);
        setLocalAbbrev(localAbbrev);
        setEntry(entry);
    }
    
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = DataBaseUtil.trim(entry);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
    }

    public String getLocalAbbrev() {
        return localAbbrev;
    }

    public void setLocalAbbrev(String localAbbrev) {
        this.localAbbrev = DataBaseUtil.trim(localAbbrev);
    }  

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = DataBaseUtil.trim(systemName);
    }

    public String getRelatedEntryText() {
        return relatedEntryText;
    }

    public void setRelatedEntryText(String relatedEntryText) {
        this.relatedEntryText = DataBaseUtil.trim(relatedEntryText);
    }

    public Integer getRelatedEntryId() {
        return relatedEntryId;
    }

    public void setRelatedEntryId(Integer relatedEntryId) {
        this.relatedEntryId = relatedEntryId;
    }

}
