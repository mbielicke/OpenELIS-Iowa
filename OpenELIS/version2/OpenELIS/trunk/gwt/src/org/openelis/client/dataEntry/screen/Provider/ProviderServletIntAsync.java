package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServletIntAsync extends AppScreenFormServiceIntAsync{
    
    public void getNoteTreeModel(Integer key, boolean topLevel, AsyncCallback callback);    
    public void getNoteTreeSecondLevelXml(String key, boolean topLevel, AsyncCallback callback);
}
