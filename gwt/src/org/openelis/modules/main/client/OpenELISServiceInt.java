package org.openelis.modules.main.client;

import org.openelis.gwt.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface OpenELISServiceInt extends RemoteService  {

    OpenELISRPC initialData() throws Exception;

    void keepAlive();

    void logout();
    
    Datetime getLastAccess();

}