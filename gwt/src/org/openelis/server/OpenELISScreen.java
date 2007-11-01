package org.openelis.server;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.stream.StreamResult;

import org.openelis.client.main.OpenELISScreenInt;
import org.openelis.gwt.common.RPCException;
import org.openelis.persistence.CachingManager;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;

public class OpenELISScreen extends RemoteServiceServlet implements
		OpenELISScreenInt {

    private String appRoot;
    
	 private void initParameters(ServletConfig config) throws ServletException {
	        SessionManager.init("OpenELIS"); 
	        if (config != null) {
	            appRoot = config.getInitParameter("app.root");
	            if(appRoot != null){
	                Constants.APP_ROOT = appRoot;
	                CachingManager.init(Constants.APP_ROOT);
	            }
	        } else {
	            throw new ServletException("Servlet Config object is NULL.");
	        }
	    }

	    public void init() throws ServletException {

	        initParameters(getServletConfig());
	    }
	    
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
        // TODO Auto-generated method stub
        if(moduleBaseURL.indexOf("/shell") > -1) {
            String temp = moduleBaseURL.substring(0, moduleBaseURL.indexOf("/shell"));
            moduleBaseURL = temp + moduleBaseURL.substring(moduleBaseURL.indexOf("/shell")+6);
        }
        return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
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
           // getPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public String getXML() throws RPCException {
		 try{
	            String loc = "en";
	            if(SessionManager.getSession().getAttribute("locale") != null)
	                loc = (String)SessionManager.getSession().getAttribute("locale");
	            Document doc = XMLUtil.createNew("doc");
	            Element root = doc.getDocumentElement();
	            Element locale = doc.createElement("locale");
	            locale.appendChild(doc.createTextNode(loc));
	            root.appendChild(locale);
	            String url = Constants.APP_ROOT+"/Forms/OpenELIS.xsl";
	            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	            XMLUtil.transformXML(doc, new File(url), new StreamResult(bytes));
	            return bytes.toString();
	        }catch(Exception e){
	            e.printStackTrace();
	            return null;
	        }
	}

}
