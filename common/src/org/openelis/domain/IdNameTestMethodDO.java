package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class IdNameTestMethodDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String test;
    protected String method;
    
    public IdNameTestMethodDO(Integer id, String name, String test, String method){
        setId(id);
        setName(name);
        setTest(test);
        setMethod(method);        
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
        this.method = DataBaseUtil.trim(method);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = DataBaseUtil.trim(test);
    }

}
