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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.openelis.bean.UserCacheBean;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.util.UTFResource;
import org.openelis.web.modules.main.client.OpenELISRPC;
import org.openelis.web.modules.main.client.OpenELISWebServiceInt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This class loads initial data for the main screen of the OpenELIS Web app
 */
@WebServlet("/openelisweb/service")
public class OpenELISWebServlet extends RemoteServiceServlet implements OpenELISWebServiceInt  {

    private static final long serialVersionUID = 1L;
    
    @EJB
    UserCacheBean userCache;

    public OpenELISRPC initialData() {
        OpenELISRPC rpc;

        rpc = new OpenELISRPC();
        rpc.appConstants = getConstants();

        return rpc;
    }

    public void keepAlive() {
        getThreadLocalRequest().getSession().setAttribute("last_access",
                                                          Datetime.getInstance(Datetime.YEAR,
                                                                               Datetime.MINUTE));
    }

    public void logout() {
        HttpSession session;

        try {
            userCache.logout();
            session = getThreadLocalRequest().getSession(false);
            if (session != null) {
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
        if (getThreadLocalRequest().getSession(false) != null)
            locale = (String)getThreadLocalRequest().getSession().getAttribute("locale");
        if (locale == null)
            locale = "en";
        resource = UTFResource.getBundle("org.openelis.constants.OpenELISWebConstants", new Locale(locale));
        
        consts = new HashMap<String, String>();
        keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            consts.put(key, resource.getString(key));
        }

        return consts;
    }

    @Override
    public Datetime getLastAccess() {
        return (Datetime)getThreadLocalRequest().getSession().getAttribute("last_access");
    }
}
