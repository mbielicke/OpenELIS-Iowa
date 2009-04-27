package org.openelis.domain;

import java.io.Serializable;

import org.openelis.gwt.common.RPC;
import org.openelis.util.DataBaseUtil;

public class SampleEnvironmentalDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected String isHazardous;
    protected String description;
    protected String collector;
    protected String collectorPhone;
    protected String samplingLocation;
    protected Integer addressId;
    
    public SampleEnvironmentalDO(){
        
    }
    
    public SampleEnvironmentalDO(Integer id, Integer sampleId, String isHazardous, String description,
                                 String collector, String collectorPhone, String samplingLocation,
                                 Integer addressId){
        setId(id);
        setSampleId(sampleId);
        setIsHazardous(isHazardous);
        setDescription(description);
        setCollector(collectorPhone);
        setCollectorPhone(collectorPhone);
        setSamplingLocation(samplingLocation);
        setAddressId(addressId);
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
    public String getIsHazardous() {
        return isHazardous;
    }
    public void setIsHazardous(String isHazardous) {
        this.isHazardous = DataBaseUtil.trim(isHazardous);
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }
    public String getCollector() {
        return collector;
    }
    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
    }
    public String getCollectorPhone() {
        return collectorPhone;
    }
    public void setCollectorPhone(String collectorPhone) {
        this.collectorPhone = DataBaseUtil.trim(collectorPhone);
    }
    public String getSamplingLocation() {
        return samplingLocation;
    }
    public void setSamplingLocation(String samplingLocation) {
        this.samplingLocation = DataBaseUtil.trim(samplingLocation);
    }
    public Integer getAddressId() {
        return addressId;
    }
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
}
