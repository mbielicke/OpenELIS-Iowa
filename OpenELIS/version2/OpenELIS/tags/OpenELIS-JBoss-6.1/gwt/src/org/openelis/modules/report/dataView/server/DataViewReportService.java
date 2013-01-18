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
package org.openelis.modules.report.dataView.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.server.EJBFactory;
import org.openelis.util.SessionManager;

public class DataViewReportService {
    
    public ArrayList<IdNameVO> fetchEnvironmentalProjectListForWeb() throws Exception {
        return EJBFactory.getDataView().fetchEnvironmentalProjectListForWeb();  
    }
    
    public DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception {
        return EJBFactory.getDataView().fetchAnalyteAndAuxField(data);
    }
    
    public DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data) throws Exception {
        return EJBFactory.getDataView().fetchAnalyteAndAuxFieldForWebEnvironmental(data);
    }
    
    public void loadQuery(FileItem file) throws Exception {
        int len;
        byte buf[];      
        DataViewVO data;
        OutputStream out;
        InputStream in;
        File temp;        
        SystemVariableDO list;
        
        out = null;
        in = null;
        temp = null;
        try {
            list = EJBFactory.getSystemVariable().fetchByName("upload_save_directory");

            temp = File.createTempFile("dataview", ".xml", new File(list.getValue()));
            out = new FileOutputStream(temp);
            buf = new byte[1024];
            in = file.getInputStream();
            while ( (len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();

            data = EJBFactory.getDataView().loadQuery(temp.getPath());
            in.close(); 
            temp.delete();
            SessionManager.getSession().setAttribute("dataViewQuery", data);
        } catch (Exception e) {
            if (in != null)
                in.close(); 
            if (temp != null)
                temp.delete();
            if (out != null) 
                out.close();
            throw e;
        }       
    }
    
    public DataViewVO openQuery() throws Exception {
        DataViewVO data;
        HttpSession session;
        
        session = SessionManager.getSession();               
        data = (DataViewVO)session.getAttribute("dataViewQuery");
        /*
         * we remove the VO from the session because there's no need to hold on
         * to it beyond one request 
         */
        session.removeAttribute("dataViewQuery");
        return data;
    }
    
    public ReportStatus saveQuery(DataViewVO data) throws Exception {
        ReportStatus st;

        st = EJBFactory.getDataView().saveQuery(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReport(DataViewVO data) throws Exception {
        ReportStatus st;

        st = EJBFactory.getDataView().runReport(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception {
        ReportStatus st;

        st = EJBFactory.getDataView().runReportForWebEnvironmental(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
}