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
import java.io.InputStream;
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
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.SystemUserPermissionProxyRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;

public class HostedFilter implements Filter {
    private static final long serialVersionUID = 1L;
    private static Logger     log              = Logger.getLogger(HostedFilter.class.getName());
    private String            locale;
    private String            user;
    private String            pass;

    public void init(FilterConfig config) throws ServletException {
        log.debug("Initializing the Application.");

        Constants.APP_ROOT = config.getInitParameter("AppRoot");
        locale = config.getInitParameter("Locale");
        user = config.getInitParameter("User");
        pass = config.getInitParameter("Pass");

        SessionManager.init("OpenELIS");
        ServiceUtils.props = "org.openelis.modules.main.server.constants.OpenELISConstants";

        CachingManager.init(Constants.APP_ROOT);

        log.debug("getting out");
    }

    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest hreq = (HttpServletRequest)req;
        if (hreq.getRequestURI().endsWith(".jpg") || hreq.getRequestURI().endsWith(".gif")) {
            try {
                chain.doFilter(req, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (locale != null)
            hreq.getSession().setAttribute("locale", locale);

        SessionManager.setSession(hreq.getSession());

        if (SessionManager.getSession().getAttribute("jndiProps") == null) {
            String username = user;
            String password = pass;
            File propFile = null;
            InputStream is = null;
            Properties props = new Properties();
            try {
                propFile = new File("/usr/pub/http/var/jndi/jndi.properties");
                is = new FileInputStream(propFile);
                props.load(is);
            } catch (Exception e) {
                props.setProperty("java.naming.factory.url.pkgs",
                                  "org.jboss.naming:org.jnp.interfaces");
                props.setProperty("java.naming.provider.url", "luchta.uhl.uiowa.edu:1299");
            }
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                              "org.jboss.security.jndi.LoginInitialContextFactory");
            props.setProperty(Context.SECURITY_PROTOCOL, "other");
            props.setProperty(InitialContext.SECURITY_CREDENTIALS, password);
            props.setProperty(Context.SECURITY_PRINCIPAL, username);
            try {
                InitialContext ctx = new InitialContext(props);
                SystemUserPermissionProxyRemote remote = (SystemUserPermissionProxyRemote)ctx.lookup("openelis/SystemUserPermissionProxyBean/remote");
                SystemUserPermission perm = remote.login();
                SessionManager.getSession().setAttribute("UserPermission", perm);
                if ( !perm.hasConnectPermission()) {
                    ((HttpServletResponse)response).sendRedirect("NoPermission.html");
                    return;
                }
                SessionManager.getSession().setAttribute("jndiProps", props);
                SessionManager.getSession().setAttribute("USER_NAME", username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            chain.doFilter(req, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        CachingManager.destroy();
    }
}
