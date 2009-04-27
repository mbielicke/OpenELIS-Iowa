package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public class QaEventsManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    Integer  sampleId;

    /**
     * Creates a new instance of this object.
     */
    public static QaEventsManager getInstance() {
        QaEventsManager qem;

        qem = new QaEventsManager();

        return qem;
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

}