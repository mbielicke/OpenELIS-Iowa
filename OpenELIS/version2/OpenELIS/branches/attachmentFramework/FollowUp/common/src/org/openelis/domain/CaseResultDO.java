package org.openelis.domain;

public class CaseResultDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, caseAnalysisId, testAnalyteId, testResultId, row, col, analyteId, typeId;
    protected String isReportable, value;
    
    public CaseResultDO() {
        
    }
    
    public CaseResultDO(Integer id, Integer caseAnalysisId, Integer testAnalyteId, Integer testResultId,
                        Integer row, Integer col, String isReportable, Integer analyteId, Integer typeId, String value) {
        
        setId(id);
        setCaseAnalysisId(caseAnalysisId);
        setTestAnalyteId(testAnalyteId);
        setTestResultId(testResultId);
        setRow(row);
        setCol(col);
        setIsReportable(isReportable);
        setAnalyteId(analyteId);
        setTypeId(typeId);
        setValue(value);
        _changed = false;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public Integer getCaseAnalysisId() {
        return caseAnalysisId;
    }
    
    public void setCaseAnalysisId(Integer caseAnalysisId) {
        this.caseAnalysisId = caseAnalysisId;
        _changed = true;
    }
    
    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }
    
    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = analyteId;
        _changed = true;
    }
    
    public Integer getTestResultId() {
        return testResultId;
    }
    
    public void setTestResultId(Integer testResultId) {
        this.testResultId = testResultId;
        _changed = true;
    }
    
    public Integer getRow() {
        return row;
    }
    
    public void setRow(Integer row) {
        this.row = row;
        _changed = true;
    }
    
    public Integer getCol() {
        return col;
    }
    
    public void setCol(Integer col) {
        this.col = col;
        _changed = true;
    }
    
    public String getIsReportable() {
        return isReportable;
    }
    
    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
        _changed = true;
    }
    
    public Integer getAnalyteId() {
        return analyteId;
    }
    
    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
        _changed = true;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
        _changed = true;
    }

}
