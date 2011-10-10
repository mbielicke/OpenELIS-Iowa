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
package org.openelis.modules.dataView.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.fileupload.FileItem;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.DataViewRemote;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.util.SessionManager;

public class DataViewService {
  
    public ArrayList<IdNameVO> fetchPermanentProjectList() throws Exception {
        return remote().fetchPermanentProjectList();  
    }
    
    public DataViewVO fetchAnalyteAndAuxField(Query query) throws Exception {
        return remote().fetchAnalyteAndAuxField(query.getFields());
    }
    
    public DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(Query query) throws Exception {
        return remote().fetchAnalyteAndAuxFieldForWebEnvironmental(query.getFields());
    }
    
    public ReportStatus runReport(DataViewVO data) throws Exception {
        ReportStatus st;

        st = remote().runReport(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception {
        ReportStatus st;

        st = remote().runReportForWebEnvironmental(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public void loadQuery(FileItem file) throws Exception {
        int len;
        byte buf[];      
        DataViewVO data;
        OutputStream out;
        InputStream in;
        File temp;        
        SystemVariableDO list;
        
        list = systemVariableRemote().fetchByName("upload_save_directory");
                    
        temp = File.createTempFile("dataview", ".xml", new File(list.getValue()));            
        out = new FileOutputStream(temp);
        buf = new byte[1024];
        in = file.getInputStream();
        while ( (len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        out.close();
        in.close();

        data = remote().loadQuery(temp.getPath());
        SessionManager.getSession().setAttribute("dataViewQuery", data);        
    }
    
    public DataViewVO openQuery() throws Exception {
        return (DataViewVO)SessionManager.getSession().getAttribute("dataViewQuery");
    }
    
    public ReportStatus saveQuery(DataViewVO data) throws Exception {
        ReportStatus st;

        st = remote().saveQuery(data);
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    private DataViewRemote remote() {
        return (DataViewRemote)EJBFactory.lookup("openelis/DataViewBean/remote");
    }       
    
    private SystemVariableRemote systemVariableRemote() {
        return (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
    } 
}