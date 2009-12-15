package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class ResultViewDO extends ResultDO {

    private static final long serialVersionUID = 1L;
    
    protected String analyte;
    protected Integer rowGroup,resultGroup;
    
    public ResultViewDO(){
        
    }
    
    public ResultViewDO(Integer id, Integer analysisId, Integer testAnalyteId, Integer testResultId, 
                        String isColumn, Integer sortOrder, String isReportable, Integer analyteId,
                        Integer typeId, String value, String analyte, Integer rowGroup, Integer resultGroup){
        super(id, analysisId, testAnalyteId, testResultId, isColumn, sortOrder, isReportable, analyteId, typeId, value);
        setAnalyte(analyte);
        setRowGroup(rowGroup);
        setResultGroup(resultGroup);
    }

    public String getAnalyte() {
        return analyte;
    }

    public void setAnalyte(String analyte) {
        this.analyte = DataBaseUtil.trim(analyte);
    }

    public Integer getRowGroup() {
        return rowGroup;
    }

    public void setRowGroup(Integer rowGroup) {
        this.rowGroup = rowGroup;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }

}
