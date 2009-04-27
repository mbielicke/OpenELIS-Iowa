package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public class SampleOrganizationsManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    Integer  sampleId;

    /**
     * Creates a new instance of this object.
     */
    public static SampleOrganizationsManager getInstance() {
        SampleOrganizationsManager som;

        som = new SampleOrganizationsManager();

        return som;
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }


}
