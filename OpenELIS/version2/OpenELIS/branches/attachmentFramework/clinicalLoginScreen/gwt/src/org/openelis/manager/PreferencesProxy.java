package org.openelis.manager;

import org.openelis.modules.preferences.client.PreferencesService;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is proxy to the preferences that will direct calls to the server
 * from the client.
 * 
 * @author tschmidt
 * 
 */
public class PreferencesProxy {


    /**
     * Cached instances for client
     */
    private static Preferences user, system;

    /**
     * Proxy call to return the Preferences for a user
     * 
     * @return
     * @throws Exception
     */
    public Preferences userRoot() throws Exception {
        if (user == null)
            user = PreferencesService.get().userRoot();
        return user;
    }

    /**
     * Proxy call to return the Preference for the system
     * 
     * @return
     * @throws Exception
     */
    public Preferences systemRoot() throws Exception {
        if (system == null)
            system = PreferencesService.get().systemRoot();
        return system;
    }

    /**
     * Method passes the call up to the server to persist the preferences
     * 
     * @param prefs
     * @throws Exception
     */
    public void flush(Preferences prefs) throws Exception {

        PreferencesService.get().flush(prefs, new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
            }
            public void onFailure(Throwable caught) {
                Window.alert("Persistence of Preference failed : " + caught.getMessage());
            }
        });

    }
}
