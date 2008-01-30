package org.openelis.client.analysis.screen.qaevent;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QAEventServletIntAsync extends AppScreenFormServiceIntAsync{
    public void getInitialModel(String cat, AsyncCallback callback);
}
