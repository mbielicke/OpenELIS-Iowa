package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public class SampleProjectsManager implements RPC {
    private static final long serialVersionUID = 1L;

    Integer  sampleId;

    /**
     * Creates a new instance of this object.
     */
    public static SampleProjectsManager getInstance() {
        SampleProjectsManager spm;

        spm = new SampleProjectsManager();

        return spm;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
}
