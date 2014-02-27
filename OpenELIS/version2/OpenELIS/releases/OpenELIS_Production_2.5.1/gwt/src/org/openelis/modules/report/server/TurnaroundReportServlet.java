package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.TurnaroundReportBean;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.report.client.TurnaroundReportServiceInt;

@WebServlet("/openelis/turnaroundReport")
public class TurnaroundReportServlet extends RemoteServlet implements TurnaroundReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    TurnaroundReportBean turnaroundReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        try {        
            return turnaroundReport.getPrompts();      
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        try {        
            st = turnaroundReport.runReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
   
}
