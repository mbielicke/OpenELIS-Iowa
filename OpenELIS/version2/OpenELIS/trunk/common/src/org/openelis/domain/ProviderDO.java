package org.openelis.domain;

import java.io.Serializable;


public class ProviderDO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3704651414103685347L;

    private Integer id;             

    private String lastName;             

    private String firstName;             

    private String middleName;             

    private Integer type;             

    private String npi;

    public ProviderDO(){
        
    }
    
    public ProviderDO(Integer id,String lastName, String firstName, String middleName, Integer type,String npi){
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName= middleName;
        this.type = type;
        this.npi = npi;        
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }      
}
