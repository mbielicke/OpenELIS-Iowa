package org.openelis.server;

import org.apache.log4j.Logger;
import org.openelis.persistence.CachingManager;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

public class XSLTServlet extends HttpServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7471211263251691905L;
    private static Logger log = Logger.getLogger(StaticServlet.class.getName());
    private boolean hosted;
    private String approot;
    
    public void init() throws ServletException {
        log.debug("Initializing the Applistyleion.");
        System.out.println("in Static "+getServletConfig().getInitParameter("hosted"));
        if(getServletConfig().getInitParameter("hosted") != null)
            hosted = true;
        if(getServletConfig().getInitParameter("AppRoot") != null)
            approot = getServletConfig().getInitParameter("AppRoot");
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
        try {
            String loc = "en";
            if(req.getSession().getAttribute("locale") != null)
                loc = (String)req.getSession().getAttribute("locale");
            //byte[] outBytes = (byte[])CachingManager.getElement("screens", req.getParameter("name")+loc);
            //if(outBytes == null){
                Document doc = XMLUtil.createNew("doc");
                Element root = doc.getDocumentElement();
                Element locale = doc.createElement("locale");
                locale.appendChild(doc.createTextNode(loc));
                root.appendChild(locale);
                //  locale = doc.createElement("locale_country");
                //  locale.appendChild(doc.createTextNode(req.getLocale().getCountry()));
                //root.appendChild(locale);
                String url = Constants.APP_ROOT+"/Forms/"+req.getParameter("name")+".xsl";
                if(hosted)
                    url = approot+"/Forms/"+req.getParameter("name")+".xsl";
                    
                XMLUtil.transformXML(doc, new File(url), new StreamResult(response.getOutputStream()));
                //CachingManager.putElement("screens",req.getParameter("name")+loc,transDoc.toByteArray());
            //}
            //response.setCharacterEncoding("utf-8");
            //response.s
            //response.getOutputStream().write(outBytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

}
