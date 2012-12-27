package org.openelis.modules.main.client;

import org.openelis.gwt.common.Datetime;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OpenELISServiceIntAsync {

    void initialData(AsyncCallback<OpenELISRPC> callback);

    void keepAlive(AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);

    void getLastAccess(AsyncCallback<Datetime> callback);

}
