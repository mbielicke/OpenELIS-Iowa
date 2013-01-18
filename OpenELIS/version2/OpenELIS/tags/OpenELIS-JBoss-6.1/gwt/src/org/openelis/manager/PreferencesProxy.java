package org.openelis.manager;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.services.ScreenService;

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
     * Service for calling persistence methods for preferences
     */
    ScreenService service = new ScreenService("controller?service=org.openelis.server.PreferencesService");

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
            user = service.call("userRoot");
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
            system = service.call("systemRoot");
        return system;
    }

    /**
     * Method passes the call up to the server to persist the preferences
     * 
     * @param prefs
     * @throws Exception
     */
    public void flush(Preferences prefs) throws Exception {

        service.call("flush", prefs, new AsyncCallback<RPC>() {
            public void onSuccess(RPC result) {
            }
            public void onFailure(Throwable caught) {
                Window.alert("Persistence of Preference failed : " + caught.getMessage());
            }
        });

    }
}
