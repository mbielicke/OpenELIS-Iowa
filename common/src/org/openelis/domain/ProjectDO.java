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
import java.util.Date;

import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class ProjectDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String description;
    protected Datetime startedDate;
    protected Datetime completedDate;
    protected String isActive;
    protected String referenceTo;
    protected Integer ownerId;
    protected String ownerName;
    protected Integer scriptletId;
    protected String scriptletName;
    
    public ProjectDO(){
        
    }
    
    public ProjectDO(Integer id, String name, String description, Date startedDate,
                     Date completedDate, String isActive, String referenceTo,
                     Integer ownerId, Integer scriptletId,String scriptletName){
        setId(id);
        setName(name);
        setDescription(description);
        setStartedDate(startedDate);
        setCompletedDate(completedDate);
        setIsActive(isActive);
        setReferenceTo(referenceTo);
        setOwnerId(ownerId);
        setScriptletId(scriptletId);
        setScriptletName(scriptletName);
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }
    public Datetime getStartedDate() {
        return startedDate;
    }
    public void setStartedDate(Date startedDate) {
        this.startedDate = new Datetime(Datetime.YEAR, Datetime.DAY, startedDate);
    }
    public Datetime getCompletedDate() {
        return completedDate;
    }
    public void setCompletedDate(Date completedDate) {
        this.completedDate = new Datetime(Datetime.YEAR, Datetime.DAY, completedDate);
    }
    public String getIsActive() {
        return isActive;
    }
    public void setIsActive(String isActive) {
        this.isActive = DataBaseUtil.trim(isActive);
    }
    public String getReferenceTo() {
        return referenceTo;
    }
    public void setReferenceTo(String referenceTo) {
        this.referenceTo = DataBaseUtil.trim(referenceTo);
    }
    public Integer getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
    public Integer getScriptletId() {
        return scriptletId;
    }
    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = DataBaseUtil.trim(ownerName);
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }
}
