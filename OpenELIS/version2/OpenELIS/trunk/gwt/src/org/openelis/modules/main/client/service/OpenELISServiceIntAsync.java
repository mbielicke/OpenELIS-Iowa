package org.openelis.modules.main.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

public interface OpenELISServiceIntAsync extends AppScreenFormServiceIntAsync {
    
    public void getObject(String method, DataObject[] args, AsyncCallback callback);
    
    public void getMenuList(AsyncCallback callback);
    
    public void logout(AsyncCallback callback);
    

}
