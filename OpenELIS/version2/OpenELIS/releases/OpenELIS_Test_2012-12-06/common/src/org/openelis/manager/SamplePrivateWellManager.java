package org.openelis.manager;

import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SamplePrivateWellManager implements RPC, SampleDomainInt {

    private static final long                                serialVersionUID = 1L;
    protected Integer                                        sampleId;
    protected SamplePrivateWellViewDO                        privateWell;

    protected transient static SamplePrivateWellManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SamplePrivateWellManager getInstance() {
        SamplePrivateWellManager spqm;

        spqm = new SamplePrivateWellManager();
        spqm.privateWell = new SamplePrivateWellViewDO();

        return spqm;
    }

    public static SamplePrivateWellManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    // setters/getters
    public SamplePrivateWellViewDO getPrivateWell() {
        return privateWell;
    }

    public void setPrivateWell(SamplePrivateWellViewDO pwDO) {
        privateWell = pwDO;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    // manager methods
    public SamplePrivateWellManager add() throws Exception {
        return proxy().add(this);
    }

    public SamplePrivateWellManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    private static SamplePrivateWellManagerProxy proxy() {
        if (proxy == null)
            proxy = new SamplePrivateWellManagerProxy();

        return proxy;
    }
}
