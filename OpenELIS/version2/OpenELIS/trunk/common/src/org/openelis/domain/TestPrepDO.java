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


public class TestPrepDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6296795961699696776L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer prepTestId;             

    protected String isOptional;
    
    protected Boolean delete = false;
    
    public TestPrepDO(){
        
    }
    
    public TestPrepDO(Integer id,Integer testId,
                      Integer prepTestId,String isOptional){        
        this.id = id;
        this.testId = testId;
        this.prepTestId = prepTestId;
        this.isOptional = isOptional;        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }

    public Integer getPrepTestId() {
        return prepTestId;
    }

    public void setPrepTestId(Integer prepTestId) {
        this.prepTestId = prepTestId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }         

}
