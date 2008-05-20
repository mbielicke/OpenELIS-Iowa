package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;


public class ProviderDO implements Serializable{

    private static final long serialVersionUID = 3704651414103685347L;

    protected Integer id;   
    protected String lastName;       
    protected String firstName;   
    protected String middleName;  
    protected String npi;
    protected Integer typeId;

    public ProviderDO(){
        
    }
    
    public ProviderDO(Integer id,String lastName, String firstName, String middleName, Integer typeId,String npi){
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);        
        setNpi(npi);   
        setTypeId(typeId);
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = (String)DataBaseUtil.trim(firstName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = DataBaseUtil.trim(lastName);
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = DataBaseUtil.trim(middleName);
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = DataBaseUtil.trim(npi);
    }    

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
   
}
