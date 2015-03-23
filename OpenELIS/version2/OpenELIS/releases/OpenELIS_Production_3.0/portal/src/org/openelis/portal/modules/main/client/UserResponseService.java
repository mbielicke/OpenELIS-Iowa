package org.openelis.portal.modules.main.client;

import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class UserResponseService implements UserResponseServiceInt,
                                       UserResponseServiceIntAsync {

    private static UserResponseService instance;

    private UserResponseServiceIntAsync       service;

    public static UserResponseService get() {
        if (instance == null)
            instance = new UserResponseService();

        return instance;
    }

    private UserResponseService() {
        service = (UserResponseServiceIntAsync)GWT.create(UserResponseServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void saveResponse(String response, AsyncCallback<ReportStatus> callback) {
        service.saveResponse(response, callback);
    }

    @Override
    public ReportStatus saveResponse(String response) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.saveResponse(response, callback);
        return callback.getResult();
    }

}
