package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class SampleQaEventDO extends DataObject {
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sampleId;
    protected Integer qaEventId;
    protected Integer typeId;
    protected String isBillable;
    
    public SampleQaEventDO(){
        
    }
    
    public SampleQaEventDO(Integer id, Integer sampleId, Integer qaEventId,
                           Integer typeId, String isBillable){
        setId(id);
        setSampleId(sampleId);
        setQaEventId(qaEventId);
        setTypeId(typeId);
        setIsBillable(isBillable);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
        _changed = true;
    }

    public Integer getQaEventId() {
        return qaEventId;
    }

    public void setQaEventId(Integer qaEventId) {
        this.qaEventId = qaEventId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = DataBaseUtil.trim(isBillable);
        _changed = true;
    }
}
