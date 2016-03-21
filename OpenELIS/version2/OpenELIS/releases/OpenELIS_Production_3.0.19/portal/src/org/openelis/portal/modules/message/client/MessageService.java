package org.openelis.portal.modules.message.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class MessageService implements MessageServiceInt, MessageServiceIntAsync {

    private static MessageService  instance;

    private MessageServiceIntAsync service;

    public static MessageService get() {
        if (instance == null)
            instance = new MessageService();

        return instance;
    }

    private MessageService() {
        service = (MessageServiceIntAsync)GWT.create(MessageServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getMessage(AsyncCallback<String> callback) {
        service.getMessage(callback);
    }
    
    @Override
    public String getMessage() throws Exception {
        Callback<String> callback;

        callback = new Callback<String>();
        service.getMessage(callback);
        return callback.getResult();
    }

}
