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

public class TestWorksheetAnalyteDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer analyteId;         
    
    protected String analyteName;

    protected Integer repeat;             

    protected Integer flagId;
    
    protected Boolean delete = false;
    
    public TestWorksheetAnalyteDO() {
        
    }
    
    public TestWorksheetAnalyteDO(Integer id,Integer testId,Integer analyteId,
                                  String analyteName,Integer repeat,Integer flagId) {        
        this.id = id;
        this.testId = testId;
        this.analyteId = analyteId;
        this.analyteName = analyteName;
        this.repeat = repeat;
        this.flagId = flagId;                
    }
    
    public TestWorksheetAnalyteDO(Integer testId,Integer analyteId,String analyteName) {        
        this.testId = testId;
        this.analyteId = analyteId;
        this.analyteName = analyteName;                       
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public Integer getFlagId() {
        return flagId;
    }

    public void setFlagId(Integer flagId) {
        this.flagId = flagId;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = analyteName;
    }

}
