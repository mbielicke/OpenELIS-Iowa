package org.openelis.remote;

import org.openelis.domain.PreferencesDO;

import javax.ejb.Remote;

@Remote
public interface PreferencesRemote {
    
    public PreferencesDO getPreferences(String key);
    
    public void storePreferences(PreferencesDO prefs);

}
