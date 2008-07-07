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

public class PreferencesDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer system_user;
    private String key;
    private String text;
    
    public PreferencesDO() {
        
    }
    
    public PreferencesDO(Integer id,
                         Integer system_user,
                         String  key,
                         String  text) {
        setId(id);
        setSystem_user(system_user);
        setKey(key);
        setText(text);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = DataBaseUtil.trim(key);
    }

    public Integer getSystem_user() {
        return system_user;
    }

    public void setSystem_user(Integer system_user) {
        this.system_user = system_user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = DataBaseUtil.trim(text);
    }
    

}
