package org.openelis.portal.modules.main.client;

import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("main")
public interface UserResponseServiceInt extends XsrfProtectedService {

    ReportStatus saveResponse(String response) throws Exception;
}