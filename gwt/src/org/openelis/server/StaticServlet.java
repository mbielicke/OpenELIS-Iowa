package org.openelis.server;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.openelis.interfaces.AbstractAction;
import org.openelis.util.SessionManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.server.constants.Constants;

//import edu.uiowa.uhl.security.remote.SecurityRemote;

// import org.openelis.tt.server.security.SecurityService;
/**
 * Elis controller is the entry point for Elis Web Application. This object
 * controls the workflow for processing request and response.
 * 
 * @author fyu
 * 
 */
public class StaticServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = -7471211263251691905L;
    private static Logger log = Logger.getLogger(StaticServlet.class.getName());
    private boolean hosted;
    
    public void init() throws ServletException {
        log.debug("Initializing the Applistyleion.");
        System.out.println("in Static "+getServletConfig().getInitParameter("hosted"));
        if(getServletConfig().getInitParameter("hosted") != null)
            hosted = true;
        if(getServletConfig().getInitParameter("AppRoot") != null)
            Constants.APP_ROOT = getServletConfig().getInitParameter("AppRoot");
        log.debug("getting out");
    }
    /**
     * Process POST request.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
        doGet(req, response);
    }

    /**
     * Process GET request.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        try {
            perform(req, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Perform controller workflow.
     */
    private void perform(HttpServletRequest req, HttpServletResponse response) throws IOException,
                                                                              SQLException {
        if(req.getParameter("logout") != null && req.getParameter("logout").equals("true")){
            logout(req,response);
            return;
        }
        if (req.getParameter("castgc") != null) {
            req.getSession().setAttribute("castgc", req.getParameter("castgc"));
            if (req.getSession().getAttribute("jndiProps") != null)
                req.getSession().removeAttribute("jndiProps");
            try {
                SessionManager.setSession(req.getSession());
                /*SecurityRemote remote = (SecurityRemote)EJBFactory.lookup("SecurityBean/remote");
                HashMap mods = remote.getModules("open");
                SessionManager.getSession().setAttribute("permissions", mods);
                if(!mods.containsKey("time-select")){
                    response.sendRedirect("NoPermission.html");
                    return;
                }
                */
            } catch (Exception e) {
                log.error("action " + e.getMessage());
                response.sendRedirect("NoPermission.html");
                return;
            }
        }
        if(req.getParameter("locale") != null){
            req.getSession().setAttribute("locale",req.getParameter("locale"));
            if(hosted)
                response.sendRedirect("shell/org.openelis.OpenELIS/OpenELIS.html?locale="+req.getParameter("locale"));
            else
                response.sendRedirect("OpenELIS.html?locale="+req.getParameter("locale"));  
        }else{
            req.getSession().removeAttribute("locale");
            if(hosted)
                response.sendRedirect("shell/org.openelis.OpenELIS/OpenELIS.html");
            else
                response.sendRedirect("OpenELIS.html");
          
        }
        /*
        try {
            AbstractAction ea;
            ea = (AbstractAction)getAction(req);
            ea.execute(req, response);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            dispatchError(response, Constants.SERVICE_NOT_AVALIABLE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            dispatchError(response, Constants.REPORT_THIS_ERROR);
        } finally {
        }
        */
    }

    /**
     * Return Action base on servletPath.
     * 
     * @param req
     * @return ElisAction
     */
    private AbstractAction getAction(HttpServletRequest req) {
        AbstractAction action = null;
        String destination;
        destination = req.getServletPath();
        destination = destination.substring(1, destination.length());
        StringBuffer className = new StringBuffer();
        className.append(Constants.TARGET_CLASS_ROOT)
                 .append(".")
                 .append(destination)
                 .append(".")
                 .append("Action");
        log.debug("Getting Action with class: " + className);
        try {
            action = (AbstractAction)Class.forName(className.toString())
                                          .newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Error: ClassNotFoundException" + e.getMessage(), e);
        } catch (InstantiationException e) {
            log.error("Error: InstantiationException" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error("Error: IllegalAccessException" + e.getMessage(), e);
        }
        return action;
    }

    /**
     * Dispatch to error page.
     * 
     * @param response
     * @param error
     */
    public void dispatchError(HttpServletResponse response, String error) {
        log.error("Dispatching Error: " + error);
        try {
            response.sendRedirect("/timetracker/error?error=" + error);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public void logout(HttpServletRequest req, HttpServletResponse response) {
        HttpSession session;
        try {
            session = req.getSession();
            SessionManager.removeSession(session.getId());
            log.debug("session removed");
            // Clear out the existing session for the user
            if (session != null) {
                session.invalidate();
            }
            log.debug("Session invalidated");
            // redirect to CAS logout servlet for deletion of CAS cookies
            response.sendRedirect("https://www.uhl.uiowa.edu/cas/logout");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
