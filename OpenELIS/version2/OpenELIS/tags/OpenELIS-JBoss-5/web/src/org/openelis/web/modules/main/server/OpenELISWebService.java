/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.web.modules.main.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.openelis.persistence.EJBFactory;
import org.openelis.remote.UserCacheRemote;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.web.modules.main.client.OpenELISRPC;

/**
 * This class loads initial data for the main screen of the OpenELIS Web app
 */
public class OpenELISWebService {

    public OpenELISRPC initialData() {
        OpenELISRPC rpc;

        rpc = new OpenELISRPC();
        rpc.appConstants = getConstants();

        return rpc;
    }

    public void keepAlive() {
    }

    public void logout() {
        HttpSession session;

        try {
            remote().logout();
            session = SessionManager.getSession();
            if (session != null) {
                SessionManager.removeSession(session.getId());
                session.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that sets up a Hash of String literals by locale to be used by the client
     */
    private HashMap<String, String> getConstants() {
        String locale, key;
        UTFResource resource;
        Enumeration<String> keys;
        HashMap<String, String> consts;
        
        locale = null;
        if (SessionManager.getSession() != null)
            locale = (String)SessionManager.getSession().getAttribute("locale");
        if (locale == null)
            locale = "en";
        resource = UTFResource.getBundle("org.openelis.web.modules.main.server.constants.OpenELISConstants", new Locale(locale));
        
        consts = new HashMap<String, String>();
        keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            consts.put(key, resource.getString(key));
        }

        return consts;
    }

    private UserCacheRemote remote() {
        return (UserCacheRemote)EJBFactory.lookup("openelis/UserCacheBean/remote");
    }
}
