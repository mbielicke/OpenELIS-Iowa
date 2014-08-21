package org.openelis.domain;

public class CaseProviderDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, caseId, caseContactId, typeId;
    
    public CaseProviderDO() {
        
    }
    
    public CaseProviderDO(Integer id, Integer caseId, Integer caseContactId, Integer typeId) {
        
        setId(id);
        setCaseId(caseId);
        setCaseContactId(caseContactId);
        setTypeId(typeId);
        
        _changed = false;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = false;
    }
    
    public Integer getCaseId() {
        return caseId;
    }
    
    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
        _changed = true;
    }
    
    public Integer getCaseContactId() {
        return caseContactId;
    }
    
    public void setCaseContactId(Integer caseContactId) {
        this.caseContactId = caseContactId;
        _changed = true;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }
 
}
