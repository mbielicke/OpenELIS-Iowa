package org.openelis.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.openelis.client.main.service.OpenELISServiceInt;

import java.util.HashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openelis.server.constants.Constants;
import org.openelis.gwt.client.services.ScreenServiceInt;
import org.openelis.gwt.client.services.TableServiceInt;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableModel;
import org.openelis.interfaces.AbstractAction;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.openelis.persistence.EJBFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenELISServiceImpl extends RemoteServiceServlet implements
                                                         OpenELISServiceInt,
                                                         TableServiceInt,
                                                         ScreenServiceInt {

    private static final long serialVersionUID = 839548243948328704L;

    private static Logger log = Logger.getLogger(OpenELISServiceImpl.class);

    private String appRoot;

    private void initParameters(ServletConfig config) throws ServletException {
        log.debug("in InitParameters");
        SessionManager.init("OpenELIS"); 
        if (config != null) {
            appRoot = config.getInitParameter("app.root");
            Constants.APP_ROOT = appRoot;
        } else {
            log.error("ServletConfig is NULL",
                      new ServletException("ServletConfig is NULL"));
            throw new ServletException("Servlet Config object is NULL.");
        }
    }

    public void init() throws ServletException {
        log.debug("Initializing the Application.");
        initParameters(getServletConfig());
        log.debug("getting out");
    }

    protected void onBeforeRequestDeserialized(String serializedRequest) {
        // TODO Auto-generated method stub
        super.onBeforeRequestDeserialized(serializedRequest);
        HttpSession session = getThreadLocalRequest().getSession();
        System.out.println("Elis Service Sess "+session.toString());
        session.setAttribute("IPAddress",
                             getThreadLocalRequest().getRemoteAddr());
        SessionManager.setSession(session);
        try {
            getPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean hasPermission(String module) {
        try {
            log.debug("set session");
            boolean result = true;
            Boolean access = (Boolean)getPermissions().get(module);
            if (access != null) {
                return access.booleanValue();
            } else {
                // for module that doesn't have access, it means no access is in
                // force on
                // this module
                return result;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public HashMap getPermissions() throws RPCException {
        // TODO Auto-generated method stub
        log.debug("in  Get Permissions");
        try {
            HttpSession sess = SessionManager.getSession();
            if (sess == null) {
                sess = getThreadLocalRequest().getSession();
                SessionManager.setSession(sess);
            }
            HashMap perms = null;
            if (sess.getAttribute("permissions") != null) {
                perms = (HashMap)sess.getAttribute("permissions");
            } else {
                //SecurityRemote remote = (SecurityRemote)EJBFactory.lookup("SecurityBean/remote");
                //perms = remote.getModules("elis");
                sess.setAttribute("permissions", perms);
            }
            return perms;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
    }
    
    public TableModel getPage(int page, int selected) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public TableModel sort(int col, boolean down, int index, int selected) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public TableModel filter(int col, Filter[] filters, int index, int selected) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter[] getFilter(int col) {
        // TODO Auto-generated method stub
        return null;
    }

    public TableModel getModel(TableModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public TableModel saveModel(TableModel model) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC action(FormRPC form) throws RPCException {
        try {
            AbstractAction action = (AbstractAction)Class.forName(Constants.TARGET_CLASS_ROOT + "."
                                                                  + form.action
                                                                  + ".Action")
                                                         .newInstance();
            SessionManager.getSession().setAttribute("action", form.action + " - "+ IForm.opNames[form.operation]);
            if (action.hasPermission(form))
                action.execute(form);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        return form;
    }

    public AbstractField query(FormRPC form) throws RPCException {
        try {
            AbstractAction action = (AbstractAction)Class.forName(Constants.TARGET_CLASS_ROOT + "."
                                                                  + form.action
                                                                  + ".Action")
                                                         .newInstance();
            SessionManager.getSession().setAttribute("action", form.action + " - Query");
            if (action.hasPermission(form)){
                return action.query(form);
            }
            else
                throw new RPCException("No Permission for action " + form.action);
        } catch (Exception e) {
            throw new RPCException(e.getMessage());
        }
    }

	public String getMenuList() {
        log.debug("in Tree xml");
        try {
            Document doc = XMLUtil.createNew("list");
            Element root = doc.getDocumentElement();
            root.setAttribute("key", "menuList");
            root.setAttribute("height", "100%");
            root.setAttribute("vertical","true");
            Element elem = doc.createElement("label");
            elem.setAttribute("text", "Sample Management");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("label");
            elem.setAttribute("text", "Analysis Management");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("label");
            elem.setAttribute("text", "Billing and Preordering");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("label");
            elem.setAttribute("text", "Supply Management");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("label");
            elem.setAttribute("text", "Reports");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("label");
            elem.setAttribute("text", "Utilities");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Test Screen");
            elem.setAttribute("style","ListSubItem");
            elem.setAttribute("key", "testScreen");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            return XMLUtil.toString(doc);
        }catch(Exception e){
        	log.error(e.getMessage());
        	e.printStackTrace();
        	return null;
        }
	}

	public void logout() {
        HttpSession session;
        try {
            session = getThreadLocalRequest().getSession();
            SessionManager.removeSession(session.getId());
            log.debug("session removed");
            // Clear out the existing session for the user
            if (session != null) {
                session.invalidate();
            }
            log.debug("Session invalidated");
            // redirect to CAS logout servlet for deletion of CAS cookies
            getThreadLocalResponse().sendRedirect("https://www.uhl.uiowa.edu/cas/logout");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
		
	}

}
