package org.openelis.domain;

import java.io.Serializable;


public class ProviderDO implements Serializable{

    /**
     * 
     */
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
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName= middleName;        
        this.npi = npi;   
        this.typeId = typeId;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }    

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
   
}
