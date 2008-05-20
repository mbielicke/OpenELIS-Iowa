package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class QaEventTestDropdownDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String test;
    protected String method;

    public QaEventTestDropdownDO(Integer id, String test, String method){
        setId(id);
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

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = DataBaseUtil.trim(test);
    }
}
