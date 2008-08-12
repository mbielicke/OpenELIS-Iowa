package org.openelis.domain;

import java.io.Serializable;


public class TestIdNameMethodIdDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8834418161951953239L;
    
    protected Integer id;             

    protected String name;
    
    protected Integer methodId;
    
    public TestIdNameMethodIdDO(){               
        
    }
    
    public TestIdNameMethodIdDO(Integer id,String name,Integer methodId){
        this.id =  id;
        this.name = name;
        this.methodId = methodId;        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
