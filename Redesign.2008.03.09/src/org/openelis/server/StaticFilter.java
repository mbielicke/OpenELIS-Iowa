package org.openelis.server;

import edu.uiowa.uhl.security.remote.SecurityRemote;

import org.apache.log4j.Logger;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.CachingManager;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class StaticFilter implements Filter {
    /**
     * 
     */
    private static final long serialVersionUID = -7471211263251691905L;
    private static Logger log = Logger.getLogger(StaticFilter.class.getName());
    private static Logger authLog = Logger.getLogger("org.openelis.auth");
    private int userTrys;
    private int ipTrys;
    
    public void init(FilterConfig config) throws ServletException {
        log.debug("Initializing the Application.");
        if(config.getInitParameter("AppRoot") != null)
            Constants.APP_ROOT = config.getInitParameter("AppRoot");
        if(config.getInitParameter("userTrys") != null)
            userTrys = Integer.parseInt(config.getInitParameter("userTrys"));
        if(config.getInitParameter("ipTrys") != null)
            ipTrys = Integer.parseInt(config.getInitParameter("ipTrys"));
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
        if(hreq.getParameter("locale") != null)
            hreq.getSession().setAttribute("locale",req.getParameter("locale"));
        SessionManager.setSession(hreq.getSession());
        String error = null;
        if(hreq.getParameter("username") != null) {
            String username = hreq.getParameter("username");
            String ip = hreq.getRemoteAddr();
            if(CachingManager.getElement("usernameLockout", username) == null &&
               CachingManager.getElement("ipLockout", ip) == null) {
                System.out.println("Checking Credentials");
                String password = req.getParameter("password");
                File propFile = null;
                InputStream is = null;
                Properties props = new Properties();
                try{
                    propFile = new File("/usr/pub/http/var/jndi/jndi.properties");
                    is = new FileInputStream(propFile);
                    props.load(is);
                }catch(Exception e){
                    props.setProperty("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
                    props.setProperty("java.naming.provider.url","nabu.uhl.uiowa.edu");
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
                    authLog.info("Login attempt for "+username+" succeeded");
                }catch(Exception e){
                    e.printStackTrace();
                    error = "authFailure";
                    Integer ipAttempt = (Integer)CachingManager.getElement("ipAttempts", ip);
                    Integer userAttempt = (Integer)CachingManager.getElement("usernameAttempts",username);
                    if(userAttempt == null)
                        userAttempt = new Integer(1);
                    else{
                        int temp = userAttempt.intValue() + 1;
                        userAttempt = new Integer(temp);
                    }
                    authLog.info("Login attempt for "+username+" failed "+userAttempt+" times");
                    CachingManager.putElement("usernameAttempts", username, userAttempt);
                    if(ipAttempt == null)
                        ipAttempt = new Integer(1);
                    else{
                        int temp = ipAttempt.intValue() + 1;
                        ipAttempt = new Integer(temp);
                    }
                    authLog.info("Login attempt from ip "+ip+" failed "+ipAttempt+" times");
                    CachingManager.putElement("ipAttempts",ip,ipAttempt);
                    if(userAttempt >= userTrys) {
                        CachingManager.putElement("usernameLockout", username, "locked");
                        authLog.info("Locking out username "+username);
                    }
                    if(ipAttempt >= ipTrys){
                        CachingManager.putElement("ipLockout", ip, "locked");
                        authLog.info("Locking out ip "+ip);
                    }
                }
            }else{
                error = "authFailure";
                if(CachingManager.getElement("usernameLockout", username) != null){
                    CachingManager.putElement("usernameLockout", username, "locked");
                    authLog.info("Ignored attempt from username "+username);
                }
                if(CachingManager.getElement("ipLockout", ip) != null) {
                    CachingManager.putElement("ipLockout", ip, "locked");
                    authLog.info("Ignored attempt from ip "+ip);
                }
            }
        }
        if (SessionManager.getSession().getAttribute("USER_NAME") == null) {
            try {
                Document doc = XMLUtil.createNew("login");
                Element action = doc.createElement("action");
                action.appendChild(doc.createTextNode(hreq.getRequestURI()));
                doc.getDocumentElement().appendChild(action);
                if(error != null){
                    Element errorEL = doc.createElement("error");
                    errorEL.appendChild(doc.createTextNode(error));
                    doc.getDocumentElement().appendChild(errorEL);
                }
                ((HttpServletResponse)response).setHeader("pragma", "no-cache");
                ((HttpServletResponse)response).setHeader("Cache-Control","no-cache");
                ((HttpServletResponse)response).setHeader("Cache-Control","no-store");
                ((HttpServletResponse)response).setDateHeader("Expires", 0 );
                ((HttpServletResponse)response).setContentType("text/html");
                response.getWriter().write(ServiceUtils.getXML(Constants.APP_ROOT+"login.xsl", doc));
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
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
