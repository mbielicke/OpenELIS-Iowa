package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class SamplePrivateWellViewDO extends SamplePrivateWellDO {

    private static final long serialVersionUID = 1L;
    
    protected String orgName;
    protected Integer orgAddressId;
    
    public SamplePrivateWellViewDO(){
        
    }
    
    public SamplePrivateWellViewDO(Integer id, Integer sampleId, Integer organizationId, String reportToName, Integer reportToAddressId, 
                                   String location, Integer locationAddressId, String locationMultipleUnit, String locationStreetAddress, 
                                   String locationCity, String locationState, String locationZipCode, String owner, String collector, 
                                   Integer wellNumber, String orgName, Integer orgAddressId){
        super(id, sampleId, organizationId, reportToName, reportToAddressId, location, locationAddressId, locationMultipleUnit,
              locationStreetAddress, locationCity, locationState, locationZipCode, owner, collector, wellNumber);
        setOrgName(orgName);
        setOrgAddressId(orgAddressId);
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = DataBaseUtil.trim(orgName);
    }

    public Integer getOrgAddressId() {
        return orgAddressId;
    }

    public void setOrgAddressId(Integer orgAddressId) {
        this.orgAddressId = orgAddressId;
    }
}
