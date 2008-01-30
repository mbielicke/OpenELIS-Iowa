package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServletIntAsync extends AppScreenFormServiceIntAsync{
    
    public void getNotesModel(Integer key, AsyncCallback callback);
    public void getInitialModel(String cat, AsyncCallback callback);
}
