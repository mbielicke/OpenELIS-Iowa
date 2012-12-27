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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/** 
 * This class's objects store the data for the individual records that populate
 * the todo lists for worksheets   
 */
public class ToDoWorksheetVO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer         id, systemUserId, statusId;
    protected Datetime        createdDate;
    protected String          systemUserName, testName, testMethodName, sectionName;
    
    public ToDoWorksheetVO() {
    }
    
    public ToDoWorksheetVO(Integer id, Date createdDate, Integer systemUserId,
                            Integer statusId, String testName, String testMethodName,
                            String sectionName) {
        setId(id);
        setCreatedDate(DataBaseUtil.toYM(createdDate));
        setSystemUserId(systemUserId);
        setStatusId(statusId);
        setTestName(testName);
        setTestMethodName(testMethodName);
        setSectionName(sectionName);
    }
   
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Datetime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Datetime createdDate) {
        this.createdDate = DataBaseUtil.toYM(createdDate);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    public String getSystemUserName() {
        return systemUserName;
    }

    public void setSystemUserName(String systemUserName) {
        this.systemUserName = DataBaseUtil.trim(systemUserName);
    }
    
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = DataBaseUtil.trim(testName);
    }   
    
    public String getTestMethodName() {
        return testMethodName;
    }
    
    public void setTestMethodName(String testMethodName) {
        this.testMethodName = DataBaseUtil.trim(testMethodName);
    }
    
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = DataBaseUtil.trim(sectionName);
    }

}
