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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table category.
 */

public class CategoryDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sectionId;
    protected String          systemName, name, description;

    public CategoryDO() {
    }

    public CategoryDO(Integer id, String systemName, String name, String description,
                      Integer sectionId) {
        setId(id);
        setSystemName(systemName);
        setName(name);
        setDescription(description);
        setSectionId(sectionId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = DataBaseUtil.trim(systemName);
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
        _changed = true;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
        _changed = true;
    }
    
    public boolean isChanged() {
        return _changed;
    }
}
