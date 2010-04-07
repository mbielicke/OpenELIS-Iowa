/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class SampleSDWISDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, sampleId, stateLabId, sampleTypeId, sampleCategoryId;
    protected String pwsId, facilityId, samplePointId, location, collector;
    
    public SampleSDWISDO() {
        
    }
        
    public SampleSDWISDO(Integer id, Integer sampleId, String pwsId,
                         Integer stateLabId, String facilityId, Integer sampleTypeId,
                         Integer sampleCategoryId,  
                         String samplePointId, String location, String collector){
        setId(id);
        setSampleId(sampleId);
        setPwsId(pwsId);
        setStateLabId(stateLabId);
        setFacilityId(facilityId);
        setSampleTypeId(sampleTypeId);
        setSampleCategoryId(sampleCategoryId);
        setSamplePointId(samplePointId);
        setLocation(location);
        setCollector(collector);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
        _changed = true;
    }

    public String getPwsId() {
        return pwsId;
    }

    public void setPwsId(String pwsId) {
        this.pwsId = DataBaseUtil.trim(pwsId);
        _changed = true;
    }

    public Integer getStateLabId() {
        return stateLabId;
    }

    public void setStateLabId(Integer stateLabId) {
        this.stateLabId = stateLabId;
        _changed = true;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = DataBaseUtil.trim(facilityId);
        _changed = true;
    }

    public Integer getSampleTypeId() {
        return sampleTypeId;
    }

    public void setSampleTypeId(Integer sampleTypeId) {
        this.sampleTypeId = sampleTypeId;
        _changed = true;
    }

    public Integer getSampleCategoryId() {
        return sampleCategoryId;
    }

    public void setSampleCategoryId(Integer sampleCategoryId) {
        this.sampleCategoryId = sampleCategoryId;
        _changed = true;
    }

    public String getSamplePointId() {
        return samplePointId;
    }

    public void setSamplePointId(String samplePointId) {
        this.samplePointId = DataBaseUtil.trim(samplePointId);
        _changed = true;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
        _changed = true;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
        _changed = true;
    }
}