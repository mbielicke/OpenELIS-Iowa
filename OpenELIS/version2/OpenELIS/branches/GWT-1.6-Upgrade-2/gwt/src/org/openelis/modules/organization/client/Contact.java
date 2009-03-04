package org.openelis.modules.organization.client;

import java.io.Serializable;

public class Contact implements Serializable {
   
    private static final long serialVersionUID = 1L;

    public Integer orgId;
    
    public Integer addId;
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return orgId.hashCode();
    }
}
