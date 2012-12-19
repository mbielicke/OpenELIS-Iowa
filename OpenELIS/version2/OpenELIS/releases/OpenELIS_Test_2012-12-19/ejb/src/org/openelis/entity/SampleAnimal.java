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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

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

    @Column(name = "collector")
    private String       collector;

    @Column(name = "collector_phone")
    private String       collectorPhone;

    @Column(name = "sampling_location")
    private String       samplingLocation;

    @Column(name = "address_id")
    private Integer      addressId;

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

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        if (DataBaseUtil.isDifferent(collector, this.collector))
            this.collector = collector;
    }

    public String getCollectorPhone() {
        return collectorPhone;
    }

    public void setCollectorPhone(String collectorPhone) {
        if (DataBaseUtil.isDifferent(collectorPhone, this.collectorPhone))
            this.collectorPhone = collectorPhone;
    }

    public String getSamplingLocation() {
        return samplingLocation;
    }

    public void setSamplingLocation(String samplingLocation) {
        if (DataBaseUtil.isDifferent(samplingLocation, this.samplingLocation))
            this.samplingLocation = samplingLocation;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }
    public void setClone() {
        try {
            original = (SampleAnimal)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SAMPLE_ANIMAL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId, ReferenceTable.SAMPLE)
                 .setField("animal_common_name_id", animalCommonNameId, original.animalCommonNameId, ReferenceTable.DICTIONARY)
                 .setField("animal_scientific_name_id", animalScientificNameId, original.animalScientificNameId, ReferenceTable.DICTIONARY)
                 .setField("collector", collector, original.collector)
                 .setField("collector_phone", collectorPhone, original.collectorPhone)
                 .setField("sampling_location", samplingLocation, original.samplingLocation)
                 .setField("address_id", addressId, original.addressId, ReferenceTable.ADDRESS);

        return audit;
    }
}
