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


public class TestTypeOfSampleDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3892726107965925790L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer typeOfSampleId;             

    protected Integer unitOfMeasureId;
    
    protected Boolean delete = false;
    
    public TestTypeOfSampleDO(){
        
    }
    
    public TestTypeOfSampleDO(Integer id,Integer testId,
                              Integer typeOfSampleId, Integer unitOfMeasureId){
     this.id = id;
     this.testId = testId;
     this.typeOfSampleId = typeOfSampleId;
     this.unitOfMeasureId = unitOfMeasureId;
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

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        this.typeOfSampleId = typeOfSampleId;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }
    
    

}
