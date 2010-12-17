package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class SampleQaEventViewDO extends SampleQaEventDO {
    private static final long serialVersionUID = 1L;
    
    protected String qaEventName;
    
    public SampleQaEventViewDO() {
    }

    public SampleQaEventViewDO(Integer id, Integer sampleId, Integer qaEventId,
                                    Integer typeId, String isBillable, String qaEventName) {
        super(id, sampleId, qaEventId, typeId, isBillable);
        
        setQaEventName(qaEventName);
    }

    public String getQaEventName() {
        return qaEventName;
    }

    public void setQaEventName(String qaEventName) {
        this.qaEventName = DataBaseUtil.trim(qaEventName);
    }
}
