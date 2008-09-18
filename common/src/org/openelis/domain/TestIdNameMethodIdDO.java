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


public class TestIdNameMethodIdDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8834418161951953239L;
    
    protected Integer id;             

    protected String name;
    
    protected Integer methodId;
    
    protected Boolean delete = false;
    
    public TestIdNameMethodIdDO(){               
        
    }
    
    public TestIdNameMethodIdDO(Integer id,String name,Integer methodId){
        this.id =  id;
        this.name = name;
        this.methodId = methodId;        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

}
