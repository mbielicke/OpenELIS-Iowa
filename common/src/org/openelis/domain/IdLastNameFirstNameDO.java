package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class IdLastNameFirstNameDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String lastName;
    protected String firstName;
    
    public IdLastNameFirstNameDO(Integer id, String lastName, String firstName){
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = DataBaseUtil.trim(firstName);
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

}
