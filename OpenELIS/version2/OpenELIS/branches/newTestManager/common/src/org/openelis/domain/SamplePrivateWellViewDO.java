package org.openelis.domain;


public class SamplePrivateWellViewDO extends SamplePrivateWellDO {

    private static final long serialVersionUID = 1L;
    
    protected OrganizationDO organization;
    
    public SamplePrivateWellViewDO(){
        
    }
    
    public SamplePrivateWellViewDO(Integer id, Integer sampleId, Integer organizationId,
                                   String reportToName, String reportToAttention,
                                   Integer reportToAddressId, String location,
                                   Integer locationAddressId, String owner, String collector,
                                   Integer wellNumber, String reportToMultipleUnit,
                                   String reportToStreetAddress, String reportToCity,
                                   String reportToState, String reportToZipCode,
                                   String reportToWorkPhone, String reportToHomePhone,
                                   String reportToCellPhone, String reportToFaxPhone,
                                   String reportToEmail, String reportToCountry,
                                   String locationMultipleUnit, String locationStreetAddress,
                                   String locationCity, String locationState,
                                   String locationZipCode, String locationWorkPhone,
                                   String locationHomePhone, String locationCellPhone,
                                   String locationFaxPhone, String locationEmail,
                                   String locationCountry) {
        super(id, sampleId, organizationId, reportToName, reportToAttention, reportToAddressId,
              location, locationAddressId, owner, collector, wellNumber, reportToMultipleUnit,
              reportToStreetAddress, reportToCity, reportToState, reportToZipCode,
              reportToWorkPhone, reportToHomePhone, reportToCellPhone, reportToFaxPhone,
              reportToEmail, reportToCountry, locationMultipleUnit, locationStreetAddress,
              locationCity, locationState, locationZipCode, locationWorkPhone, locationHomePhone,
              locationCellPhone, locationFaxPhone, locationEmail, locationCountry);
        
    }

    public OrganizationDO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDO organization) {
        this.organization = organization;
    }

}
