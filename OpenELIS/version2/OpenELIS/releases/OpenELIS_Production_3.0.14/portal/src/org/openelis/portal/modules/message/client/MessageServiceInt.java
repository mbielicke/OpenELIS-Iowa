package org.openelis.portal.modules.message.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("message")
public interface MessageServiceInt extends XsrfProtectedService {

    String getMessage() throws Exception;

}