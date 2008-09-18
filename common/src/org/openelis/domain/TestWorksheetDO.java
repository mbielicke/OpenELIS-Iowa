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


public class TestWorksheetDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1157892814197581707L;
    
    protected Integer id;             
   
    protected Integer testId;             
    
    protected Integer batchCapacity;             
    
    protected Integer totalCapacity;             
    
    protected Integer numberFormatId;             
    
    protected Integer scriptletId;  
    
    public TestWorksheetDO() {
        
    }
    
    public TestWorksheetDO(Integer id,Integer testId,Integer batchCapacity,
                           Integer totalCapacity,Integer numberFormatId,
                           Integer scriptletId) {
      this.id = id;
      this.testId = testId;
      this.batchCapacity = batchCapacity;
      this.totalCapacity = totalCapacity;
      this.numberFormatId = numberFormatId;
      this.scriptletId = scriptletId;      
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

}
