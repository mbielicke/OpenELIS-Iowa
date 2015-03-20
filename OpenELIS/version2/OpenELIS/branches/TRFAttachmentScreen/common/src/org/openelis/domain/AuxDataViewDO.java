package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;

public class AuxDataViewDO extends AuxDataDO {

    private static final long serialVersionUID = 1L;

    protected String          dictionary, auxFieldGroupName, auxFieldGroupIsActive, analyteName,
                    analyteExternalId;
    protected Integer         auxFieldGroupId, analyteId;

    public AuxDataViewDO() {

    }

    public AuxDataViewDO(Integer id, Integer sortOrder, Integer auxFieldId, Integer referenceId,
                         Integer referenceTableId, String isReportable, Integer typeId,
                         String value, String dictionary, Integer auxFieldGroupId,
                         String auxFieldGroupName, String auxFieldGroupIsActive, Integer analyteId,
                         String analyteName, String analyteExternalId) {
        super(id, sortOrder, auxFieldId, referenceId, referenceTableId, isReportable, typeId, value);
        setDictionary(dictionary);
        setAuxFieldGroupId(auxFieldGroupId);
        setAuxFieldGroupName(auxFieldGroupName);
        setAuxFieldGroupIsActive(auxFieldGroupIsActive);
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

    public Integer getAuxFieldGroupId() {
        return auxFieldGroupId;
    }

    public void setAuxFieldGroupId(Integer groupId) {
        this.auxFieldGroupId = groupId;
    }

    public String getAuxFieldGroupName() {
        return auxFieldGroupName;
    }

    public void setAuxFieldGroupName(String auxFieldGroupName) {
        this.auxFieldGroupName = DataBaseUtil.trim(auxFieldGroupName);
    }

    public String getAuxFieldGroupIsActive() {
        return auxFieldGroupIsActive;
    }

    public void setAuxFieldGroupIsActive(String auxFieldGroupIsActive) {
        this.auxFieldGroupIsActive = DataBaseUtil.trim(auxFieldGroupIsActive);
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