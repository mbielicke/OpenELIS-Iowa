package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;

public class ResultViewDO extends ResultDO {

    private static final long serialVersionUID = 1L;
    
    protected String analyte, dictionary;
    protected Integer rowGroup,testAnalyteTypeId, resultGroup;
    
    public ResultViewDO(){
        
    }
    
    public ResultViewDO(Integer id, Integer analysisId, Integer testAnalyteId, Integer testResultId, 
                        String isColumn, Integer sortOrder, String isReportable, Integer analyteId,
                        Integer typeId, String value, String analyte, Integer rowGroup, Integer testAnalyteTypeId, 
                        Integer resultGroup){
        super(id, analysisId, testAnalyteId, testResultId, isColumn, sortOrder, isReportable, analyteId, typeId, value);
        setAnalyte(analyte);
        setRowGroup(rowGroup);
        setTestAnalyteTypeId(testAnalyteTypeId);
        setResultGroup(resultGroup);
    }

    public String getAnalyte() {
        return analyte;
    }

    public void setAnalyte(String analyte) {
        this.analyte = DataBaseUtil.trim(analyte);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = DataBaseUtil.trim(dictionary);
    }

    public Integer getRowGroup() {
        return rowGroup;
    }

    public void setRowGroup(Integer rowGroup) {
        this.rowGroup = rowGroup;
    }
    
    public Integer getTestAnalyteTypeId() {
        return testAnalyteTypeId;
    }

    public void setTestAnalyteTypeId(Integer testAnalyteTypeId) {
        this.testAnalyteTypeId = testAnalyteTypeId;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }
}
