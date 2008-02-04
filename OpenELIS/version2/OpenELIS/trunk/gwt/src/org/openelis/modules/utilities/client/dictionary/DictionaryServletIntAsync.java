package org.openelis.modules.utilities.client.dictionary;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryServletIntAsync extends AppScreenFormServiceIntAsync {
    public void getEntryIdForSystemName(String systemName,AsyncCallback callback);
    public void getEntryIdForEntry(String entry,AsyncCallback callback);
    public void getInitialModel(String cat, AsyncCallback callback);
}
