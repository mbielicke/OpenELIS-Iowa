/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * Class represents the fields in database table pws_address.
 */
public class PWSAddressDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id,tinwsysIsNumber;

    protected String          typeCode, activeIndCd, name, addrLineOneTxt, addrLineTwoTxt,
                              addressCityName, addressStateCode, addressZipCode, stateFipsCode, phoneNumber;

    public PWSAddressDO() {
    }

    public PWSAddressDO(Integer id, Integer tinwsysIsNumber, String typeCode, String activeIndCd,
                        String name, String addrLineOneTxt, String addrLineTwoTxt,
                        String addressCityName, String addressStateCode, String addressZipCode,
                        String stateFipsCode, String phoneNumber) {
        setId(id);
        setTinwsysIsNumber(tinwsysIsNumber);
        setTypeCode(typeCode);
        setActiveIndCd(activeIndCd);
        setName(name);
        setAddrLineOneTxt(addrLineOneTxt);
        setAddrLineTwoTxt(addrLineTwoTxt);
        setAddressCityName(addressCityName);
        setAddressStateCode(addressStateCode);
        setAddressZipCode(addressZipCode);
        setStateFipsCode(stateFipsCode);
        setPhoneNumber(phoneNumber);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        this.tinwsysIsNumber = tinwsysIsNumber;
        _changed = true;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = DataBaseUtil.trim(typeCode);
        _changed = true;
    }

    public String getActiveIndCd() {
        return activeIndCd;
    }

    public void setActiveIndCd(String activeIndCd) {
        this.activeIndCd = DataBaseUtil.trim(activeIndCd);
        _changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getAddrLineOneTxt() {
        return addrLineOneTxt;
    }

    public void setAddrLineOneTxt(String addrLineOneTxt) {
        this.addrLineOneTxt = DataBaseUtil.trim(addrLineOneTxt);
        _changed = true;
    }

    public String getAddrLineTwoTxt() {
        return addrLineTwoTxt;
    }

    public void setAddrLineTwoTxt(String addrLineTwoTxt) {
        this.addrLineTwoTxt = DataBaseUtil.trim(addrLineTwoTxt);
        _changed = true;
    }

    public String getAddressCityName() {
        return addressCityName;
    }

    public void setAddressCityName(String addressCityName) {
        this.addressCityName = DataBaseUtil.trim(addressCityName);
        _changed = true;
    }

    public String getAddressStateCode() {
        return addressStateCode;
    }

    public void setAddressStateCode(String addressStateCode) {
        this.addressStateCode = DataBaseUtil.trim(addressStateCode);
        _changed = true;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = DataBaseUtil.trim(addressZipCode);
        _changed = true;
    }

    public String getStateFipsCode() {
        return stateFipsCode;
    }

    public void setStateFipsCode(String stateFipsCode) {
        this.stateFipsCode = DataBaseUtil.trim(stateFipsCode);
        _changed = true;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = DataBaseUtil.trim(phoneNumber);
        _changed = true;
    }
}
