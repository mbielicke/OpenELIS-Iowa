package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.VolumeReportBean;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.client.VolumeReportServiceInt;

@WebServlet("/openelis/volumeReport")
public class VolumeReportServlet extends RemoteServlet implements VolumeReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    VolumeReportBean volumeReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        return volumeReport.getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = volumeReport.runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
   
}
