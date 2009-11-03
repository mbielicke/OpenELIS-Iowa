package org.openelis.server;

import org.openelis.gwt.common.Preferences;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.services.PreferencesServiceInt;

public class PreferencesServlet extends AppServlet implements
                                                  PreferencesServiceInt {

    public Preferences getPreferences(String key) {
        // TODO Auto-generated method stub
        return PreferencesManager.getUser(key);
    }

    public void storePreferences(Preferences prefs) {
        PreferencesManager.store(prefs);

    }

}
