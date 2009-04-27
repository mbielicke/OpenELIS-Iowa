package org.openelis.domain;

import java.io.Serializable;

import org.openelis.gwt.common.RPC;
import org.openelis.util.DataBaseUtil;

public class SampleItemDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer sampleItemId;
    protected Integer itemSequence;
    protected Integer typeOfSampleId;
    protected Integer sourceOfSampleId;
    protected String sourceOther;
    protected Integer containerId;
    protected String containerReference;
    protected Double quantity;
    protected Integer unitOfMeasureId;
    
    public SampleItemDO(){
        
    }
    
    public SampleItemDO(Integer id, Integer sampleId, Integer sampleItemId, Integer itemSequence,
                        Integer typeOfSampleId, Integer sourceOfSampleId, String sourceOther,
                        Integer containerId, String containerReference, Double quantity,
                        Integer unitOfMeasureId){
        setId(id);
        setSampleId(sourceOfSampleId);
        setSampleItemId(sampleItemId);
        setItemSequence(itemSequence);
        setTypeOfSampleId(typeOfSampleId);
        setSourceOfSampleId(sourceOfSampleId);
        setSourceOther(sourceOther);
        setContainerId(containerId);
        setContainerReferenceId(containerReference);
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
    public String getContainerReferenceId() {
        return containerReference;
    }
    public void setContainerReferenceId(String containerReference) {
        this.containerReference = containerReference;
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
}
