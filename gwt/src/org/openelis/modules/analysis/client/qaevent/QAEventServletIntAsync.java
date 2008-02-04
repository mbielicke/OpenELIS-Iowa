package org.openelis.modules.analysis.client.qaevent;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QAEventServletIntAsync extends AppScreenFormServiceIntAsync{
    public void getInitialModel(String cat, AsyncCallback callback);
}
