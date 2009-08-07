package org.openelis.modules.main.client.openelis;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityUtil;

public class OpenELISRPC implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    public ArrayList<String> modules;
    public HashMap<String,String> appConstants;
    public SecurityUtil security;
    
}
