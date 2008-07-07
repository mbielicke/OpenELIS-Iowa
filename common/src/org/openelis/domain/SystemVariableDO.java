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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class SystemVariableDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String value;
    
    public SystemVariableDO(){
        
    }
    
    public SystemVariableDO(Integer id, String name, String value){       
       setId(id);
       setName(name);
       setValue(value);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = DataBaseUtil.trim(value);
    }

    
     
}
