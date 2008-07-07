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

public class OrganizationAutoDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String aptSuite;
    protected String address;
    protected String city;
    protected String state;
    protected String zipCode;
    
    public OrganizationAutoDO(Integer id, String name, String aptSuite, String address, String city, String state, String zipCode){
        setId(id);
        setName(name);
        setAptSuite(aptSuite);
        setAddress(address);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = DataBaseUtil.trim(address);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = DataBaseUtil.trim(city);
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = DataBaseUtil.trim(state);
    }

    public String getAptSuite() {
        return aptSuite;
    }

    public void setAptSuite(String aptSuite) {
        this.aptSuite = DataBaseUtil.trim(aptSuite);
    }
    
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = DataBaseUtil.trim(zipCode);
    }

}
