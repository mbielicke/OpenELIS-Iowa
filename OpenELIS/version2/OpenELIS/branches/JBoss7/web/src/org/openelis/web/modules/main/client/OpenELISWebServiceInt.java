package org.openelis.web.modules.main.client;

import org.openelis.gwt.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface OpenELISWebServiceInt extends RemoteService {

    OpenELISRPC initialData();

    void keepAlive();

    void logout();
    
    Datetime getLastAccess();

}