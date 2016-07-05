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
package org.openelis.entity;

/**
 * SampleAnimal Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SampleAnimal.FetchBySampleIds",
                query = "select distinct new org.openelis.domain.SampleAnimalDO(s.id, s.sampleId, s.animalCommonNameId, s.animalScientificNameId,"
                      + "s.location, s.locationAddressId, s.providerId, s.providerPhone, a.multipleUnit, a.streetAddress, a.city, a.state,"
                      + "a.zipCode, a.workPhone, a.homePhone, a.cellPhone, a.faxPhone, a.email, a.country)"
                      + " from SampleAnimal s LEFT JOIN s.locationAddress a where s.sampleId in (:ids)")})

@Entity
@Table(name = "sample_animal")
@EntityListeners({AuditUtil.class})
public class SampleAnimal implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer      id;

    @Column(name = "sample_id")
    private Integer      sampleId;

    @Column(name = "animal_common_name_id")
    private Integer      animalCommonNameId;

    @Column(name = "animal_scientific_name_id")
    private Integer      animalScientificNameId;
    
    @Column(name = "location")
    private String              location;

    @Column(name = "location_address_id")
    private Integer             locationAddressId;
    
    @Column(name = "provider_id")
    private Integer        providerId;

    @Column(name = "provider_phone")
    private String         providerPhone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_address_id", insertable = false, updatable = false)
    private Address             locationAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider       provider;

    @Transient
    private SampleAnimal original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }

    public Integer getAnimalCommonNameId() {
        return animalCommonNameId;
    }

    public void setAnimalCommonNameId(Integer animalCommonNameId) {
        if (DataBaseUtil.isDifferent(animalCommonNameId, this.animalCommonNameId))
            this.animalCommonNameId = animalCommonNameId;
    }

    public Integer getAnimalScientificNameId() {
        return animalScientificNameId;
    }

    public void setAnimalScientificNameId(Integer animalScientificNameId) {
        if (DataBaseUtil.isDifferent(animalScientificNameId, this.animalScientificNameId))
            this.animalScientificNameId = animalScientificNameId;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location, this.location))
            this.location = location;
    }

    public Integer getLocationAddressId() {
        return locationAddressId;
    }

    public void setLocationAddressId(Integer locationAddressId) {
        if (DataBaseUtil.isDifferent(locationAddressId, this.locationAddressId))
            this.locationAddressId = locationAddressId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        if (DataBaseUtil.isDifferent(providerId, this.providerId))
            this.providerId = providerId;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        if (DataBaseUtil.isDifferent(providerPhone, this.providerPhone))
            this.providerPhone = providerPhone;
    }
    
    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Address getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(Address locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setClone() {
        try {
            original = (SampleAnimal)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SAMPLE_ANIMAL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId, Constants.table().SAMPLE)
                 .setField("animal_common_name_id", animalCommonNameId, original.animalCommonNameId, Constants.table().DICTIONARY)
                 .setField("animal_scientific_name_id", animalScientificNameId, original.animalScientificNameId, Constants.table().DICTIONARY)
                 .setField("location", location, original.location)
                 .setField("location_address_id", locationAddressId, original.locationAddressId, Constants.table().ADDRESS)
                 .setField("provider_id", providerId, original.providerId, Constants.table().PROVIDER)
                 .setField("provider_phone", providerPhone, original.providerPhone);

        return audit;
    }
}