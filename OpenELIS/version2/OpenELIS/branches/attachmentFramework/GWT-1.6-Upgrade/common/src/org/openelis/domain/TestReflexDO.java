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


public class TestReflexDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8538938922444920188L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer testAnalyteId;             

    protected Integer testResultId;             
    
    protected Integer flagsId;             

    protected Integer addTestId;
    
    protected Boolean delete = false;    
    
    
    public TestReflexDO() {
        
    }

    public TestReflexDO(Integer id,Integer testId,Integer testAnalyteId,
                        Integer testResultId,Integer flagsId,
                        Integer addTestId,String resultValue) {
        this.id =  id;
        this.testId = testId;
        this.testAnalyteId = testAnalyteId;
        this.testResultId = testResultId;
        this.flagsId = flagsId;
        this.addTestId = addTestId;        
        
    }
                        
    public Integer getAddTestId() {
        return addTestId;
    }

    public void setAddTestId(Integer addTestId) {
        this.addTestId = addTestId;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Integer getFlagsId() {
        return flagsId;
    }

    public void setFlagsId(Integer flagsId) {
        this.flagsId = flagsId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }

    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = testAnalyteId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTestResultId() {
        return testResultId;
    }

    public void setTestResultId(Integer testResultId) {
        this.testResultId = testResultId;
    }

    

}
