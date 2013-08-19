package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.VolumeReportBean;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.report.client.VolumeReportServiceInt;

@WebServlet("/openelis/volumeReport")
public class VolumeReportServlet extends RemoteServlet implements VolumeReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    VolumeReportBean volumeReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        try {        
            return volumeReport.getPrompts();      
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        try {        
            st = volumeReport.runReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
   
}
