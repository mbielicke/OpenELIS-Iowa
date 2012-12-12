package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

public class AuxDataDO extends DataObject {
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer sortOrder;
    protected Integer auxFieldId;
    protected Integer referenceId;
    protected Integer referenceTableId;
    protected String isReportable;
    protected Integer typeId;
    protected String value;
    
    public AuxDataDO(){
        
    }
    
    public AuxDataDO(Integer id, Integer sortOrder, Integer auxFieldId, Integer referenceId,
                     Integer referenceTableId, String isReportable, Integer typeId, String value){
        setId(id);
        setSortOrder(sortOrder);
        setAuxFieldId(auxFieldId);
        setReferenceId(referenceId);
        setReferenceTableId(referenceTableId);
        setIsReportable(isReportable);
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        _changed = true;
    }

    public Integer getAuxFieldId() {
        return auxFieldId;
    }

    public void setAuxFieldId(Integer auxFieldId) {
        this.auxFieldId = auxFieldId;
        _changed = true;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
        _changed = true;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
        _changed = true;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = DataBaseUtil.trim(isReportable);
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
