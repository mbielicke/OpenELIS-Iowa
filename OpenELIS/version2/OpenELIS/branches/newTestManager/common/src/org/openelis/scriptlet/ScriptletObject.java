package org.openelis.scriptlet;

import java.io.Serializable;

public class ScriptletObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    
    public static enum Operation {ADD,UPDATE,DELETE};
    
    protected Operation op;
    
    
    public ScriptletObject() {
        
    }
    
    public ScriptletObject(Integer id, Operation op) {
        this.id = id;
        this.op = op;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setOperation(Operation op) {
        this.op = op;
    }
    
    public Operation getOperation() {
        return op;
    }

}
