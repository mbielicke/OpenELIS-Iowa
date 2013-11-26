package org.openelis.web.modules.main.client;

import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("service")
public interface OpenELISWebServiceInt extends XsrfProtectedService {

    OpenELISRPC initialData() throws Exception;

    void keepAlive();

    void logout();
    
    Datetime getLastAccess();

}