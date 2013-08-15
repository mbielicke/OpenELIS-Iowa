package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.QASummaryReportBean;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.report.client.QASummaryReportServiceInt;

@WebServlet("/openelis/qaSummary")
public class QASummaryReportServlet extends RemoteServlet implements QASummaryReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    QASummaryReportBean qaSummaryReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        try {        
            return qaSummaryReport.getPrompts();      
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        try {        
            st = qaSummaryReport.runReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

}
