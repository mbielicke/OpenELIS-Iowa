package org.openelis.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.openelis.bean.SystemVariableBean;

/**
 * This class provide the parse capability for file upload. This class works
 * with file upload widget to send files from the client to server.
 */

public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SystemVariableBean systemVariable;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                                  IOException {
        List<FileItem> files;
        List<String> paths;
        FileItemFactory factory;
        ServletFileUpload upload;
        String dir;

        try {
        	dir = systemVariable.fetchByName("upload_stream_directory").getValue();
            factory = new DiskFileItemFactory();
            upload = new ServletFileUpload(factory);
            files = upload.parseRequest(req);
            paths = (List<String>)req.getSession().getAttribute("upload");
            
            if (files != null && files.size() > 0) {
       
            	if(paths == null)
            		paths = new ArrayList<String>();
            	
            	for(FileItem file : files) {
            		file.write(new File(dir+"/"+file.getName()));
            		paths.add(dir+"/"+file.getName());
            	}
            }
            
            req.getSession().setAttribute("upload", paths);
            
        } catch (Exception e) {
        	e.printStackTrace();
            throw (ServletException)e.getCause();
        }
        
        resp.getOutputStream().print("Files Uploaded");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                                   IOException {
        doGet(req, resp);
    }
}