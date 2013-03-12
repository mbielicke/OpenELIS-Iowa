package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AnalysisQaEventViewDO extends AnalysisQaEventDO {

    private static final long serialVersionUID = 1L;

    protected String          qaEventName, qaEventReportingText;

    public AnalysisQaEventViewDO() {
    }

    public AnalysisQaEventViewDO(Integer id, Integer analysisId, Integer qaeventId, Integer typeId,
                                 String isBillable, String qaEventName, String qaEventReportingText) {
        super(id, analysisId, qaeventId, typeId, isBillable);

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
