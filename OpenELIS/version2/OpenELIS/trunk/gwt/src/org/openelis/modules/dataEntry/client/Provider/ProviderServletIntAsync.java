package org.openelis.modules.dataEntry.client.Provider;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServletIntAsync extends AppScreenFormServiceIntAsync{
    
    public void getNotesModel(Integer key, AsyncCallback callback);
    public void getInitialModel(String cat, AsyncCallback callback);
}
