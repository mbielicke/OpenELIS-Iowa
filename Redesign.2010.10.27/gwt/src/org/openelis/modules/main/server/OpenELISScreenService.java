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
package org.openelis.modules.main.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.main.client.openelis.OpenELISRPC;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class OpenELISScreenService {

    public static String APP_ROOT; 

    public String getScreen() {
        try {
            return ServiceUtils.getXML(APP_ROOT + "/Forms/main.xsl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public OpenELISRPC initialData() {
        OpenELISRPC rpc;

        rpc = new OpenELISRPC();
        rpc.appConstants = getConstants();
        rpc.systemUserPermission = (SystemUserPermission)SessionManager.getSession().getAttribute("UserPermission");

        return rpc;
    }

    public void logout() {
        HttpSession session;

        try {
            session = SessionManager.getSession();
            if (session != null) {
                SessionManager.removeSession(session.getId());
                session.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        resource = UTFResource.getBundle("org.openelis.constants.OpenELISConstants", new Locale(locale));
        
        consts = new HashMap<String, String>();
        keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            consts.put(key, resource.getString(key));
        }

        return consts;
    }

}
