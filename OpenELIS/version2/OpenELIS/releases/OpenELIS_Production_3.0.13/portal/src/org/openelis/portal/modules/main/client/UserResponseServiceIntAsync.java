package org.openelis.portal.modules.main.client;

import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserResponseServiceIntAsync {

    void saveResponse(String response, AsyncCallback<ReportStatus> callback);

}
