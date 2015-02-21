package org.openelis.client;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("service")
public interface OpenELISServiceInt extends XsrfProtectedService  {

    Constants getConstants() throws Exception;

    void logout();

}