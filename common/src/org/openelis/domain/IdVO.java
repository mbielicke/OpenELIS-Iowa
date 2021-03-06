package org.openelis.domain;

import java.io.Serializable;

/**
 * The class is used to carry id for query returns, for left 
 * display, and some auto complete fields. The fields are considered read/display
 * and do not get committed to the database.
 */
public class IdVO implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer         id;
    
    public IdVO() {
    }

    public IdVO(Integer id) {
        setId(id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
