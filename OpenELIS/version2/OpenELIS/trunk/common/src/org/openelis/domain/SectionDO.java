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

import org.openelis.utilcommon.DataBaseUtil;

public class SectionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer id; 
    protected Integer organizationId; 
    protected String organizationName; 
    protected Integer parentSectionId;
    protected String name;
    protected String description;
    protected String isExternal;
    protected boolean delete;
    
    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public SectionDO() {

    }

    public SectionDO(Integer id,Integer organizationId,String organizationName,
                     String name,String description,Integer parentSectionId,
                     String isExternal) {

        setId(id);
        setOrganizationId(organizationId);
        setOrganizationName(organizationName);
        setName(name);
        setDescription(description);
        setParentSectionId(parentSectionId);
        setIsExternal(isExternal);        
    }
    
    public SectionDO(Integer id, String name) {
        setId(id);
        setName(name);
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public Integer getParentSectionId() {
        return parentSectionId;
    }

    public void setParentSectionId(Integer parentSectionId) {
        this.parentSectionId = parentSectionId;
    }

    public String getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(String isExternal) {
        this.isExternal = DataBaseUtil.trim(isExternal);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }
}
