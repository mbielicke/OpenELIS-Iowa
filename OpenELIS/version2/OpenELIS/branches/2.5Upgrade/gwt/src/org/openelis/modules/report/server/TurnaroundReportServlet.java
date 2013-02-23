package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.TurnaroundReportBean;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.client.TurnaroundReportServiceInt;

@WebServlet("/openelis/turnaroundReport")
public class TurnaroundReportServlet extends RemoteServlet implements TurnaroundReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    TurnaroundReportBean turnaroundReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        return turnaroundReport.getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = turnaroundReport.runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
   
}
