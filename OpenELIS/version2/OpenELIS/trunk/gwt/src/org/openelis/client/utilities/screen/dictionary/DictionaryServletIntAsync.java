package org.openelis.client.utilities.screen.dictionary;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryServletIntAsync extends AppScreenFormServiceIntAsync {
    public void getEntryIdForSystemName(String systemName,AsyncCallback callback);
    public void getEntryIdForEntry(String entry,AsyncCallback callback);
}
