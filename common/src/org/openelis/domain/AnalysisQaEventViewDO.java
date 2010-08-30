package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AnalysisQaEventViewDO extends AnalysisQaEventDO {

    private static final long serialVersionUID = 1L;

    protected String qaEventName;
    
    public AnalysisQaEventViewDO() {
    }

    public AnalysisQaEventViewDO(Integer id, Integer analysisId, Integer qaeventId, Integer typeId, String isBillable, String qaEventName) {
        super(id, analysisId, qaeventId, typeId, isBillable);
        
        setQaEventName(qaEventName);
    }

    public String getQaEventName() {
        return qaEventName;
    }

    public void setQaEventName(String qaEventName) {
        this.qaEventName = DataBaseUtil.trim(qaEventName);
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
        this.isBillable = DataBaseUtil.trim(isBillable);
    }
}
