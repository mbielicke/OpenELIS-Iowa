package org.openelis.modules.main.client.service;

import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface OpenELISServiceInt extends AppScreenFormServiceInt {
    
    public DataObject getObject(String method, DataObject[] args) throws RPCException;
    
    public String getMenuList() throws RPCException;
    
    public void logout();
	 
}
