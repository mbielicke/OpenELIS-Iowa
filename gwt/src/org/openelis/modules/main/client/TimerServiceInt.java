package org.openelis.modules.main.client;

import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("timer")
public interface TimerServiceInt extends XsrfProtectedService  {

    void keepAlive();
    
    Datetime getLastAccess();

}