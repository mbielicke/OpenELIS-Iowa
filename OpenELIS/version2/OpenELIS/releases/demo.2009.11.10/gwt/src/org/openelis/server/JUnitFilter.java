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
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.LoginRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;

//import edu.uiowa.uhl.security.remote.SecurityRemote;

// import org.openelis.tt.server.security.SecurityService;
/**
 * Elis controller is the entry point for Elis Web Application. This object
 * controls the workflow for processing request and response.
 * 
 * @author fyu
 * 
 */
public class JUnitFilter implements Filter {
    /**
     * 
     */
    private static final long serialVersionUID = -7471211263251691905L;
    private static Logger log = Logger.getLogger(JUnitFilter.class.getName());
    private String locale;
    private String user;
    private String pass;
    
    public void init(FilterConfig config) throws ServletException {
        log.debug("Initializing the Application.");
        if(config.getInitParameter("AppRoot") != null)
            Constants.APP_ROOT = config.getInitParameter("AppRoot");
        System.out.println(Constants.APP_ROOT);
        if(config.getInitParameter("Locale") != null)
            locale = config.getInitParameter("Locale");
        if(config.getInitParameter("User") != null)
            user = config.getInitParameter("User");
        if(config.getInitParameter("Pass") != null)
            pass = config.getInitParameter("Pass");
        SessionManager.init("OpenELIS"); 
        ServiceUtils.props = "org.openelis.modules.main.server.constants.OpenELISConstants";
        CachingManager.init(Constants.APP_ROOT);
        log.debug("getting out");
    }

    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest hreq = (HttpServletRequest)req;
        if(hreq.getRequestURI().endsWith(".jpg") || hreq.getRequestURI().endsWith(".gif")){
            try {
                chain.doFilter(req,response);
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }
        if(locale != null)
            hreq.getSession().setAttribute("locale",locale);
        SessionManager.setSession(hreq.getSession());
        if(SessionManager.getSession().getAttribute("jndiProps") == null) {
            String username = user;
            String password = pass;
                File propFile = null;
                InputStream is = null;
                Properties props = new Properties();
                try{
                    propFile = new File("/usr/pub/http/var/jndi/jndi.properties");
                    is = new FileInputStream(propFile);
                    props.load(is);
                }catch(Exception e){
                    props.setProperty("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
                    props.setProperty("java.naming.provider.url","localhost");
                }
                props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.security.jndi.LoginInitialContextFactory");
                props.setProperty(Context.SECURITY_PROTOCOL, "other");
                props.setProperty(InitialContext.SECURITY_CREDENTIALS,
                                  password);
                props.setProperty(Context.SECURITY_PRINCIPAL, username);
                try {
                    InitialContext ctx = new InitialContext(props);
                    LoginRemote remote = (LoginRemote)ctx.lookup("openelis/LoginBean/remote");
                    SecurityUtil security = remote.login();
                    SessionManager.getSession().setAttribute("security", security);
                    if(!security.has("openelis",ModuleFlags.SELECT)){
                        ((HttpServletResponse)response).sendRedirect("NoPermission.html");
                        return;
                    }
                    SessionManager.getSession().setAttribute("jndiProps", props);
                    SessionManager.getSession().setAttribute("USER_NAME", username);
                }catch(Exception e){
                    e.printStackTrace();
                }
        }
        try {
            chain.doFilter(req, response);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void destroy(){
        CachingManager.destroy();
    }
}
