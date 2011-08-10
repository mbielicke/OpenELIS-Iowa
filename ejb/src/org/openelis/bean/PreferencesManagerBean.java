package org.openelis.bean;

import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.manager.Preferences;
import org.openelis.remote.PreferencesRemote;

/**
 * This class implements the PreferencesRemote and is the bean
 * called by the client to do Preference persistence 
 *
 */
@Stateless
@SecurityDomain("openelis")
public class PreferencesManagerBean implements PreferencesRemote {

	/**
	 * Returns the User Preferences for the logged in user
	 */
	public Preferences userRoot() throws Exception {
		return Preferences.userRoot();
	}
	
	/**
	 * Returns the System Preferences
	 */
	public Preferences systemRoot() throws Exception {
		return Preferences.systemRoot();
	}

	/**
	 * Persists the passed Preferences object
	 */
	public void flush(Preferences prefs) throws Exception {
		prefs.flush();
	}

}
