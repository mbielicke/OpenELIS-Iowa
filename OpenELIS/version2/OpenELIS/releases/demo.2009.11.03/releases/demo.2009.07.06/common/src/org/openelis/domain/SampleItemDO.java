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

import org.openelis.utilcommon.DataBaseUtil;

public class SampleItemDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer sampleItemId;
    protected Integer itemSequence;
    protected Integer typeOfSampleId;
    protected String typeOfSample;
    protected Integer sourceOfSampleId;
    protected String sourceOfSample;
    protected String sourceOther;
    protected Integer containerId;
    protected String container;
    protected String containerReference;
    protected Double quantity;
    protected Integer unitOfMeasureId;
    
    public SampleItemDO(){
        
    }
    
    public SampleItemDO(Integer id, Integer sampleId, Integer sampleItemId, Integer itemSequence,
                        Integer typeOfSampleId, String typeOfSample, Integer sourceOfSampleId, String sourceOfSample, 
                        String sourceOther, Integer containerId, String container, String containerReference, Double quantity,
                        Integer unitOfMeasureId){
        setId(id);
        setSampleId(sourceOfSampleId);
        setSampleItemId(sampleItemId);
        setItemSequence(itemSequence);
        setTypeOfSampleId(typeOfSampleId);
        setTypeOfSample(typeOfSample);
        setSourceOfSampleId(sourceOfSampleId);
        setSourceOfSample(sourceOfSample);
        setSourceOther(sourceOther);
        setContainerId(containerId);
        setContainer(container);
        setContainerReference(containerReference);
        setQuantity(quantity);
        setUnitOfMeasureId(unitOfMeasureId);
        
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
    public Integer getSampleItemId() {
        return sampleItemId;
    }
    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }
    public Integer getItemSequence() {
        return itemSequence;
    }
    public void setItemSequence(Integer itemSequence) {
        this.itemSequence = itemSequence;
    }
    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }
    public void setTypeOfSampleId(Integer typeOfSampleId) {
        this.typeOfSampleId = typeOfSampleId;
    }
    public Integer getSourceOfSampleId() {
        return sourceOfSampleId;
    }
    public void setSourceOfSampleId(Integer sourceOfSampleId) {
        this.sourceOfSampleId = sourceOfSampleId;
    }
    public String getSourceOther() {
        return sourceOther;
    }
    public void setSourceOther(String sourceOther) {
        this.sourceOther = DataBaseUtil.trim(sourceOther);
    }
    public Integer getContainerId() {
        return containerId;
    }
    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }
    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }

    public String getTypeOfSample() {
        return typeOfSample;
    }

    public void setTypeOfSample(String typeOfSample) {
        this.typeOfSample = DataBaseUtil.trim(typeOfSample);
    }

    public String getSourceOfSample() {
        return sourceOfSample;
    }

    public void setSourceOfSample(String sourceOfSample) {
        this.sourceOfSample = DataBaseUtil.trim(sourceOfSample);
    }

    public String getContainerReference() {
        return containerReference;
    }

    public void setContainerReference(String containerReference) {
        this.containerReference = DataBaseUtil.trim(containerReference);
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = DataBaseUtil.trim(container);
    }
}
