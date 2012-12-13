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
 * SampleItem Entity POJO for database
 */

import java.util.Collection;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "SampleItem.FetchById",
                query = "select new org.openelis.domain.SampleItemViewDO(item.id, item.sampleId, item.sampleItemId, item.itemSequence, item.typeOfSampleId, item.sourceOfSampleId, item.sourceOther, item.containerId," +
                        "item.containerReference, item.quantity, item.unitOfMeasureId, typeDict.entry, sourceDict.entry, contDict.entry)"
                      + " from SampleItem item LEFT JOIN item.sourceDict sourceDict LEFT JOIN item.typeDict typeDict LEFT JOIN"
                      + " item.containerDict contDict where item.id = :id"),
    @NamedQuery( name = "SampleItem.FetchBySampleId",
                query = "select new org.openelis.domain.SampleItemViewDO(item.id, item.sampleId, item.sampleItemId, item.itemSequence, item.typeOfSampleId, item.sourceOfSampleId, item.sourceOther, item.containerId," +
                        "item.containerReference, item.quantity, item.unitOfMeasureId, typeDict.entry, sourceDict.entry, contDict.entry)"
                      + " from SampleItem item LEFT JOIN item.sourceDict sourceDict LEFT JOIN item.typeDict typeDict LEFT JOIN "
                      + " item.containerDict contDict where item.sampleId = :id order by item.itemSequence ")})
@Entity
@Table(name = "sample_item")
@EntityListeners({AuditUtil.class})
public class SampleItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer              id;

    @Column(name = "sample_id")
    private Integer              sampleId;

    @Column(name = "sample_item_id")
    private Integer              sampleItemId;

    @Column(name = "item_sequence")
    private Integer              itemSequence;

    @Column(name = "type_of_sample_id")
    private Integer              typeOfSampleId;

    @Column(name = "source_of_sample_id")
    private Integer              sourceOfSampleId;

    @Column(name = "source_other")
    private String               sourceOther;

    @Column(name = "container_id")
    private Integer              containerId;

    @Column(name = "container_reference")
    private String               containerReference;

    @Column(name = "quantity")
    private Double               quantity;

    @Column(name = "unit_of_measure_id")
    private Integer              unitOfMeasureId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample               sample;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_item_id", insertable = false, updatable = false)
    private Sample               parentSampleItem;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_item_id", insertable = false, updatable = false)
    private Collection<Analysis> analysis;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_of_sample_id", insertable = false, updatable = false)
    private Dictionary           sourceDict;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_sample_id", insertable = false, updatable = false)
    private Dictionary           typeDict;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", insertable = false, updatable = false)
    private Dictionary           containerDict;

    @Transient
    private SampleItem           original;

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

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        if (DataBaseUtil.isDifferent(sampleItemId, this.sampleItemId))
            this.sampleItemId = sampleItemId;
    }

    public Integer getItemSequence() {
        return itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        if (DataBaseUtil.isDifferent(itemSequence, this.itemSequence))
            this.itemSequence = itemSequence;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        if (DataBaseUtil.isDifferent(typeOfSampleId, this.typeOfSampleId))
            this.typeOfSampleId = typeOfSampleId;
    }

    public Integer getSourceOfSampleId() {
        return sourceOfSampleId;
    }

    public void setSourceOfSampleId(Integer sourceOfSampleId) {
        if (DataBaseUtil.isDifferent(sourceOfSampleId, this.sourceOfSampleId))
            this.sourceOfSampleId = sourceOfSampleId;
    }

    public String getSourceOther() {
        return sourceOther;
    }

    public void setSourceOther(String sourceOther) {
        if (DataBaseUtil.isDifferent(sourceOther, this.sourceOther))
            this.sourceOther = sourceOther;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        if (DataBaseUtil.isDifferent(containerId, this.containerId))
            this.containerId = containerId;
    }

    public String getContainerReference() {
        return containerReference;
    }

    public void setContainerReference(String containerReference) {
        if (DataBaseUtil.isDifferent(containerReference, this.containerReference))
            this.containerReference = containerReference;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        if (DataBaseUtil.isDifferent(unitOfMeasureId, this.unitOfMeasureId))
            this.unitOfMeasureId = unitOfMeasureId;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Sample getSampleItem() {
        return parentSampleItem;
    }

    public void setSampleItem(Sample sampleItem) {
        this.parentSampleItem = sampleItem;
    }

    public Collection<Analysis> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Collection<Analysis> analysis) {
        this.analysis = analysis;
    }

    public Sample getParentSampleItem() {
        return parentSampleItem;
    }

    public void setParentSampleItem(Sample parentSampleItem) {
        this.parentSampleItem = parentSampleItem;
    }

    public Dictionary getSourceDict() {
        return sourceDict;
    }

    public void setSourceDict(Dictionary sourceDict) {
        this.sourceDict = sourceDict;
    }

    public Dictionary getTypeDict() {
        return typeDict;
    }

    public void setTypeDict(Dictionary typeDict) {
        this.typeDict = typeDict;
    }

    public Dictionary getContainerDict() {
        return containerDict;
    }

    public void setContainerDict(Dictionary containerDict) {
        this.containerDict = containerDict;
    }

    public void setClone() {
        try {
            original = (SampleItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SAMPLE_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("sample_item_id", sampleItemId, original.sampleItemId)
                 .setField("item_sequence", itemSequence, original.itemSequence)
                 .setField("type_of_sample_id", typeOfSampleId, original.typeOfSampleId, ReferenceTable.DICTIONARY)
                 .setField("source_of_sample_id", sourceOfSampleId, original.sourceOfSampleId, ReferenceTable.DICTIONARY)
                 .setField("source_other", sourceOther, original.sourceOther)
                 .setField("container_id", containerId, original.containerId, ReferenceTable.DICTIONARY)
                 .setField("container_reference", containerReference, original.containerReference)
                 .setField("quantity", quantity, original.quantity)
                 .setField("unit_of_measure_id", unitOfMeasureId, original.unitOfMeasureId, ReferenceTable.DICTIONARY);

        return audit;
    }
}
