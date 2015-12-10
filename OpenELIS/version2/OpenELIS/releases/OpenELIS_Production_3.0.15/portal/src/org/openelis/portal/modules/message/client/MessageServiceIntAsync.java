package org.openelis.portal.modules.message.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageServiceIntAsync {

    void getMessage(AsyncCallback<String> callback);

}
