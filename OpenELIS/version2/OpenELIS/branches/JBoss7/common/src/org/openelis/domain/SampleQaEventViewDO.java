package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class SampleQaEventViewDO extends SampleQaEventDO {
    private static final long serialVersionUID = 1L;

    protected String          qaEventName, qaEventReportingText;

    public SampleQaEventViewDO() {
    }

    public SampleQaEventViewDO(Integer id, Integer sampleId, Integer qaEventId, Integer typeId,
                               String isBillable, String qaEventName, String qaEventReportingText) {
        super(id, sampleId, qaEventId, typeId, isBillable);

        setQaEventName(qaEventName);
        setQaEventReportingText(qaEventReportingText);
     }

    public String getQaEventName() {
        return qaEventName;
    }

    public void setQaEventName(String qaEventName) {
        this.qaEventName = DataBaseUtil.trim(qaEventName);
    }

    public String getQaEventReportingText() {
        return qaEventReportingText;
    }

    public void setQaEventReportingText(String qaEventReportingText) {
        this.qaEventReportingText = DataBaseUtil.trim(qaEventReportingText);
    }
}
