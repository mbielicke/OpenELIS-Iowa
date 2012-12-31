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
package org.openelis.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openelis.gwt.server.ServiceUtils;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Elis controller is the entry point for Elis Web Application. This object
 * controls the workflow for processing request and response.
 * 
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger     authLog          = Logger.getLogger("org.openelis.auth");
    private static String     AppRoot;
    private static int        LOGIN_LOCK_TIME  = 1000 * 60 * 10, // minutes to lock user out
                              LOGIN_TRY_IP_CNT = 7, // max # of bad ip counts before being locked out
                              LOGIN_TRY_NM_CNT = 4; // max # of bad name counts before being locked out

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                    IOException {
        doGet(req,resp);
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException,IOException {
        String error = null,locale = "en";
        HttpServletRequest hreq = (HttpServletRequest)req;

        //
        // used for language binding
        //
        if (hreq.getParameter("locale") != null) {
            locale = req.getParameter("locale");
            hreq.getSession().setAttribute("locale", locale);
        }

        //
        // ask them to authenticate
        //
        try {
            Document doc;
            Element session,localeEl;

            doc = XMLUtil.createNew("login");
            
            session = doc.createElement("session");
            session.appendChild(doc.createTextNode(String.valueOf(req.getSession().getId())));
            doc.getDocumentElement().appendChild(session);
            
            localeEl = doc.createElement("locale");
            localeEl.appendChild(doc.createTextNode(locale));
            doc.getDocumentElement().appendChild(localeEl);
            
            if (req.getParameter("error") != null) {
                Element errorEL = doc.createElement("error");
                errorEL.appendChild(doc.createTextNode("Failed to login"));
                doc.getDocumentElement().appendChild(errorEL);
            }
            
            ((HttpServletResponse)response).setContentType("text/html");
            ((HttpServletResponse)response).setCharacterEncoding("UTF-8");
            
            response.getWriter().write(ServiceUtils.getXML(getServletContext().getRealPath("") +
                                                       "/jbosslogin.xsl", doc,(String)hreq.getSession().getAttribute("locale")));
        } catch (Exception e) {
            // log.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    /*
     * Simple class to manage login attempts
     */
    private static class LoginAttempt {
        int                                          tries;
        long                                         lastTime;

        private static HashMap<String, LoginAttempt> failed = new HashMap<String, LoginAttempt>();

        public static boolean isValid(String name, String ipAddress) {
            long cutoff;
            LoginAttempt la;

            cutoff = System.currentTimeMillis() - LOGIN_LOCK_TIME;

            la = failed.get(ipAddress);
            if (la != null && la.lastTime >= cutoff && la.tries >= LOGIN_TRY_IP_CNT)
                return false;

            la = failed.get(name);
            if (la != null && la.lastTime >= cutoff && la.tries >= LOGIN_TRY_NM_CNT)
                return false;

            return true;
        }

        /**
         * Clears the failed list for the user and ip address. TODO: need a
         * sliding window remove for clearing the ip address for better
         * security.
         */
        public static void success(String name, String ipAddress) {
            failed.remove(ipAddress);
            failed.remove(name);

            authLog.info("Login attempt for " + name + " - " + ipAddress + " succeeded");
        }

        public static void fail(String name, String ipAddress) {
            long now;
            LoginAttempt li, ln;

            now = System.currentTimeMillis();

            li = failed.get(ipAddress);
            if (li == null) {
                li = new LoginAttempt();
                failed.put(ipAddress, li);
            }
            li.lastTime = now;
            li.tries = Math.min(li.tries + 1, 9999);

            ln = failed.get(name);
            if (ln == null) {
                ln = new LoginAttempt();
                failed.put(name, ln);
            }
            ln.lastTime = now;
            ln.tries = Math.min(ln.tries + 1, 9999);

            authLog.info("Login attempt for " + name + " [" + ln.tries + "]" + " - " + ipAddress +
                         " [" + li.tries + "] failed ");
        }
    }
}