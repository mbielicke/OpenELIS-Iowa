package org.openelis.server;

import edu.uiowa.uhl.security.remote.SecurityRemote;

import org.apache.log4j.Logger;
import org.openelis.persistence.CachingManager;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        if(config.getInitParameter("Locale") != null)
            locale = config.getInitParameter("Locale");
        if(config.getInitParameter("User") != null)
            user = config.getInitParameter("User");
        if(config.getInitParameter("Pass") != null)
            pass = config.getInitParameter("Pass");
        SessionManager.init("OpenELIS"); 
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
                    SecurityRemote remote = (SecurityRemote)ctx.lookup("SecurityBean/remote");
                    HashMap mods = remote.getModules("openelis");
                    SessionManager.getSession().setAttribute("permissions", mods);
                    if(!mods.containsKey("openelis-select")){
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
