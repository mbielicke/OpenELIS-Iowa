package org.openelis.server;

import org.apache.log4j.Logger;
import org.openelis.client.main.service.OpenELISServiceInt;
import org.openelis.gwt.server.AppServlet;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpSession;

public class OpenELISServiceImpl extends AppServlet implements OpenELISServiceInt {                  

    private static final long serialVersionUID = 839548243948328704L;

    private static Logger log = Logger.getLogger(OpenELISServiceImpl.class);

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
