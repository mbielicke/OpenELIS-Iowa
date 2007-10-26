package org.openelis.server;

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openelis.client.main.service.OpenELISServiceInt;
import org.openelis.gwt.client.services.ScreenServiceInt;
import org.openelis.gwt.client.services.TableServiceInt;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableModel;
import org.openelis.interfaces.AbstractAction;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
            
            //sample management section
            Element elem = doc.createElement("label");
            elem.setAttribute("text", "favorites");
            elem.setAttribute("constant", "true");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "project");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "zcas");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "analyte");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screen1");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "method");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screen2");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "methodPanel");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screen1q");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "order");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screen2q");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "storage");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screeqn1");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "report2");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "sqcreen2");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "fastSampleLogin");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screqen1");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "organization");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftOrganization");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "sampleLogin");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screeen1");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "dictionary");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "sctreen2");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "systemVariable");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "screetn1");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "organizeFavorites");
            elem.setAttribute("constant", "true");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("key", "organizeFavoritesLeft");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
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
	
	public String getHorizontalMenuList(){
		 log.debug("in horizontal bar xml");
	        try {
	            Document doc = XMLUtil.createNew("menubar");
	            
	            Element root = doc.getDocumentElement();
	            root.setAttribute("key", "horizontalMenuBar");
	           // root.setAttribute("height", "100%");
	            root.setAttribute("vertical","false");
	            root.setAttribute("commands", "HorizontalMenuCommands");
	            root.setAttribute("autoOpen", "true");
	            
	            //file menu
	            Element elem = doc.createElement("item");
	            elem.setAttribute("text", "File");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            Element subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Exit");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	          /*  //Edit Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Edit");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Cut");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Copy");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Paste");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //Forms Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Forms");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            //sample management menu
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Sample Management >>");
	            subElem.setAttribute("autoOpen", "true");
	            subElem.setAttribute("vertical","true");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);	
	            
	            //sample managment forms menu
	            Element subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Project");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Sample Lookup");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Release");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            
	            //analysis management section
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Analysis Management >>");
	            subElem.setAttribute("autoOpen", "true");
	            subElem.setAttribute("vertical","true");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //analysis management menu items
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Analyte");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Method");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Method Panel");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "QA Events");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Results");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Test Management");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Test Trailer");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Worksheets");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            
	            //supply management menu
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Supply Management >>");
	            subElem.setAttribute("autoOpen", "true");
	            subElem.setAttribute("vertical","true");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //supply management menu items
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Instrument");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Inventory");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Order");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Storage");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);

	            //data entry menu
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Data Entry >>");
	            subElem.setAttribute("autoOpen", "true");
	            subElem.setAttribute("vertical","true");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);

	            //data entry menu items
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Fast Sample Login");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Organization");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Patient");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Person");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Provider");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Sample Login");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            
	            //utilities menu
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Utilities >>");
	            subElem.setAttribute("autoOpen", "true");
	            subElem.setAttribute("vertical","true");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //utilities menu items
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Auxiliary");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Dictionary");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Label");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Reference Table");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Scriplet");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Section");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "Standard Note");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            subSubElem = doc.createElement("item");
	            subSubElem.setAttribute("text", "System Variable");
	            subSubElem.setAttribute("cmd", "organizationFormSmall2");
	            subSubElem.setAttribute("style", "ListSubItem");
	            subElem.appendChild(subSubElem);
	            
	            //Reports Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Reports");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Report 1");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Report 2");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Report 3");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //Favorites Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Favorites");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            //Favorites Menu Items
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Fast Sample Login");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Inventory");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Analyte");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Project");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Worksheets");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Dictionary");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //Window Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Window");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Close Left Menu");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Close All Windows");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            
	            //Help Menu
	            elem = doc.createElement("item");
	            elem.setAttribute("text", "Help");
	            elem.setAttribute("cmd", "2");
	            elem.setAttribute("autoOpen", "true");
	            elem.setAttribute("vertical","true");
	            elem.setAttribute("style", "ListHeader");
	            root.appendChild(elem);
	            
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "Help Contents");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);
	            subElem = doc.createElement("item");
	            subElem.setAttribute("text", "About");
	            subElem.setAttribute("cmd", "organizationFormSmall2");
	            subElem.setAttribute("style", "ListSubItem");
	            elem.appendChild(subElem);*/
	        
		/*<item text="Sample Management" cmd="1" style="ListHeader"/>  
		 <item text="Analysis Management" cmd="1" style="ListHeader"/>  
		 <item text="Billing and Preordering" cmd="2" style="ListHeader"/>  
		 <item text="Supply Management" cmd="3" style="ListHeader"/>  
		 <item text="Reports" cmd="1" style="ListHeader"/>  
		 <item text="Data Entry" cmd="1" style="ListHeader"/>  
		 <item text="Utilities" cmd="2" style="ListHeader"/>  */
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
	
	public String getLanguage(){
		return getThreadLocalRequest().getLocale().getLanguage();
	}
}
