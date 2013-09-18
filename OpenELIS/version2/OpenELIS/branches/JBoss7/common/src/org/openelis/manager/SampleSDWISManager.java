package org.openelis.manager;

import java.io.Serializable;

import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleSDWISManager implements Serializable, SampleDomainInt {
    private static final long                          serialVersionUID = 1L;
    protected Integer                                  sampleId;
    protected SampleSDWISViewDO                        sdwis;

    private transient static SampleSDWISManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleSDWISManager getInstance() {
        SampleSDWISManager ssm;

        ssm = new SampleSDWISManager();
        ssm.sdwis = new SampleSDWISViewDO();

        return ssm;
    }

    public static SampleSDWISManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    // setters/getters
    public SampleSDWISViewDO getSDWIS() {
        return sdwis;
    }

    public void setSDWIS(SampleSDWISViewDO sdwisDO) {
        sdwis = sdwisDO;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    // manager methods
    public SampleSDWISManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleSDWISManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorsList;

        errorsList = new ValidationErrorsList();
        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }
    
    private static SampleSDWISManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleSDWISManagerProxy();

        return proxy;
    }

    public void delete() throws Exception {
        proxy().delete(this);
    }
}