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


public class QaEventDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;    
    protected String name;       
    protected String description;        
    protected Integer test;            
    protected Integer type;            
    protected String isBillable;       
    protected Integer reportingSequence;
    protected String reportingText;
    
    public QaEventDO(){
        
    }        

    public QaEventDO(Integer id, String name, String description, Integer test, Integer type, String isBillable, Integer reportingSequence, String reportingText) {
        setId(id);
        setName(name);
        setDescription(description);
        setTest(test);
        setType(type);
        setIsBillable(isBillable);
        setReportingSequence(reportingSequence);
        setReportingText(reportingText);
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

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = DataBaseUtil.trim(isBillable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        this.reportingSequence = reportingSequence;
    }

    public String getReportingText() {
        return reportingText;
    }

    public void setReportingText(String reportingText) {
        this.reportingText = DataBaseUtil.trim(reportingText);
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
