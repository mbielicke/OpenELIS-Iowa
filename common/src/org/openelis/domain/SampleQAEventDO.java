package org.openelis.domain;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class SampleQAEventDO implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer qaEventId;
    protected String qaEventName;
    protected Integer typeId;
    protected String isBillable;
    
    public SampleQAEventDO(){
        
    }
    
    public SampleQAEventDO(Integer id, Integer sampleId, Integer qaEventId, String qaEventName,
                           Integer typeId, String isBillable){
        setId(id);
        setSampleId(sampleId);
        setQaEventId(qaEventId);
        setQaEventName(qaEventName);
        setTypeId(typeId);
        setIsBillable(isBillable);
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

    public Integer getQaEventId() {
        return qaEventId;
    }

    public void setQaEventId(Integer qaEventId) {
        this.qaEventId = qaEventId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = isBillable;
    }

    public String getQaEventName() {
        return qaEventName;
    }

    public void setQaEventName(String qaEventName) {
        this.qaEventName = DataBaseUtil.trim(qaEventName);
    }
}
