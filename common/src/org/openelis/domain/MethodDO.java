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

package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.Datetime;

public class MethodDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5267208279869865540L;
    
    protected Integer id;             

    protected String name;
    
    protected String description;             

    protected String reportingDescription;   
    
    protected String isActive;             

    protected Datetime activeBegin;             

    protected Datetime activeEnd;
    
    public MethodDO() {
        
    }
    
    public MethodDO(Integer id,String name,String description,
                    String reportingDescription,String isActive,
                    Date activeBegin, Date activeEnd){
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.reportingDescription = reportingDescription;
        this.isActive = isActive;
        setActiveBegin(activeBegin);
        setActiveEnd(activeEnd);
    }

    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Date activeBegin) {
        this.activeBegin = new Datetime(Datetime.YEAR,Datetime.DAY,activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = new Datetime(Datetime.YEAR,Datetime.DAY,activeEnd);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportingDescription() {
        return reportingDescription;
    }

    public void setReportingDescription(String reportingDescription) {
        this.reportingDescription = reportingDescription;
    }   
    

}
