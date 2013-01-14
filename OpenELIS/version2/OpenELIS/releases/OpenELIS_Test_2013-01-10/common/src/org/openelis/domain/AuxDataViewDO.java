package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AuxDataViewDO extends AuxDataDO {

    private static final long serialVersionUID = 1L;
    
    protected String dictionary, analyteName, analyteExternalId;
    protected Integer analyteId, groupId;
    
    public AuxDataViewDO(){
        
    }
    
    public AuxDataViewDO(Integer id, Integer sortOrder, Integer auxFieldId, Integer referenceId,
                     Integer referenceTableId, String isReportable, Integer typeId, String value, 
                     String dictionary, Integer groupId, Integer analyteId, String analyteName, String analyteExternalId){
        super(id, sortOrder, auxFieldId, referenceId, referenceTableId, isReportable, typeId, value);
        setDictionary(dictionary);
        setGroupId(groupId);
        setAnalyteId(analyteId);
        setAnalyteName(analyteName);
        setAnalyteExternalId(analyteExternalId);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = DataBaseUtil.trim(dictionary);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public String getAnalyteExternalId() {
        return analyteExternalId;
    }

    public void setAnalyteExternalId(String analyteExternalId) {
        this.analyteExternalId = DataBaseUtil.trim(analyteExternalId);
    }
}