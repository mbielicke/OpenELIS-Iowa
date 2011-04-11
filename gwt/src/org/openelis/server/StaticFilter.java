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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.JMSMessageConsumer;
import org.openelis.remote.SystemUserPermissionProxyRemote;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Elis controller is the entry point for Elis Web Application. This object
 * controls the workflow for processing request and response.
 * 
 */
public class StaticFilter implements Filter {

    private static final long    serialVersionUID = 1L;
    private static Logger        authLog          = Logger.getLogger("org.openelis.auth");
    private static String        AppRoot;
    private static int           LOGIN_LOCK_TIME  = 1000 * 60 * 10,     // minutes to lock user out
                                 LOGIN_TRY_IP_CNT = 7,                  // max # of bad ip counts before being locked out
                                 LOGIN_TRY_NM_CNT = 4;                  // max # of bad name counts before being locked out

    public void init(FilterConfig config) throws ServletException {
        AppRoot = config.getInitParameter("AppRoot");
        ServiceUtils.props = "org.openelis.constants.OpenELISConstants";
        JMSMessageConsumer.startListener("topic/openelisTopic");
    }

    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException {
        String error = null;
        HttpServletRequest hreq = (HttpServletRequest)req;

        //
        // pass-through for images and if we are logged-in
        //
        if (hreq.getRequestURI().endsWith(".jpg") || hreq.getRequestURI().endsWith(".gif") ||
            hreq.getSession().getAttribute("USER_NAME") != null) {
            try {
                chain.doFilter(req, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        //
        // used for language binding
        //
        if (hreq.getParameter("locale") != null)
            hreq.getSession().setAttribute("locale", req.getParameter("locale"));

        //
        // register this session with SessionManager so we can access it
        // statically in gwt code
        //
        SessionManager.setSession(hreq.getSession());

        //
        // check to see if we are coming from login screen
        //
        if (hreq.getParameter("username") != null) {            
            try {
                login(hreq, hreq.getParameter("username"), req.getParameter("password"),  
                      hreq.getRemoteAddr());
                try {
                    chain.doFilter(req, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } catch (Exception e) {
                error = "authFailure";
            }
        }
        //
        // ask them to authenticate
        //
        try {
            Document doc;
            Element action;
            
            doc = XMLUtil.createNew("login");
            action = doc.createElement("action");
            action.appendChild(doc.createTextNode("OpenELIS.html"));
            doc.getDocumentElement().appendChild(action);
            if (error != null) {
                Element errorEL = doc.createElement("error");
                errorEL.appendChild(doc.createTextNode(error));
                doc.getDocumentElement().appendChild(errorEL);
            }
            ((HttpServletResponse)response).setHeader("pragma", "no-cache");
            ((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
            ((HttpServletResponse)response).setHeader("Cache-Control", "no-store");
            ((HttpServletResponse)response).setDateHeader("Expires", 0);
            ((HttpServletResponse)response).setContentType("text/html");
            ((HttpServletResponse)response).setCharacterEncoding("UTF-8");
            response.getWriter().write(ServiceUtils.getXML(AppRoot + "login.xsl", doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void destroy() {
        System.out.println("in static filter distroy");
        JMSMessageConsumer.stopListener();
    }

    /*
     * log the user into the system by sending its credentials to JBOSS for
     * authentication
     */
    
    private void login(HttpServletRequest req, String name, String password, String ipAddress) throws Exception {
        InitialContext localctx, remotectx;
        File propFile;
        Properties props;
        SystemUserPermissionProxyRemote remote;
        SystemUserPermission perm;
        
        try {
            if (! LoginAttempt.isValid(name, ipAddress))
                throw new PermissionException();

            localctx = new InitialContext();
            propFile = new File((String)localctx.lookup( ("java:comp/env/openelisJNDI")));
            props = new Properties();
            props.load(new FileInputStream(propFile));
            props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.LoginInitialContextFactory");
            props.setProperty(InitialContext.SECURITY_PROTOCOL, "other");
            props.setProperty(Context.SECURITY_PRINCIPAL, name);
            props.setProperty(InitialContext.SECURITY_CREDENTIALS, password);

            remotectx = new InitialContext(props);
            remote = (SystemUserPermissionProxyRemote)remotectx.lookup("openelis/SystemUserPermissionProxyBean/remote");
            perm = remote.login();
            //
            // check to see if she has connect permission
            //
            if (!perm.hasConnectPermission())
                throw new PermissionException();

            req.getSession().setAttribute("UserPermission", perm);
            req.getSession().setAttribute("jndiProps", props);
            req.getSession().setAttribute("USER_NAME", name);

            authLog.info("Login attempt for " + name + " succeeded");
        } catch (Exception e) {
            LoginAttempt.fail(name, ipAddress);
            throw e;
        }
    }
    
    /*
     * Simple class to manage login attempts
     */
    private static class LoginAttempt {
        int tries;
        long lastTime;

        private static HashMap<String, LoginAttempt> failed = new HashMap<String, StaticFilter.LoginAttempt>();
    
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
            li.tries = Math.min(li.tries+1, LOGIN_TRY_IP_CNT);

            ln = failed.get(name);
            if (ln == null) {
                ln = new LoginAttempt();
                failed.put(name, ln);
            }
            ln.lastTime = now;
            ln.tries = Math.min(ln.tries+1, LOGIN_TRY_NM_CNT);

            authLog.info("Login attempt for "+ name +" ["+ ln.tries +"]"+" - "+ ipAddress +" ["+ li.tries +"] failed ");
        }
    }
}