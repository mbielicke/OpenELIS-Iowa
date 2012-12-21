package org.openelis.modules.preferences.client;

import org.openelis.manager.Preferences;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("preferences")
public interface PreferencesServiceInt extends RemoteService {

    /**
     * This method will return the System Preferences for the key passed
     * @param key
     * @return
     * @throws Exception
     */
    Preferences systemRoot() throws Exception;

    /**
     * This method will return the User Preferences for the key passed
     * @param key
     * @return
     * @throws Exception
     */
    Preferences userRoot() throws Exception;

    /**
     * This method will send the Preferences passed to JBoss to be stored
     * @param prefs
     * @throws Exception
     */
    void flush(Preferences prefs) throws Exception;

}