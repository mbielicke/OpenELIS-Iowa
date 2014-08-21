package org.openelis.modules.main.client;

import java.io.Serializable;
import java.util.HashMap;

import org.openelis.domain.Constants;
public class OpenELISRPC implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public HashMap<String,String> appConstants;
    public Constants constants;
}
