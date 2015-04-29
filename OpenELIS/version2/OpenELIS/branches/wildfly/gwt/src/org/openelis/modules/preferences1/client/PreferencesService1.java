package org.openelis.modules.preferences1.client;

import org.openelis.manager.Preferences1;
import org.openelis.ui.annotation.Service;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("preferences1")
public interface PreferencesService1 extends XsrfProtectedService {

    /**
     * This method will return the System Preferences for the key passed
     * @param key
     * @return
     * @throws Exception
     */
    Preferences1 systemRoot() throws Exception;

    /**
     * This method will return the User Preferences for the key passed
     * @param key
     * @return
     * @throws Exception
     */
    Preferences1 userRoot() throws Exception;

    /**
     * This method will send the Preferences passed to JBoss to be stored
     * @param prefs
     * @throws Exception
     */
    void flush(Preferences1 prefs) throws Exception;

}