package org.openelis.modules.main.client;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OpenELISServiceIntAsync {

    void getConstants(AsyncCallback<Constants> callback);

    void keepAlive(AsyncCallback<Void> callback);

    void logout(AsyncCallback<Void> callback);

    void getLastAccess(AsyncCallback<Datetime> callback);

}
