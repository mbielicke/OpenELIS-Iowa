package org.openelis.domain;

import java.io.Serializable;

public class QaEventTestDropdownDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String test;
    protected String method;

    public QaEventTestDropdownDO(Integer id, String test, String method){
        this.id = id;
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

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
