/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.preferences.server;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.PreferencesManagerBean;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.Preferences;
import org.openelis.modules.preferences.client.PreferencesServiceInt;

/**
 * This class is the tomcat pass through for the PreferencesBean on JBoss
 *	
 */
@WebServlet("/openelis/preferences")
public class PreferencesServlet extends RemoteServlet implements PreferencesServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    PreferencesManagerBean preferences;

	/**
	 * This method will return the System Preferences for the key passed
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Preferences systemRoot() throws Exception {
		return preferences.systemRoot();
	}
	
	/**
	 * This method will return the User Preferences for the key passed
	 * @param key
	 * @return
	 * @throws Exception
	 */
    public Preferences userRoot() throws Exception {
        return preferences.userRoot();
    }

    /**
     * This method will send the Preferences passed to JBoss to be stored
     * @param prefs
     * @throws Exception
     */
    public void flush(Preferences prefs) throws Exception {
        preferences.flush(prefs);
    }

}
