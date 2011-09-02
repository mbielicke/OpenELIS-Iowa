package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AuxDataViewDO extends AuxDataDO {

    private static final long serialVersionUID = 1L;

    protected String          dictionary, analyteExternalId;
    protected Integer         groupId;

    public AuxDataViewDO() {
    }

    public AuxDataViewDO(Integer id, Integer sortOrder, Integer auxFieldId, Integer referenceId,
                         Integer referenceTableId, String isReportable, Integer typeId,
                         String value, String dictionary, Integer groupId, String analyteExternalId) {
        super(id, sortOrder, auxFieldId, referenceId, referenceTableId, isReportable, typeId, value);
        setDictionary(dictionary);
        setGroupId(groupId);
        setAnalyteExternalId(analyteExternalId);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getAnalyteExternalId() {
        return analyteExternalId;
    }

    public void setAnalyteExternalId(String analyteExternalId) {
        this.analyteExternalId = DataBaseUtil.trim(analyteExternalId);
    }
}
