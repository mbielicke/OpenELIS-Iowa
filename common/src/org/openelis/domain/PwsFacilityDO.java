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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table pws_facility.
 */

public class PwsFacilityDO extends DataObject {

    private static final long   serialVersionUID = 1L;

    protected Integer           id, tinwsysIsNumber;

    protected String            name, typeCode, stAsgnIdentCd, activityStatusCd,
                                waterTypeCode, availabilityCode, identificationCd,
                                descriptionText, sourceTypeCode;
    
    public PwsFacilityDO() {
    }

    public PwsFacilityDO(Integer id, Integer tinwsysIsNumber, String name, String typeCode, String stAsgnIdentCd, String activityStatusCd,
                         String waterTypeCode,String availabilityCode,String identificationCd,
                         String descriptionText, String sourceTypeCode) {
        setId(id);
        setTinwsysIsNumber(tinwsysIsNumber);
        setName(name); 
        setTypeCode(typeCode);
        setStAsgnIdentCd(stAsgnIdentCd);
        setActivityStatusCd(activityStatusCd);
        setWaterTypeCode(waterTypeCode);
        setAvailabilityCode(availabilityCode);
        setIdentificationCd(identificationCd);
        setDescriptionText(descriptionText);
        setSourceTypeCode(sourceTypeCode);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
        _changed = true;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = DataBaseUtil.trim(typeCode);
        _changed = true;
    }    

    public String getStAsgnIdentCd() {
        return stAsgnIdentCd;
    }

    public void setStAsgnIdentCd(String stAsgnIdentCd) {
        this.stAsgnIdentCd = DataBaseUtil.trim(stAsgnIdentCd);
        _changed = true;
    }

    public String getActivityStatusCd() {
        return activityStatusCd;
    }

    public void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = DataBaseUtil.trim(activityStatusCd);
        _changed = true;
    }

    public String getWaterTypeCode() {
        return waterTypeCode;
    }

    public void setWaterTypeCode(String waterTypeCode) {
        this.waterTypeCode = DataBaseUtil.trim(waterTypeCode);
        _changed = true;
    }

    public String getAvailabilityCode() {
        return availabilityCode;
    }

    public void setAvailabilityCode(String availabilityCode) {
        this.availabilityCode = DataBaseUtil.trim(availabilityCode);
        _changed = true;
    }

    public String getIdentificationCd() {
        return identificationCd;
    }

    public void setIdentificationCd(String identificationCd) {
        this.identificationCd = DataBaseUtil.trim(identificationCd);
        _changed = true;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = DataBaseUtil.trim(descriptionText);
        _changed = true;
    }

    public String getSourceTypeCode() {
        return sourceTypeCode;
    }

    public void setSourceTypeCode(String sourceTypeCode) {
        this.sourceTypeCode = DataBaseUtil.trim(sourceTypeCode);
        _changed = true;
    }
}
