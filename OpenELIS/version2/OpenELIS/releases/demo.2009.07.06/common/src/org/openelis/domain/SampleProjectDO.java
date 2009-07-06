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

import org.openelis.utilcommon.DataBaseUtil;

public class SampleProjectDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer projectId;
    protected String isPermanent;
    
    protected ProjectDO projectDO = new ProjectDO();
    
    public SampleProjectDO(){
        
    }
    
    public SampleProjectDO(Integer id, Integer sampleId, Integer projectId, String isPermanent){
        setId(id);
        setSampleId(sampleId);
        setProjectId(projectId);
        setIsPermanent(isPermanent);
        
    }
    
    public SampleProjectDO(Integer id, Integer sampleId, Integer projectId, String isPermanent,
                           String name, String description, Date startedDate,
                           Date completedDate, String isActive, String referenceTo,
                           Integer ownerId, Integer scriptletId){
        setId(id);
        setSampleId(sampleId);
        setProjectId(projectId);
        setIsPermanent(isPermanent);
        
        //project params
        projectDO.setId(projectId);
        projectDO.setName(name);
        projectDO.setDescription(description);
        projectDO.setStartedDate(startedDate);
        projectDO.setCompletedDate(completedDate);
        projectDO.setIsActive(isActive);
        projectDO.setReferenceTo(referenceTo);
        projectDO.setOwnerId(ownerId);
        projectDO.setScriptletId(scriptletId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(String isPermanent) {
        this.isPermanent = DataBaseUtil.trim(isPermanent);
    }

    public ProjectDO getProject() {
        return projectDO;
    }

    public void setProject(ProjectDO project) {
        this.projectDO = project;
    }

}
