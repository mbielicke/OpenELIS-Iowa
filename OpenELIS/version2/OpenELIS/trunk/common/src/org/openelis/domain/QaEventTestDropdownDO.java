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

import org.openelis.util.DataBaseUtil;

public class QaEventTestDropdownDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String test;
    protected String method;

    public QaEventTestDropdownDO(Integer id, String test, String method){
        setId(id);
        setTest(test);
        setMethod(method);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = DataBaseUtil.trim(method);
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = DataBaseUtil.trim(test);
    }
}
