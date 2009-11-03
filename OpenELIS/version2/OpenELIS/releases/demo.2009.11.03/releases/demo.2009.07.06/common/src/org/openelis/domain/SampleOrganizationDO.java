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

import java.io.Serializable;

public class SampleOrganizationDO implements Serializable {

    private static final long serialVersionUID = 1L;
     protected Integer id;
     protected Integer sampleId;
     protected Integer organizationId;
     protected Integer typeId;
     
     protected OrganizationAddressDO orgDO = new OrganizationAddressDO();
     
     public SampleOrganizationDO(){
         
     }
     
     // sample org values
     public SampleOrganizationDO(Integer id, Integer sampleId, Integer organizationId, Integer typeId){
         setId(id);
         setSampleId(sampleId);
         setOrganizationId(organizationId);
         setTypeId(typeId);
     }

     //sample org and org values
     public SampleOrganizationDO(Integer id, Integer sampleId, Integer organizationId, Integer typeId,
                                 Integer parentOrganizationId, String name, String isActive){
         setId(id);
         setSampleId(sampleId);
         setOrganizationId(organizationId);
         setTypeId(typeId);
         
         //org params
         orgDO.setOrganizationId(organizationId);
         orgDO.setParentOrganizationId(parentOrganizationId);
         orgDO.setName(name);
         orgDO.setIsActive(isActive);
     }
    
     //sample org, org, and org address values
     public SampleOrganizationDO(Integer id, Integer sampleId, Integer organizationId, Integer typeId,
                                 Integer parentOrganizationId, String name, String isActive,
                                 Integer addressId, String multipleUnit, String streetAddress, String city, String state, String zipCode,
                                 String country){
         setId(id);
         setSampleId(sampleId);
         setOrganizationId(organizationId);
         setTypeId(typeId);
         
         //org params
         orgDO.setOrganizationId(organizationId);
         orgDO.setParentOrganizationId(parentOrganizationId);
         orgDO.setName(name);
         orgDO.setIsActive(isActive);
         
         //org address params
         orgDO.addressDO.setId(addressId);
         orgDO.addressDO.setMultipleUnit(multipleUnit);
         orgDO.addressDO.setStreetAddress(streetAddress);
         orgDO.addressDO.setCity(city);
         orgDO.addressDO.setState(state);
         orgDO.addressDO.setZipCode(zipCode);
         orgDO.addressDO.setCountry(country);
     }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public OrganizationAddressDO getOrganization() {
        return orgDO;
    }

    public void setOrganization(OrganizationAddressDO orgDO) {
        this.orgDO = orgDO;
    }
}
