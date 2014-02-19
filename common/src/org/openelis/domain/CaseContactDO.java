package org.openelis.domain;

public class CaseContactDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, source, typeId;
    protected String  sourceReference, lastName, firstName, middleName, npi;
    
    public CaseContactDO() {
        
    }
    
    public CaseContactDO(Integer id, Integer source, String sourceReference, String lastName, String firstName, String middleName, 
                         Integer typeId, String npi) {
        
        setId(id);
        setSource(source);
        setSourceReference(sourceReference);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);
        setTypeId(typeId);
        setNPI(npi);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public Integer getSource() {
        return source;
    }
    
    public void setSource(Integer source) {
        this.source = source;
        _changed = true;
    }
    
    public String getSourceReference() {
        return sourceReference;
    }
    
    public void setSourceReference(String sourceReference) {
        this.sourceReference = sourceReference;
        _changed = true;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        _changed = true;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        _changed = true;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        _changed = true;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }
    
    public String getNPI() {
        return npi;
    }
    
    public void setNPI(String npi) {
        this.npi = npi;
        _changed = true;
    }
}
