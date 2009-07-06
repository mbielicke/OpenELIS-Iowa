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


public class TestWorksheetDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;                
    protected Integer testId;                 
    protected Integer batchCapacity;                 
    protected Integer totalCapacity;                 
    protected Integer numberFormatId;                 
    protected Integer scriptletId;  
    protected String  scriptletName;
    
    public TestWorksheetDO() {
        
    }
    
    public TestWorksheetDO(Integer id,Integer testId,Integer batchCapacity,
                           Integer totalCapacity,Integer numberFormatId,
                           Integer scriptletId,String scriptletName) {
          setId(id);
          setTestId(testId);
          setBatchCapacity(batchCapacity);
          setTotalCapacity(totalCapacity);
          setNumberFormatId(numberFormatId);
          setScriptletId(scriptletId);      
          setScriptletName(scriptletName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberFormatId() {
        return numberFormatId;
    }

    public void setNumberFormatId(Integer numberFormatId) {
        this.numberFormatId = numberFormatId;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getBatchCapacity() {
        return batchCapacity;
    }

    public void setBatchCapacity(Integer batchCapacity) {
        this.batchCapacity = batchCapacity;
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }

}
