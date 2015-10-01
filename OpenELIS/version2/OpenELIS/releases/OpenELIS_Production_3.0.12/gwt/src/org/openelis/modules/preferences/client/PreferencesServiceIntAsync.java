package org.openelis.modules.preferences.client;

import org.openelis.manager.Preferences;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PreferencesServiceIntAsync {

    void flush(Preferences prefs, AsyncCallback<Void> callback);

    void systemRoot(AsyncCallback<Preferences> callback);

    void userRoot(AsyncCallback<Preferences> callback);

}
