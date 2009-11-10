package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class ResultDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer analysisId;
    protected Integer testAnalyteId;
    protected Integer testResultId;
    protected String isColumn;
    protected Integer sortOrder;
    protected String isReportable;
    protected Integer analyteId;
    protected Integer typeId;
    protected String value;
    
    public ResultDO(){
        
    }
    
    public ResultDO(Integer id, Integer analysisId, Integer testAnalyteId, Integer testResultId, 
                    String isColumn, Integer sortOrder, String isReportable, Integer analyteId,
                    Integer typeId, String value){
        setId(id);
        setAnalysisId(analysisId);
        setTestAnalyteId(testAnalyteId);
        setTestResultId(testResultId);
        setIsColumn(isColumn);
        setSortOrder(sortOrder);
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
    public Integer getAnalysisId() {
        return analysisId;
    }
    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
        _changed = true;
    }
    public Integer getTestAnalyteId() {
        return testAnalyteId;
    }
    public void setTestAnalyteId(Integer testAnalyteId) {
        this.testAnalyteId = testAnalyteId;
        _changed = true;
    }
    public Integer getTestResultId() {
        return testResultId;
    }
    public void setTestResultId(Integer testResultId) {
        this.testResultId = testResultId;
        _changed = true;
    }
    public String getIsColumn() {
        return isColumn;
    }
    public void setIsColumn(String isColumn) {
        this.isColumn = DataBaseUtil.trim(isColumn);
        _changed = true;
    }
    public Integer getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        _changed = true;
    }
    public String getIsReportable() {
        return isReportable;
    }
    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
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
        this.value = DataBaseUtil.trim(value);
        _changed = true;
    }

}
