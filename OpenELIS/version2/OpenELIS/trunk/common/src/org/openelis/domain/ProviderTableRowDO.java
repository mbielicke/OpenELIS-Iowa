package org.openelis.domain;

import java.io.Serializable;

public class ProviderTableRowDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8893266886890030344L;
    private Integer id;
    private String lastName;
    private String firstName;
    
    public ProviderTableRowDO(){
        
    }
    
    public ProviderTableRowDO(Integer id, String lastName, String firstName){
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
   
}
