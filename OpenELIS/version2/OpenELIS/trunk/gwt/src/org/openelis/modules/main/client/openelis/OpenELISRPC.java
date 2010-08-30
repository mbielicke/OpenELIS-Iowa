package org.openelis.modules.main.client.openelis;

import java.util.HashMap;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SystemUserPermission;

public class OpenELISRPC implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    public HashMap<String,String> appConstants;
    public SystemUserPermission systemUserPermission;
}
