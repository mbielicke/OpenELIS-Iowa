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
import javax.servlet.ServletContext;
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
import org.openelis.persistence.JMSMessageConsumer;
import org.openelis.remote.LoginRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public static ServletContext servletCtx;
    
    public void init(FilterConfig config) throws ServletException {
        log.debug("Initializing the Application.");
        if(config.getInitParameter("AppRoot") != null)
            Constants.APP_ROOT = config.getInitParameter("AppRoot");
        if(config.getInitParameter("userTrys") != null)
            userTrys = Integer.parseInt(config.getInitParameter("userTrys"));
        if(config.getInitParameter("ipTrys") != null)
            ipTrys = Integer.parseInt(config.getInitParameter("ipTrys"));
        SessionManager.init("OpenELIS"); 
        ServiceUtils.props = "org.openelis.constants.OpenELISConstants";
        CachingManager.init(Constants.APP_ROOT);
        JMSMessageConsumer.startListener("topic/openelisTopic");
        servletCtx = config.getServletContext();
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
                try {
                    InitialContext initCtx = new InitialContext();
                    //Context envCtx = (Context)initCtx.lookup("java:comp/env");
                    propFile = new File((String)initCtx.lookup(("java:comp/env/openelisJNDI")));
                    is = new FileInputStream(propFile);
               
                    props.load(is);
                    props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY,
                    "org.jboss.security.jndi.LoginInitialContextFactory");
                    props.setProperty(InitialContext.SECURITY_PROTOCOL, "other");
                    props.setProperty(InitialContext.SECURITY_CREDENTIALS,password);
                    props.setProperty(Context.SECURITY_PRINCIPAL, username);
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
                    authLog.info("Login attempt for "+username+" succeeded");
                    /*try {
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
                        ((HttpServletResponse)response).setCharacterEncoding("UTF-8");
                        response.getWriter().write(ServiceUtils.getXML(Constants.APP_ROOT+"loading.xsl", doc));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return;
                    */
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
                ((HttpServletResponse)response).setCharacterEncoding("UTF-8");
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
        JMSMessageConsumer.stopListener();
    }
}
