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
 * Class represents the fields in database table analysis.
 */

public class SampleItemDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sampleId, sampleItemId, itemSequence, typeOfSampleId,
                              sourceOfSampleId, containerId, unitOfMeasureId;
    protected String          sourceOther, containerReference;
    protected Double          quantity;

    public SampleItemDO() {
    }

    public SampleItemDO(Integer id, Integer sampleId, Integer sampleItemId, Integer itemSequence,
                        Integer typeOfSampleId, Integer sourceOfSampleId, String sourceOther,
                        Integer containerId, String containerReference, Double quantity,
                        Integer unitOfMeasureId) {
        setId(id);
        setSampleId(sampleId);
        setSampleItemId(sampleItemId);
        setItemSequence(itemSequence);
        setTypeOfSampleId(typeOfSampleId);
        setSourceOfSampleId(sourceOfSampleId);
        setSourceOther(sourceOther);
        setContainerId(containerId);
        setContainerReference(containerReference);
        setQuantity(quantity);
        setUnitOfMeasureId(unitOfMeasureId);
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

    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
        _changed = true;
    }

    public Integer getItemSequence() {
        return itemSequence;
    }

    public void setItemSequence(Integer itemSequence) {
        this.itemSequence = itemSequence;
        _changed = true;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        this.typeOfSampleId = typeOfSampleId;
        _changed = true;
    }

    public Integer getSourceOfSampleId() {
        return sourceOfSampleId;
    }

    public void setSourceOfSampleId(Integer sourceOfSampleId) {
        this.sourceOfSampleId = sourceOfSampleId;
        _changed = true;
    }

    public String getSourceOther() {
        return sourceOther;
    }

    public void setSourceOther(String sourceOther) {
        this.sourceOther = DataBaseUtil.trim(sourceOther);
        _changed = true;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
        _changed = true;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
        _changed = true;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
        _changed = true;
    }

    public String getContainerReference() {
        return containerReference;
    }

    public void setContainerReference(String containerReference) {
        this.containerReference = DataBaseUtil.trim(containerReference);
        _changed = true;
    }
}
