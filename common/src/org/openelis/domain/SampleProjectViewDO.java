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

import org.openelis.ui.common.DataBaseUtil;

/**
 * The class extends sample project DO and carries several commonly used fields
 * such as project name and description. The additional fields are for
 * read/display only and do not get committed to the database. Note: isChanged
 * will reflect any changes to read/display fields.
 */

public class SampleProjectViewDO extends SampleProjectDO {

    private static final long serialVersionUID = 1L;

    protected String          projectName, projectDescription, projectIsActive;

    public SampleProjectViewDO() {
    }

    public SampleProjectViewDO(Integer id, Integer sampleId, Integer projectId, String isPermanent,
                               String projectName, String projectDescription, String projectIsActive) {
        super(id, sampleId, projectId, isPermanent);
        setProjectName(projectName);
        setProjectDescription(projectDescription);
        setProjectIsActive(projectIsActive);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = DataBaseUtil.trim(projectName);
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = DataBaseUtil.trim(projectDescription);
    }

    public String getProjectIsActive() {
        return projectIsActive;
    }

    public void setProjectIsActive(String projectIsActive) {
        this.projectIsActive = DataBaseUtil.trim(projectIsActive);
    }
}