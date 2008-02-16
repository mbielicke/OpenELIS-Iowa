package org.openelis.modules.main.server;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.openelis.gwt.server.AppServlet;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenELISServiceImpl extends AppServlet implements OpenELISServiceInt {                  

    private static final long serialVersionUID = 839548243948328704L;

    private static Logger log = Logger.getLogger(OpenELISServiceImpl.class);

    //private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.client.main.constants.OpenELISConstants",
	//		new Locale((SessionManager.getSession().getAttribute("locale") == null ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
    
	public String getMenuList() {
        log.debug("in Tree xml");
        try {
        //	System.out.println((SessionManager.getSession().getAttribute("locale") == null ? "en" : (String)SessionManager.getSession().getAttribute("locale")));
            Document doc = XMLUtil.createNew("list");
            Element root = doc.getDocumentElement();
            root.setAttribute("key", "menuList");
            root.setAttribute("height", "100%");
            root.setAttribute("vertical","true");
            
            //sample management section
            Element elem = doc.createElement("label");
            elem.setAttribute("text", "Favorites");
            elem.setAttribute("constant", "true");
            elem.setAttribute("style", "ListHeader");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Dictionary");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftDictionary");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Organization");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftOrganization");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Organize Favorites...");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "organizeFavoritesLeft");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Provider");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftProvider");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "QA Events");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftqaEvents");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Standard Note");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStandardNote");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Storage");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStorage");
            elem.setAttribute("style", "ListSubItem");
            elem.setAttribute("onClick", "this");
            elem.setAttribute("hover", "Hover");
            root.appendChild(elem);
            
            elem = doc.createElement("menulabel");
            elem.setAttribute("text", "Storage Unit");
            elem.setAttribute("constant", "true");
            elem.setAttribute("key", "favLeftStorageUnit");
            elem.setAttribute("style", "ListSubItem");
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
	
	public String getLanguage(){
		return getThreadLocalRequest().getLocale().getLanguage();
	}
}
