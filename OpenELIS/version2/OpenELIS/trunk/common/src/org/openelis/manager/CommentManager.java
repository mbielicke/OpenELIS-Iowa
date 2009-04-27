package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public class CommentManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    Integer  parentId;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
