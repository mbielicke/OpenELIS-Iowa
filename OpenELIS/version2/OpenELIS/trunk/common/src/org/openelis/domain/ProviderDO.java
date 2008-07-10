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


public class ProviderDO implements Serializable{

    private static final long serialVersionUID = 3704651414103685347L;

    protected Integer id;   
    protected String lastName;       
    protected String firstName;   
    protected String middleName;  
    protected String npi;
    protected Integer typeId;

    public ProviderDO(){
        
    }
    
    public ProviderDO(Integer id,String lastName, String firstName, String middleName, Integer typeId,String npi){
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);        
        setNpi(npi);   
        setTypeId(typeId);
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = (String)DataBaseUtil.trim(firstName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = DataBaseUtil.trim(lastName);
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = DataBaseUtil.trim(middleName);
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = DataBaseUtil.trim(npi);
    }    

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
   
}
