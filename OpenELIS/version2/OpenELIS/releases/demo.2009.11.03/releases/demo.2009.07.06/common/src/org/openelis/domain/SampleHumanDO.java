package org.openelis.domain;

import java.io.Serializable;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class SampleHumanDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer patientId;
    protected Integer providerId;
    protected String providerPhone;
    
    public SampleHumanDO(){
        
    }
    
    public SampleHumanDO(Integer id, Integer sampleId, Integer patientId, 
                         Integer providerId, String providerPhone){
        setId(id);
        setSampleId(sampleId);
        setPatientId(patientId);
        setProviderId(providerId);
        setProviderPhone(providerPhone);
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
    public Integer getPatientId() {
        return patientId;
    }
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
    public Integer getProviderId() {
        return providerId;
    }
    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = DataBaseUtil.trim(providerPhone);
    }
}
