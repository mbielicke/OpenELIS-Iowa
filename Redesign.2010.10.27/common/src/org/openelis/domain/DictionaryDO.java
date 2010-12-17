/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * Class represents the fields in database table analysis.
 */

public class DictionaryDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sortOrder,categoryId, relatedEntryId;
    protected String          systemName, isActive, localAbbrev, entry;

    public DictionaryDO() {
    }

    public DictionaryDO(Integer id,Integer sortOrder,Integer categoryId, Integer relatedEntryId, String systemName,
                        String isActive, String localAbbrev, String entry) {
        setId(id);
        setCategoryId(categoryId);
        setSortOrder(sortOrder);
        setRelatedEntryId(relatedEntryId);
        setSystemName(systemName);
        setIsActive(isActive);
        setLocalAbbrev(localAbbrev);
        setEntry(entry);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        _changed = true;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if(this.sortOrder == null || !(this.sortOrder.equals(sortOrder))) {
            this.sortOrder = sortOrder;
            _changed = true;
        }
    }

    public Integer getRelatedEntryId() {
        return relatedEntryId;
    }

    public void setRelatedEntryId(Integer relatedEntryId) {
        this.relatedEntryId = relatedEntryId;
        _changed = true;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = DataBaseUtil.trim(systemName);
        _changed = true;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
        _changed = true;
    }

    public String getLocalAbbrev() {
        return localAbbrev;
    }

    public void setLocalAbbrev(String localAbbrev) {
        this.localAbbrev = DataBaseUtil.trim(localAbbrev);
        _changed = true;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = DataBaseUtil.trim(entry);
        _changed = true;
    }
    
}