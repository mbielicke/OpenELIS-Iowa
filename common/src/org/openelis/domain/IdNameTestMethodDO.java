package org.openelis.domain;

import java.io.Serializable;

public class IdNameTestMethodDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String test;
    protected String method;
    
    public IdNameTestMethodDO(Integer id, String name, String test, String method){
        this.id = id;
        this.name = name;
        this.test = test;
        this.method = method;        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
