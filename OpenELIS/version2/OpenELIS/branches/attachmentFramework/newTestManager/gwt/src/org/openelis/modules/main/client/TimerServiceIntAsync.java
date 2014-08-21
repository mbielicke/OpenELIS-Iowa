package org.openelis.modules.main.client;

import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TimerServiceIntAsync {

    void keepAlive(AsyncCallback<Void> callback);

    void getLastAccess(AsyncCallback<Datetime> callback);

}
